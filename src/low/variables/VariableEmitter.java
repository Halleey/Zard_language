package low.variables;
import ast.ASTNode;
import ast.lists.ListNode;
import ast.variables.LiteralNode;
import low.functions.TypeMapper;
import low.module.LLVisitorMain;
import low.TempManager;
import ast.variables.VariableDeclarationNode;

import java.util.HashMap;
import java.util.Map;

public class VariableEmitter {
    private final Map<String, String> varTypes; // nome -> LLVM type
    private final TempManager temps;
    private final LLVisitorMain visitor;
    private final Map<String, String> localVars = new HashMap<>();

    public VariableEmitter(Map<String, String> varTypes, TempManager temps, LLVisitorMain visitor) {
        this.varTypes = varTypes;
        this.temps = temps;
        this.visitor = visitor;
    }

    public String mapLLVMType(String type) {
        return new TypeMapper().toLLVM(type);
    }

    public String emitAlloca(VariableDeclarationNode node) {
        String llvmType = mapLLVMType(node.getType());
        String ptr = "%" + node.getName();
        localVars.put(node.getName(), ptr);

        if (node.getType().equals("string")) {
            // grava o tipo "valor" como ponteiro para struct
            varTypes.put(node.getName(), "%String*");
            localVars.put(node.getName(), ptr);
            // alloca %String -> ptr (que tem tipo %String*)
            return "  " + ptr + " = alloca %String\n;;VAL:" + ptr + ";;TYPE:%String*\n";
        }

        if (node.getType().startsWith("List")) {
            varTypes.put(node.getName(), "i8*"); // ponteiro genérico
            return "  " + ptr + " = alloca i8*\n;;VAL:" + ptr + ";;TYPE:i8*\n";
        }

        varTypes.put(node.getName(), llvmType);
        return "  " + ptr + " = alloca " + llvmType + "\n;;VAL:" + ptr + ";;TYPE:" + llvmType + "\n";
    }

