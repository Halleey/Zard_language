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

@.str0 = private constant [20 x i8] c"digite um novo nome\00"
@.str1 = private constant [7 x i8] c"halley\00"
@.str2 = private constant [6 x i8] c"angel\00"

; === Função: sub ===
; === Função: sub ===
define i32 @sub(i32 %a, i32 %b) {
entry:
  %a.addr = alloca i32
;;VAL:%a.addr;;TYPE:i32
  store i32 %a, i32* %a.addr
  %b.addr = alloca i32
;;VAL:%b.addr;;TYPE:i32
  store i32 %b, i32* %b.addr
  %t0 = load i32, i32* %a.addr
;;VAL:%t0;;TYPE:i32

  %t1 = load i32, i32* %b.addr
;;VAL:%t1;;TYPE:i32

  %t2 = sub i32 %t0, %t1
;;VAL:%t2;;TYPE:i32
  ret i32 %t2
}

; === Função: dividir ===
; === Função: dividir ===
define i32 @dividir(i32 %a, i32 %b) {
entry:
  %a.addr = alloca i32
;;VAL:%a.addr;;TYPE:i32
  store i32 %a, i32* %a.addr
  %b.addr = alloca i32
;;VAL:%b.addr;;TYPE:i32
  store i32 %b, i32* %b.addr
  %t3 = load i32, i32* %a.addr
;;VAL:%t3;;TYPE:i32

  %t4 = load i32, i32* %b.addr
;;VAL:%t4;;TYPE:i32

  %t5 = sdiv i32 %t3, %t4
;;VAL:%t5;;TYPE:i32
  ret i32 %t5
}

define i32 @main() {
  ; PrintNode
  %t6 = add i32 0, 10
;;VAL:%t6;;TYPE:i32
  %t7 = add i32 0, 2
;;VAL:%t7;;TYPE:i32
  %t8 = call i32 @sub(i32 %t6, i32 %t7)
  call i32 (i8*, ...) @printf(i8* getelementptr ([4 x i8], [4 x i8]* @.strInt, i32 0, i32 0), i32 %t8)
  ; PrintNode
  %t9 = add i32 0, 5
;;VAL:%t9;;TYPE:i32
  %t10 = add i32 0, 2
;;VAL:%t10;;TYPE:i32
  %t11 = call i32 @dividir(i32 %t9, i32 %t10)
  call i32 (i8*, ...) @printf(i8* getelementptr ([4 x i8], [4 x i8]* @.strInt, i32 0, i32 0), i32 %t11)
  ; VariableDeclarationNode
  %nomes = alloca i8*
;;VAL:%nomes;;TYPE:i8*
  %t12 = call i8* @arraylist_create(i64 4)
  %t14 = call %DynValue* @createString(i8* getelementptr ([7 x i8], [7 x i8]* @.str1, i32 0, i32 0))
  call void @setItems(i8* %t12, %DynValue* %t14)
  %t16 = call %DynValue* @createString(i8* getelementptr ([6 x i8], [6 x i8]* @.str2, i32 0, i32 0))
  call void @setItems(i8* %t12, %DynValue* %t16)
;;VAL:%t12;;TYPE:i8*
  store i8* %t12, i8** %nomes
  ; PrintNode
  call i32 (i8*, ...) @printf(i8* getelementptr ([20 x i8], [20 x i8]* @.str0, i32 0, i32 0))
  ; VariableDeclarationNode
  %no = alloca i8*
;;VAL:%no;;TYPE:i8*
  %t17 = call i8* @inputString(i8* null)
;;VAL:%t17 ;;TYPE:i8*
  store i8* %t17, i8** %no
  ; ListAddNode
  %t18 = load i8*, i8** %nomes
;;VAL:%t18;;TYPE:i8*
  %t19 = load i8*, i8** %no
;;VAL:%t19;;TYPE:i8*
  %t20 = call %DynValue* @createString(i8* %t19)
  call void @setItems(i8* %t18, %DynValue* %t20)
;;VAL:%t20;;TYPE:%DynValue*
  ; PrintNode
  %t21 = load i8*, i8** %nomes
  call void @printList(i8* %t21)
  call i32 @getchar()
  ret i32 0
}
