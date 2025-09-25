	.text
	.def	 @feat.00;
	.scl	3;
	.type	0;
	.endef
	.globl	@feat.00
.set @feat.00, 0
	.file	"DynValue.c"
	.def	 sprintf;
	.scl	2;
	.type	32;
	.endef
	.section	.text,"xr",discard,sprintf
	.globl	sprintf                         # -- Begin function sprintf
	.p2align	4, 0x90
sprintf:                                # @sprintf
.seh_proc sprintf
# %bb.0:                                # %entry
	subq	$72, %rsp
	.seh_stackalloc 72
	.seh_endprologue
	movq	%r9, 104(%rsp)
	movq	%r8, 96(%rsp)
	movq	%rdx, 64(%rsp)
	movq	%rcx, 56(%rsp)
	leaq	96(%rsp), %rax
	movq	%rax, 40(%rsp)
	movq	40(%rsp), %r9
	movq	64(%rsp), %rdx
	movq	56(%rsp), %rcx
	xorl	%eax, %eax
	movl	%eax, %r8d
	callq	_vsprintf_l
	movl	%eax, 52(%rsp)
	movl	52(%rsp), %eax
	addq	$72, %rsp
	retq
	.seh_handlerdata
	.section	.text,"xr",discard,sprintf
	.seh_endproc
                                        # -- End function
	.def	 vsprintf;
	.scl	2;
	.type	32;
	.endef
	.section	.text,"xr",discard,vsprintf
	.globl	vsprintf                        # -- Begin function vsprintf
	.p2align	4, 0x90
vsprintf:                               # @vsprintf
.seh_proc vsprintf
# %bb.0:                                # %entry
	subq	$72, %rsp
	.seh_stackalloc 72
	.seh_endprologue
	xorl	%eax, %eax
	movl	%eax, %r9d
	movq	%r8, 64(%rsp)
	movq	%rdx, 56(%rsp)
	movq	%rcx, 48(%rsp)
	movq	64(%rsp), %rax
	movq	56(%rsp), %r8
	movq	48(%rsp), %rcx
	movq	$-1, %rdx
	movq	%rax, 32(%rsp)
	callq	_vsnprintf_l
	nop
	addq	$72, %rsp
	retq
	.seh_handlerdata
	.section	.text,"xr",discard,vsprintf
	.seh_endproc
                                        # -- End function
	.def	 _snprintf;
	.scl	2;
	.type	32;
	.endef
	.section	.text,"xr",discard,_snprintf
	.globl	_snprintf                       # -- Begin function _snprintf
	.p2align	4, 0x90
_snprintf:                              # @_snprintf
.seh_proc _snprintf
# %bb.0:                                # %entry
	subq	$72, %rsp
	.seh_stackalloc 72
	.seh_endprologue
	movq	%r9, 104(%rsp)
	movq	%r8, 64(%rsp)
	movq	%rdx, 56(%rsp)
	movq	%rcx, 48(%rsp)
	leaq	104(%rsp), %rax
	movq	%rax, 32(%rsp)
	movq	32(%rsp), %r9
	movq	64(%rsp), %r8
	movq	56(%rsp), %rdx
	movq	48(%rsp), %rcx
	callq	_vsnprintf
	movl	%eax, 44(%rsp)
	movl	44(%rsp), %eax
	addq	$72, %rsp
	retq
	.seh_handlerdata
	.section	.text,"xr",discard,_snprintf
	.seh_endproc
                                        # -- End function
	.def	 _vsnprintf;
	.scl	2;
	.type	32;
	.endef
	.section	.text,"xr",discard,_vsnprintf
	.globl	_vsnprintf                      # -- Begin function _vsnprintf
	.p2align	4, 0x90
