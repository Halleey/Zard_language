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
      declare void @arraylist_remove_bool(%struct.ArrayListBool*, i64)
      declare void @arraylist_free_bool(%struct.ArrayListBool*)


define i32 @main() {
  ; VariableDeclarationNode
  %list = alloca %struct.ArrayListBool*
;;VAL:%list;;TYPE:%struct.ArrayListBool*
  %t0 = call %struct.ArrayListBool* @arraylist_create_bool(i64 4)
;;VAL:%t0;;TYPE:%struct.ArrayListBool*
  store %struct.ArrayListBool* %t0, %struct.ArrayListBool** %list
  ; ListAddAllNode
  %t3 = load %struct.ArrayListBool*, %struct.ArrayListBool** %list
;;VAL:%t3;;TYPE:%struct.ArrayListBool*
  %t4 = alloca i1, i64 3
  %t5 = add i1 0, 1
;;VAL:%t5;;TYPE:i1
  %t6 = getelementptr inbounds i1, i1* %t4, i64 0
  store i1 %t5, i1* %t6
  %t7 = add i1 0, 0
;;VAL:%t7;;TYPE:i1
  %t8 = getelementptr inbounds i1, i1* %t4, i64 1
  store i1 %t7, i1* %t8
  %t9 = add i1 0, 1
;;VAL:%t9;;TYPE:i1
  %t10 = getelementptr inbounds i1, i1* %t4, i64 2
  store i1 %t9, i1* %t10
  call void @arraylist_addAll_bool(%struct.ArrayListBool* %t3, i1* %t4, i64 3)
;;VAL:%t3;;TYPE:%struct.ArrayListBool*
  ; ListRemoveNode
  %t12 = load %struct.ArrayListBool*, %struct.ArrayListBool** %list
  %t13 = add i32 0, 0
  %t14 = zext i32 %t13 to i64
  call void @arraylist_remove_bool(%struct.ArrayListBool* %t12, i64 %t14)
  ; PrintNode
  %t15 = load %struct.ArrayListBool*, %struct.ArrayListBool** %list
  call void @arraylist_print_bool(%struct.ArrayListBool* %t15)
  ; === Free das listas alocadas ===
  %t16 = load %struct.ArrayListBool*, %struct.ArrayListBool** %list
  call void @arraylist_free_bool(%struct.ArrayListBool* %t16)
  call i32 @getchar()
  ret i32 0
}
