package translate;

import ast.ASTNode;
import ast.functions.FunctionCallNode;
import ast.functions.FunctionReferenceNode;
import ast.maps.MapMethodParser;
import ast.structs.StructFieldAccessNode;
import ast.structs.StructInstanceParser;
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
        String tokenVal = parser.current().getValue();

        switch (tokenVal) {
            case "." -> {
                parser.advance();

                String memberName = parser.current().getValue();
                String varType = parser.getVariableType(name);
                if (varType != null && (varType.startsWith("Struct") || varType.contains("."))) {
                    parser.advance();
                    if (parser.current().getValue().equals("=")) {
                        parser.advance();
                        ASTNode value = parser.parseExpression();
                        parser.eat(Token.TokenType.DELIMITER, ";");
                        return new StructFieldAccessNode(new VariableNode(name), memberName, value);
                    } else {
                        return new StructFieldAccessNode(new VariableNode(name), memberName, null);
                    }
                }

                if (memberName.equals("Struct")) {
                    parser.advance();

                    String structName = parser.current().getValue();
                    parser.eat(Token.TokenType.IDENTIFIER);

                    String varName = parser.current().getValue();
                    parser.eat(Token.TokenType.IDENTIFIER);

                    StructInstanceParser structParser = new StructInstanceParser(parser);
                    String qualifiedName = name + "." + structName;
                    return structParser.parseStructInstanceAfterKeyword(qualifiedName, varName);
                }

                if (varType != null) {
                    String baseType = varType.contains("<")
                            ? varType.substring(0, varType.indexOf("<"))
                            : varType;
                    switch (baseType) {
                        case "List" -> {
                            ListMethodParser listParser = new ListMethodParser(parser);
                            return listParser.parseStatementListMethod(name);
                        }
                        case "Map" -> {
                            MapMethodParser mapParser = new MapMethodParser(parser);
                            return mapParser.parseStatementMapMethod(name);
                        }
                    }
                }

                parser.advance();
                String fullName = name + "." + memberName;

                if (parser.current().getValue().equals("(")) {
                    List<ASTNode> args = parser.parseArguments();
                    parser.eat(Token.TokenType.DELIMITER, ";");
                    return new FunctionCallNode(fullName, args);
                } else {
                    parser.eat(Token.TokenType.DELIMITER, ";");
                    return new FunctionReferenceNode(fullName);
                }
            }
            case "=" -> {
                parser.advance();
                ASTNode value = parser.parseExpression();
                parser.eat(Token.TokenType.DELIMITER, ";");
                return new AssignmentNode(name, value);
            }
            case "++", "--" -> {
                parser.advance();
                parser.eat(Token.TokenType.DELIMITER, ";");
                return new UnaryOpNode(tokenVal, new VariableNode(name));
            }
        }

        return new VariableNode(name);
    }

    public ASTNode parseAsExpression(String name) {
        Token current = parser.current();

        if (current.getValue().equals("(")) {
            List<ASTNode> args = parser.parseArguments();
            return new FunctionCallNode(name, args);
        }

        if (current.getValue().equals(".")) {
            parser.advance();

            String memberName = parser.current().getValue();
            String varType = parser.getVariableType(name);

            if (varType != null && (varType.startsWith("Struct") || varType.contains("."))) {
                parser.advance();
                return new StructFieldAccessNode(new VariableNode(name), memberName, null);
            }

            if (varType != null) {
                String baseType = varType.contains("<")
                        ? varType.substring(0, varType.indexOf("<"))
                        : varType;
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
}
