package low.prints;

import ast.ASTNode;
import ast.variables.LiteralNode;
import low.TempManager;
import low.main.GlobalStringManager;
import low.module.LLVisitorMain;


public class StringLiteralPrintHandler implements PrintHandler {
    private final GlobalStringManager stringManager;
    private final TempManager tempManager;

    public StringLiteralPrintHandler(GlobalStringManager stringManager, TempManager tempManager) {
        this.stringManager = stringManager;
        this.tempManager = tempManager;
    }

    @Override
    public boolean canHandle(ASTNode node, LLVisitorMain visitor) {
        return node instanceof LiteralNode lit && "string".equals(lit.value.type());
    }

    @Override
    public String emit(ASTNode node, LLVisitorMain visitor, boolean newline) {
        LiteralNode lit = (LiteralNode) node;
        String value = (String) lit.value.value();
        String strName = stringManager.getOrCreateString(value);
        int len = value.length() + 1;
        String tmp = tempManager.newTemp();

        String label = newline ? ".strStr" : ".strStr_noNL";

        return "  " + tmp + " = getelementptr inbounds [" + len + " x i8], [" + len + " x i8]* "
                + strName + ", i32 0, i32 0\n" +
                "  call i32 (i8*, ...) @printf(i8* getelementptr ([4 x i8], [4 x i8]* @"
                + label + ", i32 0, i32 0), i8* " + tmp + ")\n";
    }
}
