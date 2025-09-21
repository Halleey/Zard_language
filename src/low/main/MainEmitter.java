package low.main;

import ast.ASTNode;
import ast.functions.FunctionNode;
import home.MainAST;
import ifstatements.IfNode;
import loops.WhileNode;
import low.module.LLVisitorMain;
import prints.PrintNode;
import variables.LiteralNode;
import variables.VariableDeclarationNode;
public class MainEmitter {
    private final GlobalStringManager globalStrings;

    public MainEmitter(GlobalStringManager globalStrings) {
        this.globalStrings = globalStrings;
    }

    public String emit(MainAST node, LLVisitorMain visitor) {
        // Coleta todas as strings literais do AST
        for (ASTNode stmt : node.body) {
            coletarStringsRecursivo(stmt);
        }

        StringBuilder llvm = new StringBuilder();
        llvm.append(emitHeader()).append("\n");
        llvm.append(globalStrings.getGlobalStrings()).append("\n");
        llvm.append(emitMainStart());

        // Gera código para cada statement usando visitor
        for (ASTNode stmt : node.body) {
            llvm.append("  ; ").append(stmt.getClass().getSimpleName()).append("\n");
            llvm.append(stmt.accept(visitor));
        }

        llvm.append(emitMainEnd());
        return llvm.toString();
    }

    private void coletarStringsRecursivo(ASTNode node) {
        if (node instanceof PrintNode printNode && printNode.expr instanceof LiteralNode lit &&
                lit.value.getType().equals("string")) {
            globalStrings.getOrCreateString((String) lit.value.getValue());
        }

        if (node instanceof VariableDeclarationNode varDecl && varDecl.initializer instanceof LiteralNode litInit &&
                litInit.value.getType().equals("string")) {
            globalStrings.getOrCreateString((String) litInit.value.getValue());
        }

        // IfNode
        if (node instanceof IfNode ifNode) {
            coletarStringsRecursivo(ifNode.condition);
            for (ASTNode stmt : ifNode.thenBranch) coletarStringsRecursivo(stmt);
            if (ifNode.elseBranch != null)
                for (ASTNode stmt : ifNode.elseBranch) coletarStringsRecursivo(stmt);
        }

        // WhileNode no futuro
        if (node instanceof WhileNode whileNode) {
            coletarStringsRecursivo(whileNode.condition);
            for (ASTNode stmt : whileNode.body) coletarStringsRecursivo(stmt);
        }

        // FunctionNode no futuro
        if (node instanceof FunctionNode funcNode) {
            for (ASTNode stmt : funcNode.body) coletarStringsRecursivo(stmt);
        }

        // nodes com herença ou generics, dropar td aqui
    }

    private String emitHeader() {
        return """
            declare i32 @printf(i8*, ...)
            declare i32 @getchar()
            @.strInt = private constant [4 x i8] c"%d\\0A\\00"
            @.strDouble = private constant [4 x i8] c"%f\\0A\\00"
            @.strStr = private constant [4 x i8] c"%s\\0A\\00"
            """;
    }

    private String emitMainStart() {
        return "define i32 @main() {\n";
    }

    private String emitMainEnd() {
        return "  call i32 @getchar()\n  ret i32 0\n}\n";
    }
}
