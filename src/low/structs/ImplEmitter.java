package low.structs;

import ast.ASTNode;
import ast.functions.FunctionNode;
import ast.structs.ImplNode;
import ast.structs.StructNode;
import low.module.LLVisitorMain;

import java.util.Map;


public class ImplEmitter {
    private final LLVisitorMain visitor;

    public ImplEmitter(LLVisitorMain visitor) {
        this.visitor = visitor;
    }

    public String emit(ImplNode node) {
        StringBuilder llvm = new StringBuilder();
        String baseStruct = node.getStructName();

        llvm.append(";; ==== Impl Definitions ====\n");

        boolean temEspecializacao = !visitor.specializedStructs.isEmpty();

        for (FunctionNode fn : node.getMethods()) {
            if (isListImplMethod(baseStruct, fn)) {
                // só gera genérico se não há especialização inferida
                if (!temEspecializacao) {
                    llvm.append("; === Impl genérica para Struct<").append(baseStruct).append("> ===\n");
                    llvm.append(generateFunctionImpl(baseStruct, fn, null));
                }

                for (Map.Entry<String, StructNode> entry : visitor.specializedStructs.entrySet()) {
                    String name = entry.getKey();
                    if (name.startsWith(baseStruct + "<")) {
                        String inner = extractInnerType(name);
                        llvm.append("; === Impl especializada para Struct<")
                                .append(baseStruct).append("<").append(inner).append(">> ===\n");
                        llvm.append(generateFunctionImpl(baseStruct, fn, inner));
                    }
                }
            } else {
                llvm.append(emitSimpleMethod(baseStruct, fn));
            }
        }

        llvm.append("\n");
        return llvm.toString();
    }


    private boolean isListImplMethod(String baseStruct, FunctionNode fn) {
        return "Set".equals(baseStruct)
                && fn.getParams() != null
                && fn.getParams().size() >= 2;
    }

    private String emitListImpls(String baseStruct, FunctionNode fn) {
        StringBuilder llvm = new StringBuilder();

        llvm.append("; === Impl para Struct<").append(baseStruct).append("> ===\n");
        llvm.append(generateFunctionImpl(baseStruct, fn, null));

        if (!visitor.specializedStructs.isEmpty()) {
            for (Map.Entry<String, StructNode> entry : visitor.specializedStructs.entrySet()) {
                String name = entry.getKey();
                if (name.startsWith(baseStruct + "<")) {
                    String inner = extractInnerType(name);
                    llvm.append("; === Impl especializado para Struct<")
                            .append(baseStruct).append("<").append(inner).append(">> ===\n");
                    llvm.append(generateFunctionImpl(baseStruct, fn, inner));
                }
            }
        }

        return llvm.toString();
    }

    private String emitSimpleMethod(String baseStruct, FunctionNode fn) {
        StringBuilder sb = new StringBuilder();

        String fnName = baseStruct + "_" + fn.getName();
        String structLLVM = "%" + baseStruct;
        String paramTypeLLVM = structLLVM + "*";
        String retType = paramTypeLLVM;

        sb.append("; === Método: ").append(fnName).append(" ===\n");
        sb.append("define ").append(retType).append(" @").append(fnName)
                .append("(").append(paramTypeLLVM).append(" %self) {\n");
        sb.append("entry:\n");

        if (fn.getBody() != null) {
            for (ASTNode stmt : fn.getBody()) {
                sb.append(stmt.accept(visitor));
            }
        }

        sb.append("  ret ").append(retType).append(" %self\n");
        sb.append("}\n\n");

        return sb.toString();
    }

