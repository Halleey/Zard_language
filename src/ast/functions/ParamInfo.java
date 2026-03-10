package ast.functions;

import context.statics.symbols.Type;

public record ParamInfo(String name, Type type, boolean isRef) {

    public Type typeObj() {
        return type;
    }
}