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
    declare void @arraylist_addAll_double(%ArrayList*, double*, i64)
    declare void @arraylist_add_double(%ArrayList*, double)
    declare void @arraylist_print_double(%ArrayList*)

    declare void @arraylist_add_int(%ArrayList*, i32)
    declare void @arraylist_addAll_int(%ArrayList*, i32*, i64)
    declare void @arraylist_print_int(%ArrayList*)

    declare void @arraylist_add_string(%ArrayList*, i8*)
    declare void @arraylist_addAll_string(%ArrayList*, i8**, i64)
    declare void @arraylist_print_string(%ArrayList*)
    declare void @arraylist_add_String(%ArrayList*, %String*)
    declare void @arraylist_addAll_String(%ArrayList*, %String**, i64)

@.str0 = private constant [5 x i8] c"zard\00"
@.str1 = private constant [7 x i8] c"halley\00"
@.str2 = private constant [2 x i8] c"z\00"

define i32 @main() {
  ; VariableDeclarationNode
  %nome = alloca %String*
;;VAL:%nome;;TYPE:%String*
  %t0 = call i8* @malloc(i64 ptrtoint (%String* getelementptr (%String, %String* null, i32 1) to i64))
  %t1 = bitcast i8* %t0 to %String*
  %t2 = bitcast [5 x i8]* @.str0 to i8*
  %t3 = getelementptr inbounds %String, %String* %t1, i32 0, i32 0
  store i8* %t2, i8** %t3
  %t4 = getelementptr inbounds %String, %String* %t1, i32 0, i32 1
  store i64 4, i64* %t4
  store %String* %t1, %String** %nome
  ; VariableDeclarationNode
  %nombre = alloca %String*
;;VAL:%nombre;;TYPE:%String*
  %t5 = call i8* @malloc(i64 ptrtoint (%String* getelementptr (%String, %String* null, i32 1) to i64))
  %t6 = bitcast i8* %t5 to %String*
  %t7 = bitcast [7 x i8]* @.str1 to i8*
  %t8 = getelementptr inbounds %String, %String* %t6, i32 0, i32 0
  store i8* %t7, i8** %t8
  %t9 = getelementptr inbounds %String, %String* %t6, i32 0, i32 1
  store i64 6, i64* %t9
  store %String* %t6, %String** %nombre
  ; VariableDeclarationNode
  %numbers = alloca i8*
;;VAL:%numbers;;TYPE:i8*
  %t10 = call i8* @arraylist_create(i64 4)
  %t11 = bitcast i8* %t10 to %ArrayList*
;;VAL:%t10;;TYPE:i8*
  store i8* %t10, i8** %numbers
  ; ListAddAllNode
  %t12 = load i8*, i8** %numbers
;;VAL:%t12;;TYPE:i8*
  %t13 = bitcast i8* %t12 to %ArrayList*
  %t14 = fadd double 0.0, 3.13
;;VAL:%t14;;TYPE:double
  %t15 = alloca double, i64 4
  %t16 = fadd double 0.0, 3.13
;;VAL:%t16;;TYPE:double
  %t17 = getelementptr inbounds double, double* %t15, i64 0
  store double %t16, double* %t17
  %t18 = fadd double 0.0, 4.13
;;VAL:%t18;;TYPE:double
  %t19 = getelementptr inbounds double, double* %t15, i64 1
  store double %t18, double* %t19
  %t20 = fadd double 0.0, 55.1
;;VAL:%t20;;TYPE:double
  %t21 = getelementptr inbounds double, double* %t15, i64 2
  store double %t20, double* %t21
  %t22 = fadd double 0.0, 6.2
;;VAL:%t22;;TYPE:double
  %t23 = getelementptr inbounds double, double* %t15, i64 3
  store double %t22, double* %t23
  call void @arraylist_addAll_double(%ArrayList* %t13, double* %t15, i64 4)
;;VAL:%t13;;TYPE:%ArrayList*
  ; ListAddNode
  %t24 = load i8*, i8** %numbers
;;VAL:%t24;;TYPE:i8*
  %t25 = bitcast i8* %t24 to %ArrayList*
  %t26 = fadd double 0.0, 0.3
;;VAL:%t26;;TYPE:double
  call void @arraylist_add_double(%ArrayList* %t25, double %t26)
;;VAL:%t25;;TYPE:%ArrayList*
  ; PrintNode
  %t27 = load i8*, i8** %numbers
  %t28 = bitcast i8* %t27 to %ArrayList*
  call void @arraylist_print_double(%ArrayList* %t28)
  ; VariableDeclarationNode
  %numeros = alloca i8*
;;VAL:%numeros;;TYPE:i8*
  %t29 = call i8* @arraylist_create(i64 4)
  %t30 = bitcast i8* %t29 to %ArrayList*
;;VAL:%t29;;TYPE:i8*
  store i8* %t29, i8** %numeros
  ; ListAddNode
  %t31 = load i8*, i8** %numeros
;;VAL:%t31;;TYPE:i8*
  %t32 = bitcast i8* %t31 to %ArrayList*
  %t33 = add i32 0, 0
;;VAL:%t33;;TYPE:i32
  call void @arraylist_add_int(%ArrayList* %t32, i32 %t33)
;;VAL:%t32;;TYPE:%ArrayList*
  ; ListAddAllNode
  %t34 = load i8*, i8** %numeros
;;VAL:%t34;;TYPE:i8*
  %t35 = bitcast i8* %t34 to %ArrayList*
  %t36 = add i32 0, 3
;;VAL:%t36;;TYPE:i32
  %t37 = alloca i32, i64 6
  %t38 = add i32 0, 3
;;VAL:%t38;;TYPE:i32
  %t39 = getelementptr inbounds i32, i32* %t37, i64 0
  store i32 %t38, i32* %t39
  %t40 = add i32 0, 4
;;VAL:%t40;;TYPE:i32
  %t41 = getelementptr inbounds i32, i32* %t37, i64 1
  store i32 %t40, i32* %t41
  %t42 = add i32 0, 55
;;VAL:%t42;;TYPE:i32
  %t43 = getelementptr inbounds i32, i32* %t37, i64 2
  store i32 %t42, i32* %t43
  %t44 = add i32 0, 6
;;VAL:%t44;;TYPE:i32
  %t45 = getelementptr inbounds i32, i32* %t37, i64 3
  store i32 %t44, i32* %t45
  %t46 = add i32 0, 7
;;VAL:%t46;;TYPE:i32
  %t47 = getelementptr inbounds i32, i32* %t37, i64 4
  store i32 %t46, i32* %t47
  %t48 = add i32 0, 8
;;VAL:%t48;;TYPE:i32
  %t49 = getelementptr inbounds i32, i32* %t37, i64 5
  store i32 %t48, i32* %t49
  call void @arraylist_addAll_int(%ArrayList* %t35, i32* %t37, i64 6)
;;VAL:%t35;;TYPE:%ArrayList*
  ; PrintNode
  %t50 = load i8*, i8** %numeros
  %t51 = bitcast i8* %t50 to %ArrayList*
  call void @arraylist_print_int(%ArrayList* %t51)
  ; VariableDeclarationNode
  %nomes = alloca i8*
;;VAL:%nomes;;TYPE:i8*
  %t52 = call i8* @arraylist_create(i64 4)
  %t53 = bitcast i8* %t52 to %ArrayList*
  %t54 = bitcast [2 x i8]* @.str2 to i8*
;;VAL:%t54;;TYPE:i8*
  call void @arraylist_add_string(%ArrayList* %t53, i8* %t54)
  %t55 = load %String*, %String** %nombre
;;VAL:%t55;;TYPE:%String*
  %t56 = load %String*, %String** %nombre
  call void @arraylist_add_String(%ArrayList* %t53, %String* %t56)
;;VAL:%t52;;TYPE:i8*
  store i8* %t52, i8** %nomes
  ; ListAddAllNode
  %t57 = load i8*, i8** %nomes
;;VAL:%t57;;TYPE:i8*
  %t58 = bitcast i8* %t57 to %ArrayList*
  %t59 = load %String*, %String** %nome
;;VAL:%t59;;TYPE:%String*
  %t60 = alloca %String*, i64 2
  %t61 = load %String*, %String** %nome
;;VAL:%t61;;TYPE:%String*
  %t62 = getelementptr inbounds %String*, %String** %t60, i64 0
  store %String* %t61, %String** %t62
  %t63 = load %String*, %String** %nombre
;;VAL:%t63;;TYPE:%String*
  %t64 = getelementptr inbounds %String*, %String** %t60, i64 1
  store %String* %t63, %String** %t64
  call void @arraylist_addAll_String(%ArrayList* %t58, %String** %t60, i64 2)
;;VAL:%t58;;TYPE:%ArrayList*
  ; PrintNode
  %t65 = load i8*, i8** %nomes
  %t66 = bitcast i8* %t65 to %ArrayList*
  call void @arraylist_print_string(%ArrayList* %t66)
  ; === Free das listas alocadas ===
  %t67 = load i8*, i8** %numeros
  %t68 = bitcast i8* %t67 to %ArrayList*
  call void @freeList(%ArrayList* %t68)
  %t69 = load i8*, i8** %numbers
  %t70 = bitcast i8* %t69 to %ArrayList*
  call void @freeList(%ArrayList* %t70)
  %t71 = load i8*, i8** %nomes
  %t72 = bitcast i8* %t71 to %ArrayList*
  call void @freeList(%ArrayList* %t72)
  call i32 @getchar()
  ret i32 0
}
