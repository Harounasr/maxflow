package maxflow;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

/**
 * LevelGraph
 * class LevelGraph implements interface NiveauGraph
 * Directed Acyclic Graph where to each node assigned a level
 *
 */
public class LevelGraph extends DirectedGraph implements NiveauGraph {

	private int[] nodesLevel; // to assign bfs-level to each node

	/**
	 * Constructs level-graph based on input graph
	 * @param fromGraph graph based on which level-graph is built
	 */
	public LevelGraph(ResidualNet fromGraph) {
		super(fromGraph.getNumberOfNodes(),fromGraph.getSource(),fromGraph.getSink());

		/*assigning bfs-levels to  nodes;
		 * enumeration of levels starts from 0
		 * level -1 means node is unvisited
		 */
		nodesLevel = new int[getNumberOfNodes()];
		for (int nodeIdx = 0; nodeIdx<getNumberOfNodes();nodeIdx++)
			nodesLevel[nodeIdx] = -1;
		nodesLevel[getSource()] = 0;

		boolean sinkReached = false; //flag to mark that sink is reached from source

		//constructing level-graph by BFS-algorithm

		Queue<Integer> bfsQueue = new LinkedList<>(); //queue to keep nodes for bfs-search
		bfsQueue.add(getSource()); //add source node in beginning of queue

		//current bfs-level
		//bfs search goes until queue is empty or until sink was reached
		while (bfsQueue.size()>0 && !sinkReached) {
			int currentNode = bfsQueue.poll(); //take node from the top of queue
			int currentLevel = nodesLevel[currentNode];

			if (currentNode == getSink()) { //if the search reached sink- stop the search;
				removeRedundantEdges();
				sinkReached = true;
				break;
			}


			//iterate through unvisited nodes
			for (int node = 0; node < getNumberOfNodes(); node++) {
				if (fromGraph.hasEdge(currentNode, node)) {
					if (nodesLevel[node] == -1) //if node is unvisited
					{
						nodesLevel[node] = currentLevel + 1; //assign level
						bfsQueue.add(node);					//add in queue
					}
					//if node level > than level of current node add new edge
					if (nodesLevel[node] > currentLevel)
						setEdgeCapacity(currentNode, node, fromGraph.getEdgeCapacity(currentNode, node));
				}
			}
		}
	}

	private void removeRedundantEdges() {
		int sinkLvl = nodesLevel[getSink()];
		for (int u = 0; u < getNumberOfNodes(); u++) {
			if (nodesLevel[u]>=sinkLvl) { //for nodes those lvl >= than sink lvl
				for (int v = 0; v < getNumberOfNodes(); v++)  //remove all edges from those nodes
					if (hasEdge(u, v))
						setEdgeCapacity(u, v, 0);
			}
		}
	}

	@Override
	public boolean isSinkReachableFromSource() {
		return findPath()!=null;
	}

	@Override
	public Integer[] findPath() {
		ArrayList<Integer> reversePath = new ArrayList<Integer>();

		int nodeLevel = nodesLevel[getSink()];
		reversePath.add(getSink());

		//Compute path backwards going from sink to source
		int nodeV = getSink();
		while(nodeLevel>0)
		{
			int biggestCapacity = 0;
			int nodeInPath = -1;
			for (int nodeU = 0; nodeU<getNumberOfNodes(); nodeU++) {
				//find edge(u,v) that has biggest capacity
				if (hasEdge(nodeU, nodeV)) {
					int uvCapacity = getEdgeCapacity(nodeU, nodeV);
					if (uvCapacity > biggestCapacity) {
						biggestCapacity = uvCapacity;
						nodeInPath = nodeU;
					}
				}
			}
			if (nodeInPath!=-1) { //if next node in path found - add it to path and continue
				reversePath.add(nodeInPath);
				nodeV = nodeInPath;
				nodeLevel = nodesLevel[nodeInPath];

			} else //else cannot complete the path- source unreachable
				return null;
		}
		//revert the path in normal order
		Integer[] path = new Integer[reversePath.size()];
		int nodeIdx = reversePath.size()-1;
		for (Integer node: reversePath) {
			path[nodeIdx] = node;
			nodeIdx --;
		}
		return path;
	}
}
