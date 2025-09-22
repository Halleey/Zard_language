declare i32 @printf(i8*, ...)
declare i32 @getchar()
@.strInt = private constant [4 x i8] c"%d\0A\00"
@.strDouble = private constant [4 x i8] c"%f\0A\00"
@.strStr = private constant [4 x i8] c"%s\0A\00"

; === Funções do runtime DynValue ===
declare i8* @createInt(i32)
declare i8* @createDouble(double)
declare i8* @createBool(i1)
declare i8* @createString(i8*)

; === Runtime de listas ===
%ArrayList = type opaque
declare i8* @arraylist_create(i64)
declare void @setItems(i8*, i8*)
declare void @printList(i8*)
declare void @removeItem(%ArrayList*, i64)
declare void @clearList(%ArrayList*)
declare void @freeList(%ArrayList*)
declare i32 @size(%ArrayList*)

@.str0 = private constant [10 x i8] c"zardelas\0A\00"
@.str1 = private constant [10 x i8] c"hallyson\0A\00"

define i32 @main() {
  ; VariableDeclarationNode
  %a = alloca i32
  ; VariableDeclarationNode
  %b = alloca double
  ; VariableDeclarationNode
  %nome = alloca i8*
  ; VariableDeclarationNode
  %isReal = alloca i1
  store i1 1, i1* %isReal
  ; VariableDeclarationNode
  %isFake = alloca i1
  store i1 0, i1* %isFake
  ; VariableDeclarationNode
  %nomes = alloca i8*
  %t0 = call i8* @arraylist_create(i64 4)
  %t1 = call i8* @createInt(i32 1)
  call void @setItems(i8* %t0, i8* %t1)
  %t2 = call i8* @createBool(i1 1)
  call void @setItems(i8* %t0, i8* %t2)
;;VAL:%t0;;TYPE:i8*
  store i8* %t0, i8** %nomes
  ; ListAddNode
  %t3 = load i8*, i8** %nomes
;;VAL:%t3;;TYPE:i8*
  %t4 = bitcast [10 x i8]* @.str0 to i8*
;;VAL:%t4;;TYPE:i8*
  %t6 = call i8* @createString(i8* getelementptr ([10 x i8], [10 x i8]* @.str0, i32 0, i32 0))
  call void @setItems(i8* %t3, i8* %t6)
;;VAL:%t6;;TYPE:i8*
  ; AssignmentNode
  store i8* getelementptr ([10 x i8], [10 x i8]* @.str1, i32 0, i32 0), i8** %nome
  ; ListAddNode
  %t7 = load i8*, i8** %nomes
;;VAL:%t7;;TYPE:i8*
  %t8 = load i8*, i8** %nome
;;VAL:%t8;;TYPE:i8*
  %t9 = call i8* @createString(i8* %t8)
  call void @setItems(i8* %t7, i8* %t9)
;;VAL:%t9;;TYPE:i8*
  ; PrintNode
  %t10 = load i8*, i8** %nomes
  call void @printList(i8* %t10)
  ; AssignmentNode
  store i32 4, i32* %a
  ; AssignmentNode
  store double 3.14, double* %b
  ; PrintNode
  %t11 = load i32, i32* %a
  call i32 (i8*, ...) @printf(i8* getelementptr ([4 x i8], [4 x i8]* @.strInt, i32 0, i32 0), i32 %t11)
  ; PrintNode
  %t12 = load double, double* %b
  call i32 (i8*, ...) @printf(i8* getelementptr ([4 x i8], [4 x i8]* @.strDouble, i32 0, i32 0), double %t12)
  ; PrintNode
  %t13 = load i8*, i8** %nomes
  call void @printList(i8* %t13)
  ; PrintNode
  %t14 = load i1, i1* %isReal
  %t15 = zext i1 %t14 to i32
  call i32 (i8*, ...) @printf(i8* getelementptr ([4 x i8], [4 x i8]* @.strInt, i32 0, i32 0), i32 %t15)
  ; PrintNode
  %t16 = load i1, i1* %isFake
  %t17 = zext i1 %t16 to i32
  call i32 (i8*, ...) @printf(i8* getelementptr ([4 x i8], [4 x i8]* @.strInt, i32 0, i32 0), i32 %t17)
  ; ListClearNode
  %t18 = load i8*, i8** %nomes
;;VAL:%t18;;TYPE:i8*
  %t19 = bitcast i8* %t18 to %ArrayList*
  call void @clearList(%ArrayList* %t19)
  ; PrintNode
  %t20 = load i8*, i8** %nomes
  call void @printList(i8* %t20)
  ; ListAddNode
  %t21 = load i8*, i8** %nomes
;;VAL:%t21;;TYPE:i8*
  %t22 = bitcast [10 x i8]* @.str0 to i8*
;;VAL:%t22;;TYPE:i8*
  %t24 = call i8* @createString(i8* getelementptr ([10 x i8], [10 x i8]* @.str0, i32 0, i32 0))
  call void @setItems(i8* %t21, i8* %t24)
;;VAL:%t24;;TYPE:i8*
  ; ListAddNode
  %t25 = load i8*, i8** %nomes
;;VAL:%t25;;TYPE:i8*
  %t26 = add i32 0, 3
;;VAL:%t26;;TYPE:i32
  %t27 = call i8* @createInt(i32 %t26)
  call void @setItems(i8* %t25, i8* %t27)
;;VAL:%t27;;TYPE:i8*
  ; PrintNode
  %t28 = load i8*, i8** %nomes
;;VAL:%t28;;TYPE:i8*
  %t29 = bitcast i8* %t28 to %ArrayList*
  %t30 = call i32 @size(%ArrayList* %t29)
  
  call i32 (i8*, ...) @printf(i8* getelementptr ([4 x i8], [4 x i8]* @.strInt, i32 0, i32 0), i32 %t30)
  ; PrintNode
  %t31 = load i8*, i8** %nomes
  call void @printList(i8* %t31)
  call i32 @getchar()
  ret i32 0
}
