import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

/**
 * @author Scott Crawshaw
 * 5/2/19
 * Problem Set 3
 * This class contains all the helper functions needed to generate a Huffman Tree and a Code Map.
 * This class is used by HuffmanCompression.java
 */

public class HuffmanHelper {
	
	/**
	 * @param fileName
	 * @return codeMap
	 */
	public static Map<Character, String> generateCodeMap(String fileName){
		BinaryTree<CharData> huffmanTree = generateHuffmanTree(generatePriorityQueue(generateFrequencyTable(fileName)));
		Map<Character, String> codeMap = new HashMap<Character, String>();
		if(huffmanTree != null) populateCodeMap(huffmanTree, "", codeMap);
		return codeMap;
	}
	
	/**
	 * @param fileName
	 * @return Huffman Tree
	 */
	public static BinaryTree<CharData> getHuffmanTree(String fileName){
		return generateHuffmanTree(generatePriorityQueue(generateFrequencyTable(fileName)));
	}
	
	/**
	 * @param fileName			path to the text file
	 * @return frequencyTable	HashMap containing all the character's and their frequencies
	 */
	private static Map<Character, Integer> generateFrequencyTable(String fileName){
		Map<Character, Integer> frequencyTable = new HashMap<Character, Integer>();
		BufferedReader input = null;
		try {
			input = new BufferedReader(new FileReader(fileName));
			int num;
			while((num = input.read()) != -1) { //loop through all characters in file
				Character letter = (Character)((char) num);
				if(frequencyTable.containsKey(letter)) { // if letter is already in map, increase it's frequency by 1
					int currentCount = frequencyTable.get(letter).intValue();
					frequencyTable.put(letter, (Integer)(currentCount + 1));
				}
				else { // if letter is not already in the map, put it in the map with a frequency of 1
					frequencyTable.put(letter, (Integer) 1);
				}
			}
			
		} catch (Exception e) { // catch IO or FileNotFound exceptions
			e.printStackTrace();
		}
		finally { //close the input
			try {
				input.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return frequencyTable;

	}
		
	/**
	 * @param queue				takes in a priority queue that is used to generate Huffman Tree
	 * @return HuffmanTree		returns the last tree in the queue, which is the completed Huffman Tree
	 */
	private static BinaryTree<CharData> generateHuffmanTree(PriorityQueue<BinaryTree<CharData>> queue){
		while(queue.size() > 1) {
			BinaryTree<CharData> T1 = queue.poll();
			BinaryTree<CharData> T2 = queue.poll();
			int frequency = T1.getData().getFrequency() + T2.getData().getFrequency();
			
			//make new BinaryTree with children T1 and T2, their combined frequencies, and the null character
			BinaryTree<CharData> T = new BinaryTree<CharData>(new CharData(frequency), T1, T2);
			queue.add(T);
			
		}
		
		return queue.poll();
	}
	
	/**
	 * @param tree		the current node in the BinaryTree. Initially should be the root node of the Huffman tree
	 * @param path		the path taken thus far. Initially should be ""
	 * @param codeMap	the codeMap that needs to be populated
	 */
	private static void populateCodeMap(BinaryTree<CharData> tree, String path, Map<Character, String> codeMap){
		if(tree.isLeaf()) {
			codeMap.put(tree.getData().getLetter(), path);
		}
		else {
			if(tree.hasLeft()) populateCodeMap(tree.getLeft(), path+"0", codeMap);
			if(tree.hasRight()) populateCodeMap(tree.getRight(), path+"1", codeMap);
		}	
	}
	
	/**
	 * @param frequencyTable	take in a HashMap containing characters and frequencies
	 * @return queue			return a priority queue containing BinaryTree objects with the same info as frequencyTable
	 */
	private static PriorityQueue<BinaryTree<CharData>> generatePriorityQueue(Map<Character, Integer> frequencyTable) {
		// Make a priority queue holding binary trees. Use anonymous comparator to compare frequency's of CharDatas.
		PriorityQueue<BinaryTree<CharData>> queue = new PriorityQueue<BinaryTree<CharData>>((BinaryTree<CharData> t1, BinaryTree<CharData> t2) -> t1.getData().getFrequency() - t2.getData().getFrequency());
		
		// Loop through every object in the frequency table and add it to the priority queue
		for(Character key : frequencyTable.keySet()) {
			queue.add(new BinaryTree<CharData>(new CharData(key, frequencyTable.get(key))));
		}

		return queue;
	}

}
