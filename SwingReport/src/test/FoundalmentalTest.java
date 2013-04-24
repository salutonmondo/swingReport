package test;

import java.util.TreeSet;

public class FoundalmentalTest {
	public static void main(String args[]){
		TreeSet<String> t = new TreeSet<String>();
		t.add("上海");
		t.add("北京");
		for(String s:t){
			System.out.println(s);
		}
	}

}
