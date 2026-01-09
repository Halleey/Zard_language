package memory_manager.ownership.utils;

import ast.ASTNode;
import ast.structs.StructFieldAccessNode;
import ast.variables.VariableNode;
import context.statics.Symbol;

import java.util.ArrayList;
import java.util.List;

public class OwnershipUtils {

    public static Symbol resolveStructFieldTargetSymbol(StructFieldAccessNode sfa) {

        List<String> fields = new ArrayList<>();
        ASTNode base = sfa.getStructInstance();

        fields.add(sfa.getFieldName());

        while (base instanceof StructFieldAccessNode nested) {
            fields.add(0, nested.getFieldName());
            base = nested.getStructInstance();
        }

        if (base instanceof VariableNode v) {
            Symbol baseSym = v.getStaticContext().resolveVariable(v.getName());
            if (baseSym == null) return null;

            Symbol current = baseSym;
            for (String field : fields) {
                current = current.rebased(current.getName() + "." + field);
            }
            return current;
        }

        return null;
    }

    public static Symbol resolveListTargetSymbol(ASTNode node) {

        if (node instanceof VariableNode v) {
            return v.getStaticContext().resolveVariable(v.getName());
        }

        if (node instanceof StructFieldAccessNode sfa) {
            return resolveStructFieldTargetSymbol(sfa);
        }

        return null;
    }
}