_vsnprintf:                             # @_vsnprintf
.seh_proc _vsnprintf
# %bb.0:                                # %entry
	subq	$88, %rsp
	.seh_stackalloc 88
	.seh_endprologue
	xorl	%eax, %eax
                                        # kill: def $rax killed $eax
	movq	%r9, 80(%rsp)
	movq	%r8, 72(%rsp)
	movq	%rdx, 64(%rsp)
	movq	%rcx, 56(%rsp)
	movq	80(%rsp), %rcx
	movq	72(%rsp), %r8
	movq	64(%rsp), %rdx
	movq	56(%rsp), %r9
	movq	%rcx, 48(%rsp)                  # 8-byte Spill
	movq	%r9, %rcx
	movq	%rax, %r9
	movq	48(%rsp), %rax                  # 8-byte Reload
	movq	%rax, 32(%rsp)
	callq	_vsnprintf_l
	nop
	addq	$88, %rsp
	retq
	.seh_handlerdata
	.section	.text,"xr",discard,_vsnprintf
	.seh_endproc
                                        # -- End function
	.def	 createInt;
	.scl	2;
	.type	32;
	.endef
	.text
	.globl	createInt                       # -- Begin function createInt
	.p2align	4, 0x90
createInt:                              # @createInt
.seh_proc createInt
# %bb.0:                                # %entry
	subq	$56, %rsp
	.seh_stackalloc 56
	.seh_endprologue
	movl	%ecx, 52(%rsp)
	movl	$16, %ecx
	callq	malloc
	movq	%rax, 40(%rsp)
	movl	$4, %ecx
	callq	malloc
	movq	%rax, 32(%rsp)
	movl	52(%rsp), %eax
	movq	32(%rsp), %rcx
	movl	%eax, (%rcx)
	movq	40(%rsp), %rax
	movl	$0, (%rax)
	movq	32(%rsp), %rax
	movq	40(%rsp), %rcx
	movq	%rax, 8(%rcx)
	movq	40(%rsp), %rax
	addq	$56, %rsp
	retq
	.seh_handlerdata
	.text
	.seh_endproc
                                        # -- End function
	.def	 createDouble;
	.scl	2;
	.type	32;
	.endef
	.globl	createDouble                    # -- Begin function createDouble
	.p2align	4, 0x90
createDouble:                           # @createDouble
.seh_proc createDouble
# %bb.0:                                # %entry
	subq	$56, %rsp
	.seh_stackalloc 56
	.seh_endprologue
	movsd	%xmm0, 48(%rsp)
	movl	$16, %ecx
	callq	malloc
	movq	%rax, 40(%rsp)
	movl	$8, %ecx
	callq	malloc
	movq	%rax, 32(%rsp)
	movsd	48(%rsp), %xmm0                 # xmm0 = mem[0],zero
	movq	32(%rsp), %rax
	movsd	%xmm0, (%rax)
	movq	40(%rsp), %rax
	movl	$1, (%rax)
	movq	32(%rsp), %rax
	movq	40(%rsp), %rcx
	movq	%rax, 8(%rcx)
	movq	40(%rsp), %rax
	addq	$56, %rsp
	retq
	.seh_handlerdata
	.text
	.seh_endproc
                                        # -- End function
	.def	 createBool;
	.scl	2;
	.type	32;
	.endef
	.globl	createBool                      # -- Begin function createBool
	.p2align	4, 0x90
createBool:                             # @createBool
.seh_proc createBool
# %bb.0:                                # %entry
	subq	$56, %rsp
	.seh_stackalloc 56
	.seh_endprologue
	movl	%ecx, 52(%rsp)
	movl	$16, %ecx
	callq	malloc
	movq	%rax, 40(%rsp)
	movl	$4, %ecx
	callq	malloc
	xorl	%ecx, %ecx
	movq	%rax, 32(%rsp)
	movl	52(%rsp), %eax
	cmpl	$0, %eax
	movl	$1, %eax
	cmovnel	%eax, %ecx
	movq	32(%rsp), %rax
	movl	%ecx, (%rax)
	movq	40(%rsp), %rax
	movl	$2, (%rax)
	movq	32(%rsp), %rax
	movq	40(%rsp), %rcx
	movq	%rax, 8(%rcx)
	movq	40(%rsp), %rax
	addq	$56, %rsp
	retq
	.seh_handlerdata
	.text
	.seh_endproc
                                        # -- End function
	.def	 createString;
	.scl	2;
	.type	32;
	.endef
	.globl	createString                    # -- Begin function createString
	.p2align	4, 0x90
