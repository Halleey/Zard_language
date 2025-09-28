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

; === Função: factorial ===
define i32 @factorial(i32 %n) {
entry:
  %n.addr = alloca i32
;;VAL:%n.addr;;TYPE:i32
  store i32 %n, i32* %n.addr
  %result = alloca i32
;;VAL:%result;;TYPE:i32
  store i32 1, i32* %result
  %i = alloca i32
;;VAL:%i;;TYPE:i32
  store i32 1, i32* %i
  br label %while_cond_0
while_cond_0:
  %t0 = load i32, i32* %i
;;VAL:%t0;;TYPE:i32

  %t1 = load i32, i32* %n.addr
;;VAL:%t1;;TYPE:i32

  %t2 = icmp sle i32 %t0, %t1
;;VAL:%t2;;TYPE:i1
  br i1 %t2, label %while_body_1, label %while_end_2
while_body_1:
  %t3 = load i32, i32* %result
;;VAL:%t3;;TYPE:i32

  %t4 = load i32, i32* %i
;;VAL:%t4;;TYPE:i32

  %t5 = mul i32 %t3, %t4
;;VAL:%t5;;TYPE:i32
  store i32 %t5, i32* %result
  %t6 = load i32, i32* %i
;;VAL:%t6;;TYPE:i32

  %t7 = add i32 0, 1
;;VAL:%t7;;TYPE:i32

  %t8 = add i32 %t6, %t7
;;VAL:%t8;;TYPE:i32
  store i32 %t8, i32* %i
  br label %while_cond_0
while_end_2:
  %t9 = load i32, i32* %result
;;VAL:%t9;;TYPE:i32
  ret i32 %t9
}

; === Função: fac ===
define i32 @fac(i32 %n) {
entry:
  %n.addr = alloca i32
;;VAL:%n.addr;;TYPE:i32
  store i32 %n, i32* %n.addr
  %t10 = load i32, i32* %n.addr
;;VAL:%t10;;TYPE:i32

  %t11 = add i32 0, 0
;;VAL:%t11;;TYPE:i32

  %t12 = icmp slt i32 %t10, %t11
;;VAL:%t12;;TYPE:i1

  br i1 %t12, label %then_0, label %endif_0
then_0:
  call i32 (i8*, ...) @printf(i8* getelementptr ([36 x i8], [36 x i8]* @.str0, i32 0, i32 0))
  %t13 = add i32 0, 0
;;VAL:%t13;;TYPE:i32
  ret i32 %t13
  br label %endif_0
endif_0:
  %t14 = load i32, i32* %n.addr
;;VAL:%t14;;TYPE:i32

  %t15 = add i32 0, 0
;;VAL:%t15;;TYPE:i32

  %t16 = icmp eq i32 %t14, %t15
;;VAL:%t16;;TYPE:i1

  br i1 %t16, label %then_1, label %endif_1
then_1:
  %t17 = add i32 0, 1
;;VAL:%t17;;TYPE:i32
  ret i32 %t17
  br label %endif_1
endif_1:
  %t18 = load i32, i32* %n.addr
;;VAL:%t18;;TYPE:i32

  %t19 = load i32, i32* %n.addr
;;VAL:%t19;;TYPE:i32

  %t20 = add i32 0, 1
;;VAL:%t20;;TYPE:i32

  %t21 = sub i32 %t19, %t20
;;VAL:%t21;;TYPE:i32
  %t22 = call i32 @fac(i32 %t21)
;;VAL:%t22;;TYPE:i32

  %t23 = mul i32 %t18, %t22
;;VAL:%t23;;TYPE:i32
  ret i32 %t23
}

; === Função: hello ===
define i8* @hello() {
entry:
  ret i8* getelementptr inbounds ([12 x i8], [12 x i8]* @.str1, i32 0, i32 0)
}

