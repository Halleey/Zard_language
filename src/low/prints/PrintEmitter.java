package low.prints;

import ast.ASTNode;
import low.TempManager;
import low.main.GlobalStringManager;
import low.module.LLVisitorMain;
import ast.prints.PrintNode;
import ast.variables.LiteralNode;
import ast.variables.VariableNode;

public class PrintEmitter {
    private final GlobalStringManager globalStrings;
    private final TempManager temps;

    public PrintEmitter(GlobalStringManager globalStrings, TempManager temps) {
        this.globalStrings = globalStrings;
        this.temps = temps;
    }

    public String emit(PrintNode node, LLVisitorMain visitor) {
        ASTNode expr = node.expr;

        if (expr instanceof LiteralNode lit && lit.value.getType().equals("string")) {
            return emitStringLiteral((String) lit.value.getValue());
        }

        if (expr instanceof VariableNode varNode) {
            String varName = varNode.getName();
            String type = visitor.getVarType(varName);

            if ("%String".equals(type) || "%String*".equals(type)) {
                return emitStringVariable(varName);
            }

            // Listas tipadas
            String elemType = visitor.getListElementType(varName);
            if (elemType != null) {
                return emitFullList(varName, elemType, type);
            }

            // Variáveis primitivas
            return emitPrimitiveVariable(varName, type);
        }

        // Qualquer outra expressão
        String exprLLVM = expr.accept(visitor);
        return emitExprOrElement(exprLLVM, visitor);
    }

    private String emitStringLiteral(String value) {
        String strName = globalStrings.getOrCreateString(value);
        int len = value.length() + 1;
        String tmp = temps.newTemp();
        return "  " + tmp + " = getelementptr inbounds [" + len + " x i8], [" + len + " x i8]* "
                + strName + ", i32 0, i32 0\n" +
                "  call i32 (i8*, ...) @printf(i8* getelementptr ([4 x i8], [4 x i8]* @.strStr, i32 0, i32 0), i8* " + tmp + ")\n";
    }


    private String emitStringVariable(String varName) {
        String tmpLoad = temps.newTemp();
        StringBuilder sb = new StringBuilder();
        sb.append("  ").append(tmpLoad)
                .append(" = load %String*, %String** %").append(varName).append("\n");
        sb.append("  call void @printString(%String* ").append(tmpLoad).append(")\n");
        return sb.toString();
    }

    private String emitPrimitiveVariable(String varName, String type) {
        String tmp = temps.newTemp();
        StringBuilder sb = new StringBuilder();
        sb.append("  ").append(tmp)
                .append(" = load ").append(type).append(", ").append(type).append("* %").append(varName).append("\n");

        switch (type) {
            case "i32" -> sb.append("  call i32 (i8*, ...) @printf(i8* getelementptr ([4 x i8], [4 x i8]* @.strInt, i32 0, i32 0), i32 ").append(tmp).append(")\n");
            case "double" -> sb.append("  call i32 (i8*, ...) @printf(i8* getelementptr ([4 x i8], [4 x i8]* @.strDouble, i32 0, i32 0), double ").append(tmp).append(")\n");
            case "i1" -> {
                String zextTmp = temps.newTemp();
                sb.append("  ").append(zextTmp).append(" = zext i1 ").append(tmp).append(" to i32\n");
                sb.append("  call i32 (i8*, ...) @printf(i8* getelementptr ([4 x i8], [4 x i8]* @.strInt, i32 0, i32 0), i32 ").append(zextTmp).append(")\n");
            }
            default -> throw new RuntimeException("Unsupported primitive type: " + type);
        }
        return sb.toString();
    }

    private String emitFullList(String varName, String elemType, String llvmType) {
        String tmp = temps.newTemp();
        StringBuilder sb = new StringBuilder();

        if ("%struct.ArrayListInt*".equals(llvmType)) {
            // Lista de int tipada
            sb.append("  ").append(tmp).append(" = load %struct.ArrayListInt*, %struct.ArrayListInt** %").append(varName).append("\n");
            sb.append("  call void @arraylist_print_int(%struct.ArrayListInt* ").append(tmp).append(")\n");
            return sb.toString();
        }
        if ("%struct.ArrayListDouble*".equals(llvmType)) {
            // Lista de int tipada
            sb.append("  ").append(tmp).append(" = load %struct.ArrayListDouble*, %struct.ArrayListDouble** %").append(varName).append("\n");
            sb.append("  call void @arraylist_print_double(%struct.ArrayListDouble* ").append(tmp).append(")\n");
            return sb.toString();
        }

        // Genéricas: i8* -> %ArrayList*
        sb.append("  ").append(tmp).append(" = load i8*, i8** %").append(varName).append("\n");
        String tmpCast = temps.newTemp();
        sb.append("  ").append(tmpCast).append(" = bitcast i8* ").append(tmp).append(" to %ArrayList*\n");

        String printFunc;
        if (elemType.equals("string")) {
            printFunc = "@arraylist_print_string";
        } else {
            throw new RuntimeException("Unsupported list element type: " + elemType);
        }
        sb.append("  call void ").append(printFunc).append("(%ArrayList* ").append(tmpCast).append(")\n");
        return sb.toString();
    }

    private String emitExprOrElement(String exprLLVM, LLVisitorMain visitor) {
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
            case "i1" -> {
                String zextTmp = temps.newTemp();
                llvm.append("  ").append(zextTmp).append(" = zext i1 ").append(temp).append(" to i32\n");
                llvm.append("  call i32 (i8*, ...) @printf(i8* getelementptr ([4 x i8], [4 x i8]* @.strInt, i32 0, i32 0), i32 ").append(zextTmp).append(")\n");
            }
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
