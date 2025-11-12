; ModuleID = 'programa.ll'
source_filename = "programa.ll"

%Set_double = type { ptr }
%Set_int = type { ptr }

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

declare ptr @arraylist_create_double(i64)

declare void @arraylist_add_double(ptr, double)

declare void @arraylist_addAll_double(ptr, ptr, i64)

declare void @arraylist_print_double(ptr)

declare double @arraylist_get_double(ptr, i64, ptr)

declare void @arraylist_clear_double(ptr)

declare void @arraylist_remove_double(ptr, i64)

declare void @arraylist_free_double(ptr)

declare i32 @arraylist_size_double(ptr)

declare ptr @arraylist_create_int(i64)

declare void @arraylist_add_int(ptr, i32)

declare void @arraylist_addAll_int(ptr, ptr, i64)

declare void @arraylist_print_int(ptr)

declare void @arraylist_clear_int(ptr)

declare void @arraylist_free_int(ptr)

declare i32 @arraylist_get_int(ptr, i64, ptr)

declare void @arraylist_remove_int(ptr, i64)

declare i32 @arraylist_size_int(ptr)

declare void @arraylist_add_string(ptr, ptr)

declare void @arraylist_addAll_string(ptr, ptr, i64)

declare void @arraylist_print_string(ptr)

declare void @arraylist_add_String(ptr, ptr)

declare void @arraylist_addAll_String(ptr, ptr, i64)

declare void @removeItem(ptr, i64)

declare ptr @getItem(ptr, i64)

define void @print_Set(ptr %p) {
entry:
  ret void
}

define void @print_Set_double(ptr %p) {
entry:
  %val0 = load ptr, ptr %p, align 8
  call void @arraylist_print_double(ptr %val0)
  ret void
}

define void @print_Set_int(ptr %p) {
entry:
  %val0 = load ptr, ptr %p, align 8
  call void @arraylist_print_int(ptr %val0)
  ret void
}

define ptr @Set_double_add(ptr %s, double %value) {
entry:
  %tmp72 = load ptr, ptr %s, align 8
  %tmp83 = call i32 @arraylist_size_double(ptr %tmp72)
  %tmp94 = icmp slt i32 0, %tmp83
  %tmp155 = load ptr, ptr %s, align 8
  br i1 %tmp94, label %while_body_1.lr.ph, label %while_end_2

while_body_1.lr.ph:                               ; preds = %entry
  br label %while_body_1

while_cond_0:                                     ; preds = %while_body_1
  %i.0 = phi i32 [ %tmp25, %while_body_1 ]
  %tmp7 = load ptr, ptr %s, align 8
  %tmp8 = call i32 @arraylist_size_double(ptr %tmp7)
  %tmp9 = icmp slt i32 %i.0, %tmp8
  %tmp15 = load ptr, ptr %s, align 8
  br i1 %tmp9, label %while_body_1, label %while_cond_0.while_end_2_crit_edge

while_body_1:                                     ; preds = %while_body_1.lr.ph, %while_cond_0
  %tmp157 = phi ptr [ %tmp155, %while_body_1.lr.ph ], [ %tmp15, %while_cond_0 ]
  %i.06 = phi i32 [ 0, %while_body_1.lr.ph ], [ %i.0, %while_cond_0 ]
  %tmp17 = zext i32 %i.06 to i64
  %tmp18 = alloca double, align 8
  %tmp19 = call double @arraylist_get_double(ptr %tmp157, i64 %tmp17, ptr %tmp18)
  %tmp20 = load double, ptr %tmp18, align 8
  %tmp22 = fcmp oeq double %tmp20, %value
  %tmp25 = add i32 %i.06, 1
  br i1 %tmp22, label %then_0, label %while_cond_0

then_0:                                           ; preds = %while_body_1
  ret ptr %s

0:                                                ; No predecessors!
  unreachable

while_cond_0.while_end_2_crit_edge:               ; preds = %while_cond_0
  %split = phi ptr [ %tmp15, %while_cond_0 ]
  br label %while_end_2

while_end_2:                                      ; preds = %while_cond_0.while_end_2_crit_edge, %entry
  %tmp15.lcssa = phi ptr [ %split, %while_cond_0.while_end_2_crit_edge ], [ %tmp155, %entry ]
  call void @arraylist_add_double(ptr %tmp15.lcssa, double %value)
  ret ptr %s

1:                                                ; No predecessors!
  ret ptr %s
}

