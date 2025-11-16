package translate.llvm;

import java.nio.file.Path;
public class LLVMToolchain {

    private final LLVMIRWriter writer = new LLVMIRWriter();
    private final LLVMPassOptimizer optimizer = new LLVMPassOptimizer();
    private final LLVMBinaryBuilder builder = new LLVMBinaryBuilder();

    public String buildExecutable(String llvmCode) throws Exception {

        Path llPath = writer.write(llvmCode);          // gera programa.ll
        Path optLL = optimizer.optimize(llPath);       // gera programa_opt.ll
        return builder.build(optLL);                   // gera executável
    }

    public void runExecutable(String exe) throws Exception {
        builder.runExecutable(exe);
    }
}
