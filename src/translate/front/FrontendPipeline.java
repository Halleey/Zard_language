package translate.front;

import ast.ASTNode;

import ast.prints.ASTPrinter;
import context.analyzers.FlowAnalyzer;
import context.analyzers.FlowPass;
import memory_manager.EscapeAnalyzer;
import memory_manager.EscapeInfo;
import memory_manager.free.StatementLinearizer;
import memory_manager.lifetime.DeterministicLifetimeAnalyzer;
import memory_manager.ownership.OwnershipAnalyzer;
import memory_manager.ownership.frees.FreeAction;
import memory_manager.ownership.frees.FreePlanner;
import memory_manager.ownership.graphs.OwnershipGraph;
import tokens.Lexer;
import tokens.Token;
import translate.StaticBinder;
import translate.identifiers.MethodDesugarer;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;


public class FrontendPipeline {

    private final String filePath;
    private Parser parser;
    private EscapeInfo escapeInfo;
    private Map<Integer, List<FreeAction>> freePlan;

    public FrontendPipeline(String filePath) {
        this.filePath = filePath;
    }

    public Map<Integer, List<FreeAction>> getFreePlan() {
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

        OwnershipAnalyzer ownershipAnalyzer = new OwnershipAnalyzer(true);

        ownershipAnalyzer.analyzeBlock(ast);
        ownershipAnalyzer.dumpFinalStates();

        OwnershipGraph ownershipGraph = ownershipAnalyzer.getGraph();
        StatementLinearizer linearizer = new StatementLinearizer();

        linearizer.assign(ast);
        Map<String, String> varTypes = parser.getAllVariableTypes();

        DeterministicLifetimeAnalyzer lifetimeAnalyzer = new DeterministicLifetimeAnalyzer(varTypes);

        Map<String, Integer> lastUse = lifetimeAnalyzer.analyze(ast);
        EscapeAnalyzer escapeAnalyzer = new EscapeAnalyzer();

        this.escapeInfo = escapeAnalyzer.analyze(ast);

        FreePlanner freePlanner = new FreePlanner(ownershipGraph, lastUse, escapeInfo);

        this.freePlan = freePlanner.plan();

        dumpFreePlan(freePlan);

        return ast;
    }

    public EscapeInfo getEscapeInfo() {
        return escapeInfo;
    }

    public Parser getParser() {
        return parser;
    }

    private void dumpFreePlan(Map<Integer, List<FreeAction>> plan) {
        System.out.println("==== FREE PLAN ====");
        for (var e : plan.entrySet()) {
            int stmt = e.getKey();
            System.out.println(
                    stmt == FreePlanner.END_OF_SCOPE
                            ? "END_OF_SCOPE"
                            : "stmtId " + stmt
            );
            for (var action : e.getValue()) {
                System.out.println("  - " + action);
            }
        }
        System.out.println("===================");
    }
}
