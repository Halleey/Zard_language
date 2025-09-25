	.text
	.def	 @feat.00;
	.scl	3;
	.type	0;
	.endef
	.globl	@feat.00
.set @feat.00, 0
	.file	"ArrayList.c"
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
	.def	 arraylist_create;
	.scl	2;
	.type	32;
	.endef
	.text
	.globl	arraylist_create                # -- Begin function arraylist_create
	.p2align	4, 0x90
arraylist_create:                       # @arraylist_create
.seh_proc arraylist_create
# %bb.0:                                # %entry
	subq	$56, %rsp
	.seh_stackalloc 56
	.seh_endprologue
	movq	%rcx, 40(%rsp)
	movl	$24, %ecx
	callq	malloc
	movq	%rax, 32(%rsp)
	cmpq	$0, 32(%rsp)
	jne	.LBB4_2
# %bb.1:                                # %if.then
	movq	$0, 48(%rsp)
	jmp	.LBB4_5
.LBB4_2:                                # %if.end
	movq	40(%rsp), %rax
	shlq	$3, %rax
	movq	%rax, %rcx
	callq	malloc
	movq	32(%rsp), %rcx
	movq	%rax, (%rcx)
	movq	32(%rsp), %rax
	cmpq	$0, (%rax)
	jne	.LBB4_4
# %bb.3:                                # %if.then4
	movq	32(%rsp), %rax
	movq	%rax, %rcx
	callq	free
	movq	$0, 48(%rsp)
	jmp	.LBB4_5
.LBB4_4:                                # %if.end5
	movq	32(%rsp), %rax
	movq	$0, 8(%rax)
	movq	40(%rsp), %rax
	movq	32(%rsp), %rcx
	movq	%rax, 16(%rcx)
	movq	32(%rsp), %rax
	movq	%rax, 48(%rsp)
.LBB4_5:                                # %return
	movq	48(%rsp), %rax
	addq	$56, %rsp
	retq
	.seh_handlerdata
	.text
	.seh_endproc
                                        # -- End function
	.def	 setItems;
	.scl	2;
	.type	32;
	.endef
	.globl	setItems                        # -- Begin function setItems
	.p2align	4, 0x90
setItems:                               # @setItems
.seh_proc setItems
# %bb.0:                                # %entry
	subq	$72, %rsp
	.seh_stackalloc 72
	.seh_endprologue
	movq	%rdx, 64(%rsp)
	movq	%rcx, 56(%rsp)
	movq	56(%rsp), %rax
	movq	8(%rax), %rax
	movq	56(%rsp), %rcx
	cmpq	16(%rcx), %rax
	jne	.LBB5_4
# %bb.1:                                # %if.then
	movq	56(%rsp), %rax
	movq	16(%rax), %rax
	shlq	$1, %rax
	movq	%rax, 48(%rsp)
	movq	48(%rsp), %rax
	shlq	$3, %rax
	movq	56(%rsp), %rcx
	movq	(%rcx), %rcx
	movq	%rax, %rdx
	callq	realloc
	movq	%rax, 40(%rsp)
	cmpq	$0, 40(%rsp)
	jne	.LBB5_3
# %bb.2:                                # %if.then4
	movl	$2, %ecx
	callq	__acrt_iob_func
	leaq	"??_C@_0BH@FBJAHFHE@Falha?5ao?5salvar?5array?6?$AA@"(%rip), %rdx
	movq	%rax, %rcx
	callq	fprintf
	jmp	.LBB5_5
.LBB5_3:                                # %if.end
	movq	40(%rsp), %rax
	movq	56(%rsp), %rcx
	movq	%rax, (%rcx)
	movq	48(%rsp), %rax
	movq	56(%rsp), %rcx
	movq	%rax, 16(%rcx)
.LBB5_4:                                # %if.end9
	movq	64(%rsp), %rax
	movq	56(%rsp), %rcx
	movq	(%rcx), %rcx
	movq	56(%rsp), %rdx
	movq	8(%rdx), %r8
	movq	%r8, %r9
	addq	$1, %r9
	movq	%r9, 8(%rdx)
	movq	%rax, (%rcx,%r8,8)
