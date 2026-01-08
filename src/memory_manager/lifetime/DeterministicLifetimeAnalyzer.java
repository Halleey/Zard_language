package memory_manager.lifetime;
import ast.ASTNode;
import context.statics.StaticContext;
import context.statics.Symbol;

import java.util.*;
import java.util.*;
import java.util.*;
import java.util.*;
import java.util.List;
import java.util.Map;

public class DeterministicLifetimeAnalyzer {

    private final Linearizer linearizer = new Linearizer();
    private final UsageCollector usageCollector;

    public DeterministicLifetimeAnalyzer(StaticContext rootCtx) {
        Map<String, Symbol> allSymbols = collectAllSymbols(rootCtx);
        this.usageCollector = new UsageCollector(allSymbols);
    }

    public Map<Symbol, ASTNode> analyze(List<ASTNode> roots) {
        List<ASTNode> linearized = linearizer.collectLinearStatements(roots);
        usageCollector.collect(linearized);
        return usageCollector.getLastUses();
    }

    private Map<String, Symbol> collectAllSymbols(StaticContext root) {
        Map<String, Symbol> map = new LinkedHashMap<>();
        collectRecursive(root, map);
        return map;
    }

    private void collectRecursive(StaticContext ctx, Map<String, Symbol> map) {
        for (Symbol sym : ctx.getDeclaredVariables()) {
            map.put(sym.getName(), sym);
        }
        for (StaticContext child : ctx.getChildren()) {
            collectRecursive(child, map);
        }
    }
}
