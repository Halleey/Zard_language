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
import low.lists.string.ListStringEmitter;
import low.main.TypeInfos;
import low.module.LLVisitorMain;

import low.module.builders.LLVMPointer;
import low.module.builders.LLVMTYPES;
import low.module.builders.LLVMValue;
import low.variables.exps.AllocaEmitter;
import low.variables.structs.StructInitEmitter;

import java.util.*;

import context.statics.symbols.*;


import java.util.*;

import context.statics.symbols.*;

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

    private final Deque<Map<String, String>> scopes = new ArrayDeque<>();
    private int scopeId = 0;

    public VariableEmitter(Map<String, TypeInfos> varTypes, TempManager temps, LLVisitorMain visitor) {
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
        System.out.println("[VariableEmitter] Entered scope #" + scopeId);
    }

    public void exitScope() {
        scopes.pop();
        System.out.println("[VariableEmitter] Exited scope, back to #" + scopeId);
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

    public LLVMValue emitDeclaration(VariableDeclarationNode node) {
        //System.out.println("[VariableEmitter] EmitDeclaration -> " + node.getName());
        LLVMValue alloca = allocaEmitter.emit(node);
      //  System.out.println("[VariableEmitter] Alloca: " + alloca.getName());

        LLVMValue initVal = emitInit(node);
      //  System.out.println("[VariableEmitter] Init LLVMValue: " + initVal.getName());

        String code = alloca.getCode() + initVal.getCode();
        return new LLVMValue(alloca.getType(), alloca.getName(), code);
    }

    private LLVMValue emitInit(VariableDeclarationNode node) {
        TypeInfos info = varTypes.get(node.getName());
        Type type = info.getType();
     //   System.out.println("[VariableEmitter] emitInit for var: " + node.getName() + ", type: " + type);

        if (type instanceof StructType) {
            if (node.getInitializer() == null) {
            //    System.out.println("[VariableEmitter] Struct no initializer -> calling StructInitEmitter");
                return structInitEmitter.emit(node, info);
            }
            LLVMValue val = node.getInitializer().accept(visitor);
            LLVMValue stored = storeEmitter.emit(node.getName(), val);
            return new LLVMValue(val.getType(), getVarPtr(node.getName()), val.getCode() + stored.getCode());
        }

        if (node.getInitializer() == null)
            return handleDefaultInit(node, info);

        if (node.getInitializer() instanceof InputNode inputNode)
            return handleInputInit(node, inputNode, info);

        if (type instanceof ListType && node.getInitializer() instanceof ListNode listNode) {
            LLVMValue val = handleListLiteralInit(node, listNode, info);
            return new LLVMValue(val.getType(), getVarPtr(node.getName()), val.getCode());
        }

        return handleNormalExpressionInit(node, info);
    }
    private LLVMValue handleNormalExpressionInit(VariableDeclarationNode node, TypeInfos info) {
      //  System.out.println("[VariableEmitter] NormalExpressionInit -> " + node.getName());
        return expressionInitEmitter.emit(node, info);
    }

    private LLVMValue handleDefaultInit(VariableDeclarationNode node, TypeInfos info) {
        String varPtr = getVarPtr(node.getName());
        Type type = info.getType();
      //  System.out.println("[VariableEmitter] handleDefaultInit -> " + node.getName() + " type: " + type);

        String code = "";


        if (type == PrimitiveTypes.INT) code = "  store i32 0, i32* " + varPtr + "\n";
        if (type == PrimitiveTypes.DOUBLE) code = "  store double 0.0, double* " + varPtr + "\n";
        if (type == PrimitiveTypes.FLOAT) code = "  store float 0.0, float* " + varPtr + "\n";
        if (type == PrimitiveTypes.BOOL) code = "  store i1 0, i1* " + varPtr + "\n";
        if (type == PrimitiveTypes.CHAR) code = "  store i8 0, i8* " + varPtr + "\n";
        if (type == PrimitiveTypes.STRING) return stringEmitter.createEmptyString(node.getName());

        return new LLVMValue(info.getLLVMType(), varPtr, code);
    }

    public StoreEmitter getStoreEmitter() {
        return storeEmitter;
    }

    private LLVMValue handleInputInit(VariableDeclarationNode node,
                                      InputNode inputNode,
                                      TypeInfos info) {
        System.out.println("[VariableEmitter] Input init -> " + node.getName());
        InputEmitter inputEmitter = new InputEmitter(temps, visitor.getGlobalStrings());
        LLVMValue val = inputEmitter.emit(inputNode, info.getLLVMType());
        LLVMValue stored = storeEmitter.emit(node.getName(), val);

        return new LLVMValue(val.getType(), val.getName(), val.getCode() + stored.getCode());
    }

    private LLVMValue handleListLiteralInit(VariableDeclarationNode node, ListNode listNode, TypeInfos info) {

        System.out.println("[VariableEmitter] List literal init -> " + node.getName());

        Type element = ((ListType) info.getType()).elementType();
        LLVMValue val;

        if (element == PrimitiveTypes.INT) {
            System.out.println("[VariableEmitter] Using IntListEmitter");
            val = new IntListEmitter(temps).emit(listNode, visitor);

        } else if (element == PrimitiveTypes.BOOL) {
            System.out.println("[VariableEmitter] Using ListBoolEmitter");
            val = new ListBoolEmitter(temps).emit(listNode, visitor);

        } else if (element == PrimitiveTypes.DOUBLE) {
            System.out.println("[VariableEmitter] Using ListDoubleEmitter");
            val = new ListDoubleEmitter(temps).emit(listNode, visitor);
        }
        else if (element == PrimitiveTypes.STRING) {
            val = new ListStringEmitter(temps).emit(listNode,visitor);
        } else {
            System.out.println("[VariableEmitter] Using generic ListEmitter");
            val = new ListEmitter(temps).emit(listNode, visitor);
        }

        LLVMValue stored = storeEmitter.emit(node.getName(), val);

        return new LLVMValue(val.getType(), val.getName(), val.getCode() + stored.getCode()
        );
    }


    public LLVMValue emitLoad(String name) {
        TypeInfos info = varTypes.get(name);
        String ptr = getVarPtr(name);
        String tmp = temps.newTemp();
      //  System.out.println("[VariableEmitter] emitLoad -> " + name + ", tmp: " + tmp);
        LLVMTYPES llvmType = info.getLLVMType();
        LLVMTYPES ptrType = new LLVMPointer(llvmType);
        String code = "  " + tmp + " = load "
                + llvmType + ", "
                + ptrType + " " + ptr + "\n";
        return new LLVMValue(info.getLLVMType(), tmp, code);
    }
}