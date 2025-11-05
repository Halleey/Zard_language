; ModuleID = 'programa.ll'
source_filename = "programa.ll"

%Endereco = type { ptr, ptr, ptr }
%Pessoa = type { ptr, i32, ptr, ptr }
%Pais = type { ptr }

@.strChar = private constant [3 x i8] c"%c\00"
@.strTrue = private constant [6 x i8] c"true\0A\00"
@.strFalse = private constant [7 x i8] c"false\0A\00"
@.strInt = private constant [4 x i8] c"%d\0A\00"
@.strDouble = private constant [4 x i8] c"%f\0A\00"
@.strFloat = private constant [4 x i8] c"%f\0A\00"
@.strStr = private constant [4 x i8] c"%s\0A\00"
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
@.str10 = private constant [20 x i8] c"=== Original p1 ===\00"
@.str11 = private constant [9 x i8] c"Nova Rua\00"
@.str12 = private constant [8 x i8] c"Zurique\00"
@.str13 = private constant [8 x i8] c"Su\C3\AD\C3\A7a\00"
@.str14 = private constant [17 x i8] c"=== Clone p2 ===\00"

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

define void @print_Pais(ptr %p) {
entry:
  %val0 = load ptr, ptr %p, align 8
  call void @printString(ptr %val0)
  ret void
}

define void @print_Endereco(ptr %p) {
entry:
  %val0 = load ptr, ptr %p, align 8
  call void @printString(ptr %val0)
  %f1 = getelementptr inbounds %Endereco, ptr %p, i32 0, i32 1
  %val1 = load ptr, ptr %f1, align 8
  call void @printString(ptr %val1)
  %f2 = getelementptr inbounds %Endereco, ptr %p, i32 0, i32 2
  %val2 = load ptr, ptr %f2, align 8
  call void @print_Pais(ptr %val2)
  ret void
}

define void @print_Pessoa(ptr %p) {
entry:
  %val0 = load ptr, ptr %p, align 8
  call void @printString(ptr %val0)
  %f1 = getelementptr inbounds %Pessoa, ptr %p, i32 0, i32 1
  %val1 = load i32, ptr %f1, align 4
  %0 = call i32 (ptr, ...) @printf(ptr @.strInt, i32 %val1)
  %f2 = getelementptr inbounds %Pessoa, ptr %p, i32 0, i32 2
  %val2 = load ptr, ptr %f2, align 8
  call void @print_Endereco(ptr %val2)
  %f3 = getelementptr inbounds %Pessoa, ptr %p, i32 0, i32 3
  %val3 = load ptr, ptr %f3, align 8
  call void @arraylist_print_string(ptr %val3)
  ret void
}

