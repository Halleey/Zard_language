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
declare i32 @readInt(i8*)
declare double @readDouble(i8*)
declare i32 @readBool(i8*)
declare i8* @readString(i8*)



; === Runtime de listas ===
%ArrayList = type opaque
%DynValue = type opaque
declare i8* @arraylist_create(i64)
declare void @setItems(i8*, i8*)
declare void @printList(i8*)
declare void @removeItem(%ArrayList*, i64)
declare void @clearList(%ArrayList*)
declare void @freeList(%ArrayList*)
declare i32 @size(%ArrayList*)
declare %DynValue* @getItem(%ArrayList*, i32)
declare void @printDynValue(%DynValue*)


define i32 @main() {
  ; VariableDeclarationNode
  %a = alloca i32
  store i32 4, i32* %a
  ; VariableDeclarationNode
  %b = alloca i32
  store i32 3, i32* %b
  ; VariableDeclarationNode
  %c = alloca i32
  store i32 10, i32* %c
  ; VariableDeclarationNode
  %numeros = alloca i8*
  %t0 = call i8* @arraylist_create(i64 4)
;;VAL:%t0;;TYPE:i8*
  store i8* %t0, i8** %numeros
  ; ListAddNode
  %t1 = load i8*, i8** %numeros
;;VAL:%t1;;TYPE:i8*
  %t2 = load i32, i32* %a
;;VAL:%t2;;TYPE:i32
  %t3 = call i8* @createInt(i32 %t2)
  call void @setItems(i8* %t1, i8* %t3)
;;VAL:%t3;;TYPE:i8*
  ; PrintNode
  %t4 = load i8*, i8** %numeros
  call void @printList(i8* %t4)
  ; PrintNode
  %t5 = load i8*, i8** %numeros
;;VAL:%t5;;TYPE:i8*
  %t6 = bitcast i8* %t5 to %ArrayList*
  %t7 = call i32 @size(%ArrayList* %t6)
  
  call i32 (i8*, ...) @printf(i8* getelementptr ([4 x i8], [4 x i8]* @.strInt, i32 0, i32 0), i32 %t7)
  ; ListRemoveNode
  %t8 = load i8*, i8** %numeros
;;VAL:%t8;;TYPE:i8*
  %t9 = add i32 0, 0
;;VAL:%t9;;TYPE:i32
  %t10 = sext i32 %t9 to i64
  %t11 = bitcast i8* %t8 to %ArrayList*
  call void @removeItem(%ArrayList* %t11, i64 %t10)
  ; PrintNode
  %t12 = load i8*, i8** %numeros
  call void @printList(i8* %t12)
  ; ListAddAllNode
  %t13 = load i8*, i8** %numeros
;;VAL:%t13;;TYPE:i8*
  %t14 = add i32 0, 3
;;VAL:%t14;;TYPE:i32
  %t15 = call i8* @createInt(i32 %t14)
  call void @setItems(i8* %t13, i8* %t15)
;;VAL:%t15;;TYPE:i8*
  %t16 = add i32 0, 4
;;VAL:%t16;;TYPE:i32
  %t17 = call i8* @createInt(i32 %t16)
  call void @setItems(i8* %t13, i8* %t17)
;;VAL:%t17;;TYPE:i8*
  %t18 = add i32 0, 5
;;VAL:%t18;;TYPE:i32
  %t19 = call i8* @createInt(i32 %t18)
  call void @setItems(i8* %t13, i8* %t19)
;;VAL:%t19;;TYPE:i8*
  ; PrintNode
  %t20 = load i8*, i8** %numeros
  call void @printList(i8* %t20)
  call i32 @getchar()
  ret i32 0
}
