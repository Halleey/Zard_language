; ModuleID = 'programa.ll'
source_filename = "programa.ll"

%Multi = type { ptr, ptr, ptr, ptr }
%struct.ArrayListInt = type { ptr, i64, i64 }
%struct.ArrayListDouble = type { ptr, i64, i64 }
%struct.ArrayListBool = type { ptr, i64, i64 }

@.strChar = private constant [3 x i8] c"%c\00"
@.strInt = private constant [3 x i8] c"%d\00"
@.strDouble = private constant [3 x i8] c"%f\00"
@.strFloat = private constant [3 x i8] c"%f\00"
@.strStr = private constant [3 x i8] c"%s\00"
@.strTrue = private constant [5 x i8] c"true\00"
@.strFalse = private constant [6 x i8] c"false\00"
@.strNewLine = private constant [2 x i8] c"\0A\00"
@.strEmpty = private constant [1 x i8] zeroinitializer
@.str0 = private constant [2 x i8] c"A\00"
@.str1 = private constant [2 x i8] c"B\00"
@.str2 = private constant [2 x i8] c"Z\00"
@.str3 = private constant [20 x i8] c"=== Original m1 ===\00"
@.str4 = private constant [17 x i8] c"=== Clone m2 ===\00"
@.str5 = private constant [10 x i8] c"testando \00"

declare i32 @printf(ptr, ...)

declare i32 @getchar()

declare void @printString(ptr)

declare ptr @malloc(i64)

declare void @setString(ptr)

declare void @free(ptr)

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

declare ptr @arraylist_create_double(i64)

declare void @arraylist_add_double(ptr, double)

declare void @arraylist_addAll_double(ptr, ptr, i64)

declare void @arraylist_print_double(ptr)

declare double @arraylist_get_double(ptr, i64, ptr)

declare void @arraylist_clear_double(ptr)

declare void @arraylist_remove_double(ptr, i64)

declare void @arraylist_free_double(ptr)

declare i32 @arraylist_size_double(ptr)

declare ptr @arraylist_create_bool(i64)

declare void @arraylist_add_bool(ptr, i1)

declare void @arraylist_addAll_bool(ptr, ptr, i64)

declare void @arraylist_print_bool(ptr)

declare void @arraylist_clear_bool(ptr)

declare void @arraylist_remove_bool(ptr, i64)

declare void @arraylist_free_bool(ptr)

declare i1 @arraylist_get_bool(ptr, i64, ptr)

declare i32 @arraylist_size_bool(ptr)

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

declare ptr @getItem(ptr, i64)

define void @print_Multi(ptr %p) {
entry:
  %v0 = load ptr, ptr %p, align 8
  call void @arraylist_print_int(ptr %v0)
  %f1 = getelementptr inbounds %Multi, ptr %p, i32 0, i32 1
  %v1 = load ptr, ptr %f1, align 8
  call void @arraylist_print_double(ptr %v1)
  %f2 = getelementptr inbounds %Multi, ptr %p, i32 0, i32 2
  %v2 = load ptr, ptr %f2, align 8
  call void @arraylist_print_string(ptr %v2)
  %f3 = getelementptr inbounds %Multi, ptr %p, i32 0, i32 3
  %v3 = load ptr, ptr %f3, align 8
  call void @arraylist_print_bool(ptr %v3)
  ret void
}

