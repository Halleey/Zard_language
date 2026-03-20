package low.prints;

import ast.ASTNode;
import ast.functions.FunctionCallNode;
import ast.structs.StructFieldAccessNode;
import ast.variables.VariableNode;
import context.statics.symbols.ListType;
import context.statics.symbols.Type;
import low.TempManager;
import low.main.TypeInfos;
import low.module.LLVisitorMain;
import low.module.builders.LLVMPointer;
import low.module.builders.LLVMTYPES;
import low.module.builders.LLVMValue;
import low.module.builders.lists.LLVMArrayList;
import low.module.builders.primitives.LLVMBool;
import low.module.builders.primitives.LLVMDouble;
import low.module.builders.primitives.LLVMInt;
import low.module.builders.primitives.LLVMString;
import low.module.builders.structs.LLVMStruct;

public class ListPrintHandler implements PrintHandler {

    private final TempManager temps;

    public ListPrintHandler(TempManager temps) {
        this.temps = temps;
    }

    @Override
    public boolean canHandle(ASTNode node, LLVisitorMain visitor) {

        if (node instanceof VariableNode varNode) {
            TypeInfos info = visitor.getVarType(varNode.getName());
            return info != null && info.getType() instanceof ListType;
        }

        if (node instanceof StructFieldAccessNode sfa) {
            return visitor.inferListElementType(sfa) != null;
        }

        if (node instanceof FunctionCallNode fn) {
            TypeInfos fnType = visitor.getFunctionType(fn.getName());
            return fnType != null && fnType.getType() instanceof ListType;
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



        if (type instanceof LLVMPointer ptr) {

            LLVMTYPES pointee = ptr.pointee();

            if (pointee instanceof LLVMPointer) {

                String loaded = temps.newTemp();

                llvm.append("  ").append(loaded)
                        .append(" = load ")
                        .append(pointee)
                        .append(", ")
                        .append(type).append(" ")
                        .append(temp).append("\n");

                temp = loaded;
                type = pointee;
            }
        }


        emitPrintCall(llvm, type, temp, newline);

        return new LLVMValue(type, temp, llvm.toString());
    }

    private void emitPrintCall(StringBuilder sb,
                               LLVMTYPES type,
                               String val,
                               boolean newline) {

        String nl = newline ? "1" : "0";

        if (type instanceof LLVMArrayList listType) {

            LLVMTYPES elem = listType.elementType();

            if (elem instanceof LLVMInt) {
                sb.append("  call void @arraylist_print_int(%struct.ArrayListInt* ")
                        .append(val).append(", i1 ").append(nl).append(")\n");
                return;
            }

            if (elem instanceof LLVMDouble) {
                sb.append("  call void @arraylist_print_double(%struct.ArrayListDouble* ")
                        .append(val).append(", i1 ").append(nl).append(")\n");
                return;
            }

            if (elem instanceof LLVMBool) {
                sb.append("  call void @arraylist_print_bool(%struct.ArrayListBool* ")
                        .append(val).append(", i1 ").append(nl).append(")\n");
                return;
            }

            if (elem instanceof LLVMString) {
                sb.append("  call void @arraylist_print_string(%ArrayListString* ")
                        .append(val).append(", i1 ").append(nl).append(")\n");
                return;
            }

            if (elem instanceof LLVMStruct structElem) {

                String structName = structElem.getName();

                sb.append("  call void @arraylist_print_ptr(%ArrayList* ")
                        .append(val)
                        .append(", void (i8*)* @print_")
                        .append(structName)
                        .append(", i1 ")
                        .append(nl)
                        .append(")\n");

                return;
            }
        }
        // fallback genérico
        sb.append("  call void @arraylist_print_ptr(%ArrayList* ")
                .append(val)
                .append(", void (i8*)* null, i1 ")
                .append(nl)
                .append(")\n");
    }

    private Type resolveElementType(ASTNode node, LLVisitorMain visitor) {

        if (node instanceof VariableNode varNode) {

            TypeInfos info = visitor.getVarType(varNode.getName());

            if (info == null)
                throw new RuntimeException("Variável não registrada: " + varNode.getName());

            return ((ListType) info.getType()).elementType();
        }

        if (node instanceof StructFieldAccessNode sfa) {
            return visitor.inferListElementType(sfa);
        }

        if (node instanceof FunctionCallNode fn) {

            TypeInfos fnType = visitor.getFunctionType(fn.getName());

            if (fnType == null || !(fnType.getType() instanceof ListType lt))
                throw new RuntimeException("Função não retorna lista: " + fn.getName());

            return lt.elementType();
        }

        throw new RuntimeException("Não foi possível inferir tipo da lista");
    }
}