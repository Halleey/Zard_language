; ModuleID = 'programa.ll'
source_filename = "programa.ll"

%Endereco = type { ptr, ptr, ptr }
%Pessoa = type { ptr, i32, ptr, ptr }
%Pais = type { ptr }

@.strChar = private constant [3 x i8] c"%c\00"
@.strInt = private constant [3 x i8] c"%d\00"
@.strDouble = private constant [3 x i8] c"%f\00"
@.strFloat = private constant [3 x i8] c"%f\00"
@.strStr = private constant [3 x i8] c"%s\00"
@.strTrue = private constant [5 x i8] c"true\00"
@.strFalse = private constant [6 x i8] c"false\00"
@.strNewLine = private constant [2 x i8] c"\0A\00"
@.strEmpty = private constant [1 x i8] zeroinitializer
@.str0 = private constant [7 x i8] c"Brasil\00"
@.str1 = private constant [6 x i8] c"Rua A\00"
@.str2 = private constant [11 x i8] c"S\C3\A3o Paulo\00"
@.str3 = private constant [6 x i8] c"Alice\00"
@.str4 = private constant [14 x i8] c"11 99999-1111\00"
@.str5 = private constant [14 x i8] c"11 22222-3333\00"
@.str6 = private constant [5 x i8] c"Zard\00"
@.str7 = private constant [14 x i8] c"21 44444-5555\00"
@.str8 = private constant [6 x i8] c"teste\00"
@.str9 = private constant [13 x i8] c"Santo andr\C3\A9\00"
@.str10 = private constant [9 x i8] c"Nova Rua\00"
@.str11 = private constant [8 x i8] c"Zurique\00"
@.str12 = private constant [8 x i8] c"Su\C3\AD\C3\A7a\00"
@.str13 = private constant [20 x i8] c"=== Original p1 ===\00"
@.str14 = private constant [17 x i8] c"=== Clone p2 ===\00"

declare i32 @printf(ptr, ...)

declare i32 @getchar()

declare void @printString(ptr)

declare ptr @malloc(i64)

declare void @setString(ptr)

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

define void @print_Pais(ptr %p) {
entry:
  %v0 = load ptr, ptr %p, align 8
  call void @printString(ptr %v0)
  ret void
}

define void @print_Endereco(ptr %p) {
entry:
  %v0 = load ptr, ptr %p, align 8
  call void @printString(ptr %v0)
  %f1 = getelementptr inbounds %Endereco, ptr %p, i32 0, i32 1
  %v1 = load ptr, ptr %f1, align 8
  call void @printString(ptr %v1)
  %f2 = getelementptr inbounds %Endereco, ptr %p, i32 0, i32 2
  %v2 = load ptr, ptr %f2, align 8
  call void @print_Pais(ptr %v2)
  ret void
}

define void @print_Pessoa(ptr %p) {
entry:
  %v0 = load ptr, ptr %p, align 8
  call void @printString(ptr %v0)
  %f1 = getelementptr inbounds %Pessoa, ptr %p, i32 0, i32 1
  %v1 = load i32, ptr %f1, align 4
  %0 = call i32 (ptr, ...) @printf(ptr @.strInt, i32 %v1)
  %f2 = getelementptr inbounds %Pessoa, ptr %p, i32 0, i32 2
  %v2 = load ptr, ptr %f2, align 8
  call void @print_Endereco(ptr %v2)
  %f3 = getelementptr inbounds %Pessoa, ptr %p, i32 0, i32 3
  %v3 = load ptr, ptr %f3, align 8
  call void @arraylist_print_string(ptr %v3)
  ret void
}

