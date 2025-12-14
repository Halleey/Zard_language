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
@.str0 = private constant [4 x i8] c"---\00"
@.str1 = private constant [8 x i8] c"Matriz:\00"
@.str2 = private constant [12 x i8] c"Soma total:\00"

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
  %tmp2 = call ptr @malloc(i64 8)
  %tmp4 = call ptr @arraylist_create_int(i64 10)
  store ptr %tmp4, ptr %tmp2, align 8
  call void @arraylist_add_int(ptr %tmp4, i32 %a)
  %tmp20 = load ptr, ptr %tmp2, align 8
  call void @arraylist_add_int(ptr %tmp20, i32 %b)
  %tmp28 = load ptr, ptr %tmp2, align 8
  call void @arraylist_add_int(ptr %tmp28, i32 %c)
  %tmp32 = load ptr, ptr %s, align 8
  call void @arraylist_add_ptr(ptr %tmp32, ptr %tmp2)
  ret ptr %s

0:                                                ; No predecessors!
  ret ptr %s
}

define ptr @Matrix_printMatrix(ptr %s) {
entry:
  %tmp416 = load ptr, ptr %s, align 8
  %tmp437 = call i32 @length(ptr %tmp416)
  %tmp448 = icmp slt i32 0, %tmp437
  br i1 %tmp448, label %while_body_1.lr.ph, label %while_end_2

while_body_1.lr.ph:                               ; preds = %entry
  br label %while_body_1

while_body_1:                                     ; preds = %while_body_1.lr.ph, %while_end_5
  %i.09 = phi i32 [ 0, %while_body_1.lr.ph ], [ %tmp79, %while_end_5 ]
  %r_1 = alloca ptr, align 8
  %tmp47 = load ptr, ptr %s, align 8
  %tmp49 = zext i32 %i.09 to i64
  %tmp50 = call ptr @arraylist_get_ptr(ptr %tmp47, i64 %tmp49)
  store ptr %tmp50, ptr %r_1, align 8
  %j = alloca i32, align 4
  store i32 0, ptr %j, align 4
  %tmp531 = load i32, ptr %j, align 4
  %tmp572 = load ptr, ptr %r_1, align 8
  %tmp593 = load ptr, ptr %tmp572, align 8
  %tmp604 = call i32 @arraylist_size_int(ptr %tmp593)
  %tmp615 = icmp slt i32 %tmp531, %tmp604
  br i1 %tmp615, label %while_body_4.lr.ph, label %while_end_5

while_body_4.lr.ph:                               ; preds = %while_body_1
  br label %while_body_4

while_body_4:                                     ; preds = %while_body_4.lr.ph, %while_body_4
  %tmp65 = load ptr, ptr %r_1, align 8
  %tmp67 = load ptr, ptr %tmp65, align 8
  %tmp68 = load i32, ptr %j, align 4
  %tmp69 = zext i32 %tmp68 to i64
  %tmp70 = alloca i32, align 4
  %tmp71 = call i32 @arraylist_get_int(ptr %tmp67, i64 %tmp69, ptr %tmp70)
  %tmp72 = load i32, ptr %tmp70, align 4
  %0 = call i32 (ptr, ...) @printf(ptr @.strInt, i32 %tmp72)
  %tmp73 = load i32, ptr %j, align 4
  %tmp75 = add i32 %tmp73, 1
  store i32 %tmp75, ptr %j, align 4
  %tmp53 = load i32, ptr %j, align 4
  %tmp57 = load ptr, ptr %r_1, align 8
  %tmp59 = load ptr, ptr %tmp57, align 8
  %tmp60 = call i32 @arraylist_size_int(ptr %tmp59)
  %tmp61 = icmp slt i32 %tmp53, %tmp60
  br i1 %tmp61, label %while_body_4, label %while_cond_3.while_end_5_crit_edge

while_cond_3.while_end_5_crit_edge:               ; preds = %while_body_4
  br label %while_end_5

while_end_5:                                      ; preds = %while_cond_3.while_end_5_crit_edge, %while_body_1
  %1 = call i32 (ptr, ...) @printf(ptr @.strStr, ptr @.str0)
  %tmp79 = add i32 %i.09, 1
  %tmp41 = load ptr, ptr %s, align 8
  %tmp43 = call i32 @length(ptr %tmp41)
  %tmp44 = icmp slt i32 %tmp79, %tmp43
  br i1 %tmp44, label %while_body_1, label %while_cond_0.while_end_2_crit_edge

while_cond_0.while_end_2_crit_edge:               ; preds = %while_end_5
  br label %while_end_2

while_end_2:                                      ; preds = %while_cond_0.while_end_2_crit_edge, %entry
  ret ptr %s

2:                                                ; No predecessors!
  ret ptr %s
}

define i32 @Matrix_somar(ptr %s, i32 %b, i32 %c) {
entry:
  %tmp83 = add i32 %c, %b
  ret i32 %tmp83

0:                                                ; No predecessors!
  ret i32 undef
}

