package memory_manager.ownership;

public enum OwnerShipAction {
    OWNED,      // declaração
    BORROW,     // uso (print, expr, arg)
    MOVED,      // composição / return
    DEEP_COPY   // p2 = p1 (mesmo nível)
}
