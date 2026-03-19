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
import low.module.builders.LLVMTYPES;
import low.module.builders.mappers.LLVMTypeMapper;
import low.module.builders.primitives.LLVMDouble;
import low.module.builders.primitives.LLVMInt;
import low.module.builders.primitives.LLVMVoid;

import java.util.HashMap;
import java.util.Map;public class ReturnTypeInferer {

    private final LLVisitorMain visitor;

    public ReturnTypeInferer(LLVisitorMain visitor) {
        this.visitor = visitor;
    }

    public TypeInfos deduceReturnType(FunctionNode fn) {

        Map<String, TypeInfos> localVars = new HashMap<>();

        // parâmetros
        for (ParamInfo p : fn.getParameters()) {
            Type type = p.type();
            LLVMTYPES llvmType = LLVMTypeMapper.from(type);
            localVars.put(p.name(), new TypeInfos(type, llvmType));
        }

        for (ASTNode stmt : fn.getBody()) {
            collectVarDecls(stmt, localVars);

            if (stmt instanceof ReturnNode ret && ret.expr != null) {
                return inferType(ret.expr, localVars);
            }
        }

        return new TypeInfos(PrimitiveTypes.VOID, new LLVMVoid());
    }

    private void collectVarDecls(ASTNode node, Map<String, TypeInfos> localVars) {

        if (node instanceof VariableDeclarationNode decl) {
            Type type = decl.getType();
            LLVMTYPES llvmType = LLVMTypeMapper.from(type);
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
            return new TypeInfos(type, LLVMTypeMapper.from(type));
        }

        if (node instanceof VariableNode var) {
            TypeInfos type = localVars.getOrDefault(
                    var.getName(),
                    visitor.getVarType(var.getName())
            );

            if (type == null)
                throw new RuntimeException("Variável não declarada: " + var.getName());

            return type;
        }

        // ===== BIN OP =====
        if (node instanceof BinaryOpNode bin) {
            TypeInfos l = inferType(bin.left, localVars);
            TypeInfos r = inferType(bin.right, localVars);

            if (!l.getType().equals(r.getType())) {

                if ((l.getType() == PrimitiveTypes.INT && r.getType() == PrimitiveTypes.DOUBLE) ||
                        (l.getType() == PrimitiveTypes.DOUBLE && r.getType() == PrimitiveTypes.INT)) {

                    return new TypeInfos(
                            PrimitiveTypes.DOUBLE,
                            new LLVMDouble()
                    );
                }

                throw new RuntimeException("Tipos incompatíveis: " + l.getType() + " vs " + r.getType());
            }

            return l;
        }

        if (node instanceof ReturnNode ret) {
            return inferType(ret.expr, localVars);
        }

        if (node instanceof FunctionCallNode call) {

            TypeInfos type = visitor.getFunctionType(call.getName());

            if (type == null)
                throw new RuntimeException("Função não registrada: " + call.getName());

            if (type.getType() == UnknownType.UNKNOWN_TYPE &&
                    visitor.getCallEmitter().isBeingDeduced(call.getName())) {

                return new TypeInfos(
                        PrimitiveTypes.INT,
                        new LLVMInt()
                );
            }

            return type;
        }

        throw new RuntimeException("Não foi possível inferir tipo de " + node.getClass());
    }
}