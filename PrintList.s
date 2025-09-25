	.text
	.def	 @feat.00;
	.scl	3;
	.type	0;
	.endef
	.globl	@feat.00
.set @feat.00, 0
	.file	"PrintList.c"
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
	.def	 printList;
	.scl	2;
	.type	32;
	.endef
	.text
	.globl	printList                       # -- Begin function printList
	.p2align	4, 0x90
printList:                              # @printList
.seh_proc printList
# %bb.0:                                # %entry
	subq	$56, %rsp
	.seh_stackalloc 56
	.seh_endprologue
	movq	%rcx, 48(%rsp)
	cmpq	$0, 48(%rsp)
	jne	.LBB4_2
# %bb.1:                                # %if.then
	leaq	"??_C@_03FMDMPNCJ@?$FL?$FN?6?$AA@"(%rip), %rcx
	callq	printf
	jmp	.LBB4_11
.LBB4_2:                                # %if.end
	leaq	"??_C@_01OHGJGJJP@?$FL?$AA@"(%rip), %rcx
	callq	printf
	movq	$0, 40(%rsp)
.LBB4_3:                                # %for.cond
                                        # =>This Inner Loop Header: Depth=1
	movq	40(%rsp), %rax
	movq	48(%rsp), %rcx
	cmpq	8(%rcx), %rax
	jae	.LBB4_10
# %bb.4:                                # %for.body
                                        #   in Loop: Header=BB4_3 Depth=1
	movq	48(%rsp), %rax
	movq	(%rax), %rax
	movq	40(%rsp), %rcx
	movq	(%rax,%rcx,8), %rax
	movq	%rax, 32(%rsp)
	cmpq	$0, 32(%rsp)
	je	.LBB4_6
# %bb.5:                                # %if.then3
                                        #   in Loop: Header=BB4_3 Depth=1
	movq	32(%rsp), %rcx
	callq	printDynValue
.LBB4_6:                                # %if.end4
                                        #   in Loop: Header=BB4_3 Depth=1
	movq	40(%rsp), %rax
	movq	48(%rsp), %rcx
	movq	8(%rcx), %rcx
	subq	$1, %rcx
	cmpq	%rcx, %rax
	jae	.LBB4_8
# %bb.7:                                # %if.then7
                                        #   in Loop: Header=BB4_3 Depth=1
	leaq	"??_C@_02KEGNLNML@?0?5?$AA@"(%rip), %rcx
	callq	printf
.LBB4_8:                                # %if.end9
                                        #   in Loop: Header=BB4_3 Depth=1
	jmp	.LBB4_9
.LBB4_9:                                # %for.inc
                                        #   in Loop: Header=BB4_3 Depth=1
	movq	40(%rsp), %rax
	addq	$1, %rax
	movq	%rax, 40(%rsp)
	jmp	.LBB4_3
.LBB4_10:                               # %for.end
	leaq	"??_C@_02JODFHDIE@?$FN?6?$AA@"(%rip), %rcx
	callq	printf
.LBB4_11:                               # %return
	nop
	addq	$56, %rsp
	retq
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
	jge	.LBB7_2
# %bb.1:                                # %cond.true
	movl	$4294967295, %eax               # imm = 0xFFFFFFFF
	movl	%eax, 60(%rsp)                  # 4-byte Spill
	jmp	.LBB7_3
.LBB7_2:                                # %cond.false
	movl	116(%rsp), %eax
	movl	%eax, 60(%rsp)                  # 4-byte Spill
.LBB7_3:                                # %cond.end
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
	.section	.rdata,"dr",discard,"??_C@_03FMDMPNCJ@?$FL?$FN?6?$AA@"
	.globl	"??_C@_03FMDMPNCJ@?$FL?$FN?6?$AA@" # @"??_C@_03FMDMPNCJ@?$FL?$FN?6?$AA@"
"??_C@_03FMDMPNCJ@?$FL?$FN?6?$AA@":
	.asciz	"[]\n"

	.section	.rdata,"dr",discard,"??_C@_01OHGJGJJP@?$FL?$AA@"
	.globl	"??_C@_01OHGJGJJP@?$FL?$AA@"    # @"??_C@_01OHGJGJJP@?$FL?$AA@"
"??_C@_01OHGJGJJP@?$FL?$AA@":
	.asciz	"["

	.section	.rdata,"dr",discard,"??_C@_02KEGNLNML@?0?5?$AA@"
	.globl	"??_C@_02KEGNLNML@?0?5?$AA@"    # @"??_C@_02KEGNLNML@?0?5?$AA@"
"??_C@_02KEGNLNML@?0?5?$AA@":
	.asciz	", "

	.section	.rdata,"dr",discard,"??_C@_02JODFHDIE@?$FN?6?$AA@"
	.globl	"??_C@_02JODFHDIE@?$FN?6?$AA@"  # @"??_C@_02JODFHDIE@?$FN?6?$AA@"
"??_C@_02JODFHDIE@?$FN?6?$AA@":
	.asciz	"]\n"

	.lcomm	__local_stdio_printf_options._OptionsStorage,8,8 # @__local_stdio_printf_options._OptionsStorage
	.addrsig
	.addrsig_sym _vsnprintf
	.addrsig_sym printf
	.addrsig_sym printDynValue
	.addrsig_sym _vsprintf_l
	.addrsig_sym _vsnprintf_l
	.addrsig_sym __stdio_common_vsprintf
	.addrsig_sym __local_stdio_printf_options
	.addrsig_sym _vfprintf_l
	.addrsig_sym __acrt_iob_func
	.addrsig_sym __stdio_common_vfprintf
	.addrsig_sym __local_stdio_printf_options._OptionsStorage
