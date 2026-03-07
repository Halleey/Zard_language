package low.prints;

import ast.ASTNode;
import ast.functions.FunctionCallNode;
import ast.structs.StructFieldAccessNode;
import ast.variables.VariableNode;
import context.statics.symbols.ListType;
import context.statics.symbols.PrimitiveTypes;
import context.statics.symbols.StructType;
import context.statics.symbols.Type;
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
            return info != null && info.getType() instanceof ListType;
        }

        if (node instanceof StructFieldAccessNode sfa) {
            Type type = visitor.inferListElementType(sfa);
            return type instanceof ListType;
        }

        if (node instanceof FunctionCallNode fn) {
            TypeInfos fnType = visitor.getFunctionType(fn.getName());
            return fnType != null && fnType.getType() instanceof ListType;
        }

        return false;
    }

    @Override
    public String emit(ASTNode node, LLVisitorMain visitor, boolean newline) {

        StringBuilder sb = new StringBuilder();
        String llvmType;
        String tmp = temps.newTemp();
        Type elementType;

        /* ================= VARIABLE ================= */

        if (node instanceof VariableNode varNode) {

            String llvmVar = visitor.varEmitter.getVarPtr(varNode.getName());
            TypeInfos info = visitor.getVarType(varNode.getName());

            if (info == null)
                throw new RuntimeException("Variável não registrada: " + varNode.getName());

            Type type = info.getType();
            if (!(type instanceof ListType listType))
                throw new RuntimeException("Esperado ListType");

            elementType = listType.elementType();
            llvmType = info.getLLVMType();

            switch (llvmType) {

                case "%struct.ArrayListInt*" -> {
                    sb.append("  ").append(tmp)
                            .append(" = load %struct.ArrayListInt*, %struct.ArrayListInt** ")
                            .append(llvmVar).append("\n");

                    sb.append("  call void @arraylist_print_int(%struct.ArrayListInt* ")
                            .append(tmp).append(", i1 ")
                            .append(newline ? "1" : "0").append(")\n");
                }

                case "%struct.ArrayListDouble*" -> {
                    sb.append("  ").append(tmp)
                            .append(" = load %struct.ArrayListDouble*, %struct.ArrayListDouble** ")
                            .append(llvmVar).append("\n");

                    sb.append("  call void @arraylist_print_double(%struct.ArrayListDouble* ")
                            .append(tmp).append(", i1 ")
                            .append(newline ? "1" : "0").append(")\n");
                }

                case "%struct.ArrayListBool*" -> {
                    sb.append("  ").append(tmp)
                            .append(" = load %struct.ArrayListBool*, %struct.ArrayListBool** ")
                            .append(llvmVar).append("\n");

                    sb.append("  call void @arraylist_print_bool(%struct.ArrayListBool* ")
                            .append(tmp).append(", i1 ")
                            .append(newline ? "1" : "0").append(")\n");
                }

                default -> {
                    sb.append("  ").append(tmp)
                            .append(" = load %ArrayList*, %ArrayList** ")
                            .append(llvmVar).append("\n");

                    handleGeneric(elementType, sb, tmp, newline);
                }
            }
        }

        else if (node instanceof StructFieldAccessNode sfa) {

            String accessIR = visitor.visit(sfa);
            sb.append(accessIR);

            String val = extractTemp(accessIR);
            llvmType = extractType(accessIR);

            Type listType = visitor.inferListElementType(sfa);
            if (!(listType instanceof ListType lt))
                throw new RuntimeException("Esperado ListType em StructFieldAccess");

            elementType = lt.elementType();

            if (llvmType.endsWith("**")) {
                String loaded = temps.newTemp();
                sb.append("  ").append(loaded)
                        .append(" = load ")
                        .append(llvmType, 0, llvmType.length() - 1)
                        .append(", ").append(llvmType).append(" ")
                        .append(val).append("\n");

                val = loaded;
                llvmType = llvmType.substring(0, llvmType.length() - 1);
            }

            switch (llvmType) {
                case "%struct.ArrayListInt*" ->
                        sb.append("  call void @arraylist_print_int(%struct.ArrayListInt* ")
                                .append(val).append(", i1 ")
                                .append(newline ? "1" : "0").append(")\n");

                case "%struct.ArrayListDouble*" ->
                        sb.append("  call void @arraylist_print_double(%struct.ArrayListDouble* ")
                                .append(val).append(", i1 ")
                                .append(newline ? "1" : "0").append(")\n");

                case "%struct.ArrayListBool*" ->
                        sb.append("  call void @arraylist_print_bool(%struct.ArrayListBool* ")
                                .append(val).append(", i1 ")
                                .append(newline ? "1" : "0").append(")\n");

                default -> handleGeneric(elementType, sb, val, newline);
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
                        .append(" = bitcast i8* ")
                        .append(val).append(" to %ArrayList*\n");

                val = casted;
                llvmType = "%ArrayList*";
            }

            TypeInfos fnType = visitor.getFunctionType(callNode.getName());
            if (fnType == null || !(fnType.getType() instanceof ListType lt))
                throw new RuntimeException("Função não retorna lista: " + callNode.getName());

            elementType = lt.elementType();

            if (llvmType.endsWith("**")) {
                String loaded = temps.newTemp();
                sb.append("  ").append(loaded)
                        .append(" = load ")
                        .append(llvmType, 0, llvmType.length() - 1)
                        .append(", ").append(llvmType).append(" ")
                        .append(val).append("\n");

                val = loaded;
                llvmType = llvmType.substring(0, llvmType.length() - 1);
            }

            switch (llvmType) {
                case "%struct.ArrayListInt*" ->
                        sb.append("  call void @arraylist_print_int(%struct.ArrayListInt* ")
                                .append(val).append(", i1 ")
                                .append(newline ? "1" : "0").append(")\n");

                case "%struct.ArrayListDouble*" ->
                        sb.append("  call void @arraylist_print_double(%struct.ArrayListDouble* ")
                                .append(val).append(", i1 ")
                                .append(newline ? "1" : "0").append(")\n");

                case "%struct.ArrayListBool*" ->
                        sb.append("  call void @arraylist_print_bool(%struct.ArrayListBool* ")
                                .append(val).append(", i1 ")
                                .append(newline ? "1" : "0").append(")\n");

                default -> handleGeneric(elementType, sb, val, newline);
            }
        }

        else {
            throw new RuntimeException("ListPrintHandler não sabe lidar com: " + node.getClass());
        }

        return sb.toString();
    }

    private void handleGeneric(Type elemType,
                               StringBuilder sb,
                               String tmp,
                               boolean newline) {

        if (elemType == null) {
            sb.append("  call void @arraylist_print_ptr(%ArrayList* ")
                    .append(tmp)
                    .append(", void (i8*)* null, i1 ")
                    .append(newline ? "1" : "0")
                    .append(")\n");
            return;
        }

        if (elemType == PrimitiveTypes.INT) {
            sb.append("  call void @arraylist_print_int(%struct.ArrayListInt* ")
                    .append(tmp).append(", i1 ")
                    .append(newline ? "1" : "0").append(")\n");
            return;
        }

        if (elemType == PrimitiveTypes.DOUBLE) {
            sb.append("  call void @arraylist_print_double(%struct.ArrayListDouble* ")
                    .append(tmp).append(", i1 ")
                    .append(newline ? "1" : "0").append(")\n");
            return;
        }

        if (elemType == PrimitiveTypes.BOOL) {
            sb.append("  call void @arraylist_print_bool(%struct.ArrayListBool* ")
                    .append(tmp).append(", i1 ")
                    .append(newline ? "1" : "0").append(")\n");
            return;
        }

        if (elemType == PrimitiveTypes.STRING) {
            sb.append("  call void @arraylist_print_string(%ArrayList* ")
                    .append(tmp).append(", i1 ")
                    .append(newline ? "1" : "0").append(")\n");
            return;
        }

        if (elemType instanceof StructType structType) {
            String structName = structType.name().replace('.', '_');

            sb.append("  call void @arraylist_print_ptr(%ArrayList* ")
                    .append(tmp)
                    .append(", void (i8*)* @print_")
                    .append(structName)
                    .append(", i1 ")
                    .append(newline ? "1" : "0")
                    .append(")\n");
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