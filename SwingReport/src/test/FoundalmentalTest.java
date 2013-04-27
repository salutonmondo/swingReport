package test;

import java.util.HashSet;
import java.util.TreeSet;

import dataTransform.SimpleWraper;

public class FoundalmentalTest {
	public static void main(String args[]){
		TreeSet<String> t = new TreeSet<String>();
		t.add("上海");
		t.add("北京");
		for(String s:t){
			System.out.println(s);
		}
		
		HashSet<SimpleWraper> hash = new HashSet<SimpleWraper>();
		hash.add(new SimpleWraper(new String[]{"a","b"}));
		hash.add(new SimpleWraper(new String[]{"a","b"}));
		System.out.println(hash.size());
		
		HashSet<String> hash2 = new HashSet<String>();
		hash2.add("a");
		hash2.add("a");
		System.out.println(hash2.size());
		
		HashSet<StringBuilder> hash3 = new HashSet<StringBuilder>();
		hash3.add(new StringBuilder("a"));
		System.out.println(hash3.add(new StringBuilder("a")));
		System.out.println(hash2.size());
		
		HashSet<String> hash4 = new HashSet<String>();
		hash4.add("a");
		System.out.println(hash4.add("a"));
		System.out.println(hash4.size());
		
		
	}

}
