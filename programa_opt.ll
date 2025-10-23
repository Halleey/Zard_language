; ModuleID = 'programa.ll'
source_filename = "programa.ll"

@.strTrue = private constant [6 x i8] c"true\0A\00"
@.strFalse = private constant [7 x i8] c"false\0A\00"
@.strInt = private constant [4 x i8] c"%d\0A\00"
@.strDouble = private constant [4 x i8] c"%f\0A\00"
@.strStr = private constant [4 x i8] c"%s\0A\00"
@.str0 = private constant [24 x i8] c"Erro: divisao por zero!\00"
@.str1 = private constant [53 x i8] c"Aviso: expoente negativo nao suportado, retornando 0\00"
@.str2 = private constant [31 x i8] c"Erro: sqrt de numero negativo!\00"
@.str3 = private constant [36 x i8] c"Erro: factorial de numero negativo!\00"
@.str4 = private constant [27 x i8] c"Tentando dividir por zero:\00"
@.str5 = private constant [39 x i8] c"Tentando fatorial de n\C3\BAmero negativo:\00"
@.str6 = private constant [44 x i8] c"Tentando raiz quadrada de n\C3\BAmero negativo:\00"
@.str7 = private constant [16 x i8] c"Fim dos testes.\00"

define double @math_sum(double %a, double %b) {
entry:
  %t2 = fadd double %a, %b
  ret double %t2
}

define double @math_sub(double %a, double %b) {
entry:
  %t5 = fsub double %a, %b
  ret double %t5
}

define double @math_mul(double %a, double %b) {
entry:
  %t8 = fmul double %a, %b
  ret double %t8
}

define double @math_div(double %a, double %b) {
entry:
  %t11 = fcmp oeq double %b, 0.000000e+00
  br i1 %t11, label %then_0, label %endif_0

then_0:                                           ; preds = %entry
  %puts = call i32 @puts(ptr nonnull dereferenceable(1) @.str0)
  ret double 0.000000e+00

0:                                                ; No predecessors!
  br label %endif_0

endif_0:                                          ; preds = %0, %entry
  %t17 = fdiv double %a, %b
  ret double %t17
}

define double @math_pow(double %base, i32 %exp) {
entry:
  %t20 = icmp slt i32 %exp, 0
  br i1 %t20, label %then_1, label %endif_1

then_1:                                           ; preds = %entry
  %puts = call i32 @puts(ptr nonnull dereferenceable(1) @.str1)
  ret double 0.000000e+00

0:                                                ; No predecessors!
  br label %endif_1

endif_1:                                          ; preds = %0, %entry
  %result = alloca double, align 8
  store double 1.000000e+00, ptr %result, align 8
  %i = alloca i32, align 4
  br label %while_cond_0

while_cond_0:                                     ; preds = %while_body_1, %endif_1
  %storemerge = phi i32 [ 0, %endif_1 ], [ %t32, %while_body_1 ]
  store i32 %storemerge, ptr %i, align 4
  %t27 = icmp slt i32 %storemerge, %exp
  br i1 %t27, label %while_body_1, label %while_end_2

while_body_1:                                     ; preds = %while_cond_0
  %t28 = load double, ptr %result, align 8
  %t30 = fmul double %t28, %base
  store double %t30, ptr %result, align 8
  %t31 = load i32, ptr %i, align 4
  %t32 = add i32 %t31, 1
  br label %while_cond_0

while_end_2:                                      ; preds = %while_cond_0
  %t33 = load double, ptr %result, align 8
  ret double %t33
}

define double @math_sqrt(double %x) {
entry:
  %t36 = fcmp olt double %x, 0.000000e+00
  br i1 %t36, label %then_2, label %endif_2

then_2:                                           ; preds = %entry
  %puts = call i32 @puts(ptr nonnull dereferenceable(1) @.str2)
  ret double 0.000000e+00

0:                                                ; No predecessors!
  br label %endif_2

endif_2:                                          ; preds = %0, %entry
  %guess = alloca double, align 8
  %t42 = fmul double %x, 5.000000e-01
  store double %t42, ptr %guess, align 8
  %i = alloca i32, align 4
  br label %while_cond_3

while_cond_3:                                     ; preds = %while_body_4, %endif_2
  %storemerge = phi i32 [ 0, %endif_2 ], [ %t55, %while_body_4 ]
  store i32 %storemerge, ptr %i, align 4
  %t46 = icmp slt i32 %storemerge, 20
  br i1 %t46, label %while_body_4, label %while_end_5

while_body_4:                                     ; preds = %while_cond_3
  %t47 = load double, ptr %guess, align 8
  %t50 = fdiv double %x, %t47
  %t51 = fadd double %t47, %t50
  %t53 = fmul double %t51, 5.000000e-01
  store double %t53, ptr %guess, align 8
  %t54 = load i32, ptr %i, align 4
  %t55 = add i32 %t54, 1
  br label %while_cond_3

while_end_5:                                      ; preds = %while_cond_3
  %t56 = load double, ptr %guess, align 8
  ret double %t56
}

