package low.variables;


import ast.structs.StructInstanceNode;
import ast.variables.VariableDeclarationNode;
import low.TempManager;
import low.main.TypeInfos;
import low.module.LLVisitorMain;
import low.module.builders.LLVMTYPES;
import low.module.builders.LLVMValue;
import low.module.builders.primitives.*;

public class ExpressionInitEmitter {

    private final TempManager temps;
    private final LLVisitorMain visitor;
    private final StoreEmitter store;

    public ExpressionInitEmitter(TempManager temps,
                                 LLVisitorMain visitor,
                                 StoreEmitter store) {
        this.temps = temps;
        this.visitor = visitor;
        this.store = store;
    }

        public LLVMValue emit(VariableDeclarationNode node, TypeInfos info) {

            LLVMTYPES targetType = info.getLLVMType();
            String varName = node.getName();

            if (node.getInitializer() instanceof StructInstanceNode
                    && visitor.escapesVar(varName)
                    && targetType.toString().endsWith("*")) {

                return emitEscapingStructInit(node, info);
            }

            LLVMValue val = node.getInitializer().accept(visitor);

            StringBuilder sb = new StringBuilder();
            sb.append(val.getCode());

            LLVMValue finalVal = val;

            if (!val.getType().getClass().equals(targetType.getClass())) {

                String castTmp = temps.newTemp();

                if (val.getType() instanceof LLVMDouble && targetType instanceof LLVMFloat) {
                    sb.append("  ").append(castTmp)
                            .append(" = fptrunc double ")
                            .append(val.getName()).append(" to float\n");

                    finalVal = new LLVMValue(targetType, castTmp, "");
                }

                else if (val.getType() instanceof LLVMInt && targetType instanceof LLVMDouble) {
                    sb.append("  ").append(castTmp)
                            .append(" = sitofp i32 ")
                            .append(val.getName()).append(" to double\n");

                    finalVal = new LLVMValue(targetType, castTmp, "");
                }

                else if (val.getType() instanceof LLVMDouble && targetType instanceof LLVMInt) {
                    sb.append("  ").append(castTmp)
                            .append(" = fptosi double ")
                            .append(val.getName()).append(" to i32\n");

                    finalVal = new LLVMValue(targetType, castTmp, "");
                }
            }

            //  STRING (deep copy)
            if (targetType instanceof LLVMString) {

                String tmpDataPtr = temps.newTemp();
                String tmpData    = temps.newTemp();
                String tmpClone   = temps.newTemp();

                sb.append("  ").append(tmpDataPtr)
                        .append(" = getelementptr %String, %String* ")
                        .append(val.getName())
                        .append(", i32 0, i32 0\n");

                sb.append("  ").append(tmpData)
                        .append(" = load i8*, i8** ")
                        .append(tmpDataPtr)
                        .append("\n");

                sb.append("  ").append(tmpClone)
                        .append(" = call %String* @createString(i8* ")
                        .append(tmpData)
                        .append(")\n");

                LLVMValue stored = new LLVMValue(new LLVMString(), tmpClone, "");
                sb.append(store.emit(varName, stored));

                return new LLVMValue(targetType, tmpClone, sb.toString());
            }

            //  STORE NORMAL
            sb.append(store.emit(varName, finalVal));

            return new LLVMValue(targetType, finalVal.getName(), sb.toString());
        }

        private LLVMValue emitEscapingStructInit(VariableDeclarationNode node, TypeInfos info) {
            throw new RuntimeException("emitEscapingStructInit ainda não migrado");
        }
    }