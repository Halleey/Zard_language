package low.variables;


public class TempExtractor {

    public String extractTemp(String code) {
        int lastValIdx = code.lastIndexOf(";;VAL:");
        int typeIdx = code.indexOf(";;TYPE:", lastValIdx);
        return code.substring(lastValIdx + 6, typeIdx).trim();
    }

    public String extractType(String code) {
        int lastTypeIdx = code.lastIndexOf(";;TYPE:");
        return code.substring(lastTypeIdx + 7).trim();
    }
}
