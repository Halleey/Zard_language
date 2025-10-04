declare i32 @printf(i8*, ...)
declare i32 @getchar()
declare i8* @malloc(i64)
declare i8* @arraylist_create(i64)

declare i32 @inputInt(i8*)
declare double @inputDouble(i8*)
declare i1 @inputBool(i8*)
declare i8* @inputString(i8*)
declare %String* @createString(i8*) 
declare void @arraylist_add_int(%ArrayList*, i32)
declare void @arraylist_add_double(%ArrayList*, double)
declare void @arraylist_add_string(%ArrayList*, i8*)
declare void @arraylist_add_String(%ArrayList*, %String*)
declare void @arraylist_addAll_string(%ArrayList* %list, i8** %strings, i64 %n)
declare void @arraylist_addAll_String(%ArrayList* %list, %String** %strings, i64 %n)
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

@.str0 = private constant [15 x i8] c"digite um nome\00"
@.str1 = private constant [1 x i8] c"\00"
@.str2 = private constant [17 x i8] c"digite uma idade\00"
@.str3 = private constant [26 x i8] c"testando espaco em branco\00"

define i32 @main() {
  ; PrintNode
  %t0 = getelementptr inbounds [15 x i8], [15 x i8]* @.str0, i32 0, i32 0
  call i32 (i8*, ...) @printf(i8* getelementptr ([4 x i8], [4 x i8]* @.strStr, i32 0, i32 0), i8* %t0)
  ; VariableDeclarationNode
  %nome = alloca %String*
;;VAL:%nome;;TYPE:%String*
  %t2 = call i8* @inputString(i8* null)
  %t3 = call %String* @createString(i8* %t2)
;;VAL:%t3;;TYPE:%String
  store %String* %t3, %String** %nome
  ; PrintNode
  %t4 = getelementptr inbounds [17 x i8], [17 x i8]* @.str2, i32 0, i32 0
  call i32 (i8*, ...) @printf(i8* getelementptr ([4 x i8], [4 x i8]* @.strStr, i32 0, i32 0), i8* %t4)
  ; VariableDeclarationNode
  %idade = alloca i32
;;VAL:%idade;;TYPE:i32
  %t5 = call i32 @inputInt(i8* null)
;;VAL:%t5;;TYPE:i32
  store i32 %t5, i32* %idade
  ; PrintNode
  %t6 = load %String*, %String** %nome
  %t7 = getelementptr inbounds %String, %String* %t6, i32 0, i32 0
  %t8 = load i8*, i8** %t7
  call i32 (i8*, ...) @printf(i8* getelementptr ([4 x i8], [4 x i8]* @.strStr, i32 0, i32 0), i8* %t8)
  ; PrintNode
  %t9 = load i32, i32* %idade
  call i32 (i8*, ...) @printf(i8* getelementptr ([4 x i8], [4 x i8]* @.strInt, i32 0, i32 0), i32 %t9)
  ; VariableDeclarationNode
  %nomes = alloca i8*
;;VAL:%nomes;;TYPE:i8*
  %t10 = call i8* @arraylist_create(i64 4)
  %t11 = bitcast i8* %t10 to %ArrayList*
  %t12 = bitcast [26 x i8]* @.str3 to i8*
;;VAL:%t12;;TYPE:i8*
  call void @arraylist_add_string(%ArrayList* %t11, i8* %t12)
  %t13 = load %String*, %String** %nome
;;VAL:%t13;;TYPE:%String*
  %t14 = load %String*, %String** %nome
  call void @arraylist_add_String(%ArrayList* %t11, %String* %t14)
;;VAL:%t10;;TYPE:i8*
  store i8* %t10, i8** %nomes
  ; PrintNode
  %t15 = load i8*, i8** %nomes
  %t16 = bitcast i8* %t15 to %ArrayList*
  call void @arraylist_print_string(%ArrayList* %t16)
  ; === Free das listas alocadas ===
  %t17 = load i8*, i8** %nomes
  %t18 = bitcast i8* %t17 to %ArrayList*
  call void @freeList(%ArrayList* %t18)
  ; === Wait for key press before exiting ===
  call i32 @getchar()
  ret i32 0
}
