package My;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;

import javax.swing.CellRendererPane;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.plaf.basic.BasicTableUI;
import javax.swing.table.TableCellRenderer;

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

	@Override
	public void installUI(JComponent c) {
		super.installUI(c);
		rendererPane = new MyCellRendererPane();
	}


	private void paintRow(int row, Graphics g) {
		Rectangle clipRect = g.getClipBounds();
		for (int col = 0; col < table.getColumnCount(); col++) {
			Rectangle cellRect = ((MyTable)table).getCellRect(row, col, true);
			if (cellRect.intersects(clipRect)) {
				MyTableModel model = (MyTableModel) table.getModel();
				MyTableModel.SpanArea s = model.isSpaened(row, col);
//				paintCell(row, col, g, cellRect);
				if (s==null) {
					paintCell(row, col, g, cellRect);
				}
				else if(model.isLeadingCell(row, col)){
					paintCell(s.getLeadingCell()[0], s.getLeadingCell()[1], g, cellRect);
				}
				else{
//					paintBackgroundOnly(row, col, g, cellRect);
//					paintCell(row, col, g, cellRect);
//					System.out.println("this spanned cell "+row + col);
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
			Component component = table.prepareRenderer(renderer, row,column);
			if (component.getParent() == null)
				rendererPane.add(component);
//			rendererPane.paintComponent(g, component, table, area.x,
//					area.y, area.width, area.height, true);
			rendererPane.paintComponent(g, component, table, area.x,
					area.y, area.width, area.height, true);
		}
	}
	
	public void paintBackgroundOnly(int row, int column, Graphics g, Rectangle area){
		TableCellRenderer renderer = table.getCellRenderer(row, column);
		Component component = table.prepareRenderer(renderer, row,column);
		((MyCellRendererPane)rendererPane).paintComponentBackground(g,component, table, area.x,
				area.y, area.width, area.height, true);
		}
	}
	
	class MyCellRendererPane extends CellRendererPane{
		 /**
	     * Paint a cell renderer component c on graphics object g.  Before the component
	     * is drawn it's reparented to this (if that's necessary), it's bounds
	     * are set to w,h and the graphics object is (effectively) translated to x,y.
	     * If it's a JComponent, double buffering is temporarily turned off. After
	     * the component is painted it's bounds are reset to -w, -h, 0, 0 so that, if
	     * it's the last renderer component painted, it will not start consuming input.
	     * The Container p is the component we're actually drawing on, typically it's
	     * equal to this.getParent(). If shouldValidate is true the component c will be
	     * validated before painted.
	     */
		@Override
	    public void paintComponent(Graphics g, Component c, Container p, int x, int y, int w, int h, boolean shouldValidate) {
//			System.out.println("install success!");
			
	        if (c == null) {
	            if (p != null) {
	                Color oldColor = g.getColor();
	                g.setColor(p.getBackground());
	                g.fillRect(x, y, w, h);
	                g.setColor(oldColor);
	            }
	            return;
	        }

	        if (c.getParent() != this) {
	            this.add(c);
	        }

	        c.setBounds(x, y, w, h);

	        if(shouldValidate) {
	            c.validate();
	        }

	        boolean wasDoubleBuffered = false;
	        if ((c instanceof JComponent) && ((JComponent)c).isDoubleBuffered()) {
	            wasDoubleBuffered = true;
	            ((JComponent)c).setDoubleBuffered(false);
	        }

	        Graphics cg = g.create(x, y, w, h);
	        try {
	            c.paint(cg);
	        }
	        finally {
	            cg.dispose();
	        }

	        if (wasDoubleBuffered && (c instanceof JComponent)) {
	            ((JComponent)c).setDoubleBuffered(true);
	        }

	        c.setBounds(-w, -h, 0, 0);
	    }

		public void paintComponentBackground(Graphics g, Component component, Container p, int x, int y, int w, int h, boolean shouldValidate){
			g.fillRect(x, y, w, h);
		}
	}