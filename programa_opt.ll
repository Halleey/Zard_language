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

declare void @arraylist_add_String(ptr, ptr)

declare void @arraylist_addAll_String(ptr, ptr, i64)

declare void @arraylist_print_String(ptr)

declare ptr @arraylist_get_String(ptr, i64)

declare void @arraylist_add_string(ptr, ptr)

declare void @arraylist_addAll_string(ptr, ptr, i64)

define void @print_Void(ptr %p) {
entry:
  ret void
}

define void @print_Multi(ptr %p) {
entry:
  %v0 = load ptr, ptr %p, align 8
  call void @arraylist_print_int(ptr %v0)
  %f1 = getelementptr inbounds %Multi, ptr %p, i32 0, i32 1
  %v1 = load ptr, ptr %f1, align 8
  call void @arraylist_print_double(ptr %v1)
  %f2 = getelementptr inbounds %Multi, ptr %p, i32 0, i32 2
  %v2 = load ptr, ptr %f2, align 8
  call void @arraylist_print_String(ptr %v2)
  %f3 = getelementptr inbounds %Multi, ptr %p, i32 0, i32 3
  %v3 = load ptr, ptr %f3, align 8
  call void @arraylist_print_bool(ptr %v3)
  ret void
}

define i32 @Void_somar(ptr %s, i32 %b, i32 %c) {
entry:
  %tmp2 = add i32 %c, %b
  ret i32 %tmp2

0:                                                ; No predecessors!
  ret i32 undef
}

