; LLVM IR mínimo do main
declare i32 @printf(i8*, ...)
declare i32 @getchar()
@.strInt = private constant [4 x i8] c"%d\0A\00"
@.strDouble = private constant [4 x i8] c"%f\0A\00"
@.strStr = private constant [4 x i8] c"%s\0A\00"


define i32 @main() {
  ; VariableDeclarationNode
  %i = alloca i32
  store i32 0, i32* %i
  ; WhileNode
  br label %while_cond_0
while_cond_0:
  %t0 = load i32, i32* %i
;;VAL:%t0;;TYPE:i32

  %t1 = add i32 0, 5
;;VAL:%t1;;TYPE:i32

  %t2 = icmp slt i32 %t0, %t1
;;VAL:%t2;;TYPE:i1
  br i1 %t2, label %while_body_1, label %while_end_2
while_body_1:
  %t3 = load i32, i32* %i
  call i32 (i8*, ...) @printf(i8* getelementptr ([4 x i8], [4 x i8]* @.strInt, i32 0, i32 0), i32 %t3)
  %t4 = load i32, i32* %i
  %t5 = add i32 %t4, 1
  store i32 %t5, i32* %i
  br label %while_cond_0
while_end_2:
  call i32 @getchar()
  ret i32 0
}
