package low.prints;

import ast.ASTNode;
import ast.structs.StructInstaceNode;
import ast.structs.StructNode;

import ast.variables.VariableNode;
import low.TempManager;
import low.main.TypeInfos;
import low.module.LLVisitorMain;
public class StructPrintHandler implements PrintHandler {
    private final TempManager temps;

    public StructPrintHandler(TempManager temps) {
        this.temps = temps;
    }

    @Override
    public boolean canHandle(ASTNode node, LLVisitorMain visitor) {
        String llvmType = null;

        if (node instanceof VariableNode var) {
            TypeInfos info = visitor.getVarType(var.getName());
            if (info != null) llvmType = info.getLLVMType();
        }
        if (node instanceof StructInstaceNode inst) {
            llvmType = "%" + inst.getName() + "*";
        }

        return llvmType != null
                && llvmType.startsWith("%")
                && llvmType.endsWith("*")
                && !llvmType.equals("%String*");
    }

    @Override
    public String emit(ASTNode node, LLVisitorMain visitor) {
        StringBuilder llvm = new StringBuilder();

        String code = node.accept(visitor);
        if (!code.isBlank()) {
            llvm.append(code);
        }

        String temp = extractTemp(code);
        String type = extractType(code).trim();

        if (type.endsWith("**")) {
            String base = type.substring(0, type.length() - 1);
            String t = temps.newTemp();
            llvm.append("  ").append(t)
                    .append(" = load ").append(base)
                    .append(", ").append(base).append("* ").append(temp).append("\n");
            llvm.append(";;VAL:").append(t).append(";;TYPE:").append(base).append("\n");
            temp = t;
            type = base;
        }

        // Resolve struct dona
        String key = normalizeKeyFromLLVMPtr(type); // ex: %Pessoa* -> Pessoa
        StructNode def = resolveStructNode(key, visitor);
        if (def == null) {
            throw new RuntimeException("Struct não encontrada para impressão: " + key + " (type=" + type + ")");
        }

        String shortName = def.getName();
        String printFn = "@print_" + shortName;

        // chamada direta, sem bitcast para i8*
        llvm.append("  call void ").append(printFn)
                .append("(").append(type).append(" ").append(temp).append(")\n");

        llvm.append(";;VAL:").append(temp).append(";;TYPE:").append(type).append("\n");

        return llvm.toString();
    }

    private StructNode resolveStructNode(String key, LLVisitorMain visitor) {

        StructNode n = visitor.getStructNode(key);
        if (n != null) return n;

        String withDots = key.replace('_', '.');
        n = visitor.getStructNode(withDots);
        if (n != null) return n;

        int idx = key.indexOf('_');
        if (idx >= 0 && idx + 1 < key.length()) {
            String shortOnly = key.substring(idx + 1);
            n = visitor.getStructNode(shortOnly);
            if (n != null) return n;
        }

        String asGeneric = "Struct<" + withDots + ">";
        n = visitor.getStructNode(asGeneric);
        return n;
    }

    private String normalizeKeyFromLLVMPtr(String llvmPtrType) {
        String t = llvmPtrType.trim();
        if (t.startsWith("%")) t = t.substring(1);
        while (t.endsWith("*")) t = t.substring(0, t.length() - 1);
        return t;
    }

    private String extractTemp(String code) {
        int v = code.lastIndexOf(";;VAL:");
        int t = code.indexOf(";;TYPE:", v);
        return code.substring(v + 6, t).trim();
    }

    private String extractType(String code) {
        int t = code.lastIndexOf(";;TYPE:");
        return code.substring(t + 7).trim();
    }
}
