import java.awt.Color;
import java.awt.Graphics;

/**
 * A rectangle-shaped Shape
 * Defined by an upper-left corner (x1,y1) and a lower-right corner (x2,y2)
 * with x1<=x2 and y1<=y2
 * 
 * @author Chris Bailey-Kellogg, Dartmouth CS 10, Fall 2012
 * @author CBK, updated Fall 2016
 */

/**
 * May 27th 2019
 * PS-6
 * 
 * @author Scott Crawshaw
 * @author Kunaal Verma
 */

public class Rectangle implements Shape {

	private int x1, y1, x2, y2;
	private Color color;
	
	/**
	 * Rectangle with initial point, final point, and color passed as array of Strings
	 * @param args	array of parameters
	 */
	public Rectangle(String[] args) {
		setCorners(Integer.parseInt(args[0]), Integer.parseInt(args[1]), Integer.parseInt(args[2]), Integer.parseInt(args[3]));
		this.color = Color.decode(args[4]);
	}
	
	/**
	 * rectangle with only an initial point
	 * @param x1 — x coordinate
	 * @param y1 — y coordinate
	 * @param color — color of rectangle
	 */
	public Rectangle(int x1, int y1, Color color) {
		this.x1 = x1; this.x2 = x1;
		this.y1 = y1; this.y2 = y1;
		this.color = color;
	}
	
	/**
	 * Rectangle with initial and final point passed
	 * @param x1 - x coord of initial point
	 * @param y1 - y coord of initial point
	 * @param x2 - x coord of final point
	 * @param y2 - y coord of final point
	 * @param color - color of rectangle
	 */
	public Rectangle(int x1, int y1, int x2, int y2, Color color) {
		setCorners(x1, y1, x2, y2);
		this.color = color;
	}
	
	/**
	 * Redefines the ellipse based on new corners
	 */
	public void setCorners(int x1, int y1, int x2, int y2) {
		// Ensure correct upper left and lower right
		this.x1 = Math.min(x1, x2);
		this.y1 = Math.min(y1, y2);
		this.x2 = Math.max(x1, x2);
		this.y2 = Math.max(y1, y2);	
	}
	
	@Override
	public void moveBy(int dx, int dy) {
		x1 += dx; x2 += dx;
		y1 += dy; y2 += dy;
	}

	@Override
	public Color getColor() {
		return color;
	}

	@Override
	public void setColor(Color color) {
		this.color = color;
	}
		
	@Override
	public boolean contains(int x, int y) {
		if (x1 < x && x2 > x && y1 < y && y2 > y) {
			return true;
		}
		return false;
	}

	@Override
	public void draw(Graphics g) {
		g.setColor(color);
		g.fillRect(x1, y1, x2-x1, y2-y1);
	}

	public String toString() {
		return x1+"|"+y1+"|"+x2+"|"+y2+"|"+color.getRGB();
	}
}
