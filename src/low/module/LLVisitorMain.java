package low.module;

import ast.exceptions.BreakNode;
import ast.functions.FunctionNode;
import ast.home.MainAST;
import ast.ifstatements.IfNode;
import ast.lists.*;
import ast.loops.WhileNode;
import low.ifs.IfEmitter;
import low.lists.*;
import low.main.GlobalStringManager;
import low.TempManager;
import low.main.MainEmitter;
import low.prints.PrintEmitter;
import low.variables.*;
import low.whiles.WhileEmitter;
import ast.prints.PrintNode;
import ast.variables.*;

import java.util.*;

public class LLVisitorMain implements LLVMEmitVisitor {
    private final Map<String, String> varTypes = new HashMap<>();

    private final TempManager temps = new TempManager();

    private final GlobalStringManager globalStrings = new GlobalStringManager();
    private final VariableEmitter varEmitter = new VariableEmitter(varTypes, temps, globalStrings, this);
    private final PrintEmitter printEmitter = new PrintEmitter(globalStrings);
    private final AssignmentEmitter assignmentEmitter = new AssignmentEmitter(varTypes, temps, globalStrings, this);
    private final UnaryOpEmitter unaryOpEmitter = new UnaryOpEmitter(varTypes, temps);
    private final LiteralEmitter literalEmitter = new LiteralEmitter(temps,globalStrings);
    private final BinaryOpEmitter binaryEmitter = new BinaryOpEmitter(temps, this);
    private final IfEmitter ifEmitter = new IfEmitter(temps, this);
    private final WhileEmitter whileEmitter = new WhileEmitter(temps, this);
    private final ListEmitter listEmitter = new ListEmitter(temps, globalStrings);
    private final Deque<String> loopEndLabels = new ArrayDeque<>();
    private final ListAddEmitter listAddEmitter = new ListAddEmitter(temps, globalStrings);
    private final ListRemoveEmitter listRemoveEmitter = new ListRemoveEmitter(temps);
    private final ListClearEmitter clearEmitter = new ListClearEmitter(temps);
    private final ListSizeEmitter sizeEmitter = new ListSizeEmitter(temps);
    private final ListGetEmitter getEmitter = new ListGetEmitter(temps);
    private final ListAddAllEmitter allEmitter = new ListAddAllEmitter(temps, globalStrings);
    private final Set<String> listVars = new HashSet<>();;

    public void registerListVar(String name) {
        listVars.add(name);
    }
    public boolean isList(String name) {
        return listVars.contains(name);
    }

    public void pushLoopEnd(String label) {
        loopEndLabels.push(label);
    }

    public void popLoopEnd() {
        loopEndLabels.pop();
    }

    public String currentLoopEnd() {
        if (loopEndLabels.isEmpty()) {
            throw new RuntimeException("Break fora de loop!");
        }
        return loopEndLabels.peek();
    }

    @Override
    public String visit(MainAST node) {
        MainEmitter mainEmitter = new MainEmitter(globalStrings, temps);
        return mainEmitter.emit(node, this);
    }

    public String visit(VariableDeclarationNode node) {
        return varEmitter.emitAlloca(node) + varEmitter.emitInit(node);
    }

    @Override
    public String visit(LiteralNode node) {
        return literalEmitter.emit(node);
    }

    @Override
    public String visit(VariableNode node) {
        return varEmitter.emitLoad(node.getName());
    }

    @Override
    public String visit(BinaryOpNode node) {
        return binaryEmitter.emit(node);
    }

    @Override
    public String visit(WhileNode node) {
        return whileEmitter.emit(node);
    }

    @Override
    public String visit(BreakNode node) {
        String endLabel = currentLoopEnd();
        return "  br label %" + endLabel + "\n";
    }

    @Override
    public String visit(ListNode node) {
      return listEmitter.emit(node, this);

    }

    @Override
    public String visit(ListAddNode node) {
        return listAddEmitter.emit(node, this);
    }

    @Override
    public String visit(ListRemoveNode node) {
        return listRemoveEmitter.emit(node, this);
    }

    @Override
    public String visit(ListClearNode node) {
        return clearEmitter.emit(node, this);
    }

    @Override
    public String visit(IfNode node) {
        return ifEmitter.emit(node);
    }

    @Override
    public String visit(PrintNode node) {
        return printEmitter.emit(node, this);
    }

    @Override
    public String visit(UnaryOpNode node) {
        return unaryOpEmitter.emit(node.getOperator(), node.getExpr());
    }

    @Override
    public String visit(AssignmentNode node) {
        return assignmentEmitter.emit(node);
    }

    public String getVarType(String name) {
        return varTypes.get(name);
    }

    @Override
    public String visit(ListSizeNode node) {
        return sizeEmitter.emit(node, this);
    }

    @Override
    public String visit(ListGetNode node) {
        return getEmitter.emit(node, this);
    }

    @Override
    public String visit(ListAddAllNode node) {
        return allEmitter.emit(node, this);
    }

    public TempManager getTemps() {
        return temps;
    }

    @Override
    public String visit(FunctionNode node) {
        return "";
    }
}
