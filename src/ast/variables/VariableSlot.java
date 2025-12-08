package ast.variables;

import ast.expressions.TypedValue;

public class VariableSlot {
    public TypedValue typedValue;

    public VariableSlot(TypedValue typedValue) {
        this.typedValue = typedValue;
    }
}
