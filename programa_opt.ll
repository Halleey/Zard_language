; ModuleID = 'programa.ll'
source_filename = "programa.ll"

%String = type { ptr, i64 }
%st_Nomade = type { ptr, i32 }
%Pessoa = type { ptr, i32 }

@.strTrue = private constant [6 x i8] c"true\0A\00"
@.strFalse = private constant [7 x i8] c"false\0A\00"
@.strInt = private constant [4 x i8] c"%d\0A\00"
@.strDouble = private constant [4 x i8] c"%f\0A\00"
@.strStr = private constant [4 x i8] c"%s\0A\00"
@.str0 = private constant [5 x i8] c"zard\00"
@.str1 = private constant [14 x i8] c"struct import\00"
@.str2 = private constant [7 x i8] c"halley\00"
@.str3 = private constant [16 x i8] c"primeira struct\00"
@.str4 = private constant [6 x i8] c"angel\00"
@.str5 = private constant [8 x i8] c"Lawliet\00"
@.str6 = private constant [15 x i8] c"segunda struct\00"

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

define i32 @main() {
  %t3 = call ptr @malloc(i64 16)
  store ptr @.str0, ptr %t3, align 8
  %t7 = getelementptr inbounds %String, ptr %t3, i64 0, i32 1
  store i64 4, ptr %t7, align 4
  %t10 = alloca %st_Nomade, align 8
  %t11 = call ptr @createString(ptr null)
  store ptr %t11, ptr %t10, align 8
  %t13 = getelementptr inbounds %st_Nomade, ptr %t10, i64 0, i32 1
  store i32 0, ptr %t13, align 4
  store ptr %t3, ptr %t10, align 8
  %t18 = getelementptr inbounds %st_Nomade, ptr %t10, i64 0, i32 1
  store i32 1, ptr %t18, align 4
  %puts = call i32 @puts(ptr nonnull dereferenceable(1) @.str1)
  %t23 = load ptr, ptr %t10, align 8
  call void @printString(ptr %t23)
  %t24 = getelementptr inbounds %st_Nomade, ptr %t10, i64 0, i32 1
  %t25 = load i32, ptr %t24, align 4
  %1 = call i32 (ptr, ...) @printf(ptr noundef nonnull dereferenceable(1) @.strInt, i32 %t25)
  %t26 = alloca %Pessoa, align 8
  %t28 = call ptr @createString(ptr nonnull @.str2)
  store ptr %t28, ptr %t26, align 8
  %t31 = getelementptr inbounds %Pessoa, ptr %t26, i64 0, i32 1
  store i32 10, ptr %t31, align 4
  %t33 = call ptr @createString(ptr null)
  %puts1 = call i32 @puts(ptr nonnull dereferenceable(1) @.str3)
  %t40 = call ptr @createString(ptr nonnull @.str4)
  call void @printString(ptr %t40)
  %2 = call i32 (ptr, ...) @printf(ptr noundef nonnull dereferenceable(1) @.strInt, i32 16)
  %t51 = call ptr @createString(ptr nonnull @.str5)
  %puts2 = call i32 @puts(ptr nonnull dereferenceable(1) @.str6)
  %t57 = load ptr, ptr %t26, align 8
  call void @printString(ptr %t57)
  %t58 = getelementptr inbounds %Pessoa, ptr %t26, i64 0, i32 1
  %t59 = load i32, ptr %t58, align 4
  %3 = call i32 (ptr, ...) @printf(ptr noundef nonnull dereferenceable(1) @.strInt, i32 %t59)
  %4 = call i32 @getchar()
  ret i32 0
}

; Function Attrs: nofree nounwind
declare noundef i32 @puts(ptr nocapture noundef readonly) #0

attributes #0 = { nofree nounwind }
