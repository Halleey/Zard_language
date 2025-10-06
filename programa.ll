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
    %struct.ArrayListInt = type { i32*, i64, i64 }
    declare %struct.ArrayListInt* @arraylist_create_int(i64)
    declare void @arraylist_add_int(%struct.ArrayListInt*, i32)
    declare void @arraylist_addAll_int(%struct.ArrayListInt*, i32*, i64)
    declare void @arraylist_print_int(%struct.ArrayListInt*)
    declare void @arraylist_free_int(%struct.ArrayListInt*)


define i32 @main() {
  ; VariableDeclarationNode
  %nume = alloca %struct.ArrayListInt*
;;VAL:%nume;;TYPE:%struct.ArrayListInt*
  %t0 = call %struct.ArrayListInt* @arraylist_create_int(i64 4)
  %t1 = add i32 0, 3
;;VAL:%t1;;TYPE:i32
  call void @arraylist_add_int(%struct.ArrayListInt* %t0, i32 %t1)
;;VAL:%t0;;TYPE:%struct.ArrayListInt*
  store %struct.ArrayListInt* %t0, %struct.ArrayListInt** %nume
  ; PrintNode
  %t2 = load %struct.ArrayListInt*, %struct.ArrayListInt** %nume
  call void @arraylist_print_int(%struct.ArrayListInt* %t2)
  ; === Free das listas alocadas ===
  %t3 = load %struct.ArrayListInt*, %struct.ArrayListInt** %nume
  call void @arraylist_free_int(%struct.ArrayListInt* %t3)
  call i32 @getchar()
  ret i32 0
}
