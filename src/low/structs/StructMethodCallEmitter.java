package low.structs;



import ast.ASTNode;
import ast.structs.StructMethodCallNode;
import low.TempManager;

import low.module.LLVisitorMain;
public class StructMethodCallEmitter {

    private final TempManager temps;

    public StructMethodCallEmitter(TempManager temps) {
        this.temps = temps;
    }

    public String emit(StructMethodCallNode node, LLVisitorMain visitor) {
        StringBuilder llvm = new StringBuilder();

        String methodName = node.getMethodName();

        ASTNode receiver = node.getStructInstance();
        String recvIR = receiver.accept(visitor);
        String recvVal = extractLastVal(recvIR);
        String recvType = extractLastType(recvIR);

        llvm.append(recvIR);

        String cleanType = recvType.replace("%", "").replace("*", "");
        String llvmSafe = cleanType
                .replace("Struct<", "")
                .replace(">", "")
                .replace("<", "_")
                .replace(",", "_")
                .replace(" ", "_");
        String llvmFuncName = llvmSafe + "_" + methodName;

        StringBuilder callArgs = new StringBuilder();
        callArgs.append(recvType).append(" ").append(recvVal);

        if (!node.getArgs().isEmpty()) {
            ASTNode arg = node.getArgs().get(0);
            String argIR = arg.accept(visitor);
            llvm.append(argIR);

            String argVal = extractLastVal(argIR);
            String argType = extractLastType(argIR);
            callArgs.append(", ").append(argType).append(" ").append(argVal);
        }

        String retType = recvType;

        String tmp = temps.newTemp();
        llvm.append("  ").append(tmp)
                .append(" = call ").append(retType)
                .append(" @").append(llvmFuncName)
                .append("(").append(callArgs).append(")\n")
                .append(";;VAL:").append(tmp).append(";;TYPE:").append(retType).append("\n");

        return llvm.toString();
    }

    private String extractLastVal(String code) {
        int v = code.lastIndexOf(";;VAL:");
        if (v == -1) return "";
        int t = code.indexOf(";;TYPE:", v);
        return (t == -1) ? "" : code.substring(v + 6, t).trim();
    }

    private String extractLastType(String code) {
        int t = code.lastIndexOf(";;TYPE:");
        if (t == -1) return "";
        int end = code.indexOf("\n", t);
        if (end == -1) end = code.length();
        return code.substring(t + 7, end).trim();
    }
}
