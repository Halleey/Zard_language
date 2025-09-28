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

@.str0 = private constant [36 x i8] c"Erro: factorial de numero negativo!\00"
@.str1 = private constant [12 x i8] c"hello world\00"
@.str2 = private constant [5 x i8] c"zard\00"
@.str3 = private constant [6 x i8] c"angel\00"
@.str4 = private constant [43 x i8] c"Digite um numero para calcular o fatorial:\00"
@.str5 = private constant [1 x i8] c"\00"

; === Função: fac ===
define i32 @fac(i32 %n) {
entry:
  %n.addr = alloca i32
;;VAL:%n.addr;;TYPE:i32
  store i32 %n, i32* %n.addr
  %t0 = load i32, i32* %n.addr
;;VAL:%t0;;TYPE:i32

  %t1 = add i32 0, 0
;;VAL:%t1;;TYPE:i32

  %t2 = icmp slt i32 %t0, %t1
;;VAL:%t2;;TYPE:i1

  br i1 %t2, label %then_0, label %endif_0
then_0:
  call i32 (i8*, ...) @printf(i8* getelementptr ([36 x i8], [36 x i8]* @.str0, i32 0, i32 0))
  %t3 = add i32 0, 0
;;VAL:%t3;;TYPE:i32
  ret i32 %t3
  br label %endif_0
endif_0:
  %t4 = load i32, i32* %n.addr
;;VAL:%t4;;TYPE:i32

  %t5 = add i32 0, 0
;;VAL:%t5;;TYPE:i32

  %t6 = icmp eq i32 %t4, %t5
;;VAL:%t6;;TYPE:i1

  br i1 %t6, label %then_1, label %endif_1
then_1:
  %t7 = add i32 0, 1
;;VAL:%t7;;TYPE:i32
  ret i32 %t7
  br label %endif_1
endif_1:
  %t8 = load i32, i32* %n.addr
;;VAL:%t8;;TYPE:i32

  %t9 = load i32, i32* %n.addr
;;VAL:%t9;;TYPE:i32

  %t10 = add i32 0, 1
;;VAL:%t10;;TYPE:i32

  %t11 = sub i32 %t9, %t10
;;VAL:%t11;;TYPE:i32
  %t12 = call i32 @fac(i32 %t11)
;;VAL:%t12;;TYPE:i32

  %t13 = mul i32 %t8, %t12
;;VAL:%t13;;TYPE:i32
  ret i32 %t13
}

; === Função: hello ===
define i8* @hello() {
entry:
  ret i8* getelementptr inbounds ([12 x i8], [12 x i8]* @.str1, i32 0, i32 0)
}

