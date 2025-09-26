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

@.str0 = private constant [10 x i8] c"zardelas\0A\00"

define i32 @main() {
  ; VariableDeclarationNode
  %a = alloca i32
  ; AssignmentNode
  store i32 14, i32* %a
  ; VariableDeclarationNode
  %b = alloca double
  store double 3.14, double* %b
  ; VariableDeclarationNode
  %z = alloca i32
  %t0 = call i32 @inputInt(i8* null)
;;VAL:%t0 ;;TYPE:i32
  store i32 %t0, i32* %z
  ; VariableDeclarationNode
  %resultado = alloca i32
  %t1 = load i32, i32* %a
;;VAL:%t1;;TYPE:i32

  %t2 = load i32, i32* %z
;;VAL:%t2;;TYPE:i32

  %t3 = add i32 %t1, %t2
;;VAL:%t3;;TYPE:i32

  store i32 %t3, i32* %resultado
  ; PrintNode
  %t4 = load i32, i32* %resultado
  call i32 (i8*, ...) @printf(i8* getelementptr ([4 x i8], [4 x i8]* @.strInt, i32 0, i32 0), i32 %t4)
  ; VariableDeclarationNode
  %nome = alloca i8*
  store i8* getelementptr ([10 x i8], [10 x i8]* @.str0, i32 0, i32 0), i8** %nome
  ; VariableDeclarationNode
  %isReal = alloca i1
  store i1 1, i1* %isReal
  ; PrintNode
  %t5 = load i1, i1* %isReal
  %t6 = zext i1 %t5 to i32
  call i32 (i8*, ...) @printf(i8* getelementptr ([4 x i8], [4 x i8]* @.strInt, i32 0, i32 0), i32 %t6)
  ; PrintNode
  %t7 = load i32, i32* %a
  call i32 (i8*, ...) @printf(i8* getelementptr ([4 x i8], [4 x i8]* @.strInt, i32 0, i32 0), i32 %t7)
  ; PrintNode
  %t8 = load double, double* %b
  call i32 (i8*, ...) @printf(i8* getelementptr ([4 x i8], [4 x i8]* @.strDouble, i32 0, i32 0), double %t8)
  ; PrintNode
  %t9 = load i8*, i8** %nome
  call i32 (i8*, ...) @printf(i8* getelementptr ([4 x i8], [4 x i8]* @.strStr, i32 0, i32 0), i8* %t9)
  call i32 @getchar()
  ret i32 0
}
