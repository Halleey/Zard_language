package low.structs;

import ast.ASTNode;
import ast.functions.FunctionNode;
import ast.functions.ParamInfo;
import ast.structs.ImplNode;
import ast.structs.StructNode;
import context.statics.symbols.ListType;
import context.statics.symbols.PrimitiveTypes;
import context.statics.symbols.StructType;
import context.statics.symbols.Type;
import low.TempManager;
import low.module.LLVisitorMain;
import low.main.TypeInfos;

import java.util.ArrayList;
import java.util.List;



public class ImplEmitter {

    private final LLVisitorMain visitor;

    public ImplEmitter(LLVisitorMain visitor, TempManager temps) {
        this.visitor = visitor;
    }

    private boolean hasSpecializations(String baseStruct) {

        for (String key : visitor.specializedStructs.keySet()) {
            if (key.startsWith(baseStruct + "<") && key.endsWith(">")) {
                return true;
            }
        }
        return false;
    }

    public String emit(ImplNode node) {

        StringBuilder llvm = new StringBuilder();
        String baseStruct = node.getStructName();

        llvm.append(";; ==== Impl Definitions ====\n");

        boolean hasSpecs = hasSpecializations(baseStruct);

        for (FunctionNode fn : node.getMethods()) {

            if (hasSpecs) {

                for (StructNode spec : visitor.specializedStructs.values()) {

                    String specName = spec.getName();
                    if (!specName.startsWith(baseStruct + "_")) continue;

                    String inner = specName.substring(baseStruct.length() + 1);

                    llvm.append("; === Impl especializada para Struct<")
                            .append(baseStruct).append("<").append(inner).append(">> ===\n");

                    llvm.append(generateFunctionImpl(baseStruct, fn, inner));
                }

            } else {

                llvm.append(emitSimpleMethod(baseStruct, fn));
            }
        }

        llvm.append("\n");
        return llvm.toString();
    }

    private String emitSimpleMethod(String baseStruct, FunctionNode fn) {

        StringBuilder sb = new StringBuilder();

        String fnName = baseStruct + "_" + fn.getName();

        String structLLVM = "%" + baseStruct;
        String paramTypeLLVM = structLLVM + "*";

        Type retTypeSrc = fn.getReturnType();
        String retTypeLLVM = mapToLLVMType(retTypeSrc);

        boolean returnsSelf =
                retTypeSrc instanceof StructType st &&
                        st.name().equals(baseStruct);

        List<ParamInfo> params = fn.getParameters();

        String receiverName = !params.isEmpty() ? params.get(0).name() : "s";

        StringBuilder paramList = new StringBuilder();
        paramList.append(paramTypeLLVM).append(" %").append(receiverName);

        for (int i = 1; i < params.size(); i++) {

            ParamInfo p = params.get(i);

            String llvmType = mapToLLVMType(p.type());

            paramList.append(", ")
                    .append(llvmType)
                    .append(" %")
                    .append(p.name());
        }

        sb.append("; === Método: ").append(fnName).append(" ===\n");
        sb.append("define ").append(retTypeLLVM).append(" @").append(fnName)
                .append("(").append(paramList).append(") {\n");

        sb.append("entry:\n");

        String receiverPtr = "%" + receiverName + "_addr";

        sb.append("  ").append(receiverPtr)
                .append(" = alloca ").append(paramTypeLLVM).append("\n");

        sb.append("  store ")
                .append(paramTypeLLVM)
                .append(" %").append(receiverName)
                .append(", ")
                .append(paramTypeLLVM)
                .append("* ")
                .append(receiverPtr)
                .append("\n");

        visitor.getVariableEmitter().registerVarPtr(receiverName, receiverPtr);

        visitor.putVarType(
                receiverName,
                new TypeInfos(new StructType(baseStruct), paramTypeLLVM));

        for (int i = 1; i < params.size(); i++) {

            ParamInfo p = params.get(i);

            String llvmType = mapToLLVMType(p.type());
            String ptr = "%" + p.name() + "_addr";

            sb.append("  ")
                    .append(ptr)
                    .append(" = alloca ")
                    .append(llvmType)
                    .append("\n");

            sb.append("  store ")
                    .append(llvmType)
                    .append(" %")
                    .append(p.name())
                    .append(", ")
                    .append(llvmType)
                    .append("* ")
                    .append(ptr)
                    .append("\n");

            visitor.getVariableEmitter().registerVarPtr(p.name(), ptr);

            visitor.putVarType(p.name(), new TypeInfos(p.type(), llvmType));
        }

        if (fn.getBody() != null) {

            for (ASTNode stmt : fn.getBody()) {
                sb.append(stmt.accept(visitor));
            }
        }

        if ("void".equals(retTypeLLVM)) {

            sb.append("  ret void\n");

        } else if (returnsSelf) {

            sb.append("  ret ")
                    .append(paramTypeLLVM)
                    .append(" %")
                    .append(receiverName)
                    .append("\n");

        } else {

            sb.append("  ret ")
                    .append(retTypeLLVM)
                    .append(" undef\n");
        }

        sb.append("}\n\n");

        return sb.toString();
    }

