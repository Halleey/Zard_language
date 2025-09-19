; LLVM IR mínimo do main
declare i32 @printf(i8*, ...)
@.strInt = private constant [4 x i8] c"%d\0A\00"
@.strDouble = private constant [4 x i8] c"%f\0A\00"

@.str0 = private constant [28 x i8] c"testando umas paradas aqui\0A\00"

define i32 @main() {
  ; VariableDeclarationNode
  %a = alloca double
  store double 5.0, double* %a
  ; VariableDeclarationNode
  %b = alloca double
  store double 3.0, double* %b
  ; PrintNode
  call i32 (i8*, ...) @printf(i8* getelementptr ([28 x i8], [28 x i8]* @.str0, i32 0, i32 0))
  ret i32 0
}

