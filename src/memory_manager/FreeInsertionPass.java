package memory_manager;

import ast.ASTNode;
import ast.functions.FunctionNode;
import ast.home.MainAST;
import ast.ifstatements.IfNode;
import ast.loops.WhileNode;
import ast.structs.ImplNode;
import ast.structs.StructFieldAccessNode;
import ast.structs.StructInstaceNode;

import java.util.*;
//
//
//public class FreeInsertionPass {
//
//    private final Map<String, ASTNode> lastUse;
//
//    public FreeInsertionPass(Map<String, ASTNode> lastUse) {
//        this.lastUse = lastUse;
//    }
//
//    public void apply(List<ASTNode> stmts) {
//        apply(stmts, null);
//    }
//
//
//    private void apply(List<ASTNode> stmts, ASTNode container) {
//        if (stmts == null) return;
//
//        ListIterator<ASTNode> it = stmts.listIterator();
//        while (it.hasNext()) {
//            ASTNode node = it.next();
//
//            if (node instanceof MainAST main) {
//                apply(main.body, main);
//            } else if (node instanceof FunctionNode fn) {
//                apply(fn.getBody(), fn);
//            } else if (node instanceof ImplNode impl) {
//                for (FunctionNode m : impl.getMethods()) {
//                    apply(m.getBody(), m);
//                }
//            } else if (node instanceof WhileNode w) {
//                apply(w.body, w);
//            } else if (node instanceof IfNode iff) {
//                apply(iff.thenBranch, iff);
//                if (iff.elseBranch != null) apply(iff.elseBranch, iff);
//            }
//
//            // Inserção de pontos de liberação baseados no último uso
//            for (var e : lastUse.entrySet()) {
//                if (e.getValue() == node) {
//                    it.add(new FreeNode(e.getKey()));
//                    // Recursão para liberar os campos internos, como List<Row> em Matrix
//                    if (node instanceof StructFieldAccessNode fieldAccess) {
//                        String fieldName = fieldAccess.getFieldName();
//                        if ("rows".equals(fieldName)) {
//                            // Quando for um campo do tipo List<Row>, aplicar liberação recursiva
//                            applyFreeRecursively(fieldAccess.getStructInstance());
//                        }
//                    }
//                }
//            }
//        }
//
//        // Caso seja um container, insere um ponto de liberação após o término
//        if (container != null) {
//            for (var e : lastUse.entrySet()) {
//                if (e.getValue() == container) {
//                    stmts.add(new FreeNode(e.getKey()));
//                    // Recursão para liberar os campos internos
//                    if (container instanceof StructFieldAccessNode fieldAccess) {
//                        String fieldName = fieldAccess.getFieldName();
//                        if ("rows".equals(fieldName)) {
//                            applyFreeRecursively(fieldAccess.getStructInstance());
//                        }
//                    }
//                }
//            }
//        }
//    }
//
//    private void applyFreeRecursively(ASTNode structInstance) {
//        // Lógica para fazer chamada recursiva para campos internos, como List<Row>
//        if (structInstance instanceof StructInstaceNode structNode) {
//            for (ASTNode child : structNode.getChildren()) {
//                if (child instanceof StructFieldAccessNode fieldAccess) {
//                    String fieldName = fieldAccess.getFieldName();
//                    // Verifica se o campo é uma lista (como 'rows' que é uma lista de Row)
//                    if ("rows".equals(fieldName)) {
//                        applyFreeRecursively(fieldAccess.getStructInstance()); // Recursão
//                    }
//                }
//            }
//        }
//    }
//
//}
