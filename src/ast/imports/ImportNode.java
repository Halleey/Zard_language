package ast.imports;

import ast.ASTNode;
import ast.exceptions.ReturnValue;
import ast.functions.FunctionNode;
import ast.runtime.RuntimeContext;
import expressions.TypedValue;
import tokens.Lexer;
import tokens.Token;
import translate.Parser;
import variables.VariableDeclarationNode;

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
    public TypedValue evaluate(RuntimeContext ctx) {
        try {
            String code = Files.readString(Path.of(path));
            Lexer lexer = new Lexer(code);
            List<Token> tokens = lexer.tokenize();
            Parser parser = new Parser(tokens);
            List<ASTNode> ast = parser.parse();

            RuntimeContext importCtx = new RuntimeContext();

            for (ASTNode node : ast) {
                if (node instanceof FunctionNode funcNode) {
                    // Aqui armazenamos o FunctionNode no namespace
                    importCtx.declareVariable(funcNode.name, new TypedValue("function", funcNode));
                } else if (node instanceof VariableDeclarationNode varNode) {
                    varNode.evaluate(importCtx);
                }
            }

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
}
