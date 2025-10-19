; ModuleID = 'programa.ll'
source_filename = "programa.ll"

%st_Nomade = type { ptr, i32 }
%Pessoa = type { ptr, i32 }

@.strTrue = private constant [6 x i8] c"true\0A\00"
@.strFalse = private constant [7 x i8] c"false\0A\00"
@.strInt = private constant [4 x i8] c"%d\0A\00"
@.strDouble = private constant [4 x i8] c"%f\0A\00"
@.strStr = private constant [4 x i8] c"%s\0A\00"
@.str0 = private constant [6 x i8] c"angel\00"

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

define void @print_Pessoa(ptr %raw) {
entry:
  %val0 = load ptr, ptr %raw, align 8
  call void @printString(ptr %val0)
  %f1 = getelementptr inbounds %Pessoa, ptr %raw, i64 0, i32 1
  %val1 = load i32, ptr %f1, align 4
  %0 = call i32 (ptr, ...) @printf(ptr noundef nonnull dereferenceable(1) @.strInt, i32 %val1)
  ret void
}

define i32 @main() {
  %t4 = call ptr @arraylist_create(i64 4)
  %t6 = alloca %Pessoa, align 8
  %t7 = call ptr @createString(ptr null)
  store ptr %t7, ptr %t6, align 8
  %t9 = getelementptr inbounds %Pessoa, ptr %t6, i64 0, i32 1
  store i32 0, ptr %t9, align 4
  %t13 = call ptr @createString(ptr nonnull @.str0)
  store ptr %t13, ptr %t6, align 8
  %t15 = getelementptr inbounds %Pessoa, ptr %t6, i64 0, i32 1
  store i32 16, ptr %t15, align 4
  call void @printString(ptr %t13)
  %t20 = getelementptr inbounds %Pessoa, ptr %t6, i64 0, i32 1
  %t21 = load i32, ptr %t20, align 4
  %1 = call i32 (ptr, ...) @printf(ptr noundef nonnull dereferenceable(1) @.strInt, i32 %t21)
  call void @arraylist_add_ptr(ptr %t4, ptr %t6)
  call void @arraylist_print_ptr(ptr %t4, ptr nonnull @print_Pessoa)
  call void @freeList(ptr %t4)
  %2 = call i32 @getchar()
  ret i32 0
}
