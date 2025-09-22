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

@.str0 = private constant [10 x i8] c"zardelas\0A\00"
@.str1 = private constant [10 x i8] c"hallyson\0A\00"
@.str2 = private constant [24 x i8] c"saindo do loop interno\0A\00"
@.str3 = private constant [30 x i8] c"voltando para o loop externo\0A\00"

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
  %t2 = call i8* @createBool(i1 1)
  call void @setItems(i8* %t0, i8* %t2)
;;VAL:%t0;;TYPE:i8*
  store i8* %t0, i8** %nomes
  ; ListAddNode
  %t3 = load i8*, i8** %nomes
;;VAL:%t3;;TYPE:i8*
  %t4 = bitcast [10 x i8]* @.str0 to i8*
;;VAL:%t4;;TYPE:i8*
  %t6 = call i8* @createString(i8* getelementptr ([10 x i8], [10 x i8]* @.str0, i32 0, i32 0))
  call void @setItems(i8* %t3, i8* %t6)
;;VAL:%t6;;TYPE:i8*
  ; AssignmentNode
  store i8* getelementptr ([10 x i8], [10 x i8]* @.str1, i32 0, i32 0), i8** %nome
  ; ListAddNode
  %t7 = load i8*, i8** %nomes
;;VAL:%t7;;TYPE:i8*
  %t8 = load i8*, i8** %nome
;;VAL:%t8;;TYPE:i8*
  %t9 = call i8* @createString(i8* %t8)
  call void @setItems(i8* %t7, i8* %t9)
;;VAL:%t9;;TYPE:i8*
  ; PrintNode
  %t10 = load i8*, i8** %nomes
  call void @printList(i8* %t10)
  ; AssignmentNode
  store i32 4, i32* %a
  ; AssignmentNode
  store double 3.14, double* %b
  ; PrintNode
  %t11 = load i32, i32* %a
  call i32 (i8*, ...) @printf(i8* getelementptr ([4 x i8], [4 x i8]* @.strInt, i32 0, i32 0), i32 %t11)
  ; PrintNode
  %t12 = load double, double* %b
  call i32 (i8*, ...) @printf(i8* getelementptr ([4 x i8], [4 x i8]* @.strDouble, i32 0, i32 0), double %t12)
  ; PrintNode
  %t13 = load i8*, i8** %nome
  call i32 (i8*, ...) @printf(i8* getelementptr ([4 x i8], [4 x i8]* @.strStr, i32 0, i32 0), i8* %t13)
  ; PrintNode
  %t14 = load i8*, i8** %nomes
  call void @printList(i8* %t14)
  ; PrintNode
  %t15 = load i1, i1* %isReal
  %t16 = zext i1 %t15 to i32
  call i32 (i8*, ...) @printf(i8* getelementptr ([4 x i8], [4 x i8]* @.strInt, i32 0, i32 0), i32 %t16)
  ; PrintNode
  %t17 = load i1, i1* %isFake
  %t18 = zext i1 %t17 to i32
  call i32 (i8*, ...) @printf(i8* getelementptr ([4 x i8], [4 x i8]* @.strInt, i32 0, i32 0), i32 %t18)
  ; WhileNode
  br label %while_cond_0
while_cond_0:
  %t19 = load i1, i1* %isReal
;;VAL:%t19;;TYPE:i1
  br i1 %t19, label %while_body_1, label %while_end_2
while_body_1:
  br label %while_cond_3
while_cond_3:
  %t20 = load i32, i32* %a
;;VAL:%t20;;TYPE:i32

  %t21 = add i32 0, 10
;;VAL:%t21;;TYPE:i32

  %t22 = icmp slt i32 %t20, %t21
;;VAL:%t22;;TYPE:i1
  br i1 %t22, label %while_body_4, label %while_end_5
while_body_4:
  %t23 = load i32, i32* %a
;;VAL:%t23;;TYPE:i32

  %t24 = add i32 0, 8
;;VAL:%t24;;TYPE:i32

  %t25 = icmp eq i32 %t23, %t24
;;VAL:%t25;;TYPE:i1

  br i1 %t25, label %then_0, label %endif_0
then_0:
  call i32 (i8*, ...) @printf(i8* getelementptr ([24 x i8], [24 x i8]* @.str2, i32 0, i32 0))
  br label %while_end_5
endif_0:
  %t26 = load i32, i32* %a
  call i32 (i8*, ...) @printf(i8* getelementptr ([4 x i8], [4 x i8]* @.strInt, i32 0, i32 0), i32 %t26)
  %t27 = load i32, i32* %a
;;VAL:%t27;;TYPE:i32

  %t28 = add i32 0, 1
;;VAL:%t28;;TYPE:i32

  %t29 = add i32 %t27, %t28
;;VAL:%t29;;TYPE:i32

  store i32 %t29, i32* %a
  br label %while_cond_3
while_end_5:
  %t30 = load i32, i32* %a
  call i32 (i8*, ...) @printf(i8* getelementptr ([4 x i8], [4 x i8]* @.strInt, i32 0, i32 0), i32 %t30)
  call i32 (i8*, ...) @printf(i8* getelementptr ([30 x i8], [30 x i8]* @.str3, i32 0, i32 0))
  store i1 0, i1* %isReal
  br label %while_cond_0
while_end_2:
  call i32 @getchar()
  ret i32 0
}
