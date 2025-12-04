package low.module;

import ast.ASTNode;
import ast.TypeSpecializer;
import ast.exceptions.BreakNode;
import ast.exceptions.ReturnNode;
import ast.functions.FunctionCallNode;
import ast.functions.FunctionNode;
import ast.home.MainAST;
import ast.ifstatements.IfNode;
import ast.imports.ImportNode;
import ast.structs.*;
import ast.lists.*;
import ast.loops.WhileNode;
import ast.maps.MapNode;
import ast.prints.PrintNode;
import ast.variables.*;
import low.TempManager;
import low.exceptions.ReturnEmitter;
import low.functions.FunctionCallEmitter;
import low.functions.FunctionEmitter;
import low.ifs.IfEmitter;
import low.imports.ImportEmitter;
import low.lists.generics.*;
import low.main.GlobalStringManager;
import low.main.MainEmitter;
import low.main.TypeInfos;
import low.prints.PrintEmitter;
import low.structs.*;
import low.variables.*;
import low.whiles.WhileEmitter;
import java.util.*;


public class LLVisitorMain implements LLVMEmitVisitor {

    // ==== TESTE PARA IMPLS NODE
    private final List<String> implDefinitions = new ArrayList<>();

    public void addImplDefinition(String ir) {
        if (ir != null && !ir.isBlank()) {
            implDefinitions.add(ir);
        }
    }

    public String emitImplDefinitions() {
        StringBuilder sb = new StringBuilder();
        for (String s : implDefinitions) {
            sb.append(s).append("\n");
        }
        return sb.toString();
    }

    // ==== TABELAS / REGISTROS ====
    private final TypeTable types;
    private final StructRegistry structRegistry;
    private final SpecializedStructManager specializedManager;
    private final ImportRegistry importRegistry;
    private final StructTypeResolver structTypeResolver;

    // usado externamente em outros pontos do pipeline
    public final Map<String, StructNode> specializedStructs;

    private final TempManager temps;
    private final List<String> structDefinitions;
    private final GlobalStringManager globalStrings;
    private final Deque<String> loopEndLabels;

    private final Map<String, String> listElementTypesLegacyView = new HashMap<>(); // NÃO usado diretamente, só compat

    // ==== EMITTERS ====
    public final VariableEmitter varEmitter;
    public final PrintEmitter printEmitter;
    private final AssignmentEmitter assignmentEmitter;
    private final UnaryOpEmitter unaryOpEmitter;
    private final LiteralEmitter literalEmitter;
    private final BinaryOpEmitter binaryEmitter;
    private final IfEmitter ifEmitter;
    private final WhileEmitter whileEmitter;
    private final ListEmitter listEmitter;
    private final ListAddEmitter listAddEmitter;
    private final ListRemoveEmitter listRemoveEmitter;
    private final ListClearEmitter clearEmitter;
    private final ListSizeEmitter sizeEmitter;
    private final ListGetEmitter getEmitter;
    private final ListAddAllEmitter allEmitter;
    private final FunctionCallEmitter callEmiter;
    private final StructUpdateEmitter updateEmitter;

    // ==== FUNÇÕES / STRUCTS / IMPORTS ====
    public final Map<String, FunctionNode> functions;
    public final Map<String, FunctionNode> importedFunctions;
    public final Set<String> tiposDeListasUsados;

    private final ImportEmitter importEmitter;
    private final StructEmitter structEmitter;
    private final StructInstanceEmitter instanceEmitter;
    private final StructFieldAccessEmitter structFieldAccessEmitter;
    private final StructMethodCallEmitter methodCallEmitter;
    private final ImplEmitter implEmitter;

    // ==== TYPE SPECIALIZER ====
    private final TypeSpecializer typeSpecializer;

    public TypeSpecializer getTypeSpecializer() {
        return typeSpecializer;
    }

    // ==== TIPO ESPECIALIZAÇÃO (estado atual) ====
    private String currentSpecializationType = null;

    public void enterTypeSpecialization(String innerType) {
        this.currentSpecializationType = innerType;
    }

    public void exitTypeSpecialization() {
        this.currentSpecializationType = null;
    }

    public String getCurrentSpecializationType() {
        return currentSpecializationType;
    }

    // ==== CONSTRUTOR PÚBLICO (USO NORMAL) ====
    public LLVisitorMain(TypeSpecializer typeSpecializer) {
        this(
                typeSpecializer,
                new TypeTable(),   // varTypes (local) + functionTypes (compartilhado)
                new HashMap<>(),   // functions
                new HashMap<>(),   // importedFunctions
                new HashMap<>(),   // structNodes
                new HashMap<>(),   // specializedStructs
                new HashSet<>(),   // tiposDeListasUsados
                new ArrayList<>(), // structDefinitions
                new GlobalStringManager()
        );
    }

