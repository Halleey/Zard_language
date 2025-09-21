package ast.maps;

import ast.ASTNode;
import ast.runtime.RuntimeContext;
import ast.expressions.TypedValue;
import low.module.LLVMEmitVisitor;


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
    public String accept(LLVMEmitVisitor visitor) {
        return "";
    }

    @Override
    public TypedValue evaluate(RuntimeContext ctx) {
        TypedValue mapValue = mapNode.evaluate(ctx);
        if (!mapValue.getType().equals("map"))
            throw new RuntimeException("This value is not a map");

        DynamicMap dynamicMap = (DynamicMap) mapValue.getValue();
        TypedValue keyVal = keyNode.evaluate(ctx);
        TypedValue valueVal = valueNode.evaluate(ctx);
        dynamicMap.put(keyVal, valueVal);
        return new TypedValue(null, "null");
    }

    @Override
    public void print(String prefix) {
        System.out.println(prefix + "MapPutNode:");
        System.out.println(prefix + "  Map:");
        mapNode.print(prefix + "    ");
        System.out.println(prefix + "  Key:");
        keyNode.print(prefix + "    ");
        System.out.println(prefix + "  Value:");
        valueNode.print(prefix + "    ");
    }
}