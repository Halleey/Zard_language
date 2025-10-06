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

    // === Alocações ===
    public String emitAlloca(VariableDeclarationNode node) {
        String ptr = "%" + node.getName();
        localVars.put(node.getName(), ptr);

        switch (node.getType()) {
            case "string" -> {
                varTypes.put(node.getName(), "%String*");
                return "  " + ptr + " = alloca %String*\n;;VAL:" + ptr + ";;TYPE:%String*\n";
            }
            case "List<int>" -> {
                varTypes.put(node.getName(), "%struct.ArrayListInt*");
                return "  " + ptr + " = alloca %struct.ArrayListInt*\n;;VAL:" + ptr + ";;TYPE:%struct.ArrayListInt*\n";
            }
            default -> {
                if (node.getType().startsWith("List")) {
                    varTypes.put(node.getName(), "i8*"); // listas genéricas
                    return "  " + ptr + " = alloca i8*\n;;VAL:" + ptr + ";;TYPE:i8*\n";
                }
                String llvmType = mapLLVMType(node.getType());
                varTypes.put(node.getName(), llvmType);
                return "  " + ptr + " = alloca " + llvmType + "\n;;VAL:" + ptr + ";;TYPE:" + llvmType + "\n";
            }
        }
    }

    // === Inicialização de variáveis ===
    public String emitInit(VariableDeclarationNode node) {
        String llvmType = varTypes.get(node.getName());
        String varPtr = getVarPtr(node.getName());
        StringBuilder sb = new StringBuilder();

        // Sem inicializador
        if (node.initializer == null) {
            if (node.getType().startsWith("List")) {
                return node.getType().equals("List<int>")
                        ? callArrayListCreateIntAndStore(varPtr)
                        : callArrayListCreateAndStore(varPtr);
            }
            if (node.getType().equals("string")) return createEmptyString(varPtr);
            return "";
        }

        // Inicializador via input
        if (node.initializer instanceof InputNode inputNode) {
            InputEmitter inputEmitter = new InputEmitter(temps, visitor.getGlobalStrings());
            String code = inputEmitter.emit(inputNode, llvmType);
            String temp = extractTemp(code);
            return code + emitStore(node.getName(), llvmType, temp);
        }

        // Inicializador é lista
        if (node.getType().startsWith("List")) {
            if (node.initializer instanceof ListNode listNode) {
                ListEmitter listEmitter = new ListEmitter(temps);
                String listLLVM = listEmitter.emit(listNode, visitor);
                String tmpList = extractTemp(listLLVM);

                String elementType = listNode.getList().getElementType();
                visitor.registerListElementType(node.getName(), elementType);

                // Armazenamento correto por tipo
                if (node.getType().equals("List<int>")) {
                    return listLLVM + "  store %struct.ArrayListInt* " + tmpList + ", %struct.ArrayListInt** " + varPtr + "\n";
                } else {
                    return listLLVM + "  store i8* " + tmpList + ", i8** " + varPtr + "\n";
                }
            } else {
                return node.getType().equals("List<int>")
                        ? callArrayListCreateIntAndStore(varPtr)
                        : callArrayListCreateAndStore(varPtr);
            }
        }

        // Inicializador é string literal
        if (node.getType().equals("string") && node.initializer instanceof LiteralNode lit) {
            return createStringFromLiteral(varPtr, (String) lit.value.getValue());
        }

        // Inicializador padrão (expressões)
        String exprLLVM = node.initializer.accept(visitor);
        String temp = extractTemp(exprLLVM);
        return exprLLVM + emitStore(node.getName(), llvmType, temp);
    }

    // === Helpers para listas e strings ===
    private String callArrayListCreateAndStore(String varPtr) {
        String tmp = temps.newTemp();
        return "  " + tmp + " = call i8* @arraylist_create(i64 4)\n" +
                ";;VAL:" + tmp + ";;TYPE:i8*\n" +
                "  store i8* " + tmp + ", i8** " + varPtr + "\n";
    }

    private String callArrayListCreateIntAndStore(String varPtr) {
        String tmp = temps.newTemp();
        return "  " + tmp + " = call %struct.ArrayListInt* @arraylist_create_int(i64 4)\n" +
                ";;VAL:" + tmp + ";;TYPE:%struct.ArrayListInt*\n" +
                "  store %struct.ArrayListInt* " + tmp + ", %struct.ArrayListInt** " + varPtr + "\n";
    }

    private String createEmptyString(String varPtr) {
        String tmpRaw = temps.newTemp();
        String tmpStruct = temps.newTemp();
        StringBuilder sb = new StringBuilder();
        sb.append("  ").append(tmpRaw).append(" = call i8* @malloc(i64 ptrtoint (%String* getelementptr (%String, %String* null, i32 1) to i64))\n");
        sb.append("  ").append(tmpStruct).append(" = bitcast i8* ").append(tmpRaw).append(" to %String*\n");
        String ptrField = temps.newTemp();
        sb.append("  ").append(ptrField).append(" = getelementptr inbounds %String, %String* ").append(tmpStruct).append(", i32 0, i32 0\n");
        sb.append("  store i8* null, i8** ").append(ptrField).append("\n");
        String lenField = temps.newTemp();
        sb.append("  ").append(lenField).append(" = getelementptr inbounds %String, %String* ").append(tmpStruct).append(", i32 0, i32 1\n");
        sb.append("  store i64 0, i64* ").append(lenField).append("\n");
        sb.append("  store %String* ").append(tmpStruct).append(", %String** ").append(varPtr).append("\n");
        return sb.toString();
    }

    private String createStringFromLiteral(String varPtr, String literal) {
        StringBuilder sb = new StringBuilder();
        int len = literal.length();
        String globalName = visitor.getGlobalStrings().getGlobalName(literal);
        String tmpRaw = temps.newTemp();
        String tmpStruct = temps.newTemp();

        sb.append("  ").append(tmpRaw).append(" = call i8* @malloc(i64 ptrtoint (%String* getelementptr (%String, %String* null, i32 1) to i64))\n");
        sb.append("  ").append(tmpStruct).append(" = bitcast i8* ").append(tmpRaw).append(" to %String*\n");

        String tmpData = temps.newTemp();
        sb.append("  ").append(tmpData).append(" = bitcast [").append(len+1).append(" x i8]* ").append(globalName).append(" to i8*\n");

        String ptrField = temps.newTemp();
        sb.append("  ").append(ptrField).append(" = getelementptr inbounds %String, %String* ").append(tmpStruct).append(", i32 0, i32 0\n");
        sb.append("  store i8* ").append(tmpData).append(", i8** ").append(ptrField).append("\n");

        String lenField = temps.newTemp();
        sb.append("  ").append(lenField).append(" = getelementptr inbounds %String, %String* ").append(tmpStruct).append(", i32 0, i32 1\n");
        sb.append("  store i64 ").append(len).append(", i64* ").append(lenField).append("\n");

        sb.append("  store %String* ").append(tmpStruct).append(", %String** ").append(varPtr).append("\n");
        return sb.toString();
    }

    // === Load e Store ===
    private String emitStore(String name, String type, String value) {
        if (type.equals("%String*") || type.equals("%String")) {
            return "  store %String* " + value + ", %String** %" + name + "\n";
        }
        if (type.equals("%struct.ArrayListInt*")) {
            return "  store %struct.ArrayListInt* " + value + ", %struct.ArrayListInt** %" + name + "\n";
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

    public String getVarPtr(String name) { return localVars.get(name); }

    public void registerVarPtr(String name, String ptr) { localVars.put(name, ptr); }

    private String extractTemp(String code) {
        int lastValIdx = code.lastIndexOf(";;VAL:");
        if (lastValIdx == -1) throw new RuntimeException("Cannot find ;;VAL: in: " + code);
        int typeIdx = code.indexOf(";;TYPE:", lastValIdx);
        return code.substring(lastValIdx + 6, typeIdx).trim();
    }
}
