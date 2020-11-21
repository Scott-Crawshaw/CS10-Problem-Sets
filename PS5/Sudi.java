import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * 
 * @author Kunaal Verma
 * @author Scott Crawshaw
 *
 * 5/19/19
 * PS 5
 * Sudi.java
 */

public class Sudi {
	private Map<String, Map<String, Double>> transition, wordList;
	private final String trainSentences, trainTags;
	private final int UNOBSERVED = -100;

	/**
	 * Sudi constructor. Creates and trains the Sudi.
	 * @param trainSentences	file with training sentences
	 * @param trainTags			file with training tags
	 */
	
	public Sudi(String trainSentences, String trainTags) {
		this.trainSentences = trainSentences;
		this.trainTags = trainTags;

		train();
	}

	/**
	 * Trains the Sudi using provided training files
	 */
	private void train() {
		BufferedReader brTags = null;
		BufferedReader brSentence = null;
		try {
			brTags = new BufferedReader(new FileReader(trainTags));
			brSentence = new BufferedReader(new FileReader(trainSentences));
			
			//map of the edges
			transition = new HashMap<String, Map<String, Double>>();
			
			//map of the scores for each word at each POS
			wordList = new HashMap<String, Map<String, Double>>();
			
			String tags;
			String words;
			while ((tags = brTags.readLine()) != null && (words = brSentence.readLine()) != null) {
				String[] tagsArr = tags.split(" ");
				String[] wordsArr = words.split(" ");
				
				//deal with the first word before for loop

				//transition map
				if (!transition.containsKey("#")){ //start key has never been added to map
					Map<String, Double> transitionMap = new HashMap<String, Double>();
					transitionMap.put(tagsArr[0], 1.0);
					transition.put("#", transitionMap);
				}
				else if (!transition.get("#").containsKey(tagsArr[0])) { //start key has been added to map, hasn't seen tagsArr[0]
					transition.get("#").put(tagsArr[0], 1.0);
				}
				else { //start key has been added to map and has seen tagsArr[0]
					transition.get("#").put(tagsArr[0], (Double)(transition.get("#").get(tagsArr[0]).doubleValue() + 1));
				}

				//word map
				if (!wordList.containsKey(tagsArr[0])) { //word list has never seen this tag
					Map<String, Double> wordMap = new HashMap<String, Double>();
					wordMap.put(wordsArr[0], 1.0);
					wordList.put(tagsArr[0], wordMap);
				}
				else if(!wordList.get(tagsArr[0]).containsKey(wordsArr[0])) { //word list has seen this tag, but not with this word
					wordList.get(tagsArr[0]).put(wordsArr[0], 1.0);
				}
				else { //word list has seen this tag and this word
					wordList.get(tagsArr[0]).put(wordsArr[0], (Double)(wordList.get(tagsArr[0]).get(wordsArr[0]).doubleValue() + 1));
				}

				for (int i = 1; i<tagsArr.length; i++) {	
					wordsArr[i] = wordsArr[i].toLowerCase();

					//transition map
					if (!transition.containsKey(tagsArr[i-1])) { //this key has never been added to map
						Map<String, Double> transitionMap = new HashMap<String, Double>();
						transitionMap.put(tagsArr[i], 1.0);
						transition.put(tagsArr[i-1], transitionMap);
					}
					else if(!transition.get(tagsArr[i-1]).containsKey(tagsArr[i])) { //this key has been added to map, hasn't seen this tag
						transition.get(tagsArr[i-1]).put(tagsArr[i], 1.0);
					}
					else { //this key has been added to map and has seen this tag
						double curr = (double) transition.get(tagsArr[i-1]).get(tagsArr[i]);
						transition.get(tagsArr[i-1]).put(tagsArr[i], (Double) (curr+1));
					}
					//word map
					if (!wordList.containsKey(tagsArr[i])) { //word list has never seen this tag
						Map<String, Double> wordMap = new HashMap<String, Double>();
						wordMap.put(wordsArr[i], 1.0);
						wordList.put(tagsArr[i], wordMap);
					}
					else if(!wordList.get(tagsArr[i]).containsKey(wordsArr[i])) { //word list has seen this tag, but not with this word
						wordList.get(tagsArr[i]).put(wordsArr[i], 1.0);
					}
					else { //word list has seen this tag and this word
						wordList.get(tagsArr[i]).put(wordsArr[i], (Double)(wordList.get(tagsArr[i]).get(wordsArr[i]).doubleValue() + 1));
					}
				}	
			}
			
			//convert raw results to logged probabilities
			logProbabilities(transition);
			logProbabilities(wordList);

		}
		catch(Exception e) {
			System.out.println(e.getMessage());
		}
		finally {
			if(brTags != null && brSentence!=null) {
				try {
					brTags.close();
					brSentence.close();
				}catch(Exception e) {
					System.out.println(e.getStackTrace());
				}
			}
		}

	}
	
