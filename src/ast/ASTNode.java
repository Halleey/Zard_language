package ast;

import expressions.TypedValue;

import java.util.Map;

public abstract class ASTNode {
    public abstract TypedValue evaluate(Map<String, TypedValue> variables);
}