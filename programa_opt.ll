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
@.str0 = private constant [5 x i8] c"zard\00"
@.str1 = private constant [7 x i8] c"halley\00"

declare i32 @printf(ptr, ...)

declare i32 @getchar()

declare void @printString(ptr)

declare ptr @malloc(i64)

declare void @setString(ptr)

declare void @printString_noNL(ptr)

declare ptr @createString(ptr)

declare i1 @strcmp_eq(ptr, ptr)

declare i1 @strcmp_neq(ptr, ptr)

define void @print_People(ptr %p) {
entry:
  %v0 = load ptr, ptr %p, align 8
  call void @printString(ptr %v0)
  ret void
}

define void @alterar(ptr %t) {
entry:
  %tmp3 = call ptr @createString(ptr @.str1)
  store ptr %tmp3, ptr %t, align 8
  ret void
}

define i32 @main() {
  %tmp5 = call ptr @malloc(i64 8)
  %tmp8 = call ptr @createString(ptr @.str0)
  store ptr %tmp8, ptr %tmp5, align 8
  call void @print_People(ptr %tmp5)
  call void @alterar(ptr %tmp5)
  call void @print_People(ptr %tmp5)
  %1 = call i32 @getchar()
  ret i32 0
}
