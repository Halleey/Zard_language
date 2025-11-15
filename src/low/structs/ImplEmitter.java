package low.structs;

import ast.ASTNode;
import ast.functions.FunctionNode;
import ast.lists.ListAddAllNode;
import ast.lists.ListAddNode;
import ast.structs.ImplNode;
import ast.structs.StructNode;
import low.TempManager;
import low.module.LLVisitorMain;
import low.main.TypeInfos;

import java.util.Map;

public class ImplEmitter {
    private final LLVisitorMain visitor;
    private final TempManager temps;
    public ImplEmitter(LLVisitorMain visitor, TempManager temps) {
        this.visitor = visitor;
        this.temps = temps;
    }

    public String emit(ImplNode node) {
        StringBuilder llvm = new StringBuilder();
        String baseStruct = node.getStructName();
        boolean hasUsageForBase = visitor.specializedStructs.keySet().stream()
                        .anyMatch(name -> name.equals(baseStruct) || name.startsWith(baseStruct + "<"));

        if (!hasUsageForBase) {
            return "";
        }

        llvm.append(";; ==== Impl Definitions ====\n");


        llvm.append(";; ==== Impl Definitions ====\n");

        for (FunctionNode fn : node.getMethods()) {
            if (isListImplMethod(baseStruct, fn)) {
                for (Map.Entry<String, StructNode> entry : visitor.specializedStructs.entrySet()) {
                    String name = entry.getKey();
                    if (name.startsWith(baseStruct + "<")) {
                        String inner = extractInnerType(name);
                        llvm.append("; === Impl especializada para Struct<")
                                .append(baseStruct).append("<").append(inner).append(">> ===\n");
                        llvm.append(generateFunctionImpl(baseStruct, fn, inner));
                    }
                }
            } else {
                llvm.append(emitSimpleMethod(baseStruct, fn));
            }
        }


        llvm.append("\n");
        return llvm.toString();
    }

    private boolean isListImplMethod(String baseStruct, FunctionNode fn) {
        return "Set".equals(baseStruct)
                && fn.getParams() != null
                && fn.getParams().size() >= 2;
    }

