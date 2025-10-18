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

public class StructInstanceEmitter {

    private final TempManager tempManager;
    private final GlobalStringManager stringManager;
    public StructInstanceEmitter(TempManager tempManager, GlobalStringManager stringManager) {
        this.tempManager = tempManager;
        this.stringManager = stringManager;
    }

    public String emit(StructInstaceNode node, LLVisitorMain visitor) {
        StringBuilder llvm = new StringBuilder();

        String structName = node.getName();
        String structLLVMType = "%" + structName;

        String structPtr = tempManager.newTemp();
        llvm.append("  ")
                .append(structPtr)
                .append(" = alloca ")
                .append(structLLVMType)
                .append("\n");

        StructNode def = visitor.getStructNode(structName);
        List<VariableDeclarationNode> fields = def.getFields();

        List<ASTNode> values = node.getPositionalValues();
        int provided = (values == null) ? 0 : values.size();

        for (int i = 0; i < fields.size(); i++) {
            VariableDeclarationNode field = fields.get(i);

            ASTNode providedValue = (i < provided) ? values.get(i) : null;
            String fieldLLVMType = new TypeMapper().toLLVM(field.getType());

            String valueTemp;
            String codeBefore = "";

            if (providedValue != null) {

                codeBefore = providedValue.accept(visitor);
                valueTemp = extractTemp(codeBefore);
            } else {

                valueTemp = emitDefaultValue(field.getType(), llvm);
            }

            if (!codeBefore.isEmpty())
                llvm.append(codeBefore);

            String fieldPtr = tempManager.newTemp();
            llvm.append("  ")
                    .append(fieldPtr)
                    .append(" = getelementptr inbounds ")
                    .append(structLLVMType)
                    .append(", ")
                    .append(structLLVMType)
                    .append("* ")
                    .append(structPtr)
                    .append(", i32 0, i32 ")
                    .append(i)
                    .append("\n");

            llvm.append("  store ")
                    .append(fieldLLVMType)
                    .append(" ")
                    .append(valueTemp)
                    .append(", ")
                    .append(fieldLLVMType)
                    .append("* ")
                    .append(fieldPtr)
                    .append("\n");
        }

        llvm.append(";;VAL:").append(structPtr).append(";;TYPE:").append(structLLVMType).append("*\n");
        return llvm.toString();
    }


    private String emitDefaultValue(String type, StringBuilder llvm) {
        switch (type) {
            case "int":
                return "0";
            case "double":
                return "0.0";
            case "boolean":
                return "0";
            case "string": {
                String emptyLabel = stringManager.getGlobalName("");
                String tmp = tempManager.newTemp();
                llvm.append("  ")
                        .append(tmp)
                        .append(" = call %String* @createString(i8* ")
                        .append(emptyLabel)
                        .append(")\n");
                return tmp;
            }
        }
        return "null";
    }

    private String extractTemp(String code) {
        int lastValIdx = code.lastIndexOf(";;VAL:");
        if (lastValIdx == -1)
            throw new RuntimeException("Cannot find ;;VAL: in: " + code);
        int typeIdx = code.indexOf(";;TYPE:", lastValIdx);
        return code.substring(lastValIdx + 6, typeIdx).trim();
    }
}
