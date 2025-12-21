; ModuleID = 'programa.ll'
source_filename = "programa.ll"

@.strChar = private constant [3 x i8] c"%c\00"
@.strTrue = private constant [6 x i8] c"true\0A\00"
@.strFalse = private constant [7 x i8] c"false\0A\00"
@.strInt = private constant [4 x i8] c"%d\0A\00"
@.strInt_noNL = private constant [3 x i8] c"%d\00"
@.strDouble = private constant [4 x i8] c"%f\0A\00"
@.strDouble_noNL = private constant [3 x i8] c"%f\00"
@.strFloat = private constant [4 x i8] c"%f\0A\00"
@.strFloat_noNL = private constant [3 x i8] c"%f\00"
@.strStr = private constant [4 x i8] c"%s\0A\00"
@.strStr_noNL = private constant [3 x i8] c"%s\00"
@.strEmpty = private constant [1 x i8] zeroinitializer
@.str0 = private constant [2 x i8] c" \00"
@.str1 = private constant [1 x i8] zeroinitializer
@.str2 = private constant [8 x i8] c"Matriz:\00"
@.str3 = private constant [6 x i8] c"teste\00"
@.str4 = private constant [3 x i8] c"ok\00"
@.str5 = private constant [12 x i8] c"Soma total:\00"

declare i32 @printf(ptr, ...)

declare i32 @getchar()

declare void @printString(ptr)

declare ptr @malloc(i64)

declare void @setString(ptr)

declare ptr @createString(ptr)

declare i1 @strcmp_eq(ptr, ptr)

declare i1 @strcmp_neq(ptr, ptr)

declare ptr @arraylist_create(i64)

declare void @clearList(ptr)

declare void @freeList(ptr)

declare void @arraylist_add_ptr(ptr, ptr)

declare i32 @length(ptr)

declare ptr @arraylist_get_ptr(ptr, i64)

declare void @arraylist_print_ptr(ptr, ptr)

declare void @removeItem(ptr, i64)

declare ptr @arraylist_create_int(i64)

declare void @arraylist_add_int(ptr, i32)

declare void @arraylist_addAll_int(ptr, ptr, i64)

declare void @arraylist_print_int(ptr)

declare void @arraylist_clear_int(ptr)

declare void @arraylist_free_int(ptr)

declare i32 @arraylist_get_int(ptr, i64, ptr)

declare void @arraylist_remove_int(ptr, i64)

declare i32 @arraylist_size_int(ptr)

define void @print_Row(ptr %p) {
entry:
  %v0 = load ptr, ptr %p, align 8
  call void @arraylist_print_int(ptr %v0)
  ret void
}

define void @print_Matrix(ptr %p) {
entry:
  ret void
}

define ptr @Matrix_adRow(ptr %s, i32 %a, i32 %b, i32 %c) {
entry:
  %tmp0 = call ptr @malloc(i64 8)
  %tmp2 = call ptr @arraylist_create_int(i64 10)
  store ptr %tmp2, ptr %tmp0, align 8
  call void @arraylist_add_int(ptr %tmp2, i32 %a)
  %tmp18 = load ptr, ptr %tmp0, align 8
  call void @arraylist_add_int(ptr %tmp18, i32 %b)
  %tmp26 = load ptr, ptr %tmp0, align 8
  call void @arraylist_add_int(ptr %tmp26, i32 %c)
  %tmp30 = load ptr, ptr %s, align 8
  call void @arraylist_add_ptr(ptr %tmp30, ptr %tmp0)
  ret ptr %s

0:                                                ; No predecessors!
  ret ptr %s
}

