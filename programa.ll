    declare i32 @printf(i8*, ...)
    declare i32 @getchar()
    declare void @printString(%String*)
    declare i8* @malloc(i64)
    declare void @setString(%String*, i8*)

    @.strInt = private constant [4 x i8] c"%d\0A\00"
    @.strDouble = private constant [4 x i8] c"%f\0A\00"
    @.strStr = private constant [4 x i8] c"%s\0A\00"

    declare i8* @arraylist_create(i64)
    declare void @clearList(%ArrayList*)
    declare void @freeList(%ArrayList*)

    %String = type { i8*, i64 }
    %ArrayList = type opaque
    declare void @arraylist_add_string(%ArrayList*, i8*)
    declare void @arraylist_addAll_string(%ArrayList*, i8**, i64)
    declare void @arraylist_print_string(%ArrayList*)
    declare void @arraylist_add_String(%ArrayList*, %String*)
    declare void @arraylist_addAll_String(%ArrayList*, %String**, i64)
    declare i8* @getItem(%ArrayList*, i64)

@.str0 = private constant [6 x i8] c"teste\00"
@.str1 = private constant [5 x i8] c"zard\00"

define i32 @main() {
  ; VariableDeclarationNode
  %x = alloca %String*
;;VAL:%x;;TYPE:%String*
  %t0 = call i8* @malloc(i64 ptrtoint (%String* getelementptr (%String, %String* null, i32 1) to i64))
  %t1 = bitcast i8* %t0 to %String*
  %t2 = bitcast [6 x i8]* @.str0 to i8*
  %t3 = getelementptr inbounds %String, %String* %t1, i32 0, i32 0
  store i8* %t2, i8** %t3
  %t4 = getelementptr inbounds %String, %String* %t1, i32 0, i32 1
  store i64 5, i64* %t4
  store %String* %t1, %String** %x
  ; VariableDeclarationNode
  %list = alloca i8*
;;VAL:%list;;TYPE:i8*
  %t5 = call i8* @arraylist_create(i64 4)
  %t6 = bitcast i8* %t5 to %ArrayList*
  %t7 = bitcast [5 x i8]* @.str1 to i8*
;;VAL:%t7;;TYPE:i8*
  call void @arraylist_add_string(%ArrayList* %t6, i8* %t7)
;;VAL:%t5;;TYPE:i8*
  store i8* %t5, i8** %list
  ; ListAddNode
  %t8 = load i8*, i8** %list
;;VAL:%t8;;TYPE:i8*
  %t10 = bitcast i8* %t8 to %ArrayList*
  %t9 = load %String*, %String** %x
;;VAL:%t9;;TYPE:%String*
  call void @arraylist_add_String(%ArrayList* %t10, %String* %t9)
;;VAL:%t10;;TYPE:%ArrayList*
  ; PrintNode
  %t11 = load i8*, i8** %list
  %t12 = bitcast i8* %t11 to %ArrayList*
  call void @arraylist_print_string(%ArrayList* %t12)
  ; === Free das listas alocadas ===
  %t13 = load i8*, i8** %list
  %t14 = bitcast i8* %t13 to %ArrayList*
  call void @freeList(%ArrayList* %t14)
  call i32 @getchar()
  ret i32 0
}
