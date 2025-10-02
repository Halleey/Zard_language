package low.exceptions;

import ast.exceptions.ReturnNode;
import ast.functions.FunctionCallNode;
import ast.variables.LiteralNode;
import low.TempManager;
import low.functions.ReturnTypeInferer;
import low.functions.TypeMapper;
import low.module.LLVisitorMain;

import java.util.ArrayList;
import java.util.HashMap;
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

        // gera código da expressão (pode criar markers ;;VAL:;;TYPE:)
        String exprCode = node.expr.accept(visitor);
        sb.append(exprCode);

        // extrai temp e tipo do código gerado
        String temp = extractTemp(exprCode);
        String type = extractType(exprCode);

        // caso 1: função já retorna %String* (apenas repassa)
        if ("%String*".equals(type)) {
            sb.append("  ret %String* ").append(temp).append("\n");
            return sb.toString();
        }

        // caso 2: expressão é literal string com i8* temp
        // detectamos literal se node.expr instanceof LiteralNode e tipo i8* ou i8*
        if (node.expr instanceof LiteralNode lit && "string".equals(lit.value.getType())) {
            // temp provavelmente contém um getelementptr ... (i8*)
            int len = ((String) lit.value.getValue()).length();

            // monta struct %String na stack
            String sAlloca = temps.newTemp();
            sb.append("  ").append(sAlloca).append(" = alloca %String\n");

            // campo .data (i8*)
            String fld0 = temps.newTemp();
            sb.append("  ").append(fld0)
                    .append(" = getelementptr inbounds %String, %String* ")
                    .append(sAlloca).append(", i32 0, i32 0\n");
            sb.append("  store i8* ").append(temp).append(", i8** ").append(fld0).append("\n");

            // campo .length (i64)
            String fld1 = temps.newTemp();
            sb.append("  ").append(fld1)
                    .append(" = getelementptr inbounds %String, %String* ")
                    .append(sAlloca).append(", i32 0, i32 1\n");
            sb.append("  store i64 ").append(len).append(", i64* ").append(fld1).append("\n");

            sb.append("  ret %String* ").append(sAlloca).append("\n");
            return sb.toString();
        }

        // caso 3: expressão produz i8* (não-literal) -> aloca struct, armazena ptr e length=0 (ou substituir por strlen)
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

        // caso default: retorna diretamente se tipos batem (int, double, i1 etc)
        sb.append("  ret ").append(type).append(" ").append(temp).append("\n");
        return sb.toString();
    }

    private String extractTemp(String code) {
        int idx = code.lastIndexOf(";;VAL:");
        if (idx == -1) {
            // fallback: última linha se for uma expressão simples
            String[] lines = code.strip().split("\n");
            String last = lines[lines.length - 1];
            // tenta extrair token final
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
