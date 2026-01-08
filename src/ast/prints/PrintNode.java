package ast.prints;

import ast.ASTNode;
import context.statics.StaticContext;

import context.runtime.RuntimeContext;
import ast.lists.DynamicList;
import ast.expressions.TypedValue;
import low.module.LLVMEmitVisitor;

import java.util.List;



public class PrintNode extends ASTNode {
    public final ASTNode expr;
    public final boolean newline;

    public PrintNode(ASTNode expr, boolean newline) {
        this.expr = expr;
        this.newline = newline;
    }

    @Override
    public String accept(LLVMEmitVisitor visitor) {
        return visitor.visit(this);
    }

    @Override
    public TypedValue evaluate(RuntimeContext ctx) {
        TypedValue val = expr.evaluate(ctx);

        if (newline) {
            runtimePrintln(val, ctx);
        } else {
            runtimePrint(val, ctx);
        }

        return val;
    }

    private void runtimePrint(TypedValue val, RuntimeContext ctx) {
        if (val.type().equals("List")) {
            DynamicList list = (DynamicList) val.value();
            List<Object> values = list.getElements().stream()
                    .map(node -> node.evaluate(ctx).value())
                    .toList();
            System.out.print(values);
        } else {
            System.out.print(val.value());
        }
    }

    private void runtimePrintln(TypedValue val, RuntimeContext ctx) {
        if (val.type().equals("List")) {
            DynamicList list = (DynamicList) val.value();
            List<Object> values = list.getElements().stream()
                    .map(node -> node.evaluate(ctx).value())
                    .toList();
            System.out.println(values);
        } else {
            System.out.println(val.value());
        }
    }

    @Override
    public void print(String prefix) {
        System.out.println(prefix + (newline ? "PrintLn:" : "Print:"));
        expr.print(prefix + "  ");
    }


    @Override
    public boolean isStatement() {
        return true;
    }

    @Override
    public void bindChildren(StaticContext stx) {
        expr.bind(stx);
    }
}
