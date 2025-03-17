package interpreter;

import ast.ASTNode;
import returns.ReturnException;
import returns.ReturnStatement;
import variables.Statement;
import variables.VariableTable;

import java.util.List;
public class MainBlock extends ASTNode {
    private final List<Statement> statements;

    public MainBlock(List<Statement> statements) {
        this.statements = statements;
    }

    public void execute(VariableTable table) {
        for (Statement stmt : statements) {
            try {
                stmt.execute(table);
            } catch (ReturnException e) {
                System.out.println("Retorno encontrado dentro de um bloco, mas a execução do Main continua.");
            }
        }
    }
}