    // ==== CONSTRUTOR INTERNO (USADO POR fork) ====
    private LLVisitorMain(
            TypeSpecializer typeSpecializer,
            TypeTable types,
            Map<String, FunctionNode> functions,
            Map<String, FunctionNode> importedFunctions,
            Map<String, StructNode> structNodes,
            Map<String, StructNode> specializedStructs,
            Set<String> tiposDeListasUsados,
            List<String> structDefinitions,
            GlobalStringManager globalStrings
    ) {
        this.typeSpecializer = typeSpecializer;
        this.types = types;
        this.functions = functions;
        this.importedFunctions = importedFunctions;
        this.tiposDeListasUsados = tiposDeListasUsados;
        this.structDefinitions = structDefinitions;
        this.globalStrings = globalStrings;
        this.specializedStructs = specializedStructs;

        this.temps = new TempManager();
        this.loopEndLabels = new ArrayDeque<>();

        // Registries / managers
        this.structRegistry = new StructRegistry(structNodes, new HashSet<>(), new HashSet<>());
        this.structEmitter = new StructEmitter(this);
        this.specializedManager = new SpecializedStructManager(specializedStructs, structEmitter, structRegistry, structDefinitions);
        this.importRegistry = new ImportRegistry(importedFunctions, structRegistry);
        this.structTypeResolver = new StructTypeResolver(types, structRegistry);

        // Emitters dependem de varTypes → usamos o map interno do TypeTable
        Map<String, TypeInfos> varTypesView = types.getVarTypesMap();

        this.varEmitter = new VariableEmitter(varTypesView, temps, this);
        this.printEmitter = new PrintEmitter(globalStrings, temps);
        this.assignmentEmitter = new AssignmentEmitter(varTypesView, temps, globalStrings, this);
        this.unaryOpEmitter = new UnaryOpEmitter(varTypesView, temps, varEmitter);
        this.literalEmitter = new LiteralEmitter(temps, globalStrings);
        this.binaryEmitter = new BinaryOpEmitter(temps, this);
        this.ifEmitter = new IfEmitter(temps, this);
        this.whileEmitter = new WhileEmitter(temps, this);
        this.listEmitter = new ListEmitter(temps);
        this.listAddEmitter = new ListAddEmitter(temps, globalStrings);
        this.listRemoveEmitter = new ListRemoveEmitter(temps);
        this.clearEmitter = new ListClearEmitter(temps);
        this.sizeEmitter = new ListSizeEmitter(temps);
        this.getEmitter = new ListGetEmitter(temps);
        this.allEmitter = new ListAddAllEmitter(temps, globalStrings);
        this.callEmiter = new FunctionCallEmitter(temps);
        this.updateEmitter = new StructUpdateEmitter(temps, this);
        this.importEmitter = new ImportEmitter(this, this.tiposDeListasUsados);
        this.instanceEmitter = new StructInstanceEmitter(temps, globalStrings);
        this.structFieldAccessEmitter = new StructFieldAccessEmitter(temps);
        this.methodCallEmitter = new StructMethodCallEmitter(temps);
        this.implEmitter = new ImplEmitter(this, temps);
    }

    // ==== fork: VISITOR ISOLADO PARA IMPLS ESPECIALIZADAS ====
    public LLVisitorMain fork() {
        // functionTypes compartilha, varTypes é novo
        TypeTable forkTypes = new TypeTable(
                new HashMap<>(),
                this.types.getFunctionTypesMap()
        );

        return new LLVisitorMain(
                this.typeSpecializer,
                forkTypes,
                this.functions,
                this.importedFunctions,
                this.structRegistry.getStructMap(),
                this.specializedStructs,
                this.tiposDeListasUsados,
                this.structDefinitions,
                this.globalStrings
        );
    }

    // ==== STRUCTS USO / IMPORT ====
    public void markStructUsed(String name) {
        structRegistry.markUsed(name);
    }

    public void markStructImported(String name) {
        importRegistry.markStructImported(name);
    }

    public boolean isStructUsed(String name) {
        return structRegistry.isUsed(name);
    }

    // ==== LÓGICA DE STRUCTS ESPECIALIZADAS ====
    public StructNode getOrCreateSpecializedStruct(StructNode base, String elemType) {
        return specializedManager.getOrCreateSpecializedStruct(base, elemType);
    }

    public boolean hasSpecializationFor(String baseName) {
        return specializedManager.hasSpecializationFor(baseName);
    }

    // ==== LIST TYPES INFERENCE (wrappers) ====
    public String inferListElementType(ASTNode node) {
        return structTypeResolver.inferListElementType(node);
    }

    public void registerListElementType(String varName, String elementType) {
        structTypeResolver.registerListElementType(varName, elementType);
    }

