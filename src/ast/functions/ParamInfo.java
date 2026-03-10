package ast.functions;

import context.statics.symbols.Type;

public record ParamInfo(String name, Type type, boolean isRef) {

    // Método de compatibilidade provisoria
    public String typeStr() {
        return type != null ? type.name() : "?";
    }

    public Type typeObj() {
        return type;
    }
}