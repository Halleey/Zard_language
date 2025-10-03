declare i32 @printf(i8*, ...)
declare i32 @getchar()
declare i8* @malloc(i64)
declare i8* @arraylist_create(i64)

declare void @arraylist_add_int(%ArrayList*, i32)
declare void @arraylist_add_double(%ArrayList*, double)
declare void @arraylist_add_string(%ArrayList*, i8*)

declare void @arraylist_print_int(%ArrayList*)
declare void @arraylist_print_double(%ArrayList*)
declare void @arraylist_print_string(%ArrayList*)

declare void @freeList(%ArrayList*)

@.strInt = private constant [4 x i8] c"%d\0A\00"
@.strDouble = private constant [4 x i8] c"%f\0A\00"
@.strStr = private constant [4 x i8] c"%s\0A\00"

%String = type { i8*, i64 }
%ArrayList = type opaque

@.str0 = private constant [7 x i8] c"halley\00"
@.str1 = private constant [6 x i8] c"teste\00"
@.str2 = private constant [17 x i8] c"clebinho xuxucao\00"

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
  %numeros = alloca i8*
;;VAL:%numeros;;TYPE:i8*
  %t3 = call i8* @arraylist_create(i64 4)
  %t4 = bitcast i8* %t3 to %ArrayList*
;;VAL:%t3;;TYPE:i8*
  store i8* %t3, i8** %numeros
  ; ListAddNode
  %t5 = load i8*, i8** %numeros
;;VAL:%t5;;TYPE:i8*
  %t6 = bitcast i8* %t5 to %ArrayList*
  %t7 = add i32 0, 14
;;VAL:%t7;;TYPE:i32
  call void @arraylist_add_int(%ArrayList* %t6, i32 %t7)
;;VAL:%t6;;TYPE:%ArrayList*
  ; VariableDeclarationNode
  %nomes = alloca i8*
;;VAL:%nomes;;TYPE:i8*
  %t8 = call i8* @arraylist_create(i64 4)
  %t9 = bitcast i8* %t8 to %ArrayList*
  %t10 = bitcast [6 x i8]* @.str1 to i8*
;;VAL:%t10;;TYPE:i8*
  call void @arraylist_add_string(%ArrayList* %t9, i8* %t10)
  %t11 = bitcast [17 x i8]* @.str2 to i8*
;;VAL:%t11;;TYPE:i8*
  call void @arraylist_add_string(%ArrayList* %t9, i8* %t11)
  %t12 = load %String*, %String** %nome
;;VAL:%t12;;TYPE:%String*
  %t13 = getelementptr inbounds %String, %String* %t12, i32 0, i32 0
  %t14 = load i8*, i8** %t13
  call void @arraylist_add_string(%ArrayList* %t9, i8* %t14)
;;VAL:%t8;;TYPE:i8*
  store i8* %t8, i8** %nomes
  ; PrintNode
  %t15 = load i8*, i8** %numeros
  %t16 = bitcast i8* %t15 to %ArrayList*
  call void @arraylist_print_int(%ArrayList* %t16)
  ; PrintNode
  %t17 = load i8*, i8** %nomes
  %t18 = bitcast i8* %t17 to %ArrayList*
  call void @arraylist_print_string(%ArrayList* %t18)
  ; === Free das listas alocadas ===
  %t19 = load i8*, i8** %numeros
  %t20 = bitcast i8* %t19 to %ArrayList*
  call void @freeList(%ArrayList* %t20)
  %t21 = load i8*, i8** %nomes
  %t22 = bitcast i8* %t21 to %ArrayList*
  call void @freeList(%ArrayList* %t22)
  ; === Wait for key press before exiting ===
  call i32 @getchar()
  ret i32 0
}
