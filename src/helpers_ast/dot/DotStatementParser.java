package translate.identifiers;

import ast.ASTNode;
import ast.functions.FunctionCallNode;
import ast.variables.VariableNode;

import ast.structs.StructMethodCallNode;

import tokens.Token;
import translate.front.Parser;

import java.util.List;

public class DotStatementParser {

    private final Parser parser;

    public DotStatementParser(Parser parser) {
        this.parser = parser;
    }

    public ASTNode parse(ASTNode receiver, String name) {

        parser.advance();
        String memberName = parser.current().getValue();
        String receiverType = parser.getExpressionType(receiver);

        // =====================================================
        // STRUCT: x.y, x.y(), x.y { ... }
        // =====================================================
        if (receiverType != null && receiverType.startsWith("Struct")) {

            StructFieldParser structParser = new StructFieldParser(parser);
            ASTNode structAccess = structParser.parseAsStatement(receiver, memberName);

            // inline update → x.y { ... }
            if (parser.current().getValue().equals("{")) {
                return parseInlineStructUpdate(structAccess);
            }

            // method → x.metodo(...)
            if (parser.current().getValue().equals("(")) {
                List<ASTNode> args = parser.parseArguments();
                parser.eat(Token.TokenType.DELIMITER, ";");

                String structType = receiverType.substring(
                        "Struct<".length(),
                        receiverType.length() - 1
                );

                return new StructMethodCallNode(receiver, structType, memberName, args);
            }

            return structAccess;
        }

        // =====================================================
        // LIST: x.add(), x.remove(), x.get()
        // =====================================================
        if (receiverType != null && receiverType.startsWith("List")) {
            ListMethodParser listParser = new ListMethodParser(parser);
            return listParser.parseStatementListMethod(receiver, memberName);
        }

        // =====================================================
        // NAMESPACE: foo.bar → foo.bar(), foo.bar
        // =====================================================
        parser.advance();
        String fullName = name + "." + memberName;

        if (parser.current().getValue().equals("(")) {
            List<ASTNode> args = parser.parseArguments();
            if (parser.current().getValue().equals(";"))
                parser.eat(Token.TokenType.DELIMITER, ";");
            return new FunctionCallNode(fullName, args);
        } else {
            if (parser.current().getValue().equals(";"))
                parser.eat(Token.TokenType.DELIMITER, ";");
            return new VariableNode(fullName);
        }
    }
    private ASTNode parseInlineStructUpdate(ASTNode accessNode) {

        return null;
    }
}
