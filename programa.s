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
	.globl	print_Set_boolean               # -- Begin function print_Set_boolean
	.p2align	4, 0x90
	.type	print_Set_boolean,@function
print_Set_boolean:                      # @print_Set_boolean
	.cfi_startproc
# %bb.0:                                # %entry
	pushq	%rax
	.cfi_def_cfa_offset 16
	movq	(%rdi), %rdi
	callq	arraylist_print_bool@PLT
	popq	%rax
	.cfi_def_cfa_offset 8
	retq
.Lfunc_end1:
	.size	print_Set_boolean, .Lfunc_end1-print_Set_boolean
	.cfi_endproc
                                        # -- End function
	.globl	Set_boolean_add                 # -- Begin function Set_boolean_add
	.p2align	4, 0x90
	.type	Set_boolean_add,@function
Set_boolean_add:                        # @Set_boolean_add
	.cfi_startproc
# %bb.0:                                # %entry
	pushq	%rbp
	.cfi_def_cfa_offset 16
	.cfi_offset %rbp, -16
	movq	%rsp, %rbp
	.cfi_def_cfa_register %rbp
	pushq	%r15
	pushq	%r14
	pushq	%r13
	pushq	%r12
	pushq	%rbx
	pushq	%rax
	.cfi_offset %rbx, -56
	.cfi_offset %r12, -48
	.cfi_offset %r13, -40
	.cfi_offset %r14, -32
	.cfi_offset %r15, -24
	movl	%esi, %r14d
	movq	%rdi, %rbx
	movq	(%rdi), %rdi
	callq	arraylist_size_bool@PLT
	testl	%eax, %eax
	movq	(%rbx), %rdi
	jle	.LBB2_5
# %bb.1:                                # %while_body_1.lr.ph
	xorl	%r15d, %r15d
	movl	%r14d, %r12d
	andb	$1, %r12b
	.p2align	4, 0x90
.LBB2_3:                                # %while_body_1
                                        # =>This Inner Loop Header: Depth=1
	movq	%rsp, %r13
	leaq	-16(%r13), %rdx
	movq	%rdx, %rsp
	movq	%r15, %rsi
	callq	arraylist_get_bool@PLT
	cmpb	%r12b, -16(%r13)
	je	.LBB2_4
# %bb.2:                                # %while_cond_0
                                        #   in Loop: Header=BB2_3 Depth=1
	movq	(%rbx), %rdi
	callq	arraylist_size_bool@PLT
	movq	(%rbx), %rdi
	incq	%r15
	cmpl	%eax, %r15d
	jl	.LBB2_3
.LBB2_5:                                # %while_end_2
	movzbl	%r14b, %esi
	callq	arraylist_add_bool@PLT
.LBB2_4:                                # %then_0
	movq	%rbx, %rax
	leaq	-40(%rbp), %rsp
	popq	%rbx
	popq	%r12
	popq	%r13
	popq	%r14
	popq	%r15
	popq	%rbp
	.cfi_def_cfa %rsp, 8
	retq
.Lfunc_end2:
	.size	Set_boolean_add, .Lfunc_end2-Set_boolean_add
	.cfi_endproc
                                        # -- End function
	.globl	Set_boolean_get                 # -- Begin function Set_boolean_get
	.p2align	4, 0x90
	.type	Set_boolean_get,@function
Set_boolean_get:                        # @Set_boolean_get
	.cfi_startproc
# %bb.0:                                # %entry
	pushq	%rbx
	.cfi_def_cfa_offset 16
	subq	$16, %rsp
	.cfi_def_cfa_offset 32
	.cfi_offset %rbx, -16
	movq	%rdi, %rbx
	movq	(%rdi), %rdi
	movl	%esi, %esi
	leaq	15(%rsp), %rdx
	callq	arraylist_get_bool@PLT
	movzbl	15(%rsp), %esi
	movl	$.L.strInt, %edi
	xorl	%eax, %eax
	callq	printf@PLT
	movq	%rbx, %rax
	addq	$16, %rsp
	.cfi_def_cfa_offset 16
	popq	%rbx
	.cfi_def_cfa_offset 8
	retq
.Lfunc_end3:
	.size	Set_boolean_get, .Lfunc_end3-Set_boolean_get
	.cfi_endproc
                                        # -- End function
	.globl	Set_boolean_remove              # -- Begin function Set_boolean_remove
	.p2align	4, 0x90
	.type	Set_boolean_remove,@function
Set_boolean_remove:                     # @Set_boolean_remove
	.cfi_startproc
# %bb.0:                                # %entry
	pushq	%rbx
	.cfi_def_cfa_offset 16
	.cfi_offset %rbx, -16
	movq	%rdi, %rbx
	movq	(%rdi), %rdi
	movl	%esi, %esi
	callq	arraylist_remove_bool@PLT
	movq	%rbx, %rax
	popq	%rbx
	.cfi_def_cfa_offset 8
	retq
.Lfunc_end4:
	.size	Set_boolean_remove, .Lfunc_end4-Set_boolean_remove
	.cfi_endproc
                                        # -- End function
	.globl	main                            # -- Begin function main
	.p2align	4, 0x90
	.type	main,@function
main:                                   # @main
	.cfi_startproc
# %bb.0:
	pushq	%rbx
	.cfi_def_cfa_offset 16
	subq	$16, %rsp
	.cfi_def_cfa_offset 32
	.cfi_offset %rbx, -16
	movl	$10, %edi
	callq	arraylist_create_bool@PLT
	movq	%rax, 8(%rsp)
	leaq	8(%rsp), %rbx
	movq	%rbx, %rdi
	xorl	%esi, %esi
	callq	Set_boolean_add@PLT
	movq	%rbx, %rdi
	movl	$1, %esi
	callq	Set_boolean_add@PLT
	movq	%rbx, %rdi
	xorl	%esi, %esi
	callq	Set_boolean_remove@PLT
	movq	%rbx, %rdi
	xorl	%esi, %esi
	callq	Set_boolean_get@PLT
	movq	%rbx, %rdi
	callq	print_Set_boolean@PLT
	callq	getchar@PLT
	xorl	%eax, %eax
	addq	$16, %rsp
	.cfi_def_cfa_offset 16
	popq	%rbx
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
