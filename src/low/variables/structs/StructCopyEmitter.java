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

    public String emit(AssignmentNode node, String srcTemp, String dstPtr, String structTypeName) {
        StringBuilder llvm = new StringBuilder();

        String structName =
                structTypeName.substring(structTypeName.indexOf("<") + 1, structTypeName.indexOf(">")).trim();

        StructNode def = visitor.getStructNode(structName);
        if (def == null) throw new RuntimeException("Struct não encontrada: " + structName);

        String llvmName = (def.getLLVMName() != null && !def.getLLVMName().isBlank())
                ? def.getLLVMName().trim()
                : structName;

        llvm.append("  ; DEEP COPY struct ").append(structName).append("\n");

        String sizePtr = temps.newTemp();
        String sizeI64 = temps.newTemp();
        String newMem = temps.newTemp();
        String newStruct = temps.newTemp();

        llvm.append("  ").append(sizePtr)
                .append(" = getelementptr %").append(llvmName)
                .append(", %").append(llvmName).append("* null, i32 1\n");

        llvm.append("  ").append(sizeI64)
                .append(" = ptrtoint %").append(llvmName).append("* ")
                .append(sizePtr).append(" to i64\n");

        llvm.append("  ").append(newMem)
                .append(" = call i8* @malloc(i64 ").append(sizeI64).append(")\n");

        llvm.append("  ").append(newStruct)
                .append(" = bitcast i8* ").append(newMem)
                .append(" to %").append(llvmName).append("*\n");

        int idx = 0;
        for (VariableDeclarationNode field : def.getFields()) {
            String fieldType = field.getType().trim();

            String srcFieldPtr = temps.newTemp();
            String dstFieldPtr = temps.newTemp();

            llvm.append("  ").append(srcFieldPtr)
                    .append(" = getelementptr inbounds %").append(llvmName)
                    .append(", %").append(llvmName).append("* ").append(srcTemp)
                    .append(", i32 0, i32 ").append(idx).append("\n");

            llvm.append("  ").append(dstFieldPtr)
                    .append(" = getelementptr inbounds %").append(llvmName)
                    .append(", %").append(llvmName).append("* ").append(newStruct)
                    .append(", i32 0, i32 ").append(idx).append("\n");

            if (fieldType.startsWith("Struct<") && fieldType.endsWith(">")) {
                String inner = fieldType.substring(7, fieldType.length() - 1).trim();
                StructNode innerDef = visitor.getStructNode(inner);
                if (innerDef == null) throw new RuntimeException("Struct interna não encontrada: " + inner);

                String innerLLVM = (innerDef.getLLVMName() != null && !innerDef.getLLVMName().isBlank())
                        ? innerDef.getLLVMName().trim()
                        : inner;

                String srcInner = temps.newTemp();
                llvm.append("  ").append(srcInner)
                        .append(" = load %").append(innerLLVM)
                        .append("*, %").append(innerLLVM).append("** ")
                        .append(srcFieldPtr).append("\n");

                String slot = temps.newTemp();
                llvm.append("  ").append(slot).append(" = alloca %").append(innerLLVM).append("*\n");
                llvm.append("  store %").append(innerLLVM).append("* null, %")
                        .append(innerLLVM).append("** ").append(slot).append("\n");

                llvm.append(this.emit(node, srcInner, slot, "Struct<" + inner + ">"));

                String copied = temps.newTemp();
                llvm.append("  ").append(copied)
                        .append(" = load %").append(innerLLVM)
                        .append("*, %").append(innerLLVM).append("** ")
                        .append(slot).append("\n");

                llvm.append("  store %").append(innerLLVM).append("* ").append(copied)
                        .append(", %").append(innerLLVM).append("** ")
                        .append(dstFieldPtr).append("\n");
            }
            else if (fieldType.equals("string") || fieldType.equals("String")) {
                String srcStr = temps.newTemp();
                String dataPtr = temps.newTemp();
                String data = temps.newTemp();
                String newStr = temps.newTemp();

                llvm.append("  ").append(srcStr)
                        .append(" = load %String*, %String** ")
                        .append(srcFieldPtr).append("\n");

                llvm.append("  ").append(dataPtr)
                        .append(" = getelementptr inbounds %String, %String* ")
                        .append(srcStr).append(", i32 0, i32 0\n");

                llvm.append("  ").append(data)
                        .append(" = load i8*, i8** ").append(dataPtr).append("\n");

                llvm.append("  ").append(newStr)
                        .append(" = call %String* @createString(i8* ")
                        .append(data).append(")\n");

                llvm.append("  store %String* ").append(newStr)
                        .append(", %String** ").append(dstFieldPtr).append("\n");
            }
            else if (fieldType.startsWith("List<") && fieldType.endsWith(">")) {
                String elem = fieldType.substring(5, fieldType.length() - 1).trim();
                llvm.append(emitListDeepCopy(elem, srcFieldPtr, dstFieldPtr));
            }
            else {
                String llvmType = typeMapper.toLLVM(fieldType);
                String v = temps.newTemp();

                llvm.append("  ").append(v)
                        .append(" = load ").append(llvmType)
                        .append(", ").append(llvmType).append("* ")
                        .append(srcFieldPtr).append("\n");

                llvm.append("  store ").append(llvmType).append(" ").append(v)
                        .append(", ").append(llvmType).append("* ")
                        .append(dstFieldPtr).append("\n");
            }

            idx++;
        }

        llvm.append("  store %").append(llvmName).append("* ").append(newStruct)
                .append(", %").append(llvmName).append("** ").append(dstPtr).append("\n");

        llvm.append(";;VAL:").append(newStruct)
                .append(";;TYPE:%").append(llvmName).append("*\n");

        return llvm.toString();
    }

    private String emitListDeepCopy(String elemType, String srcFieldPtr, String dstFieldPtr) {
        StringBuilder sb = new StringBuilder();

        boolean isInt = elemType.equals("int");
        boolean isDouble = elemType.equals("double");
        boolean isBool = elemType.equals("bool") || elemType.equals("boolean");
        boolean isString = elemType.equals("string") || elemType.equals("String");

        String listLLVMType;
        String createFn;
        String sizeFn;
        String addFn;

        if (isInt) {
            listLLVMType = "%struct.ArrayListInt*";
            createFn = "@arraylist_create_int";
            sizeFn = "@arraylist_size_int";
            addFn = "@arraylist_add_int";
        } else if (isDouble) {
            listLLVMType = "%struct.ArrayListDouble*";
            createFn = "@arraylist_create_double";
            sizeFn = "@arraylist_size_double";
            addFn = "@arraylist_add_double";
        } else if (isBool) {
            listLLVMType = "%struct.ArrayListBool*";
            createFn = "@arraylist_create_bool";
            sizeFn = "@arraylist_size_bool";
            addFn = "@arraylist_add_bool";
        } else {
            listLLVMType = "%ArrayList*";
            createFn = "@arraylist_create";
            sizeFn = "@length";
            addFn = isString ? "@arraylist_add_String" : "@arraylist_add_ptr";
        }

        String srcList = temps.newTemp();
        sb.append("  ").append(srcList)
                .append(" = load ").append(listLLVMType)
                .append(", ").append(listLLVMType).append("* ")
                .append(srcFieldPtr).append("\n");

        String size = temps.newTemp();
        sb.append("  ").append(size)
                .append(" = call i32 ").append(sizeFn)
                .append("(").append(listLLVMType).append(" ")
                .append(srcList).append(")\n");

        String size64 = temps.newTemp();
        String rawList = temps.newTemp();
        String newList = temps.newTemp();

        sb.append("  ").append(size64)
                .append(" = zext i32 ").append(size).append(" to i64\n");

        sb.append("  ").append(rawList)
                .append(" = call i8* ").append(createFn)
                .append("(i64 ").append(size64).append(")\n");

        sb.append("  ").append(newList)
                .append(" = bitcast i8* ").append(rawList)
                .append(" to ").append(listLLVMType).append("\n");

        sb.append("  store ").append(listLLVMType).append(" ").append(newList)
                .append(", ").append(listLLVMType).append("* ")
                .append(dstFieldPtr).append("\n");

        return sb.toString();
    }
}
