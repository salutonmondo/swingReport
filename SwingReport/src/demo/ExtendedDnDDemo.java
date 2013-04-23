package demo;

/*
*
* Copyright (c) 1998 Sun Microsystems, Inc. All Rights Reserved.
*
* Sun grants you ("Licensee") a non-exclusive, royalty free, license to use,
* modify and redistribute this software in source and binary code form,
* provided that i) this copyright notice and license appear on all copies of
* the software; and ii) Licensee does not utilize the software in a manner
* which is disparaging to Sun.
*
* This software is provided "AS IS," without a warranty of any kind. ALL
* EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING ANY
* IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR
* NON-INFRINGEMENT, ARE HEREBY EXCLUDED. SUN AND ITS LICENSORS SHALL NOT BE
* LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING
* OR DISTRIBUTING THE SOFTWARE OR ITS DERIVATIVES. IN NO EVENT WILL SUN OR ITS
* LICENSORS BE LIABLE FOR ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT,
* INDIRECT, SPECIAL, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER
* CAUSED AND REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF THE USE OF
* OR INABILITY TO USE SOFTWARE, EVEN IF SUN HAS BEEN ADVISED OF THE
* POSSIBILITY OF SUCH DAMAGES.
*
* This software is not designed or intended for use in on-line control of
* aircraft, air traffic, aircraft navigation or aircraft communications; or in
* the design, construction, operation or maintenance of any nuclear
* facility. Licensee represents and warrants that it will not use or
* redistribute the Software for such purposes.
*/

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.TransferHandler;
import javax.swing.table.DefaultTableModel;

public class ExtendedDnDDemo extends JPanel {
   
   public ExtendedDnDDemo() {
       super(new GridLayout(3,1));
       add(createArea());
       add(createList());
       add(createTable());
   }
   
   private JPanel createList() {
       DefaultListModel listModel = new DefaultListModel();
       listModel.addElement("List 0");
       listModel.addElement("List 1");
       listModel.addElement("List 2");
       listModel.addElement("List 3");
       listModel.addElement("List 4");
       listModel.addElement("List 5");
       listModel.addElement("List 6");
       listModel.addElement("List 7");
       listModel.addElement("List 8");
       
       JList list = new JList(listModel);
       list.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
       JScrollPane scrollPane = new JScrollPane(list);
       scrollPane.setPreferredSize(new Dimension(400,100));
       
       list.setDragEnabled(true);
       list.setTransferHandler(new ListTransferHandler());
       
       JPanel panel = new JPanel(new BorderLayout());
       panel.add(scrollPane, BorderLayout.CENTER);
       panel.setBorder(BorderFactory.createTitledBorder("List"));
       return panel;
   }
   
   private JPanel createArea() {
       String text = "This is the text that I want to show.";
       
       JTextArea area = new JTextArea();
       area.setText(text);
       area.setDragEnabled(true);
       JScrollPane scrollPane = new JScrollPane(area);
       scrollPane.setPreferredSize(new Dimension(400,100));
       JPanel panel = new JPanel(new BorderLayout());
       panel.add(scrollPane, BorderLayout.CENTER);
       panel.setBorder(BorderFactory.createTitledBorder("Text Area"));
       return panel;
   }

   private JPanel createTable() {
       DefaultTableModel model = new DefaultTableModel();
       
       model.addColumn("Column 0");
       model.addColumn("Column 1");
       model.addColumn("Column 2");
       model.addColumn("Column 3");
       
       model.addRow(new String[]{"Table 00", "Table 01",
                                 "Table 02", "Table 03"});
       model.addRow(new String[]{"Table 10", "Table 11",
                                 "Table 12", "Table 13"});
       model.addRow(new String[]{"Table 20", "Table 21",
                                 "Table 22", "Table 23"});
       model.addRow(new String[]{"Table 30", "Table 31",
                                 "Table 32", "Table 33"});

       JTable table = new JTable(model);
       table.getTableHeader().setReorderingAllowed(false);
       table.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);

       JScrollPane scrollPane = new JScrollPane(table);
       scrollPane.setPreferredSize(new Dimension(400,100));

       table.setDragEnabled(true);
       table.setTransferHandler(new TableTransferHandler());

       JPanel panel = new JPanel(new BorderLayout());
       panel.add(scrollPane, BorderLayout.CENTER);
       panel.setBorder(BorderFactory.createTitledBorder("Table"));
       return panel;
   }

   /**
    * Create the GUI and show it.  For thread safety,
    * this method should be invoked from the
    * event-dispatching thread.
    */
   private static void createAndShowGUI() {
       //Create and set up the window.
       JFrame frame = new JFrame("ExtendedDnDDemo");
       frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

       //Create and set up the content pane.
       JComponent newContentPane = new ExtendedDnDDemo();
       newContentPane.setOpaque(true); //content panes must be opaque
       frame.setContentPane(newContentPane);

       //Display the window.
       frame.pack();
       frame.setVisible(true);
   }

   public static void main(String[] args) {
       //Schedule a job for the event-dispatching thread:
       //creating and showing this application's GUI.
       javax.swing.SwingUtilities.invokeLater(new Runnable() {
           public void run() {
               createAndShowGUI();
           }
       });
   }
}

abstract class StringTransferHandler extends TransferHandler {
   
   protected abstract String exportString(JComponent c);
   protected abstract void importString(JComponent c, String str);
   protected abstract void cleanup(JComponent c, boolean remove);
   
   protected Transferable createTransferable(JComponent c) {
       return new StringSelection(exportString(c));
   }
   
   public int getSourceActions(JComponent c) {
       return COPY_OR_MOVE;
   }
   
