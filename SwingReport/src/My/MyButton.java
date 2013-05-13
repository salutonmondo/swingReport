package My;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.plaf.basic.BasicGraphicsUtils;

import com.sun.java.swing.plaf.windows.WindowsButtonUI;
import com.sun.java.swing.plaf.windows.WindowsGraphicsUtils;

public class MyButton extends JButton {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// 0 is the default ascend 1 is for descent
	int sort = 1;
	Polygon ext;
	Rectangle extSensor = new Rectangle();
	boolean hovered = false;

	public MyButton(String text) {
		super(text);
		// this.add(extArea);
		this.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (!extSensor.contains(e.getX(), e.getY()))
					if (sort == 1)
						sort = 0;
					else {
						sort = 1;
					}
				e.consume();
			}
		});
		this.addMouseMotionListener(new MouseAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {
				if (extSensor.contains(e.getX(), e.getY())) {
					hovered = true;
					e.getComponent().repaint();
				} else {
					hovered = false;
					e.getComponent().repaint();
				}

			}
		});
	}

	public void updateUI() {
		setUI(new MyButtonUI());
		// setUI(new WindowsButtonUI());
	}

	public static void main(String args[]) {
		JFrame jf = new JFrame();
		MyButton my = new MyButton("test");
		jf.add(my);
		jf.pack();
		jf.setVisible(true);
	}

	public class MyButtonUI extends WindowsButtonUI {
		@Override
		protected void paintText(Graphics g, AbstractButton b,
				Rectangle textRect, String text) {
			textRect.x = textRect.x - 8;
			WindowsGraphicsUtils.paintText(g, b, textRect, text,
					getTextShiftOffset());
			Color oldColor = g.getColor();
			Color newColor = new Color(172, 168, 153);
			g.setColor(newColor);
			Graphics2D g2d = (Graphics2D) g;
			g2d.setStroke(new BasicStroke(1f));
			Insets in = b.getInsets();
			int x1 = textRect.x + textRect.width + 8;
			int y1 = textRect.y + in.top;
			int x2 = textRect.x + textRect.width + 8;
			int y2 = textRect.y + textRect.height - in.bottom;
			g2d.drawLine(x1, y1, x2, y2);

			int p1x = x1 - 3;
			int p2x = x1 + 3;
			int py = y2 - (y2 - y1) / 2;
			if (sort == 0) {
				g2d.drawLine(p1x, py, x2, y2);
				g2d.drawLine(p2x, py, x2, y2);
			} else {
				g2d.drawLine(p1x, py, x1, y1);
				g2d.drawLine(p2x, py, x1, y1);
			}

			int[] xpoints = new int[] { x1 + 8, x1 + 8 + 4, x1 + 8 + 8 };
			int[] ypoints = new int[] { y1 + (y1 - 4) / 2,
					y1 + (y1 - 4) / 2 + 4, y1 + (y1 - 4) / 2 };
			Polygon extendTriangle = new Polygon(xpoints, ypoints, 3);

			extSensor.x = x1 + 4;
			extSensor.y = y1;
			extSensor.width = 16;
			extSensor.height = textRect.height - in.bottom;

			if (hovered) {
				g2d.setColor(new Color(255, 224, 133));
				g2d.fill3DRect(extSensor.x, extSensor.y, extSensor.width,
						extSensor.height, true);
			}
			g2d.setColor(oldColor);
			g2d.drawPolygon(extendTriangle);
			g2d.fillPolygon(extendTriangle);

		}

		@Override
		public Dimension getPreferredSize(JComponent c) {
			AbstractButton b = (AbstractButton) c;
			Dimension old = BasicGraphicsUtils.getPreferredButtonSize(b,
					b.getIconTextGap());
			Dimension d = new Dimension(old.width + 16, old.height);
			return d;
		}

		@Override
		protected void paintButtonPressed(Graphics g, AbstractButton b) {

		}

	}
}
