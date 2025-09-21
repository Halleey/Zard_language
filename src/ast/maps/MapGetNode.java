package ast.maps;

import ast.ASTNode;
import ast.runtime.RuntimeContext;
import ast.expressions.TypedValue;
import low.module.LLVMEmitVisitor;


public class MapGetNode extends ASTNode {
    private final ASTNode mapVar;
    private final ASTNode keyNode;

    public MapGetNode(ASTNode mapVar, ASTNode keyNode) {
        this.mapVar = mapVar;
        this.keyNode = keyNode;
    }

    @Override
    public String accept(LLVMEmitVisitor visitor) {
        return "";
    }

    @Override
    public TypedValue evaluate(RuntimeContext ctx) {
        DynamicMap map = (DynamicMap) mapVar.evaluate(ctx).getValue();
        TypedValue keyVal = keyNode.evaluate(ctx);
        return map.get(keyVal);
    }

    @Override
    public void print(String prefix) {
        System.out.println(prefix + "MapGetNode:");
        mapVar.print(prefix + "  Map:");
        keyNode.print(prefix + "  Key:");
    }
}