define double @math_sin(double %x) {
entry:
  br label %while_cond_6

while_cond_6:                                     ; preds = %while_body_7, %entry
  %term.0 = phi double [ %x, %entry ], [ %t78, %while_body_7 ]
  %sum.0 = phi double [ %x, %entry ], [ %t82, %while_body_7 ]
  %storemerge = phi i32 [ 1, %entry ], [ %t84, %while_body_7 ]
  %t62 = icmp slt i32 %storemerge, 10
  br i1 %t62, label %while_body_7, label %while_end_8

while_body_7:                                     ; preds = %while_cond_6
  %t64 = fsub double 0.000000e+00, %term.0
  %t66 = fmul double %t64, %x
  %t68 = fmul double %t66, %x
  %t71 = shl i32 %storemerge, 1
  %t74 = shl i32 %storemerge, 1
  %t76 = or disjoint i32 %t74, 1
  %t77 = mul i32 %t71, %t76
  %t79 = sitofp i32 %t77 to double
  %t78 = fdiv double %t68, %t79
  %t82 = fadd double %sum.0, %t78
  %t84 = add i32 %storemerge, 1
  br label %while_cond_6

while_end_8:                                      ; preds = %while_cond_6
  ret double %sum.0
}

define double @math_cos(double %x) {
entry:
  br label %while_cond_9

while_cond_9:                                     ; preds = %while_body_10, %entry
  %term.0 = phi double [ 1.000000e+00, %entry ], [ %t107, %while_body_10 ]
  %sum.0 = phi double [ 1.000000e+00, %entry ], [ %t111, %while_body_10 ]
  %storemerge = phi i32 [ 1, %entry ], [ %t113, %while_body_10 ]
  %t91 = icmp slt i32 %storemerge, 10
  br i1 %t91, label %while_body_10, label %while_end_11

while_body_10:                                    ; preds = %while_cond_9
  %t93 = fsub double 0.000000e+00, %term.0
  %t95 = fmul double %t93, %x
  %t97 = fmul double %t95, %x
  %t100 = shl i32 %storemerge, 1
  %t102 = add i32 %t100, -1
  %t105 = shl i32 %storemerge, 1
  %t106 = mul i32 %t102, %t105
  %t108 = sitofp i32 %t106 to double
  %t107 = fdiv double %t97, %t108
  %t111 = fadd double %sum.0, %t107
  %t113 = add i32 %storemerge, 1
  br label %while_cond_9

while_end_11:                                     ; preds = %while_cond_9
  ret double %sum.0
}

define i32 @math_fact(i32 %n) {
entry:
  %t117 = icmp slt i32 %n, 0
  br i1 %t117, label %then_3, label %endif_3

then_3:                                           ; preds = %entry
  %puts = call i32 @puts(ptr nonnull dereferenceable(1) @.str3)
  ret i32 0

0:                                                ; No predecessors!
  br label %endif_3

endif_3:                                          ; preds = %0, %entry
  %result = alloca i32, align 4
  store i32 1, ptr %result, align 4
  %i = alloca i32, align 4
  br label %while_cond_12

while_cond_12:                                    ; preds = %while_body_13, %endif_3
  %storemerge = phi i32 [ 1, %endif_3 ], [ %t129, %while_body_13 ]
  store i32 %storemerge, ptr %i, align 4
  %t124.not = icmp sgt i32 %storemerge, %n
  br i1 %t124.not, label %while_end_14, label %while_body_13

while_body_13:                                    ; preds = %while_cond_12
  %t125 = load i32, ptr %result, align 4
  %t126 = load i32, ptr %i, align 4
  %t127 = mul i32 %t125, %t126
  store i32 %t127, ptr %result, align 4
  %t129 = add i32 %t126, 1
  br label %while_cond_12

while_end_14:                                     ; preds = %while_cond_12
  %t130 = load i32, ptr %result, align 4
  ret i32 %t130
}

