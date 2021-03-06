package My;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;

import My.MyTableModel.SpanArea;
import dataTransform.TransForm;
import dataTransform.TransForm.HeadGroup;

public class MyTable extends JTable {
	/**
	 * 
	 */
	boolean showLineNumbers = false;
	private static final long serialVersionUID = 1L;
	MyTableModel m = (MyTableModel)this.getModel();
	TransForm converter;
	JTable fixTable;
	
	public MyTable(MyTableModel m,TransForm converter,JTable table) {
		super(m);
		this.converter = converter;
		this.fixTable = table;
		/*
		 * if the table shows line Number then define it's line No column Renderer
		 */
		if(showLineNumbers)this.columnModel.getColumn(0).setCellRenderer(new LineNumberRenderer(true));
		for(int i=0;i<this.columnModel.getColumnCount();i++){
			TableColumn tc = this.columnModel.getColumn(i);
			tc.setHeaderRenderer(new MyHeaderRenderer());
		}
		this.setRowSelectionAllowed(false);
		this.getTableHeader().setReorderingAllowed(false);
		this.setIntercellSpacing(new Dimension(2, 2));
		this.setShowGrid(false);
//		this.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
//		this.addMouseListener(new MouseAdapter(){
//
//			@Override
//			public void mouseClicked(MouseEvent e) {
//				super.mouseClicked(e);
//				System.out.println(e.getX()+"_"+e.getY());
//				System.out.println(((JTable)e.getSource()).rowAtPoint(e.getPoint()));
//			}
//			
//		});
		this.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		if(converter!=null)
			updateContent(converter,false,0);
		this.setUI(new MyTableUI());
	}
	
	
	public Rectangle getSapnRec(int row,int col,MyTableModel.SpanArea s){
		if(m.isLeadingCell(row, col))
			return super.getCellRect(s.beginX, s.beginY, true).union(super.getCellRect(s.endX, s.endY, true));
		else
			return super.getCellRect(row, col, true);
	}
	
	public Rectangle getPaintSpanRec(MyTableModel.SpanArea s){
		return super.getCellRect(s.beginX, s.beginY, true).union(super.getCellRect(s.endX, s.endY, true));
	}

	@Override 
	public Rectangle getCellRect(int row, int col, boolean b){
		SpanArea s = m.isSpaened(row, col);
		if(s!=null){
			Rectangle or = getSapnRec(row,col,s);
			return or;
		}
		else{
			return super.getCellRect(row,col,true);
		}
	}

	@Override
	protected void paintComponent(Graphics g) {
//		this.wrapLineNumberCol();
		super.paintComponent(g);
	}

	@Override
	public void repaint(Rectangle r) {
		for(MyTableModel.SpanArea s:m.spanAreas){
			super.repaint(getPaintSpanRec(s));
		}
		super.repaint(r);
	}
	
	@Override
	/*
	 * enable tableHeader span
	 */
	protected JTableHeader createDefaultTableHeader() {
		return new GroupableTableHeader(columnModel);
	}
	
	public int getDisplayedLine(){
		Rectangle vr = getVisibleRect();
		int last = rowAtPoint(new Point(0,(int)vr.getHeight()-1));
		return last;
		
	}
	
	public void wrapLineNumberCol(){
		System.out.println("wrap been invoked");
		TableColumn tc = this.getColumnModel().getColumn(0);
		JLabel jl = (JLabel)tc.getCellRenderer();
		tc.setMaxWidth((int)jl.getPreferredSize().getWidth()+20);
	}

	public TransForm getConverter() {
		return converter;
	}
		
	GroupableTableHeader header;
	String[][] mainData;
	public void updateContent(TransForm t,boolean isSort,int sortType){
		if(!isSort)
			t.trans();
		hg = t.getColG();
		header = (GroupableTableHeader) getTableHeader();	
		/*
		 * first time initiate the plain table 
		 */
		if (hg == null) {
			for (String s : t.getResultHead()) {
				ColumnGroup g = new ColumnGroup(s);
				header.addColumnGroup(g);
			}
			return;
		}
		this.prepareRowHeaderTable(t, isSort, sortType);
		this.addRowHeaderTable(t, isSort, sortType);
		this.prepareMainTable(t, isSort, sortType);
		this.addMainTable(t.getResultHead(), mainData);
		this.headNames = t.getResultHead();
		this.addColumnHeader(t,isSort, sortType);
		
		
	}
	
	
	int foldIdentifier ;
	String[] headNames;
	Map<Integer,FoldUpInfo> foldInfo = new HashMap<Integer,FoldUpInfo>();
	
