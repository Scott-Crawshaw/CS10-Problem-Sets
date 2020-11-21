import java.util.ArrayList;
import java.util.List;
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

/**
 * Client-server graphical editor
 * 
 * @author Chris Bailey-Kellogg, Dartmouth CS 10, Fall 2012; loosely based on CS 5 code by Tom Cormen
 * @author CBK, winter 2014, overall structure substantially revised
 * @author Travis Peters, Dartmouth CS 10, Winter 2015; remove EditorCommunicatorStandalone (use echo server for testing)
 * @author CBK, spring 2016 and Fall 2016, restructured Shape and some of the GUI
 */

/**
 * May 27th 2019
 * PS-6
 * 
 * @author Scott Crawshaw
 * @author Kunaal Verma
 */

public class Editor extends JFrame {	
	private static String serverIP = "localhost";			// IP address of sketch server
	// "localhost" for your own machine;
	// or ask a friend for their IP address

	private static final int width = 800, height = 800;		// canvas size

	// Current settings on GUI
	public enum Mode {
		DRAW, MOVE, RECOLOR, DELETE
	}
	private Mode mode = Mode.DRAW;				// drawing/moving/recoloring/deleting objects
	private String shapeType = "ellipse";		// type of object to add
	private Color color = Color.black;			// current drawing color
	// Drawing state
	// these are remnants of my implementation; take them as possible suggestions or ignore them
	private Shape shape;					// current shape (if any) being drawn
	private Sketch sketch;						// holds and handles all the completed objects
	private int movingId = -1;					// current shape id (if any; else -1) being moved
	private Point drawFrom = null;				// where the drawing started
	private Point moveFrom = null;				// where object is as it's being dragged


	// Communication
	private EditorCommunicator comm;			// communication with the sketch server

	public Editor() {
		super("Graphical Editor");

		sketch = new Sketch("");

		// Connect to server
		comm = new EditorCommunicator(serverIP, this);
		comm.start();

		// Helpers to create the canvas and GUI (buttons, etc.)
		JComponent canvas = setupCanvas();
		JComponent gui = setupGUI();

		// Put the buttons and canvas together into the window
		Container cp = getContentPane();
		cp.setLayout(new BorderLayout());
		cp.add(canvas, BorderLayout.CENTER);
		cp.add(gui, BorderLayout.NORTH);

		// Usual initialization
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		pack();
		setVisible(true);
	}

	/**
	 * Creates a component to draw into
	 */
	private JComponent setupCanvas() {
		JComponent canvas = new JComponent() {
			public void paintComponent(Graphics g) {
				super.paintComponent(g);
				drawSketch(g);
			}
		};

		canvas.setPreferredSize(new Dimension(width, height));

		canvas.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent event) {
				handlePress(event.getPoint());
			}

