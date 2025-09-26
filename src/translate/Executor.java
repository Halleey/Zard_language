package translate;

import ast.ASTNode;
import ast.exceptions.ReturnValue;
import ast.runtime.RuntimeContext;
import low.module.LLVMGenerator;
import ast.prints.ASTPrinter;
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
//            LLVMGenerator llvmGen = new LLVMGenerator();
//            String llvmCode = llvmGen.generate(ast);
//
//            System.out.println("=== LLVM IR ===");
//            System.out.println(llvmCode);
//
//            // Salvar arquivo LLVM
//            Path llPath = Path.of("programa.ll");
//            Files.writeString(llPath, llvmCode);
//            System.out.println("LLVM IR salvo em programa.ll");
//
//            // arquivos C do runtime
//            List<String> runtimeFiles = List.of(
//                    "src/low/runtime/DynValue.c",
//                    "src/low/runtime/ArrayList.c",
//                    "src/low/runtime/PrintList.c",
//                    "src/low/runtime/InputUtil.c"
//            );
//            // Comando para gerar executável
//            List<String> cmdExe = new ArrayList<>();
//            cmdExe.add("clang");
//            cmdExe.add("programa.ll");
//            cmdExe.addAll(runtimeFiles);
//            cmdExe.add("-o");
//            cmdExe.add("programa.exe");
//
//            ProcessBuilder pbExe = new ProcessBuilder(cmdExe);
//            pbExe.inheritIO();
//            Process processExe = pbExe.start();
//            int exitCodeExe = processExe.waitFor();
//            if (exitCodeExe == 0) System.out.println("Executável gerado: programa.exe");
//
// Comando para gerar assembly só do LLVM IR
//            List<String> cmdAsmPure = new ArrayList<>();
//            cmdAsmPure.add("clang");
//            cmdAsmPure.add("-S");          // gera assembly
//            cmdAsmPure.add("programa.ll");
//            cmdAsmPure.add("-o");
//            cmdAsmPure.add("programa.s");
//
//            ProcessBuilder pbAsmPure = new ProcessBuilder(cmdAsmPure);
//            pbAsmPure.inheritIO();
//            Process processAsmPure = pbAsmPure.start();
//            int exitCodeAsmPure = processAsmPure.waitFor();
//            if (exitCodeAsmPure == 0) System.out.println("Assembly puro gerado: programa.s");
//
//// Comando para gerar assembly do executável completo (com runtime)
//            List<String> cmdAsmFull = new ArrayList<>();
//            cmdAsmFull.add("clang");
//            cmdAsmFull.add("-S");          // gera assembly
//            cmdAsmFull.add("programa.ll");
//            cmdAsmFull.addAll(runtimeFiles);
//
//            ProcessBuilder pbAsmFull = new ProcessBuilder(cmdAsmFull);
//            pbAsmFull.inheritIO();
//            Process processAsmFull = pbAsmFull.start();
//            int exitCodeAsmFull = processAsmFull.waitFor();
//            if (exitCodeAsmFull == 0) System.out.println("Assembly completo gerado (um .s por arquivo)");

            // Execução na AST (interpretação)
            System.out.println("=== Execution ===");
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

