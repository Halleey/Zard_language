package ast.ifstatements;

import ast.ASTNode;
import tokens.Token;
import translate.front.Parser;

import java.util.List;

public class IfParser {
    private final Parser parser;


    public IfParser(Parser parser) {
        this.parser = parser;
    }

    public ASTNode parseIf() {
        // Consome 'if'
        if (!parser.current().getType().equals(Token.TokenType.KEYWORD) || !parser.current().getValue().equals("if")) {
            throw new RuntimeException("Esperado 'if', mas encontrado " + parser.current().getValue());
        }
        parser.advance();

        // Verifica '('
        parser.eat(Token.TokenType.DELIMITER, "(");

        // Lê a condição
        ASTNode condition = parser.parseExpression();

        // Verifica ')'
        parser.eat(Token.TokenType.DELIMITER, ")");

        // Bloco then
        List<ASTNode> thenBranch =parser.parseBlock();

        // Bloco else
        List<ASTNode> elseBranch = null;
        if (parser.current().getType() == Token.TokenType.KEYWORD && parser.current().getValue().equals("else")) {
            parser.advance();

            if (parser.current().getType() == Token.TokenType.KEYWORD && parser.current().getValue().equals("if")) {
                // else if recursão
                elseBranch = List.of(parseIf());
            } else {
                // else normal
                elseBranch = parser.parseBlock();
            }
        }

        return new IfNode(condition, thenBranch, elseBranch);
    }


}


