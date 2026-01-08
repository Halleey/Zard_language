package memory_manager.lifetime;

import ast.ASTNode;
import ast.ifstatements.IfNode;
import ast.lists.ListAddNode;
import ast.lists.ListGetNode;
import ast.lists.ListRemoveNode;
import ast.lists.ListSizeNode;
import ast.loops.WhileNode;
import ast.prints.PrintNode;
import ast.structs.StructFieldAccessNode;
import ast.structs.StructMethodCallNode;
import ast.structs.StructUpdateNode;
import ast.variables.AssignmentNode;
import ast.variables.VariableDeclarationNode;
import ast.variables.VariableNode;
import context.statics.StaticContext;
import context.statics.Symbol;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


class UsageCollector {

    private final Map<String, Symbol> symbols;
    private final Map<Symbol, ASTNode> lastUse = new LinkedHashMap<>();

    public UsageCollector(Map<String, Symbol> symbols) {
        this.symbols = symbols;
    }

    public void collect(List<ASTNode> linearized) {
        for (int i = linearized.size() - 1; i >= 0; i--) {
            ASTNode stmt = linearized.get(i);
            collectUses(stmt, stmt.getStaticContext(), stmt);
        }
    }

    public Map<Symbol, ASTNode> getLastUses() {
        return lastUse;
    }
    private void collectUses(ASTNode node, StaticContext useCtx, ASTNode anchor) {
        if (node == null) return;

        if (node instanceof VariableNode v) {
            Symbol sym = resolveSymbol(v, useCtx);
            if (sym != null) registerUse(sym, useCtx, anchor);
            return;
        }

        if (node instanceof IfNode ifn) {
            collectUses(ifn.getCondition(), useCtx, anchor);
            for (ASTNode stmt : ifn.getThenBranch()) collectUses(stmt, useCtx, anchor);
            if (ifn.getElseBranch() != null)
                for (ASTNode stmt : ifn.getElseBranch()) collectUses(stmt, useCtx, anchor);
            return;
        }

        if (node instanceof WhileNode wn) {
            collectUses(wn.getCondition(), useCtx, anchor);
            for (ASTNode stmt : wn.getBody()) collectUses(stmt, useCtx, anchor);
            return;
        }

        if (node instanceof PrintNode print) {
            collectUses(print.expr, useCtx, anchor);
            return;
        }

        if (node instanceof VariableDeclarationNode decl && decl.getInitializer() != null) {
            collectUses(decl.getInitializer(), useCtx, anchor);
            return;
        }

        if (node instanceof AssignmentNode assign) {
            collectUses(assign.getValueNode(), useCtx, anchor);
            return;
        }

        if (node instanceof ListAddNode add) {
            // mark list itself as used
            if (add.getListNode() instanceof VariableNode vn) registerUse(resolveSymbol(vn, useCtx), useCtx, anchor);
            // mark values inside add
            collectUses(add.getValuesNode(), useCtx, anchor);
            return;
        }

        if (node instanceof ListGetNode get) {
            if (get.getListName() instanceof VariableNode vn) registerUse(resolveSymbol(vn, useCtx), useCtx, anchor);
            collectUses(get.getIndexNode(), useCtx, anchor);
            return;
        }

        if (node instanceof ListRemoveNode rem) {
            if (rem.getListNode() instanceof VariableNode vn) registerUse(resolveSymbol(vn, useCtx), useCtx, anchor);
            collectUses(rem.getIndexNode(), useCtx, anchor);
            return;
        }

        if (node instanceof ListSizeNode size) {
            if (size.getNome() instanceof VariableNode vn) registerUse(resolveSymbol(vn, useCtx), useCtx, anchor);
            return;
        }


        // structs
        if (node instanceof StructFieldAccessNode sfa) return;
        if (node instanceof StructUpdateNode su) return;
        if (node instanceof StructMethodCallNode smc) collectUses(smc, useCtx, anchor);

        // fallback: percorre filhos gerais
        if (node.getChildren() != null) {
            for (ASTNode child : node.getChildren()) {
                collectUses(child, useCtx, anchor);
            }
        }
    }


    private Symbol resolveSymbol(VariableNode v, StaticContext ctx) {
        try {
            return ctx.resolveVariable(v.getName());
        } catch (RuntimeException e) {
            return symbols.get(v.getName()); // fallback global
        }
    }
    private void registerUse(Symbol sym, StaticContext useCtx, ASTNode anchor) {
        if (sym == null) {
            System.out.println("[USAGE COLLECTOR] WARNING: tentado registrar uso de símbolo null");
            return;
        }

        if (lastUse.containsKey(sym)) return;

        StaticContext declCtx = sym.getDeclaredIn();
        StaticContext cur = useCtx;

        while (cur != null) {
            if (!declCtx.isAncestorOf(cur) || (cur.hasLifetimeBoundary() && cur != declCtx)) {
                lastUse.put(sym, anchor);
                return;
            }
            cur = cur.getParent();
        }

        lastUse.put(sym, anchor);
    }

}
