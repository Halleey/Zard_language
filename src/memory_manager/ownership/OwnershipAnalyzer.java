package memory_manager.ownership;

import ast.ASTNode;
import ast.exceptions.ReturnNode;
import ast.lists.ListAddNode;
import ast.structs.StructFieldAccessNode;
import ast.structs.StructUpdateNode;
import ast.variables.AssignmentNode;
import ast.variables.VariableDeclarationNode;
import ast.variables.VariableNode;
import memory_manager.ownership.enums.OwnerShipAction;
import memory_manager.ownership.enums.OwnershipState;
import memory_manager.ownership.graphs.OwnershipGraph;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class OwnershipAnalyzer {

    private final Map<String, VarOwnerShip> vars = new LinkedHashMap<>();
    private final List<OwnershipAnnotation> annotations = new ArrayList<>();
    private final boolean debug;

    private final OwnershipGraph graph = new OwnershipGraph();

    public OwnershipAnalyzer(boolean debug) {
        this.debug = debug;
    }

    public List<OwnershipAnnotation> getAnnotations() {
        return annotations;
    }

    public OwnershipGraph getGraph() {
        return graph;
    }

    public void analyzeBlock(List<ASTNode> nodes) {
        for (ASTNode node : nodes) {
            analyzeNode(node);
        }
    }

    private void analyzeNode(ASTNode node) {

        boolean visitChildren = true;

        if (node instanceof VariableDeclarationNode decl) {
            handleDeclaration(decl); // decide tudo
        }

        else if (node instanceof AssignmentNode assign) {
            handleAssignment(assign);
            visitChildren = false;
        }

        else if (node instanceof StructFieldAccessNode sfa) {
            handleStructFieldAssignment(sfa);
            visitChildren = false;
        }

        else if (node instanceof ListAddNode add) {
            handleListAdd(add);
            visitChildren = false;
        }

        else if (node instanceof StructUpdateNode up) {
            handleInlineUpdate(up);
            visitChildren = false;
        }

        else if (node instanceof ReturnNode ret) {
            handleReturn(ret);
            visitChildren = false;
        }

        else if (node instanceof VariableNode var) {
            handleVariableUse(var);
        }

        if (visitChildren) {
            for (ASTNode child : node.getChildren()) {
                analyzeNode(child);
            }
        }
    }
    private void handleDeclaration(VariableDeclarationNode decl) {
        String type = decl.getType();
        if (isPrimitive(type)) {
            log("declare " + decl.getName() + " => PRIMITIVE, ignorado para ownership graph");
            return; // não cria nó no grafo nem adiciona OWNED
        }

        // restante normal
        VarOwnerShip v = new VarOwnerShip(decl.getName());
        vars.put(decl.getName(), v);

        annotations.add(new OwnershipAnnotation(
                decl,
                OwnerShipAction.OWNED,
                decl.getName(),
                null
        ));

        graph.declareVar(decl.getName());

        log("declare " + decl.getName() + " => OWNED");
    }


    private boolean isPrimitive(String type) {
        if (type == null) return false;
        type = type.trim().toLowerCase();
        return type.equals("int") || type.equals("float") ||
                type.equals("double") || type.equals("bool") ||
                type.equals("char");
    }


    private void handleVariableUse(VariableNode var) {

        VarOwnerShip v = vars.get(var.getName());
        if (v == null) return;

        if (v.state == OwnershipState.MOVED) {
            throw new RuntimeException(
                    "Use-after-move detected: " + var.getName()
            );
        }

        annotations.add(new OwnershipAnnotation(
                var,
                OwnerShipAction.BORROW,
                var.getName(),
                null
        ));

        log("BORROW use " + var.getName());
    }

    private void handleAssignment(AssignmentNode assign) {

        if (!(assign.getValueNode() instanceof VariableNode rhs)) {
            return;
        }

        String lhs = assign.getName();
        String rhsName = rhs.getName();
        VarOwnerShip rhsVar = vars.get(rhsName);

        /* ===== DEEP COPY ===== */
        if (vars.containsKey(lhs) && rhsVar != null) {

            if (rhsVar.state == OwnershipState.MOVED) {
                throw new RuntimeException(
                        "Copy from moved value: " + rhsName
                );
            }

            vars.put(lhs, new VarOwnerShip(lhs));

            annotations.add(new OwnershipAnnotation(
                    assign,
                    OwnerShipAction.DEEP_COPY,
                    rhsName,
                    lhs
            ));

            graph.deepCopy(rhsName, lhs);

            log("DEEP_COPY " + rhsName + " -> " + lhs);
            return;
        }

        /* ===== MOVE ===== */
        if (rhsVar != null) {
            rhsVar.state = OwnershipState.MOVED;
        }

        vars.put(lhs, new VarOwnerShip(lhs));

        annotations.add(new OwnershipAnnotation(
                assign,
                OwnerShipAction.MOVED,
                rhsName,
                lhs
        ));

        graph.move(rhsName, lhs);

        log("MOVE " + rhsName + " -> " + lhs);
    }

    private void handleStructFieldAssignment(StructFieldAccessNode sfa) {

        if (!(sfa.getValue() instanceof VariableNode var)) return;

        String source = var.getName();
        String target = resolveStructFieldTarget(sfa);

        VarOwnerShip v = vars.get(source);
        if (v == null) return;

        if (v.state == OwnershipState.MOVED) {
            throw new RuntimeException(
                    "Use-after-move in struct field assignment: " + source
            );
        }

        v.state = OwnershipState.MOVED;

        annotations.add(new OwnershipAnnotation(
                sfa,
                OwnerShipAction.MOVED,
                source,
                target
        ));

        graph.move(source, target);

        log("MOVE " + source + " -> " + target);
    }

    private void handleListAdd(ListAddNode add) {

        if (!(add.getValuesNode() instanceof VariableNode var)) return;

        String source = var.getName();
        String target = resolveListTarget(add.getListNode());

        VarOwnerShip v = vars.get(source);
        if (v == null) return;

        if (v.state == OwnershipState.MOVED) {
            throw new RuntimeException(
                    "Use-after-move in list add: " + source
            );
        }

        v.state = OwnershipState.MOVED;

        annotations.add(new OwnershipAnnotation(
                add,
                OwnerShipAction.MOVED,
                source,
                target
        ));

        graph.move(source, target);

        log("MOVE " + source + " -> LIST " + target);
    }

    private void handleInlineUpdate(StructUpdateNode up) {

        if (!(up.getTargetStruct() instanceof VariableNode var)) return;

        VarOwnerShip v = vars.get(var.getName());
        if (v == null) return;

        if (v.state == OwnershipState.MOVED) {
            throw new RuntimeException(
                    "Inline update on moved value: " + var.getName()
            );
        }

        log("INLINE UPDATE consumes exclusive ownership of " + var.getName());
    }

    private void handleReturn(ReturnNode ret) {

        if (!(ret.getExpr() instanceof VariableNode var)) return;

        VarOwnerShip v = vars.get(var.getName());
        if (v == null) return;

        v.state = OwnershipState.MOVED;

        annotations.add(new OwnershipAnnotation(
                ret,
                OwnerShipAction.MOVED,
                var.getName(),
                "return"
        ));

        graph.move(var.getName(), "return");

        log("MOVE via return: " + var.getName());
    }

    private String resolveStructFieldTarget(StructFieldAccessNode sfa) {

        StringBuilder sb = new StringBuilder();
        ASTNode base = sfa.getStructInstance();

        while (base instanceof StructFieldAccessNode nested) {
            sb.insert(0, "." + nested.getFieldName());
            base = nested.getStructInstance();
        }

        if (base instanceof VariableNode v) {
            sb.insert(0, v.getName());
        } else {
            sb.insert(0, "<anonymous>");
        }

        sb.append(".").append(sfa.getFieldName());
        return sb.toString();
    }

    private String resolveListTarget(ASTNode node) {

        if (node instanceof VariableNode v) {
            return v.getName();
        }

        if (node instanceof StructFieldAccessNode sfa) {
            return resolveStructFieldTarget(sfa);
        }

        return "<anonymous-list>";
    }


    public void dumpFinalStates() {
        System.out.println("==== FINAL OWNERSHIP STATE (linear) ====");
        for (VarOwnerShip v : vars.values()) {
            System.out.println(v);
        }

        System.out.println();
        graph.dump();
    }

    private void log(String msg) {
        if (debug) {
            System.out.println("[OWNERSHIP] " + msg);
        }
    }
}
