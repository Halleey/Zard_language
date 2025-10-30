; ModuleID = 'programa.ll'
source_filename = "programa.ll"

%String = type { ptr, i64 }

@.strChar = private constant [3 x i8] c"%c\00"
@.strTrue = private constant [6 x i8] c"true\0A\00"
@.strFalse = private constant [7 x i8] c"false\0A\00"
@.strInt = private constant [4 x i8] c"%d\0A\00"
@.strDouble = private constant [4 x i8] c"%f\0A\00"
@.strStr = private constant [4 x i8] c"%s\0A\00"
@.strEmpty = private constant [1 x i8] zeroinitializer
@.str0 = private constant [7 x i8] c"safety\00"
@.str1 = private constant [6 x i8] c"teste\00"
@.str2 = private constant [3 x i8] c"ok\00"

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

declare void @arraylist_add_string(ptr, ptr)

declare void @arraylist_addAll_string(ptr, ptr, i64)

declare void @arraylist_print_string(ptr)

declare void @arraylist_add_String(ptr, ptr)

declare void @arraylist_addAll_String(ptr, ptr, i64)

declare void @removeItem(ptr, i64)

declare ptr @getItem(ptr, i64)

define ptr @teste(ptr %list) {
entry:
  %tmp2 = call ptr @createString(ptr @.str2)
  call void @arraylist_add_String(ptr %list, ptr %tmp2)
  ret ptr %list
}

define i32 @main() {
  %tmp5 = call ptr @malloc(i64 ptrtoint (ptr getelementptr (%String, ptr null, i32 1) to i64))
  store ptr @.str0, ptr %tmp5, align 8
  %tmp9 = getelementptr inbounds %String, ptr %tmp5, i32 0, i32 1
  store i64 6, ptr %tmp9, align 4
  %tmp10 = call ptr @arraylist_create(i64 4)
  %tmp13 = call ptr @createString(ptr @.str1)
  call void @arraylist_add_String(ptr %tmp10, ptr %tmp13)
  call void @arraylist_add_String(ptr %tmp10, ptr %tmp5)
  %tmp19 = call ptr @teste(ptr %tmp10)
  call void @arraylist_print_string(ptr %tmp19)
  call void @freeList(ptr %tmp10)
  %1 = call i32 @getchar()
  ret i32 0
}
