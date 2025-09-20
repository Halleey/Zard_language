package low;

import home.MainAST;
import prints.PrintNode;
import variables.*;

public interface LLVMEmitVisitor {
    String visit(MainAST node);
    String visit (VariableNode node);
    String visit(VariableDeclarationNode node);
    String visit(LiteralNode node);
    String visit (PrintNode node);
    String visit (UnaryOpNode node);
    String visit(AssignmentNode node);
    String visit (BinaryOpNode node);
}

