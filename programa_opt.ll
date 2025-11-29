; ModuleID = 'programa.ll'
source_filename = "programa.ll"

%Pessoa = type { ptr, i32 }

@.strChar = private constant [3 x i8] c"%c\00"
@.strTrue = private constant [6 x i8] c"true\0A\00"
@.strFalse = private constant [7 x i8] c"false\0A\00"
@.strInt = private constant [4 x i8] c"%d\0A\00"
@.strDouble = private constant [4 x i8] c"%f\0A\00"
@.strFloat = private constant [4 x i8] c"%f\0A\00"
@.strStr = private constant [4 x i8] c"%s\0A\00"
@.strEmpty = private constant [1 x i8] zeroinitializer
@.str0 = private constant [24 x i8] c"Ol\C3\A1 do m\C3\A9todo hello()\00"
@.str1 = private constant [5 x i8] c"Zard\00"

declare i32 @printf(ptr, ...)

declare i32 @getchar()

declare void @printString(ptr)

declare ptr @malloc(i64)

declare void @setString(ptr)

declare ptr @createString(ptr)

declare i1 @strcmp_eq(ptr, ptr)

declare i1 @strcmp_neq(ptr, ptr)

define void @print_Pessoa(ptr %p) {
entry:
  %v0 = load ptr, ptr %p, align 8
  call void @printString(ptr %v0)
  %f1 = getelementptr inbounds %Pessoa, ptr %p, i32 0, i32 1
  %v1 = load i32, ptr %f1, align 4
  %0 = call i32 (ptr, ...) @printf(ptr @.strInt, i32 %v1)
  ret void
}

define void @Pessoa_hello(ptr %s) {
entry:
  %0 = call i32 (ptr, ...) @printf(ptr @.strStr, ptr @.str0)
  ret void
}

define i32 @main() {
  %tmp1 = alloca %Pessoa, align 8
  %tmp2 = call ptr @createString(ptr null)
  store ptr %tmp2, ptr %tmp1, align 8
  %tmp4 = getelementptr inbounds %Pessoa, ptr %tmp1, i32 0, i32 1
  store i32 0, ptr %tmp4, align 4
  %tmp8 = call ptr @createString(ptr @.str1)
  store ptr %tmp8, ptr %tmp1, align 8
  store i32 22, ptr %tmp4, align 4
  %tmp15 = call ptr @Pessoa_hello(ptr %tmp1)
  %1 = call i32 @getchar()
  ret i32 0
}
