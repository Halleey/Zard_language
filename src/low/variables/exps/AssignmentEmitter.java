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
    import low.module.builders.LLVMTYPES;
    import low.module.builders.LLVMValue;
    import low.module.builders.primitives.*;
    import low.variables.StoreEmitter;
    import low.variables.structs.StructCopyEmitter;

    import java.util.Map;
    public class AssignmentEmitter {

        private final Map<String, TypeInfos> varTypes;
        private final TempManager temps;
        private final GlobalStringManager globalStrings;
        private final LLVisitorMain visitor;
        private final StoreEmitter storeEmitter;

        public AssignmentEmitter(Map<String, TypeInfos> varTypes,
                                 TempManager temps,
                                 GlobalStringManager globalStrings,
                                 LLVisitorMain visitor) {
            this.varTypes = varTypes;
            this.temps = temps;
            this.globalStrings = globalStrings;
            this.visitor = visitor;
            this.storeEmitter = visitor.getVariableEmitter().getStoreEmitter();
        }

        public LLVMValue emit(AssignmentNode assignNode) {

            String varName = assignNode.name;
            String varPtr = visitor.varEmitter.getVarPtr(varName);

            TypeInfos info = varTypes.get(varName);
            if (info == null) {
                throw new RuntimeException("Tipo não encontrado para variável: " + varName);
            }

            Type type = info.getType();
            LLVMTYPES llvmType = info.getLLVMType();

            StringBuilder llvm = new StringBuilder();

            // ===== LITERAL =====
            if (assignNode.valueNode instanceof LiteralNode lit) {

                Object val = lit.value.value();
                LLVMValue value;

                if (type.equals(PrimitiveTypes.INT)) {
                    value = new LLVMValue(new LLVMInt(), String.valueOf(val), "");
                }
                else if (type.equals(PrimitiveTypes.DOUBLE)) {
                    if (val instanceof Integer i) val = i.doubleValue();
                    value = new LLVMValue(new LLVMDouble(), String.valueOf(val), "");
                }
                else if (type.equals(PrimitiveTypes.FLOAT)) {

                    String tmpDouble = temps.newTemp();
                    String tmpFloat  = temps.newTemp();

                    llvm.append("  ").append(tmpDouble)
                            .append(" = fadd double 0.0, ").append(val).append("\n");

                    llvm.append("  ").append(tmpFloat)
                            .append(" = fptrunc double ").append(tmpDouble).append(" to float\n");

                    value = new LLVMValue(new LLVMFloat(), tmpFloat, "");
                }
                else if (type.equals(PrimitiveTypes.BOOL)) {
                    String v = ((Boolean) val) ? "1" : "0";
                    value = new LLVMValue(new LLVMBool(), v, "");
                }
                else if (type.equals(PrimitiveTypes.STRING)) {

                    String s = (String) val;
                    String strName = globalStrings.getOrCreateString(s);
                    int len = globalStrings.getLength(s);

                    String tmp = temps.newTemp();

                    llvm.append("  ").append(tmp)
                            .append(" = call %String* @createString(i8* getelementptr ([")
                            .append(len).append(" x i8], [")
                            .append(len).append(" x i8]* ").append(strName)
                            .append(", i32 0, i32 0))\n");

                    value = new LLVMValue(new LLVMString(), tmp, "");
                }
                else {
                    throw new RuntimeException("Tipo literal não suportado: " + type);
                }

                llvm.append(storeEmitter.emit(varName, value));

                return new LLVMValue(llvmType, varPtr, llvm.toString());
            }

            //  INPUT
            if (assignNode.valueNode instanceof InputNode inputNode) {

                InputEmitter inputEmitter = new InputEmitter(temps, globalStrings);
                LLVMValue val = inputEmitter.emit(inputNode, llvmType);

                llvm.append(val.getCode());
                llvm.append(storeEmitter.emit(varName, val));

                return new LLVMValue(llvmType, varPtr, llvm.toString());
            }

            //  LIST
            if (assignNode.valueNode instanceof ListNode listNode) {

                ListEmitter listEmitter = new ListEmitter(temps);
                LLVMValue listVal = listEmitter.emit(listNode, visitor);

                llvm.append(listVal.getCode());
                llvm.append(storeEmitter.emit(varName, listVal));

                return new LLVMValue(llvmType, varPtr, llvm.toString());
            }

            //  EXPRESSÃO
            LLVMValue exprVal = assignNode.valueNode.accept(visitor);
            llvm.append(exprVal.getCode());

            //STRUCT COPY
            if (type instanceof StructType) {
                StructCopyEmitter structCopyEmitter =
                        new StructCopyEmitter(varTypes, temps, globalStrings, visitor);

                llvm.append(structCopyEmitter.emit(assignNode, exprVal.getName(), varPtr, info));
                return new LLVMValue(llvmType, varPtr, llvm.toString());
            }

            //  STORE PADRÃO
            llvm.append(storeEmitter.emit(varName, exprVal));

            return new LLVMValue(llvmType, varPtr, llvm.toString());
        }
    }