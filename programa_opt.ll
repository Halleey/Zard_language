; ModuleID = 'programa.ll'
source_filename = "programa.ll"

%Endereco = type { ptr, ptr, ptr }
%Pessoa = type { ptr, i32, ptr }
%Pais = type { ptr }

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
  %t0 = call ptr @arraylist_create(i64 4)
  %t2 = alloca %Pais, align 8
  %t3 = call ptr @createString(ptr null)
  store ptr %t3, ptr %t2, align 8
  %t8 = call ptr @createString(ptr @.str0)
  store ptr %t8, ptr %t2, align 8
  %t10 = alloca %Pais, align 8
  %t11 = call ptr @createString(ptr null)
  store ptr %t11, ptr %t10, align 8
  %t16 = call ptr @createString(ptr @.str1)
  store ptr %t16, ptr %t10, align 8
  %t18 = alloca %Pessoa, align 8
  %t19 = call ptr @createString(ptr null)
  store ptr %t19, ptr %t18, align 8
  %t21 = getelementptr inbounds %Pessoa, ptr %t18, i32 0, i32 1
  store i32 0, ptr %t21, align 4
  %t22 = getelementptr inbounds %Pessoa, ptr %t18, i32 0, i32 2
  store ptr null, ptr %t22, align 8
  %t26 = call ptr @createString(ptr @.str2)
  store ptr %t26, ptr %t18, align 8
  store i32 25, ptr %t21, align 4
  %t32 = alloca %Endereco, align 8
  %t33 = call ptr @createString(ptr null)
  store ptr %t33, ptr %t32, align 8
  %t35 = call ptr @createString(ptr null)
  %t36 = getelementptr inbounds %Endereco, ptr %t32, i32 0, i32 1
  store ptr %t35, ptr %t36, align 8
  %t37 = getelementptr inbounds %Endereco, ptr %t32, i32 0, i32 2
  store ptr null, ptr %t37, align 8
  %t41 = call ptr @createString(ptr @.str3)
  store ptr %t41, ptr %t32, align 8
  %t46 = call ptr @createString(ptr @.str4)
  store ptr %t46, ptr %t36, align 8
  store ptr %t2, ptr %t37, align 8
  store ptr %t32, ptr %t22, align 8
  %t56 = alloca %Pessoa, align 8
  %t57 = call ptr @createString(ptr null)
  store ptr %t57, ptr %t56, align 8
  %t59 = getelementptr inbounds %Pessoa, ptr %t56, i32 0, i32 1
  store i32 0, ptr %t59, align 4
  %t60 = getelementptr inbounds %Pessoa, ptr %t56, i32 0, i32 2
  store ptr null, ptr %t60, align 8
  %t64 = call ptr @createString(ptr @.str5)
  store ptr %t64, ptr %t56, align 8
  store i32 17, ptr %t59, align 4
  %t70 = alloca %Endereco, align 8
  %t71 = call ptr @createString(ptr null)
  store ptr %t71, ptr %t70, align 8
  %t73 = call ptr @createString(ptr null)
  %t74 = getelementptr inbounds %Endereco, ptr %t70, i32 0, i32 1
  store ptr %t73, ptr %t74, align 8
  %t75 = getelementptr inbounds %Endereco, ptr %t70, i32 0, i32 2
  store ptr null, ptr %t75, align 8
  %t79 = call ptr @createString(ptr @.str6)
  store ptr %t79, ptr %t70, align 8
  %t84 = call ptr @createString(ptr @.str7)
  store ptr %t84, ptr %t74, align 8
  store ptr %t2, ptr %t75, align 8
  store ptr %t70, ptr %t60, align 8
  %t94 = alloca %Pessoa, align 8
  %t95 = call ptr @createString(ptr null)
  store ptr %t95, ptr %t94, align 8
  %t97 = getelementptr inbounds %Pessoa, ptr %t94, i32 0, i32 1
  store i32 0, ptr %t97, align 4
  %t98 = getelementptr inbounds %Pessoa, ptr %t94, i32 0, i32 2
  store ptr null, ptr %t98, align 8
  %t102 = call ptr @createString(ptr @.str8)
  store ptr %t102, ptr %t94, align 8
  store i32 32, ptr %t97, align 4
  %t108 = alloca %Endereco, align 8
  %t109 = call ptr @createString(ptr null)
  store ptr %t109, ptr %t108, align 8
  %t111 = call ptr @createString(ptr null)
  %t112 = getelementptr inbounds %Endereco, ptr %t108, i32 0, i32 1
  store ptr %t111, ptr %t112, align 8
  %t113 = getelementptr inbounds %Endereco, ptr %t108, i32 0, i32 2
  store ptr null, ptr %t113, align 8
  %t117 = call ptr @createString(ptr @.str9)
  store ptr %t117, ptr %t108, align 8
  %t122 = call ptr @createString(ptr @.str10)
  store ptr %t122, ptr %t112, align 8
  store ptr %t10, ptr %t113, align 8
  store ptr %t108, ptr %t98, align 8
  call void @arraylist_add_ptr(ptr %t0, ptr %t18)
  call void @arraylist_add_ptr(ptr %t0, ptr %t56)
  call void @arraylist_add_ptr(ptr %t0, ptr %t94)
  %1 = call i32 (ptr, ...) @printf(ptr @.strStr, ptr @.str11)
  %t1461 = call i32 @length(ptr %t0)
  %t1472 = icmp slt i32 0, %t1461
  br i1 %t1472, label %while_body_1.lr.ph, label %while_end_2

