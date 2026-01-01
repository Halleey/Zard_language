package context.analyzers;


public class FlowInfo {
    public boolean mayReturn;
    public boolean alwaysReturn;
    public boolean mayContinue;

    public static FlowInfo returnFlow() {
        FlowInfo f = new FlowInfo();
        f.mayReturn = true;
        f.alwaysReturn = true;
        f.mayContinue = false;
        return f;
    }

    public static FlowInfo continueFlow() {
        FlowInfo f = new FlowInfo();
        f.mayContinue = true;
        return f;
    }
}
