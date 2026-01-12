package memory_manager.ownership.enums;

public enum OwnerShipAction {
    OWNED,      // declaração
    BORROW,     // uso (print, expr, arg)
    MOVED,      // composição / return
    DEEP_COPY,   // p2 = p1 (mesmo nível)
    READ
}
