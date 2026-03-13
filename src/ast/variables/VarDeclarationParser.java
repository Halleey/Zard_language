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

        System.out.println("[VarDecl] Tipo detectado: " + typeKeyword);

        ASTNode initializer = null;
        Type varType = null;

            String varName = parser.current().getValue();
            parser.advance();

            if (!typeKeyword.equals("var")) {
                varType = TypeResolver.resolve(typeKeyword);
                parser.declareVariableType(varName, varType);
            }

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