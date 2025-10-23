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
import ast.prints.PrintNode;
import ast.structs.StructFieldAccessNode;
import ast.structs.StructInstaceNode;
import ast.structs.StructNode;
import ast.variables.AssignmentNode;
import ast.variables.BinaryOpNode;
import low.TempManager;

import low.functions.FunctionEmitter;
import low.imports.ImportEmitter;
import low.module.LLVisitorMain;
import ast.variables.LiteralNode;
import ast.variables.VariableDeclarationNode;


import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class MainEmitter {
    private final GlobalStringManager globalStrings;
    private final TempManager tempManager;
    private final Set<String> listasAlocadas = new HashSet<>();
    private final Set<String> tiposDeListasUsados;
    private final List<String> structDefinitions;
    private boolean usesInput = false;

    public MainEmitter(GlobalStringManager globalStrings, TempManager tempManager,
                       Set<String> tiposDeListasUsados, List<String> structDefinitions) {
        this.globalStrings = globalStrings;
        this.tempManager = tempManager;
        this.tiposDeListasUsados = tiposDeListasUsados;
        this.structDefinitions = structDefinitions;
    }

    public String emit(MainAST node, LLVisitorMain visitor) {
        visitor.registrarStructs(node);
        StringBuilder llvm = new StringBuilder();
        ImportEmitter importEmitter = new ImportEmitter(visitor, this.tiposDeListasUsados);

        for (ASTNode stmt : node.body) {
            if (!(stmt instanceof ImportNode)) {
                coletarStringsRecursivo(stmt);
            }
        }

        llvm.append(emitHeader()).append("\n");
        llvm.append(globalStrings.getGlobalStrings()).append("\n");

        if (!structDefinitions.isEmpty()) {
            llvm.append(";; ==== Struct Definitions ====\n");
            for (String structDef : structDefinitions) {
                llvm.append(structDef).append("\n");
            }
            llvm.append("\n");
        }

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
                llvm.append(fnEmitter.emit(fn)).append("\n");
            }
        }

        llvm.append("define i32 @main() {\n");
        for (ASTNode stmt : node.body) {

            if (stmt instanceof FunctionNode || stmt instanceof ImportNode || stmt instanceof StructNode)
                continue;

            llvm.append("  ; ").append(stmt.getClass().getSimpleName()).append("\n");
            String stmtIR = stmt.accept(visitor);
            if (stmtIR != null && !stmtIR.isBlank()) {
                llvm.append(stmtIR);
            }

            if (stmt instanceof VariableDeclarationNode varDecl &&
                    varDecl.getType().startsWith("List")) {
                listasAlocadas.add(varDecl.getName());
            }
        }

        if (!listasAlocadas.isEmpty()) {
            llvm.append("  ; === Free das listas alocadas ===\n");
            for (String varName : listasAlocadas) {
                String tipoLista = visitor.getListElementType(varName);
                String tmp = tempManager.newTemp();

                switch (tipoLista) {
                    case "int" -> {
                        llvm.append("  ").append(tmp).append(" = load %struct.ArrayListInt*, %struct.ArrayListInt** %")
                                .append(varName).append("\n");
                        llvm.append("  call void @arraylist_free_int(%struct.ArrayListInt* ").append(tmp).append(")\n");
                    }
                    case "double" -> {
                        llvm.append("  ").append(tmp)
                                .append(" = load %struct.ArrayListDouble*, %struct.ArrayListDouble** %")
                                .append(varName).append("\n");
                        llvm.append("  call void @arraylist_free_double(%struct.ArrayListDouble* ").append(tmp).append(")\n");
                    }
                    case "boolean" -> {
                        llvm.append("  ").append(tmp)
                                .append(" = load %struct.ArrayListBool*, %struct.ArrayListBool** %")
                                .append(varName).append("\n");
                        llvm.append("  call void @arraylist_free_bool(%struct.ArrayListBool* ").append(tmp).append(")\n");
                    }
                    default -> {
                        String bc = tempManager.newTemp();
                        llvm.append("  ").append(tmp).append(" = load i8*, i8** %").append(varName).append("\n");
                        llvm.append("  ").append(bc).append(" = bitcast i8* ").append(tmp).append(" to %ArrayList*\n");
                        llvm.append("  call void @freeList(%ArrayList* ").append(bc).append(")\n");
                    }
                }
            }
        }
        llvm.append("  call i32 @getchar()\n");
        llvm.append("  ret i32 0\n}\n");

        return llvm.toString();
    }




    private void coletarStringsRecursivo(ASTNode node) {
        if (node instanceof LiteralNode lit && lit.value.getType().equals("string"))
            globalStrings.getOrCreateString((String) lit.value.getValue());
        if (node instanceof InputNode inputNode) {
            usesInput = true;
            if (inputNode.getPrompt() != null)
                globalStrings.getOrCreateString(inputNode.getPrompt());
        }

        if (node instanceof PrintNode printNode) {
            ASTNode expr = printNode.expr;
            if (expr instanceof LiteralNode lit && lit.value.getType().equals("string")) {
                globalStrings.getOrCreateString((String) lit.value.getValue());
            }
        }
        if (node instanceof BinaryOpNode bin) {
            coletarStringsRecursivo(bin.left);
            coletarStringsRecursivo(bin.right);
        }



        if (node instanceof VariableDeclarationNode varDecl) {
            if (varDecl.getType().startsWith("List")) registrarTipoDeLista(varDecl.getType());
            if (varDecl.initializer != null) coletarStringsRecursivo(varDecl.initializer);
        } else if (node instanceof FunctionNode func) {
            func.getBody().forEach(this::coletarStringsRecursivo);
        } else if (node instanceof IfNode ifNode) {
            coletarStringsRecursivo(ifNode.condition);
            ifNode.thenBranch.forEach(this::coletarStringsRecursivo);
            if (ifNode.elseBranch != null) ifNode.elseBranch.forEach(this::coletarStringsRecursivo);
        } else if (node instanceof WhileNode whileNode) {
            coletarStringsRecursivo(whileNode.condition);
            whileNode.body.forEach(this::coletarStringsRecursivo);
        } else if (node instanceof ListNode listNode) {
            registrarTipoDeLista("List<" + listNode.getList().getElementType() + ">");
            listNode.getList().getElements().forEach(this::coletarStringsRecursivo);
        } else if (node instanceof ListAddNode addNode)
            coletarStringsRecursivo(addNode.getValuesNode());
        else if (node instanceof AssignmentNode assignNode)
            coletarStringsRecursivo(assignNode.valueNode);
        else if (node instanceof ListAddAllNode addAllNode)
            coletarStringsRecursivo(addAllNode.getArgs());
        else if(node instanceof ReturnNode returnNode) {
            coletarStringsRecursivo(returnNode.expr);
        }
        else if(node instanceof StructFieldAccessNode accessNode) {
                coletarStringsRecursivo(accessNode.getValue());
        }
        else if (node instanceof StructInstaceNode structInstance) {
            for (ASTNode val : structInstance.getPositionalValues()) {
                coletarStringsRecursivo(val);
            }

            if (structInstance.getNamedValues() != null) {
                for (ASTNode val : structInstance.getNamedValues().values()) {
                    coletarStringsRecursivo(val);
                }
            }
        }


    }

    private void coletarStringsRecursivo(List<ASTNode> nodes) {
        nodes.forEach(this::coletarStringsRecursivo);
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
        declare void @setString(%String*, i8*)

        @.strTrue = private constant [6 x i8] c"true\\0A\\00"
        @.strFalse = private constant [7 x i8] c"false\\0A\\00"
        @.strInt = private constant [4 x i8] c"%d\\0A\\00"
        @.strDouble = private constant [4 x i8] c"%f\\0A\\00"
        @.strStr = private constant [4 x i8] c"%s\\0A\\00"
        @.strEmpty = private constant [1 x i8] c"\00"
        declare %String* @createString(i8*)
        declare i1 @strcmp_eq(%String*, %String*)
        declare i1 @strcmp_neq(%String*, %String*)

        %String = type { i8*, i64 }
    """);

        if (!tiposDeListasUsados.isEmpty()) {
            sb.append("""
        declare i8* @arraylist_create(i64)
        declare void @clearList(%ArrayList*)
        declare void @freeList(%ArrayList*)
        declare void @arraylist_add_ptr(%ArrayList*, i8*)
        declare i32 @length(%ArrayList*)
        declare i8* @arraylist_get_ptr(%ArrayList*, i64)
        declare void @arraylist_print_ptr(%ArrayList*, void (i8*)*)
        %ArrayList = type opaque
    """);
        }


        if (usesInput) {
            sb.append("""
            declare i32 @inputInt(i8*)
            declare double @inputDouble(i8*)
            declare i1 @inputBool(i8*)
            declare i8* @inputString(i8*)
        """);
        }

        for (String tipo : tiposDeListasUsados) {
            if (tipo.contains("<int>")) {
                sb.append("""
                %struct.ArrayListInt = type { i32*, i64, i64 }
                declare %struct.ArrayListInt* @arraylist_create_int(i64)
                declare void @arraylist_add_int(%struct.ArrayListInt*, i32)
                declare void @arraylist_addAll_int(%struct.ArrayListInt*, i32*, i64)
                declare void @arraylist_print_int(%struct.ArrayListInt*)
                declare void @arraylist_clear_int(%struct.ArrayListInt*)
                declare void @arraylist_free_int(%struct.ArrayListInt*)
                declare i32  @arraylist_get_int(%struct.ArrayListInt*, i64, i32*)
                declare void @arraylist_remove_int(%struct.ArrayListInt*, i64)
                declare i32  @arraylist_size_int(%struct.ArrayListInt*)
            """);
            } else if (tipo.contains("<double>")) {
                sb.append("""
                %struct.ArrayListDouble = type { double*, i64, i64 }
                declare %struct.ArrayListDouble* @arraylist_create_double(i64)
                declare void @arraylist_add_double(%struct.ArrayListDouble*, double)
                declare void @arraylist_addAll_double(%struct.ArrayListDouble*, double*, i64)
                declare void @arraylist_print_double(%struct.ArrayListDouble*)
                declare double  @arraylist_get_double(%struct.ArrayListDouble*, i64, double*)
                declare void @arraylist_clear_double(%struct.ArrayListDouble*)
                declare void @arraylist_remove_double(%struct.ArrayListDouble*, i64)
                declare void @arraylist_free_double(%struct.ArrayListDouble*)
                declare i32  @arraylist_size_double(%struct.ArrayListDouble*)
            """);
            } else if (tipo.contains("<string>")) {
                sb.append("""
                declare void @arraylist_add_string(%ArrayList*, i8*)
                declare void @arraylist_addAll_string(%ArrayList*, i8**, i64)
                declare void @arraylist_print_string(%ArrayList*)
                declare void @arraylist_add_String(%ArrayList*, %String*)
                declare void @arraylist_addAll_String(%ArrayList*, %String**, i64)
                declare void @removeItem(%ArrayList*, i64)
                declare i8* @getItem(%ArrayList*, i64)
                
            """);
            } else if (tipo.contains("<boolean>")) {
                sb.append("""
                %struct.ArrayListBool = type { i1*, i64, i64 }
                declare %struct.ArrayListBool* @arraylist_create_bool(i64)
                declare void @arraylist_add_bool(%struct.ArrayListBool*, i1)
                declare void @arraylist_addAll_bool(%struct.ArrayListBool*, i8*, i64)
                declare void @arraylist_print_bool(%struct.ArrayListBool*)
                declare void @arraylist_clear_bool(%struct.ArrayListBool*)
                declare void @arraylist_remove_bool(%struct.ArrayListBool*, i64)
                declare void @arraylist_free_bool(%struct.ArrayListBool*)
                declare i1 @arraylist_get_bool(%struct.ArrayListBool*, i64, i1*)
                declare i32  @arraylist_size_bool(%struct.ArrayListBool*)
             
            """);
            }
        }

        return sb.toString();
    }
}