define i32 @main() {
  %tmp5 = call ptr @malloc(i64 32)
  %tmp8 = call ptr @arraylist_create_int(i64 10)
  store ptr %tmp8, ptr %tmp5, align 8
  %tmp9 = getelementptr inbounds %Multi, ptr %tmp5, i32 0, i32 1
  %tmp10 = call ptr @arraylist_create_double(i64 10)
  store ptr %tmp10, ptr %tmp9, align 8
  %tmp11 = getelementptr inbounds %Multi, ptr %tmp5, i32 0, i32 2
  %tmp12 = call ptr @arraylist_create(i64 10)
  store ptr %tmp12, ptr %tmp11, align 8
  %tmp14 = getelementptr inbounds %Multi, ptr %tmp5, i32 0, i32 3
  %tmp15 = call ptr @arraylist_create_bool(i64 10)
  store ptr %tmp15, ptr %tmp14, align 8
  %tmp18 = load ptr, ptr %tmp5, align 8
  call void @arraylist_add_int(ptr %tmp18, i32 1)
  %tmp22 = load ptr, ptr %tmp5, align 8
  call void @arraylist_add_int(ptr %tmp22, i32 2)
  %tmp26 = load ptr, ptr %tmp9, align 8
  call void @arraylist_add_double(ptr %tmp26, double 1.100000e+00)
  %tmp30 = load ptr, ptr %tmp9, align 8
  call void @arraylist_add_double(ptr %tmp30, double 2.200000e+00)
  %tmp34 = load ptr, ptr %tmp11, align 8
  %tmp36 = call ptr @createString(ptr @.str0)
  call void @arraylist_add_String(ptr %tmp34, ptr %tmp36)
  %tmp39 = load ptr, ptr %tmp11, align 8
  %tmp41 = call ptr @createString(ptr @.str1)
  call void @arraylist_add_String(ptr %tmp39, ptr %tmp41)
  %tmp44 = load ptr, ptr %tmp14, align 8
  call void @arraylist_add_bool(ptr %tmp44, i1 true)
  %tmp48 = call ptr @malloc(i64 32)
  %tmp51 = call ptr @arraylist_create_int(i64 10)
  store ptr %tmp51, ptr %tmp48, align 8
  %tmp52 = getelementptr inbounds %Multi, ptr %tmp48, i32 0, i32 1
  %tmp53 = call ptr @arraylist_create_double(i64 10)
  store ptr %tmp53, ptr %tmp52, align 8
  %tmp54 = getelementptr inbounds %Multi, ptr %tmp48, i32 0, i32 2
  %tmp55 = call ptr @arraylist_create(i64 10)
  store ptr %tmp55, ptr %tmp54, align 8
  %tmp57 = getelementptr inbounds %Multi, ptr %tmp48, i32 0, i32 3
  %tmp58 = call ptr @arraylist_create_bool(i64 10)
  store ptr %tmp58, ptr %tmp57, align 8
  %tmp63 = call ptr @malloc(i64 32)
  %tmp66 = load ptr, ptr %tmp5, align 8
  %tmp68 = load ptr, ptr %tmp66, align 8
  %tmp69 = getelementptr inbounds %struct.ArrayListInt, ptr %tmp66, i32 0, i32 1
  %tmp70 = load i64, ptr %tmp69, align 4
  %tmp71 = call ptr @arraylist_create_int(i64 %tmp70)
  call void @arraylist_addAll_int(ptr %tmp71, ptr %tmp68, i64 %tmp70)
  store ptr %tmp71, ptr %tmp63, align 8
  %tmp73 = getelementptr inbounds %Multi, ptr %tmp63, i32 0, i32 1
  %tmp74 = load ptr, ptr %tmp9, align 8
  %tmp76 = load ptr, ptr %tmp74, align 8
  %tmp77 = getelementptr inbounds %struct.ArrayListDouble, ptr %tmp74, i32 0, i32 1
  %tmp78 = load i64, ptr %tmp77, align 4
  %tmp79 = call ptr @arraylist_create_double(i64 %tmp78)
  call void @arraylist_addAll_double(ptr %tmp79, ptr %tmp76, i64 %tmp78)
  store ptr %tmp79, ptr %tmp73, align 8
  %tmp81 = getelementptr inbounds %Multi, ptr %tmp63, i32 0, i32 2
  %tmp82 = load ptr, ptr %tmp11, align 8
  %tmp83 = call i32 @length(ptr %tmp82)
  %tmp84 = zext i32 %tmp83 to i64
  %tmp85 = call ptr @arraylist_create(i64 %tmp84)
  %tmp901 = icmp ult i64 0, %tmp84
  br i1 %tmp901, label %list_copy_body_tmp88.lr.ph, label %list_copy_end_tmp88

list_copy_body_tmp88.lr.ph:                       ; preds = %0
  br label %list_copy_body_tmp88

list_copy_body_tmp88:                             ; preds = %list_copy_body_tmp88.lr.ph, %list_copy_body_tmp88
  %tmp87.02 = phi i64 [ 0, %list_copy_body_tmp88.lr.ph ], [ %tmp97, %list_copy_body_tmp88 ]
  %tmp91 = call ptr @arraylist_get_ptr(ptr %tmp82, i64 %tmp87.02)
  %tmp94 = load ptr, ptr %tmp91, align 8
  %tmp95 = call ptr @createString(ptr %tmp94)
  call void @arraylist_add_ptr(ptr %tmp85, ptr %tmp95)
  %tmp97 = add i64 %tmp87.02, 1
  %tmp90 = icmp ult i64 %tmp97, %tmp84
  br i1 %tmp90, label %list_copy_body_tmp88, label %list_copy_cond_tmp88.list_copy_end_tmp88_crit_edge

list_copy_cond_tmp88.list_copy_end_tmp88_crit_edge: ; preds = %list_copy_body_tmp88
  br label %list_copy_end_tmp88

list_copy_end_tmp88:                              ; preds = %list_copy_cond_tmp88.list_copy_end_tmp88_crit_edge, %0
  store ptr %tmp85, ptr %tmp81, align 8
  %tmp99 = getelementptr inbounds %Multi, ptr %tmp63, i32 0, i32 3
  %tmp100 = load ptr, ptr %tmp14, align 8
  %tmp102 = load ptr, ptr %tmp100, align 8
  %tmp103 = getelementptr inbounds %struct.ArrayListBool, ptr %tmp100, i32 0, i32 1
  %tmp104 = load i64, ptr %tmp103, align 4
  %tmp105 = call ptr @arraylist_create_bool(i64 %tmp104)
  call void @arraylist_addAll_bool(ptr %tmp105, ptr %tmp102, i64 %tmp104)
  store ptr %tmp105, ptr %tmp99, align 8
  %tmp108 = load ptr, ptr %tmp63, align 8
  call void @arraylist_add_int(ptr %tmp108, i32 99)
  %tmp112 = load ptr, ptr %tmp73, align 8
  call void @arraylist_add_double(ptr %tmp112, double 9.900000e+00)
  %tmp116 = load ptr, ptr %tmp81, align 8
  %tmp118 = call ptr @createString(ptr @.str2)
  call void @arraylist_add_String(ptr %tmp116, ptr %tmp118)
  %tmp121 = load ptr, ptr %tmp99, align 8
  call void @arraylist_add_bool(ptr %tmp121, i1 true)
  %1 = call i32 (ptr, ...) @printf(ptr @.strStr, ptr @.str3)
  %2 = call i32 (ptr, ...) @printf(ptr @.strNewLine)
  call void @print_Multi(ptr %tmp5)
  %3 = call i32 (ptr, ...) @printf(ptr @.strNewLine)
  %tmp127 = load ptr, ptr %tmp5, align 8
  call void @arraylist_free_int(ptr %tmp127)
  %tmp129 = load ptr, ptr %tmp9, align 8
  call void @arraylist_free_double(ptr %tmp129)
  %tmp131 = load ptr, ptr %tmp11, align 8
  call void @freeList(ptr %tmp131)
  %tmp133 = load ptr, ptr %tmp14, align 8
  call void @arraylist_free_bool(ptr %tmp133)
  call void @free(ptr %tmp5)
  %4 = call i32 (ptr, ...) @printf(ptr @.strStr, ptr @.str4)
  %5 = call i32 (ptr, ...) @printf(ptr @.strNewLine)
  call void @print_Multi(ptr %tmp63)
  %6 = call i32 (ptr, ...) @printf(ptr @.strNewLine)
  %tmp139 = load ptr, ptr %tmp63, align 8
  call void @arraylist_free_int(ptr %tmp139)
  %tmp141 = load ptr, ptr %tmp73, align 8
  call void @arraylist_free_double(ptr %tmp141)
  %tmp143 = load ptr, ptr %tmp81, align 8
  call void @freeList(ptr %tmp143)
  %tmp145 = load ptr, ptr %tmp99, align 8
  call void @arraylist_free_bool(ptr %tmp145)
  call void @free(ptr %tmp63)
  %v = alloca ptr, align 8
  %tmp149 = call ptr @malloc(i64 0)
  store ptr %tmp149, ptr %v, align 8
  %tmp154 = call i32 @Void_somar(ptr %tmp149, i32 3, i32 5)
  %7 = call i32 (ptr, ...) @printf(ptr @.strInt, i32 %tmp154)
  %8 = call i32 (ptr, ...) @printf(ptr @.strNewLine)
  %tmp155 = load ptr, ptr %v, align 8
  call void @free(ptr %tmp155)
  %9 = call i32 (ptr, ...) @printf(ptr @.strStr, ptr @.str5)
  %10 = call i32 @getchar()
  ret i32 0
}
