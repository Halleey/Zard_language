package low.structs;

import ast.ASTNode;
import ast.structs.StructInstaceNode;
import ast.structs.StructNode;
import ast.variables.VariableDeclarationNode;
import low.TempManager;
import low.functions.TypeMapper;
import low.main.GlobalStringManager;
import low.module.LLVisitorMain;

import java.util.List;
import java.util.Map;

public class StructInstanceEmitter {
    private final TempManager tempManager;
    private final GlobalStringManager stringManager;

    public StructInstanceEmitter(TempManager tempManager, GlobalStringManager stringManager) {
        this.tempManager = tempManager;
        this.stringManager = stringManager;
    }

    public String emit(StructInstaceNode node, LLVisitorMain visitor) {

        StringBuilder llvm = new StringBuilder();

        String baseStructName = node.getName();
        String concreteType = node.getConcreteType();

        TypeMapper mapper = new TypeMapper();
        String structLLVMType =
                (concreteType != null && !concreteType.isEmpty())
                        ? mapper.toLLVM(concreteType)
                        : mapper.toLLVM("Struct<" + baseStructName + ">");

        // remover * extra
        if (structLLVMType.endsWith("*")) {
            structLLVMType = structLLVMType.substring(0, structLLVMType.length() - 1);
        }

        // ==== RESOLVER elemType ====
        String elemType = null;

        if (concreteType != null && concreteType.startsWith("Struct<")) {
            int lt1 = concreteType.indexOf('<');
            int lt2 = concreteType.indexOf('<', lt1 + 1);
            int gt = concreteType.indexOf('>', lt2 + 1);
            if (lt2 != -1 && gt != -1 && lt2 + 1 < gt) {
                elemType = concreteType.substring(lt2 + 1, gt).trim();
            }
        }

        // fallback
        if (elemType == null) {
            String shortName = structLLVMType.startsWith("%")
                    ? structLLVMType.substring(1)
                    : structLLVMType;

            String prefix = baseStructName + "_";
            if (shortName.startsWith(prefix)) {
                elemType = shortName.substring(prefix.length());
            }
        }

        // ==== RESOLVER STRUCTNODE ====
        StructNode def;
        if (elemType != null) {
            StructNode base = visitor.getStructNode(baseStructName);
            def = visitor.getOrCreateSpecializedStruct(base, elemType);
        } else {
            def = visitor.getStructNode(baseStructName);
        }

        if (def == null) {
            throw new RuntimeException("Struct não encontrada: " + baseStructName);
        }

        // ==== CALCULAR TAMANHO ====
        int structSize = def.getLLVMSizeBytes();

        // ==== malloc ====
        String mallocTmp = tempManager.newTemp();
        llvm.append("  ").append(mallocTmp)
                .append(" = call i8* @malloc(i64 ").append(structSize).append(")\n");

        String structPtr = tempManager.newTemp();
        llvm.append("  ").append(structPtr)
                .append(" = bitcast i8* ").append(mallocTmp)
                .append(" to ").append(structLLVMType).append("*\n");

        // ==== inicializar campos ====

        List<VariableDeclarationNode> fields = def.getFields();
        List<ASTNode> posValues = node.getPositionalValues();
        Map<String, ASTNode> namedValues = node.getNamedValues();

        int providedPos = (posValues == null ? 0 : posValues.size());

        for (int i = 0; i < fields.size(); i++) {

            VariableDeclarationNode field = fields.get(i);
            String fname = field.getName();
            String fieldType = field.getType();

            String effectiveFieldType = fieldType;
            if (elemType != null && fieldType.startsWith("List<") && fieldType.contains("?")) {
                effectiveFieldType = "List<" + elemType + ">";
            }

            ASTNode providedValue = null;

            if (namedValues.containsKey(fname)) {
                providedValue = namedValues.get(fname);
            } else if (i < providedPos) {
                providedValue = posValues.get(i);
            }

            String fieldLLVMType = mapFieldTypeForStruct(effectiveFieldType);

            String valueTemp;
            String before = "";

            if (providedValue != null) {
                before = providedValue.accept(visitor);
                valueTemp = extractTemp(before);
            } else {
                valueTemp = emitDefaultValue(effectiveFieldType, fieldLLVMType, llvm);
            }

            llvm.append(before);

            String fieldPtr = tempManager.newTemp();
            llvm.append("  ").append(fieldPtr)
                    .append(" = getelementptr inbounds ")
                    .append(structLLVMType).append(", ").append(structLLVMType)
                    .append("* ").append(structPtr).append(", i32 0, i32 ").append(i).append("\n");

            llvm.append("  store ").append(fieldLLVMType).append(" ").append(valueTemp)
                    .append(", ").append(fieldLLVMType).append("* ").append(fieldPtr).append("\n");
        }

        llvm.append(";;VAL:").append(structPtr).append(";;TYPE:").append(structLLVMType).append("*\n");
        return llvm.toString();
    }


