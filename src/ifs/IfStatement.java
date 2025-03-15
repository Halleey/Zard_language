package ifs;

import variables.Statement;
import variables.VariableTable;

import java.util.List;

public class IfStatement extends Statement {
    private final List<ConditionBlock> conditionBlocks;
    private final Block elseBlock;

    public IfStatement(List<ConditionBlock> conditionBlocks, Block elseBlock) {
        this.conditionBlocks = conditionBlocks;
        this.elseBlock = elseBlock;
    }

    @Override
    public void execute(VariableTable table) {
        for (ConditionBlock block : conditionBlocks) {
            Object value = block.getCondition().evaluate(table).getValue();

            if (value instanceof Boolean) {
                // Lida com o caso de um valor booleano
                boolean condition = (Boolean) value;
                if (condition) {
                    // Código quando a condição for verdadeira
                    block.getBlock().execute(table);
                    return;
                }
            } else if (value instanceof Number) {
                // Lida com o caso de um valor numérico (ex: 10 < 11)
                double condition = ((Number) value).doubleValue();
                if (condition != 0) {  // Considera qualquer número diferente de 0 como "verdadeiro"
                    block.getBlock().execute(table);
                    return;
                }
            } else {
                // Se o valor não for booleano nem numérico, você pode adicionar um tratamento de erro ou log
                System.out.println("Tipo inesperado: " + value.getClass().getName());
            }
        }

        // Executa o bloco 'else' caso nenhum dos 'if' anteriores seja verdadeiro
        if (elseBlock != null) {
            elseBlock.execute(table);
        }
    }

}

