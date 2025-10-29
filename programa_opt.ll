; ModuleID = 'programa.ll'
source_filename = "programa.ll"

%Set = type { ptr }
%String = type { ptr, i64 }

@.strChar = private constant [3 x i8] c"%c\00"
@.strTrue = private constant [6 x i8] c"true\0A\00"
@.strFalse = private constant [7 x i8] c"false\0A\00"
@.strInt = private constant [4 x i8] c"%d\0A\00"
@.strDouble = private constant [4 x i8] c"%f\0A\00"
@.strStr = private constant [4 x i8] c"%s\0A\00"
@.strEmpty = private constant [1 x i8] zeroinitializer
@.str0 = private constant [16 x i8] c"digite seu nome\00"
@.str1 = private constant [1 x i8] zeroinitializer
@.str2 = private constant [5 x i8] c"zhum\00"
@.str3 = private constant [2 x i8] c"1\00"
@.str4 = private constant [18 x i8] c"Elementos do Set:\00"
@.str5 = private constant [9 x i8] c"Tamanho:\00"
@.str6 = private constant [4 x i8] c"get\00"
@.str7 = private constant [7 x i8] c"remove\00"
@.str8 = private constant [13 x i8] c"apos limpeza\00"

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

declare i32 @inputInt(ptr)

declare i8 @inputChar(i8)

declare double @inputDouble(ptr)

declare i1 @inputBool(ptr)

declare ptr @inputString(ptr)

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
  %1 = call i32 (ptr, ...) @printf(ptr @.strStr, ptr @.str0)
  %tmp5 = call ptr @inputString(ptr null)
  %tmp6 = call ptr @createString(ptr %tmp5)
  %tmp7 = call ptr @malloc(i64 ptrtoint (ptr getelementptr (%String, ptr null, i32 1) to i64))
  store ptr @.str2, ptr %tmp7, align 8
  %tmp11 = getelementptr inbounds %String, ptr %tmp7, i32 0, i32 1
  store i64 4, ptr %tmp11, align 4
  %tmp14 = load ptr, ptr %tmp0, align 8
  %tmp16 = call ptr @createString(ptr @.str3)
  call void @arraylist_add_String(ptr %tmp14, ptr %tmp16)
  %tmp20 = load ptr, ptr %tmp0, align 8
  call void @arraylist_add_String(ptr %tmp20, ptr %tmp6)
  %tmp25 = load ptr, ptr %tmp0, align 8
  call void @arraylist_add_String(ptr %tmp25, ptr %tmp7)
  %2 = call i32 (ptr, ...) @printf(ptr @.strStr, ptr @.str4)
  %tmp32 = load ptr, ptr %tmp0, align 8
  call void @arraylist_print_string(ptr %tmp32)
  %3 = call i32 (ptr, ...) @printf(ptr @.strStr, ptr @.str5)
  %tmp36 = load ptr, ptr %tmp0, align 8
  %tmp38 = call i32 @length(ptr %tmp36)
  %4 = call i32 (ptr, ...) @printf(ptr @.strInt, i32 %tmp38)
  %5 = call i32 (ptr, ...) @printf(ptr @.strStr, ptr @.str6)
  %tmp45 = call ptr @arraylist_get_ptr(ptr %tmp0, i64 0)
  call void @printString(ptr %tmp45)
  %6 = call i32 (ptr, ...) @printf(ptr @.strStr, ptr @.str7)
  %tmp50 = load ptr, ptr %tmp0, align 8
  call void @removeItem(ptr %tmp50, i64 0)
  %tmp56 = load ptr, ptr %tmp0, align 8
  call void @arraylist_print_string(ptr %tmp56)
  %7 = call i32 (ptr, ...) @printf(ptr @.strStr, ptr @.str8)
  %tmp60 = load ptr, ptr %tmp0, align 8
  call void @clearList(ptr %tmp60)
  %tmp64 = load ptr, ptr %tmp0, align 8
  call void @arraylist_print_string(ptr %tmp64)
  %8 = call i32 @getchar()
  ret i32 0
}
