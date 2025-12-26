; ModuleID = 'programa.ll'
source_filename = "programa.ll"

%People = type { i32, ptr }

@.strChar = private constant [3 x i8] c"%c\00"
@.strTrue = private constant [6 x i8] c"true\0A\00"
@.strFalse = private constant [7 x i8] c"false\0A\00"
@.strInt = private constant [4 x i8] c"%d\0A\00"
@.strInt_noNL = private constant [3 x i8] c"%d\00"
@.strDouble = private constant [4 x i8] c"%f\0A\00"
@.strDouble_noNL = private constant [3 x i8] c"%f\00"
@.strFloat = private constant [4 x i8] c"%f\0A\00"
@.strFloat_noNL = private constant [3 x i8] c"%f\00"
@.strStr = private constant [4 x i8] c"%s\0A\00"
@.strStr_noNL = private constant [3 x i8] c"%s\00"
@.strEmpty = private constant [1 x i8] zeroinitializer
@.str0 = private constant [5 x i8] c"zard\00"
@.str1 = private constant [6 x i8] c"angel\00"
@.str2 = private constant [10 x i8] c"currently\00"

declare i32 @printf(ptr, ...)

declare i32 @getchar()

declare void @printString(ptr)

declare ptr @malloc(i64)

declare void @setString(ptr)

declare void @printString_noNL(ptr)

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

declare void @removeItem(ptr, i64)

declare void @arraylist_add_string(ptr, ptr)

declare void @arraylist_addAll_string(ptr, ptr, i64)

declare void @arraylist_print_string(ptr)

declare void @arraylist_add_String(ptr, ptr)

declare void @arraylist_addAll_String(ptr, ptr, i64)

declare ptr @getItem(ptr, i64)

define void @print_People(ptr %p) {
entry:
  %v0 = load i32, ptr %p, align 4
  %0 = call i32 (ptr, ...) @printf(ptr @.strInt, i32 %v0)
  %f1 = getelementptr inbounds %People, ptr %p, i32 0, i32 1
  %v1 = load ptr, ptr %f1, align 8
  call void @printString(ptr %v1)
  ret void
}

define i32 @main() {
  %1 = call i32 (ptr, ...) @printf(ptr @.strInt_noNL, i32 10)
  %tmp7 = call ptr @arraylist_create(i64 4)
  %tmp10 = call ptr @createString(ptr @.str0)
  call void @arraylist_add_String(ptr %tmp7, ptr %tmp10)
  %tmp12 = call ptr @createString(ptr @.str1)
  call void @arraylist_add_String(ptr %tmp7, ptr %tmp12)
  %tmp14 = call ptr @arraylist_create(i64 4)
  %tmp17 = call ptr @createString(ptr @.str0)
  call void @arraylist_add_String(ptr %tmp14, ptr %tmp17)
  %tmp19 = call ptr @createString(ptr @.str1)
  call void @arraylist_add_String(ptr %tmp14, ptr %tmp19)
  call void @arraylist_print_string(ptr %tmp14, i1 false)
  call void @arraylist_print_string(ptr %tmp7, i1 false)
  br label %while_body_1

while_body_1:                                     ; preds = %0
  %nomes = alloca ptr, align 8
  %tmp29 = call ptr @arraylist_create(i64 4)
  store ptr %tmp29, ptr %nomes, align 8
  %nomes.1 = alloca ptr, align 8
  %tmp29.1 = call ptr @arraylist_create(i64 4)
  store ptr %tmp29.1, ptr %nomes.1, align 8
  %nomes.2 = alloca ptr, align 8
  %tmp29.2 = call ptr @arraylist_create(i64 4)
  store ptr %tmp29.2, ptr %nomes.2, align 8
  %nomes.3 = alloca ptr, align 8
  %tmp29.3 = call ptr @arraylist_create(i64 4)
  store ptr %tmp29.3, ptr %nomes.3, align 8
  %nomes.4 = alloca ptr, align 8
  %tmp29.4 = call ptr @arraylist_create(i64 4)
  store ptr %tmp29.4, ptr %nomes.4, align 8
  %nomes.5 = alloca ptr, align 8
  %tmp29.5 = call ptr @arraylist_create(i64 4)
  store ptr %tmp29.5, ptr %nomes.5, align 8
  %nomes.6 = alloca ptr, align 8
  %tmp29.6 = call ptr @arraylist_create(i64 4)
  store ptr %tmp29.6, ptr %nomes.6, align 8
  %nomes.7 = alloca ptr, align 8
  %tmp29.7 = call ptr @arraylist_create(i64 4)
  store ptr %tmp29.7, ptr %nomes.7, align 8
  %nomes.8 = alloca ptr, align 8
  %tmp29.8 = call ptr @arraylist_create(i64 4)
  store ptr %tmp29.8, ptr %nomes.8, align 8
  %nomes.9 = alloca ptr, align 8
  %tmp29.9 = call ptr @arraylist_create(i64 4)
  store ptr %tmp29.9, ptr %nomes.9, align 8
  br label %for_init_3

for_init_3:                                       ; preds = %while_body_1
  %i = alloca i32, align 4
  store i32 0, ptr %i, align 4
  %tmp332 = load i32, ptr %i, align 4
  %tmp353 = icmp slt i32 %tmp332, 10
  br i1 %tmp353, label %for_body_5.lr.ph, label %for_end_7

for_body_5.lr.ph:                                 ; preds = %for_init_3
  br label %for_body_5

for_body_5:                                       ; preds = %for_body_5.lr.ph, %for_inc_6
  %2 = call i32 (ptr, ...) @printf(ptr @.strStr, ptr @.str2)
  br label %for_inc_6

for_inc_6:                                        ; preds = %for_body_5
  %tmp37 = load i32, ptr %i, align 4
  %tmp38 = add i32 %tmp37, 1
  store i32 %tmp38, ptr %i, align 4
  %tmp33 = load i32, ptr %i, align 4
  %tmp35 = icmp slt i32 %tmp33, 10
  br i1 %tmp35, label %for_body_5, label %for_cond_4.for_end_7_crit_edge

for_cond_4.for_end_7_crit_edge:                   ; preds = %for_inc_6
  br label %for_end_7

for_end_7:                                        ; preds = %for_cond_4.for_end_7_crit_edge, %for_init_3
  call void @freeList(ptr %tmp7)
  call void @freeList(ptr %tmp14)
  %3 = call i32 @getchar()
  ret i32 0
}
