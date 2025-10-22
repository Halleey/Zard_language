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
  %t16 = getelementptr inbounds %Pessoa, ptr %t5, i64 0, i32 1
  store i32 25, ptr %t16, align 4
  %t19 = alloca %Endereco, align 8
  %t20 = call ptr @createString(ptr null)
  store ptr %t20, ptr %t19, align 8
  %t22 = call ptr @createString(ptr null)
  %t23 = getelementptr inbounds %Endereco, ptr %t19, i64 0, i32 1
  store ptr %t22, ptr %t23, align 8
  %t27 = call ptr @createString(ptr nonnull @.str1)
  store ptr %t27, ptr %t19, align 8
  %t30 = getelementptr inbounds %Endereco, ptr %t19, i64 0, i32 1
  %t32 = call ptr @createString(ptr nonnull @.str2)
  store ptr %t32, ptr %t30, align 8
  %t35 = getelementptr inbounds %Pessoa, ptr %t5, i64 0, i32 2
  store ptr %t19, ptr %t35, align 8
  %t38 = alloca %Pessoa, align 8
  %t39 = call ptr @createString(ptr null)
  store ptr %t39, ptr %t38, align 8
  %t41 = getelementptr inbounds %Pessoa, ptr %t38, i64 0, i32 1
  store i32 0, ptr %t41, align 4
  %t42 = getelementptr inbounds %Pessoa, ptr %t38, i64 0, i32 2
  store ptr null, ptr %t42, align 8
  %t46 = call ptr @createString(ptr nonnull @.str3)
  store ptr %t46, ptr %t38, align 8
  %t49 = getelementptr inbounds %Pessoa, ptr %t38, i64 0, i32 1
  store i32 17, ptr %t49, align 4
  %t52 = alloca %Endereco, align 8
  %t53 = call ptr @createString(ptr null)
  store ptr %t53, ptr %t52, align 8
  %t55 = call ptr @createString(ptr null)
  %t56 = getelementptr inbounds %Endereco, ptr %t52, i64 0, i32 1
  store ptr %t55, ptr %t56, align 8
  %t60 = call ptr @createString(ptr nonnull @.str4)
  store ptr %t60, ptr %t52, align 8
  %t63 = getelementptr inbounds %Endereco, ptr %t52, i64 0, i32 1
  %t65 = call ptr @createString(ptr nonnull @.str5)
  store ptr %t65, ptr %t63, align 8
  %t68 = getelementptr inbounds %Pessoa, ptr %t38, i64 0, i32 2
  store ptr %t52, ptr %t68, align 8
  call void @arraylist_add_ptr(ptr %t3, ptr %t5)
  call void @arraylist_add_ptr(ptr %t3, ptr %t38)
  %t81 = call ptr @arraylist_get_ptr(ptr %t3, i64 0)
  %t84 = load ptr, ptr %t81, align 8
  call void @printString(ptr %t84)
  %t89 = call ptr @arraylist_get_ptr(ptr %t3, i64 0)
  %t91 = getelementptr inbounds %Pessoa, ptr %t89, i64 0, i32 2
  %t92 = load ptr, ptr %t91, align 8
  %t94 = load ptr, ptr %t92, align 8
  call void @printString(ptr %t94)
  %t99 = call ptr @arraylist_get_ptr(ptr %t3, i64 0)
  %t101 = getelementptr inbounds %Pessoa, ptr %t99, i64 0, i32 2
  %t102 = load ptr, ptr %t101, align 8
  %t103 = getelementptr inbounds %Endereco, ptr %t102, i64 0, i32 1
  %t104 = load ptr, ptr %t103, align 8
  call void @printString(ptr %t104)
  %t109 = call ptr @arraylist_get_ptr(ptr %t3, i64 1)
  %t112 = load ptr, ptr %t109, align 8
  call void @printString(ptr %t112)
  %t117 = call ptr @arraylist_get_ptr(ptr %t3, i64 1)
  %t119 = getelementptr inbounds %Pessoa, ptr %t117, i64 0, i32 2
  %t120 = load ptr, ptr %t119, align 8
  %t122 = load ptr, ptr %t120, align 8
  call void @printString(ptr %t122)
  %t127 = call ptr @arraylist_get_ptr(ptr %t3, i64 1)
  %t129 = getelementptr inbounds %Pessoa, ptr %t127, i64 0, i32 2
  %t130 = load ptr, ptr %t129, align 8
  %t131 = getelementptr inbounds %Endereco, ptr %t130, i64 0, i32 1
  %t132 = load ptr, ptr %t131, align 8
  call void @printString(ptr %t132)
  %t137 = call ptr @arraylist_get_ptr(ptr %t3, i64 0)
  %t139 = getelementptr inbounds %Pessoa, ptr %t137, i64 0, i32 2
  %t140 = load ptr, ptr %t139, align 8
  %t141 = getelementptr inbounds %Endereco, ptr %t140, i64 0, i32 1
  %t142 = load ptr, ptr %t141, align 8
  %t144 = call ptr @createString(ptr nonnull @.str2)
  %t146 = call i1 @strcmp_eq(ptr %t142, ptr %t144)
  br i1 %t146, label %then_0, label %endif_0

