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

    public PrintEmitter(GlobalStringManager globalStrings, TempManager temps) {
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
        boolean newline = node.newline;

        for (PrintHandler handler : handlers) {
            if (handler.canHandle(node.expr, visitor)) {
                return handler.emit(node.expr, visitor, newline);
            }
        }
        // fallback para expressões complexas
        String exprLLVM = node.expr.accept(visitor);
        return exprHandler.emitExprOrElement(exprLLVM, visitor, node.expr, newline);
    }
}
