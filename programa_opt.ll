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

define void @print_Nomade(ptr %p) {
entry:
  %f0 = getelementptr inbounds %st_Nomade, ptr %p, i32 0, i32 0
  %val0 = load ptr, ptr %f0, align 8
  call void @printString(ptr %val0)
  %f1 = getelementptr inbounds %st_Nomade, ptr %p, i32 0, i32 1
  %val1 = load i32, ptr %f1, align 4
  %0 = call i32 (ptr, ...) @printf(ptr @.strInt, i32 %val1)
  ret void
}

define void @print_Endereco(ptr %p) {
entry:
  %f0 = getelementptr inbounds %Endereco, ptr %p, i32 0, i32 0
  %val0 = load ptr, ptr %f0, align 8
  call void @printString(ptr %val0)
  %f1 = getelementptr inbounds %Endereco, ptr %p, i32 0, i32 1
  %val1 = load ptr, ptr %f1, align 8
  call void @printString(ptr %val1)
  ret void
}

define void @print_Pessoa(ptr %p) {
entry:
  %f0 = getelementptr inbounds %Pessoa, ptr %p, i32 0, i32 0
  %val0 = load ptr, ptr %f0, align 8
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
  %t3 = call ptr @arraylist_create(i64 4)
  %t5 = alloca %Pessoa, align 8
  %t6 = call ptr @createString(ptr null)
  %t7 = getelementptr inbounds %Pessoa, ptr %t5, i32 0, i32 0
  store ptr %t6, ptr %t7, align 8
  %t8 = getelementptr inbounds %Pessoa, ptr %t5, i32 0, i32 1
  store i32 0, ptr %t8, align 4
  %t9 = getelementptr inbounds %Pessoa, ptr %t5, i32 0, i32 2
  store ptr null, ptr %t9, align 8
  %t11 = getelementptr inbounds %Pessoa, ptr %t5, i32 0, i32 0
  %t13 = call ptr @createString(ptr @.str0)
  store ptr %t13, ptr %t11, align 8
  %t16 = getelementptr inbounds %Pessoa, ptr %t5, i32 0, i32 1
  %t17 = add i32 0, 25
  store i32 %t17, ptr %t16, align 4
  %t19 = alloca %Endereco, align 8
  %t20 = call ptr @createString(ptr null)
  %t21 = getelementptr inbounds %Endereco, ptr %t19, i32 0, i32 0
  store ptr %t20, ptr %t21, align 8
  %t22 = call ptr @createString(ptr null)
  %t23 = getelementptr inbounds %Endereco, ptr %t19, i32 0, i32 1
  store ptr %t22, ptr %t23, align 8
  %t25 = getelementptr inbounds %Endereco, ptr %t19, i32 0, i32 0
  %t27 = call ptr @createString(ptr @.str1)
  store ptr %t27, ptr %t25, align 8
  %t30 = getelementptr inbounds %Endereco, ptr %t19, i32 0, i32 1
  %t32 = call ptr @createString(ptr @.str2)
  store ptr %t32, ptr %t30, align 8
  %t35 = getelementptr inbounds %Pessoa, ptr %t5, i32 0, i32 2
  store ptr %t19, ptr %t35, align 8
  %t38 = alloca %Pessoa, align 8
  %t39 = call ptr @createString(ptr null)
  %t40 = getelementptr inbounds %Pessoa, ptr %t38, i32 0, i32 0
  store ptr %t39, ptr %t40, align 8
  %t41 = getelementptr inbounds %Pessoa, ptr %t38, i32 0, i32 1
  store i32 0, ptr %t41, align 4
  %t42 = getelementptr inbounds %Pessoa, ptr %t38, i32 0, i32 2
  store ptr null, ptr %t42, align 8
  %t44 = getelementptr inbounds %Pessoa, ptr %t38, i32 0, i32 0
  %t46 = call ptr @createString(ptr @.str3)
  store ptr %t46, ptr %t44, align 8
  %t49 = getelementptr inbounds %Pessoa, ptr %t38, i32 0, i32 1
  %t50 = add i32 0, 17
  store i32 %t50, ptr %t49, align 4
  %t52 = alloca %Endereco, align 8
  %t53 = call ptr @createString(ptr null)
  %t54 = getelementptr inbounds %Endereco, ptr %t52, i32 0, i32 0
  store ptr %t53, ptr %t54, align 8
  %t55 = call ptr @createString(ptr null)
  %t56 = getelementptr inbounds %Endereco, ptr %t52, i32 0, i32 1
  store ptr %t55, ptr %t56, align 8
  %t58 = getelementptr inbounds %Endereco, ptr %t52, i32 0, i32 0
  %t60 = call ptr @createString(ptr @.str4)
  store ptr %t60, ptr %t58, align 8
  %t63 = getelementptr inbounds %Endereco, ptr %t52, i32 0, i32 1
  %t65 = call ptr @createString(ptr @.str5)
  store ptr %t65, ptr %t63, align 8
  %t68 = getelementptr inbounds %Pessoa, ptr %t38, i32 0, i32 2
  store ptr %t52, ptr %t68, align 8
  %t73 = bitcast ptr %t3 to ptr
  call void @arraylist_add_ptr(ptr %t73, ptr %t5)
  %t76 = bitcast ptr %t3 to ptr
  call void @arraylist_add_ptr(ptr %t76, ptr %t38)
  %t78 = add i32 0, 0
  %t79 = zext i32 %t78 to i64
  %t80 = call ptr @arraylist_get_ptr(ptr %t3, i64 %t79)
  %t81 = bitcast ptr %t80 to ptr
  %t82 = getelementptr inbounds %Pessoa, ptr %t81, i32 0, i32 0
  %t83 = load ptr, ptr %t82, align 8
  call void @printString(ptr %t83)
  %t85 = add i32 0, 0
  %t86 = zext i32 %t85 to i64
  %t87 = call ptr @arraylist_get_ptr(ptr %t3, i64 %t86)
  %t88 = bitcast ptr %t87 to ptr
  %t89 = getelementptr inbounds %Pessoa, ptr %t88, i32 0, i32 2
  %t90 = load ptr, ptr %t89, align 8
  %t91 = getelementptr inbounds %Endereco, ptr %t90, i32 0, i32 0
  %t92 = load ptr, ptr %t91, align 8
  call void @printString(ptr %t92)
  %t94 = add i32 0, 0
  %t95 = zext i32 %t94 to i64
  %t96 = call ptr @arraylist_get_ptr(ptr %t3, i64 %t95)
  %t97 = bitcast ptr %t96 to ptr
  %t98 = getelementptr inbounds %Pessoa, ptr %t97, i32 0, i32 2
  %t99 = load ptr, ptr %t98, align 8
  %t100 = getelementptr inbounds %Endereco, ptr %t99, i32 0, i32 1
  %t101 = load ptr, ptr %t100, align 8
  call void @printString(ptr %t101)
  %t103 = add i32 0, 1
  %t104 = zext i32 %t103 to i64
  %t105 = call ptr @arraylist_get_ptr(ptr %t3, i64 %t104)
  %t106 = bitcast ptr %t105 to ptr
  %t107 = getelementptr inbounds %Pessoa, ptr %t106, i32 0, i32 0
  %t108 = load ptr, ptr %t107, align 8
  call void @printString(ptr %t108)
  %t110 = add i32 0, 1
  %t111 = zext i32 %t110 to i64
  %t112 = call ptr @arraylist_get_ptr(ptr %t3, i64 %t111)
  %t113 = bitcast ptr %t112 to ptr
  %t114 = getelementptr inbounds %Pessoa, ptr %t113, i32 0, i32 2
  %t115 = load ptr, ptr %t114, align 8
  %t116 = getelementptr inbounds %Endereco, ptr %t115, i32 0, i32 0
  %t117 = load ptr, ptr %t116, align 8
  call void @printString(ptr %t117)
  %t119 = add i32 0, 1
  %t120 = zext i32 %t119 to i64
  %t121 = call ptr @arraylist_get_ptr(ptr %t3, i64 %t120)
  %t122 = bitcast ptr %t121 to ptr
  %t123 = getelementptr inbounds %Pessoa, ptr %t122, i32 0, i32 2
  %t124 = load ptr, ptr %t123, align 8
  %t125 = getelementptr inbounds %Endereco, ptr %t124, i32 0, i32 1
  %t126 = load ptr, ptr %t125, align 8
  call void @printString(ptr %t126)
  %t128 = add i32 0, 0
  %t129 = zext i32 %t128 to i64
  %t130 = call ptr @arraylist_get_ptr(ptr %t3, i64 %t129)
  %t131 = bitcast ptr %t130 to ptr
  %t132 = getelementptr inbounds %Pessoa, ptr %t131, i32 0, i32 2
  %t133 = load ptr, ptr %t132, align 8
  %t134 = getelementptr inbounds %Endereco, ptr %t133, i32 0, i32 1
  %t135 = load ptr, ptr %t134, align 8
  %t137 = call ptr @createString(ptr @.str2)
  %t139 = call i1 @strcmp_eq(ptr %t135, ptr %t137)
  br i1 %t139, label %then_0, label %endif_0

then_0:                                           ; preds = %0
  %t140 = getelementptr inbounds [36 x i8], ptr @.str6, i32 0, i32 0
  %1 = call i32 (ptr, ...) @printf(ptr @.strStr, ptr %t140)
  br label %endif_0

endif_0:                                          ; preds = %then_0, %0
  %t142 = add i32 0, 1
  %t143 = zext i32 %t142 to i64
  %t144 = call ptr @arraylist_get_ptr(ptr %t3, i64 %t143)
  %t145 = bitcast ptr %t144 to ptr
  %t146 = getelementptr inbounds %Pessoa, ptr %t145, i32 0, i32 1
  %t147 = load i32, ptr %t146, align 4
  %t148 = add i32 0, 18
  %t149 = icmp slt i32 %t147, %t148
  br i1 %t149, label %then_1, label %endif_1

then_1:                                           ; preds = %endif_0
  %t151 = add i32 0, 1
  %t152 = zext i32 %t151 to i64
  %t153 = call ptr @arraylist_get_ptr(ptr %t3, i64 %t152)
  %t154 = bitcast ptr %t153 to ptr
  %t155 = getelementptr inbounds %Pessoa, ptr %t154, i32 0, i32 0
  %t156 = load ptr, ptr %t155, align 8
  call void @printString(ptr %t156)
  %t157 = getelementptr inbounds [18 x i8], ptr @.str7, i32 0, i32 0
  %2 = call i32 (ptr, ...) @printf(ptr @.strStr, ptr %t157)
  br label %endif_1

endif_1:                                          ; preds = %then_1, %endif_0
  %i = alloca i32, align 4
  %t158 = add i32 0, 0
  store i32 %t158, ptr %i, align 4
  br label %while_cond_0

while_cond_0:                                     ; preds = %while_body_1, %endif_1
  %t159 = load i32, ptr %i, align 4
  %t161 = bitcast ptr %t3 to ptr
  %t162 = call i32 @length(ptr %t161)
  %t163 = icmp slt i32 %t159, %t162
  br i1 %t163, label %while_body_1, label %while_end_2

while_body_1:                                     ; preds = %while_cond_0
  %t164 = getelementptr inbounds [8 x i8], ptr @.str8, i32 0, i32 0
  %3 = call i32 (ptr, ...) @printf(ptr @.strStr, ptr %t164)
  %t166 = load i32, ptr %i, align 4
  %t167 = zext i32 %t166 to i64
  %t168 = call ptr @arraylist_get_ptr(ptr %t3, i64 %t167)
  %t169 = bitcast ptr %t168 to ptr
  %t170 = getelementptr inbounds %Pessoa, ptr %t169, i32 0, i32 0
  %t171 = load ptr, ptr %t170, align 8
  call void @printString(ptr %t171)
  %t173 = load i32, ptr %i, align 4
  %t174 = zext i32 %t173 to i64
  %t175 = call ptr @arraylist_get_ptr(ptr %t3, i64 %t174)
  %t176 = bitcast ptr %t175 to ptr
  %t177 = getelementptr inbounds %Pessoa, ptr %t176, i32 0, i32 2
  %t178 = load ptr, ptr %t177, align 8
  %t179 = getelementptr inbounds %Endereco, ptr %t178, i32 0, i32 0
  %t180 = load ptr, ptr %t179, align 8
  call void @printString(ptr %t180)
  %t182 = load i32, ptr %i, align 4
  %t183 = zext i32 %t182 to i64
  %t184 = call ptr @arraylist_get_ptr(ptr %t3, i64 %t183)
  %t185 = bitcast ptr %t184 to ptr
  %t186 = getelementptr inbounds %Pessoa, ptr %t185, i32 0, i32 2
  %t187 = load ptr, ptr %t186, align 8
  %t188 = getelementptr inbounds %Endereco, ptr %t187, i32 0, i32 1
  %t189 = load ptr, ptr %t188, align 8
  call void @printString(ptr %t189)
  %t190 = load i32, ptr %i, align 4
  %t191 = add i32 0, 1
  %t192 = add i32 %t190, %t191
  store i32 %t192, ptr %i, align 4
  br label %while_cond_0

while_end_2:                                      ; preds = %while_cond_0
  %t194 = bitcast ptr %t3 to ptr
  call void @freeList(ptr %t194)
  %4 = call i32 @getchar()
  ret i32 0
}
