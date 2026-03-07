package ast.inputs;

import ast.ASTNode;
import context.runtime.RuntimeContext;
import ast.expressions.TypedValue;
import context.statics.symbols.InputType;
import context.statics.symbols.PrimitiveTypes;
import context.statics.symbols.Type;
import low.module.LLVMEmitVisitor;

import java.util.Scanner;

public class InputNode extends ASTNode {
    private final String prompt;

    public InputNode(String prompt) {
        this.prompt = prompt;
    }

    @Override
    public String accept(LLVMEmitVisitor visitor) {
        return "";
    }

    @Override
    public TypedValue evaluate(RuntimeContext ctx) {
        Scanner scanner = new Scanner(System.in);

        if (prompt != null && !prompt.isEmpty()) {
            System.out.print(prompt + ": ");
        }

        String input = scanner.nextLine().trim();

        try {
            int intValue = Integer.parseInt(input);
            return new TypedValue(PrimitiveTypes.INT, intValue);
        } catch (NumberFormatException ignored) {}

        try {
            double doubleValue = Double.parseDouble(input);
            return new TypedValue(PrimitiveTypes.DOUBLE, doubleValue);
        } catch (NumberFormatException ignored) {}

        if (input.equalsIgnoreCase("true")) {
            return new TypedValue(PrimitiveTypes.BOOL, true);
        }
        if (input.equalsIgnoreCase("false")) {
            return
                    new TypedValue(PrimitiveTypes.BOOL, false);
        }

        return new TypedValue(PrimitiveTypes.STRING, input);
    }

    @Override
    public Type getType() {
        return new InputType();
    }

    @Override
    public void print(String prefix) {
        System.out.println(prefix + "Input" +
                (prompt != null && !prompt.isEmpty() ? " (\"" + prompt + "\")" : ""));
    }

    public String getPrompt() {
        return prompt;
    }
}
