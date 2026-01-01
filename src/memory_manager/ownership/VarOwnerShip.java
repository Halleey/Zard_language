package memory_manager.ownership;

public class VarOwnerShip {

    public final String name;
    public OwnershipState state;

    public VarOwnerShip(String name) {
        this.name = name;
        this.state = OwnershipState.OWNED;
    }

    @Override
    public String toString() {
        return name + " => " + state;
    }
}
