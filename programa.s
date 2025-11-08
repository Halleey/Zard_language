	.text
	.file	"programa.ll"
	.globl	print_Set                       # -- Begin function print_Set
	.p2align	4, 0x90
	.type	print_Set,@function
print_Set:                              # @print_Set
	.cfi_startproc
# %bb.0:                                # %entry
	pushq	%rax
	.cfi_def_cfa_offset 16
	movq	(%rdi), %rdi
	callq	arraylist_print_int@PLT
	popq	%rax
	.cfi_def_cfa_offset 8
	retq
.Lfunc_end0:
	.size	print_Set, .Lfunc_end0-print_Set
	.cfi_endproc
                                        # -- End function
	.globl	Set_add                         # -- Begin function Set_add
	.p2align	4, 0x90
	.type	Set_add,@function
Set_add:                                # @Set_add
	.cfi_startproc
# %bb.0:                                # %entry
	pushq	%rbx
	.cfi_def_cfa_offset 16
	.cfi_offset %rbx, -16
	movq	%rdi, %rbx
	movq	(%rdi), %rdi
	callq	arraylist_add_int@PLT
	movq	%rbx, %rax
	popq	%rbx
	.cfi_def_cfa_offset 8
	retq
.Lfunc_end1:
	.size	Set_add, .Lfunc_end1-Set_add
	.cfi_endproc
                                        # -- End function
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
	subq	$40, %rsp
	.cfi_def_cfa_offset 64
	.cfi_offset %rbx, -24
	.cfi_offset %r14, -16
	movl	$10, %edi
	callq	arraylist_create_int@PLT
	movq	%rax, 16(%rsp)
	movl	$4, %edi
	callq	arraylist_create_int@PLT
	movq	%rax, %rbx
	movabsq	$17179869187, %rax              # imm = 0x400000003
	movq	%rax, 28(%rsp)
	movl	$5, 36(%rsp)
	leaq	28(%rsp), %rsi
	movl	$3, %edx
	movq	%rbx, %rdi
	callq	arraylist_addAll_int@PLT
	movq	%rbx, 8(%rsp)
	leaq	8(%rsp), %rbx
	movq	%rbx, %rdi
	movl	$2, %esi
	callq	Set_add@PLT
	leaq	16(%rsp), %r14
	movq	%r14, %rdi
	movl	$1, %esi
	callq	Set_add@PLT
	movq	%r14, %rdi
	movl	$22, %esi
	callq	Set_add@PLT
	movq	%r14, %rdi
	movl	$99, %esi
	callq	Set_add@PLT
	movl	$.L.strStr, %edi
	movl	$.L.str0, %esi
	xorl	%eax, %eax
	callq	printf@PLT
	movq	%r14, %rdi
	callq	print_Set@PLT
	movq	%rbx, %rdi
	callq	print_Set@PLT
	callq	getchar@PLT
	xorl	%eax, %eax
	addq	$40, %rsp
	.cfi_def_cfa_offset 24
	popq	%rbx
	.cfi_def_cfa_offset 16
	popq	%r14
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
	.asciz	"=== Conte\303\272do do Set ==="
	.size	.L.str0, 25

	.section	".note.GNU-stack","",@progbits
