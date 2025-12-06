package ast.loops;

import ast.ASTNode;
import tokens.Token;
import translate.front.Parser;
import java.util.List;
import ast.variables.UnaryOpNode;
import ast.variables.VariableNode;


public class ForParser {

    private final Parser parser;
    private static final boolean DEBUG = true;

    public ForParser(Parser parser) {
        this.parser = parser;
    }

    private void debug(String msg) {
        if (DEBUG) {
            System.out.println("[ForParser] " + msg);
        }
    }

    private void debugCurrent() {
        if (DEBUG) {
            debug("Current token => " + parser.current().getValue());
        }
    }

    public ASTNode parse() {

        debug("ENTER parse()");
        debugCurrent();

        // for
        parser.eat(Token.TokenType.KEYWORD, "for");
        debug("Consumed 'for'");
        debugCurrent();

        // (
        parser.eat(Token.TokenType.DELIMITER, "(");
        debug("Consumed '('");
        debugCurrent();

        // ================= INIT =================
        ASTNode init = null;
        debug("Parsing INIT");

        if (!parser.current().getValue().equals(";")) {
            debug("INIT as statement");
            init = parser.parseStatement(); // consome ';'
            debug("INIT AST => " + init);
        } else {
            debug("INIT empty");
            parser.eat(Token.TokenType.DELIMITER, ";");
        }

        // ================= CONDITION =================
        ASTNode condition = null;
        debug("Parsing CONDITION");

        if (!parser.current().getValue().equals(";")) {
            condition = parser.parseExpression();
            debug("CONDITION AST => " + condition);
        } else {
            debug("CONDITION empty");
        }

        parser.eat(Token.TokenType.DELIMITER, ";");
        debug("Consumed ';' after CONDITION");
        debugCurrent();

        // ================= INCREMENT =================
        ASTNode increment = null;
        debug("Parsing INCREMENT");

        if (!parser.current().getValue().equals(")")) {

            // CASO ESPECIAL: i++ ou i--
            if (parser.current().getType() == Token.TokenType.IDENTIFIER
                    && (parser.peek().getValue().equals("++")
                    || parser.peek().getValue().equals("--"))) {

                String name = parser.current().getValue();
                String op = parser.peek().getValue();

                debug("Detected unary increment: " + name + op);

                parser.advance(); // consome IDENTIFIER
                parser.advance(); // consome ++ ou --

                increment = new UnaryOpNode(op, new VariableNode(name));
                debug("INCREMENT AST => UnaryOpNode(" + name + op + ")");

            } else {
                // fallback normal
                increment = parser.parseExpression();
                debug("INCREMENT AST => " + increment);
            }

        } else {
            debug("INCREMENT empty");
        }

        parser.eat(Token.TokenType.DELIMITER, ")");
        debug("Consumed ')'");
        debugCurrent();

        // ================= BODY =================
        debug("Parsing BODY");
        List<ASTNode> body = parser.parseBlock();
        debug("BODY size => " + body.size());

        ForNode node = new ForNode(init, condition, increment, body);
        debug("FOR NODE CREATED");

        debug("EXIT parse()");
        return node;
    }
}
