package low.main;

import ast.ASTNode;
import ast.exceptions.ReturnNode;
import ast.functions.FunctionNode;
import ast.home.MainAST;
import ast.ifstatements.IfNode;
import ast.imports.ImportNode;
import ast.inputs.InputNode;
import ast.lists.ListAddAllNode;
import ast.lists.ListAddNode;
import ast.lists.ListNode;
import ast.loops.WhileNode;
import ast.variables.AssignmentNode;
import low.TempManager;

import low.functions.FunctionEmitter;
import low.imports.ImportEmitter;
import low.module.LLVisitorMain;
import ast.prints.PrintNode;
import ast.variables.LiteralNode;
import ast.variables.VariableDeclarationNode;


import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class MainEmitter {
    private final GlobalStringManager globalStrings;
    private final TempManager tempManager;
    private final Set<String> listasAlocadas = new HashSet<>();
    private final Set<String> tiposDeListasUsados = new HashSet<>();
    private boolean usesInput = false;

    public MainEmitter(GlobalStringManager globalStrings, TempManager tempManager) {
        this.globalStrings = globalStrings;
        this.tempManager = tempManager;
    }

    public String emit(MainAST node, LLVisitorMain visitor) {
        StringBuilder llvm = new StringBuilder();

        // Coleta todas as strings e tipos de lista antes de emitir
        for (ASTNode stmt : node.body) {
            coletarStringsRecursivo(stmt);
        }

        llvm.append(emitHeader()).append("\n");

        llvm.append(globalStrings.getGlobalStrings()).append("\n");

        ImportEmitter importEmitter = new ImportEmitter(visitor);
        for (ASTNode stmt : node.body) {
            if (stmt instanceof ImportNode importNode) {
                llvm.append(";; ==== Import module: ")
                        .append(importNode.path())
                        .append(" as ")
                        .append(importNode.alias())
                        .append(" ====\n");
                llvm.append(importEmitter.emit(importNode)).append("\n");
            }
        }



        FunctionEmitter fnEmitter = new FunctionEmitter(visitor);
        for (ASTNode stmt : node.body) {
            if (stmt instanceof FunctionNode fn) {
                llvm.append(fnEmitter.emit(fn));
            }
        }

        // Função principal
        llvm.append("define i32 @main() {\n");
        for (ASTNode stmt : node.body) {
            if (stmt instanceof FunctionNode || stmt instanceof ImportNode) continue;

            llvm.append("  ; ").append(stmt.getClass().getSimpleName()).append("\n");
            llvm.append(stmt.accept(visitor));

            if (stmt instanceof VariableDeclarationNode varDecl && varDecl.getType().startsWith("List")) {
                listasAlocadas.add(varDecl.getName());
            }
        }

        // Libera as listas no final
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

        llvm.append("  ; === Wait for key press before exiting ===\n");
        llvm.append("  call i32 @getchar()\n");
        llvm.append("  ret i32 0\n}\n");

        return llvm.toString();
    }

    private void coletarStringsRecursivo(ASTNode node) {
        if (node instanceof LiteralNode lit && lit.value.getType().equals("string")) {
            globalStrings.getOrCreateString((String) lit.value.getValue());
        }

        if (node instanceof InputNode inputNode) {
            usesInput = true;
            if (inputNode.getPrompt() != null) {
                globalStrings.getOrCreateString(inputNode.getPrompt());
            }
        }

        if (node instanceof VariableDeclarationNode varDecl) {
            if (varDecl.getType().startsWith("List")) {
                registrarTipoDeLista(varDecl.getType());
            }
            if (varDecl.initializer != null) coletarStringsRecursivo(varDecl.initializer);
        } else if (node instanceof AssignmentNode assignNode && assignNode.valueNode != null) {
            coletarStringsRecursivo(assignNode.valueNode);
        } else if (node instanceof PrintNode printNode) {
            coletarStringsRecursivo(printNode.expr);
        } else if (node instanceof ReturnNode returnNode && returnNode.expr != null) {
            coletarStringsRecursivo(returnNode.expr);
        } else if (node instanceof IfNode ifNode) {
            coletarStringsRecursivo(ifNode.condition);
            for (ASTNode stmt : ifNode.thenBranch) coletarStringsRecursivo(stmt);
            if (ifNode.elseBranch != null)
                for (ASTNode stmt : ifNode.elseBranch) coletarStringsRecursivo(stmt);
        } else if (node instanceof WhileNode whileNode) {
            coletarStringsRecursivo(whileNode.condition);
            for (ASTNode stmt : whileNode.body) coletarStringsRecursivo(stmt);
        } else if (node instanceof FunctionNode funcNode) {
            for (ASTNode stmt : funcNode.getBody()) coletarStringsRecursivo(stmt);
        } else if (node instanceof ListNode listNode) {
            registrarTipoDeLista("List<" + listNode.getList().getElementType() + ">");
            for (ASTNode element : listNode.getList().getElements()) coletarStringsRecursivo(element);
        } else if (node instanceof ListAddNode addNode) {
            coletarStringsRecursivo(addNode.getValuesNode());
        } else if (node instanceof ListAddAllNode addAllNode) {
            coletarStringsRecursivo(addAllNode.getArgs());
        }
    }

    private void coletarStringsRecursivo(List<ASTNode> nodes) {
        for (ASTNode node : nodes) coletarStringsRecursivo(node);
    }

    private void registrarTipoDeLista(String tipoCompleto) {
        tiposDeListasUsados.add(tipoCompleto.trim());
    }

    private String emitHeader() {
        StringBuilder sb = new StringBuilder();
        sb.append("""
            declare i32 @printf(i8*, ...)
            declare i32 @getchar()
            declare void @printString(%String*)
            declare i8* @malloc(i64)
         
            @.strInt = private constant [4 x i8] c"%d\\0A\\00"
            @.strDouble = private constant [4 x i8] c"%f\\0A\\00"
            @.strStr = private constant [4 x i8] c"%s\\0A\\00"

            %String = type { i8*, i64 }
            %ArrayList = type opaque
        """);

        if (usesInput) {
            sb.append("""
                declare i32 @inputInt(i8*)
                declare double @inputDouble(i8*)
                declare i1 @inputBool(i8*)
                declare i8* @inputString(i8*)
                declare %String* @createString(i8*)
            """);
        }

        for (String tipo : tiposDeListasUsados) {
            if (tipo.contains("<int>")) {
                sb.append("""
                    declare i8* @arraylist_create(i64)
                    declare void @clearList(%ArrayList*)
                    declare void @freeList(%ArrayList*)
                    declare void @arraylist_add_int(%ArrayList*, i32)
                    declare void @arraylist_print_int(%ArrayList*)
                """);
            } else if (tipo.contains("<double>")) {
                sb.append("""
                     declare i8* @arraylist_create(i64)
                    declare void @clearList(%ArrayList*)
                    declare void @freeList(%ArrayList*)
                    declare void @arraylist_add_double(%ArrayList*, double)
                    declare void @arraylist_print_double(%ArrayList*)
                """);
            } else if (tipo.contains("<string>")) {
                sb.append("""
                     declare i8* @arraylist_create(i64)
                    declare void @clearList(%ArrayList*)
                    declare void @freeList(%ArrayList*)
                    declare void @arraylist_add_string(%ArrayList*, i8*)
                    declare void @arraylist_addAll_string(%ArrayList*, i8**, i64)
                    declare void @arraylist_print_string(%ArrayList*)
                """);
            } else if (tipo.contains("<String>")) {
                sb.append("""
                    declare i8* @arraylist_create(i64)
                    declare void @clearList(%ArrayList*)
                    declare void @freeList(%ArrayList*)
                    declare void @arraylist_add_String(%ArrayList*, %String*)
                    declare void @arraylist_addAll_String(%ArrayList*, %String**, i64)
                """);
            }
        }

        return sb.toString();
    }
}