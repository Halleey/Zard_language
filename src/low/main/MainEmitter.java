package low.main;

import ast.ASTNode;
import ast.functions.FunctionNode;
import ast.home.MainAST;
import ast.ifstatements.IfNode;
import ast.inputs.InputNode;
import ast.lists.ListAddNode;
import ast.lists.ListNode;
import ast.loops.WhileNode;
import ast.variables.AssignmentNode;
import low.TempManager;

import low.functions.FunctionEmitter;
import low.module.LLVisitorMain;
import ast.prints.PrintNode;
import ast.variables.LiteralNode;
import ast.variables.VariableDeclarationNode;


import java.util.HashSet;
import java.util.Set;

public class MainEmitter {
    private final GlobalStringManager globalStrings;
    private final Set<String> listasAlocadas = new HashSet<>();
    private final TempManager tempManager;

    public MainEmitter(GlobalStringManager globalStrings, TempManager tempManager) {
        this.globalStrings = globalStrings;
        this.tempManager = tempManager;
    }

    public String emit(MainAST node, LLVisitorMain visitor) {
        StringBuilder llvm = new StringBuilder();

        llvm.append(emitHeader(node)).append("\n");

        for (ASTNode stmt : node.body) {
            coletarStringsRecursivo(stmt);
        }

        for (ASTNode stmt : node.body) {
            if (stmt instanceof VariableDeclarationNode varDecl && varDecl.initializer instanceof ListNode listNode) {
                for (ASTNode element : listNode.getList().getElements()) {
                    if (element instanceof LiteralNode lit && lit.value.getType().equals("string")) {
                        globalStrings.getOrCreateString((String) lit.value.getValue());
                    }
                }
            }
        }
        llvm.append(globalStrings.getGlobalStrings()).append("\n");

        FunctionEmitter fnEmitter = new FunctionEmitter(visitor);
        for (ASTNode stmt : node.body) {
            if (stmt instanceof FunctionNode fn) {
                llvm.append("; === Função: ").append(fn.getName()).append(" ===\n");
                llvm.append(fnEmitter.emit(fn));
            }
        }


        llvm.append(emitMainStart());

        for (ASTNode stmt : node.body) {
            if (stmt instanceof FunctionNode) continue; // função já foi emitida
            llvm.append("  ; ").append(stmt.getClass().getSimpleName()).append("\n");
            llvm.append(stmt.accept(visitor)); // executa visitor
        }


        if (!listasAlocadas.isEmpty()) {
            llvm.append("  ; === Free das listas alocadas ===\n");
            for (String varName : listasAlocadas) {
                String tmp = tempManager.newTemp();
                String bc  = tempManager.newTemp();
                llvm.append("  ").append(tmp).append(" = load i8*, i8** %").append(varName).append("\n");
                llvm.append("  ").append(bc).append(" = bitcast i8* ").append(tmp).append(" to %ArrayList*\n");
                llvm.append("  call void @freeList(%ArrayList* ").append(bc).append(")\n");
            }
        }

        llvm.append(emitMainEnd());
        return llvm.toString();
    }


    private void coletarStringsRecursivo(ASTNode node) {
        if (node instanceof PrintNode printNode && printNode.expr instanceof LiteralNode lit &&
                lit.value.getType().equals("string")) {
            globalStrings.getOrCreateString((String) lit.value.getValue());
        }

        if (node instanceof VariableDeclarationNode varDecl) {
            // Se for literal string
            if (varDecl.initializer instanceof LiteralNode litInit &&
                    litInit.value.getType().equals("string")) {
                globalStrings.getOrCreateString((String) litInit.value.getValue());
            }
            // Se for lista
            else if (varDecl.initializer instanceof ListNode listInit) {
                for (ASTNode element : listInit.getList().getElements()) {
                    coletarStringsRecursivo(element); // recursivo para Literals ou outros nós
                }
            }
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
            for (ASTNode stmt : funcNode.getBody()) coletarStringsRecursivo(stmt);
        }
        if (node instanceof InputNode inputNode && inputNode.getPrompt() != null) {
            globalStrings.getOrCreateString(inputNode.getPrompt());
        }
    }


    private boolean containsList(MainAST node) {
        for (ASTNode stmt : node.body) {
            if (stmt instanceof ListNode) return true;
            if (stmt instanceof VariableDeclarationNode varDecl && varDecl.initializer instanceof ListNode)
                return true;
        }
        return false;
    }

    private String emitHeader(MainAST node) {
        StringBuilder header = new StringBuilder();

        header.append("""
            declare i32 @printf(i8*, ...)
            declare i32 @getchar()
            @.strInt = private constant [4 x i8] c"%d\\0A\\00"
            @.strDouble = private constant [4 x i8] c"%f\\0A\\00"
            @.strStr = private constant [4 x i8] c"%s\\0A\\00"

            ; === Tipo opaco DynValue ===
            %DynValue = type opaque

            ; === Funções do runtime DynValue ===
            declare %DynValue* @createInt(i32)
            declare %DynValue* @createDouble(double)
            declare %DynValue* @createBool(i1)
            declare %DynValue* @createString(i8*)

            ; === Funções de conversão DynValue -> tipo primitivo ===
            declare i32 @dynToInt(%DynValue*)
            declare double @dynToDouble(%DynValue*)
            declare i1 @dynToBool(%DynValue*)
            declare i8* @dynToString(%DynValue*)

            ; === Função de input ===
            declare i32 @inputInt(i8*)
            declare double @inputDouble(i8*)
            declare i1 @inputBool(i8*)
            declare i8*@inputString(i8*)
        """);

        if (containsList(node)) {
            header.append("\n; === Runtime de listas ===\n");
            header.append("%ArrayList = type opaque\n");
            header.append("declare i8* @arraylist_create(i64)\n")
                    .append("declare void @setItems(i8*, %DynValue*)\n") // corrigido DynValue*
                    .append("declare void @printList(i8*)\n")
                    .append("declare void @removeItem(%ArrayList*, i64)\n")
                    .append("declare void @clearList(%ArrayList*)\n")
                    .append("declare void @freeList(%ArrayList*)\n")
                    .append("declare i32 @size(%ArrayList*)\n")
                    .append("declare %DynValue* @getItem(%ArrayList*, i32)\n")
                    .append("declare void @printDynValue(%DynValue*)\n")
                    .append("declare void @addAll(%ArrayList*, %DynValue**, i64)\n");
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