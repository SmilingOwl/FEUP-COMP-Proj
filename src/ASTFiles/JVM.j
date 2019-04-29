.class public MonteCarloPi
.super java/lang/Object

; default constructor
.method public <init>()V
	aload_0
	invokespecial java/lang/Object/<init>()V
	return
.end method

.method public performSingleEstimate()Z
	.limit stack 0
	.limit locals 4

random()/	iconst_0
	iconst_100
	isub
	istore_1
random()/	iconst_0
	iconst_100
	isub
	istore_2
	iload_1
	iload_1
	imult
	iload_2
	iload_2
	imult
	iadd
	iconst_+
	iconst_100
	idiv
	istore_4
	istore_3
	istore_3
	ireturn
.end method

.method public estimatePi100(I)I
	.limit stack 0
	.limit locals 3

	iconst_0
	istore_6
	iconst_0
	istore_5
performSingleEstimate()/	iload_5
	iconst_1
	iadd
	istore_5
	iload_6
	iconst_1
	iadd
	istore_6
	iconst_400
	iload_5
	imult
	iload_-1
	idiv
	istore_7
	ireturn
.end method

.method public static main([Ljava/lang/String;)V
	.limit stack 0
	.limit locals 2

requestNumber()/	istore_9
	new MonteCarloPi
estimatePi100()/	iconst_
	istore_8
	invokevirtual ioPlus/printResult()
	return
.end method

