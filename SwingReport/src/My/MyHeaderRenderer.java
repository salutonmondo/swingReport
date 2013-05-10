package My;

import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.JTable;

import sun.swing.table.DefaultTableCellHeaderRenderer;

public class MyHeaderRenderer extends DefaultTableCellHeaderRenderer {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public Component getTableCellRendererComponent(JTable arg0, Object arg1,
			boolean arg2, boolean arg3, int arg4, int arg5) {
		super.getTableCellRendererComponent(arg0, arg1, arg2, arg3, arg4, arg5);
		this.setBorder(BorderFactory.createRaisedBevelBorder());
		return this;
	}
	

}