then_0:                                           ; preds = %0
  %puts = call i32 @puts(ptr nonnull dereferenceable(1) @.str6)
  br label %endif_0

endif_0:                                          ; preds = %then_0, %0
  %t152 = call ptr @arraylist_get_ptr(ptr %t3, i64 1)
  %t154 = getelementptr inbounds %Pessoa, ptr %t152, i64 0, i32 1
  %t155 = load i32, ptr %t154, align 4
  %t157 = icmp slt i32 %t155, 18
  br i1 %t157, label %then_1, label %endif_1

then_1:                                           ; preds = %endif_0
  %t162 = call ptr @arraylist_get_ptr(ptr %t3, i64 1)
  %t165 = load ptr, ptr %t162, align 8
  call void @printString(ptr %t165)
  %puts12 = call i32 @puts(ptr nonnull dereferenceable(1) @.str7)
  br label %endif_1

endif_1:                                          ; preds = %then_1, %endif_0
  %i = alloca i32, align 4
  br label %while_cond_0

while_cond_0:                                     ; preds = %while_body_1, %endif_1
  %storemerge = phi i32 [ 0, %endif_1 ], [ %t204, %while_body_1 ]
  store i32 %storemerge, ptr %i, align 4
  %t171 = call i32 @length(ptr %t3)
  %t172 = icmp slt i32 %storemerge, %t171
  br i1 %t172, label %while_body_1, label %while_end_2

while_body_1:                                     ; preds = %while_cond_0
  %puts16 = call i32 @puts(ptr nonnull dereferenceable(1) @.str8)
  %t176 = load i32, ptr %i, align 4
  %t177 = zext i32 %t176 to i64
  %t178 = call ptr @arraylist_get_ptr(ptr %t3, i64 %t177)
  %t181 = load ptr, ptr %t178, align 8
  call void @printString(ptr %t181)
  %t185 = zext i32 %t176 to i64
  %t186 = call ptr @arraylist_get_ptr(ptr %t3, i64 %t185)
  %t188 = getelementptr inbounds %Pessoa, ptr %t186, i64 0, i32 2
  %t189 = load ptr, ptr %t188, align 8
  %t191 = load ptr, ptr %t189, align 8
  call void @printString(ptr %t191)
  %t194 = load i32, ptr %i, align 4
  %t195 = zext i32 %t194 to i64
  %t196 = call ptr @arraylist_get_ptr(ptr %t3, i64 %t195)
  %t198 = getelementptr inbounds %Pessoa, ptr %t196, i64 0, i32 2
  %t199 = load ptr, ptr %t198, align 8
  %t200 = getelementptr inbounds %Endereco, ptr %t199, i64 0, i32 1
  %t201 = load ptr, ptr %t200, align 8
  call void @printString(ptr %t201)
  %t202 = load i32, ptr %i, align 4
  %t204 = add i32 %t202, 1
  br label %while_cond_0

while_end_2:                                      ; preds = %while_cond_0
  call void @clearList(ptr %t3)
  call void @arraylist_print_ptr(ptr %t3, ptr nonnull @print_Pessoa)
  call void @freeList(ptr %t3)
  %1 = call i32 @getchar()
  ret i32 0
}

; Function Attrs: nofree nounwind
declare noundef i32 @puts(ptr nocapture noundef readonly) #0

attributes #0 = { nofree nounwind }
