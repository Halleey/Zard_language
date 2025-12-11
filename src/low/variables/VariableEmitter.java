package low.variables;
import ast.inputs.InputNode;
import ast.lists.ListNode;
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

public class VariableEmitter {
    private final Map<String, TypeInfos> varTypes;
    private final TempManager temps;
    private final LLVisitorMain visitor;
    private final StringEmitter stringEmitter;
    private final Map<String, String> localVars = new HashMap<>(); // nome -> ponteiro (alloca)
    private final AllocaEmitter allocaEmitter;
    private final StoreEmitter storeEmitter;


    public VariableEmitter(Map<String, TypeInfos> varTypes, TempManager temps, LLVisitorMain visitor) {
        this.varTypes = varTypes;
        this.temps = temps;
        this.visitor = visitor;
        this.stringEmitter = new StringEmitter(temps, visitor.getGlobalStrings());
        this.allocaEmitter = new AllocaEmitter(varTypes, temps, visitor, localVars);
        this.storeEmitter = new StoreEmitter(stringEmitter, localVars);
    }

    public String emitAlloca(VariableDeclarationNode node) {
        return allocaEmitter.emit(node);
    }

    public String emitInit(VariableDeclarationNode node) {

        TypeInfos info = varTypes.get(node.getName());
        String srcType = info.getSourceType();

        if (node.initializer == null && srcType != null && srcType.startsWith("Struct<")) {
            return handleSpecializedStructDefaultInit(node, info);
        }

        if (node.initializer == null) {
            return handleDefaultInit(node, info);
        }

        if (node.initializer instanceof InputNode inputNode) {
            return handleInputInit(node, inputNode, info);
        }

        if (info.isList() && node.initializer instanceof ListNode listNode) {
            return handleListLiteralInit(node, listNode, info);
        }

        return handleNormalExpressionInit(node, info);
    }

    private String handleNormalExpressionInit(
            VariableDeclarationNode node,
            TypeInfos info
    ) {

        String llvmType = info.getLLVMType();
        String varPtr = getVarPtr(node.getName());

        String exprLLVM = node.initializer.accept(visitor);
        String temp = extractTemp(exprLLVM);
        String tempType = extractType(exprLLVM);

        StringBuilder sb = new StringBuilder(exprLLVM);

        if (!tempType.equals(llvmType)) {
            String castTmp = temps.newTemp();

            if (tempType.equals("double") && llvmType.equals("float")) {
                sb.append("  ").append(castTmp)
                        .append(" = fptrunc double ").append(temp).append(" to float\n");
                temp = castTmp;
            }
            else if (tempType.equals("i32") && llvmType.equals("double")) {
                sb.append("  ").append(castTmp)
                        .append(" = sitofp i32 ").append(temp).append(" to double\n");
                temp = castTmp;
            }
            else if (tempType.equals("double") && llvmType.equals("i32")) {
                sb.append("  ").append(castTmp)
                        .append(" = fptosi double ").append(temp).append(" to i32\n");
                temp = castTmp;
            }
        }

        sb.append(emitStore(node.getName(), llvmType, temp));

        return sb.toString();
    }


