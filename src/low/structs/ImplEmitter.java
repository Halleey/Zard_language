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
import low.functions.TypeMapper;
import low.module.LLVisitorMain;
import low.main.TypeInfos;
import low.module.builders.LLVMPointer;
import low.module.builders.LLVMTYPES;
import low.module.builders.LLVMValue;
import low.module.builders.primitives.LLVMVoid;
import low.module.builders.structs.LLVMStruct;

import java.util.ArrayList;
import java.util.List;
public class ImplEmitter {

    private final LLVisitorMain visitor;
    private final TempManager temps;

    public ImplEmitter(LLVisitorMain visitor, TempManager temps) {
        this.visitor = visitor;
        this.temps = temps;
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

        return llvm.toString();
    }

    private String emitSimpleMethod(String baseStruct, FunctionNode fn) {
        StringBuilder sb = new StringBuilder();

        String fnName = baseStruct + "_" + fn.getName();
        LLVMStruct structType = new LLVMStruct(baseStruct);
        LLVMPointer receiverLLVMType = new LLVMPointer(structType);

        Type retTypeSrc = fn.getReturnType();
        LLVMTYPES retLLVMType = TypeMapper.from(retTypeSrc);

        boolean returnsSelf = retTypeSrc instanceof StructType st && st.name().equals(baseStruct);

        List<ParamInfo> params = fn.getParameters();
        String receiverName = !params.isEmpty() ? params.get(0).name() : "s";

        // === Lista de parâmetros LLVM ===
        StringBuilder paramList = new StringBuilder();
        paramList.append(receiverLLVMType).append(" %").append(receiverName);

        for (int i = 1; i < params.size(); i++) {
            ParamInfo p = params.get(i);
            LLVMTYPES llvmType = TypeMapper.from(p.type());
            paramList.append(", ").append(llvmType).append(" %").append(p.name());
        }

        sb.append("; === Método: ").append(fnName).append(" ===\n");
        sb.append("define ").append(retLLVMType).append(" @").append(fnName)
                .append("(").append(paramList).append(") {\n");
        sb.append("entry:\n");

        // === Aloca receiver ===
        String receiverPtr = temps.newTemp();
        sb.append("  ").append(receiverPtr).append(" = alloca ").append(receiverLLVMType).append("\n");
        sb.append("  store ").append(receiverLLVMType).append(" %").append(receiverName)
                .append(", ").append(receiverLLVMType).append("* ").append(receiverPtr).append("\n");

        visitor.getVariableEmitter().registerVarPtr(receiverName, receiverPtr);
        visitor.putVarType(receiverName, new TypeInfos(new StructType(baseStruct), receiverLLVMType));

        // === Aloca outros parâmetros ===
        for (int i = 1; i < params.size(); i++) {
            ParamInfo p = params.get(i);
            LLVMTYPES llvmType = TypeMapper.from(p.type());
            String ptr = temps.newTemp();
            sb.append("  ").append(ptr).append(" = alloca ").append(llvmType).append("\n");
            sb.append("  store ").append(llvmType).append(" %").append(p.name())
                    .append(", ").append(llvmType).append("* ").append(ptr).append("\n");
            visitor.getVariableEmitter().registerVarPtr(p.name(), ptr);
            visitor.putVarType(p.name(), new TypeInfos(p.type(), llvmType));
        }

        // === Corpo da função ===
        if (fn.getBody() != null) {
            for (ASTNode stmt : fn.getBody()) {
                LLVMValue stmtVal = stmt.accept(visitor);
                sb.append(stmtVal.getCode());
            }
        }

        // === Retorno ===
        if (retLLVMType instanceof LLVMVoid) {
            sb.append("  ret void\n");
        } else if (returnsSelf) {
            sb.append("  ret ").append(receiverLLVMType).append(" %").append(receiverName).append("\n");
        } else {
            sb.append("  ret ").append(retLLVMType).append(" undef\n");
        }

        sb.append("}\n\n");
        return sb.toString();
    }

    private String generateFunctionImpl(String baseStruct, FunctionNode fn, String specialized) {
        StringBuilder sb = new StringBuilder();
        List<ParamInfo> params = fn.getParameters();
        if (params.isEmpty()) return "";

        String receiverName = params.get(0).name();
        String fnName = baseStruct + "_" + specialized + "_" + fn.getName();

        LLVMStruct structType = new LLVMStruct(baseStruct + "_" + specialized);
        LLVMPointer receiverLLVMType = new LLVMPointer(structType);

        StringBuilder paramList = new StringBuilder();
        paramList.append(receiverLLVMType).append(" %").append(receiverName);

        for (int i = 1; i < params.size(); i++) {
            LLVMTYPES llvmType = TypeMapper.from(params.get(i).type());
            paramList.append(", ").append(llvmType).append(" %").append(params.get(i).name());
        }

        LLVMTYPES retLLVMType = TypeMapper.from(fn.getReturnType());
        boolean returnsSelf = fn.getReturnType() instanceof StructType;

        sb.append("; === Função: ").append(fnName).append(" ===\n");
        sb.append("define ").append(retLLVMType).append(" @").append(fnName)
                .append("(").append(paramList).append(") {\nentry:\n");

        // === Aloca receiver ===
        String receiverPtr = temps.newTemp();
        sb.append("  ").append(receiverPtr).append(" = alloca ").append(receiverLLVMType).append("\n");
        sb.append("  store ").append(receiverLLVMType).append(" %").append(receiverName)
                .append(", ").append(receiverLLVMType).append("* ").append(receiverPtr).append("\n");

        // === Corpo isolado ===
        if (fn.getBody() != null) {
            LLVisitorMain forked = visitor.fork();
            for (ASTNode stmt : fn.getBody()) {
                LLVMValue stmtVal = stmt.accept(forked);
                sb.append(stmtVal.getCode());
            }
        }

        // === Retorno ===
        if (retLLVMType instanceof LLVMVoid) {
            sb.append("  ret void\n");
        } else if (returnsSelf) {
            sb.append("  ret ").append(receiverLLVMType).append(" %").append(receiverName).append("\n");
        } else {
            sb.append("  ret ").append(retLLVMType).append(" undef\n");
        }

        sb.append("}\n\n");
        return sb.toString();
    }
}