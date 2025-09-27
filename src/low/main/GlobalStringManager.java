package low.main;

import java.util.LinkedHashMap;
import java.util.Map;

public class GlobalStringManager {
    private final Map<String, String> stringMap = new LinkedHashMap<>();
    private int counter = 0;

    public String getOrCreateString(String literal) {
        if (stringMap.containsKey(literal)) return stringMap.get(literal);
        String name = "@.str" + counter++;
        stringMap.put(literal, name);
        return name;
    }

    public String getGlobalStrings() {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> e : stringMap.entrySet()) {
            String literal = e.getKey();
            String name = e.getValue();
            int len = literal.length() + 1; // +1 para o \00
            sb.append(name)
                    .append(" = private constant [")
                    .append(len)
                    .append(" x i8] c\"")
                    .append(literal.replace("\"", "\\22")) // escape aspas
                    .append("\\00\"\n");
        }
        return sb.toString();
    }
}
