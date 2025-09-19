package translate;

import ast.ASTNode;
import ast.exceptions.ReturnValue;
import ast.runtime.RuntimeContext;
import prints.ASTPrinter;
import tokens.Lexer;
import tokens.Token;
import java.nio.file.Files;
import java.nio.file.Path;

import java.util.List;

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

            // Execute AST com RuntimeContext
            System.out.println("=== Execution ===");
//            RuntimeContext ctx = new RuntimeContext();
//
//            for (ASTNode node : ast) {
//                try {
//                    node.evaluate(ctx);
//                } catch (ReturnValue rv) {
//                    System.out.println("Programa interrompido pelo return: " + rv.value.getValue());
//                    break;
//                }
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
