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


; === Função: teste ===
define i8* @teste(i8* %a, i8* %z) {
entry:
%t0 = alloca i8*
  store i8* %a, i8** %t0
%t1 = alloca i8*
  store i8* %z, i8** %t1
  %resultado = alloca i32
  %t2 = load i32, i32* %a
;;VAL:%t2;;TYPE:i32

  %t3 = load i32, i32* %z
;;VAL:%t3;;TYPE:i32

  %t4 = add i32 %t2, %t3
;;VAL:%t4;;TYPE:i32

  store i32 %t4, i32* %resultado
  %t5 = load i32, i32* %resultado
  call i32 (i8*, ...) @printf(i8* getelementptr ([4 x i8], [4 x i8]* @.strInt, i32 0, i32 0), i32 %t5)
  ret i8* null
}

define i32 @main() {
  ; VariableDeclarationNode
  %a = alloca i32
  store i32 10, i32* %a
  ; PrintNode
  %t6 = load i32, i32* %a
  call i32 (i8*, ...) @printf(i8* getelementptr ([4 x i8], [4 x i8]* @.strInt, i32 0, i32 0), i32 %t6)
  call i32 @getchar()
  ret i32 0
}
