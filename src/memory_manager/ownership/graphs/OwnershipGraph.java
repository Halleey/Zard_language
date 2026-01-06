package memory_manager.ownership.graphs;

import memory_manager.ownership.enums.Kind;

import java.util.LinkedHashMap;
import java.util.Map;

public class OwnershipGraph {

    private final Map<String, OwnershipNode> roots = new LinkedHashMap<>();

    public Map<String, OwnershipNode> getRoots() {
        return roots;
    }

    public void declareVar(String name) {
        roots.put(name, new OwnershipNode(name, Kind.VAR));
    }

    public boolean isRoot(String name) {
        return roots.containsKey(name);
    }

    public void move(String source, String targetPath) {

        OwnershipNode sourceNode = roots.remove(source);
        if (sourceNode == null) {
            return; // já movido ou literal
        }

        OwnershipNode targetParent = resolveOrCreatePath(targetPath);

        OwnershipNode moved =
                sourceNode.deepCloneWithRebase(source, targetPath);

        targetParent.addChild(moved);
    }

    public void deepCopy(String from, String to) {

        OwnershipNode source = roots.get(from);
        if (source == null) return;

        OwnershipNode clone =
                source.deepCloneWithRebase(from, to);

        roots.put(to, clone);
    }

    private OwnershipNode resolveOrCreatePath(String path) {

        String[] parts = path.split("\\.");

        OwnershipNode current = roots.computeIfAbsent(
                parts[0],
                n -> new OwnershipNode(n, Kind.VAR)
        );

        for (int i = 1; i < parts.length; i++) {

            String part = parts[i];
            OwnershipNode next = findChild(current, part);

            if (next == null) {
                next = new OwnershipNode(
                        current.getId() + "." + part,
                        Kind.FIELD
                );
                current.addChild(next);
            }

            current = next;
        }

        return current;
    }

    private OwnershipNode findChild(OwnershipNode parent, String fieldName) {
        for (OwnershipNode child : parent.getChildren()) {
            if (child.getId().endsWith("." + fieldName)) {
                return child;
            }
        }
        return null;
    }

    public void dump() {
        System.out.println("==== FINAL OWNERSHIP GRAPH ====");
        for (OwnershipNode root : roots.values()) {
            root.dump("");
            System.out.println();
        }
    }
}