while_body_1.lr.ph:                               ; preds = %0
  br label %while_body_1

while_body_1:                                     ; preds = %while_body_1.lr.ph, %endif_0
  %i.03 = phi i32 [ 0, %while_body_1.lr.ph ], [ %t181, %endif_0 ]
  %t150 = zext i32 %i.03 to i64
  %t151 = call ptr @arraylist_get_ptr(ptr %t0, i64 %t150)
  %t153 = getelementptr inbounds %Pessoa, ptr %t151, i32 0, i32 2
  %t154 = load ptr, ptr %t153, align 8
  %t155 = getelementptr inbounds %Endereco, ptr %t154, i32 0, i32 2
  %t156 = load ptr, ptr %t155, align 8
  %t158 = load ptr, ptr %t156, align 8
  %t160 = call ptr @createString(ptr @.str0)
  %t162 = call i1 @strcmp_eq(ptr %t158, ptr %t160)
  br i1 %t162, label %then_0, label %endif_0

then_0:                                           ; preds = %while_body_1
  %t166 = call ptr @arraylist_get_ptr(ptr %t0, i64 %t150)
  %t169 = load ptr, ptr %t166, align 8
  call void @printString(ptr %t169)
  %t173 = call ptr @arraylist_get_ptr(ptr %t0, i64 %t150)
  %t175 = getelementptr inbounds %Pessoa, ptr %t173, i32 0, i32 2
  %t176 = load ptr, ptr %t175, align 8
  %t177 = getelementptr inbounds %Endereco, ptr %t176, i32 0, i32 1
  %t178 = load ptr, ptr %t177, align 8
  call void @printString(ptr %t178)
  br label %endif_0

endif_0:                                          ; preds = %then_0, %while_body_1
  %t181 = add i32 %i.03, 1
  %t146 = call i32 @length(ptr %t0)
  %t147 = icmp slt i32 %t181, %t146
  br i1 %t147, label %while_body_1, label %while_cond_0.while_end_2_crit_edge

while_cond_0.while_end_2_crit_edge:               ; preds = %endif_0
  br label %while_end_2

while_end_2:                                      ; preds = %while_cond_0.while_end_2_crit_edge, %0
  %soma = alloca i32, align 4
  store i32 0, ptr %soma, align 4
  %t1864 = call i32 @length(ptr %t0)
  %t1875 = icmp slt i32 0, %t1864
  %t2006 = load i32, ptr %soma, align 4
  br i1 %t1875, label %while_body_4.lr.ph, label %while_end_5

while_body_4.lr.ph:                               ; preds = %while_end_2
  br label %while_body_4

while_body_4:                                     ; preds = %while_body_4.lr.ph, %while_body_4
  %t2008 = phi i32 [ %t2006, %while_body_4.lr.ph ], [ %t200, %while_body_4 ]
  %i.17 = phi i32 [ 0, %while_body_4.lr.ph ], [ %t199, %while_body_4 ]
  %t191 = zext i32 %i.17 to i64
  %t192 = call ptr @arraylist_get_ptr(ptr %t0, i64 %t191)
  %t194 = getelementptr inbounds %Pessoa, ptr %t192, i32 0, i32 1
  %t195 = load i32, ptr %t194, align 4
  %t196 = add i32 %t195, %t2008
  store i32 %t196, ptr %soma, align 4
  %t199 = add i32 %i.17, 1
  %t186 = call i32 @length(ptr %t0)
  %t187 = icmp slt i32 %t199, %t186
  %t200 = load i32, ptr %soma, align 4
  br i1 %t187, label %while_body_4, label %while_cond_3.while_end_5_crit_edge

while_cond_3.while_end_5_crit_edge:               ; preds = %while_body_4
  %split = phi i32 [ %t200, %while_body_4 ]
  br label %while_end_5

