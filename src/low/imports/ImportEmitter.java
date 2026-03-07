package low.imports;

import ast.ASTNode;
import ast.functions.FunctionNode;
import ast.functions.ParamInfo;
import ast.ifstatements.IfNode;
import ast.imports.ImportNode;
import ast.loops.WhileNode;
import ast.structs.ImplNode;
import ast.structs.StructNode;
import ast.variables.VariableDeclarationNode;
import ast.lists.ListNode;

import ast.lists.ListAddNode;
import ast.lists.ListAddAllNode;

import context.statics.symbols.*;
import low.functions.FunctionEmitter;
import low.main.TypeInfos;
import low.module.LLVisitorMain;
import low.structs.StructEmitter;
import tokens.Lexer;
import tokens.Token;
import translate.front.Parser;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
public class ImportEmitter {

    private final LLVisitorMain visitor;
    private final Set<Type> tiposDeListasUsados;

    public ImportEmitter(LLVisitorMain visitor, Set<Type> tiposDeListasUsados) {
        this.visitor = visitor;
        this.tiposDeListasUsados = tiposDeListasUsados;
    }

    public String emit(ImportNode node) {
        try {

            String code = Files.readString(Path.of(node.path()));

            Lexer lexer = new Lexer(code);
            List<Token> tokens = lexer.tokenize();

            Parser parser = new Parser(tokens);
            List<ASTNode> imported = parser.parse();

            StringBuilder ir = new StringBuilder();

            for (ASTNode n : imported)
                coletarListas(n);

            for (ASTNode n : imported) {
                if (n instanceof StructNode struct) {

                    visitor.registerStructNode(struct.getName(), struct);

                    String llvmDef =
                            new StructEmitter(visitor).emit(struct);

                    visitor.addStructDefinition(llvmDef);
                }
            }

            for (ASTNode n : imported) {
                if (n instanceof ImplNode impl) {

                    String codeImpl = impl.accept(visitor);

                    visitor.addImplDefinition(codeImpl);
                }
            }

            for (ASTNode n : imported) {
                if (n instanceof FunctionNode fn) {

                    String name = fn.getName();

                    visitor.registerImportedFunction(name, fn);

                    Type returnType = fn.getReturnType();

                    String elemType = null;

                    if (returnType instanceof ListType listType) {
                        elemType = listType.elementType().name();
                    }

                    visitor.registerFunctionType(
                            name,
                            new TypeInfos(returnType, elemType)
                    );

                    ir.append(new FunctionEmitter(visitor).emit(fn))
                            .append("\n");
                }
            }

            return ir.toString();

        } catch (IOException e) {
            throw new RuntimeException(
                    "Failed to import module: " + node.path(),
                    e
            );
        }
    }

    private void coletarListas(ASTNode node) {

        if (node == null) return;

        if (node instanceof FunctionNode func) {

            // retorno
            Type retType = func.getReturnType();

            if (retType instanceof ListType listType) {
                registrarLista(listType);
            }

            // parâmetros
            for (ParamInfo param : func.getParameters()) {

                Type pType = param.type();

                if (pType instanceof ListType listType) {
                    registrarLista(listType);
                }
            }

            for (ASTNode stmt : func.getBody()) {
                coletarListas(stmt);
            }
        }

        // descer AST
        for (ASTNode child : node.getChildren()) {
            coletarListas(child);
        }

        if (node instanceof VariableDeclarationNode v) {

            Type t = v.getResolvedType();

            if (t instanceof ListType listType) {
                registrarLista(listType);
            }

            if (v.getInitializer() != null) {
                coletarListas(v.getInitializer());
            }
        }

        if (node instanceof ListNode list) {

            Type type = list.getType();

            if (type instanceof ListType listType) {
                registrarLista(listType);
            }

            list.getList().getElements()
                    .forEach(this::coletarListas);
        }

        if (node instanceof IfNode i) {

            coletarListas(i.condition);

            i.thenBranch.forEach(this::coletarListas);

            if (i.elseBranch != null)
                i.elseBranch.forEach(this::coletarListas);
        }

        if (node instanceof WhileNode w) {

            coletarListas(w.condition);

            w.body.forEach(this::coletarListas);
        }

        if (node instanceof ListAddNode add) {

            coletarListas(add.getListNode());
            coletarListas(add.getValuesNode());
        }

        if (node instanceof ListAddAllNode addAll) {

            addAll.getArgs().forEach(this::coletarListas);
        }
    }

    private void registrarLista(ListType listType) {

        tiposDeListasUsados.add(listType.elementType());
    }
}