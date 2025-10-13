package low.prints;

import low.TempManager;
import low.module.LLVisitorMain;

public class ExprPrintHandler {
    private final TempManager temps;

    public ExprPrintHandler(TempManager temps) {
        this.temps = temps;
    }

    public String emitExprOrElement(String exprLLVM, LLVisitorMain visitor) {
        int markerIdx = exprLLVM.lastIndexOf(";;VAL:");
        if (markerIdx == -1) return exprLLVM;

        String codePart = exprLLVM.substring(0, markerIdx);
        String valTypePart = exprLLVM.substring(markerIdx);
        String temp = extractTemp(valTypePart);
        String type = extractType(valTypePart);

        StringBuilder llvm = new StringBuilder();
        if (!codePart.isEmpty()) {
            if (!codePart.endsWith("\n")) codePart += "\n";
            llvm.append(codePart);
        }

        switch (type) {
            case "i32" -> llvm.append("  call i32 (i8*, ...) @printf(i8* getelementptr ([4 x i8], [4 x i8]* @.strInt, i32 0, i32 0), i32 ").append(temp).append(")\n");
            case "double" -> llvm.append("  call i32 (i8*, ...) @printf(i8* getelementptr ([4 x i8], [4 x i8]* @.strDouble, i32 0, i32 0), double ").append(temp).append(")\n");
            case "i1" -> new PrimitivePrintHandler(temps).emitBoolPrint(llvm, temp);
            case "%String*" -> llvm.append("  call void @printString(%String* ").append(temp).append(")\n");
            case "i8*" -> llvm.append("  call i32 (i8*, ...) @printf(i8* getelementptr ([4 x i8], [4 x i8]* @.strStr, i32 0, i32 0), i8* ").append(temp).append(")\n");
            default -> throw new RuntimeException("Unsupported type in print: " + type);
        }

        return llvm.toString();
    }

    private String extractTemp(String valTypePart) {
        int v = valTypePart.indexOf(";;VAL:");
        int t = valTypePart.indexOf(";;TYPE:", v);
        return valTypePart.substring(v + 6, t).trim();
    }

    private String extractType(String valTypePart) {
        int t = valTypePart.indexOf(";;TYPE:");
        int end = valTypePart.indexOf("\n", t);
        if (end == -1) end = valTypePart.length();
        return valTypePart.substring(t + 7, end).trim();
    }
}