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

@.str0 = private constant [6 x i8] c"hello\00"
@.str1 = private constant [52 x i8] c"Aviso: expoente negativo no suportado, retornando 0\00"

; === Função: te ===
define i8* @te() {
entry:
  ret i8* getelementptr inbounds ([6 x i8], [6 x i8]* @.str0, i32 0, i32 0)
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
  %t0 = load i32, i32* %exp.addr
;;VAL:%t0;;TYPE:i32

  %t1 = add i32 0, 0
;;VAL:%t1;;TYPE:i32

  %t2 = icmp slt i32 %t0, %t1
;;VAL:%t2;;TYPE:i1

  br i1 %t2, label %then_0, label %endif_0
then_0:
  call i32 (i8*, ...) @printf(i8* getelementptr ([52 x i8], [52 x i8]* @.str1, i32 0, i32 0))
  %t3 = add i32 0, 0
;;VAL:%t3;;TYPE:i32
  %t4 = sitofp i32 %t3 to double
;;VAL:%t4;;TYPE:double
  ret double %t4
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
  %t5 = load i32, i32* %i
;;VAL:%t5;;TYPE:i32

  %t6 = load i32, i32* %exp.addr
;;VAL:%t6;;TYPE:i32

  %t7 = icmp slt i32 %t5, %t6
;;VAL:%t7;;TYPE:i1
  br i1 %t7, label %while_body_1, label %while_end_2
while_body_1:
  %t8 = load double, double* %result
;;VAL:%t8;;TYPE:double

  %t9 = load double, double* %base.addr
;;VAL:%t9;;TYPE:double

  %t10 = fmul double %t8, %t9
;;VAL:%t10;;TYPE:double
  store double %t10, double* %result
  %t11 = load i32, i32* %i
  %t12 = add i32 %t11, 1
  store i32 %t12, i32* %i
  br label %while_cond_0
while_end_2:
  %t13 = load double, double* %result
;;VAL:%t13;;TYPE:double
  ret double %t13
}

define i32 @main() {
  ; PrintNode
  %t14 = call i8* @te()
  call i32 (i8*, ...) @printf(i8* %t14)
  ; PrintNode
  %t15 = fadd double 0.0, 2.0
;;VAL:%t15;;TYPE:double
  %t16 = add i32 0, 1
;;VAL:%t16;;TYPE:i32
  %t17 = call double @pow(double %t15, i32 %t16)
  call i32 (i8*, ...) @printf(i8* getelementptr ([4 x i8], [4 x i8]* @.strDouble, i32 0, i32 0), double %t17)
  call i32 @getchar()
  ret i32 0
}
