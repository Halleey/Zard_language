package translate.front;

import ast.ASTNode;
import ast.home.MainAST;
import low.module.LLVisitorMain;
import memory_manager.ownership.escapes.EscapeInfo;

import java.util.List;
public class TypePipeline {

    private final Parser parser;

    public TypePipeline(Parser parser) {
        this.parser = parser;
    }

    public LLVisitorMain process(List<ASTNode> ast) {
        LLVisitorMain visitor = new LLVisitorMain(new EscapeInfo());
        for (ASTNode node : ast) {
            if (node instanceof MainAST main) {
                visitor.registrarStructs(main);
            }
        }


        return visitor;
    }
}
