; === Função: sum ===
define double @math_sum(double %a, double %b) {
entry:
  %a_addr = alloca double
  store double %a, double* %a_addr
;;VAL:%a_addr;;TYPE:double
  %b_addr = alloca double
  store double %b, double* %b_addr
;;VAL:%b_addr;;TYPE:double
  %t0 = load double, double* %a_addr
;;VAL:%t0;;TYPE:double

  %t1 = load double, double* %b_addr
;;VAL:%t1;;TYPE:double

  %t2 = fadd double %t0, %t1
;;VAL:%t2;;TYPE:double
  ret double %t2
}


; === Função: sub ===
define double @math_sub(double %a, double %b) {
entry:
  %a_addr = alloca double
  store double %a, double* %a_addr
;;VAL:%a_addr;;TYPE:double
  %b_addr = alloca double
  store double %b, double* %b_addr
;;VAL:%b_addr;;TYPE:double
  %t3 = load double, double* %a_addr
;;VAL:%t3;;TYPE:double

  %t4 = load double, double* %b_addr
;;VAL:%t4;;TYPE:double

  %t5 = fsub double %t3, %t4
;;VAL:%t5;;TYPE:double
  ret double %t5
}


; === Função: mul ===
define double @math_mul(double %a, double %b) {
entry:
  %a_addr = alloca double
  store double %a, double* %a_addr
;;VAL:%a_addr;;TYPE:double
  %b_addr = alloca double
  store double %b, double* %b_addr
;;VAL:%b_addr;;TYPE:double
  %t6 = load double, double* %a_addr
;;VAL:%t6;;TYPE:double

  %t7 = load double, double* %b_addr
;;VAL:%t7;;TYPE:double

  %t8 = fmul double %t6, %t7
;;VAL:%t8;;TYPE:double
  ret double %t8
}


; === Função: div ===
define double @math_div(double %a, double %b) {
entry:
  %a_addr = alloca double
  store double %a, double* %a_addr
;;VAL:%a_addr;;TYPE:double
  %b_addr = alloca double
  store double %b, double* %b_addr
;;VAL:%b_addr;;TYPE:double
  %t9 = load double, double* %b_addr
;;VAL:%t9;;TYPE:double

  %t10 = add i32 0, 0
;;VAL:%t10;;TYPE:i32

  %t12 = sitofp i32 %t10 to double
;;VAL:%t12;;TYPE:double
  %t11 = fcmp oeq double %t9, %t12
;;VAL:%t11;;TYPE:i1

  br i1 %t11, label %then_0, label %endif_0
then_0:
  %t13 = getelementptr inbounds [24 x i8], [24 x i8]* @.str0, i32 0, i32 0
  call i32 (i8*, ...) @printf(i8* getelementptr ([4 x i8], [4 x i8]* @.strStr, i32 0, i32 0), i8* %t13)
  %t14 = fadd double 0.0, 0.0
;;VAL:%t14;;TYPE:double
  ret double %t14
  br label %endif_0
endif_0:
  %t15 = load double, double* %a_addr
;;VAL:%t15;;TYPE:double

  %t16 = load double, double* %b_addr
;;VAL:%t16;;TYPE:double

  %t17 = fdiv double %t15, %t16
;;VAL:%t17;;TYPE:double
  ret double %t17
}


