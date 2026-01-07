package memory_manager.ownership.utils;

import ast.ASTNode;
import ast.structs.StructFieldAccessNode;
import ast.variables.VariableNode;

public class OwnershipUtils {

    public static String resolveStructFieldTarget(StructFieldAccessNode sfa) {

        StringBuilder sb = new StringBuilder();
        ASTNode base = sfa.getStructInstance();

        while (base instanceof StructFieldAccessNode nested) {
            sb.insert(0, "." + nested.getFieldName());
            base = nested.getStructInstance();
        }

        if (base instanceof VariableNode v) {
            sb.insert(0, v.getName());
        } else {
            sb.insert(0, "<anonymous>");
        }

        sb.append(".").append(sfa.getFieldName());
        return sb.toString();
    }


    public static String resolveListTarget(ASTNode node) {

        if (node instanceof VariableNode v) {
            return v.getName();
        }

        if (node instanceof StructFieldAccessNode sfa) {
            return resolveStructFieldTarget(sfa);
        }

        return "<anonymous-list>";
    }

}
