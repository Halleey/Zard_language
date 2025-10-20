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
        // se veio **, faz um load para obter %Pessoa*
        if (structLLVMType.endsWith("**")) {
            String base = structLLVMType.substring(0, structLLVMType.length() - 1); // tira 1 '*'
            String tmp = temps.newTemp();
            llvm.append("  ").append(tmp).append(" = load ")
                    .append(base).append(", ").append(base).append("* ").append(structVal).append("\n");
            llvm.append(";;VAL:").append(tmp).append(";;TYPE:").append(base).append("\n");
            structVal = tmp;
            structLLVMType = base;
        }

        //  dona (nome da struct) e definição
        String ownerType = resolveOwnerType(node.getStructInstance(), structLLVMType, visitor);
        if (ownerType == null) {
            throw new RuntimeException("Não foi possível resolver struct dona de " + node.getFieldName() +
                    " (LLVMType=" + structLLVMType + ")");
        }
        StructNode def = visitor.getStructNode(ownerType);
        if (def == null) {
            throw new RuntimeException("Acesso de campo em algo que não é struct: " + structLLVMType);
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

        //gep até o ponteiro do campo
        if (structLLVMType.equals("i8*")) {
            // dif se veio como i8*, faz bitcast para %Owner*
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
        String structTyNoPtr = structLLVMType.replace("*", "");
        llvm.append("  ").append(fieldPtr).append(" = getelementptr inbounds ")
                .append(structTyNoPtr).append(", ").append(structLLVMType).append(" ").append(structVal)
                .append(", i32 0, i32 ").append(fieldIndex).append("\n");

        final String fieldLangType = fieldDecl.getType();
        final String fieldLLType = mapFieldTypeForStruct(fieldLangType); // tipo LLVM correto do CAMPO

        if (node.getValue() != null) {
            String valCode = node.getValue().accept(visitor);
            llvm.append(valCode);
            String val = extractTemp(valCode);
            String valType = extractType(valCode).trim();

            // Se o campo é LISTA
            if (isListType(fieldLangType)) {
                String inner = getListInner(fieldLangType);


                if (isListLLVMType(valType)) {

                    if (inner.equals("int") && valType.equals("%struct.ArrayListInt*")) {
                        llvm.append("  store %struct.ArrayListInt* ").append(val)
                                .append(", %struct.ArrayListInt** ").append(fieldPtr).append("\n");
                        llvm.append(";;VAL:").append(val).append(";;TYPE:%struct.ArrayListInt*\n");
                        return llvm.toString();
                    } else if (inner.equals("double") && valType.equals("%struct.ArrayListDouble*")) {
                        llvm.append("  store %struct.ArrayListDouble* ").append(val)
                                .append(", %struct.ArrayListDouble** ").append(fieldPtr).append("\n");
                        llvm.append(";;VAL:").append(val).append(";;TYPE:%struct.ArrayListDouble*\n");
                        return llvm.toString();
                    } else if (inner.equals("boolean") && valType.equals("%struct.ArrayListBool*")) {
                        llvm.append("  store %struct.ArrayListBool* ").append(val)
                                .append(", %struct.ArrayListBool** ").append(fieldPtr).append("\n");
                        llvm.append(";;VAL:").append(val).append(";;TYPE:%struct.ArrayListBool*\n");
                        return llvm.toString();
                    } else {
                        if (valType.equals("%ArrayList*")) {
                            llvm.append("  store %ArrayList* ").append(val)
                                    .append(", %ArrayList** ").append(fieldPtr).append("\n");
                            llvm.append(";;VAL:").append(val).append(";;TYPE:%ArrayList*\n");
                            return llvm.toString();
                        } else if (valType.equals("i8*")) {
                            // bitcast i8* -> %ArrayList*
                            String bc = temps.newTemp();
                            llvm.append("  ").append(bc).append(" = bitcast i8* ").append(val).append(" to %ArrayList*\n");
                            llvm.append("  store %ArrayList* ").append(bc).append(", %ArrayList** ").append(fieldPtr).append("\n");
                            llvm.append(";;VAL:").append(bc).append(";;TYPE:%ArrayList*\n");
                            return llvm.toString();
                        }
                    }
                }

                ensureListAllocated(llvm, fieldPtr, inner);
                addToList(llvm, fieldPtr, inner, val, valType);
                String listNow = temps.newTemp();
                String listTy = listLLVMPtrType(inner);
                llvm.append("  ").append(listNow)
                        .append(" = load ").append(listTy).append(", ").append(listTy)
                        .append("* ").append(fieldPtr).append("\n");
                llvm.append(";;VAL:").append(listNow).append(";;TYPE:").append(listTy).append("\n");
                return llvm.toString();
            }

            llvm.append("  store ").append(fieldLLType).append(" ").append(val)
                    .append(", ").append(fieldLLType).append("* ").append(fieldPtr).append("\n");
            llvm.append(";;VAL:").append(val).append(";;TYPE:").append(fieldLLType).append("\n");
            return llvm.toString();
        }


        String tmp = temps.newTemp();
        llvm.append("  ").append(tmp).append(" = load ")
                .append(fieldLLType).append(", ").append(fieldLLType).append("* ").append(fieldPtr).append("\n");
        llvm.append(";;VAL:").append(tmp).append(";;TYPE:").append(fieldLLType).append("\n");
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

    private boolean isListLLVMType(String ty) {
        // reconhece todos os ponteiros de listas possíveis
        return "%struct.ArrayListInt*".equals(ty)
                || "%struct.ArrayListDouble*".equals(ty)
                || "%struct.ArrayListBool*".equals(ty)
                || "%ArrayList*".equals(ty)
                || "i8*".equals(ty); // criador genérico pode retornar i8* antes de bitcast
    }

    private String listLLVMPtrType(String inner) {
        return switch (inner) {
            case "int" -> "%struct.ArrayListInt*";
            case "double" -> "%struct.ArrayListDouble*";
            case "boolean" -> "%struct.ArrayListBool*";
            default -> "%ArrayList*"; // string/struct/ptr
        };
    }

    private void ensureListAllocated(StringBuilder llvm, String fieldPtr, String inner) {
        // carrega ptr da lista
        String listTy = listLLVMPtrType(inner);
        String cur = temps.newTemp();
        llvm.append("  ").append(cur)
                .append(" = load ").append(listTy).append(", ").append(listTy)
                .append("* ").append(fieldPtr).append("\n");

        String isNull = temps.newTemp();
        String thenLbl = "init_list_" + safeSuffix(cur);
        String endLbl = thenLbl + "_end";

        llvm.append("  ").append(isNull).append(" = icmp eq ").append(listTy).append(" ")
                .append(cur).append(", null\n");
        llvm.append("  br i1 ").append(isNull).append(", label %").append(thenLbl)
                .append(", label %").append(endLbl).append("\n");

        // init: cria lista correta
        llvm.append(thenLbl).append(":\n");
        String created = temps.newTemp();
        if (inner.equals("int")) {
            llvm.append("  ").append(created)
                    .append(" = call %struct.ArrayListInt* @arraylist_create_int(i64 10)\n");
            llvm.append("  store %struct.ArrayListInt* ").append(created)
                    .append(", %struct.ArrayListInt** ").append(fieldPtr).append("\n");
        } else if (inner.equals("double")) {
            llvm.append("  ").append(created)
                    .append(" = call %struct.ArrayListDouble* @arraylist_create_double(i64 10)\n");
            llvm.append("  store %struct.ArrayListDouble* ").append(created)
                    .append(", %struct.ArrayListDouble** ").append(fieldPtr).append("\n");
        } else if (inner.equals("boolean")) {
            llvm.append("  ").append(created)
                    .append(" = call %struct.ArrayListBool* @arraylist_create_bool(i64 10)\n");
            llvm.append("  store %struct.ArrayListBool* ").append(created)
                    .append(", %struct.ArrayListBool** ").append(fieldPtr).append("\n");
        } else {
            // genérico/ptr (string, structs, etc.) — criador retorna i8*
            String raw = temps.newTemp();
            llvm.append("  ").append(raw)
                    .append(" = call i8* @arraylist_create(i64 10)\n");
            String casted = temps.newTemp();
            llvm.append("  ").append(casted)
                    .append(" = bitcast i8* ").append(raw).append(" to %ArrayList*\n");
            llvm.append("  store %ArrayList* ").append(casted)
                    .append(", %ArrayList** ").append(fieldPtr).append("\n");
        }
        llvm.append("  br label %").append(endLbl).append("\n");
        llvm.append(endLbl).append(":\n");
    }

    private void addToList(StringBuilder llvm, String fieldPtr, String inner, String val, String valType) {
        if (inner.equals("int")) {
            String list = temps.newTemp();
            llvm.append("  ").append(list)
                    .append(" = load %struct.ArrayListInt*, %struct.ArrayListInt** ").append(fieldPtr).append("\n");
            // se o valor veio como i32, ótimo; senão, faça cast se necessário
            llvm.append("  call void @arraylist_add_int(%struct.ArrayListInt* ").append(list)
                    .append(", i32 ").append(val).append(")\n");
        } else if (inner.equals("double")) {
            String list = temps.newTemp();
            llvm.append("  ").append(list)
                    .append(" = load %struct.ArrayListDouble*, %struct.ArrayListDouble** ").append(fieldPtr).append("\n");
            // garanta que é double; se valType for i32, pode zext/sitofp, mas mantive simples:
            if (!"double".equals(valType)) {
                // cast para double se necessário
                String d = temps.newTemp();
                llvm.append("  ").append(d).append(" = sitofp i32 ").append(val).append(" to double\n");
                val = d;
            }
            llvm.append("  call void @arraylist_add_double(%struct.ArrayListDouble* ").append(list)
                    .append(", double ").append(val).append(")\n");
        } else if (inner.equals("boolean")) {
            String list = temps.newTemp();
            llvm.append("  ").append(list)
                    .append(" = load %struct.ArrayListBool*, %struct.ArrayListBool** ").append(fieldPtr).append("\n");
            // normalize para i1
            if (!"i1".equals(valType)) {
                // qualquer não-zero vira true se for i32, compare com 0
                String b = temps.newTemp();
                llvm.append("  ").append(b).append(" = icmp ne ").append(valType).append(" ")
                        .append(val).append(", 0\n");
                val = b;
            }
            llvm.append("  call void @arraylist_add_bool(%struct.ArrayListBool* ").append(list)
                    .append(", i1 ").append(val).append(")\n");
        } else {
            // genérico/ptr: string (%String*) e structs (%Tipo*) entram como i8*
            String list = temps.newTemp();
            llvm.append("  ").append(list)
                    .append(" = load %ArrayList*, %ArrayList** ").append(fieldPtr).append("\n");

            // Se valor não é i8*, fazemos bitcast
            if (!"i8*".equals(valType)) {
                String cast = temps.newTemp();
                llvm.append("  ").append(cast).append(" = bitcast ").append(valType)
                        .append(" ").append(val).append(" to i8*\n");
                val = cast;
            }
            llvm.append("  call void @arraylist_add_ptr(%ArrayList* ").append(list)
                    .append(", i8* ").append(val).append(")\n");
        }
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

    private String safeSuffix(String s) {
        // gera sufixo simples para labels
        String k = s.replace("%", "").replace(".", "_");
        if (k.isEmpty()) k = "x";
        return k;
    }
}
