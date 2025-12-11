package low.variables;


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

        String exprLLVM = node.initializer.accept(visitor);
        String temp = extractor.extractTemp(exprLLVM);
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

        sb.append(store.emit(node.getName(), llvmType, temp));

        return sb.toString();
    }
}
