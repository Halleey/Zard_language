package interpreter;

import ast.ASTNode;
import variables.Statement;
import variables.VariableTable;

import java.util.List;

public class MainBlock extends Statement {
    private final List<Statement> statements;

    public MainBlock(List<Statement> statements) {
        this.statements = statements;
    }

    @Override
    public void execute(VariableTable table) {
        try {
            for (Statement statement : statements) {
                statement.execute(table);
            }
        } catch (ReturnException e) {
            System.out.println("Retorno encontrado no nível principal. Ignorando propagação.");
        }
    }
}



