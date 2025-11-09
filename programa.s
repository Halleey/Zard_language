	.text
	.file	"programa.ll"
	.globl	print_Endereco                  # -- Begin function print_Endereco
	.p2align	4, 0x90
	.type	print_Endereco,@function
print_Endereco:                         # @print_Endereco
	.cfi_startproc
# %bb.0:                                # %entry
	pushq	%rbx
	.cfi_def_cfa_offset 16
	.cfi_offset %rbx, -16
	movq	%rdi, %rbx
	movq	(%rdi), %rdi
	callq	printString@PLT
	movq	8(%rbx), %rdi
	callq	printString@PLT
	popq	%rbx
	.cfi_def_cfa_offset 8
	retq
.Lfunc_end0:
	.size	print_Endereco, .Lfunc_end0-print_Endereco
	.cfi_endproc
                                        # -- End function
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
	movq	16(%rbx), %rdi
	callq	print_Endereco@PLT
	popq	%rbx
	.cfi_def_cfa_offset 8
	retq
.Lfunc_end1:
	.size	print_Pessoa, .Lfunc_end1-print_Pessoa
	.cfi_endproc
                                        # -- End function
	.globl	main                            # -- Begin function main
	.p2align	4, 0x90
	.type	main,@function
main:                                   # @main
	.cfi_startproc
# %bb.0:
	pushq	%rbp
	.cfi_def_cfa_offset 16
	.cfi_offset %rbp, -16
	movq	%rsp, %rbp
	.cfi_def_cfa_register %rbp
	pushq	%r15
	pushq	%r14
	pushq	%rbx
	subq	$88, %rsp
	.cfi_offset %rbx, -40
	.cfi_offset %r14, -32
	.cfi_offset %r15, -24
	movl	$4, %edi
	callq	arraylist_create@PLT
	movq	%rax, %rbx
	xorl	%edi, %edi
	callq	createString@PLT
	movq	%rax, -104(%rbp)
	movl	$0, -96(%rbp)
	movq	$0, -88(%rbp)
	movl	$.L.str0, %edi
	callq	createString@PLT
	movq	%rax, -104(%rbp)
	movl	$25, -96(%rbp)
	xorl	%edi, %edi
	callq	createString@PLT
	movq	%rax, -56(%rbp)
	xorl	%edi, %edi
	callq	createString@PLT
	movq	%rax, -48(%rbp)
	movl	$.L.str1, %edi
	callq	createString@PLT
	movq	%rax, -56(%rbp)
	movl	$.L.str2, %edi
	callq	createString@PLT
	movq	%rax, -48(%rbp)
	leaq	-56(%rbp), %rax
	movq	%rax, -88(%rbp)
	xorl	%edi, %edi
	callq	createString@PLT
	movq	%rax, -80(%rbp)
	movl	$0, -72(%rbp)
	movq	$0, -64(%rbp)
	movl	$.L.str3, %edi
	callq	createString@PLT
	movq	%rax, -80(%rbp)
	movl	$17, -72(%rbp)
	xorl	%edi, %edi
	callq	createString@PLT
	movq	%rax, -40(%rbp)
	xorl	%edi, %edi
	callq	createString@PLT
	movq	%rax, -32(%rbp)
	movl	$.L.str4, %edi
	callq	createString@PLT
	movq	%rax, -40(%rbp)
	movl	$.L.str5, %edi
	callq	createString@PLT
	movq	%rax, -32(%rbp)
	leaq	-40(%rbp), %rax
	movq	%rax, -64(%rbp)
	leaq	-104(%rbp), %rsi
	movq	%rbx, %rdi
	callq	arraylist_add_ptr@PLT
	leaq	-80(%rbp), %rsi
	movq	%rbx, %rdi
	callq	arraylist_add_ptr@PLT
	movq	%rbx, %rdi
	xorl	%esi, %esi
	callq	arraylist_get_ptr@PLT
	movq	(%rax), %rdi
	callq	printString@PLT
	movq	%rbx, %rdi
	xorl	%esi, %esi
	callq	arraylist_get_ptr@PLT
	movq	16(%rax), %rax
	movq	(%rax), %rdi
	callq	printString@PLT
	movq	%rbx, %rdi
	xorl	%esi, %esi
	callq	arraylist_get_ptr@PLT
	movq	16(%rax), %rax
	movq	8(%rax), %rdi
	callq	printString@PLT
	movl	$1, %esi
	movq	%rbx, %rdi
	callq	arraylist_get_ptr@PLT
	movq	(%rax), %rdi
	callq	printString@PLT
	movl	$1, %esi
	movq	%rbx, %rdi
	callq	arraylist_get_ptr@PLT
	movq	16(%rax), %rax
	movq	(%rax), %rdi
	callq	printString@PLT
	movl	$1, %esi
	movq	%rbx, %rdi
	callq	arraylist_get_ptr@PLT
	movq	16(%rax), %rax
	movq	8(%rax), %rdi
	callq	printString@PLT
	movq	%rbx, %rdi
	xorl	%esi, %esi
	callq	arraylist_get_ptr@PLT
	movq	16(%rax), %rax
	movq	8(%rax), %r14
	movl	$.L.str2, %edi
	callq	createString@PLT
	movq	%r14, %rdi
	movq	%rax, %rsi
	callq	strcmp_eq@PLT
	testb	$1, %al
	je	.LBB2_2
