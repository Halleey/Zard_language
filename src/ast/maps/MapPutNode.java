package ast.maps;

import ast.ASTNode;
import ast.runtime.RuntimeContext;
import expressions.TypedValue;
import variables.LiteralNode;


public class MapPutNode extends ASTNode {

    private final ASTNode mapNode;
    private final ASTNode keyNode;
    private final ASTNode valueNode;

    public MapPutNode(ASTNode mapNode, ASTNode keyNode, ASTNode valueNode) {
        this.mapNode = mapNode;
        this.keyNode = keyNode;
        this.valueNode = valueNode;
    }

    @Override
    public TypedValue evaluate(RuntimeContext ctx) {
        TypedValue mapValue = mapNode.evaluate(ctx);
        if(!mapValue.getType().equals("map")) throw new RuntimeException("this value is not map");

        DynamicMap dynamicMap =(DynamicMap) mapValue.getValue();
        TypedValue keyValue = keyNode.evaluate(ctx);
        TypedValue val = valueNode.evaluate(ctx);
        dynamicMap.put(new LiteralNode(keyValue), new LiteralNode(val));
        return new TypedValue(null, "null");
    }

    @Override
    public void print(String prefix) {
        System.out.println(prefix + "MapPut:");
        System.out.println(prefix + "  Map:");
        mapNode.print(prefix + "    ");
        System.out.println(prefix + "  Key:");
        keyNode.print(prefix + "    ");
        System.out.println(prefix + "  Value:");
        valueNode.print(prefix + "    ");
    }
}
