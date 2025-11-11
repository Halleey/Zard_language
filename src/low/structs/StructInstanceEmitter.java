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
        String structLLVMType;
        if (concreteType != null && !concreteType.isEmpty()) {
            structLLVMType = mapper.toLLVM(concreteType);
        } else {
            structLLVMType = mapper.toLLVM("Struct<" + baseStructName + ">");
        }
        if (structLLVMType.endsWith("*")) {
            structLLVMType = structLLVMType.substring(0, structLLVMType.length() - 1);
        }

        String elemType = null;
        if (concreteType != null && concreteType.startsWith("Struct<")) {
            int firstLt  = concreteType.indexOf('<');
            int secondLt = concreteType.indexOf('<', firstLt + 1);
            int innerGt  = concreteType.indexOf('>', secondLt + 1);
            if (secondLt != -1 && innerGt != -1 && secondLt + 1 < innerGt) {
                elemType = concreteType.substring(secondLt + 1, innerGt).trim();
            }
        }
        if (elemType == null) {
            String shortName = structLLVMType.startsWith("%")
                    ? structLLVMType.substring(1)
                    : structLLVMType;
            String prefix = baseStructName + "_";
            if (shortName.startsWith(prefix) && shortName.length() > prefix.length()) {
                elemType = shortName.substring(prefix.length());
            }
        }

        String structPtr = tempManager.newTemp();
        llvm.append("  ").append(structPtr).append(" = alloca ").append(structLLVMType).append("\n");

        StructNode def;
        if (elemType != null) {
            StructNode base = visitor.getStructNode(baseStructName);
            def = visitor.getOrCreateSpecializedStruct(base, elemType);
        } else {
            def = visitor.getStructNode(baseStructName);
        }

        if (def == null) {
            throw new RuntimeException("Struct não encontrada: " + baseStructName + " (elemType=" + elemType + ")");
        }


        List<VariableDeclarationNode> fields = def.getFields();
        List<ASTNode> posValues = node.getPositionalValues();
        Map<String, ASTNode> namedValues = node.getNamedValues();
        int providedPos = (posValues == null) ? 0 : posValues.size();

        for (int i = 0; i < fields.size(); i++) {
            VariableDeclarationNode field = fields.get(i);
            String fname = field.getName();
            String fieldType = field.getType();

            String effectiveFieldType = fieldType;
            if (elemType != null && fieldType.startsWith("List<") && fieldType.contains("?")) {
                effectiveFieldType = "List<" + elemType + ">";
            }

            ASTNode providedValue = null;
            if (!namedValues.isEmpty() && namedValues.containsKey(fname)) {
                providedValue = namedValues.get(fname);
            } else if (posValues != null && i < providedPos) {
                providedValue = posValues.get(i);
            }

            String fieldLLVMType = mapFieldTypeForStruct(effectiveFieldType);


            String valueTemp;
            String codeBefore = "";
            if (providedValue != null) {
                codeBefore = providedValue.accept(visitor);
                valueTemp = extractTemp(codeBefore);
            } else {
                valueTemp = emitDefaultValue(effectiveFieldType, fieldLLVMType, llvm);
            }

            if (!codeBefore.isEmpty()) {
                llvm.append(codeBefore);
            }

            String fieldPtr = tempManager.newTemp();
            llvm.append("  ").append(fieldPtr).append(" = getelementptr inbounds ")
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
                        case "string" -> {
                            llvm.append("  ").append(tmp)
                                    .append(" = call i8* @arraylist_create(i64 10)\n");
                            String casted = tempManager.newTemp();
                            llvm.append("  ").append(casted)
                                    .append(" = bitcast i8* ").append(tmp).append(" to %ArrayList*\n");
                            return casted;
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
