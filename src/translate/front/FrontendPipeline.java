package translate.front;

import ast.ASTNode;

import tokens.Lexer;
import tokens.Token;
import translate.identifiers.MethodDesugarer;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;


public class FrontendPipeline {

    private final String filePath;
    private Parser parser;

    public FrontendPipeline(String filePath) {
        this.filePath = filePath;
    }

    public Parser getParser() {
        return parser;
    }

    public List<ASTNode> process() throws Exception {
        String code = Files.readString(Path.of(filePath));

        Lexer lexer = new Lexer(code);
        List<Token> tokens = lexer.tokenize();

        parser = new Parser(tokens);
        List<ASTNode> ast = parser.parse();

        MethodDesugarer desugarer = new MethodDesugarer();
        desugarer.desugar(ast);

        return ast;
    }
}