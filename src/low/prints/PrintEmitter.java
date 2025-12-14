package low.prints;


import low.TempManager;
import low.lists.generics.ListGetEmitter;
import low.main.GlobalStringManager;
import low.module.LLVisitorMain;
import ast.prints.PrintNode;


import java.util.List;
public class PrintEmitter {
    private final List<PrintHandler> handlers;
    private final ExprPrintHandler exprHandler;
    private final TempManager temps;

    public PrintEmitter(GlobalStringManager globalStrings, TempManager temps) {
        this.temps = temps;

        ListGetEmitter listGetEmitter = new ListGetEmitter(temps);

        handlers = List.of(
                new StringLiteralPrintHandler(globalStrings, temps),
                new StringVariablePrintHandler(temps),
                new PrimitivePrintHandler(temps),
                new ListGetPrintHandler(temps, listGetEmitter),
                new ListPrintHandler(temps),
                new StructPrintHandler(temps)
        );

        exprHandler = new ExprPrintHandler(temps);
    }

    public String emit(PrintNode node, LLVisitorMain visitor) {

        String code;

        for (PrintHandler handler : handlers) {
            if (handler.canHandle(node.expr, visitor)) {
                code = handler.emit(node.expr, visitor);
                return finalizePrint(code, node.newline);
            }
        }

        // fallback para expressões complexas
        String exprLLVM = node.expr.accept(visitor);
        code = exprHandler.emitExprOrElement(exprLLVM, visitor, node.expr);
        return finalizePrint(code, node.newline);
    }

    private String finalizePrint(String code, boolean newline) {
        if (!newline) return code;

        return code +
                "  call i32 (i8*, ...) @printf(" +
                "i8* getelementptr ([2 x i8], [2 x i8]* @.strNewLine, i32 0, i32 0))\n";
    }
}
