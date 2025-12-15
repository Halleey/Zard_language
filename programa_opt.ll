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
  %tmp0 = alloca %Multi, align 8
  %tmp2 = call ptr @arraylist_create_int(i64 10)
  store ptr %tmp2, ptr %tmp0, align 8
  %tmp3 = getelementptr inbounds %Multi, ptr %tmp0, i32 0, i32 1
  %tmp4 = call ptr @arraylist_create_double(i64 10)
  store ptr %tmp4, ptr %tmp3, align 8
  %tmp5 = getelementptr inbounds %Multi, ptr %tmp0, i32 0, i32 2
  %tmp6 = call ptr @arraylist_create(i64 10)
  store ptr %tmp6, ptr %tmp5, align 8
  %tmp8 = getelementptr inbounds %Multi, ptr %tmp0, i32 0, i32 3
  %tmp9 = call ptr @arraylist_create_bool(i64 10)
  store ptr %tmp9, ptr %tmp8, align 8
  %tmp12 = load ptr, ptr %tmp0, align 8
  call void @arraylist_add_int(ptr %tmp12, i32 1)
  %tmp16 = load ptr, ptr %tmp0, align 8
  call void @arraylist_add_int(ptr %tmp16, i32 2)
  %tmp20 = load ptr, ptr %tmp3, align 8
  call void @arraylist_add_double(ptr %tmp20, double 1.100000e+00)
  %tmp24 = load ptr, ptr %tmp3, align 8
  call void @arraylist_add_double(ptr %tmp24, double 2.200000e+00)
  %tmp28 = load ptr, ptr %tmp5, align 8
  %tmp30 = call ptr @createString(ptr @.str0)
  call void @arraylist_add_ptr(ptr %tmp28, ptr %tmp30)
  %tmp34 = load ptr, ptr %tmp5, align 8
  %tmp36 = call ptr @createString(ptr @.str1)
  call void @arraylist_add_ptr(ptr %tmp34, ptr %tmp36)
  %tmp40 = load ptr, ptr %tmp8, align 8
  call void @arraylist_add_bool(ptr %tmp40, i1 true)
  %tmp44 = call ptr @arraylist_create_int(i64 10)
  %tmp46 = call ptr @arraylist_create_double(i64 10)
  %tmp48 = call ptr @arraylist_create(i64 10)
  %tmp51 = call ptr @arraylist_create_bool(i64 10)
  %tmp53 = call ptr @malloc(i64 32)
  %tmp57 = load ptr, ptr %tmp0, align 8
  %tmp59 = load ptr, ptr %tmp57, align 8
  %tmp60 = getelementptr inbounds %struct.ArrayListInt, ptr %tmp57, i32 0, i32 1
  %tmp61 = load i64, ptr %tmp60, align 4
  %tmp62 = call ptr @arraylist_create_int(i64 %tmp61)
  call void @arraylist_addAll_int(ptr %tmp62, ptr %tmp59, i64 %tmp61)
  store ptr %tmp62, ptr %tmp53, align 8
  %tmp64 = getelementptr inbounds %Multi, ptr %tmp53, i32 0, i32 1
  %tmp65 = load ptr, ptr %tmp3, align 8
  %tmp67 = load ptr, ptr %tmp65, align 8
  %tmp68 = getelementptr inbounds %struct.ArrayListDouble, ptr %tmp65, i32 0, i32 1
  %tmp69 = load i64, ptr %tmp68, align 4
  %tmp70 = call ptr @arraylist_create_double(i64 %tmp69)
  call void @arraylist_addAll_double(ptr %tmp70, ptr %tmp67, i64 %tmp69)
  store ptr %tmp70, ptr %tmp64, align 8
  %tmp72 = getelementptr inbounds %Multi, ptr %tmp53, i32 0, i32 2
  %tmp73 = load ptr, ptr %tmp5, align 8
  %tmp74 = call i32 @length(ptr %tmp73)
  %tmp75 = zext i32 %tmp74 to i64
  %tmp76 = call ptr @arraylist_create(i64 %tmp75)
  %tmp811 = icmp ult i64 0, %tmp75
  br i1 %tmp811, label %list_copy_body_tmp79.lr.ph, label %list_copy_end_tmp79

list_copy_body_tmp79.lr.ph:                       ; preds = %0
  br label %list_copy_body_tmp79

list_copy_body_tmp79:                             ; preds = %list_copy_body_tmp79.lr.ph, %list_copy_body_tmp79
  %tmp78.02 = phi i64 [ 0, %list_copy_body_tmp79.lr.ph ], [ %tmp88, %list_copy_body_tmp79 ]
  %tmp82 = call ptr @arraylist_get_ptr(ptr %tmp73, i64 %tmp78.02)
  %tmp85 = load ptr, ptr %tmp82, align 8
  %tmp86 = call ptr @createString(ptr %tmp85)
  call void @arraylist_add_ptr(ptr %tmp76, ptr %tmp86)
  %tmp88 = add i64 %tmp78.02, 1
  %tmp81 = icmp ult i64 %tmp88, %tmp75
  br i1 %tmp81, label %list_copy_body_tmp79, label %list_copy_cond_tmp79.list_copy_end_tmp79_crit_edge

list_copy_cond_tmp79.list_copy_end_tmp79_crit_edge: ; preds = %list_copy_body_tmp79
  br label %list_copy_end_tmp79

list_copy_end_tmp79:                              ; preds = %list_copy_cond_tmp79.list_copy_end_tmp79_crit_edge, %0
  store ptr %tmp76, ptr %tmp72, align 8
  %tmp90 = getelementptr inbounds %Multi, ptr %tmp53, i32 0, i32 3
  %tmp91 = load ptr, ptr %tmp8, align 8
  %tmp93 = load ptr, ptr %tmp91, align 8
  %tmp94 = getelementptr inbounds %struct.ArrayListBool, ptr %tmp91, i32 0, i32 1
  %tmp95 = load i64, ptr %tmp94, align 4
  %tmp96 = call ptr @arraylist_create_bool(i64 %tmp95)
  call void @arraylist_addAll_bool(ptr %tmp96, ptr %tmp93, i64 %tmp95)
  store ptr %tmp96, ptr %tmp90, align 8
  %tmp99 = load ptr, ptr %tmp53, align 8
  call void @arraylist_add_int(ptr %tmp99, i32 99)
  %tmp103 = load ptr, ptr %tmp64, align 8
  call void @arraylist_add_double(ptr %tmp103, double 9.900000e+00)
  %tmp107 = load ptr, ptr %tmp72, align 8
  %tmp109 = call ptr @createString(ptr @.str2)
  call void @arraylist_add_ptr(ptr %tmp107, ptr %tmp109)
  %tmp113 = load ptr, ptr %tmp90, align 8
  call void @arraylist_add_bool(ptr %tmp113, i1 true)
  %1 = call i32 (ptr, ...) @printf(ptr @.strStr, ptr @.str3)
  %2 = call i32 (ptr, ...) @printf(ptr @.strNewLine)
  call void @print_Multi(ptr %tmp0)
  %3 = call i32 (ptr, ...) @printf(ptr @.strNewLine)
  %4 = call i32 (ptr, ...) @printf(ptr @.strStr, ptr @.str4)
  %5 = call i32 (ptr, ...) @printf(ptr @.strNewLine)
  call void @print_Multi(ptr %tmp53)
  %6 = call i32 (ptr, ...) @printf(ptr @.strNewLine)
  %7 = call i32 (ptr, ...) @printf(ptr @.strStr, ptr @.str5)
  %8 = call i32 @getchar()
  ret i32 0
}
