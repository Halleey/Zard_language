package low.prints;

import ast.ASTNode;
import ast.structs.StructInstaceNode;
import ast.structs.StructNode;
import ast.variables.VariableDeclarationNode;
import ast.variables.VariableNode;
import low.TempManager;
import low.functions.TypeMapper;
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
        String temp = extractTemp(code);
        String type = extractType(code);

        if (!code.isBlank()) {
            llvm.append(code);
        }

        String structName = type.substring(1, type.length() - 1);

        StructNode def = visitor.getStructNode(structName);
        if (def == null) {
            throw new RuntimeException("Struct não encontrada: " + structName);
        }

        int index = 0;
        for (VariableDeclarationNode field : def.getFields()) {
            String fieldType = new TypeMapper().toLLVM(field.getType());

            String fieldPtr = temps.newTemp();
            llvm.append("  ").append(fieldPtr)
                    .append(" = getelementptr inbounds ")
                    .append(type.replace("*", "")).append(", ")
                    .append(type).append(" ").append(temp)
                    .append(", i32 0, i32 ").append(index).append("\n");

            String fieldVal = temps.newTemp();
            llvm.append("  ").append(fieldVal)
                    .append(" = load ").append(fieldType)
                    .append(", ").append(fieldType)
                    .append("* ").append(fieldPtr)
                    .append("\n;;VAL:").append(fieldVal)
                    .append(";;TYPE:").append(fieldType).append("\n");

            String marker = ";;VAL:" + fieldVal + ";;TYPE:" + fieldType + "\n";
            llvm.append(new ExprPrintHandler(temps).emitExprOrElement(marker, visitor));

            index++;
        }

        return llvm.toString();
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
