package low.functions;

import ast.ASTNode;
import ast.exceptions.ReturnNode;
import ast.functions.FunctionCallNode;
import ast.functions.FunctionNode;
import ast.ifstatements.IfNode;
import ast.loops.WhileNode;
import ast.variables.BinaryOpNode;
import ast.variables.LiteralNode;
import ast.variables.VariableDeclarationNode;
import ast.variables.VariableNode;
import low.module.LLVisitorMain;

import java.util.HashMap;
import java.util.Map;

public class ReturnTypeInferer {
    private final LLVisitorMain visitor;

    public ReturnTypeInferer(LLVisitorMain visitor, TypeMapper typeMapper) {
        this.visitor = visitor;
    }

    public String deduceReturnType(FunctionNode fn) {
        // Map temporário para variáveis locais durante a inferência
        Map<String, String> localVars = new HashMap<>();

        // Primeiro, registra parâmetros da função
        for (int i = 0; i < fn.getParams().size(); i++) {
            localVars.put(fn.getParams().get(i), fn.getParamTypes().get(i));
        }

        // Percorre o corpo da função
        for (ASTNode stmt : fn.getBody()) {
            // Registra variáveis declaradas localmente
            collectVarDecls(stmt, localVars);

            // Se encontrar ReturnNode com expressão, infere tipo
            if (stmt instanceof ReturnNode ret && ret.expr != null) {
                return inferType(ret.expr, localVars);
            }
        }

        return "void";
    }

    private void collectVarDecls(ASTNode node, Map<String, String> localVars) {
        if (node instanceof VariableDeclarationNode decl) {
            localVars.put(decl.getName(), decl.getType());
        } else if (node instanceof IfNode ifn) {
            ifn.getThenBranch().forEach(stmt -> collectVarDecls(stmt, localVars));
            if (ifn.getElseBranch() != null) {
                ifn.getElseBranch().forEach(stmt -> collectVarDecls(stmt, localVars));
            }
        } else if (node instanceof WhileNode wn) {
            for (ASTNode stmt : wn.getBody()) {
                collectVarDecls(stmt, localVars);
            }
        }

        // Pode expandir para outros nós compostos se necessário
    }

    public String inferType(ASTNode node, Map<String, String> localVars) {
        if (node instanceof LiteralNode lit) {
            return lit.value.type();
        } else if (node instanceof VariableNode var) {
            String type = localVars.getOrDefault(var.getName(), visitor.getVarType(var.getName()));
            if (type == null) throw new RuntimeException("Variável não declarada: " + var.getName());
            return type;
        } else if (node instanceof BinaryOpNode bin) {
            String l = inferType(bin.left, localVars);
            String r = inferType(bin.right, localVars);
            if (!l.equals(r)) {
                if ((l.equals("int") && r.equals("double")) || (l.equals("double") && r.equals("int"))) {
                    return "double";
                }
                throw new RuntimeException("Tipos incompatíveis: " + l + " vs " + r);
            }
            return l;
        } else if (node instanceof ReturnNode ret) {
            return inferType(ret.expr, localVars);
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