; === Função: pow ===
define double @math_pow(double %base, i32 %exp) {
entry:
  %base_addr = alloca double
  store double %base, double* %base_addr
;;VAL:%base_addr;;TYPE:double
  %exp_addr = alloca i32
  store i32 %exp, i32* %exp_addr
;;VAL:%exp_addr;;TYPE:i32
  %t18 = load i32, i32* %exp_addr
;;VAL:%t18;;TYPE:i32

  %t19 = add i32 0, 0
;;VAL:%t19;;TYPE:i32

  %t20 = icmp slt i32 %t18, %t19
;;VAL:%t20;;TYPE:i1

  br i1 %t20, label %then_1, label %endif_1
then_1:
  %t21 = getelementptr inbounds [53 x i8], [53 x i8]* @.str1, i32 0, i32 0
  call i32 (i8*, ...) @printf(i8* getelementptr ([4 x i8], [4 x i8]* @.strStr, i32 0, i32 0), i8* %t21)
  %t22 = fadd double 0.0, 0.0
;;VAL:%t22;;TYPE:double
  ret double %t22
  br label %endif_1
endif_1:
  %result = alloca double
;;VAL:%result;;TYPE:double
  %t23 = fadd double 0.0, 1.0
;;VAL:%t23;;TYPE:double
  store double %t23, double* %result
  %i = alloca i32
;;VAL:%i;;TYPE:i32
  %t24 = add i32 0, 0
;;VAL:%t24;;TYPE:i32
  store i32 %t24, i32* %i
  br label %while_cond_0
while_cond_0:
  %t25 = load i32, i32* %i
;;VAL:%t25;;TYPE:i32

  %t26 = load i32, i32* %exp_addr
;;VAL:%t26;;TYPE:i32

  %t27 = icmp slt i32 %t25, %t26
;;VAL:%t27;;TYPE:i1
  br i1 %t27, label %while_body_1, label %while_end_2
while_body_1:
  %t28 = load double, double* %result
;;VAL:%t28;;TYPE:double

  %t29 = load double, double* %base_addr
;;VAL:%t29;;TYPE:double

  %t30 = fmul double %t28, %t29
;;VAL:%t30;;TYPE:double
  store double %t30, double* %result
  %t31 = load i32, i32* %i
  %t32 = add i32 %t31, 1
  store i32 %t32, i32* %i
;;VAL:%t32;;TYPE:i32
  br label %while_cond_0
while_end_2:
  %t33 = load double, double* %result
;;VAL:%t33;;TYPE:double
  ret double %t33
}


; === Função: sqrt ===
define double @math_sqrt(double %x) {
entry:
  %x_addr = alloca double
  store double %x, double* %x_addr
;;VAL:%x_addr;;TYPE:double
  %t34 = load double, double* %x_addr
;;VAL:%t34;;TYPE:double

  %t35 = add i32 0, 0
;;VAL:%t35;;TYPE:i32

  %t37 = sitofp i32 %t35 to double
;;VAL:%t37;;TYPE:double
  %t36 = fcmp olt double %t34, %t37
;;VAL:%t36;;TYPE:i1

  br i1 %t36, label %then_2, label %endif_2
then_2:
  %t38 = getelementptr inbounds [31 x i8], [31 x i8]* @.str2, i32 0, i32 0
  call i32 (i8*, ...) @printf(i8* getelementptr ([4 x i8], [4 x i8]* @.strStr, i32 0, i32 0), i8* %t38)
  %t39 = fadd double 0.0, 0.0
;;VAL:%t39;;TYPE:double
  ret double %t39
  br label %endif_2
endif_2:
  %guess = alloca double
;;VAL:%guess;;TYPE:double
  %t40 = load double, double* %x_addr
;;VAL:%t40;;TYPE:double

  %t41 = fadd double 0.0, 2.0
;;VAL:%t41;;TYPE:double

  %t42 = fdiv double %t40, %t41
;;VAL:%t42;;TYPE:double
  store double %t42, double* %guess
  %i = alloca i32
;;VAL:%i;;TYPE:i32
  %t43 = add i32 0, 0
;;VAL:%t43;;TYPE:i32
  store i32 %t43, i32* %i
  br label %while_cond_3
while_cond_3:
  %t44 = load i32, i32* %i
;;VAL:%t44;;TYPE:i32

  %t45 = add i32 0, 20
;;VAL:%t45;;TYPE:i32

  %t46 = icmp slt i32 %t44, %t45
;;VAL:%t46;;TYPE:i1
  br i1 %t46, label %while_body_4, label %while_end_5
while_body_4:
  %t47 = load double, double* %guess
;;VAL:%t47;;TYPE:double

  %t48 = load double, double* %x_addr
;;VAL:%t48;;TYPE:double

  %t49 = load double, double* %guess
;;VAL:%t49;;TYPE:double

  %t50 = fdiv double %t48, %t49
;;VAL:%t50;;TYPE:double

  %t51 = fadd double %t47, %t50
;;VAL:%t51;;TYPE:double

  %t52 = fadd double 0.0, 2.0
;;VAL:%t52;;TYPE:double

  %t53 = fdiv double %t51, %t52
;;VAL:%t53;;TYPE:double
  store double %t53, double* %guess
  %t54 = load i32, i32* %i
  %t55 = add i32 %t54, 1
  store i32 %t55, i32* %i
;;VAL:%t55;;TYPE:i32
  br label %while_cond_3
while_end_5:
  %t56 = load double, double* %guess
;;VAL:%t56;;TYPE:double
  ret double %t56
}


