package low.prints;

import ast.ASTNode;
import low.module.LLVisitorMain;
import low.module.builders.LLVMValue;

public interface PrintHandler {
    boolean canHandle(ASTNode node, LLVisitorMain visitor);
    LLVMValue emit(ASTNode node, LLVisitorMain visitor, boolean newline);
}