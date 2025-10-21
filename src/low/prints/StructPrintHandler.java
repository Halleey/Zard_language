package low.prints;

import ast.ASTNode;
import ast.structs.StructInstaceNode;
import ast.structs.StructNode;

import ast.variables.VariableNode;
import low.TempManager;
import low.module.LLVisitorMain;

public class StructPrintHandler implements PrintHandler {
    private final TempManager temps;

    public StructPrintHandler(TempManager temps) {
        this.temps = temps;
    }

    @Override
    public boolean canHandle(ASTNode node, LLVisitorMain visitor) {
        String type = null;
        if (node instanceof VariableNode var) {
            type = visitor.getVarType(var.getName());
        }
        if (node instanceof StructInstaceNode) {
            type = "%" + ((StructInstaceNode) node).getName() + "*";
        }

        return type != null && type.startsWith("%") && type.endsWith("*") && !type.equals("%String*");
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
            llvm.append("  ").append(t).append(" = load ").append(base).append(", ").append(base).append("* ").append(temp).append("\n");
            llvm.append(";;VAL:").append(t).append(";;TYPE:").append(base).append("\n");
            temp = t;
            type = base;
        }

        String key = normalizeKeyFromLLVMPtr(type);      // ex: %st_Nomade* -> st_Nomade
        StructNode def = resolveStructNode(key, visitor);
        if (def == null) {
            throw new RuntimeException("Struct não encontrada para impressão: " + key + " (type=" + type + ")");
        }
        String shortName = def.getName();
        String printFn = "@" + ("print_" + shortName);

        String asRaw;
        if ("i8*".equals(type)) {
            asRaw = temp;
        } else {
            asRaw = temps.newTemp();
            llvm.append("  ").append(asRaw).append(" = bitcast ").append(type).append(" ").append(temp).append(" to i8*\n");
        }

        llvm.append("  call void ").append(printFn).append("(i8* ").append(asRaw).append(")\n");

        llvm.append(";;VAL:").append(asRaw).append(";;TYPE:i8*\n");

        return llvm.toString();
    }



    private StructNode resolveStructNode(String key, LLVisitorMain visitor) {
        // Tenta direto
        StructNode n = visitor.getStructNode(key);
        if (n != null) return n;

        // Tenta trocar '_' por '.'
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
        if (n != null) return n;

        return null;
    }

    private String normalizeKeyFromLLVMPtr(String llvmPtrType) {
        // %st_Nomade* -> st_Nomade ;  %Pessoa* -> Pessoa ; i8* -> i8 (não deve chegar aqui)
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
