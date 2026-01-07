package tokens;

import java.util.ArrayList;
import java.util.List;


public class Lexer {
    private final String input;
    private int pos = 0;
    private char currentChar;

    public Lexer(String input) {
        this.input = input;
        this.currentChar = input.charAt(pos);
    }

    private void error() {
        if (pos < input.length()) {
            throw new RuntimeException("Error parsing input at position " + pos + ": " + input.charAt(pos));
        } else {
            throw new RuntimeException("Error parsing input at position " + pos + ": EOF");
        }
    }

    private void advance() {
        pos++;

        if (pos > input.length() - 1) { // Se a posição atual for maior que o último índice da entrada
            currentChar = '\0'; // Define o caractere atual como '\0' para indicar o fim da entrada
        } else {
            currentChar = input.charAt(pos); // Atualiza o caractere atual para o próximo caractere na entrada
        }
    }

    private void skipCommment()
    {
        while (currentChar != '\0' && currentChar != '\n') {
            advance();
        }
    }
    private void skipWhitespace() {
        while (currentChar != '\0') {
            if (Character.isWhitespace(currentChar)) {
                advance();
            }
            else if(currentChar == '#') {
                skipCommment();
            }
            else {
                break;
            }
        }
    }

    private Token readIdentifier() {
        StringBuilder result = new StringBuilder();
        while (currentChar != '\0' && (Character.isLetterOrDigit(currentChar) || currentChar == '_')) {
            result.append(currentChar);
            advance();
        }
        String identifier = result.toString();

        return switch (identifier) {
            case "int", "string", "double", "float", "boolean","char", "print", "if", "else", "else if", "input", "function", "return",
                 "main", "while", "for", "call", "List",  "break","println", "import", "var", "as", "Struct", "impl", "Shd" ->
                    new Token(Token.TokenType.KEYWORD, identifier);
            case "new" -> new Token(Token.TokenType.INSTANCE, identifier);
            case "true", "false" -> new Token(Token.TokenType.BOOLEAN, identifier); // true e false como BOOLEAN
            default ->
                // Todos os métodos de lista agora entram aqui como IDENTIFIER
                    new Token(Token.TokenType.IDENTIFIER, identifier);
        };
    }


    private Token readOperator() {
        StringBuilder result = new StringBuilder();
        result.append(currentChar);
        advance();

        if ((result.toString().equals("+") && currentChar == '+') ||
                (result.toString().equals("-") && currentChar == '-') ||
                (result.toString().equals("=") && currentChar == '=') ||
                (result.toString().equals("!") && currentChar == '=') ||
                (result.toString().equals("<") && (currentChar == '=' || currentChar == '>')) ||
                (result.toString().equals(">") && currentChar == '=') ||
                (result.toString().equals("&") && currentChar == '&') ||
                (result.toString().equals("|") && currentChar == '|')) {

            result.append(currentChar);
            advance();
        }

        return new Token(Token.TokenType.OPERATOR, result.toString());
    }



    private Token readNumber() {
        StringBuilder result = new StringBuilder();
        boolean hasDecimalPoint = false;

        while (currentChar != '\0' && (Character.isDigit(currentChar) || currentChar == '.')) {
            if (currentChar == '.') {
                if (hasDecimalPoint) {
                    throw new RuntimeException("Número inválido com mais de um ponto decimal");
                }
                hasDecimalPoint = true;
            }
            result.append(currentChar);
            advance();
        }
        return new Token(Token.TokenType.NUMBER, result.toString());
    }

    private Token readString() {
        StringBuilder result = new StringBuilder();
        advance();
        while (currentChar != '\0' && currentChar != '"') {
            result.append(currentChar);
            advance();
        }
        advance();
        return new Token(Token.TokenType.STRING, result.toString());
    }


    private Token readChar() {
        advance();

        if (currentChar == '\0' || currentChar == '\'') {
            throw new RuntimeException("Empty char literal");
        }

        char value = currentChar;
        advance();

        if (currentChar != '\'') {
            throw new RuntimeException("Char literal deve terminar com aspas simples");
        }

        advance();

        return new Token(Token.TokenType.CHAR, String.valueOf(value));
    }

    public List<Token> tokenize() {
        List<Token> tokens = new ArrayList<>();
        while (currentChar != '\0') {
            if (Character.isWhitespace(currentChar)) {
                skipWhitespace();
                continue;
            }
            if (currentChar == '(' || currentChar == ')' || currentChar == '.' || currentChar == ':' ||
                    currentChar == '[' || currentChar == ']' ||
                    currentChar == '{' || currentChar == '}' || currentChar == ';' || currentChar == ',') {
                tokens.add(new Token(Token.TokenType.DELIMITER, Character.toString(currentChar)));
                advance();
                continue;
            }
            if (currentChar == '"') {
                tokens.add(readString());
                continue;
            }
            if (Character.isLetter(currentChar)) {
                tokens.add(readIdentifier());
                continue;
            }
            if (Character.isDigit(currentChar)) {
                tokens.add(readNumber());
                continue;
            }
            if(currentChar == '?') {
                tokens.add(readOperator());
                continue;
            }
            if (currentChar == '+' || currentChar == '-' || currentChar == '*' || currentChar == '/' || currentChar == '%' ||
                    currentChar == '&' || currentChar == '|') {
                tokens.add(readOperator());
                continue;
            }
            if (currentChar == '=' || currentChar == '>' || currentChar == '<' || currentChar == '!') {
                tokens.add(readOperator());
                continue;
            }
            if (currentChar == '\'') {
                tokens.add(readChar());
                continue;
            }
            error();
        }
        return tokens;
    }


}