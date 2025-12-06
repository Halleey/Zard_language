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
import low.main.TypeInfos;
import low.module.LLVisitorMain;
import low.TempManager;
import ast.variables.VariableDeclarationNode;

import java.util.HashMap;
import java.util.Map;
import java.util.HashMap;
import java.util.Map;

public class VariableEmitter {
    private final Map<String, TypeInfos> varTypes;
    private final TempManager temps;
    private final LLVisitorMain visitor;
    private final StringEmitter stringEmitter;
    private final Map<String, String> localVars = new HashMap<>(); // nome -> ponteiro (alloca)

    public VariableEmitter(Map<String, TypeInfos> varTypes, TempManager temps, LLVisitorMain visitor) {
        this.varTypes = varTypes;
        this.temps = temps;
        this.visitor = visitor;
        this.stringEmitter = new StringEmitter(temps, visitor.getGlobalStrings());
    }

    private String mapLLVMType(String type) {
        return new TypeMapper().toLLVM(type);
    }

    public String emitAlloca(VariableDeclarationNode node) {
        // right now o ponteiro é centralizado pelo TempManager
        String ptr = temps.newNamedVar(node.getName());
        localVars.put(node.getName(), ptr);

        String srcType = node.getType();
        String llvmType;
        String elemType;

        switch (srcType) {
            case "string" -> {
                llvmType = "%String*";
                varTypes.put(node.getName(), new TypeInfos(srcType, llvmType, null));
                return "  " + ptr + " = alloca %String*\n;;VAL:" + ptr + ";;TYPE:%String*\n";
            }
            case "char" -> {
                llvmType = "i8";
                varTypes.put(node.getName(), new TypeInfos(srcType, llvmType, null));
                return "  " + ptr + " = alloca i8\n;;VAL:" + ptr + ";;TYPE:i8\n";
            }
            case "List<int>" -> {
                llvmType = "%struct.ArrayListInt*";
                elemType = "int";
                varTypes.put(node.getName(), new TypeInfos(srcType, llvmType, elemType));
                return "  " + ptr + " = alloca %struct.ArrayListInt*\n;;VAL:" + ptr + ";;TYPE:%struct.ArrayListInt*\n";
            }
            case "List<double>" -> {
                llvmType = "%struct.ArrayListDouble*";
                elemType = "double";
                varTypes.put(node.getName(), new TypeInfos(srcType, llvmType, elemType));
                return "  " + ptr + " = alloca %struct.ArrayListDouble*\n;;VAL:" + ptr + ";;TYPE:%struct.ArrayListDouble*\n";
            }
            case "List<boolean>" -> {
                llvmType = "%struct.ArrayListBool*";
                elemType = "boolean";
                varTypes.put(node.getName(), new TypeInfos(srcType, llvmType, elemType));
                return "  " + ptr + " = alloca %struct.ArrayListBool*\n;;VAL:" + ptr + ";;TYPE:%struct.ArrayListBool*\n";
            }
            default -> {
                if (srcType.startsWith("List")) {
                    llvmType = "%ArrayList*";
                    elemType = srcType.substring(5, srcType.length() - 1);
                    varTypes.put(node.getName(), new TypeInfos(srcType, llvmType, elemType));
                    return "  " + ptr + " = alloca %ArrayList*\n;;VAL:" + ptr + ";;TYPE:%ArrayList*\n";
                }
                llvmType = mapLLVMType(srcType);
                varTypes.put(node.getName(), new TypeInfos(srcType, llvmType, null));
                return "  " + ptr + " = alloca " + llvmType + "\n;;VAL:" + ptr + ";;TYPE:" + llvmType + "\n";
            }
        }
    }