    private String mapFieldTypeForStruct(String type) {
        if (type.startsWith("List<")) {
            String inner = type.substring(5, type.length() - 1).trim();
            return switch (inner) {
                case "int" -> "%struct.ArrayListInt*";
                case "double" -> "%struct.ArrayListDouble*";
                case "boolean" -> "%struct.ArrayListBool*";
                case "string" -> "%ArrayList*";
                default -> "%ArrayList*";
            };
        }
        if (type.startsWith("Struct ")) {
            String inner = type.substring("Struct ".length()).trim();
            return "%" + inner + "*";
        }
        if (type.startsWith("Struct<") && type.endsWith(">")) {
            String inner = type.substring(7, type.length() - 1).trim();
            return "%" + inner + "*";
        }
        return new TypeMapper().toLLVM(type);
    }

    private String emitDefaultValue(String type, String fieldLLVMType, StringBuilder llvm) {
        switch (type) {
            case "int": return "0";
            case "double":
            case "float": return "0.0";
            case "boolean": return "0";
            case "string": {
                String emptyLabel = stringManager.getGlobalName("");
                String tmp = tempManager.newTemp();
                llvm.append("  ").append(tmp)
                        .append(" = call %String* @createString(i8* ").append(emptyLabel).append(")\n");
                return tmp;
            }
            default:
                if (type.startsWith("List<")) {
                    String inner = type.substring(5, type.length() - 1).trim();
                    String tmp = tempManager.newTemp();
                    switch (inner) {
                        case "int" -> {
                            llvm.append("  ").append(tmp)
                                    .append(" = call %struct.ArrayListInt* @arraylist_create_int(i64 10)\n");
                            return tmp;
                        }
                        case "double" -> {
                            llvm.append("  ").append(tmp)
                                    .append(" = call %struct.ArrayListDouble* @arraylist_create_double(i64 10)\n");
                            return tmp;
                        }
                        case "boolean" -> {
                            llvm.append("  ").append(tmp)
                                    .append(" = call %struct.ArrayListBool* @arraylist_create_bool(i64 10)\n");
                            return tmp;
                        }
                        default -> {
                            llvm.append("  ").append(tmp)
                                    .append(" = call i8* @arraylist_create(i64 10)\n");
                            String casted = tempManager.newTemp();
                            llvm.append("  ").append(casted)
                                    .append(" = bitcast i8* ").append(tmp).append(" to %ArrayList*\n");
                            return casted;
                        }
                    }
                } else if (type.startsWith("Struct")) {
                    return "null";
                }
        }
        return "zeroinitializer";
    }

    private String extractTemp(String code) {
        int lastValIdx = code.lastIndexOf(";;VAL:");
        if (lastValIdx == -1) {
            throw new RuntimeException("Cannot find ;;VAL: in: " + code);
        }
        int typeIdx = code.indexOf(";;TYPE:", lastValIdx);
        return code.substring(lastValIdx + 6, typeIdx).trim();
    }
}
