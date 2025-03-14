package interpreter;
import expressions.LiteralExpression;
import prints.PrintStatement;
import tokens.Token;
import expressions.Expression;
import translate.Statement;
import translate.VariableAssignment;
import translate.VariableReference;

import java.util.ArrayList;
import java.util.List;
public class Parser {
    private final List<Token> tokens;
    private int pos = 0;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    public List<Statement> parse() {
        List<Statement> statements = new ArrayList<>();
        while (pos < tokens.size()) {
            if (tokens.get(pos).getValue().trim().isEmpty()) { // Ignorar tokens vazios
                pos++;
                continue;
            }
            statements.add(parseStatement());
        }
        return statements;
    }

    private Statement parseStatement() {
        if (match(Token.TokenType.KEYWORD)) {
            String keyword = tokens.get(pos).getValue();

            if ("print".equals(keyword)) {
                return parsePrintStatement();
            }
            return parseVariableDeclaration();
        }
        throw new RuntimeException("Erro de sintaxe: declaração inválida em '" + tokens.get(pos).getValue() + "'");
    }

    private VariableAssignment parseVariableDeclaration() {
        Token typeToken = consume(Token.TokenType.KEYWORD); // int, string, double, etc.
        Token nameToken = consume(Token.TokenType.IDENTIFIER); // Nome da variável
        consume(Token.TokenType.OPERATOR); // Espera '='
        Expression value = parseExpression(); // Pega o valor
        consume(Token.TokenType.DELIMITER); // Espera ';'
        return new VariableAssignment(nameToken.getValue(), value);
    }

    private PrintStatement parsePrintStatement() {
        consume(Token.TokenType.KEYWORD); // Consome 'print'
        consume(Token.TokenType.DELIMITER); // Consome '('
        Expression expr = parseExpression(); // Captura a expressão dentro do print
        consume(Token.TokenType.DELIMITER); // Consome ')'
        consume(Token.TokenType.DELIMITER); // Consome ';'
        return new PrintStatement(expr);
    }

    private Expression parseExpression() {
        if (match(Token.TokenType.NUMBER)) {
            return new LiteralExpression(consume(Token.TokenType.NUMBER));
        }
        if (match(Token.TokenType.STRING)) {
            return new LiteralExpression(consume(Token.TokenType.STRING));
        }
        if (match(Token.TokenType.IDENTIFIER)) {
            return new VariableReference(consume(Token.TokenType.IDENTIFIER).getValue());
        }
        throw new RuntimeException("Erro de sintaxe: expressão inválida em '" + tokens.get(pos).getValue() + "'");
    }

    private Token consume(Token.TokenType expectedType) {
        if (pos < tokens.size() && tokens.get(pos).getType() == expectedType) {
            return tokens.get(pos++);
        }
        throw new RuntimeException("Erro de sintaxe: esperado " + expectedType + ", encontrado '" +
                (pos < tokens.size() ? tokens.get(pos).getValue() : "EOF") + "'");
    }

    private boolean match(Token.TokenType type) {
        return pos < tokens.size() && tokens.get(pos).getType() == type;
    }
}