package dataTransform;

import java.util.Arrays;
import java.util.HashSet;

public class Test {
	public static void main(String args[]){
		String[] s1 = new String[]{"a","b"};
		String[] s2 = new String[]{"c","b"};
		System.out.println(s1.hashCode());
		System.out.println(s2.hashCode());
		System.out.println(Arrays.hashCode(s1));
		
		HashSet<String> s = new HashSet<String>();
		System.out.println(s.add("a"));
		System.out.println(s.add("b"));
		System.out.println(s.add("a"));
	}

}
