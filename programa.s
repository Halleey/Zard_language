	.text
	.file	"programa.ll"
	.globl	print_Pessoa                    # -- Begin function print_Pessoa
	.p2align	4, 0x90
	.type	print_Pessoa,@function
print_Pessoa:                           # @print_Pessoa
	.cfi_startproc
# %bb.0:                                # %entry
	pushq	%rbx
	.cfi_def_cfa_offset 16
	.cfi_offset %rbx, -16
	movq	%rdi, %rbx
	movq	(%rdi), %rdi
	callq	printString@PLT
	movl	8(%rbx), %esi
	movl	$.L.strInt, %edi
	xorl	%eax, %eax
	callq	printf@PLT
	popq	%rbx
	.cfi_def_cfa_offset 8
	retq
.Lfunc_end0:
	.size	print_Pessoa, .Lfunc_end0-print_Pessoa
	.cfi_endproc
                                        # -- End function
	.globl	Pessoa_hello                    # -- Begin function Pessoa_hello
	.p2align	4, 0x90
	.type	Pessoa_hello,@function
Pessoa_hello:                           # @Pessoa_hello
	.cfi_startproc
# %bb.0:                                # %entry
	pushq	%rax
	.cfi_def_cfa_offset 16
	movl	$.L.strStr, %edi
	movl	$.L.str0, %esi
	xorl	%eax, %eax
	callq	printf@PLT
	popq	%rax
	.cfi_def_cfa_offset 8
	retq
.Lfunc_end1:
	.size	Pessoa_hello, .Lfunc_end1-Pessoa_hello
	.cfi_endproc
                                        # -- End function
	.globl	main                            # -- Begin function main
	.p2align	4, 0x90
	.type	main,@function
main:                                   # @main
	.cfi_startproc
# %bb.0:
	subq	$24, %rsp
	.cfi_def_cfa_offset 32
	xorl	%edi, %edi
	callq	createString@PLT
	movq	%rax, 8(%rsp)
	movl	$0, 16(%rsp)
	movl	$.L.str1, %edi
	callq	createString@PLT
	movq	%rax, 8(%rsp)
	movl	$22, 16(%rsp)
	leaq	8(%rsp), %rdi
	callq	Pessoa_hello@PLT
	callq	getchar@PLT
	xorl	%eax, %eax
	addq	$24, %rsp
	.cfi_def_cfa_offset 8
	retq
.Lfunc_end2:
	.size	main, .Lfunc_end2-main
	.cfi_endproc
                                        # -- End function
	.type	.L.strChar,@object              # @.strChar
	.section	.rodata,"a",@progbits
.L.strChar:
	.asciz	"%c"
	.size	.L.strChar, 3

	.type	.L.strTrue,@object              # @.strTrue
.L.strTrue:
	.asciz	"true\n"
	.size	.L.strTrue, 6

	.type	.L.strFalse,@object             # @.strFalse
.L.strFalse:
	.asciz	"false\n"
	.size	.L.strFalse, 7

	.type	.L.strInt,@object               # @.strInt
.L.strInt:
	.asciz	"%d\n"
	.size	.L.strInt, 4

	.type	.L.strDouble,@object            # @.strDouble
.L.strDouble:
	.asciz	"%f\n"
	.size	.L.strDouble, 4

	.type	.L.strFloat,@object             # @.strFloat
.L.strFloat:
	.asciz	"%f\n"
	.size	.L.strFloat, 4

	.type	.L.strStr,@object               # @.strStr
.L.strStr:
	.asciz	"%s\n"
	.size	.L.strStr, 4

	.type	.L.strEmpty,@object             # @.strEmpty
.L.strEmpty:
	.zero	1
	.size	.L.strEmpty, 1

	.type	.L.str0,@object                 # @.str0
	.p2align	4, 0x0
.L.str0:
	.asciz	"Ol\303\241 do m\303\251todo hello()"
	.size	.L.str0, 24

	.type	.L.str1,@object                 # @.str1
.L.str1:
	.asciz	"Zard"
	.size	.L.str1, 5

	.section	".note.GNU-stack","",@progbits
