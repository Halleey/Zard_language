package low.module;
import ast.ASTNode;
import ast.exceptions.BreakNode;
import ast.exceptions.ReturnNode;
import ast.expressions.BinaryOpNode;
import ast.expressions.CompoundAssignmentNode;
import ast.expressions.UnaryOpNode;
import ast.functions.FunctionCallNode;
import ast.functions.FunctionNode;
import ast.home.MainAST;
import ast.ifstatements.IfNode;
import ast.imports.ImportNode;
import ast.inputs.InputNode;
import ast.loops.ForNode;
import ast.structs.*;
import ast.lists.*;
import ast.loops.WhileNode;

import ast.prints.PrintNode;
import ast.variables.*;
import context.statics.symbols.Type;
import low.TempManager;
import low.functions.FunctionCallEmitter;
import low.imports.ImportEmitter;
import low.main.GlobalStringManager;

import low.main.MainEmitter;
import low.main.TypeInfos;
import low.module.builders.LLVMValue;
import low.module.flow.FlowControllVisitor;
import low.module.imports.ImportRegistry;
import low.module.lists.ListVisitor;
import low.module.structs.StructRegistry;
import low.module.structs.StructTypeResolver;
import low.prints.PrintEmitter;
import low.structs.*;
import low.variables.*;
import low.variables.exps.AssignmentEmitter;
import low.variables.exps.BinaryOpEmitter;
import low.variables.exps.CompoundAssignmentEmitter;
import low.variables.exps.UnaryOpEmitter;
import memory_manager.free.FreeEmitter;
import memory_manager.free.FreeNode;
import memory_manager.ownership.escapes.EscapeInfo;

import java.util.*;


public class LLVisitorMain implements LLVMEmitVisitor {

    private final List<LLVMValue> implDefinitions = new ArrayList<>();

    public void addImplDefinition(LLVMValue val) {
        implDefinitions.add(val);
    }

    public String emitImplDefinitions() {
        StringBuilder sb = new StringBuilder();
        for (LLVMValue val : implDefinitions) {
            sb.append(val.getCode()).append("\n");
        }
        return sb.toString();
    }

    // ==== TABELAS / REGISTROS ====
    private final TypeTable types;
    private final StructRegistry structRegistry;
    private final ImportRegistry importRegistry;
    private final StructTypeResolver structTypeResolver;

    // usado externamente em outros pontos do pipeline
    public final Map<String, StructNode> specializedStructs;

    private final TempManager temps;
    private List<LLVMValue> structDefinitions = new ArrayList<>();
    private final GlobalStringManager globalStrings;
    private final FlowControllVisitor controlFlow;



    // ==== EMITTERS GENÉRICOS ====
    public final VariableEmitter varEmitter;
    public final PrintEmitter printEmitter;
    private final AssignmentEmitter assignmentEmitter;
    private final UnaryOpEmitter unaryOpEmitter;
    private final LiteralEmitter literalEmitter;
    private final BinaryOpEmitter binaryEmitter;
    private final FunctionCallEmitter callEmiter;
    private final StructUpdateEmitter updateEmitter;
    private final CompoundAssignmentEmitter compoundAssignmentEmitter;
    // ==== LISTA: AGORA CONTROLADA POR ListVisitor ====
    private final ListVisitor listVisitor;

    // ==== FUNÇÕES / STRUCTS / IMPORTS ====
    public final Map<String, FunctionNode> functions;
    public final Map<String, FunctionNode> importedFunctions;
    public final Set<Type> tiposDeListasUsados;

    private final ImportEmitter importEmitter;
    private final StructEmitter structEmitter;
    private final StructInstanceEmitter instanceEmitter;
    private final StructFieldAccessEmitter structFieldAccessEmitter;
    private final StructMethodCallEmitter methodCallEmitter;
    private final ImplEmitter implEmitter;

    // ==== TIPO ESPECIALIZAÇÃO (estado atual) ====
    private String currentSpecializationType = null;

    // === Memory
    private final FreeEmitter freeEmitter;


    public String getCurrentSpecializationType() {
        return currentSpecializationType;
    }


    private EscapeInfo escapeInfo;

    public void setEscapeInfo(EscapeInfo info) {
        this.escapeInfo = info;
    }

    public LLVisitorMain(EscapeInfo escapeInfo) {
        this(
                new TypeTable(),
                new HashMap<>(),
                new HashMap<>(),
                new HashMap<>(),
                new HashMap<>(),
                new HashSet<>(),
                new ArrayList<>(),
                new GlobalStringManager()
        );
        this.escapeInfo = escapeInfo;
    }


    public boolean escapesVar(String name) {
        return escapeInfo != null && escapeInfo.escapes(name);
    }

