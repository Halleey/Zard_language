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
@.str1 = private constant [6 x i8] c"teste\00"
@.str2 = private constant [3 x i8] c"ok\00"
@.str3 = private constant [1 x i8] c"\00"

define i32 @main() {
  ; VariableDeclarationNode
  %t = alloca i1
;;VAL:%t;;TYPE:i1
  %t13 = add i1 0, 1
;;VAL:%t13;;TYPE:i1
  store i1 %t13, i1* %t
  ; PrintNode
  %t14 = load i1, i1* %t
  %t15 = zext i1 %t14 to i32
  call i32 (i8*, ...) @printf(i8* getelementptr ([4 x i8], [4 x i8]* @.strInt, i32 0, i32 0), i32 %t15)
  ; PrintNode
  %t16 = add i32 0, 5
;;VAL:%t16;;TYPE:i32
  %t17 = call i32 @math_factorial(i32 %t16)
  call i32 (i8*, ...) @printf(i8* getelementptr ([4 x i8], [4 x i8]* @.strInt, i32 0, i32 0), i32 %t17)
  ; VariableDeclarationNode
  %a = alloca i32
;;VAL:%a;;TYPE:i32
  ; AssignmentNode
  store i32 0, i32* %a
  ; VariableDeclarationNode
  %numeros = alloca %struct.ArrayListInt*
;;VAL:%numeros;;TYPE:%struct.ArrayListInt*
  %t18 = call %struct.ArrayListInt* @arraylist_create_int(i64 4)
;;VAL:%t18;;TYPE:%struct.ArrayListInt*
  store %struct.ArrayListInt* %t18, %struct.ArrayListInt** %numeros
  ; WhileNode
  br label %while_cond_0
while_cond_0:
  %t19 = load i32, i32* %a
;;VAL:%t19;;TYPE:i32

  %t20 = add i32 0, 10
;;VAL:%t20;;TYPE:i32

  %t21 = icmp slt i32 %t19, %t20
;;VAL:%t21;;TYPE:i1
  br i1 %t21, label %while_body_1, label %while_end_2
while_body_1:
  %t24 = load %struct.ArrayListInt*, %struct.ArrayListInt** %numeros
;;VAL:%t24;;TYPE:%struct.ArrayListInt*
  %t25 = load i32, i32* %a
;;VAL:%t25;;TYPE:i32
  call void @arraylist_add_int(%struct.ArrayListInt* %t24, i32 %t25)
;;VAL:%t24;;TYPE:%struct.ArrayListInt*
  %t26 = load i32, i32* %a
  %t27 = add i32 %t26, 1
  store i32 %t27, i32* %a
  br label %while_cond_0
while_end_2:
  ; VariableDeclarationNode
  %nomes = alloca i8*
;;VAL:%nomes;;TYPE:i8*
  %t28 = call i8* @arraylist_create(i64 4)
  %t29 = bitcast i8* %t28 to %ArrayList*
  %t30 = bitcast [6 x i8]* @.str1 to i8*
;;VAL:%t30;;TYPE:i8*
  call void @arraylist_add_string(%ArrayList* %t29, i8* %t30)
  %t31 = bitcast [6 x i8]* @.str1 to i8*
;;VAL:%t31;;TYPE:i8*
  call void @arraylist_add_string(%ArrayList* %t29, i8* %t31)
;;VAL:%t28;;TYPE:i8*
  store i8* %t28, i8** %nomes
  ; ListAddNode
  %t32 = load i8*, i8** %nomes
;;VAL:%t32;;TYPE:i8*
  %t34 = bitcast i8* %t32 to %ArrayList*
  %t33 = bitcast [3 x i8]* @.str2 to i8*
;;VAL:%t33;;TYPE:i8*
  call void @arraylist_add_string(%ArrayList* %t34, i8* getelementptr ([3 x i8], [3 x i8]* @.str2, i32 0, i32 0))
;;VAL:%t34;;TYPE:%ArrayList*
  ; VariableDeclarationNode
  %nome = alloca %String*
;;VAL:%nome;;TYPE:%String*
  %t35 = call i8* @malloc(i64 ptrtoint (%String* getelementptr (%String, %String* null, i32 1) to i64))
  %t36 = bitcast i8* %t35 to %String*
  %t37 = getelementptr inbounds %String, %String* %t36, i32 0, i32 0
  store i8* null, i8** %t37
  %t38 = getelementptr inbounds %String, %String* %t36, i32 0, i32 1
  store i64 0, i64* %t38
  store %String* %t36, %String** %nome
  ; AssignmentNode
  %t40 = call i8* @inputString(i8* null)
  %t41 = call %String* @createString(i8* %t40)
;;VAL:%t41;;TYPE:%String
  store %String* %t41, %String** %nome
  ; ListAddNode
  %t42 = load i8*, i8** %nomes
;;VAL:%t42;;TYPE:i8*
  %t44 = bitcast i8* %t42 to %ArrayList*
  %t43 = load %String*, %String** %nome
;;VAL:%t43;;TYPE:%String*
  call void @arraylist_add_String(%ArrayList* %t44, %String* %t43)
;;VAL:%t44;;TYPE:%ArrayList*
  ; PrintNode
  %t45 = load %struct.ArrayListInt*, %struct.ArrayListInt** %numeros
  call void @arraylist_print_int(%struct.ArrayListInt* %t45)
  ; FunctionCallNode
  %t46 = load i8*, i8** %nomes
;;VAL:%t46;;TYPE:i8*
  call void @math_tes(i8* %t46)
;;VAL:void;;TYPE:void
  ; PrintNode
  %t47 = load i8*, i8** %nomes
  %t48 = bitcast i8* %t47 to %ArrayList*
  call void @arraylist_print_string(%ArrayList* %t48)
  ; ListClearNode
  %t50 = load %struct.ArrayListInt*, %struct.ArrayListInt** %numeros
;;VAL:%t50;;TYPE:%struct.ArrayListInt*
  call void @arraylist_clear_int(%struct.ArrayListInt* %t50)
;;VAL:%t50;;TYPE:%struct.ArrayListInt*
  ; ListClearNode
  %t51 = load i8*, i8** %nomes
;;VAL:%t51;;TYPE:i8*
  %t52 = bitcast i8* %t51 to %ArrayList*
  call void @clearList(%ArrayList* %t52)
;;VAL:%t52;;TYPE:%ArrayList*
  ; PrintNode
  %t53 = load i8*, i8** %nomes
  %t54 = bitcast i8* %t53 to %ArrayList*
  call void @arraylist_print_string(%ArrayList* %t54)
  ; === Free das listas alocadas ===
  %t55 = load %struct.ArrayListInt*, %struct.ArrayListInt** %numeros
  call void @arraylist_free_int(%struct.ArrayListInt* %t55)
  %t56 = load i8*, i8** %nomes
  %t57 = bitcast i8* %t56 to %ArrayList*
  call void @freeList(%ArrayList* %t57)
  call i32 @getchar()
  ret i32 0
}
