package ast;

import ast.functions.FunctionNode;
import ast.functions.ParamInfo;
import ast.lists.ListAddNode;
import ast.lists.ListNode;
import ast.structs.*;
import ast.variables.LiteralNode;
import ast.variables.VariableDeclarationNode;
import ast.variables.VariableNode;
import low.module.LLVisitorMain;

import java.util.*;


import java.util.*;
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

        if (inferredStructTypes.isEmpty()) {
            System.out.println("[TypeSpecializer] Nenhum tipo inferido.");
        }
    }

    public void createSpecializedStructsFromInferences() {
        if (visitor == null) return;

        for (var entry : inferredStructTypes.entrySet()) {
            StructInstaceNode si = entry.getKey();
            String elemType = entry.getValue();
            String baseName = si.getName();

            if (elemType == null || "?".equals(elemType)) continue;

            StructNode baseNode = visitor.getStructNode(baseName);
            if (baseNode == null) continue;

            visitor.getOrCreateSpecializedStruct(baseNode, elemType);
        }
    }

    private void collectInferences(List<ASTNode> ast) {
        for (ASTNode node : ast) {

            if (node instanceof VariableDeclarationNode decl) {
                handleVarDecl(decl);
            }

            if (node instanceof ImplNode impl) {
                for (FunctionNode method : impl.getMethods()) {
                    collectInferences(method.getBody());
                }
            }

            if (node instanceof StructNode struct) {
                String name = struct.getName();
                variableTypes.putIfAbsent(name, "Struct<" + name + ">");
            }

            if (node instanceof StructInstaceNode structNode) {
                handleStructInstanceInference(structNode);
            }

            if (node instanceof StructMethodCallNode call) {
                handleStructMethodCallInference(call);
            }

            // Recursão
            for (ASTNode child : node.getChildren()) {
                collectInferences(Collections.singletonList(child));
            }
        }
    }

    private void handleVarDecl(VariableDeclarationNode decl) {
        String varName = decl.getName();
        String declType = decl.getType();
        variableTypes.put(varName, declType);

        // Caso: Struct<Set<int>>
        if (declType != null && declType.startsWith("Struct<") && declType.endsWith(">")) {

            int i1 = declType.indexOf('<', "Struct".length());
            int i2 = declType.lastIndexOf('>');

            if (i1 > 0 && i2 > i1) {
                String inner = declType.substring(i1 + 1, i2).trim(); // ex: Set<int>

                if (inner.startsWith("Set<") && inner.endsWith(">")) {
                    String elemType = inner.substring("Set<".length(), inner.length() - 1).trim();

                    StructInstaceNode si = getStructInstaceNode(decl);

                    if (si == null) {
                        si = new StructInstaceNode(
                                "Set",
                                Collections.emptyList(),
                                Collections.emptyMap()
                        );
                    }

                    structVars.put(varName, si);
                    registerStructInference(si, elemType, "declaração explícita");


                    if (visitor != null) {
                        StructNode base = visitor.getStructNode("Set");
                        if (base != null) {
                            visitor.getOrCreateSpecializedStruct(base, elemType);
                        }
                    }
                }
            }
        }

        // Inicializador é uma struct real
        if (decl.getInitializer() instanceof StructInstaceNode si) {
            structVars.put(varName, si);
            if (visitor != null) visitor.markStructUsed(si.getName());
        }
    }

    private void handleStructInstanceInference(StructInstaceNode structNode) {
        Map<String, ASTNode> named = structNode.getNamedValues();
        if (named == null || named.isEmpty()) return;

        ASTNode dataInit = named.get("data");
        if (dataInit instanceof ListNode list) {
            String elemType = extractElementType(list.getType());
            if (elemType != null) {
                registerStructInference(structNode, elemType, "inicialização explícita");
            }
        }
    }

    private void handleStructMethodCallInference(StructMethodCallNode call) {

        String varName = call.getReceiverName();
        if (varName == null) return;

        StructInstaceNode target = structVars.get(varName);
        if (target == null) return;
        if (!"Set".equals(target.getName())) {
            return;
        }

        // opcional: restringir só a métodos que realmente “inserem” elemento
        String method = call.getMethodName();
        if (method != null && !method.equals("add") && !method.equals("push") && !method.equals("insert")) {
            return;
        }

        ASTNode arg = call.getArgs().isEmpty() ? null : call.getArgs().get(0);
        if (arg == null) return;

        String inferredType = inferTypeFromArgument(arg);
        if (inferredType == null) return;

        if (visitor != null) visitor.markStructUsed(target.getName());

        if (!inferredStructTypes.containsKey(target)) {
            registerStructInference(target, inferredType, "primeiro uso (Set.add)");
        } else {
            String current = inferredStructTypes.get(target);
            if (!Objects.equals(current, inferredType)) {
                System.out.println("[TypeSpecializer][Aviso] Conflito de tipos em '" +
                        varName + "': era " + current + ", recebeu " + inferredType);
            }
        }
    }

    private static StructInstaceNode getStructInstaceNode(VariableDeclarationNode decl) {
        if (decl.getInitializer() instanceof StructInstaceNode si) {
            return si;
        }
        return null;
    }

    private void registerStructInference(StructInstaceNode node, String elemType, String origem) {
        if (node == null) return;
        if (elemType == null) return;

        inferredStructTypes.put(node, elemType);

        String base = node.getName();
        String concrete;

        if (base.contains("<")) {
            concrete = "Struct<" + base + ">";
        } else {
            concrete = "Struct<" + base + "<" + elemType + ">>";
        }

        node.setConcreteType(concrete);

        if (visitor != null) {
            visitor.markStructUsed(node.getName()); // uso REAL

            if (!elemType.equals("?")) {
                String specName = node.getName() + "_" + elemType;
                visitor.markStructUsed(specName);
            }

            StructNode baseNode = visitor.getStructNode(node.getName());
            if (baseNode != null && !isAlreadySpecializedName(baseNode.getName())) {
                visitor.getOrCreateSpecializedStruct(baseNode, elemType);
            }
        }
    }

    private boolean isAlreadySpecializedName(String name) {
        return name != null && name.contains("_");
    }

    private String extractElementType(String type) {
        if (type == null || type.equals("?")) return null;
        if (type.startsWith("List<") && type.endsWith(">")) {
            return type.substring(5, type.length() - 1).trim();
        }
        return type;
    }

    private String inferTypeFromArgument(ASTNode arg) {

        if (arg instanceof VariableNode var)
            return variableTypes.get(var.getName());

        if (arg instanceof StructInstaceNode s)
            return "Struct<" + s.getName() + ">";

        if (arg instanceof ListNode list)
            return extractElementType(list.getType());

        if (arg instanceof LiteralNode lit)
            return lit.getValue().type();

        return null;
    }

    private void applySpecializations(List<ASTNode> ast) {
        for (ASTNode node : ast) {
            if (node instanceof FunctionNode fn) {
                specializeFunction(fn);
            }
            if (node instanceof ImplNode impl) {
                for (FunctionNode method : impl.getMethods()) {
                    specializeFunction(method);
                }
            }

            for (ASTNode child : node.getChildren()) {
                applySpecializations(Collections.singletonList(child));
            }
        }
    }

    private void specializeFunction(FunctionNode fn) {

        List<ParamInfo> params = fn.getParameters();

        for (int i = 0; i < params.size(); i++) {
            ParamInfo p = params.get(i);

            if ("?".equals(p.type())) {
                String inferred = inferTypeFromBody(fn.getBody());
                if (inferred != null) {
                    params.set(i, new ParamInfo(p.name(), inferred, p.isRef()));
                }
            }
        }

        if ("?".equals(fn.getReturnType())) {
            String inferred = inferTypeFromBody(fn.getBody());
            if (inferred != null) {
                fn.setReturnType(inferred);
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
                String field = sfa.getFieldName();
                if (field != null && !field.equals("?")) {
                    return field;
                }
            }
        }
        return null;
    }

    private void propagateInferredTypes(List<ASTNode> ast) {

        for (ASTNode node : ast) {

            if (node instanceof ImplNode impl) {
                for (FunctionNode method : impl.getMethods()) {
                    propagateInferredTypes(method.getBody());
                }
            }

            if (node instanceof VariableDeclarationNode decl) {

                StructInstaceNode si = structVars.get(decl.getName());
                if (si != null && inferredStructTypes.containsKey(si)) {

                    String elemType = inferredStructTypes.get(si);
                    String base = si.getName();
                    String newType;

                    if (base.contains("<")) {
                        newType = "Struct<" + base + ">";
                    } else {
                        newType = "Struct<" + base + "<" + elemType + ">>";
                    }

                    try {
                        var field = VariableDeclarationNode.class.getDeclaredField("type");
                        field.setAccessible(true);
                        field.set(decl, newType);

                        System.out.println("[TypeSpecializer] Propagando tipo para '" +
                                decl.getName() + "': " + newType);

                    } catch (Exception e) {
                        throw new RuntimeException("Falha ao propagar tipo", e);
                    }
                }
            }

            for (ASTNode child : node.getChildren()) {
                propagateInferredTypes(Collections.singletonList(child));
            }
        }
    }
}
