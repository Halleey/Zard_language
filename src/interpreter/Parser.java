package interpreter;
import expressions.LiteralExpression;
import prints.PrintStatement;
import tokens.Token;
import expressions.Expression;
import translate.Statement;
import translate.VariableAssignment;
import translate.VariableDeclaration;
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

        // Se não for uma declaração de variável, pode ser uma atribuição!
        if (match(Token.TokenType.IDENTIFIER)) {
            return parseVariableAssignment();
        }

        throw new RuntimeException("Erro de sintaxe: declaração inválida em '" + tokens.get(pos).getValue() + "'");
    }

    private Statement parseVariableAssignment() {
        Token nameToken = consume(Token.TokenType.IDENTIFIER); // Nome da variável
        System.out.println("Detectada atribuição para variável: " + nameToken.getValue());

        consume(Token.TokenType.OPERATOR); // Deve ser '='
        Expression value = parseExpression();
        consume(Token.TokenType.DELIMITER); // Deve ser ';'

        return new VariableAssignment(nameToken.getValue(), value);
    }



    private Statement parseVariableDeclaration() {
        Token typeToken = consume(Token.TokenType.KEYWORD); // Captura tipo (ex: double)
        Token nameToken = consume(Token.TokenType.IDENTIFIER); // Captura nome (ex: teste)

        System.out.println("Declarando variável: " + nameToken.getValue());

        // Se o próximo token for ';', significa que é apenas uma declaração sem valor
        if (match(Token.TokenType.DELIMITER) && tokens.get(pos).getValue().equals(";")) {
            System.out.println("Variável '" + nameToken.getValue() + "' declarada sem valor inicial.");
            consume(Token.TokenType.DELIMITER); // Consome o ';'
            return new VariableDeclaration(typeToken, nameToken.getValue(), null); // Retorna declaração sem valor inicial
        }

        // Espera '=' para atribuição
        System.out.println("Esperando '=' para atribuição da variável '" + nameToken.getValue() + "'...");
        consume(Token.TokenType.OPERATOR); // Deve ser '='
        Expression value = parseExpression();
        consume(Token.TokenType.DELIMITER); // Deve ser ';'

        System.out.println("Variável '" + nameToken.getValue() + "' declarada com valor inicial.");
        return new VariableDeclaration(typeToken, nameToken.getValue(), value);
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