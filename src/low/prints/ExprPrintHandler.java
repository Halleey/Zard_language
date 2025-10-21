package low.prints;

import ast.ASTNode;
import ast.lists.ListGetNode;
import ast.lists.ListNode;
import low.TempManager;
import low.lists.generics.ListGetEmitter;
import low.module.LLVisitorMain;


public class ExprPrintHandler {
    private final TempManager temps;


    public ExprPrintHandler(TempManager temps) {
        this.temps = temps;
    }
    public String emitExprOrElement(String exprLLVM, LLVisitorMain visitor, ASTNode node) {
        int markerIdx = exprLLVM.lastIndexOf(";;VAL:");
        if (markerIdx == -1) {
            return exprLLVM;
        }

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
            case "i1" -> {
                new PrimitivePrintHandler(temps).emitBoolPrint(llvm, temp);
                return llvm.toString();
            }
            case "%String*" -> {
                llvm.append("  call void @printString(%String* ").append(temp).append(")\n");
                return llvm.toString();
            }
            case "i8*" -> {
                llvm.append("  call i32 (i8*, ...) @printf(")
                        .append("i8* getelementptr ([4 x i8], [4 x i8]* @.strStr, i32 0, i32 0), ")
                        .append("i8* ").append(temp).append(")\n");
                return llvm.toString();
            }
            default -> {

                if (node instanceof ListGetNode) {
                    ListGetPrintHandler handler =
                            new ListGetPrintHandler(temps, new ListGetEmitter(temps));
                    return handler.emit(node, visitor);
                }
                if(node instanceof ListNode) {
                    ListPrintHandler handler = new ListPrintHandler(temps);
                    return handler.emit(node, visitor);
                }

                if (isStructLLVMType(type)) {
                    String structName = toStructName(type);
                    String structNameSym = structName.replace('.', '_').replace(' ', '_');

                    String cast = temps.newTemp();
                    llvm.append("  ")
                            .append(cast)
                            .append(" = bitcast ")
                            .append(type).append(" ").append(temp)
                            .append(" to i8*\n");

                    llvm.append("  call void @print_")
                            .append(structNameSym)
                            .append("(i8* ")
                            .append(cast)
                            .append(")\n");

                    return llvm.toString();
                }

                throw new RuntimeException("Unsupported type in print: " + type);
            }
        }
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

    private boolean isStructLLVMType(String llvmType) {
        if (llvmType == null) return false;
        String t = llvmType.trim();

        if (t.endsWith("*")) t = t.substring(0, t.length() - 1);
        if (t.startsWith("%")) t = t.substring(1);

        if (t.equals("i32") || t.equals("i1") || t.equals("i8") || t.equals("double") || t.equals("String") || t.equals("String*"))
            return false;

        return t.startsWith("Struct") || (t.length() > 0 && Character.isUpperCase(t.charAt(0)));
    }


    private String toStructName(String llvmType) {
        String s = llvmType.trim();
        if (s.endsWith("*")) s = s.substring(0, s.length() - 1);

        if (s.startsWith("%")) s = s.substring(1);

        if (s.startsWith("Struct<") && s.endsWith(">")) {
            s = s.substring("Struct<".length(), s.length() - 1).trim();
        } else if (s.startsWith("Struct.")) {
            s = s.substring("Struct.".length()).trim();
        } else if (s.startsWith("Struct ")) {
            s = s.substring("Struct ".length()).trim();
        }

        return s;
    }
}
