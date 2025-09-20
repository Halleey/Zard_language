; LLVM IR mínimo do main
declare i32 @printf(i8*, ...)
declare i32 @getchar()
@.strInt = private constant [4 x i8] c"%d\0A\00"
@.strDouble = private constant [4 x i8] c"%f\0A\00"
@.strStr = private constant [4 x i8] c"%s\0A\00"

@.str0 = private constant [10 x i8] c"hallyson\0A\00"

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
  %tStr162163897587000 = load i8*, i8** %teste
  call i32 (i8*, ...) @printf(i8* getelementptr ([4 x i8], [4 x i8]* @.strStr, i32 0, i32 0), i8* %tStr162163897587000)
  ; AssignmentNode
  store double 4.4, double* %c
  ; VariableDeclarationNode
  %z = alloca i32
  store i32 0, i32* %z
  ; UnaryOpNode
  %t0 = load i32, i32* %z
  %t1 = add i32 %t0, 1
  store i32 %t1, i32* %z
  ; PrintNode
  %t2 = load i32, i32* %z
  call i32 (i8*, ...) @printf(i8* getelementptr ([4 x i8], [4 x i8]* @.strInt, i32 0, i32 0), i32 %t2)
  ; UnaryOpNode
  %t3 = load i32, i32* %z
  %t4 = sub i32 %t3, 1
  store i32 %t4, i32* %z
  ; PrintNode
  %t5 = load i32, i32* %z
  call i32 (i8*, ...) @printf(i8* getelementptr ([4 x i8], [4 x i8]* @.strInt, i32 0, i32 0), i32 %t5)
  call i32 @getchar()
  ret i32 0
}
