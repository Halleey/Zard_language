package low.prints;

import ast.ASTNode;
import low.module.LLVisitorMain;

public interface PrintHandler {
    boolean canHandle(ASTNode node, LLVisitorMain visitorMain);
    String emit(ASTNode node, LLVisitorMain visitorMain, boolean newline);
}
