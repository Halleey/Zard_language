; LLVM IR mínimo do main
declare i32 @printf(i8*, ...)
declare i32 @getchar()
@.strInt = private constant [4 x i8] c"%d\0A\00"
@.strDouble = private constant [4 x i8] c"%f\0A\00"
@.strStr = private constant [4 x i8] c"%s\0A\00"

@.str0 = private constant [7 x i8] c"maior\0A\00"
@.str1 = private constant [7 x i8] c"menor\0A\00"
@.str2 = private constant [7 x i8] c"igual\0A\00"

define i32 @main() {
  ; VariableDeclarationNode
  %a = alloca double
  store double 2.5, double* %a
  ; VariableDeclarationNode
  %b = alloca double
  store double 3.1, double* %b
  ; IfNode
  %t0 = load double, double* %a
;;VAL:%t0;;TYPE:double

  %t1 = load double, double* %b
;;VAL:%t1;;TYPE:double

  %t2 = fcmp ogt double %t0, %t1
;;VAL:%t2;;TYPE:i1

  br i1 %t2, label %then_0, label %else_0
then_0:
  call i32 (i8*, ...) @printf(i8* getelementptr ([7 x i8], [7 x i8]* @.str0, i32 0, i32 0))
  br label %endif_0
else_0:
  %t3 = load double, double* %a
;;VAL:%t3;;TYPE:double

  %t4 = load double, double* %b
;;VAL:%t4;;TYPE:double

  %t5 = fcmp olt double %t3, %t4
;;VAL:%t5;;TYPE:i1

  br i1 %t5, label %then_1, label %else_1
then_1:
  call i32 (i8*, ...) @printf(i8* getelementptr ([7 x i8], [7 x i8]* @.str1, i32 0, i32 0))
  br label %endif_1
else_1:
  call i32 (i8*, ...) @printf(i8* getelementptr ([7 x i8], [7 x i8]* @.str2, i32 0, i32 0))
  br label %endif_1
endif_1:
  br label %endif_0
endif_0:
  call i32 @getchar()
  ret i32 0
}
