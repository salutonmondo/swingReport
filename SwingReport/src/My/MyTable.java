package My;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

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
//		for(int i=1;i<this.columnModel.getColumnCount();i++){
//			TableColumn tc = this.columnModel.getColumn(i);
//			tc.setCellRenderer(new MyCellRenderer(true));
//		}
		this.setRowSelectionAllowed(false);
		this.getTableHeader().setReorderingAllowed(false);
		
		
//		GroupableTableHeader header = (GroupableTableHeader) getTableHeader();	
//		ColumnGroup test = new ColumnGroup("test");
//		test.add(this.getColumnModel().getColumn(0));
//		test.add(this.getColumnModel().getColumn(1));
//		header.addColumnGroup(test);
//		this.setTableHeader(header);
		
		
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
//		this.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		if(converter!=null)
			updateContent(converter);
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
	
	public void updateContent(TransForm t){
		t.trans();
		GroupableTableHeader header = (GroupableTableHeader) getTableHeader();	
		HeadGroup hg = t.getColG();
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
		/*
		 * build the row header table
		 */
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
		
		BuilderRowHeader(topList2);
		//because the paint process is executed row by row so the horizental span info must first be sorted.
		Collections.sort(this.insertInfo, new Comparator<Object[]>() {
			public int compare(Object[] o1,Object[] o2) {
				int score1 = Integer.parseInt(o1[1].toString());
				int score2 = Integer.parseInt(o2[1].toString());
				if (score1 > score2)
					return -1;
				else if (score1 < score2)
					return 1;
				else
					return 0;
			}
		});
//		
		MyTableModel m2 = new MyTableModel(false,rowHead,rowTableData,this.insertInfo,MyTableModel.MODEL_TPE_ROW);
		//add vertical span
		for(int[] spans:verticalSpanInfo){
			m2.addSpan(spans[0], spans[1], spans[0]+spans[2]-1, spans[1]);
		}
		//add horizental span
		for(Object[] horizentalSpan :this.insertInfo){
			int y = Integer.parseInt(horizentalSpan[1].toString());
			int x = Integer.parseInt(horizentalSpan[0].toString())-1;
			String content = horizentalSpan[2].toString();
			int end = m2.getColumnCount()-1;
			m2.addSpan(x, y, x, end);
			this.rowTableData[x][y] = content;
		}
		
		MyTable spanedTable = new MyTable(m2,null,null);
		JPanel leftCorner = new JPanel();
		leftCorner.setLayout(null);
		leftCorner.setBackground(Main.bgColor);
		
		/*
		 * build the column table and the spaned table header
		 */
		//prepare the new data array including total span row
		String[][] oriDataArray = t.getResultsData();
		String[][] newDataArray = new String[oriDataArray.length+this.insertInfo.size()][oriDataArray[0].length];
		int dataSpanEnd = oriDataArray[0].length-1;
		List<Integer> totalRowSpanList = new ArrayList<Integer>();
		if(this.insertInfo.size()==0){
			newDataArray = oriDataArray;
		}
		else{
			int spanIndex = 0;
			int newDataArrayIndex = 0;
			Object[] tmpSapn = this.insertInfo.get(spanIndex);
			for(int i=0;newDataArrayIndex<newDataArray.length;newDataArrayIndex++){
				if(newDataArrayIndex==Integer.parseInt(tmpSapn[0].toString())-1){
					newDataArray[newDataArrayIndex][0] = "tobespaned";
//					m.addSpan(newDataArrayIndex, 0, newDataArrayIndex,dataSpanEnd);
					totalRowSpanList.add(newDataArrayIndex);
					spanIndex++;
					if(spanIndex<this.insertInfo.size())
						tmpSapn = this.insertInfo.get(spanIndex);
				}
				else{
					newDataArray[newDataArrayIndex] = oriDataArray[i];
					i++;
				}
			}
		}
		m = new MyTableModel(false,t.getResultHead(),newDataArray,null,MyTableModel.MODEL_TPE_DATA);
		for(int spanRowNo:totalRowSpanList){
			m.addSpan(spanRowNo, 0, spanRowNo, dataSpanEnd);
		}
		this.setModel(m);
		List<HeadGroup> topList = hg.getNextOne(hg);
		this.buildHeader(topList);
		
		/*
		 * set the upper-left corner header
		 */
		JViewport v = (JViewport)this.fixTable.getParent();
		v.remove(fixTable);
		v.setView(spanedTable);
		v.setPreferredSize(spanedTable.getPreferredSize());
		JScrollPane jp = (JScrollPane)v.getParent().getParent();
		int cornerHeight = ((GroupableTableHeaderUI)header.getUI()).getHeaderHeight();
		JTableHeader leftTableHeader = spanedTable.getTableHeader();
		int leftOffSet = 0;
		for(int rowIndex = 0;rowIndex<t.getRowItem().size();rowIndex++){
			JButton jb = new JButton(t.getOriHead()[t.getRowItem().get(rowIndex)]);
			int width = leftTableHeader.getColumnModel().getColumn(rowIndex).getPreferredWidth();
			Dimension oriD = jb.getPreferredSize();
			jb.setBackground(Main.bgColor);
			leftCorner.add(jb);
			jb.setBounds(leftOffSet,cornerHeight-oriD.height,width,oriD.height);
//			jb.setBounds(leftOffSet,0,width,oriD.height);
			leftOffSet+=width;
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
			JButton jb = new JButton(t.getOriHead()[t.getColItem().get(colIndex)]);
			Dimension oriD = jb.getPreferredSize();
			jb.setBackground(Main.bgColor);
			leftCorner.add(jb);
			centerTopPanel.add(jb);
			prefredHeight = oriD.height;
			jb.setBounds(leftOffSet2,0,oriD.width,oriD.height);
			leftOffSet2+=oriD.width;
		}
		centerTopPanel.setPreferredSize(new Dimension(0,prefredHeight));
		
		for(Object[] obj:this.insertInfo){
			System.out.println("****************");
			System.out.println(obj[0]);
			System.out.println(obj[1]);
			System.out.println(obj[2]);
		}
	}
	
	
	/*
	 * the global variable which be used to build column table header and the row header table
	 */
	HashMap<String, ColumnGroup> lastColumnGroups = new HashMap<String, ColumnGroup>();
	StringBuilder parentKey = new StringBuilder();
	String[][] rowTableData;
	String[] rowHead;
	int verticalSpan = 0;
	int rowCacuHeight = 0;
	List<int[]> verticalSpanInfo = new ArrayList<int[]>();
	
	public void BuilderRowHeader(List<HeadGroup> topList2){
		if(topList2==null)return;
		int span=0;
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
				sp[0] = span;
				sp[1] = rowCacuHeight;
				sp[2] = spanRange-1;
				Object[] totalInfo = new Object[3];
				totalInfo[0] = span+spanRange;
				totalInfo[1] = rowCacuHeight;
				totalInfo[2] = "total of '"+p+"'";
				this.insertInfo.add(totalInfo);
			}
			rowTableData[p.getParent().getExtraLine()+initLine][rowCacuHeight] = p.getValue();
			p.setExtraLine(p.getParent().getExtraLine()+initLine);
			span = span+spanRange;
			initLine = initLine+spanRange;
			if(sp!=null)
				verticalSpanInfo.add(sp);
		}
		List<HeadGroup> nextList = topList2.get(0).getNext(topList2);
		rowHead[rowCacuHeight]="";
		rowCacuHeight++;
		BuilderRowHeader(nextList);
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
	 * Object[]: 0. (int)height from where the total span start 1.(int) the start row index of the total span 2.(string) the content of the total like("total of ±±¾©µê")
	 */
	List<Object[]> insertInfo = new ArrayList<Object[]>();
	
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
			} else {
				getParentsKey(p);
				ColumnGroup parent = lastColumnGroups.get(parentKey.toString());
				ColumnGroup sub = new ColumnGroup(p.getValue());
//				System.out.println("3:" + parent.text+" add "+ sub.text);
				parent.add(sub);
				last = sub;
				parentKey.append(sub.text);
				tmpLastColumnGroups.put(parentKey.toString(), sub);
				parentKey = new StringBuilder();
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
	
	public void getParentsKey(HeadGroup p) {
		if (p.getParent().getValue().equals("root"))
			return;
		parentKey.append(p.getParent().getValue());
		getParentsKey(p.getParent());
	}
	
	public void foldup(){
		setPreferredSize(super.getPreferredSize());
	}
	
	
}