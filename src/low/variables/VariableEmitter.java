package low.variables;
import ast.variables.LiteralNode;
import low.functions.TypeMapper;
import low.module.LLVisitorMain;
import low.TempManager;
import ast.variables.VariableDeclarationNode;

import java.util.HashMap;
import java.util.Map;

public class VariableEmitter {
    private final Map<String, String> varTypes; // nome -> LLVM type
    private final TempManager temps;
    private final LLVisitorMain visitor;
    private final Map<String, String> localVars = new HashMap<>();

    public VariableEmitter(Map<String, String> varTypes, TempManager temps, LLVisitorMain visitor) {
        this.varTypes = varTypes;
        this.temps = temps;
        this.visitor = visitor;
    }

    public String mapLLVMType(String type) {
        return new TypeMapper().toLLVM(type);
    }

    public String emitAlloca(VariableDeclarationNode node) {
        String llvmType = mapLLVMType(node.getType());
        // caso especial para string: aloca struct, não ponteiro
        if (node.getType().equals("string")) {
            llvmType = "%String";
        }
        varTypes.put(node.getName(), llvmType);
        String ptr = "%" + node.getName();
        localVars.put(node.getName(), ptr);

        return "  " + ptr + " = alloca " + llvmType + "\n;;VAL:" + ptr + ";;TYPE:" + llvmType + "\n";
    }


    public String emitInit(VariableDeclarationNode node) {
        String llvmType = varTypes.get(node.getName());
        if (node.initializer == null) {
            if ("List".equals(node.getType())) {
                return emitStore(node.getName(), llvmType, callArrayListCreate());
            }
            return "";
        }

        // caso especial: string literal
        if (node.getType().equals("string") && node.initializer instanceof LiteralNode lit) {
            String literal = (String) lit.value.getValue();
            String globalName = visitor.getGlobalStrings().getGlobalName(literal);
            int len = literal.length();

            StringBuilder sb = new StringBuilder();
            String varPtr = getVarPtr(node.getName());

            // ponteiro para os dados da string literal
            String tmp = temps.newTemp();
            sb.append("  ").append(tmp)
                    .append(" = getelementptr inbounds [")
                    .append(len + 1).append(" x i8], [").append(len + 1)
                    .append(" x i8]* ").append(globalName).append(", i32 0, i32 0\n")
                    .append(";;VAL:").append(tmp).append(";;TYPE:i8*\n");

            // inicializa campo .data
            String ptrField = temps.newTemp();
            sb.append("  ").append(ptrField)
                    .append(" = getelementptr inbounds %String, %String* ")
                    .append(varPtr).append(", i32 0, i32 0\n");
            sb.append("  store i8* ").append(tmp).append(", i8** ").append(ptrField).append("\n");

            // inicializa campo .length
            String lenField = temps.newTemp();
            sb.append("  ").append(lenField)
                    .append(" = getelementptr inbounds %String, %String* ")
                    .append(varPtr).append(", i32 0, i32 1\n");
            sb.append("  store i64 ").append(len).append(", i64* ").append(lenField).append("\n");

            return sb.toString();
        }

        // padrão para int/double/bool/lista
        String exprLLVM = node.initializer.accept(visitor);
        String temp = extractTemp(exprLLVM);
        return exprLLVM + emitStore(node.getName(), llvmType, temp);
    }


    private String emitStore(String name, String type, String value) {
        return "  store " + type + " " + value + ", " + type + "* %" + name + "\n";
    }

    private String callArrayListCreate() {
        String tmp = temps.newTemp();
        return tmp + " = call i8* @arraylist_create(i64 4)\n;;VAL:" + tmp + ";;TYPE:i8*\n";
    }

    public String emitLoad(String name) {
        String llvmType = varTypes.get(name); // deve ser i32, double, i1, i8*, etc.
        String ptr = localVars.get(name);
        String tmp = temps.newTemp();
        return "  " + tmp + " = load " + llvmType + ", " + llvmType + "* " + ptr
                + "\n;;VAL:" + tmp + ";;TYPE:" + llvmType + "\n";
    }


    private String extractTemp(String code) {
        int idx = code.lastIndexOf(";;VAL:");
        int endIdx = code.indexOf(";;TYPE:", idx);
        return code.substring(idx + 6, endIdx).trim();
    }

    private String extractType(String code) {
        int idx = code.indexOf(";;TYPE:");
        int endIdx = code.indexOf("\n", idx);
        return code.substring(idx + 7, endIdx == -1 ? code.length() : endIdx).trim();
    }

    public String getVarPtr(String name) {
        return localVars.get(name);
    }

    public void registerVarPtr(String name, String ptr) {
        localVars.put(name, ptr);
    }
}
