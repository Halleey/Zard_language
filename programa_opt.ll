; ModuleID = 'programa.ll'
source_filename = "programa.ll"

%Entry = type { i32, ptr }

@.strChar = private constant [3 x i8] c"%c\00"
@.strTrue = private constant [6 x i8] c"true\0A\00"
@.strFalse = private constant [7 x i8] c"false\0A\00"
@.strInt = private constant [4 x i8] c"%d\0A\00"
@.strDouble = private constant [4 x i8] c"%f\0A\00"
@.strFloat = private constant [4 x i8] c"%f\0A\00"
@.strStr = private constant [4 x i8] c"%s\0A\00"
@.strEmpty = private constant [1 x i8] zeroinitializer
@.str0 = private constant [14 x i8] c"key not found\00"
@.str1 = private constant [6 x i8] c"Sword\00"
@.str2 = private constant [7 x i8] c"Shield\00"
@.str3 = private constant [7 x i8] c"Potion\00"

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

define void @print_Entry(ptr %p) {
entry:
  %v0 = load i32, ptr %p, align 4
  %0 = call i32 (ptr, ...) @printf(ptr @.strInt, i32 %v0)
  %f1 = getelementptr inbounds %Entry, ptr %p, i32 0, i32 1
  %v1 = load ptr, ptr %f1, align 8
  call void @printString(ptr %v1)
  ret void
}

define void @print_maps(ptr %p) {
entry:
  ret void
}

define ptr @maps_put(ptr %s, i32 %key, ptr %value) {
entry:
  %tmp41 = load ptr, ptr %s, align 8
  %tmp62 = call i32 @length(ptr %tmp41)
  %tmp73 = icmp slt i32 0, %tmp62
  br i1 %tmp73, label %while_body_1.lr.ph, label %while_end_2

while_body_1.lr.ph:                               ; preds = %entry
  br label %while_body_1

while_cond_0:                                     ; preds = %while_body_1
  %i.0 = phi i32 [ %tmp26, %while_body_1 ]
  %tmp4 = load ptr, ptr %s, align 8
  %tmp6 = call i32 @length(ptr %tmp4)
  %tmp7 = icmp slt i32 %i.0, %tmp6
  br i1 %tmp7, label %while_body_1, label %while_cond_0.while_end_2_crit_edge

while_body_1:                                     ; preds = %while_body_1.lr.ph, %while_cond_0
  %i.04 = phi i32 [ 0, %while_body_1.lr.ph ], [ %i.0, %while_cond_0 ]
  %e = alloca ptr, align 8
  %tmp10 = load ptr, ptr %s, align 8
  %tmp12 = zext i32 %i.04 to i64
  %tmp13 = call ptr @arraylist_get_ptr(ptr %tmp10, i64 %tmp12)
  store ptr %tmp13, ptr %e, align 8
  %tmp17 = load i32, ptr %tmp13, align 4
  %tmp19 = icmp eq i32 %tmp17, %key
  %tmp26 = add i32 %i.04, 1
  br i1 %tmp19, label %then_0, label %while_cond_0

then_0:                                           ; preds = %while_body_1
  %tmp13.lcssa = phi ptr [ %tmp13, %while_body_1 ]
  %tmp21 = getelementptr inbounds %Entry, ptr %tmp13.lcssa, i32 0, i32 1
  store ptr %value, ptr %tmp21, align 8
  ret ptr %s

0:                                                ; No predecessors!
  unreachable

while_cond_0.while_end_2_crit_edge:               ; preds = %while_cond_0
  br label %while_end_2

while_end_2:                                      ; preds = %while_cond_0.while_end_2_crit_edge, %entry
  %n = alloca ptr, align 8
  %tmp27 = call ptr @malloc(i64 12)
  store i32 0, ptr %tmp27, align 4
  %tmp30 = call ptr @createString(ptr null)
  %tmp31 = getelementptr inbounds %Entry, ptr %tmp27, i32 0, i32 1
  store ptr %tmp30, ptr %tmp31, align 8
  store ptr %tmp27, ptr %n, align 8
  store i32 %key, ptr %tmp27, align 4
  %tmp36 = load ptr, ptr %n, align 8
  %tmp37 = getelementptr inbounds %Entry, ptr %tmp36, i32 0, i32 1
  store ptr %value, ptr %tmp37, align 8
  %tmp42 = load ptr, ptr %s, align 8
  %tmp43 = load ptr, ptr %n, align 8
  call void @arraylist_add_ptr(ptr %tmp42, ptr %tmp43)
  ret ptr %s

1:                                                ; No predecessors!
  ret ptr %s
}

