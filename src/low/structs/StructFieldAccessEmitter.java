package low.structs;

import ast.ASTNode;
import ast.lists.ListGetNode;
import ast.structs.StructFieldAccessNode;
import ast.structs.StructInstaceNode;
import ast.structs.StructNode;
import ast.variables.VariableDeclarationNode;
import ast.variables.VariableNode;
import low.TempManager;
import low.functions.TypeMapper;
import low.module.LLVisitorMain;

import java.util.List;


public class StructFieldAccessEmitter {
    private final TempManager temps;

    public StructFieldAccessEmitter(TempManager temps) {
        this.temps = temps;
    }

    public String emit(StructFieldAccessNode node, LLVisitorMain visitor) {
        StringBuilder llvm = new StringBuilder();

        String structCode = node.getStructInstance().accept(visitor);
        llvm.append(structCode);

        String structVal = extractTemp(structCode);
        String structLLVMType = extractType(structCode).trim();

        // Se veio ** (ponteiro para ponteiro), dar um load para virar %Pessoa*
        if (structLLVMType.endsWith("**")) {
            String base = structLLVMType.substring(0, structLLVMType.length()-1); // remove um '*'
            String tmp = temps.newTemp();
            llvm.append("  ").append(tmp).append(" = load ")
                    .append(base).append(", ").append(base).append("* ").append(structVal).append("\n");
            llvm.append(";;VAL:").append(tmp).append(";;TYPE:").append(base).append("\n");
            structVal = tmp;
            structLLVMType = base; // agora é %Pessoa*
        }

        //Descobrir dona e definição
        String ownerType = resolveOwnerType(node.getStructInstance(), structLLVMType, visitor);
        if (ownerType == null) {
            throw new RuntimeException("Não foi possível resolver struct dona de " + node.getFieldName() +
                    " (LLVMType=" + structLLVMType + ")");
        }
        StructNode def = visitor.getStructNode(ownerType);
        if (def == null) {
            // Nao faaa load “generico” aqui acesso de struct deve ir via GEP.
            throw new RuntimeException("Acesso de campo em algo que não é struct: " + structLLVMType);
        }

        int fieldIndex = -1;
        VariableDeclarationNode fieldDecl = null;
        List<VariableDeclarationNode> fields = def.getFields();
        for (int i = 0; i < fields.size(); i++) {
            if (fields.get(i).getName().equals(node.getFieldName())) {
                fieldIndex = i; fieldDecl = fields.get(i); break;
            }
        }
        if (fieldIndex == -1) throw new RuntimeException("Campo não encontrado: " + node.getFieldName());

        // 5) GEP para obter ponteiro para o campo
        if (structLLVMType.equals("i8*")) {
            String realTy = "%" + ownerType + "*";
            String casted = temps.newTemp();
            llvm.append("  ").append(casted)
                    .append(" = bitcast i8* ").append(structVal)
                    .append(" to ").append(realTy).append("\n");
            llvm.append(";;VAL:").append(casted).append(";;TYPE:").append(realTy).append("\n");
            structVal = casted;
            structLLVMType = realTy;
        }


        String fieldPtr = temps.newTemp();
        llvm.append("  ").append(fieldPtr).append(" = getelementptr inbounds ")
                .append(structLLVMType.replace("*",""))
                .append(", ").append(structLLVMType).append(" ").append(structVal)
                .append(", i32 0, i32 ").append(fieldIndex).append("\n");

        String fieldTy = new TypeMapper().toLLVM(fieldDecl.getType());

        if (node.getValue() != null) {

            String valCode = node.getValue().accept(visitor);
            llvm.append(valCode);
            String val = extractTemp(valCode);
            llvm.append("  store ").append(fieldTy).append(" ").append(val)
                    .append(", ").append(fieldTy).append("* ").append(fieldPtr).append("\n");
            llvm.append(";;VAL:").append(val).append(";;TYPE:").append(fieldTy).append("\n");
        } else {
            String tmp = temps.newTemp();
            llvm.append("  ").append(tmp).append(" = load ")
                    .append(fieldTy).append(", ").append(fieldTy).append("* ").append(fieldPtr).append("\n");
            llvm.append(";;VAL:").append(tmp).append(";;TYPE:").append(fieldTy).append("\n");
        }

        return llvm.toString();
    }

    private String normalizeOwnerName(String t) {
        if (t == null) return null;
        t = t.trim();

        String u = unwrapStructName(t);

        while (u.endsWith("*")) u = u.substring(0, u.length() - 1).trim();

        if (u.startsWith("%")) u = u.substring(1).trim();
        if (u.startsWith("Struct.")) u = u.substring("Struct.".length()).trim();
        if (u.startsWith("Struct ")) u = u.substring("Struct ".length()).trim();

        return u;
    }

    private String resolveOwnerType(ASTNode instance, String structTypeLLVM, LLVisitorMain visitor) {

        if (instance instanceof VariableNode varNode) {
            String t = visitor.getVarType(varNode.getName());
            String name = normalizeOwnerName(t);
            if (name != null) return name;
        }

        if (instance instanceof StructInstaceNode inst) {
            return inst.getName();
        }

        if (instance instanceof ListGetNode getNode) {
            ASTNode listExpr = getNode.getListName();
            if (listExpr instanceof VariableNode lv) {
                String elem = visitor.getListElementType(lv.getName());
                String name = normalizeOwnerName(elem);
                if (name != null) return name;
            }
        }

        String fallback = normalizeOwnerName(structTypeLLVM);
        if (fallback != null) return fallback;

        return null;
    }

    private String unwrapStructName(String type) {
        if (type == null) return null;
        type = type.trim();
        if (type.startsWith("Struct<") && type.endsWith(">")) {
            return type.substring(7, type.length() - 1).trim();
        }
        if (type.startsWith("Struct ")) {
            return type.substring("Struct ".length()).trim();
        }
        return type;
    }

    private String extractTemp(String code) {
        int lastValIdx = code.lastIndexOf(";;VAL:");
        int typeIdx = code.indexOf(";;TYPE:", lastValIdx);
        return code.substring(lastValIdx + 6, typeIdx).trim();
    }

    private String extractType(String code) {
        int lastTypeIdx = code.lastIndexOf(";;TYPE:");
        return code.substring(lastTypeIdx + 7).trim();
    }
}