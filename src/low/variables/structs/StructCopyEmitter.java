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

            else if (fieldType.startsWith("List<")) {
                llvm.append(emitListDeepCopy(fieldType, srcFieldPtr, dstFieldPtr));
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

    private String emitListDeepCopy(String fieldType, String srcFieldPtr, String dstFieldPtr) {
        StringBuilder sb = new StringBuilder();

        String inner = fieldType.substring(
                fieldType.indexOf('<') + 1,
                fieldType.lastIndexOf('>')
        ).trim();
        if (inner.equals("int")) {
            String srcList = temps.newTemp();
            sb.append("  ").append(srcList)
                    .append(" = load %struct.ArrayListInt*, %struct.ArrayListInt** ")
                    .append(srcFieldPtr).append("\n");

            String dataPtrPtr = temps.newTemp();
            String dataPtr = temps.newTemp();
            sb.append("  ").append(dataPtrPtr)
                    .append(" = getelementptr inbounds %struct.ArrayListInt, %struct.ArrayListInt* ")
                    .append(srcList).append(", i32 0, i32 0\n");
            sb.append("  ").append(dataPtr)
                    .append(" = load i32*, i32** ").append(dataPtrPtr).append("\n");

            String sizePtr = temps.newTemp();
            String size = temps.newTemp();
            sb.append("  ").append(sizePtr)
                    .append(" = getelementptr inbounds %struct.ArrayListInt, %struct.ArrayListInt* ")
                    .append(srcList).append(", i32 0, i32 1\n");
            sb.append("  ").append(size)
                    .append(" = load i64, i64* ").append(sizePtr).append("\n");

            String newList = temps.newTemp();
            sb.append("  ").append(newList)
                    .append(" = call %struct.ArrayListInt* @arraylist_create_int(i64 ")
                    .append(size).append(")\n");

            sb.append("  call void @arraylist_addAll_int(%struct.ArrayListInt* ")
                    .append(newList).append(", i32* ").append(dataPtr)
                    .append(", i64 ").append(size).append(")\n");

            sb.append("  store %struct.ArrayListInt* ").append(newList)
                    .append(", %struct.ArrayListInt** ").append(dstFieldPtr).append("\n");

            return sb.toString();
        }

        if (inner.equals("double")) {
            String srcList = temps.newTemp();
            sb.append("  ").append(srcList)
                    .append(" = load %struct.ArrayListDouble*, %struct.ArrayListDouble** ")
                    .append(srcFieldPtr).append("\n");

            String dataPtrPtr = temps.newTemp();
            String dataPtr = temps.newTemp();
            sb.append("  ").append(dataPtrPtr)
                    .append(" = getelementptr inbounds %struct.ArrayListDouble, %struct.ArrayListDouble* ")
                    .append(srcList).append(", i32 0, i32 0\n");
            sb.append("  ").append(dataPtr)
                    .append(" = load double*, double** ").append(dataPtrPtr).append("\n");

            String sizePtr = temps.newTemp();
            String size = temps.newTemp();
            sb.append("  ").append(sizePtr)
                    .append(" = getelementptr inbounds %struct.ArrayListDouble, %struct.ArrayListDouble* ")
                    .append(srcList).append(", i32 0, i32 1\n");
            sb.append("  ").append(size)
                    .append(" = load i64, i64* ").append(sizePtr).append("\n");

            String newList = temps.newTemp();
            sb.append("  ").append(newList)
                    .append(" = call %struct.ArrayListDouble* @arraylist_create_double(i64 ")
                    .append(size).append(")\n");

            sb.append("  call void @arraylist_addAll_double(%struct.ArrayListDouble* ")
                    .append(newList).append(", double* ").append(dataPtr)
                    .append(", i64 ").append(size).append(")\n");

            sb.append("  store %struct.ArrayListDouble* ").append(newList)
                    .append(", %struct.ArrayListDouble** ").append(dstFieldPtr).append("\n");

            return sb.toString();
        }

        if (inner.equals("string") || inner.equals("String")) {

            String srcList = temps.newTemp();
            sb.append("  ").append(srcList)
                    .append(" = load %ArrayList*, %ArrayList** ")
                    .append(srcFieldPtr).append("\n");

            String len32 = temps.newTemp();
            sb.append("  ").append(len32)
                    .append(" = call i32 @length(%ArrayList* ")
                    .append(srcList).append(")\n");

            String len64 = temps.newTemp();
            sb.append("  ").append(len64)
                    .append(" = zext i32 ").append(len32).append(" to i64\n");
            String rawNew = temps.newTemp();
            sb.append("  ").append(rawNew)
                    .append(" = call i8* @arraylist_create(i64 ")
                    .append(len64).append(")\n");

            String newList = temps.newTemp();
            sb.append("  ").append(newList)
                    .append(" = bitcast i8* ").append(rawNew)
                    .append(" to %ArrayList*\n");

            String idx = temps.newTemp();
            sb.append("  ").append(idx).append(" = alloca i64\n");
            sb.append("  store i64 0, i64* ").append(idx).append("\n");

            String id = temps.newTemp().replace("%", "");
            String cond = "list_copy_cond_" + id;
            String body = "list_copy_body_" + id;
            String end  = "list_copy_end_"  + id;

            sb.append("  br label %").append(cond).append("\n");

            sb.append(cond).append(":\n");
            String cur = temps.newTemp();
            sb.append("  ").append(cur)
                    .append(" = load i64, i64* ").append(idx).append("\n");

            String cmp = temps.newTemp();
            sb.append("  ").append(cmp)
                    .append(" = icmp ult i64 ").append(cur)
                    .append(", ").append(len64).append("\n");

            sb.append("  br i1 ").append(cmp)
                    .append(", label %").append(body)
                    .append(", label %").append(end).append("\n");

            sb.append(body).append(":\n");

            String rawChar = temps.newTemp();
            sb.append("  ").append(rawChar)
                    .append(" = call i8* @arraylist_get_ptr(%ArrayList* ")
                    .append(srcList).append(", i64 ").append(cur).append(")\n");

            String newStr = temps.newTemp();
            sb.append("  ").append(newStr)
                    .append(" = call %String* @createString(i8* ")
                    .append(rawChar).append(")\n");

            sb.append("  call void @arraylist_add_String(%ArrayList* ")
                    .append(newList).append(", %String* ").append(newStr).append(")\n");

            String next = temps.newTemp();
            sb.append("  ").append(next)
                    .append(" = add i64 ").append(cur).append(", 1\n");
            sb.append("  store i64 ").append(next).append(", i64* ").append(idx).append("\n");
            sb.append("  br label %").append(cond).append("\n");

            sb.append(end).append(":\n");
            sb.append("  store %ArrayList* ").append(newList)
                    .append(", %ArrayList** ").append(dstFieldPtr).append("\n");

            return sb.toString();
        }

        if (inner.equals("boolean") || inner.equals("bool")) {

            String srcList = temps.newTemp();
            sb.append("  ").append(srcList)
                    .append(" = load %struct.ArrayListBool*, %struct.ArrayListBool** ")
                    .append(srcFieldPtr).append("\n");

            String dataPtrPtr = temps.newTemp();
            String dataPtr = temps.newTemp();
            sb.append("  ").append(dataPtrPtr)
                    .append(" = getelementptr inbounds %struct.ArrayListBool, %struct.ArrayListBool* ")
                    .append(srcList).append(", i32 0, i32 0\n");
            sb.append("  ").append(dataPtr)
                    .append(" = load i1*, i1** ").append(dataPtrPtr).append("\n");

            String sizePtr = temps.newTemp();
            String size = temps.newTemp();
            sb.append("  ").append(sizePtr)
                    .append(" = getelementptr inbounds %struct.ArrayListBool, %struct.ArrayListBool* ")
                    .append(srcList).append(", i32 0, i32 1\n");
            sb.append("  ").append(size)
                    .append(" = load i64, i64* ").append(sizePtr).append("\n");

            String newList = temps.newTemp();
            sb.append("  ").append(newList)
                    .append(" = call %struct.ArrayListBool* @arraylist_create_bool(i64 ")
                    .append(size).append(")\n");

            sb.append("  call void @arraylist_addAll_bool(%struct.ArrayListBool* ")
                    .append(newList).append(", i8* ").append(dataPtr)
                    .append(", i64 ").append(size).append(")\n");

            sb.append("  store %struct.ArrayListBool* ").append(newList)
                    .append(", %struct.ArrayListBool** ").append(dstFieldPtr).append("\n");

            return sb.toString();
        }

        throw new RuntimeException("Deep copy not supported for List<" + inner + ">");
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

            else if (fieldType.startsWith("List<")) {
                llvm.append(emitListDeepCopy(fieldType, srcFieldPtr, dstFieldPtr));
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
