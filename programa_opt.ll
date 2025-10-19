; ModuleID = 'programa.ll'
source_filename = "programa.ll"

%Pessoa = type { ptr, i32 }
%String = type { ptr, i64 }

@.strTrue = private constant [6 x i8] c"true\0A\00"
@.strFalse = private constant [7 x i8] c"false\0A\00"
@.strInt = private constant [4 x i8] c"%d\0A\00"
@.strDouble = private constant [4 x i8] c"%f\0A\00"
@.strStr = private constant [4 x i8] c"%s\0A\00"
@.str0 = private constant [7 x i8] c"halley\00"
@.str1 = private constant [6 x i8] c"angel\00"
@.str2 = private constant [8 x i8] c"Lawliet\00"
@.str3 = private constant [16 x i8] c"primeira struct\00"
@.str4 = private constant [15 x i8] c"segunda struct\00"
@.str5 = private constant [20 x i8] c"In\C3\ADcio do programa\00"
@.str6 = private constant [11 x i8] c"Contador: \00"

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
  %t2 = shl i32 %valor, 1
  ret i32 %t2
}

define i32 @main() {
  %t5 = alloca %Pessoa, align 8
  %t7 = call ptr @createString(ptr nonnull @.str0)
  store ptr %t7, ptr %t5, align 8
  %t10 = getelementptr inbounds %Pessoa, ptr %t5, i64 0, i32 1
  store i32 10, ptr %t10, align 4
  %t12 = call ptr @createString(ptr null)
  %t18 = call ptr @createString(ptr nonnull @.str1)
  call void @printString(ptr %t18)
  %1 = call i32 (ptr, ...) @printf(ptr noundef nonnull dereferenceable(1) @.strInt, i32 16)
  %t27 = alloca %Pessoa, align 8
  %t29 = call ptr @createString(ptr nonnull @.str2)
  store ptr %t29, ptr %t27, align 8
  %t31 = getelementptr inbounds %Pessoa, ptr %t27, i64 0, i32 1
  store i32 0, ptr %t31, align 4
  %puts = call i32 @puts(ptr nonnull dereferenceable(1) @.str3)
  %t35 = load ptr, ptr %t5, align 8
  call void @printString(ptr %t35)
  %t36 = getelementptr inbounds %Pessoa, ptr %t5, i64 0, i32 1
  %t37 = load i32, ptr %t36, align 4
  %2 = call i32 (ptr, ...) @printf(ptr noundef nonnull dereferenceable(1) @.strInt, i32 %t37)
  %puts1 = call i32 @puts(ptr nonnull dereferenceable(1) @.str4)
  %t41 = load ptr, ptr %t27, align 8
  call void @printString(ptr %t41)
  %t42 = getelementptr inbounds %Pessoa, ptr %t27, i64 0, i32 1
  %t43 = load i32, ptr %t42, align 4
  %3 = call i32 (ptr, ...) @printf(ptr noundef nonnull dereferenceable(1) @.strInt, i32 %t43)
  %t45 = call ptr @malloc(i64 16)
  store ptr @.str5, ptr %t45, align 8
  %t49 = getelementptr inbounds %String, ptr %t45, i64 0, i32 1
  store i64 18, ptr %t49, align 4
  call void @printString(ptr nonnull %t45)
  %t51 = call ptr @arraylist_create_int(i64 4)
  call void @arraylist_add_int(ptr %t51, i32 0)
  br label %while_cond_0

while_cond_0:                                     ; preds = %endif_0, %0
  %contador.0 = phi i32 [ 0, %0 ], [ %t71, %endif_0 ]
  %t58 = icmp slt i32 %contador.0, 5
  br i1 %t58, label %while_body_1, label %while_end_2

while_body_1:                                     ; preds = %while_cond_0
  %puts2 = call i32 @puts(ptr nonnull dereferenceable(1) @.str6)
  %4 = call i32 (ptr, ...) @printf(ptr noundef nonnull dereferenceable(1) @.strInt, i32 %contador.0)
  %t63 = icmp eq i32 %contador.0, 3
  br i1 %t63, label %then_0, label %endif_0

then_0:                                           ; preds = %while_body_1
  br label %while_end_2

endif_0:                                          ; preds = %while_body_1
  %t65 = call i32 @dobrar(i32 %contador.0)
  call void @arraylist_add_int(ptr %t51, i32 %t65)
  %t71 = add i32 %t65, 1
  br label %while_cond_0

while_end_2:                                      ; preds = %then_0, %while_cond_0
  call void @arraylist_print_int(ptr %t51)
  call void @arraylist_free_int(ptr %t51)
  %5 = call i32 @getchar()
  ret i32 0
}

; Function Attrs: nofree nounwind
declare noundef i32 @puts(ptr nocapture noundef readonly) #0

attributes #0 = { nofree nounwind }
