package low;

import java.util.HashMap;
import java.util.Map;

public class GlobalStringManager {
    private final Map<String, String> stringMap = new HashMap<>();
    private final StringBuilder globalStrings = new StringBuilder();
    private final TempManager tempManager;

    public GlobalStringManager(TempManager tempManager) {
        this.tempManager = tempManager;
    }

    public StringBuilder getGlobalStrings() {
        return globalStrings;
    }

    public String getOrCreateString(String str) {
        if (stringMap.containsKey(str)) return stringMap.get(str);

        String strName = tempManager.newStrName();
        stringMap.put(str, strName);

        int len = str.length() + 2; // \n + \0
        globalStrings.append(strName)
                .append(" = private constant [")
                .append(len).append(" x i8] c\"")
                .append(str.replace("\"", "\\\""))
                .append("\\0A\\00\"\n");

        return strName;
    }
}
