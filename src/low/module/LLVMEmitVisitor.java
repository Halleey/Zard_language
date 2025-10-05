package low.module;

import ast.exceptions.BreakNode;
import ast.exceptions.ReturnNode;
import ast.functions.FunctionCallNode;
import ast.functions.FunctionNode;
import ast.home.MainAST;
import ast.ifstatements.IfNode;
import ast.imports.ImportNode;
import ast.inputs.InputNode;
import ast.lists.*;
import ast.loops.WhileNode;
import ast.prints.PrintNode;
import ast.variables.*;

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
    String visit (ListNode node);
    String visit (ListAddNode node);
    String visit (ListRemoveNode node);
    String visit(ListClearNode node);
    String visit (ListSizeNode node);
    String visit (ListGetNode node);
    String visit (ListAddAllNode node);
    String visit (FunctionNode node);
    String visit (FunctionCallNode node);
    String visit (ReturnNode node);
}

