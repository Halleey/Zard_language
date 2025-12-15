package translate.executors;

import ast.home.MainAST;
import ast.prints.ASTPrinter;
import low.module.LLVMGenerator;
import low.module.LLVisitorMain;
import memory_manager.DeterministicLifetimeAnalyzer;
import memory_manager.EscapeInfo;
import memory_manager.FreeInsertionPass;
import memory_manager.TypePipelineResult;

import translate.front.ASTInterpreter;
import translate.front.FrontendPipeline;
import translate.front.TypePipeline;
import translate.llvm.LLVMToolchain;


public class ExecutorLinux {

    public static void main(String[] args) throws Exception {
        String filePath = args.length > 0 ? args[0] : "src/language/main.zd";

        FrontendPipeline frontend = new FrontendPipeline(filePath);
        var ast = frontend.process();

        EscapeInfo escapeInfo = frontend.getEscapeInfo();

        TypePipeline typePipeline = new TypePipeline(frontend.getParser());
        TypePipelineResult typeResult = typePipeline.process(ast);

        DeterministicLifetimeAnalyzer lifetime =
                new DeterministicLifetimeAnalyzer(
                        typeResult.getSpecializer().getVariableTypes()
                );

        var lastUse = lifetime.analyze(ast);

        System.out.println("=== LAST USE (STRUCTS) ===");
        lastUse.forEach((k, v) ->
                System.out.println("  " + k + " -> " + v.getClass().getSimpleName()));
        System.out.println("==========================");

        if (ast instanceof MainAST mainAst) {
            new FreeInsertionPass(lastUse).apply(mainAst.body);
        }


        System.out.println("=== AST AFTER FREE INSERTION ===");
        ASTPrinter.printAST(ast);

//        LLVisitorMain llvmVisitor = typeResult.getVisitor().fork();
//        llvmVisitor.setEscapeInfo(escapeInfo);
//
//        System.out.println("[DEBUG ExecutorLinux] visitor no backend @"
//                + System.identityHashCode(llvmVisitor));
//
//        LLVMGenerator llgen = new LLVMGenerator(llvmVisitor);
//        String llvm = llgen.generate(ast);
//
//        LLVMToolchain toolchain = new LLVMToolchain();
//        String exePath = toolchain.buildExecutable(llvm);
//        toolchain.runExecutable(exePath);



        ASTInterpreter interpreter = new ASTInterpreter();
        interpreter.run(ast);
    }
}
