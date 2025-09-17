package translate;

import ast.ASTNode;
import ast.lists.ListNode;
import expressions.DynamicList;
import tokens.Token;
import variables.VariableDeclarationNode;

import java.util.ArrayList;
import java.util.List;



public class VarDeclarationParser {
    private final Parser parser;

    public VarDeclarationParser(Parser parser) {
        this.parser = parser;
    }

    public ASTNode parseVarDeclaration() {
        // captura tipo e nome
        String type = parser.current().getValue();
        parser.advance();
        String name = parser.current().getValue();
        parser.advance();

        ASTNode initializer = null;

        // variáveis do tipo lista
        if (type.equals("list")) {
            if (parser.current().getValue().equals("=")) {
                parser.advance();
                parser.eat(Token.TokenType.DELIMITER, "(");

                List<ASTNode> elements = new ArrayList<>();
                while (!parser.current().getValue().equals(")")) {
                    elements.add(parser.parseExpression());
                    if (parser.current().getValue().equals(",")) parser.advance();
                }

                parser.eat(Token.TokenType.DELIMITER, ")");
                initializer = new ListNode(new DynamicList(elements));
            } else {
                initializer = new ListNode(new DynamicList(new ArrayList<>()));
            }

            parser.eat(Token.TokenType.DELIMITER, ";");

            // registra o tipo no parser para uso futuro
            parser.declareVariableType(name, "list");
            return new VariableDeclarationNode(name, "list", initializer);
        }

        // variáveis de outros tipos
        if (parser.current().getValue().equals("=")) {
            parser.advance();
            initializer = parser.parseExpression();
        }
        parser.eat(Token.TokenType.DELIMITER, ";");

        parser.declareVariableType(name, type); // registra tipo
        return new VariableDeclarationNode(name, type, initializer);
    }
}
