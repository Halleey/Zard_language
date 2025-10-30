package translate.identifiers;

import ast.ASTNode;
import ast.functions.FunctionCallNode;
import ast.functions.FunctionReferenceNode;
import ast.structs.StructInstanceParser;
import tokens.Token;
import ast.variables.AssignmentNode;
import ast.variables.UnaryOpNode;
import ast.variables.VariableNode;
import translate.ListMethodParser;
import translate.Parser;

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
                    StructFieldParser structParser = new StructFieldParser(parser);
                    return structParser.parseAsStatement(receiver, memberName);
                }

                // delega para List
                if (receiverType != null && receiverType.startsWith("List")) {
                    ListMethodParser listParser = new ListMethodParser(parser);
                    return listParser.parseStatementListMethod(receiver, memberName);
                }

                // caso seja Struct instanciada diretamente
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

            // delega para Struct
            if (receiverType != null && receiverType.startsWith("Struct")) {
                StructFieldParser structParser = new StructFieldParser(parser);
                return structParser.parseAsExpression(receiver, memberName);
            }

            // delega para List
            if (receiverType != null && receiverType.startsWith("List")) {
                ListMethodParser listParser = new ListMethodParser(parser);
                return listParser.parseExpressionListMethod(receiver, memberName);
            }

            // caso geral
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