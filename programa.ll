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
  %t0 = call i8* @arraylist_create(i64 4)
  %t1 = bitcast i8* %t0 to %ArrayList*
  %t2 = add i32 0, 11
;;VAL:%t2;;TYPE:i32
  call void @arraylist_add_int(%ArrayList* %t1, i32 %t2)
  %t3 = add i32 0, 2
;;VAL:%t3;;TYPE:i32
  call void @arraylist_add_int(%ArrayList* %t1, i32 %t3)
  %t4 = add i32 0, 3
;;VAL:%t4;;TYPE:i32
  call void @arraylist_add_int(%ArrayList* %t1, i32 %t4)
;;VAL:%t0;;TYPE:i8*
  store i8* %t0, i8** %numeros
  ; PrintNode
  %t5 = load i8*, i8** %numeros
  %t6 = bitcast i8* %t5 to %ArrayList*
  call void @arraylist_print_int(%ArrayList* %t6)
  ; === Free das listas alocadas ===
  %t7 = load i8*, i8** %numeros
  %t8 = bitcast i8* %t7 to %ArrayList*
  call void @freeList(%ArrayList* %t8)
  ; === Wait for key press before exiting ===
  call i32 @getchar()
  ret i32 0
}
