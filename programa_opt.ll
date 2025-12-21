; ModuleID = 'programa.ll'
source_filename = "programa.ll"

%Item = type { i32, ptr, ptr }

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
@.str0 = private constant [6 x i8] c"Set {\00"
@.str1 = private constant [9 x i8] c"  Item {\00"
@.str2 = private constant [8 x i8] c"    id:\00"
@.str3 = private constant [10 x i8] c"    name:\00"
@.str4 = private constant [4 x i8] c"  }\00"
@.str5 = private constant [2 x i8] c"}\00"
@.str6 = private constant [6 x i8] c"Sword\00"
@.str7 = private constant [7 x i8] c"Shield\00"
@.str8 = private constant [7 x i8] c"Potion\00"

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

define void @print_Item(ptr %p) {
entry:
  %v0 = load i32, ptr %p, align 4
  %0 = call i32 (ptr, ...) @printf(ptr @.strInt, i32 %v0)
  %f1 = getelementptr inbounds %Item, ptr %p, i32 0, i32 1
  %v1 = load ptr, ptr %f1, align 8
  call void @printString(ptr %v1)
  %f2 = getelementptr inbounds %Item, ptr %p, i32 0, i32 2
  %v2 = load ptr, ptr %f2, align 8
  call void @arraylist_print_int(ptr %v2)
  ret void
}

define void @print_Set(ptr %p) {
entry:
  ret void
}

define void @print_Set_Item(ptr %p) {
entry:
  ret void
}

define ptr @Set_Item_addItem(ptr %s, i32 %id, ptr %name, i32 %quantidade) {
entry:
  %quantidade_addr = alloca i32, align 4
  store i32 %quantidade, ptr %quantidade_addr, align 4
  %tmp0 = call ptr @malloc(i64 20)
  store i32 0, ptr %tmp0, align 4
  %tmp3 = call ptr @createString(ptr null)
  %tmp4 = getelementptr inbounds %Item, ptr %tmp0, i32 0, i32 1
  store ptr %tmp3, ptr %tmp4, align 8
  %tmp5 = call ptr @arraylist_create_int(i64 10)
  %tmp6 = getelementptr inbounds %Item, ptr %tmp0, i32 0, i32 2
  store ptr %tmp5, ptr %tmp6, align 8
  store i32 %id, ptr %tmp0, align 4
  store ptr %name, ptr %tmp4, align 8
  %tmp17 = load ptr, ptr %tmp6, align 8
  %quantidade_addr.0.tmp18 = load ptr, ptr %quantidade_addr, align 4
  call void @arraylist_add_ptr(ptr %tmp17, ptr %quantidade_addr.0.tmp18)
  %tmp23 = load ptr, ptr %s, align 8
  call void @arraylist_add_ptr(ptr %tmp23, ptr %tmp0)
  ret ptr %s

0:                                                ; No predecessors!
  ret ptr %s
}

define ptr @Set_Item_removeFirst(ptr %s) {
entry:
  %tmp2 = load ptr, ptr %s, align 8
  %tmp4 = call i32 @length(ptr %tmp2)
  %tmp6 = icmp sgt i32 %tmp4, 0
  br i1 %tmp6, label %then_0, label %endif_0

then_0:                                           ; preds = %entry
  %tmp9 = load ptr, ptr %s, align 8
  call void @removeItem(ptr %tmp9, i64 0)
  br label %endif_0

endif_0:                                          ; preds = %then_0, %entry
  ret ptr %s

0:                                                ; No predecessors!
  ret ptr %s
}

define ptr @Set_Item_removeItemById(ptr %s, i32 %id) {
entry:
  %tmp41 = load ptr, ptr %s, align 8
  %tmp62 = call i32 @length(ptr %tmp41)
  %tmp73 = icmp slt i32 0, %tmp62
  br i1 %tmp73, label %while_body_1.lr.ph, label %while_end_2

while_body_1.lr.ph:                               ; preds = %entry
  br label %while_body_1

while_cond_0:                                     ; preds = %while_body_1
  %i.0 = phi i32 [ %tmp27, %while_body_1 ]
  %tmp4 = load ptr, ptr %s, align 8
  %tmp6 = call i32 @length(ptr %tmp4)
  %tmp7 = icmp slt i32 %i.0, %tmp6
  br i1 %tmp7, label %while_body_1, label %while_cond_0.while_end_2_crit_edge

while_body_1:                                     ; preds = %while_body_1.lr.ph, %while_cond_0
  %i.04 = phi i32 [ 0, %while_body_1.lr.ph ], [ %i.0, %while_cond_0 ]
  %cur = alloca ptr, align 8
  %tmp10 = load ptr, ptr %s, align 8
  %tmp12 = zext i32 %i.04 to i64
  %tmp13 = call ptr @arraylist_get_ptr(ptr %tmp10, i64 %tmp12)
  store ptr %tmp13, ptr %cur, align 8
  %tmp17 = load i32, ptr %tmp13, align 4
  %tmp19 = icmp eq i32 %tmp17, %id
  %tmp27 = add i32 %i.04, 1
  br i1 %tmp19, label %then_0, label %while_cond_0

then_0:                                           ; preds = %while_body_1
  %tmp12.lcssa = phi i64 [ %tmp12, %while_body_1 ]
  %tmp22 = load ptr, ptr %s, align 8
  call void @removeItem(ptr %tmp22, i64 %tmp12.lcssa)
  ret ptr %s

0:                                                ; No predecessors!
  unreachable

while_cond_0.while_end_2_crit_edge:               ; preds = %while_cond_0
  br label %while_end_2

while_end_2:                                      ; preds = %while_cond_0.while_end_2_crit_edge, %entry
  ret ptr %s

1:                                                ; No predecessors!
  ret ptr %s
}

