    declare i32 @printf(i8*, ...)
    declare i32 @getchar()
    declare void @printString(%String*)
    declare i8* @malloc(i64)
    declare void @setString(%String*, i8*)

    @.strInt = private constant [4 x i8] c"%d\0A\00"
    @.strDouble = private constant [4 x i8] c"%f\0A\00"
    @.strStr = private constant [4 x i8] c"%s\0A\00"
    declare %String* @createString(i8*)
    declare i8* @arraylist_create(i64)
    declare void @clearList(%ArrayList*)
    declare void @freeList(%ArrayList*)

    %String = type { i8*, i64 }
    %ArrayList = type opaque
    declare i32 @inputInt(i8*)
    declare double @inputDouble(i8*)
    declare i1 @inputBool(i8*)
    declare i8* @inputString(i8*)
    %struct.ArrayListDouble = type { double*, i64, i64 }
    declare %struct.ArrayListDouble* @arraylist_create_double(i64)
    declare void @arraylist_add_double(%struct.ArrayListDouble*, double)
    declare void @arraylist_addAll_double(%struct.ArrayListDouble*, double*, i64)
    declare void @arraylist_print_double(%struct.ArrayListDouble*)
    declare double  @arraylist_get_double(%struct.ArrayListDouble*, i64, double*)
    declare void @arraylist_clear_double(%struct.ArrayListDouble*)
    declare void @arraylist_remove_double(%struct.ArrayListDouble*, i64)
    declare void @arraylist_free_double(%struct.ArrayListDouble*)
    declare i32  @arraylist_size_double(%struct.ArrayListDouble*)
    %struct.ArrayListBool = type { i1*, i64, i64 }
    declare %struct.ArrayListBool* @arraylist_create_bool(i64)
    declare void @arraylist_add_bool(%struct.ArrayListBool*, i1)
    declare void @arraylist_addAll_bool(%struct.ArrayListBool*, i1*, i64)
    declare void @arraylist_print_bool(%struct.ArrayListBool*)
    declare void @arraylist_clear_bool(%struct.ArrayListBool*)
    declare void @arraylist_remove_bool(%struct.ArrayListBool*, i64)
    declare void @arraylist_free_bool(%struct.ArrayListBool*)
    declare i1 @arraylist_get_bool(%struct.ArrayListBool*, i64, i1*)

    @.strTrue = private constant [6 x i8] c"true\0A\00"
    @.strFalse = private constant [7 x i8] c"false\0A\00"
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
    declare void @removeItem(%ArrayList*, i64)
    declare i8* @getItem(%ArrayList*, i64)

@.str0 = private constant [7 x i8] c"halley\00"
@.str1 = private constant [6 x i8] c"misty\00"
@.str2 = private constant [1 x i8] c"\00"

define i32 @main() {
  ; VariableDeclarationNode
  %x = alloca i32
;;VAL:%x;;TYPE:i32
  %t0 = add i32 0, 10
;;VAL:%t0;;TYPE:i32
  store i32 %t0, i32* %x
  ; VariableDeclarationNode
  %y = alloca i32
;;VAL:%y;;TYPE:i32
  %t1 = add i32 0, 20
;;VAL:%t1;;TYPE:i32
  store i32 %t1, i32* %y
  ; VariableDeclarationNode
  %ints = alloca %struct.ArrayListInt*
;;VAL:%ints;;TYPE:%struct.ArrayListInt*
  %t2 = call %struct.ArrayListInt* @arraylist_create_int(i64 4)
  %t3 = alloca i32, i64 2
  %t4 = load i32, i32* %x
;;VAL:%t4;;TYPE:i32
  %t5 = getelementptr inbounds i32, i32* %t3, i64 0
  store i32 %t4, i32* %t5
  %t6 = load i32, i32* %y
;;VAL:%t6;;TYPE:i32
  %t7 = getelementptr inbounds i32, i32* %t3, i64 1
  store i32 %t6, i32* %t7
  call void @arraylist_addAll_int(%struct.ArrayListInt* %t2, i32* %t3, i64 2)
;;VAL:%t2;;TYPE:%struct.ArrayListInt*
  store %struct.ArrayListInt* %t2, %struct.ArrayListInt** %ints
  ; ListAddNode
  %t10 = load %struct.ArrayListInt*, %struct.ArrayListInt** %ints
;;VAL:%t10;;TYPE:%struct.ArrayListInt*
  %t11 = add i32 0, 30
;;VAL:%t11;;TYPE:i32
  call void @arraylist_add_int(%struct.ArrayListInt* %t10, i32 %t11)
;;VAL:%t10;;TYPE:%struct.ArrayListInt*
  ; PrintNode
  %t12 = load %struct.ArrayListInt*, %struct.ArrayListInt** %ints
  call void @arraylist_print_int(%struct.ArrayListInt* %t12)
  ; PrintNode
  %t14 = load %struct.ArrayListInt*, %struct.ArrayListInt** %ints
;;VAL:%t14;;TYPE:%struct.ArrayListInt*
  %t15 = call i32 @arraylist_size_int(%struct.ArrayListInt* %t14)
  
  call i32 (i8*, ...) @printf(i8* getelementptr ([4 x i8], [4 x i8]* @.strInt, i32 0, i32 0), i32 %t15)
  ; PrintNode
  %t17 = load %struct.ArrayListInt*, %struct.ArrayListInt** %ints
  %t18 = add i32 0, 1
  %t19 = zext i32 %t18 to i64
  %t20 = alloca i32
  %t21 = call i32 @arraylist_get_int(%struct.ArrayListInt* %t17, i64 %t19, i32* %t20)
  %t22 = load i32, i32* %t20
  call i32 (i8*, ...) @printf(i8* getelementptr ([4 x i8], [4 x i8]* @.strInt, i32 0, i32 0), i32 %t22)
  ; ListRemoveNode
  %t24 = load %struct.ArrayListInt*, %struct.ArrayListInt** %ints
  %t25 = add i32 0, 0
  %t26 = zext i32 %t25 to i64
  call void @arraylist_remove_int(%struct.ArrayListInt* %t24, i64 %t26)
  ; PrintNode
  %t27 = load %struct.ArrayListInt*, %struct.ArrayListInt** %ints
  call void @arraylist_print_int(%struct.ArrayListInt* %t27)
  ; VariableDeclarationNode
  %a = alloca double
;;VAL:%a;;TYPE:double
  %t28 = fadd double 0.0, 1.5
;;VAL:%t28;;TYPE:double
  store double %t28, double* %a
  ; VariableDeclarationNode
  %b = alloca double
;;VAL:%b;;TYPE:double
  %t29 = fadd double 0.0, 2.5
;;VAL:%t29;;TYPE:double
  store double %t29, double* %b
  ; VariableDeclarationNode
  %doubles = alloca %struct.ArrayListDouble*
;;VAL:%doubles;;TYPE:%struct.ArrayListDouble*
  %t30 = call %struct.ArrayListDouble* @arraylist_create_double(i64 4)
  %t31 = alloca double, i64 2
  %t32 = load double, double* %a
;;VAL:%t32;;TYPE:double
  %t33 = getelementptr inbounds double, double* %t31, i64 0
  store double %t32, double* %t33
  %t34 = load double, double* %b
;;VAL:%t34;;TYPE:double
  %t35 = getelementptr inbounds double, double* %t31, i64 1
  store double %t34, double* %t35
  call void @arraylist_addAll_double(%struct.ArrayListDouble* %t30, double* %t31, i64 2)
;;VAL:%t30;;TYPE:%struct.ArrayListDouble*
  store %struct.ArrayListDouble* %t30, %struct.ArrayListDouble** %doubles
  ; ListAddNode
  %t38 = load %struct.ArrayListDouble*, %struct.ArrayListDouble** %doubles
;;VAL:%t38;;TYPE:%struct.ArrayListDouble*
  %t39 = fadd double 0.0, 3.14
;;VAL:%t39;;TYPE:double
  call void @arraylist_add_double(%struct.ArrayListDouble* %t38, double %t39)
;;VAL:%t38;;TYPE:%struct.ArrayListDouble*
  ; PrintNode
  %t40 = load %struct.ArrayListDouble*, %struct.ArrayListDouble** %doubles
  call void @arraylist_print_double(%struct.ArrayListDouble* %t40)
  ; PrintNode
  %t42 = load %struct.ArrayListDouble*, %struct.ArrayListDouble** %doubles
;;VAL:%t42;;TYPE:%struct.ArrayListDouble*
  %t43 = call i32 @arraylist_size_double(%struct.ArrayListDouble* %t42)
  
  call i32 (i8*, ...) @printf(i8* getelementptr ([4 x i8], [4 x i8]* @.strInt, i32 0, i32 0), i32 %t43)
  ; PrintNode
  %t45 = load %struct.ArrayListDouble*, %struct.ArrayListDouble** %doubles
  %t46 = add i32 0, 0
  %t47 = zext i32 %t46 to i64
  %t48 = alloca double
  %t49 = call double @arraylist_get_double(%struct.ArrayListDouble* %t45, i64 %t47, double* %t48)
  %t50 = load double, double* %t48
  call i32 (i8*, ...) @printf(i8* getelementptr ([4 x i8], [4 x i8]* @.strDouble, i32 0, i32 0), double %t50)
  ; ListRemoveNode
  %t52 = load %struct.ArrayListDouble*, %struct.ArrayListDouble** %doubles
  %t53 = add i32 0, 1
  %t54 = zext i32 %t53 to i64
  call void @arraylist_remove_double(%struct.ArrayListDouble* %t52, i64 %t54)
  ; PrintNode
  %t55 = load %struct.ArrayListDouble*, %struct.ArrayListDouble** %doubles
  call void @arraylist_print_double(%struct.ArrayListDouble* %t55)
  ; VariableDeclarationNode
  %t = alloca i1
;;VAL:%t;;TYPE:i1
  %t56 = add i1 0, 1
;;VAL:%t56;;TYPE:i1
  store i1 %t56, i1* %t
  ; VariableDeclarationNode
  %f = alloca i1
;;VAL:%f;;TYPE:i1
  %t57 = add i1 0, 0
;;VAL:%t57;;TYPE:i1
  store i1 %t57, i1* %f
  ; VariableDeclarationNode
  %bools = alloca %struct.ArrayListBool*
;;VAL:%bools;;TYPE:%struct.ArrayListBool*
  %t58 = call %struct.ArrayListBool* @arraylist_create_bool(i64 4)
  %t59 = alloca i1, i64 2
  %t60 = load i1, i1* %t
;;VAL:%t60;;TYPE:i1
  %t61 = getelementptr inbounds i1, i1* %t59, i64 0
  store i1 %t60, i1* %t61
  %t62 = load i1, i1* %f
;;VAL:%t62;;TYPE:i1
  %t63 = getelementptr inbounds i1, i1* %t59, i64 1
  store i1 %t62, i1* %t63
  call void @arraylist_addAll_bool(%struct.ArrayListBool* %t58, i1* %t59, i64 2)
;;VAL:%t58;;TYPE:%struct.ArrayListBool*
  store %struct.ArrayListBool* %t58, %struct.ArrayListBool** %bools
  ; ListAddNode
  %t66 = load %struct.ArrayListBool*, %struct.ArrayListBool** %bools
;;VAL:%t66;;TYPE:%struct.ArrayListBool*
  %t67 = add i1 0, 1
;;VAL:%t67;;TYPE:i1
  call void @arraylist_add_bool(%struct.ArrayListBool* %t66, i1%t67)
;;VAL:%t66;;TYPE:struct.ArrayListBool*
  ; PrintNode
  %t68 = load %struct.ArrayListBool*, %struct.ArrayListBool** %bools
  call void @arraylist_print_bool(%struct.ArrayListBool* %t68)
  ; PrintNode
  %t70 = load %struct.ArrayListBool*, %struct.ArrayListBool** %bools
  %t71 = add i32 0, 2
  %t72 = zext i32 %t71 to i64
  %t73 = alloca i1
  %t74 = call i1 @arraylist_get_bool(%struct.ArrayListBool* %t70, i64 %t72, i1* %t73)
  %t75 = load i1, i1* %t73
  br i1 %t75, label %bool_true_0, label %bool_false_1
bool_true_0:
  call i32 (i8*, ...) @printf(i8* getelementptr ([6 x i8], [6 x i8]* @.strTrue, i32 0, i32 0))
  br label %bool_end_2
bool_false_1:
  call i32 (i8*, ...) @printf(i8* getelementptr ([7 x i8], [7 x i8]* @.strFalse, i32 0, i32 0))
  br label %bool_end_2
bool_end_2:
  ; ListRemoveNode
  %t77 = load %struct.ArrayListBool*, %struct.ArrayListBool** %bools
  %t78 = add i32 0, 1
  %t79 = zext i32 %t78 to i64
  call void @arraylist_remove_bool(%struct.ArrayListBool* %t77, i64 %t79)
  ; PrintNode
  %t80 = load %struct.ArrayListBool*, %struct.ArrayListBool** %bools
  call void @arraylist_print_bool(%struct.ArrayListBool* %t80)
  ; VariableDeclarationNode
  %nome = alloca %String*
;;VAL:%nome;;TYPE:%String*
  %t81 = call i8* @malloc(i64 ptrtoint (%String* getelementptr (%String, %String* null, i32 1) to i64))
  %t82 = bitcast i8* %t81 to %String*
  %t83 = bitcast [7 x i8]* @.str0 to i8*
  %t84 = getelementptr inbounds %String, %String* %t82, i32 0, i32 0
  store i8* %t83, i8** %t84
  %t85 = getelementptr inbounds %String, %String* %t82, i32 0, i32 1
  store i64 6, i64* %t85
  store %String* %t82, %String** %nome
  ; VariableDeclarationNode
  %outro = alloca %String*
;;VAL:%outro;;TYPE:%String*
  %t86 = call i8* @malloc(i64 ptrtoint (%String* getelementptr (%String, %String* null, i32 1) to i64))
  %t87 = bitcast i8* %t86 to %String*
  %t88 = bitcast [6 x i8]* @.str1 to i8*
  %t89 = getelementptr inbounds %String, %String* %t87, i32 0, i32 0
  store i8* %t88, i8** %t89
  %t90 = getelementptr inbounds %String, %String* %t87, i32 0, i32 1
  store i64 5, i64* %t90
  store %String* %t87, %String** %outro
  ; VariableDeclarationNode
  %teste = alloca %String*
;;VAL:%teste;;TYPE:%String*
  %t92 = call i8* @inputString(i8* null)
  %t93 = call %String* @createString(i8* %t92)
;;VAL:%t93;;TYPE:%String
  store %String* %t93, %String** %teste
  ; VariableDeclarationNode
  %nomes = alloca i8*
;;VAL:%nomes;;TYPE:i8*
  %t94 = call i8* @arraylist_create(i64 4)
  %t95 = bitcast i8* %t94 to %ArrayList*
  %t96 = load %String*, %String** %nome
;;VAL:%t96;;TYPE:%String*
  call void @arraylist_add_String(%ArrayList* %t95, %String* %t96)
  %t97 = load %String*, %String** %outro
;;VAL:%t97;;TYPE:%String*
  call void @arraylist_add_String(%ArrayList* %t95, %String* %t97)
;;VAL:%t94;;TYPE:i8*
  store i8* %t94, i8** %nomes
  ; ListAddNode
  %t98 = load i8*, i8** %nomes
;;VAL:%t98;;TYPE:i8*
  %t100 = bitcast i8* %t98 to %ArrayList*
  %t99 = load %String*, %String** %teste
;;VAL:%t99;;TYPE:%String*
  call void @arraylist_add_String(%ArrayList* %t100, %String* %t99)
;;VAL:%t100;;TYPE:%ArrayList*
  ; PrintNode
  %t101 = load i8*, i8** %nomes
  %t102 = bitcast i8* %t101 to %ArrayList*
  call void @arraylist_print_string(%ArrayList* %t102)
  ; PrintNode
  %t103 = load i8*, i8** %nomes
  %t104 = bitcast i8* %t103 to %ArrayList*
  %t105 = add i32 0, 2
  %t106 = zext i32 %t105 to i64
  %t107 = call i8* @getItem(%ArrayList* %t104, i64 %t106)
  call i32 (i8*, ...) @printf(i8* getelementptr ([4 x i8], [4 x i8]* @.strStr, i32 0, i32 0), i8* %t107)
  ; ListRemoveNode
  %t108 = load i8*, i8** %nomes
;;VAL:%t108;;TYPE:i8*
  %t109 = add i32 0, 0
;;VAL:%t109;;TYPE:i32
  %t110 = sext i32 %t109 to i64
  %t111 = bitcast i8* %t108 to %ArrayList*
  call void @removeItem(%ArrayList* %t111, i64 %t110)
  ; PrintNode
  %t112 = load i8*, i8** %nomes
  %t113 = bitcast i8* %t112 to %ArrayList*
  call void @arraylist_print_string(%ArrayList* %t113)
  ; ListClearNode
  %t114 = load i8*, i8** %nomes
;;VAL:%t114;;TYPE:i8*
  %t115 = bitcast i8* %t114 to %ArrayList*
  call void @clearList(%ArrayList* %t115)
;;VAL:%t115;;TYPE:%ArrayList*
  ; PrintNode
  %t116 = load i8*, i8** %nomes
  %t117 = bitcast i8* %t116 to %ArrayList*
  call void @arraylist_print_string(%ArrayList* %t117)
  ; === Free das listas alocadas ===
  %t118 = load %struct.ArrayListBool*, %struct.ArrayListBool** %bools
  call void @arraylist_free_bool(%struct.ArrayListBool* %t118)
  %t119 = load %struct.ArrayListInt*, %struct.ArrayListInt** %ints
  call void @arraylist_free_int(%struct.ArrayListInt* %t119)
  %t120 = load %struct.ArrayListDouble*, %struct.ArrayListDouble** %doubles
  call void @arraylist_free_double(%struct.ArrayListDouble* %t120)
  %t121 = load i8*, i8** %nomes
  %t122 = bitcast i8* %t121 to %ArrayList*
  call void @freeList(%ArrayList* %t122)
  call i32 @getchar()
  ret i32 0
}
