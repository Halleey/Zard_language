package low.variables;

import low.TempManager;
import low.main.GlobalStringManager;

public class StringEmitter {
    private final TempManager temps;
    private final GlobalStringManager globalStrings;

    public StringEmitter(TempManager temps, GlobalStringManager globalStrings) {
        this.temps = temps;
        this.globalStrings = globalStrings;
    }

    public String createEmptyString(String varPtr) {
        String tmpRaw = temps.newTemp();
        String tmpStruct = temps.newTemp();
        StringBuilder sb = new StringBuilder();
        sb.append("  ").append(tmpRaw)
                .append(" = call i8* @malloc(i64 ptrtoint (%String* getelementptr (%String, %String* null, i32 1) to i64))\n");
        sb.append("  ").append(tmpStruct)
                .append(" = bitcast i8* ").append(tmpRaw).append(" to %String*\n");

        String ptrField = temps.newTemp();
        sb.append("  ").append(ptrField)
                .append(" = getelementptr inbounds %String, %String* ").append(tmpStruct).append(", i32 0, i32 0\n");
        sb.append("  store i8* null, i8** ").append(ptrField).append("\n");

        String lenField = temps.newTemp();
        sb.append("  ").append(lenField)
                .append(" = getelementptr inbounds %String, %String* ").append(tmpStruct).append(", i32 0, i32 1\n");
        sb.append("  store i64 0, i64* ").append(lenField).append("\n");

        sb.append("  store %String* ").append(tmpStruct).append(", %String** ").append(varPtr).append("\n");
        return sb.toString();
    }

    public String createStringFromLiteral(String varPtr, String literal) {
        StringBuilder sb = new StringBuilder();
        int len = literal.length();
        String globalName = globalStrings.getGlobalName(literal);

        String tmpRaw = temps.newTemp();
        String tmpStruct = temps.newTemp();
        sb.append("  ").append(tmpRaw)
                .append(" = call i8* @malloc(i64 ptrtoint (%String* getelementptr (%String, %String* null, i32 1) to i64))\n");
        sb.append("  ").append(tmpStruct)
                .append(" = bitcast i8* ").append(tmpRaw).append(" to %String*\n");

        String tmpData = temps.newTemp();
        sb.append("  ").append(tmpData)
                .append(" = bitcast [").append(len+1).append(" x i8]* ").append(globalName).append(" to i8*\n");

        String ptrField = temps.newTemp();
        sb.append("  ").append(ptrField)
                .append(" = getelementptr inbounds %String, %String* ").append(tmpStruct).append(", i32 0, i32 0\n");
        sb.append("  store i8* ").append(tmpData).append(", i8** ").append(ptrField).append("\n");

        String lenField = temps.newTemp();
        sb.append("  ").append(lenField)
                .append(" = getelementptr inbounds %String, %String* ").append(tmpStruct).append(", i32 0, i32 1\n");
        sb.append("  store i64 ").append(len).append(", i64* ").append(lenField).append("\n");

        sb.append("  store %String* ").append(tmpStruct).append(", %String** ").append(varPtr).append("\n");
        return sb.toString();
    }

    public String emitStore(String varName, String value) {
        return "  store %String* " + value + ", %String** %" + varName + "\n";
    }
}
