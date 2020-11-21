import java.util.*;

/**
 * 
 * @author Scott Crawshaw
 * 5/13/19
 * PS4
 *
 */

public class GraphLib<V,E> {
	
	/**
	 * Finds the average distance-from-root in a shortest path tree
	 * 
	 * @param tree	shortest path tree
	 * @param root	root of tree
	 * @return 		returns the tree's average separation
	 */
	public static <V,E> double averageSeparation(Graph<V,E> tree, V root) {
		ArrayList<Integer> distances = new ArrayList<Integer>();
		calculateDistances(tree, root, 0, distances);

		//average all the distances in the distances list
		double sum = 0;
		double count = 0;
		for(Integer i : distances) {
			sum+=i.intValue();
			count+=1;
		}
		return sum/count;
		
	}
	
	/**
	 * Recursively populates a distances list with the distances of each node to the root
	 * Helper function for averageSeperation
	 * 
	 * @param tree		shortest path tree
	 * @param root		starting node
	 * @param depth		how far away we are from root of tree
	 * @param distances	list of distances
	 */
	private static <V, E> void calculateDistances(Graph<V,E> tree, V root, int depth, ArrayList<Integer> distances){
		distances.add((Integer) depth);
		for(V vertex : tree.inNeighbors(root)) {
			calculateDistances(tree, vertex, depth+1, distances); //recurse down the tree
		}
	}
	
	
	/**
	 * Given a graph and a subgraph, returns a set of all vertex's that are in the graph but not the subgraph
	 * 
	 * @param graph
	 * @param subgraph
	 * @return missingSet
	 */
	public static <V,E> Set<V> missingVertices(Graph<V,E> graph, Graph<V,E> subgraph) {
		Set<V> missingSet = new HashSet<V>();
		
		for(V vertex : graph.vertices()) {
			if(!subgraph.hasVertex(vertex)) {
				missingSet.add(vertex);
			}
		}
		
		return missingSet;
	}
	
	/**
	 * Given a tree and a starting node, gets the shortest path back to the root.
	 * 
	 * @param tree		graph tree from BFS search
	 * @param v			starting node
	 * @return path		path as an ArrayList
	 */
	public static <V,E> List<V> getPath(Graph<V,E> tree, V v){
		List<V> path = new ArrayList<>();
		path.add(v);
		
		while(tree.outDegree(v) != 0) {
			for(V out : tree.outNeighbors(v)) v = out; //tree nodes only have one out neighbor
			path.add(v);
		}
		
		return path;
	}
	
	
	/**
	 * Performs breadth first search on a given graph and creates a graph tree that illustrates the shortest paths
	 * from each node back to the root.
	 * 
	 * @param g			initial undirected graph
	 * @param source	root vertex
	 * @return			tree graph illustrating shortest paths back to source
	 * 
	 */
	public static <V,E> Graph<V,E> bfs(Graph<V,E> g, V source){
		Graph<V,E> tree = new AdjacencyMapGraph<V,E>();		
		Queue<ArrayList<V>> queue = new LinkedList<ArrayList<V>>();
		//add array containing vertex and backpointer to queue
		ArrayList<V> array = new ArrayList<V>(2); array.add(source); array.add(null);
		queue.add(array);
		
		while(!queue.isEmpty()) {
			array = queue.poll();
			V vertex = array.get(0);
			V backpointer = array.get(1);
			
			if(!tree.hasVertex(vertex)){
				tree.insertVertex(vertex);
				if(backpointer != null) tree.insertDirected(vertex, backpointer, g.getLabel(backpointer, vertex));
				
				for(V neighbor : g.outNeighbors(vertex)) {
					if(!tree.hasVertex(neighbor)) {
						array = new ArrayList<V>(2); array.add(neighbor); array.add(vertex);
						queue.add(array);
					}
				}
			}
		}
		
		return tree;
	}

}