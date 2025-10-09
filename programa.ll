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
      %struct.ArrayListBool = type { i1*, i64, i64 }
      declare %struct.ArrayListBool* @arraylist_create_bool(i64)
      declare void @arraylist_add_bool(%struct.ArrayListBool*, i1)
      declare void @arraylist_addAll_bool(%struct.ArrayListBool*, i1*, i64)
      declare void @arraylist_print_bool(%struct.ArrayListBool*)
      declare void @arraylist_clear_bool(%struct.ArrayListBool*)
      declare void @arraylist_free_bool(%struct.ArrayListBool*)


define i32 @main() {
  ; VariableDeclarationNode
  %list = alloca %struct.ArrayListBool*
;;VAL:%list;;TYPE:%struct.ArrayListBool*
  %t0 = call %struct.ArrayListBool* @arraylist_create_bool(i64 4)
  %t1 = alloca i1, i64 2
  %t2 = add i1 0, 1
;;VAL:%t2;;TYPE:i1
  %t3 = getelementptr inbounds i1, i1* %t1, i64 0
  store i1 %t2, i1* %t3
  %t4 = add i1 0, 0
;;VAL:%t4;;TYPE:i1
  %t5 = getelementptr inbounds i1, i1* %t1, i64 1
  store i1 %t4, i1* %t5
  call void @arraylist_addAll_bool(%struct.ArrayListBool* %t0, i1* %t1, i64 2)
;;VAL:%t0;;TYPE:%struct.ArrayListBool*
  store %struct.ArrayListBool* %t0, %struct.ArrayListBool** %list
  ; ListAddNode
  %t8 = load %struct.ArrayListBool*, %struct.ArrayListBool** %list
;;VAL:%t8;;TYPE:%struct.ArrayListBool*
  %t9 = add i1 0, 1
;;VAL:%t9;;TYPE:i1
  call void @arraylist_add_bool(%struct.ArrayListBool* %t8, i1%t9)
;;VAL:%t8;;TYPE:struct.ArrayListBool*
  ; PrintNode
  %t10 = load %struct.ArrayListBool*, %struct.ArrayListBool** %list
  call void @arraylist_print_bool(%struct.ArrayListBool* %t10)
  ; === Free das listas alocadas ===
  %t11 = load %struct.ArrayListBool*, %struct.ArrayListBool** %list
  call void @arraylist_free_bool(%struct.ArrayListBool* %t11)
  call i32 @getchar()
  ret i32 0
}
