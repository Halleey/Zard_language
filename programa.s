	.text
	.file	"programa.ll"
	.globl	bubbleSort                      # -- Begin function bubbleSort
	.p2align	4, 0x90
	.type	bubbleSort,@function
bubbleSort:                             # @bubbleSort
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
	subq	$40, %rsp
	.cfi_offset %rbx, -56
	.cfi_offset %r12, -48
	.cfi_offset %r13, -40
	.cfi_offset %r14, -32
	.cfi_offset %r15, -24
	movq	%rdi, %rbx
	callq	arraylist_size_int@PLT
	movl	%eax, %r12d
	movl	$0, -44(%rbp)
	decl	%r12d
	testl	%r12d, %r12d
	jle	.LBB0_15
# %bb.1:                                # %for_body_2.lr.ph
	movl	%r12d, -60(%rbp)                # 4-byte Spill
	jmp	.LBB0_2
	.p2align	4, 0x90
.LBB0_14:                               # %for_end_9
                                        #   in Loop: Header=BB0_2 Depth=1
	incl	%ecx
	movl	%ecx, -44(%rbp)
	cmpl	%r12d, %ecx
	jge	.LBB0_15
.LBB0_2:                                # %for_body_2
                                        # =>This Loop Header: Depth=1
                                        #     Child Loop BB0_4 Depth 2
                                        #       Child Loop BB0_7 Depth 3
                                        #       Child Loop BB0_10 Depth 3
                                        #       Child Loop BB0_12 Depth 3
	movq	%rsp, %rax
	leaq	-16(%rax), %r13
	movq	%r13, %rsp
	movl	$0, -16(%rax)
	movl	-44(%rbp), %ecx
	movl	%r12d, %eax
	subl	%ecx, %eax
	testl	%eax, %eax
	jle	.LBB0_14
# %bb.3:                                # %for_body_7.lr.ph
                                        #   in Loop: Header=BB0_2 Depth=1
	xorl	%eax, %eax
	movq	%r13, -56(%rbp)                 # 8-byte Spill
	jmp	.LBB0_4
	.p2align	4, 0x90
.LBB0_13:                               # %endif_0
                                        #   in Loop: Header=BB0_4 Depth=2
	movq	-56(%rbp), %r13                 # 8-byte Reload
	movl	(%r13), %eax
	incl	%eax
	movl	%eax, (%r13)
	movl	-44(%rbp), %ecx
	movl	%r12d, %edx
	subl	%ecx, %edx
	cmpl	%edx, %eax
	jge	.LBB0_14
.LBB0_4:                                # %for_body_7
                                        #   Parent Loop BB0_2 Depth=1
                                        # =>  This Loop Header: Depth=2
                                        #       Child Loop BB0_7 Depth 3
                                        #       Child Loop BB0_10 Depth 3
                                        #       Child Loop BB0_12 Depth 3
	movq	%rsp, %r14
	leaq	-16(%r14), %rcx
	movq	%rcx, -80(%rbp)                 # 8-byte Spill
	movq	%rcx, %rsp
	movl	%eax, %esi
	movq	%rsp, %r15
	leaq	-16(%r15), %rdx
	movq	%rdx, %rsp
	movq	%rbx, %rdi
	callq	arraylist_get_int@PLT
	movl	-16(%r15), %eax
	movl	%eax, -16(%r14)
	movq	%rsp, %r15
	leaq	-16(%r15), %rax
	movq	%rax, -72(%rbp)                 # 8-byte Spill
	movq	%rax, %rsp
	movl	(%r13), %esi
	incl	%esi
	movq	%rsp, %r13
	leaq	-16(%r13), %rdx
	movq	%rdx, %rsp
	movq	%rbx, %rdi
	callq	arraylist_get_int@PLT
	movl	-16(%r13), %eax
	movl	%eax, -16(%r15)
	cmpl	%eax, -16(%r14)
	jle	.LBB0_13
# %bb.5:                                # %then_0
                                        #   in Loop: Header=BB0_4 Depth=2
	movq	-56(%rbp), %r12                 # 8-byte Reload
	movl	(%r12), %esi
	movq	%rbx, %rdi
	callq	arraylist_remove_int@PLT
	movl	(%r12), %esi
	movq	%rbx, %rdi
	callq	arraylist_remove_int@PLT
	movq	%rsp, %r15
	leaq	-16(%r15), %r13
	movq	%r13, %rsp
	movl	$4, %edi
	callq	arraylist_create_int@PLT
	movq	%rax, -16(%r15)
	movq	%rsp, %rax
	leaq	-16(%rax), %r14
	movq	%r14, %rsp
	movl	$0, -16(%rax)
	cmpl	$0, (%r12)
	movq	-16(%r15), %r15
	jle	.LBB0_8
# %bb.6:                                # %for_body_12.lr.ph
                                        #   in Loop: Header=BB0_4 Depth=2
	xorl	%eax, %eax
	.p2align	4, 0x90
