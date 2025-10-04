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
            varTypes.put(node.getName(), "i8*"); // ponteiro genérico para listas
            return "  " + ptr + " = alloca i8*\n;;VAL:" + ptr + ";;TYPE:i8*\n";
        }

        varTypes.put(node.getName(), llvmType);
        return "  " + ptr + " = alloca " + llvmType + "\n;;VAL:" + ptr + ";;TYPE:" + llvmType + "\n";
    }

    public String emitInit(VariableDeclarationNode node) {
        String llvmType = varTypes.get(node.getName());
        String varPtr = getVarPtr(node.getName());
        StringBuilder sb = new StringBuilder();

        if (node.initializer == null) {
            if (node.getType().startsWith("List")) {
                return callArrayListCreateAndStore(varPtr, 4);
            }
            return "";
        }
        if(node.initializer instanceof InputNode inputNode) {
            InputEmitter inputEmitter = new InputEmitter(temps, visitor.getGlobalStrings());
            String code = inputEmitter.emit(inputNode, llvmType);
            String temp = extractTemp(code);
            return code +   emitStore(node.getName(), llvmType, temp);
        }

        if (node.getType().startsWith("List")) {
            if (node.initializer instanceof ListNode listNode) {
                // delega para o ListEmitter
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

            // Bitcast direto para i8*
            String tmp = temps.newTemp();
            sb.append("  ").append(tmp)
                    .append(" = bitcast [").append(len + 1).append(" x i8]* ").append(globalName)
                    .append(" to i8*\n")
                    .append(";;VAL:").append(tmp).append(";;TYPE:i8*\n");

            // inicializa .data
            String ptrField = temps.newTemp();
            sb.append("  ").append(ptrField)
                    .append(" = getelementptr inbounds %String, %String* ").append(varPtr)
                    .append(", i32 0, i32 0\n");
            sb.append("  store i8* ").append(tmp).append(", i8** ").append(ptrField).append("\n");

            // inicializa .length
            String lenField = temps.newTemp();
            sb.append("  ").append(lenField)
                    .append(" = getelementptr inbounds %String, %String* ").append(varPtr)
                    .append(", i32 0, i32 1\n");
            sb.append("  store i64 ").append(len).append(", i64* ").append(lenField).append("\n");

            return sb.toString();
        }

        // === Default: expressions ===
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

    private String extractType(String code) {
        int idx = code.indexOf(";;TYPE:");
        int endIdx = code.indexOf("\n", idx);
        if (endIdx == -1) endIdx = code.length();
        return code.substring(idx + 7, endIdx).trim();
    }
}