define i32 @main() {
  %1 = call i32 (ptr, ...) @printf(ptr @.strFloat, double 0x40091EB860000000)
  %tmp4 = call ptr @malloc(i64 8)
  %tmp6 = call ptr @createString(ptr null)
  store ptr %tmp6, ptr %tmp4, align 8
  %tmp11 = call ptr @createString(ptr @.str0)
  store ptr %tmp11, ptr %tmp4, align 8
  %tmp13 = call ptr @malloc(i64 24)
  %tmp15 = call ptr @createString(ptr null)
  store ptr %tmp15, ptr %tmp13, align 8
  %tmp17 = call ptr @createString(ptr null)
  %tmp18 = getelementptr inbounds %Endereco, ptr %tmp13, i32 0, i32 1
  store ptr %tmp17, ptr %tmp18, align 8
  %tmp19 = getelementptr inbounds %Endereco, ptr %tmp13, i32 0, i32 2
  store ptr null, ptr %tmp19, align 8
  %tmp23 = call ptr @createString(ptr @.str1)
  store ptr %tmp23, ptr %tmp13, align 8
  %tmp28 = call ptr @createString(ptr @.str2)
  store ptr %tmp28, ptr %tmp18, align 8
  store ptr %tmp4, ptr %tmp19, align 8
  %tmp34 = call ptr @malloc(i64 28)
  %tmp36 = call ptr @createString(ptr null)
  store ptr %tmp36, ptr %tmp34, align 8
  %tmp38 = getelementptr inbounds %Pessoa, ptr %tmp34, i32 0, i32 1
  store i32 0, ptr %tmp38, align 4
  %tmp39 = getelementptr inbounds %Pessoa, ptr %tmp34, i32 0, i32 2
  store ptr null, ptr %tmp39, align 8
  %tmp40 = call ptr @arraylist_create(i64 4)
  %tmp42 = getelementptr inbounds %Pessoa, ptr %tmp34, i32 0, i32 3
  store ptr %tmp40, ptr %tmp42, align 8
  %tmp46 = call ptr @createString(ptr @.str3)
  store ptr %tmp46, ptr %tmp34, align 8
  store i32 25, ptr %tmp38, align 4
  store ptr %tmp13, ptr %tmp39, align 8
  %tmp58 = load ptr, ptr %tmp42, align 8
  %tmp60 = call ptr @createString(ptr @.str4)
  call void @arraylist_add_String(ptr %tmp58, ptr %tmp60)
  %tmp64 = load ptr, ptr %tmp42, align 8
  %tmp66 = call ptr @createString(ptr @.str5)
  call void @arraylist_add_String(ptr %tmp64, ptr %tmp66)
  %tmp68 = call ptr @malloc(i64 28)
  %tmp70 = call ptr @createString(ptr null)
  store ptr %tmp70, ptr %tmp68, align 8
  %tmp72 = getelementptr inbounds %Pessoa, ptr %tmp68, i32 0, i32 1
  store i32 0, ptr %tmp72, align 4
  %tmp73 = getelementptr inbounds %Pessoa, ptr %tmp68, i32 0, i32 2
  store ptr null, ptr %tmp73, align 8
  %tmp74 = call ptr @arraylist_create(i64 4)
  %tmp76 = getelementptr inbounds %Pessoa, ptr %tmp68, i32 0, i32 3
  store ptr %tmp74, ptr %tmp76, align 8
  %tmp81 = load ptr, ptr %tmp34, align 8
  store ptr %tmp81, ptr %tmp68, align 8
  %tmp84 = load i32, ptr %tmp38, align 4
  store i32 %tmp84, ptr %tmp72, align 4
  %tmp87 = load ptr, ptr %tmp39, align 8
  %tmp88 = alloca %Endereco, align 8
  %tmp91 = load ptr, ptr %tmp87, align 8
  store ptr %tmp91, ptr %tmp88, align 8
  %tmp92 = getelementptr inbounds %Endereco, ptr %tmp87, i32 0, i32 1
  %tmp93 = getelementptr inbounds %Endereco, ptr %tmp88, i32 0, i32 1
  %tmp94 = load ptr, ptr %tmp92, align 8
  store ptr %tmp94, ptr %tmp93, align 8
  %tmp95 = getelementptr inbounds %Endereco, ptr %tmp87, i32 0, i32 2
  %tmp96 = getelementptr inbounds %Endereco, ptr %tmp88, i32 0, i32 2
  %tmp97 = load ptr, ptr %tmp95, align 8
  %tmp98 = alloca %Pais, align 8
  %tmp101 = load ptr, ptr %tmp97, align 8
  store ptr %tmp101, ptr %tmp98, align 8
  store ptr %tmp98, ptr %tmp96, align 8
  store ptr %tmp88, ptr %tmp73, align 8
  %tmp104 = load ptr, ptr %tmp42, align 8
  %tmp105 = call i32 @length(ptr %tmp104)
  %tmp106 = zext i32 %tmp105 to i64
  %tmp107 = call ptr @arraylist_create(i64 %tmp106)
  %tmp1121 = icmp ult i64 0, %tmp106
  br i1 %tmp1121, label %list_copy_body_tmp110.lr.ph, label %list_copy_end_tmp110

list_copy_body_tmp110.lr.ph:                      ; preds = %0
  br label %list_copy_body_tmp110

list_copy_body_tmp110:                            ; preds = %list_copy_body_tmp110.lr.ph, %list_copy_body_tmp110
  %tmp109.02 = phi i64 [ 0, %list_copy_body_tmp110.lr.ph ], [ %tmp115, %list_copy_body_tmp110 ]
  %tmp113 = call ptr @arraylist_get_ptr(ptr %tmp104, i64 %tmp109.02)
  %tmp114 = call ptr @createString(ptr %tmp113)
  call void @arraylist_add_String(ptr %tmp107, ptr %tmp114)
  %tmp115 = add i64 %tmp109.02, 1
  %tmp112 = icmp ult i64 %tmp115, %tmp106
  br i1 %tmp112, label %list_copy_body_tmp110, label %list_copy_cond_tmp110.list_copy_end_tmp110_crit_edge

list_copy_cond_tmp110.list_copy_end_tmp110_crit_edge: ; preds = %list_copy_body_tmp110
  br label %list_copy_end_tmp110

list_copy_end_tmp110:                             ; preds = %list_copy_cond_tmp110.list_copy_end_tmp110_crit_edge, %0
  store ptr %tmp107, ptr %tmp76, align 8
  %tmp119 = call ptr @createString(ptr @.str6)
  store ptr %tmp119, ptr %tmp68, align 8
  %tmp123 = load ptr, ptr %tmp76, align 8
  %tmp125 = call ptr @createString(ptr @.str7)
  call void @arraylist_add_String(ptr %tmp123, ptr %tmp125)
  %tmp130 = call ptr @createString(ptr @.str8)
  store ptr %tmp130, ptr %tmp13, align 8
  %tmp135 = call ptr @createString(ptr @.str9)
  store ptr %tmp135, ptr %tmp18, align 8
  %tmp139 = load ptr, ptr %tmp76, align 8
  call void @removeItem(ptr %tmp139, i64 0)
  %tmp144 = load ptr, ptr %tmp73, align 8
  %tmp146 = call ptr @createString(ptr @.str10)
  store ptr %tmp146, ptr %tmp144, align 8
  %tmp149 = call ptr @createString(ptr @.str11)
  %tmp150 = getelementptr inbounds %Endereco, ptr %tmp144, i32 0, i32 1
  store ptr %tmp149, ptr %tmp150, align 8
  %tmp151 = getelementptr inbounds %Endereco, ptr %tmp144, i32 0, i32 2
  %tmp152 = load ptr, ptr %tmp151, align 8
  %tmp154 = call ptr @createString(ptr @.str12)
  store ptr %tmp154, ptr %tmp152, align 8
  %2 = call i32 (ptr, ...) @printf(ptr @.strStr, ptr @.str13)
  %3 = call i32 (ptr, ...) @printf(ptr @.strNewLine)
  call void @print_Pessoa(ptr %tmp34)
  %4 = call i32 (ptr, ...) @printf(ptr @.strNewLine)
  %5 = call i32 (ptr, ...) @printf(ptr @.strStr, ptr @.str14)
  %6 = call i32 (ptr, ...) @printf(ptr @.strNewLine)
  call void @print_Pessoa(ptr %tmp68)
  %7 = call i32 (ptr, ...) @printf(ptr @.strNewLine)
  %8 = call i32 @getchar()
  ret i32 0
}
