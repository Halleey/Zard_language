package ast.variables;

import ast.ASTNode;
import ast.inputs.InputParser;
import ast.expressions.TypedValue;
import tokens.Token;
import translate.front.Parser;
import translate.identifiers.IdentifierParser;

import java.util.ArrayList;
import java.util.List;


public class ExpressionParser {
    private final Parser parent;

    public ExpressionParser(Parser parent) {
        this.parent = parent;
    }

    public ASTNode parseExpression() {
        return parseLogicalOr();
    }

    private ASTNode parseLogicalOr() {
        ASTNode left = parseLogicalAnd();

        while (parent.current().getType() == Token.TokenType.OPERATOR &&
                parent.current().getValue().equals("||")) {
            String op = parent.current().getValue();
            parent.advance();
            ASTNode right = parseLogicalAnd();
            left = new BinaryOpNode(left, op, right);
        }

        return left;
    }

    private ASTNode parseLogicalAnd() {
        ASTNode left = parseComparison();

        while (parent.current().getType() == Token.TokenType.OPERATOR &&
                parent.current().getValue().equals("&&")) {
            String op = parent.current().getValue();
            parent.advance();
            ASTNode right = parseComparison();
            left = new BinaryOpNode(left, op, right);
        }

        return left;
    }

    private ASTNode parseComparison() {
        ASTNode left = parseAddSub();

        while (parent.current().getType() == Token.TokenType.OPERATOR &&
                List.of("<", ">", "<=", ">=", "==", "!=").contains(parent.current().getValue())) {
            String op = parent.current().getValue();
            parent.advance();
            ASTNode right = parseAddSub();
            left = new BinaryOpNode(left, op, right);
        }

        return left;
    }

    private ASTNode parseAddSub() {
        ASTNode left = parseTerm();

        while (parent.current().getValue().equals("+") || parent.current().getValue().equals("-")) {
            String op = parent.current().getValue();
            parent.advance();
            ASTNode right = parseTerm();
            left = new BinaryOpNode(left, op, right);
        }

        return left;
    }

    private ASTNode parseTerm() {
        ASTNode left = parseFactor();

        while (parent.current().getValue().equals("*") || parent.current().getValue().equals("/")) {
            String op = parent.current().getValue();
            parent.advance();
            ASTNode right = parseFactor();
            left = new BinaryOpNode(left, op, right);
        }

        return left;
    }

    private ASTNode parseFactor() {
        Token tok = parent.current();

        if (tok.getType() == Token.TokenType.OPERATOR &&
                (tok.getValue().equals("+") || tok.getValue().equals("-") || tok.getValue().equals("!"))) {
            String op = tok.getValue();
            parent.advance();
            ASTNode factor = parseFactor();
            return new UnaryOpNode(op, factor);
        }

        if (tok.getValue().equals("(") && parent.peek().getValue().equals(")")) {
            throw new RuntimeException(
                    "Listas vazias devem ter tipo especificado, ex: List<int> x; nada de ()"
            );
        }

        switch (tok.getType()) {
            case NUMBER -> {
                parent.advance();
                String num = tok.getValue();
                return num.contains(".")
                        ? new LiteralNode(new TypedValue("double", Double.parseDouble(num)))
                        : new LiteralNode(new TypedValue("int", Integer.parseInt(num)));
            }
            case STRING -> {
                parent.advance();
                return new LiteralNode(new TypedValue("string", tok.getValue()));
            }
            case CHAR -> {
                parent.advance();
                return new LiteralNode(new TypedValue("char", tok.getValue()));
            }
            case BOOLEAN -> {
                parent.advance();
                return new LiteralNode(new TypedValue("boolean", Boolean.parseBoolean(tok.getValue())));
            }
            case KEYWORD -> {
                if (tok.getValue().equals("input")) {
                    InputParser inputParser = new InputParser(parent);
                    return inputParser.parse();
                }
            }
            case IDENTIFIER -> {
                String name = tok.getValue();
                parent.advance();
                IdentifierParser idParser = new IdentifierParser(parent);
                return idParser.parseAsExpression(name);
            }

        }

        if (tok.getValue().equals("(")) {
            parent.advance();
            ASTNode expr = parseExpression();
            parent.eat(Token.TokenType.DELIMITER, ")");
            return expr;
        }

        throw new RuntimeException("Fator inesperado: " + tok.getValue());
    }

    public List<ASTNode> parseArguments() {
        parent.eat(Token.TokenType.DELIMITER, "(");
        List<ASTNode> args = new ArrayList<>();

        if (!parent.current().getValue().equals(")")) {
            do {
                args.add(parseExpression());
                if (parent.current().getValue().equals(",")) parent.advance();
                else break;
            } while (!parent.current().getValue().equals(")"));
        }

        parent.eat(Token.TokenType.DELIMITER, ")");
        return args;
    }
}
