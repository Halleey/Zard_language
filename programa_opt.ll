; ModuleID = 'programa.ll'
source_filename = "programa.ll"

@.strChar = private constant [3 x i8] c"%c\00"
@.strTrue = private constant [6 x i8] c"true\0A\00"
@.strFalse = private constant [7 x i8] c"false\0A\00"
@.strInt = private constant [4 x i8] c"%d\0A\00"
@.strDouble = private constant [4 x i8] c"%f\0A\00"
@.strFloat = private constant [4 x i8] c"%f\0A\00"
@.strStr = private constant [4 x i8] c"%s\0A\00"
@.strEmpty = private constant [1 x i8] zeroinitializer
@.str0 = private constant [24 x i8] c"Erro: divisao por zero!\00"
@.str1 = private constant [53 x i8] c"Aviso: expoente negativo nao suportado, retornando 0\00"
@.str2 = private constant [31 x i8] c"Erro: sqrt de numero negativo!\00"
@.str3 = private constant [36 x i8] c"Erro: factorial de numero negativo!\00"
@.str4 = private constant [27 x i8] c"Tentando dividir por zero:\00"
@.str5 = private constant [39 x i8] c"Tentando fatorial de n\C3\BAmero negativo:\00"
@.str6 = private constant [44 x i8] c"Tentando raiz quadrada de n\C3\BAmero negativo:\00"
@.str7 = private constant [16 x i8] c"Fim dos testes.\00"

define double @sum(double %a, double %b) {
entry:
  %tmp2 = fadd double %a, %b
  ret double %tmp2
}

define double @sub(double %a, double %b) {
entry:
  %tmp5 = fsub double %a, %b
  ret double %tmp5
}

define double @mul(double %a, double %b) {
entry:
  %tmp8 = fmul double %a, %b
  ret double %tmp8
}

define double @div(double %a, double %b) {
entry:
  %tmp11 = fcmp oeq double %b, 0.000000e+00
  br i1 %tmp11, label %then_0, label %endif_0

then_0:                                           ; preds = %entry
  %0 = call i32 (ptr, ...) @printf(ptr @.strStr, ptr @.str0)
  ret double 0.000000e+00

1:                                                ; No predecessors!
  br label %endif_0

endif_0:                                          ; preds = %1, %entry
  %tmp17 = fdiv double %a, %b
  ret double %tmp17
}

define double @pow(double %base, i32 %exp) {
entry:
  %tmp20 = icmp slt i32 %exp, 0
  br i1 %tmp20, label %then_1, label %endif_1

then_1:                                           ; preds = %entry
  %0 = call i32 (ptr, ...) @printf(ptr @.strStr, ptr @.str1)
  ret double 0.000000e+00

1:                                                ; No predecessors!
  br label %endif_1

endif_1:                                          ; preds = %1, %entry
  %result = alloca double, align 8
  store double 1.000000e+00, ptr %result, align 8
  %i = alloca i32, align 4
  store i32 0, ptr %i, align 4
  %tmp251 = load i32, ptr %i, align 4
  %tmp272 = icmp slt i32 %tmp251, %exp
  %tmp333 = load double, ptr %result, align 8
  br i1 %tmp272, label %while_body_1.lr.ph, label %while_end_2

while_body_1.lr.ph:                               ; preds = %endif_1
  br label %while_body_1

while_body_1:                                     ; preds = %while_body_1.lr.ph, %while_body_1
  %tmp334 = phi double [ %tmp333, %while_body_1.lr.ph ], [ %tmp33, %while_body_1 ]
  %tmp30 = fmul double %base, %tmp334
  store double %tmp30, ptr %result, align 8
  %tmp31 = load i32, ptr %i, align 4
  %tmp32 = add i32 %tmp31, 1
  store i32 %tmp32, ptr %i, align 4
  %tmp25 = load i32, ptr %i, align 4
  %tmp27 = icmp slt i32 %tmp25, %exp
  %tmp33 = load double, ptr %result, align 8
  br i1 %tmp27, label %while_body_1, label %while_cond_0.while_end_2_crit_edge

while_cond_0.while_end_2_crit_edge:               ; preds = %while_body_1
  %split = phi double [ %tmp33, %while_body_1 ]
  br label %while_end_2

while_end_2:                                      ; preds = %while_cond_0.while_end_2_crit_edge, %endif_1
  %tmp33.lcssa = phi double [ %split, %while_cond_0.while_end_2_crit_edge ], [ %tmp333, %endif_1 ]
  ret double %tmp33.lcssa
}

