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
@.str1 = private constant [7 x i8] c"halley\00"

; === Função: dobrar ===
define i32 @dobrar(i32 %n) {
entry:
  %n.addr = alloca i32
;;VAL:%n.addr;;TYPE:i32
  store i32 %n, i32* %n.addr
  %t0 = load i32, i32* %n.addr
;;VAL:%t0;;TYPE:i32

  %t1 = add i32 0, 2
;;VAL:%t1;;TYPE:i32

  %t2 = mul i32 %t0, %t1
;;VAL:%t2;;TYPE:i32
  ret i32 %t2
}

; === Função: multiplicar ===
define i32 @multiplicar(i32 %n) {
entry:
  %n.addr = alloca i32
;;VAL:%n.addr;;TYPE:i32
  store i32 %n, i32* %n.addr
  %t3 = load i32, i32* %n.addr
;;VAL:%t3;;TYPE:i32
  %t4 = call i32 @dobrar(i32 %t3)
;;VAL:%t4;;TYPE:i32
  ret i32 %t4
}

define i32 @main() {
  ; PrintNode
  %t5 = add i32 0, 10
;;VAL:%t5;;TYPE:i32
  %t6 = call i32 @multiplicar(i32 %t5)
  call i32 (i8*, ...) @printf(i8* getelementptr ([4 x i8], [4 x i8]* @.strInt, i32 0, i32 0), i32 %t6)
  ; VariableDeclarationNode
  %nome = alloca i8*
;;VAL:%nome;;TYPE:i8*
  store i8* getelementptr ([9 x i8], [9 x i8]* @.str0, i32 0, i32 0), i8** %nome
  ; VariableDeclarationNode
  %nomes = alloca i8*
;;VAL:%nomes;;TYPE:i8*
  %t7 = call i8* @arraylist_create(i64 4)
  %t8 = load i8*, i8** %nome
;;VAL:%t8;;TYPE:i8*
  %t9 = call %DynValue* @createString(i8* %t8)
  call void @setItems(i8* %t7, %DynValue* %t9)
  %t11 = call %DynValue* @createString(i8* getelementptr ([7 x i8], [7 x i8]* @.str1, i32 0, i32 0))
  call void @setItems(i8* %t7, %DynValue* %t11)
  %t12 = call %DynValue* @createDouble(double 3.14)
  call void @setItems(i8* %t7, %DynValue* %t12)
  %t13 = call %DynValue* @createInt(i32 5)
  call void @setItems(i8* %t7, %DynValue* %t13)
  %t14 = call %DynValue* @createBool(i1 1)
  call void @setItems(i8* %t7, %DynValue* %t14)
  %t15 = call %DynValue* @createBool(i1 0)
  call void @setItems(i8* %t7, %DynValue* %t15)
;;VAL:%t7;;TYPE:i8*
  store i8* %t7, i8** %nomes
  ; PrintNode
  %t16 = load i8*, i8** %nomes
  call void @printList(i8* %t16)
  call i32 @getchar()
  ret i32 0
}
