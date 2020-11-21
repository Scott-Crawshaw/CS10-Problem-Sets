import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.TreeMap;

/**
 * May 27th 2019
 * PS-6
 * 
 * @author Scott Crawshaw
 * @author Kunaal Verma
 */

public class Sketch {
	private TreeMap<Integer, Shape> shapeMap;
	
	/**
	 * Constructs a new sketch from comma separated list 
	 * @param serverInput	comma separated list in the format id, shape type, shape info, id, shape type, shape info
	 */
	public Sketch(String serverInput) {
		shapeMap = new TreeMap<Integer, Shape>();
		if(!serverInput.equals("")) {
			String[] info = serverInput.split(",");
			for(int x=0; x<info.length; x+=3) {
				Integer id = Integer.parseInt(info[x]);
				String shapeType = info[x+1];
				String[] shapeInfo = info[x+2].split("\\|");

				if(shapeType.equals("ellipse")) {
					shapeMap.put(id, new Ellipse(shapeInfo));
				}
				if(shapeType.equals("rectangle")) {
					shapeMap.put(id, new Rectangle(shapeInfo));
				}
				if(shapeType.equals("segment")) {
					shapeMap.put(id, new Segment(shapeInfo));
				}
				if(shapeType.equals("freehand")) {
					shapeMap.put(id, new Polyline(shapeInfo));
				}
			}
		}

	}
	/**
	 * Add shape based on server input
	 * @param serverInput		String with the format id, shape type, shape info
	 * 							shape info is pipe separated
	 */
	public synchronized void addShape(String serverInput) {
		String[] info = serverInput.split(",");
		Integer id = Integer.parseInt(info[0]);
		String shapeType = info[1];
		String[] shapeInfo = info[2].split("\\|");

		if(shapeType.equals("ellipse")) {
			shapeMap.put(id, new Ellipse(shapeInfo));
		}
		if(shapeType.equals("rectangle")) {
			shapeMap.put(id, new Rectangle(shapeInfo));
		}
		if(shapeType.equals("segment")) {
			shapeMap.put(id, new Segment(shapeInfo));
		}
		if(shapeType.equals("freehand")) {
			shapeMap.put(id, new Polyline(shapeInfo));
		}
	}

	/**
	 * Remove shape based on id
	 * @param id	shape id
	 */
	public synchronized void removeShape(String serverInput) {
		shapeMap.remove(Integer.parseInt(serverInput));
	}
	
	/**
	 * moves the indicated shape by a passed amount
	 * @param serverInput
	 */
	public synchronized void moveShape(String serverInput) {
		String[] info = serverInput.split(",");
		String[] speed = info[1].split("\\|");
		Integer id = Integer.parseInt(info[0]);
		Integer dx = Integer.parseInt(speed[0]);
		Integer dy = Integer.parseInt(speed[1]);
		
		shapeMap.get(id).moveBy(dx, dy);
	}
	
	/**
	 * recolors an indicated shape
	 * @param serverInput
	 */
	public synchronized void recolorShape(String serverInput) {
		String[] info = serverInput.split(",");
		Integer id = Integer.parseInt(info[0]);
		Color color = Color.decode(info[1]);
		
		shapeMap.get(id).setColor(color);
 	}
	
	/**
	 * Check to see if mouse is contained within any shape, top shapes having precendence
	 * @param p		mouse position
	 * @return		shape that contains mouse
	 */
	public synchronized int shapeContains(Point p) {
		for (Integer id: shapeMap.descendingKeySet()) {
			if (shapeMap.get(id).contains((int)p.getX(), (int)p.getY())) return id;
		}
		return -1;
	}
	
	/**
	 * get shape based on id
	 * 
	 * @param id	id of shape
	 * @return		shape
	 */
	public synchronized Shape getShape(int id) {
		if(id==-1) return null;
		return shapeMap.get(id);
	}

	/**
	 * Draw shapes in ascending order of ids
	 * @param g		Graphics component
	 */
	public synchronized void drawShapes(Graphics g) {
		for(int id : shapeMap.navigableKeySet()) {
			shapeMap.get(id).draw(g);
		}
	}
	
	/**
	 * Convert treeMap to comma separated list in the format id, shape type, shape info, id, shape type, shape info
	 * @return	the string
	 */
	public String toString() {
		if (shapeMap.isEmpty()) return "";
		String result = "";
		for(int id : shapeMap.navigableKeySet()) {
			if(Rectangle.class.isInstance(shapeMap.get(id))) {
				result += (id + ",rectangle," + ((Rectangle)shapeMap.get(id)).toString() + ",");
			}
			if(Polyline.class.isInstance(shapeMap.get(id))) {
				result += (id + ",freehand," + ((Polyline)shapeMap.get(id)).toString() + ",");
			}
			if(Segment.class.isInstance(shapeMap.get(id))) {
				result += (id + ",segment," + ((Segment)shapeMap.get(id)).toString() + ",");
			}
			if(Ellipse.class.isInstance(shapeMap.get(id))) {
				result += (id + ",ellipse," + ((Ellipse)shapeMap.get(id)).toString() + ",");
			}
				
		}
		return result.substring(0, result.length()-1);
	}
	
	

}