createString:                           # @createString
.seh_proc createString
# %bb.0:                                # %entry
	subq	$72, %rsp
	.seh_stackalloc 72
	.seh_endprologue
	movq	%rcx, 64(%rsp)
	movl	$16, %ecx
	callq	malloc
	movq	%rax, 56(%rsp)
	movq	64(%rsp), %rcx
	callq	strlen
	addq	$1, %rax
	movq	%rax, %rcx
	callq	malloc
	movq	%rax, 48(%rsp)
	movq	64(%rsp), %r8
	movq	64(%rsp), %rcx
	movq	%r8, 40(%rsp)                   # 8-byte Spill
	callq	strlen
	addq	$1, %rax
	movq	48(%rsp), %rcx
	movq	%rax, %rdx
	movq	40(%rsp), %r8                   # 8-byte Reload
	callq	strcpy_s
	movq	56(%rsp), %rcx
	movl	$3, (%rcx)
	movq	48(%rsp), %rcx
	movq	56(%rsp), %rdx
	movq	%rcx, 8(%rdx)
	movq	56(%rsp), %rcx
	movl	%eax, 36(%rsp)                  # 4-byte Spill
	movq	%rcx, %rax
	addq	$72, %rsp
	retq
	.seh_handlerdata
	.text
	.seh_endproc
                                        # -- End function
	.def	 printDynValue;
	.scl	2;
	.type	32;
	.endef
	.globl	printDynValue                   # -- Begin function printDynValue
	.p2align	4, 0x90
printDynValue:                          # @printDynValue
.seh_proc printDynValue
# %bb.0:                                # %entry
	subq	$56, %rsp
	.seh_stackalloc 56
	.seh_endprologue
	movq	%rcx, 48(%rsp)
	cmpq	$0, 48(%rsp)
	jne	.LBB8_2
# %bb.1:                                # %if.then
	leaq	"??_C@_05IHCDMNLM@null?6?$AA@"(%rip), %rcx
	callq	printf
	jmp	.LBB8_8
.LBB8_2:                                # %if.end
	movq	48(%rsp), %rax
	movl	(%rax), %eax
                                        # kill: def $rax killed $eax
	movq	%rax, %rcx
	subq	$3, %rcx
	movq	%rax, 40(%rsp)                  # 8-byte Spill
	ja	.LBB8_7
# %bb.9:                                # %if.end
	leaq	.LJTI8_0(%rip), %rax
	movq	40(%rsp), %rcx                  # 8-byte Reload
	movslq	(%rax,%rcx,4), %rdx
	addq	%rax, %rdx
	jmpq	*%rdx
.LBB8_3:                                # %sw.bb
	movq	48(%rsp), %rax
	movq	8(%rax), %rax
	movl	(%rax), %edx
	leaq	"??_C@_03PMGGPEJJ@?$CFd?6?$AA@"(%rip), %rcx
	callq	printf
	jmp	.LBB8_8
.LBB8_4:                                # %sw.bb2
	movq	48(%rsp), %rax
	movq	8(%rax), %rax
	movsd	(%rax), %xmm0                   # xmm0 = mem[0],zero
	leaq	"??_C@_03PPOCCAPH@?$CFf?6?$AA@"(%rip), %rcx
	movaps	%xmm0, %xmm1
	movq	%xmm0, %rdx
	callq	printf
	jmp	.LBB8_8
