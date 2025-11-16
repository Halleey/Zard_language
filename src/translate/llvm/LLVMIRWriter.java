package translate.llvm;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class LLVMIRWriter {

    public Path write(String llvmCode) throws IOException {
        Path llPath = Path.of("programa.ll");
        Files.writeString(llPath, llvmCode);
        System.out.println("LLVM IR salvo em " + llPath);
        return llPath;
    }
}

