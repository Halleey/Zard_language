; ModuleID = 'programa.ll'
source_filename = "programa.ll"

%String = type { ptr, i64 }

@.strTrue = private constant [6 x i8] c"true\0A\00"
@.strFalse = private constant [7 x i8] c"false\0A\00"
@.strInt = private constant [4 x i8] c"%d\0A\00"
@.strDouble = private constant [4 x i8] c"%f\0A\00"
@.strStr = private constant [4 x i8] c"%s\0A\00"
@.str0 = private constant [5 x i8] c"zard\00"
@.str1 = private constant [10 x i8] c"era igual\00"

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

define i32 @main() {
  %t0 = call ptr @malloc(i64 16)
  store ptr @.str0, ptr %t0, align 8
  %t4 = getelementptr inbounds %String, ptr %t0, i64 0, i32 1
  store i64 4, ptr %t4, align 4
  %t5 = call ptr @arraylist_create(i64 4)
  call void @arraylist_add_String(ptr %t5, ptr nonnull %t0)
  %t12 = call ptr @arraylist_get_ptr(ptr %t5, i64 0)
  %t14 = call ptr @createString(ptr nonnull @.str0)
  %t16 = call ptr @createString(ptr %t12)
  %t17 = call i1 @strcmp_eq(ptr %t16, ptr %t14)
  br i1 %t17, label %then_0, label %endif_0

then_0:                                           ; preds = %0
  %puts = call i32 @puts(ptr nonnull dereferenceable(1) @.str1)
  br label %endif_0

endif_0:                                          ; preds = %then_0, %0
  call void @arraylist_print_string(ptr %t5)
  call void @freeList(ptr %t5)
  %1 = call i32 @getchar()
  ret i32 0
}

; Function Attrs: nofree nounwind
declare noundef i32 @puts(ptr nocapture noundef readonly) #0

attributes #0 = { nofree nounwind }
