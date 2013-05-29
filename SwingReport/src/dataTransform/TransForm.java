package dataTransform;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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
	List<String> headList = new ArrayList<String>();
	HashSet<Integer> sumField;
	List<Integer> sumColumns = new ArrayList<Integer>();

	public static void main(String args[]) {
		HashSet<Integer> sumFields = new HashSet<Integer>();
		sumFields.add(2);
		TransForm t = new TransForm(new String[] { "月份", "店铺", "销售", "商品" },
				new String[][] { { "1", "上海店", "800", "A05" },
						{ "1", "上海店", "900", "A04" },
						{ "2", "上海店", "900", "A05" },
						{ "1", "北京店", "400", "A05" },
						{ "2", "北京店", "300", "A05" },
						{ "3", "北京店", "700", "A05" }},sumFields);

		ArrayList<Integer> list = new ArrayList<Integer>();
		list.add(0);
		list.add(1);
		
		
//		list.add(3);
		ArrayList<Integer> list2 = new ArrayList<Integer>();
		list2.add(3);
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
		
//		t.construct(list2, 0);
//		t.construct2(list2,0);
		t.trans();
		
		for(String s:t.resultHead){
			System.out.print(s+"\t");
		}
////		
//		System.out.println();
		System.out.println();
//		
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
		colG = construct2(colItem,0);
		rowG = construct2(rowItem,1);	
		generateArray();
	}
	
	public void generateArray(){
		resultsData = new String[rowG.degree][colG.degree];
		String[] rowNavi = new String[rowG.height];
		int lastRowIndex = this.rowItem.get(this.rowItem.size()-1);
		String[] colNavi = new String[colG.height];
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
						r = tem.hash2.get(lastRowIndex);
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
	
	public HeadGroup construct2(List<Integer> items,int type) {
		HashSet<String> navSet = new HashSet<String>();
		HeadGroup root = new HeadGroup("root");
		root.hash = new LinkedHashMap<String,HeadGroup>();
		boolean heightCaculated = false;
		for(String[] s:this.oriData){
			StringBuilder sb = new StringBuilder();
			for(int row:items){
				sb.append(s[row]);
			}
			
//			System.out.println(sb);
			if(navSet.add(sb.toString())){
				HeadGroup parent = root;
				for(int row:items){
					HeadGroup sub = new HeadGroup(s[row]);
					if(parent.hash ==null)
						parent.setHash(new LinkedHashMap<String,HeadGroup>());
					if(parent.hash.get(sub.getValue())==null){
						parent.addSelf(sub);
						if(!heightCaculated)
							height++;
						parent = parent.hash.get(sub.getValue());
					}
					else{
						parent = parent.hash.get(sub.getValue());
//						continue;
					}
				}
			};
			
			heightCaculated = true;
		}
		
		root.next(root);
		this.type = type;
		applySort(root,false,0,0);
		return root;
	}

	int height;
	public int type = 2;
	public void applySort(HeadGroup root,boolean isSort,int buttonIndex,int sortOrder){
		if(!isSort){
			defaultAllSort(root);
		}
		else{
			sort(root,buttonIndex,sortOrder);
		}
		
		
		if(this.type ==0){
			rearrangeIndex = 0;
			degree=0;
			this.headList.clear();
			this.sumColumns.clear();
		}
		
		
		this.rearrangeLeafies(root, type);
		if(isSort){
			if(this.type==1){
				this.rowG = root;
			}
			else{
				this.colG = root;
			}
			this.generateArray();
		}
		
		root.degree = degree;
		if(!isSort)
			root.height = height;
		System.out.println("the height is "+root.height+" the degree is "+root.degree);
		for(HeadGroup p:root.list){
			System.out.println(p);
		}
		if(type==0&&(!isSort)){
			this.resultHead = new String[degree];
			for(int i = 0;i<this.headList.size();i++){
				this.resultHead[i] = this.headList.get(i);
			}
		}
		degree=0;
		rearrangeIndex=0;
		if(!isSort)
			height =0;
	}
	public int rearrangeIndex = 0;
	public int degree = 0;
	public void rearrangeLeafies(HeadGroup p, int groupType) {
		for (Map.Entry<String, HeadGroup> ent : p.hash.entrySet()) {
			if (ent.getValue().hash != null) {
				rearrangeLeafies(ent.getValue(), groupType);
			}
			else{
			HeadGroup last = ent.getValue();
//			System.out.println("<<<<<<<"+last);
			if (groupType == 0) {
				for (int dataIndex : this.dataItem) {
					if (last.hash2 == null)
						last.hash2 = new LinkedHashMap<Integer, Integer>();
					last.hash2.put(dataIndex, rearrangeIndex);
					this.headList.add(this.oriHead[dataIndex]);
					if (this.sumField.contains(dataIndex)) {
						this.sumColumns.add(this.headList.size() - 1);
					}
					rearrangeIndex++;
					degree++;
				}
			} else {
				 if (last.hash2 == null)
					 last.hash2 = new LinkedHashMap<Integer, Integer>();
//				 System.out.println("cccccccccccc"+last);
				 last.hash2.put(this.rowItem.get(this.rowItem.size()-1),rearrangeIndex);
				 rearrangeIndex++;
				 degree++; 
			}
		}
		}
	}


	public void defaultAllSort(final HeadGroup p){
		if(p.hash!=null){
			//sort Headgroup's hash
			subSortSingle(p,1);
			for(Map.Entry<String, HeadGroup> ent:p.hash.entrySet()){
				if(ent.getValue().hash!=null){
					defaultAllSort(ent.getValue());
				}
			}
		}
	}
	
//	public void sort(HeadGroup root,int buttonIndex,int sortOrder,int iteration,List<HeadGroup> groups){
//		if(buttonIndex == 0){
//			subSortSingle(root,sortOrder);
//			return ;
//		}else if(buttonIndex == iteration){
//			subSort(groups,sortOrder);
//		}
//		else{
//			iteration++;
//			sort(null,buttonIndex,sortOrder,iteration,root.getNext(groups));
//		}
//	}
	
	public void sort(HeadGroup root,int buttonIndex,int sortOrder){
		if(buttonIndex == 0){
			subSortSingle(root,sortOrder);
		}
		else{
			subSort(root.gethlByDegree(buttonIndex, root),sortOrder);
		}
	}
	
	
	public void subSort(List<HeadGroup> groups,  int sortOrder) {
		for (HeadGroup group : groups) {
			subSortSingle(group,sortOrder);
		}
	}
	
	public void subSortSingle(HeadGroup root, final int sortOrder) {
		LinkedList<Map.Entry<String,HeadGroup>> lkl = new LinkedList<Map.Entry<String,HeadGroup>>(root.hash.entrySet());
		Collections.sort(lkl, new Comparator<Map.Entry<String, HeadGroup>>() {
			@Override
			public int compare(Entry<String, HeadGroup> o1,
					Entry<String, HeadGroup> o2) {
				return sortOrder*(compareArray(o1.getKey().getBytes(),o2.getKey().getBytes()));
			}
		});
		root.hash.clear();
		for(Map.Entry<String,HeadGroup> ent:lkl){
			root.hash.put(ent.getKey(), ent.getValue());
		}
	}
	
	public  int compareArray(byte[] b1,byte[] b2){
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
	
	
	public void addRowItems(List<Integer> rowList) {
		rowItem.addAll(rowList);
	}

	public void addColtems(List<Integer> colList) {
		colItem.addAll(colList);
	}

	public void addDataItems(List<Integer> dataList) {
		dataItem.addAll(dataList);
	}

	public TransForm(String[] oriHead, String[][] oriData,HashSet<Integer> sumField) {
		this.oriData = oriData;
		this.oriHead = oriHead;
		this.sumField = sumField;
		for(int i=0;i<oriHead.length;i++){
			this.dataItem.add(i);
		}
	}

	public class HeadGroup{
		LinkedHashMap<String, HeadGroup> hash = null;
		String value;
		LinkedHashMap<Integer, Integer> hash2 = null;
        List<HeadGroup> list = new ArrayList<HeadGroup>();
        HeadGroup parent;
        int degree = 0;
        int height=0;
        int baseLine = 0;
        int extraLine = 0;
        int sortOrder = -1;// -1 for ascent 1 for decent  
        
        //the row or data header range, 0:start no 1:end no.
        int[] range = new int[2];
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
				g.setParent(this);
				hash.put(g.getValue(), g);
			}
		}

		
		
		public int[] getRange() {
			return range;
		}

		public void setRange(int[] range) {
			this.range = range;
		}

		public int getDegree() {
			return degree;
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
		
		public void setHash(LinkedHashMap<String, HeadGroup> hash) {
			this.hash = hash;
		}


		public void setHash2(LinkedHashMap<Integer, Integer> hash2) {
			this.hash2 = hash2;
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
			} else {
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
		
		public List<HeadGroup> gethlByDegree(int degree,HeadGroup g){
			if(degree==1){
				return getNextOne(g);
			}
			else return gethlByDegree(degree,getNextOne(g));
		}
		
		private List<HeadGroup> gethlByDegree(int degree,List<HeadGroup> list){
			List<HeadGroup> tmpList = list;
			int count = 0;
			while(true){
				tmpList = this.getNext(tmpList);
				if((count+2)==degree)
					break;
				count++;
			}
			return tmpList;
		}

		public HeadGroup getParent() {
			return parent;
		}


		public void setParent(HeadGroup parent) {
			this.parent = parent;
		}


		public int getBaseLine() {
			return baseLine;
		}


		public void setBaseLine(int baseLine) {
			this.baseLine = baseLine;
		}


		public int getExtraLine() {
			return extraLine;
		}


		public void setExtraLine(int extraLine) {
			this.extraLine = extraLine;
		}
		
		public int getSortOrder() {
			return sortOrder;
		}

		public void setSortOrder(int sortOrder) {
			this.sortOrder = sortOrder;
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

	public List<Integer> getSumColumns() {
		return sumColumns;
	}

	@Override
	public boolean equals(Object obj) {
		return false;
	}
	
	
	
}
