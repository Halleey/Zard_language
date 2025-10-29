; ModuleID = 'programa.ll'
source_filename = "programa.ll"

@.strChar = private constant [3 x i8] c"%c\00"
@.strTrue = private constant [6 x i8] c"true\0A\00"
@.strFalse = private constant [7 x i8] c"false\0A\00"
@.strInt = private constant [4 x i8] c"%d\0A\00"
@.strDouble = private constant [4 x i8] c"%f\0A\00"
@.strStr = private constant [4 x i8] c"%s\0A\00"
@.strEmpty = private constant [1 x i8] zeroinitializer
@.str0 = private constant [11 x i8] c"bulbassaur\00"
@.str1 = private constant [11 x i8] c"charmander\00"
@.str2 = private constant [9 x i8] c"squirtle\00"
@.str3 = private constant [9 x i8] c"\C3\A9 igual\00"
@.str4 = private constant [15 x i8] c"n\C3\A3o era igual\00"

define ptr @l_addNames(ptr %nomes) {
entry:
  %t2 = call ptr @createString(ptr @.str0)
  %t4 = alloca ptr, i64 3, align 8
  %t6 = call ptr @createString(ptr @.str0)
  store ptr %t6, ptr %t4, align 8
  %t9 = call ptr @createString(ptr @.str1)
  %t10 = getelementptr inbounds ptr, ptr %t4, i64 1
  store ptr %t9, ptr %t10, align 8
  %t12 = call ptr @createString(ptr @.str2)
  %t13 = getelementptr inbounds ptr, ptr %t4, i64 2
  store ptr %t12, ptr %t13, align 8
  call void @arraylist_addAll_String(ptr %nomes, ptr %t4, i64 3)
  ret ptr %nomes
}

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

define i32 @main() {
  br i1 true, label %then_0, label %else_0

then_0:                                           ; preds = %0
  %1 = call i32 (ptr, ...) @printf(ptr @.strStr, ptr @.str3)
  br label %endif_0

else_0:                                           ; preds = %0
  %2 = call i32 (ptr, ...) @printf(ptr @.strStr, ptr @.str4)
  br label %endif_0

endif_0:                                          ; preds = %else_0, %then_0
  %3 = call i32 @getchar()
  ret i32 0
}
