package My;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;

import My.MyTableModel.SpanArea;
import dataTransform.TransForm;
import dataTransform.TransForm.HeadGroup;

public class MyTable extends JTable {
	/**
	 * 
	 */
	boolean showLineNumbers = true;
	private static final long serialVersionUID = 1L;
	MyTableModel m = (MyTableModel)this.getModel();
	TransForm converter;

	
	public MyTable(MyTableModel m,TransForm converter) {
		super(m);
		this.converter = converter;
		/*
		 * if the table shows line Number then define it's line No column Renderer
		 */
		if(showLineNumbers)this.columnModel.getColumn(0).setCellRenderer(new LineNumberRenderer(true));
		for(int i=1;i<this.columnModel.getColumnCount();i++){
			TableColumn tc = this.columnModel.getColumn(i);
			tc.setCellRenderer(new MyCellRenderer(true));
		}
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
		m = new MyTableModel(false,t.getResultHead(),t.getResultsData());
		this.setModel(m);
		GroupableTableHeader header = (GroupableTableHeader) getTableHeader();	
		HeadGroup hg = t.getColG();
		if (hg == null) {
			for (String s : t.getResultHead()) {
				ColumnGroup g = new ColumnGroup(s);
				header.addColumnGroup(g);
			}
			return;
		}
		List<HeadGroup> topList = hg.getNextOne(hg);
		Map<String,ColumnGroup> map = new HashMap<String,ColumnGroup>();
		for(int i=0;i<hg.getHeight();i++){
			Map<String,ColumnGroup> map2 = new HashMap<String,ColumnGroup>();
			if(topList == null) break;
			for(HeadGroup h:topList){
				ColumnGroup cg = new ColumnGroup(h.getValue());
				if(i==0){
					header.addColumnGroup(cg);
					map.put(cg.getText(), cg);
					map2.put(cg.getText(), cg);
				}else{
					ColumnGroup parentCol = map.get(h.getParentName());
					parentCol.add(cg);
					map2.put(cg.getText(), cg);
				}
				if(h.getNextOne(h)==null){
					for(Map.Entry<Integer, Integer> ent:h.getHash2().entrySet()){
						cg.add(this.getColumnModel().getColumn(ent.getValue()));
					}
				}else{

				}
				
			}
			map = map2;	
			topList = hg.getNext(topList);
		}
	}
	
}