package ast.variables;

import ast.ASTNode;
import context.runtime.RuntimeContext;
import context.statics.StaticContext;
import ast.expressions.TypedValue;
import context.statics.Symbol;
import low.module.LLVMEmitVisitor;
public class VariableNode extends ASTNode {
    public final String name;
    private Symbol symbol; // ← símbolo resolvido

    public VariableNode(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public void bindChildren(StaticContext stx) {
        this.symbol = stx.resolveVariable(name);
    }

    @Override
    public String getType() {
        return symbol.getType();
    }

    public Symbol getSymbol() {
        return symbol;
    }

    @Override
    public TypedValue evaluate(RuntimeContext ctx) {
        return ctx.getVariable(name);
    }

    @Override
    public String accept(LLVMEmitVisitor visitor) {
        return visitor.visit(this);
    }

    @Override
    public void print(String prefix) {
        System.out.println(prefix + "Variable: " + name +
                (symbol != null ? " : " + symbol.getType() : ""));
    }
}
