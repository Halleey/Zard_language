package translate.front;

import ast.ASTNode;
import ast.functions.FunctionNode;
import ast.structs.ImplNode;
import ast.structs.StructNode;
import ast.variables.VariableDeclarationNode;
import tokens.Lexer;
import tokens.Token;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Preprocessor {

    public static class Result {
        public List<Token> tokens = new ArrayList<>();
        public Map<String, Map<String, String>> structs = new HashMap<>();
        public List<ASTNode> impls = new ArrayList<>();
        public List<ASTNode> functions = new ArrayList<>();
    }

    public Result processImports(List<Token> original) throws Exception {

        Result result = new Result();

        for (int i = 0; i < original.size(); i++) {
            Token t = original.get(i);

            // Quando NÃO for import → copia token normalmente
            // Não é "import" → copia normalmente
            if (!(t.getType() == Token.TokenType.KEYWORD && t.getValue().equals("import"))) {
                result.tokens.add(t);
                continue;
            }


            // ENCONTROU IMPORT
            // Encontrou import
            String path = original.get(i+1).getValue();


            i += 2; // apenas pula STRING e ';' (o loop ainda fará i++)


            String code = Files.readString(Path.of(path));

            Lexer lx = new Lexer(code);
            List<Token> importedTokens = lx.tokenize();

            Parser p = new Parser(importedTokens);
            List<ASTNode> importedAst = p.parse();

            // Coleta structs
            for (ASTNode node : importedAst) {
                if (node instanceof StructNode s) {
                    Map<String, String> fieldMap = new HashMap<>();
                    for (VariableDeclarationNode f : s.getFields()) {
                        fieldMap.put(f.getName(), f.getType());
                    }
                    result.structs.put(s.getName(), fieldMap);
                }

                else if (node instanceof ImplNode impl) {
                    result.impls.add(impl);
                }

                else if (node instanceof FunctionNode fn) {
                    result.functions.add(fn);
                }
            }

            // NÃO INSERIR importedTokens aqui!
        }

        return result;
    }
}