; === Função: sin ===
define double @math_sin(double %x) {
entry:
  %x_addr = alloca double
  store double %x, double* %x_addr
;;VAL:%x_addr;;TYPE:double
  %term = alloca double
;;VAL:%term;;TYPE:double
  %t57 = load double, double* %x_addr
;;VAL:%t57;;TYPE:double
  store double %t57, double* %term
  %sum = alloca double
;;VAL:%sum;;TYPE:double
  %t58 = load double, double* %term
;;VAL:%t58;;TYPE:double
  store double %t58, double* %sum
  %n = alloca i32
;;VAL:%n;;TYPE:i32
  %t59 = add i32 0, 1
;;VAL:%t59;;TYPE:i32
  store i32 %t59, i32* %n
  br label %while_cond_6
while_cond_6:
  %t60 = load i32, i32* %n
;;VAL:%t60;;TYPE:i32

  %t61 = add i32 0, 10
;;VAL:%t61;;TYPE:i32

  %t62 = icmp slt i32 %t60, %t61
;;VAL:%t62;;TYPE:i1
  br i1 %t62, label %while_body_7, label %while_end_8
while_body_7:
  %t63 = load double, double* %term
  %t64 = fsub double 0.0, %t63
;;VAL:%t64;;TYPE:double

  %t65 = load double, double* %x_addr
;;VAL:%t65;;TYPE:double

  %t66 = fmul double %t64, %t65
;;VAL:%t66;;TYPE:double

  %t67 = load double, double* %x_addr
;;VAL:%t67;;TYPE:double

  %t68 = fmul double %t66, %t67
;;VAL:%t68;;TYPE:double

  %t69 = add i32 0, 2
;;VAL:%t69;;TYPE:i32

  %t70 = load i32, i32* %n
;;VAL:%t70;;TYPE:i32

  %t71 = mul i32 %t69, %t70
;;VAL:%t71;;TYPE:i32

  %t72 = add i32 0, 2
;;VAL:%t72;;TYPE:i32

  %t73 = load i32, i32* %n
;;VAL:%t73;;TYPE:i32

  %t74 = mul i32 %t72, %t73
;;VAL:%t74;;TYPE:i32

  %t75 = add i32 0, 1
;;VAL:%t75;;TYPE:i32

  %t76 = add i32 %t74, %t75
;;VAL:%t76;;TYPE:i32

  %t77 = mul i32 %t71, %t76
;;VAL:%t77;;TYPE:i32

  %t79 = sitofp i32 %t77 to double
;;VAL:%t79;;TYPE:double
  %t78 = fdiv double %t68, %t79
;;VAL:%t78;;TYPE:double
  store double %t78, double* %term
  %t80 = load double, double* %sum
;;VAL:%t80;;TYPE:double

  %t81 = load double, double* %term
;;VAL:%t81;;TYPE:double

  %t82 = fadd double %t80, %t81
;;VAL:%t82;;TYPE:double
  store double %t82, double* %sum
  %t83 = load i32, i32* %n
  %t84 = add i32 %t83, 1
  store i32 %t84, i32* %n
;;VAL:%t84;;TYPE:i32
  br label %while_cond_6
while_end_8:
  %t85 = load double, double* %sum
;;VAL:%t85;;TYPE:double
  ret double %t85
}


