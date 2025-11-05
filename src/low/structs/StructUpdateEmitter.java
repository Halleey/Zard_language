package low.structs;

import ast.structs.StructNode;
import ast.structs.StructUpdateNode;
import ast.variables.VariableDeclarationNode;
import low.TempManager;
import low.functions.TypeMapper;
import low.module.LLVisitorMain;

import java.util.List;

public class StructUpdateEmitter {
    private final TempManager temps;
    private final LLVisitorMain visitor;
    private final TypeMapper typeMapper = new TypeMapper();

    public StructUpdateEmitter(TempManager temps, LLVisitorMain visitor) {
        this.temps = temps;
        this.visitor = visitor;
    }

    public String emit(StructUpdateNode node) {
        StringBuilder llvm = new StringBuilder();

        // gera o codigo do struct alvo
        String targetCode = node.getTargetStruct().accept(visitor);
        llvm.append(targetCode);

        String structVal = extractTemp(targetCode);
        String structType = extractType(targetCode).trim();

        // pode ser i8* (struct genérica) faz bitcast
        if (structType.equals("i8*")) {
            String ownerType = visitor.resolveStructName(node.getTargetStruct());
            String realTy = "%" + ownerType + "*";
            String casted = temps.newTemp();
            llvm.append("  ").append(casted)
                    .append(" = bitcast i8* ").append(structVal)
                    .append(" to ").append(realTy).append("\n");
            llvm.append(";;VAL:").append(casted).append(";;TYPE:").append(realTy).append("\n");
            structVal = casted;
            structType = realTy;
        }

        // resolve o tipo logico (nome da struct, campos, etc.)
        String ownerType = visitor.resolveStructName(node.getTargetStruct());
        StructNode def = visitor.getStructNode(ownerType);
        if (def == null)
            throw new RuntimeException("Inline update em tipo não-struct: " + structType);

        // atualiza campos simples
        for (var entry : node.getFieldUpdates().entrySet()) {
            String field = entry.getKey();
            String exprCode = entry.getValue().accept(visitor);
            llvm.append(exprCode);

            String rhsVal = extractTemp(exprCode);
            String rhsTy = extractType(exprCode).trim();

            VariableDeclarationNode fieldDecl = findField(def, field);
            int fieldIndex = findFieldIndex(def, fieldDecl);
            String fieldLLType = mapFieldType(fieldDecl.getType());

            // gera ponteiro do campo
            String fieldPtr = temps.newTemp();
            llvm.append("  ").append(fieldPtr)
                    .append(" = getelementptr inbounds %").append(ownerType)
                    .append(", %").append(ownerType).append("* ").append(structVal)
                    .append(", i32 0, i32 ").append(fieldIndex).append("\n");

            // converte se necessário
            if (!rhsTy.equals(fieldLLType)) {
                String casted = temps.newTemp();
                llvm.append("  ").append(casted)
                        .append(" = bitcast ").append(rhsTy).append(" ").append(rhsVal)
                        .append(" to ").append(fieldLLType).append("\n");
                rhsVal = casted;
            }

            llvm.append("  store ").append(fieldLLType).append(" ").append(rhsVal)
                    .append(", ").append(fieldLLType).append("* ").append(fieldPtr).append("\n");
        }

        // atualiza campos aninhados (recursivo)
        for (var nested : node.getNestedUpdates().entrySet()) {
            String field = nested.getKey();
            StructUpdateNode inner = nested.getValue();

            VariableDeclarationNode fieldDecl = findField(def, field);
            int fieldIndex = findFieldIndex(def, fieldDecl);

            String fieldPtr = temps.newTemp();
            llvm.append("  ").append(fieldPtr)
                    .append(" = getelementptr inbounds %").append(ownerType)
                    .append(", %").append(ownerType).append("* ").append(structVal)
                    .append(", i32 0, i32 ").append(fieldIndex).append("\n");

            String fieldLLType = mapFieldType(fieldDecl.getType());
            String fieldVal = temps.newTemp();

            llvm.append("  ").append(fieldVal)
                    .append(" = load ").append(fieldLLType)
                    .append(", ").append(fieldLLType).append("* ").append(fieldPtr).append("\n");

            // recursivamente chama o emitter para o campo aninhado
            StructUpdateEmitter subEmitter = new StructUpdateEmitter(temps, visitor);
            llvm.append(subEmitter.emitNested(inner, fieldVal, fieldDecl.getType()));
        }

        llvm.append(";;VAL:").append(structVal).append(";;TYPE:").append(structType).append("\n");
        return llvm.toString();
    }

