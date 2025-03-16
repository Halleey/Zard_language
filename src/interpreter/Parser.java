package interpreter;
import expressions.BinaryExpression;
import expressions.LiteralExpression;
import ifs.Block;
import ifs.ConditionBlock;
import ifs.IfStatement;
import inputs.InputStatement;
import prints.PrintStatement;
import tokens.Token;
import expressions.Expression;
import variables.Statement;
import variables.VariableAssignment;
import variables.VariableDeclaration;
import variables.VariableReference;

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
                return parsePrintStatement();
            } else if ("input".equals(keyword)) {
                return parseInputStatement();
            } else if ("if".equals(keyword)) {
                return parseIfStatement();  // Novo método para o 'if'
            }
            return parseVariableDeclaration();
        }
        if (match(Token.TokenType.IDENTIFIER)) {
            return parseVariableAssignment();
        }
        throw new RuntimeException("Erro de sintaxe: declaração inválida em '" + tokens.get(pos).getValue() + "'");
    }



    private Statement parseIfStatement() {
        consume(Token.TokenType.KEYWORD);  // Consome "if"
        consume(Token.TokenType.DELIMITER); // Consome "("
        Expression condition = parseExpression();  // Parse da condição do if
        consume(Token.TokenType.DELIMITER); // Consome ")"
        consume(Token.TokenType.DELIMITER); // Consome "{"

        // Parse do bloco do if
        List<Statement> ifStatements = parseBlock();
        Block ifBlock = new Block(ifStatements);

        List<ConditionBlock> conditionBlocks = new ArrayList<>();
        conditionBlocks.add(new ConditionBlock(condition, ifBlock));

        Block elseBlock = null;

        while (match(Token.TokenType.KEYWORD) && tokens.get(pos).getValue().equals("else")) {
            consume(Token.TokenType.KEYWORD); // Consome "else"

            // Verifica se é um else if
            if (match(Token.TokenType.KEYWORD) && tokens.get(pos).getValue().equals("if")) {
                consume(Token.TokenType.KEYWORD);  // Consome "if"
                consume(Token.TokenType.DELIMITER); // Consome "("
                Expression elseIfCondition = parseExpression();  // Condição do else if
                consume(Token.TokenType.DELIMITER); // Consome ")"
                consume(Token.TokenType.DELIMITER); // Consome "{"

                // Parse do bloco do else if
                List<Statement> elseIfStatements = parseBlock();
                Block elseIfBlock = new Block(elseIfStatements);

               // System.out.println("Else if detectado com condição: " + elseIfCondition);

                // Adiciona um novo ConditionBlock à lista
                conditionBlocks.add(new ConditionBlock(elseIfCondition, elseIfBlock));
            } else {
                consume(Token.TokenType.DELIMITER); // Consome "{"

                // Parse do bloco do else
                List<Statement> elseStatements = parseBlock();
                elseBlock = new Block(elseStatements);

               // System.out.println("Else detectado!");
                break; // Sai do loop, pois o else finaliza a estrutura
            }
        }

        return new IfStatement(conditionBlocks, elseBlock);
    }

    private List<Statement> parseBlock() {
        List<Statement> statements = new ArrayList<>();

        System.out.println("Iniciando parsing do bloco...");

        while (!match(Token.TokenType.DELIMITER) || !tokens.get(pos).getValue().equals("}")) {
            statements.add(parseStatement());
        }

        System.out.println("Fim do bloco encontrado: " + tokens.get(pos).getValue());
        consume(Token.TokenType.DELIMITER); // Consome o "}"

        return statements;
    }





    private Statement parseVariableAssignment() {
        Token nameToken = consume(Token.TokenType.IDENTIFIER);
        consume(Token.TokenType.OPERATOR);
        Expression value = parseExpression();
        consume(Token.TokenType.DELIMITER);
        return new VariableAssignment(nameToken.getValue(), value);
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

    private InputStatement parseInputStatement() {
        consume(Token.TokenType.KEYWORD);
        consume(Token.TokenType.DELIMITER);
        Token variableToken = consume(Token.TokenType.IDENTIFIER);
        consume(Token.TokenType.DELIMITER);
        consume(Token.TokenType.DELIMITER);

        return new InputStatement(variableToken.getValue());
    }

    private PrintStatement parsePrintStatement() {
        consume(Token.TokenType.KEYWORD);
        consume(Token.TokenType.DELIMITER);
        Expression expression = parseExpression();
        consume(Token.TokenType.DELIMITER);
        consume(Token.TokenType.DELIMITER);
        return new PrintStatement(expression);
    }

    private Expression parseExpression() {
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
