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
        Set<Symbol> allSymbols = collectAllSymbols(rootCtx);
        this.usageCollector = new UsageCollector(allSymbols);
    }

    public Map<Symbol, ASTNode> analyze(List<ASTNode> roots) {
        List<ASTNode> linearized = linearizer.collectLinearStatements(roots);
        usageCollector.collect(linearized);
        return usageCollector.getLastUses();
    }

    private Set<Symbol> collectAllSymbols(StaticContext root) {
        Set<Symbol> set = new LinkedHashSet<>();
        collectRecursive(root, set);
        return set;
    }

    private void collectRecursive(StaticContext ctx, Set<Symbol> set) {
        set.addAll(ctx.getDeclaredVariables());
        for (StaticContext child : ctx.getChildren()) {
            collectRecursive(child, set);
        }
    }

}
