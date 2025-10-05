; === Função: factorial ===
define i32 @math_factorial(i32 %n) {
entry:
  %n_addr = alloca i32
  store i32 %n, i32* %n_addr
;;VAL:%n_addr;;TYPE:i32
  %t0 = load i32, i32* %n_addr
;;VAL:%t0;;TYPE:i32

  %t1 = add i32 0, 0
;;VAL:%t1;;TYPE:i32

  %t2 = icmp eq i32 %t0, %t1
;;VAL:%t2;;TYPE:i1

  br i1 %t2, label %then_0, label %endif_0
then_0:
  %t3 = add i32 0, 1
;;VAL:%t3;;TYPE:i32
  ret i32 %t3
  br label %endif_0
endif_0:
  %t4 = load i32, i32* %n_addr
;;VAL:%t4;;TYPE:i32

  %t5 = load i32, i32* %n_addr
;;VAL:%t5;;TYPE:i32

  %t6 = add i32 0, 1
;;VAL:%t6;;TYPE:i32

  %t7 = sub i32 %t5, %t6
;;VAL:%t7;;TYPE:i32
  %t8 = call i32 @math_factorial(i32 %t7)
;;VAL:%t8;;TYPE:i32

  %t9 = mul i32 %t4, %t8
;;VAL:%t9;;TYPE:i32
  ret i32 %t9
}


    declare i32 @printf(i8*, ...)
    declare i32 @getchar()
    declare void @printString(%String*)
    declare i8* @malloc(i64)

    @.strInt = private constant [4 x i8] c"%d\0A\00"
    @.strDouble = private constant [4 x i8] c"%f\0A\00"
    @.strStr = private constant [4 x i8] c"%s\0A\00"

    %String = type { i8*, i64 }
    %ArrayList = type opaque

@.str0 = private constant [12 x i8] c"hello world\00"

; === Função: somar ===
define i32 @somar(i32 %a, i32 %b) {
entry:
  %a_addr = alloca i32
  store i32 %a, i32* %a_addr
;;VAL:%a_addr;;TYPE:i32
  %b_addr = alloca i32
  store i32 %b, i32* %b_addr
;;VAL:%b_addr;;TYPE:i32
  %t10 = load i32, i32* %a_addr
;;VAL:%t10;;TYPE:i32

  %t11 = load i32, i32* %b_addr
;;VAL:%t11;;TYPE:i32

  %t12 = add i32 %t10, %t11
;;VAL:%t12;;TYPE:i32
  ret i32 %t12
}

; === Função: teste ===
define %String* @teste() {
entry:
  %t13 = getelementptr inbounds [12 x i8], [12 x i8]* @.str0, i32 0, i32 0
  %t14 = alloca %String
  %t15 = getelementptr inbounds %String, %String* %t14, i32 0, i32 0
  store i8* %t13, i8** %t15
  %t16 = getelementptr inbounds %String, %String* %t14, i32 0, i32 1
  store i64 11, i64* %t16
  ret %String* %t14
}

define i32 @main() {
  ; PrintNode
  %t17 = add i32 0, 114
;;VAL:%t17;;TYPE:i32
  %t18 = add i32 0, 33
;;VAL:%t18;;TYPE:i32
  %t19 = call i32 @somar(i32 %t17, i32 %t18)
  call i32 (i8*, ...) @printf(i8* getelementptr ([4 x i8], [4 x i8]* @.strInt, i32 0, i32 0), i32 %t19)
  ; PrintNode
  %t20 = call %String* @teste()
  call void @printString(%String* %t20)
  ; === Wait for key press before exiting ===
  call i32 @getchar()
  ret i32 0
}
