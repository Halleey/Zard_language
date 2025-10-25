package ast.maps;

import ast.ASTNode;
import ast.runtime.RuntimeContext;
import ast.expressions.TypedValue;
import low.module.LLVMEmitVisitor;

import java.util.Map;

public class MapRemoveNode extends ASTNode {
    private final ASTNode mapVar;
    private final ASTNode keyNode;

    public MapRemoveNode(ASTNode mapVar, ASTNode keyNode) {
        this.mapVar = mapVar;
        this.keyNode = keyNode;
    }

    @Override
    public String accept(LLVMEmitVisitor visitor) {
        return "";
    }

    @Override
    public TypedValue evaluate(RuntimeContext ctx) {
        DynamicMap map = (DynamicMap) mapVar.evaluate(ctx).value();
        TypedValue keyVal = keyNode.evaluate(ctx);

        ASTNode toRemove = null;
        TypedValue removedValue = null;

        for (Map.Entry<ASTNode, ASTNode> entry : map.getEntries().entrySet()) {
            TypedValue k = entry.getKey().evaluate(ctx);
            if (k.value().equals(keyVal.value())) {
                removedValue = entry.getValue().evaluate(ctx);
                toRemove = entry.getKey();
                break;
            }
        }

        if (toRemove != null) {
            map.getEntries().remove(toRemove);
            return removedValue;
        }

        throw new RuntimeException("Chave '" + keyVal.value() + "' não encontrada no mapa para remoção.");
    }

    @Override
    public void print(String prefix) {
        System.out.println(prefix + "MapRemoveNode:");
        mapVar.print(prefix + "  Map:");
        keyNode.print(prefix + "  Key:");
    }
}
