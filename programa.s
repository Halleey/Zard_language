	.text
	.file	"programa.ll"
	.globl	sum                             # -- Begin function sum
	.p2align	4, 0x90
	.type	sum,@function
sum:                                    # @sum
	.cfi_startproc
# %bb.0:                                # %entry
	addsd	%xmm1, %xmm0
	retq
.Lfunc_end0:
	.size	sum, .Lfunc_end0-sum
	.cfi_endproc
                                        # -- End function
	.globl	sub                             # -- Begin function sub
	.p2align	4, 0x90
	.type	sub,@function
sub:                                    # @sub
	.cfi_startproc
# %bb.0:                                # %entry
	subsd	%xmm1, %xmm0
	retq
.Lfunc_end1:
	.size	sub, .Lfunc_end1-sub
	.cfi_endproc
                                        # -- End function
	.globl	mul                             # -- Begin function mul
	.p2align	4, 0x90
	.type	mul,@function
mul:                                    # @mul
	.cfi_startproc
# %bb.0:                                # %entry
	mulsd	%xmm1, %xmm0
	retq
.Lfunc_end2:
	.size	mul, .Lfunc_end2-mul
	.cfi_endproc
                                        # -- End function
	.globl	div                             # -- Begin function div
	.p2align	4, 0x90
	.type	div,@function
div:                                    # @div
	.cfi_startproc
# %bb.0:                                # %entry
	xorpd	%xmm2, %xmm2
	ucomisd	%xmm2, %xmm1
	jne	.LBB3_2
	jp	.LBB3_2
# %bb.1:                                # %then_0
	pushq	%rax
	.cfi_def_cfa_offset 16
	movl	$.L.strStr, %edi
	movl	$.L.str0, %esi
	xorl	%eax, %eax
	callq	printf@PLT
	xorps	%xmm0, %xmm0
	popq	%rax
	.cfi_def_cfa_offset 8
	retq
.LBB3_2:                                # %endif_0
	divsd	%xmm1, %xmm0
	retq
.Lfunc_end3:
	.size	div, .Lfunc_end3-div
	.cfi_endproc
                                        # -- End function
	.globl	pow                             # -- Begin function pow
	.p2align	4, 0x90
	.type	pow,@function
pow:                                    # @pow
	.cfi_startproc
# %bb.0:                                # %entry
	pushq	%rbp
	.cfi_def_cfa_offset 16
	.cfi_offset %rbp, -16
	movq	%rsp, %rbp
	.cfi_def_cfa_register %rbp
	testl	%edi, %edi
	js	.LBB4_1
# %bb.2:                                # %endif_1
	movq	%rsp, %rdx
	leaq	-16(%rdx), %rax
	movq	%rax, %rsp
	movabsq	$4607182418800017408, %rcx      # imm = 0x3FF0000000000000
	movq	%rcx, -16(%rdx)
	movq	%rsp, %rsi
	leaq	-16(%rsi), %rcx
	movq	%rcx, %rsp
	movl	$0, -16(%rsi)
	testl	%edi, %edi
	movsd	-16(%rdx), %xmm1                # xmm1 = mem[0],zero
	jle	.LBB4_4
	.p2align	4, 0x90
.LBB4_3:                                # %while_body_1
                                        # =>This Inner Loop Header: Depth=1
	mulsd	%xmm0, %xmm1
	movsd	%xmm1, (%rax)
	movl	(%rcx), %edx
	incl	%edx
	movl	%edx, (%rcx)
	cmpl	%edi, %edx
	movsd	(%rax), %xmm1                   # xmm1 = mem[0],zero
	jl	.LBB4_3
.LBB4_4:                                # %while_end_2
	movapd	%xmm1, %xmm0
	jmp	.LBB4_5
.LBB4_1:                                # %then_1
	movl	$.L.strStr, %edi
	movl	$.L.str1, %esi
	xorl	%eax, %eax
	callq	printf@PLT
	xorpd	%xmm0, %xmm0
.LBB4_5:                                # %while_end_2
	movq	%rbp, %rsp
	popq	%rbp
	.cfi_def_cfa %rsp, 8
	retq
