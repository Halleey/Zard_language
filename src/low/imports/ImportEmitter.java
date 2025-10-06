package low.imports;

import ast.ASTNode;
import ast.functions.FunctionNode;
import ast.imports.ImportNode;
import ast.variables.VariableDeclarationNode;
import ast.variables.VariableNode;
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
    private final Set<String> tiposDeListasUsados = new HashSet<>();

    public ImportEmitter(LLVisitorMain visitor) {
        this.visitor = visitor;
    }

    public Set<String> getTiposDeListasUsados() {
        return tiposDeListasUsados;
    }

    public String emit(ImportNode node) {
        try {
            String path = node.path();
            String alias = node.alias();

            System.out.println("=== ImportEmitter DEBUG: Processing import " + path + " as " + alias + " ===");

            String code = Files.readString(Path.of(path));
            Lexer lexer = new Lexer(code);
            List<Token> tokens = lexer.tokenize();
            Parser parser = new Parser(tokens);
            List<ASTNode> ast = parser.parse();

            StringBuilder moduleIR = new StringBuilder();
            FunctionEmitter fnEmitter = new FunctionEmitter(visitor);

            // Coleta tipos de listas usados nas funções do import
            System.out.println(">>> Starting list type collection...");
            for (ASTNode n : ast) coletarListas(n);
            System.out.println(">>> Collected list types: " + tiposDeListasUsados);

            // Emite apenas funções, não statements executáveis
            System.out.println(">>> Emitting functions from module...");
            for (ASTNode n : ast) {
                if (n instanceof FunctionNode func) {
                    String qualified = alias + "." + func.getName();
                    String llvmName = qualified.replace('.', '_');

                    visitor.registerImportedFunction(qualified, func);
                    visitor.registerFunctionType(qualified, func.getReturnType());

                    String funcIR = fnEmitter.emit(func)
                            .replace("@" + func.getName() + "(", "@" + llvmName + "(");

                    moduleIR.append(funcIR).append("\n");
                    System.out.println("    - Function emitted: " + qualified + " (LLVM name: " + llvmName + ")");
                } else {
                    System.out.println("    - Skipping non-function node: " + n.getClass().getSimpleName());
                }
            }

            System.out.println("=== ImportEmitter DEBUG: Finished import " + alias + " ===\n");
            return moduleIR.toString();

        } catch (IOException e) {
            throw new RuntimeException("Failed to import module: " + node.path(), e);
        }
    }

    private void coletarListas(ASTNode node) {
        if (node == null) return;

        System.out.println("    [coletarListas] Visiting node: " + node.getClass().getSimpleName());

        if (node instanceof VariableDeclarationNode varDecl && varDecl.getType().startsWith("List")) {
            tiposDeListasUsados.add(varDecl.getType());
            System.out.println("        -> Found List declaration: " + varDecl.getName() + " type=" + varDecl.getType());
        } else if (node instanceof ListNode listNode) {
            String tipo = "List<" + listNode.getList().getElementType() + ">";
            tiposDeListasUsados.add(tipo);
            System.out.println("        -> Found List literal with element type: " + listNode.getList().getElementType());
        } else if (node instanceof FunctionNode func) {
            System.out.println("        -> Entering function: " + func.getName());
            for (ASTNode stmt : func.getBody()) coletarListas(stmt);
        } else if (node instanceof IfNode ifNode) {
            System.out.println("        -> Visiting IfNode condition");
            coletarListas(ifNode.condition);
            ifNode.thenBranch.forEach(this::coletarListas);
            if (ifNode.elseBranch != null) ifNode.elseBranch.forEach(this::coletarListas);
        } else if (node instanceof WhileNode whileNode) {
            System.out.println("        -> Visiting WhileNode condition");
            coletarListas(whileNode.condition);
            whileNode.body.forEach(this::coletarListas);
        } else if (node instanceof ListAddNode addNode) {
            ASTNode listNode = addNode.getListNode();

            // se for VariableNode, pega o tipo registrado no visitor
            String listType = null;
            if (listNode instanceof VariableNode varNode) {
                listType = visitor.getListElementType(varNode.getName());
            } else if (listNode instanceof ListNode listLiteral) {
                listType = "List<" + listLiteral.getList().getElementType() + ">";
            }

            if (listType != null) {
                tiposDeListasUsados.add(listType);
                System.out.println("        -> Detected ListAddNode for type: " + listType);
            }

            coletarListas(addNode.getValuesNode());
        }
     else if (node instanceof ListAddAllNode addAllNode) {
            System.out.println("        -> Visiting ListAddAllNode");
            coletarListas(addAllNode.getArgs());
        }
    }

    private void coletarListas(List<ASTNode> nodes) {
        if (nodes == null) return;
        nodes.forEach(this::coletarListas);
    }
}
