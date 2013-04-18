package My;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;



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
			{ "Joe", "Brown", "Pool", new Integer(10), new Boolean(false) },
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
//		this.addSpan(2, 2, 2, 3);
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
			return columnIndex==0?rowIndex+1:data[rowIndex][columnIndex-1];
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
		System.out.println("cell1 " + x1 + "," + y1 + " cell2" + x2 + ","+ y2);
	}

	public SpanArea isSpaened(int row, int col) {
		SpanArea area = null;
		for (SpanArea s : spanAreas) {
//			if ((row > s.beginX && row <= s.endX && col > s.beginY && col <= s.endY)
//					|| ((row == s.beginX && col > s.beginY && col <= s.endY) || (col == s.beginY && row > s.beginX
//							& row <= s.endX)))
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