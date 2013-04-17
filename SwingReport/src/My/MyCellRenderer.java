package My;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;

import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.border.Border;
import javax.swing.plaf.basic.BasicTextAreaUI;
import javax.swing.table.TableCellRenderer;

class MyCellRenderer extends JComponent  implements TableCellRenderer {
	Border unselectedBorder = null;
	Border selectedBorder = null;
	boolean isBordered = true;

	public MyCellRenderer(boolean isBordered) {
		this.isBordered = isBordered;
//		setOpaque(true); // MUST do this for background to show up.
	}

	public Component getTableCellRendererComponent(JTable table, Object content, boolean isSelected, boolean hasFocus, int row, int column) {
		MyTableModel myModel = (MyTableModel)table.getModel();
		final boolean isSelectedf = isSelected; 
		final String contentf = content.toString();
//		JTextArea ren = new JTextArea(content+""+row+column);
		JTextArea ren = new JTextArea(){
			@Override
			public void paintComponent(Graphics g) {
				super.paintComponent(g);
			}
			
		};
		ren.setOpaque(true);
		if(isSelected){
			ren.setBackground(Color.CYAN);
		}
		ren.setText(content.toString());
		return ren;
	}
	
	class MyTextUI extends BasicTextAreaUI{
		@Override
		public void update(Graphics g, JComponent c) {
			if (c.isOpaque()) {
				System.out.println("install");
	            g.setColor(c.getBackground());
	            g.fillRect(0, 0, c.getWidth(),c.getHeight()/2);
	        }
	        paint(g, c);
		}
		@Override
	    protected void paintBackground(Graphics g) {
	        
	    }
		
	}
}