package translate.front;

import ast.ASTNode;

import ast.home.MainAST;
import ast.prints.ASTPrinter;
import context.analyzers.FlowPass;
import memory_manager.ownership.escapes.EscapeAnalyzer;
import memory_manager.ownership.escapes.EscapeInfo;

import memory_manager.lifetime.DeterministicLifetimeAnalyzer;
import memory_manager.ownership.OwnershipAnalyzer;
import memory_manager.free.FreeAction;
import memory_manager.free.FreeInsertionPass;
import memory_manager.free.FreePlanner;
import memory_manager.ownership.graphs.OwnershipGraph;
import tokens.Lexer;
import tokens.Token;
import translate.StaticBinder;
import translate.identifiers.MethodDesugarer;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class FrontendPipeline {

    private final String filePath;
    private Parser parser;
    private EscapeInfo escapeInfo;
    private Map<ASTNode, List<FreeAction>> freePlan;

    public FrontendPipeline(String filePath) {
        this.filePath = filePath;
    }

    /**
     * Retorna o plano de frees gerado pelo pipeline.
     */
    public Map<ASTNode, List<FreeAction>> getFreePlan() {
        return freePlan;
    }

    /**
     * Executa o pipeline completo: parsing, binding, flow analysis,
     * ownership analysis, lifetime/escape analysis e inserção de frees.
     */
    public List<ASTNode> process() throws Exception {

        // ===============================
        // 1. Leitura do código e parsing
        // ===============================
        String code = Files.readString(Path.of(filePath));

        Lexer lexer = new Lexer(code);
        List<Token> tokens = lexer.tokenize();

        parser = new Parser(tokens);
        List<ASTNode> ast = parser.parse();

        System.out.println("=== AST ORIGINAL ===");
        ASTPrinter.printAST(ast);

        // ===============================
        // 2. Desugar + static binding + flow analysis
        // ===============================
        new MethodDesugarer().desugar(ast);
        new StaticBinder().bind(ast);
        new FlowPass().analyze(ast);

        // ===============================
        // 3. Ownership Analysis
        // ===============================
        OwnershipAnalyzer ownershipAnalyzer = new OwnershipAnalyzer(true);
        ownershipAnalyzer.analyzeBlock(ast);
        ownershipAnalyzer.dumpFinalStates();

        OwnershipGraph ownershipGraph = ownershipAnalyzer.getGraph();

        // ===============================
        // 4. Lifetime & Last Use Analysis
        // ===============================
        Map<String, String> varTypes = parser.getAllVariableTypes();
        DeterministicLifetimeAnalyzer lifetimeAnalyzer =
                new DeterministicLifetimeAnalyzer(varTypes);

        Map<String, ASTNode> lastUseNode =
                lifetimeAnalyzer.analyzeAndReturnNode(ast);

        // ===============================
        // 5. Escape Analysis
        // ===============================
        EscapeAnalyzer escapeAnalyzer = new EscapeAnalyzer();
        this.escapeInfo = escapeAnalyzer.analyze(ast);

        // ===============================
        // 6. Free Planning baseado em Ownership
        // ===============================
        FreePlanner freePlanner = new FreePlanner(
                ownershipGraph,
                lastUseNode,
                escapeInfo,
                ownershipAnalyzer.getAnnotations() // garante que só libera MOVED
        );

        this.freePlan = freePlanner.plan();

        dumpFreePlan(freePlan);

        // ===============================
        // 7. Inserção de frees na AST
        // ===============================
        ASTNode root = ast.get(0);
        if (root instanceof MainAST main) {
            FreeInsertionPass freeInsertion =
                    new FreeInsertionPass(freePlan);
            freeInsertion.insert(main.getBody());
        }

        System.out.println("=== AST APÓS FREE INSERTION ===");
        ASTPrinter.printAST(ast);
        System.out.println("==============================");

        return ast;
    }

    public EscapeInfo getEscapeInfo() {
        return escapeInfo;
    }

    public Parser getParser() {
        return parser;
    }

    /**
     * Dump simples do Free Plan para debug.
     */
    private void dumpFreePlan(Map<ASTNode, List<FreeAction>> plan) {
        System.out.println("==== FREE PLAN ====");
        for (var e : plan.entrySet()) {
            ASTNode anchor = e.getKey();
            System.out.println("after " + anchor.getClass().getSimpleName());
            for (var action : e.getValue()) {
                System.out.println("  - " + action);
            }
        }
        System.out.println("===================");
    }
}
