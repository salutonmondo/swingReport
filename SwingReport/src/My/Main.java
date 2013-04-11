package My;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.border.Border;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.plaf.basic.BasicTableUI;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;

import My.Main.MyTableModel.SpanArea;

public class Main extends JFrame {
	JScrollPane js = new JScrollPane();
	JTable jt = new MyTable();
	JButton jb = new JButton("have a test!");
	BorderLayout bl = new BorderLayout();

	public static void main(String args[]) {
		Main main = new Main();
		main.initComponent();
		main.pack();
		main.setVisible(true);
	}

	public void initComponent() {
		// this.setLayout(new BorderLayout());
		js.setViewportView(jt);
		this.add(js);
	}

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
			if(showLineNumbers)
				this.columnModel.getColumn(0).setCellRenderer(new LineNumberRenderer(true));
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
		
		
		
	}

	class MyTableModel extends AbstractTableModel {
		boolean showLineNumber;
		List<SpanArea> spanAreas = new ArrayList<SpanArea>();
		private String[] columnNames = { "First Name", "Last Name", "Sport",
				"# of Years", "Vegetarian" };
		private Object[][] data = {
				{ "Kathy", "Smith", "Snowboarding", new Integer(5),
						new Boolean(false) },
				{ "John", "Doe", "Rowing", new Integer(3), new Boolean(true) },
				{ "Sue", "Black", "Knitting", new Integer(2),
						new Boolean(false) },
				{ "Jane", "White", "Speed reading", new Integer(20),
						new Boolean(true) },
				{ "Joe", "Brown", "Pool", new Integer(10), new Boolean(false) } };

		public MyTableModel(boolean showLineNumber) {
			super();
			this.showLineNumber = showLineNumber;
			this.addSpan(2, 2, 3, 3);
		}

		@Override
		public int getRowCount() {
			return data.length;
		}

		@Override
		public int getColumnCount() {
			return showLineNumber?columnNames.length+1:columnNames.length;
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			if(showLineNumber)
				return columnIndex==0?rowIndex:data[rowIndex][columnIndex-1];
			else
				return data[rowIndex][columnIndex];
		}
		
		

		@Override
		public String getColumnName(int column) {
			if(showLineNumber)
				return column==0?"":columnNames[column-1];
			else
			    return columnNames[column];
		}

		public void addSpan(int x1, int y1, int x2, int y2) {
			SpanArea s = new SpanArea();
			s.beginX = x1;
			s.endX = x2;
			s.beginY = y1;
			s.endY = y2;
			spanAreas.add(s);
			System.out.println("cell1 " + x1 + "," + y1 + " cell2" + x2 + ","
					+ y2);
		}

		public SpanArea isSpaened(int row, int col) {
			SpanArea area = null;
			for (SpanArea s : spanAreas) {
//				if ((row > s.beginX && row <= s.endX && col > s.beginY && col <= s.endY)
//						|| ((row == s.beginX && col > s.beginY && col <= s.endY) || (col == s.beginY && row > s.beginX
//								& row <= s.endX)))
				if(row>=s.beginX&&row<=s.endX&&col>=s.beginY&&col<=s.endY)
					area = s;
			}
			return area;
		}

		public boolean isLeadingCell(int row, int col) {
			for (SpanArea s : spanAreas) {
				if (row == s.beginX && col == s.beginY){
					return true;
				}
				else
					return false;
			}
			return false;
		}
		
		

		class SpanArea {
			int beginX;
			int beginY;
			int endX;
			int endY;
			
			public int getRows(){
				return endY-beginY+1;
			}
			public int getCols(){
				return endX-beginX+1;
			}
			
		    public int[] getLeadingCell(){
		    	int[] rowAndCol = new int[]{beginX,beginY};
		    	return rowAndCol;
		    }
		}
		
		
		

	}

	class MyTableUI extends BasicTableUI {
		@Override
		public void paint(Graphics g, JComponent c) {
			Rectangle r = g.getClipBounds();
			int firstRow = table.rowAtPoint(new Point(r.x, r.y));
			int lastRow = table.rowAtPoint(new Point(r.x, r.y + r.height));
			// -1 is a flag that the ending point is outside the table:
			if (lastRow < 0)
				lastRow = table.getRowCount() - 1;
			for (int row = firstRow; row <= lastRow; row++)
				paintRow(row, g);
		}

		private void paintRow(int row, Graphics g) {
			Rectangle clipRect = g.getClipBounds();
			for (int col = 0; col < table.getColumnCount(); col++) {
				Rectangle cellRect = table.getCellRect(row, col, true);
				if (cellRect.intersects(clipRect)) {
					MyTableModel model = (MyTableModel) table.getModel();
					MyTableModel.SpanArea s = model.isSpaened(row, col);
					if (s==null) {
						paintCell(row, col, g, cellRect);
					}
					else if(model.isLeadingCell(row, col)){
						paintCell(s.getLeadingCell()[0], s.getLeadingCell()[1], g, ((MyTable)table).getSapnRec(s));
					}

				}
			}
		}

		private void paintCell(int row, int column, Graphics g, Rectangle area) {
			int verticalMargin = table.getRowMargin();
			int horizontalMargin = table.getColumnModel().getColumnMargin();

			Color c = g.getColor();
			g.setColor(table.getGridColor());
			g.drawRect(area.x, area.y, area.width - 1, area.height - 1);
			g.setColor(c);

			area.setBounds(area.x + horizontalMargin / 2, area.y
					+ verticalMargin / 2, area.width - horizontalMargin,
					area.height - verticalMargin);

			if (table.isEditing() && table.getEditingRow() == row&& table.getEditingColumn() == column) {
				Component component = table.getEditorComponent();
				component.setBounds(area);
				component.validate();
			} else {
				TableCellRenderer renderer = table.getCellRenderer(row, column);
				Component component = table.prepareRenderer(renderer, row,
						column);
				if (component.getParent() == null)
					rendererPane.add(component);
				// g.drawString(row+","+column, 0, 0);
				
				rendererPane.paintComponent(g, component, table, area.x,
						area.y, area.width, area.height, true);
			}
		}
	}
	
	class MySelectionListner implements ListSelectionListener{

		@Override
		public void valueChanged(ListSelectionEvent e) {
						
		}
		
	}
	
	class LineNumberRenderer extends JLabel  implements TableCellRenderer {
		Border unselectedBorder = null;
		Border selectedBorder = null;
		boolean isBordered = true;

		public LineNumberRenderer(boolean isBordered) {
			this.isBordered = isBordered;
//			setOpaque(true); // MUST do this for background to show up.
		}

		public Component getTableCellRendererComponent(JTable table, Object content, boolean isSelected, boolean hasFocus, int row, int column) {
			setBackground(Color.gray);
			JLabel ren =  new JLabel(content.toString()+"999999");
			System.out.println(ren.getPreferredSize());
			return ren;
		}
	}
	
	
}
