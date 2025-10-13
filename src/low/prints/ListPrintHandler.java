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

        switch (llvmType) {
            case "%struct.ArrayListInt*" -> {
                sb.append("  ").append(tmp).append(" = load %struct.ArrayListInt*, %struct.ArrayListInt** %").append(varName).append("\n");
                sb.append("  call void @arraylist_print_int(%struct.ArrayListInt* ").append(tmp).append(")\n");
            }
            case "%struct.ArrayListDouble*" -> {
                sb.append("  ").append(tmp).append(" = load %struct.ArrayListDouble*, %struct.ArrayListDouble** %").append(varName).append("\n");
                sb.append("  call void @arraylist_print_double(%struct.ArrayListDouble* ").append(tmp).append(")\n");
            }
            case "%struct.ArrayListBool*" -> {
                sb.append("  ").append(tmp).append(" = load %struct.ArrayListBool*, %struct.ArrayListBool** %").append(varName).append("\n");
                sb.append("  call void @arraylist_print_bool(%struct.ArrayListBool* ").append(tmp).append(")\n");
            }
            default -> {
                sb.append("  ").append(tmp).append(" = load i8*, i8** %").append(varName).append("\n");
                String tmpCast = temps.newTemp();
                sb.append("  ").append(tmpCast).append(" = bitcast i8* ").append(tmp).append(" to %ArrayList*\n");
                String printFunc = elemType.equals("string") ? "@arraylist_print_string" : null;
                if (printFunc == null) throw new RuntimeException("Unsupported list element type: " + elemType);
                sb.append("  call void ").append(printFunc).append("(%ArrayList* ").append(tmpCast).append(")\n");
            }
        }

        return sb.toString();
    }
}