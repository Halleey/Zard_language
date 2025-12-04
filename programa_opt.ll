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
@.str0 = private constant [16 x i8] c"Lista ordenada:\00"

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
  %tmp7 = sub i32 %tmp2, 1
  %tmp817 = icmp slt i32 0, %tmp7
  br i1 %tmp817, label %while_body_1.lr.ph, label %while_end_2

while_body_1.lr.ph:                               ; preds = %entry
  br label %while_body_1

while_body_1:                                     ; preds = %while_body_1.lr.ph, %while_end_5
  %i.018 = phi i32 [ 0, %while_body_1.lr.ph ], [ %tmp126, %while_end_5 ]
  %j = alloca i32, align 4
  store i32 0, ptr %j, align 4
  %tmp1014 = load i32, ptr %j, align 4
  %i.0.neg = sub i32 0, %i.018
  %tmp13 = add i32 %tmp2, -1
  %tmp15 = add i32 %tmp13, %i.0.neg
  %tmp1615 = icmp slt i32 %tmp1014, %tmp15
  br i1 %tmp1615, label %while_body_4.lr.ph, label %while_end_5

while_body_4.lr.ph:                               ; preds = %while_body_1
  br label %while_body_4

while_body_4:                                     ; preds = %while_body_4.lr.ph, %endif_0
  %tmp1016 = phi i32 [ %tmp1014, %while_body_4.lr.ph ], [ %tmp10, %endif_0 ]
  %a = alloca i32, align 4
  %tmp20 = zext i32 %tmp1016 to i64
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

then_0:                                           ; preds = %while_body_4
  %tmp38 = load i32, ptr %j, align 4
  %tmp39 = zext i32 %tmp38 to i64
  call void @arraylist_remove_int(ptr %arr, i64 %tmp39)
  %tmp42 = load i32, ptr %j, align 4
  %tmp43 = zext i32 %tmp42 to i64
  call void @arraylist_remove_int(ptr %arr, i64 %tmp43)
  %temp = alloca ptr, align 8
  %tmp44 = call ptr @arraylist_create_int(i64 4)
  store ptr %tmp44, ptr %temp, align 8
  %k = alloca i32, align 4
  store i32 0, ptr %k, align 4
  %tmp461 = load i32, ptr %k, align 4
  %tmp472 = load i32, ptr %j, align 4
  %tmp483 = icmp slt i32 %tmp461, %tmp472
  %tmp694 = load ptr, ptr %temp, align 8
  br i1 %tmp483, label %while_body_7.lr.ph, label %while_end_8

while_body_7.lr.ph:                               ; preds = %then_0
  br label %while_body_7

while_body_7:                                     ; preds = %while_body_7.lr.ph, %while_body_7
  %tmp696 = phi ptr [ %tmp694, %while_body_7.lr.ph ], [ %tmp69, %while_body_7 ]
  %tmp465 = phi i32 [ %tmp461, %while_body_7.lr.ph ], [ %tmp46, %while_body_7 ]
  %tmp61 = zext i32 %tmp465 to i64
  %tmp62 = alloca i32, align 4
  %tmp63 = call i32 @arraylist_get_int(ptr %arr, i64 %tmp61, ptr %tmp62)
  %tmp64 = load i32, ptr %tmp62, align 4
  call void @arraylist_add_int(ptr %tmp696, i32 %tmp64)
  %tmp65 = load i32, ptr %k, align 4
  %tmp66 = add i32 %tmp65, 1
  store i32 %tmp66, ptr %k, align 4
  %tmp46 = load i32, ptr %k, align 4
  %tmp47 = load i32, ptr %j, align 4
  %tmp48 = icmp slt i32 %tmp46, %tmp47
  %tmp69 = load ptr, ptr %temp, align 8
  br i1 %tmp48, label %while_body_7, label %while_cond_6.while_end_8_crit_edge

