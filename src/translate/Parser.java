package translate;
import ast.ASTNode;
import ast.exceptions.BreakNode;
import ast.exceptions.ReturnNode;
import ast.functions.FunctionCallParser;
import ast.functions.FunctionParser;
import ast.inputs.InputParser;
import ast.lists.*;
import ast.runtime.RuntimeContext;
import expressions.DynamicList;
import expressions.TypedValue;
import home.MainParser;
import ifstatements.IfParser;
import loops.WhileParser;
import prints.PrintNode;
import tokens.Token;
import variables.*;

import java.util.*;


public class Parser {
    private final List<Token> tokens;
    private int pos = 0;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    public Token current() {
        if (pos < tokens.size()) return tokens.get(pos);
        return new Token(Token.TokenType.EOF, "");
    }

    public void advance() {
        if (pos < tokens.size()) pos++;
    }

    private void eat(Token.TokenType type) {
        if (current().getType() == type) {
            advance();
        } else {
            throw new RuntimeException("Esperado token do tipo " + type +
                    " mas encontrado " + current().getType() + " valor: " + current().getValue());
        }
    }

    public void eat(Token.TokenType type, String value) {
        if (current().getType() == type && current().getValue().equals(value)) {
            advance();
        } else {
            throw new RuntimeException("Esperado token " + value + " do tipo " + type +
                    " mas encontrado " + current().getValue() + " tipo " + current().getType());
        }
    }

    public List<ASTNode> parse() {
        List<ASTNode> nodes = new ArrayList<>();
        while (current().getType() != Token.TokenType.EOF) {
            nodes.add(parseStatement());
        }
        return nodes;
    }

    public ASTNode parseStatement() {
        Token tok = current();

        if (tok.getType() == Token.TokenType.KEYWORD) {
            switch (tok.getValue()) {
                case "int", "double", "string", "boolean" -> {
                    return parseVarDeclaration();
                }
                case "print" -> {
                    advance();
                    eat(Token.TokenType.DELIMITER, "(");
                    ASTNode expr = parseExpression();
                    eat(Token.TokenType.DELIMITER, ")");
                    eat(Token.TokenType.DELIMITER, ";");
                    return new PrintNode(expr);
                }
                case "if" -> {
                    IfParser ifParser = new IfParser(this);
                    return ifParser.parseIf();
                }
                case "main" -> {

                    MainParser mainParser = new MainParser(this);
                    return mainParser.parseMain();
                }
                case "while" -> {
                    WhileParser whileParser = new WhileParser(this);
                    return whileParser.parse();
                }
                case "input" -> {
                    InputParser inputParser = new InputParser(this);
                    return inputParser.parse();
                }
                case "return" -> {
                    advance();
                    ASTNode expr = parseExpression();
                    eat(Token.TokenType.DELIMITER, ";");
                    return new ReturnNode(expr);
                }
                case "break" -> {
                    advance();
                    eat(Token.TokenType.DELIMITER, ";");
                    return new BreakNode();
                }
                case "list" -> {
                    ListParser listParser = new ListParser(this);
                    return listParser.parseListDeclaration();
                }
                case "function" -> {
                   FunctionParser functionParser = new FunctionParser(this);
                   return functionParser.parseFunction();
                }
                case "call" -> {
                    advance();
                    String name = current().getValue();
                    advance();
                    FunctionCallParser functionCallParser = new FunctionCallParser(this);
                    return functionCallParser.parseFunctionCall(name);
                }
            }
        }

        if (tok.getType() == Token.TokenType.IDENTIFIER) {
            String name = tok.getValue();
            advance();

            // Suporte para chamadas de métodos de lista
            if (current().getValue().equals(".")) {
                advance();
                String method = current().getValue();
                advance();
                eat(Token.TokenType.DELIMITER, "(");

                ASTNode arg = null;
                if (!current().getValue().equals(")")) {
                    arg = parseExpression();
                }

                eat(Token.TokenType.DELIMITER, ")");
                eat(Token.TokenType.DELIMITER, ";");

                ASTNode listVar = new VariableNode(name);

                switch (method) {
                    case "add" -> {
                        return new ListAddNode(listVar, arg);
                    }
                    case "clear" -> {
                        return new ListClearNode(listVar);
                    }
                    case "remove" ->{
                        return new ListRemoveNode(listVar, arg);
                    }
                }
            }

            // Atribuição normal
            if (current().getValue().equals("=")) {
                advance();
                ASTNode value = parseExpression();
                eat(Token.TokenType.DELIMITER, ";");
                return new AssignmentNode(name, value);
            }

            // Operadores unários
            else if (current().getValue().equals("++") || current().getValue().equals("--")) {
                String op = current().getValue();
                advance();
                eat(Token.TokenType.DELIMITER, ";");
                return new UnaryOpNode(name, op);
            }
        }

        throw new RuntimeException("Comando inesperado: " + tok.getValue());
    }

