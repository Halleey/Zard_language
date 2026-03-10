package ast.variables;
import ast.ASTNode;
import ast.functions.FunctionCallNode;
import context.statics.StaticContext;

import context.runtime.RuntimeContext;
import ast.expressions.TypedValue;
import context.statics.symbols.*;
import context.statics.list.ListValue;
import low.module.LLVMEmitVisitor;

import java.util.LinkedHashMap;
import java.util.List;


public class VariableDeclarationNode extends ASTNode {
    public void setResolvedType(Type resolvedType) {
        this.resolvedType = resolvedType;
    }

    private final String name;
    private final Type declaredType;  // agora é Type em vez de String
    private final ASTNode initializer;

    public Type getDeclaredType() {
        return declaredType;
    }

    private Symbol symbol;
    private Type resolvedType;

    public VariableDeclarationNode(String name,
                                   Type declaredType,
                                   ASTNode initializer) {
        this.name = name;
        this.declaredType = declaredType;
        this.initializer = initializer;
    }

    public Type getResolvedType() {
        return resolvedType;
    }
    @Override
    public void bindChildren(StaticContext ctx) {
        if (declaredType != null) {
            resolvedType = ctx.resolveType(declaredType);
        } else if (initializer != null) {
            resolvedType = initializer.getType();
        } else {
            throw new RuntimeException("Cannot infer type for variable: " + name);
        }

        symbol = ctx.declareVariable(name, resolvedType);

        if (initializer != null) {
            initializer.bind(ctx);

            Type initType = (initializer instanceof FunctionCallNode call)
                    ? ctx.resolveFunction(call.getName()).getReturnType()
                    : initializer.getType();

            checkTypeCompatibility(resolvedType, initType);
        }
    }
    private void checkTypeCompatibility(Type declared, Type current) {

        if (declared.equals(current)) return;
        if(declared instanceof PrimitiveTypes && current instanceof InputType) return;

        if (declared instanceof ListType dl && current instanceof ListType cl) {
            if (dl.elementType().equals(cl.elementType())) return;
        }


        // conversões numéricas permitidas
        if (declared instanceof PrimitiveTypes dp &&
                current instanceof PrimitiveTypes cp) {

            if (dp == PrimitiveTypes.DOUBLE && cp == PrimitiveTypes.INT) return;
            if (dp == PrimitiveTypes.FLOAT  && cp == PrimitiveTypes.INT) return;
            if (dp == PrimitiveTypes.DOUBLE && cp == PrimitiveTypes.FLOAT) return;
            if(dp == PrimitiveTypes.FLOAT && cp == PrimitiveTypes.DOUBLE) return;
        }

        throw new RuntimeException(
                "Semantic error: cannot assign value of type '" +
                        current + "' to variable of type '" +
                        declared + "'"
        );
    }

    @Override
    public TypedValue evaluate(RuntimeContext ctx) {

        TypedValue value = (initializer == null)
                ? createDefaultValue()
                : initializer.evaluate(ctx);

        ctx.declareVariable(name, value);
        return value;
    }

    private TypedValue createDefaultValue() {

        if (resolvedType instanceof PrimitiveTypes p) {

            return switch (p.name()) {
                case "int" -> new TypedValue(p, 0);
                case "double" -> new TypedValue(p, 0.0);
                case "float" -> new TypedValue(p, 0.0f);
                case "string" -> new TypedValue(p, "");
                case "bool" -> new TypedValue(p, false);
                case "void" -> TypedValue.VOID;
                case "char" -> new TypedValue(p, '\0');
                default -> throw new RuntimeException("Unknown primitive type: " + p);
            };
        }

        if (resolvedType instanceof ListType listType) {
            return new TypedValue(
                    listType,
                    new ListValue(listType.elementType(), false)
            );
        }

        if (resolvedType instanceof StructType structType) {
            return new TypedValue(
                    structType,
                    new LinkedHashMap<String, TypedValue>()
            );
        }

        throw new RuntimeException(
                "Cannot create default value for type: " + resolvedType);
    }

    @Override
    public String accept(LLVMEmitVisitor visitor) {
        return visitor.visit(this);
    }

    @Override
    public boolean isStatement() {
        return true;
    }

    @Override
    public void print(String prefix) {
        System.out.println(prefix + "VarDecl: " + resolvedType + " " + name);
    }

    @Override
    public List<ASTNode> getChildren() {
        return initializer == null ? List.of() : List.of(initializer);
    }

    @Override
    public Type getType() {
        return resolvedType;
    }

    public String getName() {
        return name;
    }

    public ASTNode getInitializer() {
        return initializer;
    }

    public Symbol getSymbol() {
        return symbol;
    }
}