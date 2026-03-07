package ast.variables;

import ast.ASTNode;
import ast.lists.ListDeclarationParser;

import ast.structs.StructInstanceParser;
import context.statics.symbols.Type;
import tokens.Token;
import translate.front.Parser;
public class VarDeclarationParser {
    private final Parser parser;

    public VarDeclarationParser(Parser parser) {
        this.parser = parser;
    }

    public ASTNode parseVarDeclaration() {
        String typeKeyword = parser.current().getValue();
        parser.advance();

        ASTNode initializer = null;
        Type varType = null; // tipo real do ASTNode

        if (typeKeyword.equals("List")) {
            ListDeclarationParser listParser = new ListDeclarationParser(parser);
            return listParser.parse(null); // o próprio ListDeclarationParser já gera ASTNode com Type
        }

        else if (typeKeyword.equals("Struct")) {
            String structName = parser.current().getValue();
            parser.eat(Token.TokenType.IDENTIFIER);

            String varName = parser.current().getValue();
            parser.eat(Token.TokenType.IDENTIFIER);

            varType = TypeResolver.resolve("Struct<" + structName + ">");

            if (parser.current().getValue().equals("=")) {
                parser.advance();

                if (parser.current().getValue().equals("{")) {
                    StructInstanceParser instanceParser = new StructInstanceParser(parser);
                    VariableDeclarationNode node = instanceParser.parseStructInstanceAfterKeyword(structName, varName);
                    parser.declareVariableType(varName, varType); // registra Type
                    return node;
                } else {
                    initializer = parser.parseExpression();
                }
            } else {
                parser.eat(Token.TokenType.DELIMITER, ";");
            }

            parser.declareVariableType(varName, varType);
            return new VariableDeclarationNode(varName, varType, initializer);
        }

        else {
            String varName = parser.current().getValue();
            parser.advance();

            if (!typeKeyword.equals("var")) {
                varType = TypeResolver.resolve(typeKeyword);
                parser.declareVariableType(varName, varType);
            }

            // Inicialização
            if (parser.current().getValue().equals("=")) {
                parser.advance();
                initializer = parser.parseExpression();
            }

            parser.eat(Token.TokenType.DELIMITER, ";");

            if (typeKeyword.equals("var") && initializer != null) {
                varType = initializer.getType();
            }

            parser.declareVariableType(varName, varType);

            return new VariableDeclarationNode(varName, varType, initializer);
        }
    }
}