; === Função: cos ===
define double @math_cos(double %x) {
entry:
  %x_addr = alloca double
  store double %x, double* %x_addr
;;VAL:%x_addr;;TYPE:double
  %term = alloca double
;;VAL:%term;;TYPE:double
  %t86 = fadd double 0.0, 1.0
;;VAL:%t86;;TYPE:double
  store double %t86, double* %term
  %sum = alloca double
;;VAL:%sum;;TYPE:double
  %t87 = load double, double* %term
;;VAL:%t87;;TYPE:double
  store double %t87, double* %sum
  %n = alloca i32
;;VAL:%n;;TYPE:i32
  %t88 = add i32 0, 1
;;VAL:%t88;;TYPE:i32
  store i32 %t88, i32* %n
  br label %while_cond_9
while_cond_9:
  %t89 = load i32, i32* %n
;;VAL:%t89;;TYPE:i32

  %t90 = add i32 0, 10
;;VAL:%t90;;TYPE:i32

  %t91 = icmp slt i32 %t89, %t90
;;VAL:%t91;;TYPE:i1
  br i1 %t91, label %while_body_10, label %while_end_11
while_body_10:
  %t92 = load double, double* %term
  %t93 = fsub double 0.0, %t92
;;VAL:%t93;;TYPE:double

  %t94 = load double, double* %x_addr
;;VAL:%t94;;TYPE:double

  %t95 = fmul double %t93, %t94
;;VAL:%t95;;TYPE:double

  %t96 = load double, double* %x_addr
;;VAL:%t96;;TYPE:double

  %t97 = fmul double %t95, %t96
;;VAL:%t97;;TYPE:double

  %t98 = add i32 0, 2
;;VAL:%t98;;TYPE:i32

  %t99 = load i32, i32* %n
;;VAL:%t99;;TYPE:i32

  %t100 = mul i32 %t98, %t99
;;VAL:%t100;;TYPE:i32

  %t101 = add i32 0, 1
;;VAL:%t101;;TYPE:i32

  %t102 = sub i32 %t100, %t101
;;VAL:%t102;;TYPE:i32

  %t103 = add i32 0, 2
;;VAL:%t103;;TYPE:i32

  %t104 = load i32, i32* %n
;;VAL:%t104;;TYPE:i32

  %t105 = mul i32 %t103, %t104
;;VAL:%t105;;TYPE:i32

  %t106 = mul i32 %t102, %t105
;;VAL:%t106;;TYPE:i32

  %t108 = sitofp i32 %t106 to double
;;VAL:%t108;;TYPE:double
  %t107 = fdiv double %t97, %t108
;;VAL:%t107;;TYPE:double
  store double %t107, double* %term
  %t109 = load double, double* %sum
;;VAL:%t109;;TYPE:double

  %t110 = load double, double* %term
;;VAL:%t110;;TYPE:double

  %t111 = fadd double %t109, %t110
;;VAL:%t111;;TYPE:double
  store double %t111, double* %sum
  %t112 = load i32, i32* %n
  %t113 = add i32 %t112, 1
  store i32 %t113, i32* %n
;;VAL:%t113;;TYPE:i32
  br label %while_cond_9
while_end_11:
  %t114 = load double, double* %sum
;;VAL:%t114;;TYPE:double
  ret double %t114
}


