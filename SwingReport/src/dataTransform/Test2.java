package dataTransform;

public class Test2 {
	
	int v = 7;
	public static void main(String args[]){
		Test2 t= new Test2();
		t.test();
		
	}
	
	public void test(){
		Bean b1 = new Bean();
		trytoChang(b1);
		System.out.println(b1);
		
		int x = 3;
		tryToChangeX(x);
		System.out.println(x);
		
		Bean b2 = new Bean();
		b2.a = v;
		v = 9;
		System.out.println(b2);
		
	}
	
	public void trytoChang(Bean b5){
		b5.setA(100);
		b5.setB(1000);
	}
	
	public void tryToChangeX(int y){
		y = 1000;
	}

}
class Bean {
	public int a = 3;
	public int b = 4;
	
	public int getA() {
		return a;
	}
	public void setA(int a) {
		this.a = a;
	}
	public int getB() {
		return b;
	}
	public void setB(int b) {
		this.b = b;
	}
	@Override
	public String toString() {
		String s = "a: "+a+" b: "+b; 
		return s;
	}
}