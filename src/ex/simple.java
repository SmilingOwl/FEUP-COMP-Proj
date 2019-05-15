class Simple {
    int e;

    public int test(int maxSpeed,boolean s){

        int a;
        a = 1+2+3;
        

        return a;
    }
	
	public int[] test2(){
		int[] x;
		x = new int[5];
		x[2] = 123; 
		e = x[2];
		int len;
		len = x.length;
		
        return x;	
    }
	
	public boolean false_return() {
		boolean a;
		a = false;
        return a;
    }

    public static void main(String[] arg){
		e = 1;
		this.test(e,false);
		this.test2();
	}
}
