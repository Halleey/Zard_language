package interpreter;
import expressions.BinaryExpression;
import expressions.LiteralExpression;
import ifs.Block;
import ifs.ConditionBlock;
import ifs.IfParser;
import ifs.IfStatement;
import inputs.InputStatement;
import inputs.ParserInput;
import prints.ParserPrintStatement;
import prints.PrintStatement;
import tokens.Token;
import expressions.Expression;
import variables.*;
import whiles.WhileParser;
import whiles.WhileStatement;

import java.util.ArrayList;
import java.util.List;

public class Parser {
    public final List<Token> tokens;
    public int pos = 0;
    private final IfParser ifParser;
    private final WhileParser whileParser;
    private final ParserPrintStatement printStatement;
    private final ParserInput parserInput;
    private final ParseVariable parseVariable;


    public Parser(List<Token> tokens) {
        this.tokens = tokens;
        this.ifParser = new IfParser(this);
        this.whileParser = new WhileParser(this);
        this.printStatement = new ParserPrintStatement(this);
        this.parserInput  = new ParserInput(this);
        this.parseVariable = new ParseVariable(this);
    }

    public List<Statement> parse() {
        List<Statement> statements = new ArrayList<>();
        while (pos < tokens.size()) {
            if (tokens.get(pos).getValue().trim().isEmpty()) {
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
                return printStatement.parsePrintStatement();
            } else if ("input".equals(keyword)) {
                return parserInput.parseInputStatement();
            } else if ("if".equals(keyword)) {
                return ifParser.parseIfStatement();  // Novo método para o 'if'
            }
            else if ("while".equals(keyword)) {  // Adiciona suporte ao while
                return whileParser.parseWhileStatement();
            }
            return parseVariableDeclaration();
        }
        if (match(Token.TokenType.IDENTIFIER)) {
            return parseVariable.parseVariableAssignment();
        }
        throw new RuntimeException("Erro de sintaxe: declaração inválida em '" + tokens.get(pos).getValue() + "'");
    }



    public List<Statement> parseBlock() {
        List<Statement> statements = new ArrayList<>();

        System.out.println("Iniciando parsing do bloco...");

        while (!match(Token.TokenType.DELIMITER) || !tokens.get(pos).getValue().equals("}")) {
            statements.add(parseStatement());
        }

        System.out.println("Fim do bloco encontrado: " + tokens.get(pos).getValue());
        consume(Token.TokenType.DELIMITER); // Consome o "}"

        return statements;
    }




    private Statement parseVariableDeclaration() {
        Token typeToken = consume(Token.TokenType.KEYWORD);
        Token nameToken = consume(Token.TokenType.IDENTIFIER);

        if (match(Token.TokenType.DELIMITER) && tokens.get(pos).getValue().equals(";")) {
            consume(Token.TokenType.DELIMITER);
            return new VariableDeclaration(typeToken, nameToken.getValue(), null);
        }

        consume(Token.TokenType.OPERATOR);
        Expression value = parseExpression();
        consume(Token.TokenType.DELIMITER);

        return new VariableDeclaration(typeToken, nameToken.getValue(), value);
    }

    public MainBlock parseMainBlock() {
        List<Statement> statements = new ArrayList<>();
        if (!match(Token.TokenType.KEYWORD)) {
            throw new RuntimeException("Erro: O programa deve começar com 'main'!");
        }

        Token mainToken = consume(Token.TokenType.KEYWORD);
        if (!mainToken.getValue().equals("main")) {
            throw new RuntimeException("Erro: O programa deve começar com 'main'!");
        }

        consume(Token.TokenType.DELIMITER);
        while (!match(Token.TokenType.DELIMITER)) {
            statements.add(parseStatement());
        }
        consume(Token.TokenType.DELIMITER);

        return new MainBlock(statements);
    }



    public Expression parseExpression() {
        Expression left = parsePrimaryExpression();

        while (match(Token.TokenType.OPERATOR)) {
            Token operator = tokens.get(pos);
            String op = operator.getValue();

            // Verifica se é um operador de comparação
            if (op.equals("+") || op.equals("-") || op.equals("*") || op.equals("/") ||
                    op.equals("==") || op.equals("!=") || op.equals("<") || op.equals("<=") ||
                    op.equals(">") || op.equals(">=")) {

                consume(Token.TokenType.OPERATOR);  // Consome o operador
                Expression right = parsePrimaryExpression();  // A próxima expressão para a direita
                left = new BinaryExpression(left, operator, right);  // Cria a expressão binária
            } else {
                break;  // Sai do loop caso não seja um operador válido
            }
        }

        return left;
    }

    private Expression parsePrimaryExpression() {
        if (match(Token.TokenType.NUMBER)) {
            return new LiteralExpression(consume(Token.TokenType.NUMBER));
        }
        if (match(Token.TokenType.STRING)) {
            return new LiteralExpression(consume(Token.TokenType.STRING));
        }
        if (match(Token.TokenType.BOOLEAN)) {
            return new LiteralExpression(consume(Token.TokenType.BOOLEAN));
        }
        if (match(Token.TokenType.IDENTIFIER)) {
            return new VariableReference(consume(Token.TokenType.IDENTIFIER).getValue());
        }
        throw new RuntimeException("Erro de sintaxe: expressão inesperada em '" + tokens.get(pos).getValue() + "'");
    }

    public Token consume(Token.TokenType expectedType) {
        if (pos < tokens.size() && tokens.get(pos).getType() == expectedType) {
            return tokens.get(pos++);
        }
        throw new RuntimeException("Erro de sintaxe: esperado " + expectedType + ", encontrado '" +
                (pos < tokens.size() ? tokens.get(pos).getValue() : "EOF") + "'");
    }

    public boolean match(Token.TokenType type) {
        return pos < tokens.size() && tokens.get(pos).getType() == type;
    }
}