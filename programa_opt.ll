; ModuleID = 'programa.ll'
source_filename = "programa.ll"

%Set = type { ptr, ptr }

@.strChar = private constant [3 x i8] c"%c\00"
@.strTrue = private constant [6 x i8] c"true\0A\00"
@.strFalse = private constant [7 x i8] c"false\0A\00"
@.strInt = private constant [4 x i8] c"%d\0A\00"
@.strDouble = private constant [4 x i8] c"%f\0A\00"
@.strFloat = private constant [4 x i8] c"%f\0A\00"
@.strStr = private constant [4 x i8] c"%s\0A\00"
@.strEmpty = private constant [1 x i8] zeroinitializer
@.str0 = private constant [5 x i8] c"zard\00"
@.str1 = private constant [25 x i8] c"=== Conte\C3\BAdo do Set ===\00"
@.str2 = private constant [6 x i8] c"set T\00"

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

declare ptr @arraylist_create_int(i64)

declare void @arraylist_add_int(ptr, i32)

declare void @arraylist_addAll_int(ptr, ptr, i64)

declare void @arraylist_print_int(ptr)

declare void @arraylist_clear_int(ptr)

declare void @arraylist_free_int(ptr)

declare i32 @arraylist_get_int(ptr, i64, ptr)

declare void @arraylist_remove_int(ptr, i64)

declare i32 @arraylist_size_int(ptr)

define void @print_Set(ptr %p) {
entry:
  %val0 = load ptr, ptr %p, align 8
  call void @printString(ptr %val0)
  %f1 = getelementptr inbounds %Set, ptr %p, i32 0, i32 1
  %val1 = load ptr, ptr %f1, align 8
  call void @arraylist_print_int(ptr %val1)
  ret void
}

define ptr @Set_add(ptr %s, i32 %value) {
entry:
  %tmp5 = getelementptr inbounds %Set, ptr %s, i32 0, i32 1
  %tmp6 = load ptr, ptr %tmp5, align 8
  call void @arraylist_add_int(ptr %tmp6, i32 %value)
  ret ptr %s
}

define i32 @main() {
  %tmp9 = alloca %Set, align 8
  %tmp10 = call ptr @createString(ptr null)
  store ptr %tmp10, ptr %tmp9, align 8
  %tmp12 = call ptr @arraylist_create_int(i64 10)
  %tmp13 = getelementptr inbounds %Set, ptr %tmp9, i32 0, i32 1
  store ptr %tmp12, ptr %tmp13, align 8
  %tmp14 = alloca %Set, align 8
  %tmp16 = call ptr @createString(ptr @.str0)
  store ptr %tmp16, ptr %tmp14, align 8
  %tmp18 = call ptr @arraylist_create_int(i64 4)
  %tmp19 = alloca i32, i64 3, align 4
  store i32 3, ptr %tmp19, align 4
  %tmp23 = getelementptr inbounds i32, ptr %tmp19, i64 1
  store i32 4, ptr %tmp23, align 4
  %tmp25 = getelementptr inbounds i32, ptr %tmp19, i64 2
  store i32 5, ptr %tmp25, align 4
  call void @arraylist_addAll_int(ptr %tmp18, ptr %tmp19, i64 3)
  %tmp26 = getelementptr inbounds %Set, ptr %tmp14, i32 0, i32 1
  store ptr %tmp18, ptr %tmp26, align 8
  %tmp29 = call ptr @Set_add(ptr %tmp14, i32 2)
  %tmp32 = call ptr @Set_add(ptr %tmp9, i32 1)
  %tmp35 = call ptr @Set_add(ptr %tmp9, i32 22)
  %tmp38 = call ptr @Set_add(ptr %tmp9, i32 99)
  %1 = call i32 (ptr, ...) @printf(ptr @.strStr, ptr @.str1)
  %tmp43 = load ptr, ptr %tmp13, align 8
  call void @arraylist_print_int(ptr %tmp43)
  %2 = call i32 (ptr, ...) @printf(ptr @.strStr, ptr @.str2)
  call void @print_Set(ptr %tmp14)
  %3 = call i32 @getchar()
  ret i32 0
}
