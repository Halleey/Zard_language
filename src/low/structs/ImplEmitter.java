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
import low.module.builders.mappers.LLVMTypeMapper;
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

    // 🔥 AGORA retorna LLVMValue
    public LLVMValue emit(ImplNode node) {
        StringBuilder llvm = new StringBuilder();
        String baseStruct = node.getStructName();

        boolean hasSpecs = hasSpecializations(baseStruct);

        for (FunctionNode fn : node.getMethods()) {
            if (hasSpecs) {
                for (StructNode spec : visitor.specializedStructs.values()) {
                    String specName = spec.getName();
                    if (!specName.startsWith(baseStruct + "_")) continue;

                    String inner = specName.substring(baseStruct.length() + 1);

                    LLVMValue val = generateFunctionImpl(baseStruct, fn, inner);
                    llvm.append(val.getCode());
                }
            } else {
                LLVMValue val = emitSimpleMethod(baseStruct, fn);
                llvm.append(val.getCode());
            }
        }

        return new LLVMValue(new LLVMVoid(), "", llvm.toString());
    }

    // =========================
    // SIMPLE METHOD
    // =========================
    private LLVMValue emitSimpleMethod(String baseStruct, FunctionNode fn) {

        StringBuilder sb = new StringBuilder();

        String fnName = baseStruct + "_" + fn.getName();

        LLVMStruct structType = new LLVMStruct(baseStruct);
        LLVMPointer receiverLLVMType = new LLVMPointer(structType);

        Type retTypeSrc = fn.getReturnType();
        LLVMTYPES retLLVMType = LLVMTypeMapper.from(retTypeSrc);

        boolean returnsSelf =
                retTypeSrc instanceof StructType st &&
                        st.name().equals(baseStruct);

        List<ParamInfo> params = fn.getParameters();
        String receiverName = !params.isEmpty() ? params.get(0).name() : "self";

        // ===== assinatura =====
        List<String> paramList = new ArrayList<>();
        paramList.add(receiverLLVMType + " %" + receiverName);

        for (int i = 1; i < params.size(); i++) {
            ParamInfo p = params.get(i);
            LLVMTYPES llvmType = LLVMTypeMapper.from(p.typeObj());
            paramList.add(llvmType + " %" + p.name());
        }

        sb.append("; === Método: ").append(fnName).append(" ===\n");
        sb.append("define ").append(retLLVMType)
                .append(" @").append(fnName)
                .append("(").append(String.join(", ", paramList)).append(") {\nentry:\n");

        // ===== receiver =====
        String receiverPtr = temps.newTemp();

        sb.append("  ").append(receiverPtr)
                .append(" = alloca ").append(receiverLLVMType).append("\n");

        sb.append("  store ").append(receiverLLVMType)
                .append(" %").append(receiverName)
                .append(", ").append(receiverLLVMType)
                .append("* ").append(receiverPtr).append("\n");

        visitor.getVariableEmitter().registerVarPtr(receiverName, receiverPtr);
        visitor.putVarType(receiverName,
                new TypeInfos(new StructType(baseStruct), receiverLLVMType));

        // ===== params =====
        for (int i = 1; i < params.size(); i++) {
            ParamInfo p = params.get(i);
            LLVMTYPES llvmType = LLVMTypeMapper.from(p.typeObj());

            String ptr = temps.newTemp();

            sb.append("  ").append(ptr)
                    .append(" = alloca ").append(llvmType).append("\n");

            sb.append("  store ").append(llvmType)
                    .append(" %").append(p.name())
                    .append(", ").append(llvmType)
                    .append("* ").append(ptr).append("\n");

            visitor.getVariableEmitter().registerVarPtr(p.name(), ptr);
            visitor.putVarType(p.name(), new TypeInfos(p.typeObj(), llvmType));
        }

        // ===== corpo =====
        if (fn.getBody() != null) {
            for (ASTNode stmt : fn.getBody()) {
                LLVMValue stmtVal = stmt.accept(visitor);
                if (stmtVal != null && stmtVal.getCode() != null) {
                    sb.append(stmtVal.getCode());
                }
            }
        }

        // ===== retorno =====
        if (retLLVMType instanceof LLVMVoid) {
            sb.append("  ret void\n");
        } else if (returnsSelf) {
            sb.append("  ret ").append(receiverLLVMType)
                    .append(" %").append(receiverName).append("\n");
        } else {
            sb.append("  ret ").append(retLLVMType).append(" undef\n");
        }

        sb.append("}\n\n");

        return new LLVMValue(retLLVMType, "%" + fnName, sb.toString());
    }

    // =========================
    // SPECIALIZED
    // =========================
    private LLVMValue generateFunctionImpl(String baseStruct, FunctionNode fn, String specialized) {

        StringBuilder sb = new StringBuilder();

        List<ParamInfo> params = fn.getParameters();
        if (params.isEmpty()) {
            return new LLVMValue(new LLVMVoid(), "", "");
        }

        String receiverName = params.get(0).name();
        String fnName = baseStruct + "_" + specialized + "_" + fn.getName();

        LLVMStruct structType = new LLVMStruct(baseStruct + "_" + specialized);
        LLVMPointer receiverLLVMType = new LLVMPointer(structType);

        List<String> paramList = new ArrayList<>();
        paramList.add(receiverLLVMType + " %" + receiverName);

        for (int i = 1; i < params.size(); i++) {
            LLVMTYPES llvmType = LLVMTypeMapper.from(params.get(i).typeObj());
            paramList.add(llvmType + " %" + params.get(i).name());
        }

        LLVMTYPES retLLVMType = LLVMTypeMapper.from(fn.getReturnType());
        boolean returnsSelf = fn.getReturnType() instanceof StructType;

        sb.append("; === Função: ").append(fnName).append(" ===\n");
        sb.append("define ").append(retLLVMType)
                .append(" @").append(fnName)
                .append("(").append(String.join(", ", paramList)).append(") {\nentry:\n");

        // ===== receiver =====
        String receiverPtr = temps.newTemp();

        sb.append("  ").append(receiverPtr)
                .append(" = alloca ").append(receiverLLVMType).append("\n");

        sb.append("  store ").append(receiverLLVMType)
                .append(" %").append(receiverName)
                .append(", ").append(receiverLLVMType)
                .append("* ").append(receiverPtr).append("\n");

        // ===== corpo isolado =====
        if (fn.getBody() != null) {
            LLVisitorMain forked = visitor.fork();

            for (ASTNode stmt : fn.getBody()) {
                LLVMValue stmtVal = stmt.accept(forked);
                if (stmtVal != null && stmtVal.getCode() != null) {
                    sb.append(stmtVal.getCode());
                }
            }
        }

        // ===== retorno =====
        if (retLLVMType instanceof LLVMVoid) {
            sb.append("  ret void\n");
        } else if (returnsSelf) {
            sb.append("  ret ").append(receiverLLVMType)
                    .append(" %").append(receiverName).append("\n");
        } else {
            sb.append("  ret ").append(retLLVMType).append(" undef\n");
        }

        sb.append("}\n\n");

        return new LLVMValue(retLLVMType, "%" + fnName, sb.toString());
    }
}