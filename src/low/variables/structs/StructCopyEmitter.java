package low.variables.structs;

import ast.structs.StructNode;
import ast.variables.AssignmentNode;
import ast.variables.VariableDeclarationNode;
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
                             GlobalStringManager globalStrings, LLVisitorMain visitor) {
        this.varTypes = varTypes;
        this.temps = temps;
        this.globalStrings = globalStrings;
        this.visitor = visitor;
    }
    public String emit(AssignmentNode node, String srcTemp, String dstPtr, String structTypeName) {
        StringBuilder llvm = new StringBuilder();

        String structName = structTypeName.substring(structTypeName.indexOf("<") + 1, structTypeName.indexOf(">")).trim();
        StructNode def = visitor.getStructNode(structName);
        if (def == null) {
            throw new RuntimeException("Struct definition not found for: " + structName);
        }

        llvm.append("  ; Deep copy of struct ").append(structName).append("\n");

        String dst = temps.newTemp();
        llvm.append("  ").append(dst)
                .append(" = load %").append(structName)
                .append("*, %").append(structName)
                .append("** ").append(dstPtr).append("\n");

        int index = 0;
        for (VariableDeclarationNode field : def.getFields()) {
            String fieldType = field.getType();
            String llvmFieldType = typeMapper.toLLVM(fieldType);

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

            if (fieldType.startsWith("Struct<")) {
                String innerStructName = fieldType.substring(fieldType.indexOf("<") + 1, fieldType.indexOf(">")).trim();
                String srcInner = temps.newTemp();
                llvm.append("  ").append(srcInner)
                        .append(" = load %").append(innerStructName)
                        .append("*, %").append(innerStructName)
                        .append("** ").append(srcFieldPtr).append("\n");

                String newInner = temps.newTemp();
                llvm.append("  ").append(newInner)
                        .append(" = alloca %").append(innerStructName).append("\n");

                llvm.append(emitRecursiveCopy(innerStructName, srcInner, newInner));

                llvm.append("  store %").append(innerStructName).append("* ").append(newInner)
                        .append(", %").append(innerStructName).append("** ").append(dstFieldPtr).append("\n");
            }

            else if (fieldType.startsWith("List<string>") || fieldType.startsWith("List<String>")) {
                String val = temps.newTemp();
                llvm.append("  ").append(val)
                        .append(" = load %ArrayList*, %ArrayList** ").append(srcFieldPtr).append("\n");
                llvm.append("  store %ArrayList* ").append(val)
                        .append(", %ArrayList** ").append(dstFieldPtr).append("\n");
            }

            else if (llvmFieldType.equals("%String*")) {
                String val = temps.newTemp();
                llvm.append("  ").append(val)
                        .append(" = load %String*, %String** ").append(srcFieldPtr).append("\n");
                llvm.append("  store %String* ").append(val)
                        .append(", %String** ").append(dstFieldPtr).append("\n");
            }

            else {
                String val = temps.newTemp();
                llvm.append("  ").append(val)
                        .append(" = load ").append(llvmFieldType)
                        .append(", ").append(llvmFieldType)
                        .append("* ").append(srcFieldPtr).append("\n");
                llvm.append("  store ").append(llvmFieldType).append(" ").append(val)
                        .append(", ").append(llvmFieldType)
                        .append("* ").append(dstFieldPtr).append("\n");
            }

            index++;
        }

        llvm.append(";;VAL:").append(dst)
                .append(";;TYPE:%").append(structName).append("*\n");

        return llvm.toString();
    }



    private String emitRecursiveCopy(String structName, String srcTemp, String dstTemp) {
        StringBuilder llvm = new StringBuilder();
        StructNode def = visitor.getStructNode(structName);
        if (def == null) {
            throw new RuntimeException("Struct definition not found for nested: " + structName);
        }

        llvm.append("  ; Deep copy of nested struct ").append(structName).append("\n");

        int index = 0;
        for (VariableDeclarationNode field : def.getFields()) {
            String fieldType = field.getType();
            String llvmFieldType = typeMapper.toLLVM(fieldType);

            String srcFieldPtr = temps.newTemp();
            String dstFieldPtr = temps.newTemp();

            llvm.append("  ").append(srcFieldPtr)
                    .append(" = getelementptr inbounds %").append(structName)
                    .append(", %").append(structName).append("* ").append(srcTemp)
                    .append(", i32 0, i32 ").append(index).append("\n");

            llvm.append("  ").append(dstFieldPtr)
                    .append(" = getelementptr inbounds %").append(structName)
                    .append(", %").append(structName).append("* ").append(dstTemp)
                    .append(", i32 0, i32 ").append(index).append("\n");

            if (fieldType.startsWith("Struct<")) {
                String innerStruct = fieldType.substring(fieldType.indexOf("<") + 1, fieldType.indexOf(">")).trim();

                String srcInner = temps.newTemp();
                llvm.append("  ").append(srcInner)
                        .append(" = load %").append(innerStruct)
                        .append("*, %").append(innerStruct)
                        .append("** ").append(srcFieldPtr).append("\n");

                String newInner = temps.newTemp();
                llvm.append("  ").append(newInner)
                        .append(" = alloca %").append(innerStruct).append("\n");

                llvm.append(emitRecursiveCopy(innerStruct, srcInner, newInner));

                llvm.append("  store %").append(innerStruct).append("* ").append(newInner)
                        .append(", %").append(innerStruct).append("** ").append(dstFieldPtr).append("\n");
            }

            else if (fieldType.startsWith("List<string>") || fieldType.startsWith("List<String>")) {
                String val = temps.newTemp();
                llvm.append("  ").append(val)
                        .append(" = load %ArrayList*, %ArrayList** ").append(srcFieldPtr).append("\n");
                llvm.append("  store %ArrayList* ").append(val)
                        .append(", %ArrayList** ").append(dstFieldPtr).append("\n");
            }

            else if (llvmFieldType.equals("%String*")) {
                String val = temps.newTemp();
                llvm.append("  ").append(val)
                        .append(" = load %String*, %String** ").append(srcFieldPtr).append("\n");
                llvm.append("  store %String* ").append(val)
                        .append(", %String** ").append(dstFieldPtr).append("\n");
            }

            else {
                String val = temps.newTemp();
                llvm.append("  ").append(val)
                        .append(" = load ").append(llvmFieldType)
                        .append(", ").append(llvmFieldType)
                        .append("* ").append(srcFieldPtr).append("\n");
                llvm.append("  store ").append(llvmFieldType).append(" ").append(val)
                        .append(", ").append(llvmFieldType)
                        .append("* ").append(dstFieldPtr).append("\n");
            }

            index++;
        }

        return llvm.toString();
    }
}
