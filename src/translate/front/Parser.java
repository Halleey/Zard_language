package translate.front;

import ast.ASTNode;
import ast.structs.StructFieldAccessNode;
import ast.variables.ExpressionParser;
import ast.variables.VariableNode;
import tokens.Token;
import translate.front.specializers.StatemantParser;

import java.util.*;

public class Parser {

    private final List<Token> tokens;
    private int pos = 0;

    private final Map<String, String> variableTypes = new HashMap<>();
    private final Deque<Map<String, String>> variableStack = new ArrayDeque<>();
    private final Map<String, Map<String, String>> structDefinitions = new HashMap<>();

    private final StatemantParser statementParser;
    private final ExpressionParser expressionParser;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;

        variableStack.push(new HashMap<>()); // contexto global

        this.statementParser = new StatemantParser(this);
        this.expressionParser = new ExpressionParser(this);
    }

    public List<ASTNode> parse() {
        List<ASTNode> nodes = new ArrayList<>();

        System.out.println("=== PARSE INICIADO ===");
        System.out.println("Total tokens: " + tokens.size());
        System.out.println();

        while (current().getType() != Token.TokenType.EOF) {

            System.out.println("[parse] pos=" + pos +
                    " token=" + current().getValue() +
                    " type=" + current().getType());

            ASTNode stmt = parseStatement();

            System.out.println("[parse] ✓ Statement parseado: " + stmt.getClass().getSimpleName());
            System.out.println();

            nodes.add(stmt);
        }

        System.out.println("=== PARSE FINALIZADO ===");
        return nodes;
    }

    public ASTNode parseStatement() {
        return statementParser.parse();
    }

    public ASTNode parseExpression() {
        return expressionParser.parseExpression();
    }

    public List<ASTNode> parseArguments() {
        return expressionParser.parseArguments();
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

    public void pushContext() {
        variableStack.push(new HashMap<>());
    }

    public void popContext() {
        variableStack.pop();
    }

    public void declareVariable(String name, String type) {
        variableStack.peek().put(name, type);
    }

    public void declareVariableType(String name, String type) {
        variableTypes.put(name, type);
    }

    public String getVariableType(String name) {
        for (Map<String, String> ctx : variableStack) {
            if (ctx.containsKey(name)) return ctx.get(name);
        }
        return variableTypes.get(name);
    }

    public void declareStruct(String name, Map<String, String> fields) {
        structDefinitions.put(name, fields);
    }

    // manteve exatamente como pediu
    public boolean isKnownStruct(String name) {
        return structDefinitions.containsKey(name);
    }

    public Map<String, String> lookupStruct(String name) {
        return structDefinitions.getOrDefault(name, Collections.emptyMap());
    }

    public String getStructFieldType(String structName, String field) {
        Map<String, String> fields = structDefinitions.get(structName);
        return fields != null ? fields.get(field) : null;
    }


    public String getExpressionType(ASTNode node) {

        if (node instanceof VariableNode v) {
            return getVariableType(v.getName());
        }

        if (node instanceof StructFieldAccessNode f) {

            String structType = getExpressionType(f.getStructInstance());

            if (structType != null && structType.startsWith("Struct<")) {

                String structName =
                        structType.substring(7, structType.length() - 1);

                return getStructFieldType(structName, f.getFieldName());
            }
        }

        return null;
    }

    public Token current() {
        if (pos < tokens.size()) return tokens.get(pos);
        return new Token(Token.TokenType.EOF, "");
    }

    public void advance() {
        if (pos < tokens.size()) pos++;
    }

    public void eat(Token.TokenType type) {
        if (current().getType() == type) {
            advance();
        } else {
            throw new RuntimeException(
                    "Esperado token " + type + " mas encontrei: " +
                            current().getType()
            );
        }
    }

    public void eat(Token.TokenType type, String value) {
        if (current().getType() == type &&
                current().getValue().equals(value)) {

            advance();
        } else {
            throw new RuntimeException(
                    "Esperado '" + value + "' mas encontrei '" + current().getValue() + "'"
            );
        }
    }
    public Token peek() {
        return peek(1);
    }

    public Token peek(int k) {
        int idx = pos + k;
        if (idx < tokens.size()) return tokens.get(idx);
        return new Token(Token.TokenType.EOF, "");
    }

    public String peekValue(int k) {
        return peek(k).getValue();
    }

}
