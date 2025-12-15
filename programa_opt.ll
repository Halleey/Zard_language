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

declare void @arraylist_print_string(ptr)

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
  %tmp16 = load ptr, ptr %tmp0, align 8
  call void @arraylist_add_int(ptr %tmp16, i32 1)
  %tmp20 = load ptr, ptr %tmp0, align 8
  call void @arraylist_add_int(ptr %tmp20, i32 2)
  %tmp24 = load ptr, ptr %tmp7, align 8
  call void @arraylist_add_double(ptr %tmp24, double 1.100000e+00)
  %tmp28 = load ptr, ptr %tmp7, align 8
  call void @arraylist_add_double(ptr %tmp28, double 2.200000e+00)
  %tmp32 = load ptr, ptr %tmp10, align 8
  %tmp34 = call ptr @createString(ptr @.str0)
  call void @arraylist_add_ptr(ptr %tmp32, ptr %tmp34)
  %tmp38 = load ptr, ptr %tmp10, align 8
  %tmp40 = call ptr @createString(ptr @.str1)
  call void @arraylist_add_ptr(ptr %tmp38, ptr %tmp40)
  %tmp44 = load ptr, ptr %tmp13, align 8
  call void @arraylist_add_bool(ptr %tmp44, i1 true)
  %tmp46 = call ptr @malloc(i64 32)
  %tmp48 = call ptr @arraylist_create(i64 4)
  store ptr %tmp48, ptr %tmp46, align 8
  %tmp51 = call ptr @arraylist_create(i64 4)
  %tmp53 = getelementptr inbounds %Multi, ptr %tmp46, i32 0, i32 1
  store ptr %tmp51, ptr %tmp53, align 8
  %tmp54 = call ptr @arraylist_create(i64 4)
  %tmp56 = getelementptr inbounds %Multi, ptr %tmp46, i32 0, i32 2
  store ptr %tmp54, ptr %tmp56, align 8
  %tmp57 = call ptr @arraylist_create(i64 4)
  %tmp59 = getelementptr inbounds %Multi, ptr %tmp46, i32 0, i32 3
  store ptr %tmp57, ptr %tmp59, align 8
  %tmp61 = call ptr @malloc(i64 32)
  %tmp65 = load ptr, ptr %tmp0, align 8
  %tmp67 = load ptr, ptr %tmp65, align 8
  %tmp68 = getelementptr inbounds %struct.ArrayListInt, ptr %tmp65, i32 0, i32 1
  %tmp69 = load i64, ptr %tmp68, align 4
  %tmp70 = call ptr @arraylist_create_int(i64 %tmp69)
  call void @arraylist_addAll_int(ptr %tmp70, ptr %tmp67, i64 %tmp69)
  store ptr %tmp70, ptr %tmp61, align 8
  %tmp72 = getelementptr inbounds %Multi, ptr %tmp61, i32 0, i32 1
  %tmp73 = load ptr, ptr %tmp7, align 8
  %tmp75 = load ptr, ptr %tmp73, align 8
  %tmp76 = getelementptr inbounds %struct.ArrayListDouble, ptr %tmp73, i32 0, i32 1
  %tmp77 = load i64, ptr %tmp76, align 4
  %tmp78 = call ptr @arraylist_create_double(i64 %tmp77)
  call void @arraylist_addAll_double(ptr %tmp78, ptr %tmp75, i64 %tmp77)
  store ptr %tmp78, ptr %tmp72, align 8
  %tmp80 = getelementptr inbounds %Multi, ptr %tmp61, i32 0, i32 2
  %tmp81 = load ptr, ptr %tmp10, align 8
  %tmp82 = call i32 @length(ptr %tmp81)
  %tmp83 = zext i32 %tmp82 to i64
  %tmp84 = call ptr @arraylist_create(i64 %tmp83)
  %tmp891 = icmp ult i64 0, %tmp83
  br i1 %tmp891, label %list_copy_body_tmp87.lr.ph, label %list_copy_end_tmp87

list_copy_body_tmp87.lr.ph:                       ; preds = %0
  br label %list_copy_body_tmp87

