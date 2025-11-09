; ModuleID = 'programa.ll'
source_filename = "programa.ll"

%Endereco = type { ptr, ptr }
%Pessoa = type { ptr, i32, ptr }

@.strChar = private constant [3 x i8] c"%c\00"
@.strTrue = private constant [6 x i8] c"true\0A\00"
@.strFalse = private constant [7 x i8] c"false\0A\00"
@.strInt = private constant [4 x i8] c"%d\0A\00"
@.strDouble = private constant [4 x i8] c"%f\0A\00"
@.strFloat = private constant [4 x i8] c"%f\0A\00"
@.strStr = private constant [4 x i8] c"%s\0A\00"
@.strEmpty = private constant [1 x i8] zeroinitializer
@.str0 = private constant [6 x i8] c"Alice\00"
@.str1 = private constant [6 x i8] c"Rua A\00"
@.str2 = private constant [11 x i8] c"S\C3\A3o Paulo\00"
@.str3 = private constant [4 x i8] c"Bob\00"
@.str4 = private constant [12 x i8] c"Av. Central\00"
@.str5 = private constant [15 x i8] c"Rio de Janeiro\00"
@.str6 = private constant [37 x i8] c"A primeira pessoa mora em S\C3\A3o Paulo\00"
@.str7 = private constant [19 x i8] c" \C3\A9 menor de idade\00"
@.str8 = private constant [8 x i8] c"Pessoa:\00"

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

