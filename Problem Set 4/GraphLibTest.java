/**
 * 
 * @author Scott Crawshaw
 * 5/13/19
 * PS4
 *
 */

public class GraphLibTest {

	public static void main(String[] args) {
		Graph<String, String> testGraph = new AdjacencyMapGraph<String, String>();
		testGraph.insertVertex("Scott");
		testGraph.insertVertex("Kelly");
		testGraph.insertVertex("Keara");
		testGraph.insertVertex("Haley");
		testGraph.insertVertex("Jonas");
		testGraph.insertVertex("Renato");
		testGraph.insertVertex("Hudson");
		testGraph.insertVertex("Jade");
		testGraph.insertVertex("Geoff");
		testGraph.insertVertex("Kristina");
		
		testGraph.insertUndirected("Scott", "Kelly", "friend");
		testGraph.insertUndirected("Scott", "Keara", "ex");
		testGraph.insertUndirected("Scott", "Haley", "friend");
		testGraph.insertUndirected("Scott", "Hudson", "friend");
		testGraph.insertUndirected("Scott", "Geoff", "family");
		testGraph.insertUndirected("Scott", "Kristina", "friend");
		
		testGraph.insertUndirected("Kelly", "Keara", "friend");
		testGraph.insertUndirected("Kelly", "Haley", "friend");
		testGraph.insertUndirected("Kelly", "Kristina", "friend");
		
		testGraph.insertUndirected("Keara", "Kristina", "friend");
		testGraph.insertUndirected("Keara", "Haley", "friend");
		
		testGraph.insertUndirected("Haley", "Kristina", "friend");
		testGraph.insertUndirected("Haley", "Jonas", "ex");
		testGraph.insertUndirected("Haley", "Renato", "couple");
		
		testGraph.insertUndirected("Hudson", "Jade", "ex");

		
		Graph<String, String> tree = GraphLib.bfs(testGraph, "Scott");
		System.out.println("Full Tree: " + tree);
		
		System.out.println("");
		System.out.println("Scott's in neighbors: " + tree.inNeighbors("Scott"));
		System.out.println("Scott's out neighbors: " + tree.outNeighbors("Scott"));
		System.out.println("Haley's in neighbors: " + tree.inNeighbors("Haley"));
		System.out.println("Haley's out neighbors: " + tree.outNeighbors("Haley"));
		
		System.out.println("");
		System.out.println("Shortest Path From Renato to Scott: " + GraphLib.getPath(tree, "Renato"));
		System.out.println("Shortest Path From Jade to Scott: " + GraphLib.getPath(tree, "Jade"));
		System.out.println("Shortest Path From Kelly to Scott: " + GraphLib.getPath(tree, "Kelly"));

		System.out.println("");
		System.out.println("Average Seperation of Graph: " + GraphLib.averageSeparation(tree, "Scott"));




	}

}
