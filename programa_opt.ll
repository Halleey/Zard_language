; ModuleID = 'programa.ll'
source_filename = "programa.ll"

%String = type { ptr, i64 }

@.strTrue = private constant [6 x i8] c"true\0A\00"
@.strFalse = private constant [7 x i8] c"false\0A\00"
@.strInt = private constant [4 x i8] c"%d\0A\00"
@.strDouble = private constant [4 x i8] c"%f\0A\00"
@.strStr = private constant [4 x i8] c"%s\0A\00"
@.strEmpty = private constant [1 x i8] zeroinitializer

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

define ptr @addPeople(ptr %list) {
entry:
  %t3 = add i32 0, 3
  call void @arraylist_add_int(ptr %list, i32 %t3)
  %t5 = alloca %String, align 8
  %t6 = getelementptr inbounds %String, ptr %t5, i32 0, i32 0
  store ptr %list, ptr %t6, align 8
  %t7 = getelementptr inbounds %String, ptr %t5, i32 0, i32 1
  store i64 0, ptr %t7, align 4
  ret ptr %t5
}

define i32 @main() {
  %t8 = call ptr @arraylist_create_int(i64 4)
  %t9 = add i32 0, 3
  %t10 = call ptr @addPeople(i32 %t9)
  call void @arraylist_print_int(ptr %t8)
  call void @arraylist_free_int(ptr %t8)
  %1 = call i32 @getchar()
  ret i32 0
}
