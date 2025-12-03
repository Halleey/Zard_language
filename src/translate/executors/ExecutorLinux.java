package translate.executors;

import low.module.LLVMGenerator;
import low.module.LLVisitorMain;
import translate.front.ASTInterpreter;
import translate.front.FrontendPipeline;
import translate.front.TypePipeline;
import translate.llvm.LLVMToolchain;

public class ExecutorLinux {

    public static void main(String[] args) throws Exception {
        String filePath = args.length > 0 ? args[0] : "src/language/main.zd";

        FrontendPipeline frontend = new FrontendPipeline(filePath);
        var ast = frontend.process();

        TypePipeline typePipeline = new TypePipeline(frontend.getParser());
//        LLVisitorMain visitor = typePipeline.process(ast);
//
//        LLVMGenerator llgen = new LLVMGenerator(visitor);
//        System.out.println("[DEBUG ExecutorLinux] visitor no backend @"
//                + System.identityHashCode(visitor));
//
//        String llvm = llgen.generate(ast);
//        LLVMToolchain toolchain = new LLVMToolchain();
//        String exePath = toolchain.buildExecutable(llvm);
//
//        toolchain.runExecutable(exePath);

        ASTInterpreter interpreter = new ASTInterpreter();
        interpreter.run(ast);
    }
}


