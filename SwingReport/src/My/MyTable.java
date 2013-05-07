package My;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
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
		m = new MyTableModel(false,t.getResultHead(),t.getResultsData(),null);
//		m.addSpan(2, 2, 3, 3);
		this.setModel(m);
		HeadGroup hg = t.getColG();
		if (hg == null) {
			GroupableTableHeader header = (GroupableTableHeader) getTableHeader();	
			for (String s : t.getResultHead()) {
				ColumnGroup g = new ColumnGroup(s);
				header.addColumnGroup(g);
			}
			return;
		}
		List<HeadGroup> topList = hg.getNextOne(hg);
		this.buildHeader(topList);

		
		HeadGroup rg = t.getRowG();
		rowTableData = new String[rg.getDegree()][rg.getHeight()];
		rowHead = new String[rg.getHeight()];
		
		List<HeadGroup> topList2 = rg.getNextOne(rg);
		BuilderRowHeader(topList2);
		
		
		MyTableModel m2 = new MyTableModel(false,rowHead,rowTableData,null);
		
		
		for(int[] spans:spanInfo){
			m2.addSpan(spans[0], spans[1], spans[0]+spans[2]-1, spans[1]);
		}

		MyTable spanedTable = new MyTable(m2,null,null);
		
		JPanel leftCorner = new JPanel();
		leftCorner.setLayout(null);
		leftCorner.setBackground(Main.bgColor);
		/*
		 * set the upper-left corner header
		 */
		JViewport v = (JViewport)this.fixTable.getParent();
		v.remove(fixTable);
		v.setView(spanedTable);
		v.setPreferredSize(spanedTable.getPreferredSize());
		JScrollPane jp = (JScrollPane)v.getParent().getParent();
		GroupableTableHeader header = (GroupableTableHeader) getTableHeader();	
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
			leftOffSet+=width;
		}
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
	}
	
	/*
	 * Build row header
	 */
	HashMap<String, ColumnGroup> lastColumnGroups = new HashMap<String, ColumnGroup>();
	StringBuilder parentKey = new StringBuilder();
	String[][] rowTableData;
	String[] rowHead;
	int rowSpan = 0;
	int rowCacuHeight = 0;
	List<int[]> spanInfo = new ArrayList<int[]>();
	
	public void BuilderRowHeader(List<HeadGroup> topList2){
		if(topList2==null)return;
		int span=0;
		for (HeadGroup p : topList2) {
			getRowSpan(p);
			int spanRange = p.getNextOne(p)!=null?rowSpan:1;
			rowSpan = 0;
			int[] sp =null;
			if(spanRange>1){
				sp = new int[3];
				sp[0] = span;
				sp[1] = rowCacuHeight;
				sp[2] = spanRange;
				Object[] totalInfo = new Object[3];
				totalInfo[0] = rowCacuHeight;
				totalInfo[1] = span;
				totalInfo[2] = "total of '"+p+"'";
				this.insertInfo.add(totalInfo);
			}
			rowTableData[span][rowCacuHeight] = p.getValue();
			span = span+spanRange;
			if(sp!=null)
				spanInfo.add(sp);
		}
		List<HeadGroup> nextList = topList2.get(0).getNext(topList2);
		rowHead[rowCacuHeight]="";
		rowCacuHeight++;
		BuilderRowHeader(nextList);
	}
	
	public void getRowSpan(HeadGroup p){
		List<HeadGroup> list = p.getNextOne(p);
		if(list!=null)
		for(HeadGroup g:list){
			if(g.getHash2()!=null){
				rowSpan = rowSpan+1;
			}
			else{
				getRowSpan(g);
			}
		}
		
	}
	/*
	 * the total info to be inserted into the row header and data model
	 * Object[]: 0. (int)height from where the total span start 1.(int) the start row index of the total span 2.(string) the content of the total like("total of ±±¾©µê")
	 */
	List<Object[]> insertInfo = new ArrayList<Object[]>();
	
	/*
	 * Build column header
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