	public void onFoldUpItem(int columnIndex,int start,int end){
		String[][] foldedMainData;
		String[][] foldRowData;
		String[] tmp = null;
		int range = end-start+1;
		boolean shouldAdd = false;
		foldRowData = new String[this.rowTableData.length-range][this.rowTableData[0].length];
//		foldRowData = new String[this.rowTableData.length][this.rowTableData[0].length];
		int s= 0;
		for(int l=0;l<this.rowTableData.length;l++){
			if(l>=start&&l<=end){
				if(!shouldAdd){
					tmp = new String[columnIndex];
					for(int x = 0;x<tmp.length;x++){
						tmp[x] = this.rowTableData[l][x];
					}					
				shouldAdd = true;
				}
			}else{
				foldRowData[s] = this.rowTableData[l];
				if(shouldAdd){
					for(int x = 0;x<tmp.length;x++){
						foldRowData[s][x] = tmp[x];
						System.out.println("debugging "+tmp[x]+"___"+x+"___"+s);
					}
					shouldAdd = false;
				}
				s++;
			}
			
		}
		MyTableModel rowTableModel = (MyTableModel)this.spanedTable.getModel();
		List<int[]> tmpVerticalSpanInfo = rowTableModel.getVerticalSpanInfo();
		List<Object[]> tmpHorizenTalSpanInof = rowTableModel.getHorizentalSpanInfo();
		rowTableModel.spanAreas.clear();
		List<int[]> foldedSpanInfo = new ArrayList<int[]>();
//		for(int[] spans:verticalSpanInfo){
		for(int[] spans:tmpVerticalSpanInfo){
			if (spans[0] == start && spans[0] + spans[2] - 1 == end) {
				System.out.println(spans[0] + spans[2] - 1);
				System.out.println(spans[0]);
				foldedSpanInfo.add(new int[]{spans[0]-start,spans[1],spans[2]});
			}
			else if(spans[0]<=start&&spans[0]+spans[2]-1>=end){
				rowTableModel.addSpan(spans[0], spans[1], spans[0]+spans[2]-range-1, spans[1],MyTableModel.SPAN_DIRECTION_VER);
			}else if(spans[0]<start){
				rowTableModel.addSpan(spans[0], spans[1], spans[0]+spans[2]-1, spans[1],MyTableModel.SPAN_DIRECTION_VER);
			}	
			else if(spans[0]>end){
				rowTableModel.addSpan(spans[0]-range, spans[1], spans[0]+spans[2]-1-range, spans[1],MyTableModel.SPAN_DIRECTION_VER);
			}	
			else if(spans[0]>=start&&spans[0]+spans[2]-1<=end){
				foldedSpanInfo.add(new int[]{spans[0]-start,spans[1],spans[2]});
			}
		}
		
		
		
		this.totalRowSet.clear();
		List<Object[]> foldSpanInfoh = new ArrayList<Object[]>();
//		SpanArea spanArea = rowTableModel.new SpanArea(); 
//		for(Object[] horizentalSpan :this.rowTableTotalSpan){
		for(Object[] horizentalSpan :tmpHorizenTalSpanInof){
			int y = Integer.parseInt(horizentalSpan[1].toString());
			int x = Integer.parseInt(horizentalSpan[0].toString())-1;
			String content = horizentalSpan[2].toString();
			int end1 = rowTableModel.getColumnCount()-1;
			if(x>end){
				SpanArea spanArea = rowTableModel.new SpanArea(); 
				spanArea.beginX = x-range;
				spanArea.beginY = y;
				spanArea.endX = x-range;
				spanArea.endY = end1;
				spanArea.type = MyTableModel.SPAN_DIRECTION_HOR;
				spanArea.horizentalText = content;
				rowTableModel.addSpan(spanArea);
//				rowTableModel.addSpan(x-range, y, x-range, end1,MyTableModel.SPAN_DIRECTION_HOR);
				totalRowSet.add(x-range);
			}	
			else if(x<start){
				SpanArea spanArea = rowTableModel.new SpanArea(); 
				spanArea.beginX = x;
				spanArea.beginY = y;
				spanArea.endX = x;
				spanArea.endY = end1;
				spanArea.type = MyTableModel.SPAN_DIRECTION_HOR;
				spanArea.horizentalText = content;
//				rowTableModel.addSpan(x, y, x, end1,MyTableModel.SPAN_DIRECTION_HOR);
				rowTableModel.addSpan(spanArea);
				this.rowTableData[x][y] = content;
				totalRowSet.add(x);
			}
			else{
				foldSpanInfoh.add(new Object[]{x-start,y,content});
			}
		}
		
		int[] foldUpPosition = new int[5];
		foldUpPosition[0] = columnIndex;
		foldUpPosition[1] = start;
		foldUpPosition[2] = end;
		foldUpPosition[3] = range;
		foldUpPosition[4] = rowTableModel.getColumnCount()-1;
		
		String[][] foldUpContent = new String[range][rowTableModel.getColumnCount()];
		String[][] foldUpMainContent = new String[range][this.mainData[0].length];
		for(int a=0;a<range;a++){
			foldUpContent[a] = this.rowTableData[a+start];
			foldUpMainContent[a] = this.mainData[a+start];
		}
		FoldUpInfo foldupinfo = new FoldUpInfo(foldUpPosition,foldUpContent,foldUpMainContent,foldedSpanInfo,foldSpanInfoh);
		this.foldInfo.put(this.foldIdentifier, foldupinfo);
		
		foldIdentifier++;
		rowTableModel.data = foldRowData;
		
		/*
		 * cut the folded row of the main table data 
		 */
		foldedMainData = new String[this.mainData.length-range][this.mainData[0].length];
		int k = 0;
		for(int j=0;j<this.mainData.length;j++){
			if(j>=start&&j<=end){
			}
			else{
				foldedMainData[k] = this.mainData[j];
				k++;
			}
		}
		
		for(SpanArea x:rowTableModel.spanAreas){
			System.out.println("!!!!!!!!!!"+x);
		}
		this.addMainTable(this.headNames, foldedMainData);
		this.inserRowTable(rowTableModel, false, 1);
		this.rowTableData = foldRowData;
		this.mainData = foldedMainData;
	}
	
