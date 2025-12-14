package low.structs;



import ast.ASTNode;
import ast.functions.FunctionNode;
import ast.structs.StructMethodCallNode;
import low.TempManager;

import low.module.LLVisitorMain;
public class StructMethodCallEmitter {

    private final TempManager temps;

    public StructMethodCallEmitter(TempManager temps) {
        this.temps = temps;
    }

    public String emit(StructMethodCallNode node, LLVisitorMain visitor) {
        StringBuilder llvm = new StringBuilder();

        String methodName = node.getMethodName();

        // === Receiver ===
        ASTNode receiver = node.getStructInstance();
        String recvIR = receiver.accept(visitor);
        String recvVal = extractLastVal(recvIR);
        String recvType = extractLastType(recvIR);

        llvm.append(recvIR);

        // ============================================================
        // === MÉTODOS DE LISTA (ArrayList runtime) ===================
        // ============================================================
        if (recvType.startsWith("%struct.ArrayList")) {

            String elementType = "";

            if (recvType.contains("ArrayListInt")) elementType = "int";
            if (recvType.contains("ArrayListDouble")) elementType = "double";
            if (recvType.contains("ArrayListBool")) elementType = "bool";
            if (recvType.contains("ArrayListString")) elementType = "string";

            switch (methodName) {

                case "add" -> {
                    ASTNode arg = node.getArgs().get(0);
                    String argIR = arg.accept(visitor);
                    llvm.append(argIR);

                    String argVal = extractLastVal(argIR);
                    String argType = extractLastType(argIR);

                    String rtAdd = switch (elementType) {
                        case "int" -> "arraylist_add_int";
                        case "double" -> "arraylist_add_double";
                        case "bool" -> "arraylist_add_bool";
                        case "string" -> "arraylist_add_ptr";
                        default -> throw new RuntimeException("Tipo de lista não suportado: " + elementType);
                    };

                    llvm.append("  call void @").append(rtAdd)
                            .append("(").append(recvType).append(" ").append(recvVal)
                            .append(", ").append(argType).append(" ").append(argVal)
                            .append(")\n");

                    llvm.append(";;VAL:").append(recvVal)
                            .append(";;TYPE:").append(recvType).append("\n");

                    return llvm.toString();
                }

                case "size" -> {
                    String rtSize = switch (elementType) {
                        case "int" -> "arraylist_size_int";
                        case "double" -> "arraylist_size_double";
                        case "bool" -> "arraylist_size_bool";
                        default -> throw new RuntimeException("Tipo não suportado: " + elementType);
                    };

                    String tmp = temps.newTemp();
                    llvm.append("  ").append(tmp)
                            .append(" = call i32 @").append(rtSize)
                            .append("(").append(recvType).append(" ").append(recvVal).append(")\n")
                            .append(";;VAL:").append(tmp).append(";;TYPE:i32\n");

                    return llvm.toString();
                }

                case "get" -> {
                    ASTNode arg = node.getArgs().get(0);
                    String argIR = arg.accept(visitor);
                    llvm.append(argIR);

                    String argVal = extractLastVal(argIR);

                    String rtGet = switch (elementType) {
                        case "int" -> "arraylist_get_int";
                        case "double" -> "arraylist_get_double";
                        case "bool" -> "arraylist_get_bool";
                        default -> throw new RuntimeException("Tipo não suportado: " + elementType);
                    };

                    String tmp = temps.newTemp();
                    llvm.append("  ").append(tmp)
                            .append(" = call i32 @").append(rtGet)
                            .append("(").append(recvType).append(" ").append(recvVal)
                            .append(", i64 ").append(argVal).append(", i32* null)\n")
                            .append(";;VAL:").append(tmp).append(";;TYPE:i32\n");

                    return llvm.toString();
                }

                case "remove" -> {
                    ASTNode arg = node.getArgs().get(0);
                    String argIR = arg.accept(visitor);
                    llvm.append(argIR);

                    String argVal = extractLastVal(argIR);

                    String rtRemove = switch (elementType) {
                        case "int" -> "arraylist_remove_int";
                        case "double" -> "arraylist_remove_double";
                        case "bool" -> "arraylist_remove_bool";
                        default -> throw new RuntimeException("Tipo não suportado: " + elementType);
                    };

                    llvm.append("  call void @").append(rtRemove)
                            .append("(").append(recvType).append(" ").append(recvVal)
                            .append(", i64 ").append(argVal).append(")\n")
                            .append(";;VAL:").append(recvVal)
                            .append(";;TYPE:").append(recvType).append("\n");

                    return llvm.toString();
                }
            }
        }

        // ============================================================
        // === MÉTODOS DE STRUCT (impl) ===============================
        // ============================================================

        String cleanType = recvType.replace("%", "").replace("*", "");
        String llvmSafe = cleanType
                .replace("Struct<", "")
                .replace(">", "")
                .replace("<", "_")
                .replace(",", "_")
                .replace(" ", "_");

        String llvmFuncName = llvmSafe + "_" + methodName;

        StringBuilder callArgs = new StringBuilder();
        callArgs.append(recvType).append(" ").append(recvVal);

        for (ASTNode argNode : node.getArgs()) {
            String argIR = argNode.accept(visitor);
            llvm.append(argIR);

            String argVal = extractLastVal(argIR);
            String argType = extractLastType(argIR);

            callArgs.append(", ").append(argType).append(" ").append(argVal);
        }

        // 🔥 AQUI ESTÁ A CORREÇÃO PRINCIPAL
        String retSource = node.getReturnType();
        String retLLVM;

        if (retSource == null || "void".equals(retSource)) {
            retLLVM = "void";
        } else if (retSource.startsWith("Struct<")) {
            String inner = retSource.substring("Struct<".length(), retSource.length() - 1);
            retLLVM = "%" + inner + "*";
        } else {
            retLLVM = mapToLLVMType(retSource);
        }

        if ("void".equals(retLLVM)) {

            llvm.append("  call void @")
                    .append(llvmFuncName)
                    .append("(").append(callArgs).append(")\n")
                    .append(";;VAL:0;;TYPE:void\n");

        } else {

            String tmp = temps.newTemp();
            llvm.append("  ").append(tmp)
                    .append(" = call ").append(retLLVM)
                    .append(" @").append(llvmFuncName)
                    .append("(").append(callArgs).append(")\n")
                    .append(";;VAL:").append(tmp)
                    .append(";;TYPE:").append(retLLVM).append("\n");
        }

        return llvm.toString();
    }

    // ============================================================

    private String mapToLLVMType(String type) {
        if (type == null) return "i8*";
        type = type.trim();

        if (type.startsWith("Struct<") && type.endsWith(">")) {
            String inner = type.substring("Struct<".length(), type.length() - 1).trim();
            return "%" + inner + "*";
        }

        return switch (type) {
            case "int" -> "i32";
            case "double" -> "double";
            case "bool", "boolean" -> "i1";
            case "string", "String" -> "%String*";
            default -> "i8*";
        };
    }

    private String extractLastVal(String code) {
        int v = code.lastIndexOf(";;VAL:");
        if (v == -1) return "";
        int t = code.indexOf(";;TYPE:", v);
        return (t == -1) ? "" : code.substring(v + 6, t).trim();
    }

    private String extractLastType(String code) {
        int t = code.lastIndexOf(";;TYPE:");
        if (t == -1) return "";
        int end = code.indexOf("\n", t);
        if (end == -1) end = code.length();
        return code.substring(t + 7, end).trim();
    }
}
