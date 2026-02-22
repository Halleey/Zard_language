package ast.expressions;

import ast.ASTNode;
import context.runtime.RuntimeContext;
import context.statics.StaticContext;
import low.module.LLVMEmitVisitor;

import java.util.ArrayList;
import java.util.List;

public class BinaryOpNode extends ASTNode {
    public final ASTNode left;
    public final String operator;
    public final ASTNode right;
    private String type;


    public BinaryOpNode(ASTNode left, String operator, ASTNode right) {
        this.left = left;
        this.operator = operator;
        this.right = right;
    }

    @Override
    public String accept(LLVMEmitVisitor visitor) {
        return visitor.visit(this);
    }

    @Override
    public TypedValue evaluate(RuntimeContext ctx) {
        Object l = unwrap(left.evaluate(ctx));
        Object r = unwrap(right.evaluate(ctx));

        if (l instanceof Boolean lb && r instanceof Boolean rb) {
            return switch (operator) {
                case "&&" -> new TypedValue("boolean", lb && rb);
                case "||" -> new TypedValue("boolean", lb || rb);
                case "==" -> new TypedValue("boolean", lb == rb);
                case "!=" -> new TypedValue("boolean", lb != rb);
                default -> throw new RuntimeException("Operador inválido para boolean: " + operator);
            };
        }

        if (l instanceof Number ln && r instanceof Number rn) {
            // int + int  int
            if (ln instanceof Integer && rn instanceof Integer) {
                int li = ln.intValue();
                int ri = rn.intValue();
                return switch (operator) {
                    case "+" -> new TypedValue("int", li + ri);
                    case "-" -> new TypedValue("int", li - ri);
                    case "*" -> new TypedValue("int", li * ri);
                    case "/" -> new TypedValue("int", li / ri);
                    case ">" -> new TypedValue("boolean", li > ri);
                    case "<" -> new TypedValue("boolean", li < ri);
                    case ">=" -> new TypedValue("boolean", li >= ri);
                    case "<=" -> new TypedValue("boolean", li <= ri);
                    case "==" -> new TypedValue("boolean", li == ri);
                    case "!=" -> new TypedValue("boolean", li != ri);
                    default -> throw new RuntimeException("Operador inválido: " + operator);
                };
            }

            double lv = ln.doubleValue();
            double rv = rn.doubleValue();
            return switch (operator) {
                case "+" -> new TypedValue("double", lv + rv);
                case "-" -> new TypedValue("double", lv - rv);
                case "*" -> new TypedValue("double", lv * rv);
                case "/" -> new TypedValue("double", lv / rv);
                case ">" -> new TypedValue("boolean", lv > rv);
                case "<" -> new TypedValue("boolean", lv < rv);
                case ">=" -> new TypedValue("boolean", lv >= rv);
                case "<=" -> new TypedValue("boolean", lv <= rv);
                case "==" -> new TypedValue("boolean", lv == rv);
                case "!=" -> new TypedValue("boolean", lv != rv);
                default -> throw new RuntimeException("Operador inválido: " + operator);
            };
        }

        return switch (operator) {
            case "+" -> new TypedValue("string", l.toString() + r.toString());
            case "==" -> new TypedValue("boolean", l.equals(r));
            case "!=" -> new TypedValue("boolean", !l.equals(r));
            default -> throw new RuntimeException("Tipos incompatíveis para operador: " + operator);
        };
    }



    @Override
    public void print(String prefix) {
        System.out.println(prefix + "BinaryOp (" + operator + "):");
        System.out.println(prefix + "  Left:");
        left.print(prefix + "    ");
        System.out.println(prefix + "  Right:");
        right.print(prefix + "    ");
    }

    @Override
    public List<ASTNode> getChildren() {
        List<ASTNode> list = new ArrayList<>();
        if (left != null) list.add(left);
        if (right != null) list.add(right);
        return list;
    }

    @Override
    public void bindChildren(StaticContext stx) {

        left.setParent(this);
        left.bind(stx);

        right.setParent(this);
        right.bind(stx);

        String lt = left.getType();
        String rt = right.getType();

        if (lt == null || rt == null) {
            throw new RuntimeException("BinaryOp with null operand type: " + operator);
        }

        this.type = resolveType(lt, rt);
    }

    private String resolveType(String lt, String rt) {

        switch (operator) {

            case "+":
            case "-":
            case "*":
            case "/":

                if (lt.equals("int") && rt.equals("int"))
                    return "int";

                if (lt.equals("double") || rt.equals("double"))
                    return "double";

                if (lt.equals("float") || rt.equals("float"))
                    return "float";

                if (lt.equals("string") || rt.equals("string"))
                    return "string";

                break;

            case ">":
            case "<":
            case ">=":
            case "<=":
            case "==":
            case "!=":
            case "&&":
            case "||":
                return "boolean";
        }

        throw new RuntimeException(
                "Semantic error: incompatible types '" + lt +
                        "' and '" + rt + "' for operator '" + operator + "'"
        );
    }

    @Override
    public String getType() {
        return type;
    }

    private Object unwrap(TypedValue tv) {
        return tv.value();
    }
}
