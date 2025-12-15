package memory_manager;

import ast.TypeSpecializer;
import low.module.LLVisitorMain;

public class TypePipelineResult {
    private final LLVisitorMain visitor;
    private final TypeSpecializer specializer;

    public TypePipelineResult(LLVisitorMain visitor, TypeSpecializer specializer) {
        this.visitor = visitor;
        this.specializer = specializer;
    }

    public LLVisitorMain getVisitor() { return visitor; }
    public TypeSpecializer getSpecializer() { return specializer; }
}

