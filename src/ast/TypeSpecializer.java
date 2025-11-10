package ast;
import ast.functions.FunctionNode;
import ast.lists.ListAddNode;
import ast.lists.ListNode;
import ast.structs.ImplNode;
import ast.structs.StructFieldAccessNode;
import ast.structs.StructInstaceNode;
import ast.structs.StructNode;
import ast.variables.VariableDeclarationNode;
import ast.variables.VariableNode;
import java.util.*;

import java.util.*;

public class TypeSpecializer {

    private final Map<String, String> inferredStructTypes = new HashMap<>();
    private final Map<String, String> variableTypes = new HashMap<>();

    public void specialize(List<ASTNode> ast) {
        collectInferences(ast);
        applySpecializations(ast);
        updateStructDefinitions(ast);
    }

    private void collectInferences(List<ASTNode> ast) {
        for (ASTNode node : ast) {
            if (node instanceof VariableDeclarationNode decl) {
                variableTypes.put(decl.getName(), decl.getType());
            }

            if (node instanceof StructInstaceNode structNode) {
                if (!structNode.getNamedValues().isEmpty()) {
                    ASTNode dataInit = structNode.getNamedValues().get("data");
                    if (dataInit instanceof ListNode list) {
                        String elemType = list.getType();
                        if (elemType != null && !elemType.equals("?")) {
                            String cleanType = elemType.startsWith("List<") && elemType.endsWith(">")
                                    ? elemType.substring(5, elemType.length() - 1)
                                    : elemType;
                            inferredStructTypes.put(structNode.getName(), cleanType);
                            structNode.setConcreteType("Struct<" + structNode.getName() + "<" + cleanType + ">>");
                            structNode.replaceFieldType("data", "List<" + cleanType + ">");
                            System.out.println("[TypeSpecializer] Struct " + structNode.getName() +
                                    " inferida como " + cleanType);
                        }
                    }
                }
            }

            for (ASTNode child : node.getChildren()) {
                collectInferences(Collections.singletonList(child));
            }
        }
    }

    private void applySpecializations(List<ASTNode> ast) {
        for (ASTNode node : ast) {
            if (node instanceof ImplNode implNode) {
                specializeImpl(implNode);
            }
            if (node instanceof FunctionNode fn) {
                specializeFunction(fn);
            }

            for (ASTNode child : node.getChildren()) {
                applySpecializations(Collections.singletonList(child));
            }
        }
    }

    private void specializeImpl(ImplNode implNode) {
        String structName = implNode.getStructName();
        if (structName.startsWith("Struct<") && structName.endsWith(">")) {
            structName = structName.substring(7, structName.length() - 1);
        }

        String inferredType = inferredStructTypes.get(structName);
        if (inferredType == null) {
            System.out.println("[TypeSpecializer] Nenhum tipo inferido encontrado para " + structName);
            return;
        }

        for (FunctionNode fn : implNode.getMethods()) {
            List<String> newParamTypes = new ArrayList<>();
            for (String type : fn.getParamTypes()) {
                newParamTypes.add(type.contains("?") ? inferredType : type);
            }
            fn.getParamTypes().clear();
            fn.getParamTypes().addAll(newParamTypes);

            if (fn.getReturnType().contains("?")) {
                fn.setReturnType("Struct<" + structName + ">");
            }

            for (ASTNode stmt : fn.getBody()) {
                fixTypesInBody(stmt, inferredType);
            }

            System.out.println("[TypeSpecializer] Impl<" + structName + "> → função '" + fn.getName() +
                    "' especializada para " + inferredType);
        }
    }

    private void fixTypesInBody(ASTNode node, String concreteType) {
        if (node == null) return;
        if (node instanceof ListAddNode add) add.setType(concreteType);
        if (node instanceof StructFieldAccessNode sfa && sfa.getFieldName().equals("data"))
            sfa.setResolvedFieldType("List<" + concreteType + ">");
        for (ASTNode child : node.getChildren()) fixTypesInBody(child, concreteType);
    }

    private void specializeFunction(FunctionNode fn) {
        for (int i = 0; i < fn.getParamTypes().size(); i++) {
            String t = fn.getParamTypes().get(i);
            if (t.equals("?")) {
                String inferred = inferTypeFromBody(fn.getBody());
                if (inferred != null) fn.getParamTypes().set(i, inferred);
            }
        }

        if (fn.getReturnType().equals("?")) {
            String ret = inferTypeFromBody(fn.getBody());
            if (ret != null) fn.setReturnType(ret);
        }
    }

    private String inferTypeFromBody(List<ASTNode> body) {
        for (ASTNode stmt : body) {
            if (stmt instanceof ListAddNode add) {
                if (add.getElementType() != null && !add.getElementType().equals("?"))
                    return add.getElementType();
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

    private void updateStructDefinitions(List<ASTNode> ast) {
        for (Map.Entry<String, String> entry : inferredStructTypes.entrySet()) {
            String structName = entry.getKey();
            String elemType = entry.getValue();

            for (ASTNode node : ast) {
                if (node instanceof StructNode struct && struct.getName().equals(structName)) {
                    struct.replaceFieldType("data", "List<" + elemType + ">");
                    System.out.println("[TypeSpecializer] Atualizado Struct " + structName +
                            " → campo 'data' = List<" + elemType + ">");
                }
            }
        }
    }

    public Map<String, String> getInferredStructTypes() {
        return inferredStructTypes;
    }
}
