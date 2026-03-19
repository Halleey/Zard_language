package low.module;

import ast.exceptions.BreakNode;
import ast.exceptions.ReturnNode;
import ast.expressions.BinaryOpNode;
import ast.expressions.CompoundAssignmentNode;
import ast.expressions.UnaryOpNode;
import ast.functions.FunctionCallNode;
import ast.functions.FunctionNode;
import ast.home.MainAST;
import ast.ifstatements.IfNode;
import ast.imports.ImportNode;
import ast.inputs.InputNode;
import ast.loops.ForNode;
import ast.structs.*;
import ast.lists.*;
import ast.loops.WhileNode;

import ast.prints.PrintNode;
import ast.variables.*;
import low.module.builders.LLVMValue;
import memory_manager.free.FreeNode;

public interface LLVMEmitVisitor {
    LLVMValue visit(MainAST node);
    LLVMValue visit (VariableNode node);
    LLVMValue visit(VariableDeclarationNode node);
    LLVMValue visit(LiteralNode node);
    LLVMValue visit (PrintNode node);
    LLVMValue visit (UnaryOpNode node);
    LLVMValue visit(AssignmentNode node);
    LLVMValue visit (BinaryOpNode node);
    LLVMValue visit (IfNode node);
    LLVMValue visit (WhileNode node);
    LLVMValue visit (BreakNode node);
    LLVMValue visit (ListNode node);
    LLVMValue visit (ListAddNode node);
    LLVMValue visit (ListRemoveNode node);
    LLVMValue visit(ListClearNode node);
    LLVMValue visit (ListSizeNode node);
    LLVMValue visit (ListGetNode node);
    LLVMValue visit (ListAddAllNode node);
    LLVMValue visit (FunctionNode node);
    LLVMValue visit (FunctionCallNode node);
    LLVMValue visit (ReturnNode node);
    LLVMValue visit (ImportNode node);
    LLVMValue visit (StructNode node);
    LLVMValue visit (StructInstanceNode node);
    LLVMValue visit (StructFieldAccessNode node);
    LLVMValue visit (StructUpdateNode node);
    LLVMValue visit (StructMethodCallNode node);
    LLVMValue visit (ImplNode node);
    LLVMValue visit (ForNode node);
    LLVMValue visitFreeNode(FreeNode freeNode);
    LLVMValue visit(CompoundAssignmentNode compoundAssignmentNode);
    LLVMValue visit(InputNode inputNode);
}