.Lfunc_end4:
	.size	pow, .Lfunc_end4-pow
	.cfi_endproc
                                        # -- End function
	.section	.rodata.cst8,"aM",@progbits,8
	.p2align	3, 0x0                          # -- Begin function sqrt
.LCPI5_0:
	.quad	0x4000000000000000              # double 2
	.text
	.globl	sqrt
	.p2align	4, 0x90
	.type	sqrt,@function
sqrt:                                   # @sqrt
	.cfi_startproc
# %bb.0:                                # %entry
	pushq	%rbp
	.cfi_def_cfa_offset 16
	.cfi_offset %rbp, -16
	movq	%rsp, %rbp
	.cfi_def_cfa_register %rbp
	xorpd	%xmm1, %xmm1
	ucomisd	%xmm0, %xmm1
	jbe	.LBB5_2
# %bb.1:                                # %then_2
	movl	$.L.strStr, %edi
	movl	$.L.str2, %esi
	xorl	%eax, %eax
	callq	printf@PLT
	xorpd	%xmm0, %xmm0
	jmp	.LBB5_6
.LBB5_2:                                # %endif_2
	movq	%rsp, %rdx
	leaq	-16(%rdx), %rax
	movq	%rax, %rsp
	movapd	%xmm0, %xmm1
	divsd	.LCPI5_0(%rip), %xmm1
	movsd	%xmm1, -16(%rdx)
	movq	%rsp, %rsi
	leaq	-16(%rsi), %rcx
	movq	%rcx, %rsp
	movl	$0, -16(%rsi)
	movsd	-16(%rdx), %xmm1                # xmm1 = mem[0],zero
	xorl	%edx, %edx
	testb	%dl, %dl
	jne	.LBB5_5
# %bb.3:                                # %while_body_4.lr.ph
	movsd	.LCPI5_0(%rip), %xmm2           # xmm2 = [2.0E+0,0.0E+0]
	.p2align	4, 0x90
.LBB5_4:                                # %while_body_4
                                        # =>This Inner Loop Header: Depth=1
	movapd	%xmm0, %xmm3
	divsd	%xmm1, %xmm3
	addsd	%xmm1, %xmm3
	divsd	%xmm2, %xmm3
	movsd	%xmm3, (%rax)
	movl	(%rcx), %edx
	incl	%edx
	movl	%edx, (%rcx)
	cmpl	$20, %edx
	movsd	(%rax), %xmm1                   # xmm1 = mem[0],zero
	jl	.LBB5_4
.LBB5_5:                                # %while_end_5
	movapd	%xmm1, %xmm0
.LBB5_6:                                # %while_end_5
	movq	%rbp, %rsp
	popq	%rbp
	.cfi_def_cfa %rsp, 8
	retq
.Lfunc_end5:
	.size	sqrt, .Lfunc_end5-sqrt
	.cfi_endproc
                                        # -- End function
	.section	.rodata.cst8,"aM",@progbits,8
	.p2align	3, 0x0                          # -- Begin function sin
.LCPI6_0:
	.quad	0x4018000000000000              # double 6
.LCPI6_1:
	.quad	0x4034000000000000              # double 20
.LCPI6_2:
	.quad	0x4045000000000000              # double 42
.LCPI6_3:
	.quad	0x4052000000000000              # double 72
.LCPI6_4:
	.quad	0x405b800000000000              # double 110
.LCPI6_5:
	.quad	0x4063800000000000              # double 156
.LCPI6_6:
	.quad	0x406a400000000000              # double 210
.LCPI6_7:
	.quad	0x4071000000000000              # double 272
.LCPI6_8:
	.quad	0x4075600000000000              # double 342
	.text
	.globl	sin
	.p2align	4, 0x90
	.type	sin,@function
sin:                                    # @sin
	.cfi_startproc
# %bb.0:                                # %entry
	movapd	%xmm0, %xmm1
	xorpd	%xmm0, %xmm0
	xorpd	%xmm3, %xmm3
	subsd	%xmm1, %xmm3
	mulsd	%xmm1, %xmm3
	mulsd	%xmm1, %xmm3
	divsd	.LCPI6_0(%rip), %xmm3
	movapd	%xmm1, %xmm2
	addsd	%xmm3, %xmm2
	xorpd	%xmm4, %xmm4
	subsd	%xmm3, %xmm4
	mulsd	%xmm1, %xmm4
	mulsd	%xmm1, %xmm4
	divsd	.LCPI6_1(%rip), %xmm4
	addsd	%xmm4, %xmm2
	xorpd	%xmm3, %xmm3
	subsd	%xmm4, %xmm3
	mulsd	%xmm1, %xmm3
	mulsd	%xmm1, %xmm3
	divsd	.LCPI6_2(%rip), %xmm3
	addsd	%xmm3, %xmm2
	xorpd	%xmm4, %xmm4
	subsd	%xmm3, %xmm4
	mulsd	%xmm1, %xmm4
	mulsd	%xmm1, %xmm4
	divsd	.LCPI6_3(%rip), %xmm4
	addsd	%xmm4, %xmm2
	xorpd	%xmm3, %xmm3
	subsd	%xmm4, %xmm3
	mulsd	%xmm1, %xmm3
	mulsd	%xmm1, %xmm3
	divsd	.LCPI6_4(%rip), %xmm3
	addsd	%xmm3, %xmm2
	xorpd	%xmm4, %xmm4
	subsd	%xmm3, %xmm4
	mulsd	%xmm1, %xmm4
	mulsd	%xmm1, %xmm4
	divsd	.LCPI6_5(%rip), %xmm4
	addsd	%xmm4, %xmm2
	xorpd	%xmm3, %xmm3
	subsd	%xmm4, %xmm3
	mulsd	%xmm1, %xmm3
	mulsd	%xmm1, %xmm3
	divsd	.LCPI6_6(%rip), %xmm3
	addsd	%xmm3, %xmm2
	xorpd	%xmm4, %xmm4
	subsd	%xmm3, %xmm4
	mulsd	%xmm1, %xmm4
	mulsd	%xmm1, %xmm4
	divsd	.LCPI6_7(%rip), %xmm4
	addsd	%xmm4, %xmm2
	subsd	%xmm4, %xmm0
	mulsd	%xmm1, %xmm0
	mulsd	%xmm1, %xmm0
	divsd	.LCPI6_8(%rip), %xmm0
	addsd	%xmm2, %xmm0
	retq
.Lfunc_end6:
	.size	sin, .Lfunc_end6-sin
	.cfi_endproc
                                        # -- End function
	.section	.rodata.cst16,"aM",@progbits,16
	.p2align	4, 0x0                          # -- Begin function cos
.LCPI7_0:
	.quad	0x8000000000000000              # double -0
	.quad	0x8000000000000000              # double -0
	.section	.rodata.cst8,"aM",@progbits,8
	.p2align	3, 0x0
.LCPI7_1:
	.quad	0x4000000000000000              # double 2
.LCPI7_2:
	.quad	0x3ff0000000000000              # double 1
.LCPI7_3:
	.quad	0x4028000000000000              # double 12
.LCPI7_4:
	.quad	0x403e000000000000              # double 30
.LCPI7_5:
	.quad	0x404c000000000000              # double 56
.LCPI7_6:
	.quad	0x4056800000000000              # double 90
.LCPI7_7:
	.quad	0x4060800000000000              # double 132
.LCPI7_8:
	.quad	0x4066c00000000000              # double 182
.LCPI7_9:
	.quad	0x406e000000000000              # double 240
.LCPI7_10:
	.quad	0x4073200000000000              # double 306
	.text
	.globl	cos
	.p2align	4, 0x90
	.type	cos,@function
cos:                                    # @cos
	.cfi_startproc
