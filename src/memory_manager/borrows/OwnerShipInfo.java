package memory_manager.borrows;

import ast.ASTNode;
public class OwnerShipInfo {

    public OwnershipState state;

    // Quem controla o lifetime (variável, campo, slot de lista)
    public ASTNode owner;

    // De onde o valor veio originalmente
    public ASTNode origin;

    // MOVE ou COPY
    public AssignKind transferKind;

    // 0 = raiz, 1+ = campo/lista aninhado
    public int depth;

    public OwnerShipInfo(
            OwnershipState state,
            ASTNode owner,
            ASTNode origin,
            AssignKind transferKind,
            int depth
    ) {
        this.state = state;
        this.owner = owner;
        this.origin = origin;
        this.transferKind = transferKind;
        this.depth = depth;
    }
}
