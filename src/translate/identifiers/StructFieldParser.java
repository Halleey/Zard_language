package translate.identifiers;

import context.statics.symbols.ListType;
import context.statics.symbols.Type;
import translate.front.Parser;

import ast.ASTNode;
import ast.structs.StructFieldAccessNode;


public class StructFieldParser {
    private final Parser parser;

    public StructFieldParser(Parser parser) {
        this.parser = parser;
    }

    public ASTNode parseAsStatement(ASTNode receiver, String memberName) {
        return parseStructField(receiver, memberName, true);
    }

    public ASTNode parseAsExpression(ASTNode receiver, String memberName) {
        return parseStructField(receiver, memberName, false);
    }

    private ASTNode parseStructField(ASTNode receiver, String memberName, boolean isStatement) {
        ASTNode node;

        // Atribuição (somente statements)
        if (isStatement && parser.current().getValue().equals("=")) {
            parser.advance();
            ASTNode value = parser.parseExpression();
            parser.eat(tokens.Token.TokenType.DELIMITER, ";");
            return new StructFieldAccessNode(receiver, memberName, value);
        }

        node = new StructFieldAccessNode(receiver, memberName, null);

        // Encadeamento de campos ou listas
        while (parser.current().getValue().equals(".")) {
            parser.advance();
            String nextMember = parser.current().getValue();

            Type nodeType = parser.getExpressionType(node);

            if (nodeType instanceof ListType) {
                ListMethodParser listParser = new ListMethodParser(parser);
                return isStatement
                        ? listParser.parseStatementListMethod(node, nextMember)
                        : listParser.parseExpressionListMethod(node, nextMember);
            }

            parser.advance();
            node = new StructFieldAccessNode(node, nextMember, null);
        }

        return node;
    }
}