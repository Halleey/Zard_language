; ModuleID = 'programa.ll'
source_filename = "programa.ll"

%Set = type { ptr, i32 }

@.strTrue = private constant [6 x i8] c"true\0A\00"
@.strFalse = private constant [7 x i8] c"false\0A\00"
@.strInt = private constant [4 x i8] c"%d\0A\00"
@.strDouble = private constant [4 x i8] c"%f\0A\00"
@.strStr = private constant [4 x i8] c"%s\0A\00"
@.str0 = private constant [5 x i8] c"zhun\00"

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

define void @print_Set(ptr %raw) {
entry:
  %f1 = getelementptr inbounds %Set, ptr %raw, i64 0, i32 1
  %val1 = load i32, ptr %f1, align 4
  %0 = call i32 (ptr, ...) @printf(ptr noundef nonnull dereferenceable(1) @.strInt, i32 %val1)
  ret void
}

define i32 @main() {
  %t0 = alloca %Set, align 8
  %t1 = call ptr @arraylist_create(i64 10)
  store ptr %t1, ptr %t0, align 8
  %t3 = getelementptr inbounds %Set, ptr %t0, i64 0, i32 1
  store i32 0, ptr %t3, align 4
  %t7 = call ptr @createString(ptr nonnull @.str0)
  %t8 = load ptr, ptr %t0, align 8
  %t9 = icmp eq ptr %t8, null
  br i1 %t9, label %init_list_t8, label %init_list_t8_end

init_list_t8:                                     ; preds = %0
  %t11 = call ptr @arraylist_create(i64 10)
  store ptr %t11, ptr %t0, align 8
  br label %init_list_t8_end

init_list_t8_end:                                 ; preds = %init_list_t8, %0
  %t13 = load ptr, ptr %t0, align 8
  call void @arraylist_add_ptr(ptr %t13, ptr %t7)
  %t17 = getelementptr inbounds %Set, ptr %t0, i64 0, i32 1
  store i32 4, ptr %t17, align 4
  %t21 = load ptr, ptr %t0, align 8
  call void @arraylist_print_ptr(ptr %t21, ptr nonnull @printString)
  %t22 = getelementptr inbounds %Set, ptr %t0, i64 0, i32 1
  %t23 = load i32, ptr %t22, align 4
  %1 = call i32 (ptr, ...) @printf(ptr noundef nonnull dereferenceable(1) @.strInt, i32 %t23)
  %2 = call i32 @getchar()
  ret i32 0
}
