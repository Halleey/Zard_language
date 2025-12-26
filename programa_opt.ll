; ModuleID = 'programa.ll'
source_filename = "programa.ll"

@.strChar = private constant [3 x i8] c"%c\00"
@.strTrue = private constant [6 x i8] c"true\0A\00"
@.strFalse = private constant [7 x i8] c"false\0A\00"
@.strInt = private constant [4 x i8] c"%d\0A\00"
@.strInt_noNL = private constant [3 x i8] c"%d\00"
@.strDouble = private constant [4 x i8] c"%f\0A\00"
@.strDouble_noNL = private constant [3 x i8] c"%f\00"
@.strFloat = private constant [4 x i8] c"%f\0A\00"
@.strFloat_noNL = private constant [3 x i8] c"%f\00"
@.strStr = private constant [4 x i8] c"%s\0A\00"
@.strStr_noNL = private constant [3 x i8] c"%s\00"
@.strEmpty = private constant [1 x i8] zeroinitializer

declare i32 @printf(ptr, ...)

declare i32 @getchar()

declare void @printString(ptr)

declare ptr @malloc(i64)

declare void @setString(ptr)

declare void @printString_noNL(ptr)

declare ptr @createString(ptr)

declare i1 @strcmp_eq(ptr, ptr)

declare i1 @strcmp_neq(ptr, ptr)

define i32 @main() {
  %1 = call i32 (ptr, ...) @printf(ptr @.strInt_noNL, i32 4)
  %2 = call i32 @getchar()
  ret i32 0
}
