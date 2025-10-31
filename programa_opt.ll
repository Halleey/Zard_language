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

define i32 @teste(i32 %x) {
entry:
  ret i32 4
}

define i32 @main() {
  %1 = call i32 (ptr, ...) @printf(ptr @.strInt, i32 10)
  %tmp4 = call i32 @teste(i32 10)
  %2 = call i32 (ptr, ...) @printf(ptr @.strInt, i32 %tmp4)
  %3 = call i32 (ptr, ...) @printf(ptr @.strInt, i32 10)
  %4 = call i32 @getchar()
  ret i32 0
}
