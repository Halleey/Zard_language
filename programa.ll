    declare i32 @printf(i8*, ...)
    declare i32 @getchar()
    declare void @printString(%String*)
    declare i8* @malloc(i64)
    declare void @setString(%String*, i8*)

    @.strInt = private constant [4 x i8] c"%d\0A\00"
    @.strDouble = private constant [4 x i8] c"%f\0A\00"
    @.strStr = private constant [4 x i8] c"%s\0A\00"

    declare i8* @arraylist_create(i64)
    declare void @clearList(%ArrayList*)
    declare void @freeList(%ArrayList*)

    %String = type { i8*, i64 }
    %ArrayList = type opaque
    %struct.ArrayListDouble = type { double*, i64, i64 }
    declare %struct.ArrayListDouble* @arraylist_create_double(i64)
    declare void @arraylist_add_double(%struct.ArrayListDouble*, double)
    declare void @arraylist_addAll_double(%struct.ArrayListDouble*, double*, i64)
    declare void @arraylist_print_double(%struct.ArrayListDouble*)
    declare void @arraylist_clear_double(%struct.ArrayListDouble*)
    declare void @arraylist_free_double(%struct.ArrayListDouble*)

@.str0 = private constant [4 x i8] c"i :\00"
@.str1 = private constant [4 x i8] c"j :\00"
@.str2 = private constant [26 x i8] c"Break do loop interno (j)\00"
@.str3 = private constant [30 x i8] c"Fim do loop interno para i = \00"
@.str4 = private constant [26 x i8] c"Break do loop externo (i)\00"
@.str5 = private constant [20 x i8] c"Fim do loop externo\00"

