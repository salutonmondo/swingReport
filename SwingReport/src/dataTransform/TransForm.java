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
	String[][] resultsData;
	HeadGroup rowG;
	HeadGroup colG;
	int columnSetLength;
	

	public static void main(String args[]) {
		TransForm t = new TransForm(new String[] { "月份", "店铺", "销售", "商品" },
				new String[][] { { "1", "上海店", "800", "A05" },
						{ "1", "上海店", "900", "A04" },
						{ "2", "上海店", "900", "A05" },
						{ "1", "北京店", "400", "A05" },
						{ "2", "北京店", "300", "A05" }});

		ArrayList<Integer> list = new ArrayList<Integer>();
//		list.add(0);
		list.add(1);
		
		list.add(3);
		ArrayList<Integer> list2 = new ArrayList<Integer>();
		list2.add(0);
//		list2.add(1);
//		list2.add(2);
//		list2.add(3);
		ArrayList<Integer> list3 = new ArrayList<Integer>();
//		list3.add(3);
		list3.add(2);
//		list3.add(1);
//		list3.add(0);
//		
		
		t.setColtems(list);
		t.setRowItem(list2);
		t.setDataItem(list3);
		
//		t.construct(list);
		t.trans();
		
		for(String s:t.resultHead){
			System.out.print(s+"\t");
		}
		
		System.out.println();
		System.out.println();
		
		for(int i=0;i<t.resultsData.length;i++){
			for(int j=0;j<t.resultsData[i].length;j++){
				System.out.print(t.resultsData[i][j]+"\t");
			}
			System.out.println();
		}
	}

	private void setColtems(ArrayList<Integer> list) {
		this.colItem = list;
		
	}

	public void trans() {
		if(colItem.size()==0||rowItem.size()==0){
			this.resultsData = this.oriData;
			this.resultHead = this.oriHead;
			return ;
		}
		colG = construct(colItem,0);
		rowG = construct(rowItem,1);
		resultsData = new String[rowG.degree][colG.degree*this.dataItem.size()];
		String[] rowNavi = new String[rowG.height];
		String[] colNavi = new String[colG.height];
		String[] dataNavi = new String[this.dataItem.size()];
		for(int k=0;k<this.oriData.length;k++){
			
			int rowCount = 0;
			for(int rowNo: rowItem){
				rowNavi[rowCount] = oriData[k][rowNo];
				rowCount++;
			}
			
			int colCount = 0;
			for(int colNo: colItem){
				colNavi[colCount] = oriData[k][colNo];
				colCount++;
			}

			for(int h=0;h<this.dataItem.size();h++){
				HeadGroup tem = rowG;
				HeadGroup tem1 = colG;
				int r=0,c=0;
				int index = this.dataItem.get(h);
				String content = this.oriData[k][index];
				for(String nar:rowNavi){
					tem = tem.hash.get(nar);
					if(tem.hash2!=null){
						r = tem.hash2.get(index);
					}
					
				}
				
				for(String nac:colNavi){
					tem1 = tem1.hash.get(nac);
					if(tem1.hash2!=null){
						c = tem1.hash2.get(index);
					}
					
				}
				this.resultsData[r][c]=content;
			}
		}		
	}
	


	public HeadGroup construct(List<Integer> items,int type) {
		LinkedHashMap<Integer, TreeSet<String>> hash = new LinkedHashMap<Integer, TreeSet<String>>();
		HeadGroup root = new HeadGroup("root");
		if(items.size()==0){
			for(int i=0;i<dataItem.size();i++){
				int[] temp = new int[]{dataItem.get(i),i};
				root.addSelf(temp);
			}
		}
		else{
			for (int c : items) {
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
			root.height = hash.size();
			int resultArrayPosition0 = 0;
			int resultArrayPosition1 = 0;
			root.degree = last.size();
			if(type==0)
				this.resultHead = new String[root.degree*dataItem.size()];
			for(HeadGroup p5:last){
				for(int position:this.dataItem){
					int[] pos = new int[]{position,type==0?resultArrayPosition0:resultArrayPosition1};
					p5.addSelf(pos);
					if(type==0)
						this.resultHead[resultArrayPosition0] = this.oriHead[position];
					resultArrayPosition0++;
				}
				resultArrayPosition1++;
			}
		}
		root.next(root);
		/*
		 * for test only
		 */
//		for(HeadGroup p:root.list){
//			System.out.println(p);
//		}
		System.out.println("the degree of the group is: "+root.degree+"  height of the group is:"+root.height);
		System.out.println("thre result head is: ");
//		for(String s:this.resultHead){
//			System.out.print(s+"\t");
//		}
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
		for(int i=0;i<oriHead.length;i++){
			this.dataItem.add(i);
		}
	}

	public class HeadGroup {
		LinkedHashMap<String, HeadGroup> hash = null;
		String value;
		String parentName;
		LinkedHashMap<Integer, Integer> hash2 = null;
        List<HeadGroup> list = new ArrayList<HeadGroup>();
        int degree = 0;
        int height=0;
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
				g.setParentName(this.value);
				hash.put(g.getValue(), g);
			}
		}

		
		public String getParentName() {
			return parentName;
		}

		public void setParentName(String parentName) {
			this.parentName = parentName;
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

		
		public LinkedHashMap<String, HeadGroup> getHash() {
			return hash;
		}

		public LinkedHashMap<Integer, Integer> getHash2() {
			return hash2;
		}

		public void next(HeadGroup p) {
			if (p.hash != null) {
				list.add(p);
				for (Map.Entry<String, HeadGroup> ent : p.hash.entrySet()) {
					next(ent.getValue());
				}
			}
			else{
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

		public int getHeight() {
			return height;
		}
		
		public List<HeadGroup> getNext(List<HeadGroup> spreadList){
			List<HeadGroup> nextList = new ArrayList<HeadGroup>();
			for(HeadGroup p:spreadList){
				if(p.hash==null)
					return null;
				for(Map.Entry<String, HeadGroup> ent:p.hash.entrySet()){
					nextList.add(ent.getValue());
				}
			}
			return nextList;
		}

		public List<HeadGroup> getNextOne(HeadGroup p) {
			List<HeadGroup> nextList = new ArrayList<HeadGroup>();
			if (p.hash == null) {
				nextList = null;
			} else {
				for (Map.Entry<String, HeadGroup> ent : p.hash.entrySet()) {
					nextList.add(ent.getValue());
				}
			}
			return nextList;
		}
	}

	public String[] getOriHead() {
		return oriHead;
	}

	public String[][] getOriData() {
		return oriData;
	}

	public HeadGroup getRowG() {
		return rowG;
	}

	public void setRowG(HeadGroup rowG) {
		this.rowG = rowG;
	}

	public HeadGroup getColG() {
		return colG;
	}

	public void setColG(HeadGroup colG) {
		this.colG = colG;
	}

	public String[] getResultHead() {
		return resultHead;
	}

	public String[][] getResultsData() {
		return resultsData;
	}

	public ArrayList<Integer> getRowItem() {
		return rowItem;
	}

	public void setRowItem(ArrayList<Integer> rowItem) {
		this.rowItem = rowItem;
	}

	public ArrayList<Integer> getColItem() {
		return colItem;
	}

	public void setColItem(ArrayList<Integer> colItem) {
		this.colItem = colItem;
	}

	public ArrayList<Integer> getDataItem() {
		return dataItem;
	}

	public void setDataItem(ArrayList<Integer> dataItem) {
		this.dataItem = dataItem;
	}
	
	
	
}