    public String emitInit(VariableDeclarationNode node) {
        TypeInfos info = varTypes.get(node.getName());
        String srcType = info.getSourceType();
        String llvmType = info.getLLVMType();
        String varPtr = getVarPtr(node.getName()); // ponteiro REAL da variável

        if (node.initializer == null && srcType != null && srcType.startsWith("Struct<")) {

            String inner = srcType.substring("Struct<".length(), srcType.length() - 1).trim();

            String baseName = inner;
            String elemType = null;

            int genericIdx = inner.indexOf('<');
            if (genericIdx != -1) {
                int close = inner.lastIndexOf('>');
                elemType = inner.substring(genericIdx + 1, close).trim();
            }

            if (!llvmType.endsWith("*")) {
                throw new RuntimeException("Esperado ponteiro para struct especializada: " + llvmType);
            }

            String structLLVM = llvmType.substring(0, llvmType.length() - 1); // %Set_int
            String tmpObj   = temps.newTemp();
            String tmpList  = temps.newTemp();
            String fieldPtr = temps.newTemp();

            String createListCall;
            String arrayListPtrType;

            switch (elemType) {
                case "int" -> {
                    createListCall = "%struct.ArrayListInt* @arraylist_create_int(i64 10)";
                    arrayListPtrType = "%struct.ArrayListInt*";
                }
                case "double" -> {
                    createListCall = "%struct.ArrayListDouble* @arraylist_create_double(i64 10)";
                    arrayListPtrType = "%struct.ArrayListDouble*";
                }
                case "boolean" -> {
                    createListCall = "%struct.ArrayListBool* @arraylist_create_bool(i64 10)";
                    arrayListPtrType = "%struct.ArrayListBool*";
                }
                case "string" -> {
                    createListCall = "%ArrayList* @arraylist_create(i64 10)";
                    arrayListPtrType = "%ArrayList*";
                }
                default -> {
                    createListCall = "%ArrayList* @arraylist_create(i64 10)";
                    arrayListPtrType = "%ArrayList*";
                }
            }

            visitor.registerListElementType(node.getName(), elemType);

            StringBuilder sb = new StringBuilder();

            sb.append("  ").append(tmpObj)
                    .append(" = alloca ").append(structLLVM).append("\n")
                    .append(";;VAL:").append(tmpObj)
                    .append(";;TYPE:").append(structLLVM).append("\n");

            sb.append("  ").append(tmpList)
                    .append(" = call ").append(createListCall).append("\n")
                    .append(";;VAL:").append(tmpList)
                    .append(";;TYPE:").append(arrayListPtrType).append("\n");

            sb.append("  ").append(fieldPtr)
                    .append(" = getelementptr inbounds ").append(structLLVM)
                    .append(", ").append(structLLVM).append("* ").append(tmpObj)
                    .append(", i32 0, i32 0\n");

            sb.append("  store ").append(arrayListPtrType)
                    .append(" ").append(tmpList)
                    .append(", ").append(arrayListPtrType).append("* ").append(fieldPtr).append("\n");

            sb.append("  store ").append(structLLVM).append("* ").append(tmpObj)
                    .append(", ").append(structLLVM).append("** ").append(varPtr).append("\n");

            return sb.toString();
        }

        // =============== Inicialização padrão =================
        if (node.initializer == null) {
            if (info.isList()) {
                return switch (info.getSourceType()) {
                    case "List<int>" -> callArrayListCreateIntAndStore(varPtr);
                    case "List<boolean>" -> callArrayListCreateBoolAndStore(varPtr);
                    case "List<double>" -> callArrayListCreateDoubleAndStore(varPtr);
                    default -> callArrayListCreateAndStore(varPtr);
                };
            }
            if (info.getSourceType().equals("string"))
                return stringEmitter.createEmptyString(varPtr);
            if (info.getSourceType().equals("char"))
                return "  store i8 0, i8* " + varPtr + "\n";
            return "";
        }

        // =============== input(...) =================
        if (node.initializer instanceof InputNode inputNode) {
            InputEmitter inputEmitter = new InputEmitter(temps, visitor.getGlobalStrings());
            String code = inputEmitter.emit(inputNode, llvmType);
            String temp = extractTemp(code);
            return code + emitStore(node.getName(), llvmType, temp);
        }

        // =============== List com initializer literal =================
        if (info.isList() && node.initializer instanceof ListNode listNode) {
            visitor.registerListElementType(node.getName(), info.getElementType());

            String listLLVM;
            String tmpList;
            switch (info.getSourceType()) {
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
                    String casted = temps.newTemp();
                    return listLLVM
                            + "  " + casted + " = bitcast i8* " + tmpList + " to %ArrayList*\n"
                            + ";;VAL:" + casted + ";;TYPE:%ArrayList*\n"
                            + "  store %ArrayList* " + casted + ", %ArrayList** " + varPtr + "\n";
                }
            }
        }