    private String generateFunctionImpl(String baseStruct, FunctionNode fn, String specialized) {

        StringBuilder sb = new StringBuilder();

        List<ParamInfo> params = fn.getParameters();
        if (params.isEmpty()) {
            return "";
        }

        String receiverName = params.get(0).name();

        String fnName = baseStruct + "_" + specialized + "_" + fn.getName();

        String structLLVM = "%" + baseStruct + "_" + specialized;
        String paramTypeLLVM = structLLVM + "*";

        Type retTypeSrc = fn.getReturnType();
        String retTypeLLVM = mapToLLVMType(retTypeSrc);

        boolean returnsSelf =
                retTypeSrc instanceof StructType;

        StringBuilder paramList = new StringBuilder();
        paramList.append(paramTypeLLVM).append(" %").append(receiverName);

        List<String> llvmParamTypes = new ArrayList<>();
        llvmParamTypes.add(paramTypeLLVM);

        for (int i = 1; i < params.size(); i++) {

            ParamInfo p = params.get(i);

            String llvmType = mapToLLVMType(p.type());

            llvmParamTypes.add(llvmType);

            paramList.append(", ")
                    .append(llvmType)
                    .append(" %")
                    .append(p.name());
        }

        sb.append("; === Função: ").append(fnName).append(" ===\n");

        sb.append("define ")
                .append(retTypeLLVM)
                .append(" @")
                .append(fnName)
                .append("(")
                .append(paramList)
                .append(") {\n");

        sb.append("entry:\n");

        String receiverPtr = "%" + receiverName + "_addr";

        sb.append("  ")
                .append(receiverPtr)
                .append(" = alloca ")
                .append(paramTypeLLVM)
                .append("\n");

        sb.append("  store ")
                .append(paramTypeLLVM)
                .append(" %")
                .append(receiverName)
                .append(", ")
                .append(paramTypeLLVM)
                .append("* ")
                .append(receiverPtr)
                .append("\n");

        if (fn.getBody() != null) {

            LLVisitorMain isolated = visitor.fork();

            for (ASTNode stmt : fn.getBody()) {
                sb.append(stmt.accept(isolated));
            }
        }

        if ("void".equals(retTypeLLVM)) {

            sb.append("  ret void\n");

        } else if (returnsSelf) {

            sb.append("  ret ")
                    .append(paramTypeLLVM)
                    .append(" %")
                    .append(receiverName)
                    .append("\n");

        } else {

            sb.append("  ret ")
                    .append(retTypeLLVM)
                    .append(" undef\n");
        }

        sb.append("}\n\n");

        return sb.toString();
    }

    private String mapToLLVMType(Type type) {

        if (type == null) return "void";

        if (type instanceof PrimitiveTypes prim) {

            return switch (prim.name()) {

                case "int" -> "i32";
                case "double" -> "double";
                case "bool" -> "i1";
                case "string" -> "%String*";
                case "void" -> "void";

                default -> "i8*";
            };
        }

        if (type instanceof StructType st) {

            return "%" + st.name() + "*";
        }

        if (type instanceof ListType lt) {

            Type elemType = lt.elementType();

            if (elemType instanceof PrimitiveTypes prim) {

                return switch (prim.name()) {

                    case "int" -> "%struct.ArrayListInt*";
                    case "double" -> "%struct.ArrayListDouble*";
                    case "bool" -> "%struct.ArrayListBool*";

                    default -> "%ArrayList*";
                };
            }

            return "%ArrayList*";
        }

        return "i8*";
    }
}