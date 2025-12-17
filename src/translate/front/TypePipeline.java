package translate.front;

import ast.ASTNode;
import ast.TypeSpecializer;
import ast.home.MainAST;
import ast.structs.ImplNode;
import ast.structs.StructNode;
import low.module.LLVisitorMain;
import memory_manager.TypePipelineResult;

import java.util.List;

public class TypePipeline {

    private final Parser parser;

    public TypePipeline(Parser parser) {
        this.parser = parser;
    }

    public TypePipelineResult process(List<ASTNode> ast) {
        TypeSpecializer specializer = new TypeSpecializer();
        LLVisitorMain visitor = new LLVisitorMain(specializer);
        specializer.setVisitor(visitor);

        for (ASTNode node : ast) {
            if (node instanceof MainAST main) {
                visitor.registrarStructs(main);
            }
        }

        specializer.specialize(ast);

        return new TypePipelineResult(visitor, specializer);
    }
}
