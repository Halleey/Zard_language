package memory_manager.borrows;

import ast.ASTNode;
import ast.lists.ListAddNode;
import ast.structs.StructFieldAccessNode;
import ast.structs.StructInstaceNode;
import ast.variables.AssignmentNode;
import ast.variables.VariableDeclarationNode;
import ast.variables.VariableNode;

import java.util.IdentityHashMap;
import java.util.Map;


public class OwnershipAnalyzer {

    private final Map<ASTNode, OwnerShipInfo> ownership = new IdentityHashMap<>();
    private final Map<ASTNode, String> realNames = new IdentityHashMap<>();
    private static final boolean DEBUG = true;
    private final Map<ASTNode, ASTNode> variableBinding = new IdentityHashMap<>();

    public void analyze(ASTNode root) {
        debug("=== Ownership analysis start ===");
        visit(root);
        debug("=== Ownership analysis end ===");
        dumpFinalState();
    }
    private void visit(ASTNode node) {
        if (node == null) return;

        if (node instanceof VariableDeclarationNode vd) {

            ASTNode init = vd.getInitializer();
            if (init != null) {

                if (init instanceof StructInstaceNode sin) {

                    ownership.put(sin, new OwnerShipInfo(
                            OwnershipState.OWNED,
                            vd,
                            sin,
                            AssignKind.COPY,
                            0
                    ));

                    variableBinding.put(vd, sin);
                    realNames.put(vd, vd.getName());
                    realNames.put(sin, vd.getName());

                    debug("VAR '" + vd.getName() + "' OWNS NEW struct");
                }
                else {
                    OwnerShipInfo src = resolveOwnership(init);
                    if (src != null) {

                        variableBinding.put(vd, src.origin);
                        realNames.put(vd, vd.getName());
                        AssignmentNode fakeAssign =
                                new AssignmentNode(vd.getName(), init);

                        fakeAssign.setAssignKind(AssignKind.COPY);

                        handleAssignment(fakeAssign);

                        debug("VAR '" + vd.getName() + "' INIT via assignment");
                    }
                }
            }
        }

        else if (node instanceof AssignmentNode an) {
            handleAssignment(an);
        }

        else if (node instanceof StructFieldAccessNode fa &&
                fa.getValue() != null) {
            handleFieldAssignment(fa);
        }

        else if (node instanceof ListAddNode ln) {
            handleListAdd(ln);
        }

        for (ASTNode child : node.getChildren()) {
            visit(child);
        }
    }

    private void handleAssignment(AssignmentNode an) {
        OwnerShipInfo src = resolveOwnership(an.getValueNode());
        if (src == null) return;

        boolean isDeepCopy = false;

        if (src.origin instanceof StructInstaceNode srcStruct) {
            System.out.println("Entrou aqui");
            // pega o nome da variável destino
            String targetVarName = an.getName();
            // pega a declaração da variável destino
            VariableDeclarationNode targetDecl =
                    findVariableDeclaration(targetVarName);

            if (targetDecl != null) {
                String targetType = targetDecl.getType();              // Struct<Pessoa>
                String sourceType = "Struct<" + srcStruct.getName() + ">";
                System.out.println("segundo");
                if (targetType.equals(sourceType)) {
                    System.out.println("terceiro");
                    isDeepCopy = true;
                    an.setAssignKind(AssignKind.DEEP_COPY);
                    System.out.println("DEEP COPY detectado: " + sourceType);
                }
            }

        }

        if (an.getAssignKind() == AssignKind.MOVE) {
            if (src.state == OwnershipState.MOVED) {
                error("Uso após move", an);
            }
            src.state = OwnershipState.MOVED;
        }

        OwnerShipInfo info = new OwnerShipInfo(
                OwnershipState.OWNED,
                an,
                src.origin,
                an.getAssignKind(),
                src.depth
        );

        info.isDeepCopy = isDeepCopy;

        ownership.put(an, info);
        variableBinding.put(an, src.origin);
        realNames.put(an, an.getName());

        debug(
                "ASSIGN '" + an.getName() + "' " +
                        (isDeepCopy ? "DEEP COPY from " : "OWNS value from ") +
                        shortNode(src.origin)
        );
    }

