package maxflow;

/**
 * DinicMaxFlow
 * class implementing Dinic maxflow algorithm
 */
public final class DinicMaxFlow implements MaxFlow{
	@Override
	public void computeMaxFlow(Net net) {
		if (net == null)
			throw new NullPointerException("Net was not defined");
		
		ResidualNet residualNet = net.createResidualNet();
		while(residualNet.isSinkReachableFromSource()) {
			LevelGraph levelGraph = (LevelGraph) net.createNiveauGraph(residualNet);
			computeBlockingFlow(net, levelGraph);
			residualNet = net.createResidualNet();
		}
	}
	@Override
	public void step(Net net) {
		ResidualNet residualNet = net.createResidualNet();
		if (residualNet.isSinkReachableFromSource()) {
			LevelGraph levelGraph = (LevelGraph) net.createNiveauGraph(residualNet);
			computeBlockingFlow(net, levelGraph);
		}
	}
	
	/**
     * Computes blocking flow in {@code levelGraph}, and updates flow in this Net
     * 
     * @param levelGraph based on this graph computing the flow
     * 
     */
    private void computeBlockingFlow(Net net, NiveauGraph levelGraph) {
    	Net.Flow graphFlow = net.getFlow();
		Integer[] path = levelGraph.findPath();
		while (path!=null) {
			int pathCapacity = computePathCapacity(levelGraph, path);
			for (int e = 0; e<path.length-1; e++) {
				int oldEdegeCapacity = levelGraph.getEdgeCapacity(path[e], path[e+1]);
				levelGraph.setEdgeCapacity(path[e], path[e+1], oldEdegeCapacity-pathCapacity);
				graphFlow.addEdgeFlow(path[e], path[e+1], pathCapacity);
			}
			path = levelGraph.findPath();
		}
	}
    
	/**
     * Computes capacity of the path in the net
     * 
     * @param net where path is located, path - sequence of vertices
     * @return minimal edge capacity on the path
     */
	
	private static int computePathCapacity(ResidualNet net, Integer[] path) {
		//path capacity equals minimal capacity of edges in path
		int pathCapacity = 0;
		int pathLen = path.length;
		if (pathLen > 1) {
			pathCapacity = net.getEdgeCapacity(path[0], path[1]);
			for (int e = 0; e<pathLen-1;e++) {
				int edgeCapacity = net.getEdgeCapacity(path[e], path[e+1]);
				if (edgeCapacity<pathCapacity)
					pathCapacity = edgeCapacity;
			}
		}
		return pathCapacity;
	}
}
