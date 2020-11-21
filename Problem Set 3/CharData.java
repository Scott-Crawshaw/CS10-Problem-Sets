/**
 * @author Scott Crawshaw
 * 5/2/19
 * Problem Set 3
 * This class is the framework for an object that holds a letter and a frequency. This is the data type for the binary tree.
 */

public class CharData {
	private char letter;
	private int frequency;
	
	/**
	 * @param letter
	 * @param frequency
	 * Leaf Constructor
	 */
	public CharData(char letter, int frequency) {
		this.letter = letter;
		this.frequency = frequency;
	}
	
	/**
	 * @param frequency
	 * Non-Leaf Constructor
	 */
	public CharData(int frequency) {
		this.frequency = frequency;
	}

	/**
	 * @return the letter
	 */
	public char getLetter() {
		return letter;
	}

	/**
	 * @return the frequency
	 */
	public int getFrequency() {
		return frequency;
	}
	
	/**
	 * @return a string representation of the CharData
	 * used for testing
	 */
	public String toString() {
		if(getLetter() != 0) return "(" + getLetter() + ", " + getFrequency() + ")";
		else return "(" + getFrequency() + ")";
	}
}
