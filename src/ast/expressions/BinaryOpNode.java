package ast.expressions;

import ast.ASTNode;
import context.runtime.RuntimeContext;
import context.statics.StaticContext;
import context.statics.symbols.PrimitiveTypes;
import context.statics.symbols.Type;
import low.module.LLVMEmitVisitor;
import low.module.builders.LLVMValue;

import java.util.ArrayList;
import java.util.List;

public class BinaryOpNode extends ASTNode {

    public final ASTNode left;
    public final String operator;
    public final ASTNode right;

    private Type type;

    public BinaryOpNode(ASTNode left, String operator, ASTNode right) {
        this.left = left;
        this.operator = operator;
        this.right = right;
    }

    @Override
    public LLVMValue accept(LLVMEmitVisitor visitor) {
        return visitor.visit(this);
    }


    @Override
    public TypedValue evaluate(RuntimeContext ctx) {

        Object l = unwrap(left.evaluate(ctx));
        Object r = unwrap(right.evaluate(ctx));

        if (l instanceof Boolean lb && r instanceof Boolean rb) {
            return switch (operator) {
                case "&&" -> new TypedValue(PrimitiveTypes.BOOL, lb && rb);
                case "||" -> new TypedValue(PrimitiveTypes.BOOL, lb || rb);
                case "==" -> new TypedValue(PrimitiveTypes.BOOL, lb == rb);
                case "!=" -> new TypedValue(PrimitiveTypes.BOOL, lb != rb);
                default -> throw new RuntimeException("Operador inválido para boolean: " + operator);
            };
        }

        if (l instanceof Number ln && r instanceof Number rn) {

            if (ln instanceof Integer && rn instanceof Integer) {

                int li = ln.intValue();
                int ri = rn.intValue();

                return switch (operator) {
                    case "+" -> new TypedValue(PrimitiveTypes.INT, li + ri);
                    case "-" -> new TypedValue(PrimitiveTypes.INT, li - ri);
                    case "*" -> new TypedValue(PrimitiveTypes.INT, li * ri);
                    case "/" -> new TypedValue(PrimitiveTypes.INT, li / ri);

                    case ">" -> new TypedValue(PrimitiveTypes.BOOL, li > ri);
                    case "<" -> new TypedValue(PrimitiveTypes.BOOL, li < ri);
                    case ">=" -> new TypedValue(PrimitiveTypes.BOOL, li >= ri);
                    case "<=" -> new TypedValue(PrimitiveTypes.BOOL, li <= ri);
                    case "==" -> new TypedValue(PrimitiveTypes.BOOL, li == ri);
                    case "!=" -> new TypedValue(PrimitiveTypes.BOOL, li != ri);

                    default -> throw new RuntimeException("Operador inválido: " + operator);
                };
            }

            double lv = ln.doubleValue();
            double rv = rn.doubleValue();

            return switch (operator) {
                case "+" -> new TypedValue(PrimitiveTypes.DOUBLE, lv + rv);
                case "-" -> new TypedValue(PrimitiveTypes.DOUBLE, lv - rv);
                case "*" -> new TypedValue(PrimitiveTypes.DOUBLE, lv * rv);
                case "/" -> new TypedValue(PrimitiveTypes.DOUBLE, lv / rv);

                case ">" -> new TypedValue(PrimitiveTypes.BOOL, lv > rv);
                case "<" -> new TypedValue(PrimitiveTypes.BOOL, lv < rv);
                case ">=" -> new TypedValue(PrimitiveTypes.BOOL, lv >= rv);
                case "<=" -> new TypedValue(PrimitiveTypes.BOOL, lv <= rv);
                case "==" -> new TypedValue(PrimitiveTypes.BOOL, lv == rv);
                case "!=" -> new TypedValue(PrimitiveTypes.BOOL, lv != rv);

                default -> throw new RuntimeException("Operador inválido: " + operator);
            };
        }

        return switch (operator) {
            case "+" -> new TypedValue(PrimitiveTypes.STRING, l.toString() + r.toString());
            case "==" -> new TypedValue(PrimitiveTypes.BOOL, l.equals(r));
            case "!=" -> new TypedValue(PrimitiveTypes.BOOL, !l.equals(r));
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

        if (left != null) {
            left.setParent(this);
            left.bind(stx);
        }

        if (right != null) {
            right.setParent(this);
            right.bind(stx);
        }

        Type lt = left.getType();
        Type rt = right.getType();

        if (lt == null || rt == null) {

            System.out.println("ERRO EM BinaryOpNode: " + operator);

            System.out.println("Left node: " + left.getClass().getSimpleName());
            System.out.println("Right node: " + right.getClass().getSimpleName());

            System.out.println("Left type: " + lt);
            System.out.println("Right type: " + rt);

            throw new RuntimeException(
                    "Type resolution failed in BinaryOpNode (" + operator + ")"
            );
        }
        this.type = resolveType(lt, rt);
    }

    @Override
    public Type getType() {
        return type;
    }

    private Type resolveType(Type lt, Type rt) {

        if (operator.equals("+") || operator.equals("-") ||
                operator.equals("*") || operator.equals("/")) {

            if (lt == PrimitiveTypes.INT && rt == PrimitiveTypes.INT)
                return PrimitiveTypes.INT;

            if (lt.isNumeric() || rt.isNumeric()) {

                if (lt == PrimitiveTypes.DOUBLE || rt == PrimitiveTypes.DOUBLE)
                    return PrimitiveTypes.DOUBLE;

                if (lt == PrimitiveTypes.FLOAT || rt == PrimitiveTypes.FLOAT)
                    return PrimitiveTypes.FLOAT;

                return PrimitiveTypes.INT;
            }

            if (lt == PrimitiveTypes.STRING || rt == PrimitiveTypes.STRING)
                return PrimitiveTypes.STRING;
        }

        return switch (operator) {
            case ">", "<", ">=", "<=", "==", "!=", "&&", "||" -> PrimitiveTypes.BOOL;
            default -> throw new RuntimeException(
                    "Semantic error: incompatible types '" + lt + "' and '" + rt +
                            "' for operator '" + operator + "'"
            );
        };

    }

    private Object unwrap(TypedValue tv) {
        return tv.value();
    }
}