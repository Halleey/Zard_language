package low;

public class PrintEmitter {
    private final GlobalStringManager globalStrings;

    public PrintEmitter(GlobalStringManager globalStrings) {
        this.globalStrings = globalStrings;
    }

    // Para literais de string
    public String emitString(String str) {
        String strName = globalStrings.getOrCreateString(str);
        int len = str.length() + 2;
        return "  call i32 (i8*, ...) @printf(i8* getelementptr ([" + len +
                " x i8], [" + len + " x i8]* " + strName + ", i32 0, i32 0))\n";
    }

    // Para variáveis string (i8*)
    public String emitStringVariable(String varName) {
        String tmp = "%tStr" + System.nanoTime();
        return "  " + tmp + " = load i8*, i8** %" + varName + "\n" +
                "  call i32 (i8*, ...) @printf(i8* getelementptr ([4 x i8], [4 x i8]* @.strStr, i32 0, i32 0), i8* " + tmp + ")\n";
    }

    // Para números e booleanos
    public String emitNumber(String code, String value, String type) {
        StringBuilder llvm = new StringBuilder(code);

        switch (type) {
            case "i32" -> llvm.append("  call i32 (i8*, ...) @printf(i8* getelementptr ([4 x i8], [4 x i8]* @.strInt, i32 0, i32 0), i32 ").append(value).append(")\n");
            case "double" -> llvm.append("  call i32 (i8*, ...) @printf(i8* getelementptr ([4 x i8], [4 x i8]* @.strDouble, i32 0, i32 0), double ").append(value).append(")\n");
            case "i1" -> {
                String tmp = "%tBool" + System.nanoTime();
                llvm.append("  ").append(tmp).append(" = zext i1 ").append(value).append(" to i32\n");
                llvm.append("  call i32 (i8*, ...) @printf(i8* getelementptr ([4 x i8], [4 x i8]* @.strInt, i32 0, i32 0), i32 ").append(tmp).append(")\n");
            }
        }

        return llvm.toString();
    }
}
