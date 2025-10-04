declare i32 @printf(i8*, ...)
declare i32 @getchar()
declare i8* @malloc(i64)
declare i8* @arraylist_create(i64)

declare i32 @inputInt(i8*)
declare double @inputDouble(i8*)
declare i1 @inputBool(i8*)
declare i8* @inputString(i8*)

declare void @arraylist_add_int(%ArrayList*, i32)
declare void @arraylist_add_double(%ArrayList*, double)
declare void @arraylist_add_string(%ArrayList*, i8*)
declare void @arraylist_add_String(%ArrayList*, %String*)
declare void @arraylist_addAll_string(%ArrayList* %list, i8** %strings, i64 %n)
declare void @arraylist_addAll_String(%ArrayList* %list, %String** %strings, i64 %n)
declare i8* @getItem(%ArrayList*, i64)
declare void @arraylist_print_int(%ArrayList*)
declare void @arraylist_print_double(%ArrayList*)
declare void @arraylist_print_string(%ArrayList*)
declare void @clearList(%ArrayList*)
declare void @freeList(%ArrayList*)

@.strInt = private constant [4 x i8] c"%d\0A\00"
@.strDouble = private constant [4 x i8] c"%f\0A\00"
@.strStr = private constant [4 x i8] c"%s\0A\00"

%String = type { i8*, i64 }
%ArrayList = type opaque

@.str0 = private constant [6 x i8] c"teste\00"
@.str1 = private constant [13 x i8] c"space is ...\00"

; === Função: teste ===
define void @teste(i8* %list) {
entry:
  %list_addr = alloca i8*
  store i8* %list, i8** %list_addr
;;VAL:%list_addr;;TYPE:i8*
  %t0 = load i8*, i8** %list_addr
;;VAL:%t0;;TYPE:i8*
  %t1 = bitcast i8* %t0 to %ArrayList*
  %t2 = add i32 0, 13
;;VAL:%t2;;TYPE:i32
  call void @arraylist_add_int(%ArrayList* %t1, i32 %t2)
;;VAL:%t1;;TYPE:%ArrayList*
  ret void
}

; === Função: tes ===
define void @tes(i8* %list) {
entry:
  %list_addr = alloca i8*
  store i8* %list, i8** %list_addr
;;VAL:%list_addr;;TYPE:i8*
  %t3 = load i8*, i8** %list_addr
;;VAL:%t3;;TYPE:i8*
  %t4 = bitcast i8* %t3 to %ArrayList*
  %t5 = bitcast [6 x i8]* @.str0 to i8*
;;VAL:%t5;;TYPE:i8*
  call void @arraylist_add_string(%ArrayList* %t4, i8* getelementptr ([6 x i8], [6 x i8]* @.str0, i32 0, i32 0))
  %t6 = bitcast [13 x i8]* @.str1 to i8*
;;VAL:%t6;;TYPE:i8*
  call void @arraylist_add_string(%ArrayList* %t4, i8* getelementptr ([13 x i8], [13 x i8]* @.str1, i32 0, i32 0))
;;VAL:%t4;;TYPE:%ArrayList*
  ret void
}

define i32 @main() {
  ; VariableDeclarationNode
  %nomes = alloca i8*
;;VAL:%nomes;;TYPE:i8*
  %t7 = call i8* @arraylist_create(i64 4)
  %t8 = bitcast i8* %t7 to %ArrayList*
;;VAL:%t7;;TYPE:i8*
  store i8* %t7, i8** %nomes
  ; VariableDeclarationNode
  %numeros = alloca i8*
;;VAL:%numeros;;TYPE:i8*
  %t9 = call i8* @arraylist_create(i64 4)
  %t10 = bitcast i8* %t9 to %ArrayList*
  %t11 = add i32 0, 1
;;VAL:%t11;;TYPE:i32
  call void @arraylist_add_int(%ArrayList* %t10, i32 %t11)
  %t12 = add i32 0, 2
;;VAL:%t12;;TYPE:i32
  call void @arraylist_add_int(%ArrayList* %t10, i32 %t12)
  %t13 = add i32 0, 3
;;VAL:%t13;;TYPE:i32
  call void @arraylist_add_int(%ArrayList* %t10, i32 %t13)
;;VAL:%t9;;TYPE:i8*
  store i8* %t9, i8** %numeros
  ; PrintNode
  %t14 = load i8*, i8** %numeros
  %t15 = bitcast i8* %t14 to %ArrayList*
  call void @arraylist_print_int(%ArrayList* %t15)
  ; FunctionCallNode
  %t16 = load i8*, i8** %numeros
;;VAL:%t16;;TYPE:i8*
  call void @teste(i8* %t16)
;;VAL:void;;TYPE:void
  ; FunctionCallNode
  %t17 = load i8*, i8** %nomes
;;VAL:%t17;;TYPE:i8*
  call void @tes(i8* %t17)
;;VAL:void;;TYPE:void
  ; PrintNode
  %t18 = load i8*, i8** %nomes
  %t19 = bitcast i8* %t18 to %ArrayList*
  call void @arraylist_print_string(%ArrayList* %t19)
  ; PrintNode
  %t20 = load i8*, i8** %numeros
  %t21 = bitcast i8* %t20 to %ArrayList*
  call void @arraylist_print_int(%ArrayList* %t21)
  ; === Free das listas alocadas ===
  %t22 = load i8*, i8** %numeros
  %t23 = bitcast i8* %t22 to %ArrayList*
  call void @freeList(%ArrayList* %t23)
  %t24 = load i8*, i8** %nomes
  %t25 = bitcast i8* %t24 to %ArrayList*
  call void @freeList(%ArrayList* %t25)
  ; === Wait for key press before exiting ===
  call i32 @getchar()
  ret i32 0
}
