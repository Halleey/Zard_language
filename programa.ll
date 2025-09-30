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
    declare i8* @inputString(i8*)

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
@.str2 = private constant [5 x i8] c"zard\00"
@.str3 = private constant [7 x i8] c"halley\00"

define i32 @main() {
  ; VariableDeclarationNode
  %list = alloca i8*
;;VAL:%list;;TYPE:i8*
  %t0 = call i8* @arraylist_create(i64 4)
;;VAL:%t0;;TYPE:i8*
  store i8* %t0, i8** %list
  ; PrintNode
  %t1 = load i8*, i8** %list
  call void @printList(i8* %t1)
  ; VariableDeclarationNode
  %nome = alloca i8*
;;VAL:%nome;;TYPE:i8*
  store i8* getelementptr ([9 x i8], [9 x i8]* @.str0, i32 0, i32 0), i8** %nome
  ; VariableDeclarationNode
  %nomes = alloca i8*
;;VAL:%nomes;;TYPE:i8*
  %t2 = call i8* @arraylist_create(i64 10)
  %t3 = alloca [10 x %DynValue*]
  %t4 = call %DynValue* @createString(i8* getelementptr ([6 x i8], [6 x i8]* @.str1, i32 0, i32 0))
  %t5 = getelementptr inbounds [10 x %DynValue*], [10 x %DynValue*]* %t3, i32 0, i32 0
  store %DynValue* %t4, %DynValue** %t5
  %t6 = call %DynValue* @createInt(i32 99)
  %t7 = getelementptr inbounds [10 x %DynValue*], [10 x %DynValue*]* %t3, i32 0, i32 1
  store %DynValue* %t6, %DynValue** %t7
  %t8 = call %DynValue* @createDouble(double 9.45)
  %t9 = getelementptr inbounds [10 x %DynValue*], [10 x %DynValue*]* %t3, i32 0, i32 2
  store %DynValue* %t8, %DynValue** %t9
  %t10 = call %DynValue* @createString(i8* getelementptr ([5 x i8], [5 x i8]* @.str2, i32 0, i32 0))
  %t11 = getelementptr inbounds [10 x %DynValue*], [10 x %DynValue*]* %t3, i32 0, i32 3
  store %DynValue* %t10, %DynValue** %t11
  %t12 = load i8*, i8** %nome
;;VAL:%t12;;TYPE:i8*
  %t13 = call %DynValue* @createString(i8* %t12)
  %t14 = getelementptr inbounds [10 x %DynValue*], [10 x %DynValue*]* %t3, i32 0, i32 4
  store %DynValue* %t13, %DynValue** %t14
  %t15 = call %DynValue* @createString(i8* getelementptr ([7 x i8], [7 x i8]* @.str3, i32 0, i32 0))
  %t16 = getelementptr inbounds [10 x %DynValue*], [10 x %DynValue*]* %t3, i32 0, i32 5
  store %DynValue* %t15, %DynValue** %t16
  %t17 = call %DynValue* @createDouble(double 3.14)
  %t18 = getelementptr inbounds [10 x %DynValue*], [10 x %DynValue*]* %t3, i32 0, i32 6
  store %DynValue* %t17, %DynValue** %t18
  %t19 = call %DynValue* @createInt(i32 5)
  %t20 = getelementptr inbounds [10 x %DynValue*], [10 x %DynValue*]* %t3, i32 0, i32 7
  store %DynValue* %t19, %DynValue** %t20
  %t21 = call %DynValue* @createBool(i1 1)
  %t22 = getelementptr inbounds [10 x %DynValue*], [10 x %DynValue*]* %t3, i32 0, i32 8
  store %DynValue* %t21, %DynValue** %t22
  %t23 = call %DynValue* @createBool(i1 0)
  %t24 = getelementptr inbounds [10 x %DynValue*], [10 x %DynValue*]* %t3, i32 0, i32 9
  store %DynValue* %t23, %DynValue** %t24
  %t25 = getelementptr inbounds [10 x %DynValue*], [10 x %DynValue*]* %t3, i32 0, i32 0
  %t26 = bitcast i8* %t2 to %ArrayList*
  call void @addAll(%ArrayList* %t26, %DynValue** %t25, i64 10)
;;VAL:%t2;;TYPE:i8*
  store i8* %t2, i8** %nomes
  ; PrintNode
  %t27 = load i8*, i8** %nomes
  %t28 = bitcast i8* %t27 to %ArrayList*
  %t29 = add i32 0, 0
  %t30 = call %DynValue* @getItem(%ArrayList* %t28, i32 %t29)
;;VAL:%t30
;;TYPE:any
  call void @printDynValue(%DynValue* %t30)
  ; PrintNode
  %t31 = load i8*, i8** %nomes
;;VAL:%t31;;TYPE:i8*
  %t32 = bitcast i8* %t31 to %ArrayList*
  %t33 = call i32 @size(%ArrayList* %t32)
  
  call i32 (i8*, ...) @printf(i8* getelementptr ([4 x i8], [4 x i8]* @.strInt, i32 0, i32 0), i32 %t33)
  ; PrintNode
  %t34 = load i8*, i8** %nomes
  call void @printList(i8* %t34)
  ; ListClearNode
  %t35 = load i8*, i8** %nomes
;;VAL:%t35;;TYPE:i8*
  %t36 = bitcast i8* %t35 to %ArrayList*
  call void @clearList(%ArrayList* %t36)
  ; PrintNode
  %t37 = load i8*, i8** %nomes
  call void @printList(i8* %t37)
  call i32 @getchar()
  ret i32 0
}
