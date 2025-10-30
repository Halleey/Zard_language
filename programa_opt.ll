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
@.str1 = private constant [10 x i8] c"Argentina\00"
@.str2 = private constant [6 x i8] c"Alice\00"
@.str3 = private constant [6 x i8] c"Rua A\00"
@.str4 = private constant [11 x i8] c"S\C3\A3o Paulo\00"
@.str5 = private constant [4 x i8] c"Bob\00"
@.str6 = private constant [12 x i8] c"Av. Central\00"
@.str7 = private constant [15 x i8] c"Rio de Janeiro\00"
@.str8 = private constant [7 x i8] c"Carlos\00"
@.str9 = private constant [8 x i8] c"Calle 9\00"
@.str10 = private constant [13 x i8] c"Buenos Aires\00"
@.str11 = private constant [36 x i8] c"=== Pessoas que moram no Brasil ===\00"
@.str12 = private constant [14 x i8] c"Idade m\C3\A9dia:\00"
@.str13 = private constant [36 x i8] c" \C3\A9 argentino e tem mais de 30 anos\00"

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
  %tmp0 = call ptr @arraylist_create(i64 4)
  %tmp3 = alloca %Pais, align 8
  %tmp4 = call ptr @createString(ptr null)
  store ptr %tmp4, ptr %tmp3, align 8
  %tmp9 = call ptr @createString(ptr @.str0)
  store ptr %tmp9, ptr %tmp3, align 8
  %tmp11 = alloca %Pais, align 8
  %tmp12 = call ptr @createString(ptr null)
  store ptr %tmp12, ptr %tmp11, align 8
  %tmp17 = call ptr @createString(ptr @.str1)
  store ptr %tmp17, ptr %tmp11, align 8
  %tmp19 = alloca %Pessoa, align 8
  %tmp20 = call ptr @createString(ptr null)
  store ptr %tmp20, ptr %tmp19, align 8
  %tmp22 = getelementptr inbounds %Pessoa, ptr %tmp19, i32 0, i32 1
  store i32 0, ptr %tmp22, align 4
  %tmp23 = getelementptr inbounds %Pessoa, ptr %tmp19, i32 0, i32 2
  store ptr null, ptr %tmp23, align 8
  %tmp27 = call ptr @createString(ptr @.str2)
  store ptr %tmp27, ptr %tmp19, align 8
  store i32 25, ptr %tmp22, align 4
  %tmp33 = alloca %Endereco, align 8
  %tmp34 = call ptr @createString(ptr null)
  store ptr %tmp34, ptr %tmp33, align 8
  %tmp36 = call ptr @createString(ptr null)
  %tmp37 = getelementptr inbounds %Endereco, ptr %tmp33, i32 0, i32 1
  store ptr %tmp36, ptr %tmp37, align 8
  %tmp38 = getelementptr inbounds %Endereco, ptr %tmp33, i32 0, i32 2
  store ptr null, ptr %tmp38, align 8
  %tmp42 = call ptr @createString(ptr @.str3)
  store ptr %tmp42, ptr %tmp33, align 8
  %tmp47 = call ptr @createString(ptr @.str4)
  store ptr %tmp47, ptr %tmp37, align 8
  store ptr %tmp3, ptr %tmp38, align 8
  store ptr %tmp33, ptr %tmp23, align 8
  %tmp57 = alloca %Pessoa, align 8
  %tmp58 = call ptr @createString(ptr null)
  store ptr %tmp58, ptr %tmp57, align 8
  %tmp60 = getelementptr inbounds %Pessoa, ptr %tmp57, i32 0, i32 1
  store i32 0, ptr %tmp60, align 4
  %tmp61 = getelementptr inbounds %Pessoa, ptr %tmp57, i32 0, i32 2
  store ptr null, ptr %tmp61, align 8
  %tmp65 = call ptr @createString(ptr @.str5)
  store ptr %tmp65, ptr %tmp57, align 8
  store i32 17, ptr %tmp60, align 4
  %tmp71 = alloca %Endereco, align 8
  %tmp72 = call ptr @createString(ptr null)
  store ptr %tmp72, ptr %tmp71, align 8
  %tmp74 = call ptr @createString(ptr null)
  %tmp75 = getelementptr inbounds %Endereco, ptr %tmp71, i32 0, i32 1
  store ptr %tmp74, ptr %tmp75, align 8
  %tmp76 = getelementptr inbounds %Endereco, ptr %tmp71, i32 0, i32 2
  store ptr null, ptr %tmp76, align 8
  %tmp80 = call ptr @createString(ptr @.str6)
  store ptr %tmp80, ptr %tmp71, align 8
  %tmp85 = call ptr @createString(ptr @.str7)
  store ptr %tmp85, ptr %tmp75, align 8
  store ptr %tmp3, ptr %tmp76, align 8
  store ptr %tmp71, ptr %tmp61, align 8
  %tmp95 = alloca %Pessoa, align 8
  %tmp96 = call ptr @createString(ptr null)
  store ptr %tmp96, ptr %tmp95, align 8
  %tmp98 = getelementptr inbounds %Pessoa, ptr %tmp95, i32 0, i32 1
  store i32 0, ptr %tmp98, align 4
  %tmp99 = getelementptr inbounds %Pessoa, ptr %tmp95, i32 0, i32 2
  store ptr null, ptr %tmp99, align 8
  %tmp103 = call ptr @createString(ptr @.str8)
  store ptr %tmp103, ptr %tmp95, align 8
  store i32 32, ptr %tmp98, align 4
  %tmp109 = alloca %Endereco, align 8
  %tmp110 = call ptr @createString(ptr null)
  store ptr %tmp110, ptr %tmp109, align 8
  %tmp112 = call ptr @createString(ptr null)
  %tmp113 = getelementptr inbounds %Endereco, ptr %tmp109, i32 0, i32 1
  store ptr %tmp112, ptr %tmp113, align 8
  %tmp114 = getelementptr inbounds %Endereco, ptr %tmp109, i32 0, i32 2
  store ptr null, ptr %tmp114, align 8
  %tmp118 = call ptr @createString(ptr @.str9)
  store ptr %tmp118, ptr %tmp109, align 8
  %tmp123 = call ptr @createString(ptr @.str10)
  store ptr %tmp123, ptr %tmp113, align 8
  store ptr %tmp11, ptr %tmp114, align 8
  store ptr %tmp109, ptr %tmp99, align 8
  call void @arraylist_add_ptr(ptr %tmp0, ptr %tmp19)
  call void @arraylist_add_ptr(ptr %tmp0, ptr %tmp57)
  call void @arraylist_add_ptr(ptr %tmp0, ptr %tmp95)
  %1 = call i32 (ptr, ...) @printf(ptr @.strStr, ptr @.str11)
  %tmp1471 = call i32 @length(ptr %tmp0)
  %tmp1482 = icmp slt i32 0, %tmp1471
  br i1 %tmp1482, label %while_body_1.lr.ph, label %while_end_2

