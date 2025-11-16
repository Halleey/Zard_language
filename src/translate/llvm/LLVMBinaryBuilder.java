package translate.llvm;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class LLVMBinaryBuilder {

    private final boolean isWindows;

    public LLVMBinaryBuilder() {
        String os = System.getProperty("os.name").toLowerCase();
        this.isWindows = os.contains("win");
    }

    public String build(Path optimizedLL) throws Exception {

        String asmExt = isWindows ? "asm" : "s";
        Path asmPath = Path.of("programa." + asmExt);

        run(List.of(
                "llc",
                "-filetype=asm",
                "-O2",
                optimizedLL.toString(),
                "-o", asmPath.toString()
        ));

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
                "-Isrc/helpers",
                "-Isrc/helpers/ints",
                "-Isrc/helpers/bool",
                "-Isrc/helpers/doubles",
                "-Isrc/helpers/print"
        );

        String exe = isWindows ? "programa.exe" : "programa";

        List<String> cmd = new ArrayList<>();
        cmd.add("clang");
        cmd.add(optimizedLL.toString());
        cmd.addAll(runtimeFiles);
        cmd.addAll(includeDirs);
        cmd.add("-o");
        cmd.add(exe);

        run(cmd);

        System.out.println("Executável gerado: " + exe);
        return exe;
    }

    public void runExecutable(String exe) throws Exception {
        run(List.of(isWindows ? exe : "./" + exe));
    }

    private void run(List<String> cmd) throws Exception {
        ProcessBuilder pb = new ProcessBuilder(cmd);
        pb.inheritIO();
        Process p = pb.start();
        if (p.waitFor() != 0)
            throw new RuntimeException("Erro no comando: " + cmd);
    }
}
