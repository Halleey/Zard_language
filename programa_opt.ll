; ModuleID = 'programa.ll'
source_filename = "programa.ll"

%Entry = type { i32, ptr }

@.strChar = private constant [3 x i8] c"%c\00"
@.strInt = private constant [3 x i8] c"%d\00"
@.strDouble = private constant [3 x i8] c"%f\00"
@.strFloat = private constant [3 x i8] c"%f\00"
@.strStr = private constant [3 x i8] c"%s\00"
@.strTrue = private constant [5 x i8] c"true\00"
@.strFalse = private constant [6 x i8] c"false\00"
@.strNewLine = private constant [2 x i8] c"\0A\00"
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

declare void @free(ptr)

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
  %i.0 = phi i32 [ %tmp39, %while_body_1 ]
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
  %tmp17 = call ptr @malloc(i64 16)
  %tmp21 = load i32, ptr %tmp13, align 4
  store i32 %tmp21, ptr %tmp17, align 4
  %tmp22 = getelementptr inbounds %Entry, ptr %tmp13, i32 0, i32 1
  %tmp23 = getelementptr inbounds %Entry, ptr %tmp17, i32 0, i32 1
  %tmp24 = load ptr, ptr %tmp22, align 8
  %tmp26 = load ptr, ptr %tmp24, align 8
  %tmp27 = call ptr @createString(ptr %tmp26)
  store ptr %tmp27, ptr %tmp23, align 8
  store ptr %tmp17, ptr %e, align 8
  %tmp30 = load i32, ptr %tmp17, align 4
  %tmp32 = icmp eq i32 %tmp30, %key
  %tmp39 = add i32 %i.04, 1
  br i1 %tmp32, label %then_0, label %while_cond_0

then_0:                                           ; preds = %while_body_1
  %tmp23.lcssa = phi ptr [ %tmp23, %while_body_1 ]
  store ptr %value, ptr %tmp23.lcssa, align 8
  ret ptr %s

0:                                                ; No predecessors!
  unreachable

while_cond_0.while_end_2_crit_edge:               ; preds = %while_cond_0
  br label %while_end_2

while_end_2:                                      ; preds = %while_cond_0.while_end_2_crit_edge, %entry
  %n = alloca ptr, align 8
  %tmp40 = call ptr @malloc(i64 12)
  store i32 0, ptr %tmp40, align 4
  %tmp43 = call ptr @createString(ptr null)
  %tmp44 = getelementptr inbounds %Entry, ptr %tmp40, i32 0, i32 1
  store ptr %tmp43, ptr %tmp44, align 8
  store ptr %tmp40, ptr %n, align 8
  store i32 %key, ptr %tmp40, align 4
  %tmp49 = load ptr, ptr %n, align 8
  %tmp50 = getelementptr inbounds %Entry, ptr %tmp49, i32 0, i32 1
  store ptr %value, ptr %tmp50, align 8
  %tmp55 = load ptr, ptr %s, align 8
  %tmp56 = load ptr, ptr %n, align 8
  call void @arraylist_add_ptr(ptr %tmp55, ptr %tmp56)
  ret ptr %s

1:                                                ; No predecessors!
  ret ptr %s
}

define ptr @maps_get(ptr %s, i32 %key) {
entry:
  %tmp641 = load ptr, ptr %s, align 8
  %tmp662 = call i32 @length(ptr %tmp641)
  %tmp673 = icmp slt i32 0, %tmp662
  br i1 %tmp673, label %while_body_4.lr.ph, label %while_end_5

while_body_4.lr.ph:                               ; preds = %entry
  br label %while_body_4

while_cond_3:                                     ; preds = %while_body_4
  %i_1.0 = phi i32 [ %tmp98, %while_body_4 ]
  %tmp64 = load ptr, ptr %s, align 8
  %tmp66 = call i32 @length(ptr %tmp64)
  %tmp67 = icmp slt i32 %i_1.0, %tmp66
  br i1 %tmp67, label %while_body_4, label %while_cond_3.while_end_5_crit_edge

while_body_4:                                     ; preds = %while_body_4.lr.ph, %while_cond_3
  %i_1.04 = phi i32 [ 0, %while_body_4.lr.ph ], [ %i_1.0, %while_cond_3 ]
  %e_1 = alloca ptr, align 8
  %tmp70 = load ptr, ptr %s, align 8
  %tmp72 = zext i32 %i_1.04 to i64
  %tmp73 = call ptr @arraylist_get_ptr(ptr %tmp70, i64 %tmp72)
  %tmp77 = call ptr @malloc(i64 16)
  %tmp81 = load i32, ptr %tmp73, align 4
  store i32 %tmp81, ptr %tmp77, align 4
  %tmp82 = getelementptr inbounds %Entry, ptr %tmp73, i32 0, i32 1
  %tmp83 = getelementptr inbounds %Entry, ptr %tmp77, i32 0, i32 1
  %tmp84 = load ptr, ptr %tmp82, align 8
  %tmp86 = load ptr, ptr %tmp84, align 8
  %tmp87 = call ptr @createString(ptr %tmp86)
  store ptr %tmp87, ptr %tmp83, align 8
  store ptr %tmp77, ptr %e_1, align 8
  %tmp90 = load i32, ptr %tmp77, align 4
  %tmp92 = icmp eq i32 %tmp90, %key
  %tmp98 = add i32 %i_1.04, 1
  br i1 %tmp92, label %then_1, label %while_cond_3

then_1:                                           ; preds = %while_body_4
  %tmp83.lcssa = phi ptr [ %tmp83, %while_body_4 ]
  %tmp95 = load ptr, ptr %tmp83.lcssa, align 8
  call void @printString(ptr %tmp95)
  %0 = call i32 (ptr, ...) @printf(ptr @.strNewLine)
  ret ptr %s

1:                                                ; No predecessors!
  unreachable

while_cond_3.while_end_5_crit_edge:               ; preds = %while_cond_3
  br label %while_end_5

while_end_5:                                      ; preds = %while_cond_3.while_end_5_crit_edge, %entry
  %2 = call i32 (ptr, ...) @printf(ptr @.strStr, ptr @.str0)
  %3 = call i32 (ptr, ...) @printf(ptr @.strNewLine)
  ret ptr %s
}