.LBB5_5:                                # %return
	addq	$72, %rsp
	retq
	.seh_handlerdata
	.text
	.seh_endproc
                                        # -- End function
	.def	 fprintf;
	.scl	2;
	.type	32;
	.endef
	.section	.text,"xr",discard,fprintf
	.globl	fprintf                         # -- Begin function fprintf
	.p2align	4, 0x90
fprintf:                                # @fprintf
.seh_proc fprintf
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
	callq	_vfprintf_l
	movl	%eax, 52(%rsp)
	movl	52(%rsp), %eax
	addq	$72, %rsp
	retq
	.seh_handlerdata
	.section	.text,"xr",discard,fprintf
	.seh_endproc
                                        # -- End function
	.def	 getItem;
	.scl	2;
	.type	32;
	.endef
	.text
	.globl	getItem                         # -- Begin function getItem
	.p2align	4, 0x90
getItem:                                # @getItem
.seh_proc getItem
# %bb.0:                                # %entry
	subq	$56, %rsp
	.seh_stackalloc 56
	.seh_endprologue
	movq	%rdx, 40(%rsp)
	movq	%rcx, 32(%rsp)
	movq	40(%rsp), %rax
	movq	32(%rsp), %rcx
	cmpq	8(%rcx), %rax
	jb	.LBB7_2
# %bb.1:                                # %if.then
	movl	$2, %ecx
	callq	__acrt_iob_func
	leaq	"??_C@_0BF@NPGDBAHL@Index?5out?5of?5bounds?6?$AA@"(%rip), %rdx
	movq	%rax, %rcx
	callq	fprintf
	movq	$0, 48(%rsp)
	jmp	.LBB7_3
.LBB7_2:                                # %if.end
	movq	32(%rsp), %rax
	movq	(%rax), %rax
	movq	40(%rsp), %rcx
	movq	(%rax,%rcx,8), %rax
	movq	%rax, 48(%rsp)
.LBB7_3:                                # %return
	movq	48(%rsp), %rax
	addq	$56, %rsp
	retq
	.seh_handlerdata
	.text
	.seh_endproc
                                        # -- End function
	.def	 removeItem;
	.scl	2;
	.type	32;
	.endef
	.globl	removeItem                      # -- Begin function removeItem
	.p2align	4, 0x90
removeItem:                             # @removeItem
.seh_proc removeItem
# %bb.0:                                # %entry
	subq	$56, %rsp
	.seh_stackalloc 56
	.seh_endprologue
	movq	%rdx, 48(%rsp)
	movq	%rcx, 40(%rsp)
	movq	48(%rsp), %rax
	movq	40(%rsp), %rcx
	cmpq	8(%rcx), %rax
	jb	.LBB8_2
# %bb.1:                                # %if.then
	movl	$2, %ecx
	callq	__acrt_iob_func
	leaq	"??_C@_0BE@NIFNAHE@Position?5not?5found?6?$AA@"(%rip), %rdx
	movq	%rax, %rcx
	callq	fprintf
	jmp	.LBB8_7
.LBB8_2:                                # %if.end
	movq	40(%rsp), %rax
	movq	(%rax), %rax
	movq	48(%rsp), %rcx
	movq	(%rax,%rcx,8), %rcx
	callq	free
	movq	48(%rsp), %rax
	movq	%rax, 32(%rsp)
.LBB8_3:                                # %for.cond
                                        # =>This Inner Loop Header: Depth=1
	movq	32(%rsp), %rax
	movq	40(%rsp), %rcx
	movq	8(%rcx), %rcx
	subq	$1, %rcx
	cmpq	%rcx, %rax
	jae	.LBB8_6
# %bb.4:                                # %for.body
                                        #   in Loop: Header=BB8_3 Depth=1
	movq	40(%rsp), %rax
	movq	(%rax), %rax
	movq	32(%rsp), %rcx
	movq	8(%rax,%rcx,8), %rax
	movq	40(%rsp), %rcx
	movq	(%rcx), %rcx
	movq	32(%rsp), %rdx
	movq	%rax, (%rcx,%rdx,8)
# %bb.5:                                # %for.inc
                                        #   in Loop: Header=BB8_3 Depth=1
	movq	32(%rsp), %rax
	addq	$1, %rax
	movq	%rax, 32(%rsp)
	jmp	.LBB8_3
