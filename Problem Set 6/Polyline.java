import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

/**
 * A multi-segment Shape, with straight lines connecting "joint" points -- (x1,y1) to (x2,y2) to (x3,y3) ...
 * 
 * @author Chris Bailey-Kellogg, Dartmouth CS 10, Spring 2016
 * @author CBK, updated Fall 2016
 */

/**
 * May 27th 2019
 * PS-6
 * 
 * @author Scott Crawshaw
 * @author Kunaal Verma
 */

public class Polyline implements Shape {
	private List<Point> pointList;
	private Color color;

	/**
	 * Starts a new polyLine with the passed point list
	 * @param args		param list
	 */
	public Polyline(String args[]) {
		pointList = new ArrayList<Point>();
		for(int x=0; x<args.length-1; x+=2) {
			pointList.add(new Point(Integer.parseInt(args[x]), Integer.parseInt(args[x+1])));
		}
		color = Color.decode(args[args.length-1]);
	}
	
	/**
	 * Starts a new polyLine with the passed point
	 * @param x1 — x coord of passed point
	 * @param y1 — y coord of passed point
	 */
	public Polyline(int x1, int y1, Color color) {
		pointList = new ArrayList<Point>();
		pointList.add(new Point(x1, y1));
		this.color = color;
	}

	/**
	 * adds a new point to the list
	 */
	public void addPoint(int x1, int y1) {
		pointList.add(new Point(x1, y1));
	}

	@Override
	public void moveBy(int dx, int dy) {
		for (Point point: pointList) {
			point.setLocation((int)(point.getX() + dx), (int)(point.getY() + dy));
		}
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

		if (pointList == null || pointList.size() == 0) {
			return false;
		}
		if (pointList.size() == 1) {
			return pointList.get(0).equals(new Point(x, y));
		}

		//loops through, creates new segment for each point and checks to see if it contains the point
		Segment s;
		for (int i = 1; i < pointList.size(); i ++ ) {
			s = new Segment((int)pointList.get(i-1).getX(),(int)pointList.get(i-1).getY(),(int)pointList.get(i).getX(), (int)pointList.get(i).getY(), color);
			if (s.contains(x, y)) return true;
		}
		return false;
	}

	@Override
	public void draw(Graphics g) {
		g.setColor(color);
		for (int i = 1; i < pointList.size(); i++) {
			g.drawLine((int)pointList.get(i-1).getX(), (int)pointList.get(i-1).getY(), (int)pointList.get(i).getX(), (int)pointList.get(i).getY());
		}
	}

	@Override
	public String toString() {
		String s = "";
		// printing all points in pointList
		for (Point p: pointList) {
			s += (int)p.getX() + "|" + (int)p.getY() + "|";
		}
		return s+color.getRGB();
	}
}
