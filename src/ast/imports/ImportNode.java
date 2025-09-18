package ast.imports;

import ast.ASTNode;
import ast.exceptions.ReturnValue;
import ast.runtime.RuntimeContext;
import expressions.TypedValue;
import tokens.Lexer;
import tokens.Token;
import translate.Parser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class ImportNode extends ASTNode {

    private final String path;

    public ImportNode(String path) {
        this.path = path;
    }



    @Override
    public TypedValue evaluate(RuntimeContext ctx) {
      try {
          String code = Files.readString(Path.of(path));

          Lexer lexer = new Lexer(code);
          List<Token> tokens = lexer.tokenize();
          Parser parser = new Parser(tokens);
          List<ASTNode> ast = parser.parse();

          for (ASTNode node : ast) {
              try {
                  node.evaluate(ctx);
              } catch (ReturnValue rv) {
                  continue;
              }
          }

      } catch (IOException e) {
          throw new RuntimeException(e);
      }

        return null;
    }

    @Override
    public void print(String prefix) {
        System.out.println(prefix + "Import: \"" + path + "\"");
    }
}
