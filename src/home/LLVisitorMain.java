package home;

import ast.ASTNode;
import low.LLVMEmitVisitor;
import prints.PrintNode;
import variables.LiteralNode;
import variables.VariableDeclarationNode;
import variables.VariableNode;

import java.util.HashMap;
import java.util.Map;

public class LLVisitorMain implements LLVMEmitVisitor {

    private int tempCount = 0;
    private int strCount = 0; // nomes únicos para strings globais

    private String newTemp() {
        return "%t" + (tempCount++);
    }

    private String newStrName() {
        return "@.str" + (strCount++);
    }

    // Header básico do LLVM
    private final StringBuilder globalStrings = new StringBuilder();
    private final StringBuilder llvmHeader = new StringBuilder();

    public LLVisitorMain() {
        // declarando printf e formatos fixos
        llvmHeader.append("declare i32 @printf(i8*, ...)\n");
        // strings fixas para int e double com tamanho correto
        llvmHeader.append("@.strInt = private constant [4 x i8] c\"%d\\0A\\00\"\n");
        llvmHeader.append("@.strDouble = private constant [4 x i8] c\"%f\\0A\\00\"\n");
    }

    @Override
    public String visit(MainAST node) {
        // 1. Primeiro coleta strings globais (PrintNode adiciona à globalStrings)
        for (ASTNode stmt : node.body) {
            if (stmt instanceof PrintNode) {
                stmt.accept(this);
            }
        }

        // 2. Gera LLVM completo
        StringBuilder llvm = new StringBuilder();
        llvm.append(llvmHeader).append("\n");
        llvm.append(globalStrings).append("\n");

        llvm.append("define i32 @main() {\n");
        for (ASTNode stmt : node.body) {
            llvm.append("  ; ").append(stmt.getClass().getSimpleName()).append("\n");
            llvm.append(stmt.accept(this)); // gera código real
        }
        llvm.append("  ret i32 0\n");
        llvm.append("}\n");

        return llvm.toString();
    }

    @Override
    public String visit(VariableDeclarationNode node) {
        StringBuilder llvm = new StringBuilder();
        String llvmType = switch (node.getType()) {
            case "int" -> "i32";
            case "double" -> "double";
            default -> "i32";
        };

        llvm.append("  %").append(node.getName()).append(" = alloca ").append(llvmType).append("\n");

        if (node.initializer != null) {
            String value = node.initializer.accept(this);
            if (llvmType.equals("double") && !value.contains(".")) {
                value += ".0";
            }
            llvm.append("  store ").append(llvmType).append(" ").append(value)
                    .append(", ").append(llvmType).append("* %").append(node.getName()).append("\n");
        }

        return llvm.toString();
    }

    @Override
    public String visit(LiteralNode node) {
        Object val = node.value.getValue();
        if (val instanceof Integer || val instanceof Double) return val.toString();
        if (val instanceof String) return (String) val;
        return "0";
    }

    @Override
    public String visit(VariableNode node) {
        String tmp = newTemp();
        String type = "i32"; // simplificado, pode melhorar

        StringBuilder llvm = new StringBuilder();
        llvm.append("  ").append(tmp).append(" = load ").append(type)
                .append(", ").append(type).append("* %").append(node.getName()).append("\n");

        return tmp + "\n;;VAL:" + tmp;
    }

    private final Map<String, String> stringMap = new HashMap<>();

    @Override
    public String visit(PrintNode node) {
        StringBuilder llvm = new StringBuilder();

        // String literal
        if (node.expr instanceof LiteralNode lit && lit.value.getType().equals("string")) {
            String str = (String) lit.value.getValue();
            String strName;

            if (stringMap.containsKey(str)) {
                strName = stringMap.get(str); // reutiliza nome existente
            } else {
                strName = newStrName();
                stringMap.put(str, strName);
                int len = str.length() + 2; // +\n +\0

                // adiciona à seção global
                globalStrings.append(strName).append(" = private constant [").append(len)
                        .append(" x i8] c\"").append(str.replace("\"", "\\\"")).append("\\0A\\00\"\n");
            }

            // printf
            int len = str.length() + 2;
            llvm.append("  call i32 (i8*, ...) @printf(i8* getelementptr ([")
                    .append(len).append(" x i8], [").append(len).append(" x i8]* ")
                    .append(strName).append(", i32 0, i32 0))\n");

            return llvm.toString();
        }

        // int ou double
        String valueResult = node.expr.accept(this);
        String[] parts = valueResult.split(";;VAL:");
        String code = parts[0];
        String value = parts.length > 1 ? parts[1] : parts[0].trim();
        llvm.append(code);

        String llvmType = "i32";
        if (node.expr instanceof LiteralNode lit2 && lit2.value.getType().equals("double")) {
            llvmType = "double";
        }

        if (llvmType.equals("i32")) {
            llvm.append("  call i32 (i8*, ...) @printf(i8* getelementptr ([4 x i8], [4 x i8]* @.strInt, i32 0, i32 0), i32 ")
                    .append(value).append(")\n");
        } else {
            llvm.append("  call i32 (i8*, ...) @printf(i8* getelementptr ([4 x i8], [4 x i8]* @.strDouble, i32 0, i32 0), double ")
                    .append(value).append(")\n");
        }

        return llvm.toString();
    }
}