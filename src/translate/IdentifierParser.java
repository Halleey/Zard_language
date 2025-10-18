package translate;

import ast.ASTNode;
import ast.functions.FunctionCallNode;
import ast.functions.FunctionReferenceNode;
import ast.maps.MapMethodParser;
import tokens.Token;
import ast.variables.AssignmentNode;
import ast.variables.UnaryOpNode;
import ast.variables.VariableNode;

import java.util.List;

public class IdentifierParser {
    private final Parser parser;

    public IdentifierParser(Parser parser) {
        this.parser = parser;
    }

    public ASTNode parseAsStatement(String name) {
        System.out.println("CURRENT TOKEN ----" + parser.current() );
        String tokenVal = parser.current().getValue();

        if (parser.current().getValue().equals(".")) {
            parser.advance(); // consome '.'
            String methodName = parser.current().getValue();

            String varType = parser.getVariableType(name);
            ASTNode node = getAstNode(varType, name, methodName);
            return node;
        }

        if ("=".equals(tokenVal)) {
            parser.advance();
            ASTNode value = parser.parseExpression();
            parser.eat(Token.TokenType.DELIMITER, ";");
            return new AssignmentNode(name, value);
        }

        if ("++".equals(tokenVal) || "--".equals(tokenVal)) {
            parser.advance();
            parser.eat(Token.TokenType.DELIMITER, ";");
            return new UnaryOpNode(tokenVal, new VariableNode(name));
        }
        return new VariableNode(name);
    }
    public ASTNode parseAsExpression(String name) {
        Token current = parser.current();

        // Chamada de função
        if (current.getValue().equals("(")) {
            List<ASTNode> args = parser.parseArguments();
            return new FunctionCallNode(name, args);
        }

        // Método ou membro
        if (current.getValue().equals(".")) {
            parser.advance(); // consome '.'

            String varType = parser.getVariableType(name);
            if (varType != null) {
                String baseType = varType.contains("<") ? varType.substring(0, varType.indexOf("<")) : varType;
                switch (baseType) {
                    case "List" -> {
                        ListMethodParser listParser = new ListMethodParser(parser);
                        return listParser.parseExpressionListMethod(name);
                    }
                    case "Map" -> {
                        MapMethodParser mapParser = new MapMethodParser(parser);
                        return mapParser.parseExpressionMapMethod(name);
                    }
                }
            }

            String memberName = parser.current().getValue();
            parser.advance();
            String fullName = name + "." + memberName;

            if (parser.current().getValue().equals("(")) {
                List<ASTNode> args = parser.parseArguments();
                return new FunctionCallNode(fullName, args);
            } else {
                return new FunctionReferenceNode(fullName);
            }
        }

        return new VariableNode(name);
    }
    private ASTNode getAstNode(String varType, String name, String methodName) {
        String baseType = varType.contains("<") ? varType.substring(0, varType.indexOf("<")) : varType;

        ASTNode node;
        switch (baseType) {
            case "List" -> {
                ListMethodParser listParser = new ListMethodParser(parser);
                node = listParser.parseStatementListMethod(name);
            }
            case "Map" -> {
                MapMethodParser mapParser = new MapMethodParser(parser);
                node = mapParser.parseStatementMapMethod(name);
            }
            default -> throw new RuntimeException("Tipo " + varType + " não suporta métodos: " + methodName);
        }
        return node;
    }
}