.LBB0_7:                                # %for_body_12
                                        #   Parent Loop BB0_2 Depth=1
                                        #     Parent Loop BB0_4 Depth=2
                                        # =>    This Inner Loop Header: Depth=3
	movl	%eax, %esi
	movq	%rsp, %r12
	leaq	-16(%r12), %rdx
	movq	%rdx, %rsp
	movq	%rbx, %rdi
	callq	arraylist_get_int@PLT
	movl	-16(%r12), %esi
	movq	%r15, %rdi
	callq	arraylist_add_int@PLT
	movl	(%r14), %eax
	incl	%eax
	movl	%eax, (%r14)
	movq	-56(%rbp), %rcx                 # 8-byte Reload
	cmpl	(%rcx), %eax
	movq	(%r13), %r15
	jl	.LBB0_7
.LBB0_8:                                # %for_end_14
                                        #   in Loop: Header=BB0_4 Depth=2
	movq	-72(%rbp), %rax                 # 8-byte Reload
	movl	(%rax), %esi
	movq	%r15, %rdi
	callq	arraylist_add_int@PLT
	movq	(%r13), %rdi
	movq	-80(%rbp), %rax                 # 8-byte Reload
	movl	(%rax), %esi
	callq	arraylist_add_int@PLT
	movq	%rsp, %rax
	leaq	-16(%rax), %r14
	movq	%r14, %rsp
	movq	-56(%rbp), %rcx                 # 8-byte Reload
	movl	(%rcx), %r15d
	movl	%r15d, -16(%rax)
	.p2align	4, 0x90
.LBB0_10:                               # %for_body_17
                                        #   Parent Loop BB0_2 Depth=1
                                        #     Parent Loop BB0_4 Depth=2
                                        # =>    This Inner Loop Header: Depth=3
	movq	%rbx, %rdi
	callq	arraylist_size_int@PLT
	cmpl	%eax, %r15d
	jge	.LBB0_11
# %bb.9:                                # %for_body_17
                                        #   in Loop: Header=BB0_10 Depth=3
	movq	(%r13), %r15
	movl	(%r14), %esi
	movq	%rsp, %r12
	leaq	-16(%r12), %rdx
	movq	%rdx, %rsp
	movq	%rbx, %rdi
	callq	arraylist_get_int@PLT
	movl	-16(%r12), %esi
	movq	%r15, %rdi
	callq	arraylist_add_int@PLT
	movl	(%r14), %r15d
	incl	%r15d
	movl	%r15d, (%r14)
	jmp	.LBB0_10
	.p2align	4, 0x90
.LBB0_11:                               # %for_end_19
                                        #   in Loop: Header=BB0_4 Depth=2
	movq	%rbx, %rdi
	callq	arraylist_clear_int@PLT
	movq	%rsp, %rax
	leaq	-16(%rax), %r14
	movq	%r14, %rsp
	movl	$0, -16(%rax)
	movq	(%r13), %rdi
	callq	arraylist_size_int@PLT
	testl	%eax, %eax
	movl	-60(%rbp), %r12d                # 4-byte Reload
	jle	.LBB0_13
	.p2align	4, 0x90
.LBB0_12:                               # %for_body_22
                                        #   Parent Loop BB0_2 Depth=1
                                        #     Parent Loop BB0_4 Depth=2
                                        # =>    This Inner Loop Header: Depth=3
	movq	(%r13), %rdi
	movl	(%r14), %esi
	movq	%rsp, %r15
	leaq	-16(%r15), %rdx
	movq	%rdx, %rsp
	callq	arraylist_get_int@PLT
	movl	-16(%r15), %esi
	movq	%rbx, %rdi
	callq	arraylist_add_int@PLT
	movl	(%r14), %r15d
	incl	%r15d
	movl	%r15d, (%r14)
	movq	(%r13), %rdi
	callq	arraylist_size_int@PLT
	cmpl	%eax, %r15d
	jl	.LBB0_12
	jmp	.LBB0_13
.LBB0_15:                               # %for_end_4
	leaq	-40(%rbp), %rsp
	popq	%rbx
	popq	%r12
	popq	%r13
	popq	%r14
	popq	%r15
	popq	%rbp
	.cfi_def_cfa %rsp, 8
	retq
.Lfunc_end0:
	.size	bubbleSort, .Lfunc_end0-bubbleSort
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
	subq	$32, %rsp
	.cfi_def_cfa_offset 48
	.cfi_offset %rbx, -16
	movl	$5, %edi
	callq	arraylist_create_int@PLT
	movq	%rax, %rbx
	movabsq	$55834574871, %rax              # imm = 0xD00000017
	movq	%rax, 12(%rsp)
	movabsq	$171798691849, %rax             # imm = 0x2800000009
	movq	%rax, 20(%rsp)
	movl	$80, 28(%rsp)
	leaq	12(%rsp), %rsi
	movl	$5, %edx
	movq	%rbx, %rdi
	callq	arraylist_addAll_int@PLT
	movq	%rbx, %rdi
	callq	bubbleSort@PLT
	movq	%rbx, %rdi
	callq	arraylist_print_int@PLT
	movq	%rbx, %rdi
	callq	arraylist_free_int@PLT
	callq	getchar@PLT
	xorl	%eax, %eax
	addq	$32, %rsp
	.cfi_def_cfa_offset 16
	popq	%rbx
	.cfi_def_cfa_offset 8
	retq
.Lfunc_end1:
	.size	main, .Lfunc_end1-main
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