while_cond_6.while_end_8_crit_edge:               ; preds = %while_body_7
  %split = phi ptr [ %tmp69, %while_body_7 ]
  br label %while_end_8

while_end_8:                                      ; preds = %while_cond_6.while_end_8_crit_edge, %then_0
  %tmp69.lcssa = phi ptr [ %split, %while_cond_6.while_end_8_crit_edge ], [ %tmp694, %then_0 ]
  %tmp70 = load i32, ptr %b, align 4
  call void @arraylist_add_int(ptr %tmp69.lcssa, i32 %tmp70)
  %tmp73 = load ptr, ptr %temp, align 8
  %tmp74 = load i32, ptr %a, align 4
  call void @arraylist_add_int(ptr %tmp73, i32 %tmp74)
  %tmp757 = load i32, ptr %k, align 4
  %tmp788 = call i32 @arraylist_size_int(ptr %arr)
  %tmp799 = icmp slt i32 %tmp757, %tmp788
  br i1 %tmp799, label %while_body_10.lr.ph, label %while_end_11

while_body_10.lr.ph:                              ; preds = %while_end_8
  br label %while_body_10

while_body_10:                                    ; preds = %while_body_10.lr.ph, %while_body_10
  %tmp88 = load ptr, ptr %temp, align 8
  %tmp91 = load i32, ptr %k, align 4
  %tmp92 = zext i32 %tmp91 to i64
  %tmp93 = alloca i32, align 4
  %tmp94 = call i32 @arraylist_get_int(ptr %arr, i64 %tmp92, ptr %tmp93)
  %tmp95 = load i32, ptr %tmp93, align 4
  call void @arraylist_add_int(ptr %tmp88, i32 %tmp95)
  %tmp96 = load i32, ptr %k, align 4
  %tmp97 = add i32 %tmp96, 1
  store i32 %tmp97, ptr %k, align 4
  %tmp75 = load i32, ptr %k, align 4
  %tmp78 = call i32 @arraylist_size_int(ptr %arr)
  %tmp79 = icmp slt i32 %tmp75, %tmp78
  br i1 %tmp79, label %while_body_10, label %while_cond_9.while_end_11_crit_edge

while_cond_9.while_end_11_crit_edge:              ; preds = %while_body_10
  br label %while_end_11

while_end_11:                                     ; preds = %while_cond_9.while_end_11_crit_edge, %while_end_8
  call void @arraylist_clear_int(ptr %arr)
  store i32 0, ptr %k, align 4
  %tmp10010 = load i32, ptr %k, align 4
  %tmp10211 = load ptr, ptr %temp, align 8
  %tmp10312 = call i32 @arraylist_size_int(ptr %tmp10211)
  %tmp10413 = icmp slt i32 %tmp10010, %tmp10312
  br i1 %tmp10413, label %while_body_13.lr.ph, label %while_end_14

while_body_13.lr.ph:                              ; preds = %while_end_11
  br label %while_body_13

while_body_13:                                    ; preds = %while_body_13.lr.ph, %while_body_13
  %tmp115 = load ptr, ptr %temp, align 8
  %tmp116 = load i32, ptr %k, align 4
  %tmp117 = zext i32 %tmp116 to i64
  %tmp118 = alloca i32, align 4
  %tmp119 = call i32 @arraylist_get_int(ptr %tmp115, i64 %tmp117, ptr %tmp118)
  %tmp120 = load i32, ptr %tmp118, align 4
  call void @arraylist_add_int(ptr %arr, i32 %tmp120)
  %tmp121 = load i32, ptr %k, align 4
  %tmp122 = add i32 %tmp121, 1
  store i32 %tmp122, ptr %k, align 4
  %tmp100 = load i32, ptr %k, align 4
  %tmp102 = load ptr, ptr %temp, align 8
  %tmp103 = call i32 @arraylist_size_int(ptr %tmp102)
  %tmp104 = icmp slt i32 %tmp100, %tmp103
  br i1 %tmp104, label %while_body_13, label %while_cond_12.while_end_14_crit_edge

