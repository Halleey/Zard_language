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
        // Pega ponteiro seguro
        String varPtr = visitor.varEmitter.getVarPtr(assignNode.name);
        String llvmType = varTypes.get(assignNode.name);
        if (llvmType == null) {
            throw new RuntimeException("Tipo LLVM não encontrado para variável: " + assignNode.name);
        }

        StringBuilder llvm = new StringBuilder();

        // === LiteralNode ===
        if (assignNode.valueNode instanceof LiteralNode lit) {
            Object val = lit.value.getValue();
            if ("double".equals(llvmType) && val instanceof Integer i) val = i.doubleValue();

            switch (llvmType) {
                case "i32" -> llvm.append("  store i32 ").append(val)
                        .append(", i32* ").append(varPtr).append("\n");
                case "double" -> llvm.append("  store double ").append(val)
                        .append(", double* ").append(varPtr).append("\n");
                case "i1" -> llvm.append("  store i1 ").append((Boolean) val ? "1" : "0")
                        .append(", i1* ").append(varPtr).append("\n");
                case "i8*" -> {
                    String strName = globalStrings.getOrCreateString((String) val);
                    int len = ((String) val).length() + 1;
                    llvm.append("  store i8* getelementptr ([")
                            .append(len).append(" x i8], [").append(len).append(" x i8]* ")
                            .append(strName).append(", i32 0, i32 0), i8** ")
                            .append(varPtr).append("\n");
                }
                case "%String" -> {
                    // LiteralNode com string já aloca struct %String
                    String strTmp = globalStrings.getOrCreateString((String) val); // pode reutilizar global
                    String tmpPtr = temps.newTemp();
                    llvm.append("  ").append(tmpPtr).append(" = alloca %String\n");
                    llvm.append("  %tdata = getelementptr inbounds %String, %String* ").append(tmpPtr).append(", i32 0, i32 0\n");
                    llvm.append("  store i8* ").append(strTmp).append(", i8** %tdata\n");
                    llvm.append("  %tlen = getelementptr inbounds %String, %String* ").append(tmpPtr).append(", i32 0, i32 1\n");
                    llvm.append("  store i64 ").append(((String) val).length()).append(", i64* %tlen\n");
                    llvm.append("  store %String* ").append(tmpPtr).append(", %String** ").append(varPtr).append("\n");
                }
            }
            return llvm.toString();
        }

        if (assignNode.valueNode instanceof InputNode inputNode) {
            InputEmitter inputEmitter = new InputEmitter(temps, globalStrings);
            String llvmInput = inputEmitter.emit(inputNode, llvmType);
            String tmp = extractTemp(llvmInput);

            if ("%String".equals(llvmType)) {
                llvm.append(llvmInput)
                        .append("  store %String* ").append(tmp)
                        .append(", %String** ").append(varPtr).append("\n");
            } else {
                llvm.append(llvmInput)
                        .append("  store ").append(llvmType).append(" ").append(tmp)
                        .append(", ").append(llvmType).append("* ").append(varPtr).append("\n");
            }
            return llvm.toString();
        }


        //  (opcional)
        if (assignNode.valueNode instanceof ListNode listNode) {
            ListEmitter listEmitter = new ListEmitter(temps);
            String listLLVM = listEmitter.emit(listNode, visitor);
            String tmpList = extractTemp(listLLVM);

            llvm.append(listLLVM)
                    .append("  store i8* ").append(tmpList)
                    .append(", i8** ").append(varPtr).append("\n");
            return llvm.toString();
        }

        String exprLLVM = assignNode.valueNode.accept(visitor);
        String temp = extractTemp(exprLLVM);

        if ("%String".equals(llvmType)) {
            llvm.append(exprLLVM)
                    .append("  store %String* ").append(temp)
                    .append(", %String** ").append(varPtr).append("\n");
        } else {
            llvm.append(exprLLVM)
                    .append("  store ").append(llvmType).append(" ").append(temp)
                    .append(", ").append(llvmType).append("* ").append(varPtr).append("\n");
        }

        return llvm.toString();
    }

    private String extractTemp(String code) {
        int lastValIdx = code.lastIndexOf(";;VAL:");
        if (lastValIdx == -1) throw new RuntimeException("Não encontrou ;;VAL: em: " + code);
        int typeIdx = code.indexOf(";;TYPE:", lastValIdx);
        return code.substring(lastValIdx + 6, typeIdx).trim();
    }
}