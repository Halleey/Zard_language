package ifs;

import expressions.TypedValue;
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
            TypedValue evaluatedValue = block.getCondition().evaluate(table);
            System.out.println("Condição avaliada: " + evaluatedValue);

            if (evaluatedValue.isTruthy()) {
                System.out.println("Executando bloco do if...");
                block.getBlock().execute(table);
                algumIfExecutou = true;
                break;
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
