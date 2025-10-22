package low.prints;

import ast.ASTNode;
import ast.variables.VariableNode;
import low.TempManager;
import low.module.LLVisitorMain;
public class ListPrintHandler implements PrintHandler {
    private final TempManager temps;

    public ListPrintHandler(TempManager temps) {
        this.temps = temps;
    }

    @Override
    public boolean canHandle(ASTNode node, LLVisitorMain visitor) {
        if (node instanceof VariableNode varNode) {
            return visitor.getListElementType(varNode.getName()) != null;
        }
        return false;
    }

    @Override
    public String emit(ASTNode node, LLVisitorMain visitor) {
        VariableNode varNode = (VariableNode) node;
        String varName = varNode.getName();
        String elemType = visitor.getListElementType(varName);
        String llvmType = visitor.getVarType(varName);
        String tmp = temps.newTemp();
        StringBuilder sb = new StringBuilder();

        String normalizedElemType = elemType;
        if (!"int".equals(elemType)
                && !"double".equals(elemType)
                && !"boolean".equals(elemType)
                && !"string".equals(elemType)
                && !elemType.startsWith("Struct<")) {
            normalizedElemType = "Struct<" + elemType + ">";
        }

        switch (llvmType) {
            case "%struct.ArrayListInt*" -> {
                sb.append("  ").append(tmp)
                        .append(" = load %struct.ArrayListInt*, %struct.ArrayListInt** %")
                        .append(varName).append("\n");
                sb.append("  call void @arraylist_print_int(%struct.ArrayListInt* ")
                        .append(tmp).append(")\n");
            }
            case "%struct.ArrayListDouble*" -> {
                sb.append("  ").append(tmp)
                        .append(" = load %struct.ArrayListDouble*, %struct.ArrayListDouble** %")
                        .append(varName).append("\n");
                sb.append("  call void @arraylist_print_double(%struct.ArrayListDouble* ")
                        .append(tmp).append(")\n");
            }
            case "%struct.ArrayListBool*" -> {
                sb.append("  ").append(tmp)
                        .append(" = load %struct.ArrayListBool*, %struct.ArrayListBool** %")
                        .append(varName).append("\n");
                sb.append("  call void @arraylist_print_bool(%struct.ArrayListBool* ")
                        .append(tmp).append(")\n");
            }
            default -> {
                sb.append("  ").append(tmp)
                        .append(" = load %ArrayList*, %ArrayList** %")
                        .append(varName).append("\n");

                if ("string".equals(normalizedElemType)) {
                    sb.append("  call void @arraylist_print_string(%ArrayList* ")
                            .append(tmp).append(")\n");
                } else if (normalizedElemType.startsWith("Struct<")) {
                    String structName = normalizedElemType
                            .substring("Struct<".length(), normalizedElemType.length() - 1)
                            .replace('.', '_');
                    sb.append("  call void @arraylist_print_ptr(%ArrayList* ")
                            .append(tmp)
                            .append(", void (i8*)* @print_").append(structName).append(")\n");
                } else {
                    throw new RuntimeException("Unsupported list element type: " + elemType);
                }
            }
        }

        return sb.toString();
    }
}
