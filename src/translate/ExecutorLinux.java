package translate;

import ast.ASTNode;
import ast.TypeSpecializer;
import ast.exceptions.ReturnValue;
import ast.home.MainAST;
import ast.prints.ASTPrinter;
import ast.runtime.RuntimeContext;
import low.module.LLVMGenerator;
import low.module.LLVisitorMain;
import tokens.Lexer;
import tokens.Token;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import ast.*;
import translate.identifiers.MethodDesugarer;


import java.nio.file.*;
import java.util.*;

public class ExecutorLinux {

    public static void main(String[] args) throws Exception {
        String os = System.getProperty("os.name").toLowerCase();
        boolean isWindows = os.contains("win");

        String filePath = args.length > 0 ? args[0] : "src/language/main.zd";
        String code = Files.readString(Path.of(filePath));

        Lexer lexer = new Lexer(code);
        List<Token> tokens = lexer.tokenize();
        Parser parser = new Parser(tokens);
        List<ASTNode> ast = parser.parse();

        //System.out.println("=== AST (antes da especialização) ===");
        //ASTPrinter.printAST(ast);

        // dessugar dos métodos de impl (injeta receiver, ajusta retorno, etc.)
        MethodDesugarer desugarer = new MethodDesugarer();
        desugarer.desugar(ast);

        System.out.println("Executando TypeSpecializer...");
        TypeSpecializer specializer = new TypeSpecializer();

        LLVisitorMain visitor = new LLVisitorMain(specializer);
        specializer.setVisitor(visitor);

        // registra structs antes de especializar
        for (ASTNode n : ast) {
            if (n instanceof MainAST mainAst) {
                visitor.registrarStructs(mainAst);
            }
        }

        specializer.specialize(ast);

        System.out.println("=== AST (após especialização de tipos) ===");
        ASTPrinter.printAST(ast);

        LLVMGenerator llvmGen = new LLVMGenerator(visitor);
        String llvmCode = llvmGen.generate(ast);
        System.out.println("LLVM GERADO");
       // System.out.println(llvmCode);
        Path llPath = Path.of("programa.ll");
        Files.writeString(llPath, llvmCode);
        System.out.println("LLVM IR salvo em programa.ll");

        Path optimizedLL = Path.of("programa_opt.ll");
        System.out.println("Executando otimizador LLVM...");

        String passes = String.join(",",
                "mem2reg", "sroa", "early-cse", "gvn-hoist",
                "dce", "adce", "reassociate",
                "loop-simplify", "loop-rotate",
                "loop-unroll", "loop-vectorize"
        );

        List<String> optCmd = List.of(
                "opt",
                "-passes=" + passes,
                llPath.toString(),
                "-S",
                "-o", optimizedLL.toString()
        );

        ProcessBuilder pbOpt = new ProcessBuilder(optCmd);
        pbOpt.inheritIO();
        Process pOpt = pbOpt.start();
        int exitOpt = pOpt.waitFor();
        if (exitOpt != 0) throw new RuntimeException("Falha ao rodar otimizador LLVM (opt)");

        System.out.println("Arquivo otimizado salvo em " + optimizedLL);

        String asmExt = isWindows ? "asm" : "s";
        Path asmPath = Path.of("programa." + asmExt);
        System.out.println("Gerando assembly com llc...");

        List<String> llcCmd = List.of(
                "llc",
                "-filetype=asm",
                "-O2",
                optimizedLL.toString(),
                "-o",
                asmPath.toString()
        );

        ProcessBuilder pbLlc = new ProcessBuilder(llcCmd);
        pbLlc.inheritIO();
        Process pLlc = pbLlc.start();
        int exitLlc = pLlc.waitFor();
        if (exitLlc != 0) throw new RuntimeException("Falha ao gerar assembly (llc)");

        System.out.println("Assembly salvo em " + asmPath);

        List<String> runtimeFiles = List.of(
                "src/helpers/string/Stringz.c",
                "src/helpers/inputs/InputUtil.c",
                "src/helpers/ArrayList.c",
                "src/helpers/ints/ArrayListInt.c",
                "src/helpers/bool/ArrayListBool.c",
                "src/helpers/doubles/ArrayListDouble.c",
                "src/helpers/print/PrintList.c",
                "src/helpers/string/StringComparators.c"
        );

        List<String> includeDirs = List.of(
                "-Isrc/helpers/string",
                "-Isrc/helpers/inputs",
                "-Isrc/helpers/list",
                "-Isrc/helpers/print",
                "-Isrc/helpers/maps"
        );

        String exeName = isWindows ? "programa.exe" : "programa";

        List<String> cmdExe = new ArrayList<>();
        cmdExe.add("clang");
        cmdExe.add(optimizedLL.toString());
        cmdExe.addAll(runtimeFiles);
        cmdExe.addAll(includeDirs);
        cmdExe.add("-o");
        cmdExe.add(exeName);

        System.out.println("Executando clang para gerar executável...");
        ProcessBuilder pbExe = new ProcessBuilder(cmdExe);
        pbExe.inheritIO();
        Process processExe = pbExe.start();
        int exitCodeExe = processExe.waitFor();

        if (exitCodeExe == 0) {
            System.out.println("Executável gerado: " + exeName);
            System.out.println("Executando programa final...");
            ProcessBuilder pbRun = new ProcessBuilder(isWindows ? exeName : "./" + exeName);
            pbRun.inheritIO();
            Process pRun = pbRun.start();
            int exitRun = pRun.waitFor();
            System.out.println("Programa finalizado com código: " + exitRun);
        } else {
            throw new RuntimeException("Falha ao linkar executável");
        }
        RuntimeContext runtimeContext = new RuntimeContext();
        for (ASTNode node: ast) {
            node.evaluate(runtimeContext);
        }
    }
}