    private LLVisitorMain(
            TypeTable types,
            Map<String, FunctionNode> functions,
            Map<String, FunctionNode> importedFunctions,
            Map<String, StructNode> structNodes,
            Map<String, StructNode> specializedStructs,
            Set<Type> tiposDeListasUsados,
            List<LLVMValue> structDefinitions,
            GlobalStringManager globalStrings
    ) {
        this.types = types;
        this.functions = functions;
        this.importedFunctions = importedFunctions;
        this.tiposDeListasUsados = tiposDeListasUsados;
        this.structDefinitions = structDefinitions;
        this.globalStrings = globalStrings;
        this.specializedStructs = specializedStructs;

        this.temps = new TempManager();
        this.controlFlow = new FlowControllVisitor(this, temps);

        // Registries
        this.structRegistry = new StructRegistry(structNodes, new HashSet<>(), new HashSet<>());
        this.structEmitter = new StructEmitter(this);
        this.importRegistry = new ImportRegistry(importedFunctions, structRegistry);
        this.structTypeResolver = new StructTypeResolver(types, structRegistry);

        // Var types
        Map<String, TypeInfos> varTypesView = types.getVarTypesMap();

        this.varEmitter = new VariableEmitter(varTypesView, temps, this);
        this.printEmitter = new PrintEmitter(globalStrings, temps);
        this.assignmentEmitter = new AssignmentEmitter(varTypesView, temps, globalStrings, this);
        this.unaryOpEmitter = new UnaryOpEmitter(varTypesView, temps, varEmitter, this);
        this.literalEmitter = new LiteralEmitter(temps, globalStrings);
        this.binaryEmitter = new BinaryOpEmitter(temps, this);
        this.callEmiter = new FunctionCallEmitter(temps);
        this.updateEmitter = new StructUpdateEmitter(temps, this);
        this.importEmitter = new ImportEmitter(this, this.tiposDeListasUsados);
        this.instanceEmitter = new StructInstanceEmitter(temps, globalStrings);
        this.structFieldAccessEmitter = new StructFieldAccessEmitter(temps);
        this.methodCallEmitter = new StructMethodCallEmitter(temps);
        this.implEmitter = new ImplEmitter(this, temps);
        this.compoundAssignmentEmitter =
                new CompoundAssignmentEmitter(varTypesView, temps, varEmitter, this);

        this.freeEmitter = new FreeEmitter(this, temps);
        this.listVisitor = new ListVisitor(this, temps, globalStrings);
    }

    public LLVisitorMain fork() {

        TypeTable forkTypes = new TypeTable(
                new HashMap<>(),
                this.types.getFunctionTypesMap()
        );

        LLVisitorMain forked = new LLVisitorMain(
                forkTypes,
                this.functions,
                this.importedFunctions,
                this.structRegistry.getStructMap(),
                this.specializedStructs,
                this.tiposDeListasUsados,
                this.structDefinitions,
                this.globalStrings
        );

        System.out.println("[FORK] original temps: " + System.identityHashCode(this.temps));
        System.out.println("[FORK] forked temps:   " + System.identityHashCode(forked.temps));
        return forked;
    }


    public FlowControllVisitor getControlFlow() {
        return controlFlow;
    }



    // ==== LIST TYPES INFERENCE (wrappers) ====
    public Type inferListElementType(ASTNode node) {
        return structTypeResolver.inferListElementType(node);
    }

    public void registerListElementType(String varName, Type elementType) {
        structTypeResolver.registerListElementType(varName, elementType);
    }

    public Type getListElementType(String varName) {
        return structTypeResolver.getListElementType(varName);
    }

    // ==== STRUCTS REGISTRO / DEFINIÇÃO ====
    public void registerStructNode(StructNode node) {
        structRegistry.put(node.getName(), node);
    }

    public void registerStructNode(String qualifiedName, StructNode node) {
        structRegistry.put(qualifiedName, node);
    }

    public StructNode getStructNode(String name) {
        return structRegistry.get(name);
    }

    public void addStructDefinition(LLVMValue llvmDef) {
        structDefinitions.add(llvmDef);
    }
    public String emitStructDefinitions() {
        StringBuilder sb = new StringBuilder();
        for (LLVMValue structVal : structDefinitions) {
            if (structVal != null && structVal.getCode() != null && !structVal.getCode().isBlank()) {
                sb.append(structVal.getCode()).append("\n");
            }
        }
        return sb.toString();
    }


    // ==== IMPORTS ====
    public void registerImportedFunction(String qualifiedName, FunctionNode func) {
        importRegistry.registerImportedFunction(qualifiedName, func);
    }

