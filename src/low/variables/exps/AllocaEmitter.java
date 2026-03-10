package low.variables.exps;


import ast.variables.VariableDeclarationNode;

import context.statics.symbols.ListType;
import context.statics.symbols.PrimitiveTypes;
import context.statics.symbols.StructType;
import context.statics.symbols.Type;
import low.TempManager;
import low.functions.TypeMapper;
import low.main.TypeInfos;
import low.module.LLVisitorMain;
import low.variables.VariableEmitter;

import java.util.Map;
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

    private String mapLLVMType(Type sourceType) {
        return new TypeMapper().toLLVM(sourceType);
    }

    public String emit(VariableDeclarationNode node) {

        Type type = node.getType();

        if (type == null) {
            type = node.getDeclaredType();
        }

        if (type == null) {
            throw new RuntimeException(
                    "VariableDeclarationNode sem tipo resolvido: " + node.getName()
            );
        }

        String varName = node.getName();

        String ptr = temps.newNamedVar(varName);

        varEmitter.registerVarPtr(varName, ptr);

        String llvmType;

        if (type instanceof PrimitiveTypes prim) {

            switch (prim.name()) {

                case "string" -> {
                    llvmType = "%String*";
                    varTypes.put(varName, new TypeInfos(type, llvmType));

                    return "  " + ptr + " = alloca %String*\n"
                            + ";;VAL:" + ptr + ";;TYPE:%String*\n";
                }

                case "char" -> {
                    llvmType = "i8";
                    varTypes.put(varName, new TypeInfos(type, llvmType));

                    return "  " + ptr + " = alloca i8\n"
                            + ";;VAL:" + ptr + ";;TYPE:i8\n";
                }

                default -> {
                    llvmType = mapLLVMType(prim);
                    varTypes.put(varName, new TypeInfos(type, llvmType));

                    return "  " + ptr + " = alloca " + llvmType + "\n"
                            + ";;VAL:" + ptr + ";;TYPE:" + llvmType + "\n";
                }
            }
        }


        if (type instanceof ListType listType) {

            Type elem = listType.elementType();

            if (elem instanceof PrimitiveTypes prim) {

                switch (prim.name()) {
                    case "int" -> llvmType = "%struct.ArrayListInt*";
                    case "double" -> llvmType = "%struct.ArrayListDouble*";
                    case "boolean" -> llvmType = "%struct.ArrayListBool*";
                    default -> llvmType = "%ArrayList*";
                }

            } else {
                llvmType = "%ArrayList*";
            }

            varTypes.put(varName, new TypeInfos(type, llvmType));

            return "  " + ptr + " = alloca " + llvmType + "\n"
                    + ";;VAL:" + ptr + ";;TYPE:" + llvmType + "\n";
        }

        if (type instanceof StructType structType) {

            llvmType = "%" + structType.name() + "*";

            varTypes.put(varName, new TypeInfos(type, llvmType));

            return "  " + ptr + " = alloca " + llvmType + "\n"
                    + ";;VAL:" + ptr + ";;TYPE:" + llvmType + "\n";
        }
        throw new RuntimeException(
                "Unsupported type in AllocaEmitter: " + type.getClass().getSimpleName()
                        + " -> " + type
        );
    }
}