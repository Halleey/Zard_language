package low.variables;

import low.module.builders.LLVMTYPES;
import low.module.builders.LLVMValue;
import low.module.builders.lists.LLVMArrayList;
import low.module.builders.primitives.LLVMBool;
import low.module.builders.primitives.LLVMDouble;
import low.module.builders.primitives.LLVMInt;
import low.module.builders.primitives.LLVMString;

public class StoreEmitter {

    private final StringEmitter stringEmitter;
    private final VariableEmitter varEmitter;

    public StoreEmitter(StringEmitter stringEmitter, VariableEmitter varEmitter) {
        this.stringEmitter = stringEmitter;
        this.varEmitter = varEmitter;
    }

    private String getPtr(String name) {
        return varEmitter.getVarPtr(name);
    }

    public String emit(String name, LLVMValue value) {

        LLVMTYPES typeObj = value.getType();
        String type = typeObj.toString();
        String ptr = getPtr(name);

        if (typeObj instanceof LLVMString) {
            return stringEmitter.emitStore(name, value);
        }

        if (typeObj instanceof LLVMArrayList listType) {

            LLVMTYPES elem = listType.elementType();

            if (elem instanceof LLVMInt) {
                return "  store %struct.ArrayListInt* " + value.getName()
                        + ", %struct.ArrayListInt** " + ptr + "\n";
            }

            if (elem instanceof LLVMDouble) {
                return "  store %struct.ArrayListDouble* " + value.getName()
                        + ", %struct.ArrayListDouble** " + ptr + "\n";
            }

            if (elem instanceof LLVMBool) {
                return "  store %struct.ArrayListBool* " + value.getName()
                        + ", %struct.ArrayListBool** " + ptr + "\n";
            }

            if (elem instanceof LLVMString) {
                return "  store %ArrayListString* " + value.getName()
                        + ", %ArrayListString** " + ptr + "\n";
            }

            // ===== GENERIC / STRUCT LIST =====
            return "  store %ArrayList* " + value.getName()
                    + ", %ArrayList** " + ptr + "\n";
        }

        // ===== DEFAULT =====
        return "  store " + type + " " + value.getName()
                + ", " + type + "* " + ptr + "\n";
    }
}