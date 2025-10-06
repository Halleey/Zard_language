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
    %struct.ArrayListInt = type { i32*, i64, i64 }
    declare %struct.ArrayListInt* @arraylist_create_int(i64)
    declare void @arraylist_add_int(%struct.ArrayListInt*, i32)
    declare void @arraylist_addAll_int(%struct.ArrayListInt*, i32*, i64)
    declare void @arraylist_print_int(%struct.ArrayListInt*)
    declare void @arraylist_free_int(%struct.ArrayListInt*)
    declare void @arraylist_add_string(%ArrayList*, i8*)
    declare void @arraylist_addAll_string(%ArrayList*, i8**, i64)
    declare void @arraylist_print_string(%ArrayList*)
    declare void @arraylist_add_String(%ArrayList*, %String*)
    declare void @arraylist_addAll_String(%ArrayList*, %String**, i64)

@.str0 = private constant [21 x i8] c"teste um hello world\00"
@.str1 = private constant [6 x i8] c"teste\00"

define i32 @main() {
  ; PrintNode
  %t13 = add i32 0, 5
;;VAL:%t13;;TYPE:i32
  %t14 = call i32 @math_factorial(i32 %t13)
  call i32 (i8*, ...) @printf(i8* getelementptr ([4 x i8], [4 x i8]* @.strInt, i32 0, i32 0), i32 %t14)
  ; VariableDeclarationNode
  %a = alloca i32
;;VAL:%a;;TYPE:i32
  %t15 = add i32 0, 0
;;VAL:%t15;;TYPE:i32
  store i32 %t15, i32* %a
  ; VariableDeclarationNode
  %numeros = alloca %struct.ArrayListInt*
;;VAL:%numeros;;TYPE:%struct.ArrayListInt*
  %t16 = call %struct.ArrayListInt* @arraylist_create_int(i64 4)
;;VAL:%t16;;TYPE:%struct.ArrayListInt*
  store %struct.ArrayListInt* %t16, %struct.ArrayListInt** %numeros
  ; WhileNode
  br label %while_cond_0
while_cond_0:
  %t17 = load i32, i32* %a
;;VAL:%t17;;TYPE:i32

  %t18 = add i32 0, 10
;;VAL:%t18;;TYPE:i32

  %t19 = icmp slt i32 %t17, %t18
;;VAL:%t19;;TYPE:i1
  br i1 %t19, label %while_body_1, label %while_end_2
while_body_1:
  %t22 = load %struct.ArrayListInt*, %struct.ArrayListInt** %numeros
;;VAL:%t22;;TYPE:%struct.ArrayListInt*
  %t23 = load i32, i32* %a
;;VAL:%t23;;TYPE:i32
  call void @arraylist_add_int(%struct.ArrayListInt* %t22, i32 %t23)
;;VAL:%t22;;TYPE:%struct.ArrayListInt*
  %t24 = load i32, i32* %a
  %t25 = add i32 %t24, 1
  store i32 %t25, i32* %a
  br label %while_cond_0
while_end_2:
  ; VariableDeclarationNode
  %nomes = alloca i8*
;;VAL:%nomes;;TYPE:i8*
  %t26 = call i8* @arraylist_create(i64 4)
  %t27 = bitcast i8* %t26 to %ArrayList*
  %t28 = bitcast [6 x i8]* @.str1 to i8*
;;VAL:%t28;;TYPE:i8*
  call void @arraylist_add_string(%ArrayList* %t27, i8* %t28)
  %t29 = bitcast [6 x i8]* @.str1 to i8*
;;VAL:%t29;;TYPE:i8*
  call void @arraylist_add_string(%ArrayList* %t27, i8* %t29)
;;VAL:%t26;;TYPE:i8*
  store i8* %t26, i8** %nomes
  ; PrintNode
  %t30 = load i8*, i8** %nomes
;;VAL:%t30;;TYPE:i8*
  %t31 = bitcast i8* %t30 to %ArrayList*
  %t32 = call i32 @size(%ArrayList* %t31)
  
  call i32 (i8*, ...) @printf(i8* getelementptr ([4 x i8], [4 x i8]* @.strInt, i32 0, i32 0), i32 %t32)
  ; PrintNode
  %t33 = load %struct.ArrayListInt*, %struct.ArrayListInt** %numeros
  call void @arraylist_print_int(%struct.ArrayListInt* %t33)
  ; === Free das listas alocadas ===
  %t34 = load %struct.ArrayListInt*, %struct.ArrayListInt** %numeros
  call void @arraylist_free_int(%struct.ArrayListInt* %t34)
  %t35 = load i8*, i8** %nomes
  %t36 = bitcast i8* %t35 to %ArrayList*
  call void @freeList(%ArrayList* %t36)
  call i32 @getchar()
  ret i32 0
}
