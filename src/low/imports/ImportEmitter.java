package low.imports;

import ast.ASTNode;
import ast.functions.FunctionNode;
import ast.functions.ParamInfo;
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
            String code = Files.readString(Path.of(node.path()));
            Lexer lexer = new Lexer(code);
            List<Token> tokens = lexer.tokenize();
            Parser parser = new Parser(tokens);
            List<ASTNode> imported = parser.parse();

            StringBuilder ir = new StringBuilder();

            for (ASTNode n : imported) {
                coletarListas(n);
            }

            for (ASTNode n : imported) {
                if (n instanceof StructNode struct) {
                    visitor.registerStructNode(struct.getName(), struct);
                    String llvmDef = new StructEmitter(visitor).emit(struct);
                    visitor.addStructDefinition(llvmDef);
                }
            }

            // 👇 AQUI! Agora que Set existe no visitor, criamos Set<int>
            visitor.getTypeSpecializer().createSpecializedStructsFromInferences();

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

                    ir.append(new FunctionEmitter(visitor).emit(fn)).append("\n");
                }
            }

            return ir.toString();

        } catch (IOException e) {
            throw new RuntimeException("Failed to import module: " + node.path(), e);
        }
    }


    private void coletarListas(ASTNode node) {
        if (node == null) return;

        if (node instanceof FunctionNode func) {

            // ===== tipo de retorno =====
            String retType = func.getReturnType();
            if (retType != null
                    && retType.startsWith("List<")
                    && retType.endsWith(">")) {
                tiposDeListasUsados.add(retType);
            }

            // ===== tipos de parâmetros =====
            for (ParamInfo param : func.getParameters()) {
                String type = param.type();
                if (type != null
                        && type.startsWith("List<")
                        && type.endsWith(">")) {
                    tiposDeListasUsados.add(type);
                }
            }

            // ===== corpo da função =====
            for (ASTNode stmt : func.getBody()) {
                coletarListas(stmt);
            }
        }

        // continua descendo a AST para outros nós
        for (ASTNode child : node.getChildren()) {
            coletarListas(child);
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
