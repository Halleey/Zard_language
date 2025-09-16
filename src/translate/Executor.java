package translate;

import ast.ASTNode;
import expressions.TypedValue;
import home.MainAST;
import prints.ASTPrinter;
import tokens.Lexer;
import tokens.Token;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Executor {
    public static void main(String[] args) {
        try {
            // Caminho do arquivo teste.zd
            String filePath = "src/language/main.zd";

            // Lê todo o conteúdo do arquivo como String
            String code = Files.readString(Path.of(filePath));

            // Lexer
            Lexer lexer = new Lexer(code);
            List<Token> tokens = lexer.tokenize();

            // Parser
            Parser parser = new Parser(tokens);
            List<ASTNode> ast = parser.parse();

            // Print AST
            System.out.println("=== AST ===");
            ASTPrinter.printAST(ast);

            // Execute AST
            System.out.println("=== Execution ===");
            Map<String, TypedValue> env = new HashMap<>();

            for (ASTNode node : ast) {
                if (node instanceof MainAST mainNode) {
                    mainNode.evaluate(env); // executa o bloco main
                } else {
                    node.evaluate(env); // qualquer outro nó
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