	public void onCollapseItem(int indetifier){
		this.totalRowSet.clear();
		MyTableModel rowTableModel = (MyTableModel)this.spanedTable.getModel();
		List<int[]> tmpVerticalSpanInfo = rowTableModel.getVerticalSpanInfo();
		List<Object[]> tmpHorizenTalSpanInof = rowTableModel.getHorizentalSpanInfo();
		
		rowTableModel.spanAreas.clear();
		FoldUpInfo fi = this.foldInfo.get(indetifier);
		int[] foldUpPosition = fi.foldUpPostion;
		String[][] tmpRowTableData = new String[this.rowTableData.length+foldUpPosition[3]][this.rowTableData[0].length];
		String[][] tmpRowMainTableData = new String[this.mainData.length+foldUpPosition[3]][this.mainData[0].length];
		
		for(int i=0,j= 0;i<tmpRowTableData.length;){
			if(i!=foldUpPosition[1]){
				tmpRowTableData[i]= this.rowTableData[j];
				tmpRowMainTableData[i] = this.mainData[j];
				j++;
				i++;
			}
			else{
				for(int k=0;k<foldUpPosition[3];k++){
					tmpRowTableData[i]= fi.foldUpContent[k];
					tmpRowMainTableData[i] = fi.foldUpMainContent[k];
					i++;
				}
			}
		}
		rowTableModel.data = tmpRowTableData;

		for(int[] spans:tmpVerticalSpanInfo){
			int top = spans[0];
			int bottom = spans[0]+spans[2]-1;
			int foldPoint = foldUpPosition[1]; 
			if (top<=foldPoint&&bottom>=foldPoint) {
				rowTableModel.addSpan(spans[0], spans[1], spans[0]+spans[2]+foldUpPosition[3]-1, spans[1],MyTableModel.SPAN_DIRECTION_VER);
			}
			else if(top<foldPoint){
				rowTableModel.addSpan(spans[0], spans[1], spans[0]+spans[2]-1, spans[1],MyTableModel.SPAN_DIRECTION_VER);
			}	
			else if(top>foldPoint){
				rowTableModel.addSpan(spans[0]+foldUpPosition[3], spans[1], spans[0]+spans[2]+foldUpPosition[3]-1, spans[1],MyTableModel.SPAN_DIRECTION_VER);
			}	
		}
		
		for(int[] spans:fi.foldedSpanInfo){
			rowTableModel.addSpan(spans[0]+foldUpPosition[1], spans[1], foldUpPosition[1]+spans[0]+spans[2]-1, spans[1], 0);
		}

		for(Object[] horizentalSpan:tmpHorizenTalSpanInof){
			int y = Integer.parseInt(horizentalSpan[1].toString());
			int x = Integer.parseInt(horizentalSpan[0].toString())-1;
			if(x<foldUpPosition[1]){
				SpanArea spanArea = rowTableModel.new SpanArea(); 
				spanArea.beginX = x;
				spanArea.beginY = y;
				spanArea.endX = x;
				spanArea.endY = rowTableModel.getColumnCount()-1;
				spanArea.type = MyTableModel.SPAN_DIRECTION_HOR;
				spanArea.horizentalText = horizentalSpan[2].toString();
				rowTableModel.addSpan(spanArea);
				this.totalRowSet.add(x);
			}
			else{
				SpanArea spanArea = rowTableModel.new SpanArea(); 
				spanArea.beginX = x+foldUpPosition[3];
				spanArea.beginY = y;
				spanArea.endX = x+foldUpPosition[3];
				spanArea.endY = rowTableModel.getColumnCount()-1;
				spanArea.type = MyTableModel.SPAN_DIRECTION_HOR;
				spanArea.horizentalText = horizentalSpan[2].toString();
				rowTableModel.addSpan(spanArea);
				this.totalRowSet.add(x+foldUpPosition[3]);
			}
		}
		
		for(Object[] horizentalSpan:fi.foldSpanInfoh){
			int y = Integer.parseInt(horizentalSpan[1].toString());
			int x = Integer.parseInt(horizentalSpan[0].toString())-1;
			SpanArea spanArea = rowTableModel.new SpanArea(); 
			spanArea.beginX = x+foldUpPosition[1]+1;
			spanArea.beginY = y;
			spanArea.endX = x+foldUpPosition[1]+1;
			spanArea.endY = rowTableModel.getColumnCount()-1;
			spanArea.type = MyTableModel.SPAN_DIRECTION_HOR;
			spanArea.horizentalText = horizentalSpan[2].toString();
			rowTableModel.addSpan(spanArea);
			this.totalRowSet.add(x+foldUpPosition[1]);
		}
		
		
		this.addMainTable(this.headNames, tmpRowMainTableData);
		this.inserRowTable(rowTableModel, false, 1);
		this.rowTableData = tmpRowTableData;
		this.mainData = tmpRowMainTableData;
		
	}
	
