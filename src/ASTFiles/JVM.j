.class public MonteCarloPi
.super java/lang/Object

; default constructor
.method public <init>()V
	aload_0
	invokespecial java/lang/Object/<init>()V
	return
.end method

.method public performSingleEstimate()Z
	.limit stack 32
	.limit locals 5

	ldc 0
	ldc 100
	isub
	istore 1
	ldc 0
	ldc 100
	isub
	istore 2
	iload 1
	iload 1
	imul
	iload 2
	iload 2
	imul
	iadd
	ldc 100
	idiv
	istore 4
	iload 4
	ldc 100
	if_icmpge Label1
	ldc 1
	istore 3
	goto Label2
Label1:
	ldc 0
	istore 3
Label2:
	iload 3
	ireturn
.end method

.method public estimatePi100(I)I
	.limit stack 32
	.limit locals 5

	ldc 0
	istore 3
	ldc 0
	istore 2
Label4:
	iload 3
	iload 1
	if_icmpge Label3
	aload 0			; Method performSingleEstimate() call
	invokevirtual MonteCarloPi/performSingleEstimate()Z
	ifeq Label5
	iload 2
	ldc 1
	iadd
	istore 2
	goto Label6
Label5:
Label6:
	iload 3
	ldc 1
	iadd
	istore 3
	goto Label4
Label3:
	ldc 400
	iload 2
	imul
	iload 1
	idiv
	istore 4
	iload 4
	ireturn
.end method

.method public static main([Ljava/lang/String;)V
	.limit stack 32
	.limit locals 2

	return
.end method

