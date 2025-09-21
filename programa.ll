; LLVM IR mínimo do main
declare i32 @printf(i8*, ...)
declare i32 @getchar()
@.strInt = private constant [4 x i8] c"%d\0A\00"
@.strDouble = private constant [4 x i8] c"%f\0A\00"
@.strStr = private constant [4 x i8] c"%s\0A\00"

; === Runtime de listas ===
declare i8* @arraylist_create(i64)
declare void @setItems(i8*, i8*)

@.str0 = private constant [10 x i8] c"hallyson\0A\00"

define i32 @main() {
  ; VariableDeclarationNode
  %a = alloca i32
  ; VariableDeclarationNode
  %b = alloca double
  ; VariableDeclarationNode
  %nome = alloca i8*
  ; VariableDeclarationNode
  %nomes = alloca i8*
  %t0 = call i8* @arraylist_create(i64 4)
  %t1 = alloca i32
  store i32 1, i32* %t1
  %t2 = bitcast i32* %t1 to i8*
  call void @setItems(i8* %t0, i8* %t2)
  %t3 = bitcast [10 x i8]* @.str0 to i8*
  call void @setItems(i8* %t0, i8* %t3)
  %t4 = alloca i1
  store i1 1, i1* %t4
  %t5 = bitcast i1* %t4 to i8*
  call void @setItems(i8* %t0, i8* %t5)
;;VAL:%t0;;TYPE:i8*
  store i8* %t0, i8** %nomes
  ; AssignmentNode
  store i8* getelementptr ([10 x i8], [10 x i8]* @.str0, i32 0, i32 0), i8** %nome
  ; AssignmentNode
  store i32 4, i32* %a
  ; AssignmentNode
  store double 3.14, double* %b
  ; PrintNode
  %t6 = load i32, i32* %a
  call i32 (i8*, ...) @printf(i8* getelementptr ([4 x i8], [4 x i8]* @.strInt, i32 0, i32 0), i32 %t6)
  ; PrintNode
  %t7 = load double, double* %b
  call i32 (i8*, ...) @printf(i8* getelementptr ([4 x i8], [4 x i8]* @.strDouble, i32 0, i32 0), double %t7)
  ; PrintNode
  %tStr20492440166800 = load i8*, i8** %nome
  call i32 (i8*, ...) @printf(i8* getelementptr ([4 x i8], [4 x i8]* @.strStr, i32 0, i32 0), i8* %tStr20492440166800)
  call i32 @getchar()
  ret i32 0
}
