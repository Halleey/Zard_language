    declare i32 @printf(i8*, ...)
    declare i32 @getchar()
    declare i8* @malloc(i64) ; necessário para alocação de arrays

    @.strInt = private constant [4 x i8] c"%d\0A\00"
    @.strDouble = private constant [4 x i8] c"%f\0A\00"
    @.strStr = private constant [4 x i8] c"%s\0A\00"

    %String = type { i8*, i64 }

@.str0 = private constant [36 x i8] c"Erro: factorial de numero negativo!\00"
@.str1 = private constant [9 x i8] c"hallyson\00"
@.str2 = private constant [6 x i8] c"maior\00"
@.str3 = private constant [5 x i8] c"meio\00"
@.str4 = private constant [22 x i8] c"nenhum dos anteriores\00"

; === Função: hello ===
define i32 @hello(i32 %a, i32 %b) {
entry:
  %a_addr = alloca i32
  store i32 %a, i32* %a_addr
;;VAL:%a_addr;;TYPE:i32
  %b_addr = alloca i32
  store i32 %b, i32* %b_addr
;;VAL:%b_addr;;TYPE:i32
  %t0 = load i32, i32* %a_addr
;;VAL:%t0;;TYPE:i32

  %t1 = load i32, i32* %b_addr
;;VAL:%t1;;TYPE:i32

  %t2 = add i32 %t0, %t1
;;VAL:%t2;;TYPE:i32
  ret i32 %t2
}

; === Função: factorial ===
define i32 @factorial(i32 %n) {
entry:
  %n_addr = alloca i32
  store i32 %n, i32* %n_addr
;;VAL:%n_addr;;TYPE:i32
  %t3 = load i32, i32* %n_addr
;;VAL:%t3;;TYPE:i32

  %t4 = add i32 0, 0
;;VAL:%t4;;TYPE:i32

  %t5 = icmp slt i32 %t3, %t4
;;VAL:%t5;;TYPE:i1

  br i1 %t5, label %then_0, label %endif_0
then_0:
  %t6 = getelementptr inbounds [36 x i8], [36 x i8]* @.str0, i32 0, i32 0
  call i32 (i8*, ...) @printf(i8* getelementptr ([4 x i8], [4 x i8]* @.strStr, i32 0, i32 0), i8* %t6)
  %t7 = add i32 0, 0
;;VAL:%t7;;TYPE:i32
  ret i32 %t7
  br label %endif_0
endif_0:
  %t8 = load i32, i32* %n_addr
;;VAL:%t8;;TYPE:i32

  %t9 = add i32 0, 0
;;VAL:%t9;;TYPE:i32

  %t10 = icmp eq i32 %t8, %t9
;;VAL:%t10;;TYPE:i1

  br i1 %t10, label %then_1, label %endif_1
then_1:
  %t11 = add i32 0, 1
;;VAL:%t11;;TYPE:i32
  ret i32 %t11
  br label %endif_1
endif_1:
  %t12 = load i32, i32* %n_addr
;;VAL:%t12;;TYPE:i32

  %t13 = load i32, i32* %n_addr
;;VAL:%t13;;TYPE:i32

  %t14 = add i32 0, 1
;;VAL:%t14;;TYPE:i32

  %t15 = sub i32 %t13, %t14
;;VAL:%t15;;TYPE:i32
  %t16 = call i32 @factorial(i32 %t15)
;;VAL:%t16;;TYPE:i32

  %t17 = mul i32 %t12, %t16
;;VAL:%t17;;TYPE:i32
  ret i32 %t17
}

