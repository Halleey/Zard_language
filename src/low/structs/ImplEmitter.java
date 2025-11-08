package low.structs;

import ast.functions.FunctionNode;
import ast.structs.ImplNode;
import ast.variables.VariableDeclarationNode;
import low.module.LLVisitorMain;

import java.util.ArrayList;
import java.util.List;


public class ImplEmitter {
    private final LLVisitorMain visitor;

    public ImplEmitter(LLVisitorMain visitor) {
        this.visitor = visitor;
    }

    public String emit(ImplNode node) {
        StringBuilder llvm = new StringBuilder();
        String structName = node.getStructName();

        llvm.append(";; ==== Impl Definitions ====\n");
        llvm.append("; === Impl para Struct<").append(structName).append("> ===\n");

        for (FunctionNode fn : node.getMethods()) {
            String fnName = fn.getName();

            List<String> params = new ArrayList<>(fn.getParams());
            List<String> types = new ArrayList<>(fn.getParamTypes());
            String returnType = fn.getReturnType();

            for (int i = 0; i < types.size(); i++) {
                String t = types.get(i).trim();

                if (t.equals("?") || t.equals("Struct<?>") || t.equals("Struct <?>")) {
                    String inferred = inferConcreteType(structName);
                    if (inferred == null) inferred = "i8*";
                    types.set(i, inferred);
                }

                else if (t.equals("List<?>") || t.equals("List <?>")) {
                    String inner = inferListInnerType(structName);
                    if (inner == null) inner = "i8*";
                    types.set(i, "List<" + inner + ">");
                }

                else if (t.startsWith("Struct<") && t.endsWith(">")) {
                    String inner = t.substring(7, t.length() - 1).trim();
                    types.set(i, "%" + inner + "*");
                }

                else if (t.startsWith("Struct ")) {
                    String inner = t.substring(7).trim();
                    types.set(i, "%" + inner + "*");
                }
            }

            if (returnType.equals("?") || returnType.equals("Struct<?>")) {
                String inferred = inferConcreteType(structName);
                if (inferred == null) inferred = "i8*";
                returnType = inferred;
            }

            else if (returnType.equals("List<?>")) {
                String inner = inferListInnerType(structName);
                if (inner == null) inner = "i8*";
                returnType = "List<" + inner + ">";
            }

            else if (returnType.startsWith("Struct<" + structName + ">")) {
                returnType = "%" + structName + "*";
            }

            else if (returnType.startsWith("Struct ")) {
                String inner = returnType.substring(7).trim();
                returnType = "%" + inner + "*";
            }

            FunctionNode adapted = new FunctionNode(fnName, params, types, fn.getBody(), returnType);
            adapted.setImplStructName(structName);

            llvm.append("; === Função (impl): ").append(fnName).append(" ===\n");
            llvm.append(adapted.accept(visitor)).append("\n");
        }

        llvm.append("\n");
        return llvm.toString();
    }


    private String inferConcreteType(String structName) {
        var struct = visitor.getStructNode(structName);
        if (struct == null) return null;

        for (VariableDeclarationNode field : struct.getFields()) {
            String type = field.getType();
            if (type.startsWith("List<") && type.endsWith(">")) {
                String inner = type.substring(5, type.length() - 1).trim();
                return switch (inner) {
                    case "int" -> "i32";
                    case "double" -> "double";
                    case "boolean" -> "i1";
                    case "string" -> "%String*";
                    default -> "i8*";
                };
            }
        }
        return null;
    }

    private String inferListInnerType(String structName) {
        var struct = visitor.getStructNode(structName);
        if (struct == null) return null;

        for (VariableDeclarationNode field : struct.getFields()) {
            String type = field.getType();
            if (type.startsWith("List<") && type.endsWith(">")) {
                return type.substring(5, type.length() - 1).trim();
            }
        }
        return null;
    }
}