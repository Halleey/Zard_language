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

        String structCode = node.getStructInstance().accept(visitor);
        llvm.append(structCode);

        String structVal = extractTemp(structCode);
        String structLLVMType = extractType(structCode).trim();

        System.out.println("[StructFieldAccess] structLLVMType = " + structLLVMType);

        if (structLLVMType.endsWith("**")) {
            String base = structLLVMType.substring(0, structLLVMType.length() - 1);
            String tmp = temps.newTemp();
            llvm.append("  ").append(tmp).append(" = load ")
                    .append(base).append(", ").append(base).append("* ").append(structVal).append("\n");
            llvm.append(";;VAL:").append(tmp).append(";;TYPE:").append(base).append("\n");
            structVal = tmp;
            structLLVMType = base;
        }

        String ownerType = resolveOwnerType(node.getStructInstance(), structLLVMType, visitor);
        if (ownerType == null) {
            throw new RuntimeException("Owner não resolvido para campo " + node.getFieldName());
        }

        System.out.println("[StructFieldAccess] ownerType = " + ownerType);

        String ownerBase = ownerType;
        int genIdx = ownerBase.indexOf('<');
        if (genIdx != -1) ownerBase = ownerBase.substring(0, genIdx).trim();

        StructNode def = visitor.getStructNode(ownerBase);
        if (def == null) {
            throw new RuntimeException("Struct não encontrada: " + ownerBase);
        }

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

        String ownerMangled = mangleGenericType(ownerType);
        String ownerLLNoPtr = "%" + ownerMangled;
        String ownerLLPtr = ownerLLNoPtr + "*";

        System.out.println("[StructFieldAccess] ownerLL = " + ownerLLPtr);

        if (!structLLVMType.equals(ownerLLPtr)) {
            String casted = temps.newTemp();
            llvm.append("  ").append(casted)
                    .append(" = bitcast ").append(structLLVMType).append(" ").append(structVal)
                    .append(" to ").append(ownerLLPtr).append("\n");
            llvm.append(";;VAL:").append(casted).append(";;TYPE:").append(ownerLLPtr).append("\n");
            structVal = casted;
        }

        String fieldPtr = temps.newTemp();
        llvm.append("  ").append(fieldPtr).append(" = getelementptr inbounds ")
                .append(ownerLLNoPtr).append(", ").append(ownerLLPtr).append(" ").append(structVal)
                .append(", i32 0, i32 ").append(fieldIndex).append("\n");

        String fieldLangType = fieldDecl.getType();
        String fieldLLType = mapFieldTypeForStruct(fieldLangType);

        boolean isWrite = node.getValue() != null;

        if (isWrite) {
            String rhsCode = node.getValue().accept(visitor);
            llvm.append(rhsCode);

            String rhsVal = extractTemp(rhsCode);
            String rhsTy = extractType(rhsCode).trim();

            if (!rhsTy.equals(fieldLLType)) {
                if (rhsTy.endsWith("*") && fieldLLType.endsWith("*")) {
                    String cast = temps.newTemp();
                    llvm.append("  ").append(cast).append(" = bitcast ")
                            .append(rhsTy).append(" ").append(rhsVal)
                            .append(" to ").append(fieldLLType).append("\n");
                    rhsVal = cast;
                } else {
                    throw new RuntimeException("Tipo inválido: " + rhsTy + " -> " + fieldLLType);
                }
            }

            llvm.append("  store ").append(fieldLLType).append(" ").append(rhsVal)
                    .append(", ").append(fieldLLType).append("* ").append(fieldPtr).append("\n");

            String loaded = temps.newTemp();
            llvm.append("  ").append(loaded).append(" = load ")
                    .append(fieldLLType).append(", ").append(fieldLLType).append("* ").append(fieldPtr).append("\n");
            llvm.append(";;VAL:").append(loaded).append(";;TYPE:").append(fieldLLType).append("\n");
        } else {
            String loaded = temps.newTemp();
            llvm.append("  ").append(loaded).append(" = load ")
                    .append(fieldLLType).append(", ").append(fieldLLType).append("* ").append(fieldPtr).append("\n");
            llvm.append(";;VAL:").append(loaded).append(";;TYPE:").append(fieldLLType).append("\n");
        }

        return llvm.toString();
    }
    private String mangleGenericType(String t) {
        if (t == null) return "";

        t = t.trim();

        // remove ponteiros
        while (t.endsWith("*")) {
            t = t.substring(0, t.length() - 1).trim();
        }

        // remove %
        if (t.startsWith("%")) {
            t = t.substring(1).trim();
        }

        // remove Struct<...>
        if (t.startsWith("Struct<") && t.endsWith(">")) {
            t = t.substring(7, t.length() - 1).trim();
        }

        // remove prefixos estranhos
        if (t.startsWith("Struct ")) {
            t = t.substring("Struct ".length()).trim();
        }

        // mangling genérico
        t = t.replace(" ", "")
                .replace("<", "_")
                .replace(">", "")
                .replace(",", "_");

        while (t.contains("__")) {
            t = t.replace("__", "_");
        }

        return t;
    }

    private String mapFieldTypeForStruct(String langType) {
        if (langType == null) return "void";
        langType = langType.trim();

        if (langType.startsWith("List<")) {
            String inner = langType.substring(5, langType.length() - 1).trim();
            return switch (inner) {
                case "int" -> "%struct.ArrayListInt*";
                case "double" -> "%struct.ArrayListDouble*";
                case "boolean", "bool" -> "%struct.ArrayListBool*";
                case "string", "String" -> "%ArrayList*";
                default -> "%ArrayList*";
            };
        }

        if (langType.startsWith("Struct<") && langType.endsWith(">")) {
            return "%" + langType.substring(7, langType.length() - 1).trim() + "*";
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
        if (u.contains(".")) u = u.substring(u.lastIndexOf('.') + 1);
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
        int end = code.indexOf('\n', lastTypeIdx);
        if (end == -1) end = code.length();
        return code.substring(lastTypeIdx + 7, end).trim();
    }
}
