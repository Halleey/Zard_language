package low.module;

import ast.ASTNode;
import ast.exceptions.BreakNode;
import ast.exceptions.ReturnNode;
import ast.functions.FunctionCallNode;
import ast.functions.FunctionNode;
import ast.home.MainAST;
import ast.ifstatements.IfNode;
import ast.imports.ImportNode;
import ast.structs.StructFieldAccessNode;
import ast.structs.StructInstaceNode;
import ast.structs.StructNode;
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
import low.structs.StructEmitter;
import low.structs.StructFieldAccessEmitter;
import low.structs.StructInstanceEmitter;
import low.variables.*;
import low.whiles.WhileEmitter;

import java.util.*;

public class LLVisitorMain implements LLVMEmitVisitor {

    private final Map<String, TypeInfos> varTypes = new HashMap<>();

    private final Map<String, TypeInfos> functionTypes = new HashMap<>();

    private final TempManager temps = new TempManager();
    private final List<String> structDefinitions = new ArrayList<>();
    private final GlobalStringManager globalStrings = new GlobalStringManager();
    private final Map<String, String> listElementTypes = new HashMap<>();

    public final VariableEmitter varEmitter = new VariableEmitter(varTypes, temps, this);
    public final PrintEmitter printEmitter = new PrintEmitter(globalStrings, temps);
    private final AssignmentEmitter assignmentEmitter = new AssignmentEmitter(varTypes, temps, globalStrings, this);
    private final UnaryOpEmitter unaryOpEmitter = new UnaryOpEmitter(varTypes, temps, varEmitter);
    private final LiteralEmitter literalEmitter = new LiteralEmitter(temps, globalStrings);
    private final BinaryOpEmitter binaryEmitter = new BinaryOpEmitter(temps, this);
    private final IfEmitter ifEmitter = new IfEmitter(temps, this);
    private final WhileEmitter whileEmitter = new WhileEmitter(temps, this);
    private final ListEmitter listEmitter = new ListEmitter(temps);
    private final Deque<String> loopEndLabels = new ArrayDeque<>();
    private final ListAddEmitter listAddEmitter = new ListAddEmitter(temps, globalStrings);
    private final ListRemoveEmitter listRemoveEmitter = new ListRemoveEmitter(temps);
    private final ListClearEmitter clearEmitter = new ListClearEmitter(temps);
    private final ListSizeEmitter sizeEmitter = new ListSizeEmitter(temps);
    private final ListGetEmitter getEmitter = new ListGetEmitter(temps);
    private final ListAddAllEmitter allEmitter = new ListAddAllEmitter(temps, globalStrings);
    private final FunctionCallEmitter callEmiter = new FunctionCallEmitter(temps);

    public final Map<String, FunctionNode> functions = new HashMap<>();
    public final Map<String, FunctionNode> importedFunctions = new HashMap<>();
    public final Set<String> tiposDeListasUsados = new HashSet<>();

    private final ImportEmitter importEmitter = new ImportEmitter(this, this.tiposDeListasUsados);
    private final StructEmitter structEmitter = new StructEmitter(this);
    private final Map<String, StructNode> structNodes = new HashMap<>();
    private final StructInstanceEmitter instanceEmitter = new StructInstanceEmitter(temps, globalStrings);
    private final StructFieldAccessEmitter structFieldAccessEmitter = new StructFieldAccessEmitter(temps);
    public String inferListElementType(ASTNode node) {
        if (node instanceof VariableNode v) {
            return getListElementType(v.getName());
        }
        if (node instanceof StructFieldAccessNode sfa) {
            String fieldType = getStructFieldType(sfa);
            if (fieldType != null && fieldType.startsWith("List<") && fieldType.endsWith(">")) {
                return fieldType.substring(5, fieldType.length() - 1).trim();
            }
            return null;
        }
        if (node instanceof ListGetNode lg) {
            return inferListElementType(lg.getListName());
        }
        return null;
    }

    public void registerListElementType(String varName, String elementType) {
        listElementTypes.put(varName, elementType);
    }
    public String getListElementType(String varName) {
        return listElementTypes.get(varName);
    }

    public void registerStructNode(StructNode node) {
        structNodes.put(node.getName(), node);
    }
    public void registerStructNode(String qualifiedName, StructNode node) {
        structNodes.put(qualifiedName, node);
    }
    public StructNode getStructNode(String name) {
        return structNodes.get(name);
    }
    public void addStructDefinition(String llvmDef) {
        structDefinitions.add(llvmDef);
    }

    public void registerImportedFunction(String qualifiedName, FunctionNode func) {
        importedFunctions.put(qualifiedName, func);
    }
    public void registerFunctionType(String name, TypeInfos typeInfo) {
        functionTypes.put(name, typeInfo);
    }
    public TypeInfos getFunctionType(String name) {
        return functionTypes.get(name);
    }

    public void putVarType(String name, TypeInfos type) {
        varTypes.put(name, type);
    }

    public TypeInfos getVarType(String name) {
        return varTypes.get(name);
    }

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

