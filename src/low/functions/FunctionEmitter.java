package low.functions;

import ast.ASTNode;
import ast.exceptions.ReturnNode;
import ast.functions.FunctionNode;
import ast.variables.BinaryOpNode;
import ast.variables.LiteralNode;
import ast.variables.VariableDeclarationNode;
import ast.variables.VariableNode;
import low.module.LLVisitorMain;

import java.util.ArrayList;
import java.util.List;

public class FunctionEmitter {
    private final LLVisitorMain visitor;

    public FunctionEmitter(LLVisitorMain visitor) {
        this.visitor = visitor;
    }

    public String emit(FunctionNode fn) {
        StringBuilder sb = new StringBuilder();

        // 1. Registro dos parâmetros no varTypes antes da dedução
        for (int i = 0; i < fn.getParams().size(); i++) {
            String name = fn.getParams().get(i);
            String type = fn.getParamTypes().get(i);
            visitor.putVarType(name, type);
        }

        // 2. Deduz tipo de retorno se for void e tiver ReturnNode
        String llvmRetType = fn.getReturnType();
        if (llvmRetType.equals("void") && containsReturn(fn)) {
            llvmRetType = deduceReturnType(fn);
        }
        llvmRetType = mapType(llvmRetType);

        // 3. Parâmetros LLVM
        List<String> paramLLVM = new ArrayList<>();
        for (int i = 0; i < fn.getParams().size(); i++) {
            paramLLVM.add(mapType(fn.getParamTypes().get(i)) + " %" + fn.getParams().get(i));
        }

        // 4. Assinatura da função
        sb.append("; === Função: ").append(fn.getName()).append(" ===\n");
        sb.append("define ").append(llvmRetType).append(" @").append(fn.getName())
                .append("(").append(String.join(", ", paramLLVM)).append(") {\nentry:\n");

        // 5. Alloca + store dos parâmetros
        for (int i = 0; i < fn.getParams().size(); i++) {
            String name = fn.getParams().get(i);
            String type = fn.getParamTypes().get(i);
            VariableDeclarationNode paramNode = new VariableDeclarationNode(name, type, null);
            sb.append(visitor.varEmitter.emitAlloca(paramNode));
            sb.append("  store ").append(mapType(type)).append(" %").append(name)
                    .append(", ").append(mapType(type)).append("* ").append(visitor.varEmitter.getVarPtr(name)).append("\n");
        }

        // 6. Corpo da função
        for (ASTNode stmt : fn.getBody()) {
            sb.append(stmt.accept(visitor));
        }

        // 7. Retorno padrão se void e não houver ReturnNode
        if (llvmRetType.equals("void") && !containsReturn(fn)) {
            sb.append("  ret void\n");
        }

        sb.append("}\n\n");
        return sb.toString();
    }

    // Checa se existe algum ReturnNode no corpo
    private boolean containsReturn(FunctionNode fn) {
        for (ASTNode stmt : fn.getBody()) {
            if (stmt instanceof ReturnNode) return true;
        }
        return false;
    }

    // Deduz o tipo do primeiro ReturnNode com expressão
    private String deduceReturnType(FunctionNode fn) {
        for (ASTNode stmt : fn.getBody()) {
            if (stmt instanceof ReturnNode ret && ret.expr != null) {
                return inferType(ret.expr);
            }
        }
        return "void";
    }

    // Inferência de tipo completa baseada no AST
    private String inferType(ASTNode node) {
        if (node instanceof LiteralNode lit) {
            return lit.value.getType(); // int, double, boolean, string
        } else if (node instanceof VariableNode var) {
            String type = visitor.getVarType(var.getName());
            if (type == null) throw new RuntimeException("Variável não declarada: " + var.getName());
            return type;
        } else if (node instanceof BinaryOpNode bin) {
            String leftType = inferType(bin.left);
            String rightType = inferType(bin.right);

            // Promover int -> double se necessário
            if (!leftType.equals(rightType)) {
                if ((leftType.equals("int") && rightType.equals("double")) || (leftType.equals("double") && rightType.equals("int"))) {
                    return "double";
                } else {
                    throw new RuntimeException("Tipos incompatíveis na operação binária: " + leftType + " vs " + rightType);
                }
            }
            return leftType; // assume iguais
        } else if (node instanceof ReturnNode ret) {
            return inferType(ret.expr);
        }
//        else if (node instanceof FunctionCallNode call) {
//            FunctionNode fn = visitor.getFunctionNode(call.getName());
//            if (fn == null) throw new RuntimeException("Função não encontrada: " + call.getName());
//            return fn.getReturnType();
//        }
        throw new RuntimeException("Não foi possível inferir tipo de " + node.getClass());
    }

    // Mapeia tipos AST para LLVM
    private String mapType(String type) {
        return switch (type) {
            case "int" -> "i32";
            case "double" -> "double";
            case "boolean" -> "i1";
            case "string", "list", "var" -> "i8*";
            case "void" -> "void";
            default -> throw new RuntimeException("Tipo não suportado: " + type);
        };
    }
}
