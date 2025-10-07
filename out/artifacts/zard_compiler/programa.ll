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

@.str0 = private constant [39 x i8] c"Error: factorial of a negative number!\00"
@.str1 = private constant [12 x i8] c"hello world\00"
@.str2 = private constant [52 x i8] c"=== Welcome to the interactive AST_zard program ===\00"
@.str3 = private constant [17 x i8] c"Enter your name:\00"
@.str4 = private constant [1 x i8] c"\00"
@.str5 = private constant [41 x i8] c"Enter an integer to calculate factorial:\00"
@.str6 = private constant [5 x i8] c"name\00"
@.str7 = private constant [7 x i8] c"Angela\00"
@.str8 = private constant [26 x i8] c"\n=== Program Outputs ===\00"
@.str9 = private constant [14 x i8] c"Your name is:\00"
@.str10 = private constant [14 x i8] c"Factorial of:\00"
@.str11 = private constant [4 x i8] c"is:\00"
@.str12 = private constant [38 x i8] c"Boolean value returned by boolFunc():\00"
@.str13 = private constant [25 x i8] c"Result of subtract(3,4):\00"
@.str14 = private constant [25 x i8] c"Result of sub(10.3,2.2):\00"
@.str15 = private constant [15 x i8] c"List of names:\00"
@.str16 = private constant [22 x i8] c"Message from hello():\00"
@.str17 = private constant [25 x i8] c"\n=== End of Program ===\00"