define i32 @main() {
  ; VariableDeclarationNode
  %i = alloca i32
;;VAL:%i;;TYPE:i32
  %t0 = add i32 0, 0
;;VAL:%t0;;TYPE:i32
  store i32 %t0, i32* %i
  ; WhileNode
  br label %while_cond_0
while_cond_0:
  %t1 = load i32, i32* %i
;;VAL:%t1;;TYPE:i32

  %t2 = add i32 0, 5
;;VAL:%t2;;TYPE:i32

  %t3 = icmp slt i32 %t1, %t2
;;VAL:%t3;;TYPE:i1
  br i1 %t3, label %while_body_1, label %while_end_2
while_body_1:
  %j = alloca i32
;;VAL:%j;;TYPE:i32
  %t4 = add i32 0, 0
;;VAL:%t4;;TYPE:i32
  store i32 %t4, i32* %j
  br label %while_cond_3
while_cond_3:
  %t5 = load i32, i32* %j
;;VAL:%t5;;TYPE:i32

  %t6 = add i32 0, 5
;;VAL:%t6;;TYPE:i32

  %t7 = icmp slt i32 %t5, %t6
;;VAL:%t7;;TYPE:i1
  br i1 %t7, label %while_body_4, label %while_end_5
while_body_4:
  %t8 = getelementptr inbounds [4 x i8], [4 x i8]* @.str0, i32 0, i32 0
  call i32 (i8*, ...) @printf(i8* getelementptr ([4 x i8], [4 x i8]* @.strStr, i32 0, i32 0), i8* %t8)
  %t9 = load i32, i32* %i
  call i32 (i8*, ...) @printf(i8* getelementptr ([4 x i8], [4 x i8]* @.strInt, i32 0, i32 0), i32 %t9)
  %t10 = getelementptr inbounds [4 x i8], [4 x i8]* @.str1, i32 0, i32 0
  call i32 (i8*, ...) @printf(i8* getelementptr ([4 x i8], [4 x i8]* @.strStr, i32 0, i32 0), i8* %t10)
  %t11 = load i32, i32* %j
  call i32 (i8*, ...) @printf(i8* getelementptr ([4 x i8], [4 x i8]* @.strInt, i32 0, i32 0), i32 %t11)
  %t12 = load i32, i32* %j
;;VAL:%t12;;TYPE:i32

  %t13 = add i32 0, 2
;;VAL:%t13;;TYPE:i32

  %t14 = icmp eq i32 %t12, %t13
;;VAL:%t14;;TYPE:i1

  br i1 %t14, label %then_0, label %endif_0
then_0:
  %t15 = getelementptr inbounds [26 x i8], [26 x i8]* @.str2, i32 0, i32 0
  call i32 (i8*, ...) @printf(i8* getelementptr ([4 x i8], [4 x i8]* @.strStr, i32 0, i32 0), i8* %t15)
  br label %while_end_5
endif_0:
  %t16 = load i32, i32* %j
;;VAL:%t16;;TYPE:i32

  %t17 = add i32 0, 1
;;VAL:%t17;;TYPE:i32

  %t18 = add i32 %t16, %t17
;;VAL:%t18;;TYPE:i32
  store i32 %t18, i32* %j
  br label %while_cond_3
while_end_5:
  %t19 = getelementptr inbounds [30 x i8], [30 x i8]* @.str3, i32 0, i32 0
  call i32 (i8*, ...) @printf(i8* getelementptr ([4 x i8], [4 x i8]* @.strStr, i32 0, i32 0), i8* %t19)
  %t20 = load i32, i32* %i
  call i32 (i8*, ...) @printf(i8* getelementptr ([4 x i8], [4 x i8]* @.strInt, i32 0, i32 0), i32 %t20)
  %t21 = load i32, i32* %i
;;VAL:%t21;;TYPE:i32

  %t22 = add i32 0, 3
;;VAL:%t22;;TYPE:i32

  %t23 = icmp eq i32 %t21, %t22
;;VAL:%t23;;TYPE:i1

  br i1 %t23, label %then_1, label %endif_1
then_1:
  %t24 = getelementptr inbounds [26 x i8], [26 x i8]* @.str4, i32 0, i32 0
  call i32 (i8*, ...) @printf(i8* getelementptr ([4 x i8], [4 x i8]* @.strStr, i32 0, i32 0), i8* %t24)
  br label %while_end_2
endif_1:
  %t25 = load i32, i32* %i
;;VAL:%t25;;TYPE:i32

  %t26 = add i32 0, 1
;;VAL:%t26;;TYPE:i32

  %t27 = add i32 %t25, %t26
;;VAL:%t27;;TYPE:i32
  store i32 %t27, i32* %i
  br label %while_cond_0
while_end_2:
  ; VariableDeclarationNode
  %list = alloca %struct.ArrayListDouble*
;;VAL:%list;;TYPE:%struct.ArrayListDouble*
  %t28 = call %struct.ArrayListDouble* @arraylist_create_double(i64 4)
  %t29 = alloca double, i64 1
  %t30 = fadd double 0.0, 3.3
;;VAL:%t30;;TYPE:double
  %t31 = getelementptr inbounds double, double* %t29, i64 0
  store double %t30, double* %t31
  call void @arraylist_addAll_double(%struct.ArrayListDouble* %t28, double* %t29, i64 1)
;;VAL:%t28;;TYPE:%struct.ArrayListDouble*
  store %struct.ArrayListDouble* %t28, %struct.ArrayListDouble** %list
  ; ListAddNode
  %t34 = load %struct.ArrayListDouble*, %struct.ArrayListDouble** %list
;;VAL:%t34;;TYPE:%struct.ArrayListDouble*
  %t35 = fadd double 0.0, 4.3
;;VAL:%t35;;TYPE:double
  call void @arraylist_add_double(%struct.ArrayListDouble* %t34, double %t35)
;;VAL:%t34;;TYPE:%struct.ArrayListDouble*
  ; PrintNode
  %t36 = load %struct.ArrayListDouble*, %struct.ArrayListDouble** %list
  call void @arraylist_print_double(%struct.ArrayListDouble* %t36)
  ; PrintNode
  %t37 = getelementptr inbounds [20 x i8], [20 x i8]* @.str5, i32 0, i32 0
  call i32 (i8*, ...) @printf(i8* getelementptr ([4 x i8], [4 x i8]* @.strStr, i32 0, i32 0), i8* %t37)
  ; === Free das listas alocadas ===
  %t38 = load %struct.ArrayListDouble*, %struct.ArrayListDouble** %list
  call void @arraylist_free_double(%struct.ArrayListDouble* %t38)
  call i32 @getchar()
  ret i32 0
}
