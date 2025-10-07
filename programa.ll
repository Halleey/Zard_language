; === Função: factorial ===
define i32 @math_factorial(i32 %n) {
entry:
  %n_addr = alloca i32
  store i32 %n, i32* %n_addr
;;VAL:%n_addr;;TYPE:i32
  %t0 = load i32, i32* %n_addr
;;VAL:%t0;;TYPE:i32

  %t1 = add i32 0, 0
;;VAL:%t1;;TYPE:i32

  %t2 = icmp eq i32 %t0, %t1
;;VAL:%t2;;TYPE:i1

  br i1 %t2, label %then_0, label %endif_0
then_0:
  %t3 = add i32 0, 1
;;VAL:%t3;;TYPE:i32
  ret i32 %t3
  br label %endif_0
endif_0:
  %t4 = load i32, i32* %n_addr
;;VAL:%t4;;TYPE:i32

  %t5 = load i32, i32* %n_addr
;;VAL:%t5;;TYPE:i32

  %t6 = add i32 0, 1
;;VAL:%t6;;TYPE:i32

  %t7 = sub i32 %t5, %t6
;;VAL:%t7;;TYPE:i32
  %t8 = call i32 @math_factorial(i32 %t7)
;;VAL:%t8;;TYPE:i32

  %t9 = mul i32 %t4, %t8
;;VAL:%t9;;TYPE:i32
  ret i32 %t9
}


; === Função: tes ===
define void @math_tes(i8* %list) {
entry:
  %list_addr = alloca i8*
  store i8* %list, i8** %list_addr
;;VAL:%list_addr;;TYPE:i8*
  %t10 = load i8*, i8** %list_addr
;;VAL:%t10;;TYPE:i8*
  %t12 = bitcast i8* %t10 to %ArrayList*
  %t11 = bitcast [21 x i8]* @.str0 to i8*
;;VAL:%t11;;TYPE:i8*
  call void @arraylist_add_string(%ArrayList* %t12, i8* getelementptr ([21 x i8], [21 x i8]* @.str0, i32 0, i32 0))
;;VAL:%t12;;TYPE:%ArrayList*
  ret void
}


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
    declare void @arraylist_add_string(%ArrayList*, i8*)
    declare void @arraylist_addAll_string(%ArrayList*, i8**, i64)
    declare void @arraylist_print_string(%ArrayList*)
    declare void @arraylist_add_String(%ArrayList*, %String*)
    declare void @arraylist_addAll_String(%ArrayList*, %String**, i64)

@.str0 = private constant [21 x i8] c"teste um hello world\00"

define i32 @main() {
  ; VariableDeclarationNode
  %is = alloca %struct.ArrayListBool*
;;VAL:%is;;TYPE:%struct.ArrayListBool*
  %t13 = call %struct.ArrayListBool* @arraylist_create_bool(i64 4)
  %t14 = add i1 0, 1
;;VAL:%t14;;TYPE:i1
  call void @arraylist_add_bool(%struct.ArrayListBool* %t13, i1%t14)
  %t15 = add i1 0, 0
;;VAL:%t15;;TYPE:i1
  call void @arraylist_add_bool(%struct.ArrayListBool* %t13, i1%t15)
;;VAL:%t13;;TYPE:%struct.ArrayListBool*
  store %struct.ArrayListBool* %t13, %struct.ArrayListBool** %is
  ; PrintNode
  %t16 = load %struct.ArrayListBool*, %struct.ArrayListBool** %is
  call void @arraylist_print_bool(%struct.ArrayListBool* %t16)
  ; === Free das listas alocadas ===
  %t17 = load %struct.ArrayListBool*, %struct.ArrayListBool** %is
  call void @arraylist_free_bool(%struct.ArrayListBool* %t17)
  call i32 @getchar()
  ret i32 0
}
