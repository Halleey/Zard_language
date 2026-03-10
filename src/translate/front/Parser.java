package translate.front;

import ast.ASTNode;
import ast.functions.FunctionNode;
import ast.lists.ListClearNode;
import ast.lists.ListGetNode;
import ast.lists.ListSizeNode;
import ast.structs.StructFieldAccessNode;
import ast.expressions.ExpressionParser;
import ast.variables.VariableNode;
import context.statics.symbols.ListType;
import context.statics.symbols.PrimitiveTypes;
import context.statics.symbols.StructType;
import context.statics.symbols.Type;
import tokens.Token;
import translate.front.specializers.StatemantParser;

import java.util.*;

public class Parser {

    private final List<Token> tokens;
    private int pos = 0;

    // Variáveis: agora armazenam Type em vez de String
    private final Map<String, Type> variableTypes = new HashMap<>();
    private final Deque<Map<String, Type>> variableStack = new ArrayDeque<>();

    private final Map<String, Map<String, Type>> structDefinitions = new HashMap<>();
    private final Map<String, Map<String, FunctionNode>> structMethods = new HashMap<>();

    private final StatemantParser statementParser;
    private final ExpressionParser expressionParser;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;

        variableStack.push(new HashMap<>()); // contexto global

        this.statementParser = new StatemantParser(this);
        this.expressionParser = new ExpressionParser(this);
    }

    public void registerStructMethod(String structName, FunctionNode fn) {
        String base = baseStructName(structName);
        structMethods
                .computeIfAbsent(base, k -> new HashMap<>())
                .put(fn.getName(), fn);
    }

    private String baseStructName(String name) {
        if (name == null) return null;
        int idx = name.indexOf('<');
        if (idx != -1) return name.substring(0, idx);
        return name;
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

    // Declara variável local (Type agora)
    public void declareVariable(String name, Type type) {
        variableStack.peek().put(name, type);
    }

    // Declara variável global (Type agora)
    public void declareVariableType(String name, Type type) {
        variableTypes.put(name, type);
    }

    // Retorna tipo de variável
    public Type getVariableType(String name) {
        for (Map<String, Type> ctx : variableStack) {
            if (ctx.containsKey(name)) return ctx.get(name);
        }
        return variableTypes.get(name);
    }

    public void declareStruct(String name, Map<String, Type> fields) {
        structDefinitions.put(name, fields);
    }

    public boolean isKnownStruct(String name) {
        return structDefinitions.containsKey(name);
    }

    public Map<String, Type> lookupStruct(String name) {
        return structDefinitions.getOrDefault(name, Collections.emptyMap());
    }

    public Type getStructFieldType(String structName, String field) {
        String base = baseStructName(structName);
        Map<String, Type> fields = structDefinitions.get(base);
        return fields != null ? fields.get(field) : null;
    }

    // retorna tipo de expressão
    public Type getExpressionType(ASTNode node) {
        if (node instanceof VariableNode v) {
            return getVariableType(v.getName());
        }

        if (node instanceof StructFieldAccessNode f) {
            Type structType = getExpressionType(f.getStructInstance());

            if (structType instanceof StructType st) {
                return getStructFieldType(st.name(), f.getFieldName());
            }
        }

        if (node instanceof ListGetNode lg) {
            Type listType = getExpressionType(lg.getListName());
            if (listType instanceof ListType lt) return lt.elementType();
        }

        if (node instanceof ListSizeNode || node instanceof ListClearNode) {
            return PrimitiveTypes.INT;
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
}