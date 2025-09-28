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

@.str0 = private constant [1 x i8] c"\00"
@.str1 = private constant [9 x i8] c"hallyson\00"

; === Função: teste ===
define i32 @teste(i32 %a) {
entry:
  %a.addr = alloca i32
;;VAL:%a.addr;;TYPE:i32
  store i32 %a, i32* %a.addr
  %t0 = load i32, i32* %a.addr
;;VAL:%t0;;TYPE:i32
  ret i32 %t0
}

define i32 @main() {
  ; PrintNode
  %t1 = add i32 0, 10
;;VAL:%t1;;TYPE:i32
  %t2 = call i32 @teste(i32 %t1)
  call i32 (i8*, ...) @printf(i8* getelementptr ([4 x i8], [4 x i8]* @.strInt, i32 0, i32 0), i32 %t2)
  ; VariableDeclarationNode
  %a = alloca i32
;;VAL:%a;;TYPE:i32
  store i32 5, i32* %a
  ; VariableDeclarationNode
  %b = alloca double
;;VAL:%b;;TYPE:double
  %t3 = call double @inputDouble(i8* null)
;;VAL:%t3 ;;TYPE:double
  store double %t3, double* %b
  ; VariableDeclarationNode
  %nome = alloca i8*
;;VAL:%nome;;TYPE:i8*
  store i8* getelementptr ([9 x i8], [9 x i8]* @.str1, i32 0, i32 0), i8** %nome
  ; VariableDeclarationNode
  %l = alloca i1
;;VAL:%l;;TYPE:i1
  store i1 1, i1* %l
  ; PrintNode
  %t4 = load i32, i32* %a
  call i32 (i8*, ...) @printf(i8* getelementptr ([4 x i8], [4 x i8]* @.strInt, i32 0, i32 0), i32 %t4)
  ; PrintNode
  %t5 = load double, double* %b
  call i32 (i8*, ...) @printf(i8* getelementptr ([4 x i8], [4 x i8]* @.strDouble, i32 0, i32 0), double %t5)
  ; PrintNode
  %t6 = load i8*, i8** %nome
  call i32 (i8*, ...) @printf(i8* getelementptr ([4 x i8], [4 x i8]* @.strStr, i32 0, i32 0), i8* %t6)
  ; PrintNode
  %t7 = load i1, i1* %l
  %t8 = zext i1 %t7 to i32
  call i32 (i8*, ...) @printf(i8* getelementptr ([4 x i8], [4 x i8]* @.strInt, i32 0, i32 0), i32 %t8)
  call i32 @getchar()
  ret i32 0
}
