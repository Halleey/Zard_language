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

        // Literal string
        if (expr instanceof LiteralNode lit && lit.value.getType().equals("string")) {
            return emitStringLiteral((String) lit.value.getValue(), visitor);
        }

        // Variável
        if (expr instanceof VariableNode varNode) {
            String varName = varNode.getName();
            String type = visitor.getVarType(varName);

            if ("%String".equals(type) || "%String*".equals(type)) {
                return emitStringVariable(varName, visitor);
            } else if ("i8*".equals(type)) { // listas são representadas como i8*
                return emitListVariable(varName, visitor);
            } else {
                return emitPrimitiveVariable(varName, type, visitor);
            }
        }

        // Qualquer outra expressão
        String exprLLVM = expr.accept(visitor);
        return emitPrimitiveOrExpr(exprLLVM, visitor);
    }

    // --- Literais ---
    private String emitStringLiteral(String value, LLVisitorMain visitor) {
        String strName = globalStrings.getOrCreateString(value);
        int len = value.length() + 1; // inclui \0
        String tmp = temps.newTemp();
        return "  " + tmp + " = getelementptr inbounds [" + len + " x i8], [" + len + " x i8]* "
                + strName + ", i32 0, i32 0\n" +
                "  call i32 (i8*, ...) @printf(i8* getelementptr ([4 x i8], [4 x i8]* @.strStr, i32 0, i32 0), i8* " + tmp + ")\n";
    }

    // --- Variáveis do tipo %String ---
    private String emitStringVariable(String varName, LLVisitorMain visitor) {
        String tmpPtr = temps.newTemp();
        String tmpLoad = temps.newTemp();
        StringBuilder sb = new StringBuilder();

        sb.append("  ").append(tmpPtr)
                .append(" = getelementptr inbounds %String, %String* %").append(varName).append(", i32 0, i32 0\n");
        sb.append("  ").append(tmpLoad)
                .append(" = load i8*, i8** ").append(tmpPtr).append("\n");
        sb.append("  call i32 (i8*, ...) @printf(i8* getelementptr ([4 x i8], [4 x i8]* @.strStr, i32 0, i32 0), i8* ").append(tmpLoad).append(")\n");

        return sb.toString();
    }

    // --- Variáveis primitivas ---
    private String emitPrimitiveVariable(String varName, String type, LLVisitorMain visitor) {
        String tmp = temps.newTemp();
        StringBuilder sb = new StringBuilder();
        sb.append("  ").append(tmp).append(" = load ").append(type).append(", ").append(type).append("* %").append(varName).append("\n");

        switch (type) {
            case "i32" ->
                    sb.append("  call i32 (i8*, ...) @printf(i8* getelementptr ([4 x i8], [4 x i8]* @.strInt, i32 0, i32 0), i32 ").append(tmp).append(")\n");
            case "double" ->
                    sb.append("  call i32 (i8*, ...) @printf(i8* getelementptr ([4 x i8], [4 x i8]* @.strDouble, i32 0, i32 0), double ").append(tmp).append(")\n");
            case "i1" -> {
                String zextTmp = temps.newTemp();
                sb.append("  ").append(zextTmp).append(" = zext i1 ").append(tmp).append(" to i32\n");
                sb.append("  call i32 (i8*, ...) @printf(i8* getelementptr ([4 x i8], [4 x i8]* @.strInt, i32 0, i32 0), i32 ").append(zextTmp).append(")\n");
            }
            default -> throw new RuntimeException("Tipo não suportado no print: " + type);
        }

        return sb.toString();
    }

    // --- Variáveis do tipo lista ---
    private String emitListVariable(String varName, LLVisitorMain visitor) {
        String tmpList = temps.newTemp();
        StringBuilder sb = new StringBuilder();

        sb.append("  ").append(tmpList)
                .append(" = load i8*, i8** %").append(varName).append("\n");

        String elemType = visitor.getListElementType(varName);
        if (elemType == null) throw new RuntimeException("Elemento de lista não registrado para: " + varName);

        String printFunc;
        switch (elemType) {
            case "int" -> printFunc = "@arraylist_print_int";
            case "double" -> printFunc = "@arraylist_print_double";
            case "string" -> printFunc = "@arraylist_print_string";
            default -> throw new RuntimeException("Tipo não suportado para print de lista: " + elemType);
        }

        sb.append("  call void ").append(printFunc).append("(i8* ").append(tmpList).append(")\n");
        return sb.toString();
    }

    // --- Expressões ---
    private String emitPrimitiveOrExpr(String exprLLVM, LLVisitorMain visitor) {
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
            case "i32" ->
                    llvm.append("  call i32 (i8*, ...) @printf(i8* getelementptr ([4 x i8], [4 x i8]* @.strInt, i32 0, i32 0), i32 ").append(temp).append(")\n");
            case "double" ->
                    llvm.append("  call i32 (i8*, ...) @printf(i8* getelementptr ([4 x i8], [4 x i8]* @.strDouble, i32 0, i32 0), double ").append(temp).append(")\n");
            case "i1" -> {
                String zextTmp = temps.newTemp();
                llvm.append("  ").append(zextTmp).append(" = zext i1 ").append(temp).append(" to i32\n");
                llvm.append("  call i32 (i8*, ...) @printf(i8* getelementptr ([4 x i8], [4 x i8]* @.strInt, i32 0, i32 0), i32 ").append(zextTmp).append(")\n");
            }
            case "i32*", "double*", "%String*" -> {
                String printFunc;
                switch (type) {
                    case "i32*" -> printFunc = "@arraylist_print_int";
                    case "double*" -> printFunc = "@arraylist_print_double";
                    default -> printFunc = "@arraylist_print_string";
                }
                llvm.append("  call void ").append(printFunc).append("(i8* ").append(temp).append(")\n");
            }
            default -> throw new RuntimeException("Tipo não suportado no print: " + type);
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
