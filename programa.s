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
	.globl	print_Set_string                # -- Begin function print_Set_string
	.p2align	4, 0x90
	.type	print_Set_string,@function
print_Set_string:                       # @print_Set_string
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
	.size	print_Set_string, .Lfunc_end1-print_Set_string
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
	.globl	Set_string_add                  # -- Begin function Set_string_add
	.p2align	4, 0x90
	.type	Set_string_add,@function
Set_string_add:                         # @Set_string_add
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
	jle	.LBB3_5
# %bb.1:                                # %while_body_1.lr.ph
	xorl	%r15d, %r15d
	.p2align	4, 0x90
.LBB3_3:                                # %while_body_1
                                        # =>This Inner Loop Header: Depth=1
	movq	%r15, %rsi
	callq	arraylist_get_ptr@PLT
	movq	%rax, %rdi
	movq	%r14, %rsi
	callq	strcmp_eq@PLT
	testb	$1, %al
	jne	.LBB3_4
# %bb.2:                                # %while_cond_0
                                        #   in Loop: Header=BB3_3 Depth=1
	movq	(%rbx), %rdi
	callq	length@PLT
	movq	(%rbx), %rdi
	incq	%r15
	cmpl	%eax, %r15d
	jl	.LBB3_3
.LBB3_5:                                # %while_end_2
	movq	%r14, %rsi
	callq	arraylist_add_String@PLT
.LBB3_4:                                # %then_0
	movq	%rbx, %rax
	popq	%rbx
	.cfi_def_cfa_offset 24
	popq	%r14
	.cfi_def_cfa_offset 16
	popq	%r15
	.cfi_def_cfa_offset 8
	retq
.Lfunc_end3:
	.size	Set_string_add, .Lfunc_end3-Set_string_add
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
# %bb.1:                                # %while_body_1.lr.ph
	xorl	%r15d, %r15d
	.p2align	4, 0x90
.LBB4_3:                                # %while_body_1
                                        # =>This Inner Loop Header: Depth=1
	movq	%rsp, %r12
	leaq	-16(%r12), %rdx
	movq	%rdx, %rsp
	movq	%r15, %rsi
	callq	arraylist_get_int@PLT
	cmpl	%r14d, -16(%r12)
	je	.LBB4_4
# %bb.2:                                # %while_cond_0
                                        #   in Loop: Header=BB4_3 Depth=1
	movq	(%rbx), %rdi
	callq	arraylist_size_int@PLT
	movq	(%rbx), %rdi
	incq	%r15
	cmpl	%eax, %r15d
	jl	.LBB4_3
.LBB4_5:                                # %while_end_2
	movl	%r14d, %esi
	callq	arraylist_add_int@PLT
.LBB4_4:                                # %then_0
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
	.globl	Set_string_remove               # -- Begin function Set_string_remove
	.p2align	4, 0x90
	.type	Set_string_remove,@function
Set_string_remove:                      # @Set_string_remove
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
.Lfunc_end5:
	.size	Set_string_remove, .Lfunc_end5-Set_string_remove
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
	movl	$.L.strStr, %edi
	movl	$.L.str0, %esi
	xorl	%eax, %eax
	callq	printf@PLT
	movl	$.L.strInt, %edi
	movl	$4, %esi
	xorl	%eax, %eax
	callq	printf@PLT
	movl	$4, %edi
	callq	arraylist_create@PLT
	movq	%rax, %rbx
	movl	$.L.str1, %edi
	callq	createString@PLT
	movq	%rbx, %rdi
	movq	%rax, %rsi
	callq	arraylist_add_String@PLT
	movq	%rbx, 24(%rsp)
	movl	$.L.str2, %edi
	callq	createString@PLT
	leaq	24(%rsp), %rbx
	movq	%rbx, %rdi
	movq	%rax, %rsi
	callq	Set_string_add@PLT
	movq	%rbx, %rdi
	callq	print_Set_string@PLT
	movl	$4, %edi
	callq	arraylist_create_int@PLT
	movq	%rax, %rbx
	movl	$2, 12(%rsp)
	leaq	12(%rsp), %rsi
	movl	$1, %edx
	movq	%rax, %rdi
	callq	arraylist_addAll_int@PLT
	movq	%rbx, 16(%rsp)
	leaq	16(%rsp), %rbx
	movq	%rbx, %rdi
	movl	$2, %esi
	callq	Set_int_add@PLT
	movq	%rbx, %rdi
	callq	print_Set_int@PLT
	callq	getchar@PLT
	xorl	%eax, %eax
	addq	$32, %rsp
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

	.type	.L.str0,@object                 # @.str0
.L.str0:
	.asciz	"salve mundo"
	.size	.L.str0, 12

	.type	.L.str1,@object                 # @.str1
.L.str1:
	.asciz	"teste"
	.size	.L.str1, 6

	.type	.L.str2,@object                 # @.str2
.L.str2:
	.asciz	"zard"
	.size	.L.str2, 5

	.section	".note.GNU-stack","",@progbits
