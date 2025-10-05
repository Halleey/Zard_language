; imported module src/language/stdlib/t.zd as math
declare i32 @math_factorial(i32)
    declare i32 @printf(i8*, ...)
    declare i32 @getchar()
    declare i8* @malloc(i64)
    declare i8* @arraylist_create(i64)
    declare void @clearList(%ArrayList*)
    declare void @freeList(%ArrayList*)

    @.strInt = private constant [4 x i8] c"%d\0A\00"
    @.strDouble = private constant [4 x i8] c"%f\0A\00"
    @.strStr = private constant [4 x i8] c"%s\0A\00"

    %String = type { i8*, i64 }
    %ArrayList = type opaque
    declare void @arraylist_add_int(%ArrayList*, i32)
    declare void @arraylist_print_int(%ArrayList*)


define i32 @main() {
  ; VariableDeclarationNode
  %numeros = alloca i8*
;;VAL:%numeros;;TYPE:i8*
  %t10 = call i8* @arraylist_create(i64 4)
  %t11 = bitcast i8* %t10 to %ArrayList*
  %t12 = add i32 0, 11
;;VAL:%t12;;TYPE:i32
  call void @arraylist_add_int(%ArrayList* %t11, i32 %t12)
  %t13 = add i32 0, 2
;;VAL:%t13;;TYPE:i32
  call void @arraylist_add_int(%ArrayList* %t11, i32 %t13)
  %t14 = add i32 0, 3
;;VAL:%t14;;TYPE:i32
  call void @arraylist_add_int(%ArrayList* %t11, i32 %t14)
;;VAL:%t10;;TYPE:i8*
  store i8* %t10, i8** %numeros
  ; PrintNode
  %t15 = add i32 0, 5
;;VAL:%t15;;TYPE:i32
  %t16 = call i32 @math_factorial(i32 %t15)
  call i32 (i8*, ...) @printf(i8* getelementptr ([4 x i8], [4 x i8]* @.strInt, i32 0, i32 0), i32 %t16)
  ; PrintNode
  %t17 = load i8*, i8** %numeros
  %t18 = bitcast i8* %t17 to %ArrayList*
  call void @arraylist_print_int(%ArrayList* %t18)
  ; === Free das listas alocadas ===
  %t19 = load i8*, i8** %numeros
  %t20 = bitcast i8* %t19 to %ArrayList*
  call void @freeList(%ArrayList* %t20)
  ; === Wait for key press before exiting ===
  call i32 @getchar()
  ret i32 0
}
