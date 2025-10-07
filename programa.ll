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
    %struct.ArrayListInt = type { i32*, i64, i64 }
    declare %struct.ArrayListInt* @arraylist_create_int(i64)
    declare void @arraylist_add_int(%struct.ArrayListInt*, i32)
    declare void @arraylist_addAll_int(%struct.ArrayListInt*, i32*, i64)
    declare void @arraylist_print_int(%struct.ArrayListInt*)
    declare void @arraylist_clear_int(%struct.ArrayListInt*)
    declare void @arraylist_free_int(%struct.ArrayListInt*)
    declare i32  @arraylist_get_int(%struct.ArrayListInt*, i64, i32*)
    declare void @arraylist_remove_int(%struct.ArrayListInt*, i64)
    declare void @arraylist_add_string(%ArrayList*, i8*)
    declare void @arraylist_addAll_string(%ArrayList*, i8**, i64)
    declare void @arraylist_print_string(%ArrayList*)
    declare void @arraylist_add_String(%ArrayList*, %String*)
    declare void @arraylist_addAll_String(%ArrayList*, %String**, i64)
    declare i8* @getItem(%ArrayList*, i64)

@.str0 = private constant [21 x i8] c"teste um hello world\00"
@.str1 = private constant [5 x i8] c"zard\00"
@.str2 = private constant [5 x i8] c"test\00"

