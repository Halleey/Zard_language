package memory_manager.free;

import context.statics.symbols.*;
import low.TempManager;
import low.functions.TypeMapper;
import low.main.TypeInfos;
import low.module.LLVisitorMain;


public class FreeEmitter {

    private final LLVisitorMain visitor;
    private final TypeMapper typeMapper;
    private final TempManager temps;

    public FreeEmitter(LLVisitorMain visitor, TempManager temps) {
        this.visitor = visitor;
        this.temps = temps;
        this.typeMapper = new TypeMapper();
    }

    public String emit(FreeNode freeNode) {

        String varName = freeNode.getRoot().getSymbol().getName();

        String varPtr = visitor.getVariableEmitter().getVarPtr(varName);

        TypeInfos info = visitor.getVarType(varName);
        if (info == null) {
            return "";
        }

        Type type = info.getType();
        String llvmType = info.getLLVMType();

        String tmpFree = temps.newTemp();

        StringBuilder sb = new StringBuilder();

        // load do valor
        sb.append("  ").append(tmpFree)
                .append(" = load ").append(llvmType)
                .append(", ").append(llvmType).append("* ").append(varPtr).append("\n");


        if (type instanceof ListType listType) {

            Type elementType = listType.elementType();

            // Detecta lista de string
            if (elementType instanceof PrimitiveTypes prim && prim.name().equals("string")) {
                // lista de String* → usa função de free específica
                sb.append("  call void @arraylist_string_free(")
                        .append(llvmType).append(" ").append(tmpFree).append(")\n");
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
        }


        else if (type instanceof PrimitiveTypes prim &&
                prim.name().equals("string")) {
            sb.append("  call void @freeString(")
                    .append(llvmType).append(" ").append(tmpFree).append(")\n");
        }


//        else if (type instanceof StructType structType) {
//            System.out.println("entrou aqui");
//            String structName = structType.name();
//
//            sb.append("  call void @free_")
//                    .append(structName)
//                    .append("(")
//                    .append(llvmType)
//                    .append(" ")
//                    .append(tmpFree)
//                    .append(")\n");
//        }

        return sb.toString();
    }
}