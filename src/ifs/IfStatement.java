package ifs;

import expressions.TypedValue;
import returns.ReturnException;
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
                try {
                    block.getBlock().execute(table);
                } catch (ReturnException e) {
                    throw e; // Propaga para os blocos superiores
                }
                algumIfExecutou = true;
                break;
            }
        }

        if (!algumIfExecutou && elseBlock != null) {
            System.out.println("Nenhuma condição foi verdadeira. Executando else...");
            try {
                elseBlock.execute(table);
            } catch (ReturnException e) {
                throw e; // Propaga para os blocos superiores
            }
        } else if (!algumIfExecutou) {
            System.out.println("Nenhuma condição foi verdadeira e não há bloco else definido.");
        }
    }
}

