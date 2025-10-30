package low.structs;

import ast.ASTNode;
import ast.inputs.InputNode;
import ast.lists.ListGetNode;
import ast.structs.StructFieldAccessNode;
import ast.structs.StructInstaceNode;
import ast.structs.StructNode;
import ast.variables.VariableDeclarationNode;
import ast.variables.VariableNode;
import low.TempManager;
import low.functions.TypeMapper;
import low.inputs.InputEmitter;
import low.main.TypeInfos;
import low.module.LLVisitorMain;

import java.util.List;
public class StructFieldAccessEmitter {
    private final TempManager temps;

    public StructFieldAccessEmitter(TempManager temps) {
        this.temps = temps;
    }

    public String emit(StructFieldAccessNode node, LLVisitorMain visitor) {
        StringBuilder llvm = new StringBuilder();

        // Gera código da instância da struct
        String structCode = node.getStructInstance().accept(visitor);
        llvm.append(structCode);

        String structVal = extractTemp(structCode);
        String structLLVMType = extractType(structCode).trim();

        // Corrige ponteiro duplo (**)
        if (structLLVMType.endsWith("**")) {
            String base = structLLVMType.substring(0, structLLVMType.length() - 1);
            String tmp = temps.newTemp();
            llvm.append("  ").append(tmp).append(" = load ")
                    .append(base).append(", ").append(base).append("* ").append(structVal).append("\n");
            llvm.append(";;VAL:").append(tmp).append(";;TYPE:").append(base).append("\n");
            structVal = tmp;
            structLLVMType = base;
        }

        // Resolve o tipo lógico da struct
        String ownerType = resolveOwnerType(node.getStructInstance(), structLLVMType, visitor);
        if (ownerType == null) {
            throw new RuntimeException("Não foi possível resolver struct dona de " + node.getFieldName() +
                    " (LLVMType=" + structLLVMType + ")");
        }
        StructNode def = visitor.getStructNode(ownerType);
        if (def == null) {
            throw new RuntimeException("Acesso de campo em algo que não é struct: " + structLLVMType);
        }

        // Encontra o campo
        int fieldIndex = -1;
        VariableDeclarationNode fieldDecl = null;
        List<VariableDeclarationNode> fields = def.getFields();
        for (int i = 0; i < fields.size(); i++) {
            if (fields.get(i).getName().equals(node.getFieldName())) {
                fieldIndex = i;
                fieldDecl = fields.get(i);
                break;
            }
        }
        if (fieldIndex == -1) {
            throw new RuntimeException("Campo não encontrado: " + node.getFieldName());
        }

        // Se structVal for i8*, faz bitcast para o tipo real
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

        // GEP até o ponteiro do campo
        String fieldPtr = temps.newTemp();
        String structTyNoPtr = structLLVMType.replace("*", "");
        llvm.append("  ").append(fieldPtr).append(" = getelementptr inbounds ")
                .append(structTyNoPtr).append(", ").append(structLLVMType).append(" ").append(structVal)
                .append(", i32 0, i32 ").append(fieldIndex).append("\n");

        // Descobre tipo do campo (semântico + LLVM)
        final String fieldLangType = fieldDecl.getType();
        final String fieldLLType = mapFieldTypeForStruct(fieldLangType);
        TypeInfos fieldInfo = new TypeInfos(fieldLangType, fieldLLType,
                fieldLangType.startsWith("List<") ? fieldLangType.substring(5, fieldLangType.length() - 1) : null);

        boolean isWrite = (node.getValue() != null);
        if (isWrite) {
            // RHS
            String rhsCode;
            if (node.getValue() instanceof InputNode in) {
                InputEmitter inEmitter = new InputEmitter(temps, visitor.getGlobalStrings());
                rhsCode = inEmitter.emit(in, fieldLLType);
            } else {
                rhsCode = node.getValue().accept(visitor);
            }
            llvm.append(rhsCode);

            String rhsVal = extractTemp(rhsCode);
            String rhsTy  = extractType(rhsCode).trim();

            String storeVal = rhsVal;

            // Casting se necessário
            if (!rhsTy.equals(fieldLLType)) {
                String cast = temps.newTemp();
                llvm.append("  ").append(cast).append(" = bitcast ")
                        .append(rhsTy).append(" ").append(rhsVal)
                        .append(" to ").append(fieldLLType).append("\n");
                storeVal = cast;
            }

            // Faz o store
            llvm.append("  store ").append(fieldLLType).append(" ").append(storeVal)
                    .append(", ").append(fieldLLType).append("* ").append(fieldPtr).append("\n");

            // Recarrega para atualizar markers
            String retAlias = temps.newTemp();
            llvm.append("  ").append(retAlias).append(" = load ")
                    .append(fieldLLType).append(", ").append(fieldLLType).append("* ").append(fieldPtr).append("\n");
            llvm.append(";;VAL:").append(retAlias).append(";;TYPE:").append(fieldLLType).append("\n");

            visitor.putVarType(node.getFieldName(), fieldInfo);

        }
        else {
            // Leitura
            String loaded = temps.newTemp();
            llvm.append("  ").append(loaded).append(" = load ")
                    .append(fieldLLType).append(", ").append(fieldLLType)
                    .append("* ").append(fieldPtr).append("\n");
            llvm.append(";;VAL:").append(loaded).append(";;TYPE:").append(fieldLLType).append("\n");

            visitor.putVarType(node.getFieldName(), fieldInfo);
        }

        return llvm.toString();
    }

