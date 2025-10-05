package low.imports;

import ast.ASTNode;
import ast.functions.FunctionNode;
import ast.imports.ImportNode;
import low.functions.FunctionEmitter;
import low.functions.TypeMapper;
import low.module.LLVisitorMain;
import tokens.Lexer;
import tokens.Token;
import translate.Parser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
public class ImportEmitter {
    private final LLVisitorMain visitor;

    public ImportEmitter(LLVisitorMain visitor) {
        this.visitor = visitor;
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

            StringBuilder moduleIR = new StringBuilder();

            FunctionEmitter fnEmitter = new FunctionEmitter(visitor);

            // Para cada função, gera IR com prefixo do alias e registra
            for (ASTNode n : ast) {
                if (n instanceof FunctionNode func) {
                    String qualified = alias + "." + func.getName();
                    String llvmName = qualified.replace('.', '_');

                    visitor.registerImportedFunction(qualified, func);
                    visitor.registerFunctionType(qualified, func.getReturnType());

                    // IR da função com nome qualificado
                    String funcIR = fnEmitter.emit(func)
                            .replace("@" + func.getName() + "(", "@" + llvmName + "(");

                    moduleIR.append(funcIR).append("\n");
                }
            }

            // REMOVIDO: declarações redundantes de 'declare'
            // Não é necessário declarar funções que já têm 'define'

            return moduleIR.toString();

        } catch (IOException e) {
            throw new RuntimeException("Failed to import module: " + node.path(), e);
        }
    }
}
