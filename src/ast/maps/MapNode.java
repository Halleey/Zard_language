package ast.maps;

import ast.ASTNode;
import ast.runtime.RuntimeContext;
import expressions.TypedValue;
import low.module.LLVMEmitVisitor;

public class MapNode extends ASTNode {
    private final DynamicMap dynamicMap;

    public MapNode(DynamicMap dynamicMap) {
        this.dynamicMap = dynamicMap;
    }

    public DynamicMap getMap() {
        return dynamicMap;
    }

    @Override
    public String accept(LLVMEmitVisitor visitor) {
        return "";
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

        for (TypedValue keyVal : dynamicMap.keys()) {
            TypedValue valueVal = dynamicMap.get(keyVal);
            System.out.println(prefix + "  [" + keyVal.getValue() + " (" + keyVal.getType() + ")]: "
                    + valueVal.getValue() + " (" + valueVal.getType() + ")");
        }
    }
}