			public void mouseReleased(MouseEvent event) {
				handleRelease();
			}
		});		

		canvas.addMouseMotionListener(new MouseAdapter() {
			public void mouseDragged(MouseEvent event) {
				handleDrag(event.getPoint());
			}
		});

		return canvas;
	}

	/**
	 * Creates a panel with all the buttons
	 */
	private JComponent setupGUI() {
		// Select type of shape
		String[] shapes = {"ellipse", "freehand", "rectangle", "segment"};
		JComboBox<String> shapeB = new JComboBox<String>(shapes);
		shapeB.addActionListener(e -> shapeType = (String)((JComboBox<String>)e.getSource()).getSelectedItem());

		// Select drawing/recoloring color
		// Following Oracle example
		JButton chooseColorB = new JButton("choose color");
		JColorChooser colorChooser = new JColorChooser();
		JLabel colorL = new JLabel();
		colorL.setBackground(Color.black);
		colorL.setOpaque(true);
		colorL.setBorder(BorderFactory.createLineBorder(Color.black));
		colorL.setPreferredSize(new Dimension(25, 25));
		JDialog colorDialog = JColorChooser.createDialog(chooseColorB,
				"Pick a Color",
				true,  //modal
				colorChooser,
				e -> { color = colorChooser.getColor(); colorL.setBackground(color); },  // OK button
				null); // no CANCEL button handler
		chooseColorB.addActionListener(e -> colorDialog.setVisible(true));

		// Mode: draw, move, recolor, or delete
		JRadioButton drawB = new JRadioButton("draw");
		drawB.addActionListener(e -> mode = Mode.DRAW);
		drawB.setSelected(true);
		JRadioButton moveB = new JRadioButton("move");
		moveB.addActionListener(e -> mode = Mode.MOVE);
		JRadioButton recolorB = new JRadioButton("recolor");
		recolorB.addActionListener(e -> mode = Mode.RECOLOR);
		JRadioButton deleteB = new JRadioButton("delete");
		deleteB.addActionListener(e -> mode = Mode.DELETE);
		ButtonGroup modes = new ButtonGroup(); // make them act as radios -- only one selected
		modes.add(drawB);
		modes.add(moveB);
		modes.add(recolorB);
		modes.add(deleteB);
		JPanel modesP = new JPanel(new GridLayout(1, 0)); // group them on the GUI
		modesP.add(drawB);
		modesP.add(moveB);
		modesP.add(recolorB);
		modesP.add(deleteB);

		// Put all the stuff into a panel
		JComponent gui = new JPanel();
		gui.setLayout(new FlowLayout());
		gui.add(shapeB);
		gui.add(chooseColorB);
		gui.add(colorL);
		gui.add(modesP);
		return gui;
	}

	/**
	 * Getter for the sketch instance variable
	 */
	public Sketch getSketch() {
		return sketch;
	}

	/**
	 * Draws all the shapes in the sketch,
	 * along with the object currently being drawn in this editor (not yet part of the sketch)
	 */
	public void drawSketch(Graphics g) {
		if(shape != null) {
			shape.draw(g);
		}
		sketch.drawShapes(g);
		repaint();
	}

	public void handleMessage(String message) {
		if (!message.equals("")){ //make sure message is not empty
			
			//message format: TYPE,message
			String[] messageArr = message.split(",");
			String type = messageArr[0];
			
			if(type.equals("DRAW")) {
				sketch.addShape(message.substring(message.indexOf(",") + 1));
			}
			else if(type.equals("MOVE")) {
				sketch.moveShape(message.substring(message.indexOf(",") + 1));
			}
			else if(type.equals("RECOLOR")) {
				sketch.recolorShape(message.substring(message.indexOf(",") + 1));
			}
			else if(type.equals("DELETE")) {
				sketch.removeShape(message.substring(message.indexOf(",") + 1));
			}
			else {
				sketch = new Sketch(message);
			}
			repaint();
		}
	}

	// Helpers for event handlers

	/**
	 * Helper method for press at point
	 * In drawing mode, start a new object;
	 * in moving mode, (request to) start dragging if clicked in a shape;
	 * in recoloring mode, (request to) change clicked shape's color
	 * in deleting mode, (request to) delete clicked shape
	 */
	private void handlePress(Point p) {
		// In drawing mode, start drawing a new shape
		if(mode == Mode.DRAW) {
			if(shapeType.equals("ellipse")) {
				shape = new Ellipse(p.x, p.y, color);
			}
			if(shapeType.equals("rectangle")) {
				shape = new Rectangle(p.x, p.y, color);
			}
			if(shapeType.equals("segment")) {
				shape = new Segment(p.x, p.y, color);
			}
			if(shapeType.equals("freehand")) {
				shape = new Polyline(p.x, p.y, color);
			}
			drawFrom = p;
			repaint();
		}
		// In moving mode, start dragging if clicked in the shape
		if(mode == Mode.MOVE) {
			//TODO: Request to start moving?
			int id = sketch.shapeContains(p);
			Shape chosenShape = sketch.getShape(id);
			if(chosenShape != null) {
				moveFrom = p;
				movingId = id;
			}
		}
		// In recoloring mode, change the shape's color if clicked in it
		if(mode == Mode.RECOLOR) {
			int chosenShapeID = sketch.shapeContains(p);
			if(chosenShapeID != -1) {
				comm.send("RECOLOR," + chosenShapeID + "," + color.getRGB());
			}
		}
		// In deleting mode, delete the shape if clicked in it
		if(mode == Mode.DELETE) {
			int id;
			if((id = sketch.shapeContains(p))!= -1) {
				comm.send("DELETE," + id);
			}
		}
	}

	/**
	 * Helper method for drag to new point
	 * In drawing mode, update the other corner of the object;
	 * in moving mode, (request to) drag the object
	 */
	private void handleDrag(Point p) {
		// In drawing mode, revise the shape as it is stretched out
		if(mode == Mode.DRAW) {
			if(shape!=null) {
				if(shapeType.equals("ellipse")) {
					((Ellipse) shape).setCorners(drawFrom.x, drawFrom.y, p.x, p.y);
				}
				if(shapeType.equals("rectangle")) {
					((Rectangle) shape).setCorners(drawFrom.x, drawFrom.y, p.x, p.y);
				}
				if(shapeType.equals("segment")) {
					((Segment) shape).setEnd(p.x, p.y);
				}
				if(shapeType.equals("freehand")) {
					((Polyline) shape).addPoint(p.x, p.y);
				}
				repaint();
			}
		}
		// In moving mode, shift the object and keep track of where next step is from
		if(mode == Mode.MOVE) {
			if(movingId != -1) {
				comm.send("MOVE," + movingId + "," + (p.x - moveFrom.x) + "|" + (p.y - moveFrom.y));
				moveFrom = p;
			}
		}
	}

	/**
	 * Helper method for release
	 * In drawing mode, pass the add new object request on to the server;
	 * in moving mode, release it		
	 */
	private void handleRelease() {
		if(mode == Mode.MOVE) {
			moveFrom = null;
			movingId = -1;
		}

		if(mode == Mode.DRAW) {
			if(shape!=null) {
				comm.send("DRAW,"+shapeType+","+shape);
				shape=null;
			}
		}
	}	

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new Editor();
			}
		});	
	}
}