	public void prepareRowHeaderTable(TransForm t,boolean isSort,int sortType){
		HeadGroup rg = t.getRowG();
		rowHead = new String[rg.getHeight()];
		List<HeadGroup> topList2 = rg.getNextOne(rg);
		int rowHeaderRows = 0;
		for(HeadGroup tmph : topList2){
			this.getRowSpanIncludeTotal(tmph);
			rowHeaderRows = rowHeaderRows+ this.verticalSpan;
			verticalSpan = 0;
		}
//		rowTableData = new String[rg.getDegree()][rg.getHeight()];
		rowTableData = new String[rowHeaderRows][rg.getHeight()];
//		if(!isSort||sortType==1){
//		if(!isSort){
			BuilderRowHeaderClean(rg);
			BuilderRowHeader(topList2);
//		}
		//because the paint process is executed row by row so the horizental span info must first be sorted.
		Collections.sort(this.rowTableTotalSpan, new Comparator<Object[]>() {
			public int compare(Object[] o1,Object[] o2) {
				int score1 = Integer.parseInt(o1[0].toString());
				int score2 = Integer.parseInt(o2[0].toString());
				if (score1 > score2)
					return 1;
				else if (score1 < score2)
					return -1;
				else
					return 0;
			}
		});
		
		for(Object[] array:this.rowTableTotalSpan){
			System.out.println(array[0]+"_"+array[1]+"_"+array[2]);
		}
		
		
	}
	public void addRowHeaderTable(TransForm t,boolean isSort,int sortType){
		MyTableModel m2 = new MyTableModel(false,rowHead,rowTableData,this.rowTableTotalSpan,MyTableModel.MODEL_TPE_ROW);
		//add vertical span
		for(int[] spans:verticalSpanInfo){
			m2.addSpan(spans[0], spans[1], spans[0]+spans[2]-1, spans[1],0);
		}
		
		for(int[] array:this.verticalSpanInfo){
			System.out.println("...............");
			System.out.println(array[0]+"_"+array[1]+"_"+array[2]);
		}
		//add horizental span
		for(Object[] horizentalSpan :this.rowTableTotalSpan){
			int y = Integer.parseInt(horizentalSpan[1].toString());
			int x = Integer.parseInt(horizentalSpan[0].toString())-1;
			String content = horizentalSpan[2].toString();
			int end = m2.getColumnCount()-1;
			SpanArea s = m2.new SpanArea();
			s.beginX = x;
			s.beginY = y;
			s.endX = x;
			s.endY = end;
			s.type = MyTableModel.SPAN_DIRECTION_HOR;
			s.horizentalText = content;
			m2.addSpan(s);
//			m2.addSpan(x, y, x, end,1);
//			m2.addSpan(x, y, end, y);
			this.rowTableData[x][y] = content;
		}
		this.inserRowTable(m2, isSort, sortType);
	}
	
