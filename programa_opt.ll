; ModuleID = 'programa.ll'
source_filename = "programa.ll"

%Set_string = type { ptr }

@.strChar = private constant [3 x i8] c"%c\00"
@.strTrue = private constant [6 x i8] c"true\0A\00"
@.strFalse = private constant [7 x i8] c"false\0A\00"
@.strInt = private constant [4 x i8] c"%d\0A\00"
@.strDouble = private constant [4 x i8] c"%f\0A\00"
@.strFloat = private constant [4 x i8] c"%f\0A\00"
@.strStr = private constant [4 x i8] c"%s\0A\00"
@.strEmpty = private constant [1 x i8] zeroinitializer
@.str0 = private constant [5 x i8] c"zard\00"

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

define void @print_Set_string(ptr %p) {
entry:
  %v0 = load ptr, ptr %p, align 8
  call void @arraylist_print_string(ptr %v0)
  ret void
}

define ptr @Set_string_add(ptr %s, ptr %value) {
entry:
  %tmp42 = load ptr, ptr %s, align 8
  %tmp63 = call i32 @length(ptr %tmp42)
  %tmp74 = icmp slt i32 0, %tmp63
  %tmp105 = load ptr, ptr %s, align 8
  br i1 %tmp74, label %while_body_1.lr.ph, label %while_end_2

while_body_1.lr.ph:                               ; preds = %entry
  br label %while_body_1

while_cond_0:                                     ; preds = %while_body_1
  %i.0 = phi i32 [ %tmp21, %while_body_1 ]
  %tmp4 = load ptr, ptr %s, align 8
  %tmp6 = call i32 @length(ptr %tmp4)
  %tmp7 = icmp slt i32 %i.0, %tmp6
  %tmp10 = load ptr, ptr %s, align 8
  br i1 %tmp7, label %while_body_1, label %while_cond_0.while_end_2_crit_edge

while_body_1:                                     ; preds = %while_body_1.lr.ph, %while_cond_0
  %tmp107 = phi ptr [ %tmp105, %while_body_1.lr.ph ], [ %tmp10, %while_cond_0 ]
  %i.06 = phi i32 [ 0, %while_body_1.lr.ph ], [ %i.0, %while_cond_0 ]
  %tmp12 = zext i32 %i.06 to i64
  %tmp13 = call ptr @arraylist_get_ptr(ptr %tmp107, i64 %tmp12)
  %tmp17 = call ptr @createString(ptr %tmp13)
  %tmp18 = call i1 @strcmp_eq(ptr %tmp17, ptr %value)
  %tmp21 = add i32 %i.06, 1
  br i1 %tmp18, label %then_0, label %while_cond_0

then_0:                                           ; preds = %while_body_1
  ret ptr %s

0:                                                ; No predecessors!
  unreachable

while_cond_0.while_end_2_crit_edge:               ; preds = %while_cond_0
  %split = phi ptr [ %tmp10, %while_cond_0 ]
  br label %while_end_2

while_end_2:                                      ; preds = %while_cond_0.while_end_2_crit_edge, %entry
  %tmp10.lcssa = phi ptr [ %split, %while_cond_0.while_end_2_crit_edge ], [ %tmp105, %entry ]
  call void @arraylist_add_String(ptr %tmp10.lcssa, ptr %value)
  ret ptr %s

1:                                                ; No predecessors!
  ret ptr undef
}

define ptr @Set_string_remove(ptr %s, i32 %index) {
entry:
  %tmp2 = load ptr, ptr %s, align 8
  %tmp4 = zext i32 %index to i64
  call void @removeItem(ptr %tmp2, i64 %tmp4)
  ret ptr %s

0:                                                ; No predecessors!
  ret ptr undef
}

define i32 @main() {
  %tmp0 = alloca %Set_string, align 8
  %tmp1 = call ptr @arraylist_create(i64 10)
  store ptr %tmp1, ptr %tmp0, align 8
  %tmp5 = call ptr @createString(ptr @.str0)
  %tmp6 = call ptr @Set_string_add(ptr %tmp0, ptr %tmp5)
  %tmp9 = call ptr @createString(ptr @.str0)
  %tmp10 = call ptr @Set_string_add(ptr %tmp0, ptr %tmp9)
  call void @print_Set_string(ptr %tmp0)
  %1 = call i32 @getchar()
  ret i32 0
}
