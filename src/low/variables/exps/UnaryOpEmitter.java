package low.variables.exps;

import ast.ASTNode;
import ast.expressions.TypedValue;
import ast.variables.LiteralNode;
import context.statics.symbols.PrimitiveTypes;
import context.statics.symbols.Type;
import low.TempManager;
import ast.variables.VariableNode;
import low.main.TypeInfos;
import low.module.LLVMEmitVisitor;
import low.module.builders.LLVMTYPES;
import low.module.builders.LLVMValue;
import low.module.builders.primitives.LLVMDouble;
import low.module.builders.primitives.LLVMInt;
import low.variables.VariableEmitter;

import java.util.Map;
public class UnaryOpEmitter {

    private final Map<String, TypeInfos> varTypes;
    private final TempManager temps;
    private final VariableEmitter varEmitter;
    private final LLVMEmitVisitor visitor;

    public UnaryOpEmitter(Map<String, TypeInfos> varTypes, TempManager temps, VariableEmitter varEmitter, LLVMEmitVisitor visitor) {
        this.varTypes = varTypes;
        this.temps = temps;
        this.varEmitter = varEmitter;
        this.visitor = visitor;
    }

    public LLVMValue emit(String operator, ASTNode expr) {

        StringBuilder llvm = new StringBuilder();

        //  LITERAL
        if (expr instanceof LiteralNode literal) {

            TypedValue val = literal.getValue();
            Type t = val.type();

            LLVMTYPES llvmType;
            String valueStr;

            if (t == PrimitiveTypes.INT) {
                llvmType = new LLVMInt();
                valueStr = val.value().toString();
            }
            else if (t == PrimitiveTypes.DOUBLE) {
                llvmType = new LLVMDouble();
                valueStr = val.value().toString();
            }
            else {
                throw new RuntimeException(
                        "Unário " + operator + " não suportado para " + t);
            }

            String temp = temps.newTemp();

            switch (operator) {
                case "-" -> {
                    if (llvmType instanceof LLVMInt) {
                        llvm.append("  ").append(temp)
                                .append(" = sub i32 0, ").append(valueStr).append("\n");
                    } else {
                        llvm.append("  ").append(temp)
                                .append(" = fsub double 0.0, ").append(valueStr).append("\n");
                    }
                }

                case "+" -> {
                    if (llvmType instanceof LLVMInt) {
                        llvm.append("  ").append(temp)
                                .append(" = add i32 0, ").append(valueStr).append("\n");
                    } else {
                        llvm.append("  ").append(temp)
                                .append(" = fadd double 0.0, ").append(valueStr).append("\n");
                    }
                }

                default -> throw new RuntimeException(
                        "Operador unário " + operator + " não suportado para literal");
            }

            return new LLVMValue(llvmType, temp, llvm.toString());
        }

        // ===== EXPRESSÃO (inclui variável) =====
        LLVMValue val = expr.accept(visitor);
        llvm.append(val.getCode());

        LLVMTYPES type = val.getType();

        if (!(type instanceof LLVMInt || type instanceof LLVMDouble)) {
            throw new RuntimeException(
                    "Operador unário " + operator + " não suportado para tipo " + type);
        }

        String current = val.getName();

        switch (operator) {

            case "++" -> {
                if (!(expr instanceof VariableNode varNode)) {
                    throw new RuntimeException("++ requer variável");
                }

                String varName = varNode.getName();
                String ptr = varEmitter.getVarPtr(varName);

                String result = temps.newTemp();

                if (type instanceof LLVMInt) {
                    llvm.append("  ").append(result)
                            .append(" = add i32 ").append(current).append(", 1\n");
                } else {
                    llvm.append("  ").append(result)
                            .append(" = fadd double ").append(current).append(", 1.0\n");
                }

                llvm.append("  store ").append(type).append(" ").append(result)
                        .append(", ").append(type).append("* ").append(ptr).append("\n");

                return new LLVMValue(type, result, llvm.toString());
            }

            case "--" -> {
                if (!(expr instanceof VariableNode varNode)) {
                    throw new RuntimeException("-- requer variável");
                }

                String varName = varNode.getName();
                String ptr = varEmitter.getVarPtr(varName);

                String result = temps.newTemp();

                if (type instanceof LLVMInt) {
                    llvm.append("  ").append(result)
                            .append(" = sub i32 ").append(current).append(", 1\n");
                } else {
                    llvm.append("  ").append(result)
                            .append(" = fsub double ").append(current).append(", 1.0\n");
                }

                llvm.append("  store ").append(type).append(" ").append(result)
                        .append(", ").append(type).append("* ").append(ptr).append("\n");

                return new LLVMValue(type, result, llvm.toString());
            }

            case "+" -> {
                return new LLVMValue(type, current, llvm.toString());
            }

            case "-" -> {
                String result = temps.newTemp();

                if (type instanceof LLVMInt) {
                    llvm.append("  ").append(result)
                            .append(" = sub i32 0, ").append(current).append("\n");
                } else {
                    llvm.append("  ").append(result)
                            .append(" = fsub double 0.0, ").append(current).append("\n");
                }

                return new LLVMValue(type, result, llvm.toString());
            }
        }

        throw new RuntimeException(
                "Operador unário " + operator + " não suportado.");
    }
}