package low.variables;

import ast.inputs.InputNode;
import ast.lists.ListNode;
import ast.variables.AssignmentNode;
import ast.variables.LiteralNode;
import low.TempManager;
import low.inputs.InputEmitter;
import low.lists.generics.ListEmitter;
import low.main.GlobalStringManager;
import low.main.TypeInfos;
import low.module.LLVisitorMain;

import java.util.Map;


public class AssignmentEmitter {
    private final Map<String, TypeInfos> varTypes;
    private final TempManager temps;
    private final GlobalStringManager globalStrings;
    private final LLVisitorMain visitor;

    public AssignmentEmitter(Map<String, TypeInfos> varTypes, TempManager temps,
                             GlobalStringManager globalStrings, LLVisitorMain visitor) {
        this.varTypes = varTypes;
        this.temps = temps;
        this.globalStrings = globalStrings;
        this.visitor = visitor;
    }

    public String emit(AssignmentNode assignNode) {
        String varPtr = visitor.varEmitter.getVarPtr(assignNode.name);
        TypeInfos info = varTypes.get(assignNode.name);
        if (info == null) {
            throw new RuntimeException("Tipo não encontrado para variável: " + assignNode.name);
        }

        String llvmType = info.getLLVMType();
        String sourceType = info.getSourceType();
        StringBuilder llvm = new StringBuilder();

        if (assignNode.valueNode instanceof LiteralNode lit) {
            Object val = lit.value.value();
            if ("double".equals(llvmType) && val instanceof Integer i) val = i.doubleValue();

            switch (llvmType) {
                case "i32" -> llvm.append("  store i32 ").append(val)
                        .append(", i32* ").append(varPtr).append("\n");

                case "double" -> llvm.append("  store double ").append(val)
                        .append(", double* ").append(varPtr).append("\n");

                case "i1" -> llvm.append("  store i1 ").append((Boolean) val ? "1" : "0")
                        .append(", i1* ").append(varPtr).append("\n");

                case "i8*" -> {
                    String s = (String) val;
                    String strName = globalStrings.getOrCreateString(s);
                    int len = s.length() + 1;
                    llvm.append("  store i8* getelementptr ([")
                            .append(len).append(" x i8], [").append(len).append(" x i8]* ")
                            .append(strName).append(", i32 0, i32 0), i8** ")
                            .append(varPtr).append("\n");
                }

                case "%String*" -> {
                    String s = (String) val;
                    String strName = globalStrings.getOrCreateString(s);
                    int len = globalStrings.getLength(s); // inclui '\0'
                    String tmpNew = temps.newTemp();

                    llvm.append("  ").append(tmpNew)
                            .append(" = call %String* @createString(i8* getelementptr ([").append(len)
                            .append(" x i8], [").append(len).append(" x i8]* ").append(strName)
                            .append(", i32 0, i32 0))\n");

                    llvm.append("  store %String* ").append(tmpNew)
                            .append(", %String** ").append(varPtr).append("\n");

                    llvm.append(";;VAL:").append(tmpNew).append(";;TYPE:%String*\n");
                }
            }
            return llvm.toString();
        }

        if (assignNode.valueNode instanceof InputNode inputNode) {
            InputEmitter inputEmitter = new InputEmitter(temps, globalStrings);
            String llvmInput = inputEmitter.emit(inputNode, llvmType);
            String tmp = extractTemp(llvmInput);

            llvm.append(llvmInput);
            if ("%String".equals(llvmType) || "%String*".equals(llvmType)) {
                llvm.append("  store %String* ").append(tmp).append(", %String** ").append(varPtr).append("\n");
            } else {
                llvm.append("  store ").append(llvmType).append(" ").append(tmp)
                        .append(", ").append(llvmType).append("* ").append(varPtr).append("\n");
            }
            return llvm.toString();
        }

        if (assignNode.valueNode instanceof ListNode listNode) {
            ListEmitter listEmitter = new ListEmitter(temps);
            String listLLVM = listEmitter.emit(listNode, visitor);
            String tmpList = extractTemp(listLLVM);

            llvm.append(listLLVM);
            if (llvmType.equals("i8*")) {
                llvm.append("  store i8* ").append(tmpList).append(", i8** ").append(varPtr).append("\n");
            } else {
                llvm.append("  store ").append(llvmType).append(" ").append(tmpList)
                        .append(", ").append(llvmType).append("* ").append(varPtr).append("\n");
            }
            return llvm.toString();
        }

        String exprLLVM = assignNode.valueNode.accept(visitor);
        String temp = extractTemp(exprLLVM);

        llvm.append(exprLLVM);
        if ("%String".equals(llvmType) || "%String*".equals(llvmType)) {
            llvm.append("  store %String* ").append(temp).append(", %String** ").append(varPtr).append("\n");
        } else {
            llvm.append("  store ").append(llvmType).append(" ").append(temp)
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