define double @sqrt(double %x) {
entry:
  %tmp36 = fcmp olt double %x, 0.000000e+00
  br i1 %tmp36, label %then_2, label %endif_2

then_2:                                           ; preds = %entry
  %0 = call i32 (ptr, ...) @printf(ptr @.strStr, ptr @.str2)
  ret double 0.000000e+00

1:                                                ; No predecessors!
  br label %endif_2

endif_2:                                          ; preds = %1, %entry
  %guess = alloca double, align 8
  %tmp42 = fdiv double %x, 2.000000e+00
  store double %tmp42, ptr %guess, align 8
  %i = alloca i32, align 4
  store i32 0, ptr %i, align 4
  %tmp441 = load i32, ptr %i, align 4
  %tmp462 = icmp slt i32 %tmp441, 20
  %tmp563 = load double, ptr %guess, align 8
  br i1 %tmp462, label %while_body_4.lr.ph, label %while_end_5

while_body_4.lr.ph:                               ; preds = %endif_2
  br label %while_body_4

while_body_4:                                     ; preds = %while_body_4.lr.ph, %while_body_4
  %tmp564 = phi double [ %tmp563, %while_body_4.lr.ph ], [ %tmp56, %while_body_4 ]
  %tmp50 = fdiv double %x, %tmp564
  %tmp51 = fadd double %tmp564, %tmp50
  %tmp53 = fdiv double %tmp51, 2.000000e+00
  store double %tmp53, ptr %guess, align 8
  %tmp54 = load i32, ptr %i, align 4
  %tmp55 = add i32 %tmp54, 1
  store i32 %tmp55, ptr %i, align 4
  %tmp44 = load i32, ptr %i, align 4
  %tmp46 = icmp slt i32 %tmp44, 20
  %tmp56 = load double, ptr %guess, align 8
  br i1 %tmp46, label %while_body_4, label %while_cond_3.while_end_5_crit_edge

while_cond_3.while_end_5_crit_edge:               ; preds = %while_body_4
  %split = phi double [ %tmp56, %while_body_4 ]
  br label %while_end_5

while_end_5:                                      ; preds = %while_cond_3.while_end_5_crit_edge, %endif_2
  %tmp56.lcssa = phi double [ %split, %while_cond_3.while_end_5_crit_edge ], [ %tmp563, %endif_2 ]
  ret double %tmp56.lcssa
}

