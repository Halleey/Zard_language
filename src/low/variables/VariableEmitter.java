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
    private final StringEmitter stringEmitter;
    private final Map<String, String> localVars = new HashMap<>();

    public VariableEmitter(Map<String, String> varTypes, TempManager temps, LLVisitorMain visitor) {
        this.varTypes = varTypes;
        this.temps = temps;
        this.visitor = visitor;
        this.stringEmitter = new StringEmitter(temps, visitor.getGlobalStrings());
    }

    public String mapLLVMType(String type) {
        return new TypeMapper().toLLVM(type);
    }

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
                    varTypes.put(node.getName(), "i8*");
                    return "  " + ptr + " = alloca i8*\n;;VAL:" + ptr + ";;TYPE:i8*\n";
                }
                String llvmType = mapLLVMType(node.getType());
                varTypes.put(node.getName(), llvmType);
                return "  " + ptr + " = alloca " + llvmType + "\n;;VAL:" + ptr + ";;TYPE:" + llvmType + "\n";
            }
        }
    }

    public String emitInit(VariableDeclarationNode node) {
        String llvmType = varTypes.get(node.getName());
        String varPtr = getVarPtr(node.getName());

        if (node.initializer == null) {
            if (node.getType().startsWith("List")) {
                return node.getType().equals("List<int>")
                        ? callArrayListCreateIntAndStore(varPtr)
                        : callArrayListCreateAndStore(varPtr);
            }
            if (node.getType().equals("string")) return stringEmitter.createEmptyString(varPtr);
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

        if (node.getType().equals("string") && node.initializer instanceof LiteralNode lit) {
            return stringEmitter.createStringFromLiteral(varPtr, (String) lit.value.getValue());
        }

        String exprLLVM = node.initializer.accept(visitor);
        String temp = extractTemp(exprLLVM);
        return exprLLVM + emitStore(node.getName(), llvmType, temp);
    }

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

    // === Load e Store ===
    private String emitStore(String name, String type, String value) {
        if (type.equals("%String*") || type.equals("%String")) {
            return stringEmitter.emitStore(name, value);
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