package low.prints;

import ast.ASTNode;
import ast.functions.FunctionCallNode;
import ast.lists.ListGetNode;
import ast.lists.ListNode;
import ast.lists.ListSizeNode;
import ast.prints.PrintNode;
import low.TempManager;
import low.lists.generics.ListGetEmitter;
import low.lists.generics.ListSizeEmitter;
import low.main.TypeInfos;
import low.module.LLVisitorMain;
public class ExprPrintHandler {
    private final TempManager temps;

    public ExprPrintHandler(TempManager temps) {
        this.temps = temps;
    }

    public String emitExprOrElement(String exprLLVM, LLVisitorMain visitor,
                                    ASTNode node, boolean newline) {

        String valTypeLine = findLastValTypeMarkerOfExpression(exprLLVM);
        if (valTypeLine == null) return exprLLVM;

        String temp = extractTemp(valTypeLine);
        String type = extractType(valTypeLine);

        int cutIndex = exprLLVM.lastIndexOf(valTypeLine);
        String codePart = (cutIndex >= 0) ? exprLLVM.substring(0, cutIndex) : exprLLVM;

        StringBuilder llvm = new StringBuilder();
        if (!codePart.isBlank()) {
            if (!codePart.endsWith("\n")) codePart += "\n";
            llvm.append(codePart);
        }

        switch (type) {

            case "i32" ->
                    appendPrintf(llvm, temp, newline, ".strInt", "i32");

            case "double" ->
                    appendPrintf(llvm, temp, newline, ".strDouble", "double");

            case "float" -> {
                String tmpExt = temps.newTemp();
                llvm.append("  ").append(tmpExt)
                        .append(" = fpext float ").append(temp).append(" to double\n")
                        .append(";;VAL:").append(tmpExt).append(";;TYPE:double\n");

                appendPrintf(llvm, tmpExt, newline, ".strFloat", "double");
            }

            case "i1" ->
                    new PrimitivePrintHandler(temps).emitBoolPrint(llvm, temp, newline);

            case "i8" -> {
                String castTmp = temps.newTemp();
                llvm.append("  ").append(castTmp)
                        .append(" = sext i8 ").append(temp).append(" to i32\n")
                        .append(";;VAL:").append(castTmp).append(";;TYPE:i32\n");

                appendPrintf(llvm, castTmp, newline, ".strChar", "i32");
            }

            case "%String*" -> {
                String fn = newline ? "@printString" : "@printString_noNL";
                llvm.append("  call void ").append(fn)
                        .append("(%String* ").append(temp).append(")\n");
            }

            case "i8*" -> {
                if (node instanceof FunctionCallNode callNode) {
                    TypeInfos fnType = visitor.getFunctionType(callNode.getName());
                    if (fnType != null && fnType.isList()) {
                        return new ListPrintHandler(temps)
                                .emit(node, visitor, newline);
                    }
                }
                appendPrintf(llvm, temp, newline, ".strStr", "i8*");
            }

            default -> {
                if (node instanceof ListSizeNode)
                    return new ListSizePrintHandler(
                            temps, new ListSizeEmitter(temps))
                            .emit(node, visitor, newline);

                if (type.startsWith("%struct.ArrayList"))
                    return new ListPrintHandler(temps)
                            .emit(node, visitor, newline);

                if (node instanceof FunctionCallNode callNode) {
                    TypeInfos fnType = visitor.getFunctionType(callNode.getName());
                    if (fnType != null && fnType.isList())
                        return new ListPrintHandler(temps)
                                .emit(node, visitor, newline);
                }

                if (node instanceof ListGetNode)
                    return new ListGetPrintHandler(
                            temps, new ListGetEmitter(temps))
                            .emit(node, visitor, newline);

                if (isStructLLVMType(type))
                    return new StructPrintHandler(temps)
                            .emit(node, visitor, newline);

                throw new RuntimeException(
                        "Unsupported type in print: " + type);
            }
        }

        return llvm.toString();
    }

    private void appendPrintf(StringBuilder llvm, String temp,
                              boolean newline, String strLabel,
                              String llvmType) {

        String label = newline ? strLabel : strLabel + "_noNL";

        llvm.append("  call i32 (i8*, ...) @printf(")
                .append("i8* getelementptr ([")
                .append(newline ? "4 x i8" : "3 x i8")
                .append("], [")
                .append(newline ? "4 x i8" : "3 x i8")
                .append("]* @").append(label)
                .append(", i32 0, i32 0), ")
                .append(llvmType).append(" ").append(temp).append(")\n");
    }

    /* ================= helpers ================= */

    private String findLastValTypeMarkerOfExpression(String exprLLVM) {
        String[] lines = exprLLVM.split("\n");
        for (int i = lines.length - 1; i >= 0; i--) {
            String line = lines[i].trim();
            if (line.startsWith(";;VAL:") && line.contains(";;TYPE:"))
                return line;
        }
        return null;
    }

    private String extractTemp(String valTypeLine) {
        int v = valTypeLine.indexOf(";;VAL:");
        int t = valTypeLine.indexOf(";;TYPE:", v);
        return valTypeLine.substring(v + 6, t).trim();
    }

    private String extractType(String valTypeLine) {
        int t = valTypeLine.indexOf(";;TYPE:");
        return valTypeLine.substring(t + 7).trim();
    }

    private boolean isStructLLVMType(String llvmType) {
        if (llvmType == null) return false;
        String t = llvmType.trim();
        while (t.endsWith("*")) t = t.substring(0, t.length() - 1);
        if (t.startsWith("%")) t = t.substring(1);
        return !t.isEmpty()
                && Character.isUpperCase(t.charAt(0))
                && !t.equals("String");
    }
}
