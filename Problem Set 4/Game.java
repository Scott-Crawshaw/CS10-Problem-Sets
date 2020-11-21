import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * 
 * @author Scott Crawshaw
 * 5/13/19
 * PS4
 *
 */

public class Game {
	private static final String movieFile = "bacon/movies.txt";
	private static final String actorFile = "bacon/actors.txt";
	private static final String comboFile = "bacon/movie-actors.txt";
	private static String center = "Kevin Bacon";

	private static Map<Integer, String> actorsMap = new HashMap<Integer, String>();
	private static Map<Integer, String> moviesMap = new HashMap<Integer, String>();
	private static TreeMap<Double, ArrayList<String>> separationMap = new TreeMap<Double, ArrayList<String>>();
	private static TreeMap<Integer, ArrayList<String>> degreeMap = new TreeMap<Integer, ArrayList<String>>();
	private static Graph<String, Set<String>> tree;
	private static Graph<String, Set<String>> universe = new AdjacencyMapGraph<String, Set<String>>();

	public static void main(String[] args) {
		System.out.println("Commands:\n" + 
				"c <#>: list top (positive number) or bottom (negative) <#> centers of the universe, sorted by average separation\n" + 
				"d <low> <high>: list actors sorted by degree, with degree between low and high\n" + 
				"i: list actors with infinite separation from the current center\n" + 
				"p <name>: find path from <name> to current center of the universe\n" +  
				"u <name>: make <name> the center of the universe\n" + 
				"q: quit game\n");

		generateUniverse();
		generateSeparationMap();
		generateDegreeMap();
		generateTree();

		Scanner scanner = new Scanner(System.in);
		while(true) {
			System.out.println("\n" + center + " game >");
			String input = scanner.nextLine();
			takeAction(input);

		}

	}

	private static void generateTree() {
		tree = GraphLib.bfs(universe, center);
		System.out.println(center + " is now the center of the acting universe, connected to " + tree.numVertices() + "/" + universe.numVertices() + " actors with average separation " + GraphLib.averageSeparation(tree, center));
	}
	
	private static void takeAction(String input) {
		char action = input.charAt(0);
		if(action == 'c') {
			int number = Integer.parseInt(input.substring(input.indexOf(" ")+1, input.length()));
			if(number < 0) {
				for(Double avg : separationMap.keySet()) {
					for(String actor : separationMap.get(avg)) {
						if(number == 0) break;
						number++;
						System.out.println(actor + " -- " + avg);
					}
					if(number == 0) break;
				}
			}
			else {
				for(Double avg : separationMap.descendingKeySet()) {
					for(String actor : separationMap.get(avg)) {
						if(number == 0) break;
						number--;
						System.out.println(actor + " -- " + avg);
					}
					if(number == 0) break;
				}
			}
		}

		if(action == 'd') {
			int lower = Integer.parseInt(input.substring(input.indexOf(" ")+1, input.lastIndexOf(" ")));
			int upper = Integer.parseInt(input.substring(input.lastIndexOf(" ")+1, input.length()));
			SortedMap<Integer, ArrayList<String>> subMap = degreeMap.subMap(lower, upper+1);
			for(ArrayList<String> value : subMap.values()) {
				for(String actor : value) {
					System.out.println(actor);
				}
			}
		}

		if(action == 'i') {
			Set<String> missing = GraphLib.missingVertices(universe, tree);
			for(String actor : missing) {
				System.out.println(actor);
			}
		}
		String oldCenter = center;
		try {
			if(action == 'p') {
				String name = input.substring(input.indexOf(" ") + 1, input.length());
				List<String> path = GraphLib.getPath(tree, name);
				path.remove(0);
				System.out.println(name + "'s " + center + " number is " + path.size());
				String last = name;
				for(String actor : path) {
					System.out.println(last + " acted alongside " + actor + " in the film " + tree.getLabel(last, actor));
					last = actor;
				}
			}

			if(action == 'u') {
				center = input.substring(input.indexOf(" ") + 1, input.length());
				generateTree();
			}
		}
		catch(Exception e) {
			System.out.println("Actor not found or not within reach of center, try again");
			center = oldCenter;
		}

		if(action == 'q') {
			System.exit(0);
		}

	}

	private static void generateSeparationMap() {
		for(String actor : universe.vertices()) {
			double avg = GraphLib.averageSeparation(GraphLib.bfs(universe, actor), actor);
			if(separationMap.containsKey(avg)) {
				separationMap.get(avg).add(actor);
			}
			else {
				ArrayList<String> value = new ArrayList<String>();
				value.add(actor);
				separationMap.put(avg, value);
			}
		}
	}

	private static void generateDegreeMap() {
		for(String actor : universe.vertices()) {
			int degree = universe.inDegree(actor);
			if(degreeMap.containsKey(degree)) {
				degreeMap.get(degree).add(actor);
			}
			else {
				ArrayList<String> value = new ArrayList<String>();
				value.add(actor);
				degreeMap.put(degree, value);
			}
		}
	}

	private static void generateUniverse() {

		try {
			//Add all actors to the universe and create an ID lookup map for actor names
			BufferedReader actorInput = new BufferedReader(new FileReader(actorFile));
			String aline;
			while ((aline = actorInput.readLine()) != null) {
				String[] info = aline.split("\\|");
				actorsMap.put(Integer.parseInt(info[0]), info[1]);
				universe.insertVertex(info[1]);
			}
			actorInput.close();

			//create an ID lookup map for movie names
			BufferedReader movieInput = new BufferedReader(new FileReader(movieFile));
			String mline;
			while ((mline = movieInput.readLine()) != null) {
				String[] info = mline.split("\\|");
				moviesMap.put(Integer.parseInt(info[0]), info[1]);
			}
			movieInput.close();

			//create all the edges in the universe
			BufferedReader comboInput = new BufferedReader(new FileReader(comboFile));
			String cline;
			ArrayList<String> movieActors = new ArrayList<String>();
			String lastMovie = "";
			while ((cline = comboInput.readLine()) != null) {
				String[] info = cline.split("\\|");
				String currentActor = actorsMap.get(Integer.parseInt(info[1]));
				String currentMovie = moviesMap.get(Integer.parseInt(info[0]));
				if(lastMovie.equals(currentMovie)) {
					for(String actor : movieActors) {
						if(universe.hasEdge(currentActor, actor)) {
							Set<String> label = universe.getLabel(currentActor, actor);
							label.add(currentMovie);
							universe.insertUndirected(currentActor, actor, label);
						}
						else {
							Set<String> label = new HashSet<String>();
							label.add(currentMovie);
							universe.insertUndirected(currentActor, actor, label);
						}
					}
					movieActors.add(currentActor);
				}
				else {
					lastMovie = currentMovie;
					movieActors = new ArrayList<String>();
					movieActors.add(currentActor);
				}
			}
			comboInput.close();

		} catch (FileNotFoundException e) {
			System.out.println("File not Found");
		} catch (IOException e) {
			System.out.println("File not openable");
		}

	}

}
