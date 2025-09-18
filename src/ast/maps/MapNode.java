package ast.maps;

import ast.ASTNode;
import ast.runtime.RuntimeContext;
import expressions.TypedValue;

public class MapNode extends ASTNode {
    private final DynamicMap dynamicMap;

    public MapNode(DynamicMap dynamicMap) {
        this.dynamicMap = dynamicMap;
    }

    public DynamicMap getMap() {
        return dynamicMap;
    }

    @Override
    public TypedValue evaluate(RuntimeContext ctx) {
        return new TypedValue("map", dynamicMap);
    }

    @Override
    public void print(String prefix) {
        System.out.println(prefix + "Map:");

        if (dynamicMap.size() == 0) {
            System.out.println(prefix + "  (vazio)");
            return;
        }

        ASTNode[] valueNodes = dynamicMap.valueNodes().toArray(new ASTNode[0]);
        int i = 0;

        for (ASTNode keyNode : dynamicMap.keyNodes()) {
            ASTNode valueNode = valueNodes[i];

            // Avalia cada nó em um RuntimeContext temporário só para mostrar
            TypedValue keyVal = keyNode.evaluate(new RuntimeContext());
            TypedValue valueVal = valueNode.evaluate(new RuntimeContext());

            System.out.println(prefix + "  [" + keyVal.getValue() + " (" + keyVal.getType() + ")]: "
                    + valueVal.getValue() + " (" + valueVal.getType() + ")");

            i++;
        }
    }
}
