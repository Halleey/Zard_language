package low.imports;

import ast.ASTNode;
import ast.functions.FunctionNode;
import ast.functions.ParamInfo;
import ast.ifstatements.IfNode;
import ast.imports.ImportNode;
import ast.loops.WhileNode;
import ast.structs.ImplNode;
import ast.structs.StructNode;
import ast.variables.VariableDeclarationNode;
import ast.lists.ListNode;

import ast.lists.ListAddNode;
import ast.lists.ListAddAllNode;

import context.statics.symbols.*;
import low.functions.FunctionEmitter;
import low.main.TypeInfos;
import low.module.LLVisitorMain;
import low.module.builders.LLVMTYPES;
import low.module.builders.LLVMValue;
import low.module.builders.lists.LLVMArrayList;
import low.module.builders.primitives.*;
import low.module.builders.structs.LLVMStruct;
import low.structs.StructEmitter;
import tokens.Lexer;
import tokens.Token;
import translate.front.Parser;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;


public class ImportEmitter {

    private final LLVisitorMain visitor;
    private final Set<Type> tiposDeListasUsados;

    public ImportEmitter(LLVisitorMain visitor, Set<Type> tiposDeListasUsados) {
        this.visitor = visitor;
        this.tiposDeListasUsados = tiposDeListasUsados;
    }
    public LLVMValue emit(ImportNode node) {
        try {
            String code = Files.readString(Path.of(node.path()));
            Lexer lexer = new Lexer(code);
            List<Token> tokens = lexer.tokenize();
            Parser parser = new Parser(tokens);
            List<ASTNode> imported = parser.parse();

            StringBuilder llvm = new StringBuilder();

            // coletar tipos de listas
            for (ASTNode n : imported) coletarListas(n);

            // structs
            for (ASTNode n : imported) {
                if (n instanceof StructNode struct) {
                    visitor.registerStructNode(struct.getName(), struct);
                    LLVMValue structVal = new StructEmitter(visitor).emit(struct);
                    visitor.addStructDefinition(structVal);
                    llvm.append(structVal.getCode()).append("\n");
                }
            }

            // impls
            for (ASTNode n : imported) {
                if (n instanceof ImplNode impl) {
                    LLVMValue codeImpl = impl.accept(visitor);  // já LLVMValue
                    visitor.addImplDefinition(codeImpl);
                    llvm.append(codeImpl.getCode()).append("\n");
                }
            }

            // funções
            FunctionEmitter fnEmitter = new FunctionEmitter(visitor);
            for (ASTNode n : imported) {
                if (n instanceof FunctionNode fn) {
                    String name = fn.getName();
                    visitor.registerImportedFunction(name, fn);

                    Type returnType = fn.getReturnType();
                    LLVMTYPES llvmRetType;

                    // mapear retorno de lista
                    if (returnType instanceof ListType listType) {
                        Type elem = listType.elementType();
                        if (elem == PrimitiveTypes.INT) llvmRetType = new LLVMArrayList(new LLVMInt());
                        else if (elem == PrimitiveTypes.DOUBLE) llvmRetType = new LLVMArrayList(new LLVMDouble());
                        else if (elem == PrimitiveTypes.BOOL) llvmRetType = new LLVMArrayList(new LLVMBool());
                        else if (elem == PrimitiveTypes.STRING) llvmRetType = new LLVMArrayList(new LLVMString());
                        else llvmRetType = new LLVMArrayList(new LLVMStruct(elem.name()));
                    } else if (returnType == PrimitiveTypes.INT) llvmRetType = new LLVMInt();
                    else if (returnType == PrimitiveTypes.DOUBLE) llvmRetType = new LLVMDouble();
                    else if (returnType == PrimitiveTypes.FLOAT) llvmRetType = new LLVMFloat();
                    else if (returnType == PrimitiveTypes.BOOL) llvmRetType = new LLVMBool();
                    else if (returnType == PrimitiveTypes.STRING) llvmRetType = new LLVMString();
                    else if (returnType instanceof StructType structType) llvmRetType = new LLVMStruct(structType.name());
                    else throw new RuntimeException("Tipo de retorno não suportado: " + returnType);

                    visitor.registerFunctionType(name, new TypeInfos(returnType, llvmRetType));

                    LLVMValue fnVal = fnEmitter.emit(fn);
                    llvm.append(fnVal.getCode()).append("\n");
                }
            }

            // retornar LLVMValue tipado void (imports não produzem valor)
            return new LLVMValue(new LLVMVoid(), "%import_" + node.alias(), llvm.toString());

        } catch (IOException e) {
            throw new RuntimeException("Failed to import module: " + node.path(), e);
        }
    }

    private void coletarListas(ASTNode node) {
        if (node == null) return;

        if (node instanceof FunctionNode func) {
            Type retType = func.getReturnType();
            if (retType instanceof ListType listType) registrarLista(listType);

            for (ParamInfo param : func.getParameters()) {
                Type pType = param.type();
                if (pType instanceof ListType listType) registrarLista(listType);
            }

            for (ASTNode stmt : func.getBody()) coletarListas(stmt);
        }

        for (ASTNode child : node.getChildren()) coletarListas(child);

        if (node instanceof VariableDeclarationNode v) {
            Type t = v.getResolvedType();
            if (t instanceof ListType listType) registrarLista(listType);
            if (v.getInitializer() != null) coletarListas(v.getInitializer());
        }

        if (node instanceof ListNode list) {
            Type type = list.getType();
            if (type instanceof ListType listType) registrarLista(listType);
            list.getList().getElements().forEach(this::coletarListas);
        }

        if (node instanceof IfNode i) {
            coletarListas(i.condition);
            i.thenBranch.forEach(this::coletarListas);
            if (i.elseBranch != null) i.elseBranch.forEach(this::coletarListas);
        }

        if (node instanceof WhileNode w) {
            coletarListas(w.condition);
            w.body.forEach(this::coletarListas);
        }

        if (node instanceof ListAddNode add) {
            coletarListas(add.getListNode());
            coletarListas(add.getValuesNode());
        }

        if (node instanceof ListAddAllNode addAll) {
            addAll.getArgs().forEach(this::coletarListas);
        }
    }

    private void registrarLista(ListType listType) {
        tiposDeListasUsados.add(listType.elementType());
    }
}