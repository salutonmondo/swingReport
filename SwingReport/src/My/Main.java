package My;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.Border;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import My.dragPanel.CustomPanel;
import dataTransform.TransForm;


@SuppressWarnings("serial")
public class Main extends JFrame {
	public static Color bgColor = new Color(162,193,245);
	JScrollPane js;
	MyTable jt ;
	JButton jb = new JButton("have a test!");
	BorderLayout bl = new BorderLayout();

	public static void main(String args[]) throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {
		 UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
            	Main main = new Main();
            	main.initComponent();
            }
        });
	}

	public void initComponent() {
		String[][] data = new String[][] { { "1", "上海店", "800", "A05","red" },{ "1", "上海店", "800", "A05","yellow" },
				{ "1", "上海店", "900", "A04","red" }, { "2", "上海店", "900", "A05" ,"yellow"},
				{ "1", "北京店", "400", "A05" ,"red"}, { "2", "北京店", "300", "A05","yellow" },
				{ "3", "北京店", "700", "A05" ,"red"} };
		String[] head = new String[] { "月份", "店铺", "销售", "商品","颜色" };
		js = new JScrollPane();
		TransForm converter = new TransForm(head, data);
		MyTableModel model = new MyTableModel(false, head, data,null);
		JViewport viewport = new JViewport();
		JTable tmp = new JTable((Object[][])data,head);
		viewport.setView(tmp);
		viewport.setPreferredSize(new Dimension(0, 0));
		js.setRowHeaderView(viewport);
		jt = new MyTable(model, converter,tmp);
		js.setViewportView(jt);
		js.setBackground(bgColor);
		js.setBorder(BorderFactory.createEmptyBorder());

		 
		this.setLayout(bl);
		
		JPanel centerPanel = new JPanel();
		centerPanel.setLayout(new BorderLayout());
		JPanel centerTopPanel = new JPanel();
		centerTopPanel.setBackground(bgColor);
		centerTopPanel.setLayout(null);
//		centerTopPanel.setLayout(new FlowLayout());
		centerTopPanel.setPreferredSize(new Dimension(0,0));
		centerPanel.add(centerTopPanel,BorderLayout.NORTH);
		centerPanel.setBackground(bgColor);
		centerPanel.add(js, BorderLayout.CENTER);
		
		this.add(centerPanel, BorderLayout.CENTER);
		
		JPanel jp = new JPanel();
//		jp.setLayout(new GridBagLayout());
		FlowLayout fl = new FlowLayout();
		fl.setHgap(0);
		fl.setVgap(0);
		jp.setLayout(fl);
		jp.setBackground(bgColor);
//		jp.setBackground(Color.red);
		
		int areaWidth = getPreferredSize().width/3;
		CustomPanel area1 = new CustomPanel(jt,"Row",new Dimension(areaWidth,20));
		Border border1 = BorderFactory.createMatteBorder(0, 0, 0, 3, Color.red);
		area1.setBorder(border1);
		jp.add(area1);

		CustomPanel area2 = new CustomPanel(jt,"Column",new Dimension(areaWidth,20));
		Border border2 = BorderFactory.createMatteBorder(0, 0, 0, 3, Color.red);
		area2.setBorder(border2);
		jp.add(area2);
		
		jp.add(new CustomPanel(jt,"Data",new Dimension(getPreferredSize().width-2*areaWidth,20)));
		
		
		
		this.add(jp, BorderLayout.NORTH);
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		pack();
//		main.jt.wrapLineNumberCol();
		setVisible(true);

	}
	
	class MySelectionListner implements ListSelectionListener{
		@Override
		public void valueChanged(ListSelectionEvent e) {	
		}
	}
}
