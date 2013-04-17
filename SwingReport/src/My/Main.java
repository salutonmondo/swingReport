package My;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumnModel;


@SuppressWarnings("serial")
public class Main extends JFrame {
	JScrollPane js = new JScrollPane();
	MyTable jt = new MyTable();
	JButton jb = new JButton("have a test!");
	BorderLayout bl = new BorderLayout();

	public static void main(String args[]) {
		Main main = new Main();
		main.initComponent();
		main.pack();
		main.jt.wrapLineNumberCol();
		main.setVisible(true);
	}

	public void initComponent() {
		js.setViewportView(jt);
		TableColumnModel cm = jt.getColumnModel();
		ColumnGroup g_name = new ColumnGroup("Name");
		g_name.add(cm.getColumn(1));
		g_name.add(cm.getColumn(2));
		ColumnGroup g_lang = new ColumnGroup("Language");
		g_lang.add(cm.getColumn(3));
		g_lang.add(cm.getColumn(4));
		GroupableTableHeader header = (GroupableTableHeader) jt.getTableHeader();
		header.addColumnGroup(g_name);
		header.addColumnGroup(g_lang);
		this.setLayout(bl);
		this.add(js, BorderLayout.CENTER);
		JPanel jp = new JPanel();
		jp.setBackground(Color.RED);
		this.add(jp, BorderLayout.NORTH);

	}
	
	class MySelectionListner implements ListSelectionListener{
		@Override
		public void valueChanged(ListSelectionEvent e) {	
		}
	}
}
