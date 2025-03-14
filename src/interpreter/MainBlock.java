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

    public List<Statement> getStatements() {
        return statements;
    }

    @Override
    public String toString() {
        return "MainBlock{" + statements + "}";
    }

    @Override
    public void execute(VariableTable table) {
        for(Statement stm:statements) {
            stm.execute(table);
        }
    }
}


