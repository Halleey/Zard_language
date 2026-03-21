package low.structs;
import ast.ASTNode;

import ast.structs.StructMethodCallNode;

import context.statics.symbols.StructType;
import low.TempManager;

import low.functions.TypeMapper;
import low.main.TypeInfos;
import low.module.LLVisitorMain;
import low.module.builders.LLVMPointer;
import low.module.builders.LLVMTYPES;
import low.module.builders.LLVMValue;
import low.module.builders.lists.LLVMArrayList;
import low.module.builders.primitives.*;
import low.module.builders.structs.LLVMStruct;

public final class StructMethodCallEmitter {

    private final TempManager temps;

    public StructMethodCallEmitter(TempManager temps) {
        this.temps = temps;
    }

    public LLVMValue emit(StructMethodCallNode node, LLVisitorMain visitor) {
        StringBuilder llvm = new StringBuilder();

        String methodName = node.getMethodName();

        // ===== Receiver =====
        LLVMValue receiverVal = node.getStructInstance().accept(visitor);
        llvm.append(receiverVal.getCode());

        LLVMTYPES recvType = receiverVal.getType();
        String recvName = receiverVal.getName();

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
                    else if (elemType instanceof LLVMStruct) rtAdd = "arraylist_add_ptr"; // 🔥 struct = ponteiro
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
                    else rtSize = "length"; // 🔥 fallback para ptr/struct

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

                    String tmpRes = temps.newTemp();

                    if (elemType instanceof LLVMInt) {
                        llvm.append("  ").append(tmpRes)
                                .append(" = call i32 @arraylist_get_int(")
                                .append(recvType).append(" ").append(recvName)
                                .append(", i64 ").append(argVal.getName())
                                .append(", i32* null)\n");

                        return new LLVMValue(new LLVMInt(), tmpRes, llvm.toString());
                    }

                    if (elemType instanceof LLVMDouble) {
                        llvm.append("  ").append(tmpRes)
                                .append(" = call double @arraylist_get_double(")
                                .append(recvType).append(" ").append(recvName)
                                .append(", i64 ").append(argVal.getName())
                                .append(", double* null)\n");

                        return new LLVMValue(new LLVMDouble(), tmpRes, llvm.toString());
                    }

                    if (elemType instanceof LLVMBool) {
                        llvm.append("  ").append(tmpRes)
                                .append(" = call i32 @arraylist_get_bool(")
                                .append(recvType).append(" ").append(recvName)
                                .append(", i64 ").append(argVal.getName())
                                .append(", i1* null)\n");

                        return new LLVMValue(new LLVMInt(), tmpRes, llvm.toString());
                    }

                    // 🔥 STRUCT / STRING / PTR
                    String rawPtr = temps.newTemp();

                    llvm.append("  ").append(rawPtr)
                            .append(" = call i8* @arraylist_get_ptr(")
                            .append(recvType).append(" ").append(recvName)
                            .append(", i64 ").append(argVal.getName()).append(")\n");

                    llvm.append("  ").append(tmpRes)
                            .append(" = bitcast i8* ").append(rawPtr)
                            .append(" to ").append(elemType).append("\n");

                    return new LLVMValue(elemType, tmpRes, llvm.toString());
                }

                case "remove" -> {
                    argNode = node.getArgs().get(0);
                    argVal = argNode.accept(visitor);
                    llvm.append(argVal.getCode());

                    String rtRemove;

                    if (elemType instanceof LLVMInt) rtRemove = "arraylist_remove_int";
                    else if (elemType instanceof LLVMDouble) rtRemove = "arraylist_remove_double";
                    else if (elemType instanceof LLVMBool) rtRemove = "arraylist_remove_bool";
                    else rtRemove = "removeItem"; // 🔥 ptr/struct

                    llvm.append("  call void @").append(rtRemove)
                            .append("(").append(recvType).append(" ").append(recvName)
                            .append(", i64 ").append(argVal.getName()).append(")\n");

                    return new LLVMValue(recvType, recvName, llvm.toString());
                }

                default -> throw new RuntimeException("Método de ArrayList não suportado: " + methodName);
            }
        }

        if (!(recvType instanceof LLVMPointer ptr) || !(ptr.pointee() instanceof LLVMStruct struct)) {
            throw new RuntimeException("Chamada de método em tipo inválido: " + recvType);
        }

        String llvmFuncName = struct.getName() + "_" + methodName;


        StringBuilder callArgs = new StringBuilder();
        callArgs.append(recvType).append(" ").append(recvName);

        for (ASTNode arg : node.getArgs()) {
            LLVMValue argLLVM = arg.accept(visitor);
            llvm.append(argLLVM.getCode());
            callArgs.append(", ").append(argLLVM.getType()).append(" ").append(argLLVM.getName());
        }

        LLVMTYPES retType = node.getReturnType() != null
                ? TypeMapper.from(node.getReturnType())
                : new LLVMVoid();

        if (retType instanceof LLVMVoid) {
            llvm.append("  call void @").append(llvmFuncName)
                    .append("(").append(callArgs).append(")\n");

            return new LLVMValue(retType, "0", llvm.toString());
        }

        String retName = temps.newTemp();

        llvm.append("  ").append(retName)
                .append(" = call ").append(retType)
                .append(" @").append(llvmFuncName)
                .append("(").append(callArgs).append(")\n");

        return new LLVMValue(retType, retName, llvm.toString());
    }
}