    public String getListElementType(String varName) {
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

    public void addStructDefinition(String llvmDef) {
        structDefinitions.add(llvmDef);
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

    // ==== LOOP CONTROL ====
    public void pushLoopEnd(String label) {
        loopEndLabels.push(label);
    }

    public void popLoopEnd() {
        loopEndLabels.pop();
    }

    public String currentLoopEnd() {
        if (loopEndLabels.isEmpty()) {
            throw new RuntimeException("Break fora de loop!");
        }
        return loopEndLabels.peek();
    }

    // ==== VISITORS DE STRUCTS / IMPORT / IMPL ====

    @Override
    public String visit(StructNode node) {
        // Evita redefinir structs que já foram emitidas
        String llvmName = (node.getLLVMName() != null && !node.getLLVMName().isBlank())
                ? node.getLLVMName()
                : node.getName();

        boolean alreadyDefined = structDefinitions.stream()
                .anyMatch(def -> def.startsWith("%" + llvmName + " "));

        if (alreadyDefined) {
            return "";
        }

        String llvm = structEmitter.emit(node);
        addStructDefinition(llvm);
        registerStructNode(node);
        return "";
    }

    @Override
    public String visit(StructInstaceNode node) {
        return instanceEmitter.emit(node, this);
    }

    @Override
    public String visit(StructFieldAccessNode node) {
        return structFieldAccessEmitter.emit(node, this);
    }

    @Override
    public String visit(StructUpdateNode node) {
        return updateEmitter.emit(node);
    }

    @Override
    public String visit(StructMethodCallNode node) {
        return methodCallEmitter.emit(node, this);
    }

    @Override
    public String visit(ImportNode node) {
        System.out.println("RODANDO IMPORT ----------");
        return importEmitter.emit(node);
    }

    @Override
    public String visit(ImplNode node) {
        System.out.println("rodou aqui primeiro");
        return implEmitter.emit(node);
    }

    @Override
    public String visit(MainAST node) {
        MainEmitter mainEmitter = new MainEmitter(globalStrings, temps, tiposDeListasUsados, structDefinitions);
        return mainEmitter.emit(node, this);
    }

    @Override
    public String visit(ReturnNode node) {
        return new ReturnEmitter(this, temps).emit(node);
    }

    @Override
    public String visit(MapNode node) {
        return "";
    }

    @Override
    public String visit(VariableDeclarationNode node) {
        return varEmitter.emitAlloca(node) + varEmitter.emitInit(node);
    }

    @Override
    public String visit(LiteralNode node) {
        return literalEmitter.emit(node);
    }

    @Override
    public String visit(VariableNode node) {
        return varEmitter.emitLoad(node.getName());
    }

    @Override
    public String visit(BinaryOpNode node) {
        return binaryEmitter.emit(node);
    }

    @Override
    public String visit(WhileNode node) {
        return whileEmitter.emit(node);
    }

    @Override
    public String visit(BreakNode node) {
        return "  br label %" + currentLoopEnd() + "\n";
    }

    @Override
    public String visit(ListNode node) {
        return listEmitter.emit(node, this);
    }

    @Override
    public String visit(ListAddNode node) {
        return listAddEmitter.emit(node, this);
    }

    @Override
    public String visit(ListRemoveNode node) {
        return listRemoveEmitter.emit(node, this);
    }

    @Override
    public String visit(ListClearNode node) {
        return clearEmitter.emit(node, this);
    }

    @Override
    public String visit(IfNode node) {
        return ifEmitter.emit(node);
    }

    @Override
    public String visit(PrintNode node) {
        return printEmitter.emit(node, this);
    }

    @Override
    public String visit(UnaryOpNode node) {
        return unaryOpEmitter.emit(node.getOperator(), node.getExpr());
    }

    @Override
    public String visit(AssignmentNode node) {
        return assignmentEmitter.emit(node);
    }

    @Override
    public String visit(ListSizeNode node) {
        return sizeEmitter.emit(node, this);
    }

    @Override
    public String visit(ListGetNode node) {
        return getEmitter.emit(node, this);
    }

    @Override
    public String visit(ListAddAllNode node) {
        return allEmitter.emit(node, this);
    }

    @Override
    public String visit(FunctionNode node) {
        functions.put(node.getName(), node);
        return new FunctionEmitter(this).emit(node);
    }

    @Override
    public String visit(FunctionCallNode node) {
        return callEmiter.emit(node, this);
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

    public FunctionCallEmitter getCallEmitter() {
        return callEmiter;
    }

    public void registrarStructs(MainAST node) {
        for (ASTNode stmt : node.body) {
            if (stmt instanceof StructNode structNode) {
                structNode.accept(this);
            }
        }
    }

    public String getStructFieldType(StructFieldAccessNode node) {
        return structTypeResolver.getStructFieldType(node);
    }

    public String resolveStructName(ASTNode node) {
        return structTypeResolver.resolveStructName(node);
    }

    public Map<String, String> getListElementTypesLegacyView() {
        return listElementTypesLegacyView;
    }
}
