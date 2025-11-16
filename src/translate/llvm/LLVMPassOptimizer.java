package translate.llvm;

import java.nio.file.Path;
import java.util.List;

public class LLVMPassOptimizer {

    private static final String PASSES = String.join(",",
            "mem2reg", "sroa", "early-cse", "gvn-hoist",
            "dce", "adce", "reassociate",
            "loop-simplify", "loop-rotate",
            "loop-unroll", "loop-vectorize"
    );

    public Path optimize(Path inputLL) throws Exception {
        Path out = Path.of("programa_opt.ll");

        List<String> cmd = List.of(
                "opt",
                "-passes=" + PASSES,
                inputLL.toString(),
                "-S",
                "-o", out.toString()
        );

        run(cmd);
        System.out.println("Arquivo otimizado: " + out);
        return out;
    }

    private void run(List<String> cmd) throws Exception {
        ProcessBuilder pb = new ProcessBuilder(cmd);
        pb.inheritIO();
        Process p = pb.start();
        if (p.waitFor() != 0)
            throw new RuntimeException("Erro ao executar: " + cmd);
    }
}