while_cond_12.while_end_14_crit_edge:             ; preds = %while_body_13
  br label %while_end_14

while_end_14:                                     ; preds = %while_cond_12.while_end_14_crit_edge, %while_end_11
  br label %endif_0

endif_0:                                          ; preds = %while_end_14, %while_body_4
  %tmp123 = load i32, ptr %j, align 4
  %tmp124 = add i32 %tmp123, 1
  store i32 %tmp124, ptr %j, align 4
  %tmp10 = load i32, ptr %j, align 4
  %tmp16 = icmp slt i32 %tmp10, %tmp15
  br i1 %tmp16, label %while_body_4, label %while_cond_3.while_end_5_crit_edge

while_cond_3.while_end_5_crit_edge:               ; preds = %endif_0
  br label %while_end_5

while_end_5:                                      ; preds = %while_cond_3.while_end_5_crit_edge, %while_body_1
  %tmp126 = add i32 %i.018, 1
  %tmp8 = icmp slt i32 %tmp126, %tmp7
  br i1 %tmp8, label %while_body_1, label %while_cond_0.while_end_2_crit_edge

while_cond_0.while_end_2_crit_edge:               ; preds = %while_end_5
  br label %while_end_2

while_end_2:                                      ; preds = %while_cond_0.while_end_2_crit_edge, %entry
  ret void
}

define i32 @main() {
  %tmp127 = call ptr @arraylist_create_int(i64 5)
  %tmp128 = alloca i32, i64 5, align 4
  store i32 5, ptr %tmp128, align 4
  %tmp132 = getelementptr inbounds i32, ptr %tmp128, i64 1
  store i32 1, ptr %tmp132, align 4
  %tmp134 = getelementptr inbounds i32, ptr %tmp128, i64 2
  store i32 4, ptr %tmp134, align 4
  %tmp136 = getelementptr inbounds i32, ptr %tmp128, i64 3
  store i32 2, ptr %tmp136, align 4
  %tmp138 = getelementptr inbounds i32, ptr %tmp128, i64 4
  store i32 8, ptr %tmp138, align 4
  call void @arraylist_addAll_int(ptr %tmp127, ptr %tmp128, i64 5)
  call void @bubbleSort(ptr %tmp127)
  %1 = call i32 (ptr, ...) @printf(ptr @.strStr, ptr @.str0)
  %tmp1451 = call i32 @arraylist_size_int(ptr %tmp127)
  %tmp1462 = icmp slt i32 0, %tmp1451
  br i1 %tmp1462, label %while_body_16.lr.ph, label %while_end_17

while_body_16.lr.ph:                              ; preds = %0
  br label %while_body_16

while_body_16:                                    ; preds = %while_body_16.lr.ph, %while_body_16
  %i.03 = phi i32 [ 0, %while_body_16.lr.ph ], [ %tmp155, %while_body_16 ]
  %tmp150 = zext i32 %i.03 to i64
  %tmp151 = alloca i32, align 4
  %tmp152 = call i32 @arraylist_get_int(ptr %tmp127, i64 %tmp150, ptr %tmp151)
  %tmp153 = load i32, ptr %tmp151, align 4
  %2 = call i32 (ptr, ...) @printf(ptr @.strInt, i32 %tmp153)
  %tmp155 = add i32 %i.03, 1
  %tmp145 = call i32 @arraylist_size_int(ptr %tmp127)
  %tmp146 = icmp slt i32 %tmp155, %tmp145
  br i1 %tmp146, label %while_body_16, label %while_cond_15.while_end_17_crit_edge

while_cond_15.while_end_17_crit_edge:             ; preds = %while_body_16
  br label %while_end_17

while_end_17:                                     ; preds = %while_cond_15.while_end_17_crit_edge, %0
  call void @arraylist_free_int(ptr %tmp127)
  %3 = call i32 @getchar()
  ret i32 0
}
