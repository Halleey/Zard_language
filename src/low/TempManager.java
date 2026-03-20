package low;
import java.util.HashMap;
import java.util.Map;

public class TempManager {
    private int tempCount = 0;
    private int labelCount = 0;
    private String lastTemp; // guarda o último temp gerado

    // contador por nome de variável lógica (i, j, k, numeros, etc)
    private final Map<String, Integer> varCounters = new HashMap<>();

    public String newTemp() {
        lastTemp = "%tmp" + (tempCount++);
        return lastTemp;
    }

    public String newNamedVar(String base) {
        if (base == null || base.isEmpty()) {
            lastTemp = newTemp();
            return lastTemp;
        }

        // só por segurança, remove caracteres estranhos
        String clean = base.replace('.', '_')
                .replace('<', '_')
                .replace('>', '_');

        int idx = varCounters.getOrDefault(clean, 0);
        varCounters.put(clean, idx + 1);

        if (idx == 0) {
            lastTemp = "%" + clean;
        } else {
            lastTemp = "%" + clean + "_" + idx;
        }
        return lastTemp;
    }

    public String getLastTemp(){
        return lastTemp;
    }


    public int getTempCount() {
        return tempCount;
    }

    public String newLabel(String prefix) {
        return prefix + "_" + (labelCount++);
    }

    public void setCounter(int tempCount) {
        this.tempCount = tempCount;
    }
}
