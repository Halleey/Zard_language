package low.structs;



import ast.ASTNode;
import ast.structs.StructMethodCallNode;
import low.TempManager;
import low.functions.TypeMapper;
import low.module.LLVisitorMain;


public class StructMethodCallEmitter {
    private final TempManager temps;
    private final TypeMapper typeMapper = new TypeMapper();

    public StructMethodCallEmitter(TempManager temps) {
        this.temps = temps;
    }

    public String emit(StructMethodCallNode node, LLVisitorMain visitor) {
        StringBuilder llvm = new StringBuilder();

        String structName = visitor.resolveStructName(node.getStructInstance());
        String methodName = node.getMethodName();

        String fnName = "@" + structName + "_" + methodName;

        String recvCode = node.getStructInstance().accept(visitor);
        llvm.append(recvCode);

        String recvVal = extractVal(recvCode);
        String recvType = extractType(recvCode);
        if (recvVal == null || recvType == null)
            throw new RuntimeException("Failed to extract receiver value/type for StructMethodCallNode");

        StringBuilder argList = new StringBuilder();
        for (ASTNode arg : node.getArgs()) {
            String argCode = arg.accept(visitor);
            llvm.append(argCode);

            String argVal = extractVal(argCode);
            String argType = extractType(argCode);

            if (argVal == null || argType == null)
                throw new RuntimeException("Cannot extract argument value/type in StructMethodCallNode");

            argList.append(", ")
                    .append(argType)
                    .append(" ")
                    .append(argVal);
        }

        String tmp = temps.newTemp();
        llvm.append("  ").append(tmp).append(" = call ").append(recvType)
                .append(" ").append(fnName)
                .append("(").append(recvType).append(" ").append(recvVal)
                .append(argList).append(")\n");

        llvm.append(";;VAL:").append(tmp)
                .append(";;TYPE:").append(recvType)
                .append("\n");

        return llvm.toString();
    }

    private String extractVal(String code) {
        int i = code.lastIndexOf(";;VAL:");
        if (i == -1) return null;
        int j = code.indexOf(";;TYPE:", i);
        if (j == -1) return null;
        return code.substring(i + 6, j).trim();
    }

    private String extractType(String code) {
        int j = code.lastIndexOf(";;TYPE:");
        if (j == -1) return null;
        String line = code.substring(j + 7).trim();
        int end = line.indexOf('\n');
        return (end != -1) ? line.substring(0, end).trim() : line;
    }
}
