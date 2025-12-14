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

    public StructInstanceEmitter(TempManager tempManager, GlobalStringManager stringManager) {
        this.tempManager = tempManager;
    }

    public String emit(StructInstaceNode node, LLVisitorMain visitor) {

        StringBuilder llvm = new StringBuilder();

        String baseStructName = node.getName();
        String concreteType  = node.getConcreteType();

        TypeMapper mapper = new TypeMapper();

        String structLLVMPtrType =
                (concreteType != null && !concreteType.isEmpty())
                        ? mapper.toLLVM(concreteType)
                        : mapper.toLLVM("Struct<" + baseStructName + ">");

        if (!structLLVMPtrType.endsWith("*")) {
            structLLVMPtrType = structLLVMPtrType + "*";
        }

        String structLLVMType = structLLVMPtrType.substring(0, structLLVMPtrType.length() - 1);

        String elemType = null;

        if (concreteType != null && concreteType.startsWith("Struct<")) {
            int firstLt  = concreteType.indexOf('<');
            int secondLt = concreteType.indexOf('<', firstLt + 1);
            int gt       = concreteType.lastIndexOf('>');

            if (secondLt != -1 && gt != -1 && secondLt + 1 < gt) {
                elemType = concreteType.substring(secondLt + 1, gt).trim();
            }
        }

        if (elemType == null) {
            String shortName = structLLVMType.startsWith("%")
                    ? structLLVMType.substring(1)
                    : structLLVMType;

            String prefix = baseStructName + "_";
            if (shortName.startsWith(prefix)) {
                elemType = shortName.substring(prefix.length());
            }
        }

        StructNode def;
        if (elemType != null) {
            StructNode base = visitor.getStructNode(baseStructName);
            if (base == null) throw new RuntimeException("Struct base não encontrada: " + baseStructName);
            def = visitor.getOrCreateSpecializedStruct(base, elemType);
        } else {
            def = visitor.getStructNode(baseStructName);
        }

        if (def == null) {
            throw new RuntimeException("Struct não encontrada: " + baseStructName);
        }

        String gepTmp  = tempManager.newTemp();
        String sizeTmp = tempManager.newTemp();
        String rawPtr  = tempManager.newTemp();
        String objPtr  = tempManager.newTemp();

        llvm.append("  ").append(gepTmp)
                .append(" = getelementptr ").append(structLLVMType)
                .append(", ").append(structLLVMType).append("* null, i32 1\n");

        llvm.append("  ").append(sizeTmp)
                .append(" = ptrtoint ").append(structLLVMType)
                .append("* ").append(gepTmp).append(" to i64\n");

        llvm.append("  ").append(rawPtr)
                .append(" = call i8* @malloc(i64 ").append(sizeTmp).append(")\n");

        llvm.append("  ").append(objPtr)
                .append(" = bitcast i8* ").append(rawPtr)
                .append(" to ").append(structLLVMType).append("*\n");

        var fields = def.getFields();

        for (int i = 0; i < fields.size(); i++) {
            VariableDeclarationNode field = fields.get(i);
            String fieldType = field.getType();

            if (!fieldType.startsWith("List<")) continue;

            String elem = fieldType.substring(5, fieldType.length() - 1).trim();

            String listLLVMType;
            String listCreateFn;

            switch (elem) {
                case "int" -> {
                    listLLVMType = "%struct.ArrayListInt*";
                    listCreateFn = "@arraylist_create_int";
                }
                case "double" -> {
                    listLLVMType = "%struct.ArrayListDouble*";
                    listCreateFn = "@arraylist_create_double";
                }
                case "boolean" -> {
                    listLLVMType = "%struct.ArrayListBool*";
                    listCreateFn = "@arraylist_create_bool";
                }
                default -> {
                    listLLVMType = "%ArrayList*";
                    listCreateFn = "@arraylist_create";
                }
            }

            String listTmp  = tempManager.newTemp();
            String fieldPtr = tempManager.newTemp();

            llvm.append("  ").append(listTmp)
                    .append(" = call ").append(listLLVMType)
                    .append(" ").append(listCreateFn)
                    .append("(i64 10)\n");

            llvm.append("  ").append(fieldPtr)
                    .append(" = getelementptr inbounds ")
                    .append(structLLVMType).append(", ").append(structLLVMType)
                    .append("* ").append(objPtr)
                    .append(", i32 0, i32 ").append(i).append("\n");

            llvm.append("  store ").append(listLLVMType).append(" ").append(listTmp)
                    .append(", ").append(listLLVMType).append("* ").append(fieldPtr).append("\n");
        }

        llvm.append(";;VAL:").append(objPtr)
                .append(";;TYPE:").append(structLLVMPtrType).append("\n");

        return llvm.toString();
    }
}
