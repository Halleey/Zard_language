package memory_manager.free;

import context.statics.symbols.*;
import low.TempManager;
import low.functions.TypeMapper;
import low.main.TypeInfos;
import low.module.LLVisitorMain;
import low.module.builders.LLVMTYPES;
import low.module.builders.LLVMValue;
import low.module.builders.primitives.LLVMVoid;

public class FreeEmitter {

    private final LLVisitorMain visitor;
    private final TypeMapper typeMapper;
    private final TempManager temps;

    public FreeEmitter(LLVisitorMain visitor, TempManager temps) {
        this.visitor = visitor;
        this.temps = temps;
        this.typeMapper = new TypeMapper();
    }

    public LLVMValue emit(FreeNode freeNode) {

        String varName = freeNode.getRoot().getSymbol().getName();
        String varPtr = visitor.getVariableEmitter().getVarPtr(varName);

        TypeInfos info = visitor.getVarType(varName);
        if (info == null) {
            // retorna LLVMValue vazio, nada para liberar
            return new LLVMValue(new LLVMVoid(), "", "");
        }

        Type type = info.getType();
        LLVMTYPES llvmType = info.getLLVMType();

        String tmpFree = temps.newTemp();

        StringBuilder sb = new StringBuilder();

        // load do valor
        sb.append("  ").append(tmpFree)
                .append(" = load ").append(llvmType)
                .append(", ").append(llvmType).append("* ").append(varPtr).append("\n");

        if (type instanceof ListType listType) {
            Type elementType = listType.elementType();
            // lista de string
            if (elementType == PrimitiveTypes.STRING) {
                sb.append("  call void @arraylist_string_free(%ArrayListString* ")
                        .append(tmpFree)
                        .append(")\n");
            } else {
                // lista genérica
                String freeFunc;
                if (elementType == null || elementType instanceof UnknownType) {
                    freeFunc = "@freeList";
                } else {
                    freeFunc = typeMapper.freeFunctionForElement(elementType);
                }
                sb.append("  call void ")
                        .append(freeFunc)
                        .append("(").append(llvmType).append(" ").append(tmpFree).append(")\n");
            }

        } else if (type instanceof PrimitiveTypes prim && prim.name().equals("string")) {
            sb.append("  call void @freeString(")
                    .append(llvmType).append(" ").append(tmpFree).append(")\n");
        }

//        else if (type instanceof StructType structType) {
//            String structName = structType.name();
//            sb.append("  call void @free_")
//              .append(structName)
//              .append("(").append(llvmType).append(" ").append(tmpFree).append(")\n");
//        }

        // retorna LLVMValue tipado (LLVMVoid, pois free não retorna nada)
        return new LLVMValue(new LLVMVoid(), tmpFree, sb.toString());
    }
}