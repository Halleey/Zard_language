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
@.str1 = private constant [16 x i8] c"input your name\00"
@.str2 = private constant [1 x i8] zeroinitializer
@.str3 = private constant [15 x i8] c"input your age\00"

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

declare i32 @inputInt(ptr)

declare double @inputDouble(ptr)

declare i1 @inputBool(ptr)

declare ptr @inputString(ptr)

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
  %t7 = call ptr @createString(ptr nonnull @.str2)
  store ptr %t7, ptr %t6, align 8
  %t9 = getelementptr inbounds %Pessoa, ptr %t6, i64 0, i32 1
  store i32 0, ptr %t9, align 4
  %t13 = call ptr @createString(ptr nonnull @.str0)
  store ptr %t13, ptr %t6, align 8
  %t15 = getelementptr inbounds %Pessoa, ptr %t6, i64 0, i32 1
  store i32 16, ptr %t15, align 4
  call void @arraylist_add_ptr(ptr %t4, ptr nonnull %t6)
  %puts = call i32 @puts(ptr nonnull dereferenceable(1) @.str1)
  %t22 = call ptr @inputString(ptr null)
  %t23 = call ptr @createString(ptr %t22)
  %puts2 = call i32 @puts(ptr nonnull dereferenceable(1) @.str3)
  %t25 = call i32 @inputInt(ptr null)
  %t26 = alloca %Pessoa, align 8
  %t27 = call ptr @createString(ptr nonnull @.str2)
  store ptr %t27, ptr %t26, align 8
  %t29 = getelementptr inbounds %Pessoa, ptr %t26, i64 0, i32 1
  store i32 0, ptr %t29, align 4
  store ptr %t23, ptr %t26, align 8
  %t34 = getelementptr inbounds %Pessoa, ptr %t26, i64 0, i32 1
  store i32 %t25, ptr %t34, align 4
  call void @arraylist_add_ptr(ptr %t4, ptr %t26)
  %t43 = call ptr @arraylist_get_ptr(ptr %t4, i64 0)
  call void @print_Pessoa(ptr %t43)
  call void @freeList(ptr %t4)
  %1 = call i32 @getchar()
  ret i32 0
}

; Function Attrs: nofree nounwind
declare noundef i32 @puts(ptr nocapture noundef readonly) #0

attributes #0 = { nofree nounwind }
