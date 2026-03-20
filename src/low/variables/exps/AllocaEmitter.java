package low.variables.exps;

import ast.variables.VariableDeclarationNode;
import context.statics.symbols.ListType;
import context.statics.symbols.PrimitiveTypes;
import context.statics.symbols.StructType;
import context.statics.symbols.Type;
import low.TempManager;
import low.main.TypeInfos;
import low.module.LLVisitorMain;
import low.module.builders.LLVMTYPES;
import low.module.builders.LLVMValue;
import low.module.builders.lists.LLVMArrayList;
import low.module.builders.mappers.LLVMTypeMapper;
import low.module.builders.primitives.*;
import low.module.builders.structs.LLVMStruct;
import low.variables.VariableEmitter;

import java.util.Map;

public class AllocaEmitter {

    private final Map<String, TypeInfos> varTypes;
    private final TempManager temps;
    private final LLVisitorMain visitor;
    private final VariableEmitter varEmitter;

    public AllocaEmitter(Map<String, TypeInfos> varTypes,
                         TempManager temps,
                         LLVisitorMain visitor,
                         VariableEmitter varEmitter) {
        this.varTypes = varTypes;
        this.temps = temps;
        this.visitor = visitor;
        this.varEmitter = varEmitter;
    }

    public LLVMValue emit(VariableDeclarationNode node) {

        Type type = node.getType();
        if (type == null) type = node.getDeclaredType();
        if (type == null)
            throw new RuntimeException("VariableDeclarationNode sem tipo resolvido: " + node.getName());

        String varName = node.getName();
        // PTR da variável no LLVM
        String ptr = temps.newNamedVar(varName);
        varEmitter.registerVarPtr(varName, ptr);

        StringBuilder llvm = new StringBuilder();
        LLVMTYPES llvmType;

        System.out.println("[AllocaEmitter] Allocando var: " + varName + ", tipo = " + type + ", ptr = " + ptr);

        //  Primitivos
        if (type instanceof PrimitiveTypes prim) {
            switch (prim.name()) {
                case "string" -> {
                    llvmType = new LLVMString();
                    llvm.append("  ").append(ptr).append(" = alloca %String*\n");
                }
                case "char" -> {
                    llvmType = new LLVMChar();
                    llvm.append("  ").append(ptr).append(" = alloca i8\n");
                }
                default -> {
                    llvmType = LLVMTypeMapper.from(prim);
                    llvm.append("  ").append(ptr).append(" = alloca ").append(llvmType).append("\n");
                }
            }
            varTypes.put(varName, new TypeInfos(type, llvmType));
            return new LLVMValue(llvmType, ptr, llvm.toString());
        }

        //  Listas
        if (type instanceof ListType listType) {
            Type elem = listType.elementType();
            if (elem instanceof PrimitiveTypes) {
                switch (elem.name()) {
                    case "int" -> llvmType = new LLVMArrayList(new LLVMInt());
                    case "double" -> llvmType = new LLVMArrayList(new LLVMDouble());
                    case "bool" -> llvmType = new LLVMArrayList(new LLVMBool());
                    case "string" -> llvmType = new LLVMArrayList(new LLVMString());
                    default -> llvmType = new LLVMArrayList(null);
                }
            } else {
                llvmType = new LLVMArrayList(null);
            }

            // **Somente aloca o ponteiro**
            llvm.append("  ").append(ptr).append(" = alloca ").append(llvmType).append("*\n");

            varTypes.put(varName, new TypeInfos(type, llvmType));
            return new LLVMValue(llvmType, ptr, llvm.toString());
        }

        //  Structs
        if (type instanceof StructType structType) {
            llvmType = new LLVMStruct(structType.name());
            llvm.append("  ").append(ptr).append(" = alloca ").append(llvmType).append("\n");
            varTypes.put(varName, new TypeInfos(type, llvmType));
            return new LLVMValue(llvmType, ptr, llvm.toString());
        }

        throw new RuntimeException(
                "Unsupported type in AllocaEmitter: " + type.getClass().getSimpleName() + " -> " + type
        );
    }
}