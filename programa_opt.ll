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
@.str1 = private constant [8 x i8] c"Lawliet\00"
@.str2 = private constant [16 x i8] c"primeira struct\00"
@.str3 = private constant [15 x i8] c"segunda struct\00"
@.str4 = private constant [20 x i8] c"In\C3\ADcio do programa\00"
@.str5 = private constant [11 x i8] c"Contador: \00"

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
  %t15 = alloca %Pessoa, align 8
  %t17 = call ptr @createString(ptr nonnull @.str1)
  store ptr %t17, ptr %t15, align 8
  %t19 = getelementptr inbounds %Pessoa, ptr %t15, i64 0, i32 1
  store i32 0, ptr %t19, align 4
  %puts = call i32 @puts(ptr nonnull dereferenceable(1) @.str2)
  %t23 = load ptr, ptr %t5, align 8
  call void @printString(ptr %t23)
  %t24 = getelementptr inbounds %Pessoa, ptr %t5, i64 0, i32 1
  %t25 = load i32, ptr %t24, align 4
  %1 = call i32 (ptr, ...) @printf(ptr noundef nonnull dereferenceable(1) @.strInt, i32 %t25)
  %puts1 = call i32 @puts(ptr nonnull dereferenceable(1) @.str3)
  %t29 = load ptr, ptr %t15, align 8
  call void @printString(ptr %t29)
  %t30 = getelementptr inbounds %Pessoa, ptr %t15, i64 0, i32 1
  %t31 = load i32, ptr %t30, align 4
  %2 = call i32 (ptr, ...) @printf(ptr noundef nonnull dereferenceable(1) @.strInt, i32 %t31)
  %t33 = call ptr @malloc(i64 16)
  store ptr @.str4, ptr %t33, align 8
  %t37 = getelementptr inbounds %String, ptr %t33, i64 0, i32 1
  store i64 18, ptr %t37, align 4
  call void @printString(ptr nonnull %t33)
  %t39 = call ptr @arraylist_create_int(i64 4)
  br label %while_cond_0

while_cond_0:                                     ; preds = %endif_0, %0
  %contador.0 = phi i32 [ 0, %0 ], [ %t55, %endif_0 ]
  %t42 = icmp slt i32 %contador.0, 5
  br i1 %t42, label %while_body_1, label %while_end_2

while_body_1:                                     ; preds = %while_cond_0
  %puts2 = call i32 @puts(ptr nonnull dereferenceable(1) @.str5)
  %3 = call i32 (ptr, ...) @printf(ptr noundef nonnull dereferenceable(1) @.strInt, i32 %contador.0)
  %t47 = icmp eq i32 %contador.0, 3
  br i1 %t47, label %then_0, label %endif_0

then_0:                                           ; preds = %while_body_1
  br label %while_end_2

endif_0:                                          ; preds = %while_body_1
  %t49 = call i32 @dobrar(i32 %contador.0)
  call void @arraylist_add_int(ptr %t39, i32 %t49)
  %t55 = add i32 %t49, 1
  br label %while_cond_0

while_end_2:                                      ; preds = %then_0, %while_cond_0
  call void @arraylist_print_int(ptr %t39)
  call void @arraylist_free_int(ptr %t39)
  %4 = call i32 @getchar()
  ret i32 0
}

; Function Attrs: nofree nounwind
declare noundef i32 @puts(ptr nocapture noundef readonly) #0

attributes #0 = { nofree nounwind }
