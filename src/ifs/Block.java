package ifs;


import variables.Statement;
import variables.VariableTable;

import java.util.List;

public class Block {
    private final List<Statement> statements;

    public Block(List<Statement> statements) {
        this.statements = statements;
    }

    public void execute(VariableTable table) {
        for (Statement statement : statements) {
            statement.execute(table);
        }
    }
}