package low;

import ast.ASTNode;
import home.MainAST;
import prints.PrintNode;
import variables.LiteralNode;
import variables.VariableDeclarationNode;
import variables.VariableNode;

import java.util.HashMap;
import java.util.Map;
public class LLVisitorMain implements LLVMEmitVisitor {
    private final Map<String, String> varTypes = new HashMap<>();
    private final TempManager temps = new TempManager();
    private final GlobalStringManager globalStrings = new GlobalStringManager(temps);
    private final VariableEmitter varEmitter = new VariableEmitter(varTypes, temps);
    private final PrintEmitter printEmitter = new PrintEmitter(globalStrings);

    private final StringBuilder llvmHeader = new StringBuilder();

    public LLVisitorMain() {
        llvmHeader.append("declare i32 @printf(i8*, ...)\n");
        llvmHeader.append("@.strInt = private constant [4 x i8] c\"%d\\0A\\00\"\n");
        llvmHeader.append("@.strDouble = private constant [4 x i8] c\"%f\\0A\\00\"\n");
    }

    @Override
    public String visit(MainAST node) {
        // coleta strings globais
        for (ASTNode stmt : node.body) {
            if (stmt instanceof PrintNode) stmt.accept(this);
        }

        StringBuilder llvm = new StringBuilder();
        llvm.append(llvmHeader).append("\n");
        llvm.append(globalStrings.getGlobalStrings()).append("\n");

        llvm.append("define i32 @main() {\n");
        for (ASTNode stmt : node.body) {
            llvm.append("  ; ").append(stmt.getClass().getSimpleName()).append("\n");
            llvm.append(stmt.accept(this));
        }
        llvm.append("  ret i32 0\n}\n");

        return llvm.toString();
    }

    // Aqui só delega:
    @Override
    public String visit(VariableDeclarationNode node) {
        String init = node.initializer != null ? node.initializer.accept(this) : null;
        return varEmitter.emitDeclaration(node.getName(), node.getType(), init);
    }

    @Override
    public String visit(VariableNode node) {
        return varEmitter.emitLoad(node.getName());
    }

    @Override
    public String visit(LiteralNode node) {
        Object val = node.value.getValue();
        if (val instanceof Integer || val instanceof Double) return val.toString();
        if (val instanceof String) return (String) val;
        return "0";
    }

    @Override
    public String visit(PrintNode node) {
        if (node.expr instanceof LiteralNode lit && lit.value.getType().equals("string")) {
            return printEmitter.emitString((String) lit.value.getValue());
        }

        String valueResult = node.expr.accept(this);
        String[] valSplit = valueResult.split(";;VAL:");
        String code = valSplit[0];
        String value = valSplit.length > 1 ? valSplit[1].split(";;TYPE:")[0].trim() : valSplit[0].trim();

        String type = "i32";
        if (valueResult.contains(";;TYPE:")) type = valueResult.split(";;TYPE:")[1].trim();
        else if (node.expr instanceof LiteralNode lit2 && lit2.value.getType().equals("double")) type = "double";

        return printEmitter.emitNumber(code, value, type);
    }
}
