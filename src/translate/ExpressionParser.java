package translate;

import ast.ASTNode;
import ast.inputs.InputParser;
import ast.lists.DynamicList;
import ast.lists.ListNode;
import expressions.TypedValue;
import tokens.Token;
import variables.BinaryOpNode;
import variables.LiteralNode;

import java.util.ArrayList;
import java.util.List;

public class ExpressionParser {
    private final Parser parent;

    public ExpressionParser(Parser parent) {
        this.parent = parent;
    }

    public ASTNode parseExpression() {
        ASTNode left = parseTerm();
        while (parent.current().getValue().equals("+") || parent.current().getValue().equals("-")) {
            String op = parent.current().getValue();
            parent.advance();
            ASTNode right = parseTerm();
            left = new BinaryOpNode(left, op, right);
        }
        return parseComparison(left);
    }

    private ASTNode parseComparison(ASTNode left) {
        if (parent.current().getType() == Token.TokenType.OPERATOR &&
                List.of("<", ">", "<=", ">=", "==", "!=").contains(parent.current().getValue())) {
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

        // Lista vazia: ()
        if (tok.getValue().equals("(") && parent.peek().getValue().equals(")")) {
            parent.advance(); parent.advance();
            return new ListNode(new DynamicList(new ArrayList<>()));
        }

        switch (tok.getType()) {
            case NUMBER -> {
                parent.advance();
                String num = tok.getValue();
                if (num.contains(".")) return new LiteralNode(new TypedValue("double", Double.parseDouble(num)));
                else return new LiteralNode(new TypedValue("int", Integer.parseInt(num)));
            }
            case STRING -> {
                parent.advance();
                return new LiteralNode(new TypedValue("string", tok.getValue()));
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

                String type = parent.getVariableType(name);
                if ("list".equals(type) && parent.current().getValue().equals(".")) {
                    ListMethodParser listParser = new ListMethodParser(parent);
                    return listParser.parseExpressionListMethod(name);
                }

                IdentifierParser idParser = new IdentifierParser(parent);
                return idParser.parseAsExpression(name);
            }
        }

        // Expressão entre parênteses
        if (tok.getValue().equals("(")) {
            parent.advance();
            ASTNode expr = parseExpression();
            parent.eat(Token.TokenType.DELIMITER, ")");
            return expr;
        }

        throw new RuntimeException("Fator inesperado: " + tok.getValue());
    }

    // Parse de argumentos de função
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
