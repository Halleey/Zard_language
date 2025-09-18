package ast.maps;

import ast.ASTNode;
import ast.runtime.RuntimeContext;
import expressions.TypedValue;

public class MapRemoveNode extends ASTNode {
    private final ASTNode mapVar;
    private final ASTNode keyNode;

    public MapRemoveNode(ASTNode mapVar, ASTNode keyNode) {
        this.mapVar = mapVar;
        this.keyNode = keyNode;
    }

    @Override
    public TypedValue evaluate(RuntimeContext ctx) {
        DynamicMap map = (DynamicMap) mapVar.evaluate(ctx).getValue();
        TypedValue keyVal = keyNode.evaluate(ctx);
        return map.remove(keyVal, ctx);
    }

    @Override
    public void print(String prefix) {
        System.out.println(prefix + "MapRemoveNode:");
        mapVar.print(prefix + "  Map:");
        keyNode.print(prefix + "  Key:");
    }
}