while_body_1.lr.ph:                               ; preds = %0
  br label %while_body_1

while_body_1:                                     ; preds = %while_body_1.lr.ph, %endif_0
  %i.03 = phi i32 [ 0, %while_body_1.lr.ph ], [ %tmp182, %endif_0 ]
  %tmp151 = zext i32 %i.03 to i64
  %tmp152 = call ptr @arraylist_get_ptr(ptr %tmp0, i64 %tmp151)
  %tmp154 = getelementptr inbounds %Pessoa, ptr %tmp152, i32 0, i32 2
  %tmp155 = load ptr, ptr %tmp154, align 8
  %tmp156 = getelementptr inbounds %Endereco, ptr %tmp155, i32 0, i32 2
  %tmp157 = load ptr, ptr %tmp156, align 8
  %tmp159 = load ptr, ptr %tmp157, align 8
  %tmp161 = call ptr @createString(ptr @.str0)
  %tmp163 = call i1 @strcmp_eq(ptr %tmp159, ptr %tmp161)
  br i1 %tmp163, label %then_0, label %endif_0

then_0:                                           ; preds = %while_body_1
  %tmp167 = call ptr @arraylist_get_ptr(ptr %tmp0, i64 %tmp151)
  %tmp170 = load ptr, ptr %tmp167, align 8
  call void @printString(ptr %tmp170)
  %tmp174 = call ptr @arraylist_get_ptr(ptr %tmp0, i64 %tmp151)
  %tmp176 = getelementptr inbounds %Pessoa, ptr %tmp174, i32 0, i32 2
  %tmp177 = load ptr, ptr %tmp176, align 8
  %tmp178 = getelementptr inbounds %Endereco, ptr %tmp177, i32 0, i32 1
  %tmp179 = load ptr, ptr %tmp178, align 8
  call void @printString(ptr %tmp179)
  br label %endif_0

endif_0:                                          ; preds = %then_0, %while_body_1
  %tmp182 = add i32 %i.03, 1
  %tmp147 = call i32 @length(ptr %tmp0)
  %tmp148 = icmp slt i32 %tmp182, %tmp147
  br i1 %tmp148, label %while_body_1, label %while_cond_0.while_end_2_crit_edge

while_cond_0.while_end_2_crit_edge:               ; preds = %endif_0
  br label %while_end_2