        if (info.getSourceType().equals("string") && node.initializer instanceof LiteralNode lit) {
            return stringEmitter.createStringFromLiteral(varPtr, (String) lit.value.value());
        }

        String exprLLVM = node.initializer.accept(visitor);
        String temp = extractTemp(exprLLVM);
        String tempType = extractType(exprLLVM);

        String result = exprLLVM;
        if (!tempType.equals(llvmType)) {
            String castTmp = temps.newTemp();

            if (tempType.equals("double") && llvmType.equals("float")) {
                result += "  " + castTmp + " = fptrunc double " + temp + " to float\n" +
                        ";;VAL:" + castTmp + ";;TYPE:float\n";
                temp = castTmp;
            }

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
        String casted = temps.newTemp();
        return "  " + tmp + " = call i8* @arraylist_create(i64 4)\n" +
                "  " + casted + " = bitcast i8* " + tmp + " to %ArrayList*\n" +
                ";;VAL:" + casted + ";;TYPE:%ArrayList*\n" +
                "  store %ArrayList* " + casted + ", %ArrayList** " + varPtr + "\n";
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

    private String callArrayListCreateBoolAndStore(String varPtr) {
        String tmp = temps.newTemp();
        return "  " + tmp + " = call %struct.ArrayListBool* @arraylist_create_bool(i64 4)\n" +
                ";;VAL:" + tmp + ";;TYPE:%struct.ArrayListBool*\n" +
                "  store %struct.ArrayListBool* " + tmp + ", %struct.ArrayListBool** " + varPtr + "\n";
    }

    private String emitStore(String name, String type, String value) {
        // String continua delegando pro StringEmitter, que já sabe achar o ponteiro
        return switch (type) {
            case "%String*", "%String" -> stringEmitter.emitStore(name, value);
            case "%struct.ArrayListInt*" ->
                    "  store %struct.ArrayListInt* " + value + ", %struct.ArrayListInt** " + getVarPtr(name) + "\n";
            case "%struct.ArrayListDouble*" ->
                    "  store %struct.ArrayListDouble* " + value + ", %struct.ArrayListDouble** " + getVarPtr(name) + "\n";
            case "%struct.ArrayListBool*" ->
                    "  store %struct.ArrayListBool* " + value + ", %struct.ArrayListBool** " + getVarPtr(name) + "\n";
            case "%ArrayList*" ->
                    "  store %ArrayList* " + value + ", %ArrayList** " + getVarPtr(name) + "\n";
            default ->
                    "  store " + type + " " + value + ", " + type + "* " + getVarPtr(name) + "\n";
        };
    }

    public String emitLoad(String name) {
        TypeInfos info = varTypes.get(name);
        String llvmType = info.getLLVMType();
        String ptr = getVarPtr(name);
        String tmp = temps.newTemp();
        return "  " + tmp + " = load " + llvmType + ", " + llvmType + "* " + ptr +
                "\n;;VAL:" + tmp + ";;TYPE:" + llvmType + "\n";
    }

    public String getVarPtr(String name) {
        String ptr = localVars.get(name);
        if (ptr == null) {
            throw new RuntimeException("Ptr não encontrado para variável: " + name);
        }
        return ptr;
    }

    public void registerVarPtr(String name, String ptr) {
        localVars.put(name, ptr);
    }

    private String extractTemp(String code) {
        int lastValIdx = code.lastIndexOf(";;VAL:");
        int typeIdx = code.indexOf(";;TYPE:", lastValIdx);
        return code.substring(lastValIdx + 6, typeIdx).trim();
    }

    private String extractType(String code) {
        int lastTypeIdx = code.lastIndexOf(";;TYPE:");
        return code.substring(lastTypeIdx + 7).trim();
    }
}
