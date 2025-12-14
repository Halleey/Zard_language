package low.variables;


import ast.structs.StructInstaceNode;
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

        if (node.initializer instanceof StructInstaceNode
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

        sb.append(store.emit(varName, llvmType, temp));

        return sb.toString();
    }
    private String emitEscapingStructInit(VariableDeclarationNode node, TypeInfos info) {

        // ex: %Set_int*
        String structPtrTy = info.getLLVMType();
        String structTy    = structPtrTy.substring(0, structPtrTy.length() - 1);

        // 1) malloc sizeof(struct)
        String sizeGep = temps.newTemp();
        String sizeInt = temps.newTemp();
        String raw     = temps.newTemp();
        String heapPtr = temps.newTemp();

        StringBuilder sb = new StringBuilder();

        sb.append("  ").append(sizeGep)
                .append(" = getelementptr ").append(structTy)
                .append(", ").append(structTy).append("* null, i32 1\n");

        sb.append("  ").append(sizeInt)
                .append(" = ptrtoint ").append(structTy)
                .append("* ").append(sizeGep).append(" to i64\n");

        sb.append("  ").append(raw)
                .append(" = call i8* @malloc(i64 ").append(sizeInt).append(")\n");

        sb.append("  ").append(heapPtr)
                .append(" = bitcast i8* ").append(raw)
                .append(" to ").append(structPtrTy).append("\n");

        // 2) ***AQUI ESTÁ A CORREÇÃO***: inicializar os campos do struct no heapPtr
        //    Hoje você não faz nada → campo List fica lixo/null → segfault
        if (node.initializer instanceof StructInstaceNode sin) {
            sb.append(emitStructInstanceIntoPointer(sin, structTy, heapPtr));
        } else {
            throw new RuntimeException("emitEscapingStructInit só suporta StructInstaceNode");
        }

        // 3) store do ponteiro heap na variável
        sb.append(store.emit(node.getName(), structPtrTy, heapPtr));

        return sb.toString();
    }

    private String emitStructInstanceIntoPointer(StructInstaceNode sin,
                                                 String structTy,
                                                 String structPtr) {

        // Para seu Set: campo 0 é a lista.
        // Se você tiver mais campos, você expande isso com resolver de índice.
        StringBuilder sb = new StringBuilder();

        // Se veio com valores no struct literal, você pode tratar aqui depois.
        // Por enquanto: garante lista SEMPRE inicializada (zera segfault).
        String fieldPtr = temps.newTemp();
        String listTmp  = temps.newTemp();

        // Escolha do runtime baseado no tipo concreto (ex: %Set_int = usa ArrayListInt)
        // Regra prática: se structTy termina com "_int" -> cria int list.
        // Se você já tem resolver melhor, use ele.
        boolean isIntSpec = structTy.endsWith("_int");

        if (isIntSpec) {
            sb.append("  ").append(listTmp)
                    .append(" = call %struct.ArrayListInt* @arraylist_create_int(i64 10)\n");
            sb.append(";;VAL:").append(listTmp).append(";;TYPE:%struct.ArrayListInt*\n");

            sb.append("  ").append(fieldPtr)
                    .append(" = getelementptr inbounds ").append(structTy)
                    .append(", ").append(structTy).append("* ").append(structPtr)
                    .append(", i32 0, i32 0\n");

            sb.append("  store %struct.ArrayListInt* ").append(listTmp)
                    .append(", %struct.ArrayListInt** ").append(fieldPtr).append("\n");
        } else {
            sb.append("  ").append(listTmp)
                    .append(" = call %ArrayList* @arraylist_create(i64 10)\n");
            sb.append(";;VAL:").append(listTmp).append(";;TYPE:%ArrayList*\n");

            sb.append("  ").append(fieldPtr)
                    .append(" = getelementptr inbounds ").append(structTy)
                    .append(", ").append(structTy).append("* ").append(structPtr)
                    .append(", i32 0, i32 0\n");

            sb.append("  store %ArrayList* ").append(listTmp)
                    .append(", %ArrayList** ").append(fieldPtr).append("\n");
        }

        return sb.toString();
    }


}