	public void inserRowTable(MyTableModel m2,boolean isSort,int sortType){
		spanedTable = new MyTable(m2,null,null);
//		JPanel leftCorner = new JPanel();
		leftCorner.setLayout(null);
		leftCorner.setBackground(Main.bgColor);	
		
		JViewport v = (JViewport)this.fixTable.getParent();
//		if(!isSort||sortType==1){
//		if(!isSort||sortType==1){
			v.remove(fixTable);
			for(int i=0;i<spanedTable.columnModel.getColumnCount();i++){
				TableColumn tc = spanedTable.columnModel.getColumn(i);
				tc.setCellRenderer(new MyHeaderRenderer());
			}
			spanedTable.setIntercellSpacing(new Dimension(0,0));
			v.setView(spanedTable);
			this.fixTable = spanedTable;
			v.setPreferredSize(spanedTable.getPreferredSize());
//		}
	}
	
	MyTable spanedTable;
	public void prepareMainTable(TransForm t,boolean isSort,int sortType){
		/*
		 * build the column table and the spaned table header
		 */
		//prepare the new data array including total span row
		String[][] oriDataArray = t.getResultsData();
		String[][] newDataArray = new String[oriDataArray.length+this.rowTableTotalSpan.size()][oriDataArray[0].length];
		HashMap<Integer,int[]> scaleTotalMap = new HashMap<Integer,int[]>();
		if(this.rowTableTotalSpan.size()==0){
			newDataArray = oriDataArray;
		}
		else{
			int spanIndex = 0;
			int newDataArrayIndex = 0;
			Object[] tmpSapn = this.rowTableTotalSpan.get(spanIndex);
			List<Integer> sumColumnList = t.getSumColumns();
			int sumScale = 0;
			for(int i=0;newDataArrayIndex<newDataArray.length;newDataArrayIndex++){
				sumScale = breakMap.get(newDataArrayIndex+1);
				int []tmpArray = scaleTotalMap.get(sumScale+1);
				int tmpSumTotal[] = new int[oriDataArray[0].length];
				int tmpSumTotalIndex = 0;
				if(newDataArrayIndex==Integer.parseInt(tmpSapn[0].toString())-1){
					for(int sumColumnIndex:sumColumnList){
						newDataArray[newDataArrayIndex][sumColumnIndex] = tmpArray[sumColumnIndex]+"";
						tmpSumTotal[tmpSumTotalIndex]= tmpArray[sumColumnIndex];
						tmpSumTotalIndex++;
					}
					int[] selfTotal = scaleTotalMap.get(sumScale);
					if(selfTotal!=null){
						for(int z=0;z<selfTotal.length;z++){
							selfTotal[z] = tmpArray[z]+selfTotal[z];
						}
						scaleTotalMap.put(sumScale, selfTotal);
					}
					else{
						scaleTotalMap.put(sumScale, tmpSumTotal);
					}
					
					totalRowSet.add(newDataArrayIndex);
					spanIndex++;
					if(spanIndex<this.rowTableTotalSpan.size())
						tmpSapn = this.rowTableTotalSpan.get(spanIndex);

					int[] emptyArray = new int[oriDataArray[0].length];
					scaleTotalMap.put(sumScale+1, emptyArray);
				}
				else{
					newDataArray[newDataArrayIndex] = oriDataArray[i];
					int[] sumArray = scaleTotalMap.get(sumScale);
					if(sumArray == null){
						sumArray = new int[oriDataArray[0].length];
					}
					for (int sumColumnIndex : sumColumnList) {
						int addValue = 0;
						try{
							addValue = Integer.parseInt(oriDataArray[i][sumColumnIndex]);
						}
						catch(Exception e){
						}
						sumArray[sumColumnIndex] = sumArray[sumColumnIndex]+addValue;
					}
					scaleTotalMap.put(sumScale, sumArray);
					i++;
				}
			}
		}
//		m = new MyTableModel(false,t.getResultHead(),newDataArray,null,MyTableModel.MODEL_TPE_DATA);
////		for(int spanRowNo:totalRowSpanList){
////			m.addSpan(spanRowNo, 0, spanRowNo, dataSpanEnd);
////		}
//		this.setModel(m);
//		for(int i=0;i<this.columnModel.getColumnCount();i++){
//			TableColumn tc = this.columnModel.getColumn(i);
//			tc.setCellRenderer(new MyCellRenderer());
//			tc.setHeaderRenderer(new MyHeaderRenderer());
//		}
		mainData = newDataArray;
	}
	
