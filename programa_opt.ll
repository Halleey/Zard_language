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

declare void @arraylist_add_string(ptr, ptr)

declare void @arraylist_addAll_string(ptr, ptr, i64)

declare void @arraylist_print_string(ptr)

declare void @arraylist_add_String(ptr, ptr)

declare void @arraylist_addAll_String(ptr, ptr, i64)

declare void @removeItem(ptr, i64)

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
  %tmp39 = getelementptr inbounds %Pessoa, ptr %tmp32, i32 0, i32 3
  store ptr %tmp37, ptr %tmp39, align 8
  %tmp43 = call ptr @createString(ptr @.str3)
  store ptr %tmp43, ptr %tmp32, align 8
  store i32 25, ptr %tmp35, align 4
  store ptr %tmp12, ptr %tmp36, align 8
  %tmp55 = load ptr, ptr %tmp39, align 8
  %tmp57 = call ptr @createString(ptr @.str4)
  call void @arraylist_add_String(ptr %tmp55, ptr %tmp57)
  %tmp61 = load ptr, ptr %tmp39, align 8
  %tmp63 = call ptr @createString(ptr @.str5)
  call void @arraylist_add_String(ptr %tmp61, ptr %tmp63)
  %tmp65 = alloca %Pessoa, align 8
  %tmp66 = call ptr @createString(ptr null)
  store ptr %tmp66, ptr %tmp65, align 8
  %tmp68 = getelementptr inbounds %Pessoa, ptr %tmp65, i32 0, i32 1
  store i32 0, ptr %tmp68, align 4
  %tmp69 = getelementptr inbounds %Pessoa, ptr %tmp65, i32 0, i32 2
  store ptr null, ptr %tmp69, align 8
  %tmp70 = call ptr @arraylist_create(i64 10)
  %tmp72 = getelementptr inbounds %Pessoa, ptr %tmp65, i32 0, i32 3
  store ptr %tmp70, ptr %tmp72, align 8
  %tmp77 = load ptr, ptr %tmp32, align 8
  store ptr %tmp77, ptr %tmp65, align 8
  %tmp80 = load i32, ptr %tmp35, align 4
  store i32 %tmp80, ptr %tmp68, align 4
  %tmp83 = load ptr, ptr %tmp36, align 8
  %tmp84 = alloca %Endereco, align 8
  %tmp87 = load ptr, ptr %tmp83, align 8
  store ptr %tmp87, ptr %tmp84, align 8
  %tmp88 = getelementptr inbounds %Endereco, ptr %tmp83, i32 0, i32 1
  %tmp89 = getelementptr inbounds %Endereco, ptr %tmp84, i32 0, i32 1
  %tmp90 = load ptr, ptr %tmp88, align 8
  store ptr %tmp90, ptr %tmp89, align 8
  %tmp91 = getelementptr inbounds %Endereco, ptr %tmp83, i32 0, i32 2
  %tmp92 = getelementptr inbounds %Endereco, ptr %tmp84, i32 0, i32 2
  %tmp93 = load ptr, ptr %tmp91, align 8
  %tmp94 = alloca %Pais, align 8
  %tmp97 = load ptr, ptr %tmp93, align 8
  store ptr %tmp97, ptr %tmp94, align 8
  store ptr %tmp94, ptr %tmp92, align 8
  store ptr %tmp84, ptr %tmp69, align 8
  %tmp100 = load ptr, ptr %tmp39, align 8
  store ptr %tmp100, ptr %tmp72, align 8
  %tmp104 = call ptr @createString(ptr @.str6)
  store ptr %tmp104, ptr %tmp65, align 8
  %tmp108 = load ptr, ptr %tmp72, align 8
  %tmp110 = call ptr @createString(ptr @.str7)
  call void @arraylist_add_String(ptr %tmp108, ptr %tmp110)
  %tmp115 = call ptr @createString(ptr @.str8)
  store ptr %tmp115, ptr %tmp12, align 8
  %tmp120 = call ptr @createString(ptr @.str9)
  store ptr %tmp120, ptr %tmp16, align 8
  %2 = call i32 (ptr, ...) @printf(ptr @.strStr, ptr @.str10)
  call void @print_Pessoa(ptr %tmp32)
  %tmp126 = load ptr, ptr %tmp69, align 8
  %tmp128 = call ptr @createString(ptr @.str11)
  store ptr %tmp128, ptr %tmp126, align 8
  %tmp131 = call ptr @createString(ptr @.str12)
  %tmp132 = getelementptr inbounds %Endereco, ptr %tmp126, i32 0, i32 1
  store ptr %tmp131, ptr %tmp132, align 8
  %tmp133 = getelementptr inbounds %Endereco, ptr %tmp126, i32 0, i32 2
  %tmp134 = load ptr, ptr %tmp133, align 8
  %tmp136 = call ptr @createString(ptr @.str13)
  store ptr %tmp136, ptr %tmp134, align 8
  %3 = call i32 (ptr, ...) @printf(ptr @.strStr, ptr @.str14)
  call void @print_Pessoa(ptr %tmp65)
  %4 = call i32 @getchar()
  ret i32 0
}
