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


    /*
    need fix -> list print emitter
     if -> flag for Struct -> true
     condition will be evaluated in i8 *
     */

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
                llvm.append("  ").append(castTmp).append(" = sext i8 ").append(temp).append(" to i32\n");
                llvm.append("  call i32 (i8*, ...) @printf(")
                        .append("i8* getelementptr ([3 x i8], [3 x i8]* @.strChar, i32 0, i32 0), ")
                        .append("i32 ").append(castTmp).append(")\n");
                return llvm.toString();
            }

            case "i8*" -> {
                // se for chamada de função, checar tipo de retorno
                if (node instanceof FunctionCallNode callNode) {
                    TypeInfos fnType = visitor.getFunctionType(callNode.getName());
                    if (fnType != null && fnType.isList()) {
                        // imprime como lista
                        return new ListPrintHandler(temps).emit(node, visitor);
                    }
                }

                // fallback: tratar como string normal
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

                if(type.startsWith("%struct.ArrayList")) {

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

        return t.startsWith("Struct") || (!t.isEmpty() && Character.isUpperCase(t.charAt(0)));
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
