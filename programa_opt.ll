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
  %tmp71 = call ptr @malloc(i64 32)
  %tmp75 = load ptr, ptr %tmp34, align 8
  %tmp77 = load ptr, ptr %tmp75, align 8
  %tmp78 = call ptr @createString(ptr %tmp77)
  store ptr %tmp78, ptr %tmp71, align 8
  %tmp80 = getelementptr inbounds %Pessoa, ptr %tmp71, i32 0, i32 1
  %tmp81 = load i32, ptr %tmp38, align 4
  store i32 %tmp81, ptr %tmp80, align 4
  %tmp83 = getelementptr inbounds %Pessoa, ptr %tmp71, i32 0, i32 2
  %tmp84 = load ptr, ptr %tmp39, align 8
  %tmp88 = call ptr @malloc(i64 24)
  %tmp92 = load ptr, ptr %tmp84, align 8
  %tmp94 = load ptr, ptr %tmp92, align 8
  %tmp95 = call ptr @createString(ptr %tmp94)
  store ptr %tmp95, ptr %tmp88, align 8
  %tmp96 = getelementptr inbounds %Endereco, ptr %tmp84, i32 0, i32 1
  %tmp97 = getelementptr inbounds %Endereco, ptr %tmp88, i32 0, i32 1
  %tmp98 = load ptr, ptr %tmp96, align 8
  %tmp100 = load ptr, ptr %tmp98, align 8
  %tmp101 = call ptr @createString(ptr %tmp100)
  store ptr %tmp101, ptr %tmp97, align 8
  %tmp102 = getelementptr inbounds %Endereco, ptr %tmp84, i32 0, i32 2
  %tmp103 = getelementptr inbounds %Endereco, ptr %tmp88, i32 0, i32 2
  %tmp104 = load ptr, ptr %tmp102, align 8
  %tmp108 = call ptr @malloc(i64 8)
  %tmp112 = load ptr, ptr %tmp104, align 8
  %tmp114 = load ptr, ptr %tmp112, align 8
  %tmp115 = call ptr @createString(ptr %tmp114)
  store ptr %tmp115, ptr %tmp108, align 8
  store ptr %tmp108, ptr %tmp103, align 8
  store ptr %tmp88, ptr %tmp83, align 8
  %tmp119 = getelementptr inbounds %Pessoa, ptr %tmp71, i32 0, i32 3
  %tmp120 = load ptr, ptr %tmp42, align 8
  %tmp121 = call i32 @length(ptr %tmp120)
  %tmp122 = zext i32 %tmp121 to i64
  %tmp123 = call ptr @arraylist_create(i64 %tmp122)
  store ptr %tmp123, ptr %tmp119, align 8
  %tmp128 = call ptr @createString(ptr @.str6)
  store ptr %tmp128, ptr %tmp71, align 8
  %tmp132 = load ptr, ptr %tmp119, align 8
  %tmp134 = call ptr @createString(ptr @.str7)
  call void @arraylist_add_String(ptr %tmp132, ptr %tmp134)
  %tmp139 = call ptr @createString(ptr @.str8)
  store ptr %tmp139, ptr %tmp13, align 8
  %tmp144 = call ptr @createString(ptr @.str9)
  store ptr %tmp144, ptr %tmp18, align 8
  %tmp148 = load ptr, ptr %tmp83, align 8
  %tmp150 = call ptr @createString(ptr @.str10)
  store ptr %tmp150, ptr %tmp148, align 8
  %tmp153 = call ptr @createString(ptr @.str11)
  %tmp154 = getelementptr inbounds %Endereco, ptr %tmp148, i32 0, i32 1
  store ptr %tmp153, ptr %tmp154, align 8
  %tmp155 = getelementptr inbounds %Endereco, ptr %tmp148, i32 0, i32 2
  %tmp156 = load ptr, ptr %tmp155, align 8
  %tmp158 = call ptr @createString(ptr @.str12)
  store ptr %tmp158, ptr %tmp156, align 8
  %2 = call i32 (ptr, ...) @printf(ptr @.strStr, ptr @.str13)
  %3 = call i32 (ptr, ...) @printf(ptr @.strNewLine)
  call void @print_Pessoa(ptr %tmp34)
  %4 = call i32 (ptr, ...) @printf(ptr @.strNewLine)
  %tmp162 = call ptr @malloc(i64 24)
  %tmp164 = call ptr @createString(ptr null)
  store ptr %tmp164, ptr %tmp162, align 8
  %tmp166 = call ptr @createString(ptr null)
  %tmp167 = getelementptr inbounds %Endereco, ptr %tmp162, i32 0, i32 1
  store ptr %tmp166, ptr %tmp167, align 8
  %tmp168 = getelementptr inbounds %Endereco, ptr %tmp162, i32 0, i32 2
  store ptr null, ptr %tmp168, align 8
  %tmp172 = call ptr @malloc(i64 24)
  %tmp176 = load ptr, ptr %tmp13, align 8
  %tmp178 = load ptr, ptr %tmp176, align 8
  %tmp179 = call ptr @createString(ptr %tmp178)
  store ptr %tmp179, ptr %tmp172, align 8
  %tmp181 = getelementptr inbounds %Endereco, ptr %tmp172, i32 0, i32 1
  %tmp182 = load ptr, ptr %tmp18, align 8
  %tmp184 = load ptr, ptr %tmp182, align 8
  %tmp185 = call ptr @createString(ptr %tmp184)
  store ptr %tmp185, ptr %tmp181, align 8
  %tmp187 = getelementptr inbounds %Endereco, ptr %tmp172, i32 0, i32 2
  %tmp188 = load ptr, ptr %tmp19, align 8
  %tmp192 = call ptr @malloc(i64 8)
  %tmp196 = load ptr, ptr %tmp188, align 8
  %tmp198 = load ptr, ptr %tmp196, align 8
  %tmp199 = call ptr @createString(ptr %tmp198)
  store ptr %tmp199, ptr %tmp192, align 8
  store ptr %tmp192, ptr %tmp187, align 8
  call void @print_Endereco(ptr %tmp172)
  %5 = call i32 (ptr, ...) @printf(ptr @.strNewLine)
  %6 = call i32 (ptr, ...) @printf(ptr @.strStr, ptr @.str14)
  %7 = call i32 (ptr, ...) @printf(ptr @.strNewLine)
  call void @print_Pessoa(ptr %tmp71)
  %8 = call i32 @getchar()
  ret i32 0
}