    private String emitSimpleMethod(String baseStruct, FunctionNode fn) {
        StringBuilder sb = new StringBuilder();

        String fnName = baseStruct + "_" + fn.getName();
        String structLLVM = "%" + baseStruct;
        String paramTypeLLVM = structLLVM + "*";
        String retType = paramTypeLLVM;

        sb.append("; === Método: ").append(fnName).append(" ===\n");
        sb.append("define ").append(retType).append(" @").append(fnName)
                .append("(").append(paramTypeLLVM).append(" %self) {\n");
        sb.append("entry:\n");

        if (fn.getBody() != null) {
            for (ASTNode stmt : fn.getBody()) {
                sb.append(stmt.accept(visitor));
            }
        }

        sb.append("  ret ").append(retType).append(" %self\n");
        sb.append("}\n\n");

        return sb.toString();
    }
    private String generateFunctionImpl(String baseStruct, FunctionNode fn, String specialized) {
        StringBuilder sb = new StringBuilder();

        String declaredType = null;
        if (fn.getParamTypes() != null && fn.getParamTypes().size() > 1) {
            declaredType = fn.getParamTypes().get(1);
            if (declaredType != null) declaredType = declaredType.trim();
        }

        String fnName;
        String structLLVM;
        String paramTypeLLVM;

        if (specialized == null) {
            fnName = baseStruct + "_" + fn.getName();
            structLLVM = "%" + baseStruct;
            paramTypeLLVM = structLLVM + "*";
        } else {
            fnName = baseStruct + "_" + specialized + "_" + fn.getName();
            structLLVM = "%" + baseStruct + "_" + specialized;
            paramTypeLLVM = structLLVM + "*";
        }

        String valueTypeLLVM;
        boolean declaredIsGeneric = declaredType != null && declaredType.contains("?");

        if (declaredType != null && !declaredIsGeneric) {
            valueTypeLLVM = mapToLLVMType(declaredType);
        } else if (specialized != null) {
            valueTypeLLVM = mapToLLVMType(specialized);
        } else {

            valueTypeLLVM = "i8*";
        }

        String retType = paramTypeLLVM;
        String paramS = fn.getParams().get(0);
        String valueS = fn.getParams().size() > 1 ? fn.getParams().get(1) : "value";

        sb.append("; === Função: ").append(fnName).append(" ===\n");
        sb.append("define ").append(retType).append(" @").append(fnName)
                .append("(").append(paramTypeLLVM).append(" %").append(paramS)
                .append(", ").append(valueTypeLLVM).append(" %").append(valueS).append(") {\n");
        sb.append("entry:\n");

        // === Aloca parâmetros no stack ===
        sb.append("  %").append(paramS).append("_addr = alloca ").append(paramTypeLLVM).append("\n");
        sb.append("  store ").append(paramTypeLLVM).append(" %").append(paramS)
                .append(", ").append(paramTypeLLVM).append("* %").append(paramS).append("_addr\n");

        sb.append("  %").append(valueS).append("_addr = alloca ").append(valueTypeLLVM).append("\n");
        sb.append("  store ").append(valueTypeLLVM).append(" %").append(valueS)
                .append(", ").append(valueTypeLLVM).append("* %").append(valueS).append("_addr\n");

        String paramPtr = "%" + paramS + "_addr";
        String valuePtr = "%" + valueS + "_addr";
        visitor.getVariableEmitter().registerVarPtr(paramS, paramPtr);
        visitor.getVariableEmitter().registerVarPtr(valueS, valuePtr);

        String structSourceType;
        String structLLVMType;
        if (specialized != null) {
            structSourceType = "Struct<" + baseStruct + "<" + specialized + ">>";
            structLLVMType = "%" + baseStruct + "_" + specialized + "*";
        } else {
            structSourceType = "Struct<" + baseStruct + ">";
            structLLVMType = "%" + baseStruct + "*";
        }

        visitor.putVarType(paramS, new TypeInfos(structSourceType, structLLVMType, null));

        // ================= COERÊNCIA DE VALOR =================
        String sourceType;
        String llvmType;

        if (declaredType != null && !declaredIsGeneric) {
            sourceType = declaredType;
            llvmType = mapToLLVMType(declaredType);
        } else if (specialized != null && (declaredType == null || declaredType.contains("?"))) {
            sourceType = specialized;
            llvmType = mapToLLVMType(specialized);
        } else {
            sourceType = "?";
            llvmType = "i8*";
        }
        visitor.putVarType(valueS, new TypeInfos(sourceType, llvmType, null));

        if (fn.getBody() != null && !fn.getBody().isEmpty()) {

            LLVisitorMain isolated = visitor.fork();

            isolated.putVarType(paramS, new TypeInfos(structSourceType, structLLVMType, null));
            isolated.putVarType(valueS, new TypeInfos(sourceType, llvmType, null));

            isolated.getVariableEmitter().registerVarPtr(paramS, paramPtr);
            isolated.getVariableEmitter().registerVarPtr(valueS, valuePtr);

            if (specialized != null)
                isolated.enterTypeSpecialization(specialized);

            for (ASTNode stmt : fn.getBody()) {

                // --- LISTADD ESPECIALIZADO PARA STRING ---
                // --- LISTADD (especializado por tipo) ---
                if (stmt instanceof ListAddNode addNode) {

                    String listCode = addNode.getListNode().accept(isolated);
                    String listTmp = extractTemp(listCode);

                    String valCode = addNode.getValuesNode().accept(isolated);
                    String valTmp = extractTemp(valCode);

                    // llvmType aqui é o tipo LLVM do VALUE (valueS),
                    // calculado lá em cima (i32, double, %String*, i8*, etc.)
                    String funcName;
                    String listLLVMType;
                    String valueLLVMType = llvmType;

                    if ("%String*".equals(llvmType)) {
                        // List<string> → usa ArrayList genérica + função específica de String (se você tiver)
                        funcName = "arraylist_add_String";
                        listLLVMType = "%ArrayList*";

                        sb.append(listCode)
                                .append(valCode)
                                .append("  call void @").append(funcName)
                                .append("(").append(listLLVMType).append(" ").append(listTmp)
                                .append(", ").append(valueLLVMType).append(" ").append(valTmp).append(")\n")
                                .append(";;VAL:").append(listTmp).append(";;TYPE:").append(listLLVMType).append("\n");

                        continue;

                    } else if ("i32".equals(llvmType)) {
                        // List<int> → usa ArrayListInt especializada
                        funcName = "arraylist_add_int";
                        listLLVMType = "%struct.ArrayListInt*";

                        sb.append(listCode)
                                .append(valCode)
                                .append("  call void @").append(funcName)
                                .append("(").append(listLLVMType).append(" ").append(listTmp)
                                .append(", ").append(valueLLVMType).append(" ").append(valTmp).append(")\n");

                        continue;

                    } else if ("double".equals(llvmType)) {
                        // Se tiver suporte a double: ajuste conforme sua runtime
                        funcName = "arraylist_add_double";
                        listLLVMType = "%struct.ArrayListDouble*";

                        sb.append(listCode)
                                .append(valCode)
                                .append("  call void @").append(funcName)
                                .append("(").append(listLLVMType).append(" ").append(listTmp)
                                .append(", ").append(valueLLVMType).append(" ").append(valTmp).append(")\n");

                        continue;

                    } else {
                        // Fallback genérico: ponteiros/structs/etc → ArrayList* + i8*
                        funcName = "arraylist_add_ptr";
                        listLLVMType = "%ArrayList*";
                        String castTmp = valTmp;

                        // Se o valor não for i8*, você provavelmente vai precisar de um cast aqui (bitcast),
                        // mas isso depende de como você representa structs/String no runtime.
                        // Por enquanto, assumimos que valTmp já é i8* quando cai aqui.

                        sb.append(listCode)
                                .append(valCode)
                                .append("  call void @").append(funcName)
                                .append("(").append(listLLVMType).append(" ").append(listTmp)
                                .append(", i8* ").append(castTmp).append(")\n");

                        continue;
                    }
                }


                // --- LISTADDALL ESPECIALIZADO PARA STRING ---
                if (stmt instanceof ListAddAllNode addAllNode) {

                    // gerar array com N elementos %String*
                    int count = addAllNode.getArgs().size();
                    String arrayTmp = temps.newTemp();
                    sb.append("  ").append(arrayTmp).append(" = alloca %String*, i64 ").append(count).append("\n");

                    int idx = 0;
                    for (ASTNode arg : addAllNode.getArgs()) {
                        String vcode = arg.accept(isolated);
                        String vtmp = extractTemp(vcode);
                        sb.append(vcode);
                        sb.append("  %tmp_str_").append(idx)
                                .append(" = getelementptr %String*, %String** ")
                                .append(arrayTmp).append(", i64 ").append(idx).append("\n")
                                .append("  store %String* ").append(vtmp).append(", %String** %tmp_str_").append(idx).append("\n");
                        idx++;
                    }

                    // chamada correta
                    sb.append("  call void @arraylist_addAll_String(%ArrayList* ")
                            .append(extractTemp(addAllNode.getTargetListNode().accept(isolated)))
                            .append(", %String** ").append(arrayTmp).append(", i64 ").append(count).append(")\n");

                    continue;
                }

                // fallback normal
                sb.append(stmt.accept(isolated));
            }


            if (specialized != null)
                isolated.exitTypeSpecialization();
        }

        sb.append("  ret ").append(retType).append(" %").append(paramS).append("\n");
        sb.append("}\n\n");
        return sb.toString();
    }

    private String extractInnerType(String s) {
        int start = s.indexOf('<');
        int end = s.indexOf('>');
        if (start == -1 || end == -1) return "?";
        return s.substring(start + 1, end).trim();
    }

    private String mapToLLVMType(String inner) {
        return switch (inner) {
            case "int" -> "i32";
            case "double" -> "double";
            case "bool", "boolean" -> "i1";
            case "string", "String" -> "%String*";
            default -> "i8*";
        };
    }
    private String extractTemp(String code) {
        int idx = code.lastIndexOf(";;VAL:");
        int endIdx = code.indexOf(";;TYPE:", idx);
        return code.substring(idx + 6, endIdx).trim();
    }


}
