package low.variables;

import ast.inputs.InputNode;
import ast.lists.ListNode;
import ast.variables.VariableDeclarationNode;
import context.statics.symbols.*;
import low.TempManager;
import low.inputs.InputEmitter;
import low.lists.bool.ListBoolEmitter;
import low.lists.doubles.ListDoubleEmitter;
import low.lists.generics.ListEmitter;
import low.lists.ints.IntListEmitter;
import low.main.TypeInfos;
import low.module.LLVisitorMain;

import low.module.builders.LLVMValue;
import low.variables.exps.AllocaEmitter;
import low.variables.structs.StructInitEmitter;

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

    public StoreEmitter getStoreEmitter() {
        return storeEmitter;
    }

    private final Deque<Map<String, String>> scopes = new ArrayDeque<>();
    private int scopeId = 0;

    public VariableEmitter(Map<String, TypeInfos> varTypes,
                           TempManager temps,
                           LLVisitorMain visitor) {

        this.varTypes = varTypes;
        this.temps = temps;
        this.visitor = visitor;

        this.stringEmitter = new StringEmitter(temps, visitor.getGlobalStrings(), this);
        this.allocaEmitter = new AllocaEmitter(varTypes, temps, visitor, this);
        this.storeEmitter = new StoreEmitter(stringEmitter, this);
        this.structInitEmitter = new StructInitEmitter(temps, visitor, this);
        this.expressionInitEmitter = new ExpressionInitEmitter(temps, visitor, storeEmitter);

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

    public void registerVarPtr(String name, String llvmPtr) {
        scopes.peek().put(name, llvmPtr);
    }

    public String getVarPtr(String name) {
        for (Map<String, String> scope : scopes) {
            if (scope.containsKey(name)) return scope.get(name);
        }
        throw new RuntimeException("Ptr não encontrado: " + name);
    }

    public String emit(VariableDeclarationNode node) {
        StringBuilder llvm = new StringBuilder();

        LLVMValue alloca = allocaEmitter.emit(node);
        llvm.append(alloca.getCode());

        llvm.append(emitInit(node));

        return llvm.toString();
    }

    private String emitInit(VariableDeclarationNode node) {

        TypeInfos info = varTypes.get(node.getName());
        Type type = info.getType();

        if (type instanceof StructType) {

            if (node.getInitializer() == null) {
                LLVMValue val = structInitEmitter.emit(node, info);
                return val.getCode();
            }

            LLVMValue val = node.getInitializer().accept(visitor);
            return val.getCode() + storeEmitter.emit(node.getName(), val);
        }

        if (node.getInitializer() == null)
            return handleDefaultInit(node, info);

        if (node.getInitializer() instanceof InputNode inputNode)
            return handleInputInit(node, inputNode, info);

        if (type instanceof ListType && node.getInitializer() instanceof ListNode listNode)
            return handleListLiteralInit(node, listNode, info);

        return handleNormalExpressionInit(node, info);
    }

    private String handleNormalExpressionInit(VariableDeclarationNode node, TypeInfos info) {
        LLVMValue val = expressionInitEmitter.emit(node, info);
        return val.getCode();
    }

    private String handleDefaultInit(VariableDeclarationNode node, TypeInfos info) {

        String varPtr = getVarPtr(node.getName());
        Type type = info.getType();

        if (type instanceof ListType listType) {

            Type element = listType.elementType();

            if (element == PrimitiveTypes.INT)
                return callArrayListCreateIntAndStore(varPtr);

            if (element == PrimitiveTypes.BOOL)
                return callArrayListCreateBoolAndStore(varPtr);

            if (element == PrimitiveTypes.DOUBLE)
                return callArrayListCreateDoubleAndStore(varPtr);

            if (element == PrimitiveTypes.STRING)
                return callArrayListCreateStringAndStore(varPtr);

            return callArrayListCreateAndStore(varPtr);
        }

        if (type == PrimitiveTypes.INT)
            return "  store i32 0, i32* " + varPtr + "\n";

        if (type == PrimitiveTypes.DOUBLE)
            return "  store double 0.0, double* " + varPtr + "\n";

        if (type == PrimitiveTypes.FLOAT)
            return "  store float 0.0, float* " + varPtr + "\n";

        if (type == PrimitiveTypes.BOOL)
            return "  store i1 0, i1* " + varPtr + "\n";

        if (type == PrimitiveTypes.CHAR)
            return "  store i8 0, i8* " + varPtr + "\n";

        if (type == PrimitiveTypes.STRING) {
            LLVMValue val = stringEmitter.createEmptyString(node.getName());
            return val.getCode();
        }

        return "";
    }

    private String handleInputInit(VariableDeclarationNode node,
                                   InputNode inputNode,
                                   TypeInfos info) {

        InputEmitter inputEmitter = new InputEmitter(temps, visitor.getGlobalStrings());
        LLVMValue val = inputEmitter.emit(inputNode, info.getLLVMType());

        return val.getCode() + storeEmitter.emit(node.getName(), val);
    }

    private String handleListLiteralInit(VariableDeclarationNode node,
                                         ListNode listNode,
                                         TypeInfos info) {

        Type element = ((ListType) info.getType()).elementType();

        LLVMValue val;

        if (element == PrimitiveTypes.INT) {
            val = new IntListEmitter(temps).emit(listNode, visitor);
        } else if (element == PrimitiveTypes.BOOL) {
            val = new ListBoolEmitter(temps).emit(listNode, visitor);
        } else if (element == PrimitiveTypes.DOUBLE) {
            val = new ListDoubleEmitter(temps).emit(listNode, visitor);
        } else {
            val = new ListEmitter(temps).emit(listNode, visitor);
        }

        return val.getCode() + storeEmitter.emit(node.getName(), val);
    }

    private String callArrayListCreateAndStore(String varPtr) {
        String tmp = temps.newTemp();
        String casted = temps.newTemp();

        return "  " + tmp + " = call i8* @arraylist_create(i64 4)\n"
                + "  " + casted + " = bitcast i8* " + tmp + " to %ArrayList*\n"
                + "  store %ArrayList* " + casted + ", %ArrayList** " + varPtr + "\n";
    }

    private String callArrayListCreateStringAndStore(String varPtr) {
        String tmp = temps.newTemp();

        return "  " + tmp + " = call %ArrayListString* @arraylist_string_create(i64 4)\n"
                + "  store %ArrayListString* " + tmp + ", %ArrayListString** " + varPtr + "\n";
    }

    private String callArrayListCreateIntAndStore(String varPtr) {
        String tmp = temps.newTemp();

        return "  " + tmp + " = call %struct.ArrayListInt* @arraylist_create_int(i64 4)\n"
                + "  store %struct.ArrayListInt* " + tmp + ", %struct.ArrayListInt** " + varPtr + "\n";
    }

    private String callArrayListCreateDoubleAndStore(String varPtr) {
        String tmp = temps.newTemp();

        return "  " + tmp + " = call %struct.ArrayListDouble* @arraylist_create_double(i64 4)\n"
                + "  store %struct.ArrayListDouble* " + tmp + ", %struct.ArrayListDouble** " + varPtr + "\n";
    }

    private String callArrayListCreateBoolAndStore(String varPtr) {
        String tmp = temps.newTemp();

        return "  " + tmp + " = call %struct.ArrayListBool* @arraylist_create_bool(i64 4)\n"
                + "  store %struct.ArrayListBool* " + tmp + ", %struct.ArrayListBool** " + varPtr + "\n";
    }

    public LLVMValue emitLoad(String name) {
        TypeInfos info = varTypes.get(name);
        String ptr = getVarPtr(name);

        String tmp = temps.newTemp();

        String code = "  " + tmp + " = load " + info.getLLVMType()
                + ", " + info.getLLVMType() + "* " + ptr + "\n";

        return new LLVMValue(info.getLLVMType(), tmp, code);
    }
}