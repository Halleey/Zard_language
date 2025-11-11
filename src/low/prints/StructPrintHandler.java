package low.prints;

import ast.ASTNode;
import ast.structs.StructInstaceNode;
import ast.structs.StructNode;
import ast.variables.VariableNode;
import low.TempManager;
import low.main.TypeInfos;
import low.module.LLVisitorMain;
import low.utils.LLVMNameUtils;

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
        } else if (node instanceof StructInstaceNode inst) {
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
        if (code != null && !code.isBlank()) llvm.append(code);

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
            System.out.println("[StructPrintHandler] Corrigido ponteiro duplo -> " + type);
        }

        String key = normalizeKeyFromLLVMPtr(type);
        StructNode def = resolveStructNode(key, visitor);
        if (def == null) {
            throw new RuntimeException("Struct não encontrada para impressão: " + key + " (type=" + type + ")");
        }

        String rawLLVMName = def.getLLVMName();
        String llvmStructName = LLVMNameUtils.llvmSafe(rawLLVMName)
                .replace("<", "_")
                .replace(">", "");


        String targetPtrType = "%" + llvmStructName + "*";

        if (!type.equals(targetPtrType)) {
            String castTemp = temps.newTemp();
            llvm.append("  ").append(castTemp)
                    .append(" = bitcast ")
                    .append(type).append(" ").append(temp)
                    .append(" to ").append(targetPtrType).append("\n");
            llvm.append(";;VAL:").append(castTemp).append(";;TYPE:").append(targetPtrType).append("\n");
            temp = castTemp;
            type = targetPtrType;
            System.out.println("[StructPrintHandler] Bitcast aplicado para " + targetPtrType);
        }

        llvm.append("  call void @print_")
                .append(llvmStructName)
                .append("(").append(type).append(" ").append(temp).append(")\n");

        llvm.append(";;VAL:").append(temp).append(";;TYPE:").append(type).append("\n");

        return llvm.toString();
    }

    private StructNode resolveStructNode(String key, LLVisitorMain visitor) {
        StructNode n = visitor.getStructNode(key);
        if (n != null) return n;

        String base = stripGenericBase(key);
        String safe = LLVMNameUtils.llvmSafe(key);
        String safeBase = LLVMNameUtils.llvmSafe(base);

        n = visitor.getStructNode(base + "<int>");
        if (n != null) return n;

        n = visitor.getStructNode(base + "_" + safeBase);
        if (n != null) return n;

        n = visitor.getStructNode(safe);
        if (n != null) return n;

        n = visitor.getStructNode("%" + safe);
        if (n != null) return n;

        return null;
    }

    private String stripGenericBase(String key) {
        int idx = key.indexOf('<');
        return (idx > 0) ? key.substring(0, idx).trim() : key;
    }

    private String normalizeKeyFromLLVMPtr(String llvmPtrType) {
        String t = llvmPtrType.trim();
        if (t.startsWith("%")) t = t.substring(1);
        while (t.endsWith("*")) t = t.substring(0, t.length() - 1);
        return t;
    }

    private String extractTemp(String code) {
        if (code == null) return "%unk";
        int v = code.lastIndexOf(";;VAL:");
        int t = code.indexOf(";;TYPE:", v);
        if (v == -1 || t == -1) return "%unk";
        return code.substring(v + 6, t).trim();
    }

    private String extractType(String code) {
        if (code == null) return "%unk";
        int t = code.lastIndexOf(";;TYPE:");
        if (t == -1) return "%unk";
        return code.substring(t + 7).trim();
    }
}
