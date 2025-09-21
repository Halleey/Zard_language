package low.prints;

import low.TempManager;
public class ListPrintEmitter {
    private final TempManager temps;

    public ListPrintEmitter(TempManager temps) {
        this.temps = temps;
    }

    /**
     * Emite código LLVM para: tmp = load i8*, i8** %varName
     *                          call void @printList(i8* tmp)
     */
    public String emit(String varName) {
        String tmp = temps.newTemp(); // %tX único
        StringBuilder llvm = new StringBuilder();

        llvm.append("  ").append(tmp).append(" = load i8*, i8** %").append(varName).append("\n");
        llvm.append("  call void @printList(i8* ").append(tmp).append(")\n");

        return llvm.toString();
    }
}
