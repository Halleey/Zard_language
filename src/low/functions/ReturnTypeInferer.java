package low.functions;

import ast.ASTNode;
import ast.exceptions.ReturnNode;
import ast.functions.FunctionCallNode;
import ast.functions.FunctionNode;
import ast.functions.ParamInfo;
import ast.ifstatements.IfNode;
import ast.loops.WhileNode;
import ast.variables.BinaryOpNode;
import ast.variables.LiteralNode;
import ast.variables.VariableDeclarationNode;
import ast.variables.VariableNode;
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

        for (ParamInfo p : fn.getParameters()) {

            String sourceType = p.type();
            String llvmType   = typeMapper.toLLVM(sourceType);

            String elemType = null;
            if (sourceType.startsWith("List<") && sourceType.endsWith(">")) {
                elemType = sourceType.substring(5, sourceType.length() - 1);
            }

            localVars.put(
                    p.name(),
                    new TypeInfos(sourceType, llvmType, elemType)
            );
        }

        for (ASTNode stmt : fn.getBody()) {
            collectVarDecls(stmt, localVars);

            if (stmt instanceof ReturnNode ret && ret.expr != null) {
                return inferType(ret.expr, localVars);
            }
        }

        return new TypeInfos("void", "void", null);
    }
    private void collectVarDecls(ASTNode node, Map<String, TypeInfos> localVars) {
        if (node instanceof VariableDeclarationNode decl) {
            String srcType  = decl.getType();
            String llvmType = typeMapper.toLLVM(srcType);
            String elemType = (srcType.startsWith("List<") && srcType.endsWith(">"))
                    ? srcType.substring(5, srcType.length() - 1)
                    : null;

            localVars.put(decl.getName(), new TypeInfos(srcType, llvmType, elemType));
        }
        else if (node instanceof IfNode ifn) {
            ifn.getThenBranch().forEach(stmt -> collectVarDecls(stmt, localVars));
            if (ifn.getElseBranch() != null) {
                ifn.getElseBranch().forEach(stmt -> collectVarDecls(stmt, localVars));
            }
        }
        else if (node instanceof WhileNode wn) {
            for (ASTNode stmt : wn.getBody()) {
                collectVarDecls(stmt, localVars);
            }
        }
    }

    public TypeInfos inferType(ASTNode node, Map<String, TypeInfos> localVars) {
        if (node instanceof LiteralNode lit) {
            String srcType  = lit.value.type();
            String llvmType = typeMapper.toLLVM(srcType);
            return new TypeInfos(srcType, llvmType, null);
        }
        else if (node instanceof VariableNode var) {
            TypeInfos type = localVars.getOrDefault(var.getName(), visitor.getVarType(var.getName()));
            if (type == null) throw new RuntimeException("Variável não declarada: " + var.getName());
            return type;
        }
        else if (node instanceof BinaryOpNode bin) {
            TypeInfos l = inferType(bin.left, localVars);
            TypeInfos r = inferType(bin.right, localVars);

            if (!l.getSourceType().equals(r.getSourceType())) {
                if ((l.getSourceType().equals("int") && r.getSourceType().equals("double"))
                        || (l.getSourceType().equals("double") && r.getSourceType().equals("int"))) {
                    return new TypeInfos("double", "double", null);
                }
                throw new RuntimeException("Tipos incompatíveis: " + l.getSourceType() + " vs " + r.getSourceType());
            }
            return l;
        }
        else if (node instanceof ReturnNode ret) {
            return inferType(ret.expr, localVars);
        }
        else if (node instanceof FunctionCallNode call) {
            TypeInfos type = visitor.getFunctionType(call.getName());
            if (type == null) throw new RuntimeException("Função não registrada: " + call.getName());

            if ("any".equals(type.getSourceType())
                    && visitor.getCallEmitter().isBeingDeduced(call.getName())) {
                return new TypeInfos("int", "i32", null);
            }
            return type;
        }

        throw new RuntimeException("Não foi possível inferir tipo de " + node.getClass());
    }
}
