package low.variables.exps;

import ast.inputs.InputNode;
import ast.lists.ListNode;

import ast.variables.AssignmentNode;
import ast.variables.LiteralNode;
import context.statics.symbols.PrimitiveTypes;
import context.statics.symbols.StructType;
import context.statics.symbols.Type;
import low.TempManager;
import low.inputs.InputEmitter;
import low.lists.generics.ListEmitter;
import low.main.GlobalStringManager;
import low.main.TypeInfos;
import low.module.LLVisitorMain;
import low.variables.structs.StructCopyEmitter;

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

        Type type = info.getType(); // agora Type diretamente
        StringBuilder llvm = new StringBuilder();

        if (assignNode.valueNode instanceof LiteralNode lit) {
            Object val = lit.value.value();

            if (type.equals(PrimitiveTypes.DOUBLE) && val instanceof Integer i) {
                val = i.doubleValue();
            }

            if (type.equals(PrimitiveTypes.INT)) {
                llvm.append("  store i32 ").append(val).append(", i32* ").append(varPtr).append("\n");
            } else if (type.equals(PrimitiveTypes.DOUBLE)) {
                llvm.append("  store double ").append(val).append(", double* ").append(varPtr).append("\n");
            } else if (type.equals(PrimitiveTypes.FLOAT)) {
                String tmpDouble = temps.newTemp();
                String tmpFloat  = temps.newTemp();

                llvm.append("  ").append(tmpDouble)
                        .append(" = fadd double 0.0, ").append(val).append("\n");

                llvm.append("  ").append(tmpFloat)
                        .append(" = fptrunc double ").append(tmpDouble).append(" to float\n");

                llvm.append("  store float ").append(tmpFloat).append(", float* ").append(varPtr).append("\n");
                llvm.append(";;VAL:").append(tmpFloat).append(";;TYPE:float\n");
            } else if (type.equals(PrimitiveTypes.BOOL)) {
                llvm.append("  store i1 ").append((Boolean) val ? "1" : "0")
                        .append(", i1* ").append(varPtr).append("\n");
            } else if ("%String*".equals(info.getLLVMType()) || type.name().equals("%String")) {
                String s = (String) val;
                String strName = globalStrings.getOrCreateString(s);
                int len = globalStrings.getLength(s); // inclui '\0'
                String tmpNew = temps.newTemp();

                llvm.append("  ").append(tmpNew)
                        .append(" = call %String* @createString(i8* getelementptr ([")
                        .append(len).append(" x i8], [").append(len).append(" x i8]* ").append(strName)
                        .append(", i32 0, i32 0))\n");

                llvm.append("  store %String* ").append(tmpNew).append(", %String** ").append(varPtr).append("\n");
                llvm.append(";;VAL:").append(tmpNew).append(";;TYPE:%String*\n");
            } else {
                throw new RuntimeException("Tipo literal não suportado: " + type);
            }
            return llvm.toString();
        }

        if (assignNode.valueNode instanceof InputNode inputNode) {
            InputEmitter inputEmitter = new InputEmitter(temps, globalStrings);
            String llvmInput = inputEmitter.emit(inputNode, info.getLLVMType());
            String tmp = extractTemp(llvmInput);

            llvm.append(llvmInput);
            if ("%String*".equals(info.getLLVMType()) || type.name().equals("%String")) {
                llvm.append("  store %String* ").append(tmp).append(", %String** ").append(varPtr).append("\n");
            } else {
                llvm.append("  store ").append(info.getLLVMType()).append(" ").append(tmp)
                        .append(", ").append(info.getLLVMType()).append("* ").append(varPtr).append("\n");
            }
            return llvm.toString();
        }

        if (assignNode.valueNode instanceof ListNode listNode) {
            ListEmitter listEmitter = new ListEmitter(temps);
            String listLLVM = listEmitter.emit(listNode, visitor);
            String tmpList = extractTemp(listLLVM);

            llvm.append(listLLVM);
            llvm.append("  store ").append(info.getLLVMType()).append(" ").append(tmpList)
                    .append(", ").append(info.getLLVMType()).append("* ").append(varPtr).append("\n");
            return llvm.toString();
        }

        String exprLLVM = assignNode.valueNode.accept(visitor);
        String temp = extractTemp(exprLLVM);
        llvm.append(exprLLVM);

        if (type instanceof StructType) {
            StructCopyEmitter structCopyEmitter =
                    new StructCopyEmitter(varTypes, temps, globalStrings, visitor);
            llvm.append(structCopyEmitter.emit(assignNode, temp, varPtr, info));
            return llvm.toString();
        }

        if ("%String*".equals(info.getLLVMType()) || type.name().equals("%String")) {
            llvm.append("  store %String* ").append(temp).append(", %String** ").append(varPtr).append("\n");
        } else {
            llvm.append("  store ").append(info.getLLVMType()).append(" ").append(temp)
                    .append(", ").append(info.getLLVMType()).append("* ").append(varPtr).append("\n");
        }

        return llvm.toString();
    }

    private String extractTemp(String code) {
        int lastValIdx = code.lastIndexOf(";;VAL:");
        if (lastValIdx == -1)
            throw new RuntimeException("Não encontrou ;;VAL: em: " + code);
        int typeIdx = code.indexOf(";;TYPE:", lastValIdx);
        return code.substring(lastValIdx + 6, typeIdx).trim();
    }
}