package low.utils;

public class LLVMNameUtils {
    public static String llvmSafe(String name) {
        if (name == null) return "";
        return name
                .replace("<", "_")
                .replace(">", "")
                .replace(",", "_")
                .replaceAll("\\s+", "")
                .replaceAll("%", "")
                .replaceAll("\\*", "");
    }
}
