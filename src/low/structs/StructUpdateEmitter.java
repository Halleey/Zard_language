package low.structs;

import ast.ASTNode;
import ast.structs.StructNode;
import ast.structs.StructUpdateNode;
import ast.variables.VariableDeclarationNode;
import context.statics.symbols.*;
import low.TempManager;
import low.functions.TypeMapper;
import low.module.LLVisitorMain;
import low.module.builders.LLVMPointer;
import low.module.builders.LLVMTYPES;
import low.module.builders.LLVMValue;
import low.module.builders.structs.LLVMStruct;

import java.util.List;
public class StructUpdateEmitter {

    private final TempManager temps;
    private final LLVisitorMain visitor;
    private final TypeMapper typeMapper = new TypeMapper();

    public StructUpdateEmitter(TempManager temps, LLVisitorMain visitor) {
        this.temps = temps;
        this.visitor = visitor;
    }

    public LLVMValue emit(StructUpdateNode node) {
        StringBuilder llvm = new StringBuilder();

        // LLVMValue do struct alvo
        LLVMValue targetVal = node.getTargetStruct().accept(visitor);
        llvm.append(targetVal.getCode());

        LLVMValue structVal = targetVal;
        LLVMTYPES structType = targetVal.getType();

        // se i8*, bitcast para struct real
        if (structType instanceof LLVMPointer && structType.toString().equals("i8*")) {
            String ownerName = visitor.resolveStructName(node.getTargetStruct());
            String realLLVMTypeStr = "%" + ownerName + "*";
            LLVMValue casted = new LLVMValue(
                    new LLVMStruct(ownerName),
                    temps.newTemp(),
                    llvm.toString() + "  " + temps.newTemp() + " = bitcast i8* " + structVal.getName()
                            + " to " + realLLVMTypeStr + "\n"
            );
            llvm.append(casted.getCode());
            structVal = casted;
            structType = casted.getType();
        }

        String ownerName = visitor.resolveStructName(node.getTargetStruct());
        StructNode def = visitor.getStructNode(ownerName);
        if (def == null) throw new RuntimeException("Struct não encontrada: " + ownerName);

        // atualizar campos simples
        for (var entry : node.getFieldUpdates().entrySet()) {
            String fieldName = entry.getKey();
            ASTNode expr = entry.getValue();
            LLVMValue rhsVal = expr.accept(visitor);
            llvm.append(rhsVal.getCode());

            VariableDeclarationNode fieldDecl = findField(def, fieldName);
            int fieldIndex = findFieldIndex(def, fieldDecl);
            LLVMTYPES fieldLLVMType = TypeMapper.from(fieldDecl.getType());

            // ponteiro para o campo
            String fieldPtrName = temps.newTemp();
            llvm.append("  ").append(fieldPtrName)
                    .append(" = getelementptr inbounds %").append(ownerName)
                    .append(", %").append(ownerName).append("* ").append(structVal.getName())
                    .append(", i32 0, i32 ").append(fieldIndex).append("\n");

            LLVMValue fieldPtr = new LLVMValue(new LLVMPointer(fieldLLVMType), fieldPtrName, "");

            // bitcast se tipos diferentes
            LLVMValue storeVal = rhsVal;
            if (!rhsVal.getType().toString().equals(fieldLLVMType.toString())) {
                String castName = temps.newTemp();
                llvm.append("  ").append(castName)
                        .append(" = bitcast ")
                        .append(rhsVal.getType()).append(" ").append(rhsVal.getName())
                        .append(" to ").append(fieldLLVMType).append("\n");
                storeVal = new LLVMValue(fieldLLVMType, castName, "");
            }

            // store
            llvm.append("  store ")
                    .append(fieldLLVMType).append(" ")
                    .append(storeVal.getName())
                    .append(", ").append(fieldLLVMType).append("* ")
                    .append(fieldPtr.getName())
                    .append("\n");
        }

        // atualizar campos aninhados recursivamente
        for (var nested : node.getNestedUpdates().entrySet()) {
            String fieldName = nested.getKey();
            StructUpdateNode inner = nested.getValue();

            VariableDeclarationNode fieldDecl = findField(def, fieldName);
            int fieldIndex = findFieldIndex(def, fieldDecl);
            LLVMTYPES fieldLLVMType = TypeMapper.from(fieldDecl.getType());

            // ponteiro para campo
            String fieldPtrName = temps.newTemp();
            llvm.append("  ").append(fieldPtrName)
                    .append(" = getelementptr inbounds %").append(ownerName)
                    .append(", %").append(ownerName).append("* ").append(structVal.getName())
                    .append(", i32 0, i32 ").append(fieldIndex).append("\n");

            // load campo
            String fieldLoadName = temps.newTemp();
            llvm.append("  ").append(fieldLoadName)
                    .append(" = load ").append(fieldLLVMType)
                    .append(", ").append(fieldLLVMType).append("* ").append(fieldPtrName).append("\n");

            LLVMValue fieldVal = new LLVMValue(fieldLLVMType, fieldLoadName, "");

            // chama recursivamente
            StructUpdateEmitter subEmitter = new StructUpdateEmitter(temps, visitor);
            LLVMValue nestedVal = subEmitter.emitNested(inner, fieldVal, fieldDecl.getType());
            llvm.append(nestedVal.getCode());
        }

        return new LLVMValue(structType, structVal.getName(), llvm.toString());
    }

