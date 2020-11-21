import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.*;

/**
 * Webcam-based drawing 
 * Scaffold for PS-1, Dartmouth CS 10, Fall 2016
 * 
 * @author Chris Bailey-Kellogg, Spring 2015 (based on a different webcam app from previous terms)
 */

/**
 * 
 * @author Scott Crawshaw
 * 4/11/19
 * CS10 Lab 1
 * 
 */

public class CamPaint extends Webcam {
	private char displayMode = 'w';			// what to display: 'w': live webcam, 'r': recolored image, 'p': painting
	private RegionFinder finder;			// handles the finding
	private Color targetColor;          	// color of regions of interest (set by mouse press)
	private BufferedImage painting;			// the resulting masterpiece
	/**
	 * Initializes the region finder and the drawing
	 */
	public CamPaint() {
		finder = new RegionFinder();
		clearPainting();
	}

	/**
	 * Resets the painting to a blank image
	 */
	protected void clearPainting() {
		painting = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
	}

	/**
	 * DrawingGUI method, here drawing one of live webcam, recolored image, or painting, 
	 * depending on display variable ('w', 'r', or 'p')
	 */
	@Override
	public void draw(Graphics g) {
		if(displayMode == 'w') {
			g.drawImage(image, 0, 0, null);
		}

		if(displayMode == 'r') {
			if(targetColor != null) { //only try and recolor image if target color has been set
				finder.recolorLargestRegion(Color.red);
				BufferedImage recolored = finder.getRecoloredImage();
				g.drawImage(recolored, 0, 0, null);
			}
			else {
				g.drawImage(image, 0, 0, null);
			}
		}

		if(displayMode == 'p') {
			g.drawImage(painting, 0, 0, null);
		}

	}

	/**
	 * Webcam method, here finding regions and updating the painting.
	 */
	@Override
	public void processImage() {
		finder.setImage(image);
		if(targetColor != null) { //only process image if target color has been set
			finder.findRegions(targetColor);
			ArrayList<Point> brush = finder.largestRegion();
			for(Point pixel : brush) {
				painting.setRGB(pixel.x, pixel.y, Color.blue.getRGB());
			}
		}
	}

	/**
	 * Overrides the DrawingGUI method to set the track color.
	 */
	@Override
	public void handleMousePress(int x, int y) {
		targetColor = new Color(image.getRGB(x, y));

	}

	/**
	 * DrawingGUI method, here doing various drawing commands
	 */
	@Override
	public void handleKeyPress(char k) {
		if (k == 'p' || k == 'r' || k == 'w') { // display: painting, recolored image, or webcam
			displayMode = k;
		}
		else if (k == 'c') { // clear
			clearPainting();
		}
		else if (k == 'o') { // save the recolored image
			saveImage(finder.getRecoloredImage(), "recolored.png", "png");
		}
		else if (k == 's') { // save the painting
			saveImage(painting, "painting.png", "png");
		}
		else {
			System.out.println("unexpected key "+k);
		}
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new CamPaint();
			}
		});
	}
}