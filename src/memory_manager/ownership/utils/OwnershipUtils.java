package memory_manager.ownership.utils;

import ast.ASTNode;
import ast.structs.StructFieldAccessNode;
import ast.structs.StructNode;
import ast.variables.VariableNode;
import context.statics.StaticContext;
import context.statics.Symbol;
import context.statics.structs.StaticStructDefinition;

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
    public static StaticStructDefinition resolveStructDefFromSymbol(Symbol symbol) {

        System.out.println("[DEBUG] resolveStructDefFromSymbol");
        System.out.println("  symbol = " + symbol.getName());
        System.out.println("  type   = " + symbol.getType());

        String type = symbol.getType();

        switch (type) {
            case "int", "double", "float", "bool", "char", "string":
                System.out.println("  -> primitive, returning null");
                return null;
        }

        String structName = type;

        // 🔑 EXTRAÇÃO DO NOME REAL DO STRUCT
        if (type.startsWith("Struct<") && type.endsWith(">")) {
            structName = type.substring(
                    "Struct<".length(),
                    type.length() - 1
            );
        }

        StaticContext ctx = symbol.getDeclaredIn();
        System.out.println("  ctx kind = " + ctx.getKind());
        System.out.println("  resolving struct name = " + structName);

        try {
            StaticStructDefinition def = ctx.resolveStruct(structName);
            System.out.println("  -> resolved struct = " + def.getName());
            System.out.println("  -> isShared = " + def.isShared());
            return def;
        } catch (RuntimeException e) {
            System.out.println("  -> NOT a struct");
            return null;
        }
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
