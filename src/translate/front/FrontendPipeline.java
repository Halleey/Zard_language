package translate.front;

import ast.ASTNode;

import ast.prints.ASTPrinter;
import memory_manager.EscapeAnalyzer;
import memory_manager.EscapeInfo;
import tokens.Lexer;
import tokens.Token;
import translate.identifiers.MethodDesugarer;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;


public class FrontendPipeline {

    private final String filePath;
    private Parser parser;
    private EscapeInfo escapeInfo;

    public FrontendPipeline(String filePath) {
        this.filePath = filePath;
    }

    public Parser getParser() {
        return parser;
    }

    public EscapeInfo getEscapeInfo() {
        return escapeInfo;
    }

    public List<ASTNode> process() throws Exception {
        String code = Files.readString(Path.of(filePath));

        Lexer lexer = new Lexer(code);
        List<Token> tokens = lexer.tokenize();

        parser = new Parser(tokens);
        List<ASTNode> ast = parser.parse();

        ASTPrinter.printAST(ast);

        MethodDesugarer desugarer = new MethodDesugarer();
        desugarer.desugar(ast);

        EscapeAnalyzer escapeAnalyzer = new EscapeAnalyzer();
        this.escapeInfo = escapeAnalyzer.analyze(ast);

        System.out.println("=== Escape Analysis Results ===");
        for (var e : escapeInfo.getMap().entrySet()) {
            System.out.println("  " + e.getKey() + " -> escapes? " + e.getValue());
        }
        System.out.println("================================");

        return ast;
    }
}
