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
            Type elem = visitor.inferListElementType(sfa);
            return elem != null;
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
        String val;
        Type elementType;

        if (node instanceof VariableNode varNode) {

            String llvmVar = visitor.varEmitter.getVarPtr(varNode.getName());
            TypeInfos info = visitor.getVarType(varNode.getName());

            if (info == null)
                throw new RuntimeException("Variável não registrada: " + varNode.getName());

            ListType listType = (ListType) info.getType();
            elementType = listType.elementType();
            llvmType = info.getLLVMType();

            val = loadPointer(sb, llvmVar, llvmType);

        }

        else if (node instanceof StructFieldAccessNode sfa) {

            String accessIR = visitor.visit(sfa);
            sb.append(accessIR);

            val = extractTemp(accessIR);
            llvmType = extractType(accessIR);

            elementType = visitor.inferListElementType(sfa);

            if (elementType == null)
                throw new RuntimeException("Não foi possível inferir tipo da lista em StructFieldAccess");

            val = ensureLoaded(sb, val, llvmType);
            llvmType = normalizePointer(llvmType);
        }

        else if (node instanceof FunctionCallNode callNode) {

            String callIR = visitor.visit(callNode);
            sb.append(callIR);

            val = extractTemp(callIR);
            llvmType = extractType(callIR);

            if ("i8*".equals(llvmType)) {
                String casted = temps.newTemp();
                sb.append("  ").append(casted)
                        .append(" = bitcast i8* ")
                        .append(val)
                        .append(" to %ArrayList*\n");

                val = casted;
                llvmType = "%ArrayList*";
            }

            TypeInfos fnType = visitor.getFunctionType(callNode.getName());

            if (fnType == null || !(fnType.getType() instanceof ListType lt))
                throw new RuntimeException("Função não retorna lista: " + callNode.getName());

            elementType = lt.elementType();

            val = ensureLoaded(sb, val, llvmType);
            llvmType = normalizePointer(llvmType);
        }

        else {
            throw new RuntimeException("ListPrintHandler não sabe lidar com: " + node.getClass());
        }

        emitPrintCall(sb, llvmType, val, elementType, newline);

        return sb.toString();
    }

    private String loadPointer(StringBuilder sb, String llvmVar, String llvmType) {

        String tmp = temps.newTemp();

        sb.append("  ").append(tmp)
                .append(" = load ")
                .append(llvmType).append(", ")
                .append(llvmType).append("* ")
                .append(llvmVar).append("\n");

        return tmp;
    }

    private String ensureLoaded(StringBuilder sb, String val, String llvmType) {

        if (!llvmType.endsWith("**"))
            return val;

        String loaded = temps.newTemp();

        sb.append("  ").append(loaded)
                .append(" = load ")
                .append(llvmType, 0, llvmType.length() - 1)
                .append(", ")
                .append(llvmType)
                .append(" ")
                .append(val)
                .append("\n");

        return loaded;
    }

    private String normalizePointer(String llvmType) {
        if (llvmType.endsWith("**"))
            return llvmType.substring(0, llvmType.length() - 1);
        return llvmType;
    }

    private void emitPrintCall(StringBuilder sb,
                               String llvmType,
                               String val,
                               Type elementType,
                               boolean newline) {

        switch (llvmType) {

            case "%struct.ArrayListInt*" ->
                    sb.append("  call void @arraylist_print_int(%struct.ArrayListInt* ")
                            .append(val)
                            .append(", i1 ")
                            .append(newline ? "1" : "0")
                            .append(")\n");

            case "%struct.ArrayListDouble*" ->
                    sb.append("  call void @arraylist_print_double(%struct.ArrayListDouble* ")
                            .append(val)
                            .append(", i1 ")
                            .append(newline ? "1" : "0")
                            .append(")\n");

            case "%struct.ArrayListBool*" ->
                    sb.append("  call void @arraylist_print_bool(%struct.ArrayListBool* ")
                            .append(val)
                            .append(", i1 ")
                            .append(newline ? "1" : "0")
                            .append(")\n");

            default -> handleGeneric(elementType, sb, val, newline);
        }
    }

    private void handleGeneric(Type elemType,
                               StringBuilder sb,
                               String val,
                               boolean newline) {

        if (elemType == null) {

            sb.append("  call void @arraylist_print_ptr(%ArrayList* ")
                    .append(val)
                    .append(", void (i8*)* null, i1 ")
                    .append(newline ? "1" : "0")
                    .append(")\n");

            return;
        }

        if (elemType == PrimitiveTypes.INT) {

            sb.append("  call void @arraylist_print_int(%struct.ArrayListInt* ")
                    .append(val)
                    .append(", i1 ")
                    .append(newline ? "1" : "0")
                    .append(")\n");

            return;
        }

        if (elemType == PrimitiveTypes.DOUBLE) {

            sb.append("  call void @arraylist_print_double(%struct.ArrayListDouble* ")
                    .append(val)
                    .append(", i1 ")
                    .append(newline ? "1" : "0")
                    .append(")\n");

            return;
        }

        if (elemType == PrimitiveTypes.BOOL) {

            sb.append("  call void @arraylist_print_bool(%struct.ArrayListBool* ")
                    .append(val)
                    .append(", i1 ")
                    .append(newline ? "1" : "0")
                    .append(")\n");

            return;
        }

        if (elemType == PrimitiveTypes.STRING) {

            sb.append("  call void @arraylist_print_string(%ArrayList* ")
                    .append(val)
                    .append(", i1 ")
                    .append(newline ? "1" : "0")
                    .append(")\n");

            return;
        }

        if (elemType instanceof StructType structType) {

            String structName = structType.name().replace('.', '_');

            sb.append("  call void @arraylist_print_ptr(%ArrayList* ")
                    .append(val)
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
        if (t == -1) return "";

        return code.substring(v + 6, t).trim();
    }

    private String extractType(String code) {

        int t = code.lastIndexOf(";;TYPE:");
        if (t == -1) return "";

        int end = code.indexOf("\n", t);
        if (end == -1) end = code.length();

        return code.substring(t + 7, end).trim();
    }
}