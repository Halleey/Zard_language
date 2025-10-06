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
    declare void @arraylist_add_double(%ArrayList*, double)
    declare void @arraylist_addAll_double(%ArrayList*, double*, i64)
    declare void @arraylist_print_double(%ArrayList*)
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

@.str0 = private constant [5 x i8] c"zard\00"
@.str1 = private constant [4 x i8] c"ang\00"
@.str2 = private constant [7 x i8] c"halley\00"
@.str3 = private constant [5 x i8] c"nome\00"
@.str4 = private constant [9 x i8] c"testando\00"

define i32 @main() {
  ; VariableDeclarationNode
  %nume = alloca %struct.ArrayListInt*
;;VAL:%nume;;TYPE:%struct.ArrayListInt*
  %t0 = call %struct.ArrayListInt* @arraylist_create_int(i64 4)
  %t1 = add i32 0, 3
;;VAL:%t1;;TYPE:i32
  call void @arraylist_add_int(%struct.ArrayListInt* %t0, i32 %t1)
;;VAL:%t0;;TYPE:%struct.ArrayListInt*
  store %struct.ArrayListInt* %t0, %struct.ArrayListInt** %nume
  ; VariableDeclarationNode
  %teste = alloca i8*
;;VAL:%teste;;TYPE:i8*
  %t2 = call i8* @arraylist_create(i64 4)
  %t3 = bitcast i8* %t2 to %ArrayList*
  %t4 = bitcast [5 x i8]* @.str0 to i8*
;;VAL:%t4;;TYPE:i8*
  call void @arraylist_add_string(%ArrayList* %t3, i8* %t4)
  %t5 = bitcast [4 x i8]* @.str1 to i8*
;;VAL:%t5;;TYPE:i8*
  call void @arraylist_add_string(%ArrayList* %t3, i8* %t5)
;;VAL:%t2;;TYPE:i8*
  store i8* %t2, i8** %teste
  ; VariableDeclarationNode
  %tes = alloca i8*
;;VAL:%tes;;TYPE:i8*
  %t6 = call i8* @arraylist_create(i64 4)
  %t7 = bitcast i8* %t6 to %ArrayList*
  %t8 = fadd double 0.0, 3.14
;;VAL:%t8;;TYPE:double
  call void @arraylist_add_double(%ArrayList* %t7, double %t8)
  %t9 = fadd double 0.0, 0.2
;;VAL:%t9;;TYPE:double
  call void @arraylist_add_double(%ArrayList* %t7, double %t9)
  %t10 = fadd double 0.0, 99.2
;;VAL:%t10;;TYPE:double
  call void @arraylist_add_double(%ArrayList* %t7, double %t10)
;;VAL:%t6;;TYPE:i8*
  store i8* %t6, i8** %tes
  ; VariableDeclarationNode
  %name = alloca %String*
;;VAL:%name;;TYPE:%String*
  %t11 = call i8* @malloc(i64 ptrtoint (%String* getelementptr (%String, %String* null, i32 1) to i64))
  %t12 = bitcast i8* %t11 to %String*
  %t13 = bitcast [7 x i8]* @.str2 to i8*
  %t14 = getelementptr inbounds %String, %String* %t12, i32 0, i32 0
  store i8* %t13, i8** %t14
  %t15 = getelementptr inbounds %String, %String* %t12, i32 0, i32 1
  store i64 6, i64* %t15
  store %String* %t12, %String** %name
  ; ListAddAllNode
  %t16 = load i8*, i8** %teste
;;VAL:%t16;;TYPE:i8*
  %t17 = bitcast [5 x i8]* @.str3 to i8*
;;VAL:%t17;;TYPE:i8*
  %t18 = bitcast i8* %t16 to %ArrayList*
  %t19 = alloca i8*, i64 2
  %t20 = bitcast [5 x i8]* @.str3 to i8*
  %t21 = getelementptr inbounds i8*, i8** %t19, i64 0
  store i8* %t20, i8** %t21
  %t22 = bitcast [9 x i8]* @.str4 to i8*
  %t23 = getelementptr inbounds i8*, i8** %t19, i64 1
  store i8* %t22, i8** %t23
  call void @arraylist_addAll_string(%ArrayList* %t18, i8** %t19, i64 2)
;;VAL:%t18;;TYPE:%ArrayList*
  ; ListAddAllNode
  %t26 = load %struct.ArrayListInt*, %struct.ArrayListInt** %nume
;;VAL:%t26;;TYPE:%struct.ArrayListInt*
  %t27 = alloca i32, i64 4
  %t28 = add i32 0, 5
;;VAL:%t28;;TYPE:i32
  %t29 = getelementptr inbounds i32, i32* %t27, i64 0
  store i32 %t28, i32* %t29
  %t30 = add i32 0, 3
;;VAL:%t30;;TYPE:i32
  %t31 = getelementptr inbounds i32, i32* %t27, i64 1
  store i32 %t30, i32* %t31
  %t32 = add i32 0, 4
;;VAL:%t32;;TYPE:i32
  %t33 = getelementptr inbounds i32, i32* %t27, i64 2
  store i32 %t32, i32* %t33
  %t34 = add i32 0, 5
;;VAL:%t34;;TYPE:i32
  %t35 = getelementptr inbounds i32, i32* %t27, i64 3
  store i32 %t34, i32* %t35
  call void @arraylist_addAll_int(%struct.ArrayListInt* %t26, i32* %t27, i64 4)
;;VAL:%t26;;TYPE:%struct.ArrayListInt*
  ; PrintNode
  %t36 = load i8*, i8** %teste
  %t37 = bitcast i8* %t36 to %ArrayList*
  call void @arraylist_print_string(%ArrayList* %t37)
  ; PrintNode
  %t38 = load i8*, i8** %tes
  %t39 = bitcast i8* %t38 to %ArrayList*
  call void @arraylist_print_double(%ArrayList* %t39)
  ; PrintNode
  %t40 = load %struct.ArrayListInt*, %struct.ArrayListInt** %nume
  call void @arraylist_print_int(%struct.ArrayListInt* %t40)
  ; === Free das listas alocadas ===
  %t41 = load i8*, i8** %teste
  %t42 = bitcast i8* %t41 to %ArrayList*
  call void @freeList(%ArrayList* %t42)
  %t43 = load i8*, i8** %tes
  %t44 = bitcast i8* %t43 to %ArrayList*
  call void @freeList(%ArrayList* %t44)
  %t45 = load %struct.ArrayListInt*, %struct.ArrayListInt** %nume
  call void @arraylist_free_int(%struct.ArrayListInt* %t45)
  call i32 @getchar()
  ret i32 0
}
