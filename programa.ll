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
    %struct.ArrayListDouble = type { double*, i64, i64 }
    declare %struct.ArrayListDouble* @arraylist_create_double(i64)
    declare void @arraylist_add_double(%struct.ArrayListDouble*, double)
    declare void @arraylist_addAll_double(%struct.ArrayListDouble*, double*, i64)
    declare void @arraylist_print_double(%struct.ArrayListDouble*)
    declare void @arraylist_clear_double(%struct.ArrayListDouble*)
    declare void @arraylist_free_double(%struct.ArrayListDouble*)
    %struct.ArrayListInt = type { i32*, i64, i64 }
    declare %struct.ArrayListInt* @arraylist_create_int(i64)
    declare void @arraylist_add_int(%struct.ArrayListInt*, i32)
    declare void @arraylist_addAll_int(%struct.ArrayListInt*, i32*, i64)
    declare void @arraylist_print_int(%struct.ArrayListInt*)
    declare void @arraylist_clear_int(%struct.ArrayListInt*)
    declare void @arraylist_free_int(%struct.ArrayListInt*)
    declare void @arraylist_add_string(%ArrayList*, i8*)
    declare void @arraylist_addAll_string(%ArrayList*, i8**, i64)
    declare void @arraylist_print_string(%ArrayList*)
    declare void @arraylist_add_String(%ArrayList*, %String*)
    declare void @arraylist_addAll_String(%ArrayList*, %String**, i64)

@.str0 = private constant [21 x i8] c"teste um hello world\00"
@.str1 = private constant [5 x i8] c"zard\00"
@.str2 = private constant [6 x i8] c"teste\00"
@.str3 = private constant [3 x i8] c"ok\00"
@.str4 = private constant [1 x i8] c"\00"

