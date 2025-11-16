package translate.front;

import ast.ASTNode;
import ast.TypeSpecializer;
import ast.home.MainAST;
import low.module.LLVisitorMain;

import java.util.List;

public class TypePipeline {

    private final Parser parser;

    public TypePipeline(Parser parser) {
        this.parser = parser;
    }

    public LLVisitorMain process(List<ASTNode> ast) {
        TypeSpecializer specializer = new TypeSpecializer();
        LLVisitorMain visitor = new LLVisitorMain(specializer);
        specializer.setVisitor(visitor);

        for (ASTNode node : ast) {
            if (node instanceof MainAST main) {
                visitor.registrarStructs(main);
            }
        }

        specializer.specialize(ast);
        return visitor;
    }
}
