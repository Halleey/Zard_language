package translate.executors;

import ast.ASTNode;
import ast.TypeSpecializer;
import ast.exceptions.ReturnValue;
import ast.prints.ASTPrinter;
import ast.context.runtime.RuntimeContext;
import low.module.LLVMGenerator;
import low.module.LLVisitorMain;
import tokens.Lexer;
import tokens.Token;
import translate.front.Parser;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
public class WindowsExecutor {
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

            // === ESPECIALIZAÇÃO DE TIPOS ===
            System.out.println("Executando TypeSpecializer...");
            TypeSpecializer specializer = new TypeSpecializer();

            // Cria visitor e conecta com o specializer
            LLVisitorMain visitor = new LLVisitorMain(specializer);
            specializer.setVisitor(visitor);

            // Especializa tipos
            specializer.specialize(ast);

            // === GERAÇÃO DE LLVM IR ===
            LLVMGenerator generator = new LLVMGenerator(visitor);
            String llvmCode = generator.generate(ast);

            System.out.println("=== LLVM IR ===");
            System.out.println(llvmCode);

            // Salvar arquivo LLVM
            Path llPath = Path.of("programa.ll");
            Files.writeString(llPath, llvmCode);
            System.out.println("LLVM IR salvo em programa.ll");

            // Arquivos do runtime
            List<String> runtimeFiles = List.of(
                    "src/low/runtime/string/Stringz.c",
                    "src/low/runtime/inputs/InputUtil.c",
                    "src/low/runtime/ArrayList.c",
                    "src/low/runtime/ints/ArrayListInt.c",
                    "src/low/runtime/bool/ArrayListBool.c",
                    "src/low/runtime/doubles/ArrayListDouble.c",
                    "src/low/runtime/print/PrintList.c"
            );

            // Includes
            List<String> includeDirs = List.of(
                    "-Isrc/low/runtime/string",
                    "-Isrc/low/runtime/input",
                    "-Isrc/low/runtime/list",
                    "-Isrc/low/runtime/print"
            );

            // Montar comando clang
            List<String> cmdExe = new ArrayList<>();
            cmdExe.add("clang");
            cmdExe.add("programa.ll");
            cmdExe.addAll(runtimeFiles);
            cmdExe.addAll(includeDirs);
            cmdExe.add("-o");
            cmdExe.add("programa.exe");

            System.out.println("Executando clang para gerar executável...");
            ProcessBuilder pbExe = new ProcessBuilder(cmdExe);
            pbExe.inheritIO();
            Process processExe = pbExe.start();
            int exitCodeExe = processExe.waitFor();

            if (exitCodeExe == 0) {
                System.out.println("Executável gerado: programa.exe");
            } else {
                throw new RuntimeException("Falha ao linkar executável");
            }

            // Execução interpretada opcional
            RuntimeContext ctx = new RuntimeContext();
            for (ASTNode node : ast) {
                try {
                    node.evaluate(ctx);
                } catch (ReturnValue rv) {
                    break;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
