package low.functions;

import ast.ASTNode;
import ast.exceptions.ReturnNode;
import ast.functions.FunctionCallNode;
import ast.functions.FunctionNode;
import ast.variables.BinaryOpNode;
import ast.variables.LiteralNode;
import ast.variables.VariableDeclarationNode;
import ast.variables.VariableNode;
import low.module.LLVisitorMain;

import java.util.ArrayList;
import java.util.List;

public class FunctionEmitter {
    private final LLVisitorMain visitor;

    public FunctionEmitter(LLVisitorMain visitor) {
        this.visitor = visitor;
    }

    public String emit(FunctionNode fn) {
        StringBuilder sb = new StringBuilder();

        //  registra função imediatamente com tipo temporário "any" para permitir recursão
        visitor.registerFunctionType(fn.getName(), "any");
        System.out.println("[DEBUG] Função registrada inicialmente: " + fn.getName() + " -> any");


        for (int i = 0; i < fn.getParams().size(); i++) {
            String name = fn.getParams().get(i);
            String type = fn.getParamTypes().get(i);
            visitor.putVarType(name, type);
        }

        //  deduz tipo de retorno se necessário
        String llvmRetType = fn.getReturnType();
        if (llvmRetType.equals("void") && containsReturn(fn)) {
            // marca função como "em dedução" para recursão
            visitor.getCallEmitter().markBeingDeduced(fn.getName());
            llvmRetType = deduceReturnType(fn);
            visitor.getCallEmitter().unmarkBeingDeduced(fn.getName());
        }

        // converte para tipo LLVM
        llvmRetType = mapType(llvmRetType);

        // atualiza registro com tipo correto
        visitor.registerFunctionType(fn.getName(), llvmRetType);
        System.out.println("[DEBUG] Função atualizada: " + fn.getName() + " -> " + llvmRetType);

        // gera lista de parâmetros LLVM
        List<String> paramLLVM = new ArrayList<>();
        for (int i = 0; i < fn.getParams().size(); i++) {
            paramLLVM.add(mapType(fn.getParamTypes().get(i)) + " %" + fn.getParams().get(i));
        }

        sb.append("; === Função: ").append(fn.getName()).append(" ===\n");
        sb.append("define ").append(llvmRetType).append(" @").append(fn.getName())
                .append("(").append(String.join(", ", paramLLVM)).append(") {\nentry:\n");

        // aloca parâmetros e registra ponteiros
        for (int i = 0; i < fn.getParams().size(); i++) {
            String name = fn.getParams().get(i);
            String type = fn.getParamTypes().get(i);

            String ptrName = "%" + name + ".addr";
            VariableDeclarationNode paramNode = new VariableDeclarationNode(ptrName, type, null);
            sb.append(visitor.varEmitter.emitAlloca(paramNode));

            sb.append("  store ").append(mapType(type)).append(" %").append(name)
                    .append(", ").append(mapType(type)).append("* ").append(ptrName).append("\n");

            visitor.varEmitter.registerVarPtr(name, ptrName);
        }

        //  corpo da função
        for (ASTNode stmt : fn.getBody()) {
            sb.append(stmt.accept(visitor));
        }

        // ret void se necessário
        if (llvmRetType.equals("void") && !containsReturn(fn)) {
            sb.append("  ret void\n");
        }

        sb.append("}\n\n");
        return sb.toString();
    }

    private boolean containsReturn(FunctionNode fn) {
        for (ASTNode stmt : fn.getBody()) {
            if (stmt instanceof ReturnNode) return true;
        }
        return false;
    }

    private String deduceReturnType(FunctionNode fn) {
        for (ASTNode stmt : fn.getBody()) {
            if (stmt instanceof ReturnNode ret && ret.expr != null) {
                return inferType(ret.expr);
            }
        }
        return "void";
    }

    private String inferType(ASTNode node) {
        if (node instanceof LiteralNode lit) {
            return lit.value.getType();
        } else if (node instanceof VariableNode var) {
            String type = visitor.getVarType(var.getName());
            if (type == null) throw new RuntimeException("Variável não declarada: " + var.getName());
            return type;
        } else if (node instanceof BinaryOpNode bin) {
            String leftType = inferType(bin.left);
            String rightType = inferType(bin.right);
            if (!leftType.equals(rightType)) {
                if ((leftType.equals("int") && rightType.equals("double")) ||
                        (leftType.equals("double") && rightType.equals("int"))) {
                    return "double";
                } else {
                    throw new RuntimeException("Tipos incompatíveis na operação binária: " + leftType + " vs " + rightType);
                }
            }
            return leftType;
        } else if (node instanceof ReturnNode ret) {
            return inferType(ret.expr);
        } else if (node instanceof FunctionCallNode call) {
            String type = visitor.getFunctionType(call.getName());
            if (type == null) throw new RuntimeException("Função não registrada: " + call.getName());

            // Se a função estiver em dedução (recursiva), retorna tipo provisório
            if ("any".equals(type) && visitor.getCallEmitter().isBeingDeduced(call.getName())) {
                type = "int"; // provisório, seguro para operações como factorial(n) * n
            }
            return type;
        }
        throw new RuntimeException("Não foi possível inferir tipo de " + node.getClass());
    }

    private String mapType(String type) {
        return switch (type) {
            case "int" -> "i32";
            case "double" -> "double";
            case "boolean" -> "i1";
            case "string", "list", "var" -> "i8*";
            case "void" -> "void";
            default -> throw new RuntimeException("Tipo não suportado: " + type);
        };
    }
}