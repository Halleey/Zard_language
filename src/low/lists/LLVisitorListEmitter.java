package low.lists;

import low.TempManager;
import low.main.GlobalStringManager;

public class LLVisitorListEmitter {
    private final TempManager temps;
    private final GlobalStringManager globalStrings;

    public LLVisitorListEmitter(TempManager temps, GlobalStringManager globalStrings) {
        this.temps = temps;
        this.globalStrings = globalStrings;
    }

    // Inicializa uma lista vazia
    public String emitInitEmptyList(String listName) {
        StringBuilder llvm = new StringBuilder();
        llvm.append(listName).append(" = alloca %List\n");
        llvm.append("store i32 0, i32* getelementptr (%List, %List* ").append(listName).append(", i32 0, i32 0)\n"); // size
        llvm.append("store i32 4, i32* getelementptr (%List, %List* ").append(listName).append(", i32 0, i32 1)\n"); // capacity
        String arrayTemp = listName + "_arr";
        llvm.append(arrayTemp).append(" = call i8** @malloc(i32 4 * 8)\n"); // 4 elementos de 8 bytes
        llvm.append("store i8** ").append(arrayTemp).append(", i8*** getelementptr (%List, %List* ").append(listName).append(", i32 0, i32 2)\n");
        return llvm.toString();
    }

    // Inicializa lista com elementos (assumindo que os valores já estão convertidos para LLVM)
    public String emitInitListWithElements(String listName, String[] llvmElements) {
        StringBuilder llvm = new StringBuilder();
        llvm.append(emitInitEmptyList(listName));

        for (int i = 0; i < llvmElements.length; i++) {
            String elemTemp = llvmElements[i];
            llvm.append("%elem_ptr").append(i).append(" = getelementptr i8*, i8** ")
                    .append(listName).append("_arr, i32 ").append(i).append("\n");
            llvm.append("store i8* ").append(elemTemp).append(", i8** %elem_ptr").append(i).append("\n");
        }

        llvm.append("store i32 ").append(llvmElements.length)
                .append(", i32* getelementptr (%List, %List* ").append(listName).append(", i32 0, i32 0)\n"); // atualiza size

        return llvm.toString();
    }
}
