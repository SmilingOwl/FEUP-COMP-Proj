.class public Quicksort
.super java/lang/Object

; default constructor
.method public <init>()V
	aload_0
	invokespecial java/lang/Object/<init>()V
	return
.end method

.method public static main([Ljava/lang/String;)V
	.limit stack 9
	.limit locals 4

	bipush 10
	newarray int
	astore_1
	iconst_0
	istore_2
Label2:
	iload_2
	aload_1
	arraylength
	if_icmpge Label1
	aload 1
	iload_2
	aload_1
	arraylength
	iload_2
	isub
	iastore
	iload_2
	iconst_1
	iadd
	istore_2
	goto Label2
Label1:
	new Quicksort
	dup
	invokenonvirtual Quicksort/<init>()V
	astore_3
	aload_3
	aload_1
	invokevirtual Quicksort/quicksort([I)V
	aload_3
	aload_1
	invokevirtual Quicksort/printL([I)V
	return
.end method

.method public printL([I)Z
	.limit stack 3
	.limit locals 3

	iconst_0
	istore_2
Label4:
	iload_2
	aload_1
	arraylength
	if_icmpge Label3
	invokestatic io/println(I)V
	iload_2
	iconst_1
	iadd
	istore_2
	goto Label4
Label3:
	iconst_1
	ireturn
.end method

.method public quicksort([I)Z
	.limit stack 3
	.limit locals 2

	aload 0			; Method quicksort() call
	aload_1
	iconst_0
	invokevirtual Quicksort/quicksort([I)Z
	aload_1
	arraylength
	iconst_1
	isub
	iconst_0
	ireturn
.end method

.method public quicksort([III)Z
	.limit stack 17
	.limit locals 5

	iload_2
	iload_3
	if_icmpge Label5
	aload 0			; Method partition() call
	aload_1
	iload_2
	iload_3
	invokevirtual Quicksort/partition([III)I
	aload_1
	iload_2
	iload_3
	invokestatic Quicksort/partition([III)I
	istore 4
	aload 0			; Method quicksort() call
	aload_1
	iload_2
	invokevirtual Quicksort/quicksort([I)Z
	iload 4
	iconst_1
	isub
	aload 0			; Method quicksort() call
	aload_1
	iload_3
	invokevirtual Quicksort/quicksort([I)Z
	iload 4
	iconst_1
	iadd
	goto Label6
Label5:
Label6:
	iconst_1
	ireturn
.end method

.method public partition([III)I
	.limit stack 29
	.limit locals 8

	aload 1
	iload_3
	iaload
	istore 4
	iload_2
	istore 5
	iload_2
	istore 6
Label8:
	iload 6
	iload_3
	if_icmpge Label7
	iload 4
	if_icmpge Label9
	aload 1
	iload 5
	iaload
	istore 7
	aload 1
	iload 5
	aload_1
	iastore
	aload 1
	iload 6
	iload 7
	iastore
	iload 5
	iconst_1
	iadd
	istore 5
	goto Label10
Label9:
Label10:
	iload 6
	iconst_1
	iadd
	istore 6
	goto Label8
Label7:
	aload 1
	iload 5
	iaload
	istore 7
	aload 1
	iload 5
	aload_1
	iastore
	aload 1
	iload_3
	iload 7
	iastore
	iload 5
	ireturn
.end method

