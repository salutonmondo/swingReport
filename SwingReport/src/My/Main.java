package My;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.util.HashSet;

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
            	main.setPreferredSize(new Dimension(800,600));
            	Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
            	main.setLocation(dim.width/2-main.getPreferredSize().width/2, dim.height/2-main.getPreferredSize().height/2);
            	main.initComponent();
            }
        });
	}
//
	public void initComponent() {
		String[][] data = new String[][] { { "1", "NK", "800", "A05","red" },{ "1", "NK", "800", "A05","yellow" },
				{ "1", "NK", "900", "A04","red" }, { "2", "NK", "900", "A05" ,"yellow"},
				{ "1", "TK", "400", "A05" ,"red"}, { "2", "TK", "300", "A05","y3ellow" },
				{ "3", "TK", "700", "A05" ,"red"},{ "1", "TK", "200", "A05" ,"yellow"} };
		String[] head = new String[] { "month", "depStore", "sales", "product","color" };
		js = new JScrollPane();
		HashSet<Integer> sumFields = new HashSet<Integer>();
		sumFields.add(2);
		TransForm converter = new TransForm(head, data,sumFields);
		MyTableModel model = new MyTableModel(false, head, data,null,MyTableModel.MODEL_TPE_DATA);
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
		centerTopPanel.setPreferredSize(new Dimension(0,0));
		centerPanel.add(centerTopPanel,BorderLayout.NORTH);
		centerPanel.setBackground(bgColor);
		centerPanel.add(js, BorderLayout.CENTER);
		this.add(centerPanel, BorderLayout.CENTER);
		
		JPanel jp = new JPanel();
		FlowLayout fl = new FlowLayout();
		fl.setHgap(0);
		fl.setVgap(0);
		fl.setAlignOnBaseline(true);
		jp.setLayout(fl);
		
//		jp.setBackground(bgColor);
		jp.setBackground(Color.yellow);
		
		int areaWidth = getPreferredSize().width/3;
		System.out.println(getPreferredSize().width/3);
		CustomPanel area1 = new CustomPanel(jt,"Row",new Dimension(areaWidth,20));
		
		Border border1 = BorderFactory.createMatteBorder(0, 0, 0, 3, Color.red);
		System.out.println(this.getInsets());
		area1.setBorder(border1);
		jp.add(area1);
		
		System.out.println(getPreferredSize().width/3);
		CustomPanel area2 = new CustomPanel(jt,"Column",new Dimension(areaWidth,20));
		Border border2 = BorderFactory.createMatteBorder(0, 0, 0, 3, Color.red);
		area2.setBorder(border2);
		jp.add(area2);
		
//		System.out.println("cddddddddd"+fl.preferredLayoutSize(area2));
		CustomPanel area3 = new CustomPanel(jt,"Data",new Dimension(getPreferredSize().width-(2*areaWidth+6),20));
		Border border3 = BorderFactory.createMatteBorder(0, 0, 0, 0, Color.red);
		area3.setBorder(border3);
		jp.add(area3);
		
		System.out.println("ccc"+fl.preferredLayoutSize(jp));
		
		this.add(jp, BorderLayout.PAGE_START);
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		pack();
//		jt.wrapLineNumberCol();
		setVisible(true);

	}
	
	
	class MySelectionListner implements ListSelectionListener{
		@Override
		public void valueChanged(ListSelectionEvent e) {	
		}
	}
}
