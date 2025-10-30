; ModuleID = 'programa.ll'
source_filename = "programa.ll"

%Set = type { ptr }

@.strChar = private constant [3 x i8] c"%c\00"
@.strTrue = private constant [6 x i8] c"true\0A\00"
@.strFalse = private constant [7 x i8] c"false\0A\00"
@.strInt = private constant [4 x i8] c"%d\0A\00"
@.strDouble = private constant [4 x i8] c"%f\0A\00"
@.strStr = private constant [4 x i8] c"%s\0A\00"
@.strEmpty = private constant [1 x i8] zeroinitializer
@.str0 = private constant [6 x i8] c"teste\00"
@.str1 = private constant [6 x i8] c"hello\00"
@.str2 = private constant [3 x i8] c"30\00"
@.str3 = private constant [18 x i8] c"Elementos do Set:\00"
@.str4 = private constant [9 x i8] c"Tamanho:\00"
@.str5 = private constant [19 x i8] c"Primeiro elemento:\00"
@.str6 = private constant [25 x i8] c"Ap\C3\B3s remover \C3\ADndice 1:\00"

declare i32 @printf(ptr, ...)

declare i32 @getchar()

declare void @printString(ptr)

declare ptr @malloc(i64)

declare void @setString(ptr, ptr)

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

declare void @arraylist_add_string(ptr, ptr)

declare void @arraylist_addAll_string(ptr, ptr, i64)

declare void @arraylist_print_string(ptr)

declare void @arraylist_add_String(ptr, ptr)

declare void @arraylist_addAll_String(ptr, ptr, i64)

declare void @removeItem(ptr, i64)

declare ptr @getItem(ptr, i64)

define void @print_Set(ptr %p) {
entry:
  ret void
}

define i32 @main() {
  %tmp0 = alloca %Set, align 8
  %tmp1 = call ptr @arraylist_create(i64 10)
  store ptr %tmp1, ptr %tmp0, align 8
  %tmp7 = call ptr @createString(ptr @.str0)
  call void @arraylist_add_String(ptr %tmp1, ptr %tmp7)
  %tmp11 = load ptr, ptr %tmp0, align 8
  %tmp13 = call ptr @createString(ptr @.str1)
  call void @arraylist_add_String(ptr %tmp11, ptr %tmp13)
  %tmp17 = load ptr, ptr %tmp0, align 8
  %tmp19 = call ptr @createString(ptr @.str2)
  call void @arraylist_add_String(ptr %tmp17, ptr %tmp19)
  %1 = call i32 (ptr, ...) @printf(ptr @.strStr, ptr @.str3)
  %tmp25 = load ptr, ptr %tmp0, align 8
  call void @arraylist_print_string(ptr %tmp25)
  %2 = call i32 (ptr, ...) @printf(ptr @.strStr, ptr @.str4)
  %tmp29 = load ptr, ptr %tmp0, align 8
  %tmp31 = call i32 @length(ptr %tmp29)
  %3 = call i32 (ptr, ...) @printf(ptr @.strInt, i32 %tmp31)
  %4 = call i32 (ptr, ...) @printf(ptr @.strStr, ptr @.str5)
  %tmp38 = call ptr @arraylist_get_ptr(ptr %tmp0, i64 0)
  call void @printString(ptr %tmp38)
  %tmp42 = load ptr, ptr %tmp0, align 8
  call void @removeItem(ptr %tmp42, i64 1)
  %5 = call i32 (ptr, ...) @printf(ptr @.strStr, ptr @.str6)
  %tmp49 = load ptr, ptr %tmp0, align 8
  call void @arraylist_print_string(ptr %tmp49)
  %6 = call i32 @getchar()
  ret i32 0
}
