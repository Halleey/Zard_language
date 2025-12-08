package low.exceptions;

import ast.exceptions.ReturnNode;
import ast.variables.LiteralNode;
import ast.variables.VariableNode;
import low.TempManager;
import low.module.LLVisitorMain;
public class ReturnEmitter {
    private final LLVisitorMain visitor;
    private final TempManager temps;

    public ReturnEmitter(LLVisitorMain visitor, TempManager temps) {
        this.visitor = visitor;
        this.temps = temps;
    }

    public String emit(ReturnNode node) {
        StringBuilder sb = new StringBuilder();

        if (node.expr == null) {
            sb.append("  ret void\n");
            return sb.toString();
        }

        String exprCode = node.expr.accept(visitor);
        sb.append(exprCode);

        String temp = extractTemp(exprCode);
        String type = extractType(exprCode);

        if (node.expr instanceof VariableNode
                && ("i32*".equals(type) || "double*".equals(type) || "i1*".equals(type))) {

            String baseType = type.substring(0, type.length() - 1); // i32, double, i1
            String loadTmp = temps.newTemp();

            sb.append("  ").append(loadTmp)
                    .append(" = load ").append(baseType)
                    .append(", ").append(type).append(" ").append(temp).append("\n")
                    .append(";;VAL:").append(loadTmp)
                    .append(";;TYPE:").append(baseType).append("\n");

            temp = loadTmp;
            type = baseType;
        }

        // função já retorna %String* (apenas repassa)
        if ("%String*".equals(type)) {
            sb.append("  ret %String* ").append(temp).append("\n");
            return sb.toString();
        }

        // literal string → construir %String na hora
        if (node.expr instanceof LiteralNode lit && "string".equals(lit.value.type())) {
            int len = ((String) lit.value.value()).length();

            String sAlloca = temps.newTemp();
            sb.append("  ").append(sAlloca).append(" = alloca %String\n");

            String fld0 = temps.newTemp();
            sb.append("  ").append(fld0)
                    .append(" = getelementptr inbounds %String, %String* ")
                    .append(sAlloca).append(", i32 0, i32 0\n");
            sb.append("  store i8* ").append(temp).append(", i8** ").append(fld0).append("\n");

            String fld1 = temps.newTemp();
            sb.append("  ").append(fld1)
                    .append(" = getelementptr inbounds %String, %String* ")
                    .append(sAlloca).append(", i32 0, i32 1\n");
            sb.append("  store i64 ").append(len).append(", i64* ").append(fld1).append("\n");

            sb.append("  ret %String* ").append(sAlloca).append("\n");
            return sb.toString();
        }

        // i8* genérico → embrulha em %String (length desconhecido)
        if ("i8*".equals(type)) {
            String sAlloca = temps.newTemp();
            sb.append("  ").append(sAlloca).append(" = alloca %String\n");

            String fld0 = temps.newTemp();
            sb.append("  ").append(fld0)
                    .append(" = getelementptr inbounds %String, %String* ")
                    .append(sAlloca).append(", i32 0, i32 0\n");
            sb.append("  store i8* ").append(temp).append(", i8** ").append(fld0).append("\n");

            String fld1 = temps.newTemp();
            sb.append("  ").append(fld1)
                    .append(" = getelementptr inbounds %String, %String* ")
                    .append(sAlloca).append(", i32 0, i32 1\n");
            sb.append("  store i64 0, i64* ").append(fld1).append("\n"); // length unknown here

            sb.append("  ret %String* ").append(sAlloca).append("\n");
            return sb.toString();
        }

        // caso geral
        sb.append("  ret ").append(type).append(" ").append(temp).append("\n");
        return sb.toString();
    }

    private String extractTemp(String code) {
        int idx = code.lastIndexOf(";;VAL:");
        if (idx == -1) {
            String[] lines = code.strip().split("\n");
            String last = lines[lines.length - 1];
            String[] parts = last.trim().split("\\s+");
            return parts[parts.length - 1];
        }
        int endIdx = code.indexOf(";;TYPE:", idx);
        return code.substring(idx + 6, endIdx).trim();
    }

    private String extractType(String code) {
        int idx = code.indexOf(";;TYPE:");
        if (idx == -1) return "";
        int endIdx = code.indexOf("\n", idx);
        return code.substring(idx + 7, endIdx == -1 ? code.length() : endIdx).trim();
    }
}