# %bb.0:                                # %entry
	movapd	.LCPI7_0(%rip), %xmm3           # xmm3 = [-0.0E+0,-0.0E+0]
	xorpd	%xmm0, %xmm3
	mulsd	%xmm0, %xmm3
	divsd	.LCPI7_1(%rip), %xmm3
	movsd	.LCPI7_2(%rip), %xmm2           # xmm2 = [1.0E+0,0.0E+0]
	addsd	%xmm3, %xmm2
	xorpd	%xmm1, %xmm1
	xorpd	%xmm4, %xmm4
	subsd	%xmm3, %xmm4
	mulsd	%xmm0, %xmm4
	mulsd	%xmm0, %xmm4
	divsd	.LCPI7_3(%rip), %xmm4
	addsd	%xmm4, %xmm2
	xorpd	%xmm3, %xmm3
	subsd	%xmm4, %xmm3
	mulsd	%xmm0, %xmm3
	mulsd	%xmm0, %xmm3
	divsd	.LCPI7_4(%rip), %xmm3
	addsd	%xmm3, %xmm2
	xorpd	%xmm4, %xmm4
	subsd	%xmm3, %xmm4
	mulsd	%xmm0, %xmm4
	mulsd	%xmm0, %xmm4
	divsd	.LCPI7_5(%rip), %xmm4
	addsd	%xmm4, %xmm2
	xorpd	%xmm3, %xmm3
	subsd	%xmm4, %xmm3
	mulsd	%xmm0, %xmm3
	mulsd	%xmm0, %xmm3
	divsd	.LCPI7_6(%rip), %xmm3
	addsd	%xmm3, %xmm2
	xorpd	%xmm4, %xmm4
	subsd	%xmm3, %xmm4
	mulsd	%xmm0, %xmm4
	mulsd	%xmm0, %xmm4
	divsd	.LCPI7_7(%rip), %xmm4
	addsd	%xmm4, %xmm2
	xorpd	%xmm3, %xmm3
	subsd	%xmm4, %xmm3
	mulsd	%xmm0, %xmm3
	mulsd	%xmm0, %xmm3
	divsd	.LCPI7_8(%rip), %xmm3
	addsd	%xmm3, %xmm2
	xorpd	%xmm4, %xmm4
	subsd	%xmm3, %xmm4
	mulsd	%xmm0, %xmm4
	mulsd	%xmm0, %xmm4
	divsd	.LCPI7_9(%rip), %xmm4
	addsd	%xmm4, %xmm2
	subsd	%xmm4, %xmm1
	mulsd	%xmm0, %xmm1
	mulsd	%xmm0, %xmm1
	divsd	.LCPI7_10(%rip), %xmm1
	addsd	%xmm2, %xmm1
	movapd	%xmm1, %xmm0
	retq
.Lfunc_end7:
	.size	cos, .Lfunc_end7-cos
	.cfi_endproc
                                        # -- End function
	.globl	fact                            # -- Begin function fact
	.p2align	4, 0x90
	.type	fact,@function
fact:                                   # @fact
	.cfi_startproc
# %bb.0:                                # %entry
	pushq	%rbp
	.cfi_def_cfa_offset 16
	.cfi_offset %rbp, -16
	movq	%rsp, %rbp
	.cfi_def_cfa_register %rbp
	testl	%edi, %edi
	js	.LBB8_1
# %bb.2:                                # %endif_3
	movq	%rsp, %rax
	leaq	-16(%rax), %rcx
	movq	%rcx, %rsp
	movl	$1, -16(%rax)
	movq	%rsp, %rsi
	leaq	-16(%rsi), %rdx
	movq	%rdx, %rsp
	movl	$1, -16(%rsi)
	testl	%edi, %edi
	movl	-16(%rax), %eax
	jle	.LBB8_5
# %bb.3:                                # %while_body_13.lr.ph
	movl	$1, %esi
	.p2align	4, 0x90
.LBB8_4:                                # %while_body_13
                                        # =>This Inner Loop Header: Depth=1
	imull	%esi, %eax
	movl	%eax, (%rcx)
	movl	(%rdx), %esi
	incl	%esi
	movl	%esi, (%rdx)
	cmpl	%edi, %esi
	movl	(%rcx), %eax
	jle	.LBB8_4
	jmp	.LBB8_5
.LBB8_1:                                # %then_3
	movl	$.L.strStr, %edi
	movl	$.L.str3, %esi
	xorl	%eax, %eax
	callq	printf@PLT
	xorl	%eax, %eax
.LBB8_5:                                # %while_end_14
	movq	%rbp, %rsp
	popq	%rbp
	.cfi_def_cfa %rsp, 8
	retq
.Lfunc_end8:
	.size	fact, .Lfunc_end8-fact
	.cfi_endproc
                                        # -- End function
	.globl	factorial                       # -- Begin function factorial
	.p2align	4, 0x90
	.type	factorial,@function
