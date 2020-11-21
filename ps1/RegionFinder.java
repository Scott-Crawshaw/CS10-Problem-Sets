import java.awt.*;
import java.awt.image.*;
import java.util.*;

/**
 * Region growing algorithm: finds and holds regions in an image.
 * Each region is a list of contiguous points with colors similar to a target color.
 * Scaffold for PS-1, Dartmouth CS 10, Fall 2016
 * 
 * @author Chris Bailey-Kellogg, Winter 2014 (based on a very different structure from Fall 2012)
 * @author Travis W. Peters, Dartmouth CS 10, Updated Winter 2015
 * @author CBK, Spring 2015, updated for CamPaint
 */

/**
 * 
 * @author Scott Crawshaw
 * 4/11/19
 * CS10 Lab 1
 * 
 */


public class RegionFinder {
	private static final int maxColorDiff = 13;				// how similar a pixel color must be to the target color, to belong to a region
	private static final int minRegion = 30; 				// how many points in a region to be worth considering
	private BufferedImage image;                            // the image in which to find regions
	private BufferedImage recoloredImage;                   // the image with identified regions recolored

	private ArrayList<ArrayList<Point>> regions;			// a region is a list of points
	// so the identified regions are in a list of lists of points

	public RegionFinder() {
		this.image = null;
	}

	public RegionFinder(BufferedImage image) {
		this.image = image;		
	}

	public void setImage(BufferedImage image) {
		this.image = image;
	}

	public BufferedImage getImage() {
		return image;
	}

	public BufferedImage getRecoloredImage() {
		return recoloredImage;
	}

	/**
	 * Sets regions to the flood-fill regions in the image, similar enough to the trackColor.
	 */
	public void findRegions(Color targetColor) {
		int width = image.getWidth();
		int height = image.getHeight();
		BufferedImage visited = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		regions = new ArrayList<ArrayList<Point>>();

		for(int x=0; x<width; x++) {
			for(int y=0; y<height; y++) {
				if(visited.getRGB(x, y) == 0) { //if the pixel hasn't been visited
					Color pixelColor = new Color(image.getRGB(x, y));

					if(colorMatch(targetColor, pixelColor)) { //check to see if pixel is similar to target color
						ArrayList<Point> region = getRegion(x, y, width, height, visited, targetColor);
						if(region.size() >= minRegion) { //make sure region is large enough
							regions.add(region);
						}
					}


					visited.setRGB(x, y, 1);

				}
			}
		}
	}


	/**
	 * returns a region given a starting point
	 */
	private ArrayList<Point> getRegion(int x, int y, int width, int height, BufferedImage visited, Color targetColor){
		ArrayList<Point> region = new ArrayList<Point>();
		Queue<Point> q = new LinkedList<Point>();
		q.add(new Point(x, y));
		while(q.peek() != null) { //check to make sure the queue isn't empty
			Point pixel = q.poll();
			if(visited.getRGB(pixel.x, pixel.y) == 0) { //if the pixel has not been visited
				region.add(pixel);
				visited.setRGB(pixel.x, pixel.y, 1);
				//loop over surrounding 8 pixels
				for(int a=pixel.x-1; a<=pixel.x+1; a++) {
					for(int b=pixel.y-1; b<=pixel.y+1; b++) {
						if(a>=0 && a<width && b>=0 && b<height) {
							Color pixelColor = new Color(image.getRGB(a, b));
							if(colorMatch(targetColor, pixelColor)) { //check to see if pixel is similar to target color
								q.add(new Point(a, b));
							}
						}
					}
				}
			}
		}

		return region;
	}

	/**
	 * Tests whether the two colors are "similar enough" (your definition, subject to the maxColorDiff threshold, which you can vary).
	 */
	private static boolean colorMatch(Color c1, Color c2) {
		//check rgb of pixel
		if(c1.getRed() + maxColorDiff >= c2.getRed() && c2.getRed() >= c1.getRed() - maxColorDiff) {
			if(c1.getGreen() + maxColorDiff >= c2.getGreen() && c2.getGreen() >= c1.getGreen() - maxColorDiff) {
				if(c1.getBlue() + maxColorDiff >= c2.getBlue() && c2.getBlue() >= c1.getBlue() - maxColorDiff) {
					return true;
				}
			}
		}
		return false;	
	}

	/**
	 * Returns the largest region detected (if any region has been detected)
	 */
	public ArrayList<Point> largestRegion() {
		ArrayList<Point> largest = new ArrayList<Point>();
		for(ArrayList<Point> region : regions) {
			if(region.size() > largest.size()) {
				largest = region;
			}
		}

		return largest;
	}

	/**
	 * Sets recoloredImage to be a copy of image, 
	 * but with each region a uniform random color, 
	 * so we can see where they are
	 */
	public void recolorImage() {
		// First copy the original
		recoloredImage = new BufferedImage(image.getColorModel(), image.copyData(null), image.getColorModel().isAlphaPremultiplied(), null);
		// Now recolor the regions in it
		for(ArrayList<Point> region : regions) {
			int randomColor = (int) (Math.random()*16777216);
			for(Point pixel : region) {
				//set each pixel in region to same random color
				recoloredImage.setRGB(pixel.x, pixel.y, randomColor);
			}
		}
	}

	/**
	 * only recolors largest region
	 * colors in the target color
	 */
	public void recolorLargestRegion(Color c) {
		// First copy the original
		recoloredImage = new BufferedImage(image.getColorModel(), image.copyData(null), image.getColorModel().isAlphaPremultiplied(), null);
		ArrayList<Point> region = largestRegion();
		for(Point pixel : region) {
			//set each pixel in region to same color
			recoloredImage.setRGB(pixel.x, pixel.y, c.getRGB());
		}
	}


}