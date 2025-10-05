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

            moduleIR.append("""
                declare i32 @printf(i8*, ...)
                declare i32 @getchar()
                declare i8* @malloc(i64)
                declare i8* @arraylist_create(i64)
                declare void @clearList(%ArrayList*)
                declare void @freeList(%ArrayList*)

                %String = type { i8*, i64 }
                %ArrayList = type opaque
                """).append("\n");

            FunctionEmitter fnEmitter = new FunctionEmitter(visitor);
            for (ASTNode n : ast) {
                if (n instanceof FunctionNode func) {
                    String qualified = alias + "." + func.getName();
                    String llvmName = qualified.replace('.', '_');

                    // Registra função no visitor
                    visitor.registerImportedFunction(qualified, func);
                    visitor.registerFunctionType(qualified, func.getReturnType());

                    // Gera IR da função, renomeando o símbolo
                    String funcIR = fnEmitter.emit(func)
                            .replace("@" + func.getName() + "(", "@" + llvmName + "(");

                    moduleIR.append(funcIR).append("\n");
                }
            }

            StringBuilder declares = new StringBuilder();
            TypeMapper typeMapper = new TypeMapper();
            for (ASTNode n : ast) {
                if (n instanceof FunctionNode func) {
                    String qualified = alias + "." + func.getName();
                    String llvmName = qualified.replace('.', '_');
                    String retType = typeMapper.toLLVM(func.getReturnType());
                    List<String> paramTypes = func.getParamTypes().stream()
                            .map(typeMapper::toLLVM)
                            .toList();

                    declares.append("declare ")
                            .append(retType)
                            .append(" @").append(llvmName).append("(")
                            .append(String.join(", ", paramTypes))
                            .append(")\n");
                }
            }

            String llFileName = Path.of(path).toString().replace(".zd", ".ll");
            Files.writeString(Path.of(llFileName), moduleIR.toString());
            System.out.println("LLVM IR gerado para módulo importado: " + llFileName);

            return "; imported module " + path + " as " + alias + "\n" + declares;

        } catch (IOException e) {
            throw new RuntimeException("Failed to import module: " + node.path(), e);
        }
    }
}
