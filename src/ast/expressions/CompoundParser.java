package ast.expressions;

import ast.ASTNode;
import ast.variables.VariableNode;
import tokens.Token;
import translate.front.Parser;

public class CompoundParser {

    public final Parser parser;

    public CompoundParser(Parser parser) {
        this.parser = parser;
    }

    public ASTNode parse(String name, String operator) {
        // name -> variável já lida
        // operator -> "+=" ou "-="

        // consome o operador
        parser.advance();

        // parse da expressão RHS
        ASTNode expr = parser.parseExpression();

        // exige ';'
        parser.eat(Token.TokenType.DELIMITER, ";");

        return new CompoundAssignmentNode(
                operator,
                new VariableNode(name),
                expr
        );
    }
}
