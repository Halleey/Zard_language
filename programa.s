	.text
	.file	"programa.ll"
	.globl	print_Test                      # -- Begin function print_Test
	.p2align	4, 0x90
	.type	print_Test,@function
print_Test:                             # @print_Test
	.cfi_startproc
# %bb.0:                                # %entry
	retq
.Lfunc_end0:
	.size	print_Test, .Lfunc_end0-print_Test
	.cfi_endproc
                                        # -- End function
	.globl	print_Test_string               # -- Begin function print_Test_string
	.p2align	4, 0x90
	.type	print_Test_string,@function
print_Test_string:                      # @print_Test_string
	.cfi_startproc
# %bb.0:                                # %entry
	pushq	%rax
	.cfi_def_cfa_offset 16
	movq	(%rdi), %rdi
	callq	arraylist_print_string@PLT
	popq	%rax
	.cfi_def_cfa_offset 8
	retq
.Lfunc_end1:
	.size	print_Test_string, .Lfunc_end1-print_Test_string
	.cfi_endproc
                                        # -- End function
	.globl	Test_string_add                 # -- Begin function Test_string_add
	.p2align	4, 0x90
	.type	Test_string_add,@function
Test_string_add:                        # @Test_string_add
	.cfi_startproc
# %bb.0:                                # %entry
	pushq	%r15
	.cfi_def_cfa_offset 16
	pushq	%r14
	.cfi_def_cfa_offset 24
	pushq	%rbx
	.cfi_def_cfa_offset 32
	.cfi_offset %rbx, -32
	.cfi_offset %r14, -24
	.cfi_offset %r15, -16
	movq	%rsi, %r14
	movq	%rdi, %rbx
	movq	(%rdi), %rdi
	callq	length@PLT
	testl	%eax, %eax
	movq	(%rbx), %rdi
	jle	.LBB2_5
# %bb.1:                                # %while_body_1.lr.ph
	xorl	%r15d, %r15d
	.p2align	4, 0x90
.LBB2_3:                                # %while_body_1
                                        # =>This Inner Loop Header: Depth=1
	movq	%r15, %rsi
	callq	arraylist_get_ptr@PLT
	movq	%rax, %rdi
	callq	createString@PLT
	movq	%rax, %rdi
	movq	%r14, %rsi
	callq	strcmp_eq@PLT
	testb	$1, %al
	jne	.LBB2_4
# %bb.2:                                # %while_cond_0
                                        #   in Loop: Header=BB2_3 Depth=1
	movq	(%rbx), %rdi
	callq	length@PLT
	movq	(%rbx), %rdi
	incq	%r15
	cmpl	%eax, %r15d
	jl	.LBB2_3
.LBB2_5:                                # %while_end_2
	movq	%r14, %rsi
	callq	arraylist_add_String@PLT
.LBB2_4:                                # %then_0
	movq	%rbx, %rax
	popq	%rbx
	.cfi_def_cfa_offset 24
	popq	%r14
	.cfi_def_cfa_offset 16
	popq	%r15
	.cfi_def_cfa_offset 8
	retq
.Lfunc_end2:
	.size	Test_string_add, .Lfunc_end2-Test_string_add
	.cfi_endproc
                                        # -- End function
	.globl	Test_string_remove              # -- Begin function Test_string_remove
	.p2align	4, 0x90
	.type	Test_string_remove,@function
Test_string_remove:                     # @Test_string_remove
	.cfi_startproc
# %bb.0:                                # %entry
	pushq	%rbx
	.cfi_def_cfa_offset 16
	.cfi_offset %rbx, -16
	movq	%rdi, %rbx
	movq	(%rdi), %rdi
	movl	%esi, %esi
	callq	removeItem@PLT
	movq	%rbx, %rax
	popq	%rbx
	.cfi_def_cfa_offset 8
	retq
.Lfunc_end3:
	.size	Test_string_remove, .Lfunc_end3-Test_string_remove
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
	xorl	%edi, %edi
	callq	inputString@PLT
	movq	%rax, %rdi
	callq	createString@PLT
	movq	%rax, %r14
	movl	$4, %edi
	callq	arraylist_create@PLT
	movq	%rax, %rbx
	movl	$.L.str1, %edi
	callq	createString@PLT
	movq	%rbx, %rdi
	movq	%rax, %rsi
	callq	arraylist_add_String@PLT
	movl	$.L.str2, %edi
	callq	createString@PLT
	movq	%rbx, %rdi
	movq	%rax, %rsi
	callq	arraylist_add_String@PLT
	movq	%rbx, %rdi
	movq	%r14, %rsi
	callq	arraylist_add_String@PLT
	movl	$2, %esi
	movq	%rbx, %rdi
	callq	arraylist_get_ptr@PLT
	movl	$.L.strStr, %edi
	movq	%rax, %rsi
	xorl	%eax, %eax
	callq	printf@PLT
	movl	$10, %edi
	callq	arraylist_create@PLT
	movq	%rax, (%rsp)
	movl	$.L.str1, %edi
	callq	createString@PLT
	movq	%rsp, %r14
	movq	%r14, %rdi
	movq	%rax, %rsi
	callq	Test_string_add@PLT
	movl	$.L.str1, %edi
	callq	createString@PLT
	movq	%r14, %rdi
	movq	%rax, %rsi
	callq	Test_string_add@PLT
	movq	%r14, %rdi
	callq	print_Test_string@PLT
	movq	%rbx, %rdi
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
	.zero	1
	.size	.L.str0, 1

	.type	.L.str1,@object                 # @.str1
.L.str1:
	.asciz	"zard"
	.size	.L.str1, 5

	.type	.L.str2,@object                 # @.str2
.L.str2:
	.asciz	"teste"
	.size	.L.str2, 6

	.section	".note.GNU-stack","",@progbits
