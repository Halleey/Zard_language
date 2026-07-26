package memory_manager.ownership.escapes;

import ast.ASTNode;
import ast.exceptions.ReturnNode;
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

    private final Map<String, StructInstanceNode> structInstances =
            new HashMap<>();

    private final Set<String> returnedStructs =
            new HashSet<>();

    public EscapeInfo analyze(List<ASTNode> ast) {

        visitList(ast);

        propagate();

        return info;
    }

    private void visitList(List<ASTNode> nodes) {

        for (ASTNode node : nodes) {
            visit(node);
        }
    }

    private void visit(ASTNode node) {

        /*
         * declaração
         */

        System.out.println(
                "[ESCAPE] " +
                        node.getClass().getSimpleName()
        );

        if (node instanceof VariableDeclarationNode vd) {
            info.declare(vd.getName());
        }

        /*
         * atribuições
         */

        if (node instanceof AssignmentNode asg) {

            ASTNode value = asg.getValueNode();

            /*
             * struct literal
             */

            if (value instanceof StructInstanceNode sin) {
                structInstances.put(
                        asg.getName(),
                        sin
                );
            }

            /*
             * alias:
             *
             * a = b
             */

            if (value instanceof VariableNode v) {

                if (structInstances.containsKey(v.getName())) {

                    structInstances.put(
                            asg.getName(),
                            structInstances.get(v.getName())
                    );
                }
            }
        }

        /*
         * lista.add(x)
         */

        if (node instanceof ListAddNode add) {

            System.out.println(
                    "[ESCAPE] FOUND LIST ADD"
            );

            ASTNode value = add.getValuesNode();

            System.out.println(
                    "[ESCAPE] VALUE TYPE = " +
                            value.getClass().getSimpleName()
            );

            if (value instanceof VariableNode v) {

                System.out.println(
                        "[ESCAPE] MARK LOOP " +
                                v.getName()
                );

                info.markEscape(
                        v.getName(),
                        EscapeLevel.LOOP
                );
            }
        }

        /*
         * return x
         */

        if (node instanceof ReturnNode ret) {

            ASTNode value = ret.getExpr();

            if (value instanceof VariableNode v) {

                info.markEscape(
                        v.getName(),
                        EscapeLevel.FUNCTION
                );

                returnedStructs.add(v.getName());
            }

            if (value instanceof StructInstanceNode sin) {

                info.markEscape(
                        "<inline_struct@" + sin.hashCode() + ">",
                        EscapeLevel.FUNCTION
                );
            }
        }

        if (node instanceof ImplNode impl) {

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

                String ownerVar = entry.getKey();

                StructInstanceNode instance =
                        entry.getValue();

                EscapeLevel ownerLevel =
                        info.getLevel(ownerVar);

                if (ownerLevel == EscapeLevel.NONE) {
                    continue;
                }

                /*
                 * se a struct escapa,
                 * seus campos também escapam
                 */

                for (ASTNode child : instance.getChildren()) {

                    if (!(child instanceof VariableNode v)) {
                        continue;
                    }

                    EscapeLevel childLevel = info.getLevel(v.getName());

                    if (ownerLevel.ordinal() > childLevel.ordinal()) {
                        info.markEscape(v.getName(), ownerLevel);
                        changed = true;
                    }
                }
            }
        }
    }
}