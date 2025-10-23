; ModuleID = 'programa.ll'
source_filename = "programa.ll"

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
  %t0 = call ptr @arraylist_create(i64 4)
  %t2 = alloca %Pessoa, align 8
  %t3 = call ptr @createString(ptr null)
  %t4 = getelementptr inbounds %Pessoa, ptr %t2, i32 0, i32 0
  store ptr %t3, ptr %t4, align 8
  %t5 = getelementptr inbounds %Pessoa, ptr %t2, i32 0, i32 1
  store i32 0, ptr %t5, align 4
  %t6 = getelementptr inbounds %Pessoa, ptr %t2, i32 0, i32 2
  store ptr null, ptr %t6, align 8
  %t8 = getelementptr inbounds %Pessoa, ptr %t2, i32 0, i32 0
  %t10 = call ptr @createString(ptr @.str0)
  store ptr %t10, ptr %t8, align 8
  %t13 = getelementptr inbounds %Pessoa, ptr %t2, i32 0, i32 1
  %t14 = add i32 0, 25
  store i32 %t14, ptr %t13, align 4
  %t16 = alloca %Endereco, align 8
  %t17 = call ptr @createString(ptr null)
  %t18 = getelementptr inbounds %Endereco, ptr %t16, i32 0, i32 0
  store ptr %t17, ptr %t18, align 8
  %t19 = call ptr @createString(ptr null)
  %t20 = getelementptr inbounds %Endereco, ptr %t16, i32 0, i32 1
  store ptr %t19, ptr %t20, align 8
  %t22 = getelementptr inbounds %Endereco, ptr %t16, i32 0, i32 0
  %t24 = call ptr @createString(ptr @.str1)
  store ptr %t24, ptr %t22, align 8
  %t27 = getelementptr inbounds %Endereco, ptr %t16, i32 0, i32 1
  %t29 = call ptr @createString(ptr @.str2)
  store ptr %t29, ptr %t27, align 8
  %t32 = getelementptr inbounds %Pessoa, ptr %t2, i32 0, i32 2
  store ptr %t16, ptr %t32, align 8
  %t35 = alloca %Pessoa, align 8
  %t36 = call ptr @createString(ptr null)
  %t37 = getelementptr inbounds %Pessoa, ptr %t35, i32 0, i32 0
  store ptr %t36, ptr %t37, align 8
  %t38 = getelementptr inbounds %Pessoa, ptr %t35, i32 0, i32 1
  store i32 0, ptr %t38, align 4
  %t39 = getelementptr inbounds %Pessoa, ptr %t35, i32 0, i32 2
  store ptr null, ptr %t39, align 8
  %t41 = getelementptr inbounds %Pessoa, ptr %t35, i32 0, i32 0
  %t43 = call ptr @createString(ptr @.str3)
  store ptr %t43, ptr %t41, align 8
  %t46 = getelementptr inbounds %Pessoa, ptr %t35, i32 0, i32 1
  %t47 = add i32 0, 17
  store i32 %t47, ptr %t46, align 4
  %t49 = alloca %Endereco, align 8
  %t50 = call ptr @createString(ptr null)
  %t51 = getelementptr inbounds %Endereco, ptr %t49, i32 0, i32 0
  store ptr %t50, ptr %t51, align 8
  %t52 = call ptr @createString(ptr null)
  %t53 = getelementptr inbounds %Endereco, ptr %t49, i32 0, i32 1
  store ptr %t52, ptr %t53, align 8
  %t55 = getelementptr inbounds %Endereco, ptr %t49, i32 0, i32 0
  %t57 = call ptr @createString(ptr @.str4)
  store ptr %t57, ptr %t55, align 8
  %t60 = getelementptr inbounds %Endereco, ptr %t49, i32 0, i32 1
  %t62 = call ptr @createString(ptr @.str5)
  store ptr %t62, ptr %t60, align 8
  %t65 = getelementptr inbounds %Pessoa, ptr %t35, i32 0, i32 2
  store ptr %t49, ptr %t65, align 8
  %t70 = bitcast ptr %t0 to ptr
  call void @arraylist_add_ptr(ptr %t70, ptr %t2)
  %t73 = bitcast ptr %t0 to ptr
  call void @arraylist_add_ptr(ptr %t73, ptr %t35)
  %t75 = add i32 0, 0
  %t76 = zext i32 %t75 to i64
  %t77 = call ptr @arraylist_get_ptr(ptr %t0, i64 %t76)
  %t78 = bitcast ptr %t77 to ptr
  %t79 = getelementptr inbounds %Pessoa, ptr %t78, i32 0, i32 0
  %t80 = load ptr, ptr %t79, align 8
  call void @printString(ptr %t80)
  %t82 = add i32 0, 0
  %t83 = zext i32 %t82 to i64
  %t84 = call ptr @arraylist_get_ptr(ptr %t0, i64 %t83)
  %t85 = bitcast ptr %t84 to ptr
  %t86 = getelementptr inbounds %Pessoa, ptr %t85, i32 0, i32 2
  %t87 = load ptr, ptr %t86, align 8
  %t88 = getelementptr inbounds %Endereco, ptr %t87, i32 0, i32 0
  %t89 = load ptr, ptr %t88, align 8
  call void @printString(ptr %t89)
  %t91 = add i32 0, 0
  %t92 = zext i32 %t91 to i64
  %t93 = call ptr @arraylist_get_ptr(ptr %t0, i64 %t92)
  %t94 = bitcast ptr %t93 to ptr
  %t95 = getelementptr inbounds %Pessoa, ptr %t94, i32 0, i32 2
  %t96 = load ptr, ptr %t95, align 8
  %t97 = getelementptr inbounds %Endereco, ptr %t96, i32 0, i32 1
  %t98 = load ptr, ptr %t97, align 8
  call void @printString(ptr %t98)
  %t100 = add i32 0, 1
  %t101 = zext i32 %t100 to i64
  %t102 = call ptr @arraylist_get_ptr(ptr %t0, i64 %t101)
  %t103 = bitcast ptr %t102 to ptr
  %t104 = getelementptr inbounds %Pessoa, ptr %t103, i32 0, i32 0
  %t105 = load ptr, ptr %t104, align 8
  call void @printString(ptr %t105)
  %t107 = add i32 0, 1
  %t108 = zext i32 %t107 to i64
  %t109 = call ptr @arraylist_get_ptr(ptr %t0, i64 %t108)
  %t110 = bitcast ptr %t109 to ptr
  %t111 = getelementptr inbounds %Pessoa, ptr %t110, i32 0, i32 2
  %t112 = load ptr, ptr %t111, align 8
  %t113 = getelementptr inbounds %Endereco, ptr %t112, i32 0, i32 0
  %t114 = load ptr, ptr %t113, align 8
  call void @printString(ptr %t114)
  %t116 = add i32 0, 1
  %t117 = zext i32 %t116 to i64
  %t118 = call ptr @arraylist_get_ptr(ptr %t0, i64 %t117)
  %t119 = bitcast ptr %t118 to ptr
  %t120 = getelementptr inbounds %Pessoa, ptr %t119, i32 0, i32 2
  %t121 = load ptr, ptr %t120, align 8
  %t122 = getelementptr inbounds %Endereco, ptr %t121, i32 0, i32 1
  %t123 = load ptr, ptr %t122, align 8
  call void @printString(ptr %t123)
  %t125 = add i32 0, 0
  %t126 = zext i32 %t125 to i64
  %t127 = call ptr @arraylist_get_ptr(ptr %t0, i64 %t126)
  %t128 = bitcast ptr %t127 to ptr
  %t129 = getelementptr inbounds %Pessoa, ptr %t128, i32 0, i32 2
  %t130 = load ptr, ptr %t129, align 8
  %t131 = getelementptr inbounds %Endereco, ptr %t130, i32 0, i32 1
  %t132 = load ptr, ptr %t131, align 8
  %t134 = call ptr @createString(ptr @.str2)
  %t136 = call i1 @strcmp_eq(ptr %t132, ptr %t134)
  br i1 %t136, label %then_0, label %endif_0

then_0:                                           ; preds = %0
  %t137 = getelementptr inbounds [36 x i8], ptr @.str6, i32 0, i32 0
  %1 = call i32 (ptr, ...) @printf(ptr @.strStr, ptr %t137)
  br label %endif_0

endif_0:                                          ; preds = %then_0, %0
  %t139 = add i32 0, 1
  %t140 = zext i32 %t139 to i64
  %t141 = call ptr @arraylist_get_ptr(ptr %t0, i64 %t140)
  %t142 = bitcast ptr %t141 to ptr
  %t143 = getelementptr inbounds %Pessoa, ptr %t142, i32 0, i32 1
  %t144 = load i32, ptr %t143, align 4
  %t145 = add i32 0, 18
  %t146 = icmp slt i32 %t144, %t145
  br i1 %t146, label %then_1, label %endif_1

then_1:                                           ; preds = %endif_0
  %t148 = add i32 0, 1
  %t149 = zext i32 %t148 to i64
  %t150 = call ptr @arraylist_get_ptr(ptr %t0, i64 %t149)
  %t151 = bitcast ptr %t150 to ptr
  %t152 = getelementptr inbounds %Pessoa, ptr %t151, i32 0, i32 0
  %t153 = load ptr, ptr %t152, align 8
  call void @printString(ptr %t153)
  %t154 = getelementptr inbounds [18 x i8], ptr @.str7, i32 0, i32 0
  %2 = call i32 (ptr, ...) @printf(ptr @.strStr, ptr %t154)
  br label %endif_1

