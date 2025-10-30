; ModuleID = 'programa.ll'
source_filename = "programa.ll"

%People = type { ptr, i32 }

@.strChar = private constant [3 x i8] c"%c\00"
@.strTrue = private constant [6 x i8] c"true\0A\00"
@.strFalse = private constant [7 x i8] c"false\0A\00"
@.strInt = private constant [4 x i8] c"%d\0A\00"
@.strDouble = private constant [4 x i8] c"%f\0A\00"
@.strStr = private constant [4 x i8] c"%s\0A\00"
@.strEmpty = private constant [1 x i8] zeroinitializer
@.str0 = private constant [6 x i8] c"teste\00"

declare i32 @printf(ptr, ...)

declare i32 @getchar()

declare void @printString(ptr)

declare ptr @malloc(i64)

declare void @setString(ptr, ptr)

declare ptr @createString(ptr)

declare i1 @strcmp_eq(ptr, ptr)

declare i1 @strcmp_neq(ptr, ptr)

define void @print_People(ptr %p) {
entry:
  %val0 = load ptr, ptr %p, align 8
  call void @printString(ptr %val0)
  %f1 = getelementptr inbounds %People, ptr %p, i32 0, i32 1
  %val1 = load i32, ptr %f1, align 4
  %0 = call i32 (ptr, ...) @printf(ptr @.strInt, i32 %val1)
  ret void
}

define ptr @createPeople(ptr %p) {
entry:
  %tmp3 = call ptr @createString(ptr @.str0)
  store ptr %tmp3, ptr %p, align 8
  %tmp6 = getelementptr inbounds %People, ptr %p, i32 0, i32 1
  store i32 19, ptr %tmp6, align 4
  ret ptr %p
}

define i32 @main() {
  %tmp10 = alloca %People, align 8
  %tmp11 = call ptr @createString(ptr null)
  store ptr %tmp11, ptr %tmp10, align 8
  %tmp13 = getelementptr inbounds %People, ptr %tmp10, i32 0, i32 1
  store i32 0, ptr %tmp13, align 4
  %tmp17 = call ptr @createString(ptr @.str0)
  store ptr %tmp17, ptr %tmp10, align 8
  store i32 220, ptr %tmp13, align 4
  call void @print_People(ptr %tmp10)
  %tmp25 = call ptr @createPeople(ptr %tmp10)
  call void @print_People(ptr %tmp25)
  %1 = call i32 @getchar()
  ret i32 0
}
