; ModuleID = 'programa.ll'
source_filename = "programa.ll"

%Endereco = type { ptr, ptr, ptr }
%Pessoa = type { ptr, i32, ptr }
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
@.str4 = private constant [5 x i8] c"Zard\00"
@.str5 = private constant [6 x i8] c"teste\00"
@.str6 = private constant [13 x i8] c"Santo andr\C3\A9\00"
@.str7 = private constant [20 x i8] c"=== Original p1 ===\00"
@.str8 = private constant [9 x i8] c"Nova Rua\00"
@.str9 = private constant [8 x i8] c"Zurique\00"
@.str10 = private constant [8 x i8] c"Su\C3\AD\C3\A7a\00"
@.str11 = private constant [17 x i8] c"=== Clone p2 ===\00"

declare i32 @printf(ptr, ...)

declare i32 @getchar()

declare void @printString(ptr)

declare ptr @malloc(i64)

declare void @setString(ptr, ptr)

declare ptr @createString(ptr)

declare i1 @strcmp_eq(ptr, ptr)

declare i1 @strcmp_neq(ptr, ptr)

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
  %tmp36 = call ptr @createString(ptr @.str3)
  store ptr %tmp36, ptr %tmp28, align 8
  store i32 25, ptr %tmp31, align 4
  store ptr %tmp8, ptr %tmp32, align 8
  %tmp46 = alloca %Pessoa, align 8
  %tmp47 = call ptr @createString(ptr null)
  store ptr %tmp47, ptr %tmp46, align 8
  %tmp49 = getelementptr inbounds %Pessoa, ptr %tmp46, i32 0, i32 1
  store i32 0, ptr %tmp49, align 4
  %tmp50 = getelementptr inbounds %Pessoa, ptr %tmp46, i32 0, i32 2
  store ptr null, ptr %tmp50, align 8
  %tmp55 = load ptr, ptr %tmp28, align 8
  store ptr %tmp55, ptr %tmp46, align 8
  %tmp58 = load i32, ptr %tmp31, align 4
  store i32 %tmp58, ptr %tmp49, align 4
  %tmp61 = load ptr, ptr %tmp32, align 8
  %tmp62 = alloca %Endereco, align 8
  %tmp65 = load ptr, ptr %tmp61, align 8
  store ptr %tmp65, ptr %tmp62, align 8
  %tmp66 = getelementptr inbounds %Endereco, ptr %tmp61, i32 0, i32 1
  %tmp67 = getelementptr inbounds %Endereco, ptr %tmp62, i32 0, i32 1
  %tmp68 = load ptr, ptr %tmp66, align 8
  store ptr %tmp68, ptr %tmp67, align 8
  %tmp69 = getelementptr inbounds %Endereco, ptr %tmp61, i32 0, i32 2
  %tmp70 = getelementptr inbounds %Endereco, ptr %tmp62, i32 0, i32 2
  %tmp71 = load ptr, ptr %tmp69, align 8
  %tmp72 = alloca %Pais, align 8
  %tmp75 = load ptr, ptr %tmp71, align 8
  store ptr %tmp75, ptr %tmp72, align 8
  store ptr %tmp72, ptr %tmp70, align 8
  store ptr %tmp62, ptr %tmp50, align 8
  %tmp79 = call ptr @createString(ptr @.str4)
  store ptr %tmp79, ptr %tmp46, align 8
  %tmp84 = call ptr @createString(ptr @.str5)
  store ptr %tmp84, ptr %tmp8, align 8
  %tmp89 = call ptr @createString(ptr @.str6)
  store ptr %tmp89, ptr %tmp12, align 8
  %1 = call i32 (ptr, ...) @printf(ptr @.strStr, ptr @.str7)
  call void @print_Pessoa(ptr %tmp28)
  %tmp95 = load ptr, ptr %tmp50, align 8
  %tmp97 = call ptr @createString(ptr @.str8)
  store ptr %tmp97, ptr %tmp95, align 8
  %tmp100 = call ptr @createString(ptr @.str9)
  %tmp101 = getelementptr inbounds %Endereco, ptr %tmp95, i32 0, i32 1
  store ptr %tmp100, ptr %tmp101, align 8
  %tmp102 = getelementptr inbounds %Endereco, ptr %tmp95, i32 0, i32 2
  %tmp103 = load ptr, ptr %tmp102, align 8
  %tmp105 = call ptr @createString(ptr @.str10)
  store ptr %tmp105, ptr %tmp103, align 8
  %2 = call i32 (ptr, ...) @printf(ptr @.strStr, ptr @.str11)
  call void @print_Pessoa(ptr %tmp46)
  %3 = call i32 @getchar()
  ret i32 0
}
