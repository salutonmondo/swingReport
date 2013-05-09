package My;

import java.awt.Color;
import java.awt.Component;
import java.util.HashSet;

import javax.swing.BorderFactory;
import javax.swing.JTable;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableCellRenderer;

class MyCellRenderer  extends DefaultTableCellRenderer {
	Border unselectedBorder = null;
	Border selectedBorder = null;
	boolean isBordered = true;
	public Component getTableCellRendererComponent(JTable table, Object content, boolean isSelected, boolean hasFocus, int row, int column) {
		MyTable my = (MyTable)table;
		HashSet<Integer> set = my.totalRowSet;
		Color oriColor = table.getBackground();
		if(set.contains(row)){
			this.setBackground(new Color(251,255,226));
		}
		else{
			this.setBackground(oriColor);
		}
		this.setBorder(BorderFactory.createEmptyBorder());
		this.setText(content==null?"":content.toString());
		return this;
	}
}