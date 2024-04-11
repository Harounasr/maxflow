package maxflow;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

/**
 * DirectedGraph
 * class DirectedGraph describes graph structure of general type 
 * in form of as adjacency matrix 
 * with defined source and sink
 * 
 */
public class DirectedGraph implements ResidualNet{
	private int numberOfNodes; 
	private int[][] adjacencyMatrix; //contains edge capacities 
	private int source, sink;
	
	/**
	 * Constructor of directed graph without specified source and sink
	 * @param numberOfNodes number of nodes in graph
	 * source and sink are chosen to be first and last vertex
	 */
	public DirectedGraph(int numberOfNodes){
		this(numberOfNodes, 0, numberOfNodes-1);
	}
	
	/**
	 * Constructor of directed graph with defined source and sink
	 * @param numberOfNodes number of nodes in graph
	 * @param source_index index of source vertex
	 * @param sink_index index of sink vertex
	 */
	public DirectedGraph(int numberOfNodes, int source_index, int sink_index) {
		if (numberOfNodes < maxflowConstants.MIN_NUMBER_OF_NODES)
			throw new ArithmeticException(
					String.format("Graph must have at least %d nodes", 
							maxflowConstants.MAX_NUMBER_OF_NODES));
		if (numberOfNodes > maxflowConstants.MAX_NUMBER_OF_NODES)
			throw new ArithmeticException(
					String.format("Graph must have no more than %d nodes", 
							maxflowConstants.MAX_NUMBER_OF_NODES));
		this.numberOfNodes = numberOfNodes;
		adjacencyMatrix = new int[numberOfNodes][numberOfNodes];
		if (!indexInBounds(source_index))
			throw new ArrayIndexOutOfBoundsException("Bad source index "+source_index);
		if (!indexInBounds(sink_index))
			throw new ArrayIndexOutOfBoundsException("Bad sink index "+sink_index);
		if (source_index==sink_index)
			throw new ArithmeticException("Source and sink must be different");
		source = source_index;
		sink = sink_index;
	}
	
	/**
     * Checks if given index is within bounds
     * 
     * @param index of node to check
     * @return {@code true} if and only if given index is inside bounds
     */
	protected boolean indexInBounds(int index) {
		return (index>=0 && index < numberOfNodes);
	}
	
	@Override
	public int getEdgeCapacity(int source, int target) {
		//check if source and target indices are within bounds 
		if (!indexInBounds(source))
			throw new ArrayIndexOutOfBoundsException("Invalid vertex index " + source);
		if (!indexInBounds(target))
			throw new ArrayIndexOutOfBoundsException("Invalid vertex index " + target);
		//if no error with indices return capacity 
		return adjacencyMatrix[source][target];
	}

	@Override
	public void setEdgeCapacity(int source, int target, int capacity) {
		//check if there are error in input values
		if (!indexInBounds(source))
			throw new ArrayIndexOutOfBoundsException("Invalid vertex index " + source);
		if (!indexInBounds(target))
			throw new ArrayIndexOutOfBoundsException("Invalid vertex index " + target);				
		if (capacity<0) 
			throw new ArithmeticException("Edge capasity cannot be negative");		
		else		//if no errors occurred, change value in adjacencyMatrix to new capacity	
			adjacencyMatrix[source][target] = capacity;
	}

	@Override
	public boolean isValidEdge(int source, int target, int capacity) {
		try {		
			int actualCapacity = getEdgeCapacity(source, target);
			//return result of comparison actual and give capacity 
			return (actualCapacity == capacity);
		} catch (ArrayIndexOutOfBoundsException e){
			//ignore exception and return false
			return false;
		}
	}

	@Override
	public boolean isSinkReachableFromSource() { 
		// check node reachability by bfs-search
		Queue<Integer> bfsQueue = new LinkedList<>(); //queue to keep nodes for bfs-search
		LinkedList<Integer> unvisitedNodes = new LinkedList<>(); //list to track not yet visited nodes
		//all nodes are unvisited in beginning
		for (int i=0; i<getNumberOfNodes(); i++)
			unvisitedNodes.add(i);
		unvisitedNodes.remove((Integer)source); //remove source from unvisitedNodes
		bfsQueue.add(source); //add source node in beginning of queue
		
		//array to keep track which node were visited during iteration
		ArrayList<Integer> visitedNodes = new ArrayList<Integer>();
		
		//bfs search goes until queue is empty
		while (bfsQueue.size()>0) {
			int currentNode = bfsQueue.poll(); //take node from the top of queue
			//iterate through unvisited nodes
	        for (int node : unvisitedNodes) { 
				if (hasEdge(currentNode, node)) { //for all neighbors of current node
					if (node == sink)  //if sink found -end search;
						return true;
					else { //else add neighbor node in queue
						bfsQueue.add(node);
						visitedNodes.add((Integer)node); //consider node visited						
					}
				}
			}
	        unvisitedNodes.removeAll(visitedNodes);
	        visitedNodes.clear();
		}
		return false; //if sink was not found in bfs-search - its unreachable
	}

	@Override
	public int getNumberOfNodes() {
		return numberOfNodes;		
	}

	@Override
	public int getSource() {
		return source;
	}

	@Override
	public int getSink() {
		return sink;
	}

	@Override
	public boolean hasEdge(int source, int target) {
		try {		
			int edgeCapacity = getEdgeCapacity(source, target);
			//edge exists if its capacity > 0 
			return (edgeCapacity > 0);
		} catch (ArrayIndexOutOfBoundsException e){
			//ignore exception and return false
			return false;
		}
	}
	
	@Override
    public String toString() {
		String graphToStr = "";
		for (int i = 0; i < numberOfNodes; i++) {
			for (int j = 0; j < numberOfNodes; j++)
				graphToStr+= String.valueOf(adjacencyMatrix[i][j])+" ";
			graphToStr = graphToStr.stripTrailing();
			graphToStr +='\n';
		}
		return graphToStr;
	}
}
