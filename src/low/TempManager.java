package low;

public class TempManager {
    private int tempCount = 0;
    private int strCount = 0;

    public String newTemp() {
        return "%t" + (tempCount++);
    }

    public String newStrName() {
        return "@.str" + (strCount++);
    }
}