define double @sin(double %x) {
entry:
  br label %while_body_7

while_body_7:                                     ; preds = %entry
  %tmp64 = fsub double 0.000000e+00, %x
  %tmp66 = fmul double %x, %tmp64
  %tmp68 = fmul double %x, %tmp66
  %tmp78 = fdiv double %tmp68, 6.000000e+00
  %tmp82 = fadd double %x, %tmp78
  %tmp64.1 = fsub double 0.000000e+00, %tmp78
  %tmp66.1 = fmul double %x, %tmp64.1
  %tmp68.1 = fmul double %x, %tmp66.1
  %tmp78.1 = fdiv double %tmp68.1, 2.000000e+01
  %tmp82.1 = fadd double %tmp82, %tmp78.1
  %tmp64.2 = fsub double 0.000000e+00, %tmp78.1
  %tmp66.2 = fmul double %x, %tmp64.2
  %tmp68.2 = fmul double %x, %tmp66.2
  %tmp78.2 = fdiv double %tmp68.2, 4.200000e+01
  %tmp82.2 = fadd double %tmp82.1, %tmp78.2
  %tmp64.3 = fsub double 0.000000e+00, %tmp78.2
  %tmp66.3 = fmul double %x, %tmp64.3
  %tmp68.3 = fmul double %x, %tmp66.3
  %tmp78.3 = fdiv double %tmp68.3, 7.200000e+01
  %tmp82.3 = fadd double %tmp82.2, %tmp78.3
  %tmp64.4 = fsub double 0.000000e+00, %tmp78.3
  %tmp66.4 = fmul double %x, %tmp64.4
  %tmp68.4 = fmul double %x, %tmp66.4
  %tmp78.4 = fdiv double %tmp68.4, 1.100000e+02
  %tmp82.4 = fadd double %tmp82.3, %tmp78.4
  %tmp64.5 = fsub double 0.000000e+00, %tmp78.4
  %tmp66.5 = fmul double %x, %tmp64.5
  %tmp68.5 = fmul double %x, %tmp66.5
  %tmp78.5 = fdiv double %tmp68.5, 1.560000e+02
  %tmp82.5 = fadd double %tmp82.4, %tmp78.5
  %tmp64.6 = fsub double 0.000000e+00, %tmp78.5
  %tmp66.6 = fmul double %x, %tmp64.6
  %tmp68.6 = fmul double %x, %tmp66.6
  %tmp78.6 = fdiv double %tmp68.6, 2.100000e+02
  %tmp82.6 = fadd double %tmp82.5, %tmp78.6
  %tmp64.7 = fsub double 0.000000e+00, %tmp78.6
  %tmp66.7 = fmul double %x, %tmp64.7
  %tmp68.7 = fmul double %x, %tmp66.7
  %tmp78.7 = fdiv double %tmp68.7, 2.720000e+02
  %tmp82.7 = fadd double %tmp82.6, %tmp78.7
  %tmp64.8 = fsub double 0.000000e+00, %tmp78.7
  %tmp66.8 = fmul double %x, %tmp64.8
  %tmp68.8 = fmul double %x, %tmp66.8
  %tmp78.8 = fdiv double %tmp68.8, 3.420000e+02
  %tmp82.8 = fadd double %tmp82.7, %tmp78.8
  ret double %tmp82.8
}

define double @cos(double %x) {
entry:
  br label %while_body_10

while_body_10:                                    ; preds = %entry
  %tmp95 = fmul double %x, -1.000000e+00
  %tmp97 = fmul double %x, %tmp95
  %tmp107 = fdiv double %tmp97, 2.000000e+00
  %tmp111 = fadd double 1.000000e+00, %tmp107
  %tmp93.1 = fsub double 0.000000e+00, %tmp107
  %tmp95.1 = fmul double %x, %tmp93.1
  %tmp97.1 = fmul double %x, %tmp95.1
  %tmp107.1 = fdiv double %tmp97.1, 1.200000e+01
  %tmp111.1 = fadd double %tmp111, %tmp107.1
  %tmp93.2 = fsub double 0.000000e+00, %tmp107.1
  %tmp95.2 = fmul double %x, %tmp93.2
  %tmp97.2 = fmul double %x, %tmp95.2
  %tmp107.2 = fdiv double %tmp97.2, 3.000000e+01
  %tmp111.2 = fadd double %tmp111.1, %tmp107.2
  %tmp93.3 = fsub double 0.000000e+00, %tmp107.2
  %tmp95.3 = fmul double %x, %tmp93.3
  %tmp97.3 = fmul double %x, %tmp95.3
  %tmp107.3 = fdiv double %tmp97.3, 5.600000e+01
  %tmp111.3 = fadd double %tmp111.2, %tmp107.3
  %tmp93.4 = fsub double 0.000000e+00, %tmp107.3
  %tmp95.4 = fmul double %x, %tmp93.4
  %tmp97.4 = fmul double %x, %tmp95.4
  %tmp107.4 = fdiv double %tmp97.4, 9.000000e+01
  %tmp111.4 = fadd double %tmp111.3, %tmp107.4
  %tmp93.5 = fsub double 0.000000e+00, %tmp107.4
  %tmp95.5 = fmul double %x, %tmp93.5
  %tmp97.5 = fmul double %x, %tmp95.5
  %tmp107.5 = fdiv double %tmp97.5, 1.320000e+02
  %tmp111.5 = fadd double %tmp111.4, %tmp107.5
  %tmp93.6 = fsub double 0.000000e+00, %tmp107.5
  %tmp95.6 = fmul double %x, %tmp93.6
  %tmp97.6 = fmul double %x, %tmp95.6
  %tmp107.6 = fdiv double %tmp97.6, 1.820000e+02
  %tmp111.6 = fadd double %tmp111.5, %tmp107.6
  %tmp93.7 = fsub double 0.000000e+00, %tmp107.6
  %tmp95.7 = fmul double %x, %tmp93.7
  %tmp97.7 = fmul double %x, %tmp95.7
  %tmp107.7 = fdiv double %tmp97.7, 2.400000e+02
  %tmp111.7 = fadd double %tmp111.6, %tmp107.7
  %tmp93.8 = fsub double 0.000000e+00, %tmp107.7
  %tmp95.8 = fmul double %x, %tmp93.8
  %tmp97.8 = fmul double %x, %tmp95.8
  %tmp107.8 = fdiv double %tmp97.8, 3.060000e+02
  %tmp111.8 = fadd double %tmp111.7, %tmp107.8
  ret double %tmp111.8
}

