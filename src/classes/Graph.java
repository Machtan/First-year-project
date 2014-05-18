/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package classes;


import interfaces.IProgressBar;
import interfaces.StreamedContainer;
import java.util.ArrayList;
import java.util.HashMap;

public class Graph implements StreamedContainer<Road>{

    private int V = 0; //Number of vertices/intersections
    private int E = 0; //Number of edges/road parts
    private ArrayList<ArrayList<Road.Edge>> adj = new ArrayList<>();
    private ArrayList<Road.Node> nodes          = new ArrayList<>();
    private HashMap<Long, Integer> IDToIndex    = new HashMap<>();
    private HashMap<Integer, Long> indexToID    = new HashMap<>();
    private IProgressBar progbar                = null;

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
    
    public Graph(Model model) {
        model.getAllRoads(this);
    }
    
    public Graph(Model model, IProgressBar progbar) {
        this.progbar = progbar;
        model.getAllRoads(this);
    }

    /**
     * Returns the edges incident on vertex <tt>v</tt>.
     *
     * @return the edges incident on vertex <tt>v</tt> as an Iterable
     * @param v the vertex
     * @throws java.lang.IndexOutOfBoundsException unless 0 <= v < V
     */
    public Iterable<Road.Edge> adj(int v) {
        if (v < 0 || v >= V()) {
            throw new IndexOutOfBoundsException("vertex " + v + " is not between 0 and " + (V() - 1));
        }
        //  System.out.println("vertex " + v + " has " + adj[v].size() + " adjacent edges");
        return adj.get(v);
    }

    public int other(Road.Edge part, int firstIndex) {
        // System.out.println("Checking other index with indexes " + part.sourceID + " and " + part.targetID);
        // System.out.println("Road:" + part);
        //System.out.println("RoadPart: " + part.name);
        //System.out.println("SourceID: " + part.sourceID + "\n"
        //      + "FirstIDIndex: " + firstIDIndex);
        if (part != null) {
            if (IDToIndex.get(part.p1.id) == firstIndex) {
                if (!IDToIndex.containsKey(part.p2.id)) {
                    throw new RuntimeException("The ID '"+part.p2.id+"' is not in the Index map!");
                }
                return IDToIndex.get(part.p2.id);
            } else {
                return IDToIndex.get(part.p1.id);
            }
        } else {
            throw new RuntimeException("Roadpart is null");
            //return firstIDIndex;
        }
    }

    public long getIntersectionID(int index) {
        return indexToID.get(index);
    }

    public int getIntersectionIndex(long ID) {
        return IDToIndex.get(ID);
    }

    public Road.Node getIntersection(int index) {
        return nodes.get(index);
    }

    @Override
    public void startStream() {
        nodes = new ArrayList<>();
        System.out.println("Starting the graph population...");
    }

    @Override
    public void startStream(IProgressBar bar) {
        progbar = bar;
        startStream();
    }
    
    /**
     * Adds a new node at the given index
     * @param node The node to add
     * @param index The index at which to add it
     */
    private void addNode(Road.Node node, int index) {
        indexToID.put(index, node.id);
        IDToIndex.put(node.id, index);
        nodes.add(node);
        adj.add(new ArrayList<Road.Edge>());
        //System.out.println("Adding "+node+" at index "+index+"! ("+adj.size()+" adj)");
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
            if (!IDToIndex.containsKey(node.id)) {
                addNode(node, i++);
            }
            if (!IDToIndex.containsKey(target.id)) {
                addNode(target, i++);
            }
            //System.out.println("i: "+i+", gotten index: "+IDToIndex.get(node.id));
            adj.get(IDToIndex.get(node.id)).add(edge);
            if (!obj.oneway) { // Krak is mostly bidirectional
                adj.get(IDToIndex.get(target.id)).add(edge);
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
    }
}
