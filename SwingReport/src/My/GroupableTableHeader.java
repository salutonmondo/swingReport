package My;

/*
 * (swing1.1beta3)
 * 
 */


import java.util.*;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;

import javax.swing.*;
import javax.swing.plaf.TableHeaderUI;
import javax.swing.table.*;

 

/**
  * GroupableTableHeader
  *
  * @version 1.0 10/20/98
  * @author Nobuo Tamemasa
  */

public class GroupableTableHeader extends JTableHeader implements DragGestureListener, DragSourceListener{
//  private static final String uiClassID = "GroupableTableHeaderUI";
  public Vector columnGroups = null;
  DragSource dragSource;
  public boolean isAdded = false;
  public GroupableTableHeader(TableColumnModel model) {
    super(model);
    setUI(new GroupableTableHeaderUI());
    setReorderingAllowed(false);
    
    dragSource = new DragSource();
    dragSource.createDefaultDragGestureRecognizer(this, DnDConstants.ACTION_COPY_OR_MOVE, this);
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
  
  
  @Override
	public void dragEnter(DragSourceDragEvent dsde) {
		// TODO Auto-generated method stub

	}

	@Override
	public void dragOver(DragSourceDragEvent dsde) {
		// TODO Auto-generated method stub

	}

	@Override
	public void dropActionChanged(DragSourceDragEvent dsde) {
		// TODO Auto-generated method stub

	}

	@Override
	public void dragExit(DragSourceEvent dse) {
		// TODO Auto-generated method stub

	}

	@Override
	public void dragDropEnd(DragSourceDropEvent dsde) {
		// TODO Auto-generated method stub

	}

	@Override
	public void dragGestureRecognized(DragGestureEvent evt) {
		Container c = this.getTable().getParent().getParent();
		int colNo = this.columnAtPoint(evt.getDragOrigin());
		boolean isShowLineNo = ((MyTableModel)table.getModel()).isShowLineNumber();
		int tmp = isShowLineNo?colNo-1:colNo;
		ColumnGroup g = (ColumnGroup)columnGroups.get(tmp);
		Transferable t = new StringSelection(colNo+"");
		if(g.v.size()==0)
			dragSource.startDrag(evt, DragSource.DefaultCopyDrop, t, this);
	}

	
  
}
