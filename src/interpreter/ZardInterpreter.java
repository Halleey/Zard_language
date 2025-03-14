package interpreter;
import tokens.Lexer;
import tokens.Token;
import translate.Statement;
import translate.VariableTable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;public class ZardInterpreter {
    public static void main(String[] args) {
        try {
            // Ler o arquivo de código Zard
            String code = new String(Files.readAllBytes(Paths.get("src/interpreter/main.zd")));

            // Tokenizar código
            Lexer lexer = new Lexer(code);
            List<Token> tokens = lexer.tokenize();

            // Construir AST
            Parser parser = new Parser(tokens);
            List<Statement> statements = parser.parse();  // Agora retorna múltiplas declarações

            // Debug: Exibir a AST gerada
            System.out.println("AST Gerada:");
            for (Statement stmt : statements) {
                System.out.println(stmt);
            }

            // Criar tabela de variáveis
            VariableTable variableTable = new VariableTable();

            // Executar todas as instruções
            for (Statement stmt : statements) {
                stmt.execute(variableTable);
            }

            // Exibir estado final da tabela de variáveis
            System.out.println("\nEstado final das variáveis:");
            System.out.println(variableTable);

        } catch (IOException e) {
            System.out.println("Erro ao ler o arquivo: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Erro durante a execução: " + e.getMessage());
            e.printStackTrace(); // Exibir detalhes do erro para depuração
        }
    }
}
