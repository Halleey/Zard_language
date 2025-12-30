package context.analyzers;

import ast.ASTNode;
import ast.exceptions.ReturnNode;
import ast.functions.FunctionNode;
import ast.ifstatements.IfNode;
import ast.loops.WhileNode;

import java.util.List;


public class FlowAnalyzer {

    public void analyzeFunction(FunctionNode fn) {

        FlowInfo flow = analyzeBlock(fn.getBody());

        if (!fn.getReturnType().equals("void") && !flow.alwaysReturn) {
            throw new RuntimeException(
                    "Função '" + fn.getName() +
                            "' não pode terminar sem retornar um valor"
            );
        }
    }private FlowInfo analyzeBlock(List<ASTNode> body) {

        FlowInfo blockFlow = FlowInfo.continueFlow();

        for (ASTNode node : body) {

            if (!blockFlow.mayContinue) {
                throw new RuntimeException(
                        "Código morto detectado após return"
                );
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
            analyzeBlock(whileNode.getBody());
            return FlowInfo.continueFlow();
        }

        // qualquer outra instrução
        return FlowInfo.continueFlow();
    }

    private FlowInfo analyzeIf(IfNode node) {

        FlowInfo thenFlow = analyzeBlock(node.getThenBranch());

        FlowInfo elseFlow =
                node.getElseBranch() != null
                        ? analyzeBlock(node.getElseBranch())
                        : FlowInfo.continueFlow(); // else implícito

        FlowInfo result = new FlowInfo();

        result.mayReturn =
                thenFlow.mayReturn || elseFlow.mayReturn;

        result.alwaysReturn =
                thenFlow.alwaysReturn && elseFlow.alwaysReturn;

        result.mayContinue =
                !result.alwaysReturn;

        return result;
    }
}