	/**
	 * Using a trained Sudi, this function tags a single sentence.
	 * @param sentence Sentence that needs to be tagged
	 * @return String containing all the tags
	 */
	public String tagSentence(String sentence) {
		String tagged = "";
		String[] words = sentence.split(" ");
		
		//maps word indicies to their nextPOS and its score
		Map<Integer, Map<String, Double>> observationMap = new HashMap<Integer, Map<String, Double>>();
		
		//keeps track of the steps between POSes for later back tracking
		Map<Integer, Map<String, String>> backTrackMap = new HashMap<Integer, Map<String, String>>();

		//do the first word outside of loop
		observationMap.put(0, new HashMap<String, Double>());
		backTrackMap.put(0, new HashMap<String, String>());

		for(String nextPOS : transition.get("#").keySet()) {
			double score = transition.get("#").get(nextPOS).doubleValue();
			if (wordList.get(nextPOS).containsKey(words[0])) {
				score += wordList.get(nextPOS).get(words[0]).doubleValue();
			}
			else {
				score += UNOBSERVED;
			}
			observationMap.get(0).put(nextPOS, score);
			backTrackMap.get(0).put(nextPOS, null);
		}

		for(int i = 1; i<words.length; i++) {
			observationMap.put(i, new HashMap<String, Double>());
			backTrackMap.put(i, new HashMap<String, String>());
			for(String POS : observationMap.get(i-1).keySet()) {
				if(!transition.containsKey(POS)) continue;
				for(String nextPOS : transition.get(POS).keySet()) {
					//score = current score + transition score
					double score = observationMap.get(i-1).get(POS).doubleValue() + transition.get(POS).get(nextPOS).doubleValue();

					if (wordList.get(nextPOS).containsKey(words[i])) { //if we have seen this word in our trained sudi
						//score += word score
						score += wordList.get(nextPOS).get(words[i]).doubleValue();
					}
					else {
						//otherwise dock unobserved penalty
						score += UNOBSERVED;
					}
					
					//if we already have this part of speech for this word index, only add if score is greater than the previous score
					if(observationMap.get(i).containsKey(nextPOS)) {
						if(observationMap.get(i).get(nextPOS).doubleValue() < score) {
							observationMap.get(i).put(nextPOS, (Double) score);
							backTrackMap.get(i).put(nextPOS, POS);
						}
					}
					else { // otherwise if we dont have this part of speech, we add it to the observation map and backtrack map
						observationMap.get(i).put(nextPOS, (Double) score);
						backTrackMap.get(i).put(nextPOS, POS);
					}
				}
			}
		}

		
		// now we loop through all the scores in the last word's map, and find the highest score
		double bestScore = Double.NEGATIVE_INFINITY;
		String bestPOS = "";
		for(String POS : observationMap.get(words.length-1).keySet()) {
			if(observationMap.get(words.length-1).get(POS).doubleValue() > bestScore) {
				bestScore = observationMap.get(words.length-1).get(POS).doubleValue();
				bestPOS = POS;

			}
		}
		tagged = bestPOS;

		String nextPOS = bestPOS;

		//using that highest score, we get it's part of speech and create a string that backtracks through all the previous parts of 
		// speech in the sentence
		for(int i = words.length-1 ; i>0; i--) {
			nextPOS = backTrackMap.get(i).get(nextPOS);
			tagged = nextPOS + " " +  tagged;

		}

		//returns tagged, the sentence of all the POS
		return tagged;
	}
	
