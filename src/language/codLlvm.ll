; LLVM IR mínimo do main
        declare i32 @printf(i8*, ...)
        @.strInt = private constant [4 x i8] c"%d\0A\00"
        @.strDouble = private constant [4 x i8] c"%f\0A\00"

        @.str0 = private constant [15 x i8] c"testando aqui\0A\00"

        define i32 @main() {
        ; VariableDeclarationNode
        %a = alloca double
        store double 5.43, double* %a
        ; VariableDeclarationNode
        %b = alloca i32
        store i32 3, i32* %b
        ; VariableDeclarationNode
        %isReal = alloca i1
        store i1 1, i1* %isReal
        ; VariableDeclarationNode
        %isFalse = alloca i1
        store i1 0, i1* %isFalse
        ; PrintNode
        %t3 = load double, double* %a
        call i32 (i8*, ...) @printf(i8* getelementptr ([4 x i8], [4 x i8]* @.strDouble, i32 0, i32 0), double %t3)
        ; PrintNode
        %t4 = load i1, i1* %isReal
        %tBool150919819792400 = zext i1 %t4 to i32
        call i32 (i8*, ...) @printf(i8* getelementptr ([4 x i8], [4 x i8]* @.strInt, i32 0, i32 0), i32 %tBool150919819792400)
        ; PrintNode
        %t5 = load i1, i1* %isFalse
        %tBool150919821809100 = zext i1 %t5 to i32
        call i32 (i8*, ...) @printf(i8* getelementptr ([4 x i8], [4 x i8]* @.strInt, i32 0, i32 0), i32 %tBool150919821809100)
        ; PrintNode
        call i32 (i8*, ...) @printf(i8* getelementptr ([15 x i8], [15 x i8]* @.str0, i32 0, i32 0))
        ret i32 0
        }
