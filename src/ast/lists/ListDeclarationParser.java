package ast.lists;


import ast.ASTNode;
import ast.variables.LiteralNode;
import ast.variables.TypeResolver;
import ast.variables.VariableDeclarationNode;
import ast.variables.VariableNode;
import context.statics.symbols.ListType;
import context.statics.symbols.PrimitiveTypes;
import context.statics.symbols.Type;
import tokens.Token;
import translate.front.Parser;

import java.util.ArrayList;
import java.util.List;

public class ListDeclarationParser {
    private final Parser parser;

    public ListDeclarationParser(Parser parser) {
        this.parser = parser;
    }

    public ASTNode parse(String varNameFromCaller) {

        parser.advance();
        Type elementType = null;
        boolean isReference = false;

        // <type*> ou <type>
        if (parser.current().getValue().equals("<")) {


            parser.advance();

            String elemTypeStr = parser.current().getValue();

            parser.advance();

            if (parser.current().getValue().equals("*")) {
                isReference = true;
                parser.advance();
            }

            parser.eat(Token.TokenType.OPERATOR, ">");

            elementType = TypeResolver.resolve(elemTypeStr);
        }

        // nome da variável
        String varName = varNameFromCaller;

        if (varName == null) {
            varName = parser.current().getValue();
            parser.advance();
        }

        System.out.println("[ListParser] Token after variable: " + parser.current());

        DynamicList dynamicList;

        // lista com inicialização
        if (parser.current().getValue().equals("=")) {


            parser.advance();
            parser.eat(Token.TokenType.DELIMITER, "(");

            List<ASTNode> elements = new ArrayList<>();


            if (!parser.current().getValue().equals(")")) {

                elements.add(parser.parseExpression());

                while (parser.current().getValue().equals(",")) {

                    parser.advance();

                    if (parser.current().getValue().equals(")")) {
                        throw new RuntimeException("Trailing comma in list initializer");
                    }

                    elements.add(parser.parseExpression());
                }
            }

            parser.eat(Token.TokenType.DELIMITER, ")");


            // inferência de tipo
            if (elementType == null) {


                if (elements.isEmpty()) {
                    throw new RuntimeException("Cannot infer type from empty list: " + varName);
                }

                elementType = inferTypeFromNode(elements.get(0));

            }

            dynamicList = new DynamicList(elementType, elements, isReference);

        } else {


            if (elementType == null) {
                throw new RuntimeException("Cannot declare empty List without explicit type: " + varName);
            }

            dynamicList = new DynamicList(elementType, new ArrayList<>(), isReference);
        }

        parser.eat(Token.TokenType.DELIMITER, ";");

        ListType listType = new ListType(elementType, isReference);


        parser.declareVariableType(varName, listType);



        return new VariableDeclarationNode(varName, listType, new ListNode(dynamicList));
    }

    private Type inferTypeFromNode(ASTNode node) {
        if (node instanceof LiteralNode lit) {
            Type literalType = lit.getType();
            if (literalType instanceof PrimitiveTypes) {
                return literalType;
            }
            throw new RuntimeException("Cannot infer type from literal: " + lit);
        } else if (node instanceof VariableNode varNode) {
            Type type = parser.getVariableType(varNode.getName());
            if (type == null) {
                throw new RuntimeException("Cannot infer type from unknown variable: " + varNode.getName());
            }
            return type;
        }
        throw new RuntimeException(
                "Cannot infer type from node: " + node.getClass().getSimpleName()
        );
    }
}