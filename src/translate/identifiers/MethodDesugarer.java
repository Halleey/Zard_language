package translate.identifiers;

import ast.ASTNode;
import ast.functions.FunctionNode;
import ast.functions.ParamInfo;
import ast.structs.ImplNode;
import ast.structs.StructFieldAccessNode;
import ast.variables.VariableNode;

import java.util.*;

/**
 * Reescreve métodos de impl:
 * - Descobre receiver implícito (ex: s, teste, self) olhando campos: s.data, teste.x, etc.
 * - Adiciona como primeiro parâmetro do tipo Struct<NomeDaStructDoImpl>.
 * - Garante tipo de retorno padrão Struct<NomeDaStructDoImpl> se estiver "?" ou vazio.
 */

public class MethodDesugarer {

    public void desugar(List<ASTNode> ast) {
        if (ast == null) return;
        for (ASTNode node : ast) {
            visit(node);
        }
    }

    private void visit(ASTNode node) {
        if (node == null) return;

        if (node instanceof ImplNode impl) {
            processImpl(impl);
        }

        for (ASTNode child : node.getChildren()) {
            visit(child);
        }
    }

    private void processImpl(ImplNode impl) {
        String structName = impl.getStructName(); // ex: "Set"
        for (FunctionNode fn : impl.getMethods()) {
            normalizeMethod(structName, fn);
        }
    }

    private void normalizeMethod(String structName, FunctionNode fn) {

        List<ParamInfo> params = fn.getParameters();

        // 1) Já tem receiver explícito?
        if (hasExplicitReceiver(structName, params)) {
            ensureReturnType(structName, fn);
            return;
        }

        // 2) Descobrir nome do receiver implícito
        String receiverName = findImplicitReceiverCandidate(fn);
        if (receiverName == null || receiverName.isBlank()) {
            receiverName = "s";
        }

        // Evitar conflito de nomes
        String finalReceiverName = receiverName;
        if (params.stream().anyMatch(p -> p.name().equals(finalReceiverName))) {
            List<String> alternatives = Arrays.asList(
                    structName.toLowerCase(),
                    "self",
                    "this",
                    "_" + structName.toLowerCase()
            );
            for (String alt : alternatives) {
                boolean free = params.stream().noneMatch(p -> p.name().equals(alt));
                if (free) {
                    receiverName = alt;
                    break;
                }
            }
        }

        // 3) Inserir receiver como PRIMEIRO parâmetro
        List<ParamInfo> newParams = new ArrayList<>();
        newParams.add(new ParamInfo(
                receiverName,
                "Struct<" + structName + ">",
                false // receiver NÃO é & por padrão
        ));
        newParams.addAll(params);

        fn.getParameters().clear();
        fn.getParameters().addAll(newParams);

        fn.setImplicitReceiverName(receiverName);
        ensureReturnType(structName, fn);
    }

    private boolean hasExplicitReceiver(String structName, List<ParamInfo> params) {
        if (params == null || params.isEmpty()) return false;

        ParamInfo first = params.get(0);
        String type = first.type();
        if (type == null) return false;

        type = type.trim();

        if (type.equals(structName)) return true;
        if (type.equals("Struct<" + structName + ">")) return true;
        // ex: Struct<Set<int>>
        if (type.startsWith("Struct<" + structName + "<")) return true;

        return false;
    }

    private void ensureReturnType(String structName, FunctionNode fn) {
        String ret = fn.getReturnType();
        if (ret == null || ret.isBlank() || "?".equals(ret)) {
            fn.setReturnType("Struct<" + structName + ">");
        }
    }

    /**
     * Procura no corpo da função acessos do tipo:
     *   <ident>.campo
     * e devolve o identificador (<ident>) mais provável para ser o receiver.
     */
    private String findImplicitReceiverCandidate(FunctionNode fn) {
        Set<String> candidates = new LinkedHashSet<>();

        for (ASTNode stmt : fn.getBody()) {
            collectReceiverCandidates(stmt, candidates);
        }

        if (candidates.isEmpty()) return null;

        // Heurística: se tiver "s", "self" ou "this", preferir esses
        List<String> preferred = Arrays.asList("s", "self", "this");
        for (String p : preferred) {
            if (candidates.contains(p)) {
                return p;
            }
        }

        // Senão, pega o primeiro que apareceu
        return candidates.iterator().next();
    }

    /**
     * Percorre a árvore procurando StructFieldAccessNode,
     * e coleta o nome da variável que está antes do ponto.
     */
    private void collectReceiverCandidates(ASTNode node, Set<String> candidates) {
        if (node == null) return;

        if (node instanceof StructFieldAccessNode fieldAccess) {
            ASTNode inst = fieldAccess.getStructInstance();
            if (inst instanceof VariableNode var) {
                candidates.add(var.getName());
            }
        }

        // Se tiver um nó de StructMethodCall no seu AST, pode tratar aqui também:
        // if (node instanceof StructMethodCallNode call) { ... }

        for (ASTNode child : node.getChildren()) {
            collectReceiverCandidates(child, candidates);
        }
    }
}