    private String generateFunctionImpl(String baseStruct, FunctionNode fn, String specialized) {
        StringBuilder sb = new StringBuilder();

        String fnName;
        String structLLVM;
        String paramTypeLLVM;
        String valueTypeLLVM;

        if (specialized == null) {
            fnName = baseStruct + "_" + fn.getName();
            structLLVM = "%" + baseStruct;
            paramTypeLLVM = structLLVM + "*";
            valueTypeLLVM = "i8*";
        } else {
            fnName = baseStruct + "_" + specialized + "_" + fn.getName();
            structLLVM = "%" + baseStruct + "_" + specialized;
            paramTypeLLVM = structLLVM + "*";
            valueTypeLLVM = mapToLLVMType(specialized);
        }

        String retType = paramTypeLLVM;
        String paramS = fn.getParams().get(0);
        String valueS = fn.getParams().size() > 1 ? fn.getParams().get(1) : "value";

        sb.append("; === Função: ").append(fnName).append(" ===\n");
        sb.append("define ").append(retType).append(" @").append(fnName)
                .append("(").append(paramTypeLLVM).append(" %").append(paramS)
                .append(", ").append(valueTypeLLVM).append(" %").append(valueS).append(") {\n");
        sb.append("entry:\n");

        sb.append("  %").append(paramS).append("_addr = alloca ").append(paramTypeLLVM).append("\n");
        sb.append("  store ").append(paramTypeLLVM).append(" %").append(paramS)
                .append(", ").append(paramTypeLLVM).append("* %").append(paramS).append("_addr\n");

        sb.append("  %").append(valueS).append("_addr = alloca ").append(valueTypeLLVM).append("\n");
        sb.append("  store ").append(valueTypeLLVM).append(" %").append(valueS)
                .append(", ").append(valueTypeLLVM).append("* %").append(valueS).append("_addr\n");

        sb.append("  %tmp0 = load ").append(paramTypeLLVM).append(", ").append(paramTypeLLVM)
                .append("* %").append(paramS).append("_addr\n");
        sb.append("  %tmp1 = getelementptr inbounds ").append(structLLVM).append(", ")
                .append(structLLVM).append("* %tmp0, i32 0, i32 0\n");

        if (specialized == null) {
            sb.append("  %tmp2 = load %ArrayList*, %ArrayList** %tmp1\n");
            sb.append("  %tmp3 = load i8*, i8** %").append(valueS).append("_addr\n");
            sb.append("  call void @arraylist_add_string(%ArrayList* %tmp2, i8* %tmp3)\n");
        } else if (specialized.equals("int")) {
            sb.append("  %tmp2 = load %struct.ArrayListInt*, %struct.ArrayListInt** %tmp1\n");
            sb.append("  %tmp3 = load i32, i32* %").append(valueS).append("_addr\n");
            sb.append("  call void @arraylist_add_int(%struct.ArrayListInt* %tmp2, i32 %tmp3)\n");
        } else if (specialized.equals("double")) {
            sb.append("  %tmp2 = load %struct.ArrayListDouble*, %struct.ArrayListDouble** %tmp1\n");
            sb.append("  %tmp3 = load double, double* %").append(valueS).append("_addr\n");
            sb.append("  call void @arraylist_add_double(%struct.ArrayListDouble* %tmp2, double %tmp3)\n");
        } else if (specialized.equals("bool") || specialized.equals("boolean")) {
            sb.append("  %tmp2 = load %struct.ArrayListBool*, %struct.ArrayListBool** %tmp1\n");
            sb.append("  %tmp3 = load i1, i1* %").append(valueS).append("_addr\n");
            sb.append("  call void @arraylist_add_bool(%struct.ArrayListBool* %tmp2, i1 %tmp3)\n");
        } else {
            sb.append("  ; TODO: Suporte a tipo ").append(specialized).append("\n");
        }

        sb.append("  ret ").append(retType).append(" %tmp0\n");
        sb.append("}\n\n");
        return sb.toString();
    }

    private String extractInnerType(String s) {
        int start = s.indexOf('<');
        int end = s.indexOf('>');
        if (start == -1 || end == -1) return "?";
        return s.substring(start + 1, end).trim();
    }

    private String mapToLLVMType(String inner) {
        return switch (inner) {
            case "int" -> "i32";
            case "double" -> "double";
            case "bool", "boolean" -> "i1";
            case "string", "String" -> "%String*";
            default -> "i8*";
        };
    }
}
