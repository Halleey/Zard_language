declare i32 @printf(i8*, ...)
declare i32 @getchar()
@.strInt = private constant [4 x i8] c"%d\0A\00"
@.strDouble = private constant [4 x i8] c"%f\0A\00"
@.strStr = private constant [4 x i8] c"%s\0A\00"

; === Funções do runtime DynValue ===
declare i8* @createInt(i32)
declare i8* @createDouble(double)
declare i8* @createBool(i1)
declare i8* @createString(i8*)

; === Runtime de listas ===
declare i8* @arraylist_create(i64)
declare void @setItems(i8*, i8*)
declare void @printList(i8*)

@.str0 = private constant [10 x i8] c"hallyson\0A\00"
@.str1 = private constant [24 x i8] c"saindo do loop interno\0A\00"
@.str2 = private constant [30 x i8] c"voltando para o loop externo\0A\00"

define i32 @main() {
  ; VariableDeclarationNode
  %a = alloca i32
  ; VariableDeclarationNode
  %b = alloca double
  ; VariableDeclarationNode
  %nome = alloca i8*
  ; VariableDeclarationNode
  %isReal = alloca i1
  store i1 1, i1* %isReal
  ; VariableDeclarationNode
  %isFake = alloca i1
  store i1 0, i1* %isFake
  ; VariableDeclarationNode
  %nomes = alloca i8*
  %t0 = call i8* @arraylist_create(i64 4)
  %t1 = call i8* @createInt(i32 1)
  call void @setItems(i8* %t0, i8* %t1)
  %t2 = call i8* @createString(i8* getelementptr ([10 x i8], [10 x i8]* @.str0, i32 0, i32 0))
  call void @setItems(i8* %t0, i8* %t2)
  %t3 = call i8* @createBool(i1 1)
  call void @setItems(i8* %t0, i8* %t3)
;;VAL:%t0;;TYPE:i8*
  store i8* %t0, i8** %nomes
  ; AssignmentNode
  store i8* getelementptr ([10 x i8], [10 x i8]* @.str0, i32 0, i32 0), i8** %nome
  ; PrintNode
  %t4 = load i8*, i8** %nomes
  call void @printList(i8* %t4)
  ; AssignmentNode
  store i32 4, i32* %a
  ; AssignmentNode
  store double 3.14, double* %b
  ; PrintNode
  %t5 = load i32, i32* %a
  call i32 (i8*, ...) @printf(i8* getelementptr ([4 x i8], [4 x i8]* @.strInt, i32 0, i32 0), i32 %t5)
  ; PrintNode
  %t6 = load double, double* %b
  call i32 (i8*, ...) @printf(i8* getelementptr ([4 x i8], [4 x i8]* @.strDouble, i32 0, i32 0), double %t6)
  ; PrintNode
  %t7 = load i8*, i8** %nome
  call i32 (i8*, ...) @printf(i8* getelementptr ([4 x i8], [4 x i8]* @.strStr, i32 0, i32 0), i8* %t7)
  ; PrintNode
  %t8 = load i8*, i8** %nomes
  call void @printList(i8* %t8)
  ; PrintNode
  %t9 = load i1, i1* %isReal
  %t10 = zext i1 %t9 to i32
  call i32 (i8*, ...) @printf(i8* getelementptr ([4 x i8], [4 x i8]* @.strInt, i32 0, i32 0), i32 %t10)
  ; PrintNode
  %t11 = load i1, i1* %isFake
  %t12 = zext i1 %t11 to i32
  call i32 (i8*, ...) @printf(i8* getelementptr ([4 x i8], [4 x i8]* @.strInt, i32 0, i32 0), i32 %t12)
  ; WhileNode
  br label %while_cond_0
while_cond_0:
  %t13 = load i1, i1* %isReal
;;VAL:%t13;;TYPE:i1
  br i1 %t13, label %while_body_1, label %while_end_2
while_body_1:
  br label %while_cond_3
while_cond_3:
  %t14 = load i32, i32* %a
;;VAL:%t14;;TYPE:i32

  %t15 = add i32 0, 10
;;VAL:%t15;;TYPE:i32

  %t16 = icmp slt i32 %t14, %t15
;;VAL:%t16;;TYPE:i1
  br i1 %t16, label %while_body_4, label %while_end_5
while_body_4:
  %t17 = load i32, i32* %a
;;VAL:%t17;;TYPE:i32

  %t18 = add i32 0, 8
;;VAL:%t18;;TYPE:i32

  %t19 = icmp eq i32 %t17, %t18
;;VAL:%t19;;TYPE:i1

  br i1 %t19, label %then_0, label %endif_0
then_0:
  call i32 (i8*, ...) @printf(i8* getelementptr ([24 x i8], [24 x i8]* @.str1, i32 0, i32 0))
  br label %while_end_5
endif_0:
  %t20 = load i32, i32* %a
  call i32 (i8*, ...) @printf(i8* getelementptr ([4 x i8], [4 x i8]* @.strInt, i32 0, i32 0), i32 %t20)
  %t21 = load i32, i32* %a
;;VAL:%t21;;TYPE:i32

  %t22 = add i32 0, 1
;;VAL:%t22;;TYPE:i32

  %t23 = add i32 %t21, %t22
;;VAL:%t23;;TYPE:i32

  store i32 %t23, i32* %a
  br label %while_cond_3
while_end_5:
  %t24 = load i32, i32* %a
  call i32 (i8*, ...) @printf(i8* getelementptr ([4 x i8], [4 x i8]* @.strInt, i32 0, i32 0), i32 %t24)
  call i32 (i8*, ...) @printf(i8* getelementptr ([30 x i8], [30 x i8]* @.str2, i32 0, i32 0))
  store i1 0, i1* %isReal
  br label %while_cond_0
while_end_2:
  call i32 @getchar()
  ret i32 0
}
