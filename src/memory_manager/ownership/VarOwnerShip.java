package memory_manager.ownership;

import context.statics.Symbol;
import memory_manager.ownership.enums.OwnershipState;
public class VarOwnerShip {

    private final Symbol symbol;
    private OwnershipState state;
    private boolean shared;

    public VarOwnerShip(Symbol symbol) {
        this.symbol = symbol;
        this.state = OwnershipState.OWNED;
        this.shared = false;
    }

    public Symbol getSymbol() {
        return symbol;
    }

    public OwnershipState getState() {
        return state;
    }

    public void setState(OwnershipState state) {
        this.state = state;
    }

    public boolean isShared() {
        return shared;
    }

    public void setShared(boolean shared) {
        this.shared = shared;
    }

    @Override
    public String toString() {
        return symbol.getName() + " => " + state;
    }
}
