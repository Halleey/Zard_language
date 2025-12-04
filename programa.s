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
	subq	$56, %rsp
	.cfi_offset %rbx, -56
	.cfi_offset %r12, -48
	.cfi_offset %r13, -40
	.cfi_offset %r14, -32
	.cfi_offset %r15, -24
	movq	%rdi, %rbx
	callq	arraylist_size_int@PLT
                                        # kill: def $eax killed $eax def $rax
	movq	%rax, %rcx
	movq	%rax, -72(%rbp)                 # 8-byte Spill
	decl	%eax
	movl	%eax, -44(%rbp)                 # 4-byte Spill
	testl	%eax, %eax
	jle	.LBB0_15
# %bb.1:                                # %while_body_1.lr.ph
	xorl	%ecx, %ecx
	movq	%rbx, -64(%rbp)                 # 8-byte Spill
	jmp	.LBB0_2
	.p2align	4, 0x90
.LBB0_14:                               # %while_end_5
                                        #   in Loop: Header=BB0_2 Depth=1
	movl	-48(%rbp), %ecx                 # 4-byte Reload
	incl	%ecx
	cmpl	-44(%rbp), %ecx                 # 4-byte Folded Reload
	jge	.LBB0_15
.LBB0_2:                                # %while_body_1
                                        # =>This Loop Header: Depth=1
                                        #     Child Loop BB0_4 Depth 2
                                        #       Child Loop BB0_7 Depth 3
                                        #       Child Loop BB0_10 Depth 3
                                        #       Child Loop BB0_12 Depth 3
	movq	%rsp, %rax
	leaq	-16(%rax), %r12
	movq	%r12, %rsp
	movl	$0, -16(%rax)
	movl	%ecx, -48(%rbp)                 # 4-byte Spill
	notl	%ecx
	addl	-72(%rbp), %ecx                 # 4-byte Folded Reload
	movl	%ecx, -52(%rbp)                 # 4-byte Spill
	testl	%ecx, %ecx
	jle	.LBB0_14
# %bb.3:                                # %while_body_4.lr.ph
                                        #   in Loop: Header=BB0_2 Depth=1
	xorl	%eax, %eax
	movq	%r12, -80(%rbp)                 # 8-byte Spill
	jmp	.LBB0_4
	.p2align	4, 0x90
.LBB0_13:                               # %endif_0
                                        #   in Loop: Header=BB0_4 Depth=2
	movl	(%r12), %eax
	incl	%eax
	movl	%eax, (%r12)
	cmpl	-52(%rbp), %eax                 # 4-byte Folded Reload
	jge	.LBB0_14
.LBB0_4:                                # %while_body_4
                                        #   Parent Loop BB0_2 Depth=1
                                        # =>  This Loop Header: Depth=2
                                        #       Child Loop BB0_7 Depth 3
                                        #       Child Loop BB0_10 Depth 3
                                        #       Child Loop BB0_12 Depth 3
	movq	%rsp, %r14
	leaq	-16(%r14), %rcx
	movq	%rcx, -96(%rbp)                 # 8-byte Spill
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
	movq	%rax, -88(%rbp)                 # 8-byte Spill
	movq	%rax, %rsp
	movl	(%r12), %esi
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
	movl	(%r12), %esi
	movq	%rbx, %rdi
	callq	arraylist_remove_int@PLT
	movl	(%r12), %esi
	movq	%rbx, %rdi
	callq	arraylist_remove_int@PLT
	movq	%rsp, %r15
	leaq	-16(%r15), %r14
	movq	%r14, %rsp
	movl	$4, %edi
	callq	arraylist_create_int@PLT
	movq	%rax, -16(%r15)
	movq	%rsp, %rax
	leaq	-16(%rax), %r13
	movq	%r13, %rsp
	movl	$0, -16(%rax)
	cmpl	$0, (%r12)
	movq	-16(%r15), %r15
	jle	.LBB0_8
# %bb.6:                                # %while_body_7.lr.ph
                                        #   in Loop: Header=BB0_4 Depth=2
	xorl	%eax, %eax
	movq	%r12, %rbx
	.p2align	4, 0x90
.LBB0_7:                                # %while_body_7
                                        #   Parent Loop BB0_2 Depth=1
                                        #     Parent Loop BB0_4 Depth=2
                                        # =>    This Inner Loop Header: Depth=3
	movl	%eax, %esi
	movq	%rsp, %r12
	leaq	-16(%r12), %rdx
	movq	%rdx, %rsp
	movq	-64(%rbp), %rdi                 # 8-byte Reload
	callq	arraylist_get_int@PLT
	movl	-16(%r12), %esi
	movq	%r15, %rdi
	callq	arraylist_add_int@PLT
	movl	(%r13), %eax
	incl	%eax
	movl	%eax, (%r13)
	cmpl	(%rbx), %eax
	movq	(%r14), %r15
	jl	.LBB0_7
.LBB0_8:                                # %while_end_8
                                        #   in Loop: Header=BB0_4 Depth=2
	movq	-88(%rbp), %rax                 # 8-byte Reload
	movl	(%rax), %esi
	movq	%r15, %rdi
	callq	arraylist_add_int@PLT
	movq	(%r14), %rdi
	movq	-96(%rbp), %rax                 # 8-byte Reload
	movl	(%rax), %esi
	callq	arraylist_add_int@PLT
	movl	(%r13), %r15d
	movq	-64(%rbp), %rbx                 # 8-byte Reload
	.p2align	4, 0x90