define i32 @main() {
  %tmp0 = call ptr @malloc(i64 32)
  %tmp2 = call ptr @arraylist_create(i64 4)
  store ptr %tmp2, ptr %tmp0, align 8
  %tmp5 = call ptr @arraylist_create(i64 4)
  %tmp7 = getelementptr inbounds %Multi, ptr %tmp0, i32 0, i32 1
  store ptr %tmp5, ptr %tmp7, align 8
  %tmp8 = call ptr @arraylist_create(i64 4)
  %tmp10 = getelementptr inbounds %Multi, ptr %tmp0, i32 0, i32 2
  store ptr %tmp8, ptr %tmp10, align 8
  %tmp11 = call ptr @arraylist_create(i64 4)
  %tmp13 = getelementptr inbounds %Multi, ptr %tmp0, i32 0, i32 3
  store ptr %tmp11, ptr %tmp13, align 8
  %tmp20 = load ptr, ptr %tmp0, align 8
  call void @arraylist_add_int(ptr %tmp20, i32 1)
  %tmp28 = load ptr, ptr %tmp0, align 8
  call void @arraylist_add_int(ptr %tmp28, i32 2)
  %tmp36 = load ptr, ptr %tmp7, align 8
  call void @arraylist_add_double(ptr %tmp36, double 1.100000e+00)
  %tmp44 = load ptr, ptr %tmp7, align 8
  call void @arraylist_add_double(ptr %tmp44, double 2.200000e+00)
  %tmp48 = load ptr, ptr %tmp10, align 8
  %tmp50 = call ptr @createString(ptr @.str0)
  call void @arraylist_add_String(ptr %tmp48, ptr %tmp50)
  %tmp54 = load ptr, ptr %tmp10, align 8
  %tmp56 = call ptr @createString(ptr @.str1)
  call void @arraylist_add_String(ptr %tmp54, ptr %tmp56)
  %tmp64 = load ptr, ptr %tmp13, align 8
  call void @arraylist_add_bool(ptr %tmp64, i1 true)
  %tmp66 = call ptr @malloc(i64 32)
  %tmp68 = call ptr @arraylist_create(i64 4)
  store ptr %tmp68, ptr %tmp66, align 8
  %tmp71 = call ptr @arraylist_create(i64 4)
  %tmp73 = getelementptr inbounds %Multi, ptr %tmp66, i32 0, i32 1
  store ptr %tmp71, ptr %tmp73, align 8
  %tmp74 = call ptr @arraylist_create(i64 4)
  %tmp76 = getelementptr inbounds %Multi, ptr %tmp66, i32 0, i32 2
  store ptr %tmp74, ptr %tmp76, align 8
  %tmp77 = call ptr @arraylist_create(i64 4)
  %tmp79 = getelementptr inbounds %Multi, ptr %tmp66, i32 0, i32 3
  store ptr %tmp77, ptr %tmp79, align 8
  %tmp84 = load ptr, ptr %tmp0, align 8
  %tmp86 = load ptr, ptr %tmp84, align 8
  %tmp87 = getelementptr inbounds %struct.ArrayListInt, ptr %tmp84, i32 0, i32 1
  %tmp88 = load i64, ptr %tmp87, align 4
  %tmp89 = call ptr @arraylist_create_int(i64 %tmp88)
  call void @arraylist_addAll_int(ptr %tmp89, ptr %tmp86, i64 %tmp88)
  store ptr %tmp89, ptr %tmp66, align 8
  %tmp92 = load ptr, ptr %tmp7, align 8
  %tmp94 = load ptr, ptr %tmp92, align 8
  %tmp95 = getelementptr inbounds %struct.ArrayListDouble, ptr %tmp92, i32 0, i32 1
  %tmp96 = load i64, ptr %tmp95, align 4
  %tmp97 = call ptr @arraylist_create_double(i64 %tmp96)
  call void @arraylist_addAll_double(ptr %tmp97, ptr %tmp94, i64 %tmp96)
  store ptr %tmp97, ptr %tmp73, align 8
  %tmp100 = load ptr, ptr %tmp10, align 8
  %tmp101 = call i32 @length(ptr %tmp100)
  %tmp102 = zext i32 %tmp101 to i64
  %tmp103 = call ptr @arraylist_create(i64 %tmp102)
  %tmp1081 = icmp ult i64 0, %tmp102
  br i1 %tmp1081, label %list_copy_body_tmp106.lr.ph, label %list_copy_end_tmp106

list_copy_body_tmp106.lr.ph:                      ; preds = %0
  br label %list_copy_body_tmp106

list_copy_body_tmp106:                            ; preds = %list_copy_body_tmp106.lr.ph, %list_copy_body_tmp106
  %tmp105.02 = phi i64 [ 0, %list_copy_body_tmp106.lr.ph ], [ %tmp111, %list_copy_body_tmp106 ]
  %tmp109 = call ptr @arraylist_get_ptr(ptr %tmp100, i64 %tmp105.02)
  %tmp110 = call ptr @createString(ptr %tmp109)
  call void @arraylist_add_String(ptr %tmp103, ptr %tmp110)
  %tmp111 = add i64 %tmp105.02, 1
  %tmp108 = icmp ult i64 %tmp111, %tmp102
  br i1 %tmp108, label %list_copy_body_tmp106, label %list_copy_cond_tmp106.list_copy_end_tmp106_crit_edge

list_copy_cond_tmp106.list_copy_end_tmp106_crit_edge: ; preds = %list_copy_body_tmp106
  br label %list_copy_end_tmp106

list_copy_end_tmp106:                             ; preds = %list_copy_cond_tmp106.list_copy_end_tmp106_crit_edge, %0
  store ptr %tmp103, ptr %tmp76, align 8
  %tmp114 = load ptr, ptr %tmp13, align 8
  %tmp116 = load ptr, ptr %tmp114, align 8
  %tmp117 = getelementptr inbounds %struct.ArrayListBool, ptr %tmp114, i32 0, i32 1
  %tmp118 = load i64, ptr %tmp117, align 4
  %tmp119 = call ptr @arraylist_create_bool(i64 %tmp118)
  call void @arraylist_addAll_bool(ptr %tmp119, ptr %tmp116, i64 %tmp118)
  store ptr %tmp119, ptr %tmp79, align 8
  %tmp126 = load ptr, ptr %tmp66, align 8
  call void @arraylist_add_int(ptr %tmp126, i32 99)
  %tmp134 = load ptr, ptr %tmp73, align 8
  call void @arraylist_add_double(ptr %tmp134, double 9.900000e+00)
  %tmp138 = load ptr, ptr %tmp76, align 8
  %tmp140 = call ptr @createString(ptr @.str2)
  call void @arraylist_add_String(ptr %tmp138, ptr %tmp140)
  %tmp148 = load ptr, ptr %tmp79, align 8
  call void @arraylist_add_bool(ptr %tmp148, i1 true)
  %1 = call i32 (ptr, ...) @printf(ptr @.strStr, ptr @.str3)
  %2 = call i32 (ptr, ...) @printf(ptr @.strNewLine)
  call void @print_Multi(ptr %tmp0)
  %3 = call i32 (ptr, ...) @printf(ptr @.strNewLine)
  %4 = call i32 (ptr, ...) @printf(ptr @.strStr, ptr @.str4)
  %5 = call i32 (ptr, ...) @printf(ptr @.strNewLine)
  call void @print_Multi(ptr %tmp66)
  %6 = call i32 (ptr, ...) @printf(ptr @.strNewLine)
  %7 = call i32 (ptr, ...) @printf(ptr @.strStr, ptr @.str5)
  %8 = call i32 @getchar()
  ret i32 0
}