    private String handleSpecializedStructDefaultInit(
            VariableDeclarationNode node,
            TypeInfos info
    ) {

        String srcType = info.getSourceType();
        String llvmType = info.getLLVMType();
        String varPtr = getVarPtr(node.getName());

        // extrai T de Struct<T>
        String inner = srcType.substring("Struct<".length(), srcType.length() - 1).trim();
        String elemType = null;

        int genericIdx = inner.indexOf('<');
        if (genericIdx != -1) {
            int close = inner.lastIndexOf('>');
            elemType = inner.substring(genericIdx + 1, close).trim();
        }

        String structLLVM = llvmType.substring(0, llvmType.length() - 1);
        String tmpObj = temps.newTemp();
        String tmpList = temps.newTemp();
        String fieldPtr = temps.newTemp();

        visitor.registerListElementType(node.getName(), elemType);

        String createListCall;
        String listPtrType;

        switch (elemType) {
            case "int" -> {
                createListCall = "%struct.ArrayListInt* @arraylist_create_int(i64 10)";
                listPtrType = "%struct.ArrayListInt*";
            }
            case "double" -> {
                createListCall = "%struct.ArrayListDouble* @arraylist_create_double(i64 10)";
                listPtrType = "%struct.ArrayListDouble*";
            }
            case "boolean" -> {
                createListCall = "%struct.ArrayListBool* @arraylist_create_bool(i64 10)";
                listPtrType = "%struct.ArrayListBool*";
            }
            case "string" -> {
                createListCall = "%ArrayList* @arraylist_create(i64 10)";
                listPtrType = "%ArrayList*";
            }
            default -> {
                createListCall = "%ArrayList* @arraylist_create(i64 10)";
                listPtrType = "%ArrayList*";
            }
        }

        StringBuilder sb = new StringBuilder();

        sb.append("  ").append(tmpObj).append(" = alloca ").append(structLLVM).append("\n");
        sb.append(";;VAL:").append(tmpObj).append(";;TYPE:").append(structLLVM).append("*\n");

        sb.append("  ").append(tmpList).append(" = call ").append(createListCall).append("\n");
        sb.append(";;VAL:").append(tmpList).append(";;TYPE:").append(listPtrType).append("\n");

        sb.append("  ").append(fieldPtr)
                .append(" = getelementptr inbounds ")
                .append(structLLVM).append(", ").append(structLLVM)
                .append("* ").append(tmpObj).append(", i32 0, i32 0\n");

        sb.append("  store ").append(listPtrType).append(" ").append(tmpList)
                .append(", ").append(listPtrType).append("* ").append(fieldPtr).append("\n");

        sb.append("  store ").append(structLLVM).append("* ").append(tmpObj)
                .append(", ").append(structLLVM).append("** ").append(varPtr).append("\n");

        return sb.toString();
    }


    private String handleDefaultInit(VariableDeclarationNode node, TypeInfos info) {

        String varPtr = getVarPtr(node.getName());

        if (info.isList()) {
            return switch (info.getSourceType()) {
                case "List<int>" -> callArrayListCreateIntAndStore(varPtr);
                case "List<boolean>" -> callArrayListCreateBoolAndStore(varPtr);
                case "List<double>" -> callArrayListCreateDoubleAndStore(varPtr);
                default -> callArrayListCreateAndStore(varPtr);
            };
        }

        if ("string".equals(info.getSourceType())) {
            return stringEmitter.createEmptyString(varPtr);
        }

        if ("char".equals(info.getSourceType())) {
            return "  store i8 0, i8* " + varPtr + "\n";
        }

        return "";
    }


    private String handleInputInit(
            VariableDeclarationNode node,
            InputNode inputNode,
            TypeInfos info
    ) {
        InputEmitter inputEmitter = new InputEmitter(temps, visitor.getGlobalStrings());
        String code = inputEmitter.emit(inputNode, info.getLLVMType());
        String temp = extractTemp(code);
        return code + emitStore(node.getName(), info.getLLVMType(), temp);
    }



    private String handleListLiteralInit(
            VariableDeclarationNode node,
            ListNode listNode,
            TypeInfos info
    ) {
        visitor.registerListElementType(node.getName(), info.getElementType());

        String varPtr = getVarPtr(node.getName());

        return switch (info.getSourceType()) {
            case "List<int>" -> {
                IntListEmitter e = new IntListEmitter(temps);
                String code = e.emit(listNode, visitor);
                yield code + "  store %struct.ArrayListInt* " + extractTemp(code)
                        + ", %struct.ArrayListInt** " + varPtr + "\n";
            }
            case "List<boolean>" -> {
                ListBoolEmitter e = new ListBoolEmitter(temps);
                String code = e.emit(listNode, visitor);
                yield code + "  store %struct.ArrayListBool* " + extractTemp(code)
                        + ", %struct.ArrayListBool** " + varPtr + "\n";
            }
            case "List<double>" -> {
                ListDoubleEmitter e = new ListDoubleEmitter(temps);
                String code = e.emit(listNode, visitor);
                yield code + "  store %struct.ArrayListDouble* " + extractTemp(code)
                        + ", %struct.ArrayListDouble** " + varPtr + "\n";
            }
            default -> {
                ListEmitter e = new ListEmitter(temps);
                String code = e.emit(listNode, visitor);
                String tmp = extractTemp(code);
                String casted = temps.newTemp();
                yield code
                        + "  " + casted + " = bitcast i8* " + tmp + " to %ArrayList*\n"
                        + ";;VAL:" + casted + ";;TYPE:%ArrayList*\n"
                        + "  store %ArrayList* " + casted + ", %ArrayList** " + varPtr + "\n";
            }
        };
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
        return storeEmitter.emit(name, type, value);
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
