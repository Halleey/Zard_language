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

@.str0 = private constant [7 x i8] c"halley\00"
@.str1 = private constant [6 x i8] c"misty\00"
@.str2 = private constant [4 x i8] c"hal\00"
@.str3 = private constant [23 x i8] c"pos 0 e igual a halley\00"
@.str4 = private constant [14 x i8] c"nao era igual\00"

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
  %outro = alloca %String*
;;VAL:%outro;;TYPE:%String*
  %t5 = call i8* @malloc(i64 ptrtoint (%String* getelementptr (%String, %String* null, i32 1) to i64))
  %t6 = bitcast i8* %t5 to %String*
  %t7 = bitcast [6 x i8]* @.str1 to i8*
  %t8 = getelementptr inbounds %String, %String* %t6, i32 0, i32 0
  store i8* %t7, i8** %t8
  %t9 = getelementptr inbounds %String, %String* %t6, i32 0, i32 1
  store i64 5, i64* %t9
  store %String* %t6, %String** %outro
  ; VariableDeclarationNode
  %nomes = alloca i8*
;;VAL:%nomes;;TYPE:i8*
  %t10 = call i8* @arraylist_create(i64 4)
  %t11 = bitcast i8* %t10 to %ArrayList*
  %t12 = load %String*, %String** %nome
;;VAL:%t12;;TYPE:%String*
  call void @arraylist_add_String(%ArrayList* %t11, %String* %t12)
  %t13 = load %String*, %String** %outro
;;VAL:%t13;;TYPE:%String*
  call void @arraylist_add_String(%ArrayList* %t11, %String* %t13)
;;VAL:%t10;;TYPE:i8*
  store i8* %t10, i8** %nomes
  ; IfNode
  %t14 = load %String*, %String** %nome
;;VAL:%t14;;TYPE:%String*

  %t16 = call %String* @createString(i8* @.str2)
;;VAL:%t15;;TYPE:%String*

  %t18 = call i1 @strcmp_eq(%String* %t14, %String* %t15)
;;VAL:%t18;;TYPE:i1

  br i1 %t18, label %then_0, label %else_0
then_0:
  %t19 = getelementptr inbounds [23 x i8], [23 x i8]* @.str3, i32 0, i32 0
  call i32 (i8*, ...) @printf(i8* getelementptr ([4 x i8], [4 x i8]* @.strStr, i32 0, i32 0), i8* %t19)
  br label %endif_0
else_0:
  %t20 = getelementptr inbounds [14 x i8], [14 x i8]* @.str4, i32 0, i32 0
  call i32 (i8*, ...) @printf(i8* getelementptr ([4 x i8], [4 x i8]* @.strStr, i32 0, i32 0), i8* %t20)
  br label %endif_0
endif_0:
  ; === Free das listas alocadas ===
  %t21 = load i8*, i8** %nomes
  %t22 = bitcast i8* %t21 to %ArrayList*
  call void @freeList(%ArrayList* %t22)
  call i32 @getchar()
  ret i32 0
}
