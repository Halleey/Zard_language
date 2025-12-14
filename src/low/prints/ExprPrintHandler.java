package low.prints;

import ast.ASTNode;
import ast.functions.FunctionCallNode;
import ast.lists.ListGetNode;
import ast.lists.ListNode;
import ast.lists.ListSizeNode;
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

    public String emitExprOrElement(String exprLLVM, LLVisitorMain visitor, ASTNode node) {

        String valTypeLine = findLastValTypeMarkerOfExpression(exprLLVM);
        if (valTypeLine == null) {
            return exprLLVM;
        }

        String temp = extractTemp(valTypeLine);
        String type = extractType(valTypeLine);

        int cutIndex = exprLLVM.lastIndexOf(valTypeLine);
        String codePart = (cutIndex >= 0) ? exprLLVM.substring(0, cutIndex) : exprLLVM;

        StringBuilder llvm = new StringBuilder();
        if (!codePart.isBlank()) {
            if (!codePart.endsWith("\n")) {
                codePart += "\n";
            }
            llvm.append(codePart);
        }

        switch (type) {

            case "i32" -> {
                llvm.append("  call i32 (i8*, ...) @printf(")
                        .append("i8* getelementptr ([4 x i8], [4 x i8]* @.strInt, i32 0, i32 0), ")
                        .append("i32 ").append(temp).append(")\n");
                return llvm.toString();
            }

            case "double" -> {
                llvm.append("  call i32 (i8*, ...) @printf(")
                        .append("i8* getelementptr ([4 x i8], [4 x i8]* @.strDouble, i32 0, i32 0), ")
                        .append("double ").append(temp).append(")\n");
                return llvm.toString();
            }

            case "float" -> {
                String tmpExt = temps.newTemp();
                llvm.append("  ").append(tmpExt)
                        .append(" = fpext float ").append(temp).append(" to double\n")
                        .append(";;VAL:").append(tmpExt).append(";;TYPE:double\n");
                llvm.append("  call i32 (i8*, ...) @printf(")
                        .append("i8* getelementptr ([4 x i8], [4 x i8]* @.strFloat, i32 0, i32 0), ")
                        .append("double ").append(tmpExt).append(")\n");
                return llvm.toString();
            }

            case "i1" -> {
                new PrimitivePrintHandler(temps).emitBoolPrint(llvm, temp);
                return llvm.toString();
            }

            case "%String*" -> {
                llvm.append("  call void @printString(%String* ").append(temp).append(")\n");
                return llvm.toString();
            }

            case "i8" -> {
                String castTmp = temps.newTemp();
                llvm.append("  ").append(castTmp)
                        .append(" = sext i8 ").append(temp).append(" to i32\n");
                llvm.append("  call i32 (i8*, ...) @printf(")
                        .append("i8* getelementptr ([3 x i8], [3 x i8]* @.strChar, i32 0, i32 0), ")
                        .append("i32 ").append(castTmp).append(")\n");
                return llvm.toString();
            }

            case "i8*" -> {
                if (node instanceof FunctionCallNode callNode) {
                    TypeInfos fnType = visitor.getFunctionType(callNode.getName());
                    if (fnType != null && fnType.isList()) {
                        return new ListPrintHandler(temps).emit(node, visitor);
                    }
                }

                llvm.append("  call i32 (i8*, ...) @printf(")
                        .append("i8* getelementptr ([4 x i8], [4 x i8]* @.strStr, i32 0, i32 0), ")
                        .append("i8* ").append(temp).append(")\n");
                return llvm.toString();
            }

            default -> {
                if (node instanceof ListSizeNode) {
                    ListSizePrintHandler handler =
                            new ListSizePrintHandler(temps, new ListSizeEmitter(temps));
                    return handler.emit(node, visitor);
                }

                if (type.startsWith("%struct.ArrayList")) {
                    ListPrintHandler handler = new ListPrintHandler(temps);
                    return handler.emit(node, visitor);
                }

                if (node instanceof FunctionCallNode callNode) {
                    TypeInfos fnType = visitor.getFunctionType(callNode.getName());
                    if (fnType != null && fnType.isList()) {
                        return new ListPrintHandler(temps).emit(node, visitor);
                    }
                }

                if (node instanceof ListGetNode) {
                    ListGetPrintHandler handler =
                            new ListGetPrintHandler(temps, new ListGetEmitter(temps));
                    return handler.emit(node, visitor);
                }

                if (node instanceof ListNode) {
                    ListPrintHandler handler = new ListPrintHandler(temps);
                    return handler.emit(node, visitor);
                }

                if (isStructLLVMType(type)) {
                    return new StructPrintHandler(temps).emit(node, visitor);
                }

                throw new RuntimeException("Unsupported type in print: " + type);
            }
        }
    }

    private String findLastValTypeMarkerOfExpression(String exprLLVM) {
        String[] lines = exprLLVM.split("\n");
        for (int i = lines.length - 1; i >= 0; i--) {
            String line = lines[i].trim();
            if (line.startsWith(";;VAL:") && line.contains(";;TYPE:")) {
                return line;
            }
        }
        return null;
    }

    private String extractTemp(String valTypeLine) {
        int v = valTypeLine.indexOf(";;VAL:");
        int t = valTypeLine.indexOf(";;TYPE:", v);
        if (v == -1 || t == -1) return "";
        return valTypeLine.substring(v + 6, t).trim();
    }

    private String extractType(String valTypeLine) {
        int t = valTypeLine.indexOf(";;TYPE:");
        if (t == -1) return "";
        int end = valTypeLine.indexOf("\n", t);
        if (end == -1) end = valTypeLine.length();
        return valTypeLine.substring(t + 7, end).trim();
    }

    private boolean isStructLLVMType(String llvmType) {
        if (llvmType == null) return false;
        String t = llvmType.trim();

        while (t.endsWith("*")) {
            t = t.substring(0, t.length() - 1);
        }
        if (t.startsWith("%")) {
            t = t.substring(1);
        }

        if (t.equals("i32") || t.equals("i1") || t.equals("i8") ||
                t.equals("double") || t.equals("String") || t.equals("String*")) {
            return false;
        }

        return t.startsWith("Struct") || (!t.isEmpty() && Character.isUpperCase(t.charAt(0)));
    }
}
