package translate.front;

import ast.ASTNode;
import ast.functions.FunctionNode;
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
    private final Map<String, Map<String, FunctionNode>> structMethods = new HashMap<>();


    private final StatemantParser statementParser;
    private final ExpressionParser expressionParser;


    public void registerStructMethod(String structName, FunctionNode fn) {
        String base = baseStructName(structName);
        structMethods
                .computeIfAbsent(base, k -> new HashMap<>())
                .put(fn.getName(), fn);
    }

    public boolean hasStructMethod(String structName, String methodName) {

        Map<String, FunctionNode> methods = structMethods.get(structName);
        if (methods != null && methods.containsKey(methodName)) {
            return true;
        }

        int genericIdx = structName.indexOf('<');
        if (genericIdx != -1) {
            String base = structName.substring(0, genericIdx);
            Map<String, FunctionNode> baseMethods = structMethods.get(base);
            return baseMethods != null && baseMethods.containsKey(methodName);
        }

        return false;
    }


    private String baseStructName(String name) {
        if (name == null) return null;
        int idx = name.indexOf('<');
        if (idx != -1) return name.substring(0, idx);
        return name;
    }


    public Parser(List<Token> tokens) {
        this.tokens = tokens;

        variableStack.push(new HashMap<>()); // contexto global

        this.statementParser = new StatemantParser(this);
        this.expressionParser = new ExpressionParser(this);
    }

    public List<ASTNode> parse() {
        List<ASTNode> nodes = new ArrayList<>();


        while (current().getType() != Token.TokenType.EOF) {
            ASTNode stmt = parseStatement();
            nodes.add(stmt);
        }

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

    public boolean isKnownStruct(String name) {
        return structDefinitions.containsKey(name);
    }

    public Map<String, String> lookupStruct(String name) {
        return structDefinitions.getOrDefault(name, Collections.emptyMap());
    }

    public String getStructFieldType(String structName, String field) {
        String base = baseStructName(structName);
        Map<String, String> fields = structDefinitions.get(base);
        return fields != null ? fields.get(field) : null;
    }



    public String getExpressionType(ASTNode node) {

        if (node instanceof VariableNode v) {
            return getVariableType(v.getName());
        }

        if (node instanceof StructFieldAccessNode f) {
            String structType = getExpressionType(f.getStructInstance());

            if (structType != null && structType.startsWith("Struct<")) {
                String inside = structType.substring(7, structType.length() - 1);
                String base = baseStructName(inside);
                return getStructFieldType(base, f.getFieldName());
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

    public FunctionNode getStructMethod(String structName, String methodName) {

        String base = baseStructName(structName);

        Map<String, FunctionNode> methods = structMethods.get(base);
        if (methods == null) return null;

        return methods.get(methodName);
    }

    public Map<String, String> getAllVariableTypes() {
        return Collections.unmodifiableMap(variableTypes);
    }




}
