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
import javax.swing.JScrollPane;
import javax.swing.JTable;
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
		public MyTable() {
			super(new MyTableModel());
			this.setUI(new MyTableUI());
		}

		@Override 
		public Rectangle getCellRect(int row, int col, boolean b){
			MyTableModel m = (MyTableModel)this.getModel();
			SpanArea s = m.isLeadingCell(row, col);
			if(s!=null){
				Rectangle or = super.getCellRect(row, col, true).union(super.getCellRect(s.endX, s.endY, true));
				System.out.println(or);
				return or;
			}
			else{
				return super.getCellRect(row,col,true);
			}
		}
	}

	class MyTableModel extends AbstractTableModel {
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

		public MyTableModel() {
			super();
			this.addSpan(2, 2, 3, 3);
		}

		@Override
		public int getRowCount() {
			return data.length;
		}

		@Override
		public int getColumnCount() {
			return columnNames.length;
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			return data[rowIndex][columnIndex];
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

		public boolean isSpaened(int row, int col) {
			boolean isSpaned = false;
			for (SpanArea s : spanAreas) {
				if ((row > s.beginX && row <= s.endX && col > s.beginY && col <= s.endY)
						|| ((row == s.beginX && col > s.beginY && col <= s.endY) || (col == s.beginY && row > s.beginX
								& row <= s.endX)))
					isSpaned = true;
			}
			return isSpaned;
		}

		public SpanArea isLeadingCell(int row, int col) {
			for (SpanArea s : spanAreas) {
				if (row == s.beginX && col == s.beginY){
					return s;
				}
				else
					return null;
			}
			return null;
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
					if (!model.isSpaened(row, col)) {
						paintCell(row, col, g, cellRect);
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

			if (table.isEditing() && table.getEditingRow() == row
					&& table.getEditingColumn() == column) {
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
	
}
