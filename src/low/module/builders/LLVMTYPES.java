package low.module.builders;

import low.module.builders.lists.LLVMArrayList;
import low.module.builders.primitives.*;
import low.module.builders.structs.LLVMStruct;

public sealed interface LLVMTYPES permits
        LLVMInt, LLVMDouble, LLVMFloat,
        LLVMBool, LLVMString,LLVMChar,
        LLVMPointer, LLVMStruct, LLVMArrayList {
}
