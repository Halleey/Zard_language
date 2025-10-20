; ModuleID = 'programa.ll'
source_filename = "programa.ll"

%st_Nomade = type { ptr, i32 }
%Endereco = type { ptr, ptr }
%Pessoa = type { ptr, i32, ptr }

@.strTrue = private constant [6 x i8] c"true\0A\00"
@.strFalse = private constant [7 x i8] c"false\0A\00"
@.strInt = private constant [4 x i8] c"%d\0A\00"
@.strDouble = private constant [4 x i8] c"%f\0A\00"
@.strStr = private constant [4 x i8] c"%s\0A\00"
@.str0 = private constant [6 x i8] c"Alice\00"
@.str1 = private constant [6 x i8] c"Rua A\00"
@.str2 = private constant [11 x i8] c"S\C3\A3o Paulo\00"
@.str3 = private constant [4 x i8] c"Bob\00"
@.str4 = private constant [12 x i8] c"Av. Central\00"
@.str5 = private constant [15 x i8] c"Rio de Janeiro\00"
@.str6 = private constant [37 x i8] c"A primeira pessoa mora em S\C3\A3o Paulo\00"
@.str7 = private constant [19 x i8] c" \C3\A9 menor de idade\00"
@.str8 = private constant [8 x i8] c"Pessoa:\00"

define i32 @st_somar(i32 %a, i32 %b) {
entry:
  %t2 = add i32 %a, %b
  ret i32 %t2
}

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

define void @print_Nomade(ptr %raw) {
entry:
  %val0 = load ptr, ptr %raw, align 8
  call void @printString(ptr %val0)
  %f1 = getelementptr inbounds %st_Nomade, ptr %raw, i64 0, i32 1
  %val1 = load i32, ptr %f1, align 4
  %0 = call i32 (ptr, ...) @printf(ptr noundef nonnull dereferenceable(1) @.strInt, i32 %val1)
  ret void
}

define void @print_Endereco(ptr %raw) {
entry:
  %val0 = load ptr, ptr %raw, align 8
  call void @printString(ptr %val0)
  %f1 = getelementptr inbounds %Endereco, ptr %raw, i64 0, i32 1
  %val1 = load ptr, ptr %f1, align 8
  call void @printString(ptr %val1)
  ret void
}

define void @print_Pessoa(ptr %raw) {
entry:
  %val0 = load ptr, ptr %raw, align 8
  call void @printString(ptr %val0)
  %f1 = getelementptr inbounds %Pessoa, ptr %raw, i64 0, i32 1
  %val1 = load i32, ptr %f1, align 4
  %0 = call i32 (ptr, ...) @printf(ptr noundef nonnull dereferenceable(1) @.strInt, i32 %val1)
  %f2 = getelementptr inbounds %Pessoa, ptr %raw, i64 0, i32 2
  %val21 = load ptr, ptr %f2, align 8
  call void @print_Endereco(ptr %val21)
  ret void
}

