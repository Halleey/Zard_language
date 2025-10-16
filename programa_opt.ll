; ModuleID = 'programa.ll'
source_filename = "programa.ll"

%String = type { ptr, i64 }

@.strInt = private constant [4 x i8] c"%d\0A\00"
@.strDouble = private constant [4 x i8] c"%f\0A\00"
@.strStr = private constant [4 x i8] c"%s\0A\00"
@.str0 = private constant [24 x i8] c"Erro: divisao por zero!\00"
@.str1 = private constant [53 x i8] c"Aviso: expoente negativo nao suportado, retornando 0\00"
@.str2 = private constant [31 x i8] c"Erro: sqrt de numero negativo!\00"
@.str3 = private constant [36 x i8] c"Erro: factorial de numero negativo!\00"
@.str4 = private constant [11 x i8] c"hello guys\00"
@.str5 = private constant [1 x i8] zeroinitializer
@.str6 = private constant [7 x i8] c"halley\00"
@.str7 = private constant [6 x i8] c"misty\00"
@.str8 = private constant [23 x i8] c"pos 0 e igual a halley\00"
@.str9 = private constant [14 x i8] c"nao era igual\00"

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
  %t133 = icmp eq i32 %n, 0
  br i1 %t133, label %then_4, label %endif_4

then_4:                                           ; preds = %entry
  ret i32 1

0:                                                ; No predecessors!
  br label %endif_4

endif_4:                                          ; preds = %0, %entry
  %t138 = add i32 %n, -1
  %t139 = call i32 @math_factorial(i32 %t138)
  %t140 = mul i32 %n, %t139
  ret i32 %t140
}

declare i32 @printf(ptr, ...)

declare i32 @getchar()

declare void @printString(ptr)

declare ptr @malloc(i64)

declare void @setString(ptr, ptr)

declare ptr @createString(ptr)

declare ptr @arraylist_create(i64)

declare void @clearList(ptr)

declare void @freeList(ptr)

declare i1 @strcmp_eq(ptr, ptr)

declare i1 @strcmp_neq(ptr, ptr)

declare i32 @inputInt(ptr)

declare double @inputDouble(ptr)

declare i1 @inputBool(ptr)

declare ptr @inputString(ptr)

declare void @arraylist_add_string(ptr, ptr)

declare void @arraylist_addAll_string(ptr, ptr, i64)

declare void @arraylist_print_string(ptr)

declare void @arraylist_add_String(ptr, ptr)

declare void @arraylist_addAll_String(ptr, ptr, i64)

declare void @removeItem(ptr, i64)

declare ptr @getItem(ptr, i64)

define void @lists(ptr %list) {
entry:
  %t143 = call ptr @createString(ptr nonnull @.str4)
  call void @arraylist_add_String(ptr %list, ptr %t143)
  ret void
}

define i32 @main() {
  %t145 = call ptr @arraylist_create(i64 4)
  call void @lists(ptr %t145)
  call void @arraylist_print_string(ptr %t145)
  %t150 = call i32 @inputInt(ptr null)
  %1 = call i32 (ptr, ...) @printf(ptr noundef nonnull dereferenceable(1) @.strInt, i32 %t150)
  %t152 = call ptr @malloc(i64 16)
  store ptr @.str6, ptr %t152, align 8
  %t156 = getelementptr inbounds %String, ptr %t152, i64 0, i32 1
  store i64 6, ptr %t156, align 4
  %t157 = call ptr @malloc(i64 16)
  store ptr @.str7, ptr %t157, align 8
  %t161 = getelementptr inbounds %String, ptr %t157, i64 0, i32 1
  store i64 5, ptr %t161, align 4
  call void @printString(ptr nonnull %t157)
  %t163 = call ptr @arraylist_create(i64 4)
  call void @arraylist_add_String(ptr %t163, ptr %t152)
  call void @arraylist_add_String(ptr %t163, ptr nonnull %t157)
  %t171 = call ptr @getItem(ptr %t163, i64 0)
  %t173 = call ptr @createString(ptr nonnull @.str6)
  %t175 = call ptr @createString(ptr %t171)
  %t176 = call i1 @strcmp_eq(ptr %t175, ptr %t173)
  br i1 %t176, label %then_5, label %else_5

then_5:                                           ; preds = %0
  %puts3 = call i32 @puts(ptr nonnull dereferenceable(1) @.str8)
  br label %endif_5

else_5:                                           ; preds = %0
  %puts = call i32 @puts(ptr nonnull dereferenceable(1) @.str9)
  br label %endif_5

endif_5:                                          ; preds = %else_5, %then_5
  call void @freeList(ptr %t145)
  call void @freeList(ptr %t163)
  %2 = call i32 @getchar()
  ret i32 0
}

; Function Attrs: nofree nounwind
declare noundef i32 @puts(ptr nocapture noundef readonly) #0

attributes #0 = { nofree nounwind }