define ptr @Matrix_printMatrix(ptr %s) {
entry:
  %tmp396 = load ptr, ptr %s, align 8
  %tmp417 = call i32 @length(ptr %tmp396)
  %tmp428 = icmp slt i32 0, %tmp417
  br i1 %tmp428, label %while_body_1.lr.ph, label %while_end_2

while_body_1.lr.ph:                               ; preds = %entry
  br label %while_body_1

while_body_1:                                     ; preds = %while_body_1.lr.ph, %while_end_5
  %i.09 = phi i32 [ 0, %while_body_1.lr.ph ], [ %tmp78, %while_end_5 ]
  %r_1 = alloca ptr, align 8
  %tmp45 = load ptr, ptr %s, align 8
  %tmp47 = zext i32 %i.09 to i64
  %tmp48 = call ptr @arraylist_get_ptr(ptr %tmp45, i64 %tmp47)
  store ptr %tmp48, ptr %r_1, align 8
  %j = alloca i32, align 4
  store i32 0, ptr %j, align 4
  %tmp511 = load i32, ptr %j, align 4
  %tmp552 = load ptr, ptr %r_1, align 8
  %tmp573 = load ptr, ptr %tmp552, align 8
  %tmp584 = call i32 @arraylist_size_int(ptr %tmp573)
  %tmp595 = icmp slt i32 %tmp511, %tmp584
  br i1 %tmp595, label %while_body_4.lr.ph, label %while_end_5

while_body_4.lr.ph:                               ; preds = %while_body_1
  br label %while_body_4

while_body_4:                                     ; preds = %while_body_4.lr.ph, %while_body_4
  %tmp63 = load ptr, ptr %r_1, align 8
  %tmp65 = load ptr, ptr %tmp63, align 8
  %tmp66 = load i32, ptr %j, align 4
  %tmp67 = zext i32 %tmp66 to i64
  %tmp68 = alloca i32, align 4
  %tmp69 = call i32 @arraylist_get_int(ptr %tmp65, i64 %tmp67, ptr %tmp68)
  %tmp70 = load i32, ptr %tmp68, align 4
  %0 = call i32 (ptr, ...) @printf(ptr @.strInt_noNL, i32 %tmp70)
  %1 = call i32 (ptr, ...) @printf(ptr @.strStr_noNL, ptr @.str0)
  %tmp72 = load i32, ptr %j, align 4
  %tmp74 = add i32 %tmp72, 1
  store i32 %tmp74, ptr %j, align 4
  %tmp51 = load i32, ptr %j, align 4
  %tmp55 = load ptr, ptr %r_1, align 8
  %tmp57 = load ptr, ptr %tmp55, align 8
  %tmp58 = call i32 @arraylist_size_int(ptr %tmp57)
  %tmp59 = icmp slt i32 %tmp51, %tmp58
  br i1 %tmp59, label %while_body_4, label %while_cond_3.while_end_5_crit_edge

while_cond_3.while_end_5_crit_edge:               ; preds = %while_body_4
  br label %while_end_5

while_end_5:                                      ; preds = %while_cond_3.while_end_5_crit_edge, %while_body_1
  %2 = call i32 (ptr, ...) @printf(ptr @.strStr, ptr @.str1)
  %tmp78 = add i32 %i.09, 1
  %tmp39 = load ptr, ptr %s, align 8
  %tmp41 = call i32 @length(ptr %tmp39)
  %tmp42 = icmp slt i32 %tmp78, %tmp41
  br i1 %tmp42, label %while_body_1, label %while_cond_0.while_end_2_crit_edge

while_cond_0.while_end_2_crit_edge:               ; preds = %while_end_5
  br label %while_end_2

while_end_2:                                      ; preds = %while_cond_0.while_end_2_crit_edge, %entry
  ret ptr %s

3:                                                ; No predecessors!
  ret ptr %s
}

define i32 @Matrix_somar(ptr %s, i32 %b, i32 %c) {
entry:
  %tmp82 = add i32 %c, %b
  ret i32 %tmp82

0:                                                ; No predecessors!
  ret i32 undef
}

