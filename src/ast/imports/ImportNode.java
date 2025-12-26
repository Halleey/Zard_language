package ast.imports;

import ast.ASTNode;
import ast.context.StaticContext;
import ast.functions.FunctionNode;
import ast.context.RuntimeContext;
import ast.expressions.TypedValue;
import ast.structs.ImplNode;
import ast.structs.StructNode;
import low.module.LLVMEmitVisitor;
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
    public String accept(LLVMEmitVisitor visitor) {
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
                    // sempre coloca no contexto do módulo
                    importCtx.declareVariable(funcNode.getName(),
                            new TypedValue("function", funcNode));

                    if (hasAlias) {
                        String qualifiedName = alias + "." + funcNode.getName();
                        ctx.declareVariable(qualifiedName,
                                new TypedValue("function", funcNode));
                    } else {
                        // sem alias: nome direto no escopo global
                        ctx.declareVariable(funcNode.getName(),
                                new TypedValue("function", funcNode));
                    }
                }

                else if (node instanceof StructNode structNode) {

                    // 1) registra o tipo de struct no contexto principal
                    ctx.registerStructType(structNode.getName(), structNode.getFields());

                    // (opcional, se quiser que o módulo importado também saiba de si mesmo)
                    importCtx.registerStructType(structNode.getName(), structNode.getFields());

                    // 2) mantém o que você já tinha (expor Struct como "valor" se quiser)
                    importCtx.declareVariable(structNode.getName(),
                            new TypedValue("struct", structNode));

                    if (hasAlias) {
                        String qualifiedName = alias + "." + structNode.getName();
                        ctx.declareVariable(qualifiedName,
                                new TypedValue("struct", structNode));
                    } else {
                        ctx.declareVariable(structNode.getName(),
                                new TypedValue("struct", structNode));
                    }

                    // debug opcional
                    for (VariableDeclarationNode field : structNode.getFields()) {
                        System.out.println("         - " + field.getType() + " " + field.getName());
                    }
                }


                else if (node instanceof ImplNode implNode) {

                    String targetStruct = implNode.getStructName();

                    System.out.println("[IMPORT] Registrando métodos do impl para Struct<" + targetStruct + ">");

                    for (FunctionNode fn : implNode.getMethods()) {

                        String methodName = fn.getName();

                        if (hasAlias) {
                            // alias: math.Set.add
                            ctx.registerStructMethod(alias + "." + targetStruct, methodName, fn);
                            System.out.println("   -> Método registrado: " + alias + "." + targetStruct + "." + methodName);
                        } else {
                            // global: Set.add
                            ctx.registerStructMethod(targetStruct, methodName, fn);
                            System.out.println("   -> Método registrado: " + targetStruct + "." + methodName);
                        }
                    }
                }


                else if (node instanceof VariableDeclarationNode varNode) {
                    // inicializa variável no contexto do módulo
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

            // só cria o "namespace" se tiver alias
            if (hasAlias) {
                ctx.declareVariable(alias, new TypedValue("namespace", importCtx));
            }

        } catch (IOException e) {
            throw new RuntimeException("Erro ao importar arquivo: " + path, e);
        }

        return new TypedValue("null", null);
    }


    @Override
    public void print(String prefix) {
        System.out.println(prefix + "ImportNode:");
        System.out.println(prefix + "  ├─ Path : \"" + path + "\"");
        System.out.println(prefix + "  └─ Alias: " + alias);
    }

    @Override
    public void bind(StaticContext stx) {

    }


    public String alias() {
        return alias;
    }

    public String path() {
        return path;
    }
}