.LBB8_5:                                # %sw.bb5
	movq	48(%rsp), %rax
	movq	8(%rax), %rax
	movl	(%rax), %eax
	cmpl	$0, %eax
	leaq	"??_C@_04LOAJBDKD@true?$AA@"(%rip), %rax
	leaq	"??_C@_05LAPONLG@false?$AA@"(%rip), %rcx
	cmovneq	%rax, %rcx
	leaq	"??_C@_03OFAPEBGM@?$CFs?6?$AA@"(%rip), %rax
	movq	%rcx, 32(%rsp)                  # 8-byte Spill
	movq	%rax, %rcx
	movq	32(%rsp), %rdx                  # 8-byte Reload
	callq	printf
	jmp	.LBB8_8
.LBB8_6:                                # %sw.bb9
	movq	48(%rsp), %rax
	movq	8(%rax), %rdx
	leaq	"??_C@_03OFAPEBGM@?$CFs?6?$AA@"(%rip), %rcx
	callq	printf
	jmp	.LBB8_8
.LBB8_7:                                # %sw.default
	leaq	"??_C@_08IIMANPGO@unknown?6?$AA@"(%rip), %rcx
	callq	printf
.LBB8_8:                                # %sw.epilog
	nop
	addq	$56, %rsp
	retq
	.p2align	2, 0x90
.LJTI8_0:
	.long	.LBB8_3-.LJTI8_0
	.long	.LBB8_4-.LJTI8_0
	.long	.LBB8_5-.LJTI8_0
	.long	.LBB8_6-.LJTI8_0
	.seh_handlerdata
	.text
	.seh_endproc
                                        # -- End function
	.def	 printf;
	.scl	2;
	.type	32;
	.endef
	.section	.text,"xr",discard,printf
	.globl	printf                          # -- Begin function printf
	.p2align	4, 0x90
printf:                                 # @printf
.seh_proc printf
# %bb.0:                                # %entry
	subq	$72, %rsp
	.seh_stackalloc 72
	.seh_endprologue
	movq	%r9, 104(%rsp)
	movq	%r8, 96(%rsp)
	movq	%rdx, 88(%rsp)
	movq	%rcx, 64(%rsp)
	leaq	88(%rsp), %rax
	movq	%rax, 48(%rsp)
	movq	48(%rsp), %r9
	movq	64(%rsp), %rdx
	movl	$1, %ecx
	movq	%r9, 40(%rsp)                   # 8-byte Spill
	movq	%rdx, 32(%rsp)                  # 8-byte Spill
	callq	__acrt_iob_func
	xorl	%ecx, %ecx
	movl	%ecx, %r8d
	movq	%rax, %rcx
	movq	32(%rsp), %rdx                  # 8-byte Reload
	movq	40(%rsp), %r9                   # 8-byte Reload
	callq	_vfprintf_l
	movl	%eax, 60(%rsp)
	movl	60(%rsp), %eax
	addq	$72, %rsp
	retq
	.seh_handlerdata
	.section	.text,"xr",discard,printf
	.seh_endproc
                                        # -- End function
	.def	 _vsprintf_l;
	.scl	2;
	.type	32;
	.endef
	.section	.text,"xr",discard,_vsprintf_l
	.globl	_vsprintf_l                     # -- Begin function _vsprintf_l
	.p2align	4, 0x90
_vsprintf_l:                            # @_vsprintf_l
.seh_proc _vsprintf_l
# %bb.0:                                # %entry
	subq	$72, %rsp
	.seh_stackalloc 72
	.seh_endprologue
	movq	%r9, 64(%rsp)
	movq	%r8, 56(%rsp)
	movq	%rdx, 48(%rsp)
	movq	%rcx, 40(%rsp)
	movq	64(%rsp), %rax
	movq	56(%rsp), %r9
	movq	48(%rsp), %r8
	movq	40(%rsp), %rcx
	movq	$-1, %rdx
	movq	%rax, 32(%rsp)
	callq	_vsnprintf_l
	nop
	addq	$72, %rsp
	retq
	.seh_handlerdata
	.section	.text,"xr",discard,_vsprintf_l
	.seh_endproc
                                        # -- End function
	.def	 _vsnprintf_l;
	.scl	2;
	.type	32;
	.endef
	.section	.text,"xr",discard,_vsnprintf_l
	.globl	_vsnprintf_l                    # -- Begin function _vsnprintf_l
	.p2align	4, 0x90
