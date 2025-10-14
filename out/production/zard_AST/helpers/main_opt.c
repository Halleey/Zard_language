#include <llvm-c/Core.h>
#include <llvm-c/Transforms/Scalar.h>
#include <llvm-c/Transforms/IPO.h>
#include <llvm-c/Transforms/Utils.h>
#include <llvm-c/Analysis.h>
#include <llvm-c/BitWriter.h>
#include <stdio.h>
#include <stdlib.h>

int main(int argc, char **argv) {
    if (argc < 3) {
        fprintf(stderr, "Uso: %s <entrada.ll> <saida.ll>\n", argv[0]);
        return 1;
    }

    const char* inputFile = argv[1];
    const char* outputFile = argv[2];

    char* errorMessage = NULL;

    //  Ler módulo LLVM
    LLVMModuleRef module = NULL;
    if (LLVMParseIRInContext(LLVMGetGlobalContext(), LLVMCreateMemoryBufferWithContentsOfFile(inputFile, &errorMessage) != 0, &module, &errorMessage)) {
        fprintf(stderr, "Erro ao ler IR: %s\n", errorMessage);
        return 1;
    }

    //  Criar PassManager
    LLVMPassManagerRef passManager = LLVMCreatePassManager();

    // Adicionar passes de otimização
    LLVMAddPromoteMemoryToRegisterPass(passManager);   // Mem2Reg
    LLVMAddInstructionCombiningPass(passManager);     // Simplifica instruções
    LLVMAddReassociatePass(passManager);              // Reassocia expressões
    LLVMAddGVNPass(passManager);                      // Remove redundâncias
    LLVMAddDeadCodeEliminationPass(passManager);      // Remove código morto

    // Executar passes no módulo
    LLVMRunPassManager(passManager, module);

    // Salvar módulo otimizado
    if (LLVMPrintModuleToFile(module, outputFile, &errorMessage) != 0) {
        fprintf(stderr, "Erro ao salvar módulo otimizado: %s\n", errorMessage);
        return 1;
    }

    printf("Módulo otimizado salvo em: %s\n", outputFile);

    // Cleanup
    LLVMDisposePassManager(passManager);
    LLVMDisposeModule(module);

    return 0;
}
