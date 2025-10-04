declare i32 @printf(i8*, ...)
declare i32 @getchar()
declare i8* @malloc(i64)
declare i8* @arraylist_create(i64)

declare i32 @inputInt(i8*)
declare double @inputDouble(i8*)
declare i1 @inputBool(i8*)
declare i8* @inputString(i8*)

declare void @arraylist_add_int(%ArrayList*, i32)
declare void @arraylist_add_double(%ArrayList*, double)
declare void @arraylist_add_string(%ArrayList*, i8*)
declare void @arraylist_add_String(%ArrayList*, %String*)
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

@.str0 = private constant [1 x i8] c"\00"

define i32 @main() {
  ; VariableDeclarationNode
  %teste = alloca i1
;;VAL:%teste;;TYPE:i1
  %t0 = call i1 @inputBool(i8* null)
;;VAL:%t0;;TYPE:i1
  store i1 %t0, i1* %teste
  ; VariableDeclarationNode
  %teste2 = alloca i1
;;VAL:%teste2;;TYPE:i1
  %t1 = call i1 @inputBool(i8* null)
;;VAL:%t1;;TYPE:i1
  store i1 %t1, i1* %teste2
  ; PrintNode
  %t2 = load i1, i1* %teste
  %t3 = zext i1 %t2 to i32
  call i32 (i8*, ...) @printf(i8* getelementptr ([4 x i8], [4 x i8]* @.strInt, i32 0, i32 0), i32 %t3)
  ; PrintNode
  %t4 = load i1, i1* %teste2
  %t5 = zext i1 %t4 to i32
  call i32 (i8*, ...) @printf(i8* getelementptr ([4 x i8], [4 x i8]* @.strInt, i32 0, i32 0), i32 %t5)
  ; VariableDeclarationNode
  %teste3 = alloca double
;;VAL:%teste3;;TYPE:double
  %t6 = call double @inputDouble(i8* null)
;;VAL:%t6;;TYPE:double
  store double %t6, double* %teste3
  ; PrintNode
  %t7 = load double, double* %teste3
  call i32 (i8*, ...) @printf(i8* getelementptr ([4 x i8], [4 x i8]* @.strDouble, i32 0, i32 0), double %t7)
  ; VariableDeclarationNode
  %a = alloca i32
;;VAL:%a;;TYPE:i32
  %t8 = call i32 @inputInt(i8* null)
;;VAL:%t8;;TYPE:i32
  store i32 %t8, i32* %a
  ; VariableDeclarationNode
  %nomes = alloca i8*
;;VAL:%nomes;;TYPE:i8*
  %t9 = call i8* @arraylist_create(i64 5)
  %t10 = bitcast i8* %t9 to %ArrayList*
  %t11 = add i32 0, 1
;;VAL:%t11;;TYPE:i32
  call void @arraylist_add_int(%ArrayList* %t10, i32 %t11)
  %t12 = add i32 0, 2
;;VAL:%t12;;TYPE:i32
  call void @arraylist_add_int(%ArrayList* %t10, i32 %t12)
  %t13 = add i32 0, 3
;;VAL:%t13;;TYPE:i32
  call void @arraylist_add_int(%ArrayList* %t10, i32 %t13)
  %t14 = add i32 0, 4
;;VAL:%t14;;TYPE:i32
  call void @arraylist_add_int(%ArrayList* %t10, i32 %t14)
  %t15 = load i32, i32* %a
;;VAL:%t15;;TYPE:i32
  call void @arraylist_add_int(%ArrayList* %t10, i32 %t15)
;;VAL:%t9;;TYPE:i8*
  store i8* %t9, i8** %nomes
  ; PrintNode
  %t16 = load i8*, i8** %nomes
  %t17 = bitcast i8* %t16 to %ArrayList*
  call void @arraylist_print_int(%ArrayList* %t17)
  ; === Free das listas alocadas ===
  %t18 = load i8*, i8** %nomes
  %t19 = bitcast i8* %t18 to %ArrayList*
  call void @freeList(%ArrayList* %t19)
  ; === Wait for key press before exiting ===
  call i32 @getchar()
  ret i32 0
}