define i32 @fact(i32 %n) {
entry:
  %tmp117 = icmp slt i32 %n, 0
  br i1 %tmp117, label %then_3, label %endif_3

then_3:                                           ; preds = %entry
  %0 = call i32 (ptr, ...) @printf(ptr @.strStr, ptr @.str3)
  ret i32 0

1:                                                ; No predecessors!
  br label %endif_3

endif_3:                                          ; preds = %1, %entry
  %result = alloca i32, align 4
  store i32 1, ptr %result, align 4
  %i = alloca i32, align 4
  store i32 1, ptr %i, align 4
  %tmp1221 = load i32, ptr %i, align 4
  %tmp1242 = icmp sle i32 %tmp1221, %n
  %tmp1303 = load i32, ptr %result, align 4
  br i1 %tmp1242, label %while_body_13.lr.ph, label %while_end_14

while_body_13.lr.ph:                              ; preds = %endif_3
  br label %while_body_13

while_body_13:                                    ; preds = %while_body_13.lr.ph, %while_body_13
  %tmp1305 = phi i32 [ %tmp1303, %while_body_13.lr.ph ], [ %tmp130, %while_body_13 ]
  %tmp1224 = phi i32 [ %tmp1221, %while_body_13.lr.ph ], [ %tmp122, %while_body_13 ]
  %tmp127 = mul i32 %tmp1305, %tmp1224
  store i32 %tmp127, ptr %result, align 4
  %tmp128 = load i32, ptr %i, align 4
  %tmp129 = add i32 %tmp128, 1
  store i32 %tmp129, ptr %i, align 4
  %tmp122 = load i32, ptr %i, align 4
  %tmp124 = icmp sle i32 %tmp122, %n
  %tmp130 = load i32, ptr %result, align 4
  br i1 %tmp124, label %while_body_13, label %while_cond_12.while_end_14_crit_edge

while_cond_12.while_end_14_crit_edge:             ; preds = %while_body_13
  %split = phi i32 [ %tmp130, %while_body_13 ]
  br label %while_end_14

while_end_14:                                     ; preds = %while_cond_12.while_end_14_crit_edge, %endif_3
  %tmp130.lcssa = phi i32 [ %split, %while_cond_12.while_end_14_crit_edge ], [ %tmp1303, %endif_3 ]
  ret i32 %tmp130.lcssa
}

