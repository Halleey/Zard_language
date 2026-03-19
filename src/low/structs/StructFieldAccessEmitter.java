package low.structs;

import ast.ASTNode;
import ast.inputs.InputNode;
import ast.lists.ListGetNode;
import ast.structs.StructFieldAccessNode;
import ast.structs.StructInstanceNode;
import ast.structs.StructNode;
import ast.variables.VariableDeclarationNode;
import ast.variables.VariableNode;
import context.statics.symbols.ListType;
import context.statics.symbols.StructType;
import context.statics.symbols.Type;
import low.TempManager;
import low.functions.TypeMapper;
import low.inputs.InputEmitter;
import low.main.TypeInfos;
import low.module.LLVisitorMain;
import low.module.builders.LLVMTYPES;
import low.module.builders.LLVMValue;

import java.util.List;
public class StructFieldAccessEmitter {

    private final TempManager temps;
    private final TypeMapper typeMapper = new TypeMapper();
    private static final boolean DEBUG = false;

    public StructFieldAccessEmitter(TempManager temps) {
        this.temps = temps;
    }

    private void debug(String msg) {
        if (DEBUG) System.out.println("[StructFieldAccessEmitter] " + msg);
    }

    public LLVMValue emit(StructFieldAccessNode node, LLVisitorMain visitor) {

        StringBuilder llvm = new StringBuilder();
        debug("STRUCT FIELD ACCESS START: " + node.getFieldName());

        // ===== Obter struct como LLVMValue =====
        LLVMValue structVal = node.getStructInstance().accept(visitor);
        llvm.append(structVal.getCode());

        LLVMTYPES structLLVMType = structVal.getType();
        String structNameTmp = structVal.getName();

        // ===== Resolver owner e struct definition =====
        Type structSemanticType = node.getStructInstance().getType();
        if (structSemanticType == null)
            structSemanticType = node.getType();

        String ownerType = resolveOwnerType(node.getStructInstance(), structSemanticType, visitor);
        if (ownerType == null)
            throw new RuntimeException("Não foi possível resolver struct dona do campo: " + node.getFieldName());

        StructNode structDef = visitor.getStructNode(ownerType);
        if (structDef == null)
            throw new RuntimeException("Tipo não é struct: " + ownerType);

        // ===== Localizar campo =====
        int fieldIndex = -1;
        VariableDeclarationNode fieldDecl = null;
        List<VariableDeclarationNode> fields = structDef.getFields();

        for (int i = 0; i < fields.size(); i++) {
            if (fields.get(i).getName().equals(node.getFieldName())) {
                fieldIndex = i;
                fieldDecl = fields.get(i);
                break;
            }
        }

        if (fieldIndex == -1)
            throw new RuntimeException("Campo não encontrado: " + node.getFieldName());

        LLVMTYPES fieldLLVMType = TypeMapper.from(fieldDecl.getType());

        // ===== GEP para o campo =====
        String fieldPtr = temps.newTemp();
        llvm.append("  ").append(fieldPtr)
                .append(" = getelementptr inbounds ")
                .append(structLLVMType).append(", ")
                .append(structLLVMType).append("* ").append(structNameTmp)
                .append(", i32 0, i32 ").append(fieldIndex)
                .append("\n");

        // ===== Assignment ou Load =====
        if (node.getValue() != null) {
            debug("FIELD ASSIGNMENT detected");
            LLVMValue rhsVal = node.getValue().accept(visitor);
            llvm.append(rhsVal.getCode());

            String storeVal = rhsVal.getName();
            LLVMTYPES rhsType = rhsVal.getType();

            if (!rhsType.equals(fieldLLVMType)) {
                String castTmp = temps.newTemp();
                llvm.append("  ").append(castTmp)
                        .append(" = bitcast ").append(rhsType)
                        .append(" ").append(storeVal)
                        .append(" to ").append(fieldLLVMType).append("\n");
                storeVal = castTmp;
            }

            llvm.append("  store ").append(fieldLLVMType)
                    .append(" ").append(storeVal)
                    .append(", ").append(fieldLLVMType)
                    .append("* ").append(fieldPtr).append("\n");

            String reloadTmp = temps.newTemp();
            llvm.append("  ").append(reloadTmp)
                    .append(" = load ").append(fieldLLVMType)
                    .append(", ").append(fieldLLVMType)
                    .append("* ").append(fieldPtr).append("\n");

            return new LLVMValue(fieldLLVMType, reloadTmp, llvm.toString());

        } else {
            debug("FIELD LOAD detected");
            String loadedTmp = temps.newTemp();
            llvm.append("  ").append(loadedTmp)
                    .append(" = load ").append(fieldLLVMType)
                    .append(", ").append(fieldLLVMType)
                    .append("* ").append(fieldPtr).append("\n");

            return new LLVMValue(fieldLLVMType, loadedTmp, llvm.toString());
        }
    }

    private String resolveOwnerType(ASTNode instance, Type fallbackType, LLVisitorMain visitor) {
        if (instance instanceof VariableNode var) {
            TypeInfos info = visitor.getVarType(var.getName());
            if (info != null) return normalizeOwnerName(info.getType());
        }
        if (instance instanceof StructFieldAccessNode fieldAccess) {
            return normalizeOwnerName(fieldAccess.getType());
        }
        if (instance instanceof StructInstanceNode inst) return inst.getName();
        if (instance instanceof ListGetNode get) return normalizeOwnerName(get.getElementType());
        return normalizeOwnerName(fallbackType);
    }

    private String normalizeOwnerName(Type type) {
        if (type == null) return null;
        if (type instanceof StructType st) return st.name();
        if (type instanceof ListType list && list.elementType() instanceof StructType st) return st.name();
        return null;
    }
}