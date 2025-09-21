package low;

public class TempManager {
    private int tempCount = 0;
    private int strCount = 0;
    private int labelCount = 0;

    public String newTemp() {
        return "%t" + (tempCount++);
    }

    public String newStrName() {
        return "@.str" + (strCount++);
    }

    public String newLabel(String prefix) {
        return prefix + "_" + (labelCount++);
    }
}
