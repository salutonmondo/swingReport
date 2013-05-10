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
		
		String a = "1";
		String b = "2";
		String c = "78000";
		String d = "°¡µê";
		
		
		System.out.println(a.getBytes());
		for(byte x:a.getBytes()){
			System.out.println(x);
		}
		System.out.println("--------------");
		for(byte x:b.getBytes()){
			System.out.println(x);
		}
		System.out.println("--------------");
		for(byte x:c.getBytes()){
			System.out.println(x);
		}
//		System.out.println("--------------");
//		for(byte x:d.getBytes()){
//			System.out.println(x);
//		}
		
		System.out.println(compareArray(a.getBytes(),b.getBytes()));
		System.out.println(compareArray(a.getBytes(),c.getBytes()));
		System.out.println(compareArray(a.getBytes(),d.getBytes()));
		
	}

	public static  int compareArray(byte[] b1,byte[] b2){
		int length = Math.min(b1.length, b2.length);
		for(int i=0;i<length;i++){
			if(b1[i]<0||b2[i]<0){
				if(b1[i]>0)
					b1[i]=(byte) -b1[i];
				if(b2[i]>0)
					b2[i]=(byte) -b2[i];	
			}
			if(b1[i]>b2[i])
				return -1;
			else if(b1[i]<b2[i])
				return 1;
			if(i==length-1){
				if(b1.length>b2.length)
					return -1;
				else
				    return 1;
			}
		}
		return 0;
	}
}
