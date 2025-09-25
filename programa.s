	.text
	.def	 @feat.00;
	.scl	3;
	.type	0;
	.endef
	.globl	@feat.00
.set @feat.00, 0
	.file	"programa.ll"
	.def	 main;
	.scl	2;
	.type	32;
	.endef
	.globl	__real@40091eb851eb851f         # -- Begin function main
	.section	.rdata,"dr",discard,__real@40091eb851eb851f
	.p2align	3
__real@40091eb851eb851f:
	.quad	0x40091eb851eb851f              # double 3.1400000000000001
	.text
	.globl	main
	.p2align	4, 0x90
main:                                   # @main
.seh_proc main
# %bb.0:
	subq	$120, %rsp
	.seh_stackalloc 120
	.seh_endprologue
	movsd	__real@40091eb851eb851f(%rip), %xmm0 # xmm0 = mem[0],zero
	movl	$4, 116(%rsp)
	movsd	%xmm0, 104(%rsp)
	leaq	.L.str0(%rip), %rax
	movq	%rax, 96(%rsp)
	movb	$1, 95(%rsp)
	movl	116(%rsp), %edx
	leaq	.L.strInt(%rip), %rcx
	callq	printf
	movsd	104(%rsp), %xmm0                # xmm0 = mem[0],zero
	leaq	.L.strDouble(%rip), %rcx
	movaps	%xmm0, %xmm1
	movq	%xmm0, %rdx
	movl	%eax, 76(%rsp)                  # 4-byte Spill
	callq	printf
	movq	96(%rsp), %rdx
	leaq	.L.strStr(%rip), %rcx
	movl	%eax, 72(%rsp)                  # 4-byte Spill
	callq	printf
	movb	95(%rsp), %cl
	andb	$1, %cl
	movzbl	%cl, %edx
	leaq	.L.strInt(%rip), %rcx
	movl	%eax, 68(%rsp)                  # 4-byte Spill
	callq	printf
	movl	$4, %ecx
	movl	%eax, 64(%rsp)                  # 4-byte Spill
	callq	arraylist_create
	movq	%rax, 80(%rsp)
	movq	80(%rsp), %rcx
	movl	116(%rsp), %eax
	movq	%rcx, 56(%rsp)                  # 8-byte Spill
	movl	%eax, %ecx
	callq	createInt
	movq	56(%rsp), %rcx                  # 8-byte Reload
	movq	%rax, %rdx
	callq	setItems
	movq	80(%rsp), %rcx
	movl	$13, %eax
	addl	$0, %eax
	movq	%rcx, 48(%rsp)                  # 8-byte Spill
	movl	%eax, %ecx
	callq	createInt
	movq	48(%rsp), %rcx                  # 8-byte Reload
	movq	%rax, %rdx
	callq	setItems
	movq	80(%rsp), %rax
	movq	%rax, %rcx
	callq	size
	leaq	.L.strInt(%rip), %rcx
	movl	%eax, %edx
	callq	printf
	movq	80(%rsp), %rcx
	movl	$1, %edx
	addl	$0, %edx
	movl	%eax, 44(%rsp)                  # 4-byte Spill
	callq	getItem
	movq	%rax, %rcx
	callq	printDynValue
.LBB0_1:                                # %while_cond_0
                                        # =>This Loop Header: Depth=1
                                        #     Child Loop BB0_3 Depth 2
	testb	$1, 95(%rsp)
	jne	.LBB0_2
	jmp	.LBB0_8
.LBB0_2:                                # %while_body_1
                                        #   in Loop: Header=BB0_1 Depth=1
	jmp	.LBB0_3
.LBB0_3:                                # %while_cond_3
                                        #   Parent Loop BB0_1 Depth=1
                                        # =>  This Inner Loop Header: Depth=2
	movl	116(%rsp), %eax
	movl	$10, %ecx
	addl	$0, %ecx
	cmpl	%ecx, %eax
	jge	.LBB0_7
# %bb.4:                                # %while_body_4
                                        #   in Loop: Header=BB0_3 Depth=2
	movl	116(%rsp), %eax
	movl	$8, %ecx
	addl	$0, %ecx
	cmpl	%ecx, %eax
	jne	.LBB0_6
# %bb.5:                                # %then_0
                                        #   in Loop: Header=BB0_1 Depth=1
	leaq	.L.str1(%rip), %rcx
	callq	printf
	jmp	.LBB0_7
.LBB0_6:                                # %endif_0
                                        #   in Loop: Header=BB0_3 Depth=2
	movl	116(%rsp), %edx
	leaq	.L.strInt(%rip), %rcx
	callq	printf
	movl	116(%rsp), %ecx
	movl	$1, %edx
	addl	$0, %edx
	addl	%edx, %ecx
	movl	%ecx, 116(%rsp)
	jmp	.LBB0_3
.LBB0_7:                                # %while_end_5
                                        #   in Loop: Header=BB0_1 Depth=1
	movl	116(%rsp), %edx
	leaq	.L.strInt(%rip), %rcx
	callq	printf
	leaq	.L.str2(%rip), %rcx
	movl	%eax, 40(%rsp)                  # 4-byte Spill
	callq	printf
	movb	$0, 95(%rsp)
	jmp	.LBB0_1
.LBB0_8:                                # %while_end_2
	movq	80(%rsp), %rcx
	callq	printList
	callq	getchar
	xorl	%ecx, %ecx
	movl	%eax, 36(%rsp)                  # 4-byte Spill
	movl	%ecx, %eax
	addq	$120, %rsp
	retq
	.seh_handlerdata
	.text
	.seh_endproc
                                        # -- End function
	.section	.rdata,"dr"
.L.strInt:                              # @.strInt
	.asciz	"%d\n"

.L.strDouble:                           # @.strDouble
	.asciz	"%f\n"

.L.strStr:                              # @.strStr
	.asciz	"%s\n"

.L.str0:                                # @.str0
	.asciz	"low level\n"

	.p2align	4                               # @.str1
.L.str1:
	.asciz	"saindo do loop interno\n"

	.p2align	4                               # @.str2
.L.str2:
	.asciz	"voltando para o loop externo\n"

	.addrsig
	.addrsig_sym printf
	.addrsig_sym getchar
	.addrsig_sym createInt
	.addrsig_sym arraylist_create
	.addrsig_sym setItems
	.addrsig_sym printList
	.addrsig_sym size
	.addrsig_sym getItem
	.addrsig_sym printDynValue
	.addrsig_sym .L.strInt
	.addrsig_sym .L.strDouble
	.addrsig_sym .L.strStr
	.addrsig_sym .L.str0
	.addrsig_sym .L.str1
	.addrsig_sym .L.str2
	.globl	_fltused
