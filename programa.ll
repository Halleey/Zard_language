    declare i32 @printf(i8*, ...)
    declare i32 @getchar()
    @.strInt = private constant [4 x i8] c"%d\0A\00"
    @.strDouble = private constant [4 x i8] c"%f\0A\00"
    @.strStr = private constant [4 x i8] c"%s\0A\00"

    ; === Tipo opaco DynValue ===
    %DynValue = type opaque

    ; === Funções do runtime DynValue ===
    declare %DynValue* @createInt(i32)
    declare %DynValue* @createDouble(double)
    declare %DynValue* @createBool(i1)
    declare %DynValue* @createString(i8*)

    ; === Funções de conversão DynValue -> tipo primitivo ===
    declare i32 @dynToInt(%DynValue*)
    declare double @dynToDouble(%DynValue*)
    declare i1 @dynToBool(%DynValue*)
    declare i8* @dynToString(%DynValue*)

    ; === Função de input ===
    declare i32 @inputInt(i8*)
    declare double @inputDouble(i8*)
    declare i1 @inputBool(i8*)
    declare i8* @inputString(i8*)

; === Runtime de listas ===
%ArrayList = type opaque
declare i8* @arraylist_create(i64)
declare void @setItems(i8*, %DynValue*)
declare void @printList(i8*)
declare void @removeItem(%ArrayList*, i64)
declare void @clearList(%ArrayList*)
declare void @freeList(%ArrayList*)
declare i32 @size(%ArrayList*)
declare %DynValue* @getItem(%ArrayList*, i32)
declare void @printDynValue(%DynValue*)
declare void @addAll(%ArrayList*, %DynValue**, i64)

@.str0 = private constant [34 x i8] c"Erro factorial de numero negativo\00"
@.str1 = private constant [12 x i8] c"hello world\00"
@.str2 = private constant [1 x i8] c"\00"
@.str3 = private constant [5 x i8] c"nome\00"
@.str4 = private constant [7 x i8] c"angela\00"

; === Função: soma ===
define i32 @soma(i32 %a, i32 %b) {
entry:
  %a.addr = alloca i32
;;VAL:%a.addr;;TYPE:i32
  store i32 %a, i32* %a.addr
  %b.addr = alloca i32
;;VAL:%b.addr;;TYPE:i32
  store i32 %b, i32* %b.addr
  %t0 = load i32, i32* %a.addr
;;VAL:%t0;;TYPE:i32

  %t1 = load i32, i32* %b.addr
;;VAL:%t1;;TYPE:i32

  %t2 = sub i32 %t0, %t1
;;VAL:%t2;;TYPE:i32
  ret i32 %t2
}

; === Função: bool ===
define i1 @bool() {
entry:
  %t3 = add i1 0, 1
;;VAL:%t3;;TYPE:i1
  ret i1 %t3
}

; === Função: factorial ===
define i32 @factorial(i32 %n) {
entry:
  %n.addr = alloca i32
;;VAL:%n.addr;;TYPE:i32
  store i32 %n, i32* %n.addr
  %t4 = load i32, i32* %n.addr
;;VAL:%t4;;TYPE:i32

  %t5 = add i32 0, 0
;;VAL:%t5;;TYPE:i32

  %t6 = icmp slt i32 %t4, %t5
;;VAL:%t6;;TYPE:i1

  br i1 %t6, label %then_0, label %endif_0
then_0:
  call i32 (i8*, ...) @printf(i8* getelementptr ([34 x i8], [34 x i8]* @.str0, i32 0, i32 0))
  %t7 = add i32 0, 0
;;VAL:%t7;;TYPE:i32
  ret i32 %t7
  br label %endif_0
endif_0:
  %t8 = load i32, i32* %n.addr
;;VAL:%t8;;TYPE:i32

  %t9 = add i32 0, 0
;;VAL:%t9;;TYPE:i32

  %t10 = icmp eq i32 %t8, %t9
;;VAL:%t10;;TYPE:i1

  br i1 %t10, label %then_1, label %endif_1
then_1:
  %t11 = add i32 0, 1
;;VAL:%t11;;TYPE:i32
  ret i32 %t11
  br label %endif_1
endif_1:
  %t12 = load i32, i32* %n.addr
;;VAL:%t12;;TYPE:i32

  %t13 = load i32, i32* %n.addr
;;VAL:%t13;;TYPE:i32

  %t14 = add i32 0, 1
;;VAL:%t14;;TYPE:i32

  %t15 = sub i32 %t13, %t14
;;VAL:%t15;;TYPE:i32
  %t16 = call i32 @factorial(i32 %t15)
;;VAL:%t16;;TYPE:i32

  %t17 = mul i32 %t12, %t16
;;VAL:%t17;;TYPE:i32
  ret i32 %t17
}

