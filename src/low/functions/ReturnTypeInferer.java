package low.functions;

import ast.ASTNode;
import ast.exceptions.ReturnNode;
import ast.functions.FunctionCallNode;
import ast.functions.FunctionNode;
import ast.variables.BinaryOpNode;
import ast.variables.LiteralNode;
import ast.variables.VariableNode;
import low.module.LLVisitorMain;

class ReturnTypeInferer {
    private final LLVisitorMain visitor;
    private final TypeMapper typeMapper;

    public ReturnTypeInferer(LLVisitorMain visitor, TypeMapper typeMapper) {
        this.visitor = visitor;
        this.typeMapper = typeMapper;
    }

    public String deduceReturnType(FunctionNode fn) {
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
            String l = inferType(bin.left);
            String r = inferType(bin.right);
            if (!l.equals(r)) {
                if ((l.equals("int") && r.equals("double")) || (l.equals("double") && r.equals("int"))) {
                    return "double";
                }
                throw new RuntimeException("Tipos incompatíveis: " + l + " vs " + r);
            }
            return l;
        } else if (node instanceof ReturnNode ret) {
            return inferType(ret.expr);
        } else if (node instanceof FunctionCallNode call) {
            String type = visitor.getFunctionType(call.getName());
            if (type == null) throw new RuntimeException("Função não registrada: " + call.getName());
            if ("any".equals(type) && visitor.getCallEmitter().isBeingDeduced(call.getName())) {
                return "int"; // fallback seguro
            }
            return type;
        }
        throw new RuntimeException("Não foi possível inferir tipo de " + node.getClass());
    }
}