define ptr @maps_remove(ptr %s, i32 %key) {
entry:
  %tmp1041 = load ptr, ptr %s, align 8
  %tmp1062 = call i32 @length(ptr %tmp1041)
  %tmp1073 = icmp slt i32 0, %tmp1062
  br i1 %tmp1073, label %while_body_7.lr.ph, label %while_end_8

while_body_7.lr.ph:                               ; preds = %entry
  br label %while_body_7

while_cond_6:                                     ; preds = %while_body_7
  %i_2.0 = phi i32 [ %tmp140, %while_body_7 ]
  %tmp104 = load ptr, ptr %s, align 8
  %tmp106 = call i32 @length(ptr %tmp104)
  %tmp107 = icmp slt i32 %i_2.0, %tmp106
  br i1 %tmp107, label %while_body_7, label %while_cond_6.while_end_8_crit_edge

while_body_7:                                     ; preds = %while_body_7.lr.ph, %while_cond_6
  %i_2.04 = phi i32 [ 0, %while_body_7.lr.ph ], [ %i_2.0, %while_cond_6 ]
  %e_2 = alloca ptr, align 8
  %tmp110 = load ptr, ptr %s, align 8
  %tmp112 = zext i32 %i_2.04 to i64
  %tmp113 = call ptr @arraylist_get_ptr(ptr %tmp110, i64 %tmp112)
  %tmp117 = call ptr @malloc(i64 16)
  %tmp121 = load i32, ptr %tmp113, align 4
  store i32 %tmp121, ptr %tmp117, align 4
  %tmp122 = getelementptr inbounds %Entry, ptr %tmp113, i32 0, i32 1
  %tmp123 = getelementptr inbounds %Entry, ptr %tmp117, i32 0, i32 1
  %tmp124 = load ptr, ptr %tmp122, align 8
  %tmp126 = load ptr, ptr %tmp124, align 8
  %tmp127 = call ptr @createString(ptr %tmp126)
  store ptr %tmp127, ptr %tmp123, align 8
  store ptr %tmp117, ptr %e_2, align 8
  %tmp130 = load i32, ptr %tmp117, align 4
  %tmp132 = icmp eq i32 %tmp130, %key
  %tmp140 = add i32 %i_2.04, 1
  br i1 %tmp132, label %then_2, label %while_cond_6

then_2:                                           ; preds = %while_body_7
  %tmp112.lcssa = phi i64 [ %tmp112, %while_body_7 ]
  %tmp135 = load ptr, ptr %s, align 8
  call void @removeItem(ptr %tmp135, i64 %tmp112.lcssa)
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
  %tmp142 = call ptr @malloc(i64 8)
  %tmp144 = call ptr @arraylist_create(i64 10)
  store ptr %tmp144, ptr %tmp142, align 8
  %tmp150 = call ptr @createString(ptr @.str1)
  call void @maps_put(ptr %tmp142, i32 1, ptr %tmp150)
  %tmp154 = call ptr @createString(ptr @.str2)
  call void @maps_put(ptr %tmp142, i32 2, ptr %tmp154)
  %tmp158 = call ptr @createString(ptr @.str3)
  call void @maps_put(ptr %tmp142, i32 3, ptr %tmp158)
  call void @maps_remove(ptr %tmp142, i32 1)
  call void @maps_get(ptr %tmp142, i32 1)
  call void @maps_get(ptr %tmp142, i32 3)
  call void @maps_get(ptr %tmp142, i32 2)
  %1 = call i32 @getchar()
  ret i32 0
}
