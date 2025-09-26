    declare i32 @printf(i8*, ...)
    declare i32 @getchar()
    @.strInt = private constant [4 x i8] c"%d\0A\00"
    @.strDouble = private constant [4 x i8] c"%f\0A\00"
    @.strStr = private constant [4 x i8] c"%s\0A\00"

    ; === Tipo opaco DynValue ===
    %DynValue = type opaque

    ; === Funções do runtime DynValue ===
    declare %DynValue* @createInt(i32)
    declare %DynValue* @createDouble(double)
    declare %DynValue* @createBool(i1)
    declare %DynValue* @createString(i8*)

    ; === Funções de conversão DynValue -> tipo primitivo ===
    declare i32 @dynToInt(%DynValue*)
    declare double @dynToDouble(%DynValue*)
    declare i1 @dynToBool(%DynValue*)
    declare i8* @dynToString(%DynValue*)

    ; === Função de input ===
    declare %DynValue* @input(i8*)

; === Runtime de listas ===
%ArrayList = type opaque
declare i8* @arraylist_create(i64)
declare void @setItems(i8*, %DynValue*)
declare void @printList(i8*)
declare void @removeItem(%ArrayList*, i64)
declare void @clearList(%ArrayList*)
declare void @freeList(%ArrayList*)
declare i32 @size(%ArrayList*)
declare %DynValue* @getItem(%ArrayList*, i32)
declare void @printDynValue(%DynValue*)
declare void @addAll(%ArrayList*, %DynValue**, i64)


define i32 @main() {
  ; VariableDeclarationNode
  %a = alloca i32
  %t0 = call %DynValue* @input(i8* null)
;;VAL:%t0 ;;TYPE:dyn
  %t1 = call i32 @dynToInt(%DynValue* %t0)
  store i32 %t1, i32* %a
  ; VariableDeclarationNode
  %nome = alloca i8*
  %t2 = call %DynValue* @input(i8* null)
;;VAL:%t2 ;;TYPE:dyn
  %t3 = call i8* @dynToString(%DynValue* %t2)
  store i8* %t3, i8** %nome
  ; VariableDeclarationNode
  %test = alloca i8*
  %t4 = call i8* @arraylist_create(i64 4)
;;VAL:%t4;;TYPE:i8*
  store i8* %t4, i8** %test
  ; ListAddAllNode
  %t5 = load i8*, i8** %test
;;VAL:%t5;;TYPE:i8*
  %t6 = alloca %DynValue*, i64 2
  %t7 = load i32, i32* %a
;;VAL:%t7;;TYPE:i32
  %t8 = call %DynValue* @createInt(i32 %t7)
  %t9 = getelementptr inbounds %DynValue*, %DynValue** %t6, i64 0
  store %DynValue* %t8, %DynValue** %t9
  %t10 = load i8*, i8** %nome
;;VAL:%t10;;TYPE:i8*
  %t11 = call %DynValue* @createString(i8* %t10)
  %t12 = getelementptr inbounds %DynValue*, %DynValue** %t6, i64 1
  store %DynValue* %t11, %DynValue** %t12
  %t13 = bitcast i8* %t5 to %ArrayList*
  call void @addAll(%ArrayList* %t13, %DynValue** %t6, i64 2)
  ; ListRemoveNode
  %t14 = load i8*, i8** %test
;;VAL:%t14;;TYPE:i8*
  %t15 = add i32 0, 0
;;VAL:%t15;;TYPE:i32
  %t16 = sext i32 %t15 to i64
  %t17 = bitcast i8* %t14 to %ArrayList*
  call void @removeItem(%ArrayList* %t17, i64 %t16)
  ; PrintNode
  %t18 = load i8*, i8** %test
  call void @printList(i8* %t18)
  call i32 @getchar()
  ret i32 0
}
