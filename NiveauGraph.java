package maxflow;

/**
 * A niveau graph for the maximum flow algorithms using blocking flows.
 */
public interface NiveauGraph extends ResidualNet {
    
    /**
     * Computes a path from source to sink.
     * 
     * @return The path (of vertices), or {@code null} if sink is not reachable
     *         from source.
     */
    Integer[] findPath();

    /**
     * Computes a string representation of the niveau graph.
     *  
     * @return The string representation.
     */
    @Override
    String toString();
    
}