define void @print_Endereco(ptr %p) {
entry:
  %val0 = load ptr, ptr %p, align 8
  call void @printString(ptr %val0)
  %f1 = getelementptr inbounds %Endereco, ptr %p, i32 0, i32 1
  %val1 = load ptr, ptr %f1, align 8
  call void @printString(ptr %val1)
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
  %tmp0 = call ptr @arraylist_create(i64 4)
  %tmp3 = alloca %Pessoa, align 8
  %tmp4 = call ptr @createString(ptr null)
  store ptr %tmp4, ptr %tmp3, align 8
  %tmp6 = getelementptr inbounds %Pessoa, ptr %tmp3, i32 0, i32 1
  store i32 0, ptr %tmp6, align 4
  %tmp7 = getelementptr inbounds %Pessoa, ptr %tmp3, i32 0, i32 2
  store ptr null, ptr %tmp7, align 8
  %tmp11 = call ptr @createString(ptr @.str0)
  store ptr %tmp11, ptr %tmp3, align 8
  store i32 25, ptr %tmp6, align 4
  %tmp17 = alloca %Endereco, align 8
  %tmp18 = call ptr @createString(ptr null)
  store ptr %tmp18, ptr %tmp17, align 8
  %tmp20 = call ptr @createString(ptr null)
  %tmp21 = getelementptr inbounds %Endereco, ptr %tmp17, i32 0, i32 1
  store ptr %tmp20, ptr %tmp21, align 8
  %tmp25 = call ptr @createString(ptr @.str1)
  store ptr %tmp25, ptr %tmp17, align 8
  %tmp30 = call ptr @createString(ptr @.str2)
  store ptr %tmp30, ptr %tmp21, align 8
  store ptr %tmp17, ptr %tmp7, align 8
  %tmp36 = alloca %Pessoa, align 8
  %tmp37 = call ptr @createString(ptr null)
  store ptr %tmp37, ptr %tmp36, align 8
  %tmp39 = getelementptr inbounds %Pessoa, ptr %tmp36, i32 0, i32 1
  store i32 0, ptr %tmp39, align 4
  %tmp40 = getelementptr inbounds %Pessoa, ptr %tmp36, i32 0, i32 2
  store ptr null, ptr %tmp40, align 8
  %tmp44 = call ptr @createString(ptr @.str3)
  store ptr %tmp44, ptr %tmp36, align 8
  store i32 17, ptr %tmp39, align 4
  %tmp50 = alloca %Endereco, align 8
  %tmp51 = call ptr @createString(ptr null)
  store ptr %tmp51, ptr %tmp50, align 8
  %tmp53 = call ptr @createString(ptr null)
  %tmp54 = getelementptr inbounds %Endereco, ptr %tmp50, i32 0, i32 1
  store ptr %tmp53, ptr %tmp54, align 8
  %tmp58 = call ptr @createString(ptr @.str4)
  store ptr %tmp58, ptr %tmp50, align 8
  %tmp63 = call ptr @createString(ptr @.str5)
  store ptr %tmp63, ptr %tmp54, align 8
  store ptr %tmp50, ptr %tmp40, align 8
  call void @arraylist_add_ptr(ptr %tmp0, ptr %tmp3)
  call void @arraylist_add_ptr(ptr %tmp0, ptr %tmp36)
  %tmp78 = call ptr @arraylist_get_ptr(ptr %tmp0, i64 0)
  %tmp81 = load ptr, ptr %tmp78, align 8
  call void @printString(ptr %tmp81)
  %tmp85 = call ptr @arraylist_get_ptr(ptr %tmp0, i64 0)
  %tmp87 = getelementptr inbounds %Pessoa, ptr %tmp85, i32 0, i32 2
  %tmp88 = load ptr, ptr %tmp87, align 8
  %tmp90 = load ptr, ptr %tmp88, align 8
  call void @printString(ptr %tmp90)
  %tmp94 = call ptr @arraylist_get_ptr(ptr %tmp0, i64 0)
  %tmp96 = getelementptr inbounds %Pessoa, ptr %tmp94, i32 0, i32 2
  %tmp97 = load ptr, ptr %tmp96, align 8
  %tmp98 = getelementptr inbounds %Endereco, ptr %tmp97, i32 0, i32 1
  %tmp99 = load ptr, ptr %tmp98, align 8
  call void @printString(ptr %tmp99)
  %tmp103 = call ptr @arraylist_get_ptr(ptr %tmp0, i64 1)
  %tmp106 = load ptr, ptr %tmp103, align 8
  call void @printString(ptr %tmp106)
  %tmp110 = call ptr @arraylist_get_ptr(ptr %tmp0, i64 1)
  %tmp112 = getelementptr inbounds %Pessoa, ptr %tmp110, i32 0, i32 2
  %tmp113 = load ptr, ptr %tmp112, align 8
  %tmp115 = load ptr, ptr %tmp113, align 8
  call void @printString(ptr %tmp115)
  %tmp119 = call ptr @arraylist_get_ptr(ptr %tmp0, i64 1)
  %tmp121 = getelementptr inbounds %Pessoa, ptr %tmp119, i32 0, i32 2
  %tmp122 = load ptr, ptr %tmp121, align 8
  %tmp123 = getelementptr inbounds %Endereco, ptr %tmp122, i32 0, i32 1
  %tmp124 = load ptr, ptr %tmp123, align 8
  call void @printString(ptr %tmp124)
  %tmp128 = call ptr @arraylist_get_ptr(ptr %tmp0, i64 0)
  %tmp130 = getelementptr inbounds %Pessoa, ptr %tmp128, i32 0, i32 2
  %tmp131 = load ptr, ptr %tmp130, align 8
  %tmp132 = getelementptr inbounds %Endereco, ptr %tmp131, i32 0, i32 1
  %tmp133 = load ptr, ptr %tmp132, align 8
  %tmp135 = call ptr @createString(ptr @.str2)
  %tmp137 = call i1 @strcmp_eq(ptr %tmp133, ptr %tmp135)
  br i1 %tmp137, label %then_0, label %endif_0

then_0:                                           ; preds = %0
  %1 = call i32 (ptr, ...) @printf(ptr @.strStr, ptr @.str6)
  br label %endif_0

endif_0:                                          ; preds = %then_0, %0
  %tmp142 = call ptr @arraylist_get_ptr(ptr %tmp0, i64 1)
  %tmp144 = getelementptr inbounds %Pessoa, ptr %tmp142, i32 0, i32 1
  %tmp145 = load i32, ptr %tmp144, align 4
  %tmp147 = icmp slt i32 %tmp145, 18
  br i1 %tmp147, label %then_1, label %endif_1

then_1:                                           ; preds = %endif_0
  %tmp151 = call ptr @arraylist_get_ptr(ptr %tmp0, i64 1)
  %tmp154 = load ptr, ptr %tmp151, align 8
  call void @printString(ptr %tmp154)
  %2 = call i32 (ptr, ...) @printf(ptr @.strStr, ptr @.str7)
  br label %endif_1

endif_1:                                          ; preds = %then_1, %endif_0
  %i = alloca i32, align 4
  store i32 0, ptr %i, align 4
  %tmp1571 = load i32, ptr %i, align 4
  %tmp1602 = call i32 @length(ptr %tmp0)
  %tmp1613 = icmp slt i32 %tmp1571, %tmp1602
  br i1 %tmp1613, label %while_body_1.lr.ph, label %while_end_2

while_body_1.lr.ph:                               ; preds = %endif_1
  br label %while_body_1

while_body_1:                                     ; preds = %while_body_1.lr.ph, %while_body_1
  %3 = call i32 (ptr, ...) @printf(ptr @.strStr, ptr @.str8)
  %tmp164 = load i32, ptr %i, align 4
  %tmp165 = zext i32 %tmp164 to i64
  %tmp166 = call ptr @arraylist_get_ptr(ptr %tmp0, i64 %tmp165)
  %tmp169 = load ptr, ptr %tmp166, align 8
  call void @printString(ptr %tmp169)
  %tmp171 = load i32, ptr %i, align 4
  %tmp172 = zext i32 %tmp171 to i64
  %tmp173 = call ptr @arraylist_get_ptr(ptr %tmp0, i64 %tmp172)
  %tmp175 = getelementptr inbounds %Pessoa, ptr %tmp173, i32 0, i32 2
  %tmp176 = load ptr, ptr %tmp175, align 8
  %tmp178 = load ptr, ptr %tmp176, align 8
  call void @printString(ptr %tmp178)
  %tmp180 = load i32, ptr %i, align 4
  %tmp181 = zext i32 %tmp180 to i64
  %tmp182 = call ptr @arraylist_get_ptr(ptr %tmp0, i64 %tmp181)
  %tmp184 = getelementptr inbounds %Pessoa, ptr %tmp182, i32 0, i32 2
  %tmp185 = load ptr, ptr %tmp184, align 8
  %tmp186 = getelementptr inbounds %Endereco, ptr %tmp185, i32 0, i32 1
  %tmp187 = load ptr, ptr %tmp186, align 8
  call void @printString(ptr %tmp187)
  %tmp188 = load i32, ptr %i, align 4
  %tmp190 = add i32 %tmp188, 1
  store i32 %tmp190, ptr %i, align 4
  %tmp157 = load i32, ptr %i, align 4
  %tmp160 = call i32 @length(ptr %tmp0)
  %tmp161 = icmp slt i32 %tmp157, %tmp160
  br i1 %tmp161, label %while_body_1, label %while_cond_0.while_end_2_crit_edge

while_cond_0.while_end_2_crit_edge:               ; preds = %while_body_1
  br label %while_end_2

while_end_2:                                      ; preds = %while_cond_0.while_end_2_crit_edge, %endif_1
  call void @freeList(ptr %tmp0)
  %4 = call i32 @getchar()
  ret i32 0
}
