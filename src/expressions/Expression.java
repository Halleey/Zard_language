package expressions;

import ast.ASTNode;
import variables.VariableTable;

public abstract class Expression extends ASTNode {
    public abstract TypedValue evaluate(VariableTable table);
}
