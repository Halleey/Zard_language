package low.variables;
import ast.inputs.InputNode;
import ast.lists.ListNode;
import ast.structs.StructInstanceNode;
import low.inputs.InputEmitter;
import low.lists.generics.ListEmitter;
import low.lists.bool.ListBoolEmitter;
import low.lists.doubles.ListDoubleEmitter;
import low.lists.ints.IntListEmitter;
import low.main.TypeInfos;
import low.module.LLVisitorMain;
import low.TempManager;
import ast.variables.VariableDeclarationNode;
import low.variables.exps.AllocaEmitter;
import low.variables.structs.StructInitEmitter;

import java.util.HashMap;
import java.util.Map;
import java.util.*;


public class VariableEmitter {

    private final Map<String, TypeInfos> varTypes;
    private final TempManager temps;
    private final LLVisitorMain visitor;
    private final StringEmitter stringEmitter;
    private final AllocaEmitter allocaEmitter;
    private final StoreEmitter storeEmitter;
    private final StructInitEmitter structInitEmitter;
    private final ExpressionInitEmitter expressionInitEmitter;
    private final TempExtractor tempExtractor = new TempExtractor();

    // ======= pilha de escopos =======
    private final Deque<Map<String, String>> scopes = new ArrayDeque<>();
    private int scopeId = 0; // ID para gerar nomes únicos

    public VariableEmitter(Map<String, TypeInfos> varTypes, TempManager temps, LLVisitorMain visitor) {
        this.varTypes = varTypes;
        this.temps = temps;
        this.visitor = visitor;
        this.stringEmitter = new StringEmitter(temps, visitor.getGlobalStrings(), this);
        this.allocaEmitter = new AllocaEmitter(varTypes, temps, visitor, this); // passou this
        this.storeEmitter = new StoreEmitter(stringEmitter, this);
        this.structInitEmitter = new StructInitEmitter(temps, visitor, this);
        this.expressionInitEmitter = new ExpressionInitEmitter(temps, visitor, storeEmitter, tempExtractor);

        // escopo global
        scopes.push(new HashMap<>());
    }

    public void enterScope() {
        scopeId++;
        scopes.push(new HashMap<>());
    }

    public void exitScope() {
        scopes.pop();
    }

    public int getScopeId() {
        return scopeId;
    }

    private Map<String, String> currentScope() {
        return scopes.peek();
    }

    public void registerVarPtr(String name, String llvmPtr) {
        currentScope().put(name, llvmPtr); // escopo atual
    }

    // ao buscar:
    public String getVarPtr(String name) {
        for (Map<String,String> scope : scopes) {
            if (scope.containsKey(name)) return scope.get(name);
        }
        throw new RuntimeException("Ptr não encontrado: " + name);
    }


    public String emitAlloca(VariableDeclarationNode node) {
        return allocaEmitter.emit(node);
    }

    public String emitInit(VariableDeclarationNode node) {
        TypeInfos info = varTypes.get(node.getName());
        String srcType = info.getSourceType();
        String varPtr = getVarPtr(node.getName());

        if (srcType != null && srcType.startsWith("Struct<")) {
            if (node.initializer == null) {
                return structInitEmitter.emit(node, info);
            }
            String code = node.initializer.accept(visitor);
            String tmp = extractTemp(code);
            return code
                    + "  store " + info.getLLVMType() + " " + tmp
                    + ", " + info.getLLVMType() + "* " + varPtr + "\n";
        }

        if (node.initializer == null) return handleDefaultInit(node, info);

        if (node.initializer instanceof InputNode inputNode) {
            return handleInputInit(node, inputNode, info);
        }

        if (info.isList() && node.initializer instanceof ListNode listNode) {
            return handleListLiteralInit(node, listNode, info);
        }

        return handleNormalExpressionInit(node, info);
    }

    private String handleNormalExpressionInit(VariableDeclarationNode node, TypeInfos info) {
        return expressionInitEmitter.emit(node, info);
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

        return switch (info.getSourceType()) {
            case "int" ->
                    "  store i32 0, i32* " + varPtr + "\n";
            case "double" ->
                    "  store double 0.0, double* " + varPtr + "\n";
            case "float" ->
                    "  store float 0.0, float* " + varPtr + "\n";
            case "boolean" ->
                    "  store i1 0, i1* " + varPtr + "\n";
            case "char" ->
                    "  store i8 0, i8* " + varPtr + "\n";
            case "string" ->
                    stringEmitter.createEmptyString(node.getName());
            default ->
                    "";
        };
    }


    private String handleInputInit(VariableDeclarationNode node, InputNode inputNode, TypeInfos info) {
        InputEmitter inputEmitter = new InputEmitter(temps, visitor.getGlobalStrings());
        String code = inputEmitter.emit(inputNode, info.getLLVMType());
        String temp = extractTemp(code);
        return code + emitStore(node.getName(), info.getLLVMType(), temp);
    }

    private String handleListLiteralInit(VariableDeclarationNode node, ListNode listNode, TypeInfos info) {
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

    // ================= UTILIDADES =================
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

    public String emitStore(String name, String type, String value) {
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

    private String extractTemp(String code) {
        int lastValIdx = code.lastIndexOf(";;VAL:");
        int typeIdx = code.indexOf(";;TYPE:", lastValIdx);
        return code.substring(lastValIdx + 6, typeIdx).trim();
    }
}
