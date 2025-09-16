package ast;

import ast.runtime.RuntimeContext;
import expressions.TypedValue;



public abstract class ASTNode {
    public abstract TypedValue evaluate(RuntimeContext ctx);
}
