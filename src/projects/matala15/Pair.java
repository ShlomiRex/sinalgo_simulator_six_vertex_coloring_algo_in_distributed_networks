package projects.matala15;

public class Pair <T1, T2>{
	final private T1 a;
	final private T2 b;
	
	public Pair(T1 a, T2 b) {
		this.a = a;
		this.b = b;
	}
	
	public T1 getA() {
		return a;
	}
	
	public T2 getB() {
		return b;
	}
}
