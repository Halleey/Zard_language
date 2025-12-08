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


    public void createSpecializedStructsFromInferences() {
        if (visitor == null) {
            System.out.println("[TS.createSpecs] Visitor null, não posso materializar structs especializadas.");
            return;
        }

        System.out.println("[TS.createSpecs] Materializando structs especializadas a partir das inferências...");

        // inferredStructTypes: StructInstaceNode -> elemType ("int", "double", etc.)
        for (var entry : inferredStructTypes.entrySet()) {
            StructInstaceNode si = entry.getKey();
            String elemType = entry.getValue();
            String baseName = si.getName(); // "Set", "Pessoa", etc.

            if (elemType == null || "?".equals(elemType)) {
                continue;
            }

            StructNode baseNode = visitor.getStructNode(baseName);
            if (baseNode == null) {
                System.out.println("[TS.createSpecs] NÃO achei StructNode base para " + baseName);
                continue;
            }

            System.out.println("[TS.createSpecs] Criando specialization para "
                    + baseName + "<" + elemType + ">");

            // Isso vai popular visitor.specializedStructs e structDefinitions
            visitor.getOrCreateSpecializedStruct(baseNode, elemType);
        }
    }



    private void collectInferences(List<ASTNode> ast) {
        for (ASTNode node : ast) {

            if (node instanceof VariableDeclarationNode decl) {

                String varName = decl.getName();
                String declType = decl.getType();
                variableTypes.put(varName, declType);

                // Caso: Struct<Set<int>>
                if (declType != null &&
                        declType.startsWith("Struct<") &&
                        declType.endsWith(">")) {

                    // Ex: Struct<Set<int>>
                    int i1 = declType.indexOf('<', "Struct".length());
                    int i2 = declType.lastIndexOf('>');

                    if (i1 > 0 && i2 > i1) {
                        String inner = declType.substring(i1 + 1, i2).trim(); // ex: Set<int>

                        if (inner.startsWith("Set<") && inner.endsWith(">")) {
                            String elemType = inner.substring("Set<".length(), inner.length() - 1).trim();

                            StructInstaceNode si = getStructInstaceNode(decl);
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

                    if (visitor != null) {
                        visitor.markStructUsed(si.getName());
                    }
                }
            }

            if (node instanceof ImplNode impl) {
                for (FunctionNode method : impl.getMethods()) {
                    collectInferences(method.getBody());
                }
            }


            // StructNode — APENAS REGISTRO DE TIPO, NÃO USO


            if (node instanceof StructNode struct) {
                String name = struct.getName();

                if (!variableTypes.containsKey(name)) {
                    variableTypes.put(name, "Struct<" + name + ">");
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

            // Chamada de método (ex: s.add(x))

            if (node instanceof StructMethodCallNode call) {

                String varName = call.getReceiverName();
                ASTNode arg = call.getArgs().isEmpty() ? null : call.getArgs().get(0);

                if (arg != null && varName != null) {

                    String inferredType = inferTypeFromArgument(arg);
                    StructInstaceNode target = structVars.get(varName);

                    if (target != null && inferredType != null) {

                        if (visitor != null) {
                            visitor.markStructUsed(target.getName());
                        }

                        if (!inferredStructTypes.containsKey(target)) {
                            registerStructInference(
                                    target,
                                    inferredType,
                                    "primeiro uso (chamada " + varName + ".add)"
                            );
                        } else {
                            String current = inferredStructTypes.get(target);
                            if (!Objects.equals(current, inferredType)) {
                                System.out.println("[TypeSpecializer][Aviso] Conflito de tipos em '" +
                                        varName + "': era " + current +
                                        ", recebeu " + inferredType);
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

    private static StructInstaceNode getStructInstaceNode(VariableDeclarationNode decl) {
        if (decl.getInitializer() instanceof StructInstaceNode si) {
            return si;
        }
        return new StructInstaceNode(
                "Set",
                new ArrayList<>(),
                new LinkedHashMap<>()
        );
    }

    private void registerStructInference(StructInstaceNode node, String elemType, String origem) {

        inferredStructTypes.put(node, elemType);

        String base = node.getName();
        String concrete;

        if (base.contains("<")) {
            concrete = "Struct<" + base + ">";
        } else {
            concrete = "Struct<" + base + "<" + elemType + ">>";
        }

        node.setConcreteType(concrete);

        System.out.println("[TypeSpecializer] Struct<" + node.getName() + "> inferida como " +
                elemType + " via " + origem + " (concreteType=" + concrete + ")");

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
        return name.contains("_");
    }

    private String extractElementType(String type) {
        if (type == null || type.equals("?")) return null;
        if (type.startsWith("List<") && type.endsWith(">")) {
            return type.substring(5, type.length() - 1);
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

    private String findVarName(StructInstaceNode node) {
        for (var e : structVars.entrySet()) {
            if (e.getValue() == node) return e.getKey();
        }
        return "?";
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
                    // substitui o ParamInfo por outro com tipo inferido
                    params.set(i, new ParamInfo(
                            p.name(),
                            inferred,
                            p.isRef()
                    ));
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
