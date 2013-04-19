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
	MyTable jt ;
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
		String[] head = new String[] { "月份", "店铺", "销售", "商品" };
		String[][] data  = new String[][] { { "1", "上海店", "800", "A05" },
				{ "1", "上海店", "900", "A04" },
				{ "2", "上海店", "900", "A05" },
				{ "1", "北京店", "400", "A05" },
				{ "2", "北京店", "300", "A05" } };
		
		jt =  new MyTable(true,head,data);
		js.setViewportView(jt);
		
		
//		TableColumnModel cm = jt.getColumnModel();
//		ColumnGroup g_name = new ColumnGroup("Name");
//		g_name.add(cm.getColumn(1));
//		g_name.add(cm.getColumn(2));
//		ColumnGroup g_lang = new ColumnGroup("Language");
//		g_lang.add(cm.getColumn(3));
//		g_lang.add(cm.getColumn(4));
//		GroupableTableHeader header = (GroupableTableHeader) jt.getTableHeader();
//		header.addColumnGroup(g_name);
//		header.addColumnGroup(g_lang);
		
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
