package translate;

import ast.ASTNode;

public abstract class Statement extends ASTNode {
    public abstract void execute(VariableTable table);
}
