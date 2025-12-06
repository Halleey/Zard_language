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

declare ptr @arraylist_create_int(i64)

declare void @arraylist_add_int(ptr, i32)

declare void @arraylist_addAll_int(ptr, ptr, i64)

declare void @arraylist_print_int(ptr)

declare void @arraylist_clear_int(ptr)

declare void @arraylist_free_int(ptr)

declare i32 @arraylist_get_int(ptr, i64, ptr)

declare void @arraylist_remove_int(ptr, i64)

declare i32 @arraylist_size_int(ptr)

define void @bubbleSort(ptr %arr) {
entry:
  %tmp2 = call i32 @arraylist_size_int(ptr %arr)
  br label %for_init_0

for_init_0:                                       ; preds = %entry
  %i = alloca i32, align 4
  store i32 0, ptr %i, align 4
  %tmp421 = load i32, ptr %i, align 4
  %tmp7 = sub i32 %tmp2, 1
  %tmp822 = icmp slt i32 %tmp421, %tmp7
  br i1 %tmp822, label %for_body_2.lr.ph, label %for_end_4

for_body_2.lr.ph:                                 ; preds = %for_init_0
  br label %for_body_2

for_body_2:                                       ; preds = %for_body_2.lr.ph, %for_inc_3
  br label %for_init_5

for_init_5:                                       ; preds = %for_body_2
  %j = alloca i32, align 4
  store i32 0, ptr %j, align 4
  %tmp1014 = load i32, ptr %j, align 4
  %tmp1215 = load i32, ptr %i, align 4
  %tmp12.neg16 = sub i32 0, %tmp1215
  %tmp13 = add i32 %tmp2, -1
  %tmp1517 = add i32 %tmp13, %tmp12.neg16
  %tmp1618 = icmp slt i32 %tmp1014, %tmp1517
  br i1 %tmp1618, label %for_body_7.lr.ph, label %for_end_9

for_body_7.lr.ph:                                 ; preds = %for_init_5
  br label %for_body_7

for_body_7:                                       ; preds = %for_body_7.lr.ph, %for_inc_8
  %tmp1019 = phi i32 [ %tmp1014, %for_body_7.lr.ph ], [ %tmp10, %for_inc_8 ]
  %a = alloca i32, align 4
  %tmp20 = zext i32 %tmp1019 to i64
  %tmp21 = alloca i32, align 4
  %tmp22 = call i32 @arraylist_get_int(ptr %arr, i64 %tmp20, ptr %tmp21)
  %tmp23 = load i32, ptr %tmp21, align 4
  store i32 %tmp23, ptr %a, align 4
  %b = alloca i32, align 4
  %tmp26 = load i32, ptr %j, align 4
  %tmp28 = add i32 %tmp26, 1
  %tmp29 = zext i32 %tmp28 to i64
  %tmp30 = alloca i32, align 4
  %tmp31 = call i32 @arraylist_get_int(ptr %arr, i64 %tmp29, ptr %tmp30)
  %tmp32 = load i32, ptr %tmp30, align 4
  store i32 %tmp32, ptr %b, align 4
  %tmp33 = load i32, ptr %a, align 4
  %tmp35 = icmp sgt i32 %tmp33, %tmp32
  br i1 %tmp35, label %then_0, label %endif_0

then_0:                                           ; preds = %for_body_7
  %tmp38 = load i32, ptr %j, align 4
  %tmp39 = zext i32 %tmp38 to i64
  call void @arraylist_remove_int(ptr %arr, i64 %tmp39)
  %tmp42 = load i32, ptr %j, align 4
  %tmp43 = zext i32 %tmp42 to i64
  call void @arraylist_remove_int(ptr %arr, i64 %tmp43)
  %temp = alloca ptr, align 8
  %tmp44 = call ptr @arraylist_create_int(i64 4)
  store ptr %tmp44, ptr %temp, align 8
  br label %for_init_10

for_init_10:                                      ; preds = %then_0
  %k = alloca i32, align 4
  store i32 0, ptr %k, align 4
  %tmp461 = load i32, ptr %k, align 4
  %tmp472 = load i32, ptr %j, align 4
  %tmp483 = icmp slt i32 %tmp461, %tmp472
  %tmp694 = load ptr, ptr %temp, align 8
  br i1 %tmp483, label %for_body_12.lr.ph, label %for_end_14

for_body_12.lr.ph:                                ; preds = %for_init_10
  br label %for_body_12

for_body_12:                                      ; preds = %for_body_12.lr.ph, %for_inc_13
  %tmp696 = phi ptr [ %tmp694, %for_body_12.lr.ph ], [ %tmp69, %for_inc_13 ]
  %tmp465 = phi i32 [ %tmp461, %for_body_12.lr.ph ], [ %tmp46, %for_inc_13 ]
  %tmp61 = zext i32 %tmp465 to i64
  %tmp62 = alloca i32, align 4
  %tmp63 = call i32 @arraylist_get_int(ptr %arr, i64 %tmp61, ptr %tmp62)
  %tmp64 = load i32, ptr %tmp62, align 4
  call void @arraylist_add_int(ptr %tmp696, i32 %tmp64)
  br label %for_inc_13

for_inc_13:                                       ; preds = %for_body_12
  %tmp65 = load i32, ptr %k, align 4
  %tmp66 = add i32 %tmp65, 1
  store i32 %tmp66, ptr %k, align 4
  %tmp46 = load i32, ptr %k, align 4
  %tmp47 = load i32, ptr %j, align 4
  %tmp48 = icmp slt i32 %tmp46, %tmp47
  %tmp69 = load ptr, ptr %temp, align 8
  br i1 %tmp48, label %for_body_12, label %for_cond_11.for_end_14_crit_edge

for_cond_11.for_end_14_crit_edge:                 ; preds = %for_inc_13
  %split = phi ptr [ %tmp69, %for_inc_13 ]
  br label %for_end_14

for_end_14:                                       ; preds = %for_cond_11.for_end_14_crit_edge, %for_init_10
  %tmp69.lcssa = phi ptr [ %split, %for_cond_11.for_end_14_crit_edge ], [ %tmp694, %for_init_10 ]
  %tmp70 = load i32, ptr %b, align 4
  call void @arraylist_add_int(ptr %tmp69.lcssa, i32 %tmp70)
  %tmp73 = load ptr, ptr %temp, align 8
  %tmp74 = load i32, ptr %a, align 4
  call void @arraylist_add_int(ptr %tmp73, i32 %tmp74)
  br label %for_init_15

for_init_15:                                      ; preds = %for_end_14
  %k_1 = alloca i32, align 4
  %tmp75 = load i32, ptr %j, align 4
  store i32 %tmp75, ptr %k_1, align 4
  %tmp767 = load i32, ptr %k_1, align 4
  %tmp798 = call i32 @arraylist_size_int(ptr %arr)
  %tmp809 = icmp slt i32 %tmp767, %tmp798
  br i1 %tmp809, label %for_body_17.lr.ph, label %for_end_19

for_body_17.lr.ph:                                ; preds = %for_init_15
  br label %for_body_17

for_body_17:                                      ; preds = %for_body_17.lr.ph, %for_inc_18
  %tmp89 = load ptr, ptr %temp, align 8
  %tmp92 = load i32, ptr %k_1, align 4
  %tmp93 = zext i32 %tmp92 to i64
  %tmp94 = alloca i32, align 4
  %tmp95 = call i32 @arraylist_get_int(ptr %arr, i64 %tmp93, ptr %tmp94)
  %tmp96 = load i32, ptr %tmp94, align 4
  call void @arraylist_add_int(ptr %tmp89, i32 %tmp96)
  br label %for_inc_18

for_inc_18:                                       ; preds = %for_body_17
  %tmp97 = load i32, ptr %k_1, align 4
  %tmp98 = add i32 %tmp97, 1
  store i32 %tmp98, ptr %k_1, align 4
  %tmp76 = load i32, ptr %k_1, align 4
  %tmp79 = call i32 @arraylist_size_int(ptr %arr)
  %tmp80 = icmp slt i32 %tmp76, %tmp79
  br i1 %tmp80, label %for_body_17, label %for_cond_16.for_end_19_crit_edge

for_cond_16.for_end_19_crit_edge:                 ; preds = %for_inc_18
  br label %for_end_19

for_end_19:                                       ; preds = %for_cond_16.for_end_19_crit_edge, %for_init_15
  call void @arraylist_clear_int(ptr %arr)
  br label %for_init_20

for_init_20:                                      ; preds = %for_end_19
  %k_2 = alloca i32, align 4
  store i32 0, ptr %k_2, align 4
  %tmp10210 = load i32, ptr %k_2, align 4
  %tmp10411 = load ptr, ptr %temp, align 8
  %tmp10512 = call i32 @arraylist_size_int(ptr %tmp10411)
  %tmp10613 = icmp slt i32 %tmp10210, %tmp10512
  br i1 %tmp10613, label %for_body_22.lr.ph, label %for_end_24

for_body_22.lr.ph:                                ; preds = %for_init_20
  br label %for_body_22

for_body_22:                                      ; preds = %for_body_22.lr.ph, %for_inc_23
  %tmp117 = load ptr, ptr %temp, align 8
  %tmp118 = load i32, ptr %k_2, align 4
  %tmp119 = zext i32 %tmp118 to i64
  %tmp120 = alloca i32, align 4
  %tmp121 = call i32 @arraylist_get_int(ptr %tmp117, i64 %tmp119, ptr %tmp120)
  %tmp122 = load i32, ptr %tmp120, align 4
  call void @arraylist_add_int(ptr %arr, i32 %tmp122)
  br label %for_inc_23

for_inc_23:                                       ; preds = %for_body_22
  %tmp123 = load i32, ptr %k_2, align 4
  %tmp124 = add i32 %tmp123, 1
  store i32 %tmp124, ptr %k_2, align 4
  %tmp102 = load i32, ptr %k_2, align 4
  %tmp104 = load ptr, ptr %temp, align 8
  %tmp105 = call i32 @arraylist_size_int(ptr %tmp104)
  %tmp106 = icmp slt i32 %tmp102, %tmp105
  br i1 %tmp106, label %for_body_22, label %for_cond_21.for_end_24_crit_edge

for_cond_21.for_end_24_crit_edge:                 ; preds = %for_inc_23
  br label %for_end_24

for_end_24:                                       ; preds = %for_cond_21.for_end_24_crit_edge, %for_init_20
  br label %endif_0

endif_0:                                          ; preds = %for_end_24, %for_body_7
  br label %for_inc_8

for_inc_8:                                        ; preds = %endif_0
  %tmp125 = load i32, ptr %j, align 4
  %tmp126 = add i32 %tmp125, 1
  store i32 %tmp126, ptr %j, align 4
  %tmp10 = load i32, ptr %j, align 4
  %tmp12 = load i32, ptr %i, align 4
  %tmp12.neg = sub i32 0, %tmp12
  %tmp15 = add i32 %tmp13, %tmp12.neg
  %tmp16 = icmp slt i32 %tmp10, %tmp15
  br i1 %tmp16, label %for_body_7, label %for_cond_6.for_end_9_crit_edge

for_cond_6.for_end_9_crit_edge:                   ; preds = %for_inc_8
  %split20 = phi i32 [ %tmp12, %for_inc_8 ]
  br label %for_end_9

for_end_9:                                        ; preds = %for_cond_6.for_end_9_crit_edge, %for_init_5
  %tmp12.lcssa = phi i32 [ %split20, %for_cond_6.for_end_9_crit_edge ], [ %tmp1215, %for_init_5 ]
  br label %for_inc_3

for_inc_3:                                        ; preds = %for_end_9
  %tmp128 = add i32 %tmp12.lcssa, 1
  store i32 %tmp128, ptr %i, align 4
  %tmp4 = load i32, ptr %i, align 4
  %tmp8 = icmp slt i32 %tmp4, %tmp7
  br i1 %tmp8, label %for_body_2, label %for_cond_1.for_end_4_crit_edge

for_cond_1.for_end_4_crit_edge:                   ; preds = %for_inc_3
  br label %for_end_4

for_end_4:                                        ; preds = %for_cond_1.for_end_4_crit_edge, %for_init_0
  ret void
}

define i32 @main() {
  %tmp129 = call ptr @arraylist_create_int(i64 5)
  %tmp130 = alloca i32, i64 5, align 4
  store i32 23, ptr %tmp130, align 4
  %tmp134 = getelementptr inbounds i32, ptr %tmp130, i64 1
  store i32 13, ptr %tmp134, align 4
  %tmp136 = getelementptr inbounds i32, ptr %tmp130, i64 2
  store i32 9, ptr %tmp136, align 4
  %tmp138 = getelementptr inbounds i32, ptr %tmp130, i64 3
  store i32 40, ptr %tmp138, align 4
  %tmp140 = getelementptr inbounds i32, ptr %tmp130, i64 4
  store i32 80, ptr %tmp140, align 4
  call void @arraylist_addAll_int(ptr %tmp129, ptr %tmp130, i64 5)
  call void @bubbleSort(ptr %tmp129)
  call void @arraylist_print_int(ptr %tmp129)
  call void @arraylist_free_int(ptr %tmp129)
  %1 = call i32 @getchar()
  ret i32 0
}