; === Função: sub ===
define double @sub(double %a, double %b) {
entry:
  %a.addr = alloca double
;;VAL:%a.addr;;TYPE:double
  store double %a, double* %a.addr
  %b.addr = alloca double
;;VAL:%b.addr;;TYPE:double
  store double %b, double* %b.addr
  %t18 = load double, double* %a.addr
;;VAL:%t18;;TYPE:double

  %t19 = load double, double* %b.addr
;;VAL:%t19;;TYPE:double

  %t20 = fsub double %t18, %t19
;;VAL:%t20;;TYPE:double
  ret double %t20
}

; === Função: hello ===
define i8* @hello() {
entry:
  ret i8* getelementptr inbounds ([12 x i8], [12 x i8]* @.str1, i32 0, i32 0)
}

define i32 @main() {
  ; VariableDeclarationNode
  %nome = alloca i8*
;;VAL:%nome;;TYPE:i8*
  %t21 = call i8* @inputString(i8* null)
;;VAL:%t21 ;;TYPE:i8*
  store i8* %t21, i8** %nome
  ; VariableDeclarationNode
  %nomes = alloca i8*
;;VAL:%nomes;;TYPE:i8*
  %t22 = call i8* @arraylist_create(i64 4)
  %t24 = call %DynValue* @createString(i8* getelementptr ([5 x i8], [5 x i8]* @.str3, i32 0, i32 0))
  call void @setItems(i8* %t22, %DynValue* %t24)
  %t26 = call %DynValue* @createString(i8* getelementptr ([7 x i8], [7 x i8]* @.str4, i32 0, i32 0))
  call void @setItems(i8* %t22, %DynValue* %t26)
  %t27 = call %DynValue* @createInt(i32 14)
  call void @setItems(i8* %t22, %DynValue* %t27)
;;VAL:%t22;;TYPE:i8*
  store i8* %t22, i8** %nomes
  ; ListAddNode
  %t28 = load i8*, i8** %nomes
;;VAL:%t28;;TYPE:i8*
  %t29 = load i8*, i8** %nome
;;VAL:%t29;;TYPE:i8*
  %t30 = call %DynValue* @createString(i8* %t29)
  call void @setItems(i8* %t28, %DynValue* %t30)
;;VAL:%t30;;TYPE:%DynValue*
  ; PrintNode
  %t31 = load i8*, i8** %nome
  call i32 (i8*, ...) @printf(i8* getelementptr ([4 x i8], [4 x i8]* @.strStr, i32 0, i32 0), i8* %t31)
  ; PrintNode
  %t32 = add i32 0, 5
;;VAL:%t32;;TYPE:i32
  %t33 = call i32 @factorial(i32 %t32)
  call i32 (i8*, ...) @printf(i8* getelementptr ([4 x i8], [4 x i8]* @.strInt, i32 0, i32 0), i32 %t33)
  ; PrintNode
  %t34 = call i1 @bool()
  %t35 = zext i1 %t34 to i32
  call i32 (i8*, ...) @printf(i8* getelementptr ([4 x i8], [4 x i8]* @.strInt, i32 0, i32 0), i32 %t35)
  ; PrintNode
  %t36 = add i32 0, 3
;;VAL:%t36;;TYPE:i32
  %t37 = add i32 0, 4
;;VAL:%t37;;TYPE:i32
  %t38 = call i32 @soma(i32 %t36, i32 %t37)
  call i32 (i8*, ...) @printf(i8* getelementptr ([4 x i8], [4 x i8]* @.strInt, i32 0, i32 0), i32 %t38)
  ; PrintNode
  %t39 = fadd double 0.0, 10.3
;;VAL:%t39;;TYPE:double
  %t40 = fadd double 0.0, 2.2
;;VAL:%t40;;TYPE:double
  %t41 = call double @sub(double %t39, double %t40)
  call i32 (i8*, ...) @printf(i8* getelementptr ([4 x i8], [4 x i8]* @.strDouble, i32 0, i32 0), double %t41)
  ; PrintNode
  %t42 = load i8*, i8** %nomes
  call void @printList(i8* %t42)
  ; PrintNode
  %t43 = call i8* @hello()
  call i32 (i8*, ...) @printf(i8* %t43)
  call i32 @getchar()
  ret i32 0
}