define void @maps_get(ptr %s, i32 %key) {
entry:
  %tmp511 = load ptr, ptr %s, align 8
  %tmp532 = call i32 @length(ptr %tmp511)
  %tmp543 = icmp slt i32 0, %tmp532
  br i1 %tmp543, label %while_body_4.lr.ph, label %while_end_5

while_body_4.lr.ph:                               ; preds = %entry
  br label %while_body_4

while_cond_3:                                     ; preds = %while_body_4
  %i_1.0 = phi i32 [ %tmp71, %while_body_4 ]
  %tmp51 = load ptr, ptr %s, align 8
  %tmp53 = call i32 @length(ptr %tmp51)
  %tmp54 = icmp slt i32 %i_1.0, %tmp53
  br i1 %tmp54, label %while_body_4, label %while_cond_3.while_end_5_crit_edge

while_body_4:                                     ; preds = %while_body_4.lr.ph, %while_cond_3
  %i_1.04 = phi i32 [ 0, %while_body_4.lr.ph ], [ %i_1.0, %while_cond_3 ]
  %e_1 = alloca ptr, align 8
  %tmp57 = load ptr, ptr %s, align 8
  %tmp59 = zext i32 %i_1.04 to i64
  %tmp60 = call ptr @arraylist_get_ptr(ptr %tmp57, i64 %tmp59)
  store ptr %tmp60, ptr %e_1, align 8
  %tmp64 = load i32, ptr %tmp60, align 4
  %tmp66 = icmp eq i32 %tmp64, %key
  %tmp71 = add i32 %i_1.04, 1
  br i1 %tmp66, label %then_1, label %while_cond_3

then_1:                                           ; preds = %while_body_4
  %tmp60.lcssa = phi ptr [ %tmp60, %while_body_4 ]
  %tmp68 = getelementptr inbounds %Entry, ptr %tmp60.lcssa, i32 0, i32 1
  %tmp69 = load ptr, ptr %tmp68, align 8
  call void @printString(ptr %tmp69)
  ret void

0:                                                ; No predecessors!
  unreachable

while_cond_3.while_end_5_crit_edge:               ; preds = %while_cond_3
  br label %while_end_5

while_end_5:                                      ; preds = %while_cond_3.while_end_5_crit_edge, %entry
  %1 = call i32 (ptr, ...) @printf(ptr @.strStr, ptr @.str0)
  ret void
}

define ptr @maps_remove(ptr %s, i32 %key) {
entry:
  %tmp771 = load ptr, ptr %s, align 8
  %tmp792 = call i32 @length(ptr %tmp771)
  %tmp803 = icmp slt i32 0, %tmp792
  br i1 %tmp803, label %while_body_7.lr.ph, label %while_end_8

while_body_7.lr.ph:                               ; preds = %entry
  br label %while_body_7

while_cond_6:                                     ; preds = %while_body_7
  %i_2.0 = phi i32 [ %tmp100, %while_body_7 ]
  %tmp77 = load ptr, ptr %s, align 8
  %tmp79 = call i32 @length(ptr %tmp77)
  %tmp80 = icmp slt i32 %i_2.0, %tmp79
  br i1 %tmp80, label %while_body_7, label %while_cond_6.while_end_8_crit_edge

while_body_7:                                     ; preds = %while_body_7.lr.ph, %while_cond_6
  %i_2.04 = phi i32 [ 0, %while_body_7.lr.ph ], [ %i_2.0, %while_cond_6 ]
  %e_2 = alloca ptr, align 8
  %tmp83 = load ptr, ptr %s, align 8
  %tmp85 = zext i32 %i_2.04 to i64
  %tmp86 = call ptr @arraylist_get_ptr(ptr %tmp83, i64 %tmp85)
  store ptr %tmp86, ptr %e_2, align 8
  %tmp90 = load i32, ptr %tmp86, align 4
  %tmp92 = icmp eq i32 %tmp90, %key
  %tmp100 = add i32 %i_2.04, 1
  br i1 %tmp92, label %then_2, label %while_cond_6

then_2:                                           ; preds = %while_body_7
  %tmp85.lcssa = phi i64 [ %tmp85, %while_body_7 ]
  %tmp95 = load ptr, ptr %s, align 8
  call void @removeItem(ptr %tmp95, i64 %tmp85.lcssa)
  ret ptr %s

0:                                                ; No predecessors!
  unreachable

while_cond_6.while_end_8_crit_edge:               ; preds = %while_cond_6
  br label %while_end_8

while_end_8:                                      ; preds = %while_cond_6.while_end_8_crit_edge, %entry
  ret ptr %s

1:                                                ; No predecessors!
  ret ptr %s
}

define i32 @main() {
  %tmp102 = call ptr @malloc(i64 8)
  %tmp104 = call ptr @arraylist_create(i64 10)
  store ptr %tmp104, ptr %tmp102, align 8
  %tmp110 = call ptr @createString(ptr @.str1)
  call void @maps_put(ptr %tmp102, i32 1, ptr %tmp110)
  %tmp114 = call ptr @createString(ptr @.str2)
  call void @maps_put(ptr %tmp102, i32 2, ptr %tmp114)
  %tmp118 = call ptr @createString(ptr @.str3)
  call void @maps_put(ptr %tmp102, i32 3, ptr %tmp118)
  call void @maps_remove(ptr %tmp102, i32 1)
  call void @maps_get(ptr %tmp102, i32 1)
  call void @maps_get(ptr %tmp102, i32 3)
  call void @maps_get(ptr %tmp102, i32 2)
  %1 = call i32 @getchar()
  ret i32 0
}
