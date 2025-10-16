; ModuleID = 'programa.ll'
source_filename = "programa.ll"

%String = type { ptr, i64 }

@.strInt = private constant [4 x i8] c"%d\0A\00"
@.strDouble = private constant [4 x i8] c"%f\0A\00"
@.strStr = private constant [4 x i8] c"%s\0A\00"
@.str0 = private constant [5 x i8] c"zard\00"
@.str1 = private constant [12 x i8] c"hello world\00"

declare i32 @printf(ptr, ...)

declare i32 @getchar()

declare void @printString(ptr)

declare ptr @malloc(i64)

declare void @setString(ptr, ptr)

declare ptr @createString(ptr)

declare ptr @arraylist_create(i64)

declare void @clearList(ptr)

declare void @freeList(ptr)

declare i1 @strcmp_eq(ptr, ptr)

declare i1 @strcmp_neq(ptr, ptr)

define ptr @hi() {
entry:
  %t1 = call ptr @malloc(i64 16)
  store ptr @.str1, ptr %t1, align 8
  %t4 = getelementptr inbounds %String, ptr %t1, i64 0, i32 1
  store i64 11, ptr %t4, align 4
  ret ptr %t1
}

define i32 @main() {
  %t5 = call ptr @malloc(i64 16)
  store ptr @.str0, ptr %t5, align 8
  %t9 = getelementptr inbounds %String, ptr %t5, i64 0, i32 1
  store i64 4, ptr %t9, align 4
  call void @printString(ptr nonnull %t5)
  %t11 = call ptr @hi()
  call void @printString(ptr %t11)
  %1 = call i32 @getchar()
  ret i32 0
}
