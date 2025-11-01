; ModuleID = 'programa.ll'
source_filename = "programa.ll"

@.strChar = private constant [3 x i8] c"%c\00"
@.strTrue = private constant [6 x i8] c"true\0A\00"
@.strFalse = private constant [7 x i8] c"false\0A\00"
@.strInt = private constant [4 x i8] c"%d\0A\00"
@.strDouble = private constant [4 x i8] c"%f\0A\00"
@.strStr = private constant [4 x i8] c"%s\0A\00"
@.strEmpty = private constant [1 x i8] zeroinitializer
@.str0 = private constant [15 x i8] c"insira um nome\00"
@.str1 = private constant [1 x i8] zeroinitializer
@.str2 = private constant [6 x i8] c"teste\00"
@.str3 = private constant [20 x i8] c"uaba laba dubi dubi\00"
@.str4 = private constant [9 x i8] c"testando\00"

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

declare i32 @inputInt(ptr)

declare i8 @inputChar(i8)

declare double @inputDouble(ptr)

declare i1 @inputBool(ptr)

declare ptr @inputString(ptr)

declare void @arraylist_add_string(ptr, ptr)

declare void @arraylist_addAll_string(ptr, ptr, i64)

declare void @arraylist_print_string(ptr)

declare void @arraylist_add_String(ptr, ptr)

declare void @arraylist_addAll_String(ptr, ptr, i64)

declare void @removeItem(ptr, i64)

declare ptr @getItem(ptr, i64)

define ptr @alterName(ptr %name) {
entry:
  %tmp0 = call ptr @createString(ptr @.str3)
  ret ptr %tmp0
}

define i32 @main() {
  %1 = call i32 (ptr, ...) @printf(ptr @.strStr, ptr @.str0)
  %tmp4 = call ptr @inputString(ptr null)
  %tmp5 = call ptr @createString(ptr %tmp4)
  call void @printString(ptr %tmp5)
  %tmp7 = call ptr @arraylist_create(i64 4)
  %tmp10 = call ptr @createString(ptr @.str2)
  call void @arraylist_add_String(ptr %tmp7, ptr %tmp10)
  call void @arraylist_add_String(ptr %tmp7, ptr %tmp5)
  call void @arraylist_print_string(ptr %tmp7)
  %tmp15 = call ptr @alterName(ptr %tmp5)
  call void @printString(ptr %tmp15)
  call void @printString(ptr %tmp5)
  %tmp17 = call ptr @createString(ptr @.str4)
  call void @printString(ptr %tmp17)
  call void @freeList(ptr %tmp7)
  %2 = call i32 @getchar()
  ret i32 0
}
