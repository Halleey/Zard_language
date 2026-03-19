package low.prints;

import ast.ASTNode;
import ast.structs.StructInstanceNode;
import ast.structs.StructNode;
import ast.variables.VariableNode;
import low.TempManager;
import low.main.TypeInfos;
import low.module.LLVisitorMain;
import low.module.builders.LLVMPointer;
import low.module.builders.LLVMTYPES;
import low.module.builders.LLVMValue;
import low.module.builders.structs.LLVMStruct;
import low.utils.LLVMNameUtils;


public class StructPrintHandler implements PrintHandler {

    private final TempManager temps;

    public StructPrintHandler(TempManager temps) {
        this.temps = temps;
    }

    @Override
    public boolean canHandle(ASTNode node, LLVisitorMain visitor) {

        if (node instanceof VariableNode var) {
            TypeInfos info = visitor.getVarType(var.getName());
            return info != null && info.getLLVMType() instanceof LLVMStruct;
        }

        if (node instanceof StructInstanceNode inst) {
            return true;
        }

        return false;
    }

    @Override
    public LLVMValue emit(ASTNode node, LLVisitorMain visitor, boolean newline) {

        LLVMValue val = node.accept(visitor);

        StringBuilder llvm = new StringBuilder();
        llvm.append(val.getCode());

        LLVMTYPES type = val.getType();
        String temp = val.getName();

        if (type instanceof LLVMPointer ptr && ptr.pointee() instanceof LLVMPointer inner) {

            String loaded = temps.newTemp();

            llvm.append("  ").append(loaded)
                    .append(" = load ")
                    .append(inner).append(", ")
                    .append(type).append(" ")
                    .append(temp).append("\n");

            temp = loaded;
            type = inner;
        }

        if (!(type instanceof LLVMStruct structType)) {
            throw new RuntimeException("Esperado struct para print, encontrado: " + type);
        }

        String structName = structType.getName();

        llvm.append("  call void @print_")
                .append(structName)
                .append("(%").append(structName).append("* ")
                .append(temp)
                .append(")\n");

        return new LLVMValue(type, temp, llvm.toString());
    }
}