    // subemissão recursiva para aninhamentos
    private String emitNested(StructUpdateNode node, String structVal, String structLangType) {
        StringBuilder llvm = new StringBuilder();

        String structName = structLangType.replace("Struct<", "").replace(">", "").trim();
        StructNode def = visitor.getStructNode(structName);

        for (var entry : node.getFieldUpdates().entrySet()) {
            String field = entry.getKey();
            String exprCode = entry.getValue().accept(visitor);
            llvm.append(exprCode);

            String rhsVal = extractTemp(exprCode);
            String rhsTy = extractType(exprCode).trim();

            VariableDeclarationNode fieldDecl = findField(def, field);
            int fieldIndex = findFieldIndex(def, fieldDecl);
            String fieldLLType = mapFieldType(fieldDecl.getType());

            String fieldPtr = temps.newTemp();
            llvm.append("  ").append(fieldPtr)
                    .append(" = getelementptr inbounds %").append(structName)
                    .append(", %").append(structName).append("* ").append(structVal)
                    .append(", i32 0, i32 ").append(fieldIndex).append("\n");

            if (!rhsTy.equals(fieldLLType)) {
                String casted = temps.newTemp();
                llvm.append("  ").append(casted)
                        .append(" = bitcast ").append(rhsTy).append(" ").append(rhsVal)
                        .append(" to ").append(fieldLLType).append("\n");
                rhsVal = casted;
            }

            llvm.append("  store ").append(fieldLLType).append(" ").append(rhsVal)
                    .append(", ").append(fieldLLType).append("* ").append(fieldPtr).append("\n");
        }

        // aplica aninhados dentro do aninhado (pais { nome: ... })
        for (var nested : node.getNestedUpdates().entrySet()) {
            String field = nested.getKey();
            StructUpdateNode inner = nested.getValue();

            VariableDeclarationNode fieldDecl = findField(def, field);
            int fieldIndex = findFieldIndex(def, fieldDecl);

            String fieldPtr = temps.newTemp();
            llvm.append("  ").append(fieldPtr)
                    .append(" = getelementptr inbounds %").append(structName)
                    .append(", %").append(structName).append("* ").append(structVal)
                    .append(", i32 0, i32 ").append(fieldIndex).append("\n");

            String fieldLLType = mapFieldType(fieldDecl.getType());
            String fieldVal = temps.newTemp();
            llvm.append("  ").append(fieldVal)
                    .append(" = load ").append(fieldLLType)
                    .append(", ").append(fieldLLType).append("* ").append(fieldPtr).append("\n");

            llvm.append(emitNested(inner, fieldVal, fieldDecl.getType()));
        }

        return llvm.toString();
    }

    // utilitarios para auxiliar
    private VariableDeclarationNode findField(StructNode def, String name) {
        for (VariableDeclarationNode f : def.getFields())
            if (f.getName().equals(name)) return f;
        throw new RuntimeException("Campo não encontrado: " + name + " em struct " + def.getName());
    }

    private int findFieldIndex(StructNode def, VariableDeclarationNode target) {
        List<VariableDeclarationNode> fields = def.getFields();
        for (int i = 0; i < fields.size(); i++)
            if (fields.get(i) == target) return i;
        return -1;
    }

    private String mapFieldType(String type) {
        if (type.startsWith("Struct<") && type.endsWith(">"))
            return "%" + type.substring(7, type.length() - 1).trim() + "*";
        if (type.startsWith("Struct "))
            return "%" + type.substring("Struct ".length()).trim() + "*";
        return typeMapper.toLLVM(type);
    }

    private String extractTemp(String code) {
        int idx = code.lastIndexOf(";;VAL:");
        if (idx == -1) return "";
        return code.substring(idx + 6, code.indexOf(";;TYPE:", idx)).trim();
    }

    private String extractType(String code) {
        int idx = code.lastIndexOf(";;TYPE:");
        if (idx == -1) return "";
        return code.substring(idx + 7).trim();
    }
}