_vsnprintf_l:                           # @_vsnprintf_l
.seh_proc _vsnprintf_l
# %bb.0:                                # %entry
	subq	$152, %rsp
	.seh_stackalloc 152
	.seh_endprologue
	movq	192(%rsp), %rax
	movq	%r9, 144(%rsp)
	movq	%r8, 136(%rsp)
	movq	%rdx, 128(%rsp)
	movq	%rcx, 120(%rsp)
	movq	192(%rsp), %rcx
	movq	144(%rsp), %rdx
	movq	136(%rsp), %r9
	movq	128(%rsp), %r8
	movq	120(%rsp), %r10
	movq	%rax, 104(%rsp)                 # 8-byte Spill
	movq	%rcx, 96(%rsp)                  # 8-byte Spill
	movq	%rdx, 88(%rsp)                  # 8-byte Spill
	movq	%r9, 80(%rsp)                   # 8-byte Spill
	movq	%r8, 72(%rsp)                   # 8-byte Spill
	movq	%r10, 64(%rsp)                  # 8-byte Spill
	callq	__local_stdio_printf_options
	movq	(%rax), %rax
	orq	$1, %rax
	movq	%rax, %rcx
	movq	64(%rsp), %rdx                  # 8-byte Reload
	movq	72(%rsp), %r8                   # 8-byte Reload
	movq	80(%rsp), %r9                   # 8-byte Reload
	movq	88(%rsp), %rax                  # 8-byte Reload
	movq	%rax, 32(%rsp)
	movq	96(%rsp), %rax                  # 8-byte Reload
	movq	%rax, 40(%rsp)
	callq	__stdio_common_vsprintf
	movl	%eax, 116(%rsp)
	cmpl	$0, 116(%rsp)
	jge	.LBB11_2
# %bb.1:                                # %cond.true
	movl	$4294967295, %eax               # imm = 0xFFFFFFFF
	movl	%eax, 60(%rsp)                  # 4-byte Spill
	jmp	.LBB11_3
.LBB11_2:                               # %cond.false
	movl	116(%rsp), %eax
	movl	%eax, 60(%rsp)                  # 4-byte Spill
.LBB11_3:                               # %cond.end
	movl	60(%rsp), %eax                  # 4-byte Reload
	addq	$152, %rsp
	retq
	.seh_handlerdata
	.section	.text,"xr",discard,_vsnprintf_l
	.seh_endproc
                                        # -- End function
	.def	 __local_stdio_printf_options;
	.scl	2;
	.type	32;
	.endef
	.section	.text,"xr",discard,__local_stdio_printf_options
	.globl	__local_stdio_printf_options    # -- Begin function __local_stdio_printf_options
	.p2align	4, 0x90
__local_stdio_printf_options:           # @__local_stdio_printf_options
# %bb.0:                                # %entry
	leaq	__local_stdio_printf_options._OptionsStorage(%rip), %rax
	retq
                                        # -- End function
	.def	 _vfprintf_l;
	.scl	2;
	.type	32;
	.endef
	.section	.text,"xr",discard,_vfprintf_l
	.globl	_vfprintf_l                     # -- Begin function _vfprintf_l
	.p2align	4, 0x90
