package low.module.builders;

import low.module.builders.lists.LLVMArrayList;
import low.module.builders.primitives.*;
import low.module.builders.structs.LLVMStruct;

public sealed interface LLVMTYPES permits LLVMPointer, LLVMArrayList, LLVMBool,
        LLVMChar, LLVMDouble, LLVMFloat, LLVMGeneric,
        LLVMInt, LLVMString, LLVMVoid, LLVMStruct {
}