# %bb.1:                                # %then_0
	movl	$.L.strStr, %edi
	movl	$.L.str6, %esi
	xorl	%eax, %eax
	callq	printf@PLT
.LBB2_2:                                # %endif_0
	movl	$1, %esi
	movq	%rbx, %rdi
	callq	arraylist_get_ptr@PLT
	cmpl	$17, 8(%rax)
	jg	.LBB2_4
# %bb.3:                                # %then_1
	movl	$1, %esi
	movq	%rbx, %rdi
	callq	arraylist_get_ptr@PLT
	movq	(%rax), %rdi
	callq	printString@PLT
	movl	$.L.strStr, %edi
	movl	$.L.str7, %esi
	xorl	%eax, %eax
	callq	printf@PLT
.LBB2_4:                                # %endif_1
	movq	%rsp, %rax
	leaq	-16(%rax), %r14
	movq	%r14, %rsp
	movl	$0, -16(%rax)
	movq	%rbx, %rdi
	callq	length@PLT
	testl	%eax, %eax
	jle	.LBB2_6
	.p2align	4, 0x90
.LBB2_5:                                # %while_body_1
                                        # =>This Inner Loop Header: Depth=1
	movl	$.L.strStr, %edi
	movl	$.L.str8, %esi
	xorl	%eax, %eax
	callq	printf@PLT
	movl	(%r14), %esi
	movq	%rbx, %rdi
	callq	arraylist_get_ptr@PLT
	movq	(%rax), %rdi
	callq	printString@PLT
	movl	(%r14), %esi
	movq	%rbx, %rdi
	callq	arraylist_get_ptr@PLT
	movq	16(%rax), %rax
	movq	(%rax), %rdi
	callq	printString@PLT
	movl	(%r14), %esi
	movq	%rbx, %rdi
	callq	arraylist_get_ptr@PLT
	movq	16(%rax), %rax
	movq	8(%rax), %rdi
	callq	printString@PLT
	movl	(%r14), %r15d
	incl	%r15d
	movl	%r15d, (%r14)
	movq	%rbx, %rdi
	callq	length@PLT
	cmpl	%eax, %r15d
	jl	.LBB2_5
.LBB2_6:                                # %while_end_2
	movq	%rbx, %rdi
	callq	freeList@PLT
	callq	getchar@PLT
	xorl	%eax, %eax
	leaq	-24(%rbp), %rsp
	popq	%rbx
	popq	%r14
	popq	%r15
	popq	%rbp
	.cfi_def_cfa %rsp, 8
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
.L.str0:
	.asciz	"Alice"
	.size	.L.str0, 6

	.type	.L.str1,@object                 # @.str1
.L.str1:
	.asciz	"Rua A"
	.size	.L.str1, 6

	.type	.L.str2,@object                 # @.str2
.L.str2:
	.asciz	"S\303\243o Paulo"
	.size	.L.str2, 11

	.type	.L.str3,@object                 # @.str3
.L.str3:
	.asciz	"Bob"
	.size	.L.str3, 4

	.type	.L.str4,@object                 # @.str4
.L.str4:
	.asciz	"Av. Central"
	.size	.L.str4, 12

	.type	.L.str5,@object                 # @.str5
.L.str5:
	.asciz	"Rio de Janeiro"
	.size	.L.str5, 15

	.type	.L.str6,@object                 # @.str6
	.p2align	4, 0x0
.L.str6:
	.asciz	"A primeira pessoa mora em S\303\243o Paulo"
	.size	.L.str6, 37

	.type	.L.str7,@object                 # @.str7
	.p2align	4, 0x0
.L.str7:
	.asciz	" \303\251 menor de idade"
	.size	.L.str7, 19

	.type	.L.str8,@object                 # @.str8
.L.str8:
	.asciz	"Pessoa:"
	.size	.L.str8, 8

	.section	".note.GNU-stack","",@progbits