    public String emitInit(VariableDeclarationNode node) {
        String llvmType = varTypes.get(node.getName());
        String varPtr = getVarPtr(node.getName());

        if (node.initializer == null) {
            if (node.getType().startsWith("List")) {
                return callArrayListCreateAndStore(varPtr, 4); // capacidade padrão
            }
            return "";
        }

        StringBuilder sb = new StringBuilder();

        if (node.getType().startsWith("List") && node.initializer instanceof ListNode listNode) {
            int size = Math.max(4, listNode.getList().getElements().size());
            String tmpList = temps.newTemp();

            // Cria lista e armazena
            sb.append("  ").append(tmpList)
                    .append(" = call i8* @arraylist_create(i64 ").append(size).append(")\n")
                    .append(";;VAL:").append(tmpList).append(";;TYPE:i8*\n")
                    .append("  store i8* ").append(tmpList).append(", i8** ").append(varPtr).append("\n");

            // Registrar tipo do elemento da lista
            String elementType = listNode.getList().getElementType();
            visitor.registerListElementType(node.getName(), elementType);

            // Adiciona elementos
            for (ASTNode elem : listNode.getList().getElements()) {
                String elemLLVM = elem.accept(visitor);
                sb.append(elemLLVM);

                String temp = extractTemp(elemLLVM);
                String type = extractType(elemLLVM);

                switch (type) {
                    case "i32" -> sb.append("  call void @arraylist_add_int(i8* ").append(tmpList)
                            .append(", i32 ").append(temp).append(")\n");
                    case "double" -> sb.append("  call void @arraylist_add_double(i8* ").append(tmpList)
                            .append(", double ").append(temp).append(")\n");
                    case "%String*" -> {
                        // temp já é %String*, precisa pegar ponteiro para dados e bitcast para i8*
                        String ptrData = temps.newTemp();
                        sb.append("  ").append(ptrData)
                                .append(" = getelementptr inbounds %String, %String* ").append(temp)
                                .append(", i32 0, i32 0\n");
                        String castTemp = temps.newTemp();
                        sb.append("  ").append(castTemp)
                                .append(" = bitcast i8** ").append(ptrData).append(" to i8*\n");
                        sb.append("  call void @arraylist_add_string(i8* ").append(tmpList)
                                .append(", i8* ").append(castTemp).append(")\n");
                    }
                    case "i8*" -> {
                        String gepTemp = temps.newTemp();
                        sb.append("  ").append(gepTemp)
                                .append(" = getelementptr inbounds ([N x i8], [N x i8]* ").append(temp)
                                .append(", i32 0, i32 0)\n");
                        String castTemp = temps.newTemp();
                        sb.append("  ").append(castTemp)
                                .append(" = bitcast [N x i8]* ").append(gepTemp).append(" to i8*\n");
                        sb.append("  call void @arraylist_add_string(i8* ").append(tmpList)
                                .append(", i8* ").append(castTemp).append(")\n");
                    }

                    default -> throw new RuntimeException("Tipo de lista não suportado: " + type);
                }
            }

            return sb.toString();
        }


        if (node.getType().equals("string") && node.initializer instanceof LiteralNode lit) {
            String literal = (String) lit.value.getValue();
            String globalName = visitor.getGlobalStrings().getGlobalName(literal);
            int len = literal.length();
            // ponteiro para os dados da string literal
            String tmp = temps.newTemp();
            sb.append("  ").append(tmp)
                    .append(" = getelementptr inbounds [")
                    .append(len + 1).append(" x i8], [").append(len + 1)
                    .append(" x i8]* ").append(globalName).append(", i32 0, i32 0\n")
                    .append(";;VAL:").append(tmp).append(";;TYPE:i8*\n");

            // inicializa campo .data (i8*)
            String ptrField = temps.newTemp();
            sb.append("  ").append(ptrField)
                    .append(" = getelementptr inbounds %String, %String* ")
                    .append(varPtr).append(", i32 0, i32 0\n");
            sb.append("  store i8* ").append(tmp).append(", i8** ").append(ptrField).append("\n");

            // inicializa campo .length (i64)
            String lenField = temps.newTemp();
            sb.append("  ").append(lenField)
                    .append(" = getelementptr inbounds %String, %String* ")
                    .append(varPtr).append(", i32 0, i32 1\n");
            sb.append("  store i64 ").append(len).append(", i64* ").append(lenField).append("\n");

            return sb.toString();
        }


        String exprLLVM = node.initializer.accept(visitor);
        String temp = extractTemp(exprLLVM);
        return exprLLVM + emitStore(node.getName(), llvmType, temp);
    }

    private String callArrayListCreateAndStore(String varPtr, int capacity) {
        String tmp = temps.newTemp();
        return "  " + tmp + " = call i8* @arraylist_create(i64 " + capacity + ")\n" +
                ";;VAL:" + tmp + ";;TYPE:i8*\n" +
                "  store i8* " + tmp + ", i8** " + varPtr + "\n";
    }

    private String emitStore(String name, String type, String value) {
        return "  store " + type + " " + value + ", " + type + "* %" + name + "\n";
    }

    public String emitLoad(String name) {
        String llvmType = varTypes.get(name);
        String ptr = localVars.get(name);
        String tmp = temps.newTemp();
        return "  " + tmp + " = load " + llvmType + ", " + llvmType + "* " + ptr + "\n;;VAL:" + tmp + ";;TYPE:" + llvmType + "\n";
    }

    public String getVarPtr(String name) {
        return localVars.get(name);
    }

    public void registerVarPtr(String name, String ptr) {
        localVars.put(name, ptr);
    }

    private String extractTemp(String code) {
        int lastValIdx = code.lastIndexOf(";;VAL:");
        if (lastValIdx == -1) throw new RuntimeException("Não encontrou ;;VAL: em: " + code);
        int typeIdx = code.indexOf(";;TYPE:", lastValIdx);
        return code.substring(lastValIdx + 6, typeIdx).trim();
    }

    private String extractType(String code) {
        int idx = code.indexOf(";;TYPE:");
        int endIdx = code.indexOf("\n", idx);
        if (endIdx == -1) endIdx = code.length();
        return code.substring(idx + 7, endIdx).trim();
    }
}
