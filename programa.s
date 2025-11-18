	.text
	.file	"programa.ll"
	.globl	print_Pais                      # -- Begin function print_Pais
	.p2align	4, 0x90
	.type	print_Pais,@function
print_Pais:                             # @print_Pais
	.cfi_startproc
# %bb.0:                                # %entry
	pushq	%rax
	.cfi_def_cfa_offset 16
	movq	(%rdi), %rdi
	callq	printString@PLT
	popq	%rax
	.cfi_def_cfa_offset 8
	retq
.Lfunc_end0:
	.size	print_Pais, .Lfunc_end0-print_Pais
	.cfi_endproc
                                        # -- End function
	.globl	print_Endereco                  # -- Begin function print_Endereco
	.p2align	4, 0x90
	.type	print_Endereco,@function
print_Endereco:                         # @print_Endereco
	.cfi_startproc
# %bb.0:                                # %entry
	pushq	%rbx
	.cfi_def_cfa_offset 16
	.cfi_offset %rbx, -16
	movq	%rdi, %rbx
	movq	(%rdi), %rdi
	callq	printString@PLT
	movq	8(%rbx), %rdi
	callq	printString@PLT
	movq	16(%rbx), %rdi
	callq	print_Pais@PLT
	popq	%rbx
	.cfi_def_cfa_offset 8
	retq
.Lfunc_end1:
	.size	print_Endereco, .Lfunc_end1-print_Endereco
	.cfi_endproc
                                        # -- End function
	.globl	print_Pessoa                    # -- Begin function print_Pessoa
	.p2align	4, 0x90
	.type	print_Pessoa,@function
print_Pessoa:                           # @print_Pessoa
	.cfi_startproc
# %bb.0:                                # %entry
	pushq	%rbx
	.cfi_def_cfa_offset 16
	.cfi_offset %rbx, -16
	movq	%rdi, %rbx
	movq	(%rdi), %rdi
	callq	printString@PLT
	movl	8(%rbx), %esi
	movl	$.L.strInt, %edi
	xorl	%eax, %eax
	callq	printf@PLT
	movq	16(%rbx), %rdi
	callq	print_Endereco@PLT
	popq	%rbx
	.cfi_def_cfa_offset 8
	retq
.Lfunc_end2:
	.size	print_Pessoa, .Lfunc_end2-print_Pessoa
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
	pushq	%r12
	pushq	%rbx
	subq	$160, %rsp
	.cfi_offset %rbx, -48
	.cfi_offset %r12, -40
	.cfi_offset %r14, -32
	.cfi_offset %r15, -24
	movl	$4, %edi
	callq	arraylist_create@PLT
	movq	%rax, %rbx
	xorl	%edi, %edi
	callq	createString@PLT
	movq	%rax, -48(%rbp)
	movl	$.L.str0, %edi
	callq	createString@PLT
	movq	%rax, -48(%rbp)
	xorl	%edi, %edi
	callq	createString@PLT
	movq	%rax, -40(%rbp)
	movl	$.L.str1, %edi
	callq	createString@PLT
	movq	%rax, -40(%rbp)
	xorl	%edi, %edi
	callq	createString@PLT
	movq	%rax, -192(%rbp)
	movl	$0, -184(%rbp)
	movq	$0, -176(%rbp)
	movl	$.L.str2, %edi
	callq	createString@PLT
	movq	%rax, -192(%rbp)
	movl	$25, -184(%rbp)
	xorl	%edi, %edi
	callq	createString@PLT
	movq	%rax, -168(%rbp)
	xorl	%edi, %edi
	callq	createString@PLT
	movq	%rax, -160(%rbp)
	movq	$0, -152(%rbp)
	movl	$.L.str3, %edi
	callq	createString@PLT
	movq	%rax, -168(%rbp)
	movl	$.L.str4, %edi
	callq	createString@PLT
	movq	%rax, -160(%rbp)
	leaq	-48(%rbp), %r14
	movq	%r14, -152(%rbp)
	leaq	-168(%rbp), %rax
	movq	%rax, -176(%rbp)
	xorl	%edi, %edi
	callq	createString@PLT
	movq	%rax, -144(%rbp)
	movl	$0, -136(%rbp)
	movq	$0, -128(%rbp)
	movl	$.L.str5, %edi
	callq	createString@PLT
	movq	%rax, -144(%rbp)
	movl	$17, -136(%rbp)
	xorl	%edi, %edi
	callq	createString@PLT
	movq	%rax, -120(%rbp)
	xorl	%edi, %edi
	callq	createString@PLT
	movq	%rax, -112(%rbp)
	movq	$0, -104(%rbp)
	movl	$.L.str6, %edi
	callq	createString@PLT
	movq	%rax, -120(%rbp)
	movl	$.L.str7, %edi
	callq	createString@PLT
	movq	%rax, -112(%rbp)
	movq	%r14, -104(%rbp)
	leaq	-120(%rbp), %rax
	movq	%rax, -128(%rbp)
	xorl	%edi, %edi
	callq	createString@PLT
	movq	%rax, -96(%rbp)
	movl	$0, -88(%rbp)
	movq	$0, -80(%rbp)
	movl	$.L.str8, %edi
	callq	createString@PLT
	movq	%rax, -96(%rbp)
	movl	$32, -88(%rbp)
	xorl	%edi, %edi
	callq	createString@PLT
	movq	%rax, -72(%rbp)
	xorl	%edi, %edi
	callq	createString@PLT
	movq	%rax, -64(%rbp)
	movq	$0, -56(%rbp)
	movl	$.L.str9, %edi
	callq	createString@PLT
	movq	%rax, -72(%rbp)
	movl	$.L.str10, %edi
	callq	createString@PLT
	movq	%rax, -64(%rbp)
	leaq	-40(%rbp), %rax
	movq	%rax, -56(%rbp)
	leaq	-72(%rbp), %rax
	movq	%rax, -80(%rbp)
	leaq	-192(%rbp), %rsi
	movq	%rbx, %rdi
	callq	arraylist_add_ptr@PLT
	leaq	-144(%rbp), %rsi
	movq	%rbx, %rdi
	callq	arraylist_add_ptr@PLT
	leaq	-96(%rbp), %rsi
	movq	%rbx, %rdi
	callq	arraylist_add_ptr@PLT
	movl	$.L.strStr, %edi
	movl	$.L.str11, %esi
	xorl	%eax, %eax
	callq	printf@PLT
	movq	%rbx, %rdi
	callq	length@PLT
	testl	%eax, %eax
	jle	.LBB3_5