; === Função: fact ===
define i32 @math_fact(i32 %n) {
entry:
  %n_addr = alloca i32
  store i32 %n, i32* %n_addr
;;VAL:%n_addr;;TYPE:i32
  %t115 = load i32, i32* %n_addr
;;VAL:%t115;;TYPE:i32

  %t116 = add i32 0, 0
;;VAL:%t116;;TYPE:i32

  %t117 = icmp slt i32 %t115, %t116
;;VAL:%t117;;TYPE:i1

  br i1 %t117, label %then_3, label %endif_3
then_3:
  %t118 = getelementptr inbounds [36 x i8], [36 x i8]* @.str3, i32 0, i32 0
  call i32 (i8*, ...) @printf(i8* getelementptr ([4 x i8], [4 x i8]* @.strStr, i32 0, i32 0), i8* %t118)
  %t119 = add i32 0, 0
;;VAL:%t119;;TYPE:i32
  ret i32 %t119
  br label %endif_3
endif_3:
  %result = alloca i32
;;VAL:%result;;TYPE:i32
  %t120 = add i32 0, 1
;;VAL:%t120;;TYPE:i32
  store i32 %t120, i32* %result
  %i = alloca i32
;;VAL:%i;;TYPE:i32
  %t121 = add i32 0, 1
;;VAL:%t121;;TYPE:i32
  store i32 %t121, i32* %i
  br label %while_cond_12
while_cond_12:
  %t122 = load i32, i32* %i
;;VAL:%t122;;TYPE:i32

  %t123 = load i32, i32* %n_addr
;;VAL:%t123;;TYPE:i32

  %t124 = icmp sle i32 %t122, %t123
;;VAL:%t124;;TYPE:i1
  br i1 %t124, label %while_body_13, label %while_end_14
while_body_13:
  %t125 = load i32, i32* %result
;;VAL:%t125;;TYPE:i32

  %t126 = load i32, i32* %i
;;VAL:%t126;;TYPE:i32

  %t127 = mul i32 %t125, %t126
;;VAL:%t127;;TYPE:i32
  store i32 %t127, i32* %result
  %t128 = load i32, i32* %i
  %t129 = add i32 %t128, 1
  store i32 %t129, i32* %i
;;VAL:%t129;;TYPE:i32
  br label %while_cond_12
while_end_14:
  %t130 = load i32, i32* %result
;;VAL:%t130;;TYPE:i32
  ret i32 %t130
}


; === Função: factorial ===
define i32 @math_factorial(i32 %n) {
entry:
  %n_addr = alloca i32
  store i32 %n, i32* %n_addr
;;VAL:%n_addr;;TYPE:i32
  %t131 = load i32, i32* %n_addr
;;VAL:%t131;;TYPE:i32

  %t132 = add i32 0, 0
;;VAL:%t132;;TYPE:i32

  %t133 = icmp eq i32 %t131, %t132
;;VAL:%t133;;TYPE:i1

  br i1 %t133, label %then_4, label %endif_4
then_4:
  %t134 = add i32 0, 1
;;VAL:%t134;;TYPE:i32
  ret i32 %t134
  br label %endif_4
endif_4:
  %t135 = load i32, i32* %n_addr
;;VAL:%t135;;TYPE:i32

  %t136 = load i32, i32* %n_addr
;;VAL:%t136;;TYPE:i32

  %t137 = add i32 0, 1
;;VAL:%t137;;TYPE:i32

  %t138 = sub i32 %t136, %t137
;;VAL:%t138;;TYPE:i32
  %t139 = call i32 @math_factorial(i32 %t138)
;;VAL:%t139;;TYPE:i32

  %t140 = mul i32 %t135, %t139
;;VAL:%t140;;TYPE:i32
  ret i32 %t140
}


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

@.str0 = private constant [24 x i8] c"Erro: divisao por zero!\00"
@.str1 = private constant [53 x i8] c"Aviso: expoente negativo nao suportado, retornando 0\00"
@.str2 = private constant [31 x i8] c"Erro: sqrt de numero negativo!\00"
@.str3 = private constant [36 x i8] c"Erro: factorial de numero negativo!\00"

