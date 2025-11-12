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
.Lfunc_end1:
	.size	print_Set_double, .Lfunc_end1-print_Set_double
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
.Lfunc_end2:
	.size	print_Set_int, .Lfunc_end2-print_Set_int
	.cfi_endproc
                                        # -- End function
	.globl	Set_double_add                  # -- Begin function Set_double_add
	.p2align	4, 0x90
	.type	Set_double_add,@function
Set_double_add:                         # @Set_double_add
	.cfi_startproc
# %bb.0:                                # %entry
	pushq	%rbp
	.cfi_def_cfa_offset 16
	.cfi_offset %rbp, -16
	movq	%rsp, %rbp
	.cfi_def_cfa_register %rbp
	pushq	%r15
	pushq	%r14
	pushq	%rbx
	pushq	%rax
	.cfi_offset %rbx, -40
	.cfi_offset %r14, -32
	.cfi_offset %r15, -24
	movsd	%xmm0, -32(%rbp)                # 8-byte Spill
	movq	%rdi, %rbx
	movq	(%rdi), %rdi
	callq	arraylist_size_double@PLT
	testl	%eax, %eax
	movq	(%rbx), %rdi
	jle	.LBB3_5
# %bb.1:                                # %while_body_1.lr.ph
	xorl	%r14d, %r14d
	.p2align	4, 0x90
.LBB3_3:                                # %while_body_1
                                        # =>This Inner Loop Header: Depth=1
	movq	%rsp, %r15
	leaq	-16(%r15), %rdx
	movq	%rdx, %rsp
	movq	%r14, %rsi
	callq	arraylist_get_double@PLT
	movsd	-16(%r15), %xmm0                # xmm0 = mem[0],zero
	ucomisd	-32(%rbp), %xmm0                # 8-byte Folded Reload
	jne	.LBB3_2
	jnp	.LBB3_4
.LBB3_2:                                # %while_cond_0
                                        #   in Loop: Header=BB3_3 Depth=1
	movq	(%rbx), %rdi
	callq	arraylist_size_double@PLT
	movq	(%rbx), %rdi
	incq	%r14
	cmpl	%eax, %r14d
	jl	.LBB3_3
.LBB3_5:                                # %while_end_2
	movsd	-32(%rbp), %xmm0                # 8-byte Reload
                                        # xmm0 = mem[0],zero
	callq	arraylist_add_double@PLT
.LBB3_4:                                # %then_0
	movq	%rbx, %rax
	leaq	-24(%rbp), %rsp
	popq	%rbx
	popq	%r14
	popq	%r15
	popq	%rbp
	.cfi_def_cfa %rsp, 8
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
	jle	.LBB4_5
# %bb.1:                                # %while_body_4.lr.ph
	xorl	%r15d, %r15d
	.p2align	4, 0x90
.LBB4_3:                                # %while_body_4
                                        # =>This Inner Loop Header: Depth=1
	movq	%rsp, %r12
	leaq	-16(%r12), %rdx
	movq	%rdx, %rsp
	movq	%r15, %rsi
	callq	arraylist_get_int@PLT
	cmpl	%r14d, -16(%r12)
	je	.LBB4_4
# %bb.2:                                # %while_cond_3
                                        #   in Loop: Header=BB4_3 Depth=1
	movq	(%rbx), %rdi
	callq	arraylist_size_int@PLT
	movq	(%rbx), %rdi
	incq	%r15
	cmpl	%eax, %r15d
	jl	.LBB4_3
.LBB4_5:                                # %while_end_5
	movl	%r14d, %esi
	callq	arraylist_add_int@PLT
.LBB4_4:                                # %then_1
	movq	%rbx, %rax
	leaq	-32(%rbp), %rsp
	popq	%rbx
	popq	%r12
	popq	%r14
	popq	%r15
	popq	%rbp
	.cfi_def_cfa %rsp, 8
	retq
.Lfunc_end4:
	.size	Set_int_add, .Lfunc_end4-Set_int_add
	.cfi_endproc
                                        # -- End function
	.globl	Set_double_remove               # -- Begin function Set_double_remove
	.p2align	4, 0x90
	.type	Set_double_remove,@function
Set_double_remove:                      # @Set_double_remove
	.cfi_startproc
# %bb.0:                                # %entry
	pushq	%rbx
	.cfi_def_cfa_offset 16
	.cfi_offset %rbx, -16
	movq	%rdi, %rbx
	movq	(%rdi), %rdi
	cvttsd2si	%xmm0, %rsi
	callq	arraylist_remove_double@PLT
	movq	%rbx, %rax
	popq	%rbx
	.cfi_def_cfa_offset 8
	retq
.Lfunc_end5:
	.size	Set_double_remove, .Lfunc_end5-Set_double_remove
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
.Lfunc_end6:
	.size	Set_int_remove, .Lfunc_end6-Set_int_remove
	.cfi_endproc
                                        # -- End function
	.section	.rodata.cst8,"aM",@progbits,8
	.p2align	3, 0x0                          # -- Begin function main
.LCPI7_0:
	.quad	0x4000cccccccccccd              # double 2.1000000000000001
	.text
	.globl	main
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
	callq	arraylist_create_double@PLT
	movq	%rax, 8(%rsp)
	leaq	8(%rsp), %rbx
	movsd	.LCPI7_0(%rip), %xmm0           # xmm0 = [2.1000000000000001E+0,0.0E+0]
	movq	%rbx, %rdi
	callq	Set_double_add@PLT
	movq	%rbx, %rdi
	movsd	.LCPI7_0(%rip), %xmm0           # xmm0 = [2.1000000000000001E+0,0.0E+0]
	callq	Set_double_add@PLT
	movq	%rbx, %rdi
	xorl	%esi, %esi
	callq	Set_double_remove@PLT
	movq	%rbx, %rdi
	callq	print_Set_double@PLT
	movl	$10, %edi
	callq	arraylist_create_int@PLT
	movq	%rax, (%rsp)
	movq	%rsp, %rbx
	movq	%rbx, %rdi
	movl	$1, %esi
	callq	Set_int_add@PLT
	movq	%rbx, %rdi
	movl	$1, %esi
	callq	Set_int_add@PLT
	movq	%rbx, %rdi
	xorl	%esi, %esi
	callq	Set_int_remove@PLT
	movq	%rbx, %rdi
	callq	print_Set_int@PLT
	callq	getchar@PLT
	xorl	%eax, %eax
	addq	$16, %rsp
	.cfi_def_cfa_offset 16
	popq	%rbx
	.cfi_def_cfa_offset 8
	retq
.Lfunc_end7:
	.size	main, .Lfunc_end7-main
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
