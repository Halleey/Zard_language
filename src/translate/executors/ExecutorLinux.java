package translate.executors;

import low.module.LLVMGenerator;
import low.module.LLVisitorMain;
import memory_manager.EscapeInfo;
import translate.front.ASTInterpreter;
import translate.front.FrontendPipeline;
import translate.front.TypePipeline;
import translate.llvm.LLVMToolchain;

public class ExecutorLinux {

    public static void main(String[] args) throws Exception {
        String filePath = args.length > 0 ? args[0] : "src/language/main.zd";

        FrontendPipeline frontend = new FrontendPipeline(filePath);
        var ast = frontend.process();

//        EscapeInfo escapeInfo = frontend.getEscapeInfo();
//
//        TypePipeline typePipeline = new TypePipeline(frontend.getParser());
//        LLVisitorMain tempVisitor = typePipeline.process(ast);
//
//        LLVisitorMain llvmVisitor = tempVisitor.fork();
//
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


