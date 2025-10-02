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
@.str5 = private constant [6 x i8] c"hello\00"

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

; === Função: fac ===
define i32 @fac(i32 %n) {
entry:
  %n_addr = alloca i32
  store i32 %n, i32* %n_addr
;;VAL:%n_addr;;TYPE:i32
  %t18 = load i32, i32* %n_addr
;;VAL:%t18;;TYPE:i32

  %t19 = add i32 0, 0
;;VAL:%t19;;TYPE:i32

  %t20 = icmp slt i32 %t18, %t19
;;VAL:%t20;;TYPE:i1

  br i1 %t20, label %then_2, label %endif_2
then_2:
  %t21 = getelementptr inbounds [36 x i8], [36 x i8]* @.str0, i32 0, i32 0
  call i32 (i8*, ...) @printf(i8* getelementptr ([4 x i8], [4 x i8]* @.strStr, i32 0, i32 0), i8* %t21)
  %t22 = add i32 0, 0
;;VAL:%t22;;TYPE:i32
  ret i32 %t22
  br label %endif_2
endif_2:
  %result = alloca i32
;;VAL:%result;;TYPE:i32
  %t23 = add i32 0, 1
;;VAL:%t23;;TYPE:i32
  store i32 %t23, i32* %result
  %i = alloca i32
;;VAL:%i;;TYPE:i32
  %t24 = add i32 0, 1
;;VAL:%t24;;TYPE:i32
  store i32 %t24, i32* %i
  br label %while_cond_0
while_cond_0:
  %t25 = load i32, i32* %i
;;VAL:%t25;;TYPE:i32

  %t26 = load i32, i32* %n_addr
;;VAL:%t26;;TYPE:i32

  %t27 = icmp sle i32 %t25, %t26
;;VAL:%t27;;TYPE:i1
  br i1 %t27, label %while_body_1, label %while_end_2
while_body_1:
  %t28 = load i32, i32* %result
;;VAL:%t28;;TYPE:i32

  %t29 = load i32, i32* %i
;;VAL:%t29;;TYPE:i32

  %t30 = mul i32 %t28, %t29
;;VAL:%t30;;TYPE:i32
  store i32 %t30, i32* %result
  %t31 = load i32, i32* %i
;;VAL:%t31;;TYPE:i32

  %t32 = add i32 0, 1
;;VAL:%t32;;TYPE:i32

  %t33 = add i32 %t31, %t32
;;VAL:%t33;;TYPE:i32
  store i32 %t33, i32* %i
  br label %while_cond_0
while_end_2:
  %t34 = load i32, i32* %result
;;VAL:%t34;;TYPE:i32
  ret i32 %t34
}

; === Função: hi ===
define %String* @hi() {
entry:
  %t35 = getelementptr inbounds [6 x i8], [6 x i8]* @.str5, i32 0, i32 0
  %t36 = alloca %String
  %t37 = getelementptr inbounds %String, %String* %t36, i32 0, i32 0
  store i8* %t35, i8** %t37
  %t38 = getelementptr inbounds %String, %String* %t36, i32 0, i32 1
  store i64 5, i64* %t38
  ret %String* %t36
}

