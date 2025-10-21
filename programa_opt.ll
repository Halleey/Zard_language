; ModuleID = 'programa.ll'
source_filename = "programa.ll"

%st_Nomade = type { ptr, i32 }
%Pessoa = type { ptr, i32 }
%String = type { ptr, i64 }

@.strTrue = private constant [6 x i8] c"true\0A\00"
@.strFalse = private constant [7 x i8] c"false\0A\00"
@.strInt = private constant [4 x i8] c"%d\0A\00"
@.strDouble = private constant [4 x i8] c"%f\0A\00"
@.strStr = private constant [4 x i8] c"%s\0A\00"
@.str0 = private constant [5 x i8] c"blom\00"
@.str1 = private constant [7 x i8] c"deoxys\00"
@.str2 = private constant [16 x i8] c"struct completa\00"
@.str3 = private constant [12 x i8] c"apenas nome\00"
@.str4 = private constant [13 x i8] c"apenas idade\00"

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
  %t4 = call ptr @malloc(i64 16)
  store ptr @.str0, ptr %t4, align 8
  %t8 = getelementptr inbounds %String, ptr %t4, i64 0, i32 1
  store i64 4, ptr %t8, align 4
  %t9 = alloca %st_Nomade, align 8
  %t10 = call ptr @createString(ptr null)
  store ptr %t10, ptr %t9, align 8
  %t12 = getelementptr inbounds %st_Nomade, ptr %t9, i64 0, i32 1
  store i32 0, ptr %t12, align 4
  %t13 = alloca %Pessoa, align 8
  %t14 = call ptr @createString(ptr null)
  store ptr %t14, ptr %t13, align 8
  %t16 = getelementptr inbounds %Pessoa, ptr %t13, i64 0, i32 1
  store i32 0, ptr %t16, align 4
  %t20 = call ptr @createString(ptr nonnull @.str1)
  store ptr %t20, ptr %t13, align 8
  store ptr %t4, ptr %t9, align 8
  %t27 = getelementptr inbounds %Pessoa, ptr %t13, i64 0, i32 1
  store i32 999, ptr %t27, align 4
  %t31 = getelementptr inbounds %st_Nomade, ptr %t9, i64 0, i32 1
  store i32 333, ptr %t31, align 4
  %puts = call i32 @puts(ptr nonnull dereferenceable(1) @.str2)
  call void @print_Nomade(ptr %t9)
  %puts2 = call i32 @puts(ptr nonnull dereferenceable(1) @.str3)
  %t40 = load ptr, ptr %t9, align 8
  call void @printString(ptr %t40)
  %puts3 = call i32 @puts(ptr nonnull dereferenceable(1) @.str4)
  %t43 = getelementptr inbounds %st_Nomade, ptr %t9, i64 0, i32 1
  %t44 = load i32, ptr %t43, align 4
  %1 = call i32 (ptr, ...) @printf(ptr noundef nonnull dereferenceable(1) @.strInt, i32 %t44)
  %puts4 = call i32 @puts(ptr nonnull dereferenceable(1) @.str3)
  %t48 = load ptr, ptr %t13, align 8
  call void @printString(ptr %t48)
  %2 = call i32 @getchar()
  ret i32 0
}

; Function Attrs: nofree nounwind
declare noundef i32 @puts(ptr nocapture noundef readonly) #0

attributes #0 = { nofree nounwind }
