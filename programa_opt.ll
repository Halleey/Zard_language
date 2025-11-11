; ModuleID = 'programa.ll'
source_filename = "programa.ll"

%Set_int = type { ptr }
%Set_double = type { ptr }

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

define void @print_Set_int(ptr %p) {
entry:
  %val0 = load ptr, ptr %p, align 8
  call void @arraylist_print_int(ptr %val0)
  ret void
}

define void @print_Set_double(ptr %p) {
entry:
  %val0 = load ptr, ptr %p, align 8
  call void @arraylist_print_double(ptr %val0)
  ret void
}

define ptr @Set_double_add(ptr %s, double %value) {
entry:
  %tmp2 = load ptr, ptr %s, align 8
  call void @arraylist_add_double(ptr %tmp2, double %value)
  ret ptr %s
}

define ptr @Set_int_add(ptr %s, i32 %value) {
entry:
  %tmp2 = load ptr, ptr %s, align 8
  call void @arraylist_add_int(ptr %tmp2, i32 %value)
  ret ptr %s
}

define i32 @main() {
  %tmp0 = alloca %Set_int, align 8
  %tmp1 = call ptr @arraylist_create_int(i64 10)
  store ptr %tmp1, ptr %tmp0, align 8
  %tmp4 = call ptr @arraylist_create(i64 10)
  %tmp7 = alloca %Set_double, align 8
  %tmp8 = call ptr @arraylist_create_double(i64 10)
  store ptr %tmp8, ptr %tmp7, align 8
  %tmp10 = alloca %Set_int, align 8
  %tmp11 = call ptr @arraylist_create_int(i64 4)
  %tmp12 = alloca i32, i64 3, align 4
  store i32 3, ptr %tmp12, align 4
  %tmp16 = getelementptr inbounds i32, ptr %tmp12, i64 1
  store i32 4, ptr %tmp16, align 4
  %tmp18 = getelementptr inbounds i32, ptr %tmp12, i64 2
  store i32 5, ptr %tmp18, align 4
  call void @arraylist_addAll_int(ptr %tmp11, ptr %tmp12, i64 3)
  store ptr %tmp11, ptr %tmp10, align 8
  %tmp22 = call ptr @Set_int_add(ptr %tmp0, i32 1)
  %tmp25 = call ptr @Set_double_add(ptr %tmp7, double 3.140000e+00)
  call void @print_Set_double(ptr %tmp7)
  call void @print_Set_int(ptr %tmp10)
  call void @print_Set_int(ptr %tmp0)
  %1 = call i32 @getchar()
  ret i32 0
}
