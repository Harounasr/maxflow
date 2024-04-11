package maxflow;

/**
 * A residual net for the maximum flow algorithms using blocking flows.
 */
public interface ResidualNet {
    
    /**
     * Gets the capacity of an edge.
     * 
     * @param source The edge's source.
     * @param target The edge's target.
     * @return The edge's capacity.
     */
    int getEdgeCapacity(int source, int target);
    
    /**
     * Sets the capacity of an edge. 
     * 
     * @param source The edge's source.
     * @param target The edge's target.
     * @param capacity The new capacity for the edge.
     */
    void setEdgeCapacity(int source, int target, int capacity);

    /**
     * Checks on the validity of an edge. 
     * 
     * @param source The edge's source. Must belong to the net.
     * @param target The edge's target. Must belong to the net.
     * @param capacity The capacity must be greater or equal to zero.
     * @return {@code true} if and only if valid.
     */
    boolean isValidEdge(int source, int target, int capacity);
    
    /**
     * Checks if the sink can be reached from the source via edges with free
     * (remaining) capacity. 
     *
     * @return {@code true} if and only if reachable.
     */
    boolean isSinkReachableFromSource();

    /**
     * Gets the number of nodes.
     * 
     * @return The number of nodes.
     */
    int getNumberOfNodes();
    
    /**
     * Gets the net's source.
     * 
     * @return The source node.
     */
    int getSource();
    
    /**
     * Gets the net's sink.
     * 
     * @return The sink node.
     */
    int getSink();
    
    /**
     * Checks if an edge between {@code source} and {@code target} does exist.
     *
     * @param source The edge's source.
     * @param target The edge's target.
     * @return {@code true} if and only if edge exists.
     */
    boolean hasEdge(int source, int target);
    
    /**
     * Computes a string representation of the residual net.
     *  
     * @return The string representation.
     */
    @Override
    String toString();
    
}