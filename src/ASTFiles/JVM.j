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

	istore_2
	istore_3
	iload_2
	ifeq Label1
	iconst_0
	istore_1
	goto Label2
Label1:
	iconst_3
	istore_1
Label2:
	ireturn
.end method

.method public static main([Ljava/lang/String;)V
	.limit stack 0
	.limit locals 0

	return
.end method