    @Override public String visit(StructNode node) {
        String llvm = structEmitter.emit(node);
        addStructDefinition(llvm);
        registerStructNode(node);
        return "";
    }
    @Override public String visit(StructInstaceNode node) { return instanceEmitter.emit(node, this); }
    @Override public String visit(StructFieldAccessNode node) { return structFieldAccessEmitter.emit(node, this); }
    @Override public String visit(MainAST node) {
        MainEmitter mainEmitter = new MainEmitter(globalStrings, temps, tiposDeListasUsados, structDefinitions);
        return mainEmitter.emit(node, this);
    }

    @Override public String visit(ReturnNode node) { return new ReturnEmitter(this, temps).emit(node); }
    @Override public String visit(ImportNode node) { return importEmitter.emit(node); }
    @Override public String visit(MapNode node) { return ""; }
    @Override public String visit(VariableDeclarationNode node) {
        return varEmitter.emitAlloca(node) + varEmitter.emitInit(node);
    }
    @Override public String visit(LiteralNode node) {
        return literalEmitter.emit(node);
    }
    @Override public String visit(VariableNode node) {
        return varEmitter.emitLoad(node.getName());
    }
    @Override public String visit(BinaryOpNode node) {
        return binaryEmitter.emit(node);
    }
    @Override public String visit(WhileNode node) {
        return whileEmitter.emit(node);
    }
    @Override public String visit(BreakNode node) {
        return "  br label %" + currentLoopEnd() + "\n"; }
    @Override public String visit(ListNode node) { return listEmitter.emit(node, this); }
    @Override public String visit(ListAddNode node) { return listAddEmitter.emit(node, this); }
    @Override public String visit(ListRemoveNode node) { return listRemoveEmitter.emit(node, this); }
    @Override public String visit(ListClearNode node) { return clearEmitter.emit(node, this); }
    @Override public String visit(IfNode node) { return ifEmitter.emit(node); }
    @Override public String visit(PrintNode node) { return printEmitter.emit(node, this); }
    @Override public String visit(UnaryOpNode node) { return unaryOpEmitter.emit(node.getOperator(), node.getExpr()); }
    @Override public String visit(AssignmentNode node) { return assignmentEmitter.emit(node); }
    @Override public String visit(ListSizeNode node) { return sizeEmitter.emit(node, this); }
    @Override public String visit(ListGetNode node) { return getEmitter.emit(node, this); }
    @Override public String visit(ListAddAllNode node) { return allEmitter.emit(node, this); }
    @Override public String visit(FunctionNode node) {
        functions.put(node.getName(), node);
        return new FunctionEmitter(this).emit(node);
    }
    @Override public String visit(FunctionCallNode node) { return callEmiter.emit(node, this); }

    public TempManager getTemps() { return temps; }
    public GlobalStringManager getGlobalStrings() { return globalStrings; }
    public VariableEmitter getVariableEmitter() { return varEmitter; }
    public FunctionCallEmitter getCallEmitter() { return callEmiter; }

    public void registrarStructs(MainAST node) {
        for (ASTNode stmt : node.body) {
            if (stmt instanceof StructNode structNode) {
                structNode.accept(this);
            }
        }
    }
    public String getStructFieldType(StructFieldAccessNode node) {
        String structName = null;
        if (node.getStructInstance() instanceof VariableNode varNode) {
            TypeInfos receiverInfo = getVarType(varNode.getName());
            if (receiverInfo == null) throw new RuntimeException("Unknown receiver type for struct field access: " + node);
            structName = receiverInfo.getSourceType().replace("%","").replace("*","");
        }
        else if (node.getStructInstance() instanceof StructFieldAccessNode nested) {
            String receiverType = getStructFieldType(nested);
            if (receiverType.startsWith("Struct<") && receiverType.endsWith(">")) {
                structName = receiverType.substring("Struct<".length(), receiverType.length() - 1);
            } else {
                structName = receiverType.replace("%", "").replace("*", "");
            }
        }
        else if (node.getStructInstance() instanceof ListGetNode lg) {
            String elem = inferListElementType(lg.getListName());
            if (elem == null) throw new RuntimeException("Cannot infer element type from ListGet receiver: " + lg);
            structName = elem.startsWith("Struct<") && elem.endsWith(">") ? elem.substring("Struct<".length(), elem.length() - 1) : elem;
        }
        else {
            throw new RuntimeException("Unsupported receiver in struct field access");
        }

        String normalized = normalizeStructKey(structName);
        StructNode structNode = structNodes.get(normalized);
        if (structNode == null) {
            throw new RuntimeException("Struct not found: " + structName + " (normalized=" + normalized + ")");
        }

        for (VariableDeclarationNode field : structNode.getFields()) {
            if (field.getName().equals(node.getFieldName())) {
                return field.getType();
            }
        }
        throw new RuntimeException("Field not found: " + node.getFieldName() + " in struct " + structName);
    }

    private String normalizeStructKey(String name) {
        if (name == null) return null;
        name = name.trim();

        if (name.startsWith("Struct<") && name.endsWith(">")) {
            return name.substring(7, name.length() - 1).trim();
        }

        if (name.startsWith("Struct ")) {
            return name.substring(7).trim();
        }
        return name;
    }

}
