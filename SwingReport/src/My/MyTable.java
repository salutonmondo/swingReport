package My;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JTable;
import javax.swing.JViewport;

import My.MyTableModel.SpanArea;

class MyTable extends JTable {
	/**
	 * 
	 */
	boolean showLineNumbers = true;
	private static final long serialVersionUID = 1L;
	MyTableModel m = (MyTableModel)this.getModel();
	
	
	public MyTable() {
		super(new MyTableModel(true));
		/*
		 * if the table shows line Number then define it's line No column Renderer
		 */
		if(showLineNumbers)this.columnModel.getColumn(0).setCellRenderer(new LineNumberRenderer(true));
		this.addMouseListener(new MouseAdapter(){

			@Override
			public void mouseClicked(MouseEvent e) {
				super.mouseClicked(e);
				System.out.println(e.getX()+"_"+e.getY());
				System.out.println(((JTable)e.getSource()).rowAtPoint(e.getPoint()));
			}
			
		});
		this.setUI(new MyTableUI());
	}
	
	public Rectangle getSapnRec(MyTableModel.SpanArea s){
		return super.getCellRect(s.beginX, s.beginY, true).union(super.getCellRect(s.endX, s.endY, true));
	}

	@Override 
	public Rectangle getCellRect(int row, int col, boolean b){
		SpanArea s = m.isSpaened(row, col);
		if(s!=null){
			Rectangle or = getSapnRec(s);
			return or;
		}
		else{
			return super.getCellRect(row,col,true);
		}
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
	}

	@Override
	public void repaint(Rectangle r) {
		for(MyTableModel.SpanArea s:m.spanAreas){
			super.repaint(getSapnRec(s));
		}
		super.repaint(r);
	}
	
	public void widthAdapt(){
		JViewport js = (JViewport)this.getParent();
		System.out.println(js.getVisibleRect());
		Rectangle vr = js.getVisibleRect();
		System.out.println(vr.getHeight());
		int first = rowAtPoint(vr.getLocation());
		int last = rowAtPoint());
		System.out.println(first+"--"+last);
	}
	
}