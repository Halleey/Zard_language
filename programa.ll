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
%ArrayList = type opaque
declare i8* @arraylist_create(i64)
declare void @setItems(i8*, i8*)
declare void @printList(i8*)
declare void @removeItem(%ArrayList*, i64)

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
  %t13 = load i8*, i8** %nomes
  call void @printList(i8* %t13)
  ; PrintNode
  %t14 = load i1, i1* %isReal
  %t15 = zext i1 %t14 to i32
  call i32 (i8*, ...) @printf(i8* getelementptr ([4 x i8], [4 x i8]* @.strInt, i32 0, i32 0), i32 %t15)
  ; PrintNode
  %t16 = load i1, i1* %isFake
  %t17 = zext i1 %t16 to i32
  call i32 (i8*, ...) @printf(i8* getelementptr ([4 x i8], [4 x i8]* @.strInt, i32 0, i32 0), i32 %t17)
  ; ListRemoveNode
  %t18 = load i8*, i8** %nomes
;;VAL:%t18;;TYPE:i8*
  %t19 = add i32 0, 0
;;VAL:%t19;;TYPE:i32
  %t20 = sext i32 %t19 to i64
  %t21 = bitcast i8* %t18 to %ArrayList*
  call void @removeItem(%ArrayList* %t21, i64 %t20)
  ; ListRemoveNode
  %t22 = load i8*, i8** %nomes
;;VAL:%t22;;TYPE:i8*
  %t23 = add i32 0, 1
;;VAL:%t23;;TYPE:i32
  %t24 = sext i32 %t23 to i64
  %t25 = bitcast i8* %t22 to %ArrayList*
  call void @removeItem(%ArrayList* %t25, i64 %t24)
  ; PrintNode
  %t26 = load i8*, i8** %nomes
  call void @printList(i8* %t26)
  ; WhileNode
  br label %while_cond_0
while_cond_0:
  %t27 = load i1, i1* %isReal
;;VAL:%t27;;TYPE:i1
  br i1 %t27, label %while_body_1, label %while_end_2
while_body_1:
  br label %while_cond_3
while_cond_3:
  %t28 = load i32, i32* %a
;;VAL:%t28;;TYPE:i32

  %t29 = add i32 0, 10
;;VAL:%t29;;TYPE:i32

  %t30 = icmp slt i32 %t28, %t29
;;VAL:%t30;;TYPE:i1
  br i1 %t30, label %while_body_4, label %while_end_5
while_body_4:
  %t31 = load i32, i32* %a
;;VAL:%t31;;TYPE:i32

  %t32 = add i32 0, 8
;;VAL:%t32;;TYPE:i32

  %t33 = icmp eq i32 %t31, %t32
;;VAL:%t33;;TYPE:i1

  br i1 %t33, label %then_0, label %endif_0
then_0:
  call i32 (i8*, ...) @printf(i8* getelementptr ([24 x i8], [24 x i8]* @.str2, i32 0, i32 0))
  br label %while_end_5
endif_0:
  %t34 = load i32, i32* %a
  call i32 (i8*, ...) @printf(i8* getelementptr ([4 x i8], [4 x i8]* @.strInt, i32 0, i32 0), i32 %t34)
  %t35 = load i32, i32* %a
;;VAL:%t35;;TYPE:i32

  %t36 = add i32 0, 1
;;VAL:%t36;;TYPE:i32

  %t37 = add i32 %t35, %t36
;;VAL:%t37;;TYPE:i32

  store i32 %t37, i32* %a
  br label %while_cond_3
while_end_5:
  %t38 = load i32, i32* %a
  call i32 (i8*, ...) @printf(i8* getelementptr ([4 x i8], [4 x i8]* @.strInt, i32 0, i32 0), i32 %t38)
  call i32 (i8*, ...) @printf(i8* getelementptr ([30 x i8], [30 x i8]* @.str3, i32 0, i32 0))
  store i1 0, i1* %isReal
  br label %while_cond_0
while_end_2:
  call i32 @getchar()
  ret i32 0
}
