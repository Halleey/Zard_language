package low.structs;
import ast.ASTNode;

import ast.structs.StructMethodCallNode;

import low.TempManager;

import low.functions.TypeMapper;
import low.module.LLVisitorMain;
import low.module.builders.LLVMTYPES;
import low.module.builders.LLVMValue;
import low.module.builders.lists.LLVMArrayList;
import low.module.builders.primitives.*;

public final class StructMethodCallEmitter {

    private final TempManager temps;

    public StructMethodCallEmitter(TempManager temps) {
        this.temps = temps;
    }

    public LLVMValue emit(StructMethodCallNode node, LLVisitorMain visitor) {
        StringBuilder llvm = new StringBuilder();

        String methodName = node.getMethodName();

        // ===== LLVMValue do receiver =====
        LLVMValue receiverVal = node.getStructInstance().accept(visitor);
        llvm.append(receiverVal.getCode());
        LLVMTYPES recvType = receiverVal.getType();
        String recvName = receiverVal.getName();

        // ===== Se for ArrayList runtime =====
        if (recvType instanceof LLVMArrayList arr) {

            LLVMTYPES elemType = arr.elementType();
            ASTNode argNode;
            LLVMValue argVal;
            String tmp;

            switch (methodName) {

                case "add" -> {
                    argNode = node.getArgs().get(0);
                    argVal = argNode.accept(visitor);
                    llvm.append(argVal.getCode());

                    String rtAdd;
                    if (elemType instanceof LLVMInt) rtAdd = "arraylist_add_int";
                    else if (elemType instanceof LLVMDouble) rtAdd = "arraylist_add_double";
                    else if (elemType instanceof LLVMBool) rtAdd = "arraylist_add_bool";
                    else if (elemType instanceof LLVMString) rtAdd = "arraylist_add_ptr";
                    else throw new RuntimeException("Tipo de lista não suportado: " + elemType);

                    llvm.append("  call void @").append(rtAdd)
                            .append("(").append(recvType).append(" ").append(recvName)
                            .append(", ").append(argVal.getType()).append(" ").append(argVal.getName())
                            .append(")\n");

                    return new LLVMValue(recvType, recvName, llvm.toString());
                }

                case "size" -> {
                    String rtSize;
                    if (elemType instanceof LLVMInt) rtSize = "arraylist_size_int";
                    else if (elemType instanceof LLVMDouble) rtSize = "arraylist_size_double";
                    else if (elemType instanceof LLVMBool) rtSize = "arraylist_size_bool";
                    else throw new RuntimeException("Tipo de lista não suportado: " + elemType);

                    tmp = temps.newTemp();
                    llvm.append("  ").append(tmp)
                            .append(" = call i32 @").append(rtSize)
                            .append("(").append(recvType).append(" ").append(recvName).append(")\n");

                    return new LLVMValue(new LLVMInt(), tmp, llvm.toString());
                }

                case "get" -> {
                    argNode = node.getArgs().get(0);
                    argVal = argNode.accept(visitor);
                    llvm.append(argVal.getCode());

                    String rtGet;
                    if (elemType instanceof LLVMInt) rtGet = "arraylist_get_int";
                    else if (elemType instanceof LLVMDouble) rtGet = "arraylist_get_double";
                    else if (elemType instanceof LLVMBool) rtGet = "arraylist_get_bool";
                    else throw new RuntimeException("Tipo de lista não suportado: " + elemType);

                    tmp = temps.newTemp();
                    llvm.append("  ").append(tmp)
                            .append(" = call i32 @").append(rtGet)
                            .append("(").append(recvType).append(" ").append(recvName)
                            .append(", i64 ").append(argVal.getName())
                            .append(", i32* null)\n");

                    return new LLVMValue(new LLVMInt(), tmp, llvm.toString());
                }

                case "remove" -> {
                    argNode = node.getArgs().get(0);
                    argVal = argNode.accept(visitor);
                    llvm.append(argVal.getCode());

                    String rtRemove;
                    if (elemType instanceof LLVMInt) rtRemove = "arraylist_remove_int";
                    else if (elemType instanceof LLVMDouble) rtRemove = "arraylist_remove_double";
                    else if (elemType instanceof LLVMBool) rtRemove = "arraylist_remove_bool";
                    else throw new RuntimeException("Tipo de lista não suportado: " + elemType);

                    llvm.append("  call void @").append(rtRemove)
                            .append("(").append(recvType).append(" ").append(recvName)
                            .append(", i64 ").append(argVal.getName()).append(")\n");

                    return new LLVMValue(recvType, recvName, llvm.toString());
                }

                default -> throw new RuntimeException("Método de ArrayList não suportado: " + methodName);
            }
        }

        // ===== Chamada de método de struct =====
        String llvmFuncName = sanitizeLLVMFuncName(recvType, methodName);

        StringBuilder callArgs = new StringBuilder();
        callArgs.append(recvType).append(" ").append(recvName);

        for (ASTNode arg : node.getArgs()) {
            LLVMValue argLLVM = arg.accept(visitor);
            llvm.append(argLLVM.getCode());
            callArgs.append(", ").append(argLLVM.getType()).append(" ").append(argLLVM.getName());
        }

        LLVMTYPES retType = node.getReturnType() != null ? TypeMapper.from(node.getReturnType()) : new LLVMVoid();
        String retName = temps.newTemp();

        if (retType instanceof LLVMVoid) {
            llvm.append("  call void @").append(llvmFuncName)
                    .append("(").append(callArgs).append(")\n");
            return new LLVMValue(retType, "0", llvm.toString());
        } else {
            llvm.append("  ").append(retName)
                    .append(" = call ").append(retType)
                    .append(" @").append(llvmFuncName)
                    .append("(").append(callArgs).append(")\n");
            return new LLVMValue(retType, retName, llvm.toString());
        }
    }

    private String sanitizeLLVMFuncName(LLVMTYPES type, String methodName) {
        String llvmSafe = type.toString()
                .replace("Struct<", "")
                .replace(">", "")
                .replace("<", "_")
                .replace(",", "_")
                .replace(" ", "_");
        return llvmSafe + "_" + methodName;
    }
}