	/**
	 * Test the Sudi's ability to tag sentences using a hard coded "training".
	 * Copied the edge and node weights from PD_HMM.pdf
	 */
	public void viterbiTest() {
		
		transition = new HashMap<String, Map<String, Double>>();
		wordList = new HashMap<String, Map<String, Double>>();
		
		// adding all the hard coded transitions
		HashMap<String, Double> paths = new HashMap<String, Double>();
		paths.put("N", 7.0);
		paths.put("NP", 3.0);
		transition.put("#", paths);
		
		paths = new HashMap<String, Double>();
		paths.put("CNJ", 2.0);
		paths.put("V", 8.0);
		transition.put("N", paths);
		
		paths = new HashMap<String, Double>();
		paths.put("CNJ", 2.0);
		paths.put("N", 4.0);
		paths.put("NP", 4.0);
		transition.put("V", paths);
		
		paths = new HashMap<String, Double>();
		paths.put("V", 8.0);
		paths.put("CNJ", 2.0);
		transition.put("NP", paths);
		
		paths = new HashMap<String, Double>();
		paths.put("V", 4.0);
		paths.put("N", 4.0);
		paths.put("NP", 2.0);
		transition.put("CNJ", paths);
		
		// adding all the hard coded word counts for each POS
		HashMap<String, Double> words = new HashMap<String, Double>();
		words.put("chase", 10.0);
		wordList.put("NP", words);
		
		words = new HashMap<String, Double>();
		words.put("and", 10.0);
		wordList.put("CNJ", words);
		
		words = new HashMap<String, Double>();
		words.put("cat", 4.0);
		words.put("dog", 4.0);
		words.put("watch", 2.0);
		wordList.put("N", words);
		
		words = new HashMap<String, Double>();
		words.put("get", 1.0);
		words.put("chase", 3.0);
		words.put("watch", 6.0);
		wordList.put("V", words);
		
		//sample sentences
		String[] sentences = new String[5];
		sentences[0] = "cat chase dog";
		sentences[1] = "cat watch chase";
		sentences[2] = "chase get watch";
		sentences[3] = "chase watch dog and cat";
		sentences[4] = "dog watch cat watch dog";
		
		//results for sample sentences
		String[] results = new String[5];
		results[0] = "N V N";
		results[1] = "N V NP";
		results[2] = "NP V N";
		results[3] = "NP V N CNJ N";
		results[4] = "N V N V N";

		//compare true results to Sudi's results and alert user if there are discrepancies
		boolean failed = false;
		for(int i = 0; i<sentences.length; i++) {
			if(!results[i].equals(tagSentence(sentences[i]))) {
				failed = true;
				System.out.println("TEST FAILED!");
				System.out.println("Expected " + results[i] + ", got " + tagSentence(sentences[i]));
			}
		}
		if(!failed) System.out.println("Test was a success!");
		
		
		//reset maps to real values
		train();
		

	}

