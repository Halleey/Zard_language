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
import low.exceptions.ReturnEmitter;
import low.functions.FunctionCallEmitter;
import low.functions.FunctionEmitter;
import low.ifs.IfEmitter;
import low.imports.ImportEmitter;
import low.lists.generics.*;
import low.main.GlobalStringManager;
import low.TempManager;
import low.main.MainEmitter;
import low.prints.PrintEmitter;
import low.structs.StructEmitter;
import low.structs.StructFieldAccessEmitter;
import low.structs.StructInstanceEmitter;
import low.variables.*;
import low.whiles.WhileEmitter;
import ast.prints.PrintNode;
import ast.variables.*;
import java.util.*;

public class LLVisitorMain implements LLVMEmitVisitor {
    private final Map<String, String> varTypes = new HashMap<>();

    private final TempManager temps = new TempManager();
    private final List<String> structDefinitions = new ArrayList<>();
    private final GlobalStringManager globalStrings = new GlobalStringManager();
    private final Map<String, String> listElementTypes = new HashMap<>();
    public final VariableEmitter varEmitter = new VariableEmitter(varTypes, temps, this);
    public final PrintEmitter printEmitter = new PrintEmitter(globalStrings, temps);
    private final AssignmentEmitter assignmentEmitter = new AssignmentEmitter(varTypes, temps, globalStrings, this);
    private final UnaryOpEmitter unaryOpEmitter = new UnaryOpEmitter(varTypes, temps, varEmitter);
    private final LiteralEmitter literalEmitter = new LiteralEmitter(temps,globalStrings);
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
    private final Map<String, FunctionNode> functions = new HashMap<>();
    public final Map<String, String> functionTypes = new HashMap<>();
    private final Map<String, FunctionNode> importedFunctions = new HashMap<>();
    public final Set<String> tiposDeListasUsados = new HashSet<>();
    private final ImportEmitter importEmitter = new ImportEmitter(this, this.tiposDeListasUsados);
    private final StructEmitter structEmitter  = new StructEmitter(this);
    private final Map<String, StructNode> structNodes = new HashMap<>();
    private final StructInstanceEmitter instanceEmitter = new StructInstanceEmitter(temps, globalStrings);
    private final StructFieldAccessEmitter structFieldAccessEmitter = new StructFieldAccessEmitter(temps);

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

    @Override
    public String visit(StructNode node) {
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
    public String visit(MainAST node) {
        MainEmitter mainEmitter = new MainEmitter(globalStrings, temps, tiposDeListasUsados, structDefinitions);
        return mainEmitter.emit(node, this);
    }

    public void registerImportedFunction(String qualifiedName, FunctionNode func) {
        importedFunctions.put(qualifiedName, func);
    }
    public void registerListElementType(String varName, String elementType) {
        listElementTypes.put(varName, elementType);
    }
    public String getListElementType(String varName) {
        return listElementTypes.get(varName);
    }

    @Override
    public String visit(ReturnNode node) {
        ReturnEmitter emitter = new ReturnEmitter( this, temps);
        return emitter.emit(node);
    }

    @Override
    public String visit(ImportNode node) {
        return importEmitter.emit(node);
    }

    @Override
    public String visit(MapNode node) {
        return "";
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
        String endLabel = currentLoopEnd();
        return "  br label %" + endLabel + "\n";
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

    public String getVarType(String name) {
        return varTypes.get(name);
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
        functions.put(node.getName(), node); // registra função
        return new FunctionEmitter(this).emit(node);
    }

    @Override
    public String visit(FunctionCallNode node) {
        return callEmiter.emit(node, this);
    }
    public TempManager getTemps() {
        return temps;
    }


    public void putVarType(String name, String type) {
        varTypes.put(name, type);
    }

    public void registerFunctionType(String name, String llvmType) {
        functionTypes.put(name, llvmType);
    }

    public String getFunctionType(String name) {
        return functionTypes.get(name); // usado pelo FunctionCallEmitter
    }

    public FunctionCallEmitter getCallEmitter() {
        return callEmiter;
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


    public String getStructFieldType(StructFieldAccessNode node) {
        // pega o tipo do receiver
        String receiverType;
        if (node.getStructInstance() instanceof VariableNode varNode) {
            receiverType = getVarType(varNode.getName());
        } else if (node.getStructInstance() instanceof StructFieldAccessNode nested) {
            receiverType = getStructFieldType(nested);
        } else {
            throw new RuntimeException("Unsupported receiver in struct field access");
        }

        if (receiverType == null) {
            throw new RuntimeException("Unknown receiver type for struct field access: " + node);
        }

        // normaliza o nome, ex: %Pessoa* → Pessoa
        String structName = receiverType.replace("%", "").replace("*", "");

        StructNode structNode = structNodes.get(structName);
        if (structNode == null) {
            throw new RuntimeException("Struct not found: " + structName);
        }

        for (VariableDeclarationNode field : structNode.getFields()) {
            if (field.getName().equals(node.getFieldName())) {
                return field.getType();
            }
        }

        throw new RuntimeException("Field not found: " + node.getFieldName() + " in struct " + structName);
    }

}
