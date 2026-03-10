package low.functions;

import ast.ASTNode;
import ast.exceptions.ReturnNode;
import ast.functions.FunctionCallNode;
import ast.functions.FunctionNode;
import ast.functions.ParamInfo;
import ast.ifstatements.IfNode;
import ast.loops.WhileNode;
import ast.expressions.BinaryOpNode;
import ast.variables.LiteralNode;
import ast.variables.VariableDeclarationNode;
import ast.variables.VariableNode;
import context.statics.symbols.*;
import low.main.TypeInfos;
import low.module.LLVisitorMain;

import java.util.HashMap;
import java.util.Map;
public class ReturnTypeInferer {

    private final LLVisitorMain visitor;
    private final TypeMapper typeMapper;

    public ReturnTypeInferer(LLVisitorMain visitor, TypeMapper typeMapper) {
        this.visitor = visitor;
        this.typeMapper = typeMapper;
    }

    public TypeInfos deduceReturnType(FunctionNode fn) {
        Map<String, TypeInfos> localVars = new HashMap<>();

        // registra parâmetros com Type real
        for (ParamInfo p : fn.getParameters()) {
            Type type = p.type();
            String llvmType = typeMapper.toLLVM(type);
            localVars.put(p.name(), new TypeInfos(type, llvmType));
        }

        for (ASTNode stmt : fn.getBody()) {
            collectVarDecls(stmt, localVars);

            if (stmt instanceof ReturnNode ret && ret.expr != null) {
                return inferType(ret.expr, localVars);
            }
        }

        // função sem return explícito → void
        return new TypeInfos(PrimitiveTypes.VOID, "void");
    }

    private void collectVarDecls(ASTNode node, Map<String, TypeInfos> localVars) {
        if (node instanceof VariableDeclarationNode decl) {
            Type type = decl.getType();
            String llvmType = typeMapper.toLLVM(type);
            localVars.put(decl.getName(), new TypeInfos(type, llvmType));
        }
        else if (node instanceof IfNode ifn) {
            ifn.getThenBranch().forEach(stmt -> collectVarDecls(stmt, localVars));
            if (ifn.getElseBranch() != null) {
                ifn.getElseBranch().forEach(stmt -> collectVarDecls(stmt, localVars));
            }
        }
        else if (node instanceof WhileNode wn) {
            wn.getBody().forEach(stmt -> collectVarDecls(stmt, localVars));
        }
    }

    public TypeInfos inferType(ASTNode node, Map<String, TypeInfos> localVars) {
        if (node instanceof LiteralNode lit) {
            Type type = lit.value.type();
            String llvmType = typeMapper.toLLVM(type);
            return new TypeInfos(type, llvmType);
        }
        else if (node instanceof VariableNode var) {
            TypeInfos type = localVars.getOrDefault(var.getName(), visitor.getVarType(var.getName()));
            if (type == null) throw new RuntimeException("Variável não declarada: " + var.getName());
            return type;
        }
        else if (node instanceof BinaryOpNode bin) {
            TypeInfos l = inferType(bin.left, localVars);
            TypeInfos r = inferType(bin.right, localVars);

            if (!l.getType().equals(r.getType())) {
                // promoção int → double
                if ((l.getType() == PrimitiveTypes.INT && r.getType() == PrimitiveTypes.DOUBLE) ||
                        (l.getType() == PrimitiveTypes.DOUBLE && r.getType() == PrimitiveTypes.INT)) {
                    return new TypeInfos(PrimitiveTypes.DOUBLE, "double");
                }
                throw new RuntimeException("Tipos incompatíveis: " + l.getType() + " vs " + r.getType());
            }
            return l;
        }
        else if (node instanceof ReturnNode ret) {
            return inferType(ret.expr, localVars);
        }
        else if (node instanceof FunctionCallNode call) {
            TypeInfos type = visitor.getFunctionType(call.getName());
            if (type == null) throw new RuntimeException("Função não registrada: " + call.getName());

            // fallback para chamadas recursivas de funções ainda em dedução
            if (type.getType() == UnknownType.UNKNOWN_TYPE &&
                    visitor.getCallEmitter().isBeingDeduced(call.getName())) {
                return new TypeInfos(PrimitiveTypes.INT, "i32");
            }

            return type;
        }

        throw new RuntimeException("Não foi possível inferir tipo de " + node.getClass());
    }

}