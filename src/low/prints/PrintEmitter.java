package low.prints;

import ast.ASTNode;

import ast.lists.ListGetNode;
import low.lists.ListGetEmitter;
import low.main.GlobalStringManager;
import low.module.LLVisitorMain;
import ast.prints.PrintNode;
import ast.variables.LiteralNode;
import ast.variables.VariableNode;
public class PrintEmitter {
    private final GlobalStringManager globalStrings;

    public PrintEmitter(GlobalStringManager globalStrings) {
        this.globalStrings = globalStrings;
    }

    public String emit(PrintNode node, LLVisitorMain visitor) {
        ASTNode expr = node.expr;

        // String literal
        if (expr instanceof LiteralNode lit && lit.getValue().getType().equals("string")) {
            return emitStringLiteral((String) lit.getValue().getValue());
        }

        // Variáveis
        if (expr instanceof VariableNode varNode) {
            String varName = varNode.getName();
            String type = visitor.getVarType(varName);

            // String ou lista
            if ("i8*".equals(type)) {
                if (visitor.isList(varName)) {
                    return emitListVariable(varName, visitor);
                } else {
                    return emitStringVariable(varName, visitor);
                }
            }

            // int, double, boolean
            return emitPrimitiveVariable(varName, type, visitor);
        }

        // ListGet
        if (expr instanceof ListGetNode listGetNode) {
            ListGetEmitter listGetEmitter = new ListGetEmitter(visitor.getTemps());
            String llvmListGet = listGetEmitter.emit(listGetNode, visitor);
            String tmpDyn = extractTemp(llvmListGet);
            return llvmListGet + "  call void @printDynValue(%DynValue* " + tmpDyn + ")\n";
        }

        // LiteralNode não string ou qualquer expressão
        String exprLLVM = expr.accept(visitor);
        return emitPrimitiveOrExpr(exprLLVM, visitor);
    }

    private String emitStringLiteral(String value) {
        String strName = globalStrings.getOrCreateString(value);
        int len = value.length() + 2; // inclui \n e \0
        return "  call i32 (i8*, ...) @printf(i8* getelementptr ([" + len +
                " x i8], [" + len + " x i8]* " + strName + ", i32 0, i32 0))\n";
    }

    private String emitStringVariable(String varName, LLVisitorMain visitor) {
        String tmp = visitor.getTemps().newTemp();
        StringBuilder llvm = new StringBuilder();
        llvm.append("  ").append(tmp).append(" = load i8*, i8** %").append(varName).append(".addr\n");
        llvm.append("  call i32 (i8*, ...) @printf(i8* getelementptr ([4 x i8], [4 x i8]* @.strStr, i32 0, i32 0), i8* ").append(tmp).append(")\n");
        return llvm.toString();
    }

    private String emitListVariable(String varName, LLVisitorMain visitor) {
        String tmp = visitor.getTemps().newTemp();
        StringBuilder llvm = new StringBuilder();
        llvm.append("  ").append(tmp).append(" = load i8*, i8** %").append(varName).append(".addr\n");
        llvm.append("  call void @printList(i8* ").append(tmp).append(")\n");
        return llvm.toString();
    }

    private String emitPrimitiveVariable(String varName, String type, LLVisitorMain visitor) {
        String tmp = visitor.getTemps().newTemp();
        StringBuilder llvm = new StringBuilder();
        llvm.append("  ").append(tmp).append(" = load ").append(type).append(", ").append(type).append("* %").append(varName).append(".addr\n");
        llvm.append(emitPrimitive(tmp, type, visitor));
        return llvm.toString();
    }

    private String emitPrimitiveOrExpr(String exprLLVM, LLVisitorMain visitor) {
        int markerIdx = exprLLVM.lastIndexOf(";;VAL:");
        if (markerIdx == -1) return exprLLVM; // sem marcação, retorna como está

        String codePart = exprLLVM.substring(0, markerIdx);
        String valTypePart = exprLLVM.substring(markerIdx);

        String temp = extractTemp(valTypePart);
        String type = extractType(valTypePart);

        StringBuilder llvm = new StringBuilder();
        if (!codePart.isEmpty()) {
            if (!codePart.endsWith("\n")) codePart += "\n";
            llvm.append(codePart);
        }

        llvm.append(emitPrimitive(temp, type, visitor));
        return llvm.toString();
    }

    private String emitPrimitive(String temp, String type, LLVisitorMain visitor) {
        return switch (type) {
            case "i32" -> "  call i32 (i8*, ...) @printf(i8* getelementptr ([4 x i8], [4 x i8]* @.strInt, i32 0, i32 0), i32 " + temp + ")\n";
            case "double" -> "  call i32 (i8*, ...) @printf(i8* getelementptr ([4 x i8], [4 x i8]* @.strDouble, i32 0, i32 0), double " + temp + ")\n";
            case "i1" -> {
                String zextTmp = visitor.getTemps().newTemp();
                yield "  " + zextTmp + " = zext i1 " + temp + " to i32\n" +
                        "  call i32 (i8*, ...) @printf(i8* getelementptr ([4 x i8], [4 x i8]* @.strInt, i32 0, i32 0), i32 " + zextTmp + ")\n";
            }
            case "any" -> "  call void @printDynValue(%DynValue* " + temp + ")\n";
            default -> throw new RuntimeException("Tipo não suportado no print: " + type);
        };
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
