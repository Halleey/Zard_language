package low.variables;
import ast.inputs.InputNode;
import ast.lists.ListNode;
import low.inputs.InputEmitter;
import low.lists.ListEmitter;
import low.main.GlobalStringManager;
import low.module.LLVisitorMain;
import low.TempManager;
import ast.variables.VariableDeclarationNode;

import java.util.HashMap;
import java.util.Map;
public class VariableEmitter {
    private final Map<String, String> varTypes; // nome -> tipo LLVM
    private final TempManager temps;
    private final GlobalStringManager globalStrings;
    private final LLVisitorMain visitor;
    private final Map<String, String> localVars = new HashMap<>(); // nome -> ponteiro (%var)

    public VariableEmitter(Map<String, String> varTypes, TempManager temps, GlobalStringManager globalStrings, LLVisitorMain visitor) {
        this.varTypes = varTypes;
        this.temps = temps;
        this.globalStrings = globalStrings;
        this.visitor = visitor;
    }

    private String mapLLVMType(String type) {
        return switch (type) {
            case "int" -> "i32";
            case "double" -> "double";
            case "boolean" -> "i1";
            case "string", "list" -> "i8*";
            default -> throw new RuntimeException("Tipo desconhecido: " + type);
        };
    }

    public void registerVarPtr(String name, String ptr) {
        localVars.put(name, ptr);
    }

    public String emitAlloca(VariableDeclarationNode node) {
        String llvmType = mapLLVMType(node.getType());
        varTypes.put(node.getName(), llvmType);

        if ("list".equals(node.getType())) {
            visitor.registerListVar(node.getName());
        }

        String cleanName = node.getName();
        if (cleanName.startsWith("%")) cleanName = cleanName.substring(1);
        String ptrName = "%" + cleanName;
        localVars.put(node.getName(), ptrName);

        return "  " + ptrName + " = alloca " + llvmType + "\n;;VAL:" + ptrName + ";;TYPE:" + llvmType + "\n";
    }


    public String emitInit(VariableDeclarationNode node) {
        String llvmType = varTypes.get(node.getName());

        // Sem inicializador
        if (node.initializer == null) {
            if ("list".equals(node.getType())) {
                String tmp = temps.newTemp();
                return "  " + tmp + " = call i8* @arraylist_create(i64 4)\n" +
                        "  store i8* " + tmp + ", i8** %" + node.getName() + "\n";
            }
            return "";
        }


        if (node.initializer instanceof ast.variables.LiteralNode lit) {
            Object val = lit.value.getValue();
            if ("double".equals(llvmType) && val instanceof Integer) val = ((Integer) val).doubleValue();

            StringBuilder llvm = new StringBuilder();
            switch (llvmType) {
                case "i1" -> llvm.append("  store i1 ").append((Boolean) val ? "1" : "0")
                        .append(", i1* %").append(node.getName()).append("\n");
                case "i32" -> llvm.append("  store i32 ").append(val)
                        .append(", i32* %").append(node.getName()).append("\n");
                case "double" -> llvm.append("  store double ").append(val)
                        .append(", double* %").append(node.getName()).append("\n");
                case "i8*" -> {
                    String strName = globalStrings.getOrCreateString((String) val);
                    int len = ((String) val).length() + 1;
                    llvm.append("  store i8* getelementptr ([")
                            .append(len).append(" x i8], [").append(len).append(" x i8]* ")
                            .append(strName).append(", i32 0, i32 0), i8** %")
                            .append(node.getName()).append("\n");
                }
            }
            return llvm.toString();
        }


        if (node.initializer instanceof ListNode listNode) {
            ListEmitter listEmitter = new ListEmitter(temps, globalStrings);
            String llvmList = listEmitter.emit(listNode, visitor);
            String temp = extractTemp(llvmList);
            return llvmList + "  store i8* " + temp + ", i8** %" + node.getName() + "\n";
        }


        if (node.initializer instanceof InputNode inputNode) {
            InputEmitter inputEmitter = new InputEmitter(temps, globalStrings);
            String llvmInput = inputEmitter.emit(inputNode, llvmType);
            String tmp = extractTemp(llvmInput);
            return llvmInput +
                    "  store " + llvmType + " " + tmp + ", " + llvmType + "* %" + node.getName() + "\n";
        }

        // Expressão complexa
        String exprLLVM = node.initializer.accept(visitor);
        String temp = extractTemp(exprLLVM);
        return exprLLVM + "  store " + llvmType + " " + temp + ", " + llvmType + "* %" + node.getName() + "\n";
    }

    public String emitLoad(String name) {
        String type = varTypes.get(name);
        if (type == null) throw new RuntimeException("Variável não alocada: " + name);

        // Só remapear se for tipo AST (int, double, boolean, string, list)
        switch (type) {
            case "int" -> type = "i32";
            case "double" -> type = "double";
            case "boolean" -> type = "i1";
            case "string", "list" -> type = "i8*";
            default -> {} // tipo já em LLVM ou void, não remapeia
        }

        String tmp = temps.newTemp();
        String ptr = localVars.get(name);
        if (ptr == null) throw new RuntimeException("Variável não alocada: " + name);
        return "  " + tmp + " = load " + type + ", " + type + "* " + ptr + "\n;;VAL:" + tmp + ";;TYPE:" + type + "\n";
    }


    // Extrai o valor temporário gerado em LLVM
    private String extractTemp(String code) {
        int lastValIdx = code.lastIndexOf(";;VAL:");
        if (lastValIdx == -1) throw new RuntimeException("Não encontrou ;;VAL: em: " + code);
        int typeIdx = code.indexOf(";;TYPE:", lastValIdx);
        return code.substring(lastValIdx + 6, typeIdx).trim();
    }

    // Retorna ponteiro local da variável
    public String getVarPtr(String name) {
        return localVars.get(name);
    }
}