define i32 @Matrix_sum(ptr %s) {
entry:
  %tmp887 = load ptr, ptr %s, align 8
  %tmp908 = call i32 @length(ptr %tmp887)
  %tmp919 = icmp slt i32 0, %tmp908
  br i1 %tmp919, label %while_body_7.lr.ph, label %while_end_8

while_body_7.lr.ph:                               ; preds = %entry
  br label %while_body_7

while_body_7:                                     ; preds = %while_body_7.lr.ph, %while_end_11
  %i_1.011 = phi i32 [ 0, %while_body_7.lr.ph ], [ %tmp127, %while_end_11 ]
  %total.010 = phi i32 [ 0, %while_body_7.lr.ph ], [ %total.1.lcssa, %while_end_11 ]
  %r_2 = alloca ptr, align 8
  %tmp94 = load ptr, ptr %s, align 8
  %tmp96 = zext i32 %i_1.011 to i64
  %tmp97 = call ptr @arraylist_get_ptr(ptr %tmp94, i64 %tmp96)
  store ptr %tmp97, ptr %r_2, align 8
  %j_1 = alloca i32, align 4
  store i32 0, ptr %j_1, align 4
  %tmp1001 = load i32, ptr %j_1, align 4
  %tmp1042 = load ptr, ptr %r_2, align 8
  %tmp1063 = load ptr, ptr %tmp1042, align 8
  %tmp1074 = call i32 @arraylist_size_int(ptr %tmp1063)
  %tmp1085 = icmp slt i32 %tmp1001, %tmp1074
  br i1 %tmp1085, label %while_body_10.lr.ph, label %while_end_11

while_body_10.lr.ph:                              ; preds = %while_body_7
  br label %while_body_10

while_body_10:                                    ; preds = %while_body_10.lr.ph, %while_body_10
  %total.16 = phi i32 [ %total.010, %while_body_10.lr.ph ], [ %tmp121, %while_body_10 ]
  %tmp113 = load ptr, ptr %r_2, align 8
  %tmp115 = load ptr, ptr %tmp113, align 8
  %tmp116 = load i32, ptr %j_1, align 4
  %tmp117 = zext i32 %tmp116 to i64
  %tmp118 = alloca i32, align 4
  %tmp119 = call i32 @arraylist_get_int(ptr %tmp115, i64 %tmp117, ptr %tmp118)
  %tmp120 = load i32, ptr %tmp118, align 4
  %tmp121 = add i32 %tmp120, %total.16
  %tmp122 = load i32, ptr %j_1, align 4
  %tmp124 = add i32 %tmp122, 1
  store i32 %tmp124, ptr %j_1, align 4
  %tmp100 = load i32, ptr %j_1, align 4
  %tmp104 = load ptr, ptr %r_2, align 8
  %tmp106 = load ptr, ptr %tmp104, align 8
  %tmp107 = call i32 @arraylist_size_int(ptr %tmp106)
  %tmp108 = icmp slt i32 %tmp100, %tmp107
  br i1 %tmp108, label %while_body_10, label %while_cond_9.while_end_11_crit_edge

while_cond_9.while_end_11_crit_edge:              ; preds = %while_body_10
  %split = phi i32 [ %tmp121, %while_body_10 ]
  br label %while_end_11

while_end_11:                                     ; preds = %while_cond_9.while_end_11_crit_edge, %while_body_7
  %total.1.lcssa = phi i32 [ %split, %while_cond_9.while_end_11_crit_edge ], [ %total.010, %while_body_7 ]
  %tmp127 = add i32 %i_1.011, 1
  %tmp88 = load ptr, ptr %s, align 8
  %tmp90 = call i32 @length(ptr %tmp88)
  %tmp91 = icmp slt i32 %tmp127, %tmp90
  br i1 %tmp91, label %while_body_7, label %while_cond_6.while_end_8_crit_edge

while_cond_6.while_end_8_crit_edge:               ; preds = %while_end_11
  %split12 = phi i32 [ %total.1.lcssa, %while_end_11 ]
  br label %while_end_8

while_end_8:                                      ; preds = %while_cond_6.while_end_8_crit_edge, %entry
  %total.0.lcssa = phi i32 [ %split12, %while_cond_6.while_end_8_crit_edge ], [ 0, %entry ]
  ret i32 %total.0.lcssa

0:                                                ; No predecessors!
  ret i32 undef
}

define i32 @main() {
  %tmp129 = call ptr @malloc(i64 8)
  %tmp131 = call ptr @arraylist_create(i64 10)
  store ptr %tmp131, ptr %tmp129, align 8
  call void @Matrix_adRow(ptr %tmp129, i32 1, i32 2, i32 3)
  call void @Matrix_adRow(ptr %tmp129, i32 4, i32 5, i32 6)
  call void @Matrix_adRow(ptr %tmp129, i32 7, i32 8, i32 9)
  %tmp149 = call i32 @Matrix_somar(ptr %tmp129, i32 3, i32 5)
  %1 = call i32 (ptr, ...) @printf(ptr @.strInt, i32 %tmp149)
  %2 = call i32 (ptr, ...) @printf(ptr @.strStr, ptr @.str2)
  call void @Matrix_printMatrix(ptr %tmp129)
  %3 = call i32 (ptr, ...) @printf(ptr @.strStr_noNL, ptr @.str3)
  %4 = call i32 (ptr, ...) @printf(ptr @.strStr_noNL, ptr @.str4)
  %5 = call i32 (ptr, ...) @printf(ptr @.strStr, ptr @.str5)
  %tmp156 = call i32 @Matrix_sum(ptr %tmp129)
  %6 = call i32 (ptr, ...) @printf(ptr @.strInt, i32 %tmp156)
  %7 = call i32 @getchar()
  ret i32 0
}
