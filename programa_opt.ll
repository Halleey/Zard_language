; ModuleID = 'programa.ll'
source_filename = "programa.ll"

%String = type { ptr, i64 }

@.strChar = private constant [3 x i8] c"%c\00"
@.strTrue = private constant [6 x i8] c"true\0A\00"
@.strFalse = private constant [7 x i8] c"false\0A\00"
@.strInt = private constant [4 x i8] c"%d\0A\00"
@.strDouble = private constant [4 x i8] c"%f\0A\00"
@.strFloat = private constant [4 x i8] c"%f\0A\00"
@.strStr = private constant [4 x i8] c"%s\0A\00"
@.strEmpty = private constant [1 x i8] zeroinitializer
@.str0 = private constant [5 x i8] c"zard\00"
@.str1 = private constant [10 x i8] c"o nome \C3\A9\00"

declare i32 @printf(ptr, ...)

declare i32 @getchar()

declare void @printString(ptr)

declare ptr @malloc(i64)

declare void @setString(ptr)

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

define void @print_Set(ptr %p) {
entry:
  ret void
}

define i32 @main() {
  %tmp0 = call ptr @malloc(i64 ptrtoint (ptr getelementptr (%String, ptr null, i32 1) to i64))
  store ptr @.str0, ptr %tmp0, align 8
  %tmp4 = getelementptr inbounds %String, ptr %tmp0, i32 0, i32 1
  store i64 4, ptr %tmp4, align 4
  %1 = call i32 (ptr, ...) @printf(ptr @.strStr, ptr @.str1)
  call void @printString(ptr %tmp0)
  %2 = call i32 @getchar()
  ret i32 0
}
