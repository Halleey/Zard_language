package ast.maps;

import ast.ASTNode;
import ast.runtime.RuntimeContext;
import expressions.TypedValue;
public class MapGetNode extends ASTNode {
    private final ASTNode mapVar;
    private final ASTNode keyNode;

    public MapGetNode(ASTNode mapVar, ASTNode keyNode) {
        this.mapVar = mapVar;
        this.keyNode = keyNode;
    }

    @Override
    public TypedValue evaluate(RuntimeContext ctx) {
        DynamicMap map = (DynamicMap) mapVar.evaluate(ctx).getValue();
        TypedValue keyVal = keyNode.evaluate(ctx);
        return map.get(keyVal, ctx); // Avalia apenas na hora de buscar
    }

    @Override
    public void print(String prefix) {
        System.out.println(prefix + "MapGetNode:");
        mapVar.print(prefix + "  Map:");
        keyNode.print(prefix + "  Key:");
    }
}