; === Função: subtract ===
define i32 @subtract(i32 %a, i32 %b) {
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

; === Função: boolFunc ===
define i1 @boolFunc() {
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
  call i32 (i8*, ...) @printf(i8* getelementptr ([39 x i8], [39 x i8]* @.str0, i32 0, i32 0))
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
  ; PrintNode
  call i32 (i8*, ...) @printf(i8* getelementptr ([52 x i8], [52 x i8]* @.str2, i32 0, i32 0))
  ; PrintNode
  call i32 (i8*, ...) @printf(i8* getelementptr ([17 x i8], [17 x i8]* @.str3, i32 0, i32 0))
  ; VariableDeclarationNode
  %name = alloca i8*
;;VAL:%name;;TYPE:i8*
  %t21 = call i8* @inputString(i8* null)
;;VAL:%t21 ;;TYPE:i8*
  store i8* %t21, i8** %name
  ; PrintNode
  call i32 (i8*, ...) @printf(i8* getelementptr ([41 x i8], [41 x i8]* @.str5, i32 0, i32 0))
  ; VariableDeclarationNode
  %n = alloca i32
;;VAL:%n;;TYPE:i32
  %t22 = call i32 @inputInt(i8* null)
;;VAL:%t22 ;;TYPE:i32
  store i32 %t22, i32* %n
  ; VariableDeclarationNode
  %names = alloca i8*
;;VAL:%names;;TYPE:i8*
  %t23 = call i8* @arraylist_create(i64 4)
  %t25 = call %DynValue* @createString(i8* getelementptr ([5 x i8], [5 x i8]* @.str6, i32 0, i32 0))
  call void @setItems(i8* %t23, %DynValue* %t25)
  %t27 = call %DynValue* @createString(i8* getelementptr ([7 x i8], [7 x i8]* @.str7, i32 0, i32 0))
  call void @setItems(i8* %t23, %DynValue* %t27)
  %t28 = call %DynValue* @createInt(i32 14)
  call void @setItems(i8* %t23, %DynValue* %t28)
;;VAL:%t23;;TYPE:i8*
  store i8* %t23, i8** %names
  ; ListAddNode
  %t29 = load i8*, i8** %names
;;VAL:%t29;;TYPE:i8*
  %t30 = load i8*, i8** %name
;;VAL:%t30;;TYPE:i8*
  %t31 = call %DynValue* @createString(i8* %t30)
  call void @setItems(i8* %t29, %DynValue* %t31)
;;VAL:%t31;;TYPE:%DynValue*
  ; PrintNode
  call i32 (i8*, ...) @printf(i8* getelementptr ([26 x i8], [26 x i8]* @.str8, i32 0, i32 0))
  ; PrintNode
  call i32 (i8*, ...) @printf(i8* getelementptr ([14 x i8], [14 x i8]* @.str9, i32 0, i32 0))
  ; PrintNode
  %t32 = load i8*, i8** %name
  call i32 (i8*, ...) @printf(i8* getelementptr ([4 x i8], [4 x i8]* @.strStr, i32 0, i32 0), i8* %t32)
  ; PrintNode
  call i32 (i8*, ...) @printf(i8* getelementptr ([14 x i8], [14 x i8]* @.str10, i32 0, i32 0))
  ; PrintNode
  %t33 = load i32, i32* %n
  call i32 (i8*, ...) @printf(i8* getelementptr ([4 x i8], [4 x i8]* @.strInt, i32 0, i32 0), i32 %t33)
  ; PrintNode
  call i32 (i8*, ...) @printf(i8* getelementptr ([4 x i8], [4 x i8]* @.str11, i32 0, i32 0))
  ; PrintNode
  %t34 = load i32, i32* %n
;;VAL:%t34;;TYPE:i32
  %t35 = call i32 @factorial(i32 %t34)
  call i32 (i8*, ...) @printf(i8* getelementptr ([4 x i8], [4 x i8]* @.strInt, i32 0, i32 0), i32 %t35)
  ; PrintNode
  call i32 (i8*, ...) @printf(i8* getelementptr ([38 x i8], [38 x i8]* @.str12, i32 0, i32 0))
  ; PrintNode
  %t36 = call i1 @boolFunc()
  %t37 = zext i1 %t36 to i32
  call i32 (i8*, ...) @printf(i8* getelementptr ([4 x i8], [4 x i8]* @.strInt, i32 0, i32 0), i32 %t37)
  ; PrintNode
  call i32 (i8*, ...) @printf(i8* getelementptr ([25 x i8], [25 x i8]* @.str13, i32 0, i32 0))
  ; PrintNode
  %t38 = add i32 0, 3
;;VAL:%t38;;TYPE:i32
  %t39 = add i32 0, 4
;;VAL:%t39;;TYPE:i32
  %t40 = call i32 @subtract(i32 %t38, i32 %t39)
  call i32 (i8*, ...) @printf(i8* getelementptr ([4 x i8], [4 x i8]* @.strInt, i32 0, i32 0), i32 %t40)
  ; PrintNode
  call i32 (i8*, ...) @printf(i8* getelementptr ([25 x i8], [25 x i8]* @.str14, i32 0, i32 0))
  ; PrintNode
  %t41 = fadd double 0.0, 10.3
;;VAL:%t41;;TYPE:double
  %t42 = fadd double 0.0, 2.2
;;VAL:%t42;;TYPE:double
  %t43 = call double @sub(double %t41, double %t42)
  call i32 (i8*, ...) @printf(i8* getelementptr ([4 x i8], [4 x i8]* @.strDouble, i32 0, i32 0), double %t43)
  ; PrintNode
  call i32 (i8*, ...) @printf(i8* getelementptr ([15 x i8], [15 x i8]* @.str15, i32 0, i32 0))
  ; PrintNode
  %t44 = load i8*, i8** %names
  call void @printList(i8* %t44)
  ; PrintNode
  call i32 (i8*, ...) @printf(i8* getelementptr ([22 x i8], [22 x i8]* @.str16, i32 0, i32 0))
  ; PrintNode
  %t45 = call i8* @hello()
  call i32 (i8*, ...) @printf(i8* %t45)
  ; PrintNode
  call i32 (i8*, ...) @printf(i8* getelementptr ([25 x i8], [25 x i8]* @.str17, i32 0, i32 0))
  call i32 @getchar()
  ret i32 0
}
