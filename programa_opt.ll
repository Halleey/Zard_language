; ModuleID = 'programa.ll'
source_filename = "programa.ll"

%Pessoa = type { ptr, i32 }

@.strTrue = private constant [6 x i8] c"true\0A\00"
@.strFalse = private constant [7 x i8] c"false\0A\00"
@.strInt = private constant [4 x i8] c"%d\0A\00"
@.strDouble = private constant [4 x i8] c"%f\0A\00"
@.strStr = private constant [4 x i8] c"%s\0A\00"
@.strEmpty = private constant [1 x i8] zeroinitializer
@.str0 = private constant [1 x i8] zeroinitializer

declare i32 @printf(ptr, ...)

declare i32 @getchar()

declare void @printString(ptr)

declare ptr @malloc(i64)

declare void @setString(ptr, ptr)

declare ptr @createString(ptr)

declare i1 @strcmp_eq(ptr, ptr)

declare i1 @strcmp_neq(ptr, ptr)

declare i32 @inputInt(ptr)

declare double @inputDouble(ptr)

declare i1 @inputBool(ptr)

declare ptr @inputString(ptr)

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
  %t1 = alloca %Pessoa, align 8
  %t2 = call ptr @createString(ptr @.str0)
  store ptr %t2, ptr %t1, align 8
  %t5 = getelementptr inbounds %Pessoa, ptr %t1, i32 0, i32 1
  store i32 19, ptr %t5, align 4
  %t9 = call ptr @inputString(ptr null)
  %t10 = call ptr @createString(ptr %t9)
  store ptr %t10, ptr %t1, align 8
  %t13 = call ptr @addPeople(ptr %t1)
  call void @print_Pessoa(ptr %t13)
  %1 = call i32 @getchar()
  ret i32 0
}