list_copy_body_tmp87:                             ; preds = %list_copy_body_tmp87.lr.ph, %list_copy_body_tmp87
  %tmp86.02 = phi i64 [ 0, %list_copy_body_tmp87.lr.ph ], [ %tmp96, %list_copy_body_tmp87 ]
  %tmp90 = call ptr @arraylist_get_ptr(ptr %tmp81, i64 %tmp86.02)
  %tmp93 = load ptr, ptr %tmp90, align 8
  %tmp94 = call ptr @createString(ptr %tmp93)
  call void @arraylist_add_ptr(ptr %tmp84, ptr %tmp94)
  %tmp96 = add i64 %tmp86.02, 1
  %tmp89 = icmp ult i64 %tmp96, %tmp83
  br i1 %tmp89, label %list_copy_body_tmp87, label %list_copy_cond_tmp87.list_copy_end_tmp87_crit_edge

list_copy_cond_tmp87.list_copy_end_tmp87_crit_edge: ; preds = %list_copy_body_tmp87
  br label %list_copy_end_tmp87

list_copy_end_tmp87:                              ; preds = %list_copy_cond_tmp87.list_copy_end_tmp87_crit_edge, %0
  store ptr %tmp84, ptr %tmp80, align 8
  %tmp98 = getelementptr inbounds %Multi, ptr %tmp61, i32 0, i32 3
  %tmp99 = load ptr, ptr %tmp13, align 8
  %tmp101 = load ptr, ptr %tmp99, align 8
  %tmp102 = getelementptr inbounds %struct.ArrayListBool, ptr %tmp99, i32 0, i32 1
  %tmp103 = load i64, ptr %tmp102, align 4
  %tmp104 = call ptr @arraylist_create_bool(i64 %tmp103)
  call void @arraylist_addAll_bool(ptr %tmp104, ptr %tmp101, i64 %tmp103)
  store ptr %tmp104, ptr %tmp98, align 8
  %tmp107 = load ptr, ptr %tmp61, align 8
  call void @arraylist_add_int(ptr %tmp107, i32 99)
  %tmp111 = load ptr, ptr %tmp72, align 8
  call void @arraylist_add_double(ptr %tmp111, double 9.900000e+00)
  %tmp115 = load ptr, ptr %tmp80, align 8
  %tmp117 = call ptr @createString(ptr @.str2)
  call void @arraylist_add_ptr(ptr %tmp115, ptr %tmp117)
  %tmp121 = load ptr, ptr %tmp98, align 8
  call void @arraylist_add_bool(ptr %tmp121, i1 true)
  %1 = call i32 (ptr, ...) @printf(ptr @.strStr, ptr @.str3)
  %2 = call i32 (ptr, ...) @printf(ptr @.strNewLine)
  call void @print_Multi(ptr %tmp0)
  %3 = call i32 (ptr, ...) @printf(ptr @.strNewLine)
  %tmp127 = load ptr, ptr %tmp0, align 8
  call void @arraylist_free_int(ptr %tmp127)
  %tmp129 = load ptr, ptr %tmp7, align 8
  call void @arraylist_free_double(ptr %tmp129)
  %tmp131 = load ptr, ptr %tmp10, align 8
  call void @freeList(ptr %tmp131)
  %tmp133 = load ptr, ptr %tmp13, align 8
  call void @arraylist_free_bool(ptr %tmp133)
  call void @free(ptr %tmp0)
  %4 = call i32 (ptr, ...) @printf(ptr @.strStr, ptr @.str4)
  %5 = call i32 (ptr, ...) @printf(ptr @.strNewLine)
  call void @print_Multi(ptr %tmp61)
  %6 = call i32 (ptr, ...) @printf(ptr @.strNewLine)
  %tmp139 = load ptr, ptr %tmp61, align 8
  call void @arraylist_free_int(ptr %tmp139)
  %tmp141 = load ptr, ptr %tmp72, align 8
  call void @arraylist_free_double(ptr %tmp141)
  %tmp143 = load ptr, ptr %tmp80, align 8
  call void @freeList(ptr %tmp143)
  %tmp145 = load ptr, ptr %tmp98, align 8
  call void @arraylist_free_bool(ptr %tmp145)
  call void @free(ptr %tmp61)
  %7 = call i32 (ptr, ...) @printf(ptr @.strStr, ptr @.str5)
  %8 = call i32 @getchar()
  ret i32 0
}