    private String mapFieldTypeForStruct(String langType) {
        if (langType == null) return "void";
        langType = langType.trim();

        if (isListType(langType)) {
            String inner = getListInner(langType);
            return switch (inner) {
                case "int" -> "%struct.ArrayListInt*";
                case "double" -> "%struct.ArrayListDouble*";
                case "boolean" -> "%struct.ArrayListBool*";
                case "string" -> "%ArrayList*"; // lista genérica de String
                default -> "%ArrayList*";
            };
        }
        if (langType.startsWith("Struct ")) {
            String inner = langType.substring("Struct ".length()).trim();
            return "%" + inner + "*";
        }
        if (langType.startsWith("Struct<") && langType.endsWith(">")) {
            String inner = langType.substring(7, langType.length() - 1).trim();
            return "%" + inner + "*";
        }
        return new TypeMapper().toLLVM(langType);
    }

    private boolean isListType(String t) {
        return t != null && t.startsWith("List<") && t.endsWith(">");
    }

    private String getListInner(String t) {
        return t.substring(5, t.length() - 1).trim();
    }

    private String normalizeOwnerName(String t) {
        if (t == null) return null;
        t = t.trim();
        String u = unwrapStructName(t);
        while (u.endsWith("*")) u = u.substring(0, u.length() - 1).trim();
        if (u.startsWith("%")) u = u.substring(1).trim();
        if (u.startsWith("Struct.")) u = u.substring("Struct.".length()).trim();
        if (u.startsWith("Struct ")) u = u.substring("Struct ".length()).trim();
        if (u.contains(".")) {
            u = u.substring(u.lastIndexOf('.') + 1);
        }
        return u;
    }

    private String resolveOwnerType(ASTNode instance, String structTypeLLVM, LLVisitorMain visitor) {
        if (instance instanceof VariableNode varNode) {
            TypeInfos info = visitor.getVarType(varNode.getName());
            String t = info != null ? info.getLLVMType() : null;
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
        return normalizeOwnerName(structTypeLLVM);
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
        if (lastValIdx == -1 || typeIdx == -1) {
            throw new RuntimeException("extractTemp falhou. Código não contém ;;VAL/;;TYPE:\n" + code);
        }
        return code.substring(lastValIdx + 6, typeIdx).trim();
    }

    private String extractType(String code) {
        int lastTypeIdx = code.lastIndexOf(";;TYPE:");
        return code.substring(lastTypeIdx + 7).trim();
    }
}