define i32 @main() {
  ; PrintNode
  %t141 = fadd double 0.0, 3.0
;;VAL:%t141;;TYPE:double
  %t142 = fadd double 0.0, 4.0
;;VAL:%t142;;TYPE:double
  %t143 = call double @math_sum(double %t141, double %t142)
  call i32 (i8*, ...) @printf(i8* getelementptr ([4 x i8], [4 x i8]* @.strDouble, i32 0, i32 0), double %t143)
  ; PrintNode
  %t144 = fadd double 0.0, 10.0
;;VAL:%t144;;TYPE:double
  %t145 = fadd double 0.0, 6.0
;;VAL:%t145;;TYPE:double
  %t146 = call double @math_sub(double %t144, double %t145)
  call i32 (i8*, ...) @printf(i8* getelementptr ([4 x i8], [4 x i8]* @.strDouble, i32 0, i32 0), double %t146)
  ; PrintNode
  %t147 = fadd double 0.0, 2.0
;;VAL:%t147;;TYPE:double
  %t148 = fadd double 0.0, 5.0
;;VAL:%t148;;TYPE:double
  %t149 = call double @math_mul(double %t147, double %t148)
  call i32 (i8*, ...) @printf(i8* getelementptr ([4 x i8], [4 x i8]* @.strDouble, i32 0, i32 0), double %t149)
  ; PrintNode
  %t150 = fadd double 0.0, 10.0
;;VAL:%t150;;TYPE:double
  %t151 = fadd double 0.0, 2.0
;;VAL:%t151;;TYPE:double
  %t152 = call double @math_div(double %t150, double %t151)
  call i32 (i8*, ...) @printf(i8* getelementptr ([4 x i8], [4 x i8]* @.strDouble, i32 0, i32 0), double %t152)
  ; PrintNode
  %t153 = fadd double 0.0, 5.0
;;VAL:%t153;;TYPE:double
  %t154 = fadd double 0.0, 0.0
;;VAL:%t154;;TYPE:double
  %t155 = call double @math_div(double %t153, double %t154)
  call i32 (i8*, ...) @printf(i8* getelementptr ([4 x i8], [4 x i8]* @.strDouble, i32 0, i32 0), double %t155)
  ; PrintNode
  %t156 = fadd double 0.0, 2.0
;;VAL:%t156;;TYPE:double
  %t157 = add i32 0, 3
;;VAL:%t157;;TYPE:i32
  %t158 = call double @math_pow(double %t156, i32 %t157)
  call i32 (i8*, ...) @printf(i8* getelementptr ([4 x i8], [4 x i8]* @.strDouble, i32 0, i32 0), double %t158)
  ; PrintNode
  %t159 = fadd double 0.0, 5.0
;;VAL:%t159;;TYPE:double
  %t160 = sub i32 0, 1
;;VAL:%t160;;TYPE:i32
  %t161 = call double @math_pow(double %t159, i32 %t160)
  call i32 (i8*, ...) @printf(i8* getelementptr ([4 x i8], [4 x i8]* @.strDouble, i32 0, i32 0), double %t161)
  ; PrintNode
  %t162 = fadd double 0.0, 16.0
;;VAL:%t162;;TYPE:double
  %t163 = call double @math_sqrt(double %t162)
  call i32 (i8*, ...) @printf(i8* getelementptr ([4 x i8], [4 x i8]* @.strDouble, i32 0, i32 0), double %t163)
  ; PrintNode
  %t164 = fsub double 0.0, 4.0
;;VAL:%t164;;TYPE:double
  %t165 = call double @math_sqrt(double %t164)
  call i32 (i8*, ...) @printf(i8* getelementptr ([4 x i8], [4 x i8]* @.strDouble, i32 0, i32 0), double %t165)
  ; PrintNode
  %t166 = fadd double 0.0, 0.5
;;VAL:%t166;;TYPE:double
  %t167 = call double @math_sin(double %t166)
  call i32 (i8*, ...) @printf(i8* getelementptr ([4 x i8], [4 x i8]* @.strDouble, i32 0, i32 0), double %t167)
  ; PrintNode
  %t168 = fadd double 0.0, 0.5
;;VAL:%t168;;TYPE:double
  %t169 = call double @math_cos(double %t168)
  call i32 (i8*, ...) @printf(i8* getelementptr ([4 x i8], [4 x i8]* @.strDouble, i32 0, i32 0), double %t169)
  ; PrintNode
  %t170 = add i32 0, 5
;;VAL:%t170;;TYPE:i32
  %t171 = call i32 @math_factorial(i32 %t170)
  call i32 (i8*, ...) @printf(i8* getelementptr ([4 x i8], [4 x i8]* @.strInt, i32 0, i32 0), i32 %t171)
  ; PrintNode
  %t172 = add i32 0, 6
;;VAL:%t172;;TYPE:i32
  %t173 = call i32 @math_fact(i32 %t172)
  call i32 (i8*, ...) @printf(i8* getelementptr ([4 x i8], [4 x i8]* @.strInt, i32 0, i32 0), i32 %t173)
  call i32 @getchar()
  ret i32 0
}
