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
@.str0 = private constant [5 x i8] c"Berk\00"

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

define i32 @main() {
  %tmp0 = alloca %People, align 8
  %tmp2 = call ptr @createString(ptr @.str0)
  store ptr %tmp2, ptr %tmp0, align 8
  %tmp5 = getelementptr inbounds %People, ptr %tmp0, i32 0, i32 1
  store i32 220, ptr %tmp5, align 4
  %tmp7 = call ptr @createString(ptr null)
  call void @print_People(ptr %tmp0)
  %1 = call i32 @getchar()
  ret i32 0
}
