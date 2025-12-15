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

    public StructInstanceEmitter(
            TempManager tempManager,
            GlobalStringManager stringManager
    ) {
        this.tempManager = tempManager;
        this.stringManager = stringManager;
    }

    public String emit(
            StructInstaceNode node,
            LLVisitorMain visitor,
            String structPtr,          // <-- RECEBE O PTR
            String structLLVMType      // <-- %Item, %Set_Item
    ) {

        StringBuilder llvm = new StringBuilder();

        StructNode def = visitor.getStructNode(node.getName());
        if (def == null) {
            throw new RuntimeException("Struct não encontrada: " + node.getName());
        }

        List<VariableDeclarationNode> fields = def.getFields();

        for (int i = 0; i < fields.size(); i++) {

            VariableDeclarationNode field = fields.get(i);
            String fieldType = field.getType();
            String fieldLLVM = mapFieldTypeForStruct(fieldType);
            String valueTmp  = emitDefaultValue(fieldType, llvm);

            String fieldPtr = tempManager.newTemp();

            llvm.append("  ").append(fieldPtr)
                    .append(" = getelementptr inbounds ")
                    .append(structLLVMType).append(", ")
                    .append(structLLVMType).append("* ")
                    .append(structPtr)
                    .append(", i32 0, i32 ").append(i).append("\n");

            llvm.append("  store ")
                    .append(fieldLLVM).append(" ").append(valueTmp)
                    .append(", ").append(fieldLLVM)
                    .append("* ").append(fieldPtr).append("\n");
        }

        llvm.append(";;VAL:").append(structPtr)
                .append(";;TYPE:").append(structLLVMType).append("*\n");

        return llvm.toString();
    }

    private String mapFieldTypeForStruct(String type) {
        if (type.startsWith("List<")) {
            String inner = type.substring(5, type.length() - 1).trim();
            return switch (inner) {
                case "int" -> "%struct.ArrayListInt*";
                case "double" -> "%struct.ArrayListDouble*";
                case "boolean", "bool" -> "%struct.ArrayListBool*";
                default -> "%ArrayList*";
            };
        }

        if (type.startsWith("Struct<")) {
            String inner = type.substring(7, type.length() - 1).trim();
            return "%" + inner + "*";
        }

        return new TypeMapper().toLLVM(type);
    }

    private String emitDefaultValue(String type, StringBuilder llvm) {
        return switch (type) {
            case "int" -> "0";
            case "double", "float" -> "0.0";
            case "boolean", "bool" -> "0";
            case "string" -> {
                String tmp = tempManager.newTemp();
                String empty = stringManager.getGlobalName("");
                llvm.append("  ").append(tmp)
                        .append(" = call %String* @createString(i8* ")
                        .append(empty).append(")\n");
                yield tmp;
            }
            default -> "null";
        };
    }
}
