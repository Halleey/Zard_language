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
  ret i32 0
}