   public boolean importData(JComponent c, Transferable t) {
       if (canImport(c, t.getTransferDataFlavors())) {
           try {
               String str = (String)t.getTransferData(DataFlavor.stringFlavor);
               importString(c, str);
               return true;
           } catch (UnsupportedFlavorException ufe) {
           } catch (IOException ioe) {
           }
       }

       return false;
   }
   
   protected void exportDone(JComponent c, Transferable data, int action) {
       cleanup(c, action == MOVE);
   }
   
   public boolean canImport(JComponent c, DataFlavor[] flavors) {
       for (int i = 0; i < flavors.length; i++) {
           if (DataFlavor.stringFlavor.equals(flavors[i])) {
               return true;
           }
       }
       return false;
   }
}
class ListTransferHandler extends StringTransferHandler {
   private int[] indices = null;
   private int addIndex = -1; //Location where items were added
   private int addCount = 0;  //Number of items added.
           
   //Bundle up the selected items in the list
   //as a single string, for export.
   protected String exportString(JComponent c) {
       JList list = (JList)c;
       indices = list.getSelectedIndices();
       Object[] values = list.getSelectedValues();
       
       StringBuffer buff = new StringBuffer();

       for (int i = 0; i < values.length; i++) {
           Object val = values[i];
           buff.append(val == null ? "" : val.toString());
           if (i != values.length - 1) {
               buff.append("\n");
           }
       }
       
       return buff.toString();
   }

   //Take the incoming string and wherever there is a
   //newline, break it into a separate item in the list.
   protected void importString(JComponent c, String str) {
       JList target = (JList)c;
       DefaultListModel listModel = (DefaultListModel)target.getModel();
       int index = target.getSelectedIndex();

       //Prevent the user from dropping data back on itself.
       //For example, if the user is moving items #4,#5,#6 and #7 and
       //attempts to insert the items after item #5, this would
       //be problematic when removing the original items.
       //So this is not allowed.
       if (indices != null && index >= indices[0] - 1 &&
             index <= indices[indices.length - 1]) {
           indices = null;
           return;
       }

       int max = listModel.getSize();
       if (index < 0) {
           index = max;
       } else {
           index++;
           if (index > max) {
               index = max;
           }
       }
       addIndex = index;
       String[] values = str.split("\n");
       addCount = values.length;
       for (int i = 0; i < values.length; i++) {
           listModel.add(index++, values[i]);
       }
   }

   //If the remove argument is true, the drop has been
   //successful and it's time to remove the selected items 
   //from the list. If the remove argument is false, it
   //was a Copy operation and the original list is left
   //intact.
   protected void cleanup(JComponent c, boolean remove) {
       if (remove && indices != null) {
           JList source = (JList)c;
           DefaultListModel model  = (DefaultListModel)source.getModel();
           //If we are moving items around in the same list, we
           //need to adjust the indices accordingly, since those
           //after the insertion point have moved.
           if (addCount > 0) {
               for (int i = 0; i < indices.length; i++) {
                   if (indices[i] > addIndex) {
                       indices[i] += addCount;
                   }
               }
           }
           for (int i = indices.length - 1; i >= 0; i--) {
               model.remove(indices[i]);
           }
       }
       indices = null;
       addCount = 0;
       addIndex = -1;
   }
}

class TableTransferHandler extends StringTransferHandler {
   private int[] rows = null;
   private int addIndex = -1; //Location where items were added
   private int addCount = 0;  //Number of items added.

   protected String exportString(JComponent c) {
       JTable table = (JTable)c;
       rows = table.getSelectedRows();
       int colCount = table.getColumnCount();
       
       StringBuffer buff = new StringBuffer();
       
       for (int i = 0; i < rows.length; i++) {
           for (int j = 0; j < colCount; j++) {
               Object val = table.getValueAt(rows[i], j);
               buff.append(val == null ? "" : val.toString());
               if (j != colCount - 1) {
                   buff.append(",");
               }
           }
           if (i != rows.length - 1) {
               buff.append("\n");
           }
       }
       
       return buff.toString();
   }

   protected void importString(JComponent c, String str) {
       JTable target = (JTable)c;
       DefaultTableModel model = (DefaultTableModel)target.getModel();
       int index = target.getSelectedRow();

       //Prevent the user from dropping data back on itself.
       //For example, if the user is moving rows #4,#5,#6 and #7 and
       //attempts to insert the rows after row #5, this would
       //be problematic when removing the original rows.
       //So this is not allowed.
       if (rows != null && index >= rows[0] - 1 &&
             index <= rows[rows.length - 1]) {
           rows = null;
           return;
       }

       int max = model.getRowCount();
       if (index < 0) {
           index = max;
       } else {
           index++;
           if (index > max) {
               index = max;
           }
       }
       addIndex = index;
       String[] values = str.split("\n");
       addCount = values.length;
       int colCount = target.getColumnCount();
       for (int i = 0; i < values.length && i < colCount; i++) {
           model.insertRow(index++, values[i].split(","));
       }
   }
   protected void cleanup(JComponent c, boolean remove) {
       JTable source = (JTable)c;
       if (remove && rows != null) {
           DefaultTableModel model =
                (DefaultTableModel)source.getModel();

           //If we are moving items around in the same table, we
           //need to adjust the rows accordingly, since those
           //after the insertion point have moved.
           if (addCount > 0) {
               for (int i = 0; i < rows.length; i++) {
                   if (rows[i] > addIndex) {
                       rows[i] += addCount;
                   }
               }
           }
           for (int i = rows.length - 1; i >= 0; i--) {
               model.removeRow(rows[i]);
           }
       }
       rows = null;
       addCount = 0;
       addIndex = -1;
   }
}