package translate;

import ast.ASTNode;
import ast.exceptions.ReturnValue;
import ast.runtime.RuntimeContext;
import low.module.LLVMGenerator;
import ast.prints.ASTPrinter;
import low.module.LLVisitorMain;
import tokens.Lexer;
import tokens.Token;
import java.nio.file.Files;
import java.nio.file.Path;

import java.util.ArrayList;
import java.util.List;
public class Executor {
    public static void main(String[] args) {
        try {
            String filePath = args.length > 0 ? args[0] : "src/language/main.zd";
            String code = Files.readString(Path.of(filePath));

            // Lexer + Parser
            Lexer lexer = new Lexer(code);
            List<Token> tokens = lexer.tokenize();
            Parser parser = new Parser(tokens);
            List<ASTNode> ast = parser.parse();

            System.out.println("=== AST ===");
            ASTPrinter.printAST(ast);

            // LLVM
            LLVMGenerator llvmGen = new LLVMGenerator();
            String llvmCode = llvmGen.generate(ast);

            System.out.println("=== LLVM IR ===");
            System.out.println(llvmCode);

            // Salvar arquivo LLVM
            Path llPath = Path.of("programa.ll");
            Files.writeString(llPath, llvmCode);
            System.out.println("LLVM IR salvo em programa.ll");

            //  arquivos C do runtime
            List<String> runtimeFiles = List.of(
                    "src/low/runtime/ArrayList.c",
                    "src/low/runtime/DynValue.c",
                    "src/low/runtime/PrintList.c"
            );

            // Comando completo do clang
            List<String> cmd = new ArrayList<>();
            cmd.add("clang");
            cmd.add("programa.ll");
            cmd.addAll(runtimeFiles);
            cmd.add("-o");
            cmd.add("programa.exe");

            // Executar clang
            ProcessBuilder pb = new ProcessBuilder(cmd);
            pb.inheritIO();
            Process process = pb.start();
            int exitCode = process.waitFor();
            if (exitCode == 0) System.out.println("Executável gerado: programa.exe");

            System.out.println("=== Execution ===");
            // Opcional: executar no interpretador também
            // RuntimeContext ctx = new RuntimeContext();
            // for (ASTNode node : ast) {
            //     try { node.evaluate(ctx); }
            //     catch (ReturnValue rv) { break; }
            // }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
