package ast.inputs;

import ast.ASTNode;
import context.runtime.RuntimeContext;
import ast.expressions.TypedValue;
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
            return new TypedValue("int", intValue);
        } catch (NumberFormatException ignored) {}

        try {
            double doubleValue = Double.parseDouble(input);
            return new TypedValue("double", doubleValue);
        } catch (NumberFormatException ignored) {}

        if (input.equalsIgnoreCase("true")) {
            return new TypedValue("boolean", true);
        }
        if (input.equalsIgnoreCase("false")) {
            return new TypedValue("boolean", false);
        }

        return new TypedValue("string", input);
    }

    @Override
    public String getType() {
        return "input";
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
