package low.prints;

import ast.ASTNode;
import ast.functions.FunctionCallNode;
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
        if (node instanceof FunctionCallNode fn) {
            TypeInfos fnType = visitor.getFunctionType(fn.getName());
            return fnType != null && fnType.isList();
        }
        return false;
    }

    @Override
    public String emit(ASTNode node, LLVisitorMain visitor, boolean newline) {
        StringBuilder sb = new StringBuilder();
        String elemType;
        String llvmType;
        String tmp = temps.newTemp();

        if (node instanceof VariableNode varNode) {
            String llvmVar = visitor.varEmitter.getVarPtr(varNode.getName()); // ✅ pega o ptr correto
            TypeInfos info = visitor.getVarType(varNode.getName());
            if (info == null) throw new RuntimeException("Variável não registrada: " + varNode.getName());

            elemType = info.getElementType();
            llvmType = info.getLLVMType();

            switch (llvmType) {
                case "%struct.ArrayListInt*" -> {
                    sb.append("  ").append(tmp)
                            .append(" = load %struct.ArrayListInt*, %struct.ArrayListInt** ")
                            .append(llvmVar).append("\n");
                    sb.append("  call void @arraylist_print_int(%struct.ArrayListInt* ")
                            .append(tmp).append(", i1 ").append(newline ? "1" : "0").append(")\n");
                }
                case "%struct.ArrayListDouble*" -> {
                    sb.append("  ").append(tmp)
                            .append(" = load %struct.ArrayListDouble*, %struct.ArrayListDouble** ")
                            .append(llvmVar).append("\n");
                    sb.append("  call void @arraylist_print_double(%struct.ArrayListDouble* ")
                            .append(tmp).append(", i1 ").append(newline ? "1" : "0").append(")\n");
                }
                case "%struct.ArrayListBool*" -> {
                    sb.append("  ").append(tmp)
                            .append(" = load %struct.ArrayListBool*, %struct.ArrayListBool** ")
                            .append(llvmVar).append("\n");
                    sb.append("  call void @arraylist_print_bool(%struct.ArrayListBool* ")
                            .append(tmp).append(", i1 ").append(newline ? "1" : "0").append(")\n");
                }
                default -> {
                    sb.append("  ").append(tmp)
                            .append(" = load %ArrayList*, %ArrayList** ").append(llvmVar).append("\n");
                    handleGeneric(elemType, sb, tmp, newline);
                }
            }
        }


        else if (node instanceof StructFieldAccessNode sfa) {
            String accessIR = visitor.visit(sfa);
            sb.append(accessIR);

            String val = extractTemp(accessIR);
            llvmType = extractType(accessIR);
            elemType = visitor.inferListElementType(sfa);

            if (llvmType.endsWith("**")) {
                String loaded = temps.newTemp();
                sb.append("  ").append(loaded)
                        .append(" = load ").append(llvmType, 0, llvmType.length() - 1)
                        .append(", ").append(llvmType).append(" ").append(val).append("\n");
                val = loaded;
                llvmType = llvmType.substring(0, llvmType.length() - 1);
            }

            switch (llvmType) {
                case "%struct.ArrayListInt*" -> sb.append("  call void @arraylist_print_int(%struct.ArrayListInt* ")
                        .append(val).append(", i1 ").append(newline ? "1" : "0").append(")\n");
                case "%struct.ArrayListDouble*" -> sb.append("  call void @arraylist_print_double(%struct.ArrayListDouble* ")
                        .append(val).append(", i1 ").append(newline ? "1" : "0").append(")\n");
                case "%struct.ArrayListBool*" -> sb.append("  call void @arraylist_print_bool(%struct.ArrayListBool* ")
                        .append(val).append(", i1 ").append(newline ? "1" : "0").append(")\n");
                default -> handleGeneric(elemType, sb, val, newline);
            }
        }

        else if (node instanceof FunctionCallNode callNode) {
            String callIR = visitor.visit(callNode);
            sb.append(callIR);

            String val = extractTemp(callIR);
            llvmType = extractType(callIR);

            if ("i8*".equals(llvmType)) {
                String casted = temps.newTemp();
                sb.append("  ").append(casted)
                        .append(" = bitcast i8* ").append(val).append(" to %ArrayList*\n");
                val = casted;
                llvmType = "%ArrayList*";
            }

            TypeInfos fnType = visitor.getFunctionType(callNode.getName());
            if (fnType == null || !fnType.isList())
                throw new RuntimeException("Função não retorna lista: " + callNode.getName());

            elemType = fnType.getElementType();

            if (llvmType.endsWith("**")) {
                String loaded = temps.newTemp();
                sb.append("  ").append(loaded)
                        .append(" = load ").append(llvmType, 0, llvmType.length() - 1)
                        .append(", ").append(llvmType).append(" ").append(val).append("\n");
                val = loaded;
                llvmType = llvmType.substring(0, llvmType.length() - 1);
            }

            switch (llvmType) {
                case "%struct.ArrayListInt*" -> sb.append("  call void @arraylist_print_int(%struct.ArrayListInt* ")
                        .append(val).append(", i1 ").append(newline ? "1" : "0").append(")\n");
                case "%struct.ArrayListDouble*" -> sb.append("  call void @arraylist_print_double(%struct.ArrayListDouble* ")
                        .append(val).append(", i1 ").append(newline ? "1" : "0").append(")\n");
                case "%struct.ArrayListBool*" -> sb.append("  call void @arraylist_print_bool(%struct.ArrayListBool* ")
                        .append(val).append(", i1 ").append(newline ? "1" : "0").append(")\n");
                default -> handleGeneric(elemType, sb, val, newline);
            }
        }

        else {
            throw new RuntimeException("ListPrintHandler não sabe lidar com: " + node.getClass());
        }

        return sb.toString();
    }

    private void handleGeneric(String elemType, StringBuilder sb, String tmp, boolean newline) {
        if (elemType == null || elemType.equals("?")) {
            sb.append("  call void @arraylist_print_ptr(%ArrayList* ").append(tmp)
                    .append(", void (i8*)* null, i1 ").append(newline ? "1" : "0").append(")\n");
            return;
        }

        switch (elemType) {
            case "int" -> sb.append("  call void @arraylist_print_int(%struct.ArrayListInt* ")
                    .append(tmp).append(", i1 ").append(newline ? "1" : "0").append(")\n");
            case "double" -> sb.append("  call void @arraylist_print_double(%struct.ArrayListDouble* ")
                    .append(tmp).append(", i1 ").append(newline ? "1" : "0").append(")\n");
            case "boolean" -> sb.append("  call void @arraylist_print_bool(%struct.ArrayListBool* ")
                    .append(tmp).append(", i1 ").append(newline ? "1" : "0").append(")\n");
            case "string" -> sb.append("  call void @arraylist_print_string(%ArrayList* ")
                    .append(tmp).append(", i1 ").append(newline ? "1" : "0").append(")\n"); // ✅ segura
            default -> {
                String structName = elemType.startsWith("Struct<")
                        ? elemType.substring("Struct<".length(), elemType.length() - 1).replace('.', '_')
                        : elemType.replace('.', '_');
                sb.append("  call void @arraylist_print_ptr(%ArrayList* ")
                        .append(tmp)
                        .append(", void (i8*)* @print_").append(structName)
                        .append(", i1 ").append(newline ? "1" : "0").append(")\n");
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