define i32 @main() {
  ; PrintNode
  %t18 = add i32 0, 3
;;VAL:%t18;;TYPE:i32
  %t19 = add i32 0, 4
;;VAL:%t19;;TYPE:i32
  %t20 = call i32 @hello(i32 %t18, i32 %t19)
  call i32 (i8*, ...) @printf(i8* getelementptr ([4 x i8], [4 x i8]* @.strInt, i32 0, i32 0), i32 %t20)
  ; PrintNode
  %t21 = add i32 0, 5
;;VAL:%t21;;TYPE:i32
  %t22 = call i32 @factorial(i32 %t21)
  call i32 (i8*, ...) @printf(i8* getelementptr ([4 x i8], [4 x i8]* @.strInt, i32 0, i32 0), i32 %t22)
  ; VariableDeclarationNode
  %nome = alloca %String
;;VAL:%nome;;TYPE:%String
  %t23 = getelementptr inbounds [9 x i8], [9 x i8]* @.str1, i32 0, i32 0
;;VAL:%t23;;TYPE:i8*
  %t24 = getelementptr inbounds %String, %String* %nome, i32 0, i32 0
  store i8* %t23, i8** %t24
  %t25 = getelementptr inbounds %String, %String* %nome, i32 0, i32 1
  store i64 8, i64* %t25
  ; VariableDeclarationNode
  %a = alloca i32
;;VAL:%a;;TYPE:i32
  %t26 = add i32 0, 3
;;VAL:%t26;;TYPE:i32
  store i32 %t26, i32* %a
  ; VariableDeclarationNode
  %b = alloca double
;;VAL:%b;;TYPE:double
  %t27 = fadd double 0.0, 3.4
;;VAL:%t27;;TYPE:double
  store double %t27, double* %b
  ; VariableDeclarationNode
  %real = alloca i1
;;VAL:%real;;TYPE:i1
  %t28 = add i1 0, 0
;;VAL:%t28;;TYPE:i1
  store i1 %t28, i1* %real
  ; PrintNode
  %t29 = load i32, i32* %a
  call i32 (i8*, ...) @printf(i8* getelementptr ([4 x i8], [4 x i8]* @.strInt, i32 0, i32 0), i32 %t29)
  ; PrintNode
  %t30 = load double, double* %b
  call i32 (i8*, ...) @printf(i8* getelementptr ([4 x i8], [4 x i8]* @.strDouble, i32 0, i32 0), double %t30)
  ; PrintNode
  %t31 = load i1, i1* %real
  %t32 = zext i1 %t31 to i32
  call i32 (i8*, ...) @printf(i8* getelementptr ([4 x i8], [4 x i8]* @.strInt, i32 0, i32 0), i32 %t32)
  ; PrintNode
  %t33 = getelementptr inbounds %String, %String* %nome, i32 0, i32 0
  %t34 = load i8*, i8** %t33
  call i32 (i8*, ...) @printf(i8* getelementptr ([4 x i8], [4 x i8]* @.strStr, i32 0, i32 0), i8* %t34)
  ; IfNode
  %t35 = load i32, i32* %a
;;VAL:%t35;;TYPE:i32

  %t36 = add i32 0, 10
;;VAL:%t36;;TYPE:i32

  %t37 = icmp sgt i32 %t35, %t36
;;VAL:%t37;;TYPE:i1

  br i1 %t37, label %then_2, label %else_2
then_2:
  %t38 = getelementptr inbounds [6 x i8], [6 x i8]* @.str2, i32 0, i32 0
  call i32 (i8*, ...) @printf(i8* getelementptr ([4 x i8], [4 x i8]* @.strStr, i32 0, i32 0), i8* %t38)
  br label %endif_2
else_2:
  %t39 = load i32, i32* %a
;;VAL:%t39;;TYPE:i32

  %t40 = add i32 0, 1
;;VAL:%t40;;TYPE:i32

  %t41 = icmp slt i32 %t39, %t40
;;VAL:%t41;;TYPE:i1

  br i1 %t41, label %then_3, label %else_3
then_3:
  %t42 = getelementptr inbounds [5 x i8], [5 x i8]* @.str3, i32 0, i32 0
  call i32 (i8*, ...) @printf(i8* getelementptr ([4 x i8], [4 x i8]* @.strStr, i32 0, i32 0), i8* %t42)
  br label %endif_3
else_3:
  %t43 = getelementptr inbounds [22 x i8], [22 x i8]* @.str4, i32 0, i32 0
  call i32 (i8*, ...) @printf(i8* getelementptr ([4 x i8], [4 x i8]* @.strStr, i32 0, i32 0), i8* %t43)
  br label %endif_3
endif_3:
  br label %endif_2
endif_2:
  call i32 @getchar()
  ret i32 0
}
