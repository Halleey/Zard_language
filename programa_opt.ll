; ModuleID = 'programa.ll'
source_filename = "programa.ll"

%Set = type { ptr }

@.strTrue = private constant [6 x i8] c"true\0A\00"
@.strFalse = private constant [7 x i8] c"false\0A\00"
@.strInt = private constant [4 x i8] c"%d\0A\00"
@.strDouble = private constant [4 x i8] c"%f\0A\00"
@.strStr = private constant [4 x i8] c"%s\0A\00"
@.str0 = private constant [6 x i8] c"teste\00"
@.str1 = private constant [6 x i8] c"hello\00"
@.str2 = private constant [3 x i8] c"30\00"
@.str3 = private constant [18 x i8] c"Elementos do Set:\00"
@.str4 = private constant [9 x i8] c"Tamanho:\00"
@.str5 = private constant [19 x i8] c"Primeiro elemento:\00"
@.str6 = private constant [25 x i8] c"Ap\C3\B3s remover \C3\ADndice 1:\00"

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

declare void @arraylist_add_string(ptr, ptr)

declare void @arraylist_addAll_string(ptr, ptr, i64)

declare void @arraylist_print_string(ptr)

declare void @arraylist_add_String(ptr, ptr)

declare void @arraylist_addAll_String(ptr, ptr, i64)

declare void @removeItem(ptr, i64)

declare ptr @getItem(ptr, i64)

define void @print_Set(ptr %raw) {
entry:
  ret void
}

define i32 @main() {
  %t0 = alloca %Set, align 8
  %t1 = call ptr @arraylist_create(i64 10)
  store ptr %t1, ptr %t0, align 8
  %t7 = call ptr @createString(ptr nonnull @.str0)
  call void @arraylist_add_String(ptr %t1, ptr %t7)
  %t112 = load ptr, ptr %t0, align 8
  %t13 = call ptr @createString(ptr nonnull @.str1)
  call void @arraylist_add_String(ptr %t112, ptr %t13)
  %t173 = load ptr, ptr %t0, align 8
  %t19 = call ptr @createString(ptr nonnull @.str2)
  call void @arraylist_add_String(ptr %t173, ptr %t19)
  %puts = call i32 @puts(ptr nonnull dereferenceable(1) @.str3)
  %t25 = load ptr, ptr %t0, align 8
  call void @arraylist_print_string(ptr %t25)
  %puts4 = call i32 @puts(ptr nonnull dereferenceable(1) @.str4)
  %t295 = load ptr, ptr %t0, align 8
  %t31 = call i32 @length(ptr %t295)
  %1 = call i32 (ptr, ...) @printf(ptr noundef nonnull dereferenceable(1) @.strInt, i32 %t31)
  %puts6 = call i32 @puts(ptr nonnull dereferenceable(1) @.str5)
  %t38 = call ptr @arraylist_get_ptr(ptr %t0, i64 0)
  call void @printString(ptr %t38)
  %t42 = load ptr, ptr %t0, align 8
  call void @removeItem(ptr %t42, i64 1)
  %puts7 = call i32 @puts(ptr nonnull dereferenceable(1) @.str6)
  %t49 = load ptr, ptr %t0, align 8
  call void @arraylist_print_string(ptr %t49)
  %2 = call i32 @getchar()
  ret i32 0
}

; Function Attrs: nofree nounwind
declare noundef i32 @puts(ptr nocapture noundef readonly) #0

attributes #0 = { nofree nounwind }
