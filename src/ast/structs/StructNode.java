package ast.structs;

import ast.ASTNode;
import context.statics.ScopeKind;
import context.statics.StaticContext;
import context.statics.structs.StaticFields;
import context.statics.structs.StaticStructDefinition;
import ast.expressions.TypedValue;
import context.runtime.RuntimeContext;
import ast.variables.VariableDeclarationNode;
import context.statics.symbols.*;
import low.module.LLVMEmitVisitor;

import java.util.*;
import java.util.List;

public class StructNode extends ASTNode {

    private final String name;
    private final List<VariableDeclarationNode> fields;

    private String llvmName;
    private boolean shared = false;

    public StructNode(String name, List<VariableDeclarationNode> fields) {
        this.name = name;
        this.fields = fields;
        this.llvmName = name;
    }

    public boolean isShared() {
        return shared;
    }

    public void setShared(boolean shared) {
        this.shared = shared;
    }

    public String getName() {
        return name;
    }

    public String getLLVMName() {
        return llvmName;
    }

    public void setLLVMName(String llvmName) {
        this.llvmName = llvmName;
    }

    public List<VariableDeclarationNode> getFields() {
        return fields;
    }


    @Override
    public String accept(LLVMEmitVisitor visitor) {
        return visitor.visit(this);
    }

    @Override
    public void bindChildren(StaticContext parent) {

        StaticContext structCtx = new StaticContext(ScopeKind.STRUCT, parent);

        Set<String> seen = new HashSet<>();
        for (VariableDeclarationNode f : fields) {
            if (!seen.add(f.getName())) {
                throw new RuntimeException(
                        "Campo duplicado no struct " + name + ": " + f.getName()
                );
            }
            f.bindChildren(structCtx);
        }

        List<StaticFields> staticFields = new ArrayList<>();
        int index = 0;
        int offset = 0;

        for (VariableDeclarationNode f : fields) {

            Type resolvedType;

            if (f.getDeclaredType() != null) {
                resolvedType = structCtx.resolveType(f.getDeclaredType());
            }
            else if (f.getInitializer() != null) {
                resolvedType = f.getInitializer().getType();
            }
            else {
                throw new RuntimeException(
                        "Cannot infer type for field: " + f.getName()
                );
            }

            f.setResolvedType(resolvedType);

            staticFields.add(
                    new StaticFields(
                            f.getName(),
                            resolvedType,
                            index++,
                            offset
                    )
            );

            offset += estimateSize(resolvedType);
        }

        StaticStructDefinition def = new StaticStructDefinition(name, staticFields, shared);

        parent.declareStruct(name, def);
    }

    private int estimateSize(Type type) {

        if (type instanceof PrimitiveTypes p) {
            return switch (p.name()) {
                case "int" -> 4;
                case "double" -> 8;
                case "float" -> 4;
                case "boolean", "bool" -> 1;
                case "char" -> 1;
                case "string" -> 8;
                default -> 8;
            };
        }

        if (type instanceof StructType) return 8; // ponteiro
        if (type instanceof ListType) return 8;   // ponteiro

        throw new IllegalStateException("Tipo desconhecido: " + type);
    }

    @Override
    public TypedValue evaluate(RuntimeContext ctx) {
        ctx.registerStructType(name, fields);
        return null;
    }

    @Override
    public void print(String prefix) {
        System.out.println(prefix + "Struct " + name);
        for (VariableDeclarationNode f : fields) {
            System.out.println(prefix + "  " + f.getName() + " : " + f.getType());
        }
    }

    public StructNode cloneWithType(Type elemType) {

        List<VariableDeclarationNode> clonedFields = new ArrayList<>();

        for (VariableDeclarationNode f : fields) {

            Type originalType = f.getType();
            Type newType = originalType;

            if (originalType instanceof ListType listType && listType.elementType() instanceof UnknownType) {

                newType = new ListType(elemType, listType.isReference());
            }

            clonedFields.add(
                    new VariableDeclarationNode(
                            f.getName(),
                            newType,
                            f.getInitializer()
                    )
            );
        }
        StructNode clone = new StructNode(
                name + "_" + elemType,
                clonedFields
        );

        clone.setLLVMName(name + "_" + elemType);

        return clone;
    }

    public int getLLVMSizeBytes() {

        int size = 0;

        for (VariableDeclarationNode field : fields) {
            size += llvmSizeOf(field.getType());
        }

        return size;
    }

    private int llvmSizeOf(Type t) {

        if (t instanceof PrimitiveTypes p) {
            return switch (p.name()) {
                case "int" -> 4;
                case "double" -> 8;
                case "float" -> 4;
                case "boolean" -> 1;
                case "string" -> 8;
                default -> 8;
            };
        }

        if (t instanceof ListType) return 8;

        if (t instanceof StructType) return 8;

        return 8;
    }
}