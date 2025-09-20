; LLVM IR mínimo do main
declare i32 @printf(i8*, ...)
declare i32 @getchar()
@.strInt = private constant [4 x i8] c"%d\0A\00"
@.strDouble = private constant [4 x i8] c"%f\0A\00"
@.strStr = private constant [4 x i8] c"%s\0A\00"


define i32 @main() {
  ; VariableDeclarationNode
  %z = alloca i32
  ; VariableDeclarationNode
  %x = alloca i32
  ; AssignmentNode
  store i32 11, i32* %x
  ; AssignmentNode
  store i32 4, i32* %z
  ; VariableDeclarationNode
  %resultado = alloca i32
  %t0 = load i32, i32* %x
;;VAL:%t0;;TYPE:i32

  %t1 = load i32, i32* %z
;;VAL:%t1;;TYPE:i32

  %t2 = add i32 %t0, %t1
;;VAL:%t2;;TYPE:i32

  store i32 %t2, i32* %resultado
  ; PrintNode
  %t3 = load i32, i32* %resultado
  call i32 (i8*, ...) @printf(i8* getelementptr ([4 x i8], [4 x i8]* @.strInt, i32 0, i32 0), i32 %t3)
  call i32 @getchar()
  ret i32 0
}
