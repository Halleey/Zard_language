package low.prints;

import ast.ASTNode;
import low.main.GlobalStringManager;
import low.module.LLVisitorMain;
import prints.PrintNode;
import variables.LiteralNode;
import variables.VariableNode;
public class PrintEmitter {
    private final GlobalStringManager globalStrings;

    public PrintEmitter(GlobalStringManager globalStrings) {
        this.globalStrings = globalStrings;
    }

    // Método principal: recebe o node e o visitor
    public String emit(PrintNode node, LLVisitorMain visitor) {
        ASTNode expr = node.expr;

        // Literal de string
        if (expr instanceof LiteralNode lit && lit.value.getType().equals("string")) {
            return emitString((String) lit.value.getValue());
        }

        // Variável string (i8*)
        if (expr instanceof VariableNode varNode) {
            String type = visitor.getVarType(varNode.getName());
            if ("i8*".equals(type)) {
                return emitStringVariable(varNode.getName());
            }
        }

        // Números, booleanos ou expressões complexas
        String exprLLVM = expr.accept(visitor); // usa visitor
        return emitNumber(exprLLVM);
    }

    public String emitString(String str) {
        String strName = globalStrings.getOrCreateString(str);
        int len = str.length() + 2;
        return "  call i32 (i8*, ...) @printf(i8* getelementptr ([" + len +
                " x i8], [" + len + " x i8]* " + strName + ", i32 0, i32 0))\n";
    }

    public String emitStringVariable(String varName) {
        String tmp = "%tStr" + System.nanoTime();
        return "  " + tmp + " = load i8*, i8** %" + varName + "\n" +
                "  call i32 (i8*, ...) @printf(i8* getelementptr ([4 x i8], [4 x i8]* @.strStr, i32 0, i32 0), i8* " + tmp + ")\n";
    }

    public String emitNumber(String llvmCode) {
        String[] parts = llvmCode.split(";;VAL:");
        String code = parts[0];
        String[] valType = parts[1].split(";;TYPE:");
        String value = valType[0].trim();
        String type = valType[1].trim();

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
