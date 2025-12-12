; ModuleID = 'programa.ll'
source_filename = "programa.ll"

%Endereco = type { ptr, ptr, ptr }
%Pessoa = type { ptr, i32, ptr }

@.strChar = private constant [3 x i8] c"%c\00"
@.strTrue = private constant [6 x i8] c"true\0A\00"
@.strFalse = private constant [7 x i8] c"false\0A\00"
@.strInt = private constant [4 x i8] c"%d\0A\00"
@.strDouble = private constant [4 x i8] c"%f\0A\00"
@.strFloat = private constant [4 x i8] c"%f\0A\00"
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
  ret void
}

define i32 @main() {
  %tmp0 = call ptr @arraylist_create(i64 4)
  %tmp3 = call ptr @malloc(i64 8)
  %tmp5 = call ptr @createString(ptr null)
  store ptr %tmp5, ptr %tmp3, align 8
  %tmp10 = call ptr @createString(ptr @.str0)
  store ptr %tmp10, ptr %tmp3, align 8
  %tmp12 = call ptr @malloc(i64 8)
  %tmp14 = call ptr @createString(ptr null)
  store ptr %tmp14, ptr %tmp12, align 8
  %tmp19 = call ptr @createString(ptr @.str1)
  store ptr %tmp19, ptr %tmp12, align 8
  %tmp23 = call ptr @malloc(i64 24)
  %tmp28 = call ptr @createString(ptr @.str2)
  store ptr %tmp28, ptr %tmp23, align 8
  %tmp31 = getelementptr inbounds %Pessoa, ptr %tmp23, i32 0, i32 1
  store i32 25, ptr %tmp31, align 4
  %tmp34 = call ptr @malloc(i64 24)
  %tmp36 = call ptr @createString(ptr null)
  store ptr %tmp36, ptr %tmp34, align 8
  %tmp38 = call ptr @createString(ptr null)
  %tmp39 = getelementptr inbounds %Endereco, ptr %tmp34, i32 0, i32 1
  store ptr %tmp38, ptr %tmp39, align 8
  %tmp40 = getelementptr inbounds %Endereco, ptr %tmp34, i32 0, i32 2
  store ptr null, ptr %tmp40, align 8
  %tmp44 = call ptr @createString(ptr @.str3)
  store ptr %tmp44, ptr %tmp34, align 8
  %tmp49 = call ptr @createString(ptr @.str4)
  store ptr %tmp49, ptr %tmp39, align 8
  store ptr %tmp3, ptr %tmp40, align 8
  %tmp56 = getelementptr inbounds %Pessoa, ptr %tmp23, i32 0, i32 2
  store ptr %tmp34, ptr %tmp56, align 8
  %tmp61 = call ptr @malloc(i64 24)
  %tmp66 = call ptr @createString(ptr @.str5)
  store ptr %tmp66, ptr %tmp61, align 8
  %tmp69 = getelementptr inbounds %Pessoa, ptr %tmp61, i32 0, i32 1
  store i32 17, ptr %tmp69, align 4
  %tmp72 = call ptr @malloc(i64 24)
  %tmp74 = call ptr @createString(ptr null)
  store ptr %tmp74, ptr %tmp72, align 8
  %tmp76 = call ptr @createString(ptr null)
  %tmp77 = getelementptr inbounds %Endereco, ptr %tmp72, i32 0, i32 1
  store ptr %tmp76, ptr %tmp77, align 8
  %tmp78 = getelementptr inbounds %Endereco, ptr %tmp72, i32 0, i32 2
  store ptr null, ptr %tmp78, align 8
  %tmp82 = call ptr @createString(ptr @.str6)
  store ptr %tmp82, ptr %tmp72, align 8
  %tmp87 = call ptr @createString(ptr @.str7)
  store ptr %tmp87, ptr %tmp77, align 8
  store ptr %tmp3, ptr %tmp78, align 8
  %tmp94 = getelementptr inbounds %Pessoa, ptr %tmp61, i32 0, i32 2
  store ptr %tmp72, ptr %tmp94, align 8
  %tmp99 = call ptr @malloc(i64 24)
  %tmp104 = call ptr @createString(ptr @.str8)
  store ptr %tmp104, ptr %tmp99, align 8
  %tmp107 = getelementptr inbounds %Pessoa, ptr %tmp99, i32 0, i32 1
  store i32 32, ptr %tmp107, align 4
  %tmp110 = call ptr @malloc(i64 24)
  %tmp112 = call ptr @createString(ptr null)
  store ptr %tmp112, ptr %tmp110, align 8
  %tmp114 = call ptr @createString(ptr null)
  %tmp115 = getelementptr inbounds %Endereco, ptr %tmp110, i32 0, i32 1
  store ptr %tmp114, ptr %tmp115, align 8
  %tmp116 = getelementptr inbounds %Endereco, ptr %tmp110, i32 0, i32 2
  store ptr null, ptr %tmp116, align 8
  %tmp120 = call ptr @createString(ptr @.str9)
  store ptr %tmp120, ptr %tmp110, align 8
  %tmp125 = call ptr @createString(ptr @.str10)
  store ptr %tmp125, ptr %tmp115, align 8
  store ptr %tmp12, ptr %tmp116, align 8
  %tmp132 = getelementptr inbounds %Pessoa, ptr %tmp99, i32 0, i32 2
  store ptr %tmp110, ptr %tmp132, align 8
  call void @arraylist_add_ptr(ptr %tmp0, ptr %tmp23)
  call void @arraylist_add_ptr(ptr %tmp0, ptr %tmp61)
  call void @arraylist_add_ptr(ptr %tmp0, ptr %tmp99)
  %1 = call i32 (ptr, ...) @printf(ptr @.strStr, ptr @.str11)
  %tmp1521 = call i32 @length(ptr %tmp0)
  %tmp1532 = icmp slt i32 0, %tmp1521
  br i1 %tmp1532, label %while_body_1.lr.ph, label %while_end_2

while_body_1.lr.ph:                               ; preds = %0
  br label %while_body_1

while_body_1:                                     ; preds = %while_body_1.lr.ph, %endif_0
  %i.03 = phi i32 [ 0, %while_body_1.lr.ph ], [ %tmp187, %endif_0 ]
  %tmp156 = zext i32 %i.03 to i64
  %tmp157 = call ptr @arraylist_get_ptr(ptr %tmp0, i64 %tmp156)
  %tmp159 = getelementptr inbounds %Pessoa, ptr %tmp157, i32 0, i32 2
  %tmp160 = load ptr, ptr %tmp159, align 8
  %tmp161 = getelementptr inbounds %Endereco, ptr %tmp160, i32 0, i32 2
  %tmp162 = load ptr, ptr %tmp161, align 8
  %tmp164 = load ptr, ptr %tmp162, align 8
  %tmp166 = call ptr @createString(ptr @.str0)
  %tmp168 = call i1 @strcmp_eq(ptr %tmp164, ptr %tmp166)
  br i1 %tmp168, label %then_0, label %endif_0

then_0:                                           ; preds = %while_body_1
  %tmp172 = call ptr @arraylist_get_ptr(ptr %tmp0, i64 %tmp156)
  %tmp175 = load ptr, ptr %tmp172, align 8
  call void @printString(ptr %tmp175)
  %tmp179 = call ptr @arraylist_get_ptr(ptr %tmp0, i64 %tmp156)
  %tmp181 = getelementptr inbounds %Pessoa, ptr %tmp179, i32 0, i32 2
  %tmp182 = load ptr, ptr %tmp181, align 8
  %tmp183 = getelementptr inbounds %Endereco, ptr %tmp182, i32 0, i32 1
  %tmp184 = load ptr, ptr %tmp183, align 8
  call void @printString(ptr %tmp184)
  br label %endif_0

endif_0:                                          ; preds = %then_0, %while_body_1
  %tmp187 = add i32 %i.03, 1
  %tmp152 = call i32 @length(ptr %tmp0)
  %tmp153 = icmp slt i32 %tmp187, %tmp152
  br i1 %tmp153, label %while_body_1, label %while_cond_0.while_end_2_crit_edge

while_cond_0.while_end_2_crit_edge:               ; preds = %endif_0
  br label %while_end_2

while_end_2:                                      ; preds = %while_cond_0.while_end_2_crit_edge, %0
  %soma = alloca i32, align 4
  store i32 0, ptr %soma, align 4
  %tmp1924 = call i32 @length(ptr %tmp0)
  %tmp1935 = icmp slt i32 0, %tmp1924
  %tmp2066 = load i32, ptr %soma, align 4
  br i1 %tmp1935, label %while_body_4.lr.ph, label %while_end_5

while_body_4.lr.ph:                               ; preds = %while_end_2
  br label %while_body_4

while_body_4:                                     ; preds = %while_body_4.lr.ph, %while_body_4
  %tmp2068 = phi i32 [ %tmp2066, %while_body_4.lr.ph ], [ %tmp206, %while_body_4 ]
  %i.17 = phi i32 [ 0, %while_body_4.lr.ph ], [ %tmp205, %while_body_4 ]
  %tmp197 = zext i32 %i.17 to i64
  %tmp198 = call ptr @arraylist_get_ptr(ptr %tmp0, i64 %tmp197)
  %tmp200 = getelementptr inbounds %Pessoa, ptr %tmp198, i32 0, i32 1
  %tmp201 = load i32, ptr %tmp200, align 4
  %tmp202 = add i32 %tmp201, %tmp2068
  store i32 %tmp202, ptr %soma, align 4
  %tmp205 = add i32 %i.17, 1
  %tmp192 = call i32 @length(ptr %tmp0)
  %tmp193 = icmp slt i32 %tmp205, %tmp192
  %tmp206 = load i32, ptr %soma, align 4
  br i1 %tmp193, label %while_body_4, label %while_cond_3.while_end_5_crit_edge

while_cond_3.while_end_5_crit_edge:               ; preds = %while_body_4
  %split = phi i32 [ %tmp206, %while_body_4 ]
  br label %while_end_5

while_end_5:                                      ; preds = %while_cond_3.while_end_5_crit_edge, %while_end_2
  %tmp206.lcssa = phi i32 [ %split, %while_cond_3.while_end_5_crit_edge ], [ %tmp2066, %while_end_2 ]
  %media = alloca i32, align 4
  %tmp209 = call i32 @length(ptr %tmp0)
  %tmp210 = sdiv i32 %tmp206.lcssa, %tmp209
  store i32 %tmp210, ptr %media, align 4
  %2 = call i32 (ptr, ...) @printf(ptr @.strStr, ptr @.str12)
  %tmp212 = load i32, ptr %media, align 4
  %3 = call i32 (ptr, ...) @printf(ptr @.strInt, i32 %tmp212)
  %tmp216 = call ptr @arraylist_get_ptr(ptr %tmp0, i64 2)
  %tmp218 = getelementptr inbounds %Pessoa, ptr %tmp216, i32 0, i32 2
  %tmp219 = load ptr, ptr %tmp218, align 8
  %tmp220 = getelementptr inbounds %Endereco, ptr %tmp219, i32 0, i32 2
  %tmp221 = load ptr, ptr %tmp220, align 8
  %tmp223 = load ptr, ptr %tmp221, align 8
  %tmp225 = call ptr @createString(ptr @.str1)
  %tmp227 = call i1 @strcmp_eq(ptr %tmp223, ptr %tmp225)
  br i1 %tmp227, label %and.rhs_6, label %and.short_8

and.rhs_6:                                        ; preds = %while_end_5
  %tmp232 = call ptr @arraylist_get_ptr(ptr %tmp0, i64 2)
  %tmp234 = getelementptr inbounds %Pessoa, ptr %tmp232, i32 0, i32 1
  %tmp235 = load i32, ptr %tmp234, align 4
  %tmp237 = icmp sgt i32 %tmp235, 30
  br label %and.end_7

and.short_8:                                      ; preds = %while_end_5
  br label %and.end_7

and.end_7:                                        ; preds = %and.short_8, %and.rhs_6
  %tmp228 = phi i1 [ %tmp237, %and.rhs_6 ], [ false, %and.short_8 ]
  br i1 %tmp228, label %then_1, label %endif_1

then_1:                                           ; preds = %and.end_7
  %tmp242 = call ptr @arraylist_get_ptr(ptr %tmp0, i64 2)
  %tmp245 = load ptr, ptr %tmp242, align 8
  call void @printString(ptr %tmp245)
  %4 = call i32 (ptr, ...) @printf(ptr @.strStr, ptr @.str13)
  br label %endif_1

endif_1:                                          ; preds = %then_1, %and.end_7
  call void @freeList(ptr %tmp0)
  %5 = call i32 @getchar()
  ret i32 0
}
