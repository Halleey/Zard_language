declare i32 @printf(i8*, ...)
declare i32 @getchar()
declare i8* @malloc(i64)
declare i8* @arraylist_create(i64)

declare i32 @inputInt(i8*)
declare double @inputDouble(i8*)
declare i1 @inputBool(i8*)
declare i8* @inputString(i8*)
declare %String* @createString(i8*) 
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

@.str0 = private constant [36 x i8] c"Erro: factorial de numero negativo!\00"
@.str1 = private constant [12 x i8] c"hello world\00"

; === Função: factorial ===
define i32 @factorial(i32 %n) {
entry:
  %n_addr = alloca i32
  store i32 %n, i32* %n_addr
;;VAL:%n_addr;;TYPE:i32
  %t0 = load i32, i32* %n_addr
;;VAL:%t0;;TYPE:i32

  %t1 = add i32 0, 0
;;VAL:%t1;;TYPE:i32

  %t2 = icmp slt i32 %t0, %t1
;;VAL:%t2;;TYPE:i1

  br i1 %t2, label %then_0, label %endif_0
then_0:
  %t3 = getelementptr inbounds [36 x i8], [36 x i8]* @.str0, i32 0, i32 0
  call i32 (i8*, ...) @printf(i8* getelementptr ([4 x i8], [4 x i8]* @.strStr, i32 0, i32 0), i8* %t3)
  %t4 = add i32 0, 0
;;VAL:%t4;;TYPE:i32
  ret i32 %t4
  br label %endif_0
endif_0:
  %t5 = load i32, i32* %n_addr
;;VAL:%t5;;TYPE:i32

  %t6 = add i32 0, 0
;;VAL:%t6;;TYPE:i32

  %t7 = icmp eq i32 %t5, %t6
;;VAL:%t7;;TYPE:i1

  br i1 %t7, label %then_1, label %endif_1
then_1:
  %t8 = add i32 0, 1
;;VAL:%t8;;TYPE:i32
  ret i32 %t8
  br label %endif_1
endif_1:
  %t9 = load i32, i32* %n_addr
;;VAL:%t9;;TYPE:i32

  %t10 = load i32, i32* %n_addr
;;VAL:%t10;;TYPE:i32

  %t11 = add i32 0, 1
;;VAL:%t11;;TYPE:i32

  %t12 = sub i32 %t10, %t11
;;VAL:%t12;;TYPE:i32
  %t13 = call i32 @factorial(i32 %t12)
;;VAL:%t13;;TYPE:i32

  %t14 = mul i32 %t9, %t13
;;VAL:%t14;;TYPE:i32
  ret i32 %t14
}

; === Função: teste ===
define void @teste(i8* %list) {
entry:
  %list_addr = alloca i8*
  store i8* %list, i8** %list_addr
;;VAL:%list_addr;;TYPE:i8*
  %t15 = load i8*, i8** %list_addr
;;VAL:%t15;;TYPE:i8*
  %t16 = bitcast i8* %t15 to %ArrayList*
  %t17 = bitcast [12 x i8]* @.str1 to i8*
;;VAL:%t17;;TYPE:i8*
  call void @arraylist_add_string(%ArrayList* %t16, i8* getelementptr ([12 x i8], [12 x i8]* @.str1, i32 0, i32 0))
;;VAL:%t16;;TYPE:%ArrayList*
  ret void
}

define i32 @main() {
  ; VariableDeclarationNode
  %test = alloca i8*
;;VAL:%test;;TYPE:i8*
  %t18 = call i8* @arraylist_create(i64 4)
  %t19 = bitcast i8* %t18 to %ArrayList*
;;VAL:%t18;;TYPE:i8*
  store i8* %t18, i8** %test
  ; FunctionCallNode
  %t20 = load i8*, i8** %test
;;VAL:%t20;;TYPE:i8*
  call void @teste(i8* %t20)
;;VAL:void;;TYPE:void
  ; PrintNode
  %t21 = load i8*, i8** %test
  %t22 = bitcast i8* %t21 to %ArrayList*
  call void @arraylist_print_string(%ArrayList* %t22)
  ; === Free das listas alocadas ===
  %t23 = load i8*, i8** %test
  %t24 = bitcast i8* %t23 to %ArrayList*
  call void @freeList(%ArrayList* %t24)
  ; === Wait for key press before exiting ===
  call i32 @getchar()
  ret i32 0
}
