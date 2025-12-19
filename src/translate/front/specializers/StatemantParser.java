package translate.front.specializers;

import ast.ASTNode;
import ast.exceptions.BreakNode;
import ast.exceptions.ReturnNode;
import ast.functions.FunctionCallParser;
import ast.functions.FunctionParser;
import ast.home.MainParser;
import ast.ifstatements.IfParser;
import ast.imports.ImportNode;
import ast.inputs.InputParser;
import ast.loops.ForParser;
import ast.loops.WhileParser;
import ast.prints.PrintParser;
import ast.structs.ImplementsParser;
import ast.structs.StructParser;
import ast.variables.VarDeclarationParser;
import tokens.Token;
import translate.front.Parser;
import translate.identifiers.IdentifierParser;

public class StatemantParser {
    private final Parser parser;

    public StatemantParser(Parser parser) {
        this.parser = parser;
    }

    public ASTNode parse() {
        Token tok = parser.current();
        if (tok.getType() == Token.TokenType.KEYWORD) {
            String val = tok.getValue();
            switch (val) {
                case "int", "char", "double","float", "string", "boolean", "Map", "List", "var"-> {
                    VarDeclarationParser varParser = new VarDeclarationParser(parser);
                    return varParser.parseVarDeclaration();
                }
                case "print" -> {
                    PrintParser printParser = new PrintParser(parser, false);
                    return printParser.parsePrint();
                }
                case "println" -> {
                    PrintParser printParser = new PrintParser(parser, true);
                    return printParser.parsePrint();
                }

                case "if" -> {
                    IfParser ifParser = new IfParser(parser);
                    return ifParser.parseIf();
                }
                case "while" -> {
                    WhileParser whileParser = new WhileParser(parser);
                    return whileParser.parse();
                }
                case "for" -> {
                    ForParser forParser = new ForParser(parser);
                    return forParser.parse();

                }

                case "main" -> {
                    MainParser mainParser = new MainParser(parser);
                    return mainParser.parseMain();
                }
                case "Struct" -> {
                    parser.eat(Token.TokenType.KEYWORD, "Struct");

                    String structName = parser.current().getValue();
                    parser.eat(Token.TokenType.IDENTIFIER);
                    StructParser structParser = new StructParser(parser);
                    return structParser.parseStructAfterKeyword(structName);
                }
                case "impl" -> {
                    ImplementsParser implementsParser = new ImplementsParser(parser);
                    return implementsParser.implNode();
                }
                case "input" -> {
                    InputParser inputParser = new InputParser(parser);
                    return inputParser.parse();
                }
                case "return" -> {
                    parser.advance();

                    ASTNode expr = null;

                    if (!parser.current().getValue().equals(";")) {
                        expr = parser.parseExpression();
                    }

                    parser.eat(Token.TokenType.DELIMITER, ";");
                    return new ReturnNode(expr);
                }

                case "break" -> {
                    parser.advance();
                    parser.eat(Token.TokenType.DELIMITER, ";");
                    return new BreakNode();
                }
                case "function" -> {
                    FunctionParser functionParser = new FunctionParser(parser);
                    return functionParser.parseFunction();
                }
                case "call" -> {
                    parser.advance(); // consome a palavra 'call'
                    // Captura apenas o primeiro pedaço (nome da função)
                    String first = parser.current().getValue();
                    parser.advance();

                    // Deixa o restante do trabalho para o FunctionCallParser
                    FunctionCallParser functionCallParser = new FunctionCallParser(parser);
                    return functionCallParser.parseFunctionCall(first);
                }


                case "import" -> {
                    parser.advance(); // consome 'import'

                    Token pathToken = parser.current();
                    String path = pathToken.getValue();
                    parser.advance(); // consome o caminho

                    String alias = null;

                    // se vier "as", lê o alias; se não, é import global
                    if (parser.current().getType() == Token.TokenType.KEYWORD
                            && parser.current().getValue().equals("as")) {

                        parser.advance(); // consome 'as'
                        alias = parser.current().getValue();
                        parser.eat(Token.TokenType.IDENTIFIER); // garante que é IDENTIFIER
                    }

                    parser.eat(Token.TokenType.DELIMITER, ";");
                    return new ImportNode(path, alias);
                }

            }
        }
        if (tok.getType() == Token.TokenType.IDENTIFIER) {

            String name = tok.getValue();
            parser.advance(); // consome IDENTIFIER
            IdentifierParser idParser = new IdentifierParser(parser);
            return idParser.parseAsStatement(name);
        }
        throw new RuntimeException("Comando inesperado: " + tok.getValue());

    }


}
