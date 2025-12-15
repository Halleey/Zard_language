package low.variables.exps;

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
import low.variables.structs.StructCopyEmitter;

import java.util.Map;

public class AssignmentEmitter {

    private final Map<String, TypeInfos> varTypes;
    private final TempManager temps;
    private final GlobalStringManager globalStrings;
    private final LLVisitorMain visitor;

    public AssignmentEmitter(
            Map<String, TypeInfos> varTypes,
            TempManager temps,
            GlobalStringManager globalStrings,
            LLVisitorMain visitor
    ) {
        this.varTypes = varTypes;
        this.temps = temps;
        this.globalStrings = globalStrings;
        this.visitor = visitor;
    }

    public String emit(AssignmentNode assignNode) {

        String varPtr = visitor.varEmitter.getVarPtr(assignNode.name);
        TypeInfos info = varTypes.get(assignNode.name);

        if (info == null) {
            throw new RuntimeException(
                    "Tipo não encontrado para variável: " + assignNode.name
            );
        }

        String llvmType   = info.getLLVMType();
        String sourceType = info.getSourceType();

        StringBuilder llvm = new StringBuilder();

        /* ============================================================
         * Literais
         * ============================================================ */
        if (assignNode.valueNode instanceof LiteralNode lit) {

            Object val = lit.value.value();
            if ("double".equals(llvmType) && val instanceof Integer i) {
                val = i.doubleValue();
            }

            switch (llvmType) {

                case "i32" -> llvm.append("  store i32 ").append(val)
                        .append(", i32* ").append(varPtr).append("\n");

                case "double" -> llvm.append("  store double ").append(val)
                        .append(", double* ").append(varPtr).append("\n");

                case "float" -> llvm.append("  store float ").append(val)
                        .append(", float* ").append(varPtr).append("\n");

                case "i1" -> llvm.append("  store i1 ")
                        .append((Boolean) val ? "1" : "0")
                        .append(", i1* ").append(varPtr).append("\n");

                case "%String*" -> {
                    String s = (String) val;
                    String global = globalStrings.getOrCreateString(s);
                    int len = globalStrings.getLength(s);

                    String tmp = temps.newTemp();
                    llvm.append("  ").append(tmp)
                            .append(" = call %String* @createString(i8* getelementptr ([")
                            .append(len).append(" x i8], [")
                            .append(len).append(" x i8]* ")
                            .append(global).append(", i32 0, i32 0))\n");

                    llvm.append("  store %String* ").append(tmp)
                            .append(", %String** ").append(varPtr).append("\n");

                    llvm.append(";;VAL:").append(tmp).append(";;TYPE:%String*\n");
                }
            }

            return llvm.toString();
        }

        /* ============================================================
         * Input
         * ============================================================ */
        if (assignNode.valueNode instanceof InputNode inputNode) {

            InputEmitter inputEmitter =
                    new InputEmitter(temps, globalStrings);

            String code = inputEmitter.emit(inputNode, llvmType);
            String tmp  = extractTemp(code);

            llvm.append(code);

            if ("%String*".equals(llvmType)) {
                emitStringDeepCopy(llvm, tmp, varPtr);
                return llvm.toString();
            }

            llvm.append("  store ").append(llvmType).append(" ")
                    .append(tmp).append(", ")
                    .append(llvmType).append("* ").append(varPtr).append("\n");

            return llvm.toString();
        }

        /* ============================================================
         * List
         * ============================================================ */
        if (assignNode.valueNode instanceof ListNode listNode) {

            ListEmitter emitter = new ListEmitter(temps);
            String code = emitter.emit(listNode, visitor);
            String tmp  = extractTemp(code);

            llvm.append(code);
            llvm.append("  store ").append(llvmType).append(" ")
                    .append(tmp).append(", ")
                    .append(llvmType).append("* ").append(varPtr).append("\n");

            return llvm.toString();
        }

        /* ============================================================
         * Expressão geral
         * ============================================================ */
        String expr = assignNode.valueNode.accept(visitor);
        String temp = extractTemp(expr);
        llvm.append(expr);

        /* ---- Struct deep copy ---- */
        if (sourceType.startsWith("Struct<")) {

            StructCopyEmitter copier =
                    visitor.getStructCopyEmitter();

            String structName =
                    sourceType.substring(
                            sourceType.indexOf('<') + 1,
                            sourceType.indexOf('>')
                    ).trim();

            String copy = copier.emitDeepCopy(structName, temp);
            String out  = extractTemp(copy);

            llvm.append(copy);
            llvm.append("  store %").append(structName).append("* ")
                    .append(out).append(", %")
                    .append(structName).append("** ")
                    .append(varPtr).append("\n");

            return llvm.toString();
        }

        /* ---- String deep copy ---- */
        if ("%String*".equals(llvmType)) {
            emitStringDeepCopy(llvm, temp, varPtr);
            return llvm.toString();
        }

        /* ---- Fallback ---- */
        llvm.append("  store ").append(llvmType).append(" ")
                .append(temp).append(", ")
                .append(llvmType).append("* ")
                .append(varPtr).append("\n");

        return llvm.toString();
    }

    /* ============================================================
     * Helpers
     * ============================================================ */

    private void emitStringDeepCopy(
            StringBuilder llvm,
            String src,
            String dstPtr
    ) {
        // src->data
        String dataPtr = temps.newTemp();
        llvm.append("  ").append(dataPtr)
                .append(" = getelementptr inbounds %String, %String* ")
                .append(src).append(", i32 0, i32 0\n");

        String srcChars = temps.newTemp();
        llvm.append("  ").append(srcChars)
                .append(" = load i8*, i8** ").append(dataPtr).append("\n");

        // src->len
        String lenPtr = temps.newTemp();
        llvm.append("  ").append(lenPtr)
                .append(" = getelementptr inbounds %String, %String* ")
                .append(src).append(", i32 0, i32 1\n");

        String len = temps.newTemp();
        llvm.append("  ").append(len)
                .append(" = load i64, i64* ").append(lenPtr).append("\n");

        // malloc new buffer (len + 1)
        String size = temps.newTemp();
        llvm.append("  ").append(size)
                .append(" = add i64 ").append(len).append(", 1\n");

        String newBuf = temps.newTemp();
        llvm.append("  ").append(newBuf)
                .append(" = call i8* @malloc(i64 ").append(size).append(")\n");

        // memcpy
        llvm.append("  call void @llvm.memcpy.p0i8.p0i8.i64(")
                .append("i8* ").append(newBuf).append(", ")
                .append("i8* ").append(srcChars).append(", ")
                .append("i64 ").append(size).append(", i1 false)\n");

        // create new String
        String newStr = temps.newTemp();
        llvm.append("  ").append(newStr)
                .append(" = call %String* @createString(i8* ")
                .append(newBuf).append(")\n");

        llvm.append("  store %String* ").append(newStr)
                .append(", %String** ").append(dstPtr).append("\n");

        llvm.append(";;VAL:").append(newStr).append(";;TYPE:%String*\n");
    }

    private String extractTemp(String code) {
        int idx = code.lastIndexOf(";;VAL:");
        if (idx == -1) {
            throw new RuntimeException("Não encontrou ;;VAL: em:\n" + code);
        }
        int end = code.indexOf(";;TYPE:", idx);
        return code.substring(idx + 6, end).trim();
    }
}
