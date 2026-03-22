package low.structs;

import ast.structs.StructNode;
import ast.variables.VariableDeclarationNode;
import low.functions.TypeMapper;
import low.module.LLVisitorMain;
import low.module.builders.LLVMValue;
import low.module.builders.structs.LLVMStruct;
import low.structs.helpers.StructDefinitionEmitter;
import low.structs.helpers.StructFieldPrint;
import low.structs.helpers.StructTypeResolver;
import memory_manager.free.StructFreeEmitter;

import java.util.ArrayList;
import java.util.List;


public class StructEmitter {

    private final StructTypeResolver resolver;
    private final StructFieldPrint fieldEmitter;
    private final StructDefinitionEmitter defEmitter;
    private final LLVisitorMain visitorMain;
    private final StructFreeEmitter freeEmitter;

    public StructEmitter(LLVisitorMain visitorMain) {
        this.visitorMain = visitorMain;
        this.resolver = new StructTypeResolver(visitorMain);
        this.freeEmitter = new StructFreeEmitter(visitorMain, visitorMain.getTemps());
        this.fieldEmitter = new StructFieldPrint(resolver);
        this.defEmitter = new StructDefinitionEmitter(resolver);
    }

    public LLVMValue emit(StructNode node) {

        String llvmName = node.getLLVMName() != null && !node.getLLVMName().isBlank()
                ? node.getLLVMName()
                : node.getName();

        StringBuilder llvmCode = new StringBuilder();

        // Emissão da definição da struct (tipada)
        llvmCode.append(defEmitter.emitDefinition(node));

        // Função de print da struct
        llvmCode.append("define void @print_").append(llvmName)
                .append("(%").append(llvmName).append("* %p) {\nentry:\n");

        for (int i = 0; i < node.getFields().size(); i++) {
            fieldEmitter.emitPrintField(
                    i,
                    node.getFields().get(i).getType(),
                    llvmName,
                    visitorMain.getTemps(),
                    llvmCode
            );
        }

        llvmCode.append("  ret void\n}\n\n");

        // Emissão do free da struct
        llvmCode.append(freeEmitter.emit(node));

        // Retornamos um LLVMValue tipado para a struct
        LLVMStruct structType = new LLVMStruct(llvmName);
        return new LLVMValue(structType, "%p", llvmCode.toString());
    }
}