_vfprintf_l:                            # @_vfprintf_l
.seh_proc _vfprintf_l
# %bb.0:                                # %entry
	subq	$104, %rsp
	.seh_stackalloc 104
	.seh_endprologue
	movq	%r9, 96(%rsp)
	movq	%r8, 88(%rsp)
	movq	%rdx, 80(%rsp)
	movq	%rcx, 72(%rsp)
	movq	96(%rsp), %rax
	movq	88(%rsp), %r9
	movq	80(%rsp), %r8
	movq	72(%rsp), %rdx
	movq	%rax, 64(%rsp)                  # 8-byte Spill
	movq	%r9, 56(%rsp)                   # 8-byte Spill
	movq	%r8, 48(%rsp)                   # 8-byte Spill
	movq	%rdx, 40(%rsp)                  # 8-byte Spill
	callq	__local_stdio_printf_options
	movq	(%rax), %rcx
	movq	40(%rsp), %rdx                  # 8-byte Reload
	movq	48(%rsp), %r8                   # 8-byte Reload
	movq	56(%rsp), %r9                   # 8-byte Reload
	movq	64(%rsp), %rax                  # 8-byte Reload
	movq	%rax, 32(%rsp)
	callq	__stdio_common_vfprintf
	nop
	addq	$104, %rsp
	retq
	.seh_handlerdata
	.section	.text,"xr",discard,_vfprintf_l
	.seh_endproc
                                        # -- End function
	.section	.rdata,"dr",discard,"??_C@_05IHCDMNLM@null?6?$AA@"
	.globl	"??_C@_05IHCDMNLM@null?6?$AA@"  # @"??_C@_05IHCDMNLM@null?6?$AA@"
"??_C@_05IHCDMNLM@null?6?$AA@":
	.asciz	"null\n"

	.section	.rdata,"dr",discard,"??_C@_03PMGGPEJJ@?$CFd?6?$AA@"
	.globl	"??_C@_03PMGGPEJJ@?$CFd?6?$AA@" # @"??_C@_03PMGGPEJJ@?$CFd?6?$AA@"
"??_C@_03PMGGPEJJ@?$CFd?6?$AA@":
	.asciz	"%d\n"

	.section	.rdata,"dr",discard,"??_C@_03PPOCCAPH@?$CFf?6?$AA@"
	.globl	"??_C@_03PPOCCAPH@?$CFf?6?$AA@" # @"??_C@_03PPOCCAPH@?$CFf?6?$AA@"
"??_C@_03PPOCCAPH@?$CFf?6?$AA@":
	.asciz	"%f\n"

	.section	.rdata,"dr",discard,"??_C@_04LOAJBDKD@true?$AA@"
	.globl	"??_C@_04LOAJBDKD@true?$AA@"    # @"??_C@_04LOAJBDKD@true?$AA@"
"??_C@_04LOAJBDKD@true?$AA@":
	.asciz	"true"

	.section	.rdata,"dr",discard,"??_C@_05LAPONLG@false?$AA@"
	.globl	"??_C@_05LAPONLG@false?$AA@"    # @"??_C@_05LAPONLG@false?$AA@"
"??_C@_05LAPONLG@false?$AA@":
	.asciz	"false"

	.section	.rdata,"dr",discard,"??_C@_03OFAPEBGM@?$CFs?6?$AA@"
	.globl	"??_C@_03OFAPEBGM@?$CFs?6?$AA@" # @"??_C@_03OFAPEBGM@?$CFs?6?$AA@"
"??_C@_03OFAPEBGM@?$CFs?6?$AA@":
	.asciz	"%s\n"

	.section	.rdata,"dr",discard,"??_C@_08IIMANPGO@unknown?6?$AA@"
	.globl	"??_C@_08IIMANPGO@unknown?6?$AA@" # @"??_C@_08IIMANPGO@unknown?6?$AA@"
"??_C@_08IIMANPGO@unknown?6?$AA@":
	.asciz	"unknown\n"

	.lcomm	__local_stdio_printf_options._OptionsStorage,8,8 # @__local_stdio_printf_options._OptionsStorage
	.addrsig
	.addrsig_sym _vsnprintf
	.addrsig_sym malloc
	.addrsig_sym strlen
	.addrsig_sym strcpy_s
	.addrsig_sym printf
	.addrsig_sym _vsprintf_l
	.addrsig_sym _vsnprintf_l
	.addrsig_sym __stdio_common_vsprintf
	.addrsig_sym __local_stdio_printf_options
	.addrsig_sym _vfprintf_l
	.addrsig_sym __acrt_iob_func
	.addrsig_sym __stdio_common_vfprintf
	.addrsig_sym __local_stdio_printf_options._OptionsStorage
	.globl	_fltused