	/**
	 * Test the Sudi against the appropriate test file, and print out the results
	 */
	public void accuracyTest() {
		//get file name for test file
		String sentencesFile = trainSentences.substring(0, trainSentences.indexOf("-")) + "-test" + trainSentences.substring(trainSentences.lastIndexOf("-"), trainSentences.length());
		String tagsFile = trainTags.substring(0, trainTags.indexOf("-")) + "-test" + trainTags.substring(trainTags.lastIndexOf("-"), trainTags.length());
		
		BufferedReader brSentence = null;
		BufferedReader brTags = null;
		try {
			brSentence = new BufferedReader(new FileReader(sentencesFile));
			brTags = new BufferedReader(new FileReader(tagsFile));
			int correct = 0;
			int wrong = 0;
			String tags;
			String words;
			while ((tags = brTags.readLine()) != null && (words = brSentence.readLine()) != null) {
				String[] result = tagSentence(words).split(" ");
				String[] actual = tags.split(" ");
				//count up the number of correct answers and the number of wrong answers
				for(int i = 0; i<result.length; i++) {
					if(result[i].equals(actual[i])) {
						correct++;
					}
					else {
						wrong++;
					}
				}
			}
			System.out.println("Correct: " + correct + "  Wrong: " + wrong);
		}
		catch(Exception e){
			System.out.println(e.getStackTrace());
		}
		finally {
			if(brTags != null && brSentence!=null) {
				try {
					brTags.close();
					brSentence.close();
				}catch(Exception e) {
					System.out.println(e.getStackTrace());
				}
			}
		}

	}

	/**
	 * Convert a map of raw counts to a map of probabilities with the natural log function applied to them
	 * @param map		map of raw counts
	 */
	private void logProbabilities(Map<String, Map<String, Double>> map) {
		
		if (!map.isEmpty()) {
			Map<String, Double> sumMap = new HashMap<String, Double>();
			// loop through each of the POS, and each of the frequencies for each word/POS (depending on whether transition map or wordList map) within each POS
			for (String key1 :map.keySet()){
				double total = 0;
				Map<String, Double> miniMap = map.get(key1);
				for (String key2: miniMap.keySet()) {
					total += miniMap.get(key2);
				}
				sumMap.put(key1, total); // creating a map where the key is the part of speech and the value is the total number of observations
			}

			for (String key1: sumMap.keySet()) { // loop through again and for each frequency in the original map, we divide the frequency count by total count and take the log of that
				for (String key2: map.get(key1).keySet()) {
					map.get(key1).put(key2, Math.log(map.get(key1).get(key2)/sumMap.get(key1)));
				}
			}
		}
	}
	
	/**
	 * Creates a console interface where one can input a string and get a POS tagged result
	 */
	public void consoleTest() {
		Scanner in = new Scanner(System.in);
		String line = "";
		while (!line.equals("quit")) {
			System.out.println("Enter a sentence and I'll tag it for you!");
			System.out.println("To quit this game, type \"quit\"");
			line = in.nextLine().toLowerCase();
			if (line.equals("quit")) continue;
			if (line.equals("")) System.out.println("Not a real sentence, try again");
			else {
				System.out.println(tagSentence(line));
			}
		}
		System.out.println("Thank you, come again!");
		in.close();
	}
	
	/**
	 * Runs all of our tests in an orderly fashion
	 * @param brownSudi		the Sudi that uses the Brown files
	 * @param simpleSudi	the Sudi that uses the Simple files
	 */
	public static void runTests(Sudi brownSudi, Sudi simpleSudi) {
		System.out.println("Brown Sudi Accuracy Test: ");
		brownSudi.accuracyTest();
		
		System.out.println("\nSimple Sudi Accuracy Test: ");
		simpleSudi.accuracyTest();
		
		System.out.println("\nHard Coded Viterbi Test: ");
		simpleSudi.viterbiTest();
		
		System.out.println("\nConsole Test: ");
		brownSudi.consoleTest();
	}

	public static void main(String[] args) {
		Sudi brownSudi = new Sudi("brown-train-sentences.txt", "brown-train-tags.txt");
		Sudi simpleSudi = new Sudi("simple-train-sentences.txt", "simple-train-tags.txt");
		runTests(brownSudi, simpleSudi);
		
	}
}
