package low;

public class PrintEmitter {
    private final GlobalStringManager globalStrings;

    public PrintEmitter(GlobalStringManager globalStrings) {
        this.globalStrings = globalStrings;
    }

    public String emitString(String str) {
        String strName = globalStrings.getOrCreateString(str);
        int len = str.length() + 2;
        return "  call i32 (i8*, ...) @printf(i8* getelementptr ([" + len +
                " x i8], [" + len + " x i8]* " + strName + ", i32 0, i32 0))\n";
    }

    public String emitNumber(String code, String value, String type) {
        StringBuilder llvm = new StringBuilder(code);
        if (type.equals("i32")) {
            llvm.append("  call i32 (i8*, ...) @printf(i8* getelementptr ([4 x i8], " +
                    "[4 x i8]* @.strInt, i32 0, i32 0), i32 ").append(value).append(")\n");
        } else if (type.equals("double")) {
            llvm.append("  call i32 (i8*, ...) @printf(i8* getelementptr ([4 x i8], " +
                    "[4 x i8]* @.strDouble, i32 0, i32 0), double ").append(value).append(")\n");
        }
        return llvm.toString();
    }
}