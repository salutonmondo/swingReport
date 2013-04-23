package demo;

/*
 Core SWING Advanced Programming 
 By Kim Topley
 ISBN: 0 13 083292 8       
 Publisher: Prentice Hall  
 */

import java.awt.Font;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Iterator;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.UIManager;

public class JLabelDragSource implements DragGestureListener,
		DragSourceListener {
	public JLabelDragSource(JLabel label) {
		this.label = label;
		// Use the default DragSource
		DragSource dragSource = DragSource.getDefaultDragSource();

		// Create a DragGestureRecognizer and
		// register as the listener
		dragSource.createDefaultDragGestureRecognizer(label,
				DnDConstants.ACTION_COPY_OR_MOVE, this);
	}

	// Implementation of DragGestureListener interface.
	public void dragGestureRecognized(DragGestureEvent dge) {
		if (DnDUtils.isDebugEnabled()) {
			DnDUtils.debugPrintln("Initiating event is "
					+ dge.getTriggerEvent());
			DnDUtils.debugPrintln("Complete event set is:");
			Iterator iter = dge.iterator();
			while (iter.hasNext()) {
				DnDUtils.debugPrintln("\t" + iter.next());
			}
		}
		Transferable transferable = new JLabelTransferable(label);
		dge.startDrag(null, transferable, this);
	}

	// Implementation of DragSourceListener interface
	public void dragEnter(DragSourceDragEvent dsde) {
		DnDUtils.debugPrintln("Drag Source: dragEnter, drop action = "
				+ DnDUtils.showActions(dsde.getDropAction()));
	}

	public void dragOver(DragSourceDragEvent dsde) {
		DnDUtils.debugPrintln("Drag Source: dragOver, drop action = "
				+ DnDUtils.showActions(dsde.getDropAction()));
	}

	public void dragExit(DragSourceEvent dse) {
		DnDUtils.debugPrintln("Drag Source: dragExit");
	}

	public void dropActionChanged(DragSourceDragEvent dsde) {
		DnDUtils.debugPrintln("Drag Source: dropActionChanged, drop action = "
				+ DnDUtils.showActions(dsde.getDropAction()));
	}

	public void dragDropEnd(DragSourceDropEvent dsde) {
		DnDUtils.debugPrintln("Drag Source: drop completed, drop action = "
				+ DnDUtils.showActions(dsde.getDropAction()) + ", success: "
				+ dsde.getDropSuccess());
	}

	public static void main(String[] args) {
		try {
			UIManager
					.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		} catch (Exception evt) {
		}

		JFrame f = new JFrame("Draggable JLabel");
		JLabel label = new JLabel("Drag this text", JLabel.CENTER);
		label.setFont(new Font("Serif", Font.BOLD, 32));
		f.getContentPane().add(label);
		f.pack();
		f.setVisible(true);

		// Attach the drag source
		JLabelDragSource dragSource = new JLabelDragSource(label);
	}

	protected JLabel label; // The associated JLabel
}

class DnDUtils {
	public static String showActions(int action) {
		String actions = "";
		if ((action & (DnDConstants.ACTION_LINK | DnDConstants.ACTION_COPY_OR_MOVE)) == 0) {
			return "None";
		}

		if ((action & DnDConstants.ACTION_COPY) != 0) {
			actions += "Copy ";
		}

		if ((action & DnDConstants.ACTION_MOVE) != 0) {
			actions += "Move ";
		}

		if ((action & DnDConstants.ACTION_LINK) != 0) {
			actions += "Link";
		}

		return actions;
	}

	public static boolean isDebugEnabled() {
		return debugEnabled;
	}

	public static void debugPrintln(String s) {
		if (debugEnabled) {
			System.out.println(s);
		}
	}

	private static boolean debugEnabled = (System
			.getProperty("DnDExamples.debug") != null);
}

class JLabelTransferable implements Transferable {
	public JLabelTransferable(JLabel label) {
		this.label = label;
	}

	// Implementation of the Transferable interface
	public DataFlavor[] getTransferDataFlavors() {
		return flavors;
	}

	public boolean isDataFlavorSupported(DataFlavor fl) {
		for (int i = 0; i < flavors.length; i++) {
			if (fl.equals(flavors[i])) {
				return true;
			}
		}

		return false;
	}

	public Object getTransferData(DataFlavor fl) {
		if (!isDataFlavorSupported(fl)) {
			return null;
		}

		if (fl.equals(DataFlavor.stringFlavor)) {
			// String - return the text as a String
			return label.getText() + " (DataFlavor.stringFlavor)";
		} else if (fl.equals(jLabelFlavor)) {
			// The JLabel itself - just return the label.
			return label;
		} else {
			// Plain text - return an InputStream
			try {
				String targetText = label.getText() + " (plain text flavor)";
				int length = targetText.length();
				ByteArrayOutputStream os = new ByteArrayOutputStream();
				OutputStreamWriter w = new OutputStreamWriter(os);
				w.write(targetText, 0, length);
				w.flush();
				byte[] bytes = os.toByteArray();
				w.close();
				return new ByteArrayInputStream(bytes);
			} catch (IOException e) {
				return null;
			}
		}
	}

	// A flavor that transfers a copy of the JLabel
	public static final DataFlavor jLabelFlavor = new DataFlavor(JLabel.class,
			"Swing JLabel");

	private JLabel label; // The label being transferred

	private static final DataFlavor[] flavors = new DataFlavor[] {
			DataFlavor.stringFlavor,
			new DataFlavor("text/plain; charset=ascii", "ASCII text"),
			jLabelFlavor };
}