while_end_5:                                      ; preds = %while_cond_3.while_end_5_crit_edge, %while_end_2
  %t200.lcssa = phi i32 [ %split, %while_cond_3.while_end_5_crit_edge ], [ %t2006, %while_end_2 ]
  %media = alloca i32, align 4
  %t203 = call i32 @length(ptr %t0)
  %t204 = sdiv i32 %t200.lcssa, %t203
  store i32 %t204, ptr %media, align 4
  %2 = call i32 (ptr, ...) @printf(ptr @.strStr, ptr @.str12)
  %t206 = load i32, ptr %media, align 4
  %3 = call i32 (ptr, ...) @printf(ptr @.strInt, i32 %t206)
  %t210 = call ptr @arraylist_get_ptr(ptr %t0, i64 2)
  %t212 = getelementptr inbounds %Pessoa, ptr %t210, i32 0, i32 2
  %t213 = load ptr, ptr %t212, align 8
  %t214 = getelementptr inbounds %Endereco, ptr %t213, i32 0, i32 2
  %t215 = load ptr, ptr %t214, align 8
  %t217 = load ptr, ptr %t215, align 8
  %t219 = call ptr @createString(ptr @.str1)
  %t221 = call i1 @strcmp_eq(ptr %t217, ptr %t219)
  br i1 %t221, label %and.rhs_6, label %and.short_8

and.rhs_6:                                        ; preds = %while_end_5
  %t226 = call ptr @arraylist_get_ptr(ptr %t0, i64 2)
  %t228 = getelementptr inbounds %Pessoa, ptr %t226, i32 0, i32 1
  %t229 = load i32, ptr %t228, align 4
  %t231 = icmp sgt i32 %t229, 30
  br label %and.end_7

and.short_8:                                      ; preds = %while_end_5
  br label %and.end_7

and.end_7:                                        ; preds = %and.short_8, %and.rhs_6
  %t222 = phi i1 [ %t231, %and.rhs_6 ], [ false, %and.short_8 ]
  br i1 %t222, label %then_1, label %endif_1

then_1:                                           ; preds = %and.end_7
  %t236 = call ptr @arraylist_get_ptr(ptr %t0, i64 2)
  %t239 = load ptr, ptr %t236, align 8
  call void @printString(ptr %t239)
  %4 = call i32 (ptr, ...) @printf(ptr @.strStr, ptr @.str13)
  br label %endif_1

endif_1:                                          ; preds = %then_1, %and.end_7
  store i32 0, ptr %soma, align 4
  %t2449 = call i32 @length(ptr %t0)
  %t24510 = icmp slt i32 0, %t2449
  %t25811 = load i32, ptr %soma, align 4
  br i1 %t24510, label %while_body_10.lr.ph, label %while_end_11

while_body_10.lr.ph:                              ; preds = %endif_1
  br label %while_body_10

while_body_10:                                    ; preds = %while_body_10.lr.ph, %while_body_10
  %t25813 = phi i32 [ %t25811, %while_body_10.lr.ph ], [ %t258, %while_body_10 ]
  %i.212 = phi i32 [ 0, %while_body_10.lr.ph ], [ %t257, %while_body_10 ]
  %t249 = zext i32 %i.212 to i64
  %t250 = call ptr @arraylist_get_ptr(ptr %t0, i64 %t249)
  %t252 = getelementptr inbounds %Pessoa, ptr %t250, i32 0, i32 1
  %t253 = load i32, ptr %t252, align 4
  %t254 = add i32 %t253, %t25813
  store i32 %t254, ptr %soma, align 4
  %t257 = add i32 %i.212, 1
  %t244 = call i32 @length(ptr %t0)
  %t245 = icmp slt i32 %t257, %t244
  %t258 = load i32, ptr %soma, align 4
  br i1 %t245, label %while_body_10, label %while_cond_9.while_end_11_crit_edge

while_cond_9.while_end_11_crit_edge:              ; preds = %while_body_10
  %split14 = phi i32 [ %t258, %while_body_10 ]
  br label %while_end_11

while_end_11:                                     ; preds = %while_cond_9.while_end_11_crit_edge, %endif_1
  %t258.lcssa = phi i32 [ %split14, %while_cond_9.while_end_11_crit_edge ], [ %t25811, %endif_1 ]
  %t261 = call i32 @length(ptr %t0)
  %t262 = sdiv i32 %t258.lcssa, %t261
  store i32 %t262, ptr %media, align 4
  %5 = call i32 (ptr, ...) @printf(ptr @.strStr, ptr @.str12)
  %t264 = load i32, ptr %media, align 4
  %6 = call i32 (ptr, ...) @printf(ptr @.strInt, i32 %t264)
  call void @freeList(ptr %t0)
  %7 = call i32 @getchar()
  ret i32 0
}
