package low.main;

import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
public class GlobalStringManager {

    private final Map<String, String> stringMap = new LinkedHashMap<>();
    private int counter = 0;

    // 🔹 USADO APENAS NA FASE DE COLETA
    public String getOrCreateString(String literal) {
        return stringMap.computeIfAbsent(literal, l -> "@.str" + counter++);
    }

    // 🔹 USADO NA EMISSÃO (FAIL-FAST)
    public String getStringRef(String literal) {
        String ref = stringMap.get(literal);

        if (ref == null) {
            throw new RuntimeException(
                    "\n[STRING ERROR]\n" +
                            "String usada mas não coletada: \"" + literal + "\"\n" +
                            "Registradas: " + stringMap.keySet() + "\n"
            );
        }

        return ref;
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
        return literal
                .replace("\\", "\\5C")
                .replace("\"", "\\22");
    }



    public int getLength(String literal) {
        return getStringLen(literal);
    }

    public String getGlobalName(String s) {
        return stringMap.get(s);
    }
}