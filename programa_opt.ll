; ModuleID = 'programa.ll'
source_filename = "programa.ll"

%Pessoa = type { ptr, i32 }
%String = type { ptr, i64 }

@.strTrue = private constant [6 x i8] c"true\0A\00"
@.strFalse = private constant [7 x i8] c"false\0A\00"
@.strInt = private constant [4 x i8] c"%d\0A\00"
@.strDouble = private constant [4 x i8] c"%f\0A\00"
@.strStr = private constant [4 x i8] c"%s\0A\00"
@.str0 = private constant [10 x i8] c"testando \00"
@.str1 = private constant [4 x i8] c"sun\00"
@.str2 = private constant [7 x i8] c"halley\00"
@.str3 = private constant [6 x i8] c"angel\00"
@.str4 = private constant [8 x i8] c"Lawliet\00"
@.str5 = private constant [16 x i8] c"primeira struct\00"
@.str6 = private constant [15 x i8] c"segunda struct\00"
@.str7 = private constant [20 x i8] c"In\C3\ADcio do programa\00"
@.str8 = private constant [11 x i8] c"Contador: \00"
@.str9 = private constant [36 x i8] c"Erro: factorial de numero negativo!\00"

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

declare ptr @arraylist_create_int(i64)

declare void @arraylist_add_int(ptr, i32)

declare void @arraylist_addAll_int(ptr, ptr, i64)

declare void @arraylist_print_int(ptr)

declare void @arraylist_clear_int(ptr)

declare void @arraylist_free_int(ptr)

declare i32 @arraylist_get_int(ptr, i64, ptr)

declare void @arraylist_remove_int(ptr, i64)

declare i32 @arraylist_size_int(ptr)

define i32 @dobrar(i32 %valor) {
entry:
  %t5 = shl i32 %valor, 1
  ret i32 %t5
}

define i32 @factorial(i32 %n) {
entry:
  %t9 = icmp slt i32 %n, 0
  br i1 %t9, label %then_0, label %endif_0

then_0:                                           ; preds = %entry
  %puts = call i32 @puts(ptr nonnull dereferenceable(1) @.str9)
  ret i32 0

0:                                                ; No predecessors!
  br label %endif_0

endif_0:                                          ; preds = %0, %entry
  %t14 = icmp eq i32 %n, 0
  br i1 %t14, label %then_1, label %endif_1

then_1:                                           ; preds = %endif_0
  ret i32 1

1:                                                ; No predecessors!
  br label %endif_1

endif_1:                                          ; preds = %1, %endif_0
  %t19 = add i32 %n, -1
  %t20 = call i32 @factorial(i32 %t19)
  %t21 = mul i32 %n, %t20
  ret i32 %t21
}

define i32 @main() {
  %t23 = call ptr @arraylist_create(i64 4)
  %t27 = call ptr @createString(ptr nonnull @.str0)
  call void @printString(ptr %t27)
  %1 = call i32 (ptr, ...) @printf(ptr noundef nonnull dereferenceable(1) @.strInt, i32 2000)
  %t37 = call ptr @createString(ptr null)
  %t43 = call ptr @createString(ptr nonnull @.str1)
  call void @printString(ptr %t43)
  %t52 = call i32 @st_somar(i32 3, i32 4)
  %2 = call i32 (ptr, ...) @printf(ptr noundef nonnull dereferenceable(1) @.strInt, i32 %t52)
  %t53 = alloca %Pessoa, align 8
  %t55 = call ptr @createString(ptr nonnull @.str2)
  store ptr %t55, ptr %t53, align 8
  %t58 = getelementptr inbounds %Pessoa, ptr %t53, i64 0, i32 1
  store i32 10, ptr %t58, align 4
  %t60 = call ptr @createString(ptr null)
  %t66 = call ptr @createString(ptr nonnull @.str3)
  call void @printString(ptr %t66)
  %3 = call i32 (ptr, ...) @printf(ptr noundef nonnull dereferenceable(1) @.strInt, i32 16)
  %t75 = alloca %Pessoa, align 8
  %t77 = call ptr @createString(ptr nonnull @.str4)
  store ptr %t77, ptr %t75, align 8
  %t79 = getelementptr inbounds %Pessoa, ptr %t75, i64 0, i32 1
  store i32 0, ptr %t79, align 4
  %puts = call i32 @puts(ptr nonnull dereferenceable(1) @.str5)
  %t83 = load ptr, ptr %t53, align 8
  call void @printString(ptr %t83)
  %t84 = getelementptr inbounds %Pessoa, ptr %t53, i64 0, i32 1
  %t85 = load i32, ptr %t84, align 4
  %4 = call i32 (ptr, ...) @printf(ptr noundef nonnull dereferenceable(1) @.strInt, i32 %t85)
  %puts1 = call i32 @puts(ptr nonnull dereferenceable(1) @.str6)
  %t89 = load ptr, ptr %t75, align 8
  call void @printString(ptr %t89)
  %t90 = getelementptr inbounds %Pessoa, ptr %t75, i64 0, i32 1
  %t91 = load i32, ptr %t90, align 4
  %5 = call i32 (ptr, ...) @printf(ptr noundef nonnull dereferenceable(1) @.strInt, i32 %t91)
  %t94 = load ptr, ptr %t75, align 8
  call void @printString(ptr %t94)
  %t96 = call ptr @malloc(i64 16)
  store ptr @.str7, ptr %t96, align 8
  %t100 = getelementptr inbounds %String, ptr %t96, i64 0, i32 1
  store i64 18, ptr %t100, align 4
  call void @printString(ptr nonnull %t96)
  %t102 = call ptr @arraylist_create_int(i64 4)
  call void @arraylist_add_int(ptr %t102, i32 0)
  br label %while_cond_0

while_cond_0:                                     ; preds = %endif_2, %0
  %contador.0 = phi i32 [ 0, %0 ], [ %t122, %endif_2 ]
  %t109 = icmp slt i32 %contador.0, 5
  br i1 %t109, label %while_body_1, label %while_end_2

while_body_1:                                     ; preds = %while_cond_0
  %puts2 = call i32 @puts(ptr nonnull dereferenceable(1) @.str8)
  %6 = call i32 (ptr, ...) @printf(ptr noundef nonnull dereferenceable(1) @.strInt, i32 %contador.0)
  %t114 = icmp eq i32 %contador.0, 3
  br i1 %t114, label %then_2, label %endif_2

then_2:                                           ; preds = %while_body_1
  br label %while_end_2

endif_2:                                          ; preds = %while_body_1
  %t116 = call i32 @dobrar(i32 %contador.0)
  call void @arraylist_add_int(ptr %t102, i32 %t116)
  %t122 = add i32 %t116, 1
  br label %while_cond_0

while_end_2:                                      ; preds = %then_2, %while_cond_0
  %t124 = call i32 @factorial(i32 5)
  %7 = call i32 (ptr, ...) @printf(ptr noundef nonnull dereferenceable(1) @.strInt, i32 %t124)
  call void @arraylist_print_int(ptr %t102)
  call void @freeList(ptr %t23)
  call void @arraylist_free_int(ptr %t102)
  %8 = call i32 @getchar()
  ret i32 0
}

; Function Attrs: nofree nounwind
declare noundef i32 @puts(ptr nocapture noundef readonly) #0

attributes #0 = { nofree nounwind }
