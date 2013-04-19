package dataTransform;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

public class TransForm {

	ArrayList<Integer> rowItem = new ArrayList<Integer>();
	ArrayList<Integer> colItem = new ArrayList<Integer>();
	ArrayList<Integer> dataItem = new ArrayList<Integer>();
	String[] oriHead;
	String[][] oriData;
	String[] resultHead;
	ArrayList<String[]> resultData;
	
	int columnSetLength;
	

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
//		list2.add(3);
		ArrayList<Integer> list3 = new ArrayList<Integer>();
		list3.add(3);
		list3.add(2);
		
		
		t.addColtems(list);
		t.addRowItems(list2);
		t.addDataItems(list3);
		
		HeadGroup p = t.construct();
		t.trans(p);
	}

	public void trans(HeadGroup p) {		
		//match and transform
		HashMap<String,ColItemWrapper> collectorMap= new HashMap<String,ColItemWrapper>();
		for(int k=0;k<this.oriData.length;k++){
			StringBuilder sb = new StringBuilder();
			StringBuilder sb2 = new StringBuilder();
			String[] rowIltems = new String[this.rowItem.size()];
			int rowItemIndex = 0;
			for(int index:this.colItem){
				sb.append(oriData[k][index]);
			}
			for(int index:this.rowItem){
				sb2.append(oriData[k][index]);
				rowIltems[rowItemIndex] = oriData[k][index];
				rowItemIndex++;
			}
			ColItemWrapper c=collectorMap.get(sb2.toString());
			if(c==null){
				c = new ColItemWrapper(rowIltems,columnSetLength,sb.toString(), p,this.oriData[k]);
				collectorMap.put(sb2.toString(), c);
			}
			else{
				c.add(sb.toString(), this.oriData[k]);
				}
		}
		
//		//for Test Only
		for(Map.Entry<String,ColItemWrapper> ent:collectorMap.entrySet()){
			
			for(String[] s:ent.getValue().list){
				for(String t:ent.getValue().rowItems){
					System.out.print(t+"\t");
				}
				for(String s1:s){
					System.out.print(s1+"\t");
				}
			System.out.println();
			}
		}
		
	}

	public HeadGroup construct() {
		LinkedHashMap<Integer, TreeSet<String>> hash = new LinkedHashMap<Integer, TreeSet<String>>();
		for (int c : colItem) {
			TreeSet<String> set = new TreeSet<String>();
			hash.put(c, set);
		}

		for (int i = 0; i < oriData.length; i++) {
			for (Map.Entry<Integer, TreeSet<String>> ent : hash.entrySet()) {
				ent.getValue().add(oriData[i][ent.getKey()]);
			}
		}

		/*
		 * for test only
		 */
		for (Map.Entry<Integer, TreeSet<String>> ent : hash.entrySet()) {
			System.out.println(ent.getKey() + "____" + ent.getValue());
		}

		List<HeadGroup> last = new ArrayList<HeadGroup>();
		HeadGroup root = new HeadGroup("root");
		for (Map.Entry<Integer, TreeSet<String>> ent : hash.entrySet()) {
			TreeSet<String> set = ent.getValue();
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
		/*
		 * construct the result data
		 */
		int totalLength = (root.list.size()-1)*this.dataItem.size()+this.rowItem.size();
		this.resultHead = new String[totalLength];
		columnSetLength = (root.list.size()-1)*this.dataItem.size();
		System.out.println(resultHead.length);
		for(int i=0;i<resultHead.length;i++){
			
		}
		return root;
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
		LinkedHashMap<String, HeadGroup> hash = null;
		String value;
		LinkedHashMap<Integer, Integer> hash2 = null;
        List<HeadGroup> list = new ArrayList<HeadGroup>();
		public void addSelf(Object obj) {
			if (obj instanceof int[]) {
				if (hash2 == null)
					hash2 = new LinkedHashMap<Integer, Integer>();
				int[] array = (int[]) obj;
				hash2.put(array[0], array[1]);
			} else {
				if (hash == null)
					hash = new LinkedHashMap<String, HeadGroup>();
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
	
	class ColItemWrapper {
		HashMap<String,Integer> map = new HashMap<String,Integer>();
		ArrayList<String[]> list= new ArrayList<String[]>();
		String[] rowItems;
		HeadGroup p;
		int count;
		int length ;
		public ColItemWrapper(String[] rowItems,int length,String colItemName,HeadGroup p,String[] dataArray){
		    this.length = length;
			map.put(colItemName, 1);
			count = 1;
			this.p = p;
			fillArray(null,dataArray);
			this.rowItems = rowItems;
		} 
		public void add(String colItemName,String[] dataArray){
	        int itemCount = map.get(colItemName)==null?-1:map.get(colItemName);
			if(itemCount<count){
				String[] target = list.get(itemCount+1);
				fillArray(target,dataArray);
			}
			if(itemCount==count){
				count++;
				fillArray(null,dataArray);
			}
		}
		
		public void fillArray(String[] target,String[] dataArray){
			boolean shouldAdd= false;
			if(target==null){
				target = new String[this.length];
				shouldAdd = true;
			}
			for(int colIndex =0;colIndex<colItem.size();colIndex++){
				String lookKey = dataArray[colItem.get(colIndex)];
				HeadGroup tmpGroup = p.hash.get(lookKey);
				if(tmpGroup.hash==null){
					for(Map.Entry<Integer, Integer> ent:tmpGroup.hash2.entrySet()){
						target[ent.getValue()] =dataArray[ent.getKey()];
//						System.out.println(dataArray[ent.getKey()]);
					}
				}
			}
			if(shouldAdd)
				this.list.add(target);
		}
	}
	
}
