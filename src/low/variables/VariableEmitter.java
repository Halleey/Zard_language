package low.variables;
import ast.inputs.InputNode;
import ast.lists.ListNode;
import ast.variables.LiteralNode;
import low.functions.TypeMapper;
import low.inputs.InputEmitter;
import low.lists.ListEmitter;
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
            varTypes.put(node.getName(), "%String*"); // tipo do struct
            return "  " + ptr + " = alloca %String*\n;;VAL:" + ptr + ";;TYPE:%String*\n";
        }

        if (node.getType().startsWith("List")) {
            String listType = node.getType();
            String llvmListType;
            System.out.println("---------- tipo da lista  " + listType);
            if (listType.equals("List<int>")) {
                llvmListType = "%ArrayListInt*";
            } else {
                // Fallback genérico
                llvmListType = "i8*";
            }

            varTypes.put(node.getName(), llvmListType);
            return "  " + ptr + " = alloca " + llvmListType + "\n;;VAL:" + ptr + ";;TYPE:" + llvmListType + "\n";
        }

        varTypes.put(node.getName(), llvmType);
        return "  " + ptr + " = alloca " + llvmType + "\n;;VAL:" + ptr + ";;TYPE:" + llvmType + "\n";
    }
    public String emitInit(VariableDeclarationNode node) {
        String llvmType = varTypes.get(node.getName());
        String varPtr = getVarPtr(node.getName());
        StringBuilder sb = new StringBuilder();

        // ===== Sem inicializador =====
        if (node.initializer == null) {
            // Listas
            if (node.getType().startsWith("List")) {
                return callArrayListCreateAndStore(varPtr, 4);
            }

            // Strings vazias
            if (node.getType().equals("string")) {
                String tmpRaw = temps.newTemp();
                String tmpStruct = temps.newTemp();

                // malloc sempre retorna i8*, depois bitcast para %String*
                sb.append("  ").append(tmpRaw)
                        .append(" = call i8* @malloc(i64 ptrtoint (%String* getelementptr (%String, %String* null, i32 1) to i64))\n");
                sb.append("  ").append(tmpStruct)
                        .append(" = bitcast i8* ").append(tmpRaw).append(" to %String*\n");

                // inicializa .data para null
                String ptrField = temps.newTemp();
                sb.append("  ").append(ptrField)
                        .append(" = getelementptr inbounds %String, %String* ").append(tmpStruct).append(", i32 0, i32 0\n");
                sb.append("  store i8* null, i8** ").append(ptrField).append("\n");

                // inicializa .length para 0
                String lenField = temps.newTemp();
                sb.append("  ").append(lenField)
                        .append(" = getelementptr inbounds %String, %String* ").append(tmpStruct).append(", i32 0, i32 1\n");
                sb.append("  store i64 0, i64* ").append(lenField).append("\n");

                // armazena na variável
                sb.append("  store %String* ").append(tmpStruct).append(", %String** ").append(varPtr).append("\n");
                return sb.toString();
            }

            return "";
        }

        if (node.initializer instanceof InputNode inputNode) {
            InputEmitter inputEmitter = new InputEmitter(temps, visitor.getGlobalStrings());
            String code = inputEmitter.emit(inputNode, llvmType);
            String temp = extractTemp(code);
            return code + emitStore(node.getName(), llvmType, temp);
        }


        if (node.getType().startsWith("List")) {
            if (node.initializer instanceof ListNode listNode) {
                ListEmitter listEmitter = new ListEmitter(temps);
                String listLLVM = listEmitter.emit(listNode, visitor);
                String tmpList = extractTemp(listLLVM);

                String elementType = listNode.getList().getElementType();
                visitor.registerListElementType(node.getName(), elementType);

                return listLLVM + "  store i8* " + tmpList + ", i8** " + varPtr + "\n";
            } else {
                return callArrayListCreateAndStore(varPtr, 4);
            }
        }

        if (node.getType().equals("string") && node.initializer instanceof LiteralNode lit) {
            String literal = (String) lit.value.getValue();
            String globalName = visitor.getGlobalStrings().getGlobalName(literal);
            int len = literal.length();

            String tmpRaw = temps.newTemp();
            String tmpStruct = temps.newTemp();

            // malloc + bitcast
            sb.append("  ").append(tmpRaw)
                    .append(" = call i8* @malloc(i64 ptrtoint (%String* getelementptr (%String, %String* null, i32 1) to i64))\n");
            sb.append("  ").append(tmpStruct)
                    .append(" = bitcast i8* ").append(tmpRaw).append(" to %String*\n");

            // bitcast do literal
            String tmpData = temps.newTemp();
            sb.append("  ").append(tmpData)
                    .append(" = bitcast [").append(len + 1).append(" x i8]* ").append(globalName)
                    .append(" to i8*\n");

            // inicializa .data
            String ptrField = temps.newTemp();
            sb.append("  ").append(ptrField)
                    .append(" = getelementptr inbounds %String, %String* ").append(tmpStruct).append(", i32 0, i32 0\n");
            sb.append("  store i8* ").append(tmpData).append(", i8** ").append(ptrField).append("\n");

            // inicializa .length
            String lenField = temps.newTemp();
            sb.append("  ").append(lenField)
                    .append(" = getelementptr inbounds %String, %String* ").append(tmpStruct).append(", i32 0, i32 1\n");
            sb.append("  store i64 ").append(len).append(", i64* ").append(lenField).append("\n");

            sb.append("  store %String* ").append(tmpStruct).append(", %String** ").append(varPtr).append("\n");

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
        if (type.equals("%String")) {
            // estamos lidando com um ponteiro para struct
            return "  store %String* " + value + ", %String** %" + name + "\n";
        }
        return "  store " + type + " " + value + ", " + type + "* %" + name + "\n";
    }


    public String emitLoad(String name) {
        String llvmType = varTypes.get(name);
        String ptr = localVars.get(name);
        String tmp = temps.newTemp();
        return "  " + tmp + " = load " + llvmType + ", " + llvmType + "* " + ptr
                + "\n;;VAL:" + tmp + ";;TYPE:" + llvmType + "\n";
    }

    public String getVarPtr(String name) {
        return localVars.get(name);
    }

    public void registerVarPtr(String name, String ptr) {
        localVars.put(name, ptr);
    }

    private String extractTemp(String code) {
        int lastValIdx = code.lastIndexOf(";;VAL:");
        if (lastValIdx == -1) throw new RuntimeException("Cannot find ;;VAL: in: " + code);
        int typeIdx = code.indexOf(";;TYPE:", lastValIdx);
        return code.substring(lastValIdx + 6, typeIdx).trim();
    }

}