package ast.imports;

import ast.ASTNode;
import ast.functions.ParamInfo;
import context.statics.StaticContext;
import ast.functions.FunctionNode;
import context.runtime.RuntimeContext;
import ast.expressions.TypedValue;
import ast.structs.ImplNode;
import ast.structs.StructNode;
import context.statics.structs.StaticStructDefinition;
import context.statics.symbols.FunctionType;
import context.statics.symbols.PrimitiveTypes;
import context.statics.symbols.StructType;
import context.statics.symbols.Type;
import low.module.LLVMEmitVisitor;
import low.module.builders.LLVMValue;
import tokens.Lexer;
import tokens.Token;
import translate.front.Parser;
import ast.variables.VariableDeclarationNode;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;


public class ImportNode extends ASTNode {
    private final String path;
    private final String alias;

    public ImportNode(String path, String alias) {
        this.path = path;
        this.alias = alias; // pode ser null ou vazio
    }


    @Override
    public LLVMValue accept(LLVMEmitVisitor visitor) {
        return visitor.visit(this);
    }

    @Override
    public TypedValue evaluate(RuntimeContext ctx) {
        try {

            String code = Files.readString(Path.of(path));
            Lexer lexer = new Lexer(code);
            List<Token> tokens = lexer.tokenize();

            Parser parser = new Parser(tokens);
            List<ASTNode> ast = parser.parse();

            RuntimeContext importCtx = new RuntimeContext();

            boolean hasAlias = alias != null && !alias.isEmpty();

            for (ASTNode node : ast) {


                if (node instanceof FunctionNode funcNode) {

                    FunctionType fnType = new FunctionType(
                            funcNode.getParameters()
                                    .stream()
                                    .map(ParamInfo::typeObj)
                                    .toList(),
                            funcNode.getReturnType()
                    );

                    importCtx.declareVariable(
                            funcNode.getName(),
                            new TypedValue(fnType, funcNode)
                    );

                    if (hasAlias) {

                        String qualifiedName = alias + "." + funcNode.getName();

                        ctx.declareVariable(
                                qualifiedName,
                                new TypedValue(fnType, funcNode)
                        );

                    } else {

                        ctx.declareVariable(
                                funcNode.getName(),
                                new TypedValue(fnType, funcNode)
                        );
                    }
                }

                else if (node instanceof StructNode structNode) {

                    String structName = structNode.getName();

                    ctx.registerStructType(structName, structNode.getFields());
                    importCtx.registerStructType(structName, structNode.getFields());

                    StructType structType = new StructType(structName);

                    importCtx.declareVariable(structName, new TypedValue(structType, structNode));

                    if (hasAlias) {

                        String qualifiedName = alias + "." + structName;

                        ctx.declareVariable(qualifiedName, new TypedValue(structType, structNode));

                    } else {
                        ctx.declareVariable(structName, new TypedValue(structType, structNode));
                    }

                    for (VariableDeclarationNode field : structNode.getFields()) {
                        System.out.println(
                                "         - " +
                                        field.getType() +
                                        " " +
                                        field.getName()
                        );
                    }
                }

                else if (node instanceof ImplNode implNode) {

                    String targetStruct = implNode.getStructName();

                    System.out.println("[IMPORT] Registrando métodos do impl para Struct<" + targetStruct + ">");

                    for (FunctionNode fn : implNode.getMethods()) {
                        String methodName = fn.getName();
                        if (hasAlias) {

                            ctx.registerStructMethod(alias + "." + targetStruct, methodName, fn
                            );

                            System.out.println("   -> Método registrado: " + alias + "." + targetStruct + "." + methodName);

                        } else {

                            ctx.registerStructMethod(
                                    targetStruct,
                                    methodName,
                                    fn
                            );

                            System.out.println(
                                    "   -> Método registrado: " +
                                            targetStruct +
                                            "." +
                                            methodName
                            );
                        }
                    }
                }

                else if (node instanceof VariableDeclarationNode varNode) {

                    varNode.evaluate(importCtx);

                    TypedValue val = importCtx.getVariable(varNode.getName());

                    if (hasAlias) {

                        String qualifiedName = alias + "." + varNode.getName();
                        ctx.declareVariable(qualifiedName, val);
                    } else {
                        ctx.declareVariable(varNode.getName(), val);
                    }
                }
            }

            if (hasAlias) {
                ctx.declareVariable(alias, new TypedValue(PrimitiveTypes.ANY, importCtx));
            }

        }
        catch (IOException e) {
            throw new RuntimeException("Erro ao importar arquivo: " + path,e);
        }
        return new TypedValue(PrimitiveTypes.VOID, null);
    }


    @Override
    public void print(String prefix) {
        System.out.println(prefix + "ImportNode:");
        System.out.println(prefix + "  ├─ Path : \"" + path + "\"");
        System.out.println(prefix + "  └─ Alias: " + alias);
    }

    @Override
    public void bindChildren(StaticContext stx) {

        try {
            String code = Files.readString(Path.of(path));

            Lexer lexer = new Lexer(code);
            Parser parser = new Parser(lexer.tokenize());
            List<ASTNode> ast = parser.parse();

            boolean hasAlias = alias != null && !alias.isEmpty();

            for (ASTNode node : ast) {

                if (node instanceof StructNode structNode) {

                    String name = hasAlias
                            ? alias + "." + structNode.getName()
                            : structNode.getName();

                    stx.declareStruct(
                            name,
                            StaticStructDefinition.fromAST(structNode)
                    );
                }
            }

            for (ASTNode node : ast) {

                if (node instanceof FunctionNode fn) {
                    stx.declareFunction(fn);
                }
            }

            for (ASTNode node : ast) {

                if (node instanceof ImplNode impl) {

                    String structName = impl.getStructName();

                    if (hasAlias) {
                        structName = alias + "." + structName;
                    }

                    for (FunctionNode method : impl.getMethods()) {

                        stx.registerStructMethod(
                                structName,
                                method
                        );
                    }
                }
            }

            for (ASTNode node : ast) {

                if (node instanceof VariableDeclarationNode var) {

                    Type resolvedType = stx.resolveType(var.getType());

                    stx.declareVariable(
                            hasAlias
                                    ? alias + "." + var.getName()
                                    : var.getName(),
                            resolvedType
                    );
                }
            }

            for (ASTNode node : ast) {
                node.bind(stx);
            }

        } catch (IOException e) {
            throw new RuntimeException("Erro ao bindar import: " + path, e);
        }
    }

    public String alias() {
        return alias;
    }

    public String path() {
        return path;
    }
}
