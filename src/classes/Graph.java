/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package classes;


import interfaces.IProgressBar;
import interfaces.StreamedContainer;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.util.ArrayList;
import java.util.HashMap;

public class Graph implements StreamedContainer<Road>{
    
    public static class NoPathException extends Exception {
        public NoPathException() {
            super("A path could not be found");
        }
    }
    
    private int V = 0; //Number of vertices/intersections
    private int E = 0; //Number of edges/road parts
    private HashMap<Long, ArrayList<Road.Edge>> adj = new HashMap<>();
    private HashMap<Long, Road.Node> nodes;
    private IProgressBar progbar                    = null;
    
    public Graph(Model model, HashMap<Long, Road.Node> nodes) {
        this.nodes = nodes;
        for (Long id : nodes.keySet()) { // Create the 'adjacent' arrays
            adj.put(id, new ArrayList<Road.Edge>());
        }
        model.getAllRoads(this);
    }
    
    public Graph(Model model, HashMap<Long, Road.Node> nodes, IProgressBar progbar) {
        this.progbar = progbar;
        this.nodes = nodes;
        for (Long id : nodes.keySet()) { // Create the 'adjacent' arrays
            adj.put(id, new ArrayList<Road.Edge>());
        }
        model.getAllRoads(this);
    }
    
    /**
     * Returns the number of vertices in the edge-weighted graph.
     *
     * @return the number of vertices in the edge-weighted graph
     */
    public int V() {
        return adj.size();
    }

    /**
     * Returns the number of edges in the edge-weighted graph.
     *
     * @return the number of edges in the edge-weighted graph
     */
    public int E() {
        return E;
    }

    /**
     * Returns the edges incident on vertex <tt>v</tt>.
     *
     * @return the edges incident on vertex <tt>v</tt> as an Iterable
     * @param v the vertex
     * @throws java.lang.IndexOutOfBoundsException unless 0 <= v < V
     */
    public Iterable<Road.Edge> adj(long v) {
        //  System.out.println("vertex " + v + " has " + adj[v].size() + " adjacent edges");
        return adj.get(v);
    }

    public long other(Road.Edge part, long firstIndex) throws NoPathException {
        // System.out.println("Checking other index with indexes " + part.sourceID + " and " + part.targetID);
        // System.out.println("Road:" + part);
        //System.out.println("RoadPart: " + part.name);
        //System.out.println("SourceID: " + part.sourceID + "\n"
        //      + "FirstIDIndex: " + firstIDIndex);
        if (part != null) {
            if (part.p1.id == firstIndex) {
                return part.p2.id;
            } else {
                return part.p1.id;
            }
        } else {
            throw new NoPathException();
        }
    }

    public Road.Node getIntersection(long index) {
        return nodes.get(index);
    }

    @Override
    public void startStream() {
        System.out.println("Starting the graph population...");
    }

    @Override
    public void startStream(IProgressBar bar) {
        progbar = bar;
        startStream();
    }

    @Override
    public void add(Road obj) {
        //roads.add(obj);
        int i = nodes.size();
        Road.Edge last = null;
        for (Road.Edge edge: obj) {
            last = edge;
            Road.Node node = edge.p1;
            Road.Node target = edge.p2;
            //System.out.println("i: "+i+", gotten index: "+IDToIndex.get(node.id));
            adj.get(node.id).add(edge);
            if (!obj.oneway) { // Krak is mostly bidirectional
                adj.get(target.id).add(edge);
            }
            
            E += 1;
        }
        if (progbar != null) {
            progbar.update(1);
        }
    }

    @Override
    public void endStream() {
        System.out.println("Graph populated!");
        MemoryMXBean mxbean = ManagementFactory.getMemoryMXBean();
        System.out.printf("Heap memory usage: %d MB%n",
                mxbean.getHeapMemoryUsage().getUsed() / (1000000));
    }
}
