package low.main;

import ast.ASTNode;
import ast.functions.FunctionNode;
import ast.home.MainAST;
import ast.ifstatements.IfNode;
import ast.lists.ListAddNode;
import ast.lists.ListNode;
import ast.loops.WhileNode;
import ast.variables.AssignmentNode;
import low.lists.ListAddEmitter;
import low.module.LLVisitorMain;
import ast.prints.PrintNode;
import ast.variables.LiteralNode;
import ast.variables.VariableDeclarationNode;

public class MainEmitter {
    private final GlobalStringManager globalStrings;

    public MainEmitter(GlobalStringManager globalStrings) {
        this.globalStrings = globalStrings;
    }

    public String emit(MainAST node, LLVisitorMain visitor) {
        // coleta todas as strings do AST (Prints, Assigns, VarDecls)
        for (ASTNode stmt : node.body) {
            coletarStringsRecursivo(stmt);
        }

        // coleta strings dentro de listas
        for (ASTNode stmt : node.body) {
            if (stmt instanceof VariableDeclarationNode varDecl && varDecl.initializer instanceof ListNode listNode) {
                for (ASTNode element : listNode.getList().getElements()) {
                    if (element instanceof LiteralNode lit && lit.value.getType().equals("string")) {
                        globalStrings.getOrCreateString((String) lit.value.getValue());
                    }
                }
            }
        }

        // build o llvm
        StringBuilder llvm = new StringBuilder();

        // header + runtime (fora do main)
        llvm.append(emitHeader(node)).append("\n");
        llvm.append(globalStrings.getGlobalStrings()).append("\n");

        // Começo do main
        llvm.append(emitMainStart());

        // crpo do main
        for (ASTNode stmt : node.body) {
            llvm.append("  ; ").append(stmt.getClass().getSimpleName()).append("\n");
            llvm.append(stmt.accept(visitor));
        }

        // fim do main
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

        if (node instanceof AssignmentNode assignNode) {
            if (assignNode.valueNode instanceof LiteralNode litAssign &&
                    litAssign.value.getType().equals("string")) {
                globalStrings.getOrCreateString((String) litAssign.value.getValue());
            } else {
                coletarStringsRecursivo(assignNode.valueNode);
            }
        }

        if (node instanceof IfNode ifNode) {
            coletarStringsRecursivo(ifNode.condition);
            for (ASTNode stmt : ifNode.thenBranch) coletarStringsRecursivo(stmt);
            if (ifNode.elseBranch != null)
                for (ASTNode stmt : ifNode.elseBranch) coletarStringsRecursivo(stmt);
        }

        if (node instanceof WhileNode whileNode) {
            coletarStringsRecursivo(whileNode.condition);
            for (ASTNode stmt : whileNode.body) coletarStringsRecursivo(stmt);
        }

        if (node instanceof FunctionNode funcNode) {
            for (ASTNode stmt : funcNode.body) coletarStringsRecursivo(stmt);
        }

        if (node instanceof ListAddNode listAdd) {
            ASTNode valueNode = listAdd.getValuesNode();
            if (valueNode instanceof LiteralNode lit && lit.value.getType().equals("string")) {
                globalStrings.getOrCreateString((String) lit.value.getValue());
            } else {
                coletarStringsRecursivo(valueNode);
            }
        }

    }

    // detecta se ha pelo menos uma lista no MainAST
    private boolean containsList(MainAST node) {
        for (ASTNode stmt : node.body) {
            if (stmt instanceof ListNode) return true;
            if (stmt instanceof VariableDeclarationNode varDecl && varDecl.initializer instanceof ListNode)
                return true;
        }
        return false;
    }

    // dentro do MainEmitter
    private String emitHeader(MainAST node) {
        StringBuilder header = new StringBuilder();
        header.append("""
        declare i32 @printf(i8*, ...)
        declare i32 @getchar()
        @.strInt = private constant [4 x i8] c"%d\\0A\\00"
        @.strDouble = private constant [4 x i8] c"%f\\0A\\00"
        @.strStr = private constant [4 x i8] c"%s\\0A\\00"
        
        ; === Funções do runtime DynValue ===
        declare i8* @createInt(i32)
        declare i8* @createDouble(double)
        declare i8* @createBool(i1)
        declare i8* @createString(i8*)
        """);

        // adiciona runtime de listas se houver alguma
        if (containsList(node)) {
            header.append("\n; === Runtime de listas ===\n");
            // declaração de funções do  ArrayList
            header.append("%ArrayList = type opaque\n");
            header.append("declare i8* @arraylist_create(i64)\n")
                    .append("declare void @setItems(i8*, i8*)\n")
                    .append("declare void @printList(i8*)\n")
                    .append("declare void @removeItem(%ArrayList*, i64)\n")
                    .append("declare void @clearList(%ArrayList*)\n");
        }

        return header.toString();
    }


    private String emitMainStart() {
        return "define i32 @main() {\n";
    }

    private String emitMainEnd() {
        return "  call i32 @getchar()\n  ret i32 0\n}\n";
    }
}