define i32 @main() {
  %t3 = call ptr @arraylist_create(i64 4)
  %t5 = alloca %Pessoa, align 8
  %t6 = call ptr @createString(ptr null)
  store ptr %t6, ptr %t5, align 8
  %t8 = getelementptr inbounds %Pessoa, ptr %t5, i64 0, i32 1
  store i32 0, ptr %t8, align 4
  %t9 = getelementptr inbounds %Pessoa, ptr %t5, i64 0, i32 2
  store ptr null, ptr %t9, align 8
  %t13 = call ptr @createString(ptr nonnull @.str0)
  store ptr %t13, ptr %t5, align 8
  %t15 = getelementptr inbounds %Pessoa, ptr %t5, i64 0, i32 1
  store i32 25, ptr %t15, align 4
  %t17 = alloca %Endereco, align 8
  %t18 = call ptr @createString(ptr null)
  store ptr %t18, ptr %t17, align 8
  %t20 = call ptr @createString(ptr null)
  %t21 = getelementptr inbounds %Endereco, ptr %t17, i64 0, i32 1
  store ptr %t20, ptr %t21, align 8
  %t25 = call ptr @createString(ptr nonnull @.str1)
  store ptr %t25, ptr %t17, align 8
  %t27 = getelementptr inbounds %Endereco, ptr %t17, i64 0, i32 1
  %t29 = call ptr @createString(ptr nonnull @.str2)
  store ptr %t29, ptr %t27, align 8
  %t31 = getelementptr inbounds %Pessoa, ptr %t5, i64 0, i32 2
  store ptr %t17, ptr %t31, align 8
  %t33 = alloca %Pessoa, align 8
  %t34 = call ptr @createString(ptr null)
  store ptr %t34, ptr %t33, align 8
  %t36 = getelementptr inbounds %Pessoa, ptr %t33, i64 0, i32 1
  store i32 0, ptr %t36, align 4
  %t37 = getelementptr inbounds %Pessoa, ptr %t33, i64 0, i32 2
  store ptr null, ptr %t37, align 8
  %t41 = call ptr @createString(ptr nonnull @.str3)
  store ptr %t41, ptr %t33, align 8
  %t43 = getelementptr inbounds %Pessoa, ptr %t33, i64 0, i32 1
  store i32 17, ptr %t43, align 4
  %t45 = alloca %Endereco, align 8
  %t46 = call ptr @createString(ptr null)
  store ptr %t46, ptr %t45, align 8
  %t48 = call ptr @createString(ptr null)
  %t49 = getelementptr inbounds %Endereco, ptr %t45, i64 0, i32 1
  store ptr %t48, ptr %t49, align 8
  %t53 = call ptr @createString(ptr nonnull @.str4)
  store ptr %t53, ptr %t45, align 8
  %t55 = getelementptr inbounds %Endereco, ptr %t45, i64 0, i32 1
  %t57 = call ptr @createString(ptr nonnull @.str5)
  store ptr %t57, ptr %t55, align 8
  %t59 = getelementptr inbounds %Pessoa, ptr %t33, i64 0, i32 2
  store ptr %t45, ptr %t59, align 8
  call void @arraylist_add_ptr(ptr %t3, ptr %t5)
  call void @arraylist_add_ptr(ptr %t3, ptr %t33)
  %t71 = call ptr @arraylist_get_ptr(ptr %t3, i64 0)
  %t74 = load ptr, ptr %t71, align 8
  call void @printString(ptr %t74)
  %t79 = call ptr @arraylist_get_ptr(ptr %t3, i64 0)
  %t81 = getelementptr inbounds %Pessoa, ptr %t79, i64 0, i32 2
  %t82 = load ptr, ptr %t81, align 8
  %t84 = load ptr, ptr %t82, align 8
  call void @printString(ptr %t84)
  %t89 = call ptr @arraylist_get_ptr(ptr %t3, i64 0)
  %t91 = getelementptr inbounds %Pessoa, ptr %t89, i64 0, i32 2
  %t92 = load ptr, ptr %t91, align 8
  %t93 = getelementptr inbounds %Endereco, ptr %t92, i64 0, i32 1
  %t94 = load ptr, ptr %t93, align 8
  call void @printString(ptr %t94)
  %t99 = call ptr @arraylist_get_ptr(ptr %t3, i64 1)
  %t102 = load ptr, ptr %t99, align 8
  call void @printString(ptr %t102)
  %t107 = call ptr @arraylist_get_ptr(ptr %t3, i64 1)
  %t109 = getelementptr inbounds %Pessoa, ptr %t107, i64 0, i32 2
  %t110 = load ptr, ptr %t109, align 8
  %t112 = load ptr, ptr %t110, align 8
  call void @printString(ptr %t112)
  %t117 = call ptr @arraylist_get_ptr(ptr %t3, i64 1)
  %t119 = getelementptr inbounds %Pessoa, ptr %t117, i64 0, i32 2
  %t120 = load ptr, ptr %t119, align 8
  %t121 = getelementptr inbounds %Endereco, ptr %t120, i64 0, i32 1
  %t122 = load ptr, ptr %t121, align 8
  call void @printString(ptr %t122)
  %t127 = call ptr @arraylist_get_ptr(ptr %t3, i64 0)
  %t129 = getelementptr inbounds %Pessoa, ptr %t127, i64 0, i32 2
  %t130 = load ptr, ptr %t129, align 8
  %t131 = getelementptr inbounds %Endereco, ptr %t130, i64 0, i32 1
  %t132 = load ptr, ptr %t131, align 8
  %t134 = call ptr @createString(ptr nonnull @.str2)
  %t136 = call i1 @strcmp_eq(ptr %t132, ptr %t134)
  br i1 %t136, label %then_0, label %endif_0

then_0:                                           ; preds = %0
  %puts = call i32 @puts(ptr nonnull dereferenceable(1) @.str6)
  br label %endif_0

endif_0:                                          ; preds = %then_0, %0
  %t142 = call ptr @arraylist_get_ptr(ptr %t3, i64 1)
  %t144 = getelementptr inbounds %Pessoa, ptr %t142, i64 0, i32 1
  %t145 = load i32, ptr %t144, align 4
  %t147 = icmp slt i32 %t145, 18
  br i1 %t147, label %then_1, label %endif_1

then_1:                                           ; preds = %endif_0
  %t152 = call ptr @arraylist_get_ptr(ptr %t3, i64 1)
  %t155 = load ptr, ptr %t152, align 8
  call void @printString(ptr %t155)
  %puts12 = call i32 @puts(ptr nonnull dereferenceable(1) @.str7)
  br label %endif_1

endif_1:                                          ; preds = %then_1, %endif_0
  %i = alloca i32, align 4
  br label %while_cond_0

while_cond_0:                                     ; preds = %while_body_1, %endif_1
  %storemerge = phi i32 [ 0, %endif_1 ], [ %t194, %while_body_1 ]
  store i32 %storemerge, ptr %i, align 4
  %t161 = call i32 @length(ptr %t3)
  %t162 = icmp slt i32 %storemerge, %t161
  br i1 %t162, label %while_body_1, label %while_end_2

while_body_1:                                     ; preds = %while_cond_0
  %puts15 = call i32 @puts(ptr nonnull dereferenceable(1) @.str8)
  %t166 = load i32, ptr %i, align 4
  %t167 = zext i32 %t166 to i64
  %t168 = call ptr @arraylist_get_ptr(ptr %t3, i64 %t167)
  %t171 = load ptr, ptr %t168, align 8
  call void @printString(ptr %t171)
  %t175 = zext i32 %t166 to i64
  %t176 = call ptr @arraylist_get_ptr(ptr %t3, i64 %t175)
  %t178 = getelementptr inbounds %Pessoa, ptr %t176, i64 0, i32 2
  %t179 = load ptr, ptr %t178, align 8
  %t181 = load ptr, ptr %t179, align 8
  call void @printString(ptr %t181)
  %t184 = load i32, ptr %i, align 4
  %t185 = zext i32 %t184 to i64
  %t186 = call ptr @arraylist_get_ptr(ptr %t3, i64 %t185)
  %t188 = getelementptr inbounds %Pessoa, ptr %t186, i64 0, i32 2
  %t189 = load ptr, ptr %t188, align 8
  %t190 = getelementptr inbounds %Endereco, ptr %t189, i64 0, i32 1
  %t191 = load ptr, ptr %t190, align 8
  call void @printString(ptr %t191)
  %t192 = load i32, ptr %i, align 4
  %t194 = add i32 %t192, 1
  br label %while_cond_0

while_end_2:                                      ; preds = %while_cond_0
  call void @freeList(ptr %t3)
  %1 = call i32 @getchar()
  ret i32 0
}

; Function Attrs: nofree nounwind
declare noundef i32 @puts(ptr nocapture noundef readonly) #0

attributes #0 = { nofree nounwind }
