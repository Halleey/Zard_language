package low.main;

import java.nio.charset.StandardCharsets;
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
            int len = getStringLen(literal);
            sb.append(name)
                    .append(" = private constant [")
                    .append(len)
                    .append(" x i8] c\"")
                    .append(escapeString(literal))
                    .append("\\00\"\n");
        }
        return sb.toString();
    }

    public int getStringLen(String literal) {
        return literal.getBytes(StandardCharsets.UTF_8).length + 1;
    }


    public String escapeString(String literal) {
        return literal.replace("\"", "\\22");
    }

    public String getGlobalName(String literal) {
        return stringMap.get(literal);
    }

    public int getLength(String literal) {
        // retorna tamanho real do literal em bytes UTF-8 + 1 para o \00
        return getStringLen(literal);
    }

}
