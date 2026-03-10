package low.structs;

import ast.ASTNode;
import ast.inputs.InputNode;
import ast.lists.ListGetNode;
import ast.structs.StructFieldAccessNode;
import ast.structs.StructInstanceNode;
import ast.structs.StructNode;
import ast.variables.VariableDeclarationNode;
import ast.variables.VariableNode;
import context.statics.symbols.ListType;
import context.statics.symbols.StructType;
import context.statics.symbols.Type;
import low.TempManager;
import low.functions.TypeMapper;
import low.inputs.InputEmitter;
import low.main.TypeInfos;
import low.module.LLVisitorMain;

import java.util.List;

public class StructFieldAccessEmitter {

    private final TempManager temps;
    private final TypeMapper typeMapper = new TypeMapper();

    private static final boolean DEBUG = false
            ;

    private void debug(String msg) {
        if (DEBUG) System.out.println("[StructFieldAccessEmitter] " + msg);
    }

    public StructFieldAccessEmitter(TempManager temps) {
        this.temps = temps;
    }

    public String emit(StructFieldAccessNode node, LLVisitorMain visitor) {

        StringBuilder llvm = new StringBuilder();

        debug("=================================================");
        debug("STRUCT FIELD ACCESS START");
        debug("Field name: " + node.getFieldName());
        debug("Instance AST: " + node.getStructInstance().getClass().getSimpleName());

        String structCode = node.getStructInstance().accept(visitor);
        llvm.append(structCode);

        debug("Generated struct code:\n" + structCode);

        String structVal = extractTemp(structCode);
        String structLLVMType = extractType(structCode).trim();

        debug("structVal = " + structVal);
        debug("structLLVMType = " + structLLVMType);

        if (structLLVMType.endsWith("**")) {

            debug("Detected pointer-to-pointer struct -> performing load");

            String base = structLLVMType.substring(0, structLLVMType.length() - 1);
            String tmp = temps.newTemp();

            llvm.append("  ").append(tmp)
                    .append(" = load ")
                    .append(base).append(", ")
                    .append(base).append("* ")
                    .append(structVal).append("\n");

            llvm.append(";;VAL:").append(tmp)
                    .append(";;TYPE:").append(base)
                    .append("\n");

            debug("Loaded struct pointer: " + tmp);
            debug("New structLLVMType: " + base);

            structVal = tmp;
            structLLVMType = base;
        }

        Type structSemanticType = node.getStructInstance().getType();

        debug("Semantic type: " + structSemanticType);

        if (structSemanticType == null)
            structSemanticType = node.getType();

        String ownerType = resolveOwnerType(node.getStructInstance(), structSemanticType, visitor);

        debug("Resolved owner struct type: " + ownerType);

        if (ownerType == null)
            throw new RuntimeException(
                    "Não foi possível resolver struct dona do campo: "
                            + node.getFieldName()
                            + " (llvmType=" + structLLVMType + ")"
            );

        StructNode structDef = visitor.getStructNode(ownerType);

        if (structDef == null)
            throw new RuntimeException("Tipo não é struct: " + ownerType);

        int fieldIndex = -1;
        VariableDeclarationNode fieldDecl = null;

        List<VariableDeclarationNode> fields = structDef.getFields();

        for (int i = 0; i < fields.size(); i++) {

            if (fields.get(i).getName().equals(node.getFieldName())) {

                fieldIndex = i;
                fieldDecl = fields.get(i);
                break;

            }
        }

        if (fieldIndex == -1)
            throw new RuntimeException(
                    "Campo não encontrado: "
                            + node.getFieldName()
                            + " em struct " + ownerType
            );

        debug("Field index inside struct: " + fieldIndex);
        debug("Field semantic type: " + fieldDecl.getType());

        if (structLLVMType.equals("i8*")) {

            debug("Struct stored as i8* -> performing bitcast");

            String castType = "%" + ownerType + "*";
            String castTemp = temps.newTemp();

            llvm.append("  ").append(castTemp)
                    .append(" = bitcast i8* ")
                    .append(structVal)
                    .append(" to ")
                    .append(castType)
                    .append("\n");

            llvm.append(";;VAL:").append(castTemp)
                    .append(";;TYPE:").append(castType)
                    .append("\n");

            structVal = castTemp;
            structLLVMType = castType;
        }

        String structTypeNoPtr = structLLVMType.replace("*", "");

        debug("Struct LLVM type (no ptr): " + structTypeNoPtr);

        String fieldPtr = temps.newTemp();

        llvm.append("  ").append(fieldPtr)
                .append(" = getelementptr inbounds ")
                .append(structTypeNoPtr).append(", ")
                .append(structLLVMType).append(" ")
                .append(structVal)
                .append(", i32 0, i32 ")
                .append(fieldIndex)
                .append("\n");

        debug("Generated GEP pointer for field: " + fieldPtr);

        Type fieldType = fieldDecl.getType();
        String fieldLLVMType = typeMapper.toLLVM(fieldType);

        debug("Field LLVM type from TypeMapper: " + fieldLLVMType);

        TypeInfos fieldInfo = new TypeInfos(fieldType, fieldLLVMType);

        if (node.getValue() != null) {

            debug("FIELD ASSIGNMENT detected");

            String rhsCode;

            if (node.getValue() instanceof InputNode input) {

                debug("RHS is InputNode");

                rhsCode = new InputEmitter(
                        temps,
                        visitor.getGlobalStrings()
                ).emit(input, fieldLLVMType);

            } else {

                rhsCode = node.getValue().accept(visitor);

            }

            llvm.append(rhsCode);

            debug("Generated RHS code:\n" + rhsCode);

            String rhsVal = extractTemp(rhsCode);
            String rhsType = extractType(rhsCode).trim();

            debug("rhsVal = " + rhsVal);
            debug("rhsType = " + rhsType);

            String storeVal = rhsVal;

            if (!rhsType.equals(fieldLLVMType)) {

                debug("Type mismatch -> performing bitcast");

                String cast = temps.newTemp();

                llvm.append("  ").append(cast)
                        .append(" = bitcast ")
                        .append(rhsType).append(" ")
                        .append(rhsVal)
                        .append(" to ")
                        .append(fieldLLVMType)
                        .append("\n");

                storeVal = cast;
            }

            llvm.append("  store ")
                    .append(fieldLLVMType).append(" ")
                    .append(storeVal)
                    .append(", ")
                    .append(fieldLLVMType).append("* ")
                    .append(fieldPtr)
                    .append("\n");

            debug("Store emitted");

            String ret = temps.newTemp();

            llvm.append("  ").append(ret)
                    .append(" = load ")
                    .append(fieldLLVMType).append(", ")
                    .append(fieldLLVMType).append("* ")
                    .append(fieldPtr)
                    .append("\n");

            llvm.append(";;VAL:").append(ret)
                    .append(";;TYPE:").append(fieldLLVMType)
                    .append("\n");

            debug("Reloaded field value: " + ret);

            visitor.putVarType(node.getFieldName(), fieldInfo);
        }

        else {

            debug("FIELD LOAD detected");

            String loaded = temps.newTemp();

            llvm.append("  ").append(loaded)
                    .append(" = load ")
                    .append(fieldLLVMType).append(", ")
                    .append(fieldLLVMType).append("* ")
                    .append(fieldPtr)
                    .append("\n");

            llvm.append(";;VAL:").append(loaded)
                    .append(";;TYPE:").append(fieldLLVMType)
                    .append("\n");

            debug("Loaded field value: " + loaded);
            debug("Returned LLVM type: " + fieldLLVMType);

            visitor.putVarType(node.getFieldName(), fieldInfo);
        }

        debug("STRUCT FIELD ACCESS END");
        debug("=================================================");

        return llvm.toString();
    }

