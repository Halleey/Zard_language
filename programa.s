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
	movq	24(%rbx), %rdi
	callq	arraylist_print_string@PLT
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
	pushq	%rbx
	.cfi_def_cfa_offset 16
	subq	$128, %rsp
	.cfi_def_cfa_offset 144
	.cfi_offset %rbx, -16
	xorl	%edi, %edi
	callq	createString@PLT
	movq	%rax, 88(%rsp)
	movl	$.L.str0, %edi
	callq	createString@PLT
	movq	%rax, 88(%rsp)
	xorl	%edi, %edi
	callq	createString@PLT
	movq	%rax, 64(%rsp)
	xorl	%edi, %edi
	callq	createString@PLT
	movq	%rax, 72(%rsp)
	movq	$0, 80(%rsp)
	movl	$.L.str1, %edi
	callq	createString@PLT
	movq	%rax, 64(%rsp)
	movl	$.L.str2, %edi
	callq	createString@PLT
	movq	%rax, 72(%rsp)
	leaq	88(%rsp), %rax
	movq	%rax, 80(%rsp)
	xorl	%edi, %edi
	callq	createString@PLT
	movq	%rax, (%rsp)
	movl	$0, 8(%rsp)
	movq	$0, 16(%rsp)
	movl	$10, %edi
	callq	arraylist_create@PLT
	movq	%rax, 24(%rsp)
	movl	$.L.str3, %edi
	callq	createString@PLT
	movq	%rax, (%rsp)
	movl	$25, 8(%rsp)
	leaq	64(%rsp), %rax
	movq	%rax, 16(%rsp)
	movq	24(%rsp), %rbx
	movl	$.L.str4, %edi
	callq	createString@PLT
	movq	%rbx, %rdi
	movq	%rax, %rsi
	callq	arraylist_add_String@PLT
	movq	24(%rsp), %rbx
	movl	$.L.str5, %edi
	callq	createString@PLT
	movq	%rbx, %rdi
	movq	%rax, %rsi
	callq	arraylist_add_String@PLT
	xorl	%edi, %edi
	callq	createString@PLT
	movq	%rax, 32(%rsp)
	movl	$0, 40(%rsp)
	movq	$0, 48(%rsp)
	movl	$10, %edi
	callq	arraylist_create@PLT
	movq	%rax, 56(%rsp)
	movq	(%rsp), %rax
	movq	%rax, 32(%rsp)
	movl	8(%rsp), %eax
	movl	%eax, 40(%rsp)
	movq	16(%rsp), %rax
	movq	(%rax), %rcx
	movq	%rcx, 104(%rsp)
	movq	8(%rax), %rcx
	movq	%rcx, 112(%rsp)
	movq	16(%rax), %rax
	movq	(%rax), %rax
	movq	%rax, 96(%rsp)
	leaq	96(%rsp), %rax
	movq	%rax, 120(%rsp)
	leaq	104(%rsp), %rax
	movq	%rax, 48(%rsp)
	movq	24(%rsp), %rax
	movq	%rax, 56(%rsp)
	movl	$.L.str6, %edi
	callq	createString@PLT
	movq	%rax, 32(%rsp)
	movq	56(%rsp), %rbx
	movl	$.L.str7, %edi
	callq	createString@PLT
	movq	%rbx, %rdi
	movq	%rax, %rsi
	callq	arraylist_add_String@PLT
	movl	$.L.str8, %edi
	callq	createString@PLT
	movq	%rax, 64(%rsp)
	movl	$.L.str9, %edi
	callq	createString@PLT
	movq	%rax, 72(%rsp)
	movl	$.L.strStr, %edi
	movl	$.L.str10, %esi
	xorl	%eax, %eax
	callq	printf@PLT
	movq	%rsp, %rdi
	callq	print_Pessoa@PLT
	movq	48(%rsp), %rbx
	movl	$.L.str11, %edi
	callq	createString@PLT
	movq	%rax, (%rbx)
	movl	$.L.str12, %edi
	callq	createString@PLT
	movq	%rax, 8(%rbx)
	movq	16(%rbx), %rbx
	movl	$.L.str13, %edi
	callq	createString@PLT
	movq	%rax, (%rbx)
	movl	$.L.strStr, %edi
	movl	$.L.str14, %esi
	xorl	%eax, %eax
	callq	printf@PLT
	leaq	32(%rsp), %rdi
	callq	print_Pessoa@PLT
	callq	getchar@PLT
	xorl	%eax, %eax
	addq	$128, %rsp
	.cfi_def_cfa_offset 16
	popq	%rbx
	.cfi_def_cfa_offset 8
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
	.asciz	"Rua A"
	.size	.L.str1, 6

	.type	.L.str2,@object                 # @.str2
.L.str2:
	.asciz	"S\303\243o Paulo"
	.size	.L.str2, 11

	.type	.L.str3,@object                 # @.str3
.L.str3:
	.asciz	"Alice"
	.size	.L.str3, 6

	.type	.L.str4,@object                 # @.str4
.L.str4:
	.asciz	"11 99999-1111"
	.size	.L.str4, 14

	.type	.L.str5,@object                 # @.str5
.L.str5:
	.asciz	"11 22222-3333"
	.size	.L.str5, 14

	.type	.L.str6,@object                 # @.str6
.L.str6:
	.asciz	"Zard"
	.size	.L.str6, 5

	.type	.L.str7,@object                 # @.str7
.L.str7:
	.asciz	"21 44444-5555"
	.size	.L.str7, 14

	.type	.L.str8,@object                 # @.str8
.L.str8:
	.asciz	"teste"
	.size	.L.str8, 6

	.type	.L.str9,@object                 # @.str9
.L.str9:
	.asciz	"Santo andr\303\251"
	.size	.L.str9, 13

	.type	.L.str10,@object                # @.str10
	.p2align	4, 0x0
.L.str10:
	.asciz	"=== Original p1 ==="
	.size	.L.str10, 20

	.type	.L.str11,@object                # @.str11
.L.str11:
	.asciz	"Nova Rua"
	.size	.L.str11, 9

	.type	.L.str12,@object                # @.str12
.L.str12:
	.asciz	"Zurique"
	.size	.L.str12, 8

	.type	.L.str13,@object                # @.str13
.L.str13:
	.asciz	"Su\303\255\303\247a"
	.size	.L.str13, 8

	.type	.L.str14,@object                # @.str14
	.p2align	4, 0x0
.L.str14:
	.asciz	"=== Clone p2 ==="
	.size	.L.str14, 17

	.section	".note.GNU-stack","",@progbits