    // ==== FUNCTION TYPES ====
    public void registerFunctionType(String name, TypeInfos typeInfo) {
        types.putFunctionType(name, typeInfo);
    }

    public TypeInfos getFunctionType(String name) {
        return types.getFunctionType(name);
    }

    // ==== VAR TYPES ====
    public void putVarType(String name, TypeInfos type) {
        types.putVarType(name, type);
    }

    public TypeInfos getVarType(String name) {
        return types.getVarType(name);
    }



    public FunctionCallEmitter getCallEmitter() {
        return callEmiter;
    }

    public TempManager getTemps() {
        return temps;
    }

    public GlobalStringManager getGlobalStrings() {
        return globalStrings;
    }

    public VariableEmitter getVariableEmitter() {
        return varEmitter;
    }

    @Override
    public LLVMValue visit(MainAST node) {
        MainEmitter mainEmitter = new MainEmitter(globalStrings, temps, tiposDeListasUsados, structDefinitions);
        return mainEmitter.emit(node, this);
    }


    public void registrarStructs(MainAST node) {
        for (ASTNode stmt : node.body) {
            if (stmt instanceof StructNode structNode) {
                registerStructNode(structNode);
            }
        }
    }


    public String resolveStructName(ASTNode node) {
        return structTypeResolver.resolveStructName(node);
    }


    @Override
    public LLVMValue visit(VariableNode node) {
        return varEmitter.emitLoad(node.getName());
    }

    @Override
    public LLVMValue visit(VariableDeclarationNode node) {
        return varEmitter.emitDeclaration(node);
    }


    @Override
    public LLVMValue visit(LiteralNode node) {
        return literalEmitter.emit(node);
    }

    @Override
    public LLVMValue visit(PrintNode node) {
        return printEmitter.emit(node, this);
    }

    @Override
    public LLVMValue visit(UnaryOpNode node) {
        return unaryOpEmitter.emit(node.getOperator(), node.getExpr());
    }
    @Override
    public LLVMValue visit(AssignmentNode node) {
        return assignmentEmitter.emit(node);
    }

    @Override
    public LLVMValue visit(BinaryOpNode node) {
        return binaryEmitter.emit(node);
    }

    @Override
    public LLVMValue visit(IfNode node) {
        return controlFlow.visit(node);
    }

    @Override
    public LLVMValue visit(WhileNode node) {
        return controlFlow.visit(node);
    }

    @Override
    public LLVMValue visit(BreakNode node) {
        return null;
    }

    @Override
    public LLVMValue visit(ListNode node) {
        return listVisitor.visit(node);
    }

    @Override
    public LLVMValue visit(ListAddNode node) {
        return null;
    }

    @Override
    public LLVMValue visit(ListRemoveNode node) {
        return listVisitor.visit(node);
    }

    @Override
    public LLVMValue visit(ListClearNode node) {
        return null;
    }

    @Override
    public LLVMValue visit(ListSizeNode node) {
        return null;
    }

    @Override
    public LLVMValue visit(ListGetNode node) {
        return null;
    }

    @Override
    public LLVMValue visit(ListAddAllNode node) {
        return null;
    }

    @Override
    public LLVMValue visit(FunctionNode node) {
        return null;
    }

    @Override
    public LLVMValue visit(FunctionCallNode node) {
        return null;
    }

    @Override
    public LLVMValue visit(ReturnNode node) {
        return null;
    }

    @Override
    public LLVMValue visit(ImportNode node) {
        return null;
    }

    @Override
    public LLVMValue visit(StructNode node) {
        return structEmitter.emit(node);
    }

    @Override
    public LLVMValue visit(StructInstanceNode node) {
        return instanceEmitter.emit(node, this);
    }

    @Override
    public LLVMValue visit(StructFieldAccessNode node) {
        return structFieldAccessEmitter.emit(node, this);
    }

    @Override
    public LLVMValue visit(StructUpdateNode node) {
        return null;
    }

    @Override
    public LLVMValue visit(StructMethodCallNode node) {
        return null;
    }

    @Override
    public LLVMValue visit(ImplNode node) {
        return null;
    }

    @Override
    public LLVMValue visit(ForNode node) {
        return controlFlow.visit(node);
    }

    @Override
    public LLVMValue visitFreeNode(FreeNode freeNode) {

        return freeEmitter.emit(freeNode);
    }

    @Override
    public LLVMValue visit(CompoundAssignmentNode compoundAssignmentNode) {
        return null;
    }

    @Override
    public LLVMValue visit(InputNode inputNode) {
        return null;
    }


    public List<LLVMValue> getImplDefinitions() {
        return Collections.unmodifiableList(implDefinitions);
    }

}
