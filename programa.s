	.text
	.file	"programa.ll"
	.globl	main                            # -- Begin function main
	.p2align	4, 0x90
	.type	main,@function
main:                                   # @main
	.cfi_startproc
# %bb.0:
	pushq	%r14
	.cfi_def_cfa_offset 16
	pushq	%rbx
	.cfi_def_cfa_offset 24
	subq	$24, %rsp
	.cfi_def_cfa_offset 48
	.cfi_offset %rbx, -24
	.cfi_offset %r14, -16
	movl	$4, %edi
	callq	arraylist_create_int@PLT
	movq	%rax, %rbx
	movl	$4, %edi
	callq	arraylist_create@PLT
	movq	%rax, %r14
	movl	$.L.str0, %edi
	callq	createString@PLT
	movq	%r14, %rdi
	movq	%rax, %rsi
	callq	arraylist_add_String@PLT
	movl	$.L.str1, %edi
	callq	createString@PLT
	movq	%r14, %rdi
	movq	%rax, %rsi
	callq	arraylist_add_String@PLT
	movq	%rbx, %rdi
	movl	$3, %esi
	callq	arraylist_add_int@PLT
	movabsq	$17179869187, %rax              # imm = 0x400000003
	movq	%rax, 8(%rsp)
	movabsq	$8589934597, %rax               # imm = 0x200000005
	movq	%rax, 16(%rsp)
	leaq	8(%rsp), %rsi
	movl	$4, %edx
	movq	%rbx, %rdi
	callq	arraylist_addAll_int@PLT
	movq	%rbx, %rdi
	callq	arraylist_size_int@PLT
	movl	$.L.strInt, %edi
	movl	%eax, %esi
	xorl	%eax, %eax
	callq	printf@PLT
	movq	%rbx, %rdi
	callq	arraylist_size_int@PLT
	movl	$.L.strInt, %edi
	movl	%eax, %esi
	xorl	%eax, %eax
	callq	printf@PLT
	movq	%rbx, %rdi
	xorl	%esi, %esi
	callq	arraylist_remove_int@PLT
	leaq	4(%rsp), %rdx
	movl	$1, %esi
	movq	%rbx, %rdi
	callq	arraylist_get_int@PLT
	movl	4(%rsp), %esi
	movl	$.L.strInt, %edi
	xorl	%eax, %eax
	callq	printf@PLT
	movq	%rbx, %rdi
	callq	arraylist_clear_int@PLT
	movq	%rbx, %rdi
	callq	arraylist_free_int@PLT
	movq	%r14, %rdi
	callq	freeList@PLT
	callq	getchar@PLT
	xorl	%eax, %eax
	addq	$24, %rsp
	.cfi_def_cfa_offset 24
	popq	%rbx
	.cfi_def_cfa_offset 16
	popq	%r14
	.cfi_def_cfa_offset 8
	retq
.Lfunc_end0:
	.size	main, .Lfunc_end0-main
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
	.asciz	"zard"
	.size	.L.str0, 5

	.type	.L.str1,@object                 # @.str1
.L.str1:
	.asciz	"angel"
	.size	.L.str1, 6

	.section	".note.GNU-stack","",@progbits
