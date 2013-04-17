package My;

/*
 * (swing1.1beta3)
 * 
 */


import java.util.*;
import java.awt.*;

import javax.swing.*;
import javax.swing.plaf.TableHeaderUI;
import javax.swing.table.*;

 

/**
  * GroupableTableHeader
  *
  * @version 1.0 10/20/98
  * @author Nobuo Tamemasa
  */

public class GroupableTableHeader extends JTableHeader {
//  private static final String uiClassID = "GroupableTableHeaderUI";
  protected Vector columnGroups = null;
    
  public GroupableTableHeader(TableColumnModel model) {
    super(model);
    setUI(new GroupableTableHeaderUI());
    setReorderingAllowed(false);
  }
  
  public void updateUI(){
//      setUI(this.getUI());

      TableCellRenderer renderer = getDefaultRenderer();
      if (renderer instanceof Component) {
          SwingUtilities.updateComponentTreeUI((Component)renderer);
      }
  }
  
  public void setReorderingAllowed(boolean b) {
    reorderingAllowed = false;
  }
    
  public void addColumnGroup(ColumnGroup g) {
    if (columnGroups == null) {
      columnGroups = new Vector();
    }
    columnGroups.addElement(g);
  }

  public Enumeration getColumnGroups(TableColumn col) {
    if (columnGroups == null) return null;
    Enumeration en = columnGroups.elements();
    while (en.hasMoreElements()) {
      ColumnGroup cGroup = (ColumnGroup)en.nextElement();
      Vector v_ret = (Vector)cGroup.getColumnGroups(col,new Vector());
      if (v_ret != null) { 
	return v_ret.elements();
      }
    }
    return null;
  }
  
  public void setColumnMargin() {
    if (columnGroups == null) return;
    int columnMargin = getColumnModel().getColumnMargin();
    Enumeration en = columnGroups.elements();
    while (en.hasMoreElements()) {
      ColumnGroup cGroup = (ColumnGroup)en.nextElement();
      cGroup.setColumnMargin(columnMargin);
    }
  }
  
}
