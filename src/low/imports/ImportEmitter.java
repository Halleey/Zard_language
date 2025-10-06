package low.imports;

import ast.ASTNode;
import ast.functions.FunctionNode;
import ast.imports.ImportNode;
import ast.variables.VariableDeclarationNode;
import low.functions.FunctionEmitter;
import low.module.LLVisitorMain;
import tokens.Lexer;
import tokens.Token;
import translate.Parser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ast.ifstatements.IfNode;

import ast.lists.ListAddAllNode;
import ast.lists.ListAddNode;
import ast.lists.ListNode;
import ast.loops.WhileNode;
public class ImportEmitter {
    private final LLVisitorMain visitor;
    private final Set<String> tiposDeListasUsados; // ⚡ compartilhado com MainEmitter

    public ImportEmitter(LLVisitorMain visitor, Set<String> tiposDeListasUsados) {
        this.visitor = visitor;
        this.tiposDeListasUsados = tiposDeListasUsados;
    }

    public Set<String> getTiposDeListasUsados() {
        return tiposDeListasUsados;
    }

    public String emit(ImportNode node) {
        try {
            String path = node.path();
            String alias = node.alias();


            String code = Files.readString(Path.of(path));
            Lexer lexer = new Lexer(code);
            List<Token> tokens = lexer.tokenize();
            Parser parser = new Parser(tokens);
            List<ASTNode> ast = parser.parse();


            for (ASTNode n : ast) coletarListas(n);


            StringBuilder moduleIR = new StringBuilder();
            FunctionEmitter fnEmitter = new FunctionEmitter(visitor);


            for (ASTNode n : ast) {
                if (n instanceof FunctionNode func) {
                    String qualified = alias + "." + func.getName();
                    String llvmName = qualified.replace('.', '_');

                    visitor.registerImportedFunction(qualified, func);
                    visitor.registerFunctionType(qualified, func.getReturnType());

                    String funcIR = fnEmitter.emit(func)
                            .replace("@" + func.getName() + "(", "@" + llvmName + "(");

                    moduleIR.append(funcIR).append("\n");

                }
            }


            return moduleIR.toString();

        } catch (IOException e) {
            throw new RuntimeException("Failed to import module: " + node.path(), e);
        }
    }

    private void coletarListas(ASTNode node) {
        if (node == null) return;


        if (node instanceof VariableDeclarationNode varDecl) {
            if (varDecl.getType() != null && varDecl.getType().startsWith("List")) {
                tiposDeListasUsados.add(varDecl.getType());

            }
            if (varDecl.initializer != null) coletarListas(varDecl.initializer);
        } else if (node instanceof ListNode listNode) {
            String tipo = "List<" + listNode.getList().getElementType() + ">";
            tiposDeListasUsados.add(tipo);

            listNode.getList().getElements().forEach(this::coletarListas);
        } else if (node instanceof FunctionNode func) {

            for (int i = 0; i < func.getParams().size(); i++) {
                String tipo = func.getParamTypes().get(i);
                if (tipo != null && tipo.startsWith("List")) {
                    tiposDeListasUsados.add(tipo);

                }
            }
            func.getBody().forEach(this::coletarListas);
        } else if (node instanceof IfNode ifNode) {
            coletarListas(ifNode.condition);
            ifNode.thenBranch.forEach(this::coletarListas);
            if (ifNode.elseBranch != null) ifNode.elseBranch.forEach(this::coletarListas);
        } else if (node instanceof WhileNode whileNode) {
            coletarListas(whileNode.condition);
            whileNode.body.forEach(this::coletarListas);
        } else if (node instanceof ListAddNode addNode) {
            coletarListas(addNode.getListNode());
            coletarListas(addNode.getValuesNode());
        } else if (node instanceof ListAddAllNode addAllNode) {
            addAllNode.getArgs().forEach(this::coletarListas);
        }
    }
}
