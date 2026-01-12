package translate.front;

import ast.ASTNode;
import ast.home.MainAST;
import ast.prints.ASTPrinter;
import context.analyzers.FlowPass;
import context.statics.Symbol;
import memory_manager.free.FreeAction;
import memory_manager.free.FreeInsertionPass;
import memory_manager.free.FreePlanner;
import memory_manager.lifetime.DeterministicLifetimeAnalyzer;
import memory_manager.ownership.OwnershipAnalyzer;
import memory_manager.ownership.escapes.EscapeAnalyzer;
import memory_manager.ownership.escapes.EscapeInfo;
import memory_manager.ownership.graphs.OwnershipGraph;
import tokens.Lexer;
import tokens.Token;
import translate.StaticBinder;
import translate.identifiers.MethodDesugarer;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
public class FrontendPipeline {

    private final String filePath;
    private Parser parser;
    private EscapeInfo escapeInfo;
    private Map<ASTNode, List<FreeAction>>  freePlan;

    public FrontendPipeline(String filePath) {
        this.filePath = filePath;
    }

    public Map<ASTNode, List<FreeAction>> getFreePlan() {
        return freePlan;
    }

    public List<ASTNode> process() throws Exception {

        String code = Files.readString(Path.of(filePath));

        Lexer lexer = new Lexer(code);
        List<Token> tokens = lexer.tokenize();

        parser = new Parser(tokens);
        List<ASTNode> ast = parser.parse();

        ASTPrinter.printAST(ast);

        new MethodDesugarer().desugar(ast);
        new StaticBinder().bind(ast);
        new FlowPass().analyze(ast);

        OwnershipAnalyzer ownershipAnalyzer =
                new OwnershipAnalyzer(
                        StaticBinder.getRootContext(),
                        true
                );
        ownershipAnalyzer.analyzeBlock(ast);
        ownershipAnalyzer.dumpFinalStates();

        OwnershipGraph ownershipGraph = ownershipAnalyzer.getGraph();

        DeterministicLifetimeAnalyzer lifetimeAnalyzer =
                new DeterministicLifetimeAnalyzer(
                        StaticBinder.getRootContext()
                );

        Map<Symbol, ASTNode> lastUses =
                lifetimeAnalyzer.analyze(ast);

        EscapeAnalyzer escapeAnalyzer = new EscapeAnalyzer();
        this.escapeInfo = escapeAnalyzer.analyze(ast);

        FreePlanner freePlanner = new FreePlanner(
                ownershipGraph,
                lastUses,
                ownershipAnalyzer.getAnnotations()
        );

        this.freePlan = freePlanner.plan();

        ASTNode root = ast.get(0);
        if (root instanceof MainAST main) {
            FreeInsertionPass freeInsertion =
                    new FreeInsertionPass(freePlan);
            freeInsertion.insert(main.getBody());
        }

        ASTPrinter.printAST(ast);

        return ast;
    }

    public EscapeInfo getEscapeInfo() {
        return escapeInfo;
    }

    public Parser getParser() {
        return parser;
    }
}
