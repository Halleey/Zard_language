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
    declare i32 @inputInt(i8*)
    declare double @inputDouble(i8*)
    declare i1 @inputBool(i8*)
    declare i8* @inputString(i8*)
    declare %String* @createString(i8*)
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
    declare i32  @arraylist_size_int(%struct.ArrayListInt*)
    declare void @arraylist_add_string(%ArrayList*, i8*)
    declare void @arraylist_addAll_string(%ArrayList*, i8**, i64)
    declare void @arraylist_print_string(%ArrayList*)
    declare void @arraylist_add_String(%ArrayList*, %String*)
    declare void @arraylist_addAll_String(%ArrayList*, %String**, i64)
    declare i8* @getItem(%ArrayList*, i64)

@.str0 = private constant [21 x i8] c"teste um hello world\00"
@.str1 = private constant [3 x i8] c"za\00"
@.str2 = private constant [5 x i8] c"zard\00"
@.str3 = private constant [5 x i8] c"test\00"
@.str4 = private constant [1 x i8] c"\00"

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
  ; VariableDeclarationNode
  %nom = alloca %String*
;;VAL:%nom;;TYPE:%String*
  %t19 = call i8* @malloc(i64 ptrtoint (%String* getelementptr (%String, %String* null, i32 1) to i64))
  %t20 = bitcast i8* %t19 to %String*
  %t21 = getelementptr inbounds %String, %String* %t20, i32 0, i32 0
  store i8* null, i8** %t21
  %t22 = getelementptr inbounds %String, %String* %t20, i32 0, i32 1
  store i64 0, i64* %t22
  store %String* %t20, %String** %nom
  ; AssignmentNode
  %t23 = load %String*, %String** %nom
  call void @setString(%String* %t23, i8* getelementptr ([3 x i8], [3 x i8]* @.str1, i32 0, i32 0))
  ; PrintNode
  %t24 = load %String*, %String** %nom
  call void @printString(%String* %t24)
  ; PrintNode
  %t25 = load %struct.ArrayListBool*, %struct.ArrayListBool** %is
  call void @arraylist_print_bool(%struct.ArrayListBool* %t25)
  ; VariableDeclarationNode
  %nomes = alloca i8*
;;VAL:%nomes;;TYPE:i8*
  %t26 = call i8* @arraylist_create(i64 4)
  %t27 = bitcast i8* %t26 to %ArrayList*
  %t28 = bitcast [5 x i8]* @.str2 to i8*
;;VAL:%t28;;TYPE:i8*
  call void @arraylist_add_string(%ArrayList* %t27, i8* %t28)
  %t29 = bitcast [5 x i8]* @.str3 to i8*
;;VAL:%t29;;TYPE:i8*
  call void @arraylist_add_string(%ArrayList* %t27, i8* %t29)
;;VAL:%t26;;TYPE:i8*
  store i8* %t26, i8** %nomes
  ; PrintNode
  %t30 = load i8*, i8** %nomes
  %t31 = bitcast i8* %t30 to %ArrayList*
  %t32 = add i32 0, 1
  %t33 = zext i32 %t32 to i64
  %t34 = call i8* @getItem(%ArrayList* %t31, i64 %t33)
  call i32 (i8*, ...) @printf(i8* getelementptr ([4 x i8], [4 x i8]* @.strStr, i32 0, i32 0), i8* %t34)
  ; VariableDeclarationNode
  %numbers = alloca %struct.ArrayListInt*
;;VAL:%numbers;;TYPE:%struct.ArrayListInt*
  %t35 = call %struct.ArrayListInt* @arraylist_create_int(i64 4)
  %t36 = alloca i32, i64 4
  %t37 = add i32 0, 3
;;VAL:%t37;;TYPE:i32
  %t38 = getelementptr inbounds i32, i32* %t36, i64 0
  store i32 %t37, i32* %t38
  %t39 = add i32 0, 4
;;VAL:%t39;;TYPE:i32
  %t40 = getelementptr inbounds i32, i32* %t36, i64 1
  store i32 %t39, i32* %t40
  %t41 = add i32 0, 5
;;VAL:%t41;;TYPE:i32
  %t42 = getelementptr inbounds i32, i32* %t36, i64 2
  store i32 %t41, i32* %t42
  %t43 = add i32 0, 6
;;VAL:%t43;;TYPE:i32
  %t44 = getelementptr inbounds i32, i32* %t36, i64 3
  store i32 %t43, i32* %t44
  call void @arraylist_addAll_int(%struct.ArrayListInt* %t35, i32* %t36, i64 4)
