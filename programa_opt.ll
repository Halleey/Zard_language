; ModuleID = 'programa.ll'
source_filename = "programa.ll"

%People = type { ptr, i32 }
%String = type { ptr, i64 }

@.strTrue = private constant [6 x i8] c"true\0A\00"
@.strFalse = private constant [7 x i8] c"false\0A\00"
@.strInt = private constant [4 x i8] c"%d\0A\00"
@.strDouble = private constant [4 x i8] c"%f\0A\00"
@.strStr = private constant [4 x i8] c"%s\0A\00"
@.strEmpty = private constant [1 x i8] zeroinitializer
@.str0 = private constant [5 x i8] c"zard\00"
@.str1 = private constant [9 x i8] c"testando\00"
@.str2 = private constant [7 x i8] c"trying\00"
@.str3 = private constant [5 x i8] c"miss\00"

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

declare ptr @arraylist_create_int(i64)

declare void @arraylist_add_int(ptr, i32)

declare void @arraylist_addAll_int(ptr, ptr, i64)

declare void @arraylist_print_int(ptr)

declare void @arraylist_clear_int(ptr)

declare void @arraylist_free_int(ptr)

declare i32 @arraylist_get_int(ptr, i64, ptr)

declare void @arraylist_remove_int(ptr, i64)

declare i32 @arraylist_size_int(ptr)

declare void @arraylist_add_string(ptr, ptr)

declare void @arraylist_addAll_string(ptr, ptr, i64)

declare void @arraylist_print_string(ptr)

declare void @arraylist_add_String(ptr, ptr)

declare void @arraylist_addAll_String(ptr, ptr, i64)

declare void @removeItem(ptr, i64)

declare ptr @getItem(ptr, i64)

define void @print_People(ptr %p) {
entry:
  %f0 = getelementptr inbounds %People, ptr %p, i32 0, i32 0
  %val0 = load ptr, ptr %f0, align 8
  call void @printString(ptr %val0)
  %f1 = getelementptr inbounds %People, ptr %p, i32 0, i32 1
  %val1 = load i32, ptr %f1, align 4
  %0 = call i32 (ptr, ...) @printf(ptr @.strInt, i32 %val1)
  ret void
}

define i32 @printNumber(i32 %x) {
entry:
  ret i32 %x
}

define ptr @addPeople(ptr %nomes) {
entry:
  %t3 = call ptr @createString(ptr @.str0)
  %t4 = bitcast ptr %nomes to ptr
  %t5 = alloca ptr, i64 4, align 8
  %t7 = call ptr @createString(ptr @.str0)
  %t8 = getelementptr inbounds ptr, ptr %t5, i64 0
  store ptr %t7, ptr %t8, align 8
  %t10 = call ptr @createString(ptr @.str1)
  %t11 = getelementptr inbounds ptr, ptr %t5, i64 1
  store ptr %t10, ptr %t11, align 8
  %t13 = call ptr @createString(ptr @.str2)
  %t14 = getelementptr inbounds ptr, ptr %t5, i64 2
  store ptr %t13, ptr %t14, align 8
  %t16 = call ptr @createString(ptr @.str3)
  %t17 = getelementptr inbounds ptr, ptr %t5, i64 3
  store ptr %t16, ptr %t17, align 8
  call void @arraylist_addAll_String(ptr %t4, ptr %t5, i64 4)
  %t19 = alloca %String, align 8
  %t20 = getelementptr inbounds %String, ptr %t19, i32 0, i32 0
  store ptr %nomes, ptr %t20, align 8
  %t21 = getelementptr inbounds %String, ptr %t19, i32 0, i32 1
  store i64 0, ptr %t21, align 4
  ret ptr %t19
}

define ptr @addNum(ptr %numeros) {
entry:
  %t25 = alloca i32, i64 3, align 4
  %t26 = add i32 0, 3
  %t27 = getelementptr inbounds i32, ptr %t25, i64 0
  store i32 %t26, ptr %t27, align 4
  %t28 = add i32 0, 4
  %t29 = getelementptr inbounds i32, ptr %t25, i64 1
  store i32 %t28, ptr %t29, align 4
  %t30 = add i32 0, 2
  %t31 = getelementptr inbounds i32, ptr %t25, i64 2
  store i32 %t30, ptr %t31, align 4
  call void @arraylist_addAll_int(ptr %numeros, ptr %t25, i64 3)
  ret ptr %numeros
}

define i32 @main() {
  %t33 = call ptr @arraylist_create(i64 4)
  %t35 = call ptr @arraylist_create_int(i64 4)
  %t37 = call ptr @addPeople(ptr %t33)
  %t39 = call ptr @addNum(ptr %t35)
  call void @arraylist_print_int(ptr %t35)
  %t41 = add i32 0, 15
  %t42 = call i32 @printNumber(i32 %t41)
  %1 = call i32 (ptr, ...) @printf(ptr @.strInt, i32 %t42)
  call void @arraylist_print_string(ptr %t33)
  call void @arraylist_free_int(ptr %t35)
  %t46 = bitcast ptr %t33 to ptr
  call void @freeList(ptr %t46)
  %2 = call i32 @getchar()
  ret i32 0
}
