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

import java.util.List;


public class ExecutorLinux {

    public static void main(String[] args) throws Exception {
        String filePath = args.length > 0 ? args[0] : "src/language/main.zd";

        // ===== FRONTEND =====
        FrontendPipeline frontend = new FrontendPipeline(filePath);
        List<ASTNode> ast = frontend.process();

        // ===== TYPE PIPELINE =====
        TypePipeline typePipeline = new TypePipeline(frontend.getParser());
        TypePipelineResult typeResult = typePipeline.process(ast);

        // ===== LOCALIZA MainAST =====
        MainAST mainAst = null;
        for (ASTNode n : ast) {
            if (n instanceof MainAST m) {
                mainAst = m;
                break;
            }
        }

        // ===== ESCAPE + LIFETIME =====
        EscapeInfo escapeInfo = new EscapeInfo(); // default (nada escapa)

        if (mainAst != null) {

            // ---- Escape analysis ----
            EscapeAnalyzer escapeAnalyzer = new EscapeAnalyzer();
            escapeInfo = escapeAnalyzer.analyze(mainAst.body);

            // ---- Lifetime analysis ----
            DeterministicLifetimeAnalyzer lifetime =
                    new DeterministicLifetimeAnalyzer(
                            typeResult.getSpecializer().getVariableTypes()
                    );

            var lastUse = lifetime.analyze(mainAst.body);

            System.out.println("=== LAST USE (STRUCTS) ===");
            lastUse.forEach((k, v) ->
                    System.out.println("  " + k + " -> " + v.getClass().getSimpleName()));
            System.out.println("==========================");

            // ---- Free insertion ----
            new FreeInsertionPass(lastUse).apply(mainAst.body);
        }

        // ===== DEBUG AST =====
        System.out.println("=== AST AFTER FREE INSERTION ===");
        ASTPrinter.printAST(ast);

        // ===== BACKEND LLVM =====
        LLVisitorMain llvmVisitor =
                new LLVisitorMain(typeResult.getSpecializer(), escapeInfo);

        System.out.println("[DEBUG ExecutorLinux] visitor no backend @"
                + System.identityHashCode(llvmVisitor));

        LLVMGenerator llgen = new LLVMGenerator(llvmVisitor);
        String llvm = llgen.generate(ast);

        // ===== TOOLCHAIN =====
        LLVMToolchain toolchain = new LLVMToolchain();
        String exePath = toolchain.buildExecutable(llvm);
        toolchain.runExecutable(exePath);

//        ASTInterpreter interpreter = new ASTInterpreter();
//        interpreter.run(ast);
    }
}





