package memory_manager;

import ast.structs.StructNode;
import low.TempManager;
import low.main.TypeInfos;
import low.module.LLVisitorMain;
import low.variables.VariableEmitter;

import java.util.Map;



public class FreeEmitter {

    private final Map<String, TypeInfos> varTypes;
    private final TempManager temps;
    private final VariableEmitter varEmitter;
    private final LLVisitorMain visitor;

    public FreeEmitter(
            Map<String, TypeInfos> varTypes,
            TempManager temps,
            VariableEmitter varEmitter,
            LLVisitorMain visitor
    ) {
        this.varTypes = varTypes;
        this.temps = temps;
        this.varEmitter = varEmitter;
        this.visitor = visitor;
    }
    public String emit(FreeNode node) {
      return "\n";
    }

}