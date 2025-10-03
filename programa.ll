declare i32 @printf(i8*, ...)
declare i32 @getchar()
declare i8* @malloc(i64)
declare i8* @arraylist_create(i64)

declare void @arraylist_add_int(%ArrayList*, i32)
declare void @arraylist_add_double(%ArrayList*, double)
declare void @arraylist_add_string(%ArrayList*, i8*)
declare i8* @getItem(%ArrayList*, i64)
declare void @arraylist_print_int(%ArrayList*)
declare void @arraylist_print_double(%ArrayList*)
declare void @arraylist_print_string(%ArrayList*)
declare void @clearList(%ArrayList*)
declare void @freeList(%ArrayList*)
declare void @arraylist_add_String(%ArrayList*, %String*)
@.strInt = private constant [4 x i8] c"%d\0A\00"
@.strDouble = private constant [4 x i8] c"%f\0A\00"
@.strStr = private constant [4 x i8] c"%s\0A\00"

%String = type { i8*, i64 }
%ArrayList = type opaque

@.str0 = private constant [7 x i8] c"halley\00"
@.str1 = private constant [5 x i8] c"zard\00"
@.str2 = private constant [6 x i8] c"teste\00"
@.str3 = private constant [30 x i8] c"teste de elementos com espaco\00"

define i32 @main() {
  ; VariableDeclarationNode
  %nome = alloca %String
;;VAL:%nome;;TYPE:%String*
  %t0 = bitcast [7 x i8]* @.str0 to i8*
;;VAL:%t0;;TYPE:i8*
  %t1 = getelementptr inbounds %String, %String* %nome, i32 0, i32 0
  store i8* %t0, i8** %t1
  %t2 = getelementptr inbounds %String, %String* %nome, i32 0, i32 1
  store i64 6, i64* %t2
  ; VariableDeclarationNode
  %teste = alloca %String
;;VAL:%teste;;TYPE:%String*
  %t3 = bitcast [5 x i8]* @.str1 to i8*
;;VAL:%t3;;TYPE:i8*
  %t4 = getelementptr inbounds %String, %String* %teste, i32 0, i32 0
  store i8* %t3, i8** %t4
  %t5 = getelementptr inbounds %String, %String* %teste, i32 0, i32 1
  store i64 4, i64* %t5
  ; VariableDeclarationNode
  %numeros = alloca i8*
;;VAL:%numeros;;TYPE:i8*
  %t6 = call i8* @arraylist_create(i64 4)
  %t7 = bitcast i8* %t6 to %ArrayList*
;;VAL:%t6;;TYPE:i8*
  store i8* %t6, i8** %numeros
  ; ListAddNode
  %t8 = load i8*, i8** %numeros
;;VAL:%t8;;TYPE:i8*
  %t9 = bitcast i8* %t8 to %ArrayList*
  %t10 = add i32 0, 14
;;VAL:%t10;;TYPE:i32
  call void @arraylist_add_int(%ArrayList* %t9, i32 %t10)
;;VAL:%t9;;TYPE:%ArrayList*
  ; VariableDeclarationNode
  %nomes = alloca i8*
;;VAL:%nomes;;TYPE:i8*
  %t11 = call i8* @arraylist_create(i64 4)
  %t12 = bitcast i8* %t11 to %ArrayList*
  %t13 = load %String, %String* %teste
;;VAL:%t13;;TYPE:%String
  call void @arraylist_add_String(%ArrayList* %t12, %String* %teste)
  %t14 = bitcast [6 x i8]* @.str2 to i8*
;;VAL:%t14;;TYPE:i8*
  call void @arraylist_add_string(%ArrayList* %t12, i8* %t14)
  %t15 = bitcast [30 x i8]* @.str3 to i8*
;;VAL:%t15;;TYPE:i8*
  call void @arraylist_add_string(%ArrayList* %t12, i8* %t15)
  %t16 = load %String, %String* %nome
;;VAL:%t16;;TYPE:%String
  call void @arraylist_add_String(%ArrayList* %t12, %String* %nome)
;;VAL:%t11;;TYPE:i8*
  store i8* %t11, i8** %nomes
  ; ListClearNode
  %t17 = load i8*, i8** %numeros
;;VAL:%t17;;TYPE:i8*
  %t18 = bitcast i8* %t17 to %ArrayList*
  call void @clearList(%ArrayList* %t18)
;;VAL:%t18;;TYPE:%ArrayList*
  ; AssignmentNode
  ; PrintNode
  %t19 = load i8*, i8** %numeros
  %t20 = bitcast i8* %t19 to %ArrayList*
  call void @arraylist_print_int(%ArrayList* %t20)
  ; PrintNode
  %t21 = load i8*, i8** %nomes
  %t22 = bitcast i8* %t21 to %ArrayList*
  %t23 = add i32 0, 0
  %t24 = zext i32 %t23 to i64
  %t25 = call i8* @getItem(%ArrayList* %t22, i64 %t24)
  call i32 (i8*, ...) @printf(i8* getelementptr ([4 x i8], [4 x i8]* @.strStr, i32 0, i32 0), i8* %t25)
  ; PrintNode
  %t26 = load i8*, i8** %nomes
  %t27 = bitcast i8* %t26 to %ArrayList*
  call void @arraylist_print_string(%ArrayList* %t27)
  ; === Free das listas alocadas ===
  %t28 = load i8*, i8** %numeros
  %t29 = bitcast i8* %t28 to %ArrayList*
  call void @freeList(%ArrayList* %t29)
  %t30 = load i8*, i8** %nomes
  %t31 = bitcast i8* %t30 to %ArrayList*
  call void @freeList(%ArrayList* %t31)
  ; === Wait for key press before exiting ===
  call i32 @getchar()
  ret i32 0
}
