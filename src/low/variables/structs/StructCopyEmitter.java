package low.variables.structs;

import ast.structs.StructNode;
import ast.variables.AssignmentNode;
import ast.variables.VariableDeclarationNode;
import context.statics.symbols.ListType;
import context.statics.symbols.StructType;
import context.statics.symbols.Type;
import low.TempManager;
import low.functions.TypeMapper;
import low.main.GlobalStringManager;
import low.main.TypeInfos;
import low.module.LLVisitorMain;

import java.util.Map;
public class StructCopyEmitter {

    private final Map<String, TypeInfos> varTypes;
    private final TempManager temps;
    private final GlobalStringManager globalStrings;
    private final LLVisitorMain visitor;
    private final TypeMapper typeMapper = new TypeMapper();

    public StructCopyEmitter(Map<String, TypeInfos> varTypes, TempManager temps,
                             GlobalStringManager globalStrings, LLVisitorMain visitor
                           ) {
        this.varTypes = varTypes;
        this.temps = temps;
        this.globalStrings = globalStrings;
        this.visitor = visitor;

    }

    public String emit(AssignmentNode node, String srcTemp, String dstPtr, TypeInfos structType) {
        StringBuilder llvm = new StringBuilder();
        Type type = structType.getType();

        if (!(type instanceof StructType st)) {
            throw new RuntimeException("StructCopyEmitter requires a StructType, got: " + type);
        }

        String structName = st.name();
        StructNode def = visitor.getStructNode(structName);
        if (def == null) throw new RuntimeException("Struct definition not found for: " + structName);

        llvm.append("  ; Deep copy of struct ").append(structName).append("\n");

        String dst = temps.newTemp();
        llvm.append("  ").append(dst)
                .append(" = load %").append(structName)
                .append("*, %").append(structName)
                .append("** ").append(dstPtr).append("\n");

        int index = 0;
        for (VariableDeclarationNode field : def.getFields()) {
            TypeInfos fieldTypeInfo = resolveFieldType(field);
            String llvmFieldType = fieldTypeInfo.getLLVMType();

            String srcFieldPtr = temps.newTemp();
            String dstFieldPtr = temps.newTemp();

            llvm.append("  ").append(srcFieldPtr)
                    .append(" = getelementptr inbounds %").append(structName)
                    .append(", %").append(structName).append("* ").append(srcTemp)
                    .append(", i32 0, i32 ").append(index).append("\n");

            llvm.append("  ").append(dstFieldPtr)
                    .append(" = getelementptr inbounds %").append(structName)
                    .append(", %").append(structName).append("* ").append(dst)
                    .append(", i32 0, i32 ").append(index).append("\n");

            Type fType = fieldTypeInfo.getType();
            if (fType instanceof StructType nestedStruct) {
                String srcInner = temps.newTemp();
                llvm.append("  ").append(srcInner)
                        .append(" = load %").append(nestedStruct.name())
                        .append("*, %").append(nestedStruct.name())
                        .append("** ").append(srcFieldPtr).append("\n");

                String newInner = temps.newTemp();
                llvm.append("  ").append(newInner)
                        .append(" = alloca %").append(nestedStruct.name()).append("\n");

                llvm.append(emitRecursiveCopy(nestedStruct, srcInner, newInner));

                llvm.append("  store %").append(nestedStruct.name()).append("* ").append(newInner)
                        .append(", %").append(nestedStruct.name()).append("** ").append(dstFieldPtr).append("\n");
            }
            else if (fType instanceof ListType) {
                String val = temps.newTemp();
                llvm.append("  ").append(val)
                        .append(" = load ").append(llvmFieldType)
                        .append(", ").append(llvmFieldType).append("* ").append(srcFieldPtr).append("\n");
                llvm.append("  store ").append(llvmFieldType).append(" ").append(val)
                        .append(", ").append(llvmFieldType).append("* ").append(dstFieldPtr).append("\n");
            }
            else { // primitivo ou string
                String val = temps.newTemp();
                llvm.append("  ").append(val)
                        .append(" = load ").append(llvmFieldType)
                        .append(", ").append(llvmFieldType).append("* ").append(srcFieldPtr).append("\n");
                llvm.append("  store ").append(llvmFieldType).append(" ").append(val)
                        .append(", ").append(llvmFieldType).append("* ").append(dstFieldPtr).append("\n");
            }

            index++;
        }

        llvm.append(";;VAL:").append(dst)
                .append(";;TYPE:").append(structType.getLLVMType()).append("\n");

        return llvm.toString();
    }

    private String emitRecursiveCopy(StructType structType, String srcTemp, String dstTemp) {
        StringBuilder llvm = new StringBuilder();
        StructNode def = visitor.getStructNode(structType.name());
        if (def == null) throw new RuntimeException("Nested struct definition not found: " + structType.name());

        llvm.append("  ; Deep copy of nested struct ").append(structType.name()).append("\n");

        int index = 0;
        for (VariableDeclarationNode field : def.getFields()) {
            TypeInfos fieldTypeInfo = resolveFieldType(field);
            String llvmFieldType = fieldTypeInfo.getLLVMType();

            String srcFieldPtr = temps.newTemp();
            String dstFieldPtr = temps.newTemp();

            llvm.append("  ").append(srcFieldPtr)
                    .append(" = getelementptr inbounds %").append(structType.name())
                    .append(", %").append(structType.name()).append("* ").append(srcTemp)
                    .append(", i32 0, i32 ").append(index).append("\n");

            llvm.append("  ").append(dstFieldPtr)
                    .append(" = getelementptr inbounds %").append(structType.name())
                    .append(", %").append(structType.name()).append("* ").append(dstTemp)
                    .append(", i32 0, i32 ").append(index).append("\n");

            Type fType = fieldTypeInfo.getType();
            if (fType instanceof StructType nested) {
                String srcInner = temps.newTemp();
                llvm.append("  ").append(srcInner)
                        .append(" = load %").append(nested.name())
                        .append("*, %").append(nested.name())
                        .append("** ").append(srcFieldPtr).append("\n");

                String newInner = temps.newTemp();
                llvm.append("  ").append(newInner)
                        .append(" = alloca %").append(nested.name()).append("\n");

                llvm.append(emitRecursiveCopy(nested, srcInner, newInner));

                llvm.append("  store %").append(nested.name()).append("* ").append(newInner)
                        .append(", %").append(nested.name()).append("** ").append(dstFieldPtr).append("\n");
            }
            else { // primitivo ou lista
                String val = temps.newTemp();
                llvm.append("  ").append(val)
                        .append(" = load ").append(llvmFieldType)
                        .append(", ").append(llvmFieldType).append("* ").append(srcFieldPtr).append("\n");
                llvm.append("  store ").append(llvmFieldType).append(" ").append(val)
                        .append(", ").append(llvmFieldType).append("* ").append(dstFieldPtr).append("\n");
            }

            index++;
        }

        return llvm.toString();
    }

    private TypeInfos resolveFieldType(VariableDeclarationNode field) {
        TypeInfos typeInfo = varTypes.get(field.getName());
        if (typeInfo != null) return typeInfo;

        Type type = field.getType();
        String llvmType = typeMapper.toLLVM(type);
        return new TypeInfos(type, llvmType);
    }
}