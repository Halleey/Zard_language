package low.variables;

import ast.inputs.InputNode;
import ast.lists.ListNode;
import ast.variables.AssignmentNode;
import ast.variables.LiteralNode;
import low.TempManager;
import low.inputs.InputEmitter;
import low.lists.ListEmitter;
import low.main.GlobalStringManager;
import low.module.LLVisitorMain;

import java.util.Map;
public class AssignmentEmitter {
    private final Map<String, String> varTypes;
    private final TempManager temps;
    private final GlobalStringManager globalStrings;
    private final LLVisitorMain visitor;

    public AssignmentEmitter(Map<String, String> varTypes, TempManager temps,
                             GlobalStringManager globalStrings, LLVisitorMain visitor) {
        this.varTypes = varTypes;
        this.temps = temps;
        this.globalStrings = globalStrings;
        this.visitor = visitor;
    }

    public String emit(AssignmentNode assignNode) {
        if (!varTypes.containsKey(assignNode.name)) {
            throw new RuntimeException("Variável não declarada: " + assignNode.name);
        }

        String llvmType = varTypes.get(assignNode.name);
        StringBuilder llvm = new StringBuilder();

        // LiteralNode
        if (assignNode.valueNode instanceof LiteralNode lit) {
            Object val = lit.value.getValue();
            switch (lit.value.getType()) {
                case "int" -> llvm.append("  store i32 ").append(val)
                        .append(", i32* %").append(assignNode.name).append("\n");
                case "double" -> {
                    if (val instanceof Integer i) val = i.doubleValue();
                    llvm.append("  store double ").append(val)
                            .append(", double* %").append(assignNode.name).append("\n");
                }
                case "boolean" -> llvm.append("  store i1 ")
                        .append(((Boolean) val) ? "1" : "0")
                        .append(", i1* %").append(assignNode.name).append("\n");
                case "string" -> {
                    String strName = globalStrings.getOrCreateString((String) val);
                    int len = ((String) val).length() + 2;
                    llvm.append("  store i8* getelementptr ([")
                            .append(len).append(" x i8], [").append(len).append(" x i8]* ")
                            .append(strName).append(", i32 0, i32 0), i8** %")
                            .append(assignNode.name).append("\n");
                }
                default -> throw new RuntimeException("Literal não suportado: " + lit.value.getType());
            }
            return llvm.toString();
        }

        // ListNode
        if (assignNode.valueNode instanceof ListNode listNode) {
            ListEmitter listEmitter = new ListEmitter(temps, globalStrings);
            String llvmList = listEmitter.emit(listNode, visitor);
            String temp = extractTemp(llvmList);
            llvm.append(llvmList)
                    .append("  store i8* ").append(temp)
                    .append(", i8** %").append(assignNode.name).append("\n");
            return llvm.toString();
        }

        // InputNode
        if (assignNode.valueNode instanceof InputNode inputNode) {
            InputEmitter inputEmitter = new InputEmitter(temps, globalStrings);
            String tmpDyn = temps.newTemp();
            String llvmInput = "  " + tmpDyn + " = call %DynValue* @input(i8* " +
                    (inputNode.getPrompt() != null && !inputNode.getPrompt().isEmpty()
                            ? "getelementptr ([" + (inputNode.getPrompt().length() + 2) + " x i8], [" +
                            (inputNode.getPrompt().length() + 2) + " x i8]* " +
                            globalStrings.getOrCreateString(inputNode.getPrompt()) + ", i32 0, i32 0)"
                            : "null") + ")\n";

            String tmp = temps.newTemp();
            String extract;
            switch (llvmType) {
                case "i32" -> extract = "  " + tmp + " = call i32 @dynValueToInt(%DynValue* " + tmpDyn + ")\n";
                case "double" -> extract = "  " + tmp + " = call double @dynValueToDouble(%DynValue* " + tmpDyn + ")\n";
                case "i1" -> extract = "  " + tmp + " = call i1 @dynValueToBool(%DynValue* " + tmpDyn + ")\n";
                case "i8*" -> extract = "  " + tmp + " = call i8* @dynValueToString(%DynValue* " + tmpDyn + ")\n";
                default -> throw new RuntimeException("Tipo desconhecido para input: " + llvmType);
            }

            return llvmInput + extract + "  store " + llvmType + " " + tmp + ", " + llvmType + "* %" + assignNode.name + "\n;;VAL:" + tmp + ";;TYPE:" + llvmType + "\n";
        }

        // Expressão complexa ou variável
        String exprLLVM = assignNode.valueNode.accept(visitor);
        String temp = extractTemp(exprLLVM);
        llvm.append(exprLLVM).append("\n")
                .append("  store ").append(llvmType).append(" ").append(temp)
                .append(", ").append(llvmType).append("* %").append(assignNode.name).append("\n");

        return llvm.toString();
    }

    private String extractTemp(String code) {
        int lastValIdx = code.lastIndexOf(";;VAL:");
        if (lastValIdx == -1) throw new RuntimeException("Não encontrou ;;VAL: em: " + code);
        int typeIdx = code.indexOf(";;TYPE:", lastValIdx);
        return code.substring(lastValIdx + 6, typeIdx).trim();
    }
}
