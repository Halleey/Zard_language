package ast;

import ast.functions.FunctionNode;
import ast.lists.ListAddNode;
import ast.lists.ListNode;
import ast.structs.StructInstaceNode;
import ast.structs.StructMethodCallNode;
import ast.structs.StructFieldAccessNode;
import ast.structs.StructNode;
import ast.variables.LiteralNode;
import ast.variables.VariableDeclarationNode;
import ast.variables.VariableNode;
import low.module.LLVisitorMain;

import java.util.*;

public class TypeSpecializer {

    // StructInstaceNode -> tipo do elemento (ex: "int" para Set<int>)
    private final Map<StructInstaceNode, String> inferredStructTypes = new IdentityHashMap<>();
    // nome da variável -> nó de instância de struct
    private final Map<String, StructInstaceNode> structVars = new HashMap<>();
    // nome da variável -> tipo textual (int, Struct<Set<int>>, etc.)
    private final Map<String, String> variableTypes = new HashMap<>();

    private LLVisitorMain visitor;

    // === INJEÇÃO DO VISITOR (pode acontecer depois) ===
    public void setVisitor(LLVisitorMain visitor) {
        this.visitor = visitor;
    }

    // =========================================================
    //             PIPELINE PRINCIPAL
    // =========================================================
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

    // =========================================================
    //             COLETA DE INFERÊNCIA
    // =========================================================
    private void collectInferences(List<ASTNode> ast) {
        for (ASTNode node : ast) {

            // ======================================================
            //               DECLARAÇÃO DE VARIÁVEL
            // ======================================================
            if (node instanceof VariableDeclarationNode decl) {

                String varName = decl.getName();
                String declType = decl.getType();

                variableTypes.put(varName, declType);

                // Detectar declarações explícitas: Struct<Set<int>> s;
                if (declType != null &&
                        declType.startsWith("Struct<") &&
                        declType.contains("<") &&
                        declType.endsWith(">")) {

                    // Formato esperado: Struct<Set<int>>
                    int i1 = declType.indexOf('<', "Struct".length()); // depois de "Struct"
                    int i2 = declType.lastIndexOf('>');

                    if (i1 > 0 && i2 > i1) {

                        // inner = "Set<int>"
                        String inner = declType.substring(i1 + 1, i2).trim();

                        if (inner.startsWith("Set<") && inner.endsWith(">")) {

                            // elemento interno: "int"
                            String elemType = inner.substring("Set<".length(), inner.length() - 1).trim();

                            StructInstaceNode si;

                            if (decl.getInitializer() instanceof StructInstaceNode) {
                                si = (StructInstaceNode) decl.getInitializer();
                            } else {
                                // Criar instância sintética para registrar inferência explícita
                                si = new StructInstaceNode(
                                        "Set",
                                        new ArrayList<>(),
                                        new LinkedHashMap<>()
                                );
                            }

                            structVars.put(varName, si);

                            // Registra inferência desta instância para elemType
                            registerStructInference(si, elemType, "declaração explícita");

                            // Garante a criação da struct especializada Set_elemType (template base Set)
                            if (visitor != null) {
                                StructNode base = visitor.getStructNode("Set");
                                if (base != null) {
                                    visitor.getOrCreateSpecializedStruct(base, elemType);
                                }
                            }
                        }
                    }
                }

                // Se initializer é uma struct normal
                if (decl.getInitializer() instanceof StructInstaceNode si) {
                    structVars.put(varName, si);
                    if (visitor != null) {
                        visitor.markStructUsed(si.getName());
                    }
                }
            }

            // ======================================================
            //               DEFINIÇÃO DE STRUCT
            // ======================================================
            if (node instanceof StructNode struct) {

                String name = struct.getName();

                if (!variableTypes.containsKey(name)) {
                    variableTypes.put(name, "Struct<" + name + ">");
                }

                if (visitor != null) {
                    visitor.markStructUsed(name);
                }
            }

            // ======================================================
            //      STRUCT INSTANCIADA COM VALOR DEFAULT EM data
            // ======================================================
            if (node instanceof StructInstaceNode structNode) {

                Map<String, ASTNode> named = structNode.getNamedValues();

                if (named != null && !named.isEmpty()) {

                    ASTNode dataInit = named.get("data");

                    if (dataInit instanceof ListNode list) {

                        String elemType = extractElementType(list.getType());

                        if (elemType != null) {
                            registerStructInference(
                                    structNode,
                                    elemType,
                                    "inicialização explícita"
                            );
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
                                System.out.println(
                                        "[TypeSpecializer][Aviso] Conflito de tipos em '" +
                                                varName + "': era " + current +
                                                ", recebeu " + inferredType
                                );
                            }
                        }
                    }
                }
            }

            for (ASTNode child : node.getChildren()) {
                collectInferences(Collections.singletonList(child));
            }
        }
    }
    private void registerStructInference(StructInstaceNode node, String elemType, String origem) {

        inferredStructTypes.put(node, elemType);

        // ==============================
        // CRIAÇÃO DO concreteType CERTO
        // ==============================
        String base = node.getName(); // "Set" ou "Set<int>"

        String concrete;

        if (base.contains("<")) {
            // já veio como "Set<int>" no parser
            concrete = "Struct<" + base + ">";
        } else {
            // inferência pura
            concrete = "Struct<" + base + "<" + elemType + ">>";
        }

        node.setConcreteType(concrete);

        System.out.println(
                "[TypeSpecializer] Struct<" + node.getName() + "> inferida como " +
                        elemType + " via " + origem + " (concreteType=" + concrete + ")"
        );

        if (visitor != null) {
            visitor.markStructUsed(node.getName());

            StructNode baseNode = visitor.getStructNode(node.getName());
            if (baseNode != null && !isAlreadySpecializedName(baseNode.getName())) {
                visitor.getOrCreateSpecializedStruct(baseNode, elemType);
            }
        }
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

    private boolean isAlreadySpecializedName(String name) {
        // Exemplo: Set_int, Pessoa_string, Mapa_int_bool
        return name.contains("_");
    }

    // =========================================================
    //     ESPECIALIZAÇÃO DE TIPOS EM FUNÇÕES
    // =========================================================
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

        // Parâmetros com tipo "?" → tentar inferir
        for (int i = 0; i < fn.getParamTypes().size(); i++) {
            String t = fn.getParamTypes().get(i);
            if ("?".equals(t)) {
                String inferred = inferTypeFromBody(fn.getBody());
                if (inferred != null) {
                    fn.getParamTypes().set(i, inferred);
                }
            }
        }

        // Tipo de retorno "?" → tentar inferir
        if ("?".equals(fn.getReturnType())) {
            String ret = inferTypeFromBody(fn.getBody());
            if (ret != null) {
                fn.setReturnType(ret);
            }
        }
    }

    private String inferTypeFromBody(List<ASTNode> body) {

        for (ASTNode stmt : body) {

            // ListAddNode com elementType conhecido
            if (stmt instanceof ListAddNode add) {
                if (add.getElementType() != null && !add.getElementType().equals("?")) {
                    return add.getElementType();
                }
            }

            // uso de variável
            if (stmt instanceof VariableNode v) {
                String type = variableTypes.get(v.getName());
                if (type != null) return type;
            }

            // acesso a campo de struct (heurística antiga)
            if (stmt instanceof StructFieldAccessNode sfa) {
                String f = sfa.getFieldName();
                if (f != null && !f.equals("?")) {
                    return f;
                }
            }
        }
        return null;
    }

    private void propagateInferredTypes(List<ASTNode> ast) {

        for (ASTNode node : ast) {

            if (node instanceof VariableDeclarationNode decl) {

                StructInstaceNode si = structVars.get(decl.getName());

                if (si != null && inferredStructTypes.containsKey(si)) {

                    String elemType = inferredStructTypes.get(si);
                    String base = si.getName(); // "Set" ou "Set<int>"
                    String newType;

                    if (base.contains("<")) {
                        newType = "Struct<" + base + ">"; // já especializado
                    } else {
                        newType = "Struct<" + base + "<" + elemType + ">>";
                    }

                    try {
                        var field = VariableDeclarationNode.class.getDeclaredField("type");
                        field.setAccessible(true);
                        field.set(decl, newType);

                        System.out.println(
                                "[TypeSpecializer] Propagando tipo para '" +
                                        decl.getName() + "': " + newType
                        );

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
