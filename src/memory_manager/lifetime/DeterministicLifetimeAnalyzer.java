package memory_manager.lifetime;

import ast.ASTNode;

import java.util.*;
import java.util.*;


import java.util.*;
import java.util.*;

import java.util.List;
import java.util.Map;
public class DeterministicLifetimeAnalyzer {

    private final Map<String, String> varTypes;
    private final Linearizer linearizer;
    private final UsageCollector usageCollector;

    public DeterministicLifetimeAnalyzer(Map<String, String> varTypes) {
        this.varTypes = varTypes;
        this.linearizer = new Linearizer();
        this.usageCollector = new UsageCollector(varTypes);
    }

    public Map<String, ASTNode> analyzeAndReturnNode(List<ASTNode> roots) {
        List<ASTNode> linearized = linearizer.collectLinearStatements(roots);
        usageCollector.collect(linearized);
        return usageCollector.getLastUseNode();
    }
}