define i32 @math_factorial(i32 %n) {
entry:
  %t133 = icmp slt i32 %n, 0
  br i1 %t133, label %then_4, label %endif_4

then_4:                                           ; preds = %entry
  %puts = call i32 @puts(ptr nonnull dereferenceable(1) @.str3)
  ret i32 0

0:                                                ; No predecessors!
  br label %endif_4

endif_4:                                          ; preds = %0, %entry
  %t138 = icmp eq i32 %n, 0
  br i1 %t138, label %then_5, label %endif_5

then_5:                                           ; preds = %endif_4
  ret i32 1

1:                                                ; No predecessors!
  br label %endif_5

endif_5:                                          ; preds = %1, %endif_4
  %t143 = add i32 %n, -1
  %t144 = call i32 @math_factorial(i32 %t143)
  %t145 = mul i32 %n, %t144
  ret i32 %t145
}

declare i32 @printf(ptr, ...)

declare i32 @getchar()

declare void @printString(ptr)

declare ptr @malloc(i64)

declare void @setString(ptr, ptr)

declare ptr @createString(ptr)

declare i1 @strcmp_eq(ptr, ptr)

declare i1 @strcmp_neq(ptr, ptr)

define i32 @main() {
  %t153 = call double @math_sum(double 1.000000e+01, double 3.000000e+00)
  %1 = call i32 (ptr, ...) @printf(ptr noundef nonnull dereferenceable(1) @.strDouble, double %t153)
  %t156 = call double @math_sub(double 1.000000e+01, double 3.000000e+00)
  %2 = call i32 (ptr, ...) @printf(ptr noundef nonnull dereferenceable(1) @.strDouble, double %t156)
  %t159 = call double @math_mul(double 1.000000e+01, double 3.000000e+00)
  %3 = call i32 (ptr, ...) @printf(ptr noundef nonnull dereferenceable(1) @.strDouble, double %t159)
  %t162 = call double @math_div(double 1.000000e+01, double 3.000000e+00)
  %4 = call i32 (ptr, ...) @printf(ptr noundef nonnull dereferenceable(1) @.strDouble, double %t162)
  %t165 = call double @math_pow(double 1.000000e+01, i32 3)
  %5 = call i32 (ptr, ...) @printf(ptr noundef nonnull dereferenceable(1) @.strDouble, double %t165)
  %t167 = call i32 @math_factorial(i32 5)
  %6 = call i32 (ptr, ...) @printf(ptr noundef nonnull dereferenceable(1) @.strInt, i32 %t167)
  %t169 = call double @math_sqrt(double 1.000000e+01)
  %7 = call i32 (ptr, ...) @printf(ptr noundef nonnull dereferenceable(1) @.strDouble, double %t169)
  %t171 = call double @math_sin(double 1.000000e+01)
  %8 = call i32 (ptr, ...) @printf(ptr noundef nonnull dereferenceable(1) @.strDouble, double %t171)
  %t173 = call double @math_cos(double 1.000000e+01)
  %9 = call i32 (ptr, ...) @printf(ptr noundef nonnull dereferenceable(1) @.strDouble, double %t173)
  %puts = call i32 @puts(ptr nonnull dereferenceable(1) @.str4)
  %t178 = call double @math_div(double 1.000000e+01, double 0.000000e+00)
  %10 = call i32 (ptr, ...) @printf(ptr noundef nonnull dereferenceable(1) @.strDouble, double %t178)
  %puts1 = call i32 @puts(ptr nonnull dereferenceable(1) @.str5)
  %t181 = call i32 @math_factorial(i32 -3)
  %11 = call i32 (ptr, ...) @printf(ptr noundef nonnull dereferenceable(1) @.strInt, i32 %t181)
  %puts2 = call i32 @puts(ptr nonnull dereferenceable(1) @.str6)
  %t185 = call double @math_sqrt(double -9.000000e+00)
  %12 = call i32 (ptr, ...) @printf(ptr noundef nonnull dereferenceable(1) @.strDouble, double %t185)
  %puts3 = call i32 @puts(ptr nonnull dereferenceable(1) @.str7)
  %13 = call i32 @getchar()
  ret i32 0
}

; Function Attrs: nofree nounwind
declare noundef i32 @puts(ptr nocapture noundef readonly) #0

attributes #0 = { nofree nounwind }
