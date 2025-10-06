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
  ; ListAddNode
  %t16 = load i8*, i8** %teste
;;VAL:%t16;;TYPE:i8*
  %t18 = bitcast i8* %t16 to %ArrayList*
  %t17 = load %String*, %String** %name
;;VAL:%t17;;TYPE:%String*
  call void @arraylist_add_String(%ArrayList* %t18, %String* %t17)
;;VAL:%t18;;TYPE:%ArrayList*
  ; ListAddNode
  %t21 = load %struct.ArrayListInt*, %struct.ArrayListInt** %nume
;;VAL:%t21;;TYPE:%struct.ArrayListInt*
  %t22 = add i32 0, 5
;;VAL:%t22;;TYPE:i32
  call void @arraylist_add_int(%struct.ArrayListInt* %t21, i32 %t22)
;;VAL:%t21;;TYPE:%struct.ArrayListInt*
  ; PrintNode
  %t23 = load i8*, i8** %teste
  %t24 = bitcast i8* %t23 to %ArrayList*
  call void @arraylist_print_string(%ArrayList* %t24)
  ; PrintNode
  %t25 = load i8*, i8** %tes
  %t26 = bitcast i8* %t25 to %ArrayList*
  call void @arraylist_print_double(%ArrayList* %t26)
  ; PrintNode
  %t27 = load %struct.ArrayListInt*, %struct.ArrayListInt** %nume
  call void @arraylist_print_int(%struct.ArrayListInt* %t27)
  ; === Free das listas alocadas ===
  %t28 = load i8*, i8** %teste
  %t29 = bitcast i8* %t28 to %ArrayList*
  call void @freeList(%ArrayList* %t29)
  %t30 = load i8*, i8** %tes
  %t31 = bitcast i8* %t30 to %ArrayList*
  call void @freeList(%ArrayList* %t31)
  %t32 = load %struct.ArrayListInt*, %struct.ArrayListInt** %nume
  call void @arraylist_free_int(%struct.ArrayListInt* %t32)
  call i32 @getchar()
  ret i32 0
}
