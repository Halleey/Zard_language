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
  ; VariableDeclarationNode
  %nomes = alloca i8*
;;VAL:%nomes;;TYPE:i8*
  %t17 = call i8* @arraylist_create(i64 4)
  %t18 = bitcast i8* %t17 to %ArrayList*
  %t19 = bitcast [5 x i8]* @.str1 to i8*
;;VAL:%t19;;TYPE:i8*
  call void @arraylist_add_string(%ArrayList* %t18, i8* %t19)
  %t20 = bitcast [5 x i8]* @.str2 to i8*
;;VAL:%t20;;TYPE:i8*
  call void @arraylist_add_string(%ArrayList* %t18, i8* %t20)
;;VAL:%t17;;TYPE:i8*
  store i8* %t17, i8** %nomes
  ; PrintNode
  %t21 = load i8*, i8** %nomes
  %t22 = bitcast i8* %t21 to %ArrayList*
  %t23 = add i32 0, 1
  %t24 = zext i32 %t23 to i64
  %t25 = call i8* @getItem(%ArrayList* %t22, i64 %t24)
  call i32 (i8*, ...) @printf(i8* getelementptr ([4 x i8], [4 x i8]* @.strStr, i32 0, i32 0), i8* %t25)
  ; VariableDeclarationNode
  %numbers = alloca %struct.ArrayListInt*
;;VAL:%numbers;;TYPE:%struct.ArrayListInt*
  %t26 = call %struct.ArrayListInt* @arraylist_create_int(i64 4)
  %t27 = add i32 0, 3
;;VAL:%t27;;TYPE:i32
  call void @arraylist_add_int(%struct.ArrayListInt* %t26, i32 %t27)
  %t28 = add i32 0, 4
;;VAL:%t28;;TYPE:i32
  call void @arraylist_add_int(%struct.ArrayListInt* %t26, i32 %t28)
  %t29 = add i32 0, 5
;;VAL:%t29;;TYPE:i32
  call void @arraylist_add_int(%struct.ArrayListInt* %t26, i32 %t29)
  %t30 = add i32 0, 6
;;VAL:%t30;;TYPE:i32
  call void @arraylist_add_int(%struct.ArrayListInt* %t26, i32 %t30)
;;VAL:%t26;;TYPE:%struct.ArrayListInt*
  store %struct.ArrayListInt* %t26, %struct.ArrayListInt** %numbers
  ; ListRemoveNode
  %t32 = load %struct.ArrayListInt*, %struct.ArrayListInt** %numbers
  %t33 = add i32 0, 1
  %t34 = zext i32 %t33 to i64
  call void @arraylist_remove_int(%struct.ArrayListInt* %t32, i64 %t34)
  ; PrintNode
  %t35 = load %struct.ArrayListInt*, %struct.ArrayListInt** %numbers
  call void @arraylist_print_int(%struct.ArrayListInt* %t35)
  ; VariableDeclarationNode
  %x = alloca i32
;;VAL:%x;;TYPE:i32
  %t36 = add i32 0, 99
;;VAL:%t36;;TYPE:i32
  store i32 %t36, i32* %x
  ; VariableDeclarationNode
  %z = alloca i32
;;VAL:%z;;TYPE:i32
  %t37 = add i32 0, 33
;;VAL:%t37;;TYPE:i32
  store i32 %t37, i32* %z
  ; ListAddAllNode
  %t40 = load %struct.ArrayListInt*, %struct.ArrayListInt** %numbers
;;VAL:%t40;;TYPE:%struct.ArrayListInt*
  %t41 = alloca i32, i64 5
  %t42 = add i32 0, 3
;;VAL:%t42;;TYPE:i32
  %t43 = getelementptr inbounds i32, i32* %t41, i64 0
  store i32 %t42, i32* %t43
  %t44 = add i32 0, 4
;;VAL:%t44;;TYPE:i32
  %t45 = getelementptr inbounds i32, i32* %t41, i64 1
  store i32 %t44, i32* %t45
  %t46 = add i32 0, 5
;;VAL:%t46;;TYPE:i32
  %t47 = getelementptr inbounds i32, i32* %t41, i64 2
  store i32 %t46, i32* %t47
  %t48 = load i32, i32* %x
;;VAL:%t48;;TYPE:i32
  %t49 = getelementptr inbounds i32, i32* %t41, i64 3
  store i32 %t48, i32* %t49
  %t50 = load i32, i32* %z
;;VAL:%t50;;TYPE:i32
  %t51 = getelementptr inbounds i32, i32* %t41, i64 4
  store i32 %t50, i32* %t51
  call void @arraylist_addAll_int(%struct.ArrayListInt* %t40, i32* %t41, i64 5)
;;VAL:%t40;;TYPE:%struct.ArrayListInt*
  ; PrintNode
  %t52 = load %struct.ArrayListInt*, %struct.ArrayListInt** %numbers
  call void @arraylist_print_int(%struct.ArrayListInt* %t52)
  ; === Free das listas alocadas ===
  %t53 = load i8*, i8** %nomes
  %t54 = bitcast i8* %t53 to %ArrayList*
  call void @freeList(%ArrayList* %t54)
  %t55 = load %struct.ArrayListInt*, %struct.ArrayListInt** %numbers
  call void @arraylist_free_int(%struct.ArrayListInt* %t55)
  %t56 = load %struct.ArrayListBool*, %struct.ArrayListBool** %is
  call void @arraylist_free_bool(%struct.ArrayListBool* %t56)
  call i32 @getchar()
  ret i32 0
}