    private ASTNode parseVarDeclaration() {
        String type = current().getValue();
        advance();
        String name = current().getValue();
        advance();

        ASTNode initializer = null;

        if (type.equals("list")) {
            if (current().getValue().equals("=")) {
                advance();
                eat(Token.TokenType.DELIMITER, "(");
                List<TypedValue> elements = new ArrayList<>();

                // Avalia cada elemento usando um runtime temporário
                RuntimeContext tempCtx = new RuntimeContext();
                while (!current().getValue().equals(")")) {
                    ASTNode expr = parseExpression();
                    TypedValue val = expr.evaluate(tempCtx);
                    elements.add(val);

                    if (current().getValue().equals(",")) {
                        advance();
                    }
                }

                eat(Token.TokenType.DELIMITER, ")");
                initializer = new ListNode(new DynamicList(elements));
            } else {
                initializer = new ListNode(new DynamicList(new ArrayList<>()));
            }

            eat(Token.TokenType.DELIMITER, ";");
            return new VariableDeclarationNode(name, "list", initializer);
        } else {
            // Declarações normais (int, double, string, boolean)
            if (current().getValue().equals("=")) {
                advance();
                initializer = parseExpression();
            }
            eat(Token.TokenType.DELIMITER, ";");
            return new VariableDeclarationNode(name, type, initializer);
        }
    }


    public ASTNode parseExpression() {
        ASTNode left = parseTerm();
        while (current().getValue().equals("+") || current().getValue().equals("-")) {
            String op = current().getValue();
            advance();
            ASTNode right = parseTerm();
            left = new BinaryOpNode(left, op, right);
        }
        return parseComparison(left);
    }

    private ASTNode parseComparison(ASTNode left) {
        if (current().getType() == Token.TokenType.OPERATOR &&
                (current().getValue().equals("<") || current().getValue().equals(">") ||
                        current().getValue().equals("<=") || current().getValue().equals(">=") ||
                        current().getValue().equals("==") || current().getValue().equals("!="))) {
            String op = current().getValue();
            advance();
            ASTNode right = parseTerm();
            left = new BinaryOpNode(left, op, right);
        }
        return left;
    }


    private ASTNode parseTerm() {
        ASTNode left = parseFactor();
        while (current().getValue().equals("*") || current().getValue().equals("/")) {
            String op = current().getValue();
            advance();
            ASTNode right = parseFactor();
            left = new BinaryOpNode(left, op, right);
        }
        return left;
    }
    private ASTNode parseFactor() {
        Token tok = current();
        if (tok.getType() == Token.TokenType.NUMBER) {
            advance();
            String num = tok.getValue();
            if (num.contains(".")) {
                // Número com ponto decimal => double
                return new LiteralNode(new TypedValue("double", Double.parseDouble(num)));
            } else {
                // Número inteiro => int
                return new LiteralNode(new TypedValue("int", Integer.parseInt(num)));
            }
        }
        if (tok.getType() == Token.TokenType.STRING) {
            advance();
            return new LiteralNode(new TypedValue("string", tok.getValue()));
        }
        if (tok.getType() == Token.TokenType.BOOLEAN) {
            advance();
            return new LiteralNode(new TypedValue("boolean", Boolean.parseBoolean(tok.getValue())));
        }
        else if (tok.getType() == Token.TokenType.KEYWORD && tok.getValue().equals("input")) {
            // Agora input pode ser usado dentro de expressões
            InputParser inputParser = new InputParser(this);
            return inputParser.parse();
        }
        if (tok.getType() == Token.TokenType.IDENTIFIER) {
            advance();
            return new VariableNode(tok.getValue());
        }
        if (tok.getValue().equals("(")) {
            advance();
            ASTNode expr = parseExpression();
            eat(Token.TokenType.DELIMITER, ")");
            return expr;
        }
        throw new RuntimeException("Fator inesperado: " + tok.getValue());
    }

    public List<ASTNode> parseBlock() {
        List<ASTNode> nodes = new ArrayList<>();
        eat(Token.TokenType.DELIMITER, "{");
        while (!current().getValue().equals("}")) {
            nodes.add(parseStatement());
        }
        eat(Token.TokenType.DELIMITER, "}");
        return nodes;
    }
}