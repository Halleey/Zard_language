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
	.globl	Set_int_adicionar               # -- Begin function Set_int_adicionar
	.p2align	4, 0x90
	.type	Set_int_adicionar,@function
Set_int_adicionar:                      # @Set_int_adicionar
	.cfi_startproc
# %bb.0:                                # %entry
	pushq	%rbp
	.cfi_def_cfa_offset 16
	.cfi_offset %rbp, -16
	movq	%rsp, %rbp
	.cfi_def_cfa_register %rbp
	pushq	%r15
	pushq	%r14
	pushq	%r12
	pushq	%rbx
	.cfi_offset %rbx, -48
	.cfi_offset %r12, -40
	.cfi_offset %r14, -32
	.cfi_offset %r15, -24
	movl	%esi, %r14d
	movq	%rdi, %rbx
	movq	(%rdi), %rdi
	callq	arraylist_size_int@PLT
	testl	%eax, %eax
	movq	(%rbx), %rdi
	jle	.LBB2_5
# %bb.1:                                # %while_body_1.lr.ph
	xorl	%r15d, %r15d
	.p2align	4, 0x90
.LBB2_3:                                # %while_body_1
                                        # =>This Inner Loop Header: Depth=1
	movq	%rsp, %r12
	leaq	-16(%r12), %rdx
	movq	%rdx, %rsp
	movq	%r15, %rsi
	callq	arraylist_get_int@PLT
	cmpl	%r14d, -16(%r12)
	je	.LBB2_4
# %bb.2:                                # %while_cond_0
                                        #   in Loop: Header=BB2_3 Depth=1
	movq	(%rbx), %rdi
	callq	arraylist_size_int@PLT
	movq	(%rbx), %rdi
	incq	%r15
	cmpl	%eax, %r15d
	jl	.LBB2_3
.LBB2_5:                                # %while_end_2
	movl	%r14d, %esi
	callq	arraylist_add_int@PLT
.LBB2_4:                                # %then_0
	movq	%rbx, %rax
	leaq	-32(%rbp), %rsp
	popq	%rbx
	popq	%r12
	popq	%r14
	popq	%r15
	popq	%rbp
	.cfi_def_cfa %rsp, 8
	retq
.Lfunc_end2:
	.size	Set_int_adicionar, .Lfunc_end2-Set_int_adicionar
	.cfi_endproc
                                        # -- End function
	.globl	Set_int_remove                  # -- Begin function Set_int_remove
	.p2align	4, 0x90
	.type	Set_int_remove,@function
Set_int_remove:                         # @Set_int_remove
	.cfi_startproc
# %bb.0:                                # %entry
	pushq	%rbx
	.cfi_def_cfa_offset 16
	.cfi_offset %rbx, -16
	movq	%rdi, %rbx
	movq	(%rdi), %rdi
	movl	%esi, %esi
	callq	arraylist_remove_int@PLT
	movq	%rbx, %rax
	popq	%rbx
	.cfi_def_cfa_offset 8
	retq
.Lfunc_end3:
	.size	Set_int_remove, .Lfunc_end3-Set_int_remove
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
	pushq	%rax
	.cfi_def_cfa_offset 32
	.cfi_offset %rbx, -24
	.cfi_offset %r14, -16
	movl	$10, %edi
	callq	arraylist_create_int@PLT
	movq	%rax, (%rsp)
	movq	%rsp, %rbx
	movq	%rbx, %rdi
	movl	$1, %esi
	callq	Set_int_adicionar@PLT
	movl	$4, %edi
	callq	arraylist_create@PLT
	movq	%rax, %r14
	movl	$.L.str0, %edi
	callq	createString@PLT
	movq	%r14, %rdi
	movq	%rax, %rsi
	callq	arraylist_add_String@PLT
	movq	%r14, %rdi
	callq	arraylist_print_string@PLT
	movq	%rbx, %rdi
	callq	print_Set_int@PLT
	movq	%r14, %rdi
	callq	freeList@PLT
	callq	getchar@PLT
	xorl	%eax, %eax
	addq	$8, %rsp
	.cfi_def_cfa_offset 24
	popq	%rbx
	.cfi_def_cfa_offset 16
	popq	%r14
	.cfi_def_cfa_offset 8
	retq
.Lfunc_end4:
	.size	main, .Lfunc_end4-main
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
	.asciz	"testando"
	.size	.L.str0, 9

	.section	".note.GNU-stack","",@progbits
