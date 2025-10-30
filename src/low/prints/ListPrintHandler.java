package low.prints;

import ast.ASTNode;
import ast.structs.StructFieldAccessNode;
import ast.variables.VariableNode;
import low.TempManager;
import low.main.TypeInfos;
import low.module.LLVisitorMain;
public class ListPrintHandler implements PrintHandler {
    private final TempManager temps;

    public ListPrintHandler(TempManager temps) {
        this.temps = temps;
    }

    @Override
    public boolean canHandle(ASTNode node, LLVisitorMain visitor) {
        if (node instanceof VariableNode varNode) {
            TypeInfos info = visitor.getVarType(varNode.getName());
            return info != null && info.isList();
        }

        if (node instanceof StructFieldAccessNode sfa) {
            String elemType = visitor.inferListElementType(sfa);
            return elemType != null;
        }

        return false;
    }

    @Override
    public String emit(ASTNode node, LLVisitorMain visitor) {
        StringBuilder sb = new StringBuilder();
        String elemType;
        String llvmType;
        String tmp = temps.newTemp();

        if (node instanceof VariableNode varNode) {
            String varName = varNode.getName();
            TypeInfos info = visitor.getVarType(varName);
            if (info == null) throw new RuntimeException("Variável não registrada: " + varName);

            elemType = info.getElementType();
            llvmType = info.getLLVMType();

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
                    handleGeneric(elemType, sb, tmp);
                }
            }
        }
        else if (node instanceof StructFieldAccessNode sfa) {
            String accessIR = visitor.visit(sfa); // gera código + markers ;;VAL: ;;TYPE:
            sb.append(accessIR);

            String val = extractTemp(accessIR);
            llvmType = extractType(accessIR);
            elemType = visitor.inferListElementType(sfa);

            // load extra se for ponteiro duplo
            if (llvmType.endsWith("**")) {
                String loaded = temps.newTemp();
                sb.append("  ").append(loaded)
                        .append(" = load ").append(llvmType, 0, llvmType.length() - 1)
                        .append(", ").append(llvmType).append(" ").append(val).append("\n");
                val = loaded;
                llvmType = llvmType.substring(0, llvmType.length() - 1);
            }

            switch (llvmType) {
                case "%struct.ArrayListInt*" -> {
                    sb.append("  call void @arraylist_print_int(%struct.ArrayListInt* ").append(val).append(")\n");
                }
                case "%struct.ArrayListDouble*" -> {
                    sb.append("  call void @arraylist_print_double(%struct.ArrayListDouble* ").append(val).append(")\n");
                }
                case "%struct.ArrayListBool*" -> {
                    sb.append("  call void @arraylist_print_bool(%struct.ArrayListBool* ").append(val).append(")\n");
                }
                default -> {
                    handleGeneric(elemType, sb, val);
                }
            }
        }
        else {
            throw new RuntimeException("ListPrintHandler não sabe lidar com: " + node.getClass());
        }

        return sb.toString();
    }

    private void handleGeneric(String elemType, StringBuilder sb, String tmp) {
        if (elemType == null) {
            throw new RuntimeException("ListPrintHandler: elemType nulo");
        }

        switch (elemType) {
            case "int" -> sb.append("  call void @arraylist_print_int(%struct.ArrayListInt* ").append(tmp).append(")\n");
            case "double" -> sb.append("  call void @arraylist_print_double(%struct.ArrayListDouble* ").append(tmp).append(")\n");
            case "boolean" -> sb.append("  call void @arraylist_print_bool(%struct.ArrayListBool* ").append(tmp).append(")\n");
            case "string" -> sb.append("  call void @arraylist_print_string(%ArrayList* ").append(tmp).append(")\n");
            default -> {
                String structName;
                if (elemType.startsWith("Struct<")) {
                    structName = elemType
                            .substring("Struct<".length(), elemType.length() - 1)
                            .replace('.', '_');
                } else {
                    structName = elemType.replace('.', '_');
                }
                sb.append("  call void @arraylist_print_ptr(%ArrayList* ")
                        .append(tmp)
                        .append(", void (i8*)* @print_").append(structName).append(")\n");
            }
        }
    }

    private String extractTemp(String code) {
        int v = code.lastIndexOf(";;VAL:");
        if (v == -1) return "";
        int t = code.indexOf(";;TYPE:", v);
        return (t == -1) ? "" : code.substring(v + 6, t).trim();
    }

    private String extractType(String code) {
        int t = code.lastIndexOf(";;TYPE:");
        if (t == -1) return "";
        int end = code.indexOf("\n", t);
        if (end == -1) end = code.length();
        return code.substring(t + 7, end).trim();
    }
}
