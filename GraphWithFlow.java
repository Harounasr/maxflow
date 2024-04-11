package maxflow;
import java.lang.Math;

/**
 * GraphWithFlow
 * defines Directed Graph  and a Flow in this graph
 */
public class GraphWithFlow extends DirectedGraph implements Net {
	
	private Flow currentFlow;
	
	public GraphWithFlow(int numberOfNodes) {
		super(numberOfNodes);
		currentFlow = new GraphFlow(numberOfNodes);
	}

	public GraphWithFlow(int numberOfNodes, int source_index, int sink_index) {
		super(numberOfNodes, source_index, sink_index);
		currentFlow = new GraphFlow(numberOfNodes);
	}

	@Override
	public Flow getFlow() {
		return currentFlow;
	}

	@Override
	public ResidualNet createResidualNet() {
		ResidualNet newResidualNet = new DirectedGraph(getNumberOfNodes(),getSource(), getSink());
		for (int u = 0; u<getNumberOfNodes(); u++) 
			for (int v = 0; v<getNumberOfNodes(); v++) 
				if (hasEdge(u, v)) {
					int edgeCapacity = getEdgeCapacity(u, v);
					int uv_flow = currentFlow.getEdgeFlow(u, v);
					int restCapasity = edgeCapacity-uv_flow;
					if (restCapasity>0) //add direct edge with rest capacity
						newResidualNet.setEdgeCapacity(u, v, restCapasity);
					if (uv_flow>0) { //add backward edge with capacity of flow
						
						if (hasEdge(v, u)) { //if there already exist edge in opposite direction
							int vu_flow = currentFlow.getEdgeFlow(v, u);
							newResidualNet.setEdgeCapacity(u, v, restCapasity+vu_flow);
							newResidualNet.setEdgeCapacity(v, u, getEdgeCapacity(v, u)-vu_flow+uv_flow);
						} else
							newResidualNet.setEdgeCapacity(v, u, uv_flow);
					}
			}
		return newResidualNet;
	}

	@Override
	public NiveauGraph createNiveauGraph(ResidualNet residualNet) {
		return new LevelGraph(residualNet);
	}

	class GraphFlow implements Flow{
		private int[][] flowMatrix;
		
		public GraphFlow(int numberOfNodes) {
			flowMatrix = new int[numberOfNodes][numberOfNodes];
		}
		public GraphFlow(Net net) {
			this(net.getNumberOfNodes());
		}

		@Override
		public int getEdgeFlow(int source, int target) {
			if (!indexInBounds(source))
				throw new ArrayIndexOutOfBoundsException("Invalid flow index " + source);
			if (!indexInBounds(target))
				throw new ArrayIndexOutOfBoundsException("Invalid flow index " + target);
			return flowMatrix[source][target];
		}

		@Override
		public void addEdgeFlow(int source, int target, int flowAdd) {
			int oldTargetSourceFlow = getEdgeFlow(target, source);

			int reduction = Math.min(oldTargetSourceFlow, flowAdd);
			
			//need to update both (target, source) and (source, target) edges
			int newFlowTargetSource = flowMatrix[target][source] - reduction;
			setEdgeFlow(target, source, newFlowTargetSource);
			
			int newFlowSourceTarget = flowMatrix[source][target] + flowAdd - reduction;
			setEdgeFlow(source, target, newFlowSourceTarget);
		}

		@Override
		public void setEdgeFlow(int source, int target, int flow) {
			if (!indexInBounds(source))
				throw new ArrayIndexOutOfBoundsException("Invalid flow index " + source);
			if (!indexInBounds(target))
				throw new ArrayIndexOutOfBoundsException("Invalid flow index " + target);

			if (flow<0)
				throw new ArithmeticException("Flow cannot be negative");
			flowMatrix[source][target] = flow;
		}

		@Override
		public boolean isValidFlow() {
			
			int errors = 0; //for counting possible errors
			//arrays to accumulate incoming and outgoing flows for each vertex
			int[] outFlow = new int[flowMatrix.length];
			int[] inFlow = new int[flowMatrix.length];
			
			for (int u = 0; u<flowMatrix.length; u++) 
				for (int v = 0; v<flowMatrix.length; v++) {
					int uv_flow = getEdgeFlow(u, v);
					outFlow[u] += uv_flow;
					inFlow[v]+= uv_flow;
					if (uv_flow>getEdgeCapacity(u, v)) //flow must not exceed edge capacity
						errors++;
				}
			if (!(outFlow[getSource()]==inFlow[getSink()])) //outgoing flow from source must = incoming flow to sink
				errors++;
			for (int nodeIdx =0; nodeIdx<outFlow.length; nodeIdx++) {
				if (nodeIdx!=getSource() && nodeIdx!=getSink())
					if (outFlow[nodeIdx]!=inFlow[nodeIdx]) //check equality of outgoing and incoming flows
						errors++;
			}
			
			return errors==0;
		}

		@Override
		public void clear() {
			for (int i = 0; i<flowMatrix.length; i++) 
				for (int j = 0; j<flowMatrix.length; j++)
					flowMatrix[i][j] = 0;
		}

		@Override
		public int getTotalFlow() {
			int totalFlow = 0;
			for (int v = 0; v<flowMatrix.length; v++) 
					totalFlow += flowMatrix[getSource()][v];
			return totalFlow;
		}
		
		@Override
	    public String toString() {
			String flowToStr = "";
			for (int i = 0; i < getNumberOfNodes(); i++) 
				for (int j = 0; j < getNumberOfNodes(); j++)
					if (flowMatrix[i][j]>0)
						flowToStr+=String.format("(%d, %d) (%d/%d)\n", 
								i+maxflowConstants.INDEX_OFFSET, j+maxflowConstants.INDEX_OFFSET, 
								flowMatrix[i][j], getEdgeCapacity(i,j));
					
			return flowToStr;
		}
	}
}
