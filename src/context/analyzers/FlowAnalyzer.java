package context.analyzers;

import ast.ASTNode;
import ast.exceptions.ReturnNode;
import ast.functions.FunctionNode;
import ast.ifstatements.IfNode;
import ast.loops.WhileNode;
import context.statics.symbols.PrimitiveTypes;

import java.util.List;


public class FlowAnalyzer {

    public void analyzeFunction(FunctionNode fn) {
        FlowInfo flow = analyzeBlock(fn.getBody());

        if (!fn.getReturnType().equals(PrimitiveTypes.VOID) && !flow.alwaysReturn) {
            throw new RuntimeException(
                    "Função '" + fn.getName() + "' não pode terminar sem retornar um valor"
            );
        }
    }

    private FlowInfo analyzeBlock(List<ASTNode> body) {
        FlowInfo blockFlow = FlowInfo.continueFlow();

        for (ASTNode node : body) {

            if (!blockFlow.mayContinue) {
                throw new RuntimeException("Código morto detectado após return");
            }

            FlowInfo stmtFlow = analyzeNode(node);

            blockFlow.mayReturn |= stmtFlow.mayReturn;
            blockFlow.mayContinue = stmtFlow.mayContinue;

            if (stmtFlow.alwaysReturn) {
                blockFlow.alwaysReturn = true;
                blockFlow.mayContinue = false;
            }
        }

        return blockFlow;
    }

    private FlowInfo analyzeNode(ASTNode node) {

        if (node instanceof ReturnNode) {
            return FlowInfo.returnFlow();
        }

        if (node instanceof IfNode ifNode) {
            return analyzeIf(ifNode);
        }

        if (node instanceof WhileNode whileNode) {
            // Analisar o corpo, mas while pode não terminar, então não força alwaysReturn
            analyzeBlock(whileNode.getBody());
            return FlowInfo.continueFlow();
        }

        // Qualquer outra instrução não altera fluxo de retorno
        return FlowInfo.continueFlow();
    }

    private FlowInfo analyzeIf(IfNode node) {

        FlowInfo thenFlow = analyzeBlock(node.getThenBranch());

        FlowInfo elseFlow =
                node.getElseBranch() != null
                        ? analyzeBlock(node.getElseBranch())
                        : FlowInfo.continueFlow(); // else implícito

        FlowInfo result = new FlowInfo();

        // mayReturn: qualquer ramo pode retornar
        result.mayReturn = thenFlow.mayReturn || elseFlow.mayReturn;

        // alwaysReturn: apenas se ambos os ramos sempre retornam
        result.alwaysReturn = thenFlow.alwaysReturn && elseFlow.alwaysReturn;

        // mayContinue: só continua se não é alwaysReturn
        result.mayContinue = !result.alwaysReturn;

        return result;
    }
}