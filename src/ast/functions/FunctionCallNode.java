package ast.functions;

import ast.ASTNode;
import context.runtime.RuntimeContext;
import context.statics.StaticContext;
import ast.expressions.TypedValue;
import context.statics.symbols.InputType;
import context.statics.symbols.PrimitiveTypes;
import context.statics.symbols.Type;
import low.module.LLVMEmitVisitor;

import java.util.List;


public class FunctionCallNode extends ASTNode {
    private final String name;
    private final List<ASTNode> args;
    private Type type;
    public FunctionCallNode(String name, List<ASTNode> args) {
        this.name = name;
        this.args = args;
    }

    @Override
    public String accept(LLVMEmitVisitor visitor) {
        return visitor.visit(this);
    }

    @Override
    public TypedValue evaluate(RuntimeContext ctx) {
        RuntimeContext currentCtx = ctx;

        // permite chamadas como "math.pow" navegando por namespaces
        String[] parts = name.split("\\.");
        for (int i = 0; i < parts.length - 1; i++) {
            String nsName = parts[i];
            TypedValue nsVal = currentCtx.getVariable(nsName);

            if (!nsVal.isNamespace()) {
                throw new RuntimeException(nsName + " não é um namespace");
            }

            currentCtx = (RuntimeContext) nsVal.value();
        }

        String funcShortName = parts[parts.length - 1];
        TypedValue funcVal = currentCtx.getVariable(funcShortName);

        if (!funcVal.isFunction()) {
            throw new RuntimeException(funcShortName + " não é uma função");
        }

        FunctionNode func = funcVal.getFunction();
        return func.invoke(currentCtx, args);
    }

    @Override
    public void print(String prefix) {
        System.out.println(prefix + "FunctionCall " + name);
        if (!args.isEmpty()) {
            System.out.println(prefix + "  Args ");
            for (ASTNode arg : args) arg.print(prefix + "    ");
        } else {
            System.out.println(prefix + "  <no args>");
        }
    }
    @Override
    public void bindChildren(StaticContext stx) {

        for (ASTNode arg : args) {
            arg.setParent(this);
            arg.bind(stx);
        }

        String[] parts = name.split("\\.");
        String funcName = parts[parts.length - 1];

        FunctionNode fn = stx.resolveFunction(funcName);

        if (fn == null) {
            throw new RuntimeException("Função não declarada: " + name);
        }

        List<ParamInfo> params = fn.getParameters();

        if (args.size() != params.size()) {
            throw new RuntimeException(
                    "Número de argumentos inválido em " + funcName +
                            ": esperado " + params.size() +
                            ", recebido " + args.size()
            );
        }
        for (int i = 0; i < args.size(); i++) {

            Type expected = params.get(i).typeObj();
            Type actual = args.get(i).getType();

          checkCompatibility(expected, actual);
        }

        this.type = fn.getReturnType();
    }

    private void checkCompatibility(Type expected, Type current) {

        if (expected instanceof PrimitiveTypes dp && current instanceof PrimitiveTypes cp) {

            if (dp.name().equals(cp.name())) return;

            if (dp.name().equals("double") && cp.name().equals("int")) return;
            if (dp.name().equals("float")  && cp.name().equals("int")) return;
            if (dp.name().equals("double") && cp.name().equals("float")) return;
            if (dp.name().equals("float") && cp.name().equals("double")) return;
        }

        if (current instanceof InputType) return;

        throw new RuntimeException(
                "Semantic error: cannot assign value of type '" +
                        current + "' to variable of type '" +
                        expected + "'"
        );
    }


    @Override
    public Type getType() {
        return type;
    }

    public String getName() { return name; }
    public List<ASTNode> getArgs() { return args; }
}
