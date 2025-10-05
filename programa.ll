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

@.str0 = private constant [12 x i8] c"hello world\00"

; === Função: factorial ===
define i32 @factorial(i32 %n) {
entry:
  %n_addr = alloca i32
  store i32 %n, i32* %n_addr
;;VAL:%n_addr;;TYPE:i32
  %t10 = load i32, i32* %n_addr
;;VAL:%t10;;TYPE:i32

  %t11 = add i32 0, 0
;;VAL:%t11;;TYPE:i32

  %t12 = icmp eq i32 %t10, %t11
;;VAL:%t12;;TYPE:i1

  br i1 %t12, label %then_1, label %endif_1
then_1:
  %t13 = add i32 0, 1
;;VAL:%t13;;TYPE:i32
  ret i32 %t13
  br label %endif_1
endif_1:
  %t14 = load i32, i32* %n_addr
;;VAL:%t14;;TYPE:i32

  %t15 = load i32, i32* %n_addr
;;VAL:%t15;;TYPE:i32

  %t16 = add i32 0, 1
;;VAL:%t16;;TYPE:i32

  %t17 = sub i32 %t15, %t16
;;VAL:%t17;;TYPE:i32
  %t18 = call i32 @factorial(i32 %t17)
;;VAL:%t18;;TYPE:i32

  %t19 = mul i32 %t14, %t18
;;VAL:%t19;;TYPE:i32
  ret i32 %t19
}

; === Função: hi ===
define %String* @hi() {
entry:
  %t20 = getelementptr inbounds [12 x i8], [12 x i8]* @.str0, i32 0, i32 0
  %t21 = alloca %String
  %t22 = getelementptr inbounds %String, %String* %t21, i32 0, i32 0
  store i8* %t20, i8** %t22
  %t23 = getelementptr inbounds %String, %String* %t21, i32 0, i32 1
  store i64 11, i64* %t23
  ret %String* %t21
}

define i32 @main() {
  ; PrintNode
  %t24 = add i32 0, 6
;;VAL:%t24;;TYPE:i32
  %t25 = call i32 @factorial(i32 %t24)
  call i32 (i8*, ...) @printf(i8* getelementptr ([4 x i8], [4 x i8]* @.strInt, i32 0, i32 0), i32 %t25)
  ; PrintNode
  %t26 = call %String* @hi()
  call void @printString(%String* %t26)
  ; VariableDeclarationNode
  %numeros = alloca i8*
;;VAL:%numeros;;TYPE:i8*
  %t27 = call i8* @arraylist_create(i64 4)
  %t28 = bitcast i8* %t27 to %ArrayList*
  %t29 = add i32 0, 11
;;VAL:%t29;;TYPE:i32
  call void @arraylist_add_int(%ArrayList* %t28, i32 %t29)
  %t30 = add i32 0, 2
;;VAL:%t30;;TYPE:i32
  call void @arraylist_add_int(%ArrayList* %t28, i32 %t30)
  %t31 = add i32 0, 3
;;VAL:%t31;;TYPE:i32
  call void @arraylist_add_int(%ArrayList* %t28, i32 %t31)
;;VAL:%t27;;TYPE:i8*
  store i8* %t27, i8** %numeros
  ; PrintNode
  %t32 = add i32 0, 5
;;VAL:%t32;;TYPE:i32
  %t33 = call i32 @math_factorial(i32 %t32)
  call i32 (i8*, ...) @printf(i8* getelementptr ([4 x i8], [4 x i8]* @.strInt, i32 0, i32 0), i32 %t33)
  ; PrintNode
  %t34 = load i8*, i8** %numeros
  %t35 = bitcast i8* %t34 to %ArrayList*
  call void @arraylist_print_int(%ArrayList* %t35)
  ; === Free das listas alocadas ===
  %t36 = load i8*, i8** %numeros
  %t37 = bitcast i8* %t36 to %ArrayList*
  call void @freeList(%ArrayList* %t37)
  ; === Wait for key press before exiting ===
  call i32 @getchar()
  ret i32 0
}