    private VariableDeclarationNode findVariableDeclaration(String name) {
        for (ASTNode n : variableBinding.keySet()) {
            if (n instanceof VariableDeclarationNode vd &&
                    vd.getName().equals(name)) {
                return vd;
            }
        }
        return null;
    }


    private OwnerShipInfo resolveOwnership(ASTNode node) {


        if (node == null) {
            debug("[resolveOwnership] node is null");
            return null;
        }

        if (node instanceof VariableNode vn) {
            debug("[resolveOwnership] VariableNode -> name = " + vn.getName());

            VariableDeclarationNode decl = vn.getDeclaration();
            debug("[resolveOwnership]   declaration = " + shortNode(decl));

            if (decl != null) {
                ASTNode bound = variableBinding.get(decl);
                debug("[resolveOwnership]   bound instance = " + shortNode(bound));

                if (bound != null) {
                    OwnerShipInfo info = ownership.get(bound);
                    return info;
                } else {
                }
            } else {
                debug("[resolveOwnership]   ❌ declaration == null (binder quebrado)");
            }
        }

        if (node instanceof StructFieldAccessNode fa) {
            debug("[resolveOwnership] StructFieldAccessNode -> field = " + fa.getFieldName());
            debug("[resolveOwnership]   structInstance = " + shortNode(fa.getStructInstance()));

            OwnerShipInfo info = resolveOwnership(fa.getStructInstance());
            debug("[resolveOwnership]   resolved parent ownership = " + infoSummary(info));

            return info;
        }

        OwnerShipInfo direct = ownership.get(node);

        return direct;
    }

    private String infoSummary(OwnerShipInfo info) {
        if (info == null) return "null";
        return
                "state=" + info.state +
                        ", owner=" + shortNode(info.owner) +
                        ", origin=" + shortNode(info.origin) +
                        ", depth=" + info.depth +
                        ", kind=" + info.transferKind;
    }


    private void handleFieldAssignment(StructFieldAccessNode fa) {
        OwnerShipInfo src = resolveOwnership(fa.getValue());
        if (src == null) return;

        OwnerShipInfo parent = resolveOwnership(fa.getStructInstance());
        if (parent == null) {
            error("Struct pai sem ownership", fa);
        }

        if (fa.getAssignKind() == AssignKind.MOVE) {
            if (src.state == OwnershipState.MOVED) {
                error("Valor já movido", fa);
            }
            src.state = OwnershipState.MOVED;
        }

        src.owner = parent.owner;
        src.depth = parent.depth + 1;

        debug(
                "FIELD MOVE: " +
                        shortNode(src.origin) +
                        " agora pertence a " +
                        shortNode(parent.owner)
        );
    }

    private void handleListAdd(ListAddNode ln) {
        OwnerShipInfo src = ownership.get(ln.getValuesNode());
        if (src == null) return;

        if (ln.getAssignKind() == AssignKind.MOVE) {
            if (src.state == OwnershipState.MOVED) {
                error("Valor já movido", ln);
            }
            src.state = OwnershipState.MOVED;
        }

        ownership.put(ln, new OwnerShipInfo(
                OwnershipState.OWNED,
                ln,
                src.origin,
                ln.getAssignKind(),
                src.depth + 1
        ));

        debug("LIST ADD OWNS via " + ln.getAssignKind());
    }

    private void dumpFinalState() {
        debug("=== FINAL OWNERSHIP STATE ===");

        ownership.forEach((node, info) -> {
            debug(
                    shortNode(node) +
                            " | owner=" + shortNode(info.owner) +
                            " | origin=" + shortNode(info.origin) +
                            " | kind=" + info.transferKind +
                            " | depth=" + info.depth +
                            " | state=" + info.state
            );
        });

        debug("=============================");
    }

    private void error(String msg, ASTNode n) {
        throw new RuntimeException(
                "[OwnershipError] " + msg +
                        "\nNode: " + shortNode(n)
        );
    }

    private void debug(String msg) {
        if (DEBUG) {
            System.out.println("[OwnershipDebug] " + msg);
        }
    }

    private String shortNode(ASTNode n) {
        if (n == null) return "null";

        String name = realNames.get(n);
        if (name != null) {
            return n.getClass().getSimpleName() + "(" + name + ")";
        }

        return n.getClass().getSimpleName() + "@" +
                Integer.toHexString(System.identityHashCode(n));
    }
}
