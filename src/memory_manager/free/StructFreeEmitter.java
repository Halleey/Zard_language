package memory_manager.free;

import ast.structs.StructNode;
import ast.variables.VariableDeclarationNode;
import context.statics.symbols.Type;
import context.statics.symbols.PrimitiveTypes;
import context.statics.symbols.ListType;
import context.statics.symbols.StructType;
import low.TempManager;
import low.module.LLVisitorMain;
import low.utils.LLVMNameUtils;

public class StructFreeEmitter {

    private final TempManager temps;
    private final LLVisitorMain visitor;

    public StructFreeEmitter(LLVisitorMain visitor, TempManager temps) {
        this.visitor = visitor;
        this.temps = temps;
    }

    public String emit(StructNode struct) {

        StringBuilder llvm = new StringBuilder();

        String structName = LLVMNameUtils.llvmSafe(struct.getLLVMName());
        String llvmStructType = "%" + structName + "*";

        llvm.append("define void @free_")
                .append(structName)
                .append("(").append(llvmStructType).append(" %p) {\n");

        llvm.append("entry:\n");

        int index = 0;

        for (VariableDeclarationNode field : struct.getFields()) {

            Type fieldType = field.getType();

            String fieldPtr = temps.newTemp();
            String fieldVal = temps.newTemp();

            llvm.append("  ").append(fieldPtr)
                    .append(" = getelementptr inbounds %")
                    .append(structName)
                    .append(", %").append(structName)
                    .append("* %p, i32 0, i32 ").append(index)
                    .append("\n");

            if (fieldType instanceof PrimitiveTypes prim &&
                    prim.name().equals("string")) {

                llvm.append("  ").append(fieldVal)
                        .append(" = load %String*, %String** ")
                        .append(fieldPtr).append("\n");

                llvm.append("  call void @freeString(%String* ")
                        .append(fieldVal).append(")\n");

            }

            else if (fieldType instanceof StructType st) {

                String inner = st.name();

                llvm.append("  ").append(fieldVal)
                        .append(" = load %").append(inner).append("*, %")
                        .append(inner).append("** ").append(fieldPtr).append("\n");

                llvm.append("  call void @free_")
                        .append(inner)
                        .append("(%").append(inner).append("* ")
                        .append(fieldVal).append(")\n");
            }

            else if (fieldType instanceof ListType) {

                llvm.append("  ").append(fieldVal)
                        .append(" = load %ArrayList*, %ArrayList** ")
                        .append(fieldPtr).append("\n");

                llvm.append("  call void @freeList(%ArrayList* ")
                        .append(fieldVal).append(")\n");
            }

            index++;
        }

        String cast = temps.newTemp();

        llvm.append("  ").append(cast)
                .append(" = bitcast %").append(structName)
                .append("* %p to i8*\n");

        llvm.append("  call void @free(i8* ").append(cast).append(")\n");

        llvm.append("  ret void\n}\n\n");

        return llvm.toString();
    }
}