define i32 @main() {
  ; VariableDeclarationNode
  %is = alloca %struct.ArrayListBool*
;;VAL:%is;;TYPE:%struct.ArrayListBool*
  %t13 = call %struct.ArrayListBool* @arraylist_create_bool(i64 4)
  %t14 = alloca i1, i64 2
  %t15 = add i1 0, 1
;;VAL:%t15;;TYPE:i1
  %t16 = getelementptr inbounds i1, i1* %t14, i64 0
  store i1 %t15, i1* %t16
  %t17 = add i1 0, 0
;;VAL:%t17;;TYPE:i1
  %t18 = getelementptr inbounds i1, i1* %t14, i64 1
  store i1 %t17, i1* %t18
  call void @arraylist_addAll_bool(%struct.ArrayListBool* %t13, i1* %t14, i64 2)
;;VAL:%t13;;TYPE:%struct.ArrayListBool*
  store %struct.ArrayListBool* %t13, %struct.ArrayListBool** %is
  ; PrintNode
  %t19 = load %struct.ArrayListBool*, %struct.ArrayListBool** %is
  call void @arraylist_print_bool(%struct.ArrayListBool* %t19)
  ; VariableDeclarationNode
  %nomes = alloca i8*
;;VAL:%nomes;;TYPE:i8*
  %t20 = call i8* @arraylist_create(i64 4)
  %t21 = bitcast i8* %t20 to %ArrayList*
  %t22 = bitcast [5 x i8]* @.str1 to i8*
;;VAL:%t22;;TYPE:i8*
  call void @arraylist_add_string(%ArrayList* %t21, i8* %t22)
  %t23 = bitcast [5 x i8]* @.str2 to i8*
;;VAL:%t23;;TYPE:i8*
  call void @arraylist_add_string(%ArrayList* %t21, i8* %t23)
;;VAL:%t20;;TYPE:i8*
  store i8* %t20, i8** %nomes
  ; PrintNode
  %t24 = load i8*, i8** %nomes
  %t25 = bitcast i8* %t24 to %ArrayList*
  %t26 = add i32 0, 1
  %t27 = zext i32 %t26 to i64
  %t28 = call i8* @getItem(%ArrayList* %t25, i64 %t27)
  call i32 (i8*, ...) @printf(i8* getelementptr ([4 x i8], [4 x i8]* @.strStr, i32 0, i32 0), i8* %t28)
  ; VariableDeclarationNode
  %numbers = alloca %struct.ArrayListInt*
;;VAL:%numbers;;TYPE:%struct.ArrayListInt*
  %t29 = call %struct.ArrayListInt* @arraylist_create_int(i64 4)
  %t30 = alloca i32, i64 4
  %t31 = add i32 0, 3
;;VAL:%t31;;TYPE:i32
  %t32 = getelementptr inbounds i32, i32* %t30, i64 0
  store i32 %t31, i32* %t32
  %t33 = add i32 0, 4
;;VAL:%t33;;TYPE:i32
  %t34 = getelementptr inbounds i32, i32* %t30, i64 1
  store i32 %t33, i32* %t34
  %t35 = add i32 0, 5
;;VAL:%t35;;TYPE:i32
  %t36 = getelementptr inbounds i32, i32* %t30, i64 2
  store i32 %t35, i32* %t36
  %t37 = add i32 0, 6
;;VAL:%t37;;TYPE:i32
  %t38 = getelementptr inbounds i32, i32* %t30, i64 3
  store i32 %t37, i32* %t38
  call void @arraylist_addAll_int(%struct.ArrayListInt* %t29, i32* %t30, i64 4)
;;VAL:%t29;;TYPE:%struct.ArrayListInt*
  store %struct.ArrayListInt* %t29, %struct.ArrayListInt** %numbers
  ; ListRemoveNode
  %t40 = load %struct.ArrayListInt*, %struct.ArrayListInt** %numbers
  %t41 = add i32 0, 1
  %t42 = zext i32 %t41 to i64
  call void @arraylist_remove_int(%struct.ArrayListInt* %t40, i64 %t42)
  ; PrintNode
  %t43 = load %struct.ArrayListInt*, %struct.ArrayListInt** %numbers
  call void @arraylist_print_int(%struct.ArrayListInt* %t43)
  ; VariableDeclarationNode
  %tes = alloca %struct.ArrayListInt*
;;VAL:%tes;;TYPE:%struct.ArrayListInt*
  %t44 = call %struct.ArrayListInt* @arraylist_create_int(i64 4)
;;VAL:%t44;;TYPE:%struct.ArrayListInt*
  store %struct.ArrayListInt* %t44, %struct.ArrayListInt** %tes
  ; ListAddNode
  %t47 = load %struct.ArrayListInt*, %struct.ArrayListInt** %tes
;;VAL:%t47;;TYPE:%struct.ArrayListInt*
  %t48 = add i32 0, 3
;;VAL:%t48;;TYPE:i32
  call void @arraylist_add_int(%struct.ArrayListInt* %t47, i32 %t48)
;;VAL:%t47;;TYPE:%struct.ArrayListInt*
  ; PrintNode
  %t49 = load %struct.ArrayListInt*, %struct.ArrayListInt** %tes
  call void @arraylist_print_int(%struct.ArrayListInt* %t49)
  ; VariableDeclarationNode
  %x = alloca i32
;;VAL:%x;;TYPE:i32
  %t50 = add i32 0, 99
;;VAL:%t50;;TYPE:i32
  store i32 %t50, i32* %x
  ; VariableDeclarationNode
  %z = alloca i32
;;VAL:%z;;TYPE:i32
  %t51 = add i32 0, 33
;;VAL:%t51;;TYPE:i32
  store i32 %t51, i32* %z
  ; ListAddAllNode
  %t54 = load %struct.ArrayListInt*, %struct.ArrayListInt** %numbers
;;VAL:%t54;;TYPE:%struct.ArrayListInt*
  %t55 = alloca i32, i64 5
  %t56 = add i32 0, 3
;;VAL:%t56;;TYPE:i32
  %t57 = getelementptr inbounds i32, i32* %t55, i64 0
  store i32 %t56, i32* %t57
  %t58 = add i32 0, 4
;;VAL:%t58;;TYPE:i32
  %t59 = getelementptr inbounds i32, i32* %t55, i64 1
  store i32 %t58, i32* %t59
  %t60 = add i32 0, 5
;;VAL:%t60;;TYPE:i32
  %t61 = getelementptr inbounds i32, i32* %t55, i64 2
  store i32 %t60, i32* %t61
  %t62 = load i32, i32* %x
;;VAL:%t62;;TYPE:i32
  %t63 = getelementptr inbounds i32, i32* %t55, i64 3
  store i32 %t62, i32* %t63
  %t64 = load i32, i32* %z
;;VAL:%t64;;TYPE:i32
  %t65 = getelementptr inbounds i32, i32* %t55, i64 4
  store i32 %t64, i32* %t65
  call void @arraylist_addAll_int(%struct.ArrayListInt* %t54, i32* %t55, i64 5)
;;VAL:%t54;;TYPE:%struct.ArrayListInt*
  ; PrintNode
  %t66 = load %struct.ArrayListInt*, %struct.ArrayListInt** %numbers
  call void @arraylist_print_int(%struct.ArrayListInt* %t66)
  ; === Free das listas alocadas ===
  %t67 = load %struct.ArrayListInt*, %struct.ArrayListInt** %tes
  call void @arraylist_free_int(%struct.ArrayListInt* %t67)
  %t68 = load i8*, i8** %nomes
  %t69 = bitcast i8* %t68 to %ArrayList*
  call void @freeList(%ArrayList* %t69)
  %t70 = load %struct.ArrayListInt*, %struct.ArrayListInt** %numbers
  call void @arraylist_free_int(%struct.ArrayListInt* %t70)
  %t71 = load %struct.ArrayListBool*, %struct.ArrayListBool** %is
  call void @arraylist_free_bool(%struct.ArrayListBool* %t71)
  call i32 @getchar()
  ret i32 0
}
