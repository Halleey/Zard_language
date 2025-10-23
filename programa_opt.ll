; ModuleID = 'programa.ll'
source_filename = "programa.ll"

%Set = type { ptr }

@.strTrue = private constant [6 x i8] c"true\0A\00"
@.strFalse = private constant [7 x i8] c"false\0A\00"
@.strInt = private constant [4 x i8] c"%d\0A\00"
@.strDouble = private constant [4 x i8] c"%f\0A\00"
@.strStr = private constant [4 x i8] c"%s\0A\00"
@.str0 = private constant [18 x i8] c"Elementos do Set:\00"
@.str1 = private constant [9 x i8] c"Tamanho:\00"
@.str2 = private constant [4 x i8] c"get\00"
@.str3 = private constant [7 x i8] c"remove\00"
@.str4 = private constant [13 x i8] c"apos limpeza\00"

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

declare ptr @arraylist_create_bool(i64)

declare void @arraylist_add_bool(ptr, i1)

declare void @arraylist_addAll_bool(ptr, ptr, i64)

declare void @arraylist_print_bool(ptr)

declare void @arraylist_clear_bool(ptr)

declare void @arraylist_remove_bool(ptr, i64)

declare void @arraylist_free_bool(ptr)

declare i1 @arraylist_get_bool(ptr, i64, ptr)

declare i32 @arraylist_size_bool(ptr)

define void @print_Set(ptr %raw) {
entry:
  ret void
}

define i32 @main() {
  %t0 = alloca %Set, align 8
  %t1 = call ptr @arraylist_create_bool(i64 10)
  store ptr %t1, ptr %t0, align 8
  %t10 = alloca [3 x i8], align 1
  store i8 1, ptr %t10, align 1
  %t17 = getelementptr inbounds i8, ptr %t10, i64 1
  store i8 0, ptr %t17, align 1
  %t20 = getelementptr inbounds i8, ptr %t10, i64 2
  store i8 0, ptr %t20, align 1
  call void @arraylist_addAll_bool(ptr %t1, ptr nonnull %t10, i64 3)
  %puts = call i32 @puts(ptr nonnull dereferenceable(1) @.str0)
  %t25 = load ptr, ptr %t0, align 8
  call void @arraylist_print_bool(ptr %t25)
  %puts1 = call i32 @puts(ptr nonnull dereferenceable(1) @.str1)
  %t32 = load ptr, ptr %t0, align 8
  %t33 = call i32 @arraylist_size_bool(ptr %t32)
  %1 = call i32 (ptr, ...) @printf(ptr noundef nonnull dereferenceable(1) @.strInt, i32 %t33)
  %puts2 = call i32 @puts(ptr nonnull dereferenceable(1) @.str2)
  %t40 = load ptr, ptr %t0, align 8
  %t43 = alloca i1, align 1
  %t44 = call i1 @arraylist_get_bool(ptr %t40, i64 0, ptr nonnull %t43)
  %t45 = load i1, ptr %t43, align 1
  %t46 = zext i1 %t45 to i32
  %2 = call i32 (ptr, ...) @printf(ptr noundef nonnull dereferenceable(1) @.strInt, i32 %t46)
  %puts3 = call i32 @puts(ptr nonnull dereferenceable(1) @.str3)
  %t53 = load ptr, ptr %t0, align 8
  call void @arraylist_remove_bool(ptr %t53, i64 0)
  %t59 = load ptr, ptr %t0, align 8
  call void @arraylist_print_bool(ptr %t59)
  %puts4 = call i32 @puts(ptr nonnull dereferenceable(1) @.str4)
  %t635 = load ptr, ptr %t0, align 8
  call void @clearList(ptr %t635)
  %t68 = load ptr, ptr %t0, align 8
  call void @arraylist_print_bool(ptr %t68)
  %3 = call i32 @getchar()
  ret i32 0
}

; Function Attrs: nofree nounwind
declare noundef i32 @puts(ptr nocapture noundef readonly) #0

attributes #0 = { nofree nounwind }
