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
  ; PrintNode
  %t13 = add i32 0, 5
;;VAL:%t13;;TYPE:i32
  %t14 = call i32 @math_factorial(i32 %t13)
  call i32 (i8*, ...) @printf(i8* getelementptr ([4 x i8], [4 x i8]* @.strInt, i32 0, i32 0), i32 %t14)
  ; VariableDeclarationNode
  %a = alloca i32
;;VAL:%a;;TYPE:i32
  ; AssignmentNode
  store i32 0, i32* %a
  ; VariableDeclarationNode
  %numeros = alloca %struct.ArrayListInt*
;;VAL:%numeros;;TYPE:%struct.ArrayListInt*
  %t15 = call %struct.ArrayListInt* @arraylist_create_int(i64 4)
;;VAL:%t15;;TYPE:%struct.ArrayListInt*
  store %struct.ArrayListInt* %t15, %struct.ArrayListInt** %numeros
  ; WhileNode
  br label %while_cond_0
while_cond_0:
  %t16 = load i32, i32* %a
;;VAL:%t16;;TYPE:i32

  %t17 = add i32 0, 10
;;VAL:%t17;;TYPE:i32

  %t18 = icmp slt i32 %t16, %t17
;;VAL:%t18;;TYPE:i1
  br i1 %t18, label %while_body_1, label %while_end_2
while_body_1:
  %t21 = load %struct.ArrayListInt*, %struct.ArrayListInt** %numeros
;;VAL:%t21;;TYPE:%struct.ArrayListInt*
  %t22 = load i32, i32* %a
;;VAL:%t22;;TYPE:i32
  call void @arraylist_add_int(%struct.ArrayListInt* %t21, i32 %t22)
;;VAL:%t21;;TYPE:%struct.ArrayListInt*
  %t23 = load i32, i32* %a
  %t24 = add i32 %t23, 1
  store i32 %t24, i32* %a
  br label %while_cond_0
while_end_2:
  ; VariableDeclarationNode
  %nomes = alloca i8*
;;VAL:%nomes;;TYPE:i8*
  %t25 = call i8* @arraylist_create(i64 4)
  %t26 = bitcast i8* %t25 to %ArrayList*
  %t27 = bitcast [6 x i8]* @.str1 to i8*
;;VAL:%t27;;TYPE:i8*
  call void @arraylist_add_string(%ArrayList* %t26, i8* %t27)
  %t28 = bitcast [6 x i8]* @.str1 to i8*
;;VAL:%t28;;TYPE:i8*
  call void @arraylist_add_string(%ArrayList* %t26, i8* %t28)
;;VAL:%t25;;TYPE:i8*
  store i8* %t25, i8** %nomes
  ; ListAddNode
  %t29 = load i8*, i8** %nomes
;;VAL:%t29;;TYPE:i8*
  %t31 = bitcast i8* %t29 to %ArrayList*
  %t30 = bitcast [3 x i8]* @.str2 to i8*
;;VAL:%t30;;TYPE:i8*
  call void @arraylist_add_string(%ArrayList* %t31, i8* getelementptr ([3 x i8], [3 x i8]* @.str2, i32 0, i32 0))
;;VAL:%t31;;TYPE:%ArrayList*
  ; VariableDeclarationNode
  %nome = alloca %String*
;;VAL:%nome;;TYPE:%String*
  %t32 = call i8* @malloc(i64 ptrtoint (%String* getelementptr (%String, %String* null, i32 1) to i64))
  %t33 = bitcast i8* %t32 to %String*
  %t34 = getelementptr inbounds %String, %String* %t33, i32 0, i32 0
  store i8* null, i8** %t34
  %t35 = getelementptr inbounds %String, %String* %t33, i32 0, i32 1
  store i64 0, i64* %t35
  store %String* %t33, %String** %nome
  ; AssignmentNode
  %t37 = call i8* @inputString(i8* null)
  %t38 = call %String* @createString(i8* %t37)
;;VAL:%t38;;TYPE:%String
  store %String* %t38, %String** %nome
  ; ListAddNode
  %t39 = load i8*, i8** %nomes
;;VAL:%t39;;TYPE:i8*
  %t41 = bitcast i8* %t39 to %ArrayList*
  %t40 = load %String*, %String** %nome
;;VAL:%t40;;TYPE:%String*
  call void @arraylist_add_String(%ArrayList* %t41, %String* %t40)
;;VAL:%t41;;TYPE:%ArrayList*
  ; PrintNode
  %t42 = load %struct.ArrayListInt*, %struct.ArrayListInt** %numeros
  call void @arraylist_print_int(%struct.ArrayListInt* %t42)
  ; PrintNode
  %t43 = load i8*, i8** %nomes
  %t44 = bitcast i8* %t43 to %ArrayList*
  call void @arraylist_print_string(%ArrayList* %t44)
  ; ListClearNode
  %t46 = load %struct.ArrayListInt*, %struct.ArrayListInt** %numeros
;;VAL:%t46;;TYPE:%struct.ArrayListInt*
  call void @arraylist_clear_int(%struct.ArrayListInt* %t46)
;;VAL:%t46;;TYPE:%struct.ArrayListInt*
  ; ListClearNode
  %t47 = load i8*, i8** %nomes
;;VAL:%t47;;TYPE:i8*
  %t48 = bitcast i8* %t47 to %ArrayList*
  call void @clearList(%ArrayList* %t48)
;;VAL:%t48;;TYPE:%ArrayList*
  ; PrintNode
  %t49 = load i8*, i8** %nomes
  %t50 = bitcast i8* %t49 to %ArrayList*
  call void @arraylist_print_string(%ArrayList* %t50)
  ; PrintNode
  %t51 = load %struct.ArrayListInt*, %struct.ArrayListInt** %numeros
  call void @arraylist_print_int(%struct.ArrayListInt* %t51)
  ; === Free das listas alocadas ===
  %t52 = load %struct.ArrayListInt*, %struct.ArrayListInt** %numeros
  call void @arraylist_free_int(%struct.ArrayListInt* %t52)
  %t53 = load i8*, i8** %nomes
  %t54 = bitcast i8* %t53 to %ArrayList*
  call void @freeList(%ArrayList* %t54)
  call i32 @getchar()
  ret i32 0
}
