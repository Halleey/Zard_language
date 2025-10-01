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

@.str0 = private constant [6 x i8] c"hello\00"
@.str1 = private constant [52 x i8] c"Aviso: expoente negativo no suportado, retornando 0\00"

; === Função: te ===
define i8* @te() {
entry:
  ret i8* getelementptr inbounds ([6 x i8], [6 x i8]* @.str0, i32 0, i32 0)
}

; === Função: adde ===
define void @adde(i8* %list) {
entry:
  %list.addr = alloca i8*
;;VAL:%list.addr;;TYPE:i8*
  store i8* %list, i8** %list.addr
  %t0 = load i8*, i8** %list.addr
;;VAL:%t0;;TYPE:i8*
  %t1 = add i32 0, 3
;;VAL:%t1;;TYPE:i32
  %t2 = call %DynValue* @createInt(i32 %t1)
  call void @setItems(i8* %t0, %DynValue* %t2)
;;VAL:%t2;;TYPE:%DynValue*
  ret void
}

; === Função: pow ===
define double @pow(double %base, i32 %exp) {
entry:
  %base.addr = alloca double
;;VAL:%base.addr;;TYPE:double
  store double %base, double* %base.addr
  %exp.addr = alloca i32
;;VAL:%exp.addr;;TYPE:i32
  store i32 %exp, i32* %exp.addr
  %t3 = load i32, i32* %exp.addr
;;VAL:%t3;;TYPE:i32

  %t4 = add i32 0, 0
;;VAL:%t4;;TYPE:i32

  %t5 = icmp slt i32 %t3, %t4
;;VAL:%t5;;TYPE:i1

  br i1 %t5, label %then_0, label %endif_0
then_0:
  call i32 (i8*, ...) @printf(i8* getelementptr ([52 x i8], [52 x i8]* @.str1, i32 0, i32 0))
  %t6 = add i32 0, 0
;;VAL:%t6;;TYPE:i32
  %t7 = sitofp i32 %t6 to double
;;VAL:%t7;;TYPE:double
  ret double %t7
  br label %endif_0
endif_0:
  %result = alloca double
;;VAL:%result;;TYPE:double
  store double 1.0, double* %result
  %i = alloca i32
;;VAL:%i;;TYPE:i32
  store i32 0, i32* %i
  br label %while_cond_0
while_cond_0:
  %t8 = load i32, i32* %i
;;VAL:%t8;;TYPE:i32

  %t9 = load i32, i32* %exp.addr
;;VAL:%t9;;TYPE:i32

  %t10 = icmp slt i32 %t8, %t9
;;VAL:%t10;;TYPE:i1
  br i1 %t10, label %while_body_1, label %while_end_2
while_body_1:
  %t11 = load double, double* %result
;;VAL:%t11;;TYPE:double

  %t12 = load double, double* %base.addr
;;VAL:%t12;;TYPE:double

  %t13 = fmul double %t11, %t12
;;VAL:%t13;;TYPE:double
  store double %t13, double* %result
  %t14 = load i32, i32* %i
  %t15 = add i32 %t14, 1
  store i32 %t15, i32* %i
  br label %while_cond_0
while_end_2:
  %t16 = load double, double* %result
;;VAL:%t16;;TYPE:double
  ret double %t16
}

define i32 @main() {
  ; VariableDeclarationNode
  %teste = alloca i8*
;;VAL:%teste;;TYPE:i8*
  %t17 = call i8* @arraylist_create(i64 4)
  %t18 = alloca [3 x %DynValue*]
  %t19 = call %DynValue* @createString(i8* getelementptr ([6 x i8], [6 x i8]* @.str0, i32 0, i32 0))
  %t20 = getelementptr inbounds [3 x %DynValue*], [3 x %DynValue*]* %t18, i32 0, i32 0
  store %DynValue* %t19, %DynValue** %t20
  %t21 = call %DynValue* @createInt(i32 13)
  %t22 = getelementptr inbounds [3 x %DynValue*], [3 x %DynValue*]* %t18, i32 0, i32 1
  store %DynValue* %t21, %DynValue** %t22
  %t23 = call %DynValue* @createBool(i1 1)
  %t24 = getelementptr inbounds [3 x %DynValue*], [3 x %DynValue*]* %t18, i32 0, i32 2
  store %DynValue* %t23, %DynValue** %t24
  %t25 = getelementptr inbounds [3 x %DynValue*], [3 x %DynValue*]* %t18, i32 0, i32 0
  %t26 = bitcast i8* %t17 to %ArrayList*
  call void @addAll(%ArrayList* %t26, %DynValue** %t25, i64 3)
;;VAL:%t17;;TYPE:i8*
  store i8* %t17, i8** %teste
  ; PrintNode
  %t27 = load i8*, i8** %teste
  call void @printList(i8* %t27)
  ; PrintNode
  %t28 = call i8* @te()
  call i32 (i8*, ...) @printf(i8* %t28)
  ; FunctionCallNode
  %t29 = load i8*, i8** %teste
;;VAL:%t29;;TYPE:i8*
  call void @adde(i8* %t29)
;;VAL:void;;TYPE:void
  ; PrintNode
  %t30 = load i8*, i8** %teste
  call void @printList(i8* %t30)
  ; PrintNode
  %t31 = fadd double 0.0, 2.0
;;VAL:%t31;;TYPE:double
  %t32 = sub i32 0, 1
;;VAL:%t32;;TYPE:i32
  %t33 = call double @pow(double %t31, i32 %t32)
  call i32 (i8*, ...) @printf(i8* getelementptr ([4 x i8], [4 x i8]* @.strDouble, i32 0, i32 0), double %t33)
  call i32 @getchar()
  ret i32 0
}
