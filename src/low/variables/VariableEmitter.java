package low.variables;
import ast.inputs.InputNode;
import ast.lists.ListNode;
import ast.variables.LiteralNode;
import low.functions.TypeMapper;
import low.inputs.InputEmitter;
import low.lists.generics.ListEmitter;
import low.lists.bool.ListBoolEmitter;
import low.lists.doubles.ListDoubleEmitter;
import low.lists.ints.IntListEmitter;
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
            case "char" -> {
                varTypes.put(node.getName(), "i8");
                return "  " + ptr + " = alloca i8\n;;VAL:" + ptr + ";;TYPE:i8\n";
            }
            case "List<int>" -> {
                varTypes.put(node.getName(), "%struct.ArrayListInt*");
                return "  " + ptr + " = alloca %struct.ArrayListInt*\n;;VAL:" + ptr + ";;TYPE:%struct.ArrayListInt*\n";
            }
            case "List<double>" -> {
                varTypes.put(node.getName(), "%struct.ArrayListDouble*");
                return "  " + ptr + " = alloca %struct.ArrayListDouble*\n;;VAL:" + ptr + ";;TYPE:%struct.ArrayListDouble*\n";
            }
            case "List<boolean>" -> {
                varTypes.put(node.getName(), "%struct.ArrayListBool*");
                return "  " + ptr + " = alloca %struct.ArrayListBool*\n;;VAL:" + ptr + ";;TYPE:%struct.ArrayListBool*\n";
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
                return switch (node.getType()) {
                    case "List<int>" -> callArrayListCreateIntAndStore(varPtr);
                    case "List<boolean>" -> callArrayListCreateBoolAndStore(varPtr);
                    case "List<double>" -> callArrayListCreateDoubleAndStore(varPtr);
                    default -> callArrayListCreateAndStore(varPtr);
                };
            }
            if (node.getType().equals("string"))
                return stringEmitter.createEmptyString(varPtr);
            if (node.getType().equals("char"))
                return "  store i8 0, i8* " + varPtr + "\n";
            return "";
        }

        if (node.initializer instanceof InputNode inputNode) {
            InputEmitter inputEmitter = new InputEmitter(temps, visitor.getGlobalStrings());
            String code = inputEmitter.emit(inputNode, llvmType);
            String temp = extractTemp(code);
            return code + emitStore(node.getName(), llvmType, temp);
        }

        // Listas
        if (node.getType().startsWith("List")) {
            if (node.initializer instanceof ListNode listNode) {
                String elementType = listNode.getList().getElementType();
                visitor.registerListElementType(node.getName(), elementType);

                String listLLVM;
                String tmpList;
                switch (node.getType()) {
                    case "List<int>" -> {
                        IntListEmitter listEmitter = new IntListEmitter(temps);
                        listLLVM = listEmitter.emit(listNode, visitor);
                        tmpList = extractTemp(listLLVM);
                        return listLLVM + "  store %struct.ArrayListInt* " + tmpList + ", %struct.ArrayListInt** " + varPtr + "\n";
                    }
                    case "List<boolean>" -> {
                        ListBoolEmitter boolEmitter = new ListBoolEmitter(temps);
                        listLLVM = boolEmitter.emit(listNode, visitor);
                        tmpList = extractTemp(listLLVM);
                        return listLLVM + "  store %struct.ArrayListBool* " + tmpList + ", %struct.ArrayListBool** " + varPtr + "\n";
                    }
                    case "List<double>" -> {
                        ListDoubleEmitter listEmitter = new ListDoubleEmitter(temps);
                        listLLVM = listEmitter.emit(listNode, visitor);
                        tmpList = extractTemp(listLLVM);
                        return listLLVM + "  store %struct.ArrayListDouble* " + tmpList + ", %struct.ArrayListDouble** " + varPtr + "\n";
                    }
                    default -> {
                        ListEmitter listEmitter = new ListEmitter(temps);
                        listLLVM = listEmitter.emit(listNode, visitor);
                        tmpList = extractTemp(listLLVM);
                        return listLLVM + "  store i8* " + tmpList + ", i8** " + varPtr + "\n";
                    }
                }
            }
        }
        if (node.getType().equals("string") && node.initializer instanceof LiteralNode lit) {
            return stringEmitter.createStringFromLiteral(varPtr, (String) lit.value.value());
        }

        String exprLLVM = node.initializer.accept(visitor);
        String temp = extractTemp(exprLLVM);
        String tempType = extractType(exprLLVM);

        String result = exprLLVM;
        if (!tempType.equals(llvmType)) {
            String castTmp = temps.newTemp();

            if (tempType.equals("i32") && llvmType.equals("double")) {
                result += "  " + castTmp + " = sitofp i32 " + temp + " to double\n" +
                        ";;VAL:" + castTmp + ";;TYPE:double\n";
                temp = castTmp;
            } else if (tempType.equals("double") && llvmType.equals("i32")) {
                result += "  " + castTmp + " = fptosi double " + temp + " to i32\n" +
                        ";;VAL:" + castTmp + ";;TYPE:i32\n";
                temp = castTmp;
            }
        }

        return result + emitStore(node.getName(), llvmType, temp);
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

    private String callArrayListCreateDoubleAndStore(String varPtr) {
        String tmp = temps.newTemp();
        return "  " + tmp + " = call %struct.ArrayListDouble* @arraylist_create_double(i64 4)\n" +
                ";;VAL:" + tmp + ";;TYPE:%struct.ArrayListDouble*\n" +
                "  store %struct.ArrayListDouble* " + tmp + ", %struct.ArrayListDouble** " + varPtr + "\n";
    }

    private String callArrayListCreateBoolAndStore(String varPtr){
        String tmp = temps.newTemp();
        return "  " + tmp + " = call %struct.ArrayListBool* @arraylist_create_bool(i64 4)\n"+
                ";;VAL: "+ tmp + ";;TYPE:%struct.ArrayListBool*\n"+
                "  store %struct.ArrayListBool* " + tmp + ", %struct.ArrayListBool** " + varPtr + "\n";
    }



    private String emitStore(String name, String type, String value) {
        return switch (type) {
            case "%String*", "%String" -> stringEmitter.emitStore(name, value);
            case "%struct.ArrayListInt*" ->
                    "  store %struct.ArrayListInt* " + value + ", %struct.ArrayListInt** %" + name + "\n";
            case "%struct.ArrayListDouble*" ->
                    "  store %struct.ArrayListDouble* " + value + ", %struct.ArrayListDouble** %" + name + "\n";
            case "%struct.ArrayListBool*" ->
                    "  store %struct.ArrayListBool* " + value + ", %struct.ArrayListBool** %" + name + "\n";
            default -> "  store " + type + " " + value + ", " + type + "* %" + name + "\n";
        };
    }

    /** Carrega variável para uso em expressão */
    public String emitLoad(String name) {
        String llvmType = varTypes.get(name);
        String ptr = localVars.get(name);
        String tmp = temps.newTemp();
        return "  " + tmp + " = load " + llvmType + ", " + llvmType + "* " + ptr +
                "\n;;VAL:" + tmp + ";;TYPE:" + llvmType + "\n";
    }

    public String getVarPtr(String name) { return localVars.get(name); }

    public void registerVarPtr(String name, String ptr) { localVars.put(name, ptr); }

    private String extractTemp(String code) {
        int lastValIdx = code.lastIndexOf(";;VAL:");
        if (lastValIdx == -1)
            throw new RuntimeException("Cannot find ;;VAL: in: " + code);
        int typeIdx = code.indexOf(";;TYPE:", lastValIdx);
        return code.substring(lastValIdx + 6, typeIdx).trim();
    }

    private String extractType(String code) {
        int lastTypeIdx = code.lastIndexOf(";;TYPE:");
        if (lastTypeIdx == -1)
            throw new RuntimeException("Cannot find ;;TYPE: in: " + code);
        return code.substring(lastTypeIdx + 7).trim();
    }

}