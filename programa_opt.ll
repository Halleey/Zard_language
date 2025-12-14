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
@.str0 = private constant [12 x i8] c"hello world\00"

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

define void @print_Matrix(ptr %p) {
entry:
  %v0 = load ptr, ptr %p, align 8
  call void @arraylist_print_int(ptr %v0)
  ret void
}

define i32 @Matrix_somar(ptr %s, i32 %b, i32 %c) {
entry:
  %tmp2 = add i32 %c, %b
  ret i32 %tmp2

0:                                                ; No predecessors!
  ret i32 undef
}

define void @Matrix_ola(ptr %s) {
entry:
  %0 = call i32 (ptr, ...) @printf(ptr @.strStr, ptr @.str0)
  ret void
}

define i32 @main() {
  %tmp4 = call ptr @malloc(i64 8)
  %tmp6 = call ptr @arraylist_create_int(i64 10)
  store ptr %tmp6, ptr %tmp4, align 8
  %tmp11 = call i32 @Matrix_somar(ptr %tmp4, i32 3, i32 2)
  %1 = call i32 (ptr, ...) @printf(ptr @.strInt, i32 %tmp11)
  call void @Matrix_ola(ptr %tmp4)
  %2 = call i32 @getchar()

declare void @arraylist_add_int(ptr, i32)

declare void @arraylist_addAll_int(ptr, ptr, i64)

declare void @arraylist_print_int(ptr)

declare void @arraylist_clear_int(ptr)

declare void @arraylist_free_int(ptr)

declare i32 @arraylist_get_int(ptr, i64, ptr)

declare void @arraylist_remove_int(ptr, i64)

declare i32 @arraylist_size_int(ptr)

define void @print_Set(ptr %p) {
entry:
  ret void
}

define void @print_Set_int(ptr %p) {
entry:
  %v0 = load ptr, ptr %p, align 8
  call void @arraylist_print_int(ptr %v0)
  ret void
}

define ptr @Set_int_add(ptr %s, i32 %value) {
entry:
  %tmp72 = load ptr, ptr %s, align 8
  %tmp83 = call i32 @arraylist_size_int(ptr %tmp72)
  %tmp94 = icmp slt i32 0, %tmp83
  %tmp155 = load ptr, ptr %s, align 8
  br i1 %tmp94, label %while_body_1.lr.ph, label %while_end_2

while_body_1.lr.ph:                               ; preds = %entry
  br label %while_body_1

while_cond_0:                                     ; preds = %while_body_1
  %i.0 = phi i32 [ %tmp25, %while_body_1 ]
  %tmp7 = load ptr, ptr %s, align 8
  %tmp8 = call i32 @arraylist_size_int(ptr %tmp7)
  %tmp9 = icmp slt i32 %i.0, %tmp8
  %tmp15 = load ptr, ptr %s, align 8
  br i1 %tmp9, label %while_body_1, label %while_cond_0.while_end_2_crit_edge

while_body_1:                                     ; preds = %while_body_1.lr.ph, %while_cond_0
  %tmp157 = phi ptr [ %tmp155, %while_body_1.lr.ph ], [ %tmp15, %while_cond_0 ]
  %i.06 = phi i32 [ 0, %while_body_1.lr.ph ], [ %i.0, %while_cond_0 ]
  %tmp17 = zext i32 %i.06 to i64
  %tmp18 = alloca i32, align 4
  %tmp19 = call i32 @arraylist_get_int(ptr %tmp157, i64 %tmp17, ptr %tmp18)
  %tmp20 = load i32, ptr %tmp18, align 4
  %tmp22 = icmp eq i32 %tmp20, %value
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
  call void @arraylist_add_int(ptr %tmp15.lcssa, i32 %value)
  ret ptr %s

1:                                                ; No predecessors!
  ret ptr %s
}

define ptr @Set_int_remove(ptr %s, i32 %index) {
entry:
  %tmp5 = load ptr, ptr %s, align 8
  %tmp7 = zext i32 %index to i64
  call void @arraylist_remove_int(ptr %tmp5, i64 %tmp7)
  ret ptr %s

0:                                                ; No predecessors!
  ret ptr %s
}

define i32 @main() {
  %tmp2 = call ptr @malloc(i64 8)
  %tmp5 = call ptr @arraylist_create_int(i64 10)
  store ptr %tmp5, ptr %tmp2, align 8
  %tmp8 = call ptr @Set_int_add(ptr %tmp2, i32 1)
  %tmp11 = call ptr @Set_int_add(ptr %tmp2, i32 2)
  %tmp14 = call ptr @Set_int_remove(ptr %tmp2, i32 0)
  call void @print_Set_int(ptr %tmp2)
  %1 = call i32 @getchar()
  ret i32 0
}
