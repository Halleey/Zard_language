package memory_manager.ownership.graphs;

import memory_manager.ownership.enums.Kind;

import java.util.ArrayList;
import java.util.List;


public class OwnershipNode {

    private final String id;
    private final Kind kind;
    private final List<OwnershipNode> children = new ArrayList<>();

    public OwnershipNode(String id, Kind kind) {
        this.id = id;
        this.kind = kind;
    }

    public String getId() { return id; }
    public Kind getKind() { return kind; }
    public List<OwnershipNode> getChildren() { return children; }

    public void addChild(OwnershipNode child) {
        children.add(child);
    }

    public OwnershipNode deepCloneWithRebase(String oldBase, String newBase) {
        String newId = id.startsWith(oldBase)
                ? newBase + id.substring(oldBase.length())
                : id;

        OwnershipNode clone = new OwnershipNode(newId, kind);

        for (OwnershipNode child : children) {
            clone.addChild(child.deepCloneWithRebase(oldBase, newBase));
        }

        return clone;
    }

    public void dump(String indent) {
        System.out.println(indent + "- " + id + " [" + kind + "]");
        for (OwnershipNode child : children) {
            child.dump(indent + "  ");
        }
    }


}
