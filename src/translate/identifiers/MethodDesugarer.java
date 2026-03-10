package translate.identifiers;

import ast.ASTNode;
import ast.functions.FunctionNode;
import ast.functions.ParamInfo;
import ast.structs.ImplNode;
import ast.structs.StructFieldAccessNode;
import ast.variables.TypeResolver;
import ast.variables.VariableNode;
import context.statics.symbols.StructType;
import context.statics.symbols.Type;

import java.util.*;

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

        // Já tem receiver explícito?
        if (hasExplicitReceiver(structName, params)) {
            ensureReturnType(structName, fn);
            return;
        }

        // Descobrir nome do receiver implícito
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

        // Inserir receiver como PRIMEIRO parâmetro
        List<ParamInfo> newParams = new ArrayList<>();
        newParams.add(new ParamInfo(
                receiverName,
                TypeResolver.resolve("Struct<" + structName + ">"), // agora Type
                false
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
        Type type = first.typeObj(); // assume ParamInfo agora retorna Type
        if (type == null) return false;

        if (type instanceof StructType st) {
            String tname = st.name();
            if (tname.equals(structName)) return true;
            if (tname.startsWith(structName + "<")) return true; // ex: Set<int>
        }
        return false;
    }

    private void ensureReturnType(String structName, FunctionNode fn) {
        Type ret = fn.getReturnType();
        if (ret == null || ret.name().equals("?")) {
            fn.setReturnType(TypeResolver.resolve("Struct<" + structName + ">"));
        }
    }

    private String findImplicitReceiverCandidate(FunctionNode fn) {
        Set<String> candidates = new LinkedHashSet<>();

        for (ASTNode stmt : fn.getBody()) {
            collectReceiverCandidates(stmt, candidates);
        }

        if (candidates.isEmpty()) return null;

        List<String> preferred = Arrays.asList("s", "self", "this");
        for (String p : preferred) {
            if (candidates.contains(p)) {
                return p;
            }
        }

        return candidates.iterator().next();
    }

    private void collectReceiverCandidates(ASTNode node, Set<String> candidates) {
        if (node == null) return;

        if (node instanceof StructFieldAccessNode fieldAccess) {
            ASTNode inst = fieldAccess.getStructInstance();
            if (inst instanceof VariableNode var) {
                candidates.add(var.getName());
            }
        }
        for (ASTNode child : node.getChildren()) {
            collectReceiverCandidates(child, candidates);
        }
    }
}