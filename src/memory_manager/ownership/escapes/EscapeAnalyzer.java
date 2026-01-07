package memory_manager.ownership.escapes;

import ast.ASTNode;
import ast.exceptions.ReturnNode;
import ast.functions.FunctionCallNode;
import ast.functions.FunctionNode;
import ast.lists.ListAddNode;
import ast.structs.ImplNode;
import ast.structs.StructInstanceNode;
import ast.variables.AssignmentNode;
import ast.variables.VariableDeclarationNode;
import ast.variables.VariableNode;

import java.util.*;

public class EscapeAnalyzer {

    private final EscapeInfo info = new EscapeInfo();
    private final Map<String, StructInstanceNode> structInstances = new HashMap<>();
    private final Set<String> returnedStructs = new HashSet<>();

    public EscapeInfo analyze(List<ASTNode> ast) {
        visitList(ast);
        propagate();
        return info;
    }
    private void visitList(List<ASTNode> nodes) {
        for (ASTNode n : nodes) {
            visit(n);
        }
    }

    private void visit(ASTNode node) {

        if (node instanceof VariableDeclarationNode vd) {
            info.declare(vd.getName());
        }

        if (node instanceof AssignmentNode asg) {

            ASTNode val = asg.getValueNode();

            // se a variável recebe uma struct literal
            if (val instanceof StructInstanceNode sin) {
                structInstances.put(asg.getName(), sin);
            }

            // se recebe outra variável struct
            if (val instanceof VariableNode v) {
                // se a outra variável já tinha instância, propaga referência
                if (structInstances.containsKey(v.getName())) {
                    structInstances.put(asg.getName(), structInstances.get(v.getName()));
                }
            }
        }
        if (node instanceof StructInstanceNode sin) {
        }

        if (node instanceof ListAddNode add) {
            ASTNode value = add.getValuesNode();
            if (value instanceof VariableNode v) {
                info.markEscapes(v.getName()); // struct usado na lista => heap
            }
            if (value instanceof StructInstanceNode sin) {
                info.markEscapes("<inline_struct@" + sin.hashCode() + ">");
            }
        }

        if (node instanceof ReturnNode ret) {
            ASTNode val = ret.getExpr();
            if (val instanceof VariableNode v) {
                info.markEscapes(v.getName());
                returnedStructs.add(v.getName());
            }
            if (val instanceof StructInstanceNode sin) {
                info.markEscapes("<inline_struct@" + sin.hashCode() + ">");
            }
        }

        if (node instanceof ImplNode impl) {
            // visita métodos dentro do impl
            for (FunctionNode fn : impl.getMethods()) {
                visit(fn);
            }
        }


        for (ASTNode child : node.getChildren()) {
            visit(child);
        }
    }
    private void propagate() {
        boolean changed = true;

        while (changed) {
            changed = false;
            for (var entry : structInstances.entrySet()) {

                String varName = entry.getKey();
                StructInstanceNode instance = entry.getValue();

                if (info.escapes(varName)) {

                    // pega campos usados no instance node
                    for (ASTNode child : instance.getChildren()) {

                        if (child instanceof VariableNode v) {
                            if (!info.escapes(v.getName())) {
                                info.markEscapes(v.getName());
                                changed = true;
                            }
                        }
                    }
                }
            }
        }
    }
}

