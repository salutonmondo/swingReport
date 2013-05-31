package My;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;



public class MyTableModel extends AbstractTableModel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	boolean showLineNumber;
	List<SpanArea> spanAreas = new ArrayList<SpanArea>();
	private String[] columnNames;
	Object[][] data;
	//the insertion info of the total row.
	List<Object[]> insertInfo;
	static int MODEL_TPE_ROW = 0;
	static int MODEL_TPE_DATA = 1;
	public static int SPAN_DIRECTION_VER = 0;
	public static int SPAN_DIRECTION_HOR = 1;
	public MyTableModel(boolean showLineNumber,String[] columnNames,Object[][] data,List<Object[]> insertInfo,int modelType) {
		super();
		this.columnNames=columnNames;
		this.data = data;
		this.showLineNumber = showLineNumber;
		this.insertInfo = insertInfo;
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
		else{
			
		}
			return data[rowIndex][columnIndex];
	}
	
	

	@Override
	public String getColumnName(int column) {
		if(showLineNumber)
			return column==0?"":columnNames[column-1];
		else
		    return columnNames[column];
	}

	public void addSpan(int x1, int y1, int x2, int y2,int type) {
		SpanArea s = new SpanArea();
		s.beginX = x1;
		s.endX = x2;
		s.beginY = y1;
		s.endY = y2;
		s.type = type;
		spanAreas.add(s);
	}
	
	public void addSpan(SpanArea s) {
		spanAreas.add(s);
	}

	public SpanArea isSpaened(int row, int col) {
		SpanArea area = null;
		for (SpanArea s : spanAreas) {
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
				continue;
		}
		return false;
	}
	
	

	class SpanArea {
		public int beginX;
		public int beginY;
		public int endX;
		public int endY;
		public int type; //0 for vertial 1 for horizental
		public String horizentalText ;
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
	    
	    @Override
	    public String toString(){
	    	if(type==0)
	    		return "v  "+beginX+"_"+beginY+"_"+endX+"_"+endY;
	    	else
	    		return "h  "+beginX+"_"+beginY+"_"+endX+"_"+endY;
	    }
	}



	public boolean isShowLineNumber() {
		return showLineNumber;
	}
	
	public List<int[]> getVerticalSpanInfo(){
		List<int[]> verticalSpanInfo = new ArrayList<int[]>();
		for(SpanArea s:this.spanAreas){
			if(s.type ==0 ){
				int[] tmp= new int[3];
				tmp[0] = s.beginX;
				tmp[1] = s.beginY;
				tmp[2] = s.endX-s.beginX+1;
				verticalSpanInfo.add(tmp);
			}
		}
		return verticalSpanInfo;
	}
	
	public List<Object[]> getHorizentalSpanInfo(){
		List<Object[]> horizenTalSpanInfo = new ArrayList<Object[]>();
		for(SpanArea s:this.spanAreas){
			if(s.type ==1 ){
				Object[] tmp= new Object[3];
				tmp[0] = s.beginX+1;
				tmp[1] = s.beginY;
				tmp[2] = s.horizentalText;
				horizenTalSpanInfo.add(tmp);
			}
		}
		return horizenTalSpanInfo;
	}
}