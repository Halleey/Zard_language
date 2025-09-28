package low.main;

import java.util.HashMap;
import java.util.Map;

public class GlobalVarManager {
    private final Map<String, String> varMap = new HashMap<>();
    private int tempCounter = 0;

    public String getVarPtr(String name) {
        return varMap.computeIfAbsent(name, k -> "%" + k + ".addr");
    }

    public String newTemp() {
        return "%t" + (tempCounter++);
    }
}
