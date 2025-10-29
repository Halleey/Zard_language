package low;
public class TempManager {
    private int tempCount = 0;
    private int labelCount = 0;
    private String lastTemp; // guarda o último temp gerado

    public String newTemp() {
        lastTemp = "%tmp" + (tempCount++);
        return lastTemp;
    }

    public String newTempWithPrefix(String prefix) {
        lastTemp = "%" + prefix + (tempCount++);
        return lastTemp;
    }

    public String getLastTemp() {
        if (lastTemp == null) {
            throw new IllegalStateException("Nenhum temp gerado ainda");
        }
        return lastTemp;
    }

    public String newLabel(String prefix) {
        return "%" + prefix + "_" + (labelCount++);
    }
}
