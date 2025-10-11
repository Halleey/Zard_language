    declare i32 @printf(i8*, ...)
    declare i32 @getchar()
    declare void @printString(%String*)
    declare i8* @malloc(i64)
    declare void @setString(%String*, i8*)

    @.strInt = private constant [4 x i8] c"%d\0A\00"
    @.strDouble = private constant [4 x i8] c"%f\0A\00"
    @.strStr = private constant [4 x i8] c"%s\0A\00"
    declare %String* @createString(i8*)
    declare i8* @arraylist_create(i64)
    declare void @clearList(%ArrayList*)
    declare void @freeList(%ArrayList*)

    %String = type { i8*, i64 }
    %ArrayList = type opaque
    declare i32 @inputInt(i8*)
    declare double @inputDouble(i8*)
    declare i1 @inputBool(i8*)
    declare i8* @inputString(i8*)
    declare void @arraylist_add_string(%ArrayList*, i8*)
    declare void @arraylist_addAll_string(%ArrayList*, i8**, i64)
    declare void @arraylist_print_string(%ArrayList*)
    declare void @arraylist_add_String(%ArrayList*, %String*)
    declare void @arraylist_addAll_String(%ArrayList*, %String**, i64)
    declare void @removeItem(%ArrayList*, i64)
    declare i8* @getItem(%ArrayList*, i64)

@.str0 = private constant [7 x i8] c"halley\00"
@.str1 = private constant [1 x i8] c"\00"
@.str2 = private constant [6 x i8] c"misty\00"

define i32 @main() {
  ; VariableDeclarationNode
  %nome = alloca %String*
;;VAL:%nome;;TYPE:%String*
  %t0 = call i8* @malloc(i64 ptrtoint (%String* getelementptr (%String, %String* null, i32 1) to i64))
  %t1 = bitcast i8* %t0 to %String*
  %t2 = bitcast [7 x i8]* @.str0 to i8*
  %t3 = getelementptr inbounds %String, %String* %t1, i32 0, i32 0
  store i8* %t2, i8** %t3
  %t4 = getelementptr inbounds %String, %String* %t1, i32 0, i32 1
  store i64 6, i64* %t4
  store %String* %t1, %String** %nome
  ; VariableDeclarationNode
  %teste = alloca %String*
;;VAL:%teste;;TYPE:%String*
  %t5 = call i8* @malloc(i64 ptrtoint (%String* getelementptr (%String, %String* null, i32 1) to i64))
  %t6 = bitcast i8* %t5 to %String*
  %t7 = getelementptr inbounds %String, %String* %t6, i32 0, i32 0
  store i8* null, i8** %t7
  %t8 = getelementptr inbounds %String, %String* %t6, i32 0, i32 1
  store i64 0, i64* %t8
  store %String* %t6, %String** %teste
  ; AssignmentNode
  %t10 = call i8* @inputString(i8* null)
  %t11 = call %String* @createString(i8* %t10)
;;VAL:%t11;;TYPE:%String
  store %String* %t11, %String** %teste
  ; VariableDeclarationNode
  %nomes = alloca i8*
;;VAL:%nomes;;TYPE:i8*
  %t12 = call i8* @arraylist_create(i64 4)
  %t13 = bitcast i8* %t12 to %ArrayList*
  %t14 = load %String*, %String** %nome
;;VAL:%t14;;TYPE:%String*
  call void @arraylist_add_String(%ArrayList* %t13, %String* %t14)
  %t15 = bitcast [6 x i8]* @.str2 to i8*
;;VAL:%t15;;TYPE:i8*
  %t16 = call %String* @createString(i8* %t15)
  call void @arraylist_add_String(%ArrayList* %t13, %String* %t16)
;;VAL:%t12;;TYPE:i8*
  store i8* %t12, i8** %nomes
  ; ListAddNode
  %t17 = load i8*, i8** %nomes
;;VAL:%t17;;TYPE:i8*
  %t19 = bitcast i8* %t17 to %ArrayList*
  %t18 = load %String*, %String** %teste
;;VAL:%t18;;TYPE:%String*
  call void @arraylist_add_String(%ArrayList* %t19, %String* %t18)
;;VAL:%t19;;TYPE:%ArrayList*
  ; PrintNode
  %t20 = load i8*, i8** %nomes
  %t21 = bitcast i8* %t20 to %ArrayList*
  call void @arraylist_print_string(%ArrayList* %t21)
  ; PrintNode
  %t22 = load i8*, i8** %nomes
  %t23 = bitcast i8* %t22 to %ArrayList*
  %t24 = add i32 0, 2
  %t25 = zext i32 %t24 to i64
  %t26 = call i8* @getItem(%ArrayList* %t23, i64 %t25)
  call i32 (i8*, ...) @printf(i8* getelementptr ([4 x i8], [4 x i8]* @.strStr, i32 0, i32 0), i8* %t26)
  ; ListRemoveNode
  %t27 = load i8*, i8** %nomes
;;VAL:%t27;;TYPE:i8*
  %t28 = add i32 0, 0
;;VAL:%t28;;TYPE:i32
  %t29 = sext i32 %t28 to i64
  %t30 = bitcast i8* %t27 to %ArrayList*
  call void @removeItem(%ArrayList* %t30, i64 %t29)
  ; PrintNode
  %t31 = load i8*, i8** %nomes
  %t32 = bitcast i8* %t31 to %ArrayList*
  call void @arraylist_print_string(%ArrayList* %t32)
  ; === Free das listas alocadas ===
  %t33 = load i8*, i8** %nomes
  %t34 = bitcast i8* %t33 to %ArrayList*
  call void @freeList(%ArrayList* %t34)
  call i32 @getchar()
  ret i32 0
}
