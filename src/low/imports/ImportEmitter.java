package low.imports;

import ast.ASTNode;
import ast.functions.FunctionNode;
import ast.imports.ImportNode;
import ast.structs.ImplNode;
import ast.structs.StructNode;
import ast.variables.VariableDeclarationNode;
import low.functions.FunctionEmitter;
import low.functions.TypeMapper;
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

import ast.ifstatements.IfNode;

import ast.lists.ListAddAllNode;
import ast.lists.ListAddNode;
import ast.lists.ListNode;
import ast.loops.WhileNode;
public class ImportEmitter {
    private final LLVisitorMain visitor;
    private final Set<String> tiposDeListasUsados;

    public ImportEmitter(LLVisitorMain visitor, Set<String> tiposDeListasUsados) {
        this.visitor = visitor;
        this.tiposDeListasUsados = tiposDeListasUsados;
    }

    public String emit(ImportNode node) {
        try {
            // ----- Carregar arquivo -----
            String code = Files.readString(Path.of(node.path()));
            Lexer lexer = new Lexer(code);
            List<Token> tokens = lexer.tokenize();
            Parser parser = new Parser(tokens);
            List<ASTNode> imported = parser.parse();

            StringBuilder ir = new StringBuilder();

            // ===============================
            // 1) REGISTRA LISTAS INTERNAS
            // ===============================
            for (ASTNode n : imported) {
                coletarListas(n);
            }

            // ===============================
            // 2) REGISTRA E EMITE STRUCTS
            // ===============================
            for (ASTNode n : imported) {
                if (n instanceof StructNode struct) {

                    // registrar com o nome NORMAL
                    visitor.registerStructNode(struct.getName(), struct);

                    // emitir imediatamente a definição LLVM
                    String llvmDef = new StructEmitter(visitor).emit(struct);
                    visitor.addStructDefinition(llvmDef);
                }
            }

            // ===============================
            // 3) REGISTRA E EMITE IMPLEMENTAÇÕES
            // ===============================
            for (ASTNode n : imported) {
                if (n instanceof ImplNode impl) {
                    ir.append(impl.accept(visitor));
                }
            }

            // ===============================
            // 4) REGISTRA E EMITE FUNÇÕES NORMAIS
            // ===============================
            for (ASTNode n : imported) {
                if (n instanceof FunctionNode fn) {

                    String name = fn.getName(); // sem módulo/alias
                    String llvmName = name;

                    visitor.registerImportedFunction(name, fn);

                    String srcType = fn.getReturnType();
                    String llvmType = new TypeMapper().toLLVM(srcType);
                    String elemType = null;

                    if (srcType.startsWith("List<") && srcType.endsWith(">")) {
                        elemType = srcType.substring(5, srcType.length() - 1);
                    }

                    visitor.registerFunctionType(
                            name,
                            new TypeInfos(srcType, llvmType, elemType)
                    );

                    // Emitir IR
                    ir.append(new FunctionEmitter(visitor).emit(fn))
                            .append("\n");
                }
            }

            return ir.toString();

        } catch (IOException e) {
            throw new RuntimeException("Failed to import module: " + node.path(), e);
        }
    }

    // coletar listas (igual ao seu)
    private void coletarListas(ASTNode node) {
        if (node == null) return;

        if (node instanceof FunctionNode func) {
            String retType = func.getReturnType();
            if (retType.startsWith("List<") && retType.endsWith(">")) {
                tiposDeListasUsados.add(retType);
            }
            for (String param : func.getParamTypes()) {
                if (param.startsWith("List<") && param.endsWith(">")) {
                    tiposDeListasUsados.add(param);
                }
            }
            func.getBody().forEach(this::coletarListas);
        }

        if (node instanceof VariableDeclarationNode v) {
            if (v.getType().startsWith("List<")) {
                tiposDeListasUsados.add(v.getType());
            }
            if (v.initializer != null) coletarListas(v.initializer);
        }

        if (node instanceof ListNode list) {
            tiposDeListasUsados.add("List<" + list.getList().getElementType() + ">");
            list.getList().getElements().forEach(this::coletarListas);
        }

        if (node instanceof IfNode i) {
            coletarListas(i.condition);
            i.thenBranch.forEach(this::coletarListas);
            if (i.elseBranch != null) i.elseBranch.forEach(this::coletarListas);
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
}