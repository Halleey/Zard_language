package low;

import ast.ASTNode;
import expressions.TypedValue;
import home.MainAST;
import low.variables.*;
import prints.PrintNode;
import variables.*;
import java.util.HashMap;
import java.util.Map;

public class LLVisitorMain implements LLVMEmitVisitor {
    private final Map<String, String> varTypes = new HashMap<>();
    private final TempManager temps = new TempManager();
    private final GlobalStringManager globalStrings = new GlobalStringManager();
    private final VariableEmitter varEmitter = new VariableEmitter(varTypes, temps, globalStrings, this);
    private final PrintEmitter printEmitter = new PrintEmitter(globalStrings);
    private final AssignmentEmitter assignmentEmitter = new AssignmentEmitter(varTypes, temps);
    private final UnaryOpEmitter unaryOpEmitter = new UnaryOpEmitter(varTypes, temps);
    private final StringBuilder llvmHeader = new StringBuilder();
    private final LiteralEmitter literalEmitter = new LiteralEmitter(temps,globalStrings);
    private final BinaryOpEmitter binaryEmitter = new BinaryOpEmitter(temps, this);



    public LLVisitorMain() {
        llvmHeader.append("declare i32 @printf(i8*, ...)\n");
        llvmHeader.append("declare i32 @getchar()\n");
        llvmHeader.append("@.strInt = private constant [4 x i8] c\"%d\\0A\\00\"\n");
        llvmHeader.append("@.strDouble = private constant [4 x i8] c\"%f\\0A\\00\"\n");
        llvmHeader.append("@.strStr = private constant [4 x i8] c\"%s\\0A\\00\"\n");
    }

    @Override
    public String visit(MainAST node) {
        // Coleta strings globais
        for (ASTNode stmt : node.body) {
            if (stmt instanceof PrintNode printNode && printNode.expr instanceof LiteralNode lit &&
                    lit.value.getType().equals("string")) {
                printEmitter.emitString((String) lit.value.getValue());
            }
            if (stmt instanceof VariableDeclarationNode varDecl && varDecl.initializer instanceof LiteralNode litInit &&
                    litInit.value.getType().equals("string")) {
                globalStrings.getOrCreateString((String) litInit.value.getValue());
            }
        }

        StringBuilder llvm = new StringBuilder();
        llvm.append(llvmHeader).append("\n");
        llvm.append(globalStrings.getGlobalStrings()).append("\n");
        llvm.append("define i32 @main() {\n");

        for (ASTNode stmt : node.body) {
            llvm.append("  ; ").append(stmt.getClass().getSimpleName()).append("\n");
            llvm.append(stmt.accept(this));
        }

        llvm.append("  call i32 @getchar()\n"); // espera uma tecla
        llvm.append("  ret i32 0\n}\n");
        return llvm.toString();
    }

    public String visit(VariableDeclarationNode node) {
        StringBuilder llvm = new StringBuilder(varEmitter.emitAlloca(node));
        llvm.append(varEmitter.emitInit(node));
        return llvm.toString();
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
    public String visit(PrintNode node) {
        if (node.expr instanceof LiteralNode lit && lit.value.getType().equals("string")) {
            return printEmitter.emitString((String) lit.value.getValue());
        }
        if (node.expr instanceof VariableNode varNode && varTypes.get(varNode.getName()).equals("i8*")) {
            return printEmitter.emitStringVariable(varNode.getName());
        }

        String valueResult = node.expr.accept(this);
        String[] valSplit = valueResult.split(";;VAL:");
        String code = valSplit[0];
        String value = valSplit.length > 1 ? valSplit[1].split(";;TYPE:")[0].trim() : valSplit[0].trim();
        String type = "i32";

        if (valueResult.contains(";;TYPE:")) {
            type = valueResult.split(";;TYPE:")[1].trim();
        } else if (node.expr instanceof LiteralNode lit2 && lit2.value.getType().equals("double")) {
            type = "double";
        }

        return printEmitter.emitNumber(code, value, type);
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

}
