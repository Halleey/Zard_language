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
        ASTNode receiver = new VariableNode(name);
        String tokenVal = parser.current().getValue();

        switch (tokenVal) {
            case "." -> {
                parser.advance();
                String memberName = parser.current().getValue();

                String receiverType = parser.getExpressionType(receiver);

                if (receiverType != null && receiverType.startsWith("Struct")) {
                    parser.advance();
                    ASTNode node;
                    if (parser.current().getValue().equals("=")) {
                        parser.advance();
                        ASTNode value = parser.parseExpression();
                        parser.eat(Token.TokenType.DELIMITER, ";");
                        node = new StructFieldAccessNode(receiver, memberName, value);
                        return node;
                    } else {
                        node = new StructFieldAccessNode(receiver, memberName, null);
                    }

                    while (parser.current().getValue().equals(".")) {
                        parser.advance();
                        String nextMember = parser.current().getValue();
                        String type = parser.getExpressionType(node);

                        if (type != null) {
                            String baseType = type.contains("<")
                                    ? type.substring(0, type.indexOf("<"))
                                    : type;

                            switch (baseType) {
                                case "List" -> {
                                    ListMethodParser listParser = new ListMethodParser(parser);
                                    return listParser.parseStatementListMethod(node, nextMember);
                                }

                            }
                        }

                        parser.advance();
                        node = new StructFieldAccessNode(node, nextMember, null);
                    }

                    return node;
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

                if (receiverType != null) {
                    String baseType = receiverType.contains("<")
                            ? receiverType.substring(0, receiverType.indexOf("<"))
                            : receiverType;
                    switch (baseType) {
                        case "List" -> {
                            ListMethodParser listParser = new ListMethodParser(parser);
                            return listParser.parseStatementListMethod(receiver, memberName);
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
                return new UnaryOpNode(tokenVal, receiver);
            }
        }

        return receiver;
    }

    public ASTNode parseAsExpression(String name) {
        ASTNode receiver = new VariableNode(name);
        Token current = parser.current();

        if (current.getValue().equals("(")) {
            List<ASTNode> args = parser.parseArguments();
            return new FunctionCallNode(name, args);
        }

        if (current.getValue().equals(".")) {
            parser.advance();
            String memberName = parser.current().getValue();

            String receiverType = parser.getExpressionType(receiver);

            if (receiverType != null && receiverType.startsWith("Struct")) {
                parser.advance();
                ASTNode node = new StructFieldAccessNode(receiver, memberName, null);

                while (parser.current().getValue().equals(".")) {
                    parser.advance();
                    String nextMember = parser.current().getValue();
                    String type = parser.getExpressionType(node);

                    if (type != null) {
                        String baseType = type.contains("<")
                                ? type.substring(0, type.indexOf("<"))
                                : type;

                        switch (baseType) {
                            case "List" -> {
                                ListMethodParser listParser = new ListMethodParser(parser);
                                return listParser.parseExpressionListMethod(node, nextMember);
                            }

                        }
                    }

                    parser.advance();
                    node = new StructFieldAccessNode(node, nextMember, null);
                }

                return node;
            }

            if (receiverType != null) {
                String baseType = receiverType.contains("<")
                        ? receiverType.substring(0, receiverType.indexOf("<"))
                        : receiverType;
                switch (baseType) {
                    case "List" -> {
                        ListMethodParser listParser = new ListMethodParser(parser);
                        return listParser.parseExpressionListMethod(receiver, memberName);
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

        return receiver;
    }
}
