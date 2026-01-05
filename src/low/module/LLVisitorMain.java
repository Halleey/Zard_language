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
import ast.loops.ForNode;
import ast.structs.*;
import ast.lists.*;
import ast.loops.WhileNode;

import ast.prints.PrintNode;
import ast.variables.*;
import low.TempManager;
import low.exceptions.ReturnEmitter;
import low.functions.FunctionCallEmitter;
import low.functions.FunctionEmitter;
import low.imports.ImportEmitter;
import low.main.GlobalStringManager;
import low.main.MainEmitter;
import low.main.TypeInfos;
import low.module.flow.FlowControllVisitor;
import low.module.imports.ImportRegistry;
import low.module.lists.ListVisitor;
import low.module.structs.SpecializedStructManager;
import low.module.structs.StructRegistry;
import low.module.structs.StructTypeResolver;
import low.prints.PrintEmitter;
import low.structs.*;
import low.variables.*;
import low.variables.exps.AssignmentEmitter;
import low.variables.exps.BinaryOpEmitter;
import low.variables.exps.UnaryOpEmitter;
import memory_manager.free.FreeNode;
import memory_manager.ownership.escapes.EscapeInfo;

import java.util.*;


public class LLVisitorMain implements LLVMEmitVisitor {

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
    private final FlowControllVisitor controlFlow;


    private final Map<String, String> listElementTypesLegacyView = new HashMap<>(); // NÃO usado diretamente, só compat

    // ==== EMITTERS GENÉRICOS ====
    public final VariableEmitter varEmitter;
    public final PrintEmitter printEmitter;
    private final AssignmentEmitter assignmentEmitter;
    private final UnaryOpEmitter unaryOpEmitter;
    private final LiteralEmitter literalEmitter;
    private final BinaryOpEmitter binaryEmitter;
    private final FunctionCallEmitter callEmiter;
    private final StructUpdateEmitter updateEmitter;

    // ==== LISTA: AGORA CONTROLADA POR ListVisitor ====
    private final ListVisitor listVisitor;

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


    private EscapeInfo escapeInfo;

    public void setEscapeInfo(EscapeInfo info) {
        this.escapeInfo = info;
    }

    // novo construtor público
    public LLVisitorMain(TypeSpecializer typeSpecializer, EscapeInfo escapeInfo) {
        this(typeSpecializer);
        this.escapeInfo = escapeInfo;
    }

    public boolean escapesVar(String name) {
        return escapeInfo != null && escapeInfo.escapes(name);
    }
    public LLVisitorMain(TypeSpecializer typeSpecializer) {
        this(
                typeSpecializer,
                new TypeTable(),
                new HashMap<>(),
                new HashMap<>(),
                new HashMap<>(),
                new HashMap<>(),
                new HashSet<>(),
                new ArrayList<>(),
                new GlobalStringManager()
        );
    }

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
        this.controlFlow = new FlowControllVisitor(this, temps);


        // Registries
        this.structRegistry = new StructRegistry(structNodes, new HashSet<>(), new HashSet<>());
        this.structEmitter = new StructEmitter(this);
        this.specializedManager = new SpecializedStructManager(specializedStructs, structEmitter, structRegistry, structDefinitions);
        this.importRegistry = new ImportRegistry(importedFunctions, structRegistry);
        this.structTypeResolver = new StructTypeResolver(types, structRegistry);

        // Var types
        Map<String, TypeInfos> varTypesView = types.getVarTypesMap();

        this.varEmitter = new VariableEmitter(varTypesView, temps, this);
        this.printEmitter = new PrintEmitter(globalStrings, temps);
        this.assignmentEmitter = new AssignmentEmitter(varTypesView, temps, globalStrings, this);
        this.unaryOpEmitter = new UnaryOpEmitter(varTypesView, temps, varEmitter);
        this.literalEmitter = new LiteralEmitter(temps, globalStrings);
        this.binaryEmitter = new BinaryOpEmitter(temps, this);
        this.callEmiter = new FunctionCallEmitter(temps);
        this.updateEmitter = new StructUpdateEmitter(temps, this);
        this.importEmitter = new ImportEmitter(this, this.tiposDeListasUsados);
        this.instanceEmitter = new StructInstanceEmitter(temps, globalStrings);
        this.structFieldAccessEmitter = new StructFieldAccessEmitter(temps);
        this.methodCallEmitter = new StructMethodCallEmitter(temps);
        this.implEmitter = new ImplEmitter(this, temps);

        this.listVisitor = new ListVisitor(this, temps, globalStrings);

    }

    public LLVisitorMain fork() {

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
    public FlowControllVisitor getControlFlow() {
        return controlFlow;
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
    public String visit(ForNode node) {
        return controlFlow.visit(node);
    }

    @Override
    public String visitFreeNode(FreeNode freeNode) {
        return "";
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
        return controlFlow.visit(node);
    }

    @Override
    public String visit(IfNode node) {
        return controlFlow.visit(node);
    }

    @Override
    public String visit(BreakNode node) {
        return "  br label %" + controlFlow.currentLoopEnd() + "\n";
    }

    @Override
    public String visit(ListNode node) {
        return listVisitor.visit(node);
    }

    @Override
    public String visit(ListAddNode node) {
        return listVisitor.visit(node);
    }

    @Override
    public String visit(ListRemoveNode node) {
        return listVisitor.visit(node);
    }

    @Override
    public String visit(ListClearNode node) {
        return listVisitor.visit(node);
    }

    @Override
    public String visit(ListSizeNode node) {
        return listVisitor.visit(node);
    }

    @Override
    public String visit(ListGetNode node) {
        return listVisitor.visit(node);
    }

    @Override
    public String visit(ListAddAllNode node) {
        return listVisitor.visit(node);
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
    public String visit(FunctionNode node) {
        functions.put(node.getName(), node);
        return new FunctionEmitter(this).emit(node);
    }

    @Override
    public String visit(FunctionCallNode node) {
        return callEmiter.emit(node, this);
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



    public void registrarStructs(MainAST node) {
        for (ASTNode stmt : node.body) {
            if (stmt instanceof StructNode structNode) {
                structNode.accept(this);
            }
        }
    }



    public int getStructSizeInBytes(StructNode def) {
        int size = 0;
        for (VariableDeclarationNode f : def.getFields()) {
            size += sizeOf(f.getType());
        }
        return size;
    }

    private int sizeOf(String type) {
        return switch (type) {
            case "int" -> 4;
            case "boolean" -> 1;
            case "double", "float" -> 8;
            case "string" -> 8; // ponteiro
            default -> 8; // List, Struct, qualquer ponteiro
        };
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