;;VAL:%t35;;TYPE:%struct.ArrayListInt*
  store %struct.ArrayListInt* %t35, %struct.ArrayListInt** %numbers
  ; ListRemoveNode
  %t46 = load %struct.ArrayListInt*, %struct.ArrayListInt** %numbers
  %t47 = add i32 0, 1
  %t48 = zext i32 %t47 to i64
  call void @arraylist_remove_int(%struct.ArrayListInt* %t46, i64 %t48)
  ; PrintNode
  %t49 = load %struct.ArrayListInt*, %struct.ArrayListInt** %numbers
  call void @arraylist_print_int(%struct.ArrayListInt* %t49)
  ; VariableDeclarationNode
  %tes = alloca %struct.ArrayListInt*
;;VAL:%tes;;TYPE:%struct.ArrayListInt*
  %t50 = call %struct.ArrayListInt* @arraylist_create_int(i64 4)
;;VAL:%t50;;TYPE:%struct.ArrayListInt*
  store %struct.ArrayListInt* %t50, %struct.ArrayListInt** %tes
  ; ListAddNode
  %t53 = load %struct.ArrayListInt*, %struct.ArrayListInt** %tes
;;VAL:%t53;;TYPE:%struct.ArrayListInt*
  %t54 = add i32 0, 3
;;VAL:%t54;;TYPE:i32
  call void @arraylist_add_int(%struct.ArrayListInt* %t53, i32 %t54)
;;VAL:%t53;;TYPE:%struct.ArrayListInt*
  ; PrintNode
  %t55 = load %struct.ArrayListInt*, %struct.ArrayListInt** %tes
  call void @arraylist_print_int(%struct.ArrayListInt* %t55)
  ; VariableDeclarationNode
  %x = alloca i32
;;VAL:%x;;TYPE:i32
  ; AssignmentNode
  %t56 = call i32 @inputInt(i8* null)
;;VAL:%t56;;TYPE:i32
  store i32 %t56, i32* %x
  ; VariableDeclarationNode
  %z = alloca i32
;;VAL:%z;;TYPE:i32
  %t57 = add i32 0, 33
;;VAL:%t57;;TYPE:i32
  store i32 %t57, i32* %z
  ; ListAddAllNode
  %t60 = load %struct.ArrayListInt*, %struct.ArrayListInt** %numbers
;;VAL:%t60;;TYPE:%struct.ArrayListInt*
  %t61 = alloca i32, i64 5
  %t62 = add i32 0, 3
;;VAL:%t62;;TYPE:i32
  %t63 = getelementptr inbounds i32, i32* %t61, i64 0
  store i32 %t62, i32* %t63
  %t64 = add i32 0, 4
;;VAL:%t64;;TYPE:i32
  %t65 = getelementptr inbounds i32, i32* %t61, i64 1
  store i32 %t64, i32* %t65
  %t66 = add i32 0, 5
;;VAL:%t66;;TYPE:i32
  %t67 = getelementptr inbounds i32, i32* %t61, i64 2
  store i32 %t66, i32* %t67
  %t68 = load i32, i32* %x
;;VAL:%t68;;TYPE:i32
  %t69 = getelementptr inbounds i32, i32* %t61, i64 3
  store i32 %t68, i32* %t69
  %t70 = load i32, i32* %z
;;VAL:%t70;;TYPE:i32
  %t71 = getelementptr inbounds i32, i32* %t61, i64 4
  store i32 %t70, i32* %t71
  call void @arraylist_addAll_int(%struct.ArrayListInt* %t60, i32* %t61, i64 5)
;;VAL:%t60;;TYPE:%struct.ArrayListInt*
  ; PrintNode
  %t72 = load %struct.ArrayListInt*, %struct.ArrayListInt** %numbers
  call void @arraylist_print_int(%struct.ArrayListInt* %t72)
  ; PrintNode
  %t74 = load %struct.ArrayListInt*, %struct.ArrayListInt** %numbers
;;VAL:%t74;;TYPE:%struct.ArrayListInt*
  %t75 = call i32 @arraylist_size_int(%struct.ArrayListInt* %t74)
  
  call i32 (i8*, ...) @printf(i8* getelementptr ([4 x i8], [4 x i8]* @.strInt, i32 0, i32 0), i32 %t75)
  ; === Free das listas alocadas ===
  %t76 = load %struct.ArrayListInt*, %struct.ArrayListInt** %tes
  call void @arraylist_free_int(%struct.ArrayListInt* %t76)
  %t77 = load i8*, i8** %nomes
  %t78 = bitcast i8* %t77 to %ArrayList*
  call void @freeList(%ArrayList* %t78)
  %t79 = load %struct.ArrayListInt*, %struct.ArrayListInt** %numbers
  call void @arraylist_free_int(%struct.ArrayListInt* %t79)
  %t80 = load %struct.ArrayListBool*, %struct.ArrayListBool** %is
  call void @arraylist_free_bool(%struct.ArrayListBool* %t80)
  call i32 @getchar()
  ret i32 0
}
