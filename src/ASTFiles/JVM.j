.class public TestIf
.super java/lang/Object

; default constructor
.method public <init>()V
	aload_0
	invokespecial java/lang/Object/<init>()V
	return
.end method

.method public test()I
	.limit stack 0
	.limit locals 3

	iconst_0
	istore_1
	iconst_1
	istore_2
	istore_3
Label2:
	iload_1
	iload_2
	if_icmpge Label1
	iconst_0
	istore_1
	iload_3
	ifeq Label3
	iconst_1
	istore_1
	goto Label4
Label3:
	iconst_2
	istore_1
Label4:
	goto Label2
Label1:
	ireturn
.end method

.method public static main([Ljava/lang/String;)V
	.limit stack 0
	.limit locals 0

	return
.end method

