package low.module;

import ast.ASTNode;
import ast.exceptions.BreakNode;
import ast.expressions.TypedValue;
import ast.home.MainAST;
import ast.ifstatements.IfNode;
import ast.lists.ListNode;
import ast.loops.WhileNode;
import low.ifs.IfEmitter;
import low.lists.LLVisitorListEmitter;
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
    private final AssignmentEmitter assignmentEmitter = new AssignmentEmitter(varTypes, temps);
    private final UnaryOpEmitter unaryOpEmitter = new UnaryOpEmitter(varTypes, temps);
    private final LiteralEmitter literalEmitter = new LiteralEmitter(temps,globalStrings);
    private final BinaryOpEmitter binaryEmitter = new BinaryOpEmitter(temps, this);
    private final IfEmitter ifEmitter = new IfEmitter(temps, this);
    private final WhileEmitter whileEmitter = new WhileEmitter(temps, this);
    private final LLVisitorListEmitter listEmitter = new LLVisitorListEmitter(temps, globalStrings);
    private final Deque<String> loopEndLabels = new ArrayDeque<>();

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
        MainEmitter mainEmitter = new MainEmitter(globalStrings);
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
      return "";

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
        TypedValue value;
        if (node.valueNode instanceof LiteralNode lit) {
            value = lit.value;
        } else if (node.valueNode instanceof VariableNode varNode) {
            String llvmType = varTypes.get(varNode.getName());
            value = new TypedValue(
                    switch (llvmType) {
                        case "i32" -> "int";
                        case "double" -> "double";
                        case "i1" -> "boolean";
                        default -> "string";
                    },
                    varNode.getName()
            );
        } else {
            throw new RuntimeException("Expressão de atribuição não suportada ainda");
        }
        return assignmentEmitter.emitAssignment(node.name, value);
    }

    public String getVarType(String name) {
        return varTypes.get(name);
    }
}
