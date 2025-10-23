; ModuleID = 'programa.ll'
source_filename = "programa.ll"

%Set = type { ptr }
%String = type { ptr, i64 }

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
  %t0 = alloca %Set, align 8
  %t1 = call ptr @arraylist_create(i64 10)
  %t2 = getelementptr inbounds %Set, ptr %t0, i32 0, i32 0
  store ptr %t1, ptr %t2, align 8
  %t3 = getelementptr inbounds [16 x i8], ptr @.str0, i32 0, i32 0
  %1 = call i32 (ptr, ...) @printf(ptr @.strStr, ptr %t3)
  %t5 = call ptr @inputString(ptr null)
  %t6 = call ptr @createString(ptr %t5)
  %t7 = call ptr @malloc(i64 ptrtoint (ptr getelementptr (%String, ptr null, i32 1) to i64))
  %t8 = bitcast ptr %t7 to ptr
  %t9 = bitcast ptr @.str2 to ptr
  %t10 = getelementptr inbounds %String, ptr %t8, i32 0, i32 0
  store ptr %t9, ptr %t10, align 8
  %t11 = getelementptr inbounds %String, ptr %t8, i32 0, i32 1
  store i64 4, ptr %t11, align 4
  %t13 = getelementptr inbounds %Set, ptr %t0, i32 0, i32 0
  %t14 = load ptr, ptr %t13, align 8
  %t17 = bitcast ptr %t14 to ptr
  %t16 = call ptr @createString(ptr @.str3)
  call void @arraylist_add_String(ptr %t17, ptr %t16)
  %t19 = getelementptr inbounds %Set, ptr %t0, i32 0, i32 0
  %t20 = load ptr, ptr %t19, align 8
  %t22 = bitcast ptr %t20 to ptr
  call void @arraylist_add_String(ptr %t22, ptr %t6)
  %t24 = getelementptr inbounds %Set, ptr %t0, i32 0, i32 0
  %t25 = load ptr, ptr %t24, align 8
  %t27 = bitcast ptr %t25 to ptr
  call void @arraylist_add_String(ptr %t27, ptr %t8)
  %t28 = getelementptr inbounds [18 x i8], ptr @.str4, i32 0, i32 0
  %2 = call i32 (ptr, ...) @printf(ptr @.strStr, ptr %t28)
  %t31 = getelementptr inbounds %Set, ptr %t0, i32 0, i32 0
  %t32 = load ptr, ptr %t31, align 8
  call void @arraylist_print_string(ptr %t32)
  %t33 = getelementptr inbounds [9 x i8], ptr @.str5, i32 0, i32 0
  %3 = call i32 (ptr, ...) @printf(ptr @.strStr, ptr %t33)
  %t35 = getelementptr inbounds %Set, ptr %t0, i32 0, i32 0
  %t36 = load ptr, ptr %t35, align 8
  %t37 = bitcast ptr %t36 to ptr
  %t38 = call i32 @length(ptr %t37)
  %4 = call i32 (ptr, ...) @printf(ptr @.strInt, i32 %t38)
  %t39 = getelementptr inbounds [4 x i8], ptr @.str6, i32 0, i32 0
  %5 = call i32 (ptr, ...) @printf(ptr @.strStr, ptr %t39)
  %t43 = add i32 0, 0
  %t44 = zext i32 %t43 to i64
  %t45 = call ptr @arraylist_get_ptr(ptr %t0, i64 %t44)
  %t46 = bitcast ptr %t45 to ptr
  call void @printString(ptr %t46)
  %t47 = getelementptr inbounds [7 x i8], ptr @.str7, i32 0, i32 0
  %6 = call i32 (ptr, ...) @printf(ptr @.strStr, ptr %t47)
  %t49 = getelementptr inbounds %Set, ptr %t0, i32 0, i32 0
  %t50 = load ptr, ptr %t49, align 8
  %t51 = add i32 0, 0
  %t52 = zext i32 %t51 to i64
  call void @removeItem(ptr %t50, i64 %t52)
  %t55 = getelementptr inbounds %Set, ptr %t0, i32 0, i32 0
  %t56 = load ptr, ptr %t55, align 8
  call void @arraylist_print_string(ptr %t56)
  %t57 = getelementptr inbounds [13 x i8], ptr @.str8, i32 0, i32 0
  %7 = call i32 (ptr, ...) @printf(ptr @.strStr, ptr %t57)
  %t59 = getelementptr inbounds %Set, ptr %t0, i32 0, i32 0
  %t60 = load ptr, ptr %t59, align 8
  call void @clearList(ptr %t60)
  %t63 = getelementptr inbounds %Set, ptr %t0, i32 0, i32 0
  %t64 = load ptr, ptr %t63, align 8
  call void @arraylist_print_string(ptr %t64)
  %8 = call i32 @getchar()
  ret i32 0
}