while_end_2:                                      ; preds = %while_cond_0.while_end_2_crit_edge, %0
  %soma = alloca i32, align 4
  store i32 0, ptr %soma, align 4
  %tmp1874 = call i32 @length(ptr %tmp0)
  %tmp1885 = icmp slt i32 0, %tmp1874
  %tmp2016 = load i32, ptr %soma, align 4
  br i1 %tmp1885, label %while_body_4.lr.ph, label %while_end_5

while_body_4.lr.ph:                               ; preds = %while_end_2
  br label %while_body_4

while_body_4:                                     ; preds = %while_body_4.lr.ph, %while_body_4
  %tmp2018 = phi i32 [ %tmp2016, %while_body_4.lr.ph ], [ %tmp201, %while_body_4 ]
  %i.17 = phi i32 [ 0, %while_body_4.lr.ph ], [ %tmp200, %while_body_4 ]
  %tmp192 = zext i32 %i.17 to i64
  %tmp193 = call ptr @arraylist_get_ptr(ptr %tmp0, i64 %tmp192)
  %tmp195 = getelementptr inbounds %Pessoa, ptr %tmp193, i32 0, i32 1
  %tmp196 = load i32, ptr %tmp195, align 4
  %tmp197 = add i32 %tmp196, %tmp2018
  store i32 %tmp197, ptr %soma, align 4
  %tmp200 = add i32 %i.17, 1
  %tmp187 = call i32 @length(ptr %tmp0)
  %tmp188 = icmp slt i32 %tmp200, %tmp187
  %tmp201 = load i32, ptr %soma, align 4
  br i1 %tmp188, label %while_body_4, label %while_cond_3.while_end_5_crit_edge

while_cond_3.while_end_5_crit_edge:               ; preds = %while_body_4
  %split = phi i32 [ %tmp201, %while_body_4 ]
  br label %while_end_5

while_end_5:                                      ; preds = %while_cond_3.while_end_5_crit_edge, %while_end_2
  %tmp201.lcssa = phi i32 [ %split, %while_cond_3.while_end_5_crit_edge ], [ %tmp2016, %while_end_2 ]
  %media = alloca i32, align 4
  %tmp204 = call i32 @length(ptr %tmp0)
  %tmp205 = sdiv i32 %tmp201.lcssa, %tmp204
  store i32 %tmp205, ptr %media, align 4
  %2 = call i32 (ptr, ...) @printf(ptr @.strStr, ptr @.str12)
  %tmp207 = load i32, ptr %media, align 4
  %3 = call i32 (ptr, ...) @printf(ptr @.strInt, i32 %tmp207)
  %tmp211 = call ptr @arraylist_get_ptr(ptr %tmp0, i64 2)
  %tmp213 = getelementptr inbounds %Pessoa, ptr %tmp211, i32 0, i32 2
  %tmp214 = load ptr, ptr %tmp213, align 8
  %tmp215 = getelementptr inbounds %Endereco, ptr %tmp214, i32 0, i32 2
  %tmp216 = load ptr, ptr %tmp215, align 8
  %tmp218 = load ptr, ptr %tmp216, align 8
  %tmp220 = call ptr @createString(ptr @.str1)
  %tmp222 = call i1 @strcmp_eq(ptr %tmp218, ptr %tmp220)
  br i1 %tmp222, label %and.rhs_6, label %and.short_8

and.rhs_6:                                        ; preds = %while_end_5
  %tmp227 = call ptr @arraylist_get_ptr(ptr %tmp0, i64 2)
  %tmp229 = getelementptr inbounds %Pessoa, ptr %tmp227, i32 0, i32 1
  %tmp230 = load i32, ptr %tmp229, align 4
  %tmp232 = icmp sgt i32 %tmp230, 30
  br label %and.end_7

and.short_8:                                      ; preds = %while_end_5
  br label %and.end_7

and.end_7:                                        ; preds = %and.short_8, %and.rhs_6
  %tmp223 = phi i1 [ %tmp232, %and.rhs_6 ], [ false, %and.short_8 ]
  br i1 %tmp223, label %then_1, label %endif_1

then_1:                                           ; preds = %and.end_7
  %tmp237 = call ptr @arraylist_get_ptr(ptr %tmp0, i64 2)
  %tmp240 = load ptr, ptr %tmp237, align 8
  call void @printString(ptr %tmp240)
  %4 = call i32 (ptr, ...) @printf(ptr @.strStr, ptr @.str13)
  br label %endif_1

endif_1:                                          ; preds = %then_1, %and.end_7
  call void @freeList(ptr %tmp0)
  %5 = call i32 @getchar()
  ret i32 0
}
