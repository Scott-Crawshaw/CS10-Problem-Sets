import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

/**
 * @author Scott Crawshaw
 * 5/2/19
 * Problem Set 3
 * This class compresses and decompresses a text file. It uses the helper methods from HuffmanHelper.java
 */

public class HuffmanCompression {
	
	public static void main(String[] args) {
		String pathName = "WarAndPeace.txt";
		BinaryTree<CharData> huffmanTree = compress(pathName);
		decompress(pathName.substring(0, pathName.indexOf(".")) + "_compressed", huffmanTree);

	}
	
	/**
	 * @param fileName
	 * @return a huffman tree for later decompression
	 * This method compresses a file and saves the huffman tree for decompression.
	 */
	public static BinaryTree<CharData> compress(String fileName) {
		Map<Character, String> codeMap = HuffmanHelper.generateCodeMap(fileName);
		BinaryTree<CharData> huffmanTree = HuffmanHelper.getHuffmanTree(fileName);
		BufferedReader input = null;
		BufferedBitWriter bitOutput = null;
		try {
			input = new BufferedReader(new FileReader(fileName));
			bitOutput = new BufferedBitWriter(fileName.substring(0, fileName.indexOf(".")) + "_compressed");
			int num;
			while((num = input.read()) != -1) {
				Character letter = (Character) ((char) num);
				String code = codeMap.get(letter);
				for(char bit : code.toCharArray()) {
					if(bit == '0') bitOutput.writeBit(false);
					if(bit == '1') bitOutput.writeBit(true);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			try {
				input.close();
				bitOutput.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return huffmanTree;
	}
	
	/**
	 * @param fileName
	 * @param huffmanTree
	 * This method decompresses a file using the old Huffman Tree
	 */
	public static void decompress(String fileName, BinaryTree<CharData> huffmanTree) {
		BufferedBitReader bitInput = null;
		BufferedWriter output = null;
		BinaryTree<CharData> root = huffmanTree;
		try {
			bitInput = new BufferedBitReader(fileName);
			output = new BufferedWriter(new FileWriter(fileName.substring(0, fileName.lastIndexOf("_")) + "_decompressed.txt"));
			while (bitInput.hasNext()) {
				  boolean bit = bitInput.readBit();
				  if(bit && huffmanTree.hasRight()) {
					  huffmanTree = huffmanTree.getRight();
				  }
				  if(!bit && huffmanTree.hasLeft()) {
					  huffmanTree = huffmanTree.getLeft();
				  }
				  if(huffmanTree.isLeaf()) {
					  output.write(huffmanTree.getData().getLetter());
					  huffmanTree = root;
				  }
				}
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			try {
				bitInput.close();
				output.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
