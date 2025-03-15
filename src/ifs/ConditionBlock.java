package ifs;


import expressions.Expression;


public class ConditionBlock {
    private final Expression condition;
    private final Block block;

    public ConditionBlock(Expression condition, Block block) {
        this.condition = condition;
        this.block = block;
    }

    public Expression getCondition() {
        return condition;
    }

    public Block getBlock() {
        return block;
    }
}