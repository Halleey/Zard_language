    declare i32 @printf(i8*, ...)
    declare i32 @getchar()
    declare void @printString(%String*)
    declare i8* @malloc(i64)

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

@.str0 = private constant [6 x i8] c"teste\00"

define i32 @main() {
  ; VariableDeclarationNode
  %nomes = alloca i8*
;;VAL:%nomes;;TYPE:i8*
  %t0 = call i8* @arraylist_create(i64 4)
  %t1 = bitcast i8* %t0 to %ArrayList*
;;VAL:%t0;;TYPE:i8*
  store i8* %t0, i8** %nomes
  ; ListAddNode
  %t2 = load i8*, i8** %nomes
;;VAL:%t2;;TYPE:i8*
  %t3 = bitcast i8* %t2 to %ArrayList*
  %t4 = bitcast [6 x i8]* @.str0 to i8*
;;VAL:%t4;;TYPE:i8*
  call void @arraylist_add_string(%ArrayList* %t3, i8* getelementptr ([6 x i8], [6 x i8]* @.str0, i32 0, i32 0))
;;VAL:%t3;;TYPE:%ArrayList*
  ; PrintNode
  %t5 = load i8*, i8** %nomes
  %t6 = bitcast i8* %t5 to %ArrayList*
  call void @arraylist_print_string(%ArrayList* %t6)
  ; === Free das listas alocadas ===
  %t7 = load i8*, i8** %nomes
  %t8 = bitcast i8* %t7 to %ArrayList*
  call void @freeList(%ArrayList* %t8)
  call i32 @getchar()
  ret i32 0
}
