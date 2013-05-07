package demo;
/*
Java Swing, 2nd Edition
By Marc Loy, Robert Eckstein, Dave Wood, James Elliott, Brian Cole
ISBN: 0-596-00408-7
Publisher: O'Reilly 
*/
// ScrollDemo2.java
//Another JScrollPane demonstration. This version activates some of the
//features of JScrollPane such as row and column headers.
//

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.ScrollPaneConstants;

public class ScrollDemo2 extends JFrame {

  JScrollPane scrollpane;

  public ScrollDemo2() {
    super("JScrollPane Demonstration");
    setSize(300, 200);
    setDefaultCloseOperation(EXIT_ON_CLOSE);
    init();
    setVisible(true);
  }

  public void init() {
    JRadioButton form[][] = new JRadioButton[12][5];
    String counts[] = { "", "0-1", "2-5", "6-10", "11-100", "101+" };
    String categories[] = { "Household", "Office", "Extended Family",
        "Company (US)", "Company (World)", "Team", "Will",
        "Birthday Card List", "High School", "Country", "Continent",
        "Planet" };
    JPanel p = new JPanel();
    p.setSize(600, 400);
    p.setLayout(new GridLayout(13, 6, 10, 0));
    for (int row = 0; row < 13; row++) {
      ButtonGroup bg = new ButtonGroup();
      for (int col = 0; col < 6; col++) {
        if (row == 0) {
          p.add(new JLabel(counts[col]));
        } else {
          if (col == 0) {
            p.add(new JLabel(categories[row - 1]));
          } else {
            form[row - 1][col - 1] = new JRadioButton();
            bg.add(form[row - 1][col - 1]);
            p.add(form[row - 1][col - 1]);
          }
        }
      }
    }
    scrollpane = new JScrollPane(p);

    // Add in some JViewports for the column and row headers
    JViewport jv1 = new JViewport();
    jv1.setView(new JLabel(new ImageIcon("columnlabel.gif")));
    scrollpane.setColumnHeader(jv1);
    JViewport jv2 = new JViewport();
    jv2.setView(new JLabel(new ImageIcon("rowlabel.gif")));
    scrollpane.setRowHeader(jv2);

    // And throw in an information button
    JButton jb1 = new JButton(new ImageIcon("question.gif"));
    jb1.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent ae) {
        JOptionPane.showMessageDialog(null,
            "This is an Active Corner!", "Information",
            JOptionPane.INFORMATION_MESSAGE);
      }
    });
    scrollpane.setCorner(ScrollPaneConstants.UPPER_LEFT_CORNER, jb1);
    getContentPane().add(scrollpane, BorderLayout.CENTER);
  }

  public static void main(String args[]) {
    new ScrollDemo2();
  }
}
