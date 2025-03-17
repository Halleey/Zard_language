package ifs;


import returns.ReturnException;
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
            try {
                statement.execute(table);
            } catch (ReturnException e) {
                System.out.println("Retorno encontrado, encerrando execução do bloco atual.");
                throw e; // Propaga o retorno para o escopo superior
            }
        }
    }
}
