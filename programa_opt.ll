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
@.str0 = private constant [5 x i8] c"zard\00"
@.str1 = private constant [6 x i8] c"angel\00"

declare i32 @printf(ptr, ...)

declare i32 @getchar()

declare void @printString(ptr)

declare ptr @malloc(i64)

declare void @setString(ptr, ptr)

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

declare void @arraylist_add_string(ptr, ptr)

declare void @arraylist_addAll_string(ptr, ptr, i64)

declare void @arraylist_print_string(ptr)

declare void @arraylist_add_String(ptr, ptr)

declare void @arraylist_addAll_String(ptr, ptr, i64)

declare void @removeItem(ptr, i64)

declare ptr @getItem(ptr, i64)

define i32 @main() {
  %tmp0 = call ptr @arraylist_create_int(i64 4)
  %tmp1 = call ptr @arraylist_create(i64 4)
  %tmp4 = call ptr @createString(ptr @.str0)
  call void @arraylist_add_String(ptr %tmp1, ptr %tmp4)
  %tmp6 = call ptr @createString(ptr @.str1)
  call void @arraylist_add_String(ptr %tmp1, ptr %tmp6)
  call void @arraylist_add_int(ptr %tmp0, i32 3)
  %tmp16 = alloca i32, i64 4, align 4
  store i32 3, ptr %tmp16, align 4
  %tmp20 = getelementptr inbounds i32, ptr %tmp16, i64 1
  store i32 4, ptr %tmp20, align 4
  %tmp22 = getelementptr inbounds i32, ptr %tmp16, i64 2
  store i32 5, ptr %tmp22, align 4
  %tmp24 = getelementptr inbounds i32, ptr %tmp16, i64 3
  store i32 2, ptr %tmp24, align 4
  call void @arraylist_addAll_int(ptr %tmp0, ptr %tmp16, i64 4)
  %tmp27 = call i32 @arraylist_size_int(ptr %tmp0)
  %1 = call i32 (ptr, ...) @printf(ptr @.strInt, i32 %tmp27)
  %tmp31 = call i32 @arraylist_size_int(ptr %tmp0)
  %2 = call i32 (ptr, ...) @printf(ptr @.strInt, i32 %tmp31)
  call void @arraylist_remove_int(ptr %tmp0, i64 0)
  %tmp40 = alloca i32, align 4
  %tmp41 = call i32 @arraylist_get_int(ptr %tmp0, i64 1, ptr %tmp40)
  %tmp42 = load i32, ptr %tmp40, align 4
  %3 = call i32 (ptr, ...) @printf(ptr @.strInt, i32 %tmp42)
  call void @arraylist_clear_int(ptr %tmp0)
  call void @arraylist_free_int(ptr %tmp0)
  call void @freeList(ptr %tmp1)
  %4 = call i32 @getchar()
  ret i32 0
}
