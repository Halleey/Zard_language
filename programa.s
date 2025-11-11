	.text
	.file	"programa.ll"
	.globl	print_Set                       # -- Begin function print_Set
	.p2align	4, 0x90
	.type	print_Set,@function
print_Set:                              # @print_Set
	.cfi_startproc
# %bb.0:                                # %entry
	retq
.Lfunc_end0:
	.size	print_Set, .Lfunc_end0-print_Set
	.cfi_endproc
                                        # -- End function
	.globl	print_Set_int                   # -- Begin function print_Set_int
	.p2align	4, 0x90
	.type	print_Set_int,@function
print_Set_int:                          # @print_Set_int
	.cfi_startproc
# %bb.0:                                # %entry
	pushq	%rax
	.cfi_def_cfa_offset 16
	movq	(%rdi), %rdi
	callq	arraylist_print_int@PLT
	popq	%rax
	.cfi_def_cfa_offset 8
	retq
.Lfunc_end1:
	.size	print_Set_int, .Lfunc_end1-print_Set_int
	.cfi_endproc
                                        # -- End function
	.globl	print_Set_double                # -- Begin function print_Set_double
	.p2align	4, 0x90
	.type	print_Set_double,@function
print_Set_double:                       # @print_Set_double
	.cfi_startproc
# %bb.0:                                # %entry
	pushq	%rax
	.cfi_def_cfa_offset 16
	movq	(%rdi), %rdi
	callq	arraylist_print_double@PLT
	popq	%rax
	.cfi_def_cfa_offset 8
	retq
.Lfunc_end2:
	.size	print_Set_double, .Lfunc_end2-print_Set_double
	.cfi_endproc
                                        # -- End function
	.globl	Set_double_add                  # -- Begin function Set_double_add
	.p2align	4, 0x90
	.type	Set_double_add,@function
Set_double_add:                         # @Set_double_add
	.cfi_startproc
# %bb.0:                                # %entry
	pushq	%rbx
	.cfi_def_cfa_offset 16
	.cfi_offset %rbx, -16
	movq	%rdi, %rbx
	movq	(%rdi), %rdi
	callq	arraylist_add_double@PLT
	movq	%rbx, %rax
	popq	%rbx
	.cfi_def_cfa_offset 8
	retq
.Lfunc_end3:
	.size	Set_double_add, .Lfunc_end3-Set_double_add
	.cfi_endproc
                                        # -- End function
	.globl	Set_int_add                     # -- Begin function Set_int_add
	.p2align	4, 0x90
	.type	Set_int_add,@function
Set_int_add:                            # @Set_int_add
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
.Lfunc_end4:
	.size	Set_int_add, .Lfunc_end4-Set_int_add
	.cfi_endproc
                                        # -- End function
	.section	.rodata.cst8,"aM",@progbits,8
	.p2align	3, 0x0                          # -- Begin function main
.LCPI5_0:
	.quad	0x40091eb851eb851f              # double 3.1400000000000001
	.text
	.globl	main
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
	movl	$10, %edi
	callq	arraylist_create@PLT
	movl	$10, %edi
	callq	arraylist_create_double@PLT
	movq	%rax, 8(%rsp)
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
	movq	%rbx, (%rsp)
	leaq	16(%rsp), %rbx
	movq	%rbx, %rdi
	movl	$1, %esi
	callq	Set_int_add@PLT
	leaq	8(%rsp), %r14
	movsd	.LCPI5_0(%rip), %xmm0           # xmm0 = [3.1400000000000001E+0,0.0E+0]
	movq	%r14, %rdi
	callq	Set_double_add@PLT
	movq	%r14, %rdi
	callq	print_Set_double@PLT
	movq	%rsp, %rdi
	callq	print_Set_int@PLT
	movq	%rbx, %rdi
	callq	print_Set_int@PLT
	callq	getchar@PLT
	xorl	%eax, %eax
	addq	$40, %rsp
	.cfi_def_cfa_offset 24
	popq	%rbx
	.cfi_def_cfa_offset 16
	popq	%r14
	.cfi_def_cfa_offset 8
	retq
.Lfunc_end5:
	.size	main, .Lfunc_end5-main
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

	.section	".note.GNU-stack","",@progbits