define i32 @main() {
  ; VariableDeclarationNode
  %inteiros = alloca %struct.ArrayListInt*
;;VAL:%inteiros;;TYPE:%struct.ArrayListInt*
  %t13 = call %struct.ArrayListInt* @arraylist_create_int(i64 4)
;;VAL:%t13;;TYPE:%struct.ArrayListInt*
  store %struct.ArrayListInt* %t13, %struct.ArrayListInt** %inteiros
  ; VariableDeclarationNode
  %list = alloca i8*
;;VAL:%list;;TYPE:i8*
  %t14 = call i8* @arraylist_create(i64 4)
  %t15 = bitcast i8* %t14 to %ArrayList*
  %t16 = bitcast [5 x i8]* @.str1 to i8*
;;VAL:%t16;;TYPE:i8*
  call void @arraylist_add_string(%ArrayList* %t15, i8* %t16)
;;VAL:%t14;;TYPE:i8*
  store i8* %t14, i8** %list
  ; VariableDeclarationNode
  %numberss = alloca %struct.ArrayListInt*
;;VAL:%numberss;;TYPE:%struct.ArrayListInt*
  %t17 = call %struct.ArrayListInt* @arraylist_create_int(i64 4)
  %t18 = add i32 0, 2
;;VAL:%t18;;TYPE:i32
  call void @arraylist_add_int(%struct.ArrayListInt* %t17, i32 %t18)
  %t19 = add i32 0, 3
;;VAL:%t19;;TYPE:i32
  call void @arraylist_add_int(%struct.ArrayListInt* %t17, i32 %t19)
  %t20 = add i32 0, 4
;;VAL:%t20;;TYPE:i32
  call void @arraylist_add_int(%struct.ArrayListInt* %t17, i32 %t20)
;;VAL:%t17;;TYPE:%struct.ArrayListInt*
  store %struct.ArrayListInt* %t17, %struct.ArrayListInt** %numberss
  ; ListAddNode
  %t23 = load %struct.ArrayListInt*, %struct.ArrayListInt** %inteiros
;;VAL:%t23;;TYPE:%struct.ArrayListInt*
  %t24 = add i32 0, 2
;;VAL:%t24;;TYPE:i32
  call void @arraylist_add_int(%struct.ArrayListInt* %t23, i32 %t24)
;;VAL:%t23;;TYPE:%struct.ArrayListInt*
  ; VariableDeclarationNode
  %flut = alloca %struct.ArrayListDouble*
;;VAL:%flut;;TYPE:%struct.ArrayListDouble*
  %t25 = call %struct.ArrayListDouble* @arraylist_create_double(i64 4)
  %t26 = fadd double 0.0, 3.14
;;VAL:%t26;;TYPE:double
  call void @arraylist_add_double(%struct.ArrayListDouble* %t25, double %t26)
;;VAL:%t25;;TYPE:%struct.ArrayListDouble*
  store %struct.ArrayListDouble* %t25, %struct.ArrayListDouble** %flut
  ; PrintNode
  %t27 = load %struct.ArrayListDouble*, %struct.ArrayListDouble** %flut
  call void @arraylist_print_double(%struct.ArrayListDouble* %t27)
  ; VariableDeclarationNode
  %t = alloca i1
;;VAL:%t;;TYPE:i1
  %t28 = add i1 0, 1
;;VAL:%t28;;TYPE:i1
  store i1 %t28, i1* %t
  ; PrintNode
  %t29 = load i1, i1* %t
  %t30 = zext i1 %t29 to i32
  call i32 (i8*, ...) @printf(i8* getelementptr ([4 x i8], [4 x i8]* @.strInt, i32 0, i32 0), i32 %t30)
  ; PrintNode
  %t31 = add i32 0, 5
;;VAL:%t31;;TYPE:i32
  %t32 = call i32 @math_factorial(i32 %t31)
  call i32 (i8*, ...) @printf(i8* getelementptr ([4 x i8], [4 x i8]* @.strInt, i32 0, i32 0), i32 %t32)
  ; VariableDeclarationNode
  %a = alloca i32
;;VAL:%a;;TYPE:i32
  ; AssignmentNode
  store i32 0, i32* %a
  ; VariableDeclarationNode
  %numeros = alloca %struct.ArrayListInt*
;;VAL:%numeros;;TYPE:%struct.ArrayListInt*
  %t33 = call %struct.ArrayListInt* @arraylist_create_int(i64 4)
;;VAL:%t33;;TYPE:%struct.ArrayListInt*
  store %struct.ArrayListInt* %t33, %struct.ArrayListInt** %numeros
  ; WhileNode
  br label %while_cond_0
while_cond_0:
  %t34 = load i32, i32* %a
;;VAL:%t34;;TYPE:i32

  %t35 = add i32 0, 10
;;VAL:%t35;;TYPE:i32

  %t36 = icmp slt i32 %t34, %t35
;;VAL:%t36;;TYPE:i1
  br i1 %t36, label %while_body_1, label %while_end_2
while_body_1:
  %t39 = load %struct.ArrayListInt*, %struct.ArrayListInt** %numeros
;;VAL:%t39;;TYPE:%struct.ArrayListInt*
  %t40 = load i32, i32* %a
;;VAL:%t40;;TYPE:i32
  call void @arraylist_add_int(%struct.ArrayListInt* %t39, i32 %t40)
;;VAL:%t39;;TYPE:%struct.ArrayListInt*
  %t41 = load i32, i32* %a
  %t42 = add i32 %t41, 1
  store i32 %t42, i32* %a
  br label %while_cond_0
while_end_2:
  ; VariableDeclarationNode
  %nomes = alloca i8*
;;VAL:%nomes;;TYPE:i8*
  %t43 = call i8* @arraylist_create(i64 4)
  %t44 = bitcast i8* %t43 to %ArrayList*
  %t45 = bitcast [6 x i8]* @.str2 to i8*
;;VAL:%t45;;TYPE:i8*
  call void @arraylist_add_string(%ArrayList* %t44, i8* %t45)
  %t46 = bitcast [6 x i8]* @.str2 to i8*
;;VAL:%t46;;TYPE:i8*
  call void @arraylist_add_string(%ArrayList* %t44, i8* %t46)
;;VAL:%t43;;TYPE:i8*
  store i8* %t43, i8** %nomes
  ; ListAddNode
  %t47 = load i8*, i8** %nomes
;;VAL:%t47;;TYPE:i8*
  %t49 = bitcast i8* %t47 to %ArrayList*
  %t48 = bitcast [3 x i8]* @.str3 to i8*
;;VAL:%t48;;TYPE:i8*
  call void @arraylist_add_string(%ArrayList* %t49, i8* getelementptr ([3 x i8], [3 x i8]* @.str3, i32 0, i32 0))
;;VAL:%t49;;TYPE:%ArrayList*
  ; VariableDeclarationNode
  %nome = alloca %String*
;;VAL:%nome;;TYPE:%String*
  %t50 = call i8* @malloc(i64 ptrtoint (%String* getelementptr (%String, %String* null, i32 1) to i64))
  %t51 = bitcast i8* %t50 to %String*
  %t52 = getelementptr inbounds %String, %String* %t51, i32 0, i32 0
  store i8* null, i8** %t52
  %t53 = getelementptr inbounds %String, %String* %t51, i32 0, i32 1
  store i64 0, i64* %t53
  store %String* %t51, %String** %nome
  ; AssignmentNode
  %t55 = call i8* @inputString(i8* null)
  %t56 = call %String* @createString(i8* %t55)
;;VAL:%t56;;TYPE:%String
  store %String* %t56, %String** %nome
  ; ListAddNode
  %t57 = load i8*, i8** %nomes
;;VAL:%t57;;TYPE:i8*
  %t59 = bitcast i8* %t57 to %ArrayList*
  %t58 = load %String*, %String** %nome
;;VAL:%t58;;TYPE:%String*
  call void @arraylist_add_String(%ArrayList* %t59, %String* %t58)
;;VAL:%t59;;TYPE:%ArrayList*
  ; PrintNode
  %t60 = load %struct.ArrayListInt*, %struct.ArrayListInt** %numeros
  call void @arraylist_print_int(%struct.ArrayListInt* %t60)
  ; FunctionCallNode
  %t61 = load i8*, i8** %nomes
;;VAL:%t61;;TYPE:i8*
  call void @math_tes(i8* %t61)
;;VAL:void;;TYPE:void
  ; PrintNode
  %t62 = load i8*, i8** %nomes
  %t63 = bitcast i8* %t62 to %ArrayList*
  call void @arraylist_print_string(%ArrayList* %t63)
  ; ListClearNode
  %t65 = load %struct.ArrayListInt*, %struct.ArrayListInt** %numeros
;;VAL:%t65;;TYPE:%struct.ArrayListInt*
  call void @arraylist_clear_int(%struct.ArrayListInt* %t65)
;;VAL:%t65;;TYPE:%struct.ArrayListInt*
  ; ListClearNode
  %t66 = load i8*, i8** %nomes
;;VAL:%t66;;TYPE:i8*
  %t67 = bitcast i8* %t66 to %ArrayList*
  call void @clearList(%ArrayList* %t67)
;;VAL:%t67;;TYPE:%ArrayList*
  ; PrintNode
  %t68 = load i8*, i8** %nomes
  %t69 = bitcast i8* %t68 to %ArrayList*
  call void @arraylist_print_string(%ArrayList* %t69)
  ; === Free das listas alocadas ===
  %t70 = load %struct.ArrayListInt*, %struct.ArrayListInt** %numeros
  call void @arraylist_free_int(%struct.ArrayListInt* %t70)
  %t71 = load %struct.ArrayListDouble*, %struct.ArrayListDouble** %flut
  call void @arraylist_free_double(%struct.ArrayListDouble* %t71)
  %t72 = load %struct.ArrayListInt*, %struct.ArrayListInt** %numberss
  call void @arraylist_free_int(%struct.ArrayListInt* %t72)
  %t73 = load %struct.ArrayListInt*, %struct.ArrayListInt** %inteiros
  call void @arraylist_free_int(%struct.ArrayListInt* %t73)
  %t74 = load i8*, i8** %nomes
  %t75 = bitcast i8* %t74 to %ArrayList*
  call void @freeList(%ArrayList* %t75)
  %t76 = load i8*, i8** %list
  %t77 = bitcast i8* %t76 to %ArrayList*
  call void @freeList(%ArrayList* %t77)
  call i32 @getchar()
  ret i32 0
}