define ptr @Set_Item_prints(ptr %s) {
entry:
  %0 = call i32 (ptr, ...) @printf(ptr @.strStr_noNL, ptr @.str0)
  %tmp51 = load ptr, ptr %s, align 8
  %tmp72 = call i32 @length(ptr %tmp51)
  %tmp83 = icmp slt i32 0, %tmp72
  br i1 %tmp83, label %while_body_1.lr.ph, label %while_end_2

while_body_1.lr.ph:                               ; preds = %entry
  br label %while_body_1

while_body_1:                                     ; preds = %while_body_1.lr.ph, %while_body_1
  %i.04 = phi i32 [ 0, %while_body_1.lr.ph ], [ %tmp27, %while_body_1 ]
  %cur = alloca ptr, align 8
  %tmp11 = load ptr, ptr %s, align 8
  %tmp13 = zext i32 %i.04 to i64
  %tmp14 = call ptr @arraylist_get_ptr(ptr %tmp11, i64 %tmp13)
  store ptr %tmp14, ptr %cur, align 8
  %1 = call i32 (ptr, ...) @printf(ptr @.strStr_noNL, ptr @.str1)
  %2 = call i32 (ptr, ...) @printf(ptr @.strStr_noNL, ptr @.str2)
  %tmp18 = load ptr, ptr %cur, align 8
  %tmp20 = load i32, ptr %tmp18, align 4
  %3 = call i32 (ptr, ...) @printf(ptr @.strInt_noNL, i32 %tmp20)
  %4 = call i32 (ptr, ...) @printf(ptr @.strStr_noNL, ptr @.str3)
  %tmp22 = load ptr, ptr %cur, align 8
  %tmp23 = getelementptr inbounds %Item, ptr %tmp22, i32 0, i32 1
  %tmp24 = load ptr, ptr %tmp23, align 8
  call void @printString_noNL(ptr %tmp24)
  %5 = call i32 (ptr, ...) @printf(ptr @.strStr_noNL, ptr @.str4)
  %tmp27 = add i32 %i.04, 1
  %tmp5 = load ptr, ptr %s, align 8
  %tmp7 = call i32 @length(ptr %tmp5)
  %tmp8 = icmp slt i32 %tmp27, %tmp7
  br i1 %tmp8, label %while_body_1, label %while_cond_0.while_end_2_crit_edge

while_cond_0.while_end_2_crit_edge:               ; preds = %while_body_1
  br label %while_end_2

while_end_2:                                      ; preds = %while_cond_0.while_end_2_crit_edge, %entry
  %6 = call i32 (ptr, ...) @printf(ptr @.strStr_noNL, ptr @.str5)
  ret ptr %s

7:                                                ; No predecessors!
  ret ptr %s
}

define i32 @main() {
  %tmp0 = call ptr @malloc(i64 8)
  %tmp2 = call ptr @arraylist_create(i64 10)
  store ptr %tmp2, ptr %tmp0, align 8
  %tmp5 = call ptr @malloc(i64 20)
  store i32 0, ptr %tmp5, align 4
  %tmp8 = call ptr @createString(ptr null)
  %tmp9 = getelementptr inbounds %Item, ptr %tmp5, i32 0, i32 1
  store ptr %tmp8, ptr %tmp9, align 8
  %tmp10 = call ptr @arraylist_create_int(i64 10)
  %tmp11 = getelementptr inbounds %Item, ptr %tmp5, i32 0, i32 2
  store ptr %tmp10, ptr %tmp11, align 8
  %tmp15 = call ptr @createString(ptr @.str6)
  call void @Set_Item_addItem(ptr %tmp0, i32 1, ptr %tmp15, i32 3)
  %tmp20 = call ptr @createString(ptr @.str7)
  call void @Set_Item_addItem(ptr %tmp0, i32 2, ptr %tmp20, i32 2)
  %tmp25 = call ptr @createString(ptr @.str8)
  call void @Set_Item_addItem(ptr %tmp0, i32 3, ptr %tmp25, i32 6)
  call void @Set_Item_removeItemById(ptr %tmp0, i32 2)
  call void @Set_Item_prints(ptr %tmp0)
  %1 = call i32 @getchar()
  ret i32 0
}
