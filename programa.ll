; LLVM IR mínimo do main
declare i32 @printf(i8*, ...)
declare i32 @getchar()
@.strInt = private constant [4 x i8] c"%d\0A\00"
@.strDouble = private constant [4 x i8] c"%f\0A\00"
@.strStr = private constant [4 x i8] c"%s\0A\00"

@.str0 = private constant [10 x i8] c"hallyson\0A\00"
@.str1 = private constant [15 x i8] c"testando aqui\0A\00"

define i32 @main() {
  ; VariableDeclarationNode
  %a = alloca double
  store double 5.43, double* %a
  ; VariableDeclarationNode
  %b = alloca i32
  store i32 3, i32* %b
  ; VariableDeclarationNode
  %isReal = alloca i1
  store i1 1, i1* %isReal
  ; VariableDeclarationNode
  %isFalse = alloca i1
  store i1 0, i1* %isFalse
  ; VariableDeclarationNode
  %c = alloca double
  ; VariableDeclarationNode
  %teste = alloca i8*
  store i8* getelementptr ([10 x i8], [10 x i8]* @.str0, i32 0, i32 0), i8** %teste
  ; PrintNode
  %tStr159938786288300 = load i8*, i8** %teste
  call i32 (i8*, ...) @printf(i8* getelementptr ([4 x i8], [4 x i8]* @.strStr, i32 0, i32 0), i8* %tStr159938786288300)
  ; AssignmentNode
  store double 4.4, double* %c
  ; PrintNode
  %t0 = load double, double* %a
  call i32 (i8*, ...) @printf(i8* getelementptr ([4 x i8], [4 x i8]* @.strDouble, i32 0, i32 0), double %t0)
  ; PrintNode
  %t1 = load i1, i1* %isReal
  %tBool159938794195700 = zext i1 %t1 to i32
  call i32 (i8*, ...) @printf(i8* getelementptr ([4 x i8], [4 x i8]* @.strInt, i32 0, i32 0), i32 %tBool159938794195700)
  ; PrintNode
  %t2 = load i1, i1* %isFalse
  %tBool159938794383800 = zext i1 %t2 to i32
  call i32 (i8*, ...) @printf(i8* getelementptr ([4 x i8], [4 x i8]* @.strInt, i32 0, i32 0), i32 %tBool159938794383800)
  ; PrintNode
  call i32 (i8*, ...) @printf(i8* getelementptr ([15 x i8], [15 x i8]* @.str1, i32 0, i32 0))
  ; PrintNode
  %t3 = load double, double* %c
  call i32 (i8*, ...) @printf(i8* getelementptr ([4 x i8], [4 x i8]* @.strDouble, i32 0, i32 0), double %t3)
  call i32 @getchar()
  ret i32 0
}
