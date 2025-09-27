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

@.str0 = private constant [7 x i8] c"halley\00"
@.str1 = private constant [6 x i8] c"teste\00"

; === Função: somar ===
; === Função: somar ===
define i32 @somar(i32 %a, i32 %b) {
entry:
  %a.addr = alloca i32
  store i32 %a, i32* %a.addr
  %b.addr = alloca i32
  store i32 %b, i32* %b.addr
  %t0 = load i32, i32* %a.addr
;;VAL:%t0;;TYPE:i32

  %t1 = load i32, i32* %b.addr
;;VAL:%t1;;TYPE:i32

  %t2 = add i32 %t0, %t1
;;VAL:%t2;;TYPE:i32
  ret i32 %t2
}

; === Função: multiplicar ===
; === Função: multiplicar ===
define double @multiplicar(double %a, i32 %b) {
entry:
  %a.addr = alloca double
  store double %a, double* %a.addr
  %b.addr = alloca i32
  store i32 %b, i32* %b.addr
  %t3 = load double, double* %a.addr
;;VAL:%t3;;TYPE:double

  %t4 = load i32, i32* %b.addr
;;VAL:%t4;;TYPE:i32

  %t6 = sitofp i32 %t4 to double
  %t5 = fadd double %t3, %t6
;;VAL:%t5;;TYPE:double
  ret double %t5
}

define i32 @main() {
  ; PrintNode
  %t7 = add i32 0, 14
;;VAL:%t7;;TYPE:i32
  %t8 = add i32 0, 13
;;VAL:%t8;;TYPE:i32
  %t9 = call i32 @somar(i32 %t7, i32 %t8)
  call i32 (i8*, ...) @printf(i8* getelementptr ([4 x i8], [4 x i8]* @.strInt, i32 0, i32 0), i32 %t9)
  ; VariableDeclarationNode
  %random.addr = alloca i8*
  %t10 = call i8* @arraylist_create(i64 4)
  %t12 = call %DynValue* @createString(i8* getelementptr ([6 x i8], [6 x i8]* @.str1, i32 0, i32 0))
  call void @setItems(i8* %t10, %DynValue* %t12)
  %t13 = call %DynValue* @createBool(i1 1)
  call void @setItems(i8* %t10, %DynValue* %t13)
  %t14 = call %DynValue* @createBool(i1 0)
  call void @setItems(i8* %t10, %DynValue* %t14)
  %t15 = call %DynValue* @createDouble(double 3.75)
  call void @setItems(i8* %t10, %DynValue* %t15)
  %t16 = call %DynValue* @createInt(i32 9)
  call void @setItems(i8* %t10, %DynValue* %t16)
;;VAL:%t10;;TYPE:i8*
  store i8* %t10, i8** %random.addr
;;VAL:%t10;;TYPE:i8*
  ; VariableDeclarationNode
  %nome.addr = alloca i8*
  store i8* getelementptr ([7 x i8], [7 x i8]* @.str0, i32 0, i32 0), i8** %nome.addr
;;VAL:@.str0;;TYPE:i8*
  ; ListAddNode
  %t17 = load i8*, i8** %random.addr
;;VAL:%t17;;TYPE:i8*
  %t18 = load i8*, i8** %nome.addr
;;VAL:%t18;;TYPE:i8*
  %t19 = call %DynValue* @createString(i8* %t18)
  call void @setItems(i8* %t17, %DynValue* %t19)
;;VAL:%t19;;TYPE:%DynValue*
  ; ListRemoveNode
  %t20 = load i8*, i8** %random.addr
;;VAL:%t20;;TYPE:i8*
  %t21 = add i32 0, 1
;;VAL:%t21;;TYPE:i32
  %t22 = sext i32 %t21 to i64
  %t23 = bitcast i8* %t20 to %ArrayList*
  call void @removeItem(%ArrayList* %t23, i64 %t22)
  ; PrintNode
  %t24 = load i8*, i8** %random.addr
  %t25 = bitcast i8* %t24 to %ArrayList*
  %t26 = add i32 0, 1
  %t27 = call %DynValue* @getItem(%ArrayList* %t25, i32 %t26)
;;VAL:%t27
;;TYPE:any
  call void @printDynValue(%DynValue* %t27)
  ; PrintNode
  %t28 = load i8*, i8** %random.addr
  call void @printList(i8* %t28)
  call i32 @getchar()
  ret i32 0
}