	public void addMainTable(String[] headNames,String[][] newDataArray){
		m = new MyTableModel(false,headNames,newDataArray,null,MyTableModel.MODEL_TPE_DATA);
		this.setModel(m);
		for(int i=0;i<this.columnModel.getColumnCount();i++){
			TableColumn tc = this.columnModel.getColumn(i);
			tc.setCellRenderer(new MyCellRenderer());
			tc.setHeaderRenderer(new MyHeaderRenderer());
		}
		List<HeadGroup> topList = hg.getNextOne(hg);
		buildHeaderClean();
		this.buildHeader(topList);
	}
	HeadGroup hg;
	public void addColumnHeader(TransForm t,boolean isSort,int sortType){
		JViewport v = (JViewport)this.fixTable.getParent();		
		JScrollPane jp = (JScrollPane)v.getParent().getParent();
		int cornerHeight = ((GroupableTableHeaderUI)header.getUI()).getHeaderHeight();
		JTableHeader leftTableHeader = spanedTable.getTableHeader();
		int leftOffSet = 0;
		if(!isSort){
			for(int rowIndex = 0;rowIndex<t.getRowItem().size();rowIndex++){
				MyButton jb = new MyButton(t.getOriHead()[t.getRowItem().get(rowIndex)],t,1,this,rowIndex);
				TableColumn tc = leftTableHeader.getColumnModel().getColumn(rowIndex);
				int width = tc.getPreferredWidth();
				Dimension oriD = jb.getPreferredSize();
				jb.setBackground(Main.bgColor);
				leftCorner.add(jb);
				System.out.println();
				jb.setBounds(leftOffSet,cornerHeight-oriD.height,width,oriD.height);
	//			jb.setBounds(leftOffSet,0,width,oriD.height);
				leftOffSet+=width;
			}
			
		}
		jp.setBackground(Color.red);
		jp.setCorner(JScrollPane.UPPER_LEFT_CORNER, leftCorner);
		
		/*
		 * set the colorName of the column header
		 */
		JPanel centerPanel = (JPanel)jp.getParent();
		JPanel centerTopPanel = (JPanel)centerPanel.getComponent(0);
		int leftOffSet2 = spanedTable.getPreferredSize().width;
		int prefredHeight = 0;
		for(int colIndex = 0;colIndex<t.getColItem().size();colIndex++){
			MyButton jb = new MyButton(t.getOriHead()[t.getColItem().get(colIndex)],t,0,this,colIndex);
			Dimension oriD = jb.getPreferredSize();
			jb.setBackground(Main.bgColor);
			leftCorner.add(jb);
			centerTopPanel.add(jb);
			prefredHeight = oriD.height;
			jb.setBounds(leftOffSet2,0,oriD.width,oriD.height);
			leftOffSet2+=oriD.width;
		}
		centerTopPanel.setPreferredSize(new Dimension(0,prefredHeight));
	}
	
