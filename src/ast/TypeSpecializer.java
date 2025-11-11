package ast;

import ast.*;
import ast.functions.FunctionNode;
import ast.lists.*;
import ast.structs.*;
import ast.variables.*;
import low.module.LLVisitorMain;

import java.util.*;

public class TypeSpecializer {

    private final Map<StructInstaceNode, String> inferredStructTypes = new IdentityHashMap<>();
    private final Map<String, StructInstaceNode> structVars = new HashMap<>();
    private final Map<String, String> variableTypes = new HashMap<>();

    private LLVisitorMain visitor;

    public void setVisitor(LLVisitorMain visitor) {
        this.visitor = visitor;
    }

    public void specialize(List<ASTNode> ast) {
        collectInferences(ast);
        applySpecializations(ast);
        propagateInferredTypes(ast);

        if (!inferredStructTypes.isEmpty()) {
            System.out.println("=== [TypeSpecializer] Tipos inferidos ===");
            for (var entry : inferredStructTypes.entrySet()) {
                StructInstaceNode node = entry.getKey();
                String elemType = entry.getValue();
                System.out.println("→ Struct<" + node.getName() + "> (" + findVarName(node) + ") : " + elemType);
            }
            System.out.println("=========================================");
        } else {
            System.out.println("[TypeSpecializer] Nenhum tipo inferido.");
        }
    }

    private void collectInferences(List<ASTNode> ast) {
        for (ASTNode node : ast) {

            if (node instanceof VariableDeclarationNode decl) {
                variableTypes.put(decl.getName(), decl.getType());
                if (decl.getInitializer() instanceof StructInstaceNode si) {
                    structVars.put(decl.getName(), si);
                }
            }

            if (node instanceof StructInstaceNode structNode) {
                Map<String, ASTNode> named = structNode.getNamedValues();
                if (named != null && !named.isEmpty()) {
                    ASTNode dataInit = named.get("data");
                    if (dataInit instanceof ListNode list) {
                        String elemType = extractElementType(list.getType());
                        if (elemType != null) {
                            registerStructInference(structNode, elemType, "inicialização explícita");
                        }
                    }
                }
            }

            if (node instanceof StructMethodCallNode call) {
                String varName = call.getReceiverName();
                ASTNode arg = call.getArgs().isEmpty() ? null : call.getArgs().get(0);
                if (arg != null && varName != null) {
                    String inferredType = inferTypeFromArgument(arg);
                    StructInstaceNode target = structVars.get(varName);
                    if (target != null && inferredType != null) {
                        if (!inferredStructTypes.containsKey(target)) {
                            registerStructInference(target, inferredType,
                                    "primeiro uso (chamada " + varName + ".add)");
                        } else {
                            String current = inferredStructTypes.get(target);
                            if (!current.equals(inferredType)) {
                                System.out.println("[TypeSpecializer][Aviso] Conflito de tipos para '" + varName +
                                        "': já era " + current + ", recebeu " + inferredType);
                            }
                        }
                    }
                }
            }

            // Recursão
            for (ASTNode child : node.getChildren()) {
                collectInferences(Collections.singletonList(child));
            }
        }
    }

    private void registerStructInference(StructInstaceNode node, String elemType, String origem) {
        inferredStructTypes.put(node, elemType);
        node.setConcreteType("Struct<" + node.getName() + "<" + elemType + ">>");

        System.out.println("[TypeSpecializer] Struct<" + node.getName() + "> inferida como " +
                elemType + " via " + origem);

        if (visitor != null) {
            StructNode base = visitor.getStructNode(node.getName());
            if (base != null) {
                visitor.getOrCreateSpecializedStruct(base, elemType);
            } else {
                System.out.println("[TypeSpecializer][Aviso] Struct base '" + node.getName() + "' não encontrada no visitor.");
            }
        }
    }

    private String extractElementType(String type) {
        if (type == null || type.equals("?")) return null;
        if (type.startsWith("List<") && type.endsWith(">"))
            return type.substring(5, type.length() - 1);
        return type;
    }

    private String inferTypeFromArgument(ASTNode arg) {
        if (arg instanceof VariableNode var) {
            return variableTypes.get(var.getName());
        }
        if (arg instanceof StructInstaceNode s) {
            return "Struct<" + s.getName() + ">";
        }
        if (arg instanceof ListNode list) {
            return extractElementType(list.getType());
        }
        if (arg instanceof ast.variables.LiteralNode lit) {
            return lit.getValue().type();
        }
        return null;
    }

    private String findVarName(StructInstaceNode node) {
        for (Map.Entry<String, StructInstaceNode> e : structVars.entrySet()) {
            if (e.getValue() == node) return e.getKey();
        }
        return "?";
    }

    private void applySpecializations(List<ASTNode> ast) {
        for (ASTNode node : ast) {
            if (node instanceof FunctionNode fn) {
                specializeFunction(fn);
            }
            for (ASTNode child : node.getChildren()) {
                applySpecializations(Collections.singletonList(child));
            }
        }
    }

    private void specializeFunction(FunctionNode fn) {
        for (int i = 0; i < fn.getParamTypes().size(); i++) {
            String t = fn.getParamTypes().get(i);
            if ("?".equals(t)) {
                String inferred = inferTypeFromBody(fn.getBody());
                if (inferred != null) {
                    fn.getParamTypes().set(i, inferred);
                    System.out.println("[TypeSpecializer] Parâmetro " + i + " da função " + fn.getName() +
                            " especializado como " + inferred);
                }
            }
        }

        if ("?".equals(fn.getReturnType())) {
            String ret = inferTypeFromBody(fn.getBody());
            if (ret != null) {
                fn.setReturnType(ret);
                System.out.println("[TypeSpecializer] Retorno da função " + fn.getName() +
                        " especializado como " + ret);
            }
        }
    }

    private String inferTypeFromBody(List<ASTNode> body) {
        for (ASTNode stmt : body) {
            if (stmt instanceof ListAddNode add) {
                if (add.getElementType() != null && !add.getElementType().equals("?")) {
                    return add.getElementType();
                }
            }
            if (stmt instanceof VariableNode v) {
                String type = variableTypes.get(v.getName());
                if (type != null) return type;
            }
            if (stmt instanceof StructFieldAccessNode sfa) {
                String f = sfa.getFieldName();
                if (f != null && !f.equals("?")) return f;
            }
        }
        return null;
    }

    private void propagateInferredTypes(List<ASTNode> ast) {
        for (ASTNode node : ast) {
            if (node instanceof VariableDeclarationNode decl) {
                String name = decl.getName();
                StructInstaceNode si = structVars.get(name);
                if (si != null && inferredStructTypes.containsKey(si)) {
                    String elemType = inferredStructTypes.get(si);
                    String newType = "Struct<" + si.getName() + "<" + elemType + ">>";
                    try {
                        var typeField = VariableDeclarationNode.class.getDeclaredField("type");
                        typeField.setAccessible(true);
                        typeField.set(decl, newType);
                        System.out.println("[TypeSpecializer] Propagado tipo → " + name + " : " + newType);
                    } catch (Exception e) {
                        throw new RuntimeException("Falha ao propagar tipo para " + name, e);
                    }
                }
            }

            for (ASTNode child : node.getChildren()) {
                propagateInferredTypes(Collections.singletonList(child));
            }
        }
    }
}
