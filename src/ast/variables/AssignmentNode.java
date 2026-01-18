package ast.variables;

import ast.ASTNode;
import ast.functions.FunctionCallNode;
import ast.functions.FunctionNode;
import context.runtime.RuntimeContext;
import context.statics.StaticContext;
import ast.expressions.TypedValue;
import context.statics.Symbol;
import low.module.LLVMEmitVisitor;

public class AssignmentNode extends ASTNode {
    public final String name;
    public final ASTNode valueNode;

    public ASTNode getValueNode() {
        return valueNode;
    }

    public String getName() {
        return name;
    }

    public AssignmentNode(String name, ASTNode valueNode) {
        this.name = name;
        this.valueNode = valueNode;
    }

    @Override
    public String accept(LLVMEmitVisitor visitor) {
        return visitor.visit(this);
    }

    @Override
    public TypedValue evaluate(RuntimeContext ctx) {
        if (!ctx.hasVariable(name)) {
            throw new RuntimeException("Variável não declarada: " + name);
        }

        TypedValue value = valueNode.evaluate(ctx);

        String expectedType = ctx.getVariable(name).type();
        if (!expectedType.equals(value.type())) {
            throw new RuntimeException(
                    "Erro de tipo: esperado " + expectedType + " mas encontrado " + value.type()
            );
        }

        ctx.setVariable(name, value);
        return value;
    }


    @Override
    public void print(String prefix) {
        System.out.println(prefix + "Assign: " + name);
        valueNode.print(prefix + "  ");
    }

    @Override
    public boolean isStatement() {
        return true;
    }

    @Override
    public void bindChildren(StaticContext stx) {

        // variável deve existir
        Symbol sym = stx.resolveVariable(name);
        String expectedType = sym.getType();

        // resolve tipos do RHS
        valueNode.bind(stx);
        String actualType = valueNode.getType();

        // bloqueia void
        if ("void".equals(actualType)) {
            throw new RuntimeException(
                    "Semantic error: cannot assign void value to variable '" + name + "'"
            );
        }

        checkTypeCompatibility(expectedType, actualType);
    }

    protected void checkTypeCompatibility(String declared, String currently) {

        if (isStructType(declared) || isStructType(currently)) {
            return;
        }

        if (declared.equals(currently)) return;

        if (declared.equals("double") && currently.equals("int")) return;
        if (declared.equals("float")  && currently.equals("int")) return;
        if (declared.equals("double") && currently.equals("float")) return;

        throw new RuntimeException(
                "Semantic error: cannot assign value of type '" +
                        currently + "' to variable of type '" +
                        declared + "'"
        );
    }

    private boolean isStructType(String type) {
        return type != null && type.startsWith("Struct<");
    }




}
