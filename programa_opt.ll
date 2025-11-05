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
  %tmp0 = alloca %Pais, align 8
  %tmp1 = call ptr @createString(ptr null)
  store ptr %tmp1, ptr %tmp0, align 8
  %tmp6 = call ptr @createString(ptr @.str0)
  store ptr %tmp6, ptr %tmp0, align 8
  %tmp8 = alloca %Endereco, align 8
  %tmp9 = call ptr @createString(ptr null)
  store ptr %tmp9, ptr %tmp8, align 8
  %tmp11 = call ptr @createString(ptr null)
  %tmp12 = getelementptr inbounds %Endereco, ptr %tmp8, i32 0, i32 1
  store ptr %tmp11, ptr %tmp12, align 8
  %tmp13 = getelementptr inbounds %Endereco, ptr %tmp8, i32 0, i32 2
  store ptr null, ptr %tmp13, align 8
  %tmp17 = call ptr @createString(ptr @.str1)
  store ptr %tmp17, ptr %tmp8, align 8
  %tmp22 = call ptr @createString(ptr @.str2)
  store ptr %tmp22, ptr %tmp12, align 8
  store ptr %tmp0, ptr %tmp13, align 8
  %tmp28 = alloca %Pessoa, align 8
  %tmp29 = call ptr @createString(ptr null)
  store ptr %tmp29, ptr %tmp28, align 8
  %tmp31 = getelementptr inbounds %Pessoa, ptr %tmp28, i32 0, i32 1
  store i32 0, ptr %tmp31, align 4
  %tmp32 = getelementptr inbounds %Pessoa, ptr %tmp28, i32 0, i32 2
  store ptr null, ptr %tmp32, align 8
  %tmp33 = call ptr @arraylist_create(i64 10)
  %tmp34 = getelementptr inbounds %Pessoa, ptr %tmp28, i32 0, i32 3
  store ptr %tmp33, ptr %tmp34, align 8
  %tmp38 = call ptr @createString(ptr @.str3)
  store ptr %tmp38, ptr %tmp28, align 8
  store i32 25, ptr %tmp31, align 4
  store ptr %tmp8, ptr %tmp32, align 8
  %tmp50 = load ptr, ptr %tmp34, align 8
  %tmp52 = call ptr @createString(ptr @.str4)
  call void @arraylist_add_String(ptr %tmp50, ptr %tmp52)
  %tmp56 = load ptr, ptr %tmp34, align 8
  %tmp58 = call ptr @createString(ptr @.str5)
  call void @arraylist_add_String(ptr %tmp56, ptr %tmp58)
  %tmp60 = alloca %Pessoa, align 8
  %tmp61 = call ptr @createString(ptr null)
  store ptr %tmp61, ptr %tmp60, align 8
  %tmp63 = getelementptr inbounds %Pessoa, ptr %tmp60, i32 0, i32 1
  store i32 0, ptr %tmp63, align 4
  %tmp64 = getelementptr inbounds %Pessoa, ptr %tmp60, i32 0, i32 2
  store ptr null, ptr %tmp64, align 8
  %tmp65 = call ptr @arraylist_create(i64 10)
  %tmp66 = getelementptr inbounds %Pessoa, ptr %tmp60, i32 0, i32 3
  store ptr %tmp65, ptr %tmp66, align 8
  %tmp71 = load ptr, ptr %tmp28, align 8
  store ptr %tmp71, ptr %tmp60, align 8
  %tmp74 = load i32, ptr %tmp31, align 4
  store i32 %tmp74, ptr %tmp63, align 4
  %tmp77 = load ptr, ptr %tmp32, align 8
  %tmp78 = alloca %Endereco, align 8
  %tmp81 = load ptr, ptr %tmp77, align 8
  store ptr %tmp81, ptr %tmp78, align 8
  %tmp82 = getelementptr inbounds %Endereco, ptr %tmp77, i32 0, i32 1
  %tmp83 = getelementptr inbounds %Endereco, ptr %tmp78, i32 0, i32 1
  %tmp84 = load ptr, ptr %tmp82, align 8
  store ptr %tmp84, ptr %tmp83, align 8
  %tmp85 = getelementptr inbounds %Endereco, ptr %tmp77, i32 0, i32 2
  %tmp86 = getelementptr inbounds %Endereco, ptr %tmp78, i32 0, i32 2
  %tmp87 = load ptr, ptr %tmp85, align 8
  %tmp88 = alloca %Pais, align 8
  %tmp91 = load ptr, ptr %tmp87, align 8
  store ptr %tmp91, ptr %tmp88, align 8
  store ptr %tmp88, ptr %tmp86, align 8
  store ptr %tmp78, ptr %tmp64, align 8
  %tmp94 = load ptr, ptr %tmp34, align 8
  store ptr %tmp94, ptr %tmp66, align 8
  %tmp98 = call ptr @createString(ptr @.str6)
  store ptr %tmp98, ptr %tmp60, align 8
  %tmp102 = load ptr, ptr %tmp66, align 8
  %tmp104 = call ptr @createString(ptr @.str7)
  call void @arraylist_add_String(ptr %tmp102, ptr %tmp104)
  %tmp109 = call ptr @createString(ptr @.str8)
  store ptr %tmp109, ptr %tmp8, align 8
  %tmp114 = call ptr @createString(ptr @.str9)
  store ptr %tmp114, ptr %tmp12, align 8
  %1 = call i32 (ptr, ...) @printf(ptr @.strStr, ptr @.str10)
  call void @print_Pessoa(ptr %tmp28)
  %tmp120 = load ptr, ptr %tmp64, align 8
  %tmp122 = call ptr @createString(ptr @.str11)
  store ptr %tmp122, ptr %tmp120, align 8
  %tmp125 = call ptr @createString(ptr @.str12)
  %tmp126 = getelementptr inbounds %Endereco, ptr %tmp120, i32 0, i32 1
  store ptr %tmp125, ptr %tmp126, align 8
  %tmp127 = getelementptr inbounds %Endereco, ptr %tmp120, i32 0, i32 2
  %tmp128 = load ptr, ptr %tmp127, align 8
  %tmp130 = call ptr @createString(ptr @.str13)
  store ptr %tmp130, ptr %tmp128, align 8
  %2 = call i32 (ptr, ...) @printf(ptr @.strStr, ptr @.str14)
  call void @print_Pessoa(ptr %tmp60)
  %3 = call i32 @getchar()
  ret i32 0
}