.LBB8_6:                                # %for.end
	movq	40(%rsp), %rax
	movq	8(%rax), %rcx
	addq	$-1, %rcx
	movq	%rcx, 8(%rax)
.LBB8_7:                                # %return
	addq	$56, %rsp
	retq
	.seh_handlerdata
	.text
	.seh_endproc
                                        # -- End function
	.def	 clearList;
	.scl	2;
	.type	32;
	.endef
	.globl	clearList                       # -- Begin function clearList
	.p2align	4, 0x90
clearList:                              # @clearList
.seh_proc clearList
# %bb.0:                                # %entry
	subq	$56, %rsp
	.seh_stackalloc 56
	.seh_endprologue
	movq	%rcx, 48(%rsp)
	cmpq	$0, 48(%rsp)
	jne	.LBB9_2
# %bb.1:                                # %if.then
	jmp	.LBB9_9
.LBB9_2:                                # %if.end
	movq	$0, 40(%rsp)
.LBB9_3:                                # %for.cond
                                        # =>This Inner Loop Header: Depth=1
	movq	40(%rsp), %rax
	movq	48(%rsp), %rcx
	cmpq	8(%rcx), %rax
	jae	.LBB9_8
# %bb.4:                                # %for.body
                                        #   in Loop: Header=BB9_3 Depth=1
	movq	48(%rsp), %rax
	movq	(%rax), %rax
	movq	40(%rsp), %rcx
	movq	(%rax,%rcx,8), %rax
	movq	%rax, 32(%rsp)
	cmpq	$0, 32(%rsp)
	je	.LBB9_6
# %bb.5:                                # %if.then2
                                        #   in Loop: Header=BB9_3 Depth=1
	movq	32(%rsp), %rax
	movq	8(%rax), %rcx
	callq	free
	movq	32(%rsp), %rax
	movq	%rax, %rcx
	callq	free
.LBB9_6:                                # %if.end3
                                        #   in Loop: Header=BB9_3 Depth=1
	jmp	.LBB9_7
.LBB9_7:                                # %for.inc
                                        #   in Loop: Header=BB9_3 Depth=1
	movq	40(%rsp), %rax
	addq	$1, %rax
	movq	%rax, 40(%rsp)
	jmp	.LBB9_3
.LBB9_8:                                # %for.end
	movq	48(%rsp), %rax
	movq	$0, 8(%rax)
.LBB9_9:                                # %return
	addq	$56, %rsp
	retq
	.seh_handlerdata
	.text
	.seh_endproc
                                        # -- End function
	.def	 size;
	.scl	2;
	.type	32;
	.endef
	.globl	size                            # -- Begin function size
	.p2align	4, 0x90
size:                                   # @size
.seh_proc size
# %bb.0:                                # %entry
	subq	$16, %rsp
	.seh_stackalloc 16
	.seh_endprologue
	movq	%rcx, (%rsp)
	cmpq	$0, (%rsp)
	jne	.LBB10_2
# %bb.1:                                # %if.then
	movq	$0, 8(%rsp)
	jmp	.LBB10_3
.LBB10_2:                               # %if.end
	movq	(%rsp), %rax
	movq	8(%rax), %rax
                                        # kill: def $eax killed $eax killed $rax
	cltq
	movq	%rax, 8(%rsp)
.LBB10_3:                               # %return
	movq	8(%rsp), %rax
	addq	$16, %rsp
	retq
	.seh_handlerdata
	.text
	.seh_endproc
                                        # -- End function
	.def	 freeList;
	.scl	2;
	.type	32;
	.endef
	.globl	freeList                        # -- Begin function freeList
	.p2align	4, 0x90
freeList:                               # @freeList
.seh_proc freeList
# %bb.0:                                # %entry
	subq	$56, %rsp
	.seh_stackalloc 56
	.seh_endprologue
	movq	%rcx, 48(%rsp)
	cmpq	$0, 48(%rsp)
	jne	.LBB11_2
# %bb.1:                                # %if.then
	jmp	.LBB11_9
.LBB11_2:                               # %if.end
	movq	$0, 40(%rsp)
.LBB11_3:                               # %for.cond
                                        # =>This Inner Loop Header: Depth=1
	movq	40(%rsp), %rax
	movq	48(%rsp), %rcx
	cmpq	8(%rcx), %rax
	jae	.LBB11_8
