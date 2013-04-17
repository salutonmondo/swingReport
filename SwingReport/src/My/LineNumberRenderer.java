package My;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.table.TableCellRenderer;

class LineNumberRenderer extends JLabel  implements TableCellRenderer {
	Border unselectedBorder = null;
	Border selectedBorder = null;
	boolean isBordered = true;

	public LineNumberRenderer(boolean isBordered) {
		this.isBordered = isBordered;
//		setOpaque(true); // MUST do this for background to show up.
	}

	public Component getTableCellRendererComponent(JTable table, Object content, boolean isSelected, boolean hasFocus, int row, int column) {
		setBackground(Color.gray);
		JLabel ren =  new JLabel(content.toString());
		ren.setBorder(javax.swing.BorderFactory.createEmptyBorder());
		ren.setHorizontalAlignment(SwingConstants.CENTER );
		return ren;
	}
}