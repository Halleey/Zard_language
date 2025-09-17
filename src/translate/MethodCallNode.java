package translate;

import ast.ASTNode;
import ast.runtime.RuntimeContext;
import expressions.DynamicList;
import expressions.TypedValue;

import java.util.List;
import java.util.Map;

public class MethodCallNode extends ASTNode {
    private final String objName;
    private final String method;
    private final List<ASTNode> args;

    public MethodCallNode(String objName, String method, List<ASTNode> args) {
        this.objName = objName;
        this.method = method;
        this.args = args;
    }

    @Override
    public TypedValue evaluate(RuntimeContext ctx) {
        TypedValue obj = ctx.getVariable(objName);
        if (obj == null) {
            throw new RuntimeException("Variável não encontrada: " + objName);
        }

        switch (obj.getType()) {
            case "list" -> {
                if (!obj.getType().equals("list")) {
                    throw new RuntimeException(objName + " não é uma lista");
                }
                DynamicList list = (DynamicList) obj.getValue();
                return evaluateList(obj, ctx);
            }
//            case "string" -> {
//                String str = (String) obj.getValue();
//                return evaluateString(str, ctx);
//            }
//            case "map" -> {
//                Map<String, TypedValue> map = obj.asMap();
//                return evaluateMap(map, ctx);
//            }
            default -> throw new RuntimeException(
                    "Tipo " + obj.getType() + " não suporta métodos: " + objName
            );
        }
    }

    private TypedValue evaluateList(TypedValue obj, RuntimeContext ctx) {
        DynamicList list = (DynamicList) obj.getValue();

        switch (method) {
            case "add" -> {
                if (args.size() != 1) throw new RuntimeException("add requer 1 argumento");
                TypedValue val = args.get(0).evaluate(ctx);
                list.add(val);
                return new TypedValue("list", list);
            }
            case "addAll" -> {
                if (args.isEmpty()) throw new RuntimeException("addAll requer pelo menos 1 argumento");

                for (ASTNode argNode : args) {
                    TypedValue val = argNode.evaluate(ctx);

                    if (val.getType().equals("list")) {
                        // Se for uma lista, adiciona todos os elementos
                        DynamicList other = (DynamicList) val.getValue();
                        for (TypedValue item : other.evaluate(ctx)) { // <--- aqui
                            list.add(item);
                        }
                    } else {
                        // Se for um único elemento, adiciona diretamente
                        list.add(val);
                    }
                }

                return new TypedValue("list", list);
            }

            case "remove" -> {
                if (args.size() != 1) throw new RuntimeException("remove requer 1 argumento");
                TypedValue val = args.get(0).evaluate(ctx);

                // extrai int do TypedValue
                int index = ((Number) val.getValue()).intValue();

                list.removeByIndex(index, ctx); // agora funciona
                return new TypedValue("list", list);
            }

            case "clear" -> {
                list.clear();
                return new TypedValue("list", list);
            }
            case "size" -> new TypedValue("int", list.size());
            default -> throw new RuntimeException("Método de lista inválido: " + method);
        }
        return obj;
    }

//    private TypedValue evaluateString(String str, RuntimeContext ctx) {
//        switch (method) {
//            case "length" -> new TypedValue("int", str.length());
//            case "upper" -> new TypedValue("string", str.toUpperCase());
//            case "lower" -> new TypedValue("string", str.toLowerCase());
//            default -> throw new RuntimeException("Método de string inválido: " + method);
//        }
//    }
//
//    private TypedValue evaluateMap(Map<String, TypedValue> map, RuntimeContext ctx) {
//        switch (method) {
//            case "put" -> {
//                if (args.size() != 2) throw new RuntimeException("put requer 2 argumentos");
//                String key = args.get(0).evaluate(ctx).getValue().toString();
//                TypedValue val = args.get(1).evaluate(ctx);
//                map.put(key, val);
//                return new TypedValue("map", map);
//            }
//            case "get" -> {
//                if (args.size() != 1) throw new RuntimeException("get requer 1 argumento");
//                String key = args.get(0).evaluate(ctx).getValue().toString();
//                return map.getOrDefault(key, new TypedValue("null", null));
//            }
//            default -> throw new RuntimeException("Método de map inválido: " + method);
//        }
//    }

    @Override
    public void print(String prefix) {
        System.out.println(prefix + "MethodCall: " + objName + "." + method + "(" + args.size() + " args)");
    }
}