    private LLVMValue emitNested(StructUpdateNode node, LLVMValue structVal, Type structType) {

        StringBuilder llvm = new StringBuilder();

        if (!(structType instanceof StructType struct)) {
            throw new RuntimeException("Nested update em tipo não-struct: " + structType);
        }

        String structName = struct.name();
        StructNode def = visitor.getStructNode(structName);

        for (var entry : node.getFieldUpdates().entrySet()) {
            String fieldName = entry.getKey();
            ASTNode expr = entry.getValue();
            LLVMValue rhsVal = expr.accept(visitor);
            llvm.append(rhsVal.getCode());

            VariableDeclarationNode fieldDecl = findField(def, fieldName);
            int fieldIndex = findFieldIndex(def, fieldDecl);
            LLVMTYPES fieldLLVMType = TypeMapper.from(fieldDecl.getType());

            String fieldPtrName = temps.newTemp();
            llvm.append("  ").append(fieldPtrName)
                    .append(" = getelementptr inbounds %").append(structName)
                    .append(", %").append(structName).append("* ").append(structVal.getName())
                    .append(", i32 0, i32 ").append(fieldIndex).append("\n");

            LLVMValue fieldPtr = new LLVMValue(new LLVMPointer(fieldLLVMType), fieldPtrName, "");

            LLVMValue storeVal = rhsVal;
            if (!rhsVal.getType().toString().equals(fieldLLVMType.toString())) {
                String castName = temps.newTemp();
                llvm.append("  ").append(castName)
                        .append(" = bitcast ").append(rhsVal.getType()).append(" ")
                        .append(rhsVal.getName())
                        .append(" to ").append(fieldLLVMType).append("\n");
                storeVal = new LLVMValue(fieldLLVMType, castName, "");
            }

            llvm.append("  store ")
                    .append(fieldLLVMType).append(" ").append(storeVal.getName())
                    .append(", ").append(fieldLLVMType).append("* ").append(fieldPtr.getName())
                    .append("\n");
        }

        // nested dentro de nested
        for (var nested : node.getNestedUpdates().entrySet()) {
            String fieldName = nested.getKey();
            StructUpdateNode inner = nested.getValue();

            VariableDeclarationNode fieldDecl = findField(def, fieldName);
            int fieldIndex = findFieldIndex(def, fieldDecl);
            LLVMTYPES fieldLLVMType = TypeMapper.from(fieldDecl.getType());

            String fieldPtrName = temps.newTemp();
            llvm.append("  ").append(fieldPtrName)
                    .append(" = getelementptr inbounds %").append(structName)
                    .append(", %").append(structName).append("* ").append(structVal.getName())
                    .append(", i32 0, i32 ").append(fieldIndex).append("\n");

            String fieldLoadName = temps.newTemp();
            llvm.append("  ").append(fieldLoadName)
                    .append(" = load ").append(fieldLLVMType)
                    .append(", ").append(fieldLLVMType).append("* ").append(fieldPtrName).append("\n");

            LLVMValue fieldVal = new LLVMValue(fieldLLVMType, fieldLoadName, "");

            LLVMValue nestedVal = emitNested(inner, fieldVal, fieldDecl.getType());
            llvm.append(nestedVal.getCode());
        }

        return new LLVMValue(new LLVMStruct(structType.toString()), structVal.getName(), llvm.toString());
    }

    // utilitários
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
}