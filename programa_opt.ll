; ModuleID = 'programa.ll'
source_filename = "programa.ll"

%Pessoa = type { i32, ptr, i1 }

@.strTrue = private constant [6 x i8] c"true\0A\00"
@.strFalse = private constant [7 x i8] c"false\0A\00"
@.strInt = private constant [4 x i8] c"%d\0A\00"
@.strDouble = private constant [4 x i8] c"%f\0A\00"
@.strStr = private constant [4 x i8] c"%s\0A\00"
@.str0 = private constant [7 x i8] c"halley\00"
@str = private unnamed_addr constant [6 x i8] c"false\00", align 1
@str.1 = private unnamed_addr constant [5 x i8] c"true\00", align 1
@str.2 = private unnamed_addr constant [6 x i8] c"false\00", align 1
@str.3 = private unnamed_addr constant [5 x i8] c"true\00", align 1

declare i32 @printf(ptr, ...)

declare i32 @getchar()

declare void @printString(ptr)

declare ptr @malloc(i64)

declare void @setString(ptr, ptr)

declare ptr @createString(ptr)

declare i1 @strcmp_eq(ptr, ptr)

declare i1 @strcmp_neq(ptr, ptr)

define i32 @main() {
  %t0 = alloca %Pessoa, align 8
  store i32 19, ptr %t0, align 4
  %t4 = call ptr @createString(ptr nonnull @.str0)
  %t5 = getelementptr inbounds %Pessoa, ptr %t0, i64 0, i32 1
  store ptr %t4, ptr %t5, align 8
  %t6 = getelementptr inbounds %Pessoa, ptr %t0, i64 0, i32 2
  store i1 false, ptr %t6, align 1
  %t7 = alloca %Pessoa, align 8
  store i32 0, ptr %t7, align 4
  %t9 = call ptr @createString(ptr null)
  %t10 = getelementptr inbounds %Pessoa, ptr %t7, i64 0, i32 1
  store ptr %t9, ptr %t10, align 8
  %t11 = getelementptr inbounds %Pessoa, ptr %t7, i64 0, i32 2
  store i1 false, ptr %t11, align 1
  %t14 = load i32, ptr %t0, align 4
  %1 = call i32 (ptr, ...) @printf(ptr noundef nonnull dereferenceable(1) @.strInt, i32 %t14)
  %t15 = getelementptr inbounds %Pessoa, ptr %t0, i64 0, i32 1
  %t16 = load ptr, ptr %t15, align 8
  call void @printString(ptr %t16)
  %t17 = getelementptr inbounds %Pessoa, ptr %t0, i64 0, i32 2
  %t18 = load i1, ptr %t17, align 1
  br i1 %t18, label %bool_true_0, label %bool_false_1

bool_true_0:                                      ; preds = %0
  %puts1 = call i32 @puts(ptr nonnull dereferenceable(1) @str.1)
  br label %bool_end_2

bool_false_1:                                     ; preds = %0
  %puts = call i32 @puts(ptr nonnull dereferenceable(1) @str)
  br label %bool_end_2

bool_end_2:                                       ; preds = %bool_false_1, %bool_true_0
  %t21 = load i32, ptr %t7, align 4
  %2 = call i32 (ptr, ...) @printf(ptr noundef nonnull dereferenceable(1) @.strInt, i32 %t21)
  %t22 = getelementptr inbounds %Pessoa, ptr %t7, i64 0, i32 1
  %t23 = load ptr, ptr %t22, align 8
  call void @printString(ptr %t23)
  %t24 = getelementptr inbounds %Pessoa, ptr %t7, i64 0, i32 2
  %t25 = load i1, ptr %t24, align 1
  br i1 %t25, label %bool_true_3, label %bool_false_4

bool_true_3:                                      ; preds = %bool_end_2
  %puts3 = call i32 @puts(ptr nonnull dereferenceable(1) @str.3)
  br label %bool_end_5

bool_false_4:                                     ; preds = %bool_end_2
  %puts2 = call i32 @puts(ptr nonnull dereferenceable(1) @str.2)
  br label %bool_end_5

bool_end_5:                                       ; preds = %bool_false_4, %bool_true_3
  %3 = call i32 @getchar()
  ret i32 0
}

; Function Attrs: nofree nounwind
declare noundef i32 @puts(ptr nocapture noundef readonly) #0

attributes #0 = { nofree nounwind }
