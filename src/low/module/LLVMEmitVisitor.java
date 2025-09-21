package low.module;

import ast.exceptions.BreakNode;
import home.MainAST;
import ifstatements.IfNode;
import loops.WhileNode;
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
    String visit (IfNode node);
    String visit (WhileNode node);
    String visit (BreakNode node);
}

