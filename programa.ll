declare i32 @printf(i8*, ...)
declare i32 @getchar()
declare i8* @malloc(i64)
declare i8* @arraylist_create(i64)

declare void @arraylist_add_int(i8*, i32)
declare void @arraylist_add_double(i8*, double)
declare void @arraylist_add_string(i8*, i8*)

declare void @arraylist_print_int(i8*)
declare void @arraylist_print_double(i8*)
declare void @arraylist_print_string(i8*)

declare void @freeList(%ArrayList*)

@.strInt = private constant [4 x i8] c"%d\0A\00"
@.strDouble = private constant [4 x i8] c"%f\0A\00"
@.strStr = private constant [4 x i8] c"%s\0A\00"

%String = type { i8*, i64 }
%ArrayList = type opaque

@.str0 = private constant [36 x i8] c"Erro: factorial de numero negativo!\00"
@.str1 = private constant [6 x i8] c"teste\00"
@.str2 = private constant [17 x i8] c"clebinho xuxucao\00"
@.str3 = private constant [7 x i8] c"halley\00"

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

define i32 @main() {
  ; PrintNode
  %t15 = add i32 0, 5
;;VAL:%t15;;TYPE:i32
  %t16 = call i32 @factorial(i32 %t15)
  call i32 (i8*, ...) @printf(i8* getelementptr ([4 x i8], [4 x i8]* @.strInt, i32 0, i32 0), i32 %t16)
  ; VariableDeclarationNode
  %nomes = alloca i8*
;;VAL:%nomes;;TYPE:i8*
  %t17 = call i8* @arraylist_create(i64 4)
  %t18 = bitcast [6 x i8]* @.str1 to i8*
;;VAL:%t18;;TYPE:i8*
  call void @arraylist_add_string(i8* %t17, i8* %t18)
  %t19 = bitcast [17 x i8]* @.str2 to i8*
;;VAL:%t19;;TYPE:i8*
  call void @arraylist_add_string(i8* %t17, i8* %t19)
;;VAL:%t17;;TYPE:i8*
  store i8* %t17, i8** %nomes
  ; VariableDeclarationNode
  %nome = alloca %String
;;VAL:%nome;;TYPE:%String*
  %t20 = bitcast [7 x i8]* @.str3 to i8*
;;VAL:%t20;;TYPE:i8*
  %t21 = getelementptr inbounds %String, %String* %nome, i32 0, i32 0
  store i8* %t20, i8** %t21
  %t22 = getelementptr inbounds %String, %String* %nome, i32 0, i32 1
  store i64 6, i64* %t22
  ; VariableDeclarationNode
  %numeros = alloca i8*
;;VAL:%numeros;;TYPE:i8*
  %t23 = call i8* @arraylist_create(i64 4)
  %t24 = add i32 0, 3
;;VAL:%t24;;TYPE:i32
  call void @arraylist_add_int(i8* %t23, i32 %t24)
  %t25 = add i32 0, 4
;;VAL:%t25;;TYPE:i32
  call void @arraylist_add_int(i8* %t23, i32 %t25)
  %t26 = add i32 0, 55
;;VAL:%t26;;TYPE:i32
  call void @arraylist_add_int(i8* %t23, i32 %t26)
;;VAL:%t23;;TYPE:i8*
  store i8* %t23, i8** %numeros
  ; PrintNode
  %t27 = load i8*, i8** %numeros
  call void @arraylist_print_int(i8* %t27)
  ; PrintNode
  %t28 = load i8*, i8** %nomes
  call void @arraylist_print_string(i8* %t28)
  ; VariableDeclarationNode
  %flut = alloca i8*
;;VAL:%flut;;TYPE:i8*
  %t29 = call i8* @arraylist_create(i64 4)
  %t30 = fadd double 0.0, 3.14
;;VAL:%t30;;TYPE:double
  call void @arraylist_add_double(i8* %t29, double %t30)
  %t31 = fadd double 0.0, 4.4
;;VAL:%t31;;TYPE:double
  call void @arraylist_add_double(i8* %t29, double %t31)
;;VAL:%t29;;TYPE:i8*
  store i8* %t29, i8** %flut
  ; PrintNode
  %t32 = load i8*, i8** %flut
  call void @arraylist_print_double(i8* %t32)
  ; PrintNode
  %t33 = getelementptr inbounds %String, %String* %nome, i32 0, i32 0
  %t34 = load i8*, i8** %t33
  call i32 (i8*, ...) @printf(i8* getelementptr ([4 x i8], [4 x i8]* @.strStr, i32 0, i32 0), i8* %t34)
  ; === Free das listas alocadas ===
  %t35 = load i8*, i8** %numeros
  %t36 = bitcast i8* %t35 to %ArrayList*
  call void @freeList(%ArrayList* %t36)
  %t37 = load i8*, i8** %flut
  %t38 = bitcast i8* %t37 to %ArrayList*
  call void @freeList(%ArrayList* %t38)
  %t39 = load i8*, i8** %nomes
  %t40 = bitcast i8* %t39 to %ArrayList*
  call void @freeList(%ArrayList* %t40)
  ; === Wait for key press before exiting ===
  call i32 @getchar()
  ret i32 0
}
