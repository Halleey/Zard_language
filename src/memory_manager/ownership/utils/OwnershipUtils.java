package memory_manager.ownership.utils;

import ast.ASTNode;
import ast.structs.StructFieldAccessNode;
import ast.variables.VariableNode;
import context.statics.StaticContext;
import context.statics.symbols.PrimitiveTypes;
import context.statics.symbols.StructType;
import context.statics.symbols.Symbol;
import context.statics.structs.StaticStructDefinition;
import context.statics.symbols.Type;

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

        Type type = symbol.getType();
        System.out.println("  type   = " + type);

        // se é primitivo, não é struct
        if (type instanceof PrimitiveTypes) {
            System.out.println("  -> primitive, returning null");
            return null;
        }

        if (!(type instanceof StructType st)) {
            System.out.println("  -> not a struct, returning null");
            return null;
        }

        String structName = st.name();
        StaticContext ctx = symbol.getDeclaredIn();
        System.out.println("  ctx kind = " + ctx.getKind());
        System.out.println("  resolving struct name = " + structName);

        while (ctx != null) {
            try {
                StaticStructDefinition def = ctx.resolveStruct(structName);
                System.out.println("  -> resolved struct = " + def.getName());
                System.out.println("  -> isShared = " + def.isShared());
                return def;
            } catch (RuntimeException e) {
                ctx = ctx.getParent();
            }
        }

        System.out.println("  -> struct not found");
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