define i32 @Matrix_sum(ptr %s) {
entry:
  %tmp88 = load ptr, ptr %s, align 8
  %tmp91 = call ptr @arraylist_get_ptr(ptr %tmp88, i64 0)
  %tmp967 = load ptr, ptr %s, align 8
  %tmp988 = call i32 @length(ptr %tmp967)
  %tmp999 = icmp slt i32 0, %tmp988
  br i1 %tmp999, label %while_body_7.lr.ph, label %while_end_8

while_body_7.lr.ph:                               ; preds = %entry
  br label %while_body_7

while_body_7:                                     ; preds = %while_body_7.lr.ph, %while_end_11
  %i_1.011 = phi i32 [ 0, %while_body_7.lr.ph ], [ %tmp134, %while_end_11 ]
  %total.010 = phi i32 [ 0, %while_body_7.lr.ph ], [ %total.1.lcssa, %while_end_11 ]
  %r_3 = alloca ptr, align 8
  %tmp102 = load ptr, ptr %s, align 8
  %tmp104 = zext i32 %i_1.011 to i64
  %tmp105 = call ptr @arraylist_get_ptr(ptr %tmp102, i64 %tmp104)
  store ptr %tmp105, ptr %r_3, align 8
  %j_1 = alloca i32, align 4
  store i32 0, ptr %j_1, align 4
  %tmp1081 = load i32, ptr %j_1, align 4
  %tmp1122 = load ptr, ptr %r_3, align 8
  %tmp1143 = load ptr, ptr %tmp1122, align 8
  %tmp1154 = call i32 @arraylist_size_int(ptr %tmp1143)
  %tmp1165 = icmp slt i32 %tmp1081, %tmp1154
  br i1 %tmp1165, label %while_body_10.lr.ph, label %while_end_11

while_body_10.lr.ph:                              ; preds = %while_body_7
  br label %while_body_10

while_body_10:                                    ; preds = %while_body_10.lr.ph, %while_body_10
  %total.16 = phi i32 [ %total.010, %while_body_10.lr.ph ], [ %tmp129, %while_body_10 ]
  %tmp121 = load ptr, ptr %r_3, align 8
  %tmp123 = load ptr, ptr %tmp121, align 8
  %tmp124 = load i32, ptr %j_1, align 4
  %tmp125 = zext i32 %tmp124 to i64
  %tmp126 = alloca i32, align 4
  %tmp127 = call i32 @arraylist_get_int(ptr %tmp123, i64 %tmp125, ptr %tmp126)
  %tmp128 = load i32, ptr %tmp126, align 4
  %tmp129 = add i32 %tmp128, %total.16
  %tmp130 = load i32, ptr %j_1, align 4
  %tmp132 = add i32 %tmp130, 1
  store i32 %tmp132, ptr %j_1, align 4
  %tmp108 = load i32, ptr %j_1, align 4
  %tmp112 = load ptr, ptr %r_3, align 8
  %tmp114 = load ptr, ptr %tmp112, align 8
  %tmp115 = call i32 @arraylist_size_int(ptr %tmp114)
  %tmp116 = icmp slt i32 %tmp108, %tmp115
  br i1 %tmp116, label %while_body_10, label %while_cond_9.while_end_11_crit_edge

while_cond_9.while_end_11_crit_edge:              ; preds = %while_body_10
  %split = phi i32 [ %tmp129, %while_body_10 ]
  br label %while_end_11

while_end_11:                                     ; preds = %while_cond_9.while_end_11_crit_edge, %while_body_7
  %total.1.lcssa = phi i32 [ %split, %while_cond_9.while_end_11_crit_edge ], [ %total.010, %while_body_7 ]
  %tmp134 = add i32 %i_1.011, 1
  %tmp96 = load ptr, ptr %s, align 8
  %tmp98 = call i32 @length(ptr %tmp96)
  %tmp99 = icmp slt i32 %tmp134, %tmp98
  br i1 %tmp99, label %while_body_7, label %while_cond_6.while_end_8_crit_edge

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
  %tmp138 = call ptr @malloc(i64 8)
  %tmp140 = call ptr @arraylist_create(i64 10)
  store ptr %tmp140, ptr %tmp138, align 8
  call void @Matrix_adRow(ptr %tmp138, i32 1, i32 2, i32 3)
  call void @Matrix_adRow(ptr %tmp138, i32 4, i32 5, i32 6)
  call void @Matrix_adRow(ptr %tmp138, i32 7, i32 8, i32 9)
  %1 = call i32 (ptr, ...) @printf(ptr @.strStr, ptr @.str1)
  call void @Matrix_printMatrix(ptr %tmp138)
  %2 = call i32 (ptr, ...) @printf(ptr @.strStr, ptr @.str2)
  %tmp158 = call i32 @Matrix_sum(ptr %tmp138)
  %3 = call i32 (ptr, ...) @printf(ptr @.strInt, i32 %tmp158)
  %4 = call i32 @getchar()
  ret i32 0
}