endif_1:                                          ; preds = %then_1, %endif_0
  %i = alloca i32, align 4
  %t155 = add i32 0, 0
  store i32 %t155, ptr %i, align 4
  br label %while_cond_0

while_cond_0:                                     ; preds = %while_body_1, %endif_1
  %t156 = load i32, ptr %i, align 4
  %t158 = bitcast ptr %t0 to ptr
  %t159 = call i32 @length(ptr %t158)
  %t160 = icmp slt i32 %t156, %t159
  br i1 %t160, label %while_body_1, label %while_end_2

while_body_1:                                     ; preds = %while_cond_0
  %t161 = getelementptr inbounds [8 x i8], ptr @.str8, i32 0, i32 0
  %3 = call i32 (ptr, ...) @printf(ptr @.strStr, ptr %t161)
  %t163 = load i32, ptr %i, align 4
  %t164 = zext i32 %t163 to i64
  %t165 = call ptr @arraylist_get_ptr(ptr %t0, i64 %t164)
  %t166 = bitcast ptr %t165 to ptr
  %t167 = getelementptr inbounds %Pessoa, ptr %t166, i32 0, i32 0
  %t168 = load ptr, ptr %t167, align 8
  call void @printString(ptr %t168)
  %t170 = load i32, ptr %i, align 4
  %t171 = zext i32 %t170 to i64
  %t172 = call ptr @arraylist_get_ptr(ptr %t0, i64 %t171)
  %t173 = bitcast ptr %t172 to ptr
  %t174 = getelementptr inbounds %Pessoa, ptr %t173, i32 0, i32 2
  %t175 = load ptr, ptr %t174, align 8
  %t176 = getelementptr inbounds %Endereco, ptr %t175, i32 0, i32 0
  %t177 = load ptr, ptr %t176, align 8
  call void @printString(ptr %t177)
  %t179 = load i32, ptr %i, align 4
  %t180 = zext i32 %t179 to i64
  %t181 = call ptr @arraylist_get_ptr(ptr %t0, i64 %t180)
  %t182 = bitcast ptr %t181 to ptr
  %t183 = getelementptr inbounds %Pessoa, ptr %t182, i32 0, i32 2
  %t184 = load ptr, ptr %t183, align 8
  %t185 = getelementptr inbounds %Endereco, ptr %t184, i32 0, i32 1
  %t186 = load ptr, ptr %t185, align 8
  call void @printString(ptr %t186)
  %t187 = load i32, ptr %i, align 4
  %t188 = add i32 0, 1
  %t189 = add i32 %t187, %t188
  store i32 %t189, ptr %i, align 4
  br label %while_cond_0

while_end_2:                                      ; preds = %while_cond_0
  %t191 = bitcast ptr %t0 to ptr
  call void @freeList(ptr %t191)
  %4 = call i32 @getchar()
  ret i32 0
}
