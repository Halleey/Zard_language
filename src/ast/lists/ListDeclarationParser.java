package ast.lists;


import ast.ASTNode;
import ast.variables.LiteralNode;
import ast.variables.VariableDeclarationNode;
import ast.variables.VariableNode;
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

        String elementType = null;

        // Detecta tipo explícito <type>
        if (parser.current().getValue().equals("<")) {
            parser.advance();
            elementType = parser.current().getValue(); // ex: int, string
            parser.advance(); // consome tipo
            parser.eat(Token.TokenType.OPERATOR, ">");

        }

        // Captura o nome da variável
        String varName = varNameFromCaller;
        if (varName == null) {
            varName = parser.current().getValue();
            parser.advance();

        }

        DynamicList dynamicList;

        // Verifica inicialização
        if (parser.current().getValue().equals("=")) {

            parser.advance(); // consome '='
            parser.eat(Token.TokenType.DELIMITER, "(");

            List<ASTNode> elements = new ArrayList<>();
            while (!parser.current().getValue().equals(")")) {
                ASTNode elementNode = parser.parseExpression();
                elements.add(elementNode);
                if (parser.current().getValue().equals(",")) parser.advance();
            }

            parser.eat(Token.TokenType.DELIMITER, ")");

            // Inferência automática de tipo se não havia tipo explícito
            if (elementType == null) {
                if (elements.isEmpty()) {
                    throw new RuntimeException(
                            "Cannot infer type from empty list: " + varName
                    );
                }
                elementType = inferTypeFromNode(elements.get(0));

            }

            dynamicList = new DynamicList(elementType, elements);
        } else {
            // Lista vazia precisa de tipo explícito
            if (elementType == null) {
                throw new RuntimeException(
                        "Cannot declare empty List without explicit type: " + varName
                );
            }
            dynamicList = new DynamicList(elementType, new ArrayList<>());

        }

        parser.eat(Token.TokenType.DELIMITER, ";");


        parser.declareVariableType(varName, "List<" + elementType + ">");


        return new VariableDeclarationNode(varName, "List<" + elementType + ">", new ListNode(dynamicList));
    }

    private String inferTypeFromNode(ASTNode node) {
        if (node instanceof LiteralNode lit) {
            return switch (lit.getType()) {
                case "int" -> "int";
                case "double" -> "double";
                case "boolean" -> "boolean";
                case "string" -> "string";
                default -> throw new RuntimeException(
                        "Cannot infer type from literal: " + lit
                );
            };
        } else if (node instanceof VariableNode varNode) {
            String type = parser.getVariableType(varNode.getName());
            if (type == null) {
                throw new RuntimeException(
                        "Cannot infer type from unknown variable: " + varNode.getName()
                );
            }
            return type;
        }
        throw new RuntimeException(
                "Cannot infer type from node: " + node.getClass().getSimpleName()
        );
    }
}