define i32 @factorial(i32 %n) {
entry:
  %tmp133 = icmp slt i32 %n, 0
  br i1 %tmp133, label %then_4, label %endif_4

then_4:                                           ; preds = %entry
  %0 = call i32 (ptr, ...) @printf(ptr @.strStr, ptr @.str3)
  ret i32 0

1:                                                ; No predecessors!
  br label %endif_4

endif_4:                                          ; preds = %1, %entry
  %tmp138 = icmp eq i32 %n, 0
  br i1 %tmp138, label %then_5, label %endif_5

then_5:                                           ; preds = %endif_4
  ret i32 1

2:                                                ; No predecessors!
  br label %endif_5

endif_5:                                          ; preds = %2, %endif_4
  %tmp143 = sub i32 %n, 1
  %tmp144 = call i32 @factorial(i32 %tmp143)
  %tmp145 = mul i32 %tmp144, %n
  ret i32 %tmp145
}

declare i32 @printf(ptr, ...)

declare i32 @getchar()

declare void @printString(ptr)

declare ptr @malloc(i64)

declare void @setString(ptr)

declare ptr @createString(ptr)

declare i1 @strcmp_eq(ptr, ptr)

declare i1 @strcmp_neq(ptr, ptr)

define double @somar(double %x, double %b) {
entry:
  %tmp148 = fadd double %x, %b
  ret double %tmp148
}

define i32 @main() {
  %tmp156 = call double @somar(i32 3, i32 4)
  %1 = call i32 (ptr, ...) @printf(ptr @.strDouble, double %tmp156)
  %tmp160 = call double @sum(double 1.000000e+01, double 3.000000e+00)
  %2 = call i32 (ptr, ...) @printf(ptr @.strDouble, double %tmp160)
  %tmp163 = call double @sub(double 1.000000e+01, double 3.000000e+00)
  %3 = call i32 (ptr, ...) @printf(ptr @.strDouble, double %tmp163)
  %tmp166 = call double @mul(double 1.000000e+01, double 3.000000e+00)
  %4 = call i32 (ptr, ...) @printf(ptr @.strDouble, double %tmp166)
  %tmp169 = call double @div(double 1.000000e+01, double 3.000000e+00)
  %5 = call i32 (ptr, ...) @printf(ptr @.strDouble, double %tmp169)
  %tmp172 = call double @pow(double 1.000000e+01, i32 3)
  %6 = call i32 (ptr, ...) @printf(ptr @.strDouble, double %tmp172)
  %tmp174 = call i32 @factorial(i32 5)
  %7 = call i32 (ptr, ...) @printf(ptr @.strInt, i32 %tmp174)
  %tmp176 = call double @sqrt(double 1.000000e+01)
  %8 = call i32 (ptr, ...) @printf(ptr @.strDouble, double 0x40094C583ADA5B53)
  %tmp178 = call double @sin(double 1.000000e+01)
  %9 = call i32 (ptr, ...) @printf(ptr @.strDouble, double 0xBFE1689EF5F34F52)
  %tmp180 = call double @cos(double 1.000000e+01)
  %10 = call i32 (ptr, ...) @printf(ptr @.strDouble, double 0xBFEAD9AC890C6B1F)
  %11 = call i32 (ptr, ...) @printf(ptr @.strStr, ptr @.str4)
  %tmp184 = call double @div(double 1.000000e+01, i32 0)
  %12 = call i32 (ptr, ...) @printf(ptr @.strDouble, double %tmp184)
  %13 = call i32 (ptr, ...) @printf(ptr @.strStr, ptr @.str5)
  %tmp187 = call i32 @factorial(i32 -3)
  %14 = call i32 (ptr, ...) @printf(ptr @.strInt, i32 %tmp187)
  %15 = call i32 (ptr, ...) @printf(ptr @.strStr, ptr @.str6)
  %tmp190 = call double @sqrt(i32 -9)
  %16 = call i32 (ptr, ...) @printf(ptr @.strDouble, double %tmp190)
  %17 = call i32 (ptr, ...) @printf(ptr @.strStr, ptr @.str7)
  %18 = call i32 @getchar()
  ret i32 0
}
