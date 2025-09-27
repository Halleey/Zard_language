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
    declare i32 @inputInt(i8*)
    declare double @inputDouble(i8*)
    declare i1 @inputBool(i8*)
    declare i8*@inputString(i8*)

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

@.str0 = private constant [9 x i8] c"hallyson\00"
@.str1 = private constant [6 x i8] c"teste\00"

define i32 @main() {
  ; VariableDeclarationNode
  %a = alloca i32
  ; VariableDeclarationNode
  %b = alloca double
  %t0 = call double @inputDouble(i8* null)
;;VAL:%t0 ;;TYPE:double
  store double %t0, double* %b
  ; VariableDeclarationNode
  %nome = alloca i8*
  store i8* getelementptr ([9 x i8], [9 x i8]* @.str0, i32 0, i32 0), i8** %nome
  ; VariableDeclarationNode
  %isReal = alloca i1
  %t1 = call i1 @inputBool(i8* null)
;;VAL:%t1 ;;TYPE:i1
  store i1 %t1, i1* %isReal
  ; VariableDeclarationNode
  %teste = alloca i8*
  %t2 = call i8* @arraylist_create(i64 4)
  %t4 = call %DynValue* @createString(i8* getelementptr ([6 x i8], [6 x i8]* @.str1, i32 0, i32 0))
  call void @setItems(i8* %t2, %DynValue* %t4)
  %t5 = call %DynValue* @createInt(i32 13)
  call void @setItems(i8* %t2, %DynValue* %t5)
  %t6 = call %DynValue* @createBool(i1 1)
  call void @setItems(i8* %t2, %DynValue* %t6)
  %t7 = call %DynValue* @createBool(i1 0)
  call void @setItems(i8* %t2, %DynValue* %t7)
;;VAL:%t2;;TYPE:i8*
  store i8* %t2, i8** %teste
  ; ListAddAllNode
  %t8 = load i8*, i8** %teste
;;VAL:%t8;;TYPE:i8*
  %t9 = alloca %DynValue*, i64 4
  %t10 = load i32, i32* %a
;;VAL:%t10;;TYPE:i32
  %t11 = call %DynValue* @createInt(i32 %t10)
  %t12 = getelementptr inbounds %DynValue*, %DynValue** %t9, i64 0
  store %DynValue* %t11, %DynValue** %t12
  %t13 = load double, double* %b
;;VAL:%t13;;TYPE:double
  %t14 = call %DynValue* @createDouble(double %t13)
  %t15 = getelementptr inbounds %DynValue*, %DynValue** %t9, i64 1
  store %DynValue* %t14, %DynValue** %t15
  %t16 = load i8*, i8** %nome
;;VAL:%t16;;TYPE:i8*
  %t17 = call %DynValue* @createString(i8* %t16)
  %t18 = getelementptr inbounds %DynValue*, %DynValue** %t9, i64 2
  store %DynValue* %t17, %DynValue** %t18
  %t19 = load i1, i1* %isReal
;;VAL:%t19;;TYPE:i1
  %t20 = call %DynValue* @createBool(i1 %t19)
  %t21 = getelementptr inbounds %DynValue*, %DynValue** %t9, i64 3
  store %DynValue* %t20, %DynValue** %t21
  %t22 = bitcast i8* %t8 to %ArrayList*
  call void @addAll(%ArrayList* %t22, %DynValue** %t9, i64 4)
  ; PrintNode
  %t23 = load i8*, i8** %teste
  call void @printList(i8* %t23)
  call i32 @getchar()
  ret i32 0
}