define ptr @Set_int_add(ptr %s, i32 %value) {
entry:
  %tmp382 = load ptr, ptr %s, align 8
  %tmp393 = call i32 @arraylist_size_int(ptr %tmp382)
  %tmp404 = icmp slt i32 0, %tmp393
  %tmp465 = load ptr, ptr %s, align 8
  br i1 %tmp404, label %while_body_4.lr.ph, label %while_end_5

while_body_4.lr.ph:                               ; preds = %entry
  br label %while_body_4

while_cond_3:                                     ; preds = %while_body_4
  %i.0 = phi i32 [ %tmp56, %while_body_4 ]
  %tmp38 = load ptr, ptr %s, align 8
  %tmp39 = call i32 @arraylist_size_int(ptr %tmp38)
  %tmp40 = icmp slt i32 %i.0, %tmp39
  %tmp46 = load ptr, ptr %s, align 8
  br i1 %tmp40, label %while_body_4, label %while_cond_3.while_end_5_crit_edge

while_body_4:                                     ; preds = %while_body_4.lr.ph, %while_cond_3
  %tmp467 = phi ptr [ %tmp465, %while_body_4.lr.ph ], [ %tmp46, %while_cond_3 ]
  %i.06 = phi i32 [ 0, %while_body_4.lr.ph ], [ %i.0, %while_cond_3 ]
  %tmp48 = zext i32 %i.06 to i64
  %tmp49 = alloca i32, align 4
  %tmp50 = call i32 @arraylist_get_int(ptr %tmp467, i64 %tmp48, ptr %tmp49)
  %tmp51 = load i32, ptr %tmp49, align 4
  %tmp53 = icmp eq i32 %tmp51, %value
  %tmp56 = add i32 %i.06, 1
  br i1 %tmp53, label %then_1, label %while_cond_3

then_1:                                           ; preds = %while_body_4
  ret ptr %s

0:                                                ; No predecessors!
  unreachable

while_cond_3.while_end_5_crit_edge:               ; preds = %while_cond_3
  %split = phi ptr [ %tmp46, %while_cond_3 ]
  br label %while_end_5

while_end_5:                                      ; preds = %while_cond_3.while_end_5_crit_edge, %entry
  %tmp46.lcssa = phi ptr [ %split, %while_cond_3.while_end_5_crit_edge ], [ %tmp465, %entry ]
  call void @arraylist_add_int(ptr %tmp46.lcssa, i32 %value)
  ret ptr %s

1:                                                ; No predecessors!
  ret ptr %s
}

define ptr @Set_double_remove(ptr %s, double %index) {
entry:
  %tmp67 = load ptr, ptr %s, align 8
  %tmp69 = fptosi double %index to i64
  call void @arraylist_remove_double(ptr %tmp67, i64 %tmp69)
  ret ptr %s

0:                                                ; No predecessors!
  ret ptr %s
}

define ptr @Set_int_remove(ptr %s, i32 %index) {
entry:
  %tmp76 = load ptr, ptr %s, align 8
  %tmp78 = zext i32 %index to i64
  call void @arraylist_remove_int(ptr %tmp76, i64 %tmp78)
  ret ptr %s

0:                                                ; No predecessors!
  ret ptr %s
}

define i32 @main() {
  %tmp80 = alloca %Set_double, align 8
  %tmp81 = call ptr @arraylist_create_double(i64 10)
  store ptr %tmp81, ptr %tmp80, align 8
  %tmp85 = call ptr @Set_double_add(ptr %tmp80, double 2.100000e+00)
  %tmp88 = call ptr @Set_double_add(ptr %tmp80, double 2.100000e+00)
  %tmp91 = call ptr @Set_double_remove(ptr %tmp80, i32 0)
  call void @print_Set_double(ptr %tmp80)
  %tmp93 = alloca %Set_int, align 8
  %tmp94 = call ptr @arraylist_create_int(i64 10)
  store ptr %tmp94, ptr %tmp93, align 8
  %tmp98 = call ptr @Set_int_add(ptr %tmp93, i32 1)
  %tmp101 = call ptr @Set_int_add(ptr %tmp93, i32 1)
  %tmp104 = call ptr @Set_int_remove(ptr %tmp93, i32 0)
  call void @print_Set_int(ptr %tmp93)
  %1 = call i32 @getchar()
  ret i32 0
}
