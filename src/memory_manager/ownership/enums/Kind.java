package memory_manager.ownership.enums;

public enum Kind {
    VAR,        // variável root
    FIELD,      // campo de struct
    LIST_ELEM,  // elemento de lista
    ANON        // fallback / temporário
}