.LBB0_10:                               # %while_body_10
                                        #   Parent Loop BB0_2 Depth=1
                                        #     Parent Loop BB0_4 Depth=2
                                        # =>    This Inner Loop Header: Depth=3
	movq	%rbx, %rdi
	callq	arraylist_size_int@PLT
	cmpl	%eax, %r15d
	jge	.LBB0_11
# %bb.9:                                # %while_body_10
                                        #   in Loop: Header=BB0_10 Depth=3
	movq	(%r14), %r15
	movl	(%r13), %esi
	movq	%rsp, %r12
	leaq	-16(%r12), %rdx
	movq	%rdx, %rsp
	movq	%rbx, %rdi
	callq	arraylist_get_int@PLT
	movl	-16(%r12), %esi
	movq	%r15, %rdi
	callq	arraylist_add_int@PLT
	movl	(%r13), %r15d
	incl	%r15d
	movl	%r15d, (%r13)
	jmp	.LBB0_10
	.p2align	4, 0x90
.LBB0_11:                               # %while_end_11
                                        #   in Loop: Header=BB0_4 Depth=2
	movq	%rbx, %rdi
	callq	arraylist_clear_int@PLT
	movl	$0, (%r13)
	movq	(%r14), %rdi
	callq	arraylist_size_int@PLT
	testl	%eax, %eax
	movq	-80(%rbp), %r12                 # 8-byte Reload
	jle	.LBB0_13
	.p2align	4, 0x90
.LBB0_12:                               # %while_body_13
                                        #   Parent Loop BB0_2 Depth=1
                                        #     Parent Loop BB0_4 Depth=2
                                        # =>    This Inner Loop Header: Depth=3
	movq	(%r14), %rdi
	movl	(%r13), %esi
	movq	%rsp, %r15
	leaq	-16(%r15), %rdx
	movq	%rdx, %rsp
	callq	arraylist_get_int@PLT
	movl	-16(%r15), %esi
	movq	%rbx, %rdi
	callq	arraylist_add_int@PLT
	movl	(%r13), %r15d
	incl	%r15d
	movl	%r15d, (%r13)
	movq	(%r14), %rdi
	callq	arraylist_size_int@PLT
	cmpl	%eax, %r15d
	jl	.LBB0_12
	jmp	.LBB0_13
.LBB0_15:                               # %while_end_2
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
	pushq	%rbp
	.cfi_def_cfa_offset 16
	.cfi_offset %rbp, -16
	movq	%rsp, %rbp
	.cfi_def_cfa_register %rbp
	pushq	%r15
	pushq	%r14
	pushq	%rbx
	subq	$24, %rsp
	.cfi_offset %rbx, -40
	.cfi_offset %r14, -32
	.cfi_offset %r15, -24
	movl	$5, %edi
	callq	arraylist_create_int@PLT
	movq	%rax, %rbx
	movabsq	$4294967301, %rax               # imm = 0x100000005
	movq	%rax, -44(%rbp)
	movabsq	$8589934596, %rax               # imm = 0x200000004
	movq	%rax, -36(%rbp)
	movl	$8, -28(%rbp)
	leaq	-44(%rbp), %rsi
	movl	$5, %edx
	movq	%rbx, %rdi
	callq	arraylist_addAll_int@PLT
	movq	%rbx, %rdi
	callq	bubbleSort@PLT
	movl	$.L.strStr, %edi
	movl	$.L.str0, %esi
	xorl	%eax, %eax
	callq	printf@PLT
	movq	%rbx, %rdi
	callq	arraylist_size_int@PLT
	testl	%eax, %eax
	jle	.LBB1_3
# %bb.1:                                # %while_body_16.lr.ph
	xorl	%r14d, %r14d
	.p2align	4, 0x90
.LBB1_2:                                # %while_body_16
                                        # =>This Inner Loop Header: Depth=1
	movq	%rsp, %r15
	leaq	-16(%r15), %rdx
	movq	%rdx, %rsp
	movq	%rbx, %rdi
	movq	%r14, %rsi
	callq	arraylist_get_int@PLT
	movl	-16(%r15), %esi
	movl	$.L.strInt, %edi
	xorl	%eax, %eax
	callq	printf@PLT
	movq	%rbx, %rdi
	callq	arraylist_size_int@PLT
	incq	%r14
	cmpl	%eax, %r14d
	jl	.LBB1_2
.LBB1_3:                                # %while_end_17
	movq	%rbx, %rdi
	callq	arraylist_free_int@PLT
	callq	getchar@PLT
	xorl	%eax, %eax
	leaq	-24(%rbp), %rsp
	popq	%rbx
	popq	%r14
	popq	%r15
	popq	%rbp
	.cfi_def_cfa %rsp, 8
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

	.type	.L.str0,@object                 # @.str0
.L.str0:
	.asciz	"Lista ordenada:"
	.size	.L.str0, 16

	.section	".note.GNU-stack","",@progbits