	JPanel leftCorner = new JPanel();

	/*
	 * the global variable which be used to build column table header and the row header table
	 */
	HashMap<String, ColumnGroup> lastColumnGroups = new HashMap<String, ColumnGroup>();
	String parentKey = "" ;
	String[][] rowTableData;
	String[] rowHead;
	int verticalSpan = 0;
	int rowCacuHeight = 0;
	List<int[]> verticalSpanInfo = new ArrayList<int[]>();
	HashSet<Integer> totalRowSet = new HashSet<Integer>();
	HashMap<Integer,Integer> breakMap = new HashMap<Integer,Integer>();
	
	public void BuilderRowHeader(List<HeadGroup> topList2){
		if(topList2==null)return;
		int initLine = 0;
		for (HeadGroup p : topList2) {
			this.getRowSpanIncludeTotal(p);
			int spanRange = p.getNextOne(p)!=null?verticalSpan:1;
			if(p.getParent().getBaseLine()==0){
				initLine = 0;
			}
			p.getParent().setBaseLine(1);
			verticalSpan = 0;
			int[] sp =null;
			if(spanRange>1){
				sp = new int[3];
				sp[0] = p.getParent().getExtraLine()+initLine;
				sp[1] = rowCacuHeight;
				sp[2] = spanRange-1;
				Object[] totalInfo = new Object[3];
				totalInfo[0] = p.getParent().getExtraLine()+initLine+spanRange;
				totalInfo[1] = rowCacuHeight;
				totalInfo[2] = p+" total";
				this.rowTableTotalSpan.add(totalInfo);
				breakMap.put(p.getParent().getExtraLine()+initLine+spanRange, rowCacuHeight);
			}
			if(breakMap.get(p.getParent().getExtraLine()+initLine+spanRange)==null)
				breakMap.put(p.getParent().getExtraLine()+initLine+spanRange,rowCacuHeight);
			rowTableData[p.getParent().getExtraLine()+initLine][rowCacuHeight] = p.getValue();
			p.setExtraLine(p.getParent().getExtraLine()+initLine);
			initLine = initLine+spanRange;
			if(sp!=null)
				verticalSpanInfo.add(sp);
			
		}
		List<HeadGroup> nextList = topList2.get(0).getNext(topList2);
		rowHead[rowCacuHeight]="";
		rowCacuHeight++;
		BuilderRowHeader(nextList);
	}
	
	public void BuilderRowHeaderClean(HeadGroup p){
		rowCacuHeight = 0;
		verticalSpan = 0;
		verticalSpanInfo = new ArrayList<int[]>();
		totalRowSet = new HashSet<Integer>();
		breakMap = new HashMap<Integer,Integer>();
		rowTableTotalSpan = new ArrayList<Object[]>();
		cleanRowSpanInfo(p);
	}
	
	public void cleanRowSpanInfo( HeadGroup p){
		for(Map.Entry<String, HeadGroup> ent:p.getHash().entrySet()){
			HeadGroup subGroup = ent.getValue();
			subGroup.setBaseLine(0);
			subGroup.setExtraLine(0);
			if(subGroup.getHash2()!=null){
//				subGroup.setHash2(new LinkedHashMap<Integer,Integer>());
			}
			if(subGroup.getHash()!=null){
				cleanRowSpanInfo(subGroup);
			}
		}
		
	}
	
