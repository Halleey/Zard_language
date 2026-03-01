package low.variables;


import ast.structs.StructInstanceNode;
import ast.variables.VariableDeclarationNode;
import low.TempManager;
import low.main.TypeInfos;
import low.module.LLVisitorMain;

public class ExpressionInitEmitter {

    private final TempManager temps;
    private final LLVisitorMain visitor;
    private final StoreEmitter store;
    private final TempExtractor extractor;

    public ExpressionInitEmitter(TempManager temps,
                                 LLVisitorMain visitor,
                                 StoreEmitter store,
                                 TempExtractor extractor) {
        this.temps = temps;
        this.visitor = visitor;
        this.store = store;
        this.extractor = extractor;
    }

    public String emit(VariableDeclarationNode node, TypeInfos info) {

        String llvmType = info.getLLVMType();
        String varName  = node.getName();

        if (node.initializer instanceof StructInstanceNode
                && visitor.escapesVar(varName)
                && llvmType.endsWith("*")) {

            return emitEscapingStructInit(node, info);
        }

        String exprLLVM = node.initializer.accept(visitor);
        String temp     = extractor.extractTemp(exprLLVM);
        String tempType = extractor.extractType(exprLLVM);

        StringBuilder sb = new StringBuilder(exprLLVM);

        if (!tempType.equals(llvmType)) {
            String castTmp = temps.newTemp();

            if (tempType.equals("double") && llvmType.equals("float")) {
                sb.append("  ").append(castTmp)
                        .append(" = fptrunc double ").append(temp).append(" to float\n");
                temp = castTmp;
            }
            else if (tempType.equals("i32") && llvmType.equals("double")) {
                sb.append("  ").append(castTmp)
                        .append(" = sitofp i32 ").append(temp).append(" to double\n");
                temp = castTmp;
            }
            else if (tempType.equals("double") && llvmType.equals("i32")) {
                sb.append("  ").append(castTmp)
                        .append(" = fptosi double ").append(temp).append(" to i32\n");
                temp = castTmp;
            }
        }


        if ("%String*".equals(llvmType)) {

            String tmpDataPtr = temps.newTemp();
            String tmpData    = temps.newTemp();
            String tmpClone   = temps.newTemp();

            sb.append("  ").append(tmpDataPtr)
                    .append(" = getelementptr %String, %String* ")
                    .append(temp)
                    .append(", i32 0, i32 0\n");

            sb.append("  ").append(tmpData)
                    .append(" = load i8*, i8** ")
                    .append(tmpDataPtr)
                    .append("\n");

            sb.append("  ").append(tmpClone)
                    .append(" = call %String* @createString(i8* ")
                    .append(tmpData)
                    .append(")\n");

            sb.append(store.emit(varName, llvmType, tmpClone));

            sb.append(";;VAL:")
                    .append(tmpClone)
                    .append(";;TYPE:%String*\n");

            return sb.toString();
        }

        sb.append(store.emit(varName, llvmType, temp));

        return sb.toString();
    }

    private String emitEscapingStructInit(VariableDeclarationNode node, TypeInfos info) {

        String llvmType    = info.getLLVMType();      // ex: %Item*
        String varName     = node.getName();
        String structPtrTy = llvmType;                // %Item*
        String structTy    = structPtrTy.substring(0, structPtrTy.length() - 1); // %Item

        String sizeGep = temps.newTemp();
        String sizeInt = temps.newTemp();
        String raw     = temps.newTemp();
        String heapPtr = temps.newTemp();

        StringBuilder sb = new StringBuilder();

        // calcula sizeof(%Item) via GEP null,1 + ptrtoint
        sb.append("  ").append(sizeGep)
                .append(" = getelementptr ").append(structTy)
                .append(", ").append(structTy).append("* null, i32 1\n");

        sb.append("  ").append(sizeInt)
                .append(" = ptrtoint ").append(structTy)
                .append("* ").append(sizeGep).append(" to i64\n");

        // malloc(i64 size)
        sb.append("  ").append(raw)
                .append(" = call i8* @malloc(i64 ").append(sizeInt).append(")\n");

        // bitcast i8* -> %Item*
        sb.append("  ").append(heapPtr)
                .append(" = bitcast i8* ").append(raw)
                .append(" to ").append(structPtrTy).append("\n");

        // guarda o ponteiro HEAP na variável
        sb.append(store.emit(varName, llvmType, heapPtr));

        return sb.toString();
    }
}
