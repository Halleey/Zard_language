//package low.functions;
//
//import ast.ASTNode;
//import ast.functions.FunctionNode;
//import low.TempManager;
//import low.module.LLVisitorMain;
//
//import java.util.List;
//
//public class FunctionEmitter {
//    private final LLVisitorMain visitor;
//    private final TempManager tempManager;
//
//    public FunctionEmitter(LLVisitorMain visitor, TempManager tempManager) {
//        this.visitor = visitor;
//        this.tempManager = tempManager;
//    }
//
//    public String emit(FunctionNode fn) {
//        StringBuilder ir = new StringBuilder();
//
//        String fnName = fn.getName();
//        List<String> params = fn.getParams();
//
//        ir.append("define i8* @").append(fnName).append("(");
//        for (int i = 0; i < params.size(); i++) {
//            ir.append("i8* %").append(params.get(i));
//            if (i < params.size() - 1) ir.append(", ");
//        }
//        ir.append(") {\n");
//
//        ir.append("entry:\n");
//
//        for (String param : params) {
//            String temp = tempManager.newTemp();
//            ir.append(temp).append(" = alloca i8*\n");
//            ir.append("  store i8* %").append(param).append(", i8** ").append(temp).append("\n");
//            visitor.registerLocalVariable(param, temp);
//        }
//
//        for (ASTNode stmt : fn.getBody()) {
//            ir.append(stmt.accept(visitor));
//        }
//
//        ir.append("  ret i8* null\n");
//        ir.append("}\n\n");
//
//        return ir.toString();
//    }
//}
