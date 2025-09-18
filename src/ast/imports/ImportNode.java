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

            // Cria um sub-contexto isolado para o alias
            RuntimeContext importCtx = new RuntimeContext();

            // Executa apenas declarações de função e variáveis
            for (ASTNode node : ast) {
                if (node instanceof FunctionNode || node instanceof VariableDeclarationNode) {
                    try {
                        node.evaluate(importCtx);
                    } catch (ReturnValue rv) {
                        continue;
                    }
                }
            }

            // Registra o namespace no contexto principal
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
