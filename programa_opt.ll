; ModuleID = 'programa.ll'
source_filename = "programa.ll"

%Endereco = type { ptr, ptr, ptr }
%Pessoa = type { ptr, i32, ptr, ptr }

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
  %tmp40 = call ptr @arraylist_create(i64 10)
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
  %tmp74 = call ptr @arraylist_create(i64 10)
  %tmp76 = getelementptr inbounds %Pessoa, ptr %tmp68, i32 0, i32 3
  store ptr %tmp74, ptr %tmp76, align 8
  %tmp80 = call ptr @malloc(i64 32)
  %tmp84 = load ptr, ptr %tmp34, align 8
  %tmp86 = load ptr, ptr %tmp84, align 8
  %tmp87 = call ptr @createString(ptr %tmp86)
  store ptr %tmp87, ptr %tmp80, align 8
  %tmp89 = getelementptr inbounds %Pessoa, ptr %tmp80, i32 0, i32 1
  %tmp90 = load i32, ptr %tmp38, align 4
  store i32 %tmp90, ptr %tmp89, align 4
  %tmp92 = getelementptr inbounds %Pessoa, ptr %tmp80, i32 0, i32 2
  %tmp93 = load ptr, ptr %tmp39, align 8
  %tmp97 = call ptr @malloc(i64 24)
  %tmp101 = load ptr, ptr %tmp93, align 8
  %tmp103 = load ptr, ptr %tmp101, align 8
  %tmp104 = call ptr @createString(ptr %tmp103)
  store ptr %tmp104, ptr %tmp97, align 8
  %tmp105 = getelementptr inbounds %Endereco, ptr %tmp93, i32 0, i32 1
  %tmp106 = getelementptr inbounds %Endereco, ptr %tmp97, i32 0, i32 1
  %tmp107 = load ptr, ptr %tmp105, align 8
  %tmp109 = load ptr, ptr %tmp107, align 8
  %tmp110 = call ptr @createString(ptr %tmp109)
  store ptr %tmp110, ptr %tmp106, align 8
  %tmp111 = getelementptr inbounds %Endereco, ptr %tmp93, i32 0, i32 2
  %tmp112 = getelementptr inbounds %Endereco, ptr %tmp97, i32 0, i32 2
  %tmp113 = load ptr, ptr %tmp111, align 8
  %tmp117 = call ptr @malloc(i64 8)
  %tmp121 = load ptr, ptr %tmp113, align 8
  %tmp123 = load ptr, ptr %tmp121, align 8
  %tmp124 = call ptr @createString(ptr %tmp123)
  store ptr %tmp124, ptr %tmp117, align 8
  store ptr %tmp117, ptr %tmp112, align 8
  store ptr %tmp97, ptr %tmp92, align 8
  %tmp128 = getelementptr inbounds %Pessoa, ptr %tmp80, i32 0, i32 3
  %tmp129 = load ptr, ptr %tmp42, align 8
  %tmp130 = call i32 @length(ptr %tmp129)
  %tmp131 = zext i32 %tmp130 to i64
  %tmp132 = call ptr @arraylist_create(i64 %tmp131)
  store ptr %tmp132, ptr %tmp128, align 8
  %tmp137 = call ptr @createString(ptr @.str6)
  store ptr %tmp137, ptr %tmp80, align 8
  %tmp141 = load ptr, ptr %tmp128, align 8
  %tmp143 = call ptr @createString(ptr @.str7)
  call void @arraylist_add_String(ptr %tmp141, ptr %tmp143)
  %tmp148 = call ptr @createString(ptr @.str8)
  store ptr %tmp148, ptr %tmp13, align 8
  %tmp153 = call ptr @createString(ptr @.str9)
  store ptr %tmp153, ptr %tmp18, align 8
  %tmp157 = load ptr, ptr %tmp92, align 8
  %tmp159 = call ptr @createString(ptr @.str10)
  store ptr %tmp159, ptr %tmp157, align 8
  %tmp162 = call ptr @createString(ptr @.str11)
  %tmp163 = getelementptr inbounds %Endereco, ptr %tmp157, i32 0, i32 1
  store ptr %tmp162, ptr %tmp163, align 8
  %tmp164 = getelementptr inbounds %Endereco, ptr %tmp157, i32 0, i32 2
  %tmp165 = load ptr, ptr %tmp164, align 8
  %tmp167 = call ptr @createString(ptr @.str12)
  store ptr %tmp167, ptr %tmp165, align 8
  %2 = call i32 (ptr, ...) @printf(ptr @.strStr, ptr @.str13)
  %3 = call i32 (ptr, ...) @printf(ptr @.strNewLine)
  call void @print_Pessoa(ptr %tmp34)
  %4 = call i32 (ptr, ...) @printf(ptr @.strNewLine)
  %5 = call i32 (ptr, ...) @printf(ptr @.strStr, ptr @.str14)
  %6 = call i32 (ptr, ...) @printf(ptr @.strNewLine)
  call void @print_Pessoa(ptr %tmp80)
  %7 = call i32 @getchar()
  ret i32 0
}
