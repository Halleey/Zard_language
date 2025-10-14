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
    declare i1 @strcmp_eq(%String*, %String*)
    declare i1 @strcmp_neq(%String*, %String*)



    %String = type { i8*, i64 }
    %ArrayList = type opaque
    declare void @arraylist_add_string(%ArrayList*, i8*)
    declare void @arraylist_addAll_string(%ArrayList*, i8**, i64)
    declare void @arraylist_print_string(%ArrayList*)
    declare void @arraylist_add_String(%ArrayList*, %String*)
    declare void @arraylist_addAll_String(%ArrayList*, %String**, i64)
    declare void @removeItem(%ArrayList*, i64)
    declare i8* @getItem(%ArrayList*, i64)

@.str0 = private constant [11 x i8] c"hello guys\00"
@.str1 = private constant [6 x i8] c"teste\00"
@.str2 = private constant [7 x i8] c"halley\00"
@.str3 = private constant [6 x i8] c"misty\00"
@.str4 = private constant [4 x i8] c"hal\00"
@.str5 = private constant [23 x i8] c"pos 0 e igual a halley\00"
@.str6 = private constant [14 x i8] c"nao era igual\00"

; === Função: listas ===
define void @listas(i8* %list) {
entry:
  %list_addr = alloca i8*
  store i8* %list, i8** %list_addr
;;VAL:%list_addr;;TYPE:i8*
  %t0 = load i8*, i8** %list_addr
;;VAL:%t0;;TYPE:i8*
  %t3 = bitcast i8* %t0 to %ArrayList*
  %t2 = call %String* @createString(i8* @.str0)
;;VAL:%t2;;TYPE:%String*
  call void @arraylist_add_String(%ArrayList* %t3, %String* %t2)
;;VAL:%t3;;TYPE:%ArrayList*
  ret void
}

; === Função: hi ===
define %String* @hi() {
entry:
  %t4 = getelementptr inbounds [6 x i8], [6 x i8]* @.str1, i32 0, i32 0
  %t5 = alloca %String
  %t6 = getelementptr inbounds %String, %String* %t5, i32 0, i32 0
  store i8* %t4, i8** %t6
  %t7 = getelementptr inbounds %String, %String* %t5, i32 0, i32 1
  store i64 5, i64* %t7
  ret %String* %t5
}

define i32 @main() {
  ; VariableDeclarationNode
  %name = alloca i8*
;;VAL:%name;;TYPE:i8*
  %t8 = call i8* @arraylist_create(i64 4)
  %t9 = bitcast i8* %t8 to %ArrayList*
;;VAL:%t8;;TYPE:i8*
  store i8* %t8, i8** %name
  ; FunctionCallNode
  %t10 = load i8*, i8** %name
;;VAL:%t10;;TYPE:i8*
  call void @listas(i8* %t10)
;;VAL:void;;TYPE:void
  ; PrintNode
  %t11 = load i8*, i8** %name
  %t12 = bitcast i8* %t11 to %ArrayList*
  call void @arraylist_print_string(%ArrayList* %t12)
  ; PrintNode
  %t13 = call %String* @hi()
  call void @printString(%String* %t13)
  ; VariableDeclarationNode
  %nome = alloca %String*
;;VAL:%nome;;TYPE:%String*
  %t14 = call i8* @malloc(i64 ptrtoint (%String* getelementptr (%String, %String* null, i32 1) to i64))
  %t15 = bitcast i8* %t14 to %String*
  %t16 = bitcast [7 x i8]* @.str2 to i8*
  %t17 = getelementptr inbounds %String, %String* %t15, i32 0, i32 0
  store i8* %t16, i8** %t17
  %t18 = getelementptr inbounds %String, %String* %t15, i32 0, i32 1
  store i64 6, i64* %t18
  store %String* %t15, %String** %nome
  ; VariableDeclarationNode
  %outro = alloca %String*
;;VAL:%outro;;TYPE:%String*
  %t19 = call i8* @malloc(i64 ptrtoint (%String* getelementptr (%String, %String* null, i32 1) to i64))
  %t20 = bitcast i8* %t19 to %String*
  %t21 = bitcast [6 x i8]* @.str3 to i8*
  %t22 = getelementptr inbounds %String, %String* %t20, i32 0, i32 0
  store i8* %t21, i8** %t22
  %t23 = getelementptr inbounds %String, %String* %t20, i32 0, i32 1
  store i64 5, i64* %t23
  store %String* %t20, %String** %outro
  ; VariableDeclarationNode
  %nomes = alloca i8*
;;VAL:%nomes;;TYPE:i8*
  %t24 = call i8* @arraylist_create(i64 4)
  %t25 = bitcast i8* %t24 to %ArrayList*
  %t26 = load %String*, %String** %nome
;;VAL:%t26;;TYPE:%String*
  call void @arraylist_add_String(%ArrayList* %t25, %String* %t26)
  %t27 = load %String*, %String** %outro
;;VAL:%t27;;TYPE:%String*
  call void @arraylist_add_String(%ArrayList* %t25, %String* %t27)
;;VAL:%t24;;TYPE:i8*
  store i8* %t24, i8** %nomes
  ; IfNode
  %t28 = load i8*, i8** %nomes
  %t29 = bitcast i8* %t28 to %ArrayList*
  %t30 = add i32 0, 0
  %t31 = zext i32 %t30 to i64
  %t32 = call i8* @getItem(%ArrayList* %t29, i64 %t31)
;;VAL:%t32
;;TYPE:i8*

  %t34 = call %String* @createString(i8* @.str4)
;;VAL:%t34;;TYPE:%String*

  %t36 = call %String* @createString(i8* %t32)
;;VAL:%t36;;TYPE:%String*
  %t37 = call i1 @strcmp_neq(%String* %t36, %String* %t34)
;;VAL:%t37;;TYPE:i1

  br i1 %t37, label %then_0, label %else_0
then_0:
  %t38 = getelementptr inbounds [23 x i8], [23 x i8]* @.str5, i32 0, i32 0
  call i32 (i8*, ...) @printf(i8* getelementptr ([4 x i8], [4 x i8]* @.strStr, i32 0, i32 0), i8* %t38)
  br label %endif_0
else_0:
  %t39 = getelementptr inbounds [14 x i8], [14 x i8]* @.str6, i32 0, i32 0
  call i32 (i8*, ...) @printf(i8* getelementptr ([4 x i8], [4 x i8]* @.strStr, i32 0, i32 0), i8* %t39)
  br label %endif_0
endif_0:
  ; === Free das listas alocadas ===
  %t40 = load i8*, i8** %name
  %t41 = bitcast i8* %t40 to %ArrayList*
  call void @freeList(%ArrayList* %t41)
  %t42 = load i8*, i8** %nomes
  %t43 = bitcast i8* %t42 to %ArrayList*
  call void @freeList(%ArrayList* %t43)
  call i32 @getchar()
  ret i32 0
}
