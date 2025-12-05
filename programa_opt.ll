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
@.str0 = private constant [15 x i8] c"n\C3\A3o era igual\00"
@.str1 = private constant [26 x i8] c"continua\C3\A7\C3\A3o do programa\00"

declare i32 @printf(ptr, ...)

declare i32 @getchar()

declare void @printString(ptr)

declare ptr @malloc(i64)

declare void @setString(ptr)

declare ptr @createString(ptr)

declare i1 @strcmp_eq(ptr, ptr)

declare i1 @strcmp_neq(ptr, ptr)

define i32 @main() {
  br i1 true, label %then_0, label %else_0

then_0:                                           ; preds = %0
  ret i32 0

1:                                                ; No predecessors!
  br label %endif_0

else_0:                                           ; preds = %0
  %2 = call i32 (ptr, ...) @printf(ptr @.strStr, ptr @.str0)
  br label %endif_0

endif_0:                                          ; preds = %else_0, %1
  %3 = call i32 (ptr, ...) @printf(ptr @.strStr, ptr @.str1)
  %4 = call i32 @getchar()
  ret i32 0
}
