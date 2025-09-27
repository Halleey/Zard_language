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


        for (int i = 0; i < fn.getParams().size(); i++) {
            String name = fn.getParams().get(i);
            String type = fn.getParamTypes().get(i);
            visitor.putVarType(name, type);
        }


        String llvmRetType = fn.getReturnType();
        if (llvmRetType.equals("void") && containsReturn(fn)) {
            llvmRetType = deduceReturnType(fn);
        }
        llvmRetType = mapType(llvmRetType);


        List<String> paramLLVM = new ArrayList<>();
        for (int i = 0; i < fn.getParams().size(); i++) {
            paramLLVM.add(mapType(fn.getParamTypes().get(i)) + " %" + fn.getParams().get(i));
        }

        sb.append("; === Função: ").append(fn.getName()).append(" ===\n");
        sb.append("define ").append(llvmRetType).append(" @").append(fn.getName())
                .append("(").append(String.join(", ", paramLLVM)).append(") {\nentry:\n");

        for (int i = 0; i < fn.getParams().size(); i++) {
            String name = fn.getParams().get(i);
            String type = fn.getParamTypes().get(i);

            // cria alloca com nome diferente (%a.addr)
            String ptrName = "%" + name + ".addr";
            VariableDeclarationNode paramNode = new VariableDeclarationNode(ptrName, type, null);
            sb.append(visitor.varEmitter.emitAlloca(paramNode)); // agora cria %a.addr

            // store do parâmetro para o ponteiro local
            sb.append("  store ").append(mapType(type)).append(" %").append(name)
                    .append(", ").append(mapType(type)).append("* ").append(ptrName).append("\n");

            // registra o ponteiro local no VariableEmitter
            visitor.varEmitter.registerVarPtr(name, ptrName);
        }

        for (ASTNode stmt : fn.getBody()) {
            sb.append(stmt.accept(visitor));
        }


        if (llvmRetType.equals("void") && !containsReturn(fn)) {
            sb.append("  ret void\n");
        }

        sb.append("}\n\n");
        return sb.toString();
    }

    private boolean containsReturn(FunctionNode fn) {
        for (ASTNode stmt : fn.getBody()) {
            if (stmt instanceof ReturnNode) return true;
        }
        return false;
    }

    private String deduceReturnType(FunctionNode fn) {
        for (ASTNode stmt : fn.getBody()) {
            if (stmt instanceof ReturnNode ret && ret.expr != null) {
                return inferType(ret.expr);
            }
        }
        return "void";
    }

    private String inferType(ASTNode node) {
        if (node instanceof LiteralNode lit) {
            return lit.value.getType();
        } else if (node instanceof VariableNode var) {
            String type = visitor.getVarType(var.getName());
            if (type == null) throw new RuntimeException("Variável não declarada: " + var.getName());
            return type;
        } else if (node instanceof BinaryOpNode bin) {
            String leftType = inferType(bin.left);
            String rightType = inferType(bin.right);
            if (!leftType.equals(rightType)) {
                if ((leftType.equals("int") && rightType.equals("double")) ||
                        (leftType.equals("double") && rightType.equals("int"))) {
                    return "double";
                } else {
                    throw new RuntimeException("Tipos incompatíveis na operação binária: " + leftType + " vs " + rightType);
                }
            }
            return leftType;
        } else if (node instanceof ReturnNode ret) {
            return inferType(ret.expr);
        }
        throw new RuntimeException("Não foi possível inferir tipo de " + node.getClass());
    }

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