define i32 @main() {
  %1 = call i32 (ptr, ...) @printf(ptr @.strFloat, double 0x40091EB860000000)
  %tmp4 = alloca %Pais, align 8
  %tmp5 = call ptr @createString(ptr null)
  store ptr %tmp5, ptr %tmp4, align 8
  %tmp10 = call ptr @createString(ptr @.str0)
  store ptr %tmp10, ptr %tmp4, align 8
  %tmp12 = alloca %Endereco, align 8
  %tmp13 = call ptr @createString(ptr null)
  store ptr %tmp13, ptr %tmp12, align 8
  %tmp15 = call ptr @createString(ptr null)
  %tmp16 = getelementptr inbounds %Endereco, ptr %tmp12, i32 0, i32 1
  store ptr %tmp15, ptr %tmp16, align 8
  %tmp17 = getelementptr inbounds %Endereco, ptr %tmp12, i32 0, i32 2
  store ptr null, ptr %tmp17, align 8
  %tmp21 = call ptr @createString(ptr @.str1)
  store ptr %tmp21, ptr %tmp12, align 8
  %tmp26 = call ptr @createString(ptr @.str2)
  store ptr %tmp26, ptr %tmp16, align 8
  store ptr %tmp4, ptr %tmp17, align 8
  %tmp32 = alloca %Pessoa, align 8
  %tmp33 = call ptr @createString(ptr null)
  store ptr %tmp33, ptr %tmp32, align 8
  %tmp35 = getelementptr inbounds %Pessoa, ptr %tmp32, i32 0, i32 1
  store i32 0, ptr %tmp35, align 4
  %tmp36 = getelementptr inbounds %Pessoa, ptr %tmp32, i32 0, i32 2
  store ptr null, ptr %tmp36, align 8
  %tmp37 = call ptr @arraylist_create(i64 10)
  %tmp38 = getelementptr inbounds %Pessoa, ptr %tmp32, i32 0, i32 3
  store ptr %tmp37, ptr %tmp38, align 8
  %tmp42 = call ptr @createString(ptr @.str3)
  store ptr %tmp42, ptr %tmp32, align 8
  store i32 25, ptr %tmp35, align 4
  store ptr %tmp12, ptr %tmp36, align 8
  %tmp54 = load ptr, ptr %tmp38, align 8
  %tmp56 = call ptr @createString(ptr @.str4)
  call void @arraylist_add_String(ptr %tmp54, ptr %tmp56)
  %tmp60 = load ptr, ptr %tmp38, align 8
  %tmp62 = call ptr @createString(ptr @.str5)
  call void @arraylist_add_String(ptr %tmp60, ptr %tmp62)
  %tmp64 = alloca %Pessoa, align 8
  %tmp65 = call ptr @createString(ptr null)
  store ptr %tmp65, ptr %tmp64, align 8
  %tmp67 = getelementptr inbounds %Pessoa, ptr %tmp64, i32 0, i32 1
  store i32 0, ptr %tmp67, align 4
  %tmp68 = getelementptr inbounds %Pessoa, ptr %tmp64, i32 0, i32 2
  store ptr null, ptr %tmp68, align 8
  %tmp69 = call ptr @arraylist_create(i64 10)
  %tmp70 = getelementptr inbounds %Pessoa, ptr %tmp64, i32 0, i32 3
  store ptr %tmp69, ptr %tmp70, align 8
  %tmp75 = load ptr, ptr %tmp32, align 8
  store ptr %tmp75, ptr %tmp64, align 8
  %tmp78 = load i32, ptr %tmp35, align 4
  store i32 %tmp78, ptr %tmp67, align 4
  %tmp81 = load ptr, ptr %tmp36, align 8
  %tmp82 = alloca %Endereco, align 8
  %tmp85 = load ptr, ptr %tmp81, align 8
  store ptr %tmp85, ptr %tmp82, align 8
  %tmp86 = getelementptr inbounds %Endereco, ptr %tmp81, i32 0, i32 1
  %tmp87 = getelementptr inbounds %Endereco, ptr %tmp82, i32 0, i32 1
  %tmp88 = load ptr, ptr %tmp86, align 8
  store ptr %tmp88, ptr %tmp87, align 8
  %tmp89 = getelementptr inbounds %Endereco, ptr %tmp81, i32 0, i32 2
  %tmp90 = getelementptr inbounds %Endereco, ptr %tmp82, i32 0, i32 2
  %tmp91 = load ptr, ptr %tmp89, align 8
  %tmp92 = alloca %Pais, align 8
  %tmp95 = load ptr, ptr %tmp91, align 8
  store ptr %tmp95, ptr %tmp92, align 8
  store ptr %tmp92, ptr %tmp90, align 8
  store ptr %tmp82, ptr %tmp68, align 8
  %tmp98 = load ptr, ptr %tmp38, align 8
  store ptr %tmp98, ptr %tmp70, align 8
  %tmp102 = call ptr @createString(ptr @.str6)
  store ptr %tmp102, ptr %tmp64, align 8
  %tmp106 = load ptr, ptr %tmp70, align 8
  %tmp108 = call ptr @createString(ptr @.str7)
  call void @arraylist_add_String(ptr %tmp106, ptr %tmp108)
  %tmp113 = call ptr @createString(ptr @.str8)
  store ptr %tmp113, ptr %tmp12, align 8
  %tmp118 = call ptr @createString(ptr @.str9)
  store ptr %tmp118, ptr %tmp16, align 8
  %2 = call i32 (ptr, ...) @printf(ptr @.strStr, ptr @.str10)
  call void @print_Pessoa(ptr %tmp32)
  %tmp124 = load ptr, ptr %tmp68, align 8
  %tmp126 = call ptr @createString(ptr @.str11)
  store ptr %tmp126, ptr %tmp124, align 8
  %tmp129 = call ptr @createString(ptr @.str12)
  %tmp130 = getelementptr inbounds %Endereco, ptr %tmp124, i32 0, i32 1
  store ptr %tmp129, ptr %tmp130, align 8
  %tmp131 = getelementptr inbounds %Endereco, ptr %tmp124, i32 0, i32 2
  %tmp132 = load ptr, ptr %tmp131, align 8
  %tmp134 = call ptr @createString(ptr @.str13)
  store ptr %tmp134, ptr %tmp132, align 8
  %3 = call i32 (ptr, ...) @printf(ptr @.strStr, ptr @.str14)
  call void @print_Pessoa(ptr %tmp64)
  %4 = call i32 @getchar()
  ret i32 0
}
