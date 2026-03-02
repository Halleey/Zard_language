package memory_manager.ownership.graphs;

import context.statics.StaticContext;
import context.statics.Symbol;
import memory_manager.ownership.enums.Kind;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
public class OwnershipGraph {

    private final Map<Symbol, OwnershipNode> roots = new LinkedHashMap<>();
    private final StaticContext rootContext;

    // contador lógico de elementos por lista
    private final Map<Symbol, Integer> listCounters = new HashMap<>();

    public OwnershipGraph(StaticContext rootContext) {
        this.rootContext = rootContext;
    }

    public Map<Symbol, OwnershipNode> getRoots() {
        return roots;
    }

    public void declareVar(Symbol symbol) {
        roots.put(symbol, new OwnershipNode(symbol, Kind.VAR));
    }

    public boolean isRoot(Symbol symbol) {
        return roots.containsKey(symbol);
    }

    /*
     * MOVE NORMAL (atribuição)
     */
    public void move(Symbol source, Symbol target) {

        OwnershipNode sourceNode = roots.remove(source);
        if (sourceNode == null) return;

        OwnershipNode targetParent =
                resolveOrCreatePath(target.getName());

        OwnershipNode moved =
                sourceNode.deepCloneWithRebase(
                        source.getName(),
                        target.getName()
                );

        targetParent.addChild(moved);
    }

    //MOVE PARA DENTRO DE LISTA


    public void moveIntoList(Symbol element, Symbol list) {

        OwnershipNode sourceNode = roots.remove(element);
        if (sourceNode == null) return;

        OwnershipNode listNode = roots.get(list);
        if (listNode == null) return;

        int index = listCounters.getOrDefault(list, 0);
        listCounters.put(list, index + 1);

        // símbolo lógico do elemento
        String elemName = list.getName() + "[" + index + "]";
        Symbol elemSymbol = element.rebased(elemName);

        OwnershipNode listElemNode = new OwnershipNode(elemSymbol, Kind.LIST_ELEM);

        OwnershipNode moved = sourceNode.deepCloneWithRebase(element.getName(), elemName);

        listElemNode.addChild(moved);
        listNode.addChild(listElemNode);
    }


    public void deepCopy(Symbol from, Symbol to) {

        OwnershipNode source = roots.get(from);
        if (source == null) return;

        OwnershipNode clone =
                source.deepCloneWithRebase(
                        from.getName(),
                        to.getName()
                );

        roots.put(to, clone);
    }


     // RESOLVER PATH (struct.field)

    private OwnershipNode resolveOrCreatePath(String path) {

        String[] parts = path.split("\\.");

        OwnershipNode current = roots.computeIfAbsent(
                rootContext.resolveVariable(parts[0]),
                sym -> new OwnershipNode(sym, Kind.VAR)
        );

        for (int i = 1; i < parts.length; i++) {

            String part = parts[i];
            OwnershipNode next = findChild(current, part);

            if (next == null) {
                Symbol fieldSymbol = current.getSymbol()
                        .rebased(current.getSymbol().getName() + "." + part);

                next = new OwnershipNode(fieldSymbol, Kind.FIELD);
                current.addChild(next);
            }

            current = next;
        }

        return current;
    }

    private OwnershipNode findChild(OwnershipNode parent, String fieldName) {
        for (OwnershipNode child : parent.getChildren()) {
            if (child.getSymbol().getName().endsWith("." + fieldName)) {
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