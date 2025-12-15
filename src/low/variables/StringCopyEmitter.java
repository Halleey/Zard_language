package low.variables;

import low.TempManager;

public class StringCopyEmitter {
    private final TempManager temps;

    public StringCopyEmitter(TempManager temps) {
        this.temps = temps;
    }

    public String emit(String srcStr) {
        StringBuilder sb = new StringBuilder();

        // src->data
        String dataPtr = temps.newTemp();
        sb.append("  ").append(dataPtr)
                .append(" = getelementptr inbounds %String, %String* ")
                .append(srcStr).append(", i32 0, i32 0\n");

        String chars = temps.newTemp();
        sb.append("  ").append(chars)
                .append(" = load i8*, i8** ").append(dataPtr).append("\n");

        // src->len
        String lenPtr = temps.newTemp();
        sb.append("  ").append(lenPtr)
                .append(" = getelementptr inbounds %String, %String* ")
                .append(srcStr).append(", i32 0, i32 1\n");

        String len = temps.newTemp();
        sb.append("  ").append(len)
                .append(" = load i64, i64* ").append(lenPtr).append("\n");

        // malloc new char buffer (len + 1)
        String size = temps.newTemp();
        sb.append("  ").append(size)
                .append(" = add i64 ").append(len).append(", 1\n");

        String raw = temps.newTemp();
        sb.append("  ").append(raw)
                .append(" = call i8* @malloc(i64 ").append(size).append(")\n");

        // memcpy
        sb.append("  call void @llvm.memcpy.p0i8.p0i8.i64(")
                .append("i8* ").append(raw).append(", ")
                .append("i8* ").append(chars).append(", ")
                .append("i64 ").append(size).append(", i1 false)\n");

        // create new String struct
        String newStr = temps.newTemp();
        sb.append("  ").append(newStr)
                .append(" = call %String* @createString(i8* ").append(raw).append(")\n");

        sb.append(";;VAL:").append(newStr).append(";;TYPE:%String*\n");
        return sb.toString();
    }
}