define i32 @main() {
  ; PrintNode
  %t39 = add i32 0, 3
;;VAL:%t39;;TYPE:i32
  %t40 = add i32 0, 4
;;VAL:%t40;;TYPE:i32
  %t41 = call i32 @hello(i32 %t39, i32 %t40)
  call i32 (i8*, ...) @printf(i8* getelementptr ([4 x i8], [4 x i8]* @.strInt, i32 0, i32 0), i32 %t41)
  ; PrintNode
  %t42 = add i32 0, 5
;;VAL:%t42;;TYPE:i32
  %t43 = call i32 @fac(i32 %t42)
  call i32 (i8*, ...) @printf(i8* getelementptr ([4 x i8], [4 x i8]* @.strInt, i32 0, i32 0), i32 %t43)
  ; PrintNode
  %t44 = add i32 0, 5
;;VAL:%t44;;TYPE:i32
  %t45 = call i32 @factorial(i32 %t44)
  call i32 (i8*, ...) @printf(i8* getelementptr ([4 x i8], [4 x i8]* @.strInt, i32 0, i32 0), i32 %t45)
  ; VariableDeclarationNode
  %nome = alloca %String
;;VAL:%nome;;TYPE:%String*
  %t46 = getelementptr inbounds [9 x i8], [9 x i8]* @.str1, i32 0, i32 0
;;VAL:%t46;;TYPE:i8*
  %t47 = getelementptr inbounds %String, %String* %nome, i32 0, i32 0
  store i8* %t46, i8** %t47
  %t48 = getelementptr inbounds %String, %String* %nome, i32 0, i32 1
  store i64 8, i64* %t48
  ; VariableDeclarationNode
  %a = alloca i32
;;VAL:%a;;TYPE:i32
  %t49 = add i32 0, 3
;;VAL:%t49;;TYPE:i32
  store i32 %t49, i32* %a
  ; VariableDeclarationNode
  %b = alloca double
;;VAL:%b;;TYPE:double
  %t50 = fadd double 0.0, 3.4
;;VAL:%t50;;TYPE:double
  store double %t50, double* %b
  ; VariableDeclarationNode
  %real = alloca i1
;;VAL:%real;;TYPE:i1
  %t51 = add i1 0, 0
;;VAL:%t51;;TYPE:i1
  store i1 %t51, i1* %real
  ; PrintNode
  %t52 = load i32, i32* %a
  call i32 (i8*, ...) @printf(i8* getelementptr ([4 x i8], [4 x i8]* @.strInt, i32 0, i32 0), i32 %t52)
  ; PrintNode
  %t53 = load double, double* %b
  call i32 (i8*, ...) @printf(i8* getelementptr ([4 x i8], [4 x i8]* @.strDouble, i32 0, i32 0), double %t53)
  ; PrintNode
  %t54 = load i1, i1* %real
  %t55 = zext i1 %t54 to i32
  call i32 (i8*, ...) @printf(i8* getelementptr ([4 x i8], [4 x i8]* @.strInt, i32 0, i32 0), i32 %t55)
  ; PrintNode
  %t56 = getelementptr inbounds %String, %String* %nome, i32 0, i32 0
  %t57 = load i8*, i8** %t56
  call i32 (i8*, ...) @printf(i8* getelementptr ([4 x i8], [4 x i8]* @.strStr, i32 0, i32 0), i8* %t57)
  ; IfNode
  %t58 = load i32, i32* %a
;;VAL:%t58;;TYPE:i32

  %t59 = add i32 0, 10
;;VAL:%t59;;TYPE:i32

  %t60 = icmp sgt i32 %t58, %t59
;;VAL:%t60;;TYPE:i1

  br i1 %t60, label %then_3, label %else_3
then_3:
  %t61 = getelementptr inbounds [6 x i8], [6 x i8]* @.str2, i32 0, i32 0
  call i32 (i8*, ...) @printf(i8* getelementptr ([4 x i8], [4 x i8]* @.strStr, i32 0, i32 0), i8* %t61)
  br label %endif_3
else_3:
  %t62 = load i32, i32* %a
;;VAL:%t62;;TYPE:i32

  %t63 = add i32 0, 1
;;VAL:%t63;;TYPE:i32

  %t64 = icmp slt i32 %t62, %t63
;;VAL:%t64;;TYPE:i1

  br i1 %t64, label %then_4, label %else_4
then_4:
  %t65 = getelementptr inbounds [5 x i8], [5 x i8]* @.str3, i32 0, i32 0
  call i32 (i8*, ...) @printf(i8* getelementptr ([4 x i8], [4 x i8]* @.strStr, i32 0, i32 0), i8* %t65)
  br label %endif_4
else_4:
  %t66 = getelementptr inbounds [22 x i8], [22 x i8]* @.str4, i32 0, i32 0
  call i32 (i8*, ...) @printf(i8* getelementptr ([4 x i8], [4 x i8]* @.strStr, i32 0, i32 0), i8* %t66)
  br label %endif_4
endif_4:
  br label %endif_3
endif_3:
  ; PrintNode
  %t67 = call %String* @hi()
  %t68 = getelementptr inbounds %String, %String* %t67, i32 0, i32 0
  %t69 = load i8*, i8** %t68
  call i32 (i8*, ...) @printf(i8* getelementptr ([4 x i8], [4 x i8]* @.strStr, i32 0, i32 0), i8* %t69)
  call i32 @getchar()
  ret i32 0
}
