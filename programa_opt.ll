; ModuleID = 'programa.ll'
source_filename = "programa.ll"

%Pessoa = type { ptr, i32 }

@.strTrue = private constant [6 x i8] c"true\0A\00"
@.strFalse = private constant [7 x i8] c"false\0A\00"
@.strInt = private constant [4 x i8] c"%d\0A\00"
@.strDouble = private constant [4 x i8] c"%f\0A\00"
@.strStr = private constant [4 x i8] c"%s\0A\00"
@.strEmpty = private constant [1 x i8] zeroinitializer
@.str0 = private constant [6 x i8] c"teste\00"
@.str1 = private constant [1 x i8] zeroinitializer

define ptr @f_addNames(ptr %nomes) {
entry:
  %t2 = call ptr @createString(ptr @.str0)
  call void @arraylist_add_String(ptr %nomes, ptr %t2)
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

declare i32 @inputInt(ptr)

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

define void @print_Pessoa(ptr %p) {
entry:
  %val0 = load ptr, ptr %p, align 8
  call void @printString(ptr %val0)
  %f1 = getelementptr inbounds %Pessoa, ptr %p, i32 0, i32 1
  %val1 = load i32, ptr %f1, align 4
  %0 = call i32 (ptr, ...) @printf(ptr @.strInt, i32 %val1)
  ret void
}

define ptr @addPeople(ptr %p) {
entry:
  ret ptr %p
}

define i32 @main() {
  %t6 = alloca %Pessoa, align 8
  %t7 = call ptr @createString(ptr @.str1)
  store ptr %t7, ptr %t6, align 8
  %t10 = getelementptr inbounds %Pessoa, ptr %t6, i32 0, i32 1
  store i32 19, ptr %t10, align 4
  %t14 = call ptr @inputString(ptr null)
  %t15 = call ptr @createString(ptr %t14)
  store ptr %t15, ptr %t6, align 8
  %t17 = call ptr @arraylist_create(i64 4)
  %t20 = call ptr @addPeople(ptr %t6)
  call void @print_Pessoa(ptr %t20)
  call void @freeList(ptr %t17)
  %1 = call i32 @getchar()
  ret i32 0
}
