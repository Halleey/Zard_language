package ast.structs;

import ast.ASTNode;
import context.statics.StaticContext;
import ast.expressions.TypedValue;
import ast.functions.FunctionNode;
import ast.functions.ParamInfo;
import context.runtime.RuntimeContext;
import ast.variables.VariableNode;
import context.statics.symbols.*;
import low.module.LLVMEmitVisitor;

import java.util.ArrayList;
import java.util.List;

public class StructMethodCallNode extends ASTNode {

    private final ASTNode structInstance;
    private final String structName;
    private final String methodName;
    private final List<ASTNode> args;

    private Type returnType;

    public void setReturnType(Type returnType) {
        this.returnType = returnType;
    }

    public Type getReturnType() {
        return returnType;
    }

    public StructMethodCallNode(ASTNode structInstance,
                                String structName,
                                String methodName,
                                List<ASTNode> args) {

        this.structInstance = structInstance;
        this.structName = structName;
        this.methodName = methodName;
        this.args = args;
    }

    public ASTNode getStructInstance() {
        return structInstance;
    }

    public String getStructName() {
        return structName;
    }

    public String getMethodName() {
        return methodName;
    }

    public List<ASTNode> getArgs() {
        return args;
    }

    @Override
    public String accept(LLVMEmitVisitor visitor) {
        return visitor.visit(this);
    }

    @Override
    public TypedValue evaluate(RuntimeContext ctx) {

        ASTNode instanceExpr = structInstance;

        String base = structName.contains("<")
                ? structName.substring(0, structName.indexOf('<'))
                : structName;

        FunctionNode method = ctx.getStructMethod(base, methodName);

        if (method == null) {
            throw new RuntimeException(
                    "Method " + methodName + " not found in Struct " + structName
            );
        }

        List<ASTNode> callArgs = new ArrayList<>();

        List<ParamInfo> params = method.getParameters();

        if (!params.isEmpty()) {

            Type firstType = params.get(0).type();

            if (firstType instanceof StructType structType) {

                if (structType.name().equals(base)) {
                    callArgs.add(instanceExpr);
                }

            }
        }

        if (args != null) {
            callArgs.addAll(args);
        }

        return method.invoke(ctx, callArgs);
    }


    @Override
    public void print(String prefix) {

        System.out.println(prefix + "StructMethodCall:");
        System.out.println(prefix + "  Struct: " + structName);
        System.out.println(prefix + "  Method: " + methodName);

        if (!args.isEmpty()) {

            System.out.println(prefix + "  Args:");

            for (ASTNode a : args)
                a.print(prefix + "    ");
        }
    }
    @Override
    public void bindChildren(StaticContext stx) {

        structInstance.bind(stx);

        if (args != null) {
            for (ASTNode arg : args) {
                arg.bind(stx);
            }
        }

        FunctionNode fn = stx.getStructMethod(structName, methodName);

        if (fn == null) {
            throw new RuntimeException(
                    "Método " + methodName + " não encontrado em " + structName
            );
        }

        List<ParamInfo> params = fn.getParameters();

        // ignora primeiro parametro se for o receiver (s: maps)
        int paramOffset = 0;

        if (!params.isEmpty()) {
            Type first = params.get(0).type();

            if (first instanceof StructType st && st.name().equals(structName)) {
                paramOffset = 1;
            }
        }

        int expectedArgs = params.size() - paramOffset;
        int providedArgs = args == null ? 0 : args.size();

        if (expectedArgs != providedArgs) {
            throw new RuntimeException(
                    "Número de argumentos inválido em " + methodName +
                            ": esperado " + expectedArgs +
                            ", recebido " + providedArgs
            );
        }

        for (int i = 0; i < providedArgs; i++) {

            Type expected = params.get(i + paramOffset).type();
            Type actual = args.get(i).getType();
            System.out.println("currently token for debug " + expected );
            System.out.println("currently token for debug " + actual );
            checkTypeCompatibility(expected, actual);
        }

        returnType = fn.getReturnType();
    }

    protected void checkTypeCompatibility(Type declared, Type current) {

        if (declared instanceof StructType d && current instanceof StructType c) {
            if (!d.name().equals(c.name())) {
                throw new RuntimeException(
                        "Type mismatch: expected " + d.name() + " but got " + c.name()
                );
            }
            return;
        }

        if (declared instanceof PrimitiveTypes dp && current instanceof PrimitiveTypes cp) {

            String d = dp.name();
            String c = cp.name();

            if (d.equals(c)) return;

            if (d.equals("double") && c.equals("int")) return;
            if (d.equals("float") && c.equals("int")) return;
            if (d.equals("double") && c.equals("float")) return;
            if (d.equals("float") && c.equals("double")) return;
        }

        if (current instanceof InputType) return;

        if(declared instanceof ListType dl && current instanceof ListType cl) {
            Type expectedElement  = dl.elementType();
            Type currentlyElement  = cl.elementType();
            checkTypeCompatibility(expectedElement, currentlyElement);
            return;
        }

        throw new RuntimeException(
                "Semantic error: cannot assign value of type '" +
                        current + "' to variable of type '" +
                        declared + "'"
        );
    }

    @Override
    public Type getType() {
        return returnType;
    }
}