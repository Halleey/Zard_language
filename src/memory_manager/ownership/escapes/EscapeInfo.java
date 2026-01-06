package memory_manager.ownership.escapes;

import java.util.HashMap;
import java.util.Map;

public class EscapeInfo {
    private final Map<String, Boolean> escapes = new HashMap<>();

    public void declare(String var) {
        escapes.putIfAbsent(var, false);
    }

    public void markEscapes(String var) {
        escapes.put(var, true);
    }

    public boolean escapes(String var) {
        return escapes.getOrDefault(var, false);
    }

    public Map<String, Boolean> getMap() {
        return escapes;
    }
}
