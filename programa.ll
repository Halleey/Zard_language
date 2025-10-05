; === Função: factorial ===
define i32 @math_factorial(i32 %n) {
entry:
  %n_addr = alloca i32
  store i32 %n, i32* %n_addr
;;VAL:%n_addr;;TYPE:i32
  %t0 = load i32, i32* %n_addr
;;VAL:%t0;;TYPE:i32

  %t1 = add i32 0, 0
;;VAL:%t1;;TYPE:i32

  %t2 = icmp eq i32 %t0, %t1
;;VAL:%t2;;TYPE:i1

  br i1 %t2, label %then_0, label %endif_0
then_0:
  %t3 = add i32 0, 1
;;VAL:%t3;;TYPE:i32
  ret i32 %t3
  br label %endif_0
endif_0:
  %t4 = load i32, i32* %n_addr
;;VAL:%t4;;TYPE:i32

  %t5 = load i32, i32* %n_addr
;;VAL:%t5;;TYPE:i32

  %t6 = add i32 0, 1
;;VAL:%t6;;TYPE:i32

  %t7 = sub i32 %t5, %t6
;;VAL:%t7;;TYPE:i32
  %t8 = call i32 @math_factorial(i32 %t7)
;;VAL:%t8;;TYPE:i32

  %t9 = mul i32 %t4, %t8
;;VAL:%t9;;TYPE:i32
  ret i32 %t9
}


; === Função: addlist ===
define void @math_addlist(i8* %list) {
entry:
  %list_addr = alloca i8*
  store i8* %list, i8** %list_addr
;;VAL:%list_addr;;TYPE:i8*
  %t10 = load i8*, i8** %list_addr
;;VAL:%t10;;TYPE:i8*
  %t11 = bitcast i8* %t10 to %ArrayList*
  %t12 = bitcast [21 x i8]* @.str0 to i8*
;;VAL:%t12;;TYPE:i8*
  call void @arraylist_add_string(%ArrayList* %t11, i8* getelementptr ([21 x i8], [21 x i8]* @.str0, i32 0, i32 0))
;;VAL:%t11;;TYPE:%ArrayList*
  ret void
}


    declare i32 @printf(i8*, ...)
    declare i32 @getchar()
    declare void @printString(%String*)
    declare i8* @malloc(i64)
    declare i8* @arraylist_create(i64)
    declare void @clearList(%ArrayList*)
    declare void @freeList(%ArrayList*)

    @.strInt = private constant [4 x i8] c"%d\0A\00"
    @.strDouble = private constant [4 x i8] c"%f\0A\00"
    @.strStr = private constant [4 x i8] c"%s\0A\00"

    %String = type { i8*, i64 }
    %ArrayList = type opaque
    declare void @arraylist_add_int(%ArrayList*, i32)
    declare void @arraylist_print_int(%ArrayList*)
    declare void @arraylist_add_string(%ArrayList*, i8*)
    declare void @arraylist_addAll_string(%ArrayList*, i8**, i64)
    declare void @arraylist_print_string(%ArrayList*)

@.str0 = private constant [21 x i8] c"teste um hello world\00"

; === Função: factorial ===
define i32 @factorial(i32 %n) {
entry:
  %n_addr = alloca i32
  store i32 %n, i32* %n_addr
;;VAL:%n_addr;;TYPE:i32
  %t13 = load i32, i32* %n_addr
;;VAL:%t13;;TYPE:i32

  %t14 = add i32 0, 0
;;VAL:%t14;;TYPE:i32

  %t15 = icmp eq i32 %t13, %t14
;;VAL:%t15;;TYPE:i1

  br i1 %t15, label %then_1, label %endif_1
then_1:
  %t16 = add i32 0, 1
;;VAL:%t16;;TYPE:i32
  ret i32 %t16
  br label %endif_1
endif_1:
  %t17 = load i32, i32* %n_addr
;;VAL:%t17;;TYPE:i32

  %t18 = load i32, i32* %n_addr
;;VAL:%t18;;TYPE:i32

  %t19 = add i32 0, 1
;;VAL:%t19;;TYPE:i32

  %t20 = sub i32 %t18, %t19
;;VAL:%t20;;TYPE:i32
  %t21 = call i32 @factorial(i32 %t20)
;;VAL:%t21;;TYPE:i32

  %t22 = mul i32 %t17, %t21
;;VAL:%t22;;TYPE:i32
  ret i32 %t22
}

define i32 @main() {
  ; PrintNode
  %t23 = add i32 0, 6
;;VAL:%t23;;TYPE:i32
  %t24 = call i32 @factorial(i32 %t23)
  call i32 (i8*, ...) @printf(i8* getelementptr ([4 x i8], [4 x i8]* @.strInt, i32 0, i32 0), i32 %t24)
  ; VariableDeclarationNode
  %nomes = alloca i8*
;;VAL:%nomes;;TYPE:i8*
  %t25 = call i8* @arraylist_create(i64 4)
  %t26 = bitcast i8* %t25 to %ArrayList*
;;VAL:%t25;;TYPE:i8*
  store i8* %t25, i8** %nomes
  ; FunctionCallNode
  %t27 = load i8*, i8** %nomes
;;VAL:%t27;;TYPE:i8*
  call void @math_addlist(i8* %t27)
;;VAL:void;;TYPE:void
  ; PrintNode
  %t28 = load i8*, i8** %nomes
  %t29 = bitcast i8* %t28 to %ArrayList*
  call void @arraylist_print_string(%ArrayList* %t29)
  ; VariableDeclarationNode
  %numeros = alloca i8*
;;VAL:%numeros;;TYPE:i8*
  %t30 = call i8* @arraylist_create(i64 4)
  %t31 = bitcast i8* %t30 to %ArrayList*
  %t32 = add i32 0, 11
;;VAL:%t32;;TYPE:i32
  call void @arraylist_add_int(%ArrayList* %t31, i32 %t32)
  %t33 = add i32 0, 2
;;VAL:%t33;;TYPE:i32
  call void @arraylist_add_int(%ArrayList* %t31, i32 %t33)
  %t34 = add i32 0, 3
;;VAL:%t34;;TYPE:i32
  call void @arraylist_add_int(%ArrayList* %t31, i32 %t34)
;;VAL:%t30;;TYPE:i8*
  store i8* %t30, i8** %numeros
  ; PrintNode
  %t35 = add i32 0, 5
;;VAL:%t35;;TYPE:i32
  %t36 = call i32 @math_factorial(i32 %t35)
  call i32 (i8*, ...) @printf(i8* getelementptr ([4 x i8], [4 x i8]* @.strInt, i32 0, i32 0), i32 %t36)
  ; PrintNode
  %t37 = load i8*, i8** %numeros
  %t38 = bitcast i8* %t37 to %ArrayList*
  call void @arraylist_print_int(%ArrayList* %t38)
  ; === Free das listas alocadas ===
  %t39 = load i8*, i8** %numeros
  %t40 = bitcast i8* %t39 to %ArrayList*
  call void @freeList(%ArrayList* %t40)
  %t41 = load i8*, i8** %nomes
  %t42 = bitcast i8* %t41 to %ArrayList*
  call void @freeList(%ArrayList* %t42)
  ; === Wait for key press before exiting ===
  call i32 @getchar()
  ret i32 0
}
