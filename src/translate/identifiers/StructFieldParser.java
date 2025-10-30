package translate.identifiers;

import translate.ListMethodParser;
import translate.Parser;

import ast.ASTNode;
import ast.structs.StructFieldAccessNode;

public class StructFieldParser {
    private final Parser parser;

    public StructFieldParser(Parser parser) {
        this.parser = parser;
    }

    public ASTNode parseAsStatement(ASTNode receiver, String memberName) {
        parser.advance();
        ASTNode node;
        if (parser.current().getValue().equals("=")) {
            parser.advance();
            ASTNode value = parser.parseExpression();
            parser.eat(tokens.Token.TokenType.DELIMITER, ";");
            node = new StructFieldAccessNode(receiver, memberName, value);
            return node;
        } else {
            node = new StructFieldAccessNode(receiver, memberName, null);
        }

        while (parser.current().getValue().equals(".")) {
            parser.advance();
            String nextMember = parser.current().getValue();
            String type = parser.getExpressionType(node);

            if (type != null && type.startsWith("List")) {
                ListMethodParser listParser = new ListMethodParser(parser);
                return listParser.parseStatementListMethod(node, nextMember);
            }

            parser.advance();
            node = new StructFieldAccessNode(node, nextMember, null);
        }

        return node;
    }

    public ASTNode parseAsExpression(ASTNode receiver, String memberName) {
        parser.advance();
        ASTNode node = new StructFieldAccessNode(receiver, memberName, null);

        while (parser.current().getValue().equals(".")) {
            parser.advance();
            String nextMember = parser.current().getValue();
            String type = parser.getExpressionType(node);

            if (type != null && type.startsWith("List")) {
                ListMethodParser listParser = new ListMethodParser(parser);
                return listParser.parseExpressionListMethod(node, nextMember);
            }

            parser.advance();
            node = new StructFieldAccessNode(node, nextMember, null);
        }

        return node;
    }
}
