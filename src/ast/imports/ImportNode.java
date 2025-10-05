package ast.imports;

import ast.ASTNode;
import ast.functions.FunctionNode;
import ast.runtime.RuntimeContext;
import ast.expressions.TypedValue;
import low.module.LLVMEmitVisitor;
import tokens.Lexer;
import tokens.Token;
import translate.Parser;
import ast.variables.VariableDeclarationNode;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;


public class ImportNode extends ASTNode {
    private final String path;
    private final String alias;

    public ImportNode(String path, String alias) {
        if (alias == null || alias.isEmpty()) {
            throw new RuntimeException("Alias obrigatório para import de " + path);
        }
        this.path = path;
        this.alias = alias;
    }

    @Override
    public String accept(LLVMEmitVisitor visitor) {
        return visitor.visit(this);
    }

    @Override
    public TypedValue evaluate(RuntimeContext ctx) {
        try {
            String code = Files.readString(Path.of(path));
            Lexer lexer = new Lexer(code);
            List<Token> tokens = lexer.tokenize();
            Parser parser = new Parser(tokens);
            List<ASTNode> ast = parser.parse();

            // Contexto isolado para o módulo
            RuntimeContext importCtx = new RuntimeContext();

            for (ASTNode node : ast) {
                if (node instanceof FunctionNode funcNode) {
                    importCtx.declareVariable(funcNode.getName(), new TypedValue("function", funcNode));

                    // Também registra no contexto global com prefixo: alias.nomeFunc
                    String qualifiedName = alias + "." + funcNode.getName();
                    ctx.declareVariable(qualifiedName, new TypedValue("function", funcNode));
                }
                else if (node instanceof VariableDeclarationNode varNode) {
                    varNode.evaluate(importCtx);

                    // registra variável também com alias.
                    String qualifiedName = alias + "." + varNode.getName();
                    TypedValue val = importCtx.getVariable(varNode.getName());
                    ctx.declareVariable(qualifiedName, val);
                }
            }

            // Também guarda o namespace completo, caso o usuário queira usar alias diretamente
            ctx.declareVariable(alias, new TypedValue("namespace", importCtx));

        } catch (IOException e) {
            throw new RuntimeException("Erro ao importar arquivo: " + path, e);
        }

        return new TypedValue("null", null);
    }



    @Override
    public void print(String prefix) {
        System.out.println(prefix + "Import: \"" + path + "\" as " + alias);
    }

    public String alias() {
        return alias;
    }

    public String path() {
        return path;
    }
}
