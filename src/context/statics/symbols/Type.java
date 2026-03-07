package context.statics.symbols;

public sealed interface Type permits FunctionType, InputType, ListType, PrimitiveTypes, StructType, UnknownType {
    String name();

    boolean isNumeric();
}
