package maxflow;

/**
 * A net for the maximum flow algorithms. Can also be used for blocking flow
 * implementations.
 */
public interface Net extends ResidualNet {

    /**
     * Gets the current flow in this net.
     * 
     * @return The flow.
     */
    Flow getFlow();
    
    /**
     * Creates a residual net based on this net with its current flow.
     * 
     * @return The residual net.
     */
    ResidualNet createResidualNet();
    
    /**
     * Creates a niveau graph based on {@code residualNet}.
     * 
     * @param residualNet The residual net based on this net with its current
     *        flow.
     * @return The niveau graph.
     */
    NiveauGraph createNiveauGraph(ResidualNet residualNet);
    
    /**
     * Computes a string representation of this net. Only capacities are shown.
     *  
     * @return The string representation.
     */
    @Override
    String toString();

    /**
     * An inner class for network flows - every flow belongs to a net.
     */
    interface Flow {
        
        /**
         * Gets the flow of an edge.
         * 
         * @param source The edge's source.
         * @param target The edge's target.
         * @return The edge's flow.
         */
        int getEdgeFlow(int source, int target);
        
        /**
         * Adds {@code flowAdd} to the flow of an edge, which may cause implicit
         * updates for the flow of the complementary/back edge. The method does
         * not check if the resulting flow in the network is valid.
         * 
         * @param source The edge's source.
         * @param target The edge's target.
         * @param flowAdd The flow addendum. Must be greater or equal to zero.
         */
        void addEdgeFlow(int source, int target, int flowAdd);
        
        /**
         * Sets the flow of an edge to {@code flow}. The method does not check
         * if the resulting flow in the network is valid.
         * 
         * @param source The edge's source.
         * @param target The edge's target.
         * @param flow The new flow. Must be greater or equal to zero.
         */
        void setEdgeFlow(int source, int target, int flow);
        
        /**
         * Checks if the flow on the net is valid, i.e., if it obeys capacity
         * restrictions and Kirchoff's rules.
         * 
         * @return {@code true} if and only if flow is valid.
         */
        boolean isValidFlow();

        /**
         * Resets the flow of each edge to zero.
         */
        void clear();
        
        /**
         * Gets the current total flow of the net.
         * 
         * @return The flow value.
         */
        int getTotalFlow();
        
        /**
         * Computes a string representation of this flow.
         *  
         * @return The string representation.
         */
        @Override
        String toString();
    }
}