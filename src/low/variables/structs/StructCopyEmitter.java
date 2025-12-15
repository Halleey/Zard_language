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

    public StructCopyEmitter(
            Map<String, TypeInfos> varTypes,
            TempManager temps,
            GlobalStringManager globalStrings,
            LLVisitorMain visitor
    ) {
        this.varTypes = varTypes;
        this.temps = temps;
        this.globalStrings = globalStrings;
        this.visitor = visitor;
    }

    /**
     * Deep copy SEMÂNTICO:
     * - aloca novo struct na heap
     * - copia todos os campos recursivamente
     * - retorna um %Struct*
     */
    public String emitDeepCopy(String structName, String srcPtr) {
        StringBuilder llvm = new StringBuilder();

        StructNode def = visitor.getStructNode(structName);
        if (def == null) {
            throw new RuntimeException("Struct definition not found: " + structName);
        }

        int size = def.getLLVMSizeBytes();

        String raw = temps.newTemp();
        String dst = temps.newTemp();

        llvm.append("  ").append(raw)
                .append(" = call i8* @malloc(i64 ").append(size).append(")\n");

        llvm.append("  ").append(dst)
                .append(" = bitcast i8* ").append(raw)
                .append(" to %").append(structName).append("*\n");

        int index = 0;
        for (VariableDeclarationNode field : def.getFields()) {

            String fieldType = field.getType();
            String llvmFieldType = typeMapper.toLLVM(fieldType);

            String srcFieldPtr = temps.newTemp();
            String dstFieldPtr = temps.newTemp();

            llvm.append("  ").append(srcFieldPtr)
                    .append(" = getelementptr inbounds %").append(structName)
                    .append(", %").append(structName).append("* ").append(srcPtr)
                    .append(", i32 0, i32 ").append(index).append("\n");

            llvm.append("  ").append(dstFieldPtr)
                    .append(" = getelementptr inbounds %").append(structName)
                    .append(", %").append(structName).append("* ").append(dst)
                    .append(", i32 0, i32 ").append(index).append("\n");

            /* ===== Struct dentro de Struct ===== */
            if (fieldType.startsWith("Struct<")) {

                String inner = fieldType.substring(7, fieldType.length() - 1).trim();

                String srcInner = temps.newTemp();
                llvm.append("  ").append(srcInner)
                        .append(" = load %").append(inner)
                        .append("*, %").append(inner).append("** ").append(srcFieldPtr).append("\n");

                llvm.append(emitDeepCopy(inner, srcInner));

                String copied = extractLastTemp(llvm.toString());

                llvm.append("  store %").append(inner).append("* ").append(copied)
                        .append(", %").append(inner).append("** ").append(dstFieldPtr).append("\n");
            }

            /* ===== List<T> ===== */
            else if (fieldType.startsWith("List<")) {
                llvm.append(emitListDeepCopy(fieldType, srcFieldPtr, dstFieldPtr));
            }

            /* ===== String ===== */
            else if ("%String*".equals(llvmFieldType)) {
                String val = temps.newTemp();
                llvm.append("  ").append(val)
                        .append(" = load %String*, %String** ").append(srcFieldPtr).append("\n");
                llvm.append("  store %String* ").append(val)
                        .append(", %String** ").append(dstFieldPtr).append("\n");
            }

            /* ===== Primitivos ===== */
            else {
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
            String srcList    = temps.newTemp();
            String dataPtrPtr = temps.newTemp();
            String dataPtr    = temps.newTemp();
            String sizePtr    = temps.newTemp();
            String size       = temps.newTemp();
            String newList    = temps.newTemp();

            sb.append("  ").append(srcList)
                    .append(" = load %struct.ArrayListInt*, %struct.ArrayListInt** ").append(srcFieldPtr).append("\n");

            sb.append("  ").append(dataPtrPtr)
                    .append(" = getelementptr inbounds %struct.ArrayListInt, %struct.ArrayListInt* ").append(srcList)
                    .append(", i32 0, i32 0\n");

            sb.append("  ").append(dataPtr)
                    .append(" = load i32*, i32** ").append(dataPtrPtr).append("\n");

            sb.append("  ").append(sizePtr)
                    .append(" = getelementptr inbounds %struct.ArrayListInt, %struct.ArrayListInt* ").append(srcList)
                    .append(", i32 0, i32 1\n");

            sb.append("  ").append(size)
                    .append(" = load i64, i64* ").append(sizePtr).append("\n");

            sb.append("  ").append(newList)
                    .append(" = call %struct.ArrayListInt* @arraylist_create_int(i64 ").append(size).append(")\n");

            sb.append("  call void @arraylist_addAll_int(%struct.ArrayListInt* ").append(newList)
                    .append(", i32* ").append(dataPtr).append(", i64 ").append(size).append(")\n");

            sb.append("  store %struct.ArrayListInt* ").append(newList)
                    .append(", %struct.ArrayListInt** ").append(dstFieldPtr).append("\n");

            return sb.toString();
        }

        if (inner.equals("double")) {
            String srcList    = temps.newTemp();
            String dataPtrPtr = temps.newTemp();
            String dataPtr    = temps.newTemp();
            String sizePtr    = temps.newTemp();
            String size       = temps.newTemp();
            String newList    = temps.newTemp();

            sb.append("  ").append(srcList)
                    .append(" = load %struct.ArrayListDouble*, %struct.ArrayListDouble** ").append(srcFieldPtr).append("\n");

            sb.append("  ").append(dataPtrPtr)
                    .append(" = getelementptr inbounds %struct.ArrayListDouble, %struct.ArrayListDouble* ").append(srcList)
                    .append(", i32 0, i32 0\n");

            sb.append("  ").append(dataPtr)
                    .append(" = load double*, double** ").append(dataPtrPtr).append("\n");

            sb.append("  ").append(sizePtr)
                    .append(" = getelementptr inbounds %struct.ArrayListDouble, %struct.ArrayListDouble* ").append(srcList)
                    .append(", i32 0, i32 1\n");

            sb.append("  ").append(size)
                    .append(" = load i64, i64* ").append(sizePtr).append("\n");

            sb.append("  ").append(newList)
                    .append(" = call %struct.ArrayListDouble* @arraylist_create_double(i64 ").append(size).append(")\n");

            sb.append("  call void @arraylist_addAll_double(%struct.ArrayListDouble* ").append(newList)
                    .append(", double* ").append(dataPtr).append(", i64 ").append(size).append(")\n");

            sb.append("  store %struct.ArrayListDouble* ").append(newList)
                    .append(", %struct.ArrayListDouble** ").append(dstFieldPtr).append("\n");

            return sb.toString();
        }

        if (inner.equals("boolean") || inner.equals("bool")) {
            String srcList    = temps.newTemp();
            String dataPtrPtr = temps.newTemp();
            String dataPtr    = temps.newTemp();
            String sizePtr    = temps.newTemp();
            String size       = temps.newTemp();
            String newList    = temps.newTemp();

            sb.append("  ").append(srcList)
                    .append(" = load %struct.ArrayListBool*, %struct.ArrayListBool** ").append(srcFieldPtr).append("\n");

            sb.append("  ").append(dataPtrPtr)
                    .append(" = getelementptr inbounds %struct.ArrayListBool, %struct.ArrayListBool* ").append(srcList)
                    .append(", i32 0, i32 0\n");

            sb.append("  ").append(dataPtr)
                    .append(" = load i1*, i1** ").append(dataPtrPtr).append("\n");

            sb.append("  ").append(sizePtr)
                    .append(" = getelementptr inbounds %struct.ArrayListBool, %struct.ArrayListBool* ").append(srcList)
                    .append(", i32 0, i32 1\n");

            sb.append("  ").append(size)
                    .append(" = load i64, i64* ").append(sizePtr).append("\n");

            sb.append("  ").append(newList)
                    .append(" = call %struct.ArrayListBool* @arraylist_create_bool(i64 ").append(size).append(")\n");

            sb.append("  call void @arraylist_addAll_bool(%struct.ArrayListBool* ").append(newList)
                    .append(", i1* ").append(dataPtr).append(", i64 ").append(size).append(")\n");

            sb.append("  store %struct.ArrayListBool* ").append(newList)
                    .append(", %struct.ArrayListBool** ").append(dstFieldPtr).append("\n");

            return sb.toString();
        }

        if (inner.equals("string") || inner.equals("String")) {
            String srcList = temps.newTemp();
            sb.append("  ").append(srcList)
                    .append(" = load %ArrayList*, %ArrayList** ").append(srcFieldPtr).append("\n");

            String len32 = temps.newTemp();
            sb.append("  ").append(len32)
                    .append(" = call i32 @length(%ArrayList* ").append(srcList).append(")\n");

            String len64 = temps.newTemp();
            sb.append("  ").append(len64)
                    .append(" = zext i32 ").append(len32).append(" to i64\n");

            String raw = temps.newTemp();
            sb.append("  ").append(raw)
                    .append(" = call i8* @arraylist_create(i64 ").append(len64).append(")\n");

            String newList = temps.newTemp();
            sb.append("  ").append(newList)
                    .append(" = bitcast i8* ").append(raw).append(" to %ArrayList*\n");

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
                    .append(" = icmp ult i64 ").append(cur).append(", ").append(len64).append("\n");

            sb.append("  br i1 ").append(cmp)
                    .append(", label %").append(body)
                    .append(", label %").append(end).append("\n");

            sb.append(body).append(":\n");

            String rawPtr = temps.newTemp();
            sb.append("  ").append(rawPtr)
                    .append(" = call i8* @arraylist_get_ptr(%ArrayList* ").append(srcList)
                    .append(", i64 ").append(cur).append(")\n");

            String strPtr = temps.newTemp();
            sb.append("  ").append(strPtr)
                    .append(" = bitcast i8* ").append(rawPtr).append(" to %String*\n");

            String oldDataPtr = temps.newTemp();
            sb.append("  ").append(oldDataPtr)
                    .append(" = getelementptr inbounds %String, %String* ").append(strPtr)
                    .append(", i32 0, i32 0\n");

            String oldChars = temps.newTemp();
            sb.append("  ").append(oldChars)
                    .append(" = load i8*, i8** ").append(oldDataPtr).append("\n");

            String newStr = temps.newTemp();
            sb.append("  ").append(newStr)
                    .append(" = call %String* @createString(i8* ").append(oldChars).append(")\n");

            String castedStr = temps.newTemp();

            sb.append("  ").append(castedStr)
                    .append(" = bitcast %String* ").append(newStr).append(" to i8*\n");

            sb.append("  call void @arraylist_add_ptr(%ArrayList* ")
                    .append(newList)
                    .append(", i8* ").append(castedStr).append(")\n");


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

        if (visitor.getStructNode(inner) != null) {
            String srcList = temps.newTemp();
            sb.append("  ").append(srcList)
                    .append(" = load %ArrayList*, %ArrayList** ").append(srcFieldPtr).append("\n");

            String len32 = temps.newTemp();
            sb.append("  ").append(len32)
                    .append(" = call i32 @length(%ArrayList* ").append(srcList).append(")\n");

            String len64 = temps.newTemp();
            sb.append("  ").append(len64)
                    .append(" = zext i32 ").append(len32).append(" to i64\n");

            String raw = temps.newTemp();
            sb.append("  ").append(raw)
                    .append(" = call i8* @arraylist_create(i64 ").append(len64).append(")\n");

            String newList = temps.newTemp();
            sb.append("  ").append(newList)
                    .append(" = bitcast i8* ").append(raw).append(" to %ArrayList*\n");

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
                    .append(" = icmp ult i64 ").append(cur).append(", ").append(len64).append("\n");

            sb.append("  br i1 ").append(cmp)
                    .append(", label %").append(body)
                    .append(", label %").append(end).append("\n");

            sb.append(body).append(":\n");

            String srcElem = temps.newTemp();
            sb.append("  ").append(srcElem)
                    .append(" = call i8* @arraylist_get_ptr(%ArrayList* ")
                    .append(srcList).append(", i64 ").append(cur).append(")\n");

            String casted = temps.newTemp();
            sb.append("  ").append(casted)
                    .append(" = bitcast i8* ").append(srcElem)
                    .append(" to %").append(inner).append("*\n");

            sb.append(emitDeepCopy(inner, casted));
            String copied = extractLastTemp(sb.toString());

            sb.append("  call void @arraylist_add_ptr(%ArrayList* ")
                    .append(newList).append(", i8* bitcast(%").append(inner).append("* ")
                    .append(copied).append(" to i8*))\n");

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

        throw new RuntimeException("Deep copy not supported for List<" + inner + ">");
    }


    private String extractLastTemp(String llvm) {
        int idx = llvm.lastIndexOf(";;VAL:");
        int end = llvm.indexOf(";;TYPE:", idx);
        return llvm.substring(idx + 6, end).trim();
    }
}
