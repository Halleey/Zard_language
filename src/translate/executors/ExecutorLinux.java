package translate.executors;

import ast.ASTNode;
import ast.home.MainAST;
import ast.prints.ASTPrinter;
import low.module.LLVMGenerator;
import low.module.LLVisitorMain;
import memory_manager.*;

import translate.front.ASTInterpreter;
import translate.front.FrontendPipeline;
import translate.front.TypePipeline;
import translate.llvm.LLVMToolchain;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
public class ExecutorLinux {

    public static void main(String[] args) throws Exception {
        String filePath = args.length > 0 ? args[0] : "src/language/main.zd";

        // ========= FRONTEND =========
        FrontendPipeline frontend = new FrontendPipeline(filePath);
        List<ASTNode> ast = frontend.process();

        // ========= ESCAPE ANALYSIS =========
        EscapeAnalyzer escapeAnalyzer = new EscapeAnalyzer();
        EscapeInfo escapeInfo = escapeAnalyzer.analyze(ast);

        System.out.println("=== Escape Analysis Results ===");
        escapeInfo.getMap().forEach((k, v) ->
                System.out.println("  " + k + " -> escapes? " + v));
        System.out.println("================================");

        // ========= TYPE PIPELINE =========
        TypePipeline typePipeline = new TypePipeline(frontend.getParser());
        TypePipelineResult typeResult = typePipeline.process(ast);

        // ========= LIFETIME ANALYSIS =========
        MainAST mainAst = null;
        for (ASTNode n : ast) {
            if (n instanceof MainAST m) {
                mainAst = m;
                break;
            }
        }

        if (mainAst != null) {

            Map<String, String> varTypesForLifetime =
                    typeResult.getSpecializer().getVariableTypes();
            // ↑ JÁ É STRING, NÃO INVENTA MÉTODO

            DeterministicLifetimeAnalyzer lifetime =
                    new DeterministicLifetimeAnalyzer(varTypesForLifetime);

            var lastUse = lifetime.analyze(mainAst.body);

            System.out.println("=== LAST USE (STRUCTS) ===");
            lastUse.forEach((k, v) ->
                    System.out.println("  " + k + " -> " + v.getClass().getSimpleName()));
            System.out.println("==========================");

            new FreeInsertionPass(lastUse).apply(mainAst.body);

            System.out.println("=== AST AFTER FREE INSERTION ===");
            ASTPrinter.printAST(ast);
        }

        // ========= LLVM BACKEND =========
        LLVisitorMain llvmVisitor = typeResult.getVisitor().fork();
        llvmVisitor.setEscapeInfo(escapeInfo);

        System.out.println(
                "[DEBUG ExecutorLinux] LLVM visitor @"
                        + System.identityHashCode(llvmVisitor)
        );

        LLVMGenerator llgen = new LLVMGenerator(llvmVisitor);
        String llvm = llgen.generate(ast);

        LLVMToolchain toolchain = new LLVMToolchain();
        String exePath = toolchain.buildExecutable(llvm);
        toolchain.runExecutable(exePath);

        // ========= INTERPRETER (opcional) =========
        // ASTInterpreter interpreter = new ASTInterpreter();
        // interpreter.run(ast);
    }
}