factorial:                              # @factorial
	.cfi_startproc
# %bb.0:                                # %entry
	pushq	%rbx
	.cfi_def_cfa_offset 16
	.cfi_offset %rbx, -16
	testl	%edi, %edi
	js	.LBB9_3
# %bb.1:                                # %endif_4
	je	.LBB9_4
# %bb.2:                                # %endif_5
	movl	%edi, %ebx
	leal	-1(%rbx), %edi
	callq	factorial@PLT
	imull	%ebx, %eax
	popq	%rbx
	.cfi_def_cfa_offset 8
	retq
.LBB9_3:                                # %then_4
	.cfi_def_cfa_offset 16
	movl	$.L.strStr, %edi
	movl	$.L.str3, %esi
	xorl	%eax, %eax
	callq	printf@PLT
	xorl	%eax, %eax
	popq	%rbx
	.cfi_def_cfa_offset 8
	retq
.LBB9_4:                                # %then_5
	.cfi_def_cfa_offset 16
	movl	$1, %eax
	popq	%rbx
	.cfi_def_cfa_offset 8
	retq
.Lfunc_end9:
	.size	factorial, .Lfunc_end9-factorial
	.cfi_endproc
                                        # -- End function
	.globl	somar                           # -- Begin function somar
	.p2align	4, 0x90
	.type	somar,@function
somar:                                  # @somar
	.cfi_startproc
# %bb.0:                                # %entry
	addsd	%xmm1, %xmm0
	retq
.Lfunc_end10:
	.size	somar, .Lfunc_end10-somar
	.cfi_endproc
                                        # -- End function
	.section	.rodata.cst8,"aM",@progbits,8
	.p2align	3, 0x0                          # -- Begin function main
.LCPI11_0:
	.quad	0x4024000000000000              # double 10
.LCPI11_1:
	.quad	0x4008000000000000              # double 3
.LCPI11_2:
	.quad	0x40094c583ada5b53              # double 3.1622776601683795
.LCPI11_3:
	.quad	0xbfe1689ef5f34f52              # double -0.54402111088936977
.LCPI11_4:
	.quad	0xbfead9ac890c6b1f              # double -0.83907152907645244
	.text
	.globl	main
	.p2align	4, 0x90
	.type	main,@function
main:                                   # @main
	.cfi_startproc
