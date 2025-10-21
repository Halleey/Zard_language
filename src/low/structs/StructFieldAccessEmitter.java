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

        System.out.println("[DEBUG] Entrando em StructFieldAccessEmitter.emit para campo: " + node.getFieldName());

        String structCode = node.getStructInstance().accept(visitor);
        llvm.append(structCode);

        String structVal = extractTemp(structCode);
        String structLLVMType = extractType(structCode).trim();

        System.out.println("[DEBUG] structCode gerado:\n" + structCode);
        System.out.println("[DEBUG] structVal=" + structVal + " | structLLVMType=" + structLLVMType);

        // se veio **, faz um load para obter %Pessoa*
        if (structLLVMType.endsWith("**")) {
            System.out.println("[DEBUG] structLLVMType termina com **, aplicando load...");
            String base = structLLVMType.substring(0, structLLVMType.length() - 1); // tira 1 '*'
            String tmp = temps.newTemp();
            llvm.append("  ").append(tmp).append(" = load ")
                    .append(base).append(", ").append(base).append("* ").append(structVal).append("\n");
            llvm.append(";;VAL:").append(tmp).append(";;TYPE:").append(base).append("\n");
            structVal = tmp;
            structLLVMType = base;
            System.out.println("[DEBUG] Após load: structVal=" + structVal + " | structLLVMType=" + structLLVMType);
        }

        //  dona (nome da struct) e definição
        String ownerType = resolveOwnerType(node.getStructInstance(), structLLVMType, visitor);
        System.out.println("[DEBUG] ownerType resolvido: " + ownerType);

        if (ownerType == null) {
            throw new RuntimeException("Não foi possível resolver struct dona de " + node.getFieldName() +
                    " (LLVMType=" + structLLVMType + ")");
        }
        StructNode def = visitor.getStructNode(ownerType);
        if (def == null) {
            System.out.println("[DEBUG] getStructNode(" + ownerType + ") retornou null!");
            throw new RuntimeException("Acesso de campo em algo que não é struct: " + structLLVMType);
        }

        System.out.println("[DEBUG] StructNode encontrado: " + def.getName() + " com " + def.getFields().size() + " campos");

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
        System.out.println("[DEBUG] fieldIndex=" + fieldIndex);

        if (fieldIndex == -1) {
            throw new RuntimeException("Campo não encontrado: " + node.getFieldName());
        }

        //gep até o ponteiro do campo
        if (structLLVMType.equals("i8*")) {
            System.out.println("[DEBUG] structLLVMType é i8*, aplicando bitcast para %" + ownerType + "*");
            String realTy = "%" + ownerType + "*";
            String casted = temps.newTemp();
            llvm.append("  ").append(casted)
                    .append(" = bitcast i8* ").append(structVal)
                    .append(" to ").append(realTy).append("\n");
            llvm.append(";;VAL:").append(casted).append(";;TYPE:").append(realTy).append("\n");
            structVal = casted;
            structLLVMType = realTy;
        }

        System.out.println("[DEBUG] Preparando GEP: structLLVMType=" + structLLVMType + " | fieldIndex=" + fieldIndex);

        String fieldPtr = temps.newTemp();
        String structTyNoPtr = structLLVMType.replace("*", "");
        llvm.append("  ").append(fieldPtr).append(" = getelementptr inbounds ")
                .append(structTyNoPtr).append(", ").append(structLLVMType).append(" ").append(structVal)
                .append(", i32 0, i32 ").append(fieldIndex).append("\n");

        final String fieldLangType = fieldDecl.getType();
        final String fieldLLType = mapFieldTypeForStruct(fieldLangType); // tipo LLVM do CAMPO (não do ponteiro ao campo)

        System.out.println("[DEBUG] Campo " + node.getFieldName() + " tipoLang=" + fieldLangType + " | tipoLLVM=" + fieldLLType);

        boolean isWrite = (node.getValue() != null);
        if (isWrite) {
            System.out.println("[DEBUG] Emitindo atribuição para campo " + node.getFieldName() + ")");
            // Avalia RHS
            String rhsCode = node.getValue().accept(visitor);
            llvm.append(rhsCode);
            String rhsVal = extractTemp(rhsCode);
            String rhsTy = extractType(rhsCode).trim();

            // Ajusta tipo do valor para o tipo do campo
            String storeVal = rhsVal;
            String storeTy = rhsTy;

            if (!storeTy.equals(fieldLLType)) {
                // Casos comuns de conversão
                if ("i8*".equals(storeTy) && fieldLLType.startsWith("%")) {
                    // i8* -> %Algo*
                    String cast = temps.newTemp();
                    llvm.append("  ").append(cast).append(" = bitcast i8* ").append(storeVal)
                            .append(" to ").append(fieldLLType).append("\n");
                    storeVal = cast;
                    storeTy = fieldLLType;
                } else if (storeTy.startsWith("%") && "i8*".equals(fieldLLType)) {

                    String cast = temps.newTemp();
                    llvm.append("  ").append(cast).append(" = bitcast ").append(storeTy).append(" ").append(storeVal)
                            .append(" to i8*\n");
                    storeVal = cast;

                } else if ("i32".equals(storeTy) && "double".equals(fieldLLType)) {
                    String conv = temps.newTemp();
                    llvm.append("  ").append(conv).append(" = sitofp i32 ").append(storeVal).append(" to double\n");
                    storeVal = conv;
                    storeTy = "double";
                } else if ("double".equals(storeTy) && "i32".equals(fieldLLType)) {
                    String conv = temps.newTemp();
                    llvm.append("  ").append(conv).append(" = fptosi double ").append(storeVal).append(" to i32\n");
                    storeVal = conv;

                } else if (!storeTy.equals(fieldLLType)) {
                    // fallback seguro para ponteiros compatíveis
                    if ((storeTy.endsWith("*") && fieldLLType.endsWith("*"))) {
                        String cast = temps.newTemp();
                        llvm.append("  ").append(cast).append(" = bitcast ").append(storeTy).append(" ").append(storeVal)
                                .append(" to ").append(fieldLLType).append("\n");
                        storeVal = cast;
                    } else {
                        throw new RuntimeException("Tipo incompatível para store em campo " + node.getFieldName() +
                                ": RHS=" + storeTy + " -> Field=" + fieldLLType);
                    }
                }
            }

            // fieldPtr é ponteiro para o campo => tipo é fieldLLType*
            llvm.append("  store ").append(fieldLLType).append(" ").append(storeVal)
                    .append(", ").append(fieldLLType).append("* ").append(fieldPtr).append("\n");

            // Para encadeamento após uma atribuição (ex. a.b = c e depois usa a.b), devolvemos o valor armazenado
            String retAlias = temps.newTemp();
            llvm.append("  ").append(retAlias).append(" = load ").append(fieldLLType)
                    .append(", ").append(fieldLLType).append("* ").append(fieldPtr).append("\n");
            llvm.append(";;VAL:").append(retAlias).append(";;TYPE:").append(fieldLLType).append("\n");

        } else {
            System.out.println("[DEBUG] Emitindo leitura para campo " + node.getFieldName() + ")");
            // fieldPtr é *para o campo*; precisamos carregar o valor do campo para poder encadear
            String loaded = temps.newTemp();
            llvm.append("  ").append(loaded).append(" = load ").append(fieldLLType)
                    .append(", ").append(fieldLLType).append("* ").append(fieldPtr).append("\n");
            llvm.append(";;VAL:").append(loaded).append(";;TYPE:").append(fieldLLType).append("\n");
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
                case "string" -> "%ArrayList*";
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

    private String listLLVMPtrType(String inner) {
        return switch (inner) {
            case "int" -> "%struct.ArrayListInt*";
            case "double" -> "%struct.ArrayListDouble*";
            case "boolean" -> "%struct.ArrayListBool*";
            default -> "%ArrayList*"; // string/struct/ptr
        };
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

    private String safeSuffix(String s) {
        // gera sufixo simples para labels
        String k = s.replace("%", "").replace(".", "_");
        if (k.isEmpty()) k = "x";
        return k;
    }
}