# %bb.1:                                # %while_body_1.lr.ph
	xorl	%r14d, %r14d
	jmp	.LBB3_2
	.p2align	4, 0x90
.LBB3_4:                                # %endif_0
                                        #   in Loop: Header=BB3_2 Depth=1
	movq	%rbx, %rdi
	callq	length@PLT
	incq	%r14
	cmpl	%eax, %r14d
	jge	.LBB3_5
.LBB3_2:                                # %while_body_1
                                        # =>This Inner Loop Header: Depth=1
	movq	%rbx, %rdi
	movq	%r14, %rsi
	callq	arraylist_get_ptr@PLT
	movq	16(%rax), %rax
	movq	16(%rax), %rax
	movq	(%rax), %r15
	movl	$.L.str0, %edi
	callq	createString@PLT
	movq	%r15, %rdi
	movq	%rax, %rsi
	callq	strcmp_eq@PLT
	testb	$1, %al
	je	.LBB3_4
# %bb.3:                                # %then_0
                                        #   in Loop: Header=BB3_2 Depth=1
	movq	%rbx, %rdi
	movq	%r14, %rsi
	callq	arraylist_get_ptr@PLT
	movq	(%rax), %rdi
	callq	printString@PLT
	movq	%rbx, %rdi
	movq	%r14, %rsi
	callq	arraylist_get_ptr@PLT
	movq	16(%rax), %rax
	movq	8(%rax), %rdi
	callq	printString@PLT
	jmp	.LBB3_4
.LBB3_5:                                # %while_end_2
	movq	%rsp, %r14
	leaq	-16(%r14), %r12
	movq	%r12, %rsp
	movl	$0, -16(%r14)
	movq	%rbx, %rdi
	callq	length@PLT
	testl	%eax, %eax
	movl	-16(%r14), %r14d
	jle	.LBB3_8
# %bb.6:                                # %while_body_4.lr.ph
	xorl	%r15d, %r15d
	.p2align	4, 0x90
.LBB3_7:                                # %while_body_4
                                        # =>This Inner Loop Header: Depth=1
	movq	%rbx, %rdi
	movq	%r15, %rsi
	callq	arraylist_get_ptr@PLT
	addl	8(%rax), %r14d
	movl	%r14d, (%r12)
	movq	%rbx, %rdi
	callq	length@PLT
	movl	(%r12), %r14d
	incq	%r15
	cmpl	%eax, %r15d
	jl	.LBB3_7
.LBB3_8:                                # %while_end_5
	movq	%rsp, %r12
	leaq	-16(%r12), %rsp
	movq	%rbx, %rdi
	callq	length@PLT
	movl	%eax, %ecx
	movl	%r14d, %eax
	cltd
	idivl	%ecx
	movl	%eax, -16(%r12)
	xorl	%r15d, %r15d
	movl	$.L.strStr, %edi
	movl	$.L.str12, %esi
	xorl	%eax, %eax
	callq	printf@PLT
	movl	-16(%r12), %esi
	movl	$.L.strInt, %edi
	xorl	%eax, %eax
	callq	printf@PLT
	movl	$2, %esi
	movq	%rbx, %rdi
	callq	arraylist_get_ptr@PLT
	movq	16(%rax), %rax
	movq	16(%rax), %rax
	movq	(%rax), %r14
	movl	$.L.str1, %edi
	callq	createString@PLT
	movq	%r14, %rdi
	movq	%rax, %rsi
	callq	strcmp_eq@PLT
	testb	$1, %al
	je	.LBB3_10
# %bb.9:                                # %and.rhs_6
	movl	$2, %esi
	movq	%rbx, %rdi
	callq	arraylist_get_ptr@PLT
	cmpl	$31, 8(%rax)
	setge	%r15b
.LBB3_10:                               # %and.end_7
	testb	%r15b, %r15b
	je	.LBB3_12
# %bb.11:                               # %then_1
	movl	$2, %esi
	movq	%rbx, %rdi
	callq	arraylist_get_ptr@PLT
	movq	(%rax), %rdi
	callq	printString@PLT
	movl	$.L.strStr, %edi
	movl	$.L.str13, %esi
	xorl	%eax, %eax
	callq	printf@PLT
.LBB3_12:                               # %endif_1
	movq	%rbx, %rdi
	callq	freeList@PLT
	callq	getchar@PLT
	xorl	%eax, %eax
	leaq	-32(%rbp), %rsp
	popq	%rbx
	popq	%r12
	popq	%r14
	popq	%r15
	popq	%rbp
	.cfi_def_cfa %rsp, 8
	retq
.Lfunc_end3:
	.size	main, .Lfunc_end3-main
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
	.asciz	"Brasil"
	.size	.L.str0, 7

	.type	.L.str1,@object                 # @.str1
.L.str1:
	.asciz	"Argentina"
	.size	.L.str1, 10

	.type	.L.str2,@object                 # @.str2
.L.str2:
	.asciz	"Alice"
	.size	.L.str2, 6

	.type	.L.str3,@object                 # @.str3
.L.str3:
	.asciz	"Rua A"
	.size	.L.str3, 6

	.type	.L.str4,@object                 # @.str4
.L.str4:
	.asciz	"S\303\243o Paulo"
	.size	.L.str4, 11

	.type	.L.str5,@object                 # @.str5
.L.str5:
	.asciz	"Bob"
	.size	.L.str5, 4

	.type	.L.str6,@object                 # @.str6
.L.str6:
	.asciz	"Av. Central"
	.size	.L.str6, 12

	.type	.L.str7,@object                 # @.str7
.L.str7:
	.asciz	"Rio de Janeiro"
	.size	.L.str7, 15

	.type	.L.str8,@object                 # @.str8
.L.str8:
	.asciz	"Carlos"
	.size	.L.str8, 7

	.type	.L.str9,@object                 # @.str9
.L.str9:
	.asciz	"Calle 9"
	.size	.L.str9, 8

	.type	.L.str10,@object                # @.str10
.L.str10:
	.asciz	"Buenos Aires"
	.size	.L.str10, 13

	.type	.L.str11,@object                # @.str11
	.p2align	4, 0x0
.L.str11:
	.asciz	"=== Pessoas que moram no Brasil ==="
	.size	.L.str11, 36

	.type	.L.str12,@object                # @.str12
.L.str12:
	.asciz	"Idade m\303\251dia:"
	.size	.L.str12, 14

	.type	.L.str13,@object                # @.str13
	.p2align	4, 0x0
.L.str13:
	.asciz	" \303\251 argentino e tem mais de 30 anos"
	.size	.L.str13, 36

	.section	".note.GNU-stack","",@progbits
