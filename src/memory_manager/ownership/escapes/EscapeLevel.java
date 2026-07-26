package memory_manager.ownership.escapes;

public enum EscapeLevel {
    NONE,
    BLOCK,
    LOOP,
    FUNCTION,
    GLOBAL
}