define i32 @main() {
  ; PrintNode
  %t14 = call i8* @hello()
  call i32 (i8*, ...) @printf(i8* %t14)
  ; VariableDeclarationNode
  %nomes = alloca i8*
;;VAL:%nomes;;TYPE:i8*
  %t15 = call i8* @arraylist_create(i64 4)
  %t17 = call %DynValue* @createString(i8* getelementptr ([5 x i8], [5 x i8]* @.str2, i32 0, i32 0))
  call void @setItems(i8* %t15, %DynValue* %t17)
  %t19 = call %DynValue* @createString(i8* getelementptr ([6 x i8], [6 x i8]* @.str3, i32 0, i32 0))
  call void @setItems(i8* %t15, %DynValue* %t19)
;;VAL:%t15;;TYPE:i8*
  store i8* %t15, i8** %nomes
  ; PrintNode
  %t20 = load i8*, i8** %nomes
  call void @printList(i8* %t20)
  ; VariableDeclarationNode
  %a = alloca i32
;;VAL:%a;;TYPE:i32
  store i32 4, i32* %a
  ; VariableDeclarationNode
  %b = alloca double
;;VAL:%b;;TYPE:double
  store double 3.14, double* %b
  ; VariableDeclarationNode
  %name = alloca i8*
;;VAL:%name;;TYPE:i8*
  store i8* getelementptr ([5 x i8], [5 x i8]* @.str2, i32 0, i32 0), i8** %name
  ; VariableDeclarationNode
  %i = alloca i1
;;VAL:%i;;TYPE:i1
  store i1 1, i1* %i
  ; VariableDeclarationNode
  %f = alloca i1
;;VAL:%f;;TYPE:i1
  store i1 0, i1* %f
  ; PrintNode
  %t21 = load i32, i32* %a
  call i32 (i8*, ...) @printf(i8* getelementptr ([4 x i8], [4 x i8]* @.strInt, i32 0, i32 0), i32 %t21)
  ; PrintNode
  %t22 = load double, double* %b
  call i32 (i8*, ...) @printf(i8* getelementptr ([4 x i8], [4 x i8]* @.strDouble, i32 0, i32 0), double %t22)
  ; PrintNode
  %t23 = load i8*, i8** %name
  call i32 (i8*, ...) @printf(i8* getelementptr ([4 x i8], [4 x i8]* @.strStr, i32 0, i32 0), i8* %t23)
  ; PrintNode
  %t24 = load i1, i1* %i
  %t25 = zext i1 %t24 to i32
  call i32 (i8*, ...) @printf(i8* getelementptr ([4 x i8], [4 x i8]* @.strInt, i32 0, i32 0), i32 %t25)
  ; PrintNode
  %t26 = load i1, i1* %f
  %t27 = zext i1 %t26 to i32
  call i32 (i8*, ...) @printf(i8* getelementptr ([4 x i8], [4 x i8]* @.strInt, i32 0, i32 0), i32 %t27)
  ; ListAddAllNode
  %t28 = load i8*, i8** %nomes
;;VAL:%t28;;TYPE:i8*
  %t29 = alloca %DynValue*, i64 4
  %t30 = load i32, i32* %a
;;VAL:%t30;;TYPE:i32
  %t31 = call %DynValue* @createInt(i32 %t30)
  %t32 = getelementptr inbounds %DynValue*, %DynValue** %t29, i64 0
  store %DynValue* %t31, %DynValue** %t32
  %t33 = load double, double* %b
;;VAL:%t33;;TYPE:double
  %t34 = call %DynValue* @createDouble(double %t33)
  %t35 = getelementptr inbounds %DynValue*, %DynValue** %t29, i64 1
  store %DynValue* %t34, %DynValue** %t35
  %t36 = load i8*, i8** %name
;;VAL:%t36;;TYPE:i8*
  %t37 = call %DynValue* @createString(i8* %t36)
  %t38 = getelementptr inbounds %DynValue*, %DynValue** %t29, i64 2
  store %DynValue* %t37, %DynValue** %t38
  %t39 = load i1, i1* %f
;;VAL:%t39;;TYPE:i1
  %t40 = call %DynValue* @createBool(i1 %t39)
  %t41 = getelementptr inbounds %DynValue*, %DynValue** %t29, i64 3
  store %DynValue* %t40, %DynValue** %t41
  %t42 = bitcast i8* %t28 to %ArrayList*
  call void @addAll(%ArrayList* %t42, %DynValue** %t29, i64 4)
  ; ListRemoveNode
  %t43 = load i8*, i8** %nomes
;;VAL:%t43;;TYPE:i8*
  %t44 = add i32 0, 0
;;VAL:%t44;;TYPE:i32
  %t45 = sext i32 %t44 to i64
  %t46 = bitcast i8* %t43 to %ArrayList*
  call void @removeItem(%ArrayList* %t46, i64 %t45)
  ; PrintNode
  %t47 = load i8*, i8** %nomes
  call void @printList(i8* %t47)
  ; PrintNode
  call i32 (i8*, ...) @printf(i8* getelementptr ([43 x i8], [43 x i8]* @.str4, i32 0, i32 0))
  ; VariableDeclarationNode
  %no = alloca i32
;;VAL:%no;;TYPE:i32
  %t48 = call i32 @inputInt(i8* null)
;;VAL:%t48 ;;TYPE:i32
  store i32 %t48, i32* %no
  ; PrintNode
  %t49 = load i32, i32* %no
;;VAL:%t49;;TYPE:i32
  %t50 = call i32 @fac(i32 %t49)
  call i32 (i8*, ...) @printf(i8* getelementptr ([4 x i8], [4 x i8]* @.strInt, i32 0, i32 0), i32 %t50)
  call i32 @getchar()
  ret i32 0
}