define i32 @main() {
  ; PrintNode
  %t24 = call i8* @hello()
  call i32 (i8*, ...) @printf(i8* %t24)
  ; VariableDeclarationNode
  %nomes = alloca i8*
;;VAL:%nomes;;TYPE:i8*
  %t25 = call i8* @arraylist_create(i64 4)
  %t27 = call %DynValue* @createString(i8* getelementptr ([5 x i8], [5 x i8]* @.str2, i32 0, i32 0))
  call void @setItems(i8* %t25, %DynValue* %t27)
  %t29 = call %DynValue* @createString(i8* getelementptr ([6 x i8], [6 x i8]* @.str3, i32 0, i32 0))
  call void @setItems(i8* %t25, %DynValue* %t29)
;;VAL:%t25;;TYPE:i8*
  store i8* %t25, i8** %nomes
  ; PrintNode
  %t30 = load i8*, i8** %nomes
  call void @printList(i8* %t30)
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
  %t31 = load i32, i32* %a
  call i32 (i8*, ...) @printf(i8* getelementptr ([4 x i8], [4 x i8]* @.strInt, i32 0, i32 0), i32 %t31)
  ; PrintNode
  %t32 = load double, double* %b
  call i32 (i8*, ...) @printf(i8* getelementptr ([4 x i8], [4 x i8]* @.strDouble, i32 0, i32 0), double %t32)
  ; PrintNode
  %t33 = load i8*, i8** %name
  call i32 (i8*, ...) @printf(i8* getelementptr ([4 x i8], [4 x i8]* @.strStr, i32 0, i32 0), i8* %t33)
  ; PrintNode
  %t34 = load i1, i1* %i
  %t35 = zext i1 %t34 to i32
  call i32 (i8*, ...) @printf(i8* getelementptr ([4 x i8], [4 x i8]* @.strInt, i32 0, i32 0), i32 %t35)
  ; PrintNode
  %t36 = load i1, i1* %f
  %t37 = zext i1 %t36 to i32
  call i32 (i8*, ...) @printf(i8* getelementptr ([4 x i8], [4 x i8]* @.strInt, i32 0, i32 0), i32 %t37)
  ; ListAddAllNode
  %t38 = load i8*, i8** %nomes
;;VAL:%t38;;TYPE:i8*
  %t39 = alloca %DynValue*, i64 4
  %t40 = load i32, i32* %a
;;VAL:%t40;;TYPE:i32
  %t41 = call %DynValue* @createInt(i32 %t40)
  %t42 = getelementptr inbounds %DynValue*, %DynValue** %t39, i64 0
  store %DynValue* %t41, %DynValue** %t42
  %t43 = load double, double* %b
;;VAL:%t43;;TYPE:double
  %t44 = call %DynValue* @createDouble(double %t43)
  %t45 = getelementptr inbounds %DynValue*, %DynValue** %t39, i64 1
  store %DynValue* %t44, %DynValue** %t45
  %t46 = load i8*, i8** %name
;;VAL:%t46;;TYPE:i8*
  %t47 = call %DynValue* @createString(i8* %t46)
  %t48 = getelementptr inbounds %DynValue*, %DynValue** %t39, i64 2
  store %DynValue* %t47, %DynValue** %t48
  %t49 = load i1, i1* %f
;;VAL:%t49;;TYPE:i1
  %t50 = call %DynValue* @createBool(i1 %t49)
  %t51 = getelementptr inbounds %DynValue*, %DynValue** %t39, i64 3
  store %DynValue* %t50, %DynValue** %t51
  %t52 = bitcast i8* %t38 to %ArrayList*
  call void @addAll(%ArrayList* %t52, %DynValue** %t39, i64 4)
  ; ListRemoveNode
  %t53 = load i8*, i8** %nomes
;;VAL:%t53;;TYPE:i8*
  %t54 = add i32 0, 0
;;VAL:%t54;;TYPE:i32
  %t55 = sext i32 %t54 to i64
  %t56 = bitcast i8* %t53 to %ArrayList*
  call void @removeItem(%ArrayList* %t56, i64 %t55)
  ; PrintNode
  %t57 = load i8*, i8** %nomes
  call void @printList(i8* %t57)
  ; PrintNode
  call i32 (i8*, ...) @printf(i8* getelementptr ([43 x i8], [43 x i8]* @.str4, i32 0, i32 0))
  ; VariableDeclarationNode
  %no = alloca i32
;;VAL:%no;;TYPE:i32
  %t58 = call i32 @inputInt(i8* null)
;;VAL:%t58 ;;TYPE:i32
  store i32 %t58, i32* %no
  ; PrintNode
  %t59 = load i32, i32* %no
;;VAL:%t59;;TYPE:i32
  %t60 = call i32 @factorial(i32 %t59)
  call i32 (i8*, ...) @printf(i8* getelementptr ([4 x i8], [4 x i8]* @.strInt, i32 0, i32 0), i32 %t60)
  call i32 @getchar()
  ret i32 0
}