    // resto da classe permanece igual
    private String resolveOwnerType(ASTNode instance, Type fallbackType, LLVisitorMain visitor) {

        if (instance instanceof VariableNode var) {

            TypeInfos info = visitor.getVarType(var.getName());

            if (info != null) {
                return normalizeOwnerName(info.getType());
            }
        }

        if (instance instanceof StructFieldAccessNode fieldAccess) {

            Type t = fieldAccess.getType();

            return normalizeOwnerName(t);
        }

        if (instance instanceof StructInstanceNode inst) {
            return inst.getName();
        }

        if (instance instanceof ListGetNode get) {

            return normalizeOwnerName(get.getElementType());

        }
        return normalizeOwnerName(fallbackType);
    }


    private String normalizeOwnerName(Type type) {

        if (type == null)
            return null;

        if (type instanceof StructType struct)
            return struct.name();

        if (type instanceof ListType list) {

            Type elem = list.elementType();

            if (elem instanceof StructType struct)
                return struct.name();
        }

        return null;
    }

    private String extractTemp(String code) {

        int valIndex = code.lastIndexOf(";;VAL:");
        int typeIndex = code.indexOf(";;TYPE:", valIndex);

        if (valIndex == -1 || typeIndex == -1)
            throw new RuntimeException(
                    "extractTemp falhou:\n" + code
            );

        return code.substring(valIndex + 6, typeIndex).trim();
    }

    private String extractType(String code) {

        int typeIndex = code.lastIndexOf(";;TYPE:");

        if (typeIndex == -1)
            throw new RuntimeException(
                    "extractType falhou:\n" + code
            );

        return code.substring(typeIndex + 7).trim();
    }
}