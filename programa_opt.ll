; ModuleID = 'programa.ll'
source_filename = "programa.ll"

%String = type { ptr, i64 }

@.strInt = private constant [4 x i8] c"%d\0A\00"
@.strDouble = private constant [4 x i8] c"%f\0A\00"
@.strStr = private constant [4 x i8] c"%s\0A\00"
@.str0 = private constant [11 x i8] c"hello guys\00"
@.str1 = private constant [6 x i8] c"teste\00"
@.str2 = private constant [7 x i8] c"halley\00"
@.str3 = private constant [6 x i8] c"misty\00"
@.str4 = private constant [4 x i8] c"hal\00"
@.str5 = private constant [23 x i8] c"pos 0 e igual a halley\00"
@.str6 = private constant [14 x i8] c"nao era igual\00"

declare i32 @printf(ptr, ...)

declare i32 @getchar()

declare void @printString(ptr)

declare ptr @malloc(i64)

declare void @setString(ptr, ptr)

declare ptr @createString(ptr)

declare ptr @arraylist_create(i64)

declare void @clearList(ptr)

declare void @freeList(ptr)

declare i1 @strcmp_eq(ptr, ptr)

declare i1 @strcmp_neq(ptr, ptr)

declare void @arraylist_add_string(ptr, ptr)

declare void @arraylist_addAll_string(ptr, ptr, i64)

declare void @arraylist_print_string(ptr)

declare void @arraylist_add_String(ptr, ptr)

declare void @arraylist_addAll_String(ptr, ptr, i64)

declare void @removeItem(ptr, i64)

declare ptr @getItem(ptr, i64)

define void @listas(ptr %list) {
entry:
  %t2 = call ptr @createString(ptr nonnull @.str0)
  call void @arraylist_add_String(ptr %list, ptr %t2)
  ret void
}

define ptr @hi() {
entry:
  %t5 = alloca %String, align 8
  store ptr @.str1, ptr %t5, align 8
  %t7 = getelementptr inbounds %String, ptr %t5, i64 0, i32 1
  store i64 5, ptr %t7, align 4
  ret ptr %t5
}

define i32 @main() {
  %t8 = call ptr @arraylist_create(i64 4)
  call void @listas(ptr %t8)
  call void @arraylist_print_string(ptr %t8)
  %t13 = call ptr @hi()
  call void @printString(ptr %t13)
  %t14 = call ptr @malloc(i64 16)
  store ptr @.str2, ptr %t14, align 8
  %t18 = getelementptr inbounds %String, ptr %t14, i64 0, i32 1
  store i64 6, ptr %t18, align 4
  %t19 = call ptr @malloc(i64 16)
  store ptr @.str3, ptr %t19, align 8
  %t23 = getelementptr inbounds %String, ptr %t19, i64 0, i32 1
  store i64 5, ptr %t23, align 4
  %t24 = call ptr @arraylist_create(i64 4)
  call void @arraylist_add_String(ptr %t24, ptr %t14)
  call void @arraylist_add_String(ptr %t24, ptr nonnull %t19)
  %t32 = call ptr @getItem(ptr %t24, i64 0)
  %t34 = call ptr @createString(ptr nonnull @.str4)
  %t36 = call ptr @createString(ptr %t32)
  %t37 = call i1 @strcmp_eq(ptr %t36, ptr %t34)
  br i1 %t37, label %then_0, label %else_0

then_0:                                           ; preds = %0
  %puts3 = call i32 @puts(ptr nonnull dereferenceable(1) @.str5)
  br label %endif_0

else_0:                                           ; preds = %0
  %puts = call i32 @puts(ptr nonnull dereferenceable(1) @.str6)
  br label %endif_0

endif_0:                                          ; preds = %else_0, %then_0
  call void @freeList(ptr %t8)
  call void @freeList(ptr %t24)
  %1 = call i32 @getchar()
  ret i32 0
}

; Function Attrs: nofree nounwind
declare noundef i32 @puts(ptr nocapture noundef readonly) #0

attributes #0 = { nofree nounwind }
