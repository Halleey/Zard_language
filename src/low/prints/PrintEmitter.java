package low.prints;

import ast.ASTNode;

import low.main.GlobalStringManager;
import low.module.LLVisitorMain;
import ast.prints.PrintNode;
import ast.variables.LiteralNode;
import ast.variables.VariableNode;
public class PrintEmitter {
    private final GlobalStringManager globalStrings;

    public PrintEmitter(GlobalStringManager globalStrings) {
        this.globalStrings = globalStrings;
    }

    public String emit(PrintNode node, LLVisitorMain visitor) {
        ASTNode expr = node.expr;

        // 1) Literal string direto
        if (expr instanceof LiteralNode lit && lit.value.getType().equals("string")) {
            return emitStringLiteral((String) lit.value.getValue());
        }

        // 2) Variável
        if (expr instanceof VariableNode varNode) {
            String varName = varNode.getName();
            String type = visitor.getVarType(varName);

            if ("i8*".equals(type)) {
                // lista ou string? verifica registro de lista
                if (visitor.isList(varName)) {
                    ListPrintEmitter listEmitter = new ListPrintEmitter(visitor.getTemps());
                    return listEmitter.emit(varName);
                } else {
                    return emitStringVariable(varName, visitor);
                }
            }
        }

        // 3) Expressão: números, booleanos ou qualquer outra expressão complexa
        //    O visitor já gera código + marcador ";;VAL:...;;TYPE:..."
        String exprLLVM = expr.accept(visitor);
        return emitPrimitiveOrExpr(exprLLVM, visitor);
    }

    // Imprime literal de string (usa getelementptr direto para o literal)
    private String emitStringLiteral(String value) {
        String strName = globalStrings.getOrCreateString(value);
        int len = value.length() + 2;
        return "  call i32 (i8*, ...) @printf(i8* getelementptr ([" + len +
                " x i8], [" + len + " x i8]* " + strName + ", i32 0, i32 0))\n";
    }

    // Imprime variável string (carrega o i8* e passa como %s)
    private String emitStringVariable(String varName, LLVisitorMain visitor) {
        String tmp = visitor.getTemps().newTemp();
        StringBuilder llvm = new StringBuilder();
        llvm.append("  ").append(tmp).append(" = load i8*, i8** %").append(varName).append("\n");
        // usa formato %s em @.strStr
        llvm.append("  call i32 (i8*, ...) @printf(i8* getelementptr ([4 x i8], [4 x i8]* @.strStr, i32 0, i32 0), i8* ").append(tmp).append(")\n");
        return llvm.toString();
    }

    // Emite código para números/booleanos/expressões.
    // exprLLVM já contém as instruções necessárias (loads etc.) + ";;VAL:tmp;;TYPE:tipo"
    private String emitPrimitiveOrExpr(String exprLLVM, LLVisitorMain visitor) {
        // split código real e as metadatas
        int markerIdx = exprLLVM.lastIndexOf(";;VAL:");
        if (markerIdx == -1) {
            // caso inesperado: exprLLVM sem marcador; apenas retorna o código (ou lança)
            return exprLLVM;
        }

        String codePart = exprLLVM.substring(0, markerIdx); // pode conter linhas com loads e outros
        String valTypePart = exprLLVM.substring(markerIdx); // ";;VAL:%tX;;TYPE:TYPE\n..."

        // extrair temp e type
        String temp = extractTemp(valTypePart); // pega %tX
        String type = extractType(valTypePart); // pega i32/double/i1/etc.

        StringBuilder llvm = new StringBuilder();
        // primeiro, adiciona o código que produz o valor (loads, ops...)
        if (!codePart.isEmpty()) {
            // garantir que codePart termine com newline
            if (!codePart.endsWith("\n")) codePart += "\n";
            llvm.append(codePart);
        }

        // agora a chamada ao printf correta dependendo do tipo
        switch (type) {
            case "i32" ->
                    llvm.append("  call i32 (i8*, ...) @printf(i8* getelementptr ([4 x i8], [4 x i8]* @.strInt, i32 0, i32 0), i32 ").append(temp).append(")\n");
            case "double" ->
                    llvm.append("  call i32 (i8*, ...) @printf(i8* getelementptr ([4 x i8], [4 x i8]* @.strDouble, i32 0, i32 0), double ").append(temp).append(")\n");
            case "i1" -> {
                String zextTmp = visitor.getTemps().newTemp();
                llvm.append("  ").append(zextTmp).append(" = zext i1 ").append(temp).append(" to i32\n");
                llvm.append("  call i32 (i8*, ...) @printf(i8* getelementptr ([4 x i8], [4 x i8]* @.strInt, i32 0, i32 0), i32 ").append(zextTmp).append(")\n");
            }
            default -> throw new RuntimeException("Tipo não suportado no print: " + type);
        }

        return llvm.toString();
    }

    // Helpers para extrair temp e type a partir da parte ";;VAL:%tX;;TYPE:TYPE"
    private String extractTemp(String valTypePart) {
        int v = valTypePart.indexOf(";;VAL:");
        int t = valTypePart.indexOf(";;TYPE:", v);
        if (v == -1 || t == -1) throw new RuntimeException("Formato invalido: " + valTypePart);
        return valTypePart.substring(v + 6, t).trim();
    }

    private String extractType(String valTypePart) {
        int t = valTypePart.indexOf(";;TYPE:");
        if (t == -1) throw new RuntimeException("Formato invalido: " + valTypePart);
        int end = valTypePart.indexOf("\n", t);
        if (end == -1) end = valTypePart.length();
        return valTypePart.substring(t + 7, end).trim();
    }
}