# %bb.0:                                # %.split
	pushq	%rax
	.cfi_def_cfa_offset 16
	movl	$3, %edi
	movl	$4, %esi
	callq	somar@PLT
	movl	$.L.strDouble, %edi
	movb	$1, %al
	callq	printf@PLT
	movsd	.LCPI11_0(%rip), %xmm0          # xmm0 = [1.0E+1,0.0E+0]
	movsd	.LCPI11_1(%rip), %xmm1          # xmm1 = [3.0E+0,0.0E+0]
	callq	sum@PLT
	movl	$.L.strDouble, %edi
	movb	$1, %al
	callq	printf@PLT
	movsd	.LCPI11_0(%rip), %xmm0          # xmm0 = [1.0E+1,0.0E+0]
	movsd	.LCPI11_1(%rip), %xmm1          # xmm1 = [3.0E+0,0.0E+0]
	callq	sub@PLT
	movl	$.L.strDouble, %edi
	movb	$1, %al
	callq	printf@PLT
	movsd	.LCPI11_0(%rip), %xmm0          # xmm0 = [1.0E+1,0.0E+0]
	movsd	.LCPI11_1(%rip), %xmm1          # xmm1 = [3.0E+0,0.0E+0]
	callq	mul@PLT
	movl	$.L.strDouble, %edi
	movb	$1, %al
	callq	printf@PLT
	movsd	.LCPI11_0(%rip), %xmm0          # xmm0 = [1.0E+1,0.0E+0]
	movsd	.LCPI11_1(%rip), %xmm1          # xmm1 = [3.0E+0,0.0E+0]
	callq	div@PLT
	movl	$.L.strDouble, %edi
	movb	$1, %al
	callq	printf@PLT
	movsd	.LCPI11_0(%rip), %xmm0          # xmm0 = [1.0E+1,0.0E+0]
	movl	$3, %edi
	callq	pow@PLT
	movl	$.L.strDouble, %edi
	movb	$1, %al
	callq	printf@PLT
	movl	$5, %edi
	callq	factorial@PLT
	movl	$.L.strInt, %edi
	movl	%eax, %esi
	xorl	%eax, %eax
	callq	printf@PLT
	movsd	.LCPI11_2(%rip), %xmm0          # xmm0 = [3.1622776601683795E+0,0.0E+0]
	movl	$.L.strDouble, %edi
	movb	$1, %al
	callq	printf@PLT
	movsd	.LCPI11_0(%rip), %xmm0          # xmm0 = [1.0E+1,0.0E+0]
	callq	sin@PLT
	movsd	.LCPI11_3(%rip), %xmm0          # xmm0 = [-5.4402111088936977E-1,0.0E+0]
	movl	$.L.strDouble, %edi
	movb	$1, %al
	callq	printf@PLT
	movsd	.LCPI11_0(%rip), %xmm0          # xmm0 = [1.0E+1,0.0E+0]
	callq	cos@PLT
	movsd	.LCPI11_4(%rip), %xmm0          # xmm0 = [-8.3907152907645244E-1,0.0E+0]
	movl	$.L.strDouble, %edi
	movb	$1, %al
	callq	printf@PLT
	movl	$.L.strStr, %edi
	movl	$.L.str4, %esi
	xorl	%eax, %eax
	callq	printf@PLT
	movsd	.LCPI11_0(%rip), %xmm0          # xmm0 = [1.0E+1,0.0E+0]
	xorl	%edi, %edi
	callq	div@PLT
	movl	$.L.strDouble, %edi
	movb	$1, %al
	callq	printf@PLT
	movl	$.L.strStr, %edi
	movl	$.L.str5, %esi
	xorl	%eax, %eax
	callq	printf@PLT
	movl	$-3, %edi
	callq	factorial@PLT
	movl	$.L.strInt, %edi
	movl	%eax, %esi
	xorl	%eax, %eax
	callq	printf@PLT
	movl	$.L.strStr, %edi
	movl	$.L.str6, %esi
	xorl	%eax, %eax
	callq	printf@PLT
	movl	$-9, %edi
	callq	sqrt@PLT
	movl	$.L.strDouble, %edi
	movb	$1, %al
	callq	printf@PLT
	movl	$.L.strStr, %edi
	movl	$.L.str7, %esi
	xorl	%eax, %eax
	callq	printf@PLT
	callq	getchar@PLT
	xorl	%eax, %eax
	popq	%rcx
	.cfi_def_cfa_offset 8
	retq
.Lfunc_end11:
	.size	main, .Lfunc_end11-main
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
	.asciz	"Erro: divisao por zero!"
	.size	.L.str0, 24

	.type	.L.str1,@object                 # @.str1
	.p2align	4, 0x0
.L.str1:
	.asciz	"Aviso: expoente negativo nao suportado, retornando 0"
	.size	.L.str1, 53

	.type	.L.str2,@object                 # @.str2
	.p2align	4, 0x0
.L.str2:
	.asciz	"Erro: sqrt de numero negativo!"
	.size	.L.str2, 31

	.type	.L.str3,@object                 # @.str3
	.p2align	4, 0x0
.L.str3:
	.asciz	"Erro: factorial de numero negativo!"
	.size	.L.str3, 36

	.type	.L.str4,@object                 # @.str4
	.p2align	4, 0x0
.L.str4:
	.asciz	"Tentando dividir por zero:"
	.size	.L.str4, 27

	.type	.L.str5,@object                 # @.str5
	.p2align	4, 0x0
.L.str5:
	.asciz	"Tentando fatorial de n\303\272mero negativo:"
	.size	.L.str5, 39

	.type	.L.str6,@object                 # @.str6
	.p2align	4, 0x0
.L.str6:
	.asciz	"Tentando raiz quadrada de n\303\272mero negativo:"
	.size	.L.str6, 44

	.type	.L.str7,@object                 # @.str7
.L.str7:
	.asciz	"Fim dos testes."
	.size	.L.str7, 16

	.section	".note.GNU-stack","",@progbits