	public void getRowSpanIncludeTotal(HeadGroup p) {
		if(p.getHash2()!=null){
			this.verticalSpan = verticalSpan+ 1;
		}
		List<HeadGroup> list = p.getNextOne(p);
		if (list != null) {
			if (list.size() > 1) {
				verticalSpan = verticalSpan + 1;
			}
			for (HeadGroup g : list) {
				if (g.getHash2() != null) {
					verticalSpan = verticalSpan + 1;
				} else {
					getRowSpanIncludeTotal(g);
				}
			}
		}
	}
	
	public void getRowSpan(HeadGroup p) {
		List<HeadGroup> list = p.getNextOne(p);
		if (list != null) {
			for (HeadGroup g : list) {
				if (g.getHash2() != null) {
					verticalSpan = verticalSpan + 1;
				} else {
					getRowSpan(g);
				}
			}
		}
	}
	

	/*
	 * the total info to be inserted into the row header and data model
	 * Object[]: 0. (int)height from where the total span start 1.(int) the start row index of the total span 2.(string) the content of the total like("total of ������")
	 */
	List<Object[]> rowTableTotalSpan = new ArrayList<Object[]>();
	
	/*
	 * Build column header and gather the total span rows info.
	 */
	public void buildHeader(List<HeadGroup> topList) {
		int columnIndex = 0;
		GroupableTableHeader header = (GroupableTableHeader) getTableHeader();	
		HashMap<String, ColumnGroup> tmpLastColumnGroups = new HashMap<String, ColumnGroup>();
		if(topList==null)return;
		ColumnGroup last;
		for (HeadGroup p : topList) {
			if (!header.isAdded) {
				ColumnGroup cg = new ColumnGroup(p.getValue());
				last = cg;
				header.addColumnGroup(cg);
				lastColumnGroups.put(cg.text, cg);
				tmpLastColumnGroups.put(cg.text, cg);
//				header.is
			} else {
				getParentsKey(p);
				ColumnGroup parent = lastColumnGroups.get(parentKey.toString());
				System.out.println("........................"+parentKey.toString());
				ColumnGroup sub = new ColumnGroup(p.getValue());
//				System.out.println("3:" + parent.text+" add "+ sub.text);
				parent.add(sub);
				last = sub;
//				parentKey.append(sub.text);
				parentKey = sub.text+parentKey;
				tmpLastColumnGroups.put(parentKey.toString(), sub);
				parentKey = "";
			}
			if (p.getHash2() != null) {
				for (int j=0;j<p.getHash2().size();j++) {
					last.add(this.getColumnModel().getColumn(columnIndex));
					columnIndex++;
//					System.out.println("1:" +last.text+" add "+ ent.getValue());
				}
			}
		}
		lastColumnGroups = tmpLastColumnGroups;
		//tmpLastColumnGroups.clear();
		header.isAdded = true;
		List<HeadGroup> nextList = topList.get(0).getNext(topList);
		buildHeader(nextList);
	}
	
	public void buildHeaderClean(){
		GroupableTableHeader header = (GroupableTableHeader) getTableHeader();	
		header.isAdded = false;
	}
	
	public void getParentsKey(HeadGroup p) {
		if (p.getParent().getValue().equals("root"))
			return;
		parentKey = parentKey+(p.getParent().getValue());
		getParentsKey(p.getParent());
	}
	
	public void foldup(){
		setPreferredSize(super.getPreferredSize());
	}

	public boolean getScrollableTracksViewportWidth() {
		return getPreferredSize().width < getParent().getWidth();
	}
	
	class FoldUpInfo{
		int[] foldUpPostion;
		String[][] foldUpContent;
		String[][] foldUpMainContent;
		List<int[]> foldedSpanInfo;
		List<Object[]> foldSpanInfoh;
		public FoldUpInfo(int[] foldUpPostion,String[][] foldUpContent,String[][] foldUpMainContent,List<int[]> foldedSpanInfo,List<Object[]> foldSpanInfoh){
			this.foldUpContent = foldUpContent;
			this.foldUpPostion = foldUpPostion;
			this.foldUpMainContent = foldUpMainContent;
			this.foldedSpanInfo = foldedSpanInfo;
			this.foldSpanInfoh = foldSpanInfoh;
		}	
	}
	
}