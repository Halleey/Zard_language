package memory_manager.ownership.graphs;

import context.statics.Symbol;
import memory_manager.ownership.enums.Kind;

import java.util.ArrayList;
import java.util.List;

public class OwnershipNode {

    private final Symbol symbol;
    private final Kind kind;
    private final List<OwnershipNode> children = new ArrayList<>();

    public OwnershipNode(Symbol symbol, Kind kind) {
        this.symbol = symbol;
        this.kind = kind;
    }

    public Symbol getSymbol() {
        return symbol;
    }

    public Kind getKind() {
        return kind;
    }

    public List<OwnershipNode> getChildren() {
        return children;
    }

    public void addChild(OwnershipNode child) {
        children.add(child);
    }

    public OwnershipNode deepCloneWithRebase(String oldBase, String newBase) {
        Symbol newSymbol = symbol;

        String name = symbol.getName();
        if (name.startsWith(oldBase)) {
            String rebased = newBase + name.substring(oldBase.length());
            newSymbol = symbol.rebased(rebased);
        }

        OwnershipNode clone = new OwnershipNode(newSymbol, kind);

        for (OwnershipNode child : children) {
            clone.addChild(child.deepCloneWithRebase(oldBase, newBase));
        }

        return clone;
    }

    public void dump(String indent) {
        System.out.println(indent + "- " + symbol.getName() + " [" + kind + "]");
        for (OwnershipNode child : children) {
            child.dump(indent + "  ");
        }
    }
}

