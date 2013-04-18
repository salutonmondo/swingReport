package dataTransform;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class TransForm {

	ArrayList<Integer> rowItem = new ArrayList<Integer>();
	ArrayList<Integer> colItem = new ArrayList<Integer>();
	ArrayList<Integer> dataItem = new ArrayList<Integer>();
	String[] oriHead;
	String[][] oriData;
	String[] resultHead;
	String[][] resultData;

	public static void main(String args[]) {
		TransForm t = new TransForm(new String[] { "月份", "店铺", "销售", "商品" },
				new String[][] { { "1", "上海店", "800", "A05" },
						{ "1", "上海店", "900", "A04" },
						{ "2", "上海店", "900", "A05" },
						{ "1", "北京店", "400", "A05" },
						{ "2", "北京店", "300", "A05" } });

		ArrayList<Integer> list = new ArrayList<Integer>();
//		list.add(0);
		list.add(1);
//		list.add(3);
		ArrayList<Integer> list2 = new ArrayList<Integer>();
		list2.add(0);
		
		ArrayList<Integer> list3 = new ArrayList<Integer>();
		list3.add(2);
		list3.add(3);
		
		t.addColtems(list);
		t.addRowItems(list2);
		t.addDataItems(list3);
		
		t.construct();
	}

	public void trans() {
		
	}

	public void construct() {
		HashMap<Integer, HashSet<String>> hash = new HashMap<Integer, HashSet<String>>();
		for (int c : colItem) {
			HashSet<String> set = new HashSet<String>();
			hash.put(c, set);
		}

		for (int i = 0; i < oriData.length; i++) {
			for (Map.Entry<Integer, HashSet<String>> ent : hash.entrySet()) {
				ent.getValue().add(oriData[i][ent.getKey()]);
			}
		}

		/*
		 * for test only
		 */
		for (Map.Entry<Integer, HashSet<String>> ent : hash.entrySet()) {
			System.out.println(ent.getKey() + "____" + ent.getValue());
		}

		List<HeadGroup> last = new ArrayList<HeadGroup>();
		HeadGroup root = new HeadGroup("root");
		for (Map.Entry<Integer, HashSet<String>> ent : hash.entrySet()) {
			HashSet<String> set = ent.getValue();
			if (last.size() == 0) {
				for (String s : set) {
					HeadGroup p = new HeadGroup(s);
					last.add(p);
					root.addSelf(p);
				}
			} else {
				List<HeadGroup> tmp = new ArrayList<HeadGroup>();
				for (HeadGroup p2 : last) {
					for (String s2 : set) {
						HeadGroup p3 = new HeadGroup(s2);
						p2.addSelf(p3);
						tmp.add(p3);
					}
				}
				last = tmp;
				tmp = null;
			}
		}
		
		int resultArrayPosition = 0;
		for(HeadGroup p5:last){
			for(int position:this.dataItem){
				int[] pos = new int[]{position,resultArrayPosition};
				p5.addSelf(pos);
				resultArrayPosition++;
			}
		}
		root.next(root);
		/*
		 * for test only
		 */
		for(HeadGroup p:root.list){
			System.out.println(p);
		}
		
		this.resultHead = new String[root.list.size()*this.dataItem.size()+this.rowItem.size()];
		System.out.println(resultHead.length);
		for(int i=0;i<resultHead.length;i++){
			
		}
	}

	public void addRowItems(List<Integer> rowList) {
		rowItem.addAll(rowList);
	}

	public void addColtems(List<Integer> colList) {
		colItem.addAll(colList);
	}

	public void addDataItems(List<Integer> dataList) {
		dataItem.addAll(dataList);
	}

	public TransForm(String[] oriHead, String[][] oriData) {
		this.oriData = oriData;
		this.oriHead = oriHead;
	}

	class HeadGroup {
		HashMap<String, HeadGroup> hash = null;
		String value;
		HashMap<Integer, Integer> hash2 = null;
        List<HeadGroup> list = new ArrayList<HeadGroup>();
		public void addSelf(Object obj) {
			if (obj instanceof int[]) {
				if (hash2 == null)
					hash2 = new HashMap<Integer, Integer>();
				int[] array = (int[]) obj;
				hash2.put(array[0], array[1]);
			} else {
				if (hash == null)
					hash = new HashMap<String, HeadGroup>();
				HeadGroup g = (HeadGroup) obj;
				hash.put(g.getValue(), g);
			}
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}

		public HeadGroup(String value) {
			this.value = value;
		}

		public void next(HeadGroup p) {
			if (p.hash != null) {
//				System.out.println(p.value);
				list.add(p);
				for (Map.Entry<String, HeadGroup> ent : p.hash.entrySet()) {
					next(ent.getValue());
				}
			}
			else{
//				System.out.println(p.value);
				list.add(p);
			}
		}

		@Override
		public String toString() {
			String leaf="";
			if(hash2!=null){
				for(Map.Entry<Integer, Integer> ent:hash2.entrySet()){
					leaf = leaf+ent.getKey()+"_"+ent.getValue()+" ";
				}
			return value+"     "+leaf;
			}
			return this.value;
		}
	}
}
