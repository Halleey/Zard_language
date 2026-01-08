package context.statics;


public enum ScopeKind {

    // Raiz do programa
    GLOBAL(false, false),

    // Função sempre cria boundary forte
    FUNCTION(true, false),
    ROOT(false,false ),
    // Bloco simples (ex: { ... })
    BLOCK(true, false),

    // If como nó lógico (não usado diretamente para variáveis)
    IF(false, true),

    // Ramos reais do if (onde variáveis vivem)
    IF_THEN(true, true),
    IF_ELSE(true, true),

    // Loops
    WHILE(true, true),
    FOR(true, true),

    // Definição de struct (escopo de tipo, não de execução)
    STRUCT(false, false);

    /** Este escopo define um limite de lifetime? */
    private final boolean lifetimeBoundary;

    /** Este escopo faz parte de controle de fluxo? */
    private final boolean controlFlow;

    ScopeKind(boolean lifetimeBoundary, boolean controlFlow) {
        this.lifetimeBoundary = lifetimeBoundary;
        this.controlFlow = controlFlow;
    }

    public boolean hasLifetimeBoundary() {
        return lifetimeBoundary;
    }

    public boolean isControlFlow() {
        return controlFlow;
    }

    public boolean isLoop() {
        return this == WHILE || this == FOR;
    }

    public boolean isConditional() {
        return this == IF || this == IF_THEN || this == IF_ELSE;
    }

    public boolean isFunction() {
        return this == FUNCTION;
    }

    public boolean isBlockLike() {
        return lifetimeBoundary && !controlFlow;
    }
}
