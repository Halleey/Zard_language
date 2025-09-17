package translate;
import ast.ASTNode;
import ast.exceptions.BreakNode;
import ast.exceptions.ReturnNode;
import ast.functions.FunctionCallParser;
import ast.functions.FunctionParser;
import ast.inputs.InputParser;
import expressions.TypedValue;
import home.MainParser;
import ifstatements.IfParser;
import loops.WhileParser;
import prints.PrintParser;
import tokens.Token;
import variables.*;

import java.util.*;

public class Parser {
    private final List<Token> tokens;
    private int pos = 0;
    private final Map<String, String> variableTypes = new HashMap<>();
    private final Deque<Map<String, String>> variableStack = new ArrayDeque<>();

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
        variableStack.push(new HashMap<>()); // contexto global
    }

    public void pushContext() {
        variableStack.push(new HashMap<>());
    }

    public void popContext() {
        variableStack.pop();
    }

    public void declareVariable(String name, String type) {
        variableStack.peek().put(name, type);
    }

    public String getVariableType(String name) {
        // primeiro verifica a stack de escopo
        for (Map<String, String> ctx : variableStack) {
            if (ctx.containsKey(name)) return ctx.get(name);
        }

        // depois verifica o mapa global
        return variableTypes.get(name);
    }


    public Token current() {
        if (pos < tokens.size()) return tokens.get(pos);
        return new Token(Token.TokenType.EOF, "");
    }

    public void advance() {
        if (pos < tokens.size()) pos++;
    }

    public void eat(Token.TokenType type) {
        if (current().getType() == type) advance();
        else throw new RuntimeException("Esperado token do tipo " + type +
                " mas encontrado " + current().getType() + " valor: " + current().getValue());
    }

    public void eat(Token.TokenType type, String value) {
        if (current().getType() == type && current().getValue().equals(value)) advance();
        else throw new RuntimeException("Esperado token " + value + " do tipo " + type +
                " mas encontrado " + current().getValue() + " tipo " + current().getType());
    }

    // ------------------- MÉTODO CENTRAL -------------------
    public List<ASTNode> parse() {
        List<ASTNode> nodes = new ArrayList<>();
        while (current().getType() != Token.TokenType.EOF) {
            nodes.add(parseStatement());
        }
        return nodes;
    }

    // ------------------- STATEMENTS -------------------
    public ASTNode parseStatement() {
        Token tok = current();

        if (tok.getType() == Token.TokenType.KEYWORD) {
            String val = tok.getValue();
            switch (val) {
                case "int", "double", "string", "boolean", "list", "var"-> {
                    VarDeclarationParser varParser = new VarDeclarationParser(this);
                    return varParser.parseVarDeclaration();
                }
                case "print" -> {

                    PrintParser printParser = new PrintParser(this);
                    return printParser.parsePrint();
                }
                case "if" -> {
                    IfParser ifParser = new IfParser(this);
                    return ifParser.parseIf();
                }
                case "while" -> {
                    WhileParser whileParser = new WhileParser(this);
                    return whileParser.parse();
                }
                case "main" -> {
                    MainParser mainParser = new MainParser(this);
                    return mainParser.parseMain();
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
            advance(); // consome IDENTIFIER

            // Se o próximo token for '.', cria MethodCallNode genérico
            if (current().getValue().equals(".")) {
                advance(); // consome '.'
                String methodName = current().getValue();
                advance(); // consome o nome do método
                List<ASTNode> args = parseArguments(); // argumentos do método
                MethodCallNode node = new MethodCallNode(name, methodName, args);

                // consome o ';' final do statement
                eat(Token.TokenType.DELIMITER, ";");

                return node;
            }

            // Caso normal: variável/atribuição/unário
            IdentifierParser idParser = new IdentifierParser(this);
            return idParser.parseAsStatement(name);
        }

        throw new RuntimeException("Comando inesperado: " + tok.getValue());
    }

    // ------------------- EXPRESSIONS -------------------
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
                List.of("<", ">", "<=", ">=", "==", "!=").contains(current().getValue())) {
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
        switch (tok.getType()) {
            case NUMBER -> {
                advance();
                String num = tok.getValue();
                if (num.contains(".")) return new LiteralNode(new TypedValue("double", Double.parseDouble(num)));
                else return new LiteralNode(new TypedValue("int", Integer.parseInt(num)));
            }
            case STRING -> {
                advance();
                return new LiteralNode(new TypedValue("string", tok.getValue()));
            }
            case BOOLEAN -> {
                advance();
                return new LiteralNode(new TypedValue("boolean", Boolean.parseBoolean(tok.getValue())));
            }
            case KEYWORD -> {
                if (tok.getValue().equals("input")) {
                    InputParser inputParser = new InputParser(this);
                    return inputParser.parse();
                }
            }
            case IDENTIFIER -> {
                String name = tok.getValue();
                advance(); // consome IDENTIFIER
                // verifica se a variável existe e se é do tipo list
                String type = getVariableType(name);
                if ("list".equals(type) && current().getValue().equals(".")) {
                    ListMethodParser listParser = new ListMethodParser(this);
                    return listParser.parseExpressionListMethod(name);
                }

                // se não for lista ou não houver '.', trata como variável normal
                IdentifierParser idParser = new IdentifierParser(this);
                return idParser.parseAsExpression(name);
            }
        }

        if (tok.getValue().equals("(")) {
            advance();
            ASTNode expr = parseExpression();
            eat(Token.TokenType.DELIMITER, ")");
            return expr;
        }

        throw new RuntimeException("Fator inesperado: " + tok.getValue());
    }

        public Token peek() {
            if (pos + 1 < tokens.size()) {
                return tokens.get(pos + 1);
            }
            return new Token(Token.TokenType.EOF, "");
        }

    List<ASTNode> parseArguments() {
        eat(Token.TokenType.DELIMITER, "(");
        List<ASTNode> args = new ArrayList<>();
        if (!current().getValue().equals(")")) {
            do {
                args.add(parseExpression());
                if (current().getValue().equals(",")) advance();
                else break;
            } while (!current().getValue().equals(")"));
        }
        eat(Token.TokenType.DELIMITER, ")");
        return args;
    }

    // ------------------- BLOCKS -------------------
    public List<ASTNode> parseBlock() {
        List<ASTNode> nodes = new ArrayList<>();
        eat(Token.TokenType.DELIMITER, "{");
        while (!current().getValue().equals("}")) {
            nodes.add(parseStatement());
        }
        eat(Token.TokenType.DELIMITER, "}");
        return nodes;
    }


    public void declareVariableType(String name, String type) {
        variableTypes.put(name, type);
    }

}
