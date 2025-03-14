package expressions;

import ast.ASTNode;
import translate.VariableTable;

public abstract class Expression extends ASTNode {
    public abstract TypedValue evaluate(VariableTable table);
}
