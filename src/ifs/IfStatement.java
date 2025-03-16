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
        boolean algumIfExecutou = false;

        for (ConditionBlock block : conditionBlocks) {
            Object value = block.getCondition().evaluate(table).getValue();
            System.out.println("Condição avaliada: " + value + " (" + value.getClass().getSimpleName() + ")");

            if (value instanceof Boolean) {
                if ((Boolean) value) {
                    System.out.println("Executando bloco do if...");
                    block.getBlock().execute(table);
                    algumIfExecutou = true;
                    break;
                }
            } else if (value instanceof Number) {
                if (((Number) value).doubleValue() != 0) {
                    System.out.println("Executando bloco do if (numérico)...");
                    block.getBlock().execute(table);
                    algumIfExecutou = true;
                    break;
                }
            } else {
                System.out.println("Erro: Tipo inesperado na condição do if -> " + value.getClass().getName());
            }
        }

        if (!algumIfExecutou && elseBlock != null) {
            System.out.println("Nenhuma condição foi verdadeira. Executando else...");
            elseBlock.execute(table);
        } else if (!algumIfExecutou) {
            System.out.println("Nenhuma condição foi verdadeira e não há bloco else definido.");
        }
    }
}
