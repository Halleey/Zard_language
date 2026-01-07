package memory_manager.ownership;

import memory_manager.ownership.enums.OwnershipState;

public class VarOwnerShip {

    public final String name;
    public OwnershipState state;
    public boolean isShared;


    public VarOwnerShip(String name) {
        this.name = name;
        this.state = OwnershipState.OWNED;
        this.isShared = false;
    }

    @Override
    public String toString() {
        return name + " => " + state;
    }
}