# %bb.4:                                # %for.body
                                        #   in Loop: Header=BB11_3 Depth=1
	movq	48(%rsp), %rax
	movq	(%rax), %rax
	movq	40(%rsp), %rcx
	movq	(%rax,%rcx,8), %rax
	movq	%rax, 32(%rsp)
	cmpq	$0, 32(%rsp)
	je	.LBB11_6
# %bb.5:                                # %if.then2
                                        #   in Loop: Header=BB11_3 Depth=1
	movq	32(%rsp), %rax
	movq	8(%rax), %rcx
	callq	free
	movq	32(%rsp), %rax
	movq	%rax, %rcx
	callq	free
.LBB11_6:                               # %if.end3
                                        #   in Loop: Header=BB11_3 Depth=1
	jmp	.LBB11_7
.LBB11_7:                               # %for.inc
                                        #   in Loop: Header=BB11_3 Depth=1
	movq	40(%rsp), %rax
	addq	$1, %rax
	movq	%rax, 40(%rsp)
	jmp	.LBB11_3
.LBB11_8:                               # %for.end
	movq	48(%rsp), %rax
	movq	(%rax), %rax
	movq	%rax, %rcx
	callq	free
	movq	48(%rsp), %rax
	movq	%rax, %rcx
	callq	free
.LBB11_9:                               # %return
	nop
	addq	$56, %rsp
	retq
	.seh_handlerdata
	.text
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
	jge	.LBB13_2
# %bb.1:                                # %cond.true
	movl	$4294967295, %eax               # imm = 0xFFFFFFFF
	movl	%eax, 60(%rsp)                  # 4-byte Spill
	jmp	.LBB13_3
.LBB13_2:                               # %cond.false
	movl	116(%rsp), %eax
	movl	%eax, 60(%rsp)                  # 4-byte Spill
.LBB13_3:                               # %cond.end
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
	.section	.rdata,"dr",discard,"??_C@_0BH@FBJAHFHE@Falha?5ao?5salvar?5array?6?$AA@"
	.globl	"??_C@_0BH@FBJAHFHE@Falha?5ao?5salvar?5array?6?$AA@" # @"??_C@_0BH@FBJAHFHE@Falha?5ao?5salvar?5array?6?$AA@"
"??_C@_0BH@FBJAHFHE@Falha?5ao?5salvar?5array?6?$AA@":
	.asciz	"Falha ao salvar array\n"

	.section	.rdata,"dr",discard,"??_C@_0BF@NPGDBAHL@Index?5out?5of?5bounds?6?$AA@"
	.globl	"??_C@_0BF@NPGDBAHL@Index?5out?5of?5bounds?6?$AA@" # @"??_C@_0BF@NPGDBAHL@Index?5out?5of?5bounds?6?$AA@"
"??_C@_0BF@NPGDBAHL@Index?5out?5of?5bounds?6?$AA@":
	.asciz	"Index out of bounds\n"

	.section	.rdata,"dr",discard,"??_C@_0BE@NIFNAHE@Position?5not?5found?6?$AA@"
	.globl	"??_C@_0BE@NIFNAHE@Position?5not?5found?6?$AA@" # @"??_C@_0BE@NIFNAHE@Position?5not?5found?6?$AA@"
"??_C@_0BE@NIFNAHE@Position?5not?5found?6?$AA@":
	.asciz	"Position not found\n"

	.lcomm	__local_stdio_printf_options._OptionsStorage,8,8 # @__local_stdio_printf_options._OptionsStorage
	.addrsig
	.addrsig_sym _vsnprintf
	.addrsig_sym malloc
	.addrsig_sym free
	.addrsig_sym realloc
	.addrsig_sym fprintf
	.addrsig_sym __acrt_iob_func
	.addrsig_sym _vsprintf_l
	.addrsig_sym _vsnprintf_l
	.addrsig_sym __stdio_common_vsprintf
	.addrsig_sym __local_stdio_printf_options
	.addrsig_sym _vfprintf_l
	.addrsig_sym __stdio_common_vfprintf
	.addrsig_sym __local_stdio_printf_options._OptionsStorage
