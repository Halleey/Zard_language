package translate;

import ast.ASTNode;
import ast.exceptions.ReturnValue;
import ast.prints.ASTPrinter;
import ast.runtime.RuntimeContext;
import low.module.LLVMGenerator;
import tokens.Lexer;
import tokens.Token;
import java.nio.file.Files;
import java.nio.file.Path;

import java.util.ArrayList;
import java.util.List;


import java.nio.file.*;
import java.util.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.io.*;
public class ExecutorLinux {
    public static void main(String[] args) throws Exception {
        // Detectar sistema operacional
        String os = System.getProperty("os.name").toLowerCase();
        boolean isWindows = os.contains("win");

        // Caminho do arquivo fonte
        String filePath = args.length > 0 ? args[0] : "src/language/main.zd";
        String code = Files.readString(Path.of(filePath));

        // Lexer + Parser
        Lexer lexer = new Lexer(code);
        List<Token> tokens = lexer.tokenize();
        Parser parser = new Parser(tokens);
        List<ASTNode> ast = parser.parse();

        System.out.println("=== AST ===");
        ASTPrinter.printAST(ast);


        // Gerar LLVM IR
//        LLVMGenerator llvmGen = new LLVMGenerator();
//        String llvmCode = llvmGen.generate(ast);
//
//        System.out.println("=== LLVM IR ===");
//        System.out.println(llvmCode);
//
//        // Salvar o IR
//        Path llPath = Path.of("programa.ll");
//        Files.writeString(llPath, llvmCode);
//        System.out.println("LLVM IR salvo em programa.ll");
//
//        // --- Etapa 1: Otimizar com o LLVM opt ---
//        Path optimizedLL = Path.of("programa_opt.ll");
//
//        System.out.println("Executando otimizador LLVM (opt -passes=dce)...");
//        List<String> optCmd = new ArrayList<>();
//        optCmd.add("opt");
//        optCmd.add("-passes=dce,instcombine,mem2reg"); // passes simples úteis
//        optCmd.add(llPath.toString());
//        optCmd.add("-S");
//        optCmd.add("-o");
//        optCmd.add(optimizedLL.toString());
//
//        ProcessBuilder pbOpt = new ProcessBuilder(optCmd);
//        pbOpt.inheritIO();
//        Process pOpt = pbOpt.start();
//        int exitOpt = pOpt.waitFor();
//        if (exitOpt != 0) {
//            throw new RuntimeException("Falha ao rodar otimizador LLVM (opt)");
//        }
//
//        System.out.println("Arquivo otimizado salvo em " + optimizedLL);
//
//        // --- Etapa 2: Compilar executável final ---
//        List<String> runtimeFiles = List.of(
//                "src/helpers/string/Stringz.c",
//                "src/helpers/inputs/InputUtil.c",
//                "src/helpers/ArrayList.c",
//                "src/helpers/ints/ArrayListInt.c",
//                "src/helpers/bool/ArrayListBool.c",
//                "src/helpers/doubles/ArrayListDouble.c",
//                "src/helpers/print/PrintList.c",
//                "src/helpers/string/StringComparators.c"
//        );
//
//        List<String> includeDirs = List.of(
//                "-Isrc/helpers/string",
//                "-Isrc/helpers/inputs",
//                "-Isrc/helpers/list",
//                "-Isrc/helpers/print",
//                "-Isrc/helpers/maps"
//        );
//
//        String exeName = isWindows ? "programa.exe" : "programa";
//
//        List<String> cmdExe = new ArrayList<>();
//        cmdExe.add("clang");
//        cmdExe.add(optimizedLL.toString());
//        cmdExe.addAll(runtimeFiles);
//        cmdExe.addAll(includeDirs);
//        cmdExe.add("-o");
//        cmdExe.add(exeName);
//
//        System.out.println("Executando clang para gerar executável...");
//        ProcessBuilder pbExe = new ProcessBuilder(cmdExe);
//        pbExe.inheritIO();
//        Process processExe = pbExe.start();
//        int exitCodeExe = processExe.waitFor();
//
//        if (exitCodeExe == 0) {
//            System.out.println("Executável gerado: " + exeName);
//
//            System.out.println("Executando programa final...");
//            ProcessBuilder pbRun = new ProcessBuilder(isWindows ? exeName : "./" + exeName);
//            pbRun.inheritIO();
//            Process pRun = pbRun.start();
//            int exitRun = pRun.waitFor();
//            System.out.println("Programa finalizado com código: " + exitRun);
//        } else {
//            throw new RuntimeException("Falha ao linkar executável");
//        }
        RuntimeContext ctx = new RuntimeContext();
        for (ASTNode node : ast) {
            try {
                node.evaluate(ctx);
            } catch (ReturnValue rv) {
                break;
            }
        }
    }
}
