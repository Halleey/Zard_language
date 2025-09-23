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
%DynValue = type opaque
declare i8* @arraylist_create(i64)
declare void @setItems(i8*, i8*)
declare void @printList(i8*)
declare void @removeItem(%ArrayList*, i64)
declare void @clearList(%ArrayList*)
declare void @freeList(%ArrayList*)
declare i32 @size(%ArrayList*)
declare %DynValue* @getItem(%ArrayList*, i32)
declare void @printDynValue(%DynValue*)

@.str0 = private constant [10 x i8] c"hallyson\0A\00"
@.str1 = private constant [24 x i8] c"saindo do loop interno\0A\00"
@.str2 = private constant [30 x i8] c"voltando para o loop externo\0A\00"

define i32 @main() {
  ; VariableDeclarationNode
  %a = alloca i32
  ; AssignmentNode
  store i32 4, i32* %a
  ; VariableDeclarationNode
  %b = alloca double
  store double 3.14, double* %b
  ; VariableDeclarationNode
  %nome = alloca i8*
  store i8* getelementptr ([10 x i8], [10 x i8]* @.str0, i32 0, i32 0), i8** %nome
  ; VariableDeclarationNode
  %isReal = alloca i1
  store i1 1, i1* %isReal
  ; PrintNode
  %t0 = load i32, i32* %a
  call i32 (i8*, ...) @printf(i8* getelementptr ([4 x i8], [4 x i8]* @.strInt, i32 0, i32 0), i32 %t0)
  ; PrintNode
  %t1 = load double, double* %b
  call i32 (i8*, ...) @printf(i8* getelementptr ([4 x i8], [4 x i8]* @.strDouble, i32 0, i32 0), double %t1)
  ; PrintNode
  %t2 = load i8*, i8** %nome
  call i32 (i8*, ...) @printf(i8* getelementptr ([4 x i8], [4 x i8]* @.strStr, i32 0, i32 0), i8* %t2)
  ; PrintNode
  %t3 = load i1, i1* %isReal
  %t4 = zext i1 %t3 to i32
  call i32 (i8*, ...) @printf(i8* getelementptr ([4 x i8], [4 x i8]* @.strInt, i32 0, i32 0), i32 %t4)
  ; VariableDeclarationNode
  %teste = alloca i8*
  %t5 = call i8* @arraylist_create(i64 4)
;;VAL:%t5;;TYPE:i8*
  store i8* %t5, i8** %teste
  ; ListAddNode
  %t6 = load i8*, i8** %teste
;;VAL:%t6;;TYPE:i8*
  %t7 = load i32, i32* %a
;;VAL:%t7;;TYPE:i32
  %t8 = call i8* @createInt(i32 %t7)
  call void @setItems(i8* %t6, i8* %t8)
;;VAL:%t8;;TYPE:i8*
  ; ListAddNode
  %t9 = load i8*, i8** %teste
;;VAL:%t9;;TYPE:i8*
  %t10 = add i32 0, 13
;;VAL:%t10;;TYPE:i32
  %t11 = call i8* @createInt(i32 %t10)
  call void @setItems(i8* %t9, i8* %t11)
;;VAL:%t11;;TYPE:i8*
  ; PrintNode
  %t12 = load i8*, i8** %teste
;;VAL:%t12;;TYPE:i8*
  %t13 = bitcast i8* %t12 to %ArrayList*
  %t14 = call i32 @size(%ArrayList* %t13)
  
  call i32 (i8*, ...) @printf(i8* getelementptr ([4 x i8], [4 x i8]* @.strInt, i32 0, i32 0), i32 %t14)
  ; PrintNode
  %t15 = load i8*, i8** %teste
  %t16 = bitcast i8* %t15 to %ArrayList*
  %t17 = add i32 0, 1
  %t18 = call %DynValue* @getItem(%ArrayList* %t16, i32 %t17)
;;VAL:%t18
;;TYPE:any
  call void @printDynValue(%DynValue* %t18)
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
  call i32 (i8*, ...) @printf(i8* getelementptr ([24 x i8], [24 x i8]* @.str1, i32 0, i32 0))
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
  call i32 (i8*, ...) @printf(i8* getelementptr ([30 x i8], [30 x i8]* @.str2, i32 0, i32 0))
  store i1 0, i1* %isReal
  br label %while_cond_0
while_end_2:
  call i32 @getchar()
  ret i32 0
}
