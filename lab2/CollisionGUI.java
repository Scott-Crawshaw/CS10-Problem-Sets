import java.awt.*;

import javax.swing.*;

import java.util.List;
import java.util.ArrayList;

/**
 * Using a quadtree for collision detection
 * 
 * @author Chris Bailey-Kellogg, Dartmouth CS 10, Spring 2015
 * @author CBK, Spring 2016, updated for blobs
 * @author CBK, Fall 2016, using generic PointQuadtree
 */

/**
 * @author scott crawshaw
 *		   4/23/19
 */

public class CollisionGUI extends DrawingGUI {
	private static final int width=800, height=600;		// size of the universe

	private List<Blob> blobs;						// all the blobs
	private List<Blob> colliders;					// the blobs who collided at this step
	private char blobType = 'b';						// what type of blob to create
	private char collisionHandler = 'c';				// when there's a collision, 'c'olor them, or 'd'estroy them
	private int delay = 100;							// timer control

	public CollisionGUI() {
		super("super-collider", width, height);

		blobs = new ArrayList<Blob>();

		// Timer drives the animation.
		startTimer();
	}

	/**
	 * Adds an blob of the current blobType at the location
	 */
	private void add(int x, int y) {
		if (blobType=='b') {
			blobs.add(new Bouncer(x,y,width,height));
		}
		else if (blobType=='w') {
			blobs.add(new Wanderer(x,y));
		}
		else {
			System.err.println("Unknown blob type "+blobType);
		}
	}

	/**
	 * DrawingGUI method, here creating a new blob
	 */
	public void handleMousePress(int x, int y) {
		add(x,y);
		repaint();
	}

	/**
	 * DrawingGUI method
	 */
	public void handleKeyPress(char k) {
		if (k == 'f') { // faster
			if (delay>1) delay /= 2;
			setTimerDelay(delay);
			System.out.println("delay:"+delay);
		}
		else if (k == 's') { // slower
			delay *= 2;
			setTimerDelay(delay);
			System.out.println("delay:"+delay);
		}
		else if (k == 'r') { // add some new blobs at random positions
			for (int i=0; i<10; i++) {
				add((int)(width*Math.random()), (int)(height*Math.random()));
				repaint();
			}			
		}
		else if (k == 'c' || k == 'd') { // control how collisions are handled
			collisionHandler = k;
			System.out.println("collision:"+k);
		}
		else if (k == 't') {
			test();
		}
		else { // set the type for new blobs
			blobType = k;			
		}
	}

	/**
	 * DrawingGUI method, here drawing all the blobs and then re-drawing the colliders in red
	 */
	public void draw(Graphics g) {
		// Ask all the blobs to draw themselves.
		g.setColor(Color.BLACK);
		for(Blob b: blobs) {
			b.draw(g);
		}

		// Ask the colliders to draw themselves in red.
		g.setColor(Color.RED);
		if(colliders != null) {
			for(Blob c: colliders) {
				c.draw(g);
			}
		}
	}

	/**
	 * Sets colliders to include all blobs in contact with another blob
	 */
	private void findColliders() {
		// Create the tree
		PointQuadtree<Blob> tree = new PointQuadtree<Blob>(blobs.get(0), 0, 0, width, height);
		for(int x=1; x<blobs.size(); x++) {
			tree.insert(blobs.get(x));
		}
		// For each blob, see if anybody else collided with it
		colliders = new ArrayList<Blob>();
		for(Blob b: blobs) {
			List<Blob> hits = tree.findInCircle(b.getX(), b.getY(), 2*b.getR());
			if(hits.size()>1) {
				colliders.addAll(hits);
			}
		}
	}
	
	/**
	 * Tests collisions by creating blobs that should all be found by findColliders
	 */
	
	private void test() {
		add(100,100);
		add(101,101);
		add(99,99);
		
		add(0,0);
		add(1,1);
		
		add(500,500);
		add(501,500);
		add(499,500);
		findColliders();
		if(colliders.size() != 22) {
			System.err.println("findColliders method not working! Expected 22 colliders, found " + colliders.size());
		}
		else {
			System.out.println("Test passed! findColliders method working properly");
		}
		
	}

	/**
	 * DrawingGUI method, here moving all the blobs and checking for collisions
	 */
	public void handleTimer() {
		// Ask all the blobs to move themselves.
		for (Blob blob : blobs) {
			blob.step();
		}
		// Check for collisions
		if (blobs.size() > 0) {
			findColliders();
			if (collisionHandler=='d') {
				blobs.removeAll(colliders);
				colliders = null;
			}
		}
		// Now update the drawing
		repaint();
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new CollisionGUI();
			}
		});
	}
}
