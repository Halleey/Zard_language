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

    public LLVMValue emit(String name, LLVMValue value) {

        LLVMTYPES typeObj = value.getType();
        String ptr = getPtr(name);
        String storeCode;

        if (typeObj instanceof LLVMString) {
            storeCode = stringEmitter.emitStore(name, value);

        } else if (typeObj instanceof LLVMArrayList listType) {

            LLVMTYPES elem = listType.elementType();

            if (elem instanceof LLVMInt) {
                storeCode = "  store %struct.ArrayListInt* " + value.getName()
                        + ", %struct.ArrayListInt** " + ptr + "\n";

            } else if (elem instanceof LLVMDouble) {
                storeCode = "  store %struct.ArrayListDouble* " + value.getName()
                        + ", %struct.ArrayListDouble** " + ptr + "\n";

            } else if (elem instanceof LLVMBool) {
                storeCode = "  store %struct.ArrayListBool* " + value.getName()
                        + ", %struct.ArrayListBool** " + ptr + "\n";

            } else if (elem instanceof LLVMString) {
                storeCode = "  store %ArrayListString* " + value.getName()
                        + ", %ArrayListString** " + ptr + "\n";

            } else {
                // GENERIC / STRUCT LIST
                storeCode = "  store %ArrayList* " + value.getName()
                        + ", %ArrayList** " + ptr + "\n";
            }

        } else {
            storeCode = "  store " + typeObj + " " + value.getName()
                    + ", " + typeObj + "* " + ptr + "\n";
        }

        return new LLVMValue(typeObj, value.getName(), storeCode);
    }
}