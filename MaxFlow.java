package maxflow;

/**
 * A maximum flow algorithm.
 */
public interface MaxFlow {

    /**
     * Calculates a maximum flow in a given network.
     * 
     * @param net The net a maximum flow shall be computed for. The net's flow
     *        will be updated.
     */
    void computeMaxFlow(Net net);

    /**
     * Executes only one step in calculating a maximum flow in a given network.
     * This is for debugging purposes.
     * 
     * @param net The net a maximum flow shall be computed for. The net's flow
     *        will be updated.
     */
    void step(Net net);
     
}