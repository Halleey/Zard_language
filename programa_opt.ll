; ModuleID = 'programa.ll'
source_filename = "programa.ll"

%Set = type { ptr }

@.strTrue = private constant [6 x i8] c"true\0A\00"
@.strFalse = private constant [7 x i8] c"false\0A\00"
@.strInt = private constant [4 x i8] c"%d\0A\00"
@.strDouble = private constant [4 x i8] c"%f\0A\00"
@.strStr = private constant [4 x i8] c"%s\0A\00"
@.strEmpty = private constant [1 x i8] zeroinitializer
@.str0 = private constant [17 x i8] c"digite um numero\00"
@.str1 = private constant [1 x i8] zeroinitializer
@.str2 = private constant [18 x i8] c"Elementos do Set:\00"
@.str3 = private constant [9 x i8] c"Tamanho:\00"
@.str4 = private constant [4 x i8] c"get\00"
@.str5 = private constant [7 x i8] c"remove\00"
@.str6 = private constant [13 x i8] c"apos limpeza\00"

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

declare ptr @arraylist_create_double(i64)

declare void @arraylist_add_double(ptr, double)

declare void @arraylist_addAll_double(ptr, ptr, i64)

declare void @arraylist_print_double(ptr)

declare double @arraylist_get_double(ptr, i64, ptr)

declare void @arraylist_clear_double(ptr)

declare void @arraylist_remove_double(ptr, i64)

declare void @arraylist_free_double(ptr)

declare i32 @arraylist_size_double(ptr)

define void @print_Set(ptr %p) {
entry:
  ret void
}

define i32 @main() {
  %t0 = alloca %Set, align 8
  %t1 = call ptr @arraylist_create_double(i64 10)
  %t2 = getelementptr inbounds %Set, ptr %t0, i32 0, i32 0
  store ptr %t1, ptr %t2, align 8
  %t3 = getelementptr inbounds [17 x i8], ptr @.str0, i32 0, i32 0
  %1 = call i32 (ptr, ...) @printf(ptr @.strStr, ptr %t3)
  %t4 = call double @inputDouble(ptr null)
  %t5 = fadd double 0.000000e+00, 3.140000e+00
  %t11 = getelementptr inbounds %Set, ptr %t0, i32 0, i32 0
  %t12 = load ptr, ptr %t11, align 8
  %t13 = fadd double 0.000000e+00, 1.000000e+00
  call void @arraylist_add_double(ptr %t12, double %t13)
  %t19 = getelementptr inbounds %Set, ptr %t0, i32 0, i32 0
  %t20 = load ptr, ptr %t19, align 8
  call void @arraylist_add_double(ptr %t20, double %t4)
  %t27 = getelementptr inbounds %Set, ptr %t0, i32 0, i32 0
  %t28 = load ptr, ptr %t27, align 8
  call void @arraylist_add_double(ptr %t28, double %t5)
  %t30 = getelementptr inbounds [18 x i8], ptr @.str2, i32 0, i32 0
  %2 = call i32 (ptr, ...) @printf(ptr @.strStr, ptr %t30)
  %t33 = getelementptr inbounds %Set, ptr %t0, i32 0, i32 0
  %t34 = load ptr, ptr %t33, align 8
  call void @arraylist_print_double(ptr %t34)
  %t35 = getelementptr inbounds [9 x i8], ptr @.str3, i32 0, i32 0
  %3 = call i32 (ptr, ...) @printf(ptr @.strStr, ptr %t35)
  %t40 = getelementptr inbounds %Set, ptr %t0, i32 0, i32 0
  %t41 = load ptr, ptr %t40, align 8
  %t42 = call i32 @arraylist_size_double(ptr %t41)
  %4 = call i32 (ptr, ...) @printf(ptr @.strInt, i32 %t42)
  %t43 = getelementptr inbounds [4 x i8], ptr @.str4, i32 0, i32 0
  %5 = call i32 (ptr, ...) @printf(ptr @.strStr, ptr %t43)
  %t48 = getelementptr inbounds %Set, ptr %t0, i32 0, i32 0
  %t49 = load ptr, ptr %t48, align 8
  %t50 = add i32 0, 0
  %t51 = zext i32 %t50 to i64
  %t52 = alloca double, align 8
  %t53 = call double @arraylist_get_double(ptr %t49, i64 %t51, ptr %t52)
  %t54 = load double, ptr %t52, align 8
  %6 = call i32 (ptr, ...) @printf(ptr @.strDouble, double %t54)
  %t55 = getelementptr inbounds [7 x i8], ptr @.str5, i32 0, i32 0
  %7 = call i32 (ptr, ...) @printf(ptr @.strStr, ptr %t55)
  %t60 = getelementptr inbounds %Set, ptr %t0, i32 0, i32 0
  %t61 = load ptr, ptr %t60, align 8
  %t62 = add i32 0, 0
  %t63 = zext i32 %t62 to i64
  call void @arraylist_remove_double(ptr %t61, i64 %t63)
  %t66 = getelementptr inbounds %Set, ptr %t0, i32 0, i32 0
  %t67 = load ptr, ptr %t66, align 8
  call void @arraylist_print_double(ptr %t67)
  %t68 = getelementptr inbounds [13 x i8], ptr @.str6, i32 0, i32 0
  %8 = call i32 (ptr, ...) @printf(ptr @.strStr, ptr %t68)
  %t73 = getelementptr inbounds %Set, ptr %t0, i32 0, i32 0
  %t74 = load ptr, ptr %t73, align 8
  call void @arraylist_clear_double(ptr %t74)
  %t77 = getelementptr inbounds %Set, ptr %t0, i32 0, i32 0
  %t78 = load ptr, ptr %t77, align 8
  call void @arraylist_print_double(ptr %t78)
  %9 = call i32 @getchar()
  ret i32 0
}
