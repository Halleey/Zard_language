; ModuleID = 'programa.ll'
source_filename = "programa.ll"

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
@.str0 = private constant [2 x i8] c" \00"
@.str1 = private constant [1 x i8] zeroinitializer

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

declare ptr @arraylist_create_int(i64)

declare void @arraylist_add_int(ptr, i32)

declare void @arraylist_addAll_int(ptr, ptr, i64)

declare void @arraylist_print_int(ptr)

declare void @arraylist_clear_int(ptr)

declare void @arraylist_free_int(ptr)

declare i32 @arraylist_get_int(ptr, i64, ptr)

declare void @arraylist_remove_int(ptr, i64)

declare i32 @arraylist_size_int(ptr)

define void @print_Linha(ptr %p) {
entry:
  %v0 = load ptr, ptr %p, align 8
  call void @arraylist_print_int(ptr %v0)
  ret void
}

define void @print_Matriz(ptr %p) {
entry:
  ret void
}

define ptr @Matriz_addLinha(ptr %s, ptr %l) {
entry:
  %tmp2 = load ptr, ptr %s, align 8
  call void @arraylist_add_ptr(ptr %tmp2, ptr %l)
  ret ptr %s

0:                                                ; No predecessors!
  ret ptr %s
}

define ptr @Matriz_printMatriz(ptr %s) {
entry:
  %tmp116 = load ptr, ptr %s, align 8
  %tmp137 = call i32 @length(ptr %tmp116)
  %tmp148 = icmp slt i32 0, %tmp137
  br i1 %tmp148, label %while_body_1.lr.ph, label %while_end_2

while_body_1.lr.ph:                               ; preds = %entry
  br label %while_body_1

while_body_1:                                     ; preds = %while_body_1.lr.ph, %while_end_5
  %i.09 = phi i32 [ 0, %while_body_1.lr.ph ], [ %tmp48, %while_end_5 ]
  %ln = alloca ptr, align 8
  %tmp17 = load ptr, ptr %s, align 8
  %tmp19 = zext i32 %i.09 to i64
  %tmp20 = call ptr @arraylist_get_ptr(ptr %tmp17, i64 %tmp19)
  store ptr %tmp20, ptr %ln, align 8
  %j = alloca i32, align 4
  store i32 0, ptr %j, align 4
  %tmp231 = load i32, ptr %j, align 4
  %tmp272 = load ptr, ptr %ln, align 8
  %tmp293 = load ptr, ptr %tmp272, align 8
  %tmp304 = call i32 @arraylist_size_int(ptr %tmp293)
  %tmp315 = icmp slt i32 %tmp231, %tmp304
  br i1 %tmp315, label %while_body_4.lr.ph, label %while_end_5

while_body_4.lr.ph:                               ; preds = %while_body_1
  br label %while_body_4

while_body_4:                                     ; preds = %while_body_4.lr.ph, %while_body_4
  %tmp35 = load ptr, ptr %ln, align 8
  %tmp37 = load ptr, ptr %tmp35, align 8
  %tmp38 = load i32, ptr %j, align 4
  %tmp39 = zext i32 %tmp38 to i64
  %tmp40 = alloca i32, align 4
  %tmp41 = call i32 @arraylist_get_int(ptr %tmp37, i64 %tmp39, ptr %tmp40)
  %tmp42 = load i32, ptr %tmp40, align 4
  %0 = call i32 (ptr, ...) @printf(ptr @.strInt_noNL, i32 %tmp42)
  %1 = call i32 (ptr, ...) @printf(ptr @.strStr_noNL, ptr @.str0)
  %tmp44 = load i32, ptr %j, align 4
  %tmp45 = add i32 %tmp44, 1
  store i32 %tmp45, ptr %j, align 4
  %tmp23 = load i32, ptr %j, align 4
  %tmp27 = load ptr, ptr %ln, align 8
  %tmp29 = load ptr, ptr %tmp27, align 8
  %tmp30 = call i32 @arraylist_size_int(ptr %tmp29)
  %tmp31 = icmp slt i32 %tmp23, %tmp30
  br i1 %tmp31, label %while_body_4, label %while_cond_3.while_end_5_crit_edge

while_cond_3.while_end_5_crit_edge:               ; preds = %while_body_4
  br label %while_end_5

while_end_5:                                      ; preds = %while_cond_3.while_end_5_crit_edge, %while_body_1
  %2 = call i32 (ptr, ...) @printf(ptr @.strStr, ptr @.str1)
  %tmp48 = add i32 %i.09, 1
  %tmp11 = load ptr, ptr %s, align 8
  %tmp13 = call i32 @length(ptr %tmp11)
  %tmp14 = icmp slt i32 %tmp48, %tmp13
  br i1 %tmp14, label %while_body_1, label %while_cond_0.while_end_2_crit_edge

while_cond_0.while_end_2_crit_edge:               ; preds = %while_end_5
  br label %while_end_2

while_end_2:                                      ; preds = %while_cond_0.while_end_2_crit_edge, %entry
  ret ptr %s
}

define i32 @main() {
  %tmp49 = call ptr @malloc(i64 8)
  %tmp51 = call ptr @arraylist_create(i64 10)
  store ptr %tmp51, ptr %tmp49, align 8
  %tmp54 = call ptr @malloc(i64 8)
  %tmp56 = call ptr @arraylist_create_int(i64 10)
  store ptr %tmp56, ptr %tmp54, align 8
  call void @arraylist_add_int(ptr %tmp56, i32 1)
  %tmp72 = load ptr, ptr %tmp54, align 8
  call void @arraylist_add_int(ptr %tmp72, i32 2)
  %tmp80 = load ptr, ptr %tmp54, align 8
  call void @arraylist_add_int(ptr %tmp80, i32 3)
  call void @Matriz_addLinha(ptr %tmp49, ptr %tmp54)
  %tmp84 = call ptr @malloc(i64 8)
  %tmp86 = call ptr @arraylist_create_int(i64 10)
  store ptr %tmp86, ptr %tmp84, align 8
  call void @arraylist_add_int(ptr %tmp86, i32 4)
  %tmp102 = load ptr, ptr %tmp84, align 8
  call void @arraylist_add_int(ptr %tmp102, i32 5)
  %tmp110 = load ptr, ptr %tmp84, align 8
  call void @arraylist_add_int(ptr %tmp110, i32 6)
  call void @Matriz_addLinha(ptr %tmp49, ptr %tmp84)
  %tmp114 = call ptr @malloc(i64 8)
  %tmp116 = call ptr @arraylist_create_int(i64 10)
  store ptr %tmp116, ptr %tmp114, align 8
  call void @arraylist_add_int(ptr %tmp116, i32 7)
  %tmp132 = load ptr, ptr %tmp114, align 8
  call void @arraylist_add_int(ptr %tmp132, i32 8)
  %tmp140 = load ptr, ptr %tmp114, align 8
  call void @arraylist_add_int(ptr %tmp140, i32 9)
  call void @Matriz_addLinha(ptr %tmp49, ptr %tmp114)
  call void @Matriz_printMatriz(ptr %tmp49)
  %1 = call i32 @getchar()
  ret i32 0
}
