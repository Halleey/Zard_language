package low.structs;



import ast.ASTNode;
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

        ASTNode receiver = node.getStructInstance();
        String recvIR = receiver.accept(visitor);
        String recvVal = extractLastVal(recvIR);
        String recvType = extractLastType(recvIR);
        System.out.println("Dentro do struct method call");
        System.out.println(recvIR);
        System.out.println(recvVal);
        System.out.println(recvType);
        llvm.append(recvIR);

        if (recvType.startsWith("%struct.ArrayList")) {

            String elementType = "";

            if (recvType.contains("ArrayListInt")) elementType = "int";
            if (recvType.contains("ArrayListDouble")) elementType = "double";
            if (recvType.contains("ArrayListBool")) elementType = "bool";
            if (recvType.contains("ArrayListString")) elementType = "string";

            // Agora mapeia o método
            switch (methodName) {
                case "add" -> {
                    ASTNode arg = node.getArgs().get(0);
                    String argIR = arg.accept(visitor);
                    llvm.append(argIR);

                    String argVal = extractLastVal(argIR);
                    String argType = extractLastType(argIR);

                    // Runtime correto
                    String rtAdd = switch (elementType) {
                        case "int" -> "arraylist_add_int";
                        case "double" -> "arraylist_add_double";
                        case "bool" -> "arraylist_add_bool";
                        case "string" -> "arraylist_add_ptr"; // strings são ponteiros
                        default -> throw new RuntimeException("Tipo de lista não suportado: " + elementType);
                    };

                    llvm.append("  call void @").append(rtAdd)
                            .append("(").append(recvType).append(" ").append(recvVal)
                            .append(", ").append(argType).append(" ").append(argVal)
                            .append(")\n");

                    // retorna o próprio receiver (sem criar temp!)
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
                    llvm.append("  ").append(tmp).append(" = call i32 @").append(rtSize)
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

        if (!node.getArgs().isEmpty()) {
            ASTNode arg = node.getArgs().get(0);
            String argIR = arg.accept(visitor);
            llvm.append(argIR);

            String argVal = extractLastVal(argIR);
            String argType = extractLastType(argIR);
            callArgs.append(", ").append(argType).append(" ").append(argVal);
        }

        String retType = recvType;
        String tmp = temps.newTemp();
        llvm.append("  ").append(tmp)
                .append(" = call ").append(retType)
                .append(" @").append(llvmFuncName)
                .append("(").append(callArgs).append(")\n")
                .append(";;VAL:").append(tmp).append(";;TYPE:").append(retType).append("\n");

        return llvm.toString();
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
