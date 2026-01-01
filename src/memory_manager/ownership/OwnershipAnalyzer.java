package memory_manager.ownership;

import ast.ASTNode;
import ast.exceptions.ReturnNode;
import ast.lists.ListAddNode;
import ast.structs.StructFieldAccessNode;
import ast.structs.StructUpdateNode;
import ast.variables.AssignmentNode;
import ast.variables.VariableDeclarationNode;
import ast.variables.VariableNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.*;
import java.util.*;

public class OwnershipAnalyzer {

    private final Map<String, VarOwnerShip> vars = new LinkedHashMap<>();
    private final List<OwnershipAnnotation> annotations = new ArrayList<>();
    private final boolean debug;

    public OwnershipAnalyzer(boolean debug) {
        this.debug = debug;
    }

    public List<OwnershipAnnotation> getAnnotations() {
        return annotations;
    }


    public void analyzeBlock(List<ASTNode> nodes) {
        for (ASTNode node : nodes) {
            analyzeNode(node);
        }
    }

    private void analyzeNode(ASTNode node) {

        boolean visitChildren = true;

        if (node instanceof VariableDeclarationNode decl) {
            handleDeclaration(decl);
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

        VarOwnerShip v = new VarOwnerShip(decl.getName());
        vars.put(decl.getName(), v);

        annotations.add(new OwnershipAnnotation(
                decl,
                OwnerShipAction.OWNED,
                decl.getName(),
                null
        ));

        log("declare " + decl.getName() + " => OWNED");
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

        String lhsName = assign.getName();
        String rhsName = rhs.getName();

        VarOwnerShip rhsVar = vars.get(rhsName);

        // p2 = p1 → DEEP COPY
        if (vars.containsKey(lhsName) && rhsVar != null) {

            if (rhsVar.state == OwnershipState.MOVED) {
                throw new RuntimeException(
                        "Copy from moved value: " + rhsName
                );
            }

            vars.put(lhsName, new VarOwnerShip(lhsName));

            annotations.add(new OwnershipAnnotation(
                    assign,
                    OwnerShipAction.DEEP_COPY,
                    rhsName,
                    lhsName
            ));

            log("DEEP_COPY " + rhsName + " -> " + lhsName);
            return;
        }

        // MOVE
        if (rhsVar != null) {
            rhsVar.state = OwnershipState.MOVED;
        }

        vars.put(lhsName, new VarOwnerShip(lhsName));

        annotations.add(new OwnershipAnnotation(
                assign,
                OwnerShipAction.MOVED,
                rhsName,
                lhsName
        ));

        log("MOVE " + rhsName + " -> " + lhsName);
    }


    private void handleStructFieldAssignment(StructFieldAccessNode sfa) {

        if (!(sfa.getValue() instanceof VariableNode var)) {
            return;
        }

        VarOwnerShip v = vars.get(var.getName());
        if (v == null) return;

        if (v.state == OwnershipState.MOVED) {
            throw new RuntimeException(
                    "Use-after-move in struct field assignment: " + var.getName()
            );
        }

        v.state = OwnershipState.MOVED;

        annotations.add(new OwnershipAnnotation(
                sfa,
                OwnerShipAction.MOVED,
                var.getName(),
                "struct_field"
        ));

        log("MOVE " + var.getName() + " into struct field");
    }

    private void handleListAdd(ListAddNode add) {

        if (!(add.getValuesNode() instanceof VariableNode var)) {
            return;
        }

        VarOwnerShip v = vars.get(var.getName());
        if (v == null) return;

        if (v.state == OwnershipState.MOVED) {
            throw new RuntimeException(
                    "Use-after-move in list add: " + var.getName()
            );
        }

        v.state = OwnershipState.MOVED;

        annotations.add(new OwnershipAnnotation(
                add,
                OwnerShipAction.MOVED,
                var.getName(),
                "list"
        ));

        log("MOVE " + var.getName() + " into list");
    }


    private void handleInlineUpdate(StructUpdateNode up) {

        if (!(up.getTargetStruct() instanceof VariableNode var)) {
            return;
        }

        VarOwnerShip v = vars.get(var.getName());
        if (v == null) return;

        if (v.state == OwnershipState.MOVED) {
            throw new RuntimeException(
                    "Inline update on moved value: " + var.getName()
            );
        }

        // não move, mas consome ownership exclusivo
        log("INLINE UPDATE consumes ownership of " + var.getName());
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

        log("MOVE via return: " + var.getName());
    }


    public void dumpFinalStates() {
        System.out.println("==== FINAL OWNERSHIP STATE ====");
        for (VarOwnerShip v : vars.values()) {
            System.out.println(v);
        }
    }

    private void log(String msg) {
        if (debug) {
            System.out.println("[OWNERSHIP] " + msg);
        }
    }
}
