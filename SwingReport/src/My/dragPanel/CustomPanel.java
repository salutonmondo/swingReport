package My.dragPanel;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;

import My.ColumnGroup;
import My.GroupableTableHeader;
import My.MyTable;
import My.MyTableModel;
import dataTransform.TransForm;
import My.Main;

public class CustomPanel extends JPanel implements DropTargetListener{
	private Color normalHue;
	String areaName;
	private final Dimension preferredSize;
	protected final static float[] hue = {0.0f, 0.33f, 0.67f};
	protected DropTarget dropTarget;
	List<String> items = new ArrayList<String>();
	JTable table;
	
    public void paint(Graphics g) {
    	super.paint(g);
//    	normalHue = Color.getHSBColor(hue[0], 0.4f, 0.85f);
        int width = getWidth();
        int height = getHeight();
//        float alignmentX = getAlignmentX();
//          g.setColor(new Color(.3f, .4f, .5f, .6f));
//          Color color = Main.bgColor.brighter();
//          g.setColor(color);
//          g.fill3DRect(0, 0, width, height, true);
//        int count = 0;
//        int distance = 0;
//        for(String r:items){
//        	g.setColor(Color.getHSBColor(hue[count%4], 0.4f, 0.85f));
//        	g.fill3DRect(distance*80+2, 2, 80, 30, true);
//        	g.setColor(Color.BLACK);
//        	g.drawString(r, 3, 13);
//        	count++;
//        	distance++;
//        	if(count==3)
//        		count=0;
//        }
        /* Say what the alignment point is. */
        g.setColor(Color.black);
        g.drawString(areaName, 3, height - 3);
    }
    public boolean isOpaque() {
        return true;
    }
    public CustomPanel(JTable table ,String areaName,Dimension preferredSize){
    	super();
//    	setBackground(Main.bgColor);
    	setBackground(Color.green);
    	this.table = table;
    	this.preferredSize = preferredSize;
    	this.areaName = areaName;
    	dropTarget = new DropTarget(this,DnDConstants.ACTION_COPY_OR_MOVE,this, true, null);
		this.setLayout(new FlowLayout());
    }
    
    public Dimension getPreferredSize() {
        return preferredSize;
    }

    public Dimension getMinimumSize() {
        return preferredSize;
    }

    public Dimension getMaximumSize() {
        return preferredSize;
    }
	@Override
	public void dragEnter(DropTargetDragEvent dtde) {
		
	}
	@Override
	public void dragOver(DropTargetDragEvent dtde) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void dropActionChanged(DropTargetDragEvent dtde) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void dragExit(DropTargetEvent dte) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void drop(DropTargetDropEvent dtde) {
		Transferable t = dtde.getTransferable();
		try {
			String s = t.getTransferData(DataFlavor.stringFlavor).toString();
			boolean isShowLineNo = ((MyTableModel)table.getModel()).isShowLineNumber();
			
			int colNo = isShowLineNo?Integer.parseInt(s)-1:Integer.parseInt(s);
			GroupableTableHeader head = (GroupableTableHeader)table.getTableHeader();
			Vector v = head.columnGroups;
			ColumnGroup g = (ColumnGroup) v.get(colNo);
			JLabel newItem = new JLabel(g.getText());
			// apply transform
			TransForm converter = ((MyTable)table).getConverter();
			if(areaName.equals("Column")){
				converter.getColItem().add(colNo);
				deletItems(converter.getRowItem(),colNo	);
				deletItems(converter.getDataItem(),colNo	);
			}
			if(areaName.equals("Row")){
				converter.getRowItem().add(colNo);
				deletItems(converter.getDataItem(),colNo	);
				deletItems(converter.getColItem(),colNo	);
			}
			if(areaName.equals("Data")){
				converter.getDataItem().add(colNo);
				deletItems(converter.getRowItem(),colNo	);
				deletItems(converter.getColItem(),colNo	);
			}
			
			((MyTable)table).updateContent(converter,false,0);
			
			/*
			 * remove the label by double click;
			 */
			newItem.addMouseListener(new MouseAdapter(){
				@Override
				public void mouseClicked(MouseEvent e) {
					if(e.getClickCount()==2){
						JLabel label = (JLabel)e.getSource();
						Container parent = label.getParent();
						parent.remove(label);
						parent.repaint();
					}
					super.mouseClicked(e);
				}
			});
			this.add(newItem);
			this.updateUI();
			this.repaint();
		} catch (UnsupportedFlavorException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		
		dtde.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
		dtde.dropComplete(true);
	}

	public void act(String s){
		items.add(s);
	}
	
	public void deletItems(List<Integer> list,int item){
		for(int i=0;i<list.size();i++){
			if(list.get(i)==item)
				list.remove(i);
		}
	}
	
}
