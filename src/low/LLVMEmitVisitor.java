package low;

import home.MainAST;
import prints.PrintNode;
import variables.LiteralNode;
import variables.VariableDeclarationNode;
import variables.VariableNode;

public interface LLVMEmitVisitor {
    String visit(MainAST node);
    String visit (VariableNode node);
    String visit(VariableDeclarationNode node);
    String visit(LiteralNode node);
    String visit (PrintNode node);
}

