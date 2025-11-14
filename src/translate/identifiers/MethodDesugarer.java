package translate.identifiers;

import ast.ASTNode;
import ast.functions.FunctionNode;
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
        List<String> names = fn.getParams();
        List<String> types = fn.getParamTypes();

        if (names == null) {
            names = new ArrayList<>();
            fn.setParams(names);
        }
        if (types == null) {
            types = new ArrayList<>(Collections.nCopies(names.size(), "?"));
            fn.setParamTypes(types);
        }

        // 1) Já tem receiver explícito? (ex: function add(Struct<Set> s, ? value))
        if (hasExplicitReceiver(structName, fn)) {
            ensureReturnType(structName, fn);
            return;
        }

        // 2) Tentar descobrir receiver implícito pelo corpo
        String implicit = findImplicitReceiverCandidate(fn);

        // Se não achar nada, cai em um default ("s")
        if (implicit == null || implicit.isBlank()) {
            implicit = "s";
        }

        // Evitar conflito de nome com outros parâmetros
        if (names.contains(implicit)) {
            // Se já tem "s" como param, tenta "self", depois "this", etc.
            List<String> alternatives = Arrays.asList(
                    structName.toLowerCase(),
                    "self",
                    "this",
                    "_" + structName.toLowerCase()
            );
            for (String alt : alternatives) {
                if (!names.contains(alt)) {
                    implicit = alt;
                    break;
                }
            }
        }

        // Adiciona como primeiro parâmetro
        names.add(0, implicit);
        types.add(0, "Struct<" + structName + ">");
        fn.setParams(names);
        fn.setParamTypes(types);
        fn.setImplicitReceiverName(implicit);

        ensureReturnType(structName, fn);
    }

    private boolean hasExplicitReceiver(String structName, FunctionNode fn) {
        List<String> types = fn.getParamTypes();
        if (types == null || types.isEmpty()) return false;

        String first = types.get(0);
        if (first == null) return false;

        first = first.trim();
        if (first.equals(structName)) return true;
        if (first.equals("Struct<" + structName + ">")) return true;
        // ex: Struct<Set<string>> ainda é "receiver desta struct"
        if (first.startsWith("Struct<" + structName + "<")) return true;

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
