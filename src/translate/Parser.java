package translate;
import ast.ASTNode;
import ast.exceptions.BreakNode;
import ast.exceptions.ReturnNode;
import ast.functions.FunctionCallParser;
import ast.functions.FunctionParser;
import ast.imports.ImportNode;
import ast.inputs.InputParser;
import ast.maps.MapMethodParser;
import ast.home.MainParser;
import ast.ifstatements.IfParser;
import ast.loops.WhileParser;
import ast.prints.PrintParser;
import ast.structs.StructInstanceParser;
import ast.structs.StructParser;
import tokens.Token;

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
            String val = tok.getValue();
            switch (val) {
                case "int", "double", "string", "boolean", "Map", "List", "var"-> {
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
                case "Struct" -> {
                    eat(Token.TokenType.KEYWORD, "Struct");

                    String structName = current().getValue();
                    eat(Token.TokenType.IDENTIFIER);

                    Token next = current();

                    if (next.getValue().equals("{")) {
                        StructParser structParser = new StructParser(this);
                        return structParser.parseStructAfterKeyword(structName);
                    } else {
                        StructInstanceParser instanceParser = new StructInstanceParser(this);
                        return instanceParser.parseStructInstanceAfterKeyword(structName);
                    }
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
                    // Começa a ler o identificador da função
                    String funcName = current().getValue();
                    advance();

                    // Aceitar alias: se tiver '.', concatenar
                    while (current().getValue().equals(".")) {
                        advance(); // consome '.'
                        String nextPart = current().getValue();
                        advance();
                        funcName += "." + nextPart; // concatena o alias
                    }

                    FunctionCallParser functionCallParser = new FunctionCallParser(this);
                    return functionCallParser.parseFunctionCall(funcName);
                }

                case "import" -> {
                    advance();
                    Token pathToken = current();
                    String path = pathToken.getValue();
                    advance();
                    eat(Token.TokenType.KEYWORD, "as");
                    String alias = current().getValue();
                    advance();
                    eat(Token.TokenType.DELIMITER, ";");
                    return new ImportNode(path, alias);
                }

            }
        }
        if (tok.getType() == Token.TokenType.IDENTIFIER) {
            String name = tok.getValue();
            advance(); // consome IDENTIFIER
            IdentifierParser idParser = new IdentifierParser(this);
            return idParser.parseAsStatement(name);
        }
        throw new RuntimeException("Comando inesperado: " + tok.getValue());
    }

    public ASTNode parseExpression() {
        return new ExpressionParser(this).parseExpression();
    }

    public Token peek() {
        if (pos + 1 < tokens.size()) {
            return tokens.get(pos + 1);
        }
        return new Token(Token.TokenType.EOF, "");
    }
    public List<ASTNode> parseArguments() {
        return new ExpressionParser(this).parseArguments();
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


    public void declareVariableType(String name, String type) {
        variableTypes.put(name, type);
    }

}
