package ast.maps;

import ast.ASTNode;
import ast.runtime.RuntimeContext;
import ast.expressions.TypedValue;
import low.module.LLVMEmitVisitor;

import java.util.Map;
public class MapNode extends ASTNode {
    private final String name; // nome da variável
    private final DynamicMap dynamicMap;

    public MapNode(String name, DynamicMap dynamicMap) {
        this.name = name;
        this.dynamicMap = dynamicMap;
    }

    public String getName() {
        return name;
    }

    public DynamicMap getDynamicMap() {
        return dynamicMap;
    }

    @Override
    public String accept(LLVMEmitVisitor visitor) {
        return visitor.visit(this);
    }

    @Override
    public TypedValue evaluate(RuntimeContext ctx) {
        return new TypedValue("map<" + dynamicMap.getKeyType() + "," + dynamicMap.getValueType() + ">", dynamicMap);
    }

    @Override
    public void print(String prefix) {
        System.out.println(prefix + "Map<" + dynamicMap.getKeyType() + "," + dynamicMap.getValueType() + "> (" + name + "):");
        Map<TypedValue, TypedValue> evaluated = dynamicMap.evaluate(new RuntimeContext());
        if (evaluated.isEmpty()) {
            System.out.println(prefix + "  (vazio)");
            return;
        }
        for (Map.Entry<TypedValue, TypedValue> e : evaluated.entrySet()) {
            System.out.println(prefix + "  [" + e.getKey().getValue() + " (" + e.getKey().getType() + ")]: "
                    + e.getValue().getValue() + " (" + e.getValue().getType() + ")");
        }
    }
}
