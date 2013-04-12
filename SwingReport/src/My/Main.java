package My;

import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;


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
		main.setVisible(true);
		main.jt.widthAdapt();
	}

	public void initComponent() {
		// this.setLayout(new BorderLayout());
		js.setViewportView(jt);
		this.add(js);
		
	}
	
	class MySelectionListner implements ListSelectionListener{
		@Override
		public void valueChanged(ListSelectionEvent